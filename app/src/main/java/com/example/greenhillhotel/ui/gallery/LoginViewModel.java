package com.example.greenhillhotel.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LoginViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Tu będzie cos");
    }

    public LiveData<String> getText() {
        return mText;
    }
}