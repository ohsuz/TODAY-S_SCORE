package com.example.ohjeom.ui.templates.templateMaker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;
import com.example.ohjeom.adapters.AppAdapter;
import com.example.ohjeom.adapters.AppCheckAdapter;
import com.example.ohjeom.adapters.LocationAdapter;
import com.example.ohjeom.etc.PermissionChecker;
import com.example.ohjeom.models.Location;
import com.example.ohjeom.etc.GpsTracker;
import com.example.ohjeom.models.Template;
import com.example.ohjeom.models.Templates;
import com.example.ohjeom.retrofit.RetrofitClient;
import com.example.ohjeom.retrofit.TemplateService;
import com.example.ohjeom.ui.templates.privateTemplate.PrivateFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

public class MakerActivity2 extends AppCompatActivity {
    private static final String TAG = "MakerActivity2";
    private Retrofit retrofit;
    private TemplateService templateService;

    private Template privateTemplate;
    private String templateName;
    private ArrayList<String> selectedOptions;
    private String[] components = {"false", "false", "false", "false", "false", "false"};

    private RecyclerView.LayoutManager lLayoutManager;
    private RecyclerView.LayoutManager gLayoutManager;

    private AppAdapter appAdapter;
    private LocationAdapter locationAdapter;
    private AppCheckAdapter appCheckAdapter;

    private ArrayList<Location> locationList;
    private String[] appNames;

    private int wakeupHour, wakeupMin;
    private int walkHour, walkMin, walkCount;
    private int sleepHour, sleepMin;
    private int startHour, startMin;
    private int stopHour, stopMin;
    private String locationName;
    private double locationLat, locationLng;
    private int locationHour, locationMin;
    private int money;

    private Calendar currentTime;
    private TimePicker wakeupTimePicker, walkTimePicker, sleepTimePicker, phoneStartTimePicker, phoneStopTimePicker, locationTimePicker;
    private RelativeLayout seekbarLayout;
    private TextView thumbText;
    private SeekBar countSb;
    private List<ResolveInfo> appInfos;
    private EditText moneyText;

    // 위치 설정
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templates_maker_2);
        retrofit = RetrofitClient.getInstance();
        templateService = retrofit.create(TemplateService.class);

        TextView template = (TextView)findViewById(R.id.temp_name);
        //레이아웃 출력
        LinearLayout wakeupLayout = (LinearLayout) findViewById(R.id.wakeup_layout);
        LinearLayout stepLayout = (LinearLayout) findViewById(R.id.step_layout);
        LinearLayout sleepLayout = (LinearLayout) findViewById(R.id.sleep_layout);
        LinearLayout locationLayout = (LinearLayout) findViewById(R.id.location_layout);
        LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.phone_layout);
        LinearLayout payLayout = findViewById(R.id.pay_layout);

        Intent intent = getIntent();
        templateName = intent.getStringExtra("templateName");
        selectedOptions = (ArrayList<String>) intent.getSerializableExtra("selectedOptions");

        template.setText(templateName);

        privateTemplate = new Template();

        int i = 0;
        for (String option : selectedOptions) {
            option = selectedOptions.get(i++).replace(" ","");
            switch (option) {
                case "기상검사":
                    wakeupLayout.setVisibility(View.VISIBLE);
                    components[0] = "true";
                    break;
                case "수면검사":
                    sleepLayout.setVisibility(View.VISIBLE);
                    components[1] = "true";
                    break;
                case "걸음수검사":
                    stepLayout.setVisibility(View.VISIBLE);
                    components[2] = "true";
                    break;
                case "핸드폰사용량검사":
                    phoneLayout.setVisibility(View.VISIBLE);
                    components[3] = "true";
                    break;
                case "장소도착검사":
                    locationLayout.setVisibility(View.VISIBLE);
                    components[4] = "true";
                    if (!checkLocationServicesStatus()) {
                        showDialogForLocationServiceSetting();
                    }else {
                        PermissionChecker.checkRunTimePermission(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                    break;
                case "소비검사":
                    payLayout.setVisibility(View.VISIBLE);
                    components[5] = "true";
            }
        }

        currentTime = Calendar.getInstance();

        /*
        기상 검사
         */
        wakeupHour = currentTime.get(Calendar.HOUR_OF_DAY);
        wakeupMin = currentTime.get(Calendar.MINUTE);

        wakeupTimePicker = (TimePicker) findViewById(R.id.wakeup_timepicker);
        wakeupTimePicker.setIs24HourView(true);
        wakeupTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                wakeupHour = hourOfDay;
                wakeupMin = minute;
            }
        });

        /*
        운동 검사
         */
        walkHour = currentTime.get(Calendar.HOUR_OF_DAY);
        walkMin = currentTime.get(Calendar.MINUTE);

        walkTimePicker = (TimePicker) findViewById(R.id.walk_timepicker);
        walkTimePicker.setIs24HourView(true);
        walkTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                walkHour = hourOfDay;
                walkMin = minute;
            }
        });

        seekbarLayout = (RelativeLayout)findViewById(R.id.SeekbarLayout);
        thumbText =(TextView)findViewById(R.id.thumbtext);
        countSb = (SeekBar) findViewById(R.id.countsb);

        final ViewTreeObserver viewTreeObserver = countSb.getViewTreeObserver();

        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    walkCount = countSb.getProgress() *1000 +5000;
                    thumbText.setText(Integer.toString(walkCount));
                    int padding = countSb.getPaddingLeft() + countSb.getPaddingRight();
                    int startPos = countSb.getLeft() + countSb.getPaddingLeft();
                    int moveX = (countSb.getWidth()-padding) * countSb.getProgress() / countSb.getMax() + startPos - (seekbarLayout.getWidth()/2);
                    seekbarLayout.setX(moveX);
                    countSb.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }

        countSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                walkCount = progress *1000 +5000;
                thumbText.setText(Integer.toString(walkCount));
                int padding = countSb.getPaddingLeft() + countSb.getPaddingRight();
                int startPos = countSb.getLeft() + countSb.getPaddingLeft();
                int moveX = (countSb.getWidth()-padding) * countSb.getProgress() / countSb.getMax() + startPos - (seekbarLayout.getWidth()/2);
                seekbarLayout.setX(moveX);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        /*
        수면 검사
         */
        sleepHour = currentTime.get(Calendar.HOUR_OF_DAY);
        sleepMin = currentTime.get(Calendar.MINUTE);

        sleepTimePicker = (TimePicker) findViewById(R.id.sleep_timepicker);
        sleepTimePicker.setIs24HourView(true);
        sleepTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                sleepHour = hourOfDay;
                sleepMin = minute;
            }
        });

        /*
        장소 검사
         */
        RecyclerView locationListView = (RecyclerView) findViewById(R.id.location_listview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        locationListView.setLayoutManager(linearLayoutManager);
        locationList = new ArrayList<>();

        locationAdapter = new LocationAdapter(locationList);
        locationListView.setAdapter(locationAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(locationListView.getContext(),
                linearLayoutManager.getOrientation());
        locationListView.addItemDecoration(dividerItemDecoration);

        Button location_btn = (Button) findViewById(R.id.location_btn);
        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MakerActivity2.this);

                View view = LayoutInflater.from(MakerActivity2.this)
                        .inflate(R.layout.dialog_location, null, false);
                builder.setView(view);

                final Button button = (Button) view.findViewById(R.id.button);

                locationHour = currentTime.get(Calendar.HOUR_OF_DAY);
                locationMin = currentTime.get(Calendar.MINUTE);
                locationTimePicker = (TimePicker) view.findViewById(R.id.location_timepicker);
                locationTimePicker.setIs24HourView(true);
                locationTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                        locationHour = hourOfDay;
                        locationMin = minute;
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Places.initialize(getApplicationContext(), getString(R.string.api_key));
                //PlacesClient placesClient = Places.createClient(this);
                AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)).setCountry("KR");
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onPlaceSelected(@NotNull Place place) {
                        // TODO: Get info about the selected place.
                        locationName = place.getName();
                        locationLng = Double.parseDouble(String.format("%.4f", place.getLatLng().longitude));
                        locationLat = Double.parseDouble(String.format("%.4f", place.getLatLng().latitude));
                        Log.i(TAG, "목표 위치: " + locationName + " 위도: " + locationLng + " 경도: " + locationLat);
                    }

                    @Override
                    public void onError(@NotNull Status status) {
                        // TODO: Handle the error.
                        Log.i(TAG, "An error occurred: " + status);
                    }
                });

                if (!checkLocationServicesStatus()) {
                    showDialogForLocationServiceSetting();
                }else {
                    PermissionChecker.checkRunTimePermission(MakerActivity2.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                }

                button.setText("추가하기");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Location location = new Location(locationName, locationLat, locationLng, locationHour, locationMin);

                        locationList.add(location);
                        locationAdapter.notifyDataSetChanged();

                        dialog.dismiss();

                        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();

                        ft2.remove(getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment));
                        ft2.commit();
                    }
                });
                dialog.show();
            }
        });

        /*
        핸드폰 사용량 검사
         */
        startHour = currentTime.get(Calendar.HOUR_OF_DAY);
        startMin = currentTime.get(Calendar.MINUTE);

        phoneStartTimePicker = (TimePicker) findViewById(R.id.phone_timepicker);
        phoneStartTimePicker.setIs24HourView(true);
        phoneStartTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                startHour = hourOfDay;
                startMin = minute;
            }
        });

        stopHour = currentTime.get(Calendar.HOUR_OF_DAY);
        stopMin = currentTime.get(Calendar.MINUTE);

        phoneStopTimePicker = (TimePicker) findViewById(R.id.phone_endtimepicker);
        phoneStopTimePicker.setIs24HourView(true);
        phoneStopTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                stopHour = hourOfDay;
                stopMin = minute;
            }
        });

        Button appButton = (Button) findViewById(R.id.appButton);
        appButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MakerActivity2.this);
                View view = LayoutInflater.from(MakerActivity2.this)
                        .inflate(R.layout.dialog_app, null, false);
                builder.setView(view);

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

                final Button button = (Button) view.findViewById(R.id.button);
                final PackageManager pm = getPackageManager();
                Intent applistIntent = new Intent(Intent.ACTION_MAIN);
                applistIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> appList = pm.queryIntentActivities(applistIntent, 0);

                appAdapter = new AppAdapter(pm, appList);
                RecyclerView appListView = view.findViewById(R.id.app_listview);
                appListView.setHasFixedSize(true);
                lLayoutManager = new LinearLayoutManager(MakerActivity2.this);
                appListView.setLayoutManager(lLayoutManager);
                appListView.setAdapter(appAdapter);

                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        appInfos = ((AppAdapter)appAdapter).getChecklist();
                        PackageManager pm2 = getPackageManager();
                        appCheckAdapter = new AppCheckAdapter(pm2, appInfos);
                        RecyclerView appcheckListView = findViewById(R.id.appcheck_listview);
                        appcheckListView.setHasFixedSize(true);
                        gLayoutManager = new GridLayoutManager(MakerActivity2.this,2);
                        appcheckListView.setLayoutManager(gLayoutManager);
                        appcheckListView.setAdapter(appCheckAdapter);
                        dialog.dismiss();

                        appNames = new String[appInfos.size()];
                        for (int i = 0; i < appInfos.size(); i++) {
                            String appName = appInfos.get(i).activityInfo.packageName;
                            appNames[i] = appName;
                            Log.d("앱 이름", appName);
                        }
                    }
                });
                dialog.show();
            }

        });

        PackageManager pm2 = getPackageManager();
        appCheckAdapter = new AppCheckAdapter(pm2, appInfos);
        RecyclerView appcheckListView = findViewById(R.id.appcheck_listview);
        appcheckListView.setHasFixedSize(true);
        lLayoutManager = new LinearLayoutManager(MakerActivity2.this);
        appcheckListView.setLayoutManager(lLayoutManager);
        appcheckListView.setAdapter(appCheckAdapter);

        /*
        소비 검사
         */
        moneyText = findViewById(R.id.money_text);
        money = Integer.parseInt(moneyText.getText().toString());
    }

    public void mOnClick(View v) {
        money = Integer.parseInt(moneyText.getText().toString());

        JsonObject body = getBody();
        templateService.registerTemplate(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 404) { // 템플릿 등록 실패
                    try {
                        Toast.makeText(MakerActivity2.this, "템플릿 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        Log.d("TemplateService", "res:" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else { // 템플릿 등록 성공
                    try {
                        Log.d("TemplateService", "res:" + response.body().string());
                        updateTemplateList();
                        Intent intentHome = new Intent(MakerActivity2.this, MainActivity.class);
                        intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intentHome);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("TemplateService", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }

    public void updateTemplateList() {
        SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
        String userID = user.getString("id", "aaa");

        templateService.getPrivateNames(userID).enqueue(new Callback<Templates>() {
            @Override
            public void onResponse(Call<Templates> call, Response<Templates> response) {
                if (response.code() == 404) {
                    try {
                        Log.d("TemplateService", "@@@@@@@ res:" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
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
    }

    public JsonObject getBody() {
        JsonObject body = new JsonObject();
        body.addProperty("userID", "aaa");
        body.addProperty("templateName", templateName);
        body.addProperty("isSelected", false);
        body.addProperty("components", Arrays.toString(components));

        // 순서: 기상 수면 걸음수 핸드폰사용량 장소도착 소비
        if (components[0].equals("true")) {
            JsonObject  wakeObj = new JsonObject ();
            wakeObj.addProperty("h", wakeupHour);
            wakeObj.addProperty("m", wakeupMin);
            body.addProperty("wake", wakeObj.toString());
        }
        if (components[1].equals("true")) {
            JsonObject  sleepObj = new JsonObject ();
            sleepObj.addProperty("h", sleepHour);
            sleepObj.addProperty("m", sleepMin);
            body.addProperty("sleep", sleepObj.toString());
        }
        if (components[2].equals("true")) {
            JsonObject walkObj = new JsonObject();
            walkObj.addProperty("goal", walkCount);
            walkObj.addProperty("h", walkHour);
            walkObj.addProperty("m", walkMin);
            body.addProperty("walk", walkObj.toString());
        }
        if (components[3].equals("true")) {
            JsonObject appObj= new JsonObject();
            appObj.addProperty("appNames", Arrays.toString(appNames));
            appObj.addProperty("startH", startHour);
            appObj.addProperty("startM", startMin);
            appObj.addProperty("stopH", stopHour);
            appObj.addProperty("stopM", stopMin);
            body.addProperty("app", String.valueOf(appObj));
        }
        if (components[4].equals("true")) {
            JsonArray locationArr = new JsonArray();
            for (Location location: locationList) {
                JsonObject locationObj = new JsonObject();
                locationObj.addProperty("locationName", location.getName());
                locationObj.addProperty("lat", location.getLat());
                locationObj.addProperty("lng", location.getLng());
                locationObj.addProperty("h", location.getLocationHour());
                locationObj.addProperty("m", location.getLocationMin());
                locationArr.add(locationObj);
            }
            body.addProperty("locations", locationArr.toString());
        }
        if (components[5].equals("true")) {
            body.addProperty("money", money);
        }
        return body;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_ENABLE_REQUEST_CODE) {
            if (checkLocationServicesStatus()) {
                if (checkLocationServicesStatus()) {
                    Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                    PermissionChecker.checkRunTimePermission(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                }
            }
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
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