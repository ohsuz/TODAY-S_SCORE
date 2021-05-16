package com.example.ohjeom;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        /*
        // 가장 먼저 permission 체크
        if(!checkUsageStatsPermissions()){
            Intent usageAccessIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            usageAccessIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(usageAccessIntent);
            if(checkUsageStatsPermissions()){
            }
            else{
                Toast.makeText(getApplicationContext(),"please give access", Toast.LENGTH_SHORT).show();
            }
        }*/

        SharedPreferences user = getSharedPreferences("user",MODE_PRIVATE);
        if(user.getBoolean("Login",false)){
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        LinearLayout buttonLayout = findViewById(R.id.layout_button);
        LinearLayout loginLayout = findViewById(R.id.layout_login);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
            }
        });

        EditText idEditText = findViewById(R.id.id_text);
        EditText passwordEditText = findViewById(R.id.password_text);
        Button loginButton2 = findViewById(R.id.login_button2);
        loginButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String id = idEditText.getText().toString();
                String pwd = passwordEditText.getText().toString();
                //원래는 id와 pwd를 서버로 보내서 로그인 성공 응답을 받아와야 함.
                if(id.equals("aaa") && pwd.equals("1234")){ //로그인 성공했다면
                    SharedPreferences user = getSharedPreferences("user",MODE_PRIVATE);
                    SharedPreferences.Editor editor = user.edit();
                    editor.putString("id",id);
                    editor.putBoolean("Login",true);
                    editor.commit();
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean checkUsageStatsPermissions(){
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);

            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}