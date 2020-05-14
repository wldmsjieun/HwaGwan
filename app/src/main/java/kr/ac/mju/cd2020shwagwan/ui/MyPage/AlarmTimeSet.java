package kr.ac.mju.cd2020shwagwan.ui.MyPage;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import kr.ac.mju.cd2020shwagwan.R;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class AlarmTimeSet extends Dialog {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        // WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        // layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        // layoutParams.dimAmount = 0.8f;
        //getWindow().setAttributes(layoutParams);

//        setContentView(R.layout.alarm_time_set);

//        setTime();
        showAddDialog();

    }

    void setTime() {

    }

    public void showAddDialog() {


    }

    public AlarmTimeSet(@NonNull Context context) {
        super(context);
    }
}