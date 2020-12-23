package com.example.ohjeom.ui.templates.templateMaker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.ohjeom.services.walkService;
import com.example.ohjeom.services.sleepService;
import com.example.ohjeom.services.wakeupService;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MakerActivity2 extends AppCompatActivity {
    private ArrayList<String> selectedOptions;
    private String templateName;

    private RecyclerView.LayoutManager lLayoutManager;
    private RecyclerView.LayoutManager gLayoutManager;
    private AppAdapter appAdapter;
    private ArrayList<Location> locationList;
    private LocationAdapter locationAdapter;
    private AppCheckAdapter appCheckAdapter;
    private Template privateTemplate;
    private boolean[] components = {false,false,false,false,false};

    private int wakeupHour, wakeupMin;
    private int walkHour, walkMin, walkCount;
    private int sleepHour, sleepMin;
    private int startHour, startMin;
    private int stopHour, stopMin;
    private int locationHour, locationMin;

    private Calendar currentTime;
    private TimePicker wakeupTimePicker, walkTimePicker, sleepTimePicker, phoneStartTimePicker, phoneStopTimePicker, locationTimePicker;
    private Timer wakeupTimer, walkTimer, sleepTimer;
    private RelativeLayout seekbarLayout;
    private TextView thumbText;
    private SeekBar countSb;
    private List<ResolveInfo> appNames;

    // 위치 설정
    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final String TAG = "aaaaaaaaaaaaaaa";
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templates_maker_2);

        TextView template = (TextView)findViewById(R.id.temp_name);

        //레이아웃 출력
        LinearLayout wakeup_layout = (LinearLayout) findViewById(R.id.wakeup_layout);
        LinearLayout step_layout = (LinearLayout) findViewById(R.id.step_layout);
        LinearLayout sleep_layout = (LinearLayout) findViewById(R.id.sleep_layout);
        LinearLayout location_layout = (LinearLayout) findViewById(R.id.location_layout);
        LinearLayout phone_layout = (LinearLayout) findViewById(R.id.phone_layout);

        Intent intent = getIntent();
        templateName = intent.getStringExtra("templateName");
        selectedOptions = (ArrayList<String>) intent.getSerializableExtra("selectedOptions");

        template.setText(templateName);

        privateTemplate = new Template();

        int i = 0;
        for (String s : selectedOptions) {
            s = selectedOptions.get(i++).replace(" ","");
            switch (s) {
                case "기상검사":
                    wakeup_layout.setVisibility(View.VISIBLE);
                    components[0] = true;
                    break;
                case "걸음수검사":
                    step_layout.setVisibility(View.VISIBLE);
                    components[2] = true;
                    break;
                case "수면검사":
                    sleep_layout.setVisibility(View.VISIBLE);
                    components[1] = true;
                    break;
                case "장소도착검사":
                    location_layout.setVisibility(View.VISIBLE);
                    components[4] = true;
                    if (!checkLocationServicesStatus()) {
                        showDialogForLocationServiceSetting();
                    }else {
                        PermissionChecker.checkRunTimePermission(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                    break;
                case "핸드폰사용량검사":
                    phone_layout.setVisibility(View.VISIBLE);
                    components[3] = true;
                    break;
            }
        }

        privateTemplate.setComponents(components);

        //현재 시간
        currentTime = Calendar.getInstance();

        //기상
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

        //운동
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

        //수면
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

        //장소
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

                wakeupHour = currentTime.get(Calendar.HOUR_OF_DAY);
                wakeupMin = currentTime.get(Calendar.MINUTE);

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
                final Location dest = new Location();

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
                        dest.setName(place.getName());
                        dest.setLat(Double.parseDouble(String.format("%.4f", place.getLatLng().latitude)));
                        dest.setLng(Double.parseDouble(String.format("%.4f", place.getLatLng().longitude)));
                        Log.i(TAG, "목표 위치: " + dest.getName() + " 위도: " + dest.getLat() + " 경도: " + dest.getLng());
                    }

                    @Override
                    public void onError(@NotNull Status status) {
                        // TODO: Handle the error.
                        Log.i(TAG, "An error occurred: " + status);
                    }
                });

                button.setText("추가하기");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dest.setLocationHour(locationHour);
                        dest.setLocationMin(locationMin);

                        locationList.add(dest);
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

        //공부
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

                final Button button = (Button) view.findViewById(R.id.button);

                //Data
                final PackageManager pm = getPackageManager();

                Intent applistIntent = new Intent(Intent.ACTION_MAIN);
                applistIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> appList = pm.queryIntentActivities(applistIntent, 0);

                /*
                for (ResolveInfo info : app_list) {
                    String appActivity = info.activityInfo.name;
                    String appPackageName = info.activityInfo.packageName;
                    String appName = info.loadLabel(pm).toString();
                    Drawable drawable = info.activityInfo.loadIcon(pm);
                    Log.d("TEST", "appName : " + appName + ", appActivity : " + appActivity + ", appPackageName : " + appPackageName);
                }
                */

                //View
                appAdapter = new AppAdapter(pm, appList);
                RecyclerView appListView = view.findViewById(R.id.app_listview);
                appListView.setHasFixedSize(true);
                lLayoutManager = new LinearLayoutManager(MakerActivity2.this);
                appListView.setLayoutManager(lLayoutManager);
                appListView.setAdapter(appAdapter);

                final AlertDialog dialog = builder.create();

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        appNames = ((AppAdapter)appAdapter).getChecklist();
                        PackageManager pm2 = getPackageManager();
                        appCheckAdapter = new AppCheckAdapter(pm2, appNames);
                        RecyclerView appcheckListView = findViewById(R.id.appcheck_listview);
                        appcheckListView.setHasFixedSize(true);
                        gLayoutManager = new GridLayoutManager(MakerActivity2.this,2);
                        appcheckListView.setLayoutManager(gLayoutManager);
                        appcheckListView.setAdapter(appCheckAdapter);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

        });

        PackageManager pm2 = getPackageManager();
        appCheckAdapter = new AppCheckAdapter(pm2, appNames);
        RecyclerView appcheckListView = findViewById(R.id.appcheck_listview);
        appcheckListView.setHasFixedSize(true);
        lLayoutManager = new LinearLayoutManager(MakerActivity2.this);
        appcheckListView.setLayoutManager(lLayoutManager);
        appcheckListView.setAdapter(appCheckAdapter);

    }

    public void mOnClick(View v) {

        privateTemplate.setTemplateName(templateName);
        privateTemplate.setWalkHour(walkHour);
        privateTemplate.setWalkMin(walkMin);
        privateTemplate.setWakeupHour(wakeupHour);
        privateTemplate.setWakeupMin(wakeupMin);
        privateTemplate.setSleepHour(sleepHour);
        privateTemplate.setSleepMin(sleepMin);
        privateTemplate.setLocations(locationList);
        privateTemplate.setAppNames(appNames);
        privateTemplate.setStartHour(startHour);
        privateTemplate.setStartMin(startMin);
        privateTemplate.setStopHour(stopHour);
        privateTemplate.setStopMin(stopMin);
        privateTemplate.setWalkCount(walkCount);

        Templates.getTemplates().add(privateTemplate);

        Intent intentHome = new Intent(this, MainActivity.class);
        intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intentHome);
        finish();

        //보낼 데이터 : 템플릿 이름, 기상 시간 , 운동 시간&걸음 수, 핸드폰 앱 list&시작 시간&종료 시간, 장소 list(이름, 위도, 경도)&시간, 수면 시간

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
}