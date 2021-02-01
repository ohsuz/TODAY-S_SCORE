package com.example.ohjeom.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.util.LongSparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.ohjeom.BuildConfig;
import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class PhoneService extends Service {

    private long phoneUsageToday=0;
    private long startTime, stopTime;
    private int phoneTime;
    private boolean run = true;
    private List<AppUsageInfo> appUsageList;
    private ArrayList<String> appNames = new ArrayList<>();
    private ArrayList<Integer> appUsageTimes = new ArrayList<>();

    private class AppUsageInfo {
        String appName, packageName;
        long timeInForeground;
        long temp;
        int launchCount;

        AppUsageInfo(String pName) {
            this.packageName=pName;
        }
    }

    public PhoneService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent clsIntent = new Intent(PhoneService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(PhoneService.this, 0, clsIntent, 0);

        NotificationCompat.Builder clsBuilder;
        if( Build.VERSION.SDK_INT >= 26 )
        {
            String CHANNEL_ID = "channel_id";
            NotificationChannel clsChannel = new NotificationChannel(CHANNEL_ID, "서비스 앱", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(clsChannel);

            clsBuilder = new NotificationCompat.Builder(this, CHANNEL_ID );
        }
        else
        {
            clsBuilder = new NotificationCompat.Builder(this);
        }

        // QQQ: notification 에 보여줄 타이틀, 내용을 수정한다.
        clsBuilder.setSmallIcon(R.drawable.icon_school)
                .setContentTitle("서비스 앱" ).setContentText("서비스 앱")
                .setContentIntent(pendingIntent);

        // foreground 서비스로 실행한다.
        startForeground(1, clsBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("앱 사용시간-","가져오기 시작");
        appNames = intent.getStringArrayListExtra("appNames");
        startTime = intent.getLongExtra("startTime",1);
        stopTime = intent.getLongExtra("stopTime",1);

        for(int i=0;i<appNames.size();i++)
            appUsageTimes.add(0);

        PhoneThread thread = new PhoneThread();
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        run = false;

        int Time = (int) ((stopTime - startTime)/(60*1000));
        phoneTime /= 60;
        int phoneScore = ScoreCalculate(Time,phoneTime);

        Log.d("핸드폰 사용 점수", String.valueOf(phoneScore));
        /*
        점수보내기 & appUsageTimes(앱별 사용시간), phoneTime(총 사용시간)
         */
        Log.d("핸드폰 측정","종료");

        Log.d("앱 사용시간-","가져오기 종료");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class PhoneThread extends Thread {
        private static final String TAG = "ScreenThread";
        public void run(){
            while (run) {
                getUsageStatistics();
                try {
                    Thread.sleep(1000*60);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    void getUsageStatistics() {

        UsageEvents.Event currentEvent;
        List<UsageEvents.Event> allEvents = new ArrayList<>();
        HashMap<String, AppUsageInfo> map = new HashMap <String, AppUsageInfo> ();

        UsageStatsManager mUsageStatsManager =  (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);

        assert mUsageStatsManager != null;
        UsageEvents usageEvents = mUsageStatsManager.queryEvents(startTime, System.currentTimeMillis());
        Log.d("가져오는 시간", String.valueOf(System.currentTimeMillis()));
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

        //만약 이벤트 발생한것이 없을 시? 현재 foreground에 있는 앱 이름 가져와서 비교 후, timeInforeground에 시간 추가
        if(allEvents == null){
            Log.d("이벤트 발생안함","ㅇㅇㅇ");
            String appName = getTopPackageName(PhoneService.this);
            if(appNames.contains(appName))
                map.get(appName).timeInForeground += System.currentTimeMillis() - startTime;
            //map.get().timeInforeground = System.currentTimeMillis() - startTime;
        }

        //iterating through the arraylist
        for (int i=0;i<allEvents.size()-1;i++){
            UsageEvents.Event E0=allEvents.get(i);
            UsageEvents.Event E1=allEvents.get(i+1);

            if(i==allEvents.size()-2&&E1.getEventType()==1){
                map.get(E1.getPackageName()).timeInForeground += System.currentTimeMillis() - E1.getTimeStamp();
            }

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
                map.get(E0.getPackageName()).timeInForeground += diff;
            }
        }

        //transferred final data into modal class object
        appUsageList = new ArrayList<>(map.values());
        PackageManager pm = getPackageManager();

        int i = 0;
        phoneTime = 0;
        for(String appName:appNames){
            boolean isin = false;
            for (AppUsageInfo appUsageInfo : appUsageList) {
                if(appName.equals(appUsageInfo.packageName)) {
                    Log.d("앱 이름:", appName + ", 사용 시간:" + (appUsageInfo.timeInForeground) / 1000 + "초");
                    appUsageTimes.set(i, (int) appUsageInfo.timeInForeground/1000);
                    phoneTime+=(appUsageInfo.timeInForeground/1000);
                    isin = true;
                }
            }
            if(isin)
                continue;
            else {
                Log.d("앱 이름:", appName + ", 사용 시간: 0 초");
            }
        }
        NotificationWarning(30);
    }

    public void NotificationWarning(int i) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_warning")
                .setSmallIcon(R.drawable.icon_school) //BitMap 이미지 요구
                .setContentTitle("오늘의 점수 : 앱 사용량 알리미")
                .setContentText("핸드폰 목표 사용량의 "+i+"%를 도달했습니다.")
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.icon_school); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            NotificationChannel channel = new NotificationChannel("channel_warning", "오늘의 점수 알리미", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("앱 사용량 경고");
            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }else builder.setSmallIcon(R.drawable.icon_school); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(2, builder.build()); // 고유숫자로 노티피케이션 동작시킴
    }

    public int ScoreCalculate(long Time, long phoneTime){
        int score = 0;
        if(phoneTime<Time * (1/10))
            score = 100;
        else if(phoneTime < Time*(2/10))
            score = 80;
        else if(phoneTime < Time*(3/10))
            score = 60;
        else if(phoneTime < Time*(4/10))
            score = 40;
        else if(phoneTime < Time*(5/10))
            score = 20;
        else
            score = 0;
        return score;
    }

    public static String getTopPackageName(@NonNull Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        long lastRunAppTimeStamp = 0L;

        final long INTERVAL = 1000 * 60 * 5;
        final long end = System.currentTimeMillis();
        // 1 minute ago
        final long begin = end - INTERVAL;

        LongSparseArray packageNameMap = new LongSparseArray<>();
        final UsageEvents usageEvents = usageStatsManager.queryEvents(begin, end);
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);

            if(isForeGroundEvent(event)) {
                packageNameMap.put(event.getTimeStamp(), event.getPackageName());
                if(event.getTimeStamp() > lastRunAppTimeStamp) {
                    lastRunAppTimeStamp = event.getTimeStamp();
                }
            }
        }

        return (String) packageNameMap.get(lastRunAppTimeStamp, "");
    }

    private static boolean isForeGroundEvent(UsageEvents.Event event) {
        if(event == null) {
            return false;
        }
        if(BuildConfig.VERSION_CODE >= 29) {
            return event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED;
        }
        return event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND;
    }

}