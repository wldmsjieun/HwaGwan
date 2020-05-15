package kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kr.ac.mju.cd2020shwagwan.Cosmetics;
import kr.ac.mju.cd2020shwagwan.DBHelper;
import kr.ac.mju.cd2020shwagwan.R;
import kr.ac.mju.cd2020shwagwan.ui.MyPage.CompletedUse;
import kr.ac.mju.cd2020shwagwan.ui.home.HomeFragment;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AdditionalInformation extends AppCompatActivity {

    private TextView tvBrand;
    private TextView tvKind;
    private TextView tvName;
    private TextView tvVolume;
    private TextView tvOeen;
    private TextView tvExp;
    private TextView tvAddCont;

    private String brandStr;
    private String mKindString;
    private String nameStr;
    private String volumeStr;
    private String openStr;
    private String expStr;
    private String addContStr;

    private EditText edOpen, edExp; //개봉일, 만료일
    private EditText etBrand, etName, etAddCont, etVolume; //브랜드명, 상품명, 추가사항, 용량
    private TextView tvComment,tvBarcode;
    int openYear = 0, openMonth = 0, openDay = 0; //개봉일 연도, 월, 일
    private CheckBox cbWeek, cbMonth; //체크박스

    private ProgressBar pbUsage;

    private Button btModify;

    private String mCheck;

    private Context mContext;

    private int mId;
    private int mAlarmCheck;

    private Calendar expCalendar = Calendar.getInstance(); //만료일을 위한 달력

    private Spinner spKinds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additional_information_page);
        mContext = this;

        setId();

        setDB();

        setText();

        setModify();
    }

    void setId() {
        tvBrand = findViewById(R.id.additional_information_tvBrand);
        tvKind =findViewById(R.id.additional_information_tvKind);
        tvName = findViewById(R.id.additional_information_tvName);
        tvVolume = findViewById(R.id.additional_information_volume);
        tvOeen = findViewById(R.id.additional_information_tvOpen);
        tvExp = findViewById(R.id.additional_information_tvExp);
        tvAddCont = findViewById(R.id.additional_information_additionalContent);

        pbUsage = findViewById(R.id.additional_information_pbUsage);

        btModify = findViewById(R.id.btModify);
    }

    void setDB() {
        mId = getIntent().getIntExtra("id", -1);
        mCheck = getIntent().getStringExtra("check");
        String sql;
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            if(mCheck.equals("home")) {
                sql = "SELECT * FROM cosmetics WHERE cid='" + mId + "'";
            } else {
                sql = "SELECT * FROM mypage WHERE mid='" + mId + "'";
            }
            Cursor cursor = db.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                brandStr = cursor.getString(cursor.getColumnIndex("brandName"));
                mKindString = cursor.getString(cursor.getColumnIndex("kind"));
                nameStr = cursor.getString(cursor.getColumnIndex("productName"));
                volumeStr = cursor.getString(cursor.getColumnIndex("volume"));
                openStr = cursor.getString(cursor.getColumnIndex("dtOpen"));
                expStr = cursor.getString(cursor.getColumnIndex("dtExp"));
                if(mCheck.equals("home")) {
                    mAlarmCheck = cursor.getInt(cursor.getColumnIndex("alarm"));
                }
                addContStr = cursor.getString(cursor.getColumnIndex("additionalContent"));
            }
            cursor.close();
        } catch (SQLException e) { }

        db.close();
    }

    void setText() {
        tvBrand.setText(brandStr);
        tvKind.setText(mKindString);
        tvName.setText(nameStr);
        tvVolume.setText(volumeStr);
        tvOeen.setText(openStr);
        tvExp.setText(expStr);
        tvAddCont.setText(addContStr);

        if (mCheck.equals("completeUse")) {
            pbUsage.setVisibility(View.GONE);
        }

        // 프로그레스바 설정
        try{
            SimpleDateFormat trans = new SimpleDateFormat("yyyy-MM-dd");
            Date open = trans.parse(openStr);
            Date exp = trans.parse(expStr);

            long period = exp.getTime() - open.getTime();
            long periodDay = period / (24 * 60 * 60 * 1000);

            pbUsage.setMax((int)periodDay);

            long now = System.currentTimeMillis();
            Date dt = new Date(now);

            long usage = dt.getTime() - open.getTime();
            long usageDay = usage / (24 * 60 * 60 * 1000);

            pbUsage.setProgress((int) usageDay);

        }catch(Exception e){ }
    }

    void setModify() {
        btModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModifyDialog();
            }
        });
    }

    /* 추가폼 호출 */
    public void showModifyDialog() {
        // AlertDialog View layout
        final Calendar openCalendar = Calendar.getInstance(); //개봉일을 위한 달력
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.content_add, null);

        //스피너 생성
        spKinds = layout.findViewById(R.id.spKinds);
        ArrayAdapter kindsAdapter = ArrayAdapter.createFromResource(this, R.array.product_kinds_array, android.R.layout.simple_spinner_item);
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
        TextView tvChoose = layout.findViewById(R.id.tvChoose);

        tvBarcode.setVisibility(View.GONE);

        etBrand.setText(brandStr);
        etName.setText(nameStr);
        edOpen.setText(openStr);
        edExp.setText(expStr);
        etVolume.setText(volumeStr);
        etAddCont.setText(addContStr);

        int spinnerIndex = 0;
        String[] productKind = getResources().getStringArray(R.array.product_kinds_array);

        for (int i = 0; i < productKind.length; i++) {
            if(mKindString.equals(productKind[i])) {
                spinnerIndex = i;
            }
        }

        spKinds.setSelection(spinnerIndex);
        kindsAdapter.notifyDataSetChanged();

        if (mCheck.equals("home")) {
            switch (mAlarmCheck) {
                case 1:
                    cbWeek.setChecked(true);
                    break;
                case 2:
                    cbMonth.setChecked(true);
                    break;
                case 3:
                    cbWeek.setChecked(true);
                    cbMonth.setChecked(true);
                    break;
                default:
                    break;
            }
        } else {
            cbWeek.setVisibility(View.GONE);
            cbMonth.setVisibility(View.GONE);
            tvChoose.setVisibility(View.GONE);
        }

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
                            Toast ts = Toast.makeText(mContext, "만료일을 설정하세요", Toast.LENGTH_SHORT);
                            ts.setGravity(Gravity.CENTER, 0, 0);
                            ts.show();
                            edExp.setText("");
                        }
                    }
                };

                openYear = openCalendar.get(Calendar.YEAR);
                openMonth = openCalendar.get(Calendar.MONTH);
                openDay = openCalendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(mContext, myDatePicker, openYear, openMonth, openDay).show();

            }
        });


        // 만료일 캘린더로 선택 및 최소날짜 세팅
        edExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        SharedPreferences sp = mContext.getSharedPreferences("alarmTime", MODE_PRIVATE);

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, myDatePicker, expCalendar.get(Calendar.YEAR), expCalendar.get(Calendar.MONTH), expCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(openCalendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });


        spKinds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Calendar cCal = Calendar.getInstance();
                String myFormat = "yyyy-MM-dd";    // 출력형식   2018-11-11
                if (position == 1 || position == 2 || position == 11 || position == 12 || position == 13 || position == 14) {
                    //6개월 이내
                    //자외선 차단제,  립밤,           립스틱,         립글로스,          아이라이너,        마스카라
                    tvComment.setText("사용 권장 기한 : 6개월 이내");
                } else if (position == 3) {
                    //8개월 이내
                    //에센스
                    tvComment.setText("사용 권장 기한 : 8개월 이내");
                } else if (position == 0 || position == 4 || position == 5 || position == 6 || position == 8 || position == 9 || position == 10 || position == 15) {
                    //1년 이내
                    //스킨,              크림,             메이크업 베이스,   컨실러,           아이새도우,        아이브로우,      블러셔,           클렌저
                    tvComment.setText("사용 권장 기한 : 1년 이내");
                } else if (position == 7) {
                    //2년 이내
                    //파우더
                    tvComment.setText("사용 권장 기한 : 2년 이내");
                }
                expCalendar = cCal;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /* 추가폼에 데이터 입력 */
        new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        // 추가
                        String brand = etBrand.getText().toString();
                        if (TextUtils.isEmpty(brand)) {
                            Toast.makeText(mContext, "Brand empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String name = etName.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(mContext, "Name empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 데이터 추가
                        ModifyData(brand, name, edOpen.getText().toString(), edExp.getText().toString(), spKinds.getSelectedItem().toString(),
                                setCheckBox(), etVolume.getText().toString(), etAddCont.getText().toString());
                    }
                })
                .setCancelable(true)
                .setTitle("정보 수정")
                .setView(layout)
                .show();
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

    void ModifyData(String brand, String name, String open, String exp, String kind, int alarm, String volume, String additionalContent) {
        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {

            if (mCheck.equals("home")) {
                Object[] args = {brand, name, open, exp, kind, alarm, volume, additionalContent, mId};
                String sql = "UPDATE cosmetics SET brandName = ?, productName = ?, dtOpen = ? " +
                        ", dtExp = ?, kind = ?, alarm = ?, volume = ?, additionalContent = ? WHERE cid = ?";

                db.execSQL(sql, args);
            } else {
                Object[] args = {brand, name, open, exp, kind, volume, additionalContent, mId};
                String sql = "UPDATE mypage SET brandName = ?, productName = ?, dtOpen = ? " +
                        ", dtExp = ?, kind = ?, volume = ?, additionalContent = ? WHERE mid = ?";

                db.execSQL(sql, args);
            }


        } catch (SQLException e) { }

        db.close();

        //새로고침
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }
}
