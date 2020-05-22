package kr.ac.mju.cd2020shwagwan.ui.MyPage;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import kr.ac.mju.cd2020shwagwan.R;
import static android.content.Context.MODE_PRIVATE;

public class MypageFragment extends Fragment {

    private MypageViewModel mfMyPageViewModel;

    private View mfRoot;
    static public TextView mfTvHour;
    static public TextView mfTvMinute;
    int mfSetHour =22, mfSetMin =0;
    SharedPreferences mfSp;
    View mfLayout;

    private FragmentManager mfFragmentManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mfRoot = inflater.inflate(R.layout.fragment_mypage, container, false);
        mfLayout = inflater.inflate(R.layout.alarm_time_set, null);
        mfSp = getContext().getSharedPreferences("alarmTime", MODE_PRIVATE);

        mfTvHour = mfLayout.findViewById(R.id.ats_tvHour);
        mfTvMinute = mfLayout.findViewById(R.id.ats_tvMinute);

        mfTvHour.setText(String.valueOf(mfSp.getInt("hour", 22)));
        mfTvMinute.setText(String.valueOf(mfSp.getInt("minute", 00)));

        mfFragmentManager = getActivity().getSupportFragmentManager();
        mfMyPageViewModel = ViewModelProviders.of(this).get(MypageViewModel.class);

        setCompletedUse();

        setAlarmTime();

        return mfRoot;
    }

    void setCompletedUse() {
        Button mfBtUsed = (Button) mfRoot.findViewById(R.id.fm_btnUsed);
        mfBtUsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mfIntent = new Intent(getActivity(), UsedPage.class);
                startActivity(mfIntent);
            }
        });
    }

    //알람 시각을 설정할 수 있는 함수
    void setAlarmTime() {
        Button mfBtAlarmTimeSet = (Button) mfRoot.findViewById(R.id.fm_btnAlarm);

        mfBtAlarmTimeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout mfLinearlayout = mfLayout.findViewById(R.id.ats_layout);

                mfLinearlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Log.d("alarm set picker hour", String.valueOf(mfSetHour));
                                Log.d("alarm set picker minute", String.valueOf(mfSetMin));
                                mfTvHour.setText(Integer.toString(hourOfDay));
                                mfTvMinute.setText(Integer.toString(minute));
                                mfSetHour = hourOfDay;
                                mfSetMin = minute;

                            }
                        },  Integer.parseInt(mfTvHour.getText().toString()), Integer.parseInt(mfTvMinute.getText().toString()),true);

                        timePickerDialog.show();

                    }
                });

                if (mfLayout.getParent() != null){
                    ((ViewGroup) mfLayout.getParent()).removeView(mfLayout);
                }

                new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
                        .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = mfSp.edit();
                                Log.d("alarm set save hour", String.valueOf(mfSetHour));
                                Log.d("alarm set save minute", String.valueOf(mfSetMin));
                                editor.putInt("hour", mfSetHour);
                                editor.putInt("minute", mfSetMin);
                                editor.commit();
                            }
                        })
                        .setCancelable(true)
                        .setTitle("현재 설정된 알림 시각")
                        .setView(mfLayout)
                        .show();
            }
        });
    }
}