package kr.ac.mju.cd2020shwagwan;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.mju.cd2020shwagwan.ui.dashboard.DashboardFragment;
import kr.ac.mju.cd2020shwagwan.ui.home.HomeFragment;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class CustomArrayAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList items;
    public static TextView mTvKind;
    static ProgressBar pbUsage;
    //
    private int count = 0;
    public double aDay;


    public CustomArrayAdapter(Context context, ArrayList items) {
        super(context, 0, items);

        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        final Cosmetics cosmetics = (Cosmetics) getItem(position);

        TextView tvBrand = convertView.findViewById(R.id.tvBrand);
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvOpen = convertView.findViewById(R.id.tvOpen);
        TextView tvExp = convertView.findViewById(R.id.tvExp);
        mTvKind = convertView.findViewById(R.id.tvKind);
        pbUsage = convertView.findViewById(R.id.pbUsage);


        tvBrand.setText(cosmetics.getProductBrand());
        tvName.setText(cosmetics.getProductName());
        tvOpen.setText(cosmetics.getProductOpen());
        tvExp.setText(cosmetics.getProductExp());
        mTvKind.setText(cosmetics.getProductKind());

        Date exp = new Date(), dt = new Date();

        // 프로그레스바 설정
        try{

            String openStr = cosmetics.getProductOpen();
            SimpleDateFormat trans = new SimpleDateFormat("yyyy-MM-dd");
            Date open = trans.parse(openStr);

            String expStr = cosmetics.getProductExp();
            exp = trans.parse(expStr);

            long period = exp.getTime() - open.getTime();
            long periodDay = period / (24 * 60 * 60 * 1000);
            periodDay = Math.abs(periodDay);

            pbUsage.setMax((int)periodDay);

            long now = System.currentTimeMillis();
            dt = new Date(now);

            long usage = dt.getTime() - open.getTime();
            long usageDay = usage / (24 * 60 * 60 * 1000);
            usageDay = Math.abs(usageDay);
            Log.d(TAG , "pb - usageDay : " + usageDay);

            pbUsage.setProgress((int) usageDay);
            Log.d(TAG , "pb - pbUsage : " + pbUsage.getProgress());


        }catch(Exception e){
            Log.d(TAG , " pb - error : " + e.getMessage());
        }


        double alarm = exp.getTime()-dt.getTime();
        double alarmDay = alarm / (24 * 60 * 60 * 1000);

        aDay = Math.abs(alarmDay);
        aDay = Math.ceil(alarmDay);

        Intent intent = new Intent(getContext(), MyService.class);
        SharedPreferences sp = getContext().getSharedPreferences(String.valueOf(cosmetics.getId()), MODE_PRIVATE);
        int notiWeek = sp.getInt("notiWeek", 0);
        int notiMonth= sp.getInt("notiMonth", 0);
        SharedPreferences.Editor editor = sp.edit();
        intent.putExtra("productName", cosmetics.getProductName());
                if (aDay == 7 && notiWeek == 0){
                    Log.d(TAG, "알람7 " + aDay);
                    intent.putExtra("cbWeekOn", true);
                    intent.putExtra("weekCid", cosmetics.getId());
                    editor.putInt("notiWeek", 1);
                    editor.commit();
                    getContext().startService(intent);
                }else if(notiWeek != 0){
                    NotificationManagerCompat.from(getContext()).cancel(1);
                }


                if (aDay == 30 && notiMonth == 0){
                    intent.putExtra("cbMonthOn", true);
                    editor.putInt("notiMonth", 1);
                    intent.putExtra("monthCid", cosmetics.getId());
                    editor.commit();
                    getContext().startService(intent);
                }else {
//                    getContext().stopService(intent);
                    NotificationManagerCompat.from(getContext()).cancel(1);
                }


        ImageView ivDel = convertView.findViewById(R.id.ivDel);
        ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 삭제
                new AlertDialog.Builder(context)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                // 삭제
                                deleteData(cosmetics);
                            }
                        })
                        .setNegativeButton("NO", null)
                        .setCancelable(false)
                        .setTitle("Do you want to Delete? ")
                        .setMessage(cosmetics.getProductName())
                        .show();
            }
        });

        return convertView;
    }
    /* 삭제 */
    private void deleteData(Cosmetics cosmetics) {
        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(this.context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 삭제 (id(tID) 값으로 삭제)
            Object[] args = { cosmetics.getId() };
            String sql = "DELETE FROM cosmetics WHERE cID = ?";

            db.execSQL(sql, args);

            // 항목 삭제
            this.items.remove(cosmetics);
            // 리스트 적용
            notifyDataSetChanged();

        } catch (SQLException e) {}

        db.close();
    }


}