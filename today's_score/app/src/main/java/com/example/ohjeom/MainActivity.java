package com.example.ohjeom;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import static com.example.ohjeom.ui.templates.privateTemplate.PrivateFragment.pAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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