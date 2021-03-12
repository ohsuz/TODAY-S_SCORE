package com.example.ohjeom.ui.rankings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RankingsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RankingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Ranking");
    }

    public LiveData<String> getText() {
        return mText;
    }
}