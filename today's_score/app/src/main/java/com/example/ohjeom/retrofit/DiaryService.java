package com.example.ohjeom.retrofit;

import com.example.ohjeom.models.Diary;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DiaryService {
    @FormUrlEncoded
    @POST("/api/diary/addDiary")
    Call<ResponseBody> addDiary (
            @Field("userID") String userID,
            @Field("date") String date,
            @Field("title") String title,
            @Field("content") String content,
            @Field("good") String good,
            @Field("bad") String bad
    );

    @FormUrlEncoded
    @POST("/api/diary/getDiary")
    Call<Diary> getDiary (
            @Field("userID") String userID,
            @Field("date") String date
    );
}
