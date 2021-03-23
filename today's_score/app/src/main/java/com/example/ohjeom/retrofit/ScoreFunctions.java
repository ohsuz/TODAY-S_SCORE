package com.example.ohjeom.retrofit;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.ohjeom.models.Score;
import com.example.ohjeom.models.Storage;
import com.example.ohjeom.models.Template;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScoreFunctions {
    private static String userID;
    private static String templateName;
    private static String date;
    private static final Retrofit retrofit = RetrofitClient.getInstance();
    private static final ScoreService scoreService = retrofit.create(ScoreService.class);

    public ScoreFunctions() {
        date = getDate();
    }

    public static String getUserID() {
        return userID;
    }

    public static void setUserID(String userID) {
        ScoreFunctions.userID = userID;
    }

    public static String getTemplateName() {
        return templateName;
    }

    public static void setTemplateName(String templateName) {
        ScoreFunctions.templateName = templateName;
    }

    public static void getScores(String userID, String date) {
        scoreService.getScores(userID, date).enqueue(new Callback<Score>() {
            @Override
            public void onResponse(Call<Score> call, Response<Score> response) {
                if (response.code() == 200) {
                    Storage.setIsScored(true);
                    Score score = response.body();
                    Storage.setScore(score);
                    Storage.getScore().parseInfo();
                    Log.d("@@@@@@@@", String.valueOf(Storage.getScore().getTemplateName()));
                }
                if (response.code() == 404) {
                    Storage.setIsScored(false);
                }
            }
            @Override
            public void onFailure(Call<Score> call, Throwable t) {
                Log.d("ScoreService", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }

    public void addWakeScore(int score, String wakeupTime) {
        JsonObject wakeScore = new JsonObject();
        wakeScore.addProperty("score", score);
        wakeScore.addProperty("wakeupTime", wakeupTime);

        scoreService.addWakeScore(userID, templateName, date, wakeScore).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("@@@@@@", "Score Functions - addWakeScore: " + response.body().toString());
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Score Functions", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }

    public void addSleepScore(int score, long phoneTime, long lightTime) {
        JsonObject sleepScore = new JsonObject();
        sleepScore.addProperty("score", score);
        sleepScore.addProperty("phoneTime", phoneTime);
        sleepScore.addProperty("lightTime", lightTime);

        scoreService.addSleepScore(userID, templateName, date, sleepScore).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("@@@@@@", "Score Functions - addSleepScore: " + response.body().toString());
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Score Functions", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }

    public void addWalkScore(int score, int goal, int real) {
        JsonObject walkScore = new JsonObject();
        walkScore.addProperty("score", score);
        walkScore.addProperty("goal", goal);
        walkScore.addProperty("real", real);

        scoreService.addWalkScore(userID, templateName, date, walkScore).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("@@@@@@", "Score Functions - addWalkScore: " + response.body().toString());
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Score Functions", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }

    public void addPhoneUsageScore(int score, int totalTime, int usageTime) {
        JsonObject phoneUsageScore = new JsonObject();
        phoneUsageScore.addProperty("score", score);
        phoneUsageScore.addProperty("totalTime", totalTime);
        phoneUsageScore.addProperty("usageTime", usageTime);

        scoreService.addPhoneUsageScore(userID, templateName, date, phoneUsageScore).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("@@@@@@", "Score Functions - addPhoneUsageScore: " + response.body().toString());
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Score Functions", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }

    public void addLocationScore(int score, String location, int opt) {
        JsonObject locationScore = new JsonObject();
        locationScore.addProperty("score", score);
        locationScore.addProperty("location", location);

        scoreService.addLocationScore(userID, templateName, date, opt, locationScore).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("@@@@@@", "Score Functions - addLocationScore: " + response.body().toString());
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Score Functions", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }

    public void addPaymentScore(int score, int goal, int real) {
        JsonObject paymentScore = new JsonObject();
        paymentScore.addProperty("score", score);
        paymentScore.addProperty("goal", goal);
        paymentScore.addProperty("real", real);

        scoreService.addPaymentScore(userID, templateName, date, paymentScore).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("@@@@@@", "Score Functions - addPaymentScore: " + response.body().toString());
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Score Functions", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }

    public static String getDate() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(mDate);
        return date;
    }
}
