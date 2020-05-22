package kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.search;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import kr.ac.mju.cd2020shwagwan.DBHelper;
import kr.ac.mju.cd2020shwagwan.R;
import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.util.ActivityUtils;

public class SearchActivity extends AppCompatActivity {

    private TextView saTvInfoBrand;
    private TextView saTvInfoKind;
    private TextView saTvInfoName;
    private TextView saTvInfoVolume;
    private TextView saTvInfoOpen;
    private TextView saTvInfoExp;
    private TextView saTvInfoAddCont;

    private String saBrandStr;
    private String saKindStr;
    private String saNameStr;
    private String saVolumeStr;
    private String saOpenStr;
    private String saExpStr;
    private String saAddContStr;
    private String saTitle;

    private EditText saEdOpen, saEdExp; //개봉일, 만료일
    private EditText saEtBrand, saEtName, saEtAddCont, saEtVolume; //브랜드명, 상품명, 추가사항, 용량
    private TextView saTvComment, saTvBarcode;
    int saOpenYear = 0, saOpenMonth = 0, saOpenDay = 0; //개봉일 연도, 월, 일
    private CheckBox saCbWeek, saCbMonth; //체크박스

    private ProgressBar saPbUsage;

    private Button saBtModify;

    private String saCheckStr;

    private Context saContext;

    private int saCosID;
    private int saAlarmCheck;

    private Calendar saExpCalendar = Calendar.getInstance(); //만료일을 위한 달력

    private Spinner saSpKinds;

    private static final String TAG = SearchActivity.class.getName();

    private SearchPresenter saSearchPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additional_information_page);

        setId();
        setDB();
        setText();
        setModify();

        SearchFragment saSearchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.flLowestList);
        // 앱의 검색 활동은 SearchFragment가 포함된 선형 레이아웃을 사용해야한다.
        // 이 프래그먼트는 검색 결과를 표시하기 위해 SearchFragment.SearchResultProvider 인터페이스도 구현해야한다.
        saSearchFragment = new SearchFragment(saNameStr);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), saSearchFragment, R.id.flLowestList);

        String saBaseUrl = getResources().getString(R.string.baseUrl);
        saSearchPresenter = new SearchPresenter(saSearchFragment, saBaseUrl);
    }

    public void setId() {
        saTvInfoBrand = findViewById(R.id.aip_tvBrand);
        saTvInfoKind = findViewById(R.id.aip_tvName);
        saTvInfoName = findViewById(R.id.aip_tvName);
        saTvInfoVolume = findViewById(R.id.aip_tvVolume);
        saTvInfoOpen = findViewById(R.id.aip_tvOpen);
        saTvInfoExp = findViewById(R.id.aip_tvExp);
        saTvInfoAddCont = findViewById(R.id.aip_tvAddCont);
        saPbUsage = findViewById(R.id.aip_pbUsage);
        saBtModify = findViewById(R.id.aip_btModify);
    }


    public void setDB() {
        saCosID = getIntent().getIntExtra("id", -1);
        saCheckStr = getIntent().getStringExtra("check");

        String sql;
        DBHelper saDbHelper = DBHelper.getInstance(this);
        SQLiteDatabase saDb = saDbHelper.getReadableDatabase();

        try {
            if(saCheckStr.equals("home")) {
                sql = "SELECT * FROM cosmetics WHERE cid='" + saCosID + "'";
            } else {
                sql = "SELECT * FROM mypage WHERE mid='" + saCosID + "'";
            }
            Cursor cursor = saDb.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                saBrandStr = cursor.getString(cursor.getColumnIndex("brandName"));
                saKindStr = cursor.getString(cursor.getColumnIndex("kind"));
                saNameStr = cursor.getString(cursor.getColumnIndex("productName"));
                saVolumeStr = cursor.getString(cursor.getColumnIndex("volume"));
                saOpenStr = cursor.getString(cursor.getColumnIndex("dtOpen"));
                saExpStr = cursor.getString(cursor.getColumnIndex("dtExp"));
                if(saCheckStr.equals("home")) {
                    saAlarmCheck = cursor.getInt(cursor.getColumnIndex("alarm"));
                }
                saAddContStr = cursor.getString(cursor.getColumnIndex("additionalContent"));
            }
            cursor.close();
        } catch (SQLException e) { }

        saDb.close();

        try{
            Thread.sleep(220);
            setText();
        }
        catch(Exception e){}

    }


    public void setText() {
        saTvInfoBrand.setText(saBrandStr);
        saTvInfoKind.setText(saKindStr);
        saTvInfoName.setText(saNameStr);
        saTvInfoVolume.setText(saVolumeStr);
        saTvInfoOpen.setText(saOpenStr);
        saTvInfoExp.setText(saExpStr);
        saTvInfoAddCont.setText(saAddContStr);

        if (saCheckStr.equals("completeUse")) {
            saPbUsage.setVisibility(View.GONE);
        }

        // 프로그레스바 설정
        try{
            SimpleDateFormat saTrans = new SimpleDateFormat("yyyy-MM-dd");
            Date open = saTrans.parse(saOpenStr);
            Date exp = saTrans.parse(saExpStr);

            long saPeriod = exp.getTime() - open.getTime();
            long saPeriodDay = saPeriod / (24 * 60 * 60 * 1000);

            saPbUsage.setMax((int)saPeriodDay);

            long now = System.currentTimeMillis();
            Date dt = new Date(now);

            long saUsage = dt.getTime() - open.getTime();
            long saUsageDay = saUsage / (24 * 60 * 60 * 1000);

            saPbUsage.setProgress((int) saUsageDay);

        }catch(Exception e){ }
    }


    public void setModify() {
        saBtModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModifyDialog();
            }
        });
    }

    /* 추가폼 호출 */
    public void showModifyDialog() {
        // AlertDialog View layout
        final Calendar saOpenCalendar = Calendar.getInstance(); //개봉일을 위한 달력
        LayoutInflater saInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View saLayout = saInflater.inflate(R.layout.content_add, null);

        //스피너 생성
        saSpKinds = saLayout.findViewById(R.id.ca_spKind);
        ArrayAdapter saKindsAdapter = ArrayAdapter.createFromResource(this, R.array.product_kinds_array, android.R.layout.simple_spinner_item);
        saKindsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        saSpKinds.setAdapter(saKindsAdapter);

        // findViewById
        saEdOpen = saLayout.findViewById(R.id.ca_edOpen);
        saEdExp = saLayout.findViewById(R.id.ca_edExp);
        saEtBrand = saLayout.findViewById(R.id.ca_etBrand);
        saEtName = saLayout.findViewById(R.id.ca_etName);
        saTvComment = saLayout.findViewById(R.id.ca_tvComment);
        saTvBarcode = saLayout.findViewById(R.id.ca_tvBarcode);
        saCbWeek = saLayout.findViewById(R.id.ca_cbWeek);
        saCbMonth = saLayout.findViewById(R.id.ca_cbMonth);
        saEtVolume = saLayout.findViewById(R.id.ca_etVolume);
        saEtAddCont = saLayout.findViewById(R.id.ca_etComment);
        TextView saTvChoose = saLayout.findViewById(R.id.ca_tvChoose);

        saTvBarcode.setVisibility(View.GONE);

        saEtBrand.setText(saBrandStr);
        saEtName.setText(saNameStr);
        saEdOpen.setText(saOpenStr);
        saEdExp.setText(saExpStr);
        saEtVolume.setText(saVolumeStr);
        saEtAddCont.setText(saAddContStr);

        int saSpinnerIndex = 0;
        String[] saProductKind = getResources().getStringArray(R.array.product_kinds_array);

        for (int i = 0; i < saProductKind.length; i++) {
            if(saKindStr.equals(saProductKind[i])) {
                saSpinnerIndex = i;
            }
        }

        saSpKinds.setSelection(saSpinnerIndex);
        saKindsAdapter.notifyDataSetChanged();

        if (saCheckStr.equals("home")) {
            switch (saAlarmCheck) {
                case 1:
                    saCbWeek.setChecked(true);
                    break;
                case 2:
                    saCbMonth.setChecked(true);
                    break;
                case 3:
                    saCbWeek.setChecked(true);
                    saCbMonth.setChecked(true);
                    break;
                default:
                    break;
            }
        } else {
            saCbWeek.setVisibility(View.GONE);
            saCbMonth.setVisibility(View.GONE);
            saTvChoose.setVisibility(View.GONE);
        }

        // 개봉일 캘린더로 선택
        saEdOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener saDatePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        saOpenCalendar.set(Calendar.YEAR, year);
                        saOpenCalendar.set(Calendar.MONTH, month);
                        saOpenCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String saFormat = "yyyy-MM-dd";    // 출력형식   2018-11-18
                        SimpleDateFormat saSdf = new SimpleDateFormat(saFormat, Locale.KOREA);

                        saEdOpen.setText(saSdf.format(saOpenCalendar.getTime()));

                        //상품별 만료일 자동 계산
                        if (saSpKinds.getSelectedItemPosition() == 1 || saSpKinds.getSelectedItemPosition() == 2
                                || saSpKinds.getSelectedItemPosition() == 11 || saSpKinds.getSelectedItemPosition() == 12
                                || saSpKinds.getSelectedItemPosition() == 13 || saSpKinds.getSelectedItemPosition() == 14) {
                            //6개월 이내
                            saOpenCalendar.add(Calendar.DATE, 180);
                            saEdExp.setText(saSdf.format(saOpenCalendar.getTime()));
                        } else if (saSpKinds.getSelectedItemPosition() == 3) {
                            //8개월 이내
                            saOpenCalendar.add(Calendar.DATE, 240);
                            saEdExp.setText(saSdf.format(saOpenCalendar.getTime()));
                        } else if (saSpKinds.getSelectedItemPosition() == 0 || saSpKinds.getSelectedItemPosition() == 4
                                || saSpKinds.getSelectedItemPosition() == 5 || saSpKinds.getSelectedItemPosition() == 6
                                || saSpKinds.getSelectedItemPosition() == 8 || saSpKinds.getSelectedItemPosition() == 9
                                || saSpKinds.getSelectedItemPosition() == 10 || saSpKinds.getSelectedItemPosition() == 15) {
                            //1년 이내
                            saOpenCalendar.add(Calendar.DATE, 365);
                            saEdExp.setText(saSdf.format(saOpenCalendar.getTime()));
                        } else if (saSpKinds.getSelectedItemPosition() == 7) {
                            //2년 이내
                            saOpenCalendar.add(Calendar.DATE, 730);
                            saEdExp.setText(saSdf.format(saOpenCalendar.getTime()));
                        } else if (saSpKinds.getSelectedItemPosition() == 16) {
                            Toast ts = Toast.makeText(saContext, "만료일을 설정하세요", Toast.LENGTH_SHORT);
                            ts.setGravity(Gravity.CENTER, 0, 0);
                            ts.show();
                            saEdExp.setText("");
                        }
                    }
                };

                saOpenYear = saOpenCalendar.get(Calendar.YEAR);
                saOpenMonth = saOpenCalendar.get(Calendar.MONTH);
                saOpenDay = saOpenCalendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(saContext, saDatePicker, saOpenYear, saOpenMonth, saOpenDay).show();

            }
        });


        // 만료일 캘린더로 선택 및 최소날짜 세팅
        saEdExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatePickerDialog.OnDateSetListener saDatePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        SharedPreferences saSp = saContext.getSharedPreferences("alarmTime", MODE_PRIVATE);

                        saExpCalendar.set(Calendar.YEAR, year);
                        saExpCalendar.set(Calendar.MONTH, month);
                        saExpCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        Log.d(TAG , "set HOME tvHour : " + saSp.getInt("hour", 22));
                        Log.d(TAG , "set HOME tvMinute : " + saSp.getInt("minute", 00));

                        saExpCalendar.set(Calendar.HOUR_OF_DAY, saSp.getInt("hour", 22));
                        saExpCalendar.set(Calendar.MINUTE, saSp.getInt("minute", 00));
                        String myFormat = "yyyy-MM-dd HH:mm:ss";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

                        saEdExp.setText(sdf.format(saExpCalendar.getTime()));
                        Log.d(TAG , "tvEXP : " + saExpCalendar.getTime());
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(saContext, saDatePicker, saExpCalendar.get(Calendar.YEAR), saExpCalendar.get(Calendar.MONTH), saExpCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(saOpenCalendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });


        saSpKinds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Calendar saCal = Calendar.getInstance();
                String saFormat = "yyyy-MM-dd";    // 출력형식   2018-11-11
                if (position == 1 || position == 2 || position == 11 || position == 12 || position == 13 || position == 14) {
                    //6개월 이내
                    //자외선 차단제,  립밤,           립스틱,         립글로스,          아이라이너,        마스카라
                    saTvComment.setText("사용 권장 기한 : 6개월 이내");
                } else if (position == 3) {
                    //8개월 이내
                    //에센스
                    saTvComment.setText("사용 권장 기한 : 8개월 이내");
                } else if (position == 0 || position == 4 || position == 5 || position == 6 || position == 8 || position == 9 || position == 10 || position == 15) {
                    //1년 이내
                    //스킨,              크림,             메이크업 베이스,   컨실러,           아이새도우,        아이브로우,      블러셔,           클렌저
                    saTvComment.setText("사용 권장 기한 : 1년 이내");
                } else if (position == 7) {
                    //2년 이내
                    //파우더
                    saTvComment.setText("사용 권장 기한 : 2년 이내");
                }
                saExpCalendar = saCal;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /* 추가폼에 데이터 입력 */
        new AlertDialog.Builder(saContext, R.style.MyAlertDialogStyle)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        // 추가
                        String brand = saEtBrand.getText().toString();
                        if (TextUtils.isEmpty(brand)) {
                            Toast.makeText(saContext, "Brand empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String name = saEtName.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(saContext, "Name empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 데이터 추가
                        ModifyData(brand, name, saEdOpen.getText().toString(), saEdExp.getText().toString(), saSpKinds.getSelectedItem().toString(),
                                setCheckBox(), saEtVolume.getText().toString(), saEtAddCont.getText().toString());
                    }
                })
                .setCancelable(true)
                .setTitle("정보 수정")
                .setView(saLayout)
                .show();
    }

    //체크박스 상태 함수
    private int setCheckBox() {
        // no : 0, week : 1, month : 2, all : 3

        if (saCbWeek.isChecked() == false){
            if (saCbMonth.isChecked() == false)  return 0;
            else    return 2;
        }
        else{
            if (saCbMonth.isChecked() == false)  return 1;
            else    return 3;
        }
    }

    void ModifyData(String brand, String name, String open, String exp, String kind, int alarm, String volume, String additionalContent) {
        // SQLite 사용
        DBHelper saDbHelper = DBHelper.getInstance(this);
        SQLiteDatabase saDb = saDbHelper.getReadableDatabase();

        try {

            if (saCheckStr.equals("home")) {
                Object[] args = {brand, name, open, exp, kind, alarm, volume, additionalContent, saCosID};
                String sql = "UPDATE cosmetics SET brandName = ?, productName = ?, dtOpen = ? " +
                        ", dtExp = ?, kind = ?, alarm = ?, volume = ?, additionalContent = ? WHERE cid = ?";

                saDb.execSQL(sql, args);
            } else {
                Object[] args = {brand, name, open, exp, kind, volume, additionalContent, saCosID};
                String sql = "UPDATE mypage SET brandName = ?, productName = ?, dtOpen = ? " +
                        ", dtExp = ?, kind = ?, volume = ?, additionalContent = ? WHERE mid = ?";

                saDb.execSQL(sql, args);
            }


        } catch (SQLException e) { }

        saDb.close();

        //새로고침
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }
}
