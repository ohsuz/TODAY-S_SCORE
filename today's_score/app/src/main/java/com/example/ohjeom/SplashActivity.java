package com.example.ohjeom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
