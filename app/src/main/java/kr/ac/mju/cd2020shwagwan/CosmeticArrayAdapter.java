package kr.ac.mju.cd2020shwagwan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import kr.ac.mju.cd2020shwagwan.Notification.MyService;
import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.search.SearchActivity;
import kr.ac.mju.cd2020shwagwan.ui.home.HomeFragment;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class CosmeticArrayAdapter extends ArrayAdapter {

    private Context caaContext;
    private ArrayList caaArrList;
    public static TextView caaTvKind;
    static ProgressBar caaPbUsage;

    String insertMypageSql = "INSERT INTO "+DBHelper.TABLE_MYPAGE+"(brandName, productName, dtOpen, dtExp, kind,  volume, additionalContent) VALUES(?,?,?,?,?,?,?)";

    public CosmeticArrayAdapter(Context context, ArrayList items) {
        super(context, 0, items);

        this.caaContext = context;
        this.caaArrList = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cosmetic_item, parent, false);
        }
        final Cosmetics caaCos = (Cosmetics) getItem(position);

        TextView caaTvBrand = convertView.findViewById(R.id.ci_tvBrand);
        TextView caaTvName = convertView.findViewById(R.id.ci_tvName);
        TextView caaTvOpen = convertView.findViewById(R.id.ci_tvOpen);
        TextView caaTvExp = convertView.findViewById(R.id.ci_tvExp);
        caaTvKind = convertView.findViewById(R.id.ci_tvKind);
        caaPbUsage = convertView.findViewById(R.id.ci_pbUsage);


        caaTvBrand.setText(caaCos.getBrandName());
        caaTvName.setText(caaCos.getProductName());
        caaTvOpen.setText(caaCos.getDtOpen());
        caaTvExp.setText(caaCos.getDtExp());
        caaTvKind.setText(caaCos.getKind());

//        TextView ciOpenTextView = convertView.findViewById(R.id.ci_tvOpen_text);
//        TextView ciExpTextView = convertView.findViewById(R.id.ci_tvExp_text);
//
//        ciOpenTextView.setVisibility(View.GONE);
//        ciExpTextView.setVisibility(View.GONE);

        Date caaDtExp = new Date(), caaDtToday = new Date();

        Log.d("확인", "용량 = " + caaCos.getVolume() + " 알람 = " + caaCos.getAlarm());

        // 프로그레스바 설정
        try{

            String caaStrOpen = caaCos.getDtOpen();
            SimpleDateFormat caaSdfTrans = new SimpleDateFormat("yyyy-MM-dd");
            Date caaDtOpen = caaSdfTrans.parse(caaStrOpen);

            String caaStrExp = caaCos.getDtExp();
            caaDtExp = caaSdfTrans.parse(caaStrExp);

            long caaPeriod = caaDtExp.getTime() - caaDtOpen.getTime();
            long caaPeriodDay = caaPeriod / (24 * 60 * 60 * 1000);

            caaPbUsage.setMax((int)caaPeriodDay);

            long caaUnixTimeNow = System.currentTimeMillis();
            caaDtToday = new Date(caaUnixTimeNow);

            long usage = caaDtToday.getTime() - caaDtOpen.getTime();
            long usageDay = usage / (24 * 60 * 60 * 1000);
            Log.d(TAG , "pb - usageDay : " + usageDay);

            caaPbUsage.setProgress((int) usageDay);
            Log.d(TAG , "pb - caaPbUsage : " + caaPbUsage.getProgress());


        }catch(Exception e){
            Log.d(TAG , " pb - error : " + e.getMessage());
        }

        SharedPreferences caaSPAlarm = getContext().getSharedPreferences("alarmTime", MODE_PRIVATE);
        Calendar caaCalToday = Calendar.getInstance();
        try{
            Calendar caaCalAlarm = CalendarFromString(caaCos.getDtExp());
            Log.d(TAG , " caaCos - getDtExp : " + caaCos.getDtExp());
            Log.d(TAG , " caaCos - caaCalAlarm : " + caaCalAlarm.getTime());
            caaCalAlarm.set(Calendar.HOUR_OF_DAY, caaSPAlarm.getInt("hour", 22));
            caaCalAlarm.set(Calendar.MINUTE, caaSPAlarm.getInt("minute", 00));
            Log.d(TAG , "alarm set caaCalAlarm : " + caaCalAlarm.getTime());
            int caaAlarmCheck = caaCos.getAlarm();

            Intent caaIntent = new Intent(getContext(), MyService.class);
            caaIntent.putExtra("productName", caaCos.getProductName());
            caaIntent.putExtra("cid", caaCos.getId());

            //알림 설정 - 한주전 또는 모든 알림 설정 시
            if((caaAlarmCheck == 1 || caaAlarmCheck == 3) ){
                caaCalAlarm.add(Calendar.DATE, -7);
                setSeconds(caaCalToday, caaCalAlarm);

                if(caaCalAlarm.compareTo(caaCalToday) == 0){
                    caaIntent.putExtra("cbWeekOn", true);
                    caaIntent.putExtra("weekCid", caaCos.getId());
                    getContext().startService(caaIntent);

                    if(caaAlarmCheck == 1) {//알림 아무것도 없이 업데이트
                        updateAlarm(0, caaCos);
                        Log.d(TAG , "notiAlarm : " + caaCos.getAlarm());
                    }
                    else if (caaAlarmCheck == 3) {//알림 한달전만 남도록 업데이트
                        updateAlarm(2, caaCos);
                        Log.d(TAG , "notiAlarm : " + caaCos.getAlarm());
                    }
                }
                caaCalAlarm.add(Calendar.DATE, 7);
            }

            //알림 설정 - 한달전 또는 모든 알림 설정 시
            if((caaAlarmCheck == 2 || caaAlarmCheck == 3)){
                caaCalAlarm.add(Calendar.DATE, -30);
                setSeconds(caaCalToday, caaCalAlarm);

                if(caaCalAlarm.compareTo(caaCalToday) == 0){
                    caaIntent.putExtra("cbMonthOn", true);
                    caaIntent.putExtra("monthCid", caaCos.getId());
                    getContext().startService(caaIntent);

                    if(caaAlarmCheck == 2){//알림 아무것도 없이 업데이트
                        updateAlarm(0, caaCos);
                        Log.d(TAG , "notiAlarm : " + caaCos.getAlarm());
                    }
                    else if (caaAlarmCheck == 3) {//알림 한주전만 남도록 업데이트
                        updateAlarm(1, caaCos);
                        Log.d(TAG , "notiAlarm : " + caaCos.getAlarm());
                    }
                }
                caaCalAlarm.add(Calendar.DATE, 30);
            }
        }catch(Exception e){
            Log.d(TAG, "caaCos  e - "+ e.getMessage());
        }


        //사용 기간 만료시 리스트에서 삭제
        Calendar caaCalExp = Calendar.getInstance();
        try{
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date tempDate = sdFormat.parse(caaCos.getDtExp());
            Log.d(TAG , "alarm set expdate : " + tempDate);
            caaCalExp.setTime(tempDate);
        }catch (Exception e){
            Log.d(TAG , "str to cal err : " + e.getMessage());
        }
        caaCalExp.set(Calendar.HOUR_OF_DAY, caaSPAlarm.getInt("hour", 22));
        caaCalExp.set(Calendar.MINUTE, caaSPAlarm.getInt("minute", 00));
        setSeconds(caaCalToday, caaCalExp);
        Log.d(TAG , "alarm set today : " + caaCalToday.getTime());
        if (caaCalExp.compareTo(caaCalToday) <= 0) {
            Log.d(TAG , "alarm set compareTo in");
            moveMypage(caaCos);
        }

        ImageButton caaIvDel = convertView.findViewById(R.id.ci_ivDel);
        caaIvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 삭제
                new AlertDialog.Builder(caaContext,  R.style.MyAlertDialogStyle)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                // 삭제
                                deleteData(caaCos);
                            }
                        })
                        .setNegativeButton("NO", null)
                        .setCancelable(false)
                        .setTitle("상품을 삭제하시겠습니까? ")
                        .setMessage("선택된 제품 : " +caaCos.getProductName())
                        .show();
            }
        });

        ImageButton btComplete = convertView.findViewById(R.id.ci_btUsed);
        btComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(caaContext, R.style.MyAlertDialogStyle)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                // 삭제
                                moveMypage(caaCos);
                            }
                        })
                        .setNegativeButton("NO", null)
                        .setCancelable(false)
                        .setTitle("사용완료 처리를 하시겠습니까? ")
                        .setMessage("선택된 제품 : " + caaCos.getProductName())
                        .show();
            }
        });



        LinearLayout caaLayout = convertView.findViewById(R.id.ci_layout);

        caaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", caaCos.getId());
                intent.putExtra("check", "home");
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    /* 삭제 */
    private void deleteData(Cosmetics cosmetics) {
        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(this.caaContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 삭제 (id(tID) 값으로 삭제)
            Object[] args = { cosmetics.getId() };
            String sql = "DELETE FROM cosmetics WHERE cID = ?";

            db.execSQL(sql, args);

            // 항목 삭제
            this.caaArrList.remove(cosmetics);
            // 리스트 적용
            notifyDataSetChanged();

        } catch (SQLException e) {}

        db.close();
    }


    /* 알림 설정 수정 */
    private void updateAlarm(int newAlarm, Cosmetics cosmetic) {
        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(this.caaContext);
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
            String sql = insertMypageSql;

            db.execSQL(sql, args);

        } catch (SQLException e) {
        }

        db.close();
    }

    /* cosmetics에서 삭제, mypage에 추가 */
    private void moveMypage(Cosmetics cos){
        addData(cos.getBrandName(), cos.getProductName(), cos.getDtOpen(),
                cos.getDtExp(), cos.getKind(), cos.getVolume(), cos.getAdditionalContent());
        deleteData(cos);
    }

    public static Calendar CalendarFromString(String date)
    {
        Calendar cal = Calendar.getInstance();

        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            cal.setTime(formatter.parse(date));
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
        return cal;
    }


}