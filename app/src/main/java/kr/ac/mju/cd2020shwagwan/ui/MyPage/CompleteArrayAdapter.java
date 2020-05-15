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
import java.util.Date;

import kr.ac.mju.cd2020shwagwan.DBHelper;
import kr.ac.mju.cd2020shwagwan.R;
import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.AdditionalInformation;

public class CompleteArrayAdapter  extends ArrayAdapter {
    private Context mContext;
    private ArrayList mData;
    private TextView tvKind;

    public CompleteArrayAdapter(@NonNull Context context, ArrayList data) {
        super(context, 0, data);

        mContext = context;
        mData = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        final MyPage myPage = (MyPage) getItem(position);

        TextView tvBrand = convertView.findViewById(R.id.tvBrand);
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvOpen = convertView.findViewById(R.id.tvOpen);
        TextView tvExp = convertView.findViewById(R.id.tvExp);
        tvKind = convertView.findViewById(R.id.tvKind);


        tvBrand.setText(myPage.getBrandName());
        tvName.setText(myPage.getProductName());
        tvOpen.setText(myPage.getDtOpen());
        tvExp.setText(myPage.getDtExp());
        tvKind.setText(myPage.getKind());

        Button btComplete = convertView.findViewById(R.id.btComplete);
        ProgressBar pbUsage = convertView.findViewById(R.id.pbUsage);

        pbUsage.setVisibility(View.INVISIBLE);
        btComplete.setVisibility(View.GONE);

        Log.d("확인", "용량 = " + myPage.getVolume());


        // 삭제
        ImageView ivDel = convertView.findViewById(R.id.ivDel);
        ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                                // 삭제
                                deleteData(myPage);
                            }
                        })
                        .setNegativeButton("NO", null)
                        .setCancelable(false)
                        .setTitle("Do you want to Delete? ")
                        .setMessage(myPage.getProductName())
                        .show();
            }
        });

        LinearLayout itemLayout = convertView.findViewById(R.id.item_layout);

        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AdditionalInformation.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", myPage.getId());
                intent.putExtra("check", "completeUse");
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }


    /* 삭제 */
    private void deleteData(MyPage myPage) {
        // SQLite 사용
        DBHelper dbHelper = DBHelper.getInstance(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 삭제 (id(tID) 값으로 삭제)
            Object[] args = { myPage.getId() };
            String sql = "DELETE FROM mypage WHERE mID = ?";

            db.execSQL(sql, args);

            // 항목 삭제
            mData.remove(myPage);
            // 리스트 적용
            notifyDataSetChanged();

        } catch (SQLException e) {}

        db.close();
    }
}
