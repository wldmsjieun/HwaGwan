package kr.ac.mju.cd2020shwagwan;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.mju.cd2020shwagwan.ui.home.HomeFragment;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CustomArrayAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList items;
    public static TextView mTvKind;
    static ProgressBar pbUsage;

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


        // 프로그레스바 설정
        try{

            String openStr = cosmetics.getProductOpen();
            SimpleDateFormat trans = new SimpleDateFormat("yyyy-MM-dd");
            Date open = trans.parse(openStr);

            String expStr = cosmetics.getProductExp();
            Date exp = trans.parse(expStr);

            long period = exp.getTime() - open.getTime();
            long periodDay = period / (24 * 60 * 60 * 1000);
            periodDay = Math.abs(periodDay);

            pbUsage.setMax((int)periodDay);

            long now = System.currentTimeMillis();
            Date dt = new Date(now);

            long usage = dt.getTime() - open.getTime();
            long usageDay = usage / (24 * 60 * 60 * 1000);
            usageDay = Math.abs(usageDay);
            Log.d(TAG , "pb - usageDay : " + usageDay);

            pbUsage.setProgress((int) usageDay);
            Log.d(TAG , "pb - pbUsage : " + pbUsage.getProgress());


        }catch(Exception e){
            Log.d(TAG , " pb - error : " + e.getMessage());
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