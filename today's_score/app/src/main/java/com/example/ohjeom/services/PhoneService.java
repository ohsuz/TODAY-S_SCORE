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

import com.android.volley.BuildConfig;
import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;
import com.example.ohjeom.models.User;
import com.example.ohjeom.models.Weather;
import com.example.ohjeom.retrofit.ScoreFunctions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PhoneService extends Service {
    private static final String TAG = "PhoneService";
    private int phoneUsageToday;
    private long startTime, stopTime;
    private boolean run = true;
    private List<AppUsageInfo> appUsageList;
    private String[] appNames;
    private ArrayList<Integer> appUsageTimes = new ArrayList<>(); //시간만 보내고 싶을때 (순서 꼬이는 경우가 없으면 사용 가능)
    private Map<String,Long> appUsageTimesList = new HashMap<>();

    private class AppUsageInfo {
        String packageName;
        long timeInForeground;

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
        if( Build.VERSION.SDK_INT >= 26 ) {
            String CHANNEL_ID = "channel_id";
            NotificationChannel clsChannel = new NotificationChannel(CHANNEL_ID, "서비스 앱", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(clsChannel);

            clsBuilder = new NotificationCompat.Builder(this, CHANNEL_ID );
        } else {
            clsBuilder = new NotificationCompat.Builder(this);
        }

        clsBuilder.setSmallIcon(R.drawable.icon_school)
                .setContentTitle("오늘의 점수").setContentText("Service is running...")
                .setContentIntent(pendingIntent);

        startForeground(1, clsBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG + "앱 사용시간-","가져오기 시작");
        appNames = intent.getStringArrayExtra("appNames");
        startTime = intent.getLongExtra("startTime",1);
        stopTime = intent.getLongExtra("stopTime",1);

        for (int i = 0; i < appNames.length; i++)
            appUsageTimes.add(0);

        PhoneThread thread = new PhoneThread();
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        run = false;

        int time = (int) ((stopTime - startTime)/(60*1000));
        phoneUsageToday /= 60;
        int phoneScore = ScoreCalculate(time, phoneUsageToday);

        Log.d(TAG + "핸드폰 사용 점수", String.valueOf(phoneScore));
        Log.d(TAG + "총 사용시간", String.valueOf(phoneUsageToday));
        Set set = appUsageTimesList.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            String appName = (String) iterator.next();
            Log.d(TAG + "앱별 사용시간", appName+","+appUsageTimesList.get(appName));
        }

        /*
        점수보내기 & appUsageTimes(앱별 사용시간), phoneTime(총 사용시간)
         */
        ScoreFunctions scoreFunctions = new ScoreFunctions();
        scoreFunctions.addPhoneUsageScore(phoneScore, time, phoneUsageToday);

        /* 업데이트된 점수 가져오기 to HomeAdapter 업데이트*/
        String userID = getSharedPreferences("user", MODE_PRIVATE).getString("id", "aaa");
        ScoreFunctions.getScores(userID, ScoreFunctions.getDate()); // 서버에서 오늘의 날짜에 해당하는 점수 정보를 얻어와서 score 변수에 저장됨
        User.setIsInitialized(true);
        Log.d(TAG + "앱 사용시간-","가져오기 종료");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class PhoneThread extends Thread {
        public void run(){
            long diff = (stopTime - startTime) / 1000;
            double[] notificationTime = {0.25, 0.5, 0.75, 1};
            int i = 0;
            while (run) {
                phoneUsageToday = getUsageStatistics();
                if(i <= 3) {
                    if (phoneUsageToday >= (long) diff * notificationTime[i]) {
                        Log.d(TAG + "알림 보내기", (notificationTime[i] * 100) + "% 초과");
                        NotificationWarning((int) (notificationTime[i] * 100));
                        i++;
                    }
                }
                try {
                    Thread.sleep(1000*60);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    private int getUsageStatistics() {
        int sum = 0;

        UsageEvents.Event currentEvent;
        List<UsageEvents.Event> allEvents = new ArrayList<>();
        HashMap<String, AppUsageInfo> map = new HashMap <String, AppUsageInfo> ();

        UsageStatsManager mUsageStatsManager =  (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);

        assert mUsageStatsManager != null;
        UsageEvents usageEvents = mUsageStatsManager.queryEvents(startTime, System.currentTimeMillis());
        while (usageEvents.hasNextEvent()) {
            currentEvent = new UsageEvents.Event();
            usageEvents.getNextEvent(currentEvent);
            if (currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                    currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                allEvents.add(currentEvent);
                String key = currentEvent.getPackageName();
                if (map.get(key)==null)
                    map.put(key,new AppUsageInfo(key));
            }
        }

        // 만약 이벤트 발생한것이 없을 시? 현재 foreground에 있는 앱 이름 가져와서 비교 후, timeInforeground에 시간 추가
        if(allEvents.isEmpty()){
            String appName = getTopPackageName(PhoneService.this);
            Log.d(TAG ,"이벤트 미발생" + appName);
            if(Arrays.asList(appNames).contains(appName)) {
                map.put(appName,new AppUsageInfo(appName));
                map.get(appName).timeInForeground += System.currentTimeMillis() - startTime;
            }
        }

        for (int i = 0; i < allEvents.size()-1; i++){
            long timestamp = allEvents.get(i).getTimeStamp();
            Date date1 = new Date(timestamp);
            SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
            String date22 = datef.format(date1);
            Log.d(TAG + "이벤트 시작 시간", date22);
            Log.d(TAG + "이벤트 발생 어디서 발생하는거냐",allEvents.get(i).getPackageName()+","+allEvents.get(i).getEventType());
            UsageEvents.Event E0=allEvents.get(i);
            UsageEvents.Event E1=allEvents.get(i+1);

            //처음 Background 에 들어온 경우
            if(i==0&&E0.getEventType()==2){
                map.get(E0.getPackageName()).timeInForeground += E0.getTimeStamp() - startTime;
            }

            //계속 Foreground인 경우
            if(i==allEvents.size()-2&&E1.getEventType()==1){
                map.get(E1.getPackageName()).timeInForeground += System.currentTimeMillis() - E1.getTimeStamp();
            }

            //for UsageTime of apps in time range
            if (E0.getEventType()==1 && E1.getEventType()==2
                    && E0.getClassName().equals(E1.getClassName())){
                long diff = E1.getTimeStamp()-E0.getTimeStamp();
                map.get(E0.getPackageName()).timeInForeground += diff;
            }
        }

        //transferred final data into modal class object
        appUsageList = new ArrayList<>(map.values());
        int i =0;
        for(String appName: appNames){
            boolean isin = false;
            for (AppUsageInfo appUsageInfo : appUsageList) {
                if(appName.equals(appUsageInfo.packageName)) {
                    Log.d(TAG + "앱 이름:", appName + ", 사용 시간:" + (appUsageInfo.timeInForeground) / 1000 + "초");
                    appUsageTimes.set(i, (int) appUsageInfo.timeInForeground/1000);
                    appUsageTimesList.put(appName,appUsageInfo.timeInForeground/1000);
                    sum += (appUsageInfo.timeInForeground/1000);
                    isin = true;
                }
            }
            if(!isin) {
                Log.d(TAG + "앱 이름:", appName + ", 사용 시간: 0초");
                appUsageTimes.set(i, 0);
                appUsageTimesList.put(appName, (long) 0);
            }
            i++;
        }
        Log.d(TAG + "총 사용시간", sum + "초");
        return sum;
    }

    public void NotificationWarning(int percent) {
        Log.d("percent:", String.valueOf(percent));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_warning")
                .setSmallIcon(R.drawable.icon_school)
                .setContentTitle("오늘의 점수 : 앱 사용량 알리미")
                .setContentText("핸드폰 목표 사용량의 "+percent+"%를 도달했습니다.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.icon_school); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            NotificationChannel channel = new NotificationChannel("channel_warning", "오늘의 점수 알리미", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("앱 사용량 경고");
            assert notificationManager != null; // 노티피케이션 채널을 시스템에 등록
            notificationManager.createNotificationChannel(channel);
        }else builder.setSmallIcon(R.drawable.icon_school); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(2, builder.build()); // 고유숫자로 노티피케이션 동작시킴
    }

    public int ScoreCalculate(long time, long phoneTime){
        int score;
        if (phoneTime < time*0.1)
            score = 100;
        else if (phoneTime < time*0.2)
            score = 80;
        else if (phoneTime < time *0.3)
            score = 60;
        else if (phoneTime < time*0.4)
            score = 40;
        else if (phoneTime < time*0.5)
            score = 20;
        else
            score = 0;
        return score;
    }

    public static String getTopPackageName(@NonNull Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long lastRunAppTimeStamp = 0L;
        final long INTERVAL = 1000 * 60 * 60;
        final long end = System.currentTimeMillis();
        // 1 hour ago
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