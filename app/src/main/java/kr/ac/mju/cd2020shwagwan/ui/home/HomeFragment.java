package kr.ac.mju.cd2020shwagwan.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import kr.ac.mju.cd2020shwagwan.Cosmetics;
import kr.ac.mju.cd2020shwagwan.CustomArrayAdapter;
import kr.ac.mju.cd2020shwagwan.DBHelper;
import kr.ac.mju.cd2020shwagwan.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ListView listView;
    private CustomArrayAdapter adapter;

    private ArrayList<Cosmetics> items;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final FloatingActionButton fabAdd = root.findViewById(R.id.fabAdd);
        listView = root.findViewById(R.id.lvItem);

        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
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