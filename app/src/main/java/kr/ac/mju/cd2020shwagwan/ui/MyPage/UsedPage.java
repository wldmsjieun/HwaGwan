package kr.ac.mju.cd2020shwagwan.ui.MyPage;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import kr.ac.mju.cd2020shwagwan.DBHelper;
import kr.ac.mju.cd2020shwagwan.R;

public class UsedPage extends AppCompatActivity {

    private ListView upLvComplete;
    private String mSql;
    private UsedArrayAdapter upUsedArrAdapter;
    private ArrayList<MyPage> upMypageArrList;
    private Spinner upSpKind; //사용내역 목록에서 화장품 종류 스피너

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_completeduse);
        setId();
        setSpinner();
        setList();
    }

    void setId() {
        upLvComplete = findViewById(R.id.mc_lvUsed);
    }

    void setKind(String kind, boolean all) {
        upMypageArrList = new ArrayList<>();

        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 쿼리문
            if (all) {
                mSql = "SELECT * FROM mypage";
            } else {
                mSql = "SELECT * FROM mypage WHERE kind='" + kind + "'";
            }
            Cursor cursor = db.rawQuery(mSql, null);
            while (cursor.moveToNext()) {
                // 데이터
                MyPage upMypage = new MyPage(cursor.getInt(cursor.getColumnIndex("mID")),
                        cursor.getString(cursor.getColumnIndex("brandName")), cursor.getString(cursor.getColumnIndex("productName")),
                        cursor.getString(cursor.getColumnIndex("dtOpen")), cursor.getString(cursor.getColumnIndex("dtExp")),
                        cursor.getString(cursor.getColumnIndex("kind")), cursor.getString(cursor.getColumnIndex("volume")),
                        cursor.getString(cursor.getColumnIndex("additionalContent")));

                upMypageArrList.add(upMypage);
            }

            cursor.close();
        } catch (SQLException e) {
        }

        db.close();

        // 리스트 구성
        upUsedArrAdapter = new UsedArrayAdapter(this, upMypageArrList);
        upLvComplete.setAdapter(upUsedArrAdapter);
    }

    // 사용완료 내역에서 화장품 종류 스피너 설정
    void setSpinner() {
        upSpKind = findViewById(R.id.mc_spKind);
        ArrayAdapter upArrAdapter = ArrayAdapter.createFromResource(this, R.array.kinds_array, android.R.layout.simple_spinner_item);
        upArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        upSpKind.setAdapter(upArrAdapter);

        upSpKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    setKind(upSpKind.getSelectedItem().toString(), true);
                }
                else {
                    setKind(upSpKind.getSelectedItem().toString(), false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void setList() {
        upMypageArrList = new ArrayList<>();

        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            mSql = "SELECT * FROM mypage";

            Cursor upMypageCur = db.rawQuery(mSql, null);
            while (upMypageCur.moveToNext()) {
                // 데이터
                MyPage myPage = new MyPage(upMypageCur.getInt(upMypageCur.getColumnIndex("mID")),
                        upMypageCur.getString(upMypageCur.getColumnIndex("brandName")), upMypageCur.getString(upMypageCur.getColumnIndex("productName")),
                        upMypageCur.getString(upMypageCur.getColumnIndex("dtOpen")), upMypageCur.getString(upMypageCur.getColumnIndex("dtExp")),
                        upMypageCur.getString(upMypageCur.getColumnIndex("kind")), upMypageCur.getString(upMypageCur.getColumnIndex("volume")),
                        upMypageCur.getString(upMypageCur.getColumnIndex("additionalContent"))
                );

                upMypageArrList.add(myPage);
            }

            upMypageCur.close();
        } catch (SQLException e) {
        }

        db.close();

        upUsedArrAdapter = new UsedArrayAdapter(this, upMypageArrList);

        upLvComplete.setAdapter(upUsedArrAdapter);
    }
}
