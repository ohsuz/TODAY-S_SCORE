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
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TemplateService {
    @POST("/register")
    Call<ResponseBody> registerTemplate (
            @Body JsonObject body
    );

    @POST("/getPrivateName")
    Call<Templates> getPrivateName (
            @Body JsonObject body // @Query("보낼거") String 어쩌구 아마 사용자 id를 보내야할듯하다
    );

    @POST("/getPrivateDetails")
    Call<Template> getPrivateDetails (
            @Body JsonObject body // 사용자 id, 템플릿 명
    );
    /*
    @POST("/api/template/register")
    Call<ResponseBody> registerTemplate (
        @Body JsonObject body
    );

    @POST("/api/template/getPublicName")
    Call<ResponseBody> getPublicName (
            @Body JsonObject body // @Query("보낼거") 아마 보낼거없을듯??
    );

    @POST("/api/template/getPublicDetails")
    Call<Template> getPublicDetails (
            @Body JsonObject body // @Query("보낼거") String 어쩌구
    );

    @POST("/api/template/getName")
    Call<ResponseBody> getPrivateName (
            @Body JsonObject body // @Query("보낼거") String 어쩌구 아마 사용자 id를 보내야할듯하다
    );

    @POST("/api/template/getName")
    Call<Template> getPrivateDetails (
            @Body JsonObject body // @Query("보낼거") String 어쩌구 아마 템플릿 이름...?
    );

     */
}

