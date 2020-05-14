package kr.ac.mju.cd2020shwagwan.ui.MyPage;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import kr.ac.mju.cd2020shwagwan.Cosmetics;
import kr.ac.mju.cd2020shwagwan.CustomArrayAdapter;
import kr.ac.mju.cd2020shwagwan.DBHelper;
import kr.ac.mju.cd2020shwagwan.R;

public class CompletedUse extends AppCompatActivity {
    private ListView mCompletedUseListview;

    private String mSql;

    private CompleteArrayAdapter mCompleteArrayAdapter;

    private ArrayList<MyPage> mMyPageArrayList;

    private View mRoot;

    private Spinner mKindSpinner; //사용내역 목록에서 화장품 종류 스피너

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_completeduse);
        setId();
        setSpinner();
        setList();

    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        Log.d("확인", "CompletedUse onDestroyView");
//        mFragmentManager.popBackStack();
//        mFragmentManager.beginTransaction()
//                .remove(CompletedUse.this)
//                .commit();
//    }

    void setId() {
        mCompletedUseListview = (ListView) findViewById(R.id.completed_use_listview);
    }

    void setKind(String kind, boolean all) {
        mMyPageArrayList = new ArrayList<>();

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
                MyPage myPage = new MyPage(cursor.getInt(cursor.getColumnIndex("mID")),
                        cursor.getString(cursor.getColumnIndex("brandName")), cursor.getString(cursor.getColumnIndex("productName")),
                        cursor.getString(cursor.getColumnIndex("dtOpen")), cursor.getString(cursor.getColumnIndex("dtExp")),
                        cursor.getString(cursor.getColumnIndex("kind")), cursor.getString(cursor.getColumnIndex("volume")),
                        cursor.getString(cursor.getColumnIndex("additionalContent")));

                mMyPageArrayList.add(myPage);
            }

            cursor.close();
        } catch (SQLException e) {
        }

        db.close();

        // 리스트 구성
        mCompleteArrayAdapter = new CompleteArrayAdapter(this, mMyPageArrayList);
        mCompletedUseListview.setAdapter(mCompleteArrayAdapter);
    }

    // 사용완료 내역에서 화장품 종류 스피너 설정
    void setSpinner() {
        mKindSpinner = (Spinner) findViewById(R.id.spUseKindSpinner);
        ArrayAdapter kindsAdapter = ArrayAdapter.createFromResource(this, R.array.kinds_array, android.R.layout.simple_spinner_item);
        kindsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mKindSpinner.setAdapter(kindsAdapter);

        mKindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    setKind(mKindSpinner.getSelectedItem().toString(), true);
                }
                else {
                    setKind(mKindSpinner.getSelectedItem().toString(), false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void setList() {
        mMyPageArrayList = new ArrayList<>();

        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            mSql = "SELECT * FROM mypage";

            Cursor cursor = db.rawQuery(mSql, null);
            while (cursor.moveToNext()) {
                // 데이터
                MyPage myPage = new MyPage(cursor.getInt(cursor.getColumnIndex("mID")),
                        cursor.getString(cursor.getColumnIndex("brandName")), cursor.getString(cursor.getColumnIndex("productName")),
                        cursor.getString(cursor.getColumnIndex("dtOpen")), cursor.getString(cursor.getColumnIndex("dtExp")),
                        cursor.getString(cursor.getColumnIndex("kind")), cursor.getString(cursor.getColumnIndex("volume")),
                        cursor.getString(cursor.getColumnIndex("additionalContent"))
                );

                mMyPageArrayList.add(myPage);
            }

            cursor.close();
        } catch (SQLException e) {
        }

        db.close();

        mCompleteArrayAdapter = new CompleteArrayAdapter(this, mMyPageArrayList);

        mCompletedUseListview.setAdapter(mCompleteArrayAdapter);
    }
}
