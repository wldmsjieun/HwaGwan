package kr.ac.mju.cd2020shwagwan.ui.home;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kr.ac.mju.cd2020shwagwan.Cosmetics;
import kr.ac.mju.cd2020shwagwan.CustomArrayAdapter;
import kr.ac.mju.cd2020shwagwan.DBHelper;
import kr.ac.mju.cd2020shwagwan.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ListView listView;
    private CustomArrayAdapter adapter;

    private ArrayList<Cosmetics> items;
    private SimpleDateFormat sdfNow;
    private EditText edOpen, edExp;
    int openYear=0, openMonth=0, openDay=0;


    Calendar openCalendar = Calendar.getInstance();
    Calendar expCalendar = Calendar.getInstance();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final FloatingActionButton fabAdd = root.findViewById(R.id.fabAdd);
        listView = root.findViewById(R.id.lvItem);


        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                fabAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAddDialog();
                    }
                });
            }
        });
        return root;
    }



    /* 추가폼 호출 */
    private void showAddDialog() {
        // AlertDialog View layout
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.content_add, null);

        // findById
        edExp = layout.findViewById(R.id.edExp);


        // 개봉일 현재 날짜로 설정
        setToday(layout);


        // 개봉일 캘린더로 선택
        edOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        openCalendar.set(Calendar.YEAR, year);
                        openCalendar.set(Calendar.MONTH, month);
                        openCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "yyyy-MM-dd";    // 출력형식   2018-11-18
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

                        edOpen.setText(sdf.format(openCalendar.getTime()));
                    }
                };

                openYear = openCalendar.get(Calendar.YEAR);
                openMonth = openCalendar.get(Calendar.MONTH);
                openDay = openCalendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(getContext(), myDatePicker, openYear, openMonth, openDay)
                        .show();
            }
        });


        // 만료일 캘린더로 선택 및 최소날짜 세팅
        edExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        expCalendar.set(Calendar.YEAR, year);
                        expCalendar.set(Calendar.MONTH, month);
                        expCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "yyyy-MM-dd";    // 출력형식   2018-11-18
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

                        edExp.setText(sdf.format(expCalendar.getTime()));
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), myDatePicker, expCalendar.get(Calendar.YEAR), expCalendar.get(Calendar.MONTH), expCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(openCalendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });


        // 추가폼에 데이터 입력
        new AlertDialog.Builder(getContext())
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        // 추가
                        String brand = ((EditText) layout.findViewById(R.id.etBrand)).getText().toString();
                        if (TextUtils.isEmpty(brand)) {
                            Toast.makeText(getContext(), "Brand empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String name = ((EditText) layout.findViewById(R.id.etName)).getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(getContext(), "Name empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 데이터 추가
                         addData(brand, name);
                    }
                })
                .setCancelable(true)
                .setTitle("사용중인 내 화장품 추가")
                .setView(layout)
                .show();
    }



    /* 개봉일 현재 날짜로 설정 */
    public void setToday (View layout) {
        edOpen = layout.findViewById(R.id.edOpen);
        long now = System.currentTimeMillis();
        Date dt = new Date(now);
        sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = sdfNow.format(dt);
        edOpen.setText(formatDate);
    }


    /* 리스트 구성 */
    private void listData() {
        this.items = new ArrayList<>();

        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 쿼리문
            String sql = "SELECT cID, brand, name FROM cosmetics";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                // 데이터
                Cosmetics cosmetic = new Cosmetics(cursor.getInt(cursor.getColumnIndex("cID")),
                        cursor.getString(cursor.getColumnIndex("brand")), cursor.getString(cursor.getColumnIndex("name")));

                this.items.add(cosmetic);
            }

            cursor.close();
        } catch (SQLException e) {}

        db.close();

        // 리스트 구성
        this.adapter = new CustomArrayAdapter(getContext(), this.items);
        this.listView.setAdapter(this.adapter);
    }



    /* 추가 */
    private void addData(String brand, String name) {
        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 등록
            Object[] args = { brand, name };
            String sql = "INSERT INTO cosmetics(brand, name) VALUES(?,?)";

            db.execSQL(sql, args);

            // 리스트 새로고침
            listData();

        } catch (SQLException e) {}

        db.close();
    }


}