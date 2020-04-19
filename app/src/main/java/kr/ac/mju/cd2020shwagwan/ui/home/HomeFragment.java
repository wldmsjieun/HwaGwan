package kr.ac.mju.cd2020shwagwan.ui.home;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kr.ac.mju.cd2020shwagwan.Cosmetics;
import kr.ac.mju.cd2020shwagwan.CustomArrayAdapter;
import kr.ac.mju.cd2020shwagwan.DBHelper;
import kr.ac.mju.cd2020shwagwan.R;
import kr.ac.mju.cd2020shwagwan.ScanBarcode;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ListView listView;
    private CustomArrayAdapter adapter;

    private View mRoot;

    private String mSql;

    private Spinner mSpinner;

    private ArrayList<Cosmetics> items;
    private SimpleDateFormat sdfNow;
    private EditText edOpen, edExp;
    int openYear=0, openMonth=0, openDay=0;
    TextView tvComment;
    FloatingActionButton fabBarcode;
    Calendar openCalendar = Calendar.getInstance();
    Calendar expCalendar = Calendar.getInstance();

    static int initPeriod = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mRoot = inflater.inflate(R.layout.fragment_home, container, false);


        final FloatingActionButton fabAdd = mRoot.findViewById(R.id.fabAdd);
        listView = mRoot.findViewById(R.id.lvItem);
        listData();

        setSpinner();

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

        //바코드 스캔 실행 버튼
        fabBarcode= mRoot.findViewById(R.id.fabBarcode);
        fabBarcode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getContext(), ScanBarcode.class);
                startActivity(intent);
            }
        });
        return mRoot;
    }


    /* 추가폼 호출 */
    private void showAddDialog() {
        // AlertDialog View layout
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.content_add, null);

        //스피너 생성
        final Spinner spKinds = layout.findViewById(R.id.spKinds);
        ArrayAdapter kindsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.product_kinds_array, android.R.layout.simple_spinner_item);
        kindsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKinds.setAdapter(kindsAdapter);

        // findViewById
        edOpen = layout.findViewById(R.id.edOpen);
        edExp = layout.findViewById(R.id.edExp);
        tvComment = layout.findViewById(R.id.tvComment);

        // 개봉일 현재 날짜로 설정
        setToday(edOpen);


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

                        //상품별 만료일 자동 계산
                        if(spKinds.getSelectedItemPosition() == 1 || spKinds.getSelectedItemPosition() == 2
                                || spKinds.getSelectedItemPosition() == 11 || spKinds.getSelectedItemPosition() == 12
                                || spKinds.getSelectedItemPosition() == 13 || spKinds.getSelectedItemPosition() == 14){
                            //6개월 이내
                            openCalendar.add(Calendar.DATE, 180);
                            edExp.setText(sdf.format(openCalendar.getTime()));
                        }else if(spKinds.getSelectedItemPosition() == 3){
                            //8개월 이내
                            openCalendar.add(Calendar.DATE, 240);
                            edExp.setText(sdf.format(openCalendar.getTime()));
                        }else if(spKinds.getSelectedItemPosition() == 0 || spKinds.getSelectedItemPosition() == 4
                                || spKinds.getSelectedItemPosition() == 5 || spKinds.getSelectedItemPosition() == 6
                                || spKinds.getSelectedItemPosition() == 8 || spKinds.getSelectedItemPosition() == 9
                                ||spKinds.getSelectedItemPosition() == 10 || spKinds.getSelectedItemPosition() == 15 ){
                            //1년 이내
                            openCalendar.add(Calendar.DATE, 365);
                            edExp.setText(sdf.format(openCalendar.getTime()));
                        }else if(spKinds.getSelectedItemPosition() == 7){
                            //2년 이내
                            openCalendar.add(Calendar.DATE, 730);
                            edExp.setText(sdf.format(openCalendar.getTime()));
                        }else if(spKinds.getSelectedItemPosition() == 16){
                            Toast ts = Toast.makeText(getContext(), "만료일을 설정하세요", Toast.LENGTH_SHORT);
                            ts.setGravity(Gravity.CENTER, 0,0);
                            ts.show();
                            edExp.setText("");
                        }
                    }
                };

                openYear = openCalendar.get(Calendar.YEAR);
                openMonth = openCalendar.get(Calendar.MONTH);
                openDay = openCalendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(getContext(), myDatePicker, openYear, openMonth, openDay).show();

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


        spKinds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                etExp.setText("Exp :" + position + parent.getItemAtPosition(position));
                Calendar cCal = Calendar.getInstance();
                String myFormat = "yyyy-MM-dd";    // 출력형식   2018-11-18
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
                if(position == 1 || position == 2 ||position == 11 || position == 12 || position == 13 || position == 14) {
                    //6개월 이내
                    //자외선 차단제,  립밤,           립스틱,         립글로스,          아이라이너,        마스카라
                    cCal.add(Calendar.DATE, 180);
                    edExp.setText(sdf.format(cCal.getTime()));
                    tvComment.setText("사용 권장 기한 : 6개월 이내");
                }else if(position == 3){
                    //8개월 이내
                    //에센스
                    edExp.setText("Exp : 8개월 이내");
                    cCal.add(Calendar.DATE, 240);
                    edExp.setText(sdf.format(cCal.getTime()));
                    tvComment.setText("사용 권장 기한 : 8개월 이내");
                }
                else if(position == 0 || position == 4 || position == 5 || position == 6 || position == 8 || position == 9 || position == 10 ||position == 15) {
                    //1년 이내
                    //스킨,              크림,             메이크업 베이스,   컨실러,           아이새도우,        아이브로우,      블러셔,           클렌저
                    cCal.add(Calendar.DATE, 365);
                    edExp.setText(sdf.format(cCal.getTime()));
                    tvComment.setText("사용 권장 기한 : 1년 이내");
                }else if(position == 7 ){
                    //2년 이내
                    //파우더
                    cCal.add(Calendar.DATE, 730);
                    edExp.setText(sdf.format(cCal.getTime()));
                    tvComment.setText("사용 권장 기한 : 2년 이내");
                }else if(position == 16){
                    edExp.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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


                        if(spKinds.getSelectedItemPosition() == 1 || spKinds.getSelectedItemPosition() == 2
                                || spKinds.getSelectedItemPosition() == 11 || spKinds.getSelectedItemPosition() == 12
                                || spKinds.getSelectedItemPosition() == 13 || spKinds.getSelectedItemPosition() == 14){
                            //6개월 이내
                            initPeriod = 180;
                        }else if(spKinds.getSelectedItemPosition() == 3){
                            //8개월 이내
                            initPeriod = 240;
                        }else if(spKinds.getSelectedItemPosition() == 0 || spKinds.getSelectedItemPosition() == 4
                                || spKinds.getSelectedItemPosition() == 5 || spKinds.getSelectedItemPosition() == 6
                                || spKinds.getSelectedItemPosition() == 8 || spKinds.getSelectedItemPosition() == 9
                                ||spKinds.getSelectedItemPosition() == 10 || spKinds.getSelectedItemPosition() == 15 ){
                            //1년 이내
                            initPeriod = 365;
                        }else if(spKinds.getSelectedItemPosition() == 7){
                            //2년 이내
                            initPeriod = 730;
                        }else if(spKinds.getSelectedItemPosition() == 16){
                            initPeriod = 365;
                        }


                        // 데이터 추가
                        addData(brand, name, edOpen.getText().toString(), edExp.getText().toString(), spKinds.getSelectedItem().toString(), initPeriod);
                    }
                })
                .setCancelable(true)
                .setTitle("화장품 추가 관리")
                .setView(layout)
                .show();
    }



    /* 개봉일 현재 날짜로 설정 */
    public void setToday (EditText ed) {

        long now = System.currentTimeMillis();
        Date dt = new Date(now);
        sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = sdfNow.format(dt);
        ed.setText(formatDate);
    }


    /* 리스트 구성 */
    private void listData() {
        this.items = new ArrayList<>();

        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 쿼리문
            String sql = "SELECT cID, brand, name, open, exp, kind, initPeriod FROM cosmetics";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                // 데이터
                Cosmetics cosmetic = new Cosmetics(cursor.getInt(cursor.getColumnIndex("cID")),
                        cursor.getString(cursor.getColumnIndex("brand")), cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("open")), cursor.getString(cursor.getColumnIndex("exp")),
                        cursor.getString(cursor.getColumnIndex("kind")), cursor.getInt(cursor.getColumnIndex("initPeriod"))
                        );

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
    private void addData(String brand, String name, String open, String exp, String kind, int initPeriod) {
        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        mSpinner.setSelection(0);
        try {
            // 등록
            Object[] args = { brand, name, open, exp, kind, initPeriod};
            String sql = "INSERT INTO cosmetics(brand, name, open, exp, kind, initPeriod) VALUES(?,?,?,?,?,?)";

            db.execSQL(sql, args);

//            Toast.makeText(getContext(), "Scanned: " + initPeriod, Toast.LENGTH_LONG).show();

            // 리스트 새로고침
            listData();

        } catch (SQLException e) {}

        db.close();
    }


    void setSpinner() {
        mSpinner = (Spinner)mRoot.findViewById(R.id.spShowKinds);

        ArrayAdapter kindsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.kinds_array, android.R.layout.simple_spinner_item);
        kindsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(kindsAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setKind(mSpinner.getSelectedItem().toString(), true);
                        break;
                    case 1:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 2:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 3:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 4:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 5:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 6:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 7:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 8:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 9:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 10:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 11:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 12:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 13:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 14:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    case 15:
                        setKind(mSpinner.getSelectedItem().toString(), false);
                        break;
                    default:
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void setKind(String kind, boolean all) {
        this.items = new ArrayList<>();

        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 쿼리문
            if (all) {
                mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod FROM cosmetics";
            } else {
                mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod FROM cosmetics WHERE kind='" + kind + "'";
            }
            Cursor cursor = db.rawQuery(mSql, null);
            while (cursor.moveToNext()) {
                // 데이터
                Cosmetics cosmetic = new Cosmetics(cursor.getInt(cursor.getColumnIndex("cID")),
                        cursor.getString(cursor.getColumnIndex("brand")), cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("open")), cursor.getString(cursor.getColumnIndex("exp")),
                        cursor.getString(cursor.getColumnIndex("kind")), cursor.getInt(cursor.getColumnIndex("initPeriod"))
                );

                this.items.add(cosmetic);
            }

            cursor.close();
        } catch (SQLException e) {}

        db.close();

        // 리스트 구성
        this.adapter = new CustomArrayAdapter(getContext(), this.items);
        this.listView.setAdapter(this.adapter);
    }


}