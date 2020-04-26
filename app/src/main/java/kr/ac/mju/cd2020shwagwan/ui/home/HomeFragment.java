package kr.ac.mju.cd2020shwagwan.ui.home;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kr.ac.mju.cd2020shwagwan.BarcodeInfo;
import kr.ac.mju.cd2020shwagwan.Cosmetics;
import kr.ac.mju.cd2020shwagwan.CustomArrayAdapter;
import kr.ac.mju.cd2020shwagwan.DBHelper;
import kr.ac.mju.cd2020shwagwan.MyService;
import kr.ac.mju.cd2020shwagwan.R;
import kr.ac.mju.cd2020shwagwan.ResultActivity;
import kr.ac.mju.cd2020shwagwan.ScanBarcode;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static android.app.Activity.RESULT_OK;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;
public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ListView listView;
    private CustomArrayAdapter adapter;

    private View mRoot;

    //1: no, 2: week, 3: month
    private int mAlarmCheck;

    private String mSql;

    private boolean mCbWeekCheck;
    private boolean mCbMonthCheck;

    private Spinner mSpinner;
    private Spinner mSortSpinner;

    private ArrayList<Cosmetics> items;
    private SimpleDateFormat sdfNow;
    private EditText edOpen, edExp;
    int openYear = 0, openMonth = 0, openDay = 0;
    TextView tvComment, tvBarcode;
    EditText etBrand, etName;
    FloatingActionButton fabBarcode;
    Calendar openCalendar = Calendar.getInstance();
    static public Calendar expCalendar = Calendar.getInstance();
    int REQUEST_SUCESS = 0;
    String barcode;
    static int initPeriod = 0;
    static public CheckBox  cbWeek, cbMonth;


    public String bcdBrand, bcdName;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mRoot = inflater.inflate(R.layout.fragment_home, container, false);
        //findViewById
        final FloatingActionButton fabAdd = mRoot.findViewById(R.id.fabAdd);
        listView = mRoot.findViewById(R.id.lvItem);
        //초기 설정
//        listData();
        setSpinner();

        setSortSpinner();

        setKind(null, true);

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
        fabBarcode = mRoot.findViewById(R.id.fabBarcode);
        fabBarcode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ScanBarcode.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        return mRoot;
    }


    //바코드 얻어오는 부분
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            barcode = data.getStringExtra("barcode");
            if (resultCode == REQUEST_SUCESS) {//성공시
//                Toast.makeText(getContext(), "HomeFragment.java Scanned: " + barcode, Toast.LENGTH_LONG).show();
                compareBarcode(barcode);
                showAddDialog();
                tvBarcode.setText(barcode);
                etBrand.setText(bcdBrand);
                etName.setText(bcdName);
            } else {//실패시
//                Toast.makeText(getContext(), "바코드를 스캔해주세요", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void setSpinner() {
        mSpinner = (Spinner) mRoot.findViewById(R.id.spShowKinds);
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

    void setSortSpinner() {
        mSortSpinner = (Spinner) mRoot.findViewById(R.id.spSort);

        ArrayAdapter kindsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sort, android.R.layout.simple_spinner_item);
        kindsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(kindsAdapter);

        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // 최신 등록순
                        setSort(position);
                        break;

                    case 1:
                        // 나중 등록순
                        setSort(position);
                        break;

                    case 2:
                        // 사용기간 얼마 안남은 순
                        setSort(position);
                        break;

                    case 3:
                        // 사용기간이 넉넉한 순
                        setSort(position);
                        break;

                    case 4:
                        // 이름순 ㄱ - ㅎ
                        setSort(position);
                        break;

                    case 5:
                        // 이름순 ㅎ - ㄱ
                        setSort(position);
                        break;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    void setSort(int check) {
        String kind = mSpinner.getSelectedItem().toString();
        this.items = new ArrayList<>();

        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
        try {
            // 쿼리문

            switch (check) {
                case 0:
                    //개봉일순
                    if (mSpinner.getSelectedItemPosition() == 0) {
                        mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics ORDER BY open";
                    } else {
                        mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics WHERE kind='" + kind + "' ORDER BY open";
                    }
                    break;

                case 1:
                    //개봉일 역순
                    if (mSpinner.getSelectedItemPosition() == 0) {
                        mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics ORDER BY open desc";
                    } else {
                        mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics WHERE kind='" + kind + "' ORDER BY open desc";
                    }
                    break;

                case 2:
                    //사용기한 임박한 순
                    if (mSpinner.getSelectedItemPosition() == 0) {
                        mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics ORDER BY exp";
                    } else {
                        mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics WHERE kind='" + kind + "' ORDER BY exp";
                    }
                    break;

                case 3:
                    //사용기한 넉넉한 순
                    if (mSpinner.getSelectedItemPosition() == 0) {
                        mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics ORDER BY exp desc";
                    } else {
                        mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics WHERE kind='" + kind + "' ORDER BY exp desc";
                    }
                    break;

                case 4:
                    //제품명 ㄱ ~ ㅎ
                    if (mSpinner.getSelectedItemPosition() == 0) {
                        mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics ORDER BY name";
                    } else {
                        mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics WHERE kind='" + kind + "' ORDER BY name";
                    }
                    break;

                case 5:
                    //제품명 ㅎ ~ ㄱ
                    if (mSpinner.getSelectedItemPosition() == 0) {
                        mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics ORDER BY name desc";
                    } else {
                        mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics WHERE kind='" + kind + "' ORDER BY name desc";
                    }
                    break;
            }
            Cursor cursor = db.rawQuery(mSql, null);
            while (cursor.moveToNext()) {
                // 데이터
                Cosmetics cosmetic = new Cosmetics(cursor.getInt(cursor.getColumnIndex("cID")),
                        cursor.getString(cursor.getColumnIndex("brand")), cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("open")), cursor.getString(cursor.getColumnIndex("exp")),
                        cursor.getString(cursor.getColumnIndex("kind")), cursor.getInt(cursor.getColumnIndex("initPeriod")),
                        cursor.getInt(cursor.getColumnIndex("alarm"))
                );

                this.items.add(cosmetic);
            }

            cursor.close();
        } catch (SQLException e) {
        }

        db.close();

        // 리스트 구성
        this.adapter = new CustomArrayAdapter(getContext(), this.items);
        this.listView.setAdapter(this.adapter);
    }

    void setKind(String kind, boolean all) {
        this.items = new ArrayList<>();

        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 쿼리문
            if (all) {
                mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics";
            } else {
                mSql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics WHERE kind='" + kind + "'";
            }
            Cursor cursor = db.rawQuery(mSql, null);
            while (cursor.moveToNext()) {
                // 데이터
                 Cosmetics cosmetic = new Cosmetics(cursor.getInt(cursor.getColumnIndex("cID")),
                        cursor.getString(cursor.getColumnIndex("brand")), cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("open")), cursor.getString(cursor.getColumnIndex("exp")),
                        cursor.getString(cursor.getColumnIndex("kind")), cursor.getInt(cursor.getColumnIndex("initPeriod")),
                        cursor.getInt(cursor.getColumnIndex("alarm")));

                this.items.add(cosmetic);
            }

            cursor.close();
        } catch (SQLException e) {
        }

        db.close();

        // 리스트 구성
        this.adapter = new CustomArrayAdapter(getContext(), this.items);
        this.listView.setAdapter(this.adapter);

        setSort(mSortSpinner.getSelectedItemPosition());
    }

    /* 추가폼 호출 */
    public void showAddDialog() {
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
        etBrand = layout.findViewById(R.id.etBrand);
        etName = layout.findViewById(R.id.etName);
        tvComment = layout.findViewById(R.id.tvComment);
        tvBarcode = layout.findViewById(R.id.tvBarcode);


        cbWeek = layout.findViewById(R.id.cbWeek);
        cbMonth = layout.findViewById(R.id.cbMonth);

//        체크박스 설정
        setCheckBox();

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
                        if (spKinds.getSelectedItemPosition() == 1 || spKinds.getSelectedItemPosition() == 2
                                || spKinds.getSelectedItemPosition() == 11 || spKinds.getSelectedItemPosition() == 12
                                || spKinds.getSelectedItemPosition() == 13 || spKinds.getSelectedItemPosition() == 14) {
                            //6개월 이내
                            openCalendar.add(Calendar.DATE, 180);
                            edExp.setText(sdf.format(openCalendar.getTime()));
                        } else if (spKinds.getSelectedItemPosition() == 3) {
                            //8개월 이내
                            openCalendar.add(Calendar.DATE, 240);
                            edExp.setText(sdf.format(openCalendar.getTime()));
                        } else if (spKinds.getSelectedItemPosition() == 0 || spKinds.getSelectedItemPosition() == 4
                                || spKinds.getSelectedItemPosition() == 5 || spKinds.getSelectedItemPosition() == 6
                                || spKinds.getSelectedItemPosition() == 8 || spKinds.getSelectedItemPosition() == 9
                                || spKinds.getSelectedItemPosition() == 10 || spKinds.getSelectedItemPosition() == 15) {
                            //1년 이내
                            openCalendar.add(Calendar.DATE, 365);
                            edExp.setText(sdf.format(openCalendar.getTime()));
                        } else if (spKinds.getSelectedItemPosition() == 7) {
                            //2년 이내
                            openCalendar.add(Calendar.DATE, 730);
                            edExp.setText(sdf.format(openCalendar.getTime()));
                        } else if (spKinds.getSelectedItemPosition() == 16) {
                            Toast ts = Toast.makeText(getContext(), "만료일을 설정하세요", Toast.LENGTH_SHORT);
                            ts.setGravity(Gravity.CENTER, 0, 0);
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

        cbWeek.setOnClickListener(new CheckBox.OnClickListener() {
            Intent intent = new Intent(getContext(), MyService.class);
            @Override
            public void onClick(View v) {
                // TODO : process the click event.
                if(cbWeek.isChecked() == true){
                    Toast.makeText(getContext(), "cbWeek checked", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getContext(), "cbWeek unchecked", Toast.LENGTH_SHORT).show();

                }
            }
        });
//
        cbMonth.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : process the click event.

                if(cbMonth.isChecked() == true){
                    Toast.makeText(getContext(), "cbMonth checked", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "cbMonth unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spKinds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                etExp.setText("Exp :" + position + parent.getItemAtPosition(position));
                Calendar cCal = Calendar.getInstance();
                String myFormat = "yyyy-MM-dd";    // 출력형식   2018-11-18
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
                if (position == 1 || position == 2 || position == 11 || position == 12 || position == 13 || position == 14) {
                    //6개월 이내
                    //자외선 차단제,  립밤,           립스틱,         립글로스,          아이라이너,        마스카라
                    cCal.add(Calendar.DATE, 180);
                    edExp.setText(sdf.format(cCal.getTime()));
                    tvComment.setText("사용 권장 기한 : 6개월 이내");
                } else if (position == 3) {
                    //8개월 이내
                    //에센스
                    edExp.setText("Exp : 8개월 이내");
                    cCal.add(Calendar.DATE, 240);
                    edExp.setText(sdf.format(cCal.getTime()));
                    tvComment.setText("사용 권장 기한 : 8개월 이내");
                } else if (position == 0 || position == 4 || position == 5 || position == 6 || position == 8 || position == 9 || position == 10 || position == 15) {
                    //1년 이내
                    //스킨,              크림,             메이크업 베이스,   컨실러,           아이새도우,        아이브로우,      블러셔,           클렌저
                    cCal.add(Calendar.DATE, 365);
                    edExp.setText(sdf.format(cCal.getTime()));
                    tvComment.setText("사용 권장 기한 : 1년 이내");
                } else if (position == 7) {
                    //2년 이내
                    //파우더
                    cCal.add(Calendar.DATE, 730);
                    edExp.setText(sdf.format(cCal.getTime()));
                    tvComment.setText("사용 권장 기한 : 2년 이내");
                } else if (position == 16) {
                    edExp.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        /* 추가폼에 데이터 입력 */
        new AlertDialog.Builder(getContext())
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        // 추가
                        String brand = etBrand.getText().toString();
                        if (TextUtils.isEmpty(brand)) {
                            Toast.makeText(getContext(), "Brand empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String name = etName.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(getContext(), "Name empty", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        if (spKinds.getSelectedItemPosition() == 1 || spKinds.getSelectedItemPosition() == 2
                                || spKinds.getSelectedItemPosition() == 11 || spKinds.getSelectedItemPosition() == 12
                                || spKinds.getSelectedItemPosition() == 13 || spKinds.getSelectedItemPosition() == 14) {
                            //6개월 이내
                            initPeriod = 180;
                        } else if (spKinds.getSelectedItemPosition() == 3) {
                            //8개월 이내
                            initPeriod = 240;
                        } else if (spKinds.getSelectedItemPosition() == 0 || spKinds.getSelectedItemPosition() == 4
                                || spKinds.getSelectedItemPosition() == 5 || spKinds.getSelectedItemPosition() == 6
                                || spKinds.getSelectedItemPosition() == 8 || spKinds.getSelectedItemPosition() == 9
                                || spKinds.getSelectedItemPosition() == 10 || spKinds.getSelectedItemPosition() == 15) {
                            //1년 이내
                            initPeriod = 365;
                        } else if (spKinds.getSelectedItemPosition() == 7) {
                            //2년 이내
                            initPeriod = 730;
                        } else if (spKinds.getSelectedItemPosition() == 16) {
                            initPeriod = 365;
                        }


                        // 데이터 추가
                        addData(brand, name, edOpen.getText().toString(), edExp.getText().toString(), spKinds.getSelectedItem().toString(), initPeriod, mAlarmCheck);
                    }
                })
                .setCancelable(true)
                .setTitle("화장품 추가 관리")
                .setView(layout)
                .show();
    }


    /* 개봉일 현재 날짜로 설정 */
    public void setToday(EditText ed) {

        long now = System.currentTimeMillis();
        Date dt = new Date(now);
        sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = sdfNow.format(dt);
        ed.setText(formatDate);
    }



    /* 화장품 정보 가져오기 */
    public void compareBarcode(String barcode){
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int isSame = 0;

        try {
            // 쿼리문
            Log.d(TAG , "compareBarcode try 입성");

            String sql = "SELECT bID, bcdId, bcdBrand, bcdName, bcdVolume FROM barcodeInfos";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                // 데이터
                Log.d(TAG , "compareBarcode while 입성");
                BarcodeInfo barcodeInfo = new BarcodeInfo(cursor.getInt(cursor.getColumnIndex("bID")),
                        cursor.getString(cursor.getColumnIndex("bcdId")), cursor.getString(cursor.getColumnIndex("bcdBrand")),
                        cursor.getString(cursor.getColumnIndex("bcdName")), cursor.getString(cursor.getColumnIndex("bcdVolume"))
                );
                String cmp =barcodeInfo.getBcdId().substring(0,13);
                Log.d(TAG , "barcode : "+barcode);
                Log.d(TAG , "cmp : "+cmp);

                if(cmp.equals(barcode)) {
                    Toast.makeText(getContext(), "바코드 정보 가져오기 성공", Toast.LENGTH_LONG).show();
                    bcdBrand = barcodeInfo.getBcdBrand();
                    bcdName = barcodeInfo.getBcdName();
                    isSame = 1;
                    break;
                }

            }
            if (isSame == 0){
                Toast.makeText(getContext(), "바코드 정보 가져오기 실패", Toast.LENGTH_LONG).show();
            }
            cursor.close();
        } catch (SQLException e) {
            Log.d(TAG , "compareBarcode error : " + e.getMessage());
        }

        db.close();
    }



    /* 리스트 구성 */
    private void listData() {
        this.items = new ArrayList<>();

        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 쿼리문
            String sql = "SELECT cID, brand, name, open, exp, kind, initPeriod, alarm FROM cosmetics";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                // 데이터
                Cosmetics cosmetic = new Cosmetics(cursor.getInt(cursor.getColumnIndex("cID")),
                        cursor.getString(cursor.getColumnIndex("brand")), cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("open")), cursor.getString(cursor.getColumnIndex("exp")),
                        cursor.getString(cursor.getColumnIndex("kind")), cursor.getInt(cursor.getColumnIndex("initPeriod")),
                        cursor.getInt(cursor.getColumnIndex("alarm"))
                );

                this.items.add(cosmetic);

            }

            cursor.close();
        } catch (SQLException e) {
        }

        db.close();

        // 리스트 구성
        this.adapter = new CustomArrayAdapter(getContext(), this.items);
        this.listView.setAdapter(this.adapter);

    }


    /* 추가 */
    private void addData(String brand, String name, String open, String exp, String kind, int initPeriod, int alarm) {
        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        mSpinner.setSelection(0);
        try {
            // 등록
            Object[] args = {brand, name, open, exp, kind, initPeriod, alarm};
            String sql = "INSERT INTO cosmetics(brand, name, open, exp, kind, initPeriod, alarm) VALUES(?,?,?,?,?,?,?)";

            db.execSQL(sql, args);

            // 리스트 새로고침
            listData();
            setKind(null, true);

        } catch (SQLException e) {
        }

        db.close();
    }

    void setCheckBox() {
        //0: week, 1: month
        mAlarmCheck = 0;

        mCbWeekCheck = true;
        mCbMonthCheck = true;

        // mAlarmCheck
        // 0 : 일주일 1 : 한달
        CompoundButton.OnCheckedChangeListener cHandler = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.cbWeek:
                        if (isChecked) {
                            mCbWeekCheck = true;
                            mCbMonthCheck = false;

                            cbMonth.setChecked(false);
                            mAlarmCheck = 0;
                        } else {
                            if (mCbWeekCheck) {
                                cbWeek.setChecked(true);
                            }
                        }
                        break;
                    case R.id.cbMonth:
                        if (isChecked) {
                            mCbMonthCheck = true;
                            mCbWeekCheck = false;

                            cbWeek.setChecked(false);
                            mAlarmCheck = 1;
                        } else {
                            if (mCbMonthCheck) {
                                cbMonth.setChecked(true);
                            }
                        }
                        break;
                }
            }
        };

        cbWeek.setOnCheckedChangeListener(cHandler);
        cbMonth.setOnCheckedChangeListener(cHandler);
    }
}

