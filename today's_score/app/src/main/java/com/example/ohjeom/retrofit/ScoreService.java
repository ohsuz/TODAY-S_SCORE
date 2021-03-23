package com.example.ohjeom.retrofit;

import com.example.ohjeom.models.Score;
import com.example.ohjeom.models.Template;
import com.example.ohjeom.models.Templates;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ScoreService {

    @FormUrlEncoded
    @POST("/api/score/addWakeScore")
    Call<ResponseBody> addWakeScore (
            @Field("userID") String userID,
            @Field("templateName") String templateName,
            @Field("date") String date,
            @Field("wakeScore") JsonObject wakeScore
    );

    @FormUrlEncoded
    @POST("/api/score/addSleepScore")
    Call<ResponseBody> addSleepScore (
            @Field("userID") String userID,
            @Field("templateName") String templateName,
            @Field("date") String date,
            @Field("sleepScore") JsonObject sleepScore
    );

    @FormUrlEncoded
    @POST("/api/score/addWalkScore")
    Call<ResponseBody> addWalkScore (
            @Field("userID") String userID,
            @Field("templateName") String templateName,
            @Field("date") String date,
            @Field("walkScore") JsonObject walkScore
    );

    @FormUrlEncoded
    @POST("/api/score/addPhoneUsageScore")
    Call<ResponseBody> addPhoneUsageScore (
            @Field("userID") String userID,
            @Field("templateName") String templateName,
            @Field("date") String date,
            @Field("phoneUsageScore") JsonObject phoneUsageScore
    );

    @FormUrlEncoded
    @POST("/api/score/addLocationScore")
    Call<ResponseBody> addLocationScore (
            @Field("userID") String userID,
            @Field("templateName") String templateName,
            @Field("date") String date,
            @Field("opt") int opt,
            @Field("locationScore") JsonObject locationScore
    );

    @FormUrlEncoded
    @POST("/api/score/addPaymentScore")
    Call<ResponseBody> addPaymentScore (
            @Field("userID") String userID,
            @Field("templateName") String templateName,
            @Field("date") String date,
            @Field("paymentScore") JsonObject paymentScore
    );

    @FormUrlEncoded
    @POST("/api/score/getScores")
    Call<Score> getScores (
            @Field("userID") String userID,
            @Field("date") String date
    );
}