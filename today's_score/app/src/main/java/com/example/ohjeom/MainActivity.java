package com.example.ohjeom;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.ohjeom.models.Template;
import com.example.ohjeom.models.Templates;
import com.example.ohjeom.retrofit.RetrofitClient;
import com.example.ohjeom.retrofit.ScoreService;
import com.example.ohjeom.retrofit.TemplateService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import static com.example.ohjeom.ui.templates.privateTemplate.PrivateFragment.pAdapter;

public class MainActivity extends AppCompatActivity {
    String[] templates = new String[]{};
    private Retrofit retrofit = RetrofitClient.getInstance();
    private TemplateService templateService = retrofit.create(TemplateService.class);
    private ScoreService scoreService = retrofit.create(ScoreService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
        String userID = user.getString("id", "aaa");

        templateService.getPrivateNames(userID).enqueue(new Callback<Templates>() {
            @Override
            public void onResponse(Call<Templates> call, Response<Templates> response) {
                if (response.code() == 404) {
                    try {
                        Toast.makeText(MainActivity.this, "개인 템플릿 이름 호출에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        Log.d("TemplateService", "@@@@@@@@@ res:" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "개인 템플릿 이름 호출에 성공했습니다.", Toast.LENGTH_SHORT).show();
                    Templates templates = response.body();
                    Templates.templateNames = templates.getTemplateNamesResult();
                    Templates.isSelectedArr = templates.getIsSelectedArrResult();
                }
            }

            @Override
            public void onFailure(Call<Templates> call, Throwable t) {
                Log.d("TemplateService", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });

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
        }

        checkSMSPermissions();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_statistics, R.id.navigation_templates,
                R.id.navigation_home,
                R.id.navigation_rankings, R.id.navigation_groups)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager() .
                findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        // 액션바 추가하고 싶으면
        //  theme: AppTheme으로 변경하고 아래 주석 제거
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
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

    private void checkSMSPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"권한 있음",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"권한 없음",Toast.LENGTH_LONG).show();

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)){
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                String[] permissions = {
                        Manifest.permission.RECEIVE_SMS
                };

                //권한이 할당되지 않았으면 해당 권한을 요청
                ActivityCompat.requestPermissions(this,permissions,1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == 1){
            for(int i=0;i<permissions.length;i++)
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,permissions[i]+"권한이 승인됨.",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this,permissions[i]+"권한이 승인되지 않음.",Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
        }
        super.onNewIntent(intent);
    }

    public void refresh(){
        pAdapter.notifyDataSetChanged();
    }
}