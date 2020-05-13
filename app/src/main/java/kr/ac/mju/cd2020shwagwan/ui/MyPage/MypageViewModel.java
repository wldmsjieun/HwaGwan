package kr.ac.mju.cd2020shwagwan.ui.MyPage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MypageViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MypageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is My Page fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}