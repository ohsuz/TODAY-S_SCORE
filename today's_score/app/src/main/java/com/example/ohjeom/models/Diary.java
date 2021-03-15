package com.example.ohjeom.models;

import android.util.Log;

import com.example.ohjeom.retrofit.DiaryService;
import com.example.ohjeom.retrofit.RetrofitClient;
import com.example.ohjeom.retrofit.ScoreService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Diary {
    private String title;
    private String content;
    private String good;
    private String bad;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getGood() {
        return good;
    }

    public String getBad() {
        return bad;
    }

    public static void getScores(String userID, String date) {
        Retrofit retrofit = RetrofitClient.getInstance();
        DiaryService diaryService = retrofit.create(DiaryService.class);

        diaryService.getDiary(userID, date).enqueue(new Callback<Diary>() {
            @Override
            public void onResponse(Call<Diary> call, Response<Diary> response) {
                Log.d("@@@@@@@@", String.valueOf(response.body()));
                Diary diary = new Diary();
                diary = response.body();
            }
            @Override
            public void onFailure(Call<Diary> call, Throwable t) {
                Log.d("DiaryService", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }
}
