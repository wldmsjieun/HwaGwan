package kr.ac.mju.cd2020shwagwan.ui.home;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import kr.ac.mju.cd2020shwagwan.BarcodeInfo;
import kr.ac.mju.cd2020shwagwan.Cosmetics;
import kr.ac.mju.cd2020shwagwan.CosmeticArrayAdapter;
import kr.ac.mju.cd2020shwagwan.DBHelper;
import kr.ac.mju.cd2020shwagwan.MainActivity;
import kr.ac.mju.cd2020shwagwan.R;
import kr.ac.mju.cd2020shwagwan.ScanBarcode;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;
public class HomeFragment extends Fragment {

    public static View hfView;
    public static CosmeticArrayAdapter hfCosArrAdapter;

    //private
    private HomeViewModel homeViewModel;
    private ListView hfLvCos;
    private String mSql; //SQL문을 위한 변수
    private Spinner hfSpKind; //화장품 종류 스피너
    private Spinner hfSpSort; //정렬 스피너
    private ArrayList<Cosmetics> hfCosArrList; //리스트를 위한 변수
    private SimpleDateFormat hfSdfToday;
    private EditText hfEdOpen, hfEdExp; //개봉일, 만료일


    //
    int REQUEST_SUCESS = 0; //바코드 얻어오는 부분에서 사용
    int hfOpenYear = 0, hfOpenMonth = 0, hfOpenDay = 0; //개봉일 연도, 월, 일
    String hfbarcode; //바코드 번호를 위한 변수
    TextView hfTvComment, hfTvBarcode; //사용 권장 기한, 바코드 번호 텍스트뷰
    EditText hfEtBrand, hfEtName, hfEtComment, hfEtVolume; //브랜드명, 상품명, 추가사항, 용량
    FloatingActionButton hfFabBarcode; //fab버튼
    Calendar hfOpenCal = Calendar.getInstance(); //개봉일을 위한 달력

    String mJsonString;

    private static final String TAG_JSON="result";
    private static final String TAG_BID = "bID";
    private static final String TAG_BCDID = "bcdId";
    private static final String TAG_BCDBRAND ="bcdBrand";
    private static final String TAG_BCDPRODUCT ="bcdProduct";
    private static final String TAG_BCDVOLUME ="bcdVolume";

    //public
    public String bcdBrand, bcdProduct; //바코드 상품명, 브랜드명 변수

    //static
    public Calendar hfExpCal = Calendar.getInstance(); //만료일을 위한 달력
    static public CheckBox  hfCbWeek, hfCbMonth; //체크박스
    static public Context hfContext;
    String insertCosSql = "INSERT INTO "+DBHelper.TABLE_COSMETIC+"(brandName, productName, dtOpen, dtExp, kind, alarm, volume, additionalContent) VALUES(?,?,?,?,?,?,?,?)";


    public View onCreateView(@NonNull LayoutInflater hfLayoutInflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        hfView = hfLayoutInflater.inflate(R.layout.fragment_home, container, false);
        hfContext = getContext();
        //findViewById
        final FloatingActionButton hfFabDirect = hfView.findViewById(R.id.hfFabDirect);
        hfLvCos = hfView.findViewById(R.id.lvItem);

        //초기 설정
        listData();
        setSpinner();
        setSortSpinner();
        setKind(null, true);

        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //hfFabDirect 누르면 입력폼 보여줌
                hfFabDirect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAddDialog();
                    }
                });
            }
        });

        //바코드 스캔 실행 버튼
        hfFabBarcode = hfView.findViewById(R.id.hfFabBarcode);
        hfFabBarcode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent hfIntent = new Intent(getContext(), ScanBarcode.class);
                startActivityForResult(hfIntent, REQUEST_CODE);
            }
        });
        return hfView;
    }



    //바코드 얻어오는 부분
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            hfbarcode = data.getStringExtra("barcode");
            if (resultCode == REQUEST_SUCESS) {//성공시
                getBarcode(hfbarcode);
//                compareBarcode(hfbarcode);
                showAddDialog();
                hfTvBarcode.setText(hfbarcode);
            } else {//실패시
                Toast.makeText(getContext(), "바코드를 스캔해주세요", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // 화장품 종류 스피너 설정
    void setSpinner() {
        hfSpKind = hfView.findViewById(R.id.hfSpKind);
        ArrayAdapter hfKindArrAdapter = ArrayAdapter.createFromResource(getContext(), R.array.kinds_array, android.R.layout.simple_spinner_item);
        hfKindArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hfSpKind.setAdapter(hfKindArrAdapter);

        hfSpKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    setKind(hfSpKind.getSelectedItem().toString(), true);
                }
                else {
                    setKind(hfSpKind.getSelectedItem().toString(), false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //정렬 스피너 설정
    void setSortSpinner() {
        hfSpSort = hfView.findViewById(R.id.hfSpSort);

        ArrayAdapter hfKindArrAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sort, android.R.layout.simple_spinner_item);
        hfKindArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hfSpSort.setAdapter(hfKindArrAdapter);

        hfSpSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSort(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> hfCosArrAdapterView) { }
        });
    }

    //정렬 함수
    void setSort(int check) {
        String kind = hfSpKind.getSelectedItem().toString();
        String hfStrSortType = "dtOpen";

        this.hfCosArrList = new ArrayList<>();

        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 정렬 기준 선택
            if (check == 0){
                hfStrSortType = "dtOpen";
            } else if (check == 1){
                hfStrSortType = "dtOpen desc";
            } else if (check == 2){
                hfStrSortType = "dtExp";
            } else if (check == 3){
                hfStrSortType = "dtExp desc";
            } else if (check == 4){
                hfStrSortType = "productName";
            } else if (check == 5){
                hfStrSortType = "productName desc";
            }

            // 화장품 종류 선택
            if (hfSpKind.getSelectedItemPosition() == 0) {
                mSql = "SELECT * FROM cosmetics ORDER BY " + hfStrSortType;
            } else {
                mSql = "SELECT * FROM cosmetics WHERE kind='" + kind + "' ORDER BY " + hfStrSortType;
            }

            Cursor hfCosCur = db.rawQuery(mSql, null);
            while (hfCosCur.moveToNext()) {
                // 데이터
                Cosmetics hfCos = new Cosmetics(hfCosCur.getInt(hfCosCur.getColumnIndex("cID")),
                        hfCosCur.getString(hfCosCur.getColumnIndex("brandName")), hfCosCur.getString(hfCosCur.getColumnIndex("productName")),
                        hfCosCur.getString(hfCosCur.getColumnIndex("dtOpen")), hfCosCur.getString(hfCosCur.getColumnIndex("dtExp")),
                        hfCosCur.getString(hfCosCur.getColumnIndex("kind")), hfCosCur.getInt(hfCosCur.getColumnIndex("alarm")),
                        hfCosCur.getString(hfCosCur.getColumnIndex("volume")), hfCosCur.getString(hfCosCur.getColumnIndex("additionalContent"))
                );

                this.hfCosArrList.add(hfCos);
            }

            hfCosCur.close();
        } catch (SQLException e) { }

        db.close();

        // 리스트 구성
        this.hfCosArrAdapter = new CosmeticArrayAdapter(getContext(), this.hfCosArrList);
        this.hfLvCos.setAdapter(this.hfCosArrAdapter);
    }

    void setKind(String kind, boolean all) {
        this.hfCosArrList = new ArrayList<>();

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
            Cursor hfCosCur = db.rawQuery(mSql, null);
            while (hfCosCur.moveToNext()) {
                // 데이터
                Cosmetics hfCos = new Cosmetics(hfCosCur.getInt(hfCosCur.getColumnIndex("cID")),
                        hfCosCur.getString(hfCosCur.getColumnIndex("brandName")), hfCosCur.getString(hfCosCur.getColumnIndex("productName")),
                        hfCosCur.getString(hfCosCur.getColumnIndex("dtOpen")), hfCosCur.getString(hfCosCur.getColumnIndex("dtExp")),
                        hfCosCur.getString(hfCosCur.getColumnIndex("kind")), hfCosCur.getInt(hfCosCur.getColumnIndex("alarm")),
                        hfCosCur.getString(hfCosCur.getColumnIndex("volume")), hfCosCur.getString(hfCosCur.getColumnIndex("additionalContent")));

                this.hfCosArrList.add(hfCos);
            }

            hfCosCur.close();
        } catch (SQLException e) { }

        db.close();

        // 리스트 구성
        this.hfCosArrAdapter = new CosmeticArrayAdapter(getContext(), this.hfCosArrList);
        this.hfLvCos.setAdapter(this.hfCosArrAdapter);

        setSort(hfSpSort.getSelectedItemPosition());
    }

    /* 추가폼 호출 */
    public void showAddDialog() {
        // AlertDialog View layout
        LayoutInflater hfLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View hfViewDialog = hfLayoutInflater.inflate(R.layout.content_add, null);

        //스피너 생성
        final Spinner hfSpAddKind = hfViewDialog.findViewById(R.id.ca_spKind);
        ArrayAdapter hfKindArrAdapter = ArrayAdapter.createFromResource(getContext(), R.array.product_kinds_array, android.R.layout.simple_spinner_item);
        hfKindArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hfSpAddKind.setAdapter(hfKindArrAdapter);

        // findViewById
        hfEdOpen = hfViewDialog.findViewById(R.id.ca_edOpen);
        hfEdExp = hfViewDialog.findViewById(R.id.ca_edExp);
        hfEtBrand = hfViewDialog.findViewById(R.id.ca_etBrand);
        hfEtName = hfViewDialog.findViewById(R.id.ca_etName);
        hfTvComment = hfViewDialog.findViewById(R.id.ca_tvComment);
        hfTvBarcode = hfViewDialog.findViewById(R.id.ca_tvBarcode);
        hfCbWeek = hfViewDialog.findViewById(R.id.ca_cbWeek);
        hfCbMonth = hfViewDialog.findViewById(R.id.ca_cbMonth);
        hfEtVolume = hfViewDialog.findViewById(R.id.ca_etVolume);
        hfEtComment = hfViewDialog.findViewById(R.id.ca_etComment);

        // 개봉일 현재 날짜로 설정
        setToday(hfEdOpen);

        // 개봉일 캘린더로 선택
        hfEdOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        hfOpenCal.set(Calendar.YEAR, year);
                        hfOpenCal.set(Calendar.MONTH, month);
                        hfOpenCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String hfFormat = "yyyy-MM-dd";    // 출력형식   2018-11-18
                        SimpleDateFormat hfSdfExp = new SimpleDateFormat(hfFormat, Locale.KOREA);

                        hfEdOpen.setText(hfSdfExp.format(hfOpenCal.getTime()));

                        //상품별 만료일 자동 계산
                        if (hfSpAddKind.getSelectedItemPosition() == 1 || hfSpAddKind.getSelectedItemPosition() == 2
                                || hfSpAddKind.getSelectedItemPosition() == 11 || hfSpAddKind.getSelectedItemPosition() == 12
                                || hfSpAddKind.getSelectedItemPosition() == 13 || hfSpAddKind.getSelectedItemPosition() == 14) {
                            //6개월 이내
                            hfOpenCal.add(Calendar.DATE, 180);
                            hfEdExp.setText(hfSdfExp.format(hfOpenCal.getTime()));
                        } else if (hfSpAddKind.getSelectedItemPosition() == 3) {
                            //8개월 이내
                            hfOpenCal.add(Calendar.DATE, 240);
                            hfEdExp.setText(hfSdfExp.format(hfOpenCal.getTime()));
                        } else if (hfSpAddKind.getSelectedItemPosition() == 0 || hfSpAddKind.getSelectedItemPosition() == 4
                                || hfSpAddKind.getSelectedItemPosition() == 5 || hfSpAddKind.getSelectedItemPosition() == 6
                                || hfSpAddKind.getSelectedItemPosition() == 8 || hfSpAddKind.getSelectedItemPosition() == 9
                                || hfSpAddKind.getSelectedItemPosition() == 10 || hfSpAddKind.getSelectedItemPosition() == 15) {
                            //1년 이내
                            hfOpenCal.add(Calendar.DATE, 365);
                            hfEdExp.setText(hfSdfExp.format(hfOpenCal.getTime()));
                        } else if (hfSpAddKind.getSelectedItemPosition() == 7) {
                            //2년 이내
                            hfOpenCal.add(Calendar.DATE, 730);
                            hfEdExp.setText(hfSdfExp.format(hfOpenCal.getTime()));
                        } else if (hfSpAddKind.getSelectedItemPosition() == 16) {
                            Toast ts = Toast.makeText(getContext(), "만료일을 설정하세요", Toast.LENGTH_SHORT);
                            ts.setGravity(Gravity.CENTER, 0, 0);
                            ts.show();
                            hfEdExp.setText("");
                        }
                    }
                };

                hfOpenYear = hfOpenCal.get(Calendar.YEAR);
                hfOpenMonth = hfOpenCal.get(Calendar.MONTH);
                hfOpenDay = hfOpenCal.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(getContext(), myDatePicker, hfOpenYear, hfOpenMonth, hfOpenDay).show();

            }
        });


        // 만료일 캘린더로 선택 및 최소날짜 세팅
        hfEdExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        SharedPreferences hfSPAlarm = getContext().getSharedPreferences("alarmTime", MODE_PRIVATE);

                        hfExpCal.set(Calendar.YEAR, year);
                        hfExpCal.set(Calendar.MONTH, month);
                        hfExpCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        Log.d(TAG , "set HOME tvHour : " + hfSPAlarm.getInt("hour", 22));
                        Log.d(TAG , "set HOME tvMinute : " + hfSPAlarm.getInt("minute", 00));

                        hfExpCal.set(Calendar.HOUR_OF_DAY, hfSPAlarm.getInt("hour", 22));
                        hfExpCal.set(Calendar.MINUTE, hfSPAlarm.getInt("minute", 00));
                        String hfFormat = "yyyy-MM-dd HH:mm:ss";
                        SimpleDateFormat hfSdfExp = new SimpleDateFormat(hfFormat, Locale.KOREA);

                        hfEdExp.setText(hfSdfExp.format(hfExpCal.getTime()));
                        Log.d(TAG , "tvEXP : " + hfExpCal.getTime());
                    }
                };

                DatePickerDialog hfDpdExp = new DatePickerDialog(getContext(), myDatePicker, hfExpCal.get(Calendar.YEAR), hfExpCal.get(Calendar.MONTH), hfExpCal.get(Calendar.DAY_OF_MONTH));
                hfDpdExp.getDatePicker().setMinDate(hfOpenCal.getTimeInMillis());
                hfDpdExp.show();
            }
        });


        hfSpAddKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Calendar hfCalTmp = Calendar.getInstance();
                String hfFormat = "yyyy-MM-dd";    // 출력형식   2018-11-18
                SimpleDateFormat hfSdfExp = new SimpleDateFormat(hfFormat, Locale.KOREA);
                if (position == 1 || position == 2 || position == 11 || position == 12 || position == 13 || position == 14) {
                    //6개월 이내
                    //자외선 차단제,  립밤,           립스틱,         립글로스,          아이라이너,        마스카라
                    hfCalTmp.add(Calendar.DATE, 180);
                    hfEdExp.setText(hfSdfExp.format(hfCalTmp.getTime()));
                    hfTvComment.setText("사용 권장 기한 : 6개월 이내");
                } else if (position == 3) {
                    //8개월 이내
                    //에센스
                    hfEdExp.setText("Exp : 8개월 이내");
                    hfCalTmp.add(Calendar.DATE, 240);
                    hfEdExp.setText(hfSdfExp.format(hfCalTmp.getTime()));
                    hfTvComment.setText("사용 권장 기한 : 8개월 이내");
                } else if (position == 0 || position == 4 || position == 5 || position == 6 || position == 8 || position == 9 || position == 10 || position == 15) {
                    //1년 이내
                    //스킨,              크림,             메이크업 베이스,   컨실러,           아이새도우,        아이브로우,      블러셔,           클렌저
                    hfCalTmp.add(Calendar.DATE, 365);
                    hfEdExp.setText(hfSdfExp.format(hfCalTmp.getTime()));
                    hfTvComment.setText("사용 권장 기한 : 1년 이내");
                } else if (position == 7) {
                    //2년 이내
                    //파우더
                    hfCalTmp.add(Calendar.DATE, 730);
                    hfEdExp.setText(hfSdfExp.format(hfCalTmp.getTime()));
                    hfTvComment.setText("사용 권장 기한 : 2년 이내");
                } else if (position == 16) {
                    hfEdExp.setText("");
                }
                hfExpCal = hfCalTmp;
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
                        String hfStrbrand = hfEtBrand.getText().toString();
                        if (TextUtils.isEmpty(hfStrbrand)) {
                            Toast.makeText(getContext(), "Brand empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String hfStrname = hfEtName.getText().toString();
                        if (TextUtils.isEmpty(hfStrname)) {
                            Toast.makeText(getContext(), "Name empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 데이터 추가
                        addData(hfStrbrand, hfStrname, hfEdOpen.getText().toString(), hfEdExp.getText().toString(), hfSpAddKind.getSelectedItem().toString(),
                                setCheckBox(), hfEtVolume.getText().toString(), hfEtComment.getText().toString());
                    }
                })
                .setCancelable(true)
                .setTitle("화장품 추가 관리")
                .setView(hfViewDialog)
                .show();
    }


    /* 개봉일 현재 날짜로 설정 */
    public void setToday(EditText ed) {

        long hfUnixTimeNow = System.currentTimeMillis();
        Date hfDtNow = new Date(hfUnixTimeNow);
        hfSdfToday = new SimpleDateFormat("yyyy-MM-dd");
        String hfFormat = hfSdfToday.format(hfDtNow);
        ed.setText(hfFormat);
    }



    /* 화장품 정보 가져오기 */
    public void compareBarcode(String barcode){
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int hfIsSame = 0;

        try {
            // 쿼리문
            Log.d(TAG , "compareBarcode try 입성");

            String hfSelectBarcodeSql = "SELECT bID, bcdId, bcdBrand, bcdProduct, bcdVolume FROM barcodeInfos";
            Cursor hfBarcodeCur = db.rawQuery(hfSelectBarcodeSql, null);
            while (hfBarcodeCur.moveToNext()) {
                // 데이터
                Log.d(TAG , "compareBarcode while 입성");
                BarcodeInfo barcodeInfo = new BarcodeInfo(hfBarcodeCur.getInt(hfBarcodeCur.getColumnIndex("bID")),
                        hfBarcodeCur.getString(hfBarcodeCur.getColumnIndex("bcdId")), hfBarcodeCur.getString(hfBarcodeCur.getColumnIndex("bcdBrand")),
                        hfBarcodeCur.getString(hfBarcodeCur.getColumnIndex("bcdProduct")), hfBarcodeCur.getString(hfBarcodeCur.getColumnIndex("bcdVolume"))
                );
                String hfStrCmp = barcodeInfo.getBcdId().substring(0,13);
                Log.d(TAG , "barcode : "+barcode);
                Log.d(TAG , "hfStrCmp : "+hfStrCmp);

                if(hfStrCmp.equals(barcode)) {
                    Toast.makeText(getContext(), "바코드 정보 가져오기 성공", Toast.LENGTH_SHORT).show();
                    bcdBrand = barcodeInfo.getBcdBrand();
                    bcdProduct = barcodeInfo.getBcdProduct();
                    hfIsSame = 1;
                    break;
                } else{
                    String barcodeBrand = barcode.substring(0, 7);
                    String barcodeInfoBrand = hfStrCmp.substring(0, 7);

                    Log.d(TAG , "hfStrCmp - barcodeBrand : "+barcodeBrand);
                    Log.d(TAG , "hfStrCmp - barcodeInfoBrand : "+barcodeInfoBrand);

                    if (barcodeInfoBrand.equals(barcodeBrand)) {
                        bcdBrand = barcodeInfo.getBcdBrand();
                        bcdProduct = "";
                        Toast.makeText(getContext(), "바코드 브랜드 정보 가져오기 성공", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            if (hfIsSame == 0){
                Toast.makeText(getContext(), "바코드 정보 가져오기 실패", Toast.LENGTH_SHORT).show();
            }
            hfBarcodeCur.close();
        } catch (SQLException e) {
            Log.d(TAG , "compareBarcode error : " + e.getMessage());
        }

        db.close();
    }



    /* 리스트 구성 */
    private void listData() {
        this.hfCosArrList = new ArrayList<>();

        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 쿼리문
            String hfSelectCosSql = "SELECT * FROM cosmetics";
            Cursor hfCosCur = db.rawQuery(hfSelectCosSql, null);
            while (hfCosCur.moveToNext()) {
                // 데이터
                Cosmetics hfCos = new Cosmetics(hfCosCur.getInt(hfCosCur.getColumnIndex("cID")),
                        hfCosCur.getString(hfCosCur.getColumnIndex("brandName")), hfCosCur.getString(hfCosCur.getColumnIndex("productName")),
                        hfCosCur.getString(hfCosCur.getColumnIndex("dtOpen")), hfCosCur.getString(hfCosCur.getColumnIndex("dtExp")),
                        hfCosCur.getString(hfCosCur.getColumnIndex("kind")), hfCosCur.getInt(hfCosCur.getColumnIndex("alarm")),
                        hfCosCur.getString(hfCosCur.getColumnIndex("volume")), hfCosCur.getString(hfCosCur.getColumnIndex("additionalContent"))
                );

                this.hfCosArrList.add(hfCos);

            }

            hfCosCur.close();
        } catch (SQLException e) { }

        db.close();

        // 리스트 구성
        this.hfCosArrAdapter = new CosmeticArrayAdapter(getContext(), this.hfCosArrList);
        this.hfLvCos.setAdapter(this.hfCosArrAdapter);

    }


    /* 추가 */
    private void addData(String brand, String name, String open, String exp, String kind, int alarm, String volume, String additionalContent) {
        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        hfSpKind.setSelection(0);
        try {
            // 등록
            Object[] args = {brand, name, open, exp, kind, alarm, volume, additionalContent};
            String sql = insertCosSql;

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

        if (hfCbWeek.isChecked() == false){
            if (hfCbMonth.isChecked() == false)  return 0;
            else    return 2;
        }
        else{
            if (hfCbMonth.isChecked() == false)  return 1;
            else    return 3;
        }
    }

    /*얻은 바코드 id로 정보 가져오기*/

    void getBarcode(String barcode) {
        GetData task = new GetData();
        Log.d("확인", "barcode = " + barcode);
        task.execute(barcode);
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getContext(),
                    "Please Wait", null, true, true);
        }


        // 에러의 경우 에러메시지 보여주고 아니면 JSON 파싱하여 화면에 보여주기
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d("확인", "response - " + result);

            if (result == null){

                Log.d("확인", "error");
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String barcode = params[0];

            //HTTP 통신의 아규먼트로 하여 서버에 있는 PHP파일 실행
            String serverURL = "http://2d1be335.ngrok.io/PHP_connection.php";
            String postParameters = "bcdId=" + barcode;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("확인", "response code - " + responseStatusCode); // 오류확인

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                //StringBuilder를 사용하여 PHP가 보여준 문자열 저장, 스트링으로 변환해서 리턴
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON); //TAG_JSON 키를 갖는 JSONArray 가져오기

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String bid = item.getString(TAG_BID);
                String bcdId = item.getString(TAG_BCDID);
                bcdBrand = item.getString(TAG_BCDBRAND);
                bcdProduct = item.getString(TAG_BCDPRODUCT);
                String bcdVolume = item.getString(TAG_BCDVOLUME);

                //바코드 스캔을 통해 입력폼에 내용 입력
                hfEtBrand.setText(bcdBrand);
                hfEtName.setText(bcdProduct);
                hfEtVolume.setText(bcdVolume);
                Log.d("확인", "bid = " + bid + " bcdid = " + bcdId + " bcdBrand = " + bcdBrand);
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}
