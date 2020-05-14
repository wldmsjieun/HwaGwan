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
import android.widget.ListView;

import java.util.ArrayList;

import kr.ac.mju.cd2020shwagwan.DBHelper;
import kr.ac.mju.cd2020shwagwan.R;

public class CompletedUse extends AppCompatActivity {
    private ListView mCompletedUseListview;

    private String mSql;

    private CompleteArrayAdapter mCompleteArrayAdapter;

    private ArrayList<MyPage> mMyPageArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_completeduse);
        setId();

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
