package kr.ac.mju.cd2020shwagwan.ui.MyPage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MypageViewModel extends ViewModel {

    private MutableLiveData<String> mvmMLD;

    public MypageViewModel() {
        mvmMLD = new MutableLiveData<>();
        mvmMLD.setValue("This is My Page fragment");
    }

    public LiveData<String> getText() {
        return mvmMLD;
    }
}