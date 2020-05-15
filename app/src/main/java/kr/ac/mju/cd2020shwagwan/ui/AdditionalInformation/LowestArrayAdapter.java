package kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import kr.ac.mju.cd2020shwagwan.R;

public class LowestArrayAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList items;

    public LowestArrayAdapter(@NonNull Context context, ArrayList items) {
        super(context, 0, items);

        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lowest_price_item, parent, false);
        }

        TextView tvBrand = convertView.findViewById(R.id.tvBrand);
        TextView tvName = convertView.findViewById(R.id.tvName);

        tvBrand.setText("1");
        tvName.setText("1");

        return convertView;
    }
}
