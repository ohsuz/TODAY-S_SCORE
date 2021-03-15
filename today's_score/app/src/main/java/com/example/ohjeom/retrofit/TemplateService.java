package com.example.ohjeom.retrofit;
import com.example.ohjeom.models.Template;
import com.example.ohjeom.models.Templates;
import com.google.gson.JsonObject;

import java.util.HashMap;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TemplateService {
    @POST("/api/template/register")
    Call<ResponseBody> registerTemplate (
            @Body JsonObject body
    );

    @FormUrlEncoded
    @POST("/api/template/getPrivateNames")
    Call<Templates> getPrivateNames (
            //@Body JsonObject body // @Query("보낼거") String 어쩌구 아마 사용자 id를 보내야할듯하다
            @Field("userID") String userID
    );

    @FormUrlEncoded
    @POST("/api/template/getPrivateDetails")
    Call<Template> getPrivateDetails (
            @Field("userID") String userID,
            @Field("templateName") String templateName
    );

    @FormUrlEncoded
    @POST("/api/template/startTemplate")
    Call<ResponseBody> startTemplate (
            @Field("userID") String userID,
            @Field("templateName") String templateName
    );

    @FormUrlEncoded
    @POST("/api/template/stopTemplate")
    Call<ResponseBody> stopTemplate (
            @Field("userID") String userID,
            @Field("templateName") String templateName
    );

    @FormUrlEncoded
    @POST("/api/template/deleteTemplate")
    Call<ResponseBody> deleteTemplate (
            @Field("userID") String userID,
            @Field("templateName") String templateName
    );

    @FormUrlEncoded
    @POST("/api/template/getSelectedTemplate")
    Call<JsonObject> getSelectedTemplate (
            @Field("userID") String userID
    );
}

