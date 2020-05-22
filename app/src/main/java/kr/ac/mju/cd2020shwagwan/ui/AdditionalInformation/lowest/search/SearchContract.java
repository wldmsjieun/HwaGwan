package kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.search;

import java.util.ArrayList;
import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.base.BasePresenter;
import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.base.BaseView;
import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.repository.ResponseItem;

public interface SearchContract {

    interface View extends BaseView<Presenter> {

        void showEmptyField();
        void showNotFindItem();
        void showLowCosPage(int position);
        void showMoreLowCos(ArrayList<ResponseItem> items);
        void showNewLowCos(ArrayList<ResponseItem> items);

    }

    interface Presenter extends BasePresenter {

        void startSearch(String title);
        void getLowCos(String title, int startPosition);

    }

}
