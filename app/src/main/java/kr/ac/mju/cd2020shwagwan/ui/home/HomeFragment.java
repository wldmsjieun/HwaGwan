package kr.ac.mju.cd2020shwagwan.ui.home;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kr.ac.mju.cd2020shwagwan.BarcodeInfo;
import kr.ac.mju.cd2020shwagwan.Cosmetics;
import kr.ac.mju.cd2020shwagwan.CustomArrayAdapter;
import kr.ac.mju.cd2020shwagwan.DBHelper;
import kr.ac.mju.cd2020shwagwan.R;
import kr.ac.mju.cd2020shwagwan.ScanBarcode;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;
public class HomeFragment extends Fragment {

    public static View mRoot;
    public static CustomArrayAdapter adapter;

    //private
    private HomeViewModel homeViewModel;
    private ListView listView;
    private String mSql; //SQL문을 위한 변수
    private Spinner spKind; //화장품 종류 스피너
    private Spinner spSort; //정렬 스피너
    private ArrayList<Cosmetics> items; //리스트를 위한 변수
    private SimpleDateFormat sdfNow;
    private EditText edOpen, edExp; //개봉일, 만료일
    private Context mContext;

    //
    int REQUEST_SUCESS = 0; //바코드 얻어오는 부분에서 사용
    int openYear = 0, openMonth = 0, openDay = 0; //개봉일 연도, 월, 일
    String barcode; //바코드 번호를 위한 변수
    TextView tvComment, tvBarcode; //사용 권장 기한, 바코드 번호 텍스트뷰
    EditText etBrand, etName, etAddCont, etVolume; //브랜드명, 상품명, 추가사항, 용량
    FloatingActionButton fabBarcode; //fab버튼
    Calendar openCalendar = Calendar.getInstance(); //개봉일을 위한 달력

    //public
    public String bcdBrand, bcdProduct; //바코드 상품명, 브랜드명 변수

    //static
    static public Calendar expCalendar = Calendar.getInstance(); //만료일을 위한 달력
    static public CheckBox  cbWeek, cbMonth; //체크박스

    String mInseartSql = "INSERT INTO "+DBHelper.TABLE_COSMETIC+"(brandName, productName, dtOpen, dtExp, kind, alarm, volume, additionalContent) VALUES(?,?,?,?,?,?,?,?)";


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mRoot = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getContext();
        //findViewById
        final FloatingActionButton fabAdd = mRoot.findViewById(R.id.fabAdd);
        listView = mRoot.findViewById(R.id.lvItem);

        //초기 설정
        listData();
        setSpinner();
        setSortSpinner();
        setKind(null, true);

        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //fabAdd 누르면 입력폼 보여줌
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
                compareBarcode(barcode);
                showAddDialog();
                tvBarcode.setText(barcode);
                etBrand.setText(bcdBrand);
                etName.setText(bcdProduct);
            } else {//실패시
                Toast.makeText(getContext(), "바코드를 스캔해주세요", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // 화장품 종류 스피너 설정
    void setSpinner() {
        spKind = mRoot.findViewById(R.id.spShowKinds);
        ArrayAdapter kindsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.kinds_array, android.R.layout.simple_spinner_item);
        kindsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKind.setAdapter(kindsAdapter);

        spKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    setKind(spKind.getSelectedItem().toString(), true);
                }
                else {
                    setKind(spKind.getSelectedItem().toString(), false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //정렬 스피너 설정
    void setSortSpinner() {
        spSort = (Spinner) mRoot.findViewById(R.id.spSort);

        ArrayAdapter kindsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sort, android.R.layout.simple_spinner_item);
        kindsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSort.setAdapter(kindsAdapter);

        spSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSort(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    //정렬 함수
    void setSort(int check) {
        String kind = spKind.getSelectedItem().toString();
        String sortType = "dtOpen";

        this.items = new ArrayList<>();

        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 정렬 기준 선택
            if (check == 0){
                sortType = "dtOpen";
            } else if (check == 1){
                sortType = "dtOpen desc";
            } else if (check == 2){
                sortType = "dtExp";
            } else if (check == 3){
                sortType = "dtExp desc";
            } else if (check == 4){
                sortType = "productName";
            } else if (check == 5){
                sortType = "productName desc";
            }

            // 화장품 종류 선택
            if (spKind.getSelectedItemPosition() == 0) {
                mSql = "SELECT * FROM cosmetics ORDER BY " + sortType;
            } else {
                mSql = "SELECT * FROM cosmetics WHERE kind='" + kind + "' ORDER BY " + sortType;
            }

            Cursor cursor = db.rawQuery(mSql, null);
            while (cursor.moveToNext()) {
                // 데이터
                Cosmetics cosmetic = new Cosmetics(cursor.getInt(cursor.getColumnIndex("cID")),
                        cursor.getString(cursor.getColumnIndex("brandName")), cursor.getString(cursor.getColumnIndex("productName")),
                        cursor.getString(cursor.getColumnIndex("dtOpen")), cursor.getString(cursor.getColumnIndex("dtExp")),
                        cursor.getString(cursor.getColumnIndex("kind")), cursor.getInt(cursor.getColumnIndex("alarm")),
                        cursor.getString(cursor.getColumnIndex("volume")), cursor.getString(cursor.getColumnIndex("additionalContent"))
                );

                this.items.add(cosmetic);
            }

            cursor.close();
        } catch (SQLException e) { }

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
                mSql = "SELECT * FROM cosmetics";
            } else {
                mSql = "SELECT * FROM cosmetics WHERE kind='" + kind + "'";
            }
            Cursor cursor = db.rawQuery(mSql, null);
            while (cursor.moveToNext()) {
                // 데이터
                Cosmetics cosmetic = new Cosmetics(cursor.getInt(cursor.getColumnIndex("cID")),
                        cursor.getString(cursor.getColumnIndex("brandName")), cursor.getString(cursor.getColumnIndex("productName")),
                        cursor.getString(cursor.getColumnIndex("dtOpen")), cursor.getString(cursor.getColumnIndex("dtExp")),
                        cursor.getString(cursor.getColumnIndex("kind")), cursor.getInt(cursor.getColumnIndex("alarm")),
                        cursor.getString(cursor.getColumnIndex("volume")), cursor.getString(cursor.getColumnIndex("additionalContent")));

                this.items.add(cosmetic);
            }

            cursor.close();
        } catch (SQLException e) { }

        db.close();

        // 리스트 구성
        this.adapter = new CustomArrayAdapter(getContext(), this.items);
        this.listView.setAdapter(this.adapter);

        setSort(spSort.getSelectedItemPosition());
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
        etVolume = layout.findViewById(R.id.etVolume);
        etAddCont = layout.findViewById(R.id.etcontent);

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

                        SharedPreferences sp = getContext().getSharedPreferences("alarmTime", MODE_PRIVATE);

                        expCalendar.set(Calendar.YEAR, year);
                        expCalendar.set(Calendar.MONTH, month);
                        expCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        Log.d(TAG , "set HOME tvHour : " + sp.getInt("hour", 22));
                        Log.d(TAG , "set HOME tvMinute : " + sp.getInt("minute", 00));

                        expCalendar.set(Calendar.HOUR_OF_DAY, sp.getInt("hour", 22));
                        expCalendar.set(Calendar.MINUTE, sp.getInt("minute", 00));
                        String myFormat = "yyyy-MM-dd HH:mm:ss";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

                        edExp.setText(sdf.format(expCalendar.getTime()));
                        Log.d(TAG , "tvEXP : " + expCalendar.getTime());
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
                expCalendar = cCal;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });



        /* 추가폼에 데이터 입력 */
        new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle)
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

                        // 데이터 추가
                        addData(brand, name, edOpen.getText().toString(), edExp.getText().toString(), spKinds.getSelectedItem().toString(),
                                setCheckBox(), etVolume.getText().toString(), etAddCont.getText().toString());
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

            String sql = "SELECT bID, bcdId, bcdBrand, bcdProduct, bcdVolume FROM barcodeInfos";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                // 데이터
                Log.d(TAG , "compareBarcode while 입성");
                BarcodeInfo barcodeInfo = new BarcodeInfo(cursor.getInt(cursor.getColumnIndex("bID")),
                        cursor.getString(cursor.getColumnIndex("bcdId")), cursor.getString(cursor.getColumnIndex("bcdBrand")),
                        cursor.getString(cursor.getColumnIndex("bcdProduct")), cursor.getString(cursor.getColumnIndex("bcdVolume"))
                );
                String cmp =barcodeInfo.getBcdId().substring(0,13);
                Log.d(TAG , "barcode : "+barcode);
                Log.d(TAG , "cmp : "+cmp);

                if(cmp.equals(barcode)) {
                    Toast.makeText(getContext(), "바코드 정보 가져오기 성공", Toast.LENGTH_SHORT).show();
                    bcdBrand = barcodeInfo.getBcdBrand();
                    bcdProduct = barcodeInfo.getBcdProduct();
                    isSame = 1;
                    break;
                } else{
                    String barcodeBrand = barcode.substring(0, 7);
                    String barcodeInfoBrand = cmp.substring(0, 7);

                    Log.d(TAG , "cmp - barcodeBrand : "+barcodeBrand);
                    Log.d(TAG , "cmp - barcodeInfoBrand : "+barcodeInfoBrand);

                    if (barcodeInfoBrand.equals(barcodeBrand)) {
                        bcdBrand = barcodeInfo.getBcdBrand();
                        bcdProduct = "";
                        Toast.makeText(getContext(), "바코드 브랜드 정보 가져오기 성공", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            if (isSame == 0){
                Toast.makeText(getContext(), "바코드 정보 가져오기 실패", Toast.LENGTH_SHORT).show();
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
            String sql = "SELECT * FROM cosmetics";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                // 데이터
                Cosmetics cosmetic = new Cosmetics(cursor.getInt(cursor.getColumnIndex("cID")),
                        cursor.getString(cursor.getColumnIndex("brandName")), cursor.getString(cursor.getColumnIndex("productName")),
                        cursor.getString(cursor.getColumnIndex("dtOpen")), cursor.getString(cursor.getColumnIndex("dtExp")),
                        cursor.getString(cursor.getColumnIndex("kind")), cursor.getInt(cursor.getColumnIndex("alarm")),
                        cursor.getString(cursor.getColumnIndex("volume")), cursor.getString(cursor.getColumnIndex("additionalContent"))
                );

                this.items.add(cosmetic);

            }

            cursor.close();
        } catch (SQLException e) { }

        db.close();

        // 리스트 구성
        this.adapter = new CustomArrayAdapter(getContext(), this.items);
        this.listView.setAdapter(this.adapter);

    }


    /* 추가 */
    private void addData(String brand, String name, String open, String exp, String kind, int alarm, String volume, String additionalContent) {
        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        spKind.setSelection(0);
        try {
            // 등록
            Object[] args = {brand, name, open, exp, kind, alarm, volume, additionalContent};
            String sql = mInseartSql;

            db.execSQL(sql, args);

            // 리스트 새로고침
            listData();
            setKind(null, true);

        } catch (SQLException e) {
        }

        db.close();
    }

    //체크박스 상태 함수
    private int setCheckBox() {
        // no : 0, week : 1, month : 2, all : 3

        if (cbWeek.isChecked() == false){
            if (cbMonth.isChecked() == false)  return 0;
            else    return 2;
        }
        else{
            if (cbMonth.isChecked() == false)  return 1;
            else    return 3;
        }
    }

}
