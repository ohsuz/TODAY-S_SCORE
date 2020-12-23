package com.example.ohjeom.ui.templates;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.example.ohjeom.adapters.AppAdapter;
import com.example.ohjeom.adapters.AppCheckAdapter;
import com.example.ohjeom.adapters.LocationAdapter;
import com.example.ohjeom.models.Location;
import com.example.ohjeom.models.Template;
import com.example.ohjeom.models.Templates;
import com.example.ohjeom.services.startService;

import java.util.ArrayList;
import java.util.List;

public class TemplateActivity extends AppCompatActivity {

    private int position;
    private int wakeupHour, wakeupMin;
    private int walkHour, walkMin, walkCount;
    private int sleepHour, sleepMin;
    private int startHour, startMin;
    private int stopHour, stopMin;
    private RelativeLayout seekbarLayout;
    private TextView thumbText;
    private SeekBar countSb;
    private boolean[] components;
    private Template privateTemplate;

    private ArrayList<Template> templates;
    private ArrayList<Location> locationList;
    private List<ResolveInfo> checkList;
    private LocationAdapter locationAdapter;
    private AppCheckAdapter appCheckAdapter;

    private TimePicker wakeupTimePicker, walkTimePicker, sleepTimePicker, phoneStartTimepicker, phoneStopTimepicker;

    public TemplateActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);

        Intent intent = getIntent();
        position = intent.getIntExtra("position",0);

        templates = new ArrayList<>();
        templates = Templates.getTemplates();
        privateTemplate = templates.get(position);

        //
        TextView templateName = (TextView)findViewById(R.id.temp_name);
        templateName.setText(privateTemplate.getTemplateName());

        //레이아웃 출력
        LinearLayout wakeupLayout = (LinearLayout) findViewById(R.id.wakeup_layout);
        LinearLayout walkLayout = (LinearLayout) findViewById(R.id.walk_layout);
        LinearLayout sleepLayout = (LinearLayout) findViewById(R.id.sleep_layout);
        LinearLayout locationLayout = (LinearLayout) findViewById(R.id.location_layout);
        LinearLayout phoneLayout = (LinearLayout) findViewById(R.id.phone_layout);

        //if문으로 검사할것!
        //기상
        components = privateTemplate.getComponents();
        if(components[0]==true)
            wakeupLayout.setVisibility(View.VISIBLE);

        wakeupHour = privateTemplate.getWakeupHour();
        wakeupMin = privateTemplate.getWakeupMin();

        wakeupTimePicker = (TimePicker) findViewById(R.id.wakeup_timepicker);
        wakeupTimePicker.setHour(wakeupHour);
        wakeupTimePicker.setMinute(wakeupMin);
        wakeupTimePicker.setIs24HourView(true);
        wakeupTimePicker.setEnabled(false);

        //수면
        if(components[1]==true)
            sleepLayout.setVisibility(View.VISIBLE);
        sleepHour = privateTemplate.getSleepHour();
        sleepMin = privateTemplate.getSleepMin();

        sleepTimePicker = (TimePicker) findViewById(R.id.sleep_timepicker);
        sleepTimePicker.setHour(sleepHour);
        sleepTimePicker.setMinute(sleepMin);
        sleepTimePicker.setIs24HourView(true);
        sleepTimePicker.setEnabled(false);

        //운동
        if(components[2]==true)
            walkLayout.setVisibility(View.VISIBLE);
        walkHour = privateTemplate.getWalkHour();
        walkMin = privateTemplate.getWalkMin();
        walkCount = privateTemplate.getWalkCount();

        walkTimePicker = (TimePicker) findViewById(R.id.walk_timepicker);
        walkTimePicker.setHour(walkHour);
        walkTimePicker.setMinute(walkMin);
        walkTimePicker.setIs24HourView(true);
        walkTimePicker.setEnabled(false);

        seekbarLayout = (RelativeLayout)findViewById(R.id.SeekbarLayout);
        thumbText =(TextView)findViewById(R.id.thumbtext);

        countSb = (SeekBar) findViewById(R.id.countsb);
        countSb.setProgress((walkCount-5000)/1000);
        countSb.setEnabled(false);

        final ViewTreeObserver viewTreeObserver = countSb.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    thumbText.setText(Integer.toString(walkCount));
                    int padding = countSb.getPaddingLeft() + countSb.getPaddingRight();
                    int startPos = countSb.getLeft() + countSb.getPaddingLeft();
                    int moveX = (countSb.getWidth()-padding) * countSb.getProgress() / countSb.getMax() + startPos - (seekbarLayout.getWidth()/2);
                    seekbarLayout.setX(moveX);
                    countSb.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }


        //핸드폰 사용
        if(components[3]==true)
            phoneLayout.setVisibility(View.VISIBLE);
        startHour = privateTemplate.getStartHour();
        startMin = privateTemplate.getStartMin();

        phoneStartTimepicker = (TimePicker) findViewById(R.id.phonestart_timepicker);
        phoneStartTimepicker.setHour(startHour);
        phoneStartTimepicker.setMinute(startMin);
        phoneStartTimepicker.setIs24HourView(true);
        phoneStartTimepicker.setEnabled(false);

        stopHour = privateTemplate.getStopHour();
        stopMin = privateTemplate.getStopMin();

        phoneStopTimepicker = (TimePicker) findViewById(R.id.phonestop_timepicker);
        phoneStopTimepicker.setHour(stopHour);
        phoneStopTimepicker.setMinute(stopMin);
        phoneStopTimepicker.setIs24HourView(true);
        phoneStopTimepicker.setEnabled(false);

        checkList = privateTemplate.getAppNames();
        PackageManager pm = getPackageManager();
        appCheckAdapter = new AppCheckAdapter(pm,checkList);
        RecyclerView appcheckListView = findViewById(R.id.appcheck_listview);
        appcheckListView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        appcheckListView.setLayoutManager(gridLayoutManager);
        appcheckListView.setAdapter(appCheckAdapter);

        //장소
        if(components[4]==true)
            locationLayout.setVisibility(View.VISIBLE);

        RecyclerView locationListView = (RecyclerView) findViewById(R.id.location_listview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        locationListView.setLayoutManager(linearLayoutManager);
        locationList = new ArrayList<>();
        locationList = privateTemplate.getLocations();

        locationAdapter = new LocationAdapter(locationList);
        locationListView.setAdapter(locationAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(locationListView.getContext(),
                linearLayoutManager.getOrientation());
        locationListView.addItemDecoration(dividerItemDecoration);

    }
    //Setting
    public void mOnClick(View v){

        templates.get(position).setSelected(true);

        Intent intentService = new Intent(this, startService.class);
        intentService.putExtra("template",templates.get(position));
        startService(intentService);

        Intent intentHome = new Intent(this, MainActivity.class);
        intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intentHome);
        finish();
    }
}
