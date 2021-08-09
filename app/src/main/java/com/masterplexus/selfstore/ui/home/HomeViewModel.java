package com.masterplexus.selfstore.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public void setmText(String mText) {
        this.mText.setValue(mText);
    }

    public LiveData<String> getText() {
        return mText;
    }
}