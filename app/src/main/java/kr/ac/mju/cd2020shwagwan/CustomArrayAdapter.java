package kr.ac.mju.cd2020shwagwan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList items;

    public CustomArrayAdapter(Context context, ArrayList items) {
        super(context, 0, items);

        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        return convertView;
    }
}