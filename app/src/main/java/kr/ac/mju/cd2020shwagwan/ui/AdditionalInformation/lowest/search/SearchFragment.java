package kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import kr.ac.mju.cd2020shwagwan.R;
import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.adapter.LowestAdapter;
import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.customtab.CustomTabServiceController;
import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.listener.EndlessRecyclerViewScrollListener;
import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.listener.RecyclerItemClickListener;
import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.repository.ResponseItem;
import static kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.util.Constants.LOWEST_DISPLAY_SIZE;

public class SearchFragment extends Fragment implements SearchContract.View, View.OnClickListener {

    private static final String TAG = SearchFragment.class.getName();
    private SearchPresenter sfPresenter;
    private RecyclerView sfRecyclerView;
    private LowestAdapter sfLowestAdapter;
    private RecyclerView.LayoutManager sfLayoutManager;
    private InputMethodManager sfInputMethodManager;
    private EndlessRecyclerViewScrollListener sfEndlessRecyclerViewScrollListener;
    private String sfProduct, sfKind;

    public static SearchFragment newInstance() {
        return new SearchFragment("","");
    }

    public SearchFragment(String product, String kind) {
        sfProduct = product;
        sfKind = kind;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View sfRoot = inflater.inflate(R.layout.lowest_list, container, false);
        setupRecyclerView(sfRoot);

        sfInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        sfInputMethodManager.hideSoftInputFromWindow(sfRoot.getWindowToken(), 0);
        sfEndlessRecyclerViewScrollListener.resetState();
        sfPresenter.startSearch(sfProduct);
        return sfRoot;
    }

    @Override
    public void onResume() {
        super.onResume();
        sfPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull SearchContract.Presenter presenter) {
        sfPresenter = (SearchPresenter) presenter;
    }

    private void setupRecyclerView(View view) {
        sfRecyclerView = view.findViewById(R.id.rvLowest);
        sfRecyclerView.setHasFixedSize(true);
        sfLayoutManager = new LinearLayoutManager(getContext());
        sfRecyclerView.setLayoutManager(sfLayoutManager);
        sfRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                sfRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                showLowCosPage(position);
            }
        }));

        ArrayList<ResponseItem> LowInfoArrayList = new ArrayList<>();

        sfEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) sfLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                sfPresenter.getLowCos(sfProduct, page * LOWEST_DISPLAY_SIZE + 1, "asc");
            }
        };
        sfRecyclerView.addOnScrollListener(sfEndlessRecyclerViewScrollListener);
        sfLowestAdapter = new LowestAdapter(LowInfoArrayList);
        sfRecyclerView.setAdapter(sfLowestAdapter);
    }

    @Override
    public void onClick(View view) {
        sfInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        sfEndlessRecyclerViewScrollListener.resetState();
        sfPresenter.startSearch(sfProduct);
    }

    @Override
    public void showEmptyField() {
        // Toast.makeText(getContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNotFindItem() {
        sfLowestAdapter.clearItems();
        Toast.makeText(getContext(), "검색결과는 없습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLowCosPage(int position) {
        //sfLowestAdapter = sfLowestAdapter.getLowestAdapter();
        String sfUrl = sfLowestAdapter.getItem(position).getLink();
        Intent sfIntent = setupCustomTabs(sfUrl);
        startActivity(sfIntent);
    }

    private Intent setupCustomTabs(String url) {
        CustomTabServiceController customTabServiceController = new CustomTabServiceController(getContext(), url);
        customTabServiceController.bindCustomTabService();
        Intent sfCustomTabIntent = customTabServiceController.createCustomTabIntent(null, Color.rgb(38,182,172));
        customTabServiceController.unbindCustomTabService();
        return sfCustomTabIntent;
    }

    @Override
    public void showMoreLowCos(ArrayList<ResponseItem> items) {
        sfLowestAdapter.addItems(items);
    }

    @Override
    public void showNewLowCos(ArrayList<ResponseItem> items) {
        sfLayoutManager.scrollToPosition(0);
        sfLowestAdapter.clearAndAddItems(items);
    }
}