package kr.ac.mju.cd2020shwagwan.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> hfMLD;


    public HomeViewModel() {
        hfMLD = new MutableLiveData<>();
        hfMLD.setValue("This is home fragment");
    }



    public LiveData<String> getText() {
        return hfMLD;
    }
}