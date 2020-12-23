package com.example.ohjeom.services;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class phoneService extends Service {

    private long phoneUsageToday=0;
    private long startTime, stopTime;
    List<AppUsageInfo> appUsageList;
    List<ResolveInfo> appNames;

    private class AppUsageInfo {
        Drawable appIcon;
        String appName, packageName;
        long timeInForeground;
        int launchCount;

        AppUsageInfo(String pName) {
            this.packageName=pName;
        }
    }

    public phoneService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("앱 사용시간-","가져오기 시작");
        appNames = (List<ResolveInfo>) intent.getSerializableExtra("appNames");
        startTime = intent.getLongExtra("startTime",1);
        stopTime = intent.getLongExtra("stopTime",1);

        getUsageStatistics();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("앱 사용시간-","가져오기 종료");

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void getUsageStatistics() {

        UsageEvents.Event currentEvent;
        List<UsageEvents.Event> allEvents = new ArrayList<>();
        HashMap<String, AppUsageInfo> map = new HashMap <String, AppUsageInfo> ();

        long currTime = stopTime; //querying past three hours

        UsageStatsManager mUsageStatsManager =  (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);

        assert mUsageStatsManager != null;
        UsageEvents usageEvents = mUsageStatsManager.queryEvents(startTime, currTime);

        //capturing all events in a array to compare with next element
        while (usageEvents.hasNextEvent()) {
            currentEvent = new UsageEvents.Event();
            usageEvents.getNextEvent(currentEvent);
            if (currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                    currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                allEvents.add(currentEvent);
                String key = currentEvent.getPackageName();
        // taking it into a collection to access by package name
                if (map.get(key)==null)
                    map.put(key,new AppUsageInfo(key));
            }
        }

        //iterating through the arraylist
        for (int i=0;i<allEvents.size()-1;i++){
            UsageEvents.Event E0=allEvents.get(i);
            UsageEvents.Event E1=allEvents.get(i+1);

            //for launchCount of apps in time range
            if (!E0.getPackageName().equals(E1.getPackageName()) && E1.getEventType()==1){
            // if true, E1 (launch event of an app) app launched
                map.get(E1.getPackageName()).launchCount++;
            }

            //for UsageTime of apps in time range
            if (E0.getEventType()==1 && E1.getEventType()==2
                    && E0.getClassName().equals(E1.getClassName())){
                long diff = E1.getTimeStamp()-E0.getTimeStamp();
                phoneUsageToday+=diff; //gloabl Long var for total usagetime in the timerange
                map.get(E0.getPackageName()).timeInForeground+= diff;
            }
        }

        //transferred final data into modal class object
        appUsageList = new ArrayList<>(map.values());
        PackageManager pm = getPackageManager();

        for(ResolveInfo s:appNames){
            String appName = s.activityInfo.packageName;
            for (AppUsageInfo appUsageInfo : appUsageList) {
                if(appName.equals(appUsageInfo.packageName)) {
                    Log.d("앱 이름:", s.loadLabel(pm).toString() + ", 사용 시간:" + (appUsageInfo.timeInForeground) / (60 * 1000) + "분");
                }
            }
        }

        stopSelf();
    }
}