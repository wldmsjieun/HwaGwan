package kr.ac.mju.cd2020shwagwan.ui.MyPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


import kr.ac.mju.cd2020shwagwan.R;

public class MypageFragment extends Fragment {

    private MypageViewModel mypageViewModel;

    private View mRoot;

    private AlarmTimeSet mAlarmTimeSetDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mypageViewModel =
                ViewModelProviders.of(this).get(MypageViewModel.class);
        mRoot = inflater.inflate(R.layout.fragment_mypage, container, false);

//        notificationsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//
//            }
//        });

        setSwitch();

        setAlarmTime();

        return mRoot;
    }

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

    void setAlarmTime() {
        Button alarmTimeSet = (Button) mRoot.findViewById(R.id.my_page_alarm_time_set_button);

        mAlarmTimeSetDialog = new AlarmTimeSet(getContext());

        alarmTimeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlarmTimeSetDialog.show();
            }
        });
    }
}