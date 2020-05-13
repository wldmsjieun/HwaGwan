package kr.ac.mju.cd2020shwagwan.ui.MyPage;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import kr.ac.mju.cd2020shwagwan.R;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class AlarmTimeSet extends Dialog {
    static public TextView tvHour;
    static public TextView tvMinute;
    int setHour=22, setMin=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.alarm_time_set);

        setTime();

        
    }

    void setTime() {
        LinearLayout timeSetButton = (LinearLayout) findViewById(R.id.dialog_alarm_time_set_button);
        Button btnSave = findViewById(R.id.btnSave);
        tvHour = (TextView) findViewById(R.id.dialog_hour);
        tvMinute = (TextView) findViewById(R.id.dialog_minute);

        final SharedPreferences sp = getContext().getSharedPreferences("alarmTime", MODE_PRIVATE);

        timeSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                     tvHour.setText(Integer.toString(hourOfDay));
                     tvMinute.setText(Integer.toString(minute));
                     setHour = hourOfDay;
                     setMin = minute;

                    }
                },  Integer.parseInt(tvHour.getText().toString()), Integer.parseInt(tvMinute.getText().toString()),true);

                timePickerDialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             SharedPreferences.Editor editor = sp.edit();
             editor.putInt("hour", setHour);
             editor.putInt("minute", setMin);
             editor.commit();
            }
        });
    }

    public AlarmTimeSet(@NonNull Context context) {
        super(context);
    }
}
