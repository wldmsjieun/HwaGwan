package kr.ac.mju.cd2020shwagwan.ui.MyPage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import kr.ac.mju.cd2020shwagwan.DBHelper;
import kr.ac.mju.cd2020shwagwan.R;
import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.search.SearchActivity;

public class UsedArrayAdapter extends ArrayAdapter {
    private Context uaaContext;
    private ArrayList uaaArrList;
    private TextView uaaTvKind;

    public UsedArrayAdapter(@NonNull Context context, ArrayList data) {
        super(context, 0, data);

        uaaContext = context;
        uaaArrList = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cosmetic_item, parent, false);
        }
        final MyPage caaMypage = (MyPage) getItem(position);

        TextView uaaTvBrand = convertView.findViewById(R.id.ci_tvBrand);
        TextView uaaTvName = convertView.findViewById(R.id.ci_tvName);
        TextView uaaTvOpen = convertView.findViewById(R.id.ci_tvOpen);
        TextView uaaTvExp = convertView.findViewById(R.id.ci_tvExp);
        uaaTvKind = convertView.findViewById(R.id.ci_tvKind);


        uaaTvBrand.setText(caaMypage.getBrandName());
        uaaTvName.setText(caaMypage.getProductName());
        uaaTvOpen.setText(caaMypage.getDtOpen());
        uaaTvExp.setText(caaMypage.getDtExp());
        uaaTvKind.setText(caaMypage.getKind());

        Button uaaBtnUsed = convertView.findViewById(R.id.ci_btUsed);
        ProgressBar uaaPbUsage = convertView.findViewById(R.id.ci_pbUsage);

        uaaPbUsage.setVisibility(View.INVISIBLE);
        uaaBtnUsed.setVisibility(View.GONE);

        Log.d("확인", "용량 = " + caaMypage.getVolume());


        // 삭제
        ImageView ivDel = convertView.findViewById(R.id.ci_ivDel);
        ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(uaaContext)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                // 삭제
                                deleteData(caaMypage);
                            }
                        })
                        .setNegativeButton("NO", null)
                        .setCancelable(false)
                        .setTitle("Do you want to Delete? ")
                        .setMessage(caaMypage.getProductName())
                        .show();
            }
        });

        LinearLayout uaaLayout = convertView.findViewById(R.id.ci_layout);

        uaaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", caaMypage.getId());
                intent.putExtra("check", "completeUse");
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }


    /* 삭제 */
    private void deleteData(MyPage myPage) {
        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(uaaContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 삭제 (id(tID) 값으로 삭제)
            Object[] args = { myPage.getId() };
            String sql = "DELETE FROM mypage WHERE mID = ?";

            db.execSQL(sql, args);

            // 항목 삭제
            uaaArrList.remove(myPage);
            // 리스트 적용
            notifyDataSetChanged();

        } catch (SQLException e) {}

        db.close();
    }
}
