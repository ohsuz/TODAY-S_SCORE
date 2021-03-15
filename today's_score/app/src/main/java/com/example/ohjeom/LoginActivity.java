package com.example.ohjeom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText idEditText = findViewById(R.id.id_text);
        EditText passwordEditText = findViewById(R.id.password_text);
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
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
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
