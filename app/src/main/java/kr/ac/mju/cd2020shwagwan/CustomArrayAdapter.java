package kr.ac.mju.cd2020shwagwan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import kr.ac.mju.cd2020shwagwan.Notification.MyService;
import kr.ac.mju.cd2020shwagwan.ui.home.HomeFragment;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class CustomArrayAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList items;
    public static TextView mTvKind;
    static ProgressBar pbUsage;

    String mMyPageInseartSql = "INSERT INTO "+DBHelper.TABLE_MYPAGE+"(brandName, productName, dtOpen, dtExp, kind,  volume, additionalContent) VALUES(?,?,?,?,?,?,?)";

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


        tvBrand.setText(cosmetics.getBrandName());
        tvName.setText(cosmetics.getProductName());
        tvOpen.setText(cosmetics.getDtOpen());
        tvExp.setText(cosmetics.getDtExp());
        mTvKind.setText(cosmetics.getKind());

        Date exp = new Date(), dt = new Date();

        Log.d("확인", "용량 = " + cosmetics.getVolume());

        // 프로그레스바 설정
        try{

            String openStr = cosmetics.getDtOpen();
            SimpleDateFormat trans = new SimpleDateFormat("yyyy-MM-dd");
            Date open = trans.parse(openStr);

            String expStr = cosmetics.getDtExp();
            exp = trans.parse(expStr);

            long period = exp.getTime() - open.getTime();
            long periodDay = period / (24 * 60 * 60 * 1000);
//            periodDay = Math.abs(periodDay);

            pbUsage.setMax((int)periodDay);

            long now = System.currentTimeMillis();
            dt = new Date(now);

            long usage = dt.getTime() - open.getTime();
            long usageDay = usage / (24 * 60 * 60 * 1000);
//            usageDay = Math.abs(usageDay);
            Log.d(TAG , "pb - usageDay : " + usageDay);

            pbUsage.setProgress((int) usageDay);
            Log.d(TAG , "pb - pbUsage : " + pbUsage.getProgress());


        }catch(Exception e){
            Log.d(TAG , " pb - error : " + e.getMessage());
        }

        SharedPreferences sp = getContext().getSharedPreferences("alarmTime", MODE_PRIVATE);
        Calendar todayCal = Calendar.getInstance();
        Calendar expCal = HomeFragment.expCalendar;
        expCal.set(Calendar.HOUR_OF_DAY, sp.getInt("hour", 22));
        expCal.set(Calendar.MINUTE, sp.getInt("minute", 00));
        Log.d(TAG , "alarm set expCal : " + expCal.getTime());
        int alarmCheck = cosmetics.getAlarm();

        Intent intent = new Intent(getContext(), MyService.class);
        intent.putExtra("productName", cosmetics.getProductName());

        //알림 설정 - 한주전 또는 모든 알림 설정 시
        if((alarmCheck == 1 || alarmCheck == 3) ){
            expCal.add(Calendar.DATE, -7);
            setSeconds(todayCal, expCal);

            if(expCal.compareTo(todayCal) == 0){
                intent.putExtra("cbWeekOn", true);
                intent.putExtra("weekCid", cosmetics.getId());
                getContext().startService(intent);

                if(alarmCheck == 1) {//알림 아무것도 없이 업데이트
                    updateAlarm(0, cosmetics);
                    Log.d(TAG , "notiAlarm : " + cosmetics.getAlarm());
                }
                else if (alarmCheck == 3) {//알림 한달전만 남도록 업데이트
                    updateAlarm(2, cosmetics);
                    Log.d(TAG , "notiAlarm : " + cosmetics.getAlarm());
                }
            }
            expCal.add(Calendar.DATE, 7);
        }

        //알림 설정 - 한달전 또는 모든 알림 설정 시
        if((alarmCheck == 2 || alarmCheck == 3)){
            expCal.add(Calendar.DATE, -30);
            setSeconds(todayCal, expCal);

            if(expCal.compareTo(todayCal) == 0){
                intent.putExtra("cbMonthOn", true);
                intent.putExtra("monthCid", cosmetics.getId());
                getContext().startService(intent);

                if(alarmCheck == 2){//알림 아무것도 없이 업데이트
                    updateAlarm(0, cosmetics);
                    Log.d(TAG , "notiAlarm : " + cosmetics.getAlarm());
                }
                else if (alarmCheck == 3) {//알림 한주전만 남도록 업데이트
                    updateAlarm(1, cosmetics);
                    Log.d(TAG , "notiAlarm : " + cosmetics.getAlarm());
                }
            }
            expCal.add(Calendar.DATE, 30);
        }


        //사용 기간 만료시 리스트에서 삭제
        setSeconds(todayCal, expCal);
        if(expCal.compareTo(todayCal) == 0){
            deleteData(cosmetics);
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

        Button btComplete = convertView.findViewById(R.id.btComplete);
        btComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                // 삭제
                                addData(cosmetics.getBrandName(), cosmetics.getProductName(), cosmetics.getDtOpen(),
                                        cosmetics.getDtExp(), cosmetics.getKind(), cosmetics.getVolume(), cosmetics.getAdditionalContent());
                                deleteData(cosmetics);
                            }
                        })
                        .setNegativeButton("NO", null)
                        .setCancelable(false)
                        .setTitle("사용완료 처리 하시겠습니까? ")
                        .setMessage("선택된 제품 : " + cosmetics.getProductName())
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


    /* 알림 설정 수정 */
    private void updateAlarm(int newAlarm, Cosmetics cosmetic) {
        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(this.context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // 삭제 (id(tID) 값으로 삭제)
            Object[] args = { newAlarm, cosmetic.getId() };
            String sql = "UPDATE cosmetics SET alarm = ? WHERE cID = ?";

            db.execSQL(sql, args);

            Log.d(TAG , "notiUpdateAlarm : " + cosmetic.getAlarm());

        } catch (SQLException e) {
            Log.d(TAG , "notiUpdateAlarm error : " + e.getMessage());
        }

        db.close();
    }


    public void setSeconds(Calendar today, Calendar exp){
        exp.set(Calendar.SECOND, 00);
        today.set(Calendar.SECOND, 00);
        exp.set(Calendar.MILLISECOND, 00);
        today.set(Calendar.MILLISECOND, 00);
    }

    /* MY PAGE 추가 */
    private void addData(String brand, String name, String open, String exp, String kind, String volume, String additionalContent) {
        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 등록
            Object[] args = {brand, name, open, exp, kind, volume, additionalContent};
            String sql = mMyPageInseartSql;

            db.execSQL(sql, args);

        } catch (SQLException e) {
        }

        db.close();
    }
}