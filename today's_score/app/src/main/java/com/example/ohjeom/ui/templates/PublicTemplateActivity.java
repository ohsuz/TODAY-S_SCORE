package com.example.ohjeom.ui.templates;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;
import com.example.ohjeom.adapters.AppCheckAdapter;
import com.example.ohjeom.adapters.LocationAdapter;
import com.example.ohjeom.models.Location;
import com.example.ohjeom.models.Template;
import com.example.ohjeom.models.Templates;
import com.example.ohjeom.models.User;
import com.example.ohjeom.retrofit.RetrofitClient;
import com.example.ohjeom.retrofit.ScoreFunctions;
import com.example.ohjeom.retrofit.TemplateService;
import com.example.ohjeom.services.StartService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PublicTemplateActivity extends AppCompatActivity {
    private int wakeupHour, wakeupMin;
    private int walkHour, walkMin, walkCount;
    private int sleepHour, sleepMin;
    private int startHour, startMin;
    private int stopHour, stopMin;
    private int startMonth, startDay;
    private RelativeLayout seekbarLayout;
    private TextView thumbText;
    private SeekBar countSb;
    private String[] components;
    private Template privateTemplate;
    private Calendar startCal;
    private ArrayList<Location> locationList = new ArrayList<>();
    private String[] appNames;
    private List<ResolveInfo> checkList;
    private LocationAdapter locationAdapter;
    private AppCheckAdapter appCheckAdapter;
    private TimePicker wakeupTimePicker, walkTimePicker, sleepTimePicker, phoneStartTimepicker, phoneStopTimepicker;
    private SharedPreferences user;
    private String userID;
    private Retrofit retrofit = RetrofitClient.getInstance();
    private TemplateService templateService = retrofit.create(TemplateService.class);

    public PublicTemplateActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);

        Intent intent = getIntent();

        TextView templateName = (TextView) findViewById(R.id.temp_name);
        templateName.setText(intent.getStringExtra("name"));

        //레이아웃 출력
        LinearLayout wakeupLayout = (LinearLayout) findViewById(R.id.wakeup_layout);
        LinearLayout walkLayout = (LinearLayout) findViewById(R.id.walk_layout);
        LinearLayout sleepLayout = (LinearLayout) findViewById(R.id.sleep_layout);
        LinearLayout locationLayout = (LinearLayout) findViewById(R.id.location_layout);
        LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.phone_layout);
        LinearLayout payLayout = (LinearLayout) findViewById(R.id.pay_layout);

        //if문으로 검사할것!
        //기상
        components = intent.getStringArrayExtra("components");

        if (components[0].equals("true")) {
            wakeupLayout.setVisibility(View.VISIBLE);

            wakeupHour = intent.getIntExtra("wakeupHour",0);
            wakeupMin = intent.getIntExtra("wakeupMin",0);

            wakeupTimePicker = (TimePicker) findViewById(R.id.wakeup_timepicker);
            wakeupTimePicker.setHour(wakeupHour);
            wakeupTimePicker.setMinute(wakeupMin);
            wakeupTimePicker.setIs24HourView(true);
            wakeupTimePicker.setEnabled(false);
        }

        //수면
        if (components[1].equals("true")) {
            sleepLayout.setVisibility(View.VISIBLE);
            sleepHour = intent.getIntExtra("sleepHour",0);
            sleepMin = intent.getIntExtra("sleepMin",0);

            sleepTimePicker = (TimePicker) findViewById(R.id.sleep_timepicker);
            sleepTimePicker.setHour(sleepHour);
            sleepTimePicker.setMinute(sleepMin);
            sleepTimePicker.setIs24HourView(true);
            sleepTimePicker.setEnabled(false);
        }

        //운동
        if (components[2].equals("true")) {
            walkLayout.setVisibility(View.VISIBLE);
            walkHour = intent.getIntExtra("walkHour",0);
            walkMin = intent.getIntExtra("walkMin",0);
            walkCount = intent.getIntExtra("walkCount",0);;

            walkTimePicker = (TimePicker) findViewById(R.id.walk_timepicker);
            walkTimePicker.setHour(walkHour);
            walkTimePicker.setMinute(walkMin);
            walkTimePicker.setIs24HourView(true);
            walkTimePicker.setEnabled(false);

            seekbarLayout = (RelativeLayout) findViewById(R.id.SeekbarLayout);
            thumbText = (TextView) findViewById(R.id.thumbtext);

            countSb = (SeekBar) findViewById(R.id.countsb);
            countSb.setProgress((walkCount - 5000) / 1000);
            countSb.setEnabled(false);

            final ViewTreeObserver viewTreeObserver = countSb.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        thumbText.setText(Integer.toString(walkCount));
                        int padding = countSb.getPaddingLeft() + countSb.getPaddingRight();
                        int startPos = countSb.getLeft() + countSb.getPaddingLeft();
                        int moveX = (countSb.getWidth() - padding) * countSb.getProgress() / countSb.getMax() + startPos - (seekbarLayout.getWidth() / 2);
                        seekbarLayout.setX(moveX);
                        countSb.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
            }
        }

        //핸드폰 사용
        if (components[3].equals("true")) {
            phoneLayout.setVisibility(View.VISIBLE);
            startHour = intent.getIntExtra("startHour",0);;
            startMin = intent.getIntExtra("startMin",0);;

            phoneStartTimepicker = (TimePicker) findViewById(R.id.phonestart_timepicker);
            phoneStartTimepicker.setHour(startHour);
            phoneStartTimepicker.setMinute(startMin);
            phoneStartTimepicker.setIs24HourView(true);
            phoneStartTimepicker.setEnabled(false);

            stopHour = intent.getIntExtra("stopHour",0);
            stopMin = intent.getIntExtra("stopMin",0);

            phoneStopTimepicker = (TimePicker) findViewById(R.id.phonestop_timepicker);
            phoneStopTimepicker.setHour(stopHour);
            phoneStopTimepicker.setMinute(stopMin);
            phoneStopTimepicker.setIs24HourView(true);
            phoneStopTimepicker.setEnabled(false);

            appNames = intent.getStringArrayExtra("appNames");
            PackageManager pm = getPackageManager();
            List<ResolveInfo> resolveInfos;
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveInfos = pm.queryIntentActivities(mainIntent, 0);
            checkList = new ArrayList<>();
            for (String appName : appNames) {
                for (ResolveInfo resolveInfo : resolveInfos) {
                    if (resolveInfo.activityInfo.packageName.equals(appName))
                        checkList.add(resolveInfo);
                }
            }
            appCheckAdapter = new AppCheckAdapter(pm, checkList);
            RecyclerView appcheckListView = findViewById(R.id.appcheck_listview);
            appcheckListView.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            appcheckListView.setLayoutManager(gridLayoutManager);
            appcheckListView.setAdapter(appCheckAdapter);
        }

        //장소
        if (components[4].equals("true")) {
            locationLayout.setVisibility(View.VISIBLE);

            RecyclerView locationListView = (RecyclerView) findViewById(R.id.location_listview);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            locationListView.setLayoutManager(linearLayoutManager);

            locationList = intent.getParcelableArrayListExtra("locations");
            locationAdapter = new LocationAdapter(locationList);
            locationListView.setAdapter(locationAdapter);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(locationListView.getContext(),
                    linearLayoutManager.getOrientation());
            locationListView.addItemDecoration(dividerItemDecoration);
        }

        //소비
        if (components[5].equals("true")) {
            payLayout.setVisibility(View.VISIBLE);

            TextView moneyText = findViewById(R.id.money_text);
            moneyText.setText(Integer.toString(intent.getIntExtra("money",0)));
        }
    }

    /*
    Start examination using this template
     */
    public void mOnClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PublicTemplateActivity.this);

        View view = LayoutInflater.from(PublicTemplateActivity.this)
                .inflate(R.layout.dialog_template, null, false);
        builder.setView(view);

        startCal = Calendar.getInstance();
        startMonth = startCal.get(Calendar.MONTH);
        startDay = startCal.get(Calendar.DAY_OF_MONTH);

        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        datePicker.init(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                startMonth = month;
                startDay = dayOfMonth;
            }
        });

        final Button registerBtn = (Button) view.findViewById(R.id.register_button);
        final Button cancelBtn = (Button) view.findViewById(R.id.cancel_button);
        final AlertDialog dialog = builder.create();
        final Intent intentHome = new Intent(this, MainActivity.class);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentHome);
                dialog.dismiss();
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}


