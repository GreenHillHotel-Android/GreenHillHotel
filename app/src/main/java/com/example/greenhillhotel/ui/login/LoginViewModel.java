package com.example.greenhillhotel.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LoginViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Tu będzie logowanie");
    }

    public LiveData<String> getText() {
        return mText;
    }
}