package com.example.ohjeom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.ohjeom.models.Storage;
import com.example.ohjeom.retrofit.RetrofitClient;
import com.example.ohjeom.retrofit.ScoreFunctions;
import com.example.ohjeom.retrofit.TemplateService;
import com.example.ohjeom.ui.statistics.StatisticsFragment;
import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String userID = getSharedPreferences("user", MODE_PRIVATE).getString("id", "aaa");
        TextView one = findViewById(R.id.one);
        TextView two = findViewById(R.id.two);
        TextView three = findViewById(R.id.three);
        TextView four = findViewById(R.id.four);
        TextView five = findViewById(R.id.five);

        /*
        미리 userID에 해당하는 템플릿 설정 여부와 오늘의 점수를 가져옴
         */
        Retrofit retrofit = RetrofitClient.getInstance();
        TemplateService templateService = retrofit.create(TemplateService.class);

        ScoreFunctions.getScores(userID, ScoreFunctions.getDate()); // home fragment 초기화
        StatisticsFragment.getScores(userID, ScoreFunctions.getDate()); // calendar details 초기화
        templateService.getSelectedTemplate(userID).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 404) {
                    Storage.setIsSelected(false);
                } else {
                    Storage.setIsSelected(true);
                    String result = response.body().get("components").getAsString();
                    Log.d("@@@@@@@Test isSelected", result);
                    String[] components = result.substring(1, result.length()-1).split(","); // 기상 수면 걸음수 핸드폰사용량 장소도착

                    for (int i=0; i < components.length; i++) {
                        components[i] = components[i].trim();
                    }

                    Storage.setComponents(components);
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("TemplateService", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });

        Handler handler1 = new Handler(){
            public void handleMessage(Message msg){
                one.setVisibility(View.VISIBLE);
            }
        };

        Handler handler2 = new Handler(){
            public void handleMessage(Message msg){
                two.setVisibility(View.VISIBLE);
            }
        };

        Handler handler3 = new Handler(){
            public void handleMessage(Message msg){
                three.setVisibility(View.VISIBLE);
            }
        };

        Handler handler4 = new Handler(){
            public void handleMessage(Message msg){
                four.setVisibility(View.VISIBLE);
            }
        };

        Handler handler5 = new Handler(){
            public void handleMessage(Message msg){
                five.setVisibility(View.VISIBLE);
            }
        };

        TimerTask tt1 = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler1.obtainMessage();
                handler1.sendMessage(msg);
            }
        };

        TimerTask tt2 = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler2.obtainMessage();
                handler2.sendMessage(msg);
            }
        };

        TimerTask tt3 = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler3.obtainMessage();
                handler3.sendMessage(msg);
            }
        };

        TimerTask tt4 = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler4.obtainMessage();
                handler4.sendMessage(msg);
            }
        };

        TimerTask tt5 = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler5.obtainMessage();
                handler5.sendMessage(msg);
            }
        };

        Timer timer = new Timer();
        timer.schedule(tt1, 500);
        timer.schedule(tt2, 1000);
        timer.schedule(tt3, 1500);
        timer.schedule(tt4, 2000);
        timer.schedule(tt5, 2500);

        Handler handler = new Handler();
        handler.postDelayed(new SplashHandler(), 3000);
    }

    private class SplashHandler implements Runnable{
        @Override
        public void run() {
            Intent intent = new Intent(SplashActivity.this,StartActivity.class);
            startActivity(intent);

            finish();
        }
    }

    @Override
    public void onBackPressed() {
        // 뒤로 가기 못하게
    }
}
