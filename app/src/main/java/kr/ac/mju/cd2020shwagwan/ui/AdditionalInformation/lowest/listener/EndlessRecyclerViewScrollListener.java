package kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.listener;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private int ervslVisibleThreshold = 5;
    private int ervslCurrentPage = 0;
    private int ervslPreviousTotalItemCount = 0;
    private boolean ervslLoading = true;
    private int ervslStartingPageIndex = 0;

    RecyclerView.LayoutManager ervslLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.ervslLayoutManager = layoutManager;
    }

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        this.ervslLayoutManager = layoutManager;
        ervslVisibleThreshold = ervslVisibleThreshold * layoutManager.getSpanCount();
    }

    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
        this.ervslLayoutManager = layoutManager;
        ervslVisibleThreshold = ervslVisibleThreshold * layoutManager.getSpanCount();
    }

    public int getLastVisibleItem(int[] ervslLastVisibleItemPositions) {
        int ervslMaxSize = 0;
        for (int i = 0; i < ervslLastVisibleItemPositions.length; i++) {
            if (i == 0) {
                ervslMaxSize = ervslLastVisibleItemPositions[i];
            }
            else if (ervslLastVisibleItemPositions[i] > ervslMaxSize) {
                ervslMaxSize = ervslLastVisibleItemPositions[i];
            }
        }
        return ervslMaxSize;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int ervslLastVisibleItemPosition = 0;
        int ervslTotalItemCount = ervslLayoutManager.getItemCount();

        if (ervslLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] ervslLastVisibleItemPositions = ((StaggeredGridLayoutManager) ervslLayoutManager).findLastVisibleItemPositions(null);
            ervslLastVisibleItemPosition = getLastVisibleItem(ervslLastVisibleItemPositions);
        } else if (ervslLayoutManager instanceof GridLayoutManager) {
            ervslLastVisibleItemPosition = ((GridLayoutManager) ervslLayoutManager).findLastVisibleItemPosition();
        } else if (ervslLayoutManager instanceof LinearLayoutManager) {
            ervslLastVisibleItemPosition = ((LinearLayoutManager) ervslLayoutManager).findLastVisibleItemPosition();
        }

        if (ervslTotalItemCount < ervslPreviousTotalItemCount) {
            this.ervslCurrentPage = this.ervslStartingPageIndex;
            this.ervslPreviousTotalItemCount = ervslTotalItemCount;
            if (ervslTotalItemCount == 0) {
                this.ervslLoading = true;
            }
        }

        if (ervslLoading && (ervslTotalItemCount > ervslPreviousTotalItemCount)) {
            ervslLoading = false;
            ervslPreviousTotalItemCount = ervslTotalItemCount;
        }

        if (!ervslLoading && (ervslLastVisibleItemPosition + ervslVisibleThreshold) > ervslTotalItemCount) {
            ervslCurrentPage++;
            onLoadMore(ervslCurrentPage, ervslTotalItemCount, view);
            ervslLoading = true;
        }
    }

    public void resetState() {
        this.ervslCurrentPage = this.ervslStartingPageIndex;
        this.ervslPreviousTotalItemCount = 0;
        this.ervslLoading = true;
    }

    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);

}
