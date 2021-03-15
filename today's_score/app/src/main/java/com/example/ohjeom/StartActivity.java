package com.example.ohjeom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

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
}
