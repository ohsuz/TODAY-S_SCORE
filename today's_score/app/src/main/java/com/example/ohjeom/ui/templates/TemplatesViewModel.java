package com.example.ohjeom.ui.templates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TemplatesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TemplatesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Templates");
    }

    public LiveData<String> getText() {
        return mText;
    }
}