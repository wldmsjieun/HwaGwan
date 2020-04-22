package kr.ac.mju.cd2020shwagwan;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import kr.ac.mju.cd2020shwagwan.ui.home.HomeFragment;

import static android.content.Context.MODE_PRIVATE;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
//
//            // on device boot complete, reset the alarm
//            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
//
//            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
////
//
//            SharedPreferences sharedPreferences = context.getSharedPreferences("month alarm", MODE_PRIVATE);
//            long millis = sharedPreferences.getLong("monthNotifyTime", Calendar.getInstance().getTimeInMillis());
//
//
//            Calendar notifyTime = Calendar.getInstance();
//            notifyTime.set(HomeFragment.expCalendar.get(Calendar.YEAR), HomeFragment.expCalendar.get(Calendar.MONTH), HomeFragment.expCalendar.get(Calendar.DAY_OF_MONTH));
//            notifyTime.add(Calendar.DATE, -30);
//            notifyTime.setTimeInMillis(sharedPreferences.getLong("nextNotifyTime", millis));
//
//
////            Date currentDateTime = nextNotifyTime.getTime();
////            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
//
//
//            if (manager != null) {
//                manager.setRepeating(AlarmManager.RTC_WAKEUP, notifyTime.getTimeInMillis(),
//                        AlarmManager.INTERVAL_DAY, pendingIntent);
//            }
//        }
    }
}
