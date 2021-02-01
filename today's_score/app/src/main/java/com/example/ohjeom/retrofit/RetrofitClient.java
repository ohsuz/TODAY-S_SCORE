package com.example.ohjeom.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://52.20.188.81:8080";
    private static Retrofit instance;

    public static Retrofit getInstance() {
        if (instance  == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }

    /*
    public TemplateService getTemplateService() {
        return retrofit.create(TemplateService.class);
    }
     */
}