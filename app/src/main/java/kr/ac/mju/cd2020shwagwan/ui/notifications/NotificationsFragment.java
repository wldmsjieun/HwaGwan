package kr.ac.mju.cd2020shwagwan.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.kyleduo.switchbutton.SwitchButton;

import kr.ac.mju.cd2020shwagwan.R;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    private View mRoot;

    private AlarmTimeSet mAlarmTimeSetDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        mRoot = inflater.inflate(R.layout.fragment_notifications, container, false);

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