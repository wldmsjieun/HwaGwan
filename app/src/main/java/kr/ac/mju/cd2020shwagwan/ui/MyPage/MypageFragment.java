package kr.ac.mju.cd2020shwagwan.ui.MyPage;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import kr.ac.mju.cd2020shwagwan.R;

import static android.content.Context.MODE_PRIVATE;

public class MypageFragment extends Fragment {

    private View mRoot;
    static public TextView tvHour;
    static public TextView tvMinute;
    int setHour=22, setMin=0;
    SharedPreferences sp;
    View layout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRoot = inflater.inflate(R.layout.fragment_mypage, container, false);
        layout = inflater.inflate(R.layout.alarm_time_set, null);
        sp = getContext().getSharedPreferences("alarmTime", MODE_PRIVATE);

        tvHour = layout.findViewById(R.id.dialog_hour);
        tvMinute = layout.findViewById(R.id.dialog_minute);

        tvHour.setText(String.valueOf(sp.getInt("hour", 22)));
        tvMinute.setText(String.valueOf(sp.getInt("minute", 00)));

        setSwitch();
        setAlarmTime();
        return mRoot;
    }

    //활성 판단 함수
    void setSwitch() {
        SwitchCompat pushAlarmButton = (SwitchCompat) mRoot.findViewById(R.id.my_page_push_alarm_button);
        pushAlarmButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getActivity(), "활성화", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "비활성화", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //알람 시각을 설정할 수 있는 함수
    void setAlarmTime() {
        Button alarmTimeSet = (Button) mRoot.findViewById(R.id.my_page_alarm_time_set_button);
        alarmTimeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout timeSetButton = layout.findViewById(R.id.dialog_alarm_time_set_button);

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

                if (layout.getParent() != null){
                    ((ViewGroup) layout.getParent()).removeView(layout);
                }

                new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                        .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = sp.edit();
                                Log.d("save hour", String.valueOf(setHour));
                                Log.d("save minute", String.valueOf(setMin));
                                editor.putInt("hour", setHour);
                                editor.putInt("minute", setMin);
                                editor.commit();
                            }
                        })
                        .setCancelable(true)
                        .setTitle("현재 설정된 알림 시각")
                        .setView(layout)
                        .show();
            }
        });
    }
}