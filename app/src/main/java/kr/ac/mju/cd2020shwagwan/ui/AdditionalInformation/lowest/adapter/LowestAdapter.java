package kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.adapter;

import android.content.Intent;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.util.ArrayList;

import kr.ac.mju.cd2020shwagwan.MainActivity;
import kr.ac.mju.cd2020shwagwan.R;
import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.repository.ResponseItem;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.util.Constants.LOWEST_DISPLAY_SIZE;

public class LowestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ResponseItem> LowInfoArrList;

    public LowestAdapter(ArrayList<ResponseItem> lowInfoArrList) {
        LowInfoArrList = lowInfoArrList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater laInflater = LayoutInflater.from(parent.getContext());
        View laView = laInflater.inflate(R.layout.lowest_price_item, parent, false);
        return new LowCosViewHolder(laView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LowCosViewHolder lowcosViewHolder = (LowCosViewHolder) holder;

        ResponseItem item = LowInfoArrList.get(position);

        String getBrand = Html.fromHtml(item.getBrand()).toString();
        if (getBrand.equals("")) {
            lowcosViewHolder.brandLayout.setVisibility(View.INVISIBLE);
        }
        lowcosViewHolder.tvLowAdaptBrand.setText(getBrand);
        lowcosViewHolder.tvLowAdaptName.setText(Html.fromHtml(item.getTitle()));

        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        lowcosViewHolder.tvLowAdaptPrice.setText(decimalFormat.format(Integer.parseInt(Html.fromHtml(item.getLprice()).toString())));
        lowcosViewHolder.tvLowAdaptMall.setText(Html.fromHtml(item.getMallName()));
        try {
            Glide.with(MainActivity.getMyContext())
                    .load(item.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(lowcosViewHolder.getImage());
        } catch (NullPointerException e) {
            Log.d(TAG, "Not Found Image Url");
        }
    }

    @Override
    public int getItemCount() {
        return LowInfoArrList.size();
    }

    public ResponseItem getItem(int position) {
        return LowInfoArrList.get(position);
    }

    public void addItems(ArrayList<ResponseItem> items) {
        LowInfoArrList.addAll(items);
        notifyDataSetChanged();
    }

    public void clearItems() {
        LowInfoArrList.clear();
        notifyDataSetChanged();
    }

    public void clearAndAddItems(ArrayList<ResponseItem> items) {
        LowInfoArrList.clear();
        addItems(items);
        notifyDataSetChanged();
    }

    public static class LowCosViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivLowAdaptProductImg;
        private TextView tvLowAdaptBrand;
        private LinearLayout brandLayout;
        private TextView tvLowAdaptName;
        private TextView tvLowAdaptPrice;
        private TextView tvLowAdaptMall;

        LowCosViewHolder(View view) {
            super(view);
            ivLowAdaptProductImg = view.findViewById(R.id.lpi_ivProduct);
            tvLowAdaptBrand = view.findViewById(R.id.lpi_tvBrand);
            tvLowAdaptName = view.findViewById(R.id.lpi_tvName);
            tvLowAdaptPrice = view.findViewById(R.id.lpi_tvPrice);
            tvLowAdaptMall = view.findViewById(R.id.lpi_tvMall);
            brandLayout = view.findViewById(R.id.lpi_brand_layout);
        }

        public ImageView getImage() {
            return ivLowAdaptProductImg;
        }

    }
}
