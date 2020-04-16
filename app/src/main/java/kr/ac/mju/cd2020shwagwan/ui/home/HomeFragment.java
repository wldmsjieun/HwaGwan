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
    private EditText edOpen;
    private Button btOpen;
    int year=0;
    int month=0;
    int day=0;
    String str;
    private DatePickerDialog.OnDateSetListener callbackMethod;

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

        // 오늘 날짜로 설정
        edOpen = layout.findViewById(R.id.edOpen);
        btOpen = layout.findViewById(R.id.btOpen);
        long now = System.currentTimeMillis();
        Date dt = new Date(now);
//        Toast.makeText(getContext(), dt.toString(), Toast.LENGTH_SHORT).show();
        sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = sdfNow.format(dt);
        edOpen.setText(formatDate);


        btOpen.setOnClickListener(new CalendarView.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDate();
            }
        });



//        edOpen.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
//        edOpen.setInputType(android.text.InputType.TYPE_CLASS_PHONE);

//        edOpen.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // 입력되는 텍스트에 변화가 있을 때
//                if( edOpen.isFocusable()){
//                    str = PhoneNumberUtils.formatNumber(s.toString());
//
////                    edOpen.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
////                    edOpen.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
//                    Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
////                    edOpen.setText(s.toString());
//
//
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable arg0) {
//                // 입력이 끝났을 때
//                if(str.length() == 10){
////                    Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
////                    Toast.makeText(getContext(), edOpen.getText(), Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // 입력하기 전에
//            }
//
//        });



        edOpen.addTextChangedListener(new TextWatcher() {

            private int _beforeLenght = 0;
            private int _afterLenght = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                _beforeLenght = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    Log.d("addTextChangedListener", "onTextChanged: Intput text is wrong (Type : Length)");
                    return;
                }

                char inputChar = s.charAt(s.length() - 1);
                if (inputChar != '-' && (inputChar < '0' || inputChar > '9')) {
                    edOpen.getText().delete(s.length() - 1, s.length());
                    Log.d("addTextChangedListener", "onTextChanged: Intput text is wrong (Type : Number)");
                    return;
                }

                _afterLenght = s.length();

                // 삭제 중
                if (_beforeLenght > _afterLenght) {
                    // 삭제 중에 마지막에 -는 자동으로 지우기
                    if (s.toString().endsWith("-")) {
                        edOpen.setText(s.toString().substring(0, s.length() - 1));
                    }
                }
                // 입력 중
                else if (_beforeLenght < _afterLenght) {
                    if (_afterLenght == 5 && s.toString().indexOf("-") < 0) {
                        edOpen.setText(s.toString().subSequence(0, 4) + "-" + s.toString().substring(4, s.length()));
                    } else if (_afterLenght == 8) {
                        edOpen.setText(s.toString().subSequence(0, 7) + "-" + s.toString().substring(7, s.length()));
//                    } else if (_afterLenght == 9) {
//                        edOpen.setText(s.toString().subSequence(0, 8) + "-" + s.toString().substring(8, s.length()));
//                    }
                    }
                }
                edOpen.setSelection(edOpen.length());

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 생략
            }
        });


        출처: https://devvkkid.tistory.com/111 [개발자입니까?]


        /* 추가폼에 데이터 입력 */
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

//                        if(str.length() == 10){
//                            Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
//
//                            edOpen.setText(str);
//                        }


                        // 데이터 추가
                         addData(brand, name);
                    }
                })
                //.setNegativeButton("CANCEL", null)
                //.setCancelable(false)
                .setCancelable(true)
                .setTitle("Add new TODO")
                .setView(layout)
                .show();
    }


    /* 버튼 클릭하면 캘린더 보여줌 */
    void showDate() {
        callbackMethod = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d)
            {
                year = y;
                month = m+1;
                day = d;

                StringBuilder from = new StringBuilder();
                from.append(year);
                from.append("-");
                from.append(month);
                from.append("-");
                from.append(day);

                Toast.makeText(getContext(), from, Toast.LENGTH_SHORT).show();
                SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-M-dd");

                try {
                    Date to = transFormat.parse(from.toString());
                    Toast.makeText(getContext(), to.toString(), Toast.LENGTH_SHORT).show();
                    sdfNow = new SimpleDateFormat("yyyy-MM-dd");
                    String formatDate = sdfNow.format(to);
                    edOpen.setText(formatDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                }


            }
        };


        Calendar cal = Calendar.getInstance();

        int presentYear = cal.get(Calendar.YEAR);
        int presentMonth = cal.get(Calendar.MONTH);
        int presentDay = cal.get(Calendar.DATE);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), callbackMethod, presentYear, presentMonth, presentDay);

        dialog.show();

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