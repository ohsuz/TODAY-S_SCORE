package com.example.ohjeom.models;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.widget.EditText;

import com.example.ohjeom.services.phoneService;
import com.example.ohjeom.services.sleepService;
import com.example.ohjeom.services.wakeupService;
import com.example.ohjeom.services.walkService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.ACTIVITY_SERVICE;

public class Template implements Serializable {

    private Context context;
    private String templateName;
    private boolean isSelected = false;
    private boolean[] components; // 기상, 수면, 걸음수, 핸드폰, 장소
    public static String[] componentNames = {"기상 검사", "수면 검사", "걸음수 검사", "핸드폰 사용량 검사", "장소 도착 검사"};
    private int walkHour, walkMin, walkCount;
    private int wakeupHour, wakeupMin;
    private int sleepHour, sleepMin;
    private ArrayList<Location> locations;
    private List<ResolveInfo> appNames;
    private int startHour, startMin, stopHour, stopMin;

    public boolean[] getComponents() {
        return components;
    }

    public void setComponents(boolean[] components) {
        this.components = components;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getWalkHour() {
        return walkHour;
    }

    public void setWalkHour(int walkHour) {
        this.walkHour = walkHour;
    }

    public int getWalkMin() {
        return walkMin;
    }

    public void setWalkMin(int walkMin) {
        this.walkMin = walkMin;
    }

    public int getWalkCount() {
        return walkCount;
    }

    public void setWalkCount(int walkCount) {
        this.walkCount = walkCount;
    }

    public int getWakeupHour() {
        return wakeupHour;
    }

    public void setWakeupHour(int wakeupHour) {
        this.wakeupHour = wakeupHour;
    }

    public int getWakeupMin() {
        return wakeupMin;
    }

    public void setWakeupMin(int wakeupMin) {
        this.wakeupMin = wakeupMin;
    }

    public int getSleepHour() {
        return sleepHour;
    }

    public void setSleepHour(int sleepHour) {
        this.sleepHour = sleepHour;
    }

    public int getSleepMin() {
        return sleepMin;
    }

    public void setSleepMin(int sleepMin) {
        this.sleepMin = sleepMin;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public List<ResolveInfo> getAppNames() {
        return appNames;
    }

    public void setAppNames(List<ResolveInfo> appNames) {
        this.appNames = appNames;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getStopHour() {
        return stopHour;
    }

    public void setStopHour(int stopHour) {
        this.stopHour = stopHour;
    }

    public int getStopMin() {
        return stopMin;
    }

    public void setStopMin(int stopMin) {
        this.stopMin = stopMin;
    }

    public void startExamination(Context mContext) {
        context = mContext;
        //기상 검사 시작
        if(components[0]) {
            Calendar wakeupCal = Calendar.getInstance();
            wakeupCal.set(Calendar.HOUR_OF_DAY, wakeupHour);
            wakeupCal.set(Calendar.MINUTE, wakeupMin);
            wakeupCal.set(Calendar.SECOND, 0);
            long wakeupTime = wakeupCal.getTimeInMillis();

            Intent wakeupIntent = new Intent(context,wakeupService.class);
            wakeupIntent.putExtra("wakeupTime", wakeupTime);
            PendingIntent wakeupSender = PendingIntent.getService(context, 0, wakeupIntent, 0);

            AlarmManager wakeupAM = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            wakeupAM.set(AlarmManager.RTC_WAKEUP, wakeupTime, wakeupSender);

            //타이머 설정
            long wakeupStoptime = wakeupTime + (60 * 60 * 1000);
            Date wakeupStop = new Date(wakeupStoptime);

            Timer wakeupTimer = new Timer();
            wakeupTimer.schedule(new WakeupTimer(), wakeupStop);
        }

        //수면 검사 시작
        if(components[1]){
            Calendar sleepCal = Calendar.getInstance();
            sleepCal.set(Calendar.HOUR_OF_DAY, sleepHour);
            sleepCal.set(Calendar.MINUTE, sleepMin);
            sleepCal.set(Calendar.SECOND, 0);

            Calendar wakeupCal = Calendar.getInstance();
            wakeupCal.set(Calendar.HOUR_OF_DAY, wakeupHour);
            wakeupCal.set(Calendar.MINUTE, wakeupMin);
            wakeupCal.set(Calendar.SECOND, 0);
            long wakeupTime = wakeupCal.getTimeInMillis();

            Intent sleepIntent = new Intent(context, sleepService.class);
            PendingIntent sleepSender = PendingIntent.getService(context, 0, sleepIntent, 0);

            AlarmManager sleepAM = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            sleepAM.set(AlarmManager.RTC_WAKEUP, sleepCal.getTimeInMillis(), sleepSender);

            //타이머 설정
            if (sleepCal.getTimeInMillis() - wakeupTime >= 0) {
                wakeupCal.add(Calendar.DATE, 1);
            }

            long sleepStopTime = wakeupCal.getTimeInMillis();

            Date sleepStop = new Date(sleepStopTime);

            Timer sleepTimer = new Timer();
            sleepTimer.schedule(new SleepTimer(), sleepStop);
        }

        //걸음수 검사 시작
        if(components[2]){
            Calendar walkCal = Calendar.getInstance();
            walkCal.set(Calendar.HOUR_OF_DAY, walkHour);
            walkCal.set(Calendar.MINUTE, walkMin);
            walkCal.set(Calendar.SECOND, 0);

            Calendar walkstartCal = Calendar.getInstance();
            walkstartCal.set(Calendar.HOUR_OF_DAY, wakeupHour);
            walkstartCal.set(Calendar.MINUTE, wakeupMin);
            walkstartCal.set(Calendar.SECOND, 0);

            Intent walkIntent = new Intent(context, walkService.class);
            walkIntent.putExtra("walkCount", walkCount);
            PendingIntent walk_sender = PendingIntent.getService(context, 0, walkIntent, 0);

            AlarmManager walkAM = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            walkAM.set(AlarmManager.RTC_WAKEUP,walkstartCal.getTimeInMillis(), walk_sender);

            //타이머 설정
            Date walkStop = new Date(walkCal.getTimeInMillis());

            Timer walkTimer = new Timer();
            walkTimer.schedule(new WalkTimer(), walkStop);
        }

        //핸드폰 사용 검사 시작
        if(components[3]){
            Calendar startCal = Calendar.getInstance();
            startCal.set(Calendar.HOUR_OF_DAY, startHour);
            startCal.set(Calendar.MINUTE, startMin);
            startCal.set(Calendar.SECOND, 0);

            Calendar stopCal = Calendar.getInstance();
            stopCal.set(Calendar.HOUR_OF_DAY, stopHour);
            stopCal.set(Calendar.MINUTE, stopMin);
            stopCal.set(Calendar.SECOND, 0);

            long startTime = startCal.getTimeInMillis();
            long stopTime = stopCal.getTimeInMillis();

            Intent phoneIntent = new Intent(context, phoneService.class);
            phoneIntent.putExtra("appNames", (Serializable) appNames);
            phoneIntent.putExtra("startTime",startTime);
            phoneIntent.putExtra("stopTime",stopTime);
            PendingIntent phoneSender = PendingIntent.getService(context, 0, phoneIntent, 0);

            AlarmManager phoneAM = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            phoneAM.set(AlarmManager.RTC_WAKEUP,stopTime, phoneSender);

        }
        //장소 검사 시작
        if(components[4]){
            for(Location s:locations) {
                Calendar locationCal = Calendar.getInstance();
                locationCal.set(Calendar.HOUR_OF_DAY, s.getLocationHour());
                locationCal.set(Calendar.MINUTE, s.getLocationMin());
                locationCal.set(Calendar.SECOND, 0);

                Date checkTime = new Date(locationCal.getTimeInMillis());
                Timer locationTimer = new Timer();
                locationTimer.schedule(new LocationTimer(), checkTime, 60000);
            }
        }
    }

    //기상
    class WakeupTimer extends TimerTask {
        @Override
        public void run() {
            if (isServiceRunning("com.example.ohjeom.services.wakeupService")) {
                Log.d("wakeup_service 작동 :", "O");
                Intent intent = new Intent(context.getApplicationContext(), wakeupService.class); // 이동할 컴포넌트
                context.stopService(intent);
            } else
                Log.d("wakeup_service 작동", "X");
        }
    }

    //운동
    class WalkTimer extends TimerTask {
        @Override
        public void run() {
            if (isServiceRunning("com.example.ohjeom.services.walkService")) {
                Log.d("walk_service 작동 :", "O");
                Intent intent = new Intent(context.getApplicationContext(), walkService.class); //이동할 컴포넌트
                context.stopService(intent);
            } else
                Log.d("walk_service 작동", "X");
        }
    }

    //수면
    class SleepTimer extends TimerTask {
        @Override
        public void run() {
            getServiceList();
            if (isServiceRunning("com.example.ohjeom.services.sleepService")) {
                Log.d("service 작동 :", "O");
                Intent intent = new Intent(context.getApplicationContext(), sleepService.class); // 이동할 컴포넌트
                context.stopService(intent);
            } else
                Log.d("service 작동 :", "X");
        }
    }

    class LocationTimer extends TimerTask {
        private static final String TAG = "aaaaaaaaaaaaaaa";
        final Location dest = new Location();
        final Location real = new Location();
        private boolean run = true;
        // 위치 검사 결과
        private String tempTime; // 확인할 때만 쓸 거고 나중에 삭제
        private EditText timeText, destText, realText, resultText;
        private int tryNum = 0; // 장소 검사를 실행한 횟수
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
        }
    }

    //서비스 작동 여부 체크
    public boolean isServiceRunning(String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runServiceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(runServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    void getServiceList()	{
        ActivityManager activity_manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> service_info = activity_manager.getRunningServices(100);

        for(int i=0; i<service_info.size(); i++) {
            Log.d("aaaaaaaaaaaaa", "Service: " + service_info.get(i).service.getPackageName() + ", className: " + service_info.get(i).service.getClassName());
            Log.d("aaaaaaaaaaaa", "       PID/UID: " + service_info.get(i).pid + "/" + service_info.get(i).uid + "  process: " + service_info.get(i).process);
        }
    }
}
