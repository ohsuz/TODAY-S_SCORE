package com.example.ohjeom.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;
import com.example.ohjeom.models.Template;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StartService extends Service {
    private String TAG = "StartService";
    private Template template;
    private int month, day;
    private PendingIntent wakeupSender,sleepSender,walkSender,phoneSender,locationSender1,locationSender2,locationSender3,paySender;
    private AlarmManager wakeupAM,sleepAM,walkAM,phoneAM,locationAM1,locationAM2,locationAM3,payAM;

    public StartService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent clsIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, clsIntent, 0);

        NotificationCompat.Builder clsBuilder;
        if(Build.VERSION.SDK_INT >= 26)
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
                .setContentTitle("서비스 앱").setContentText("서비스 앱")
                .setContentIntent(pendingIntent);

        // foreground 서비스로 실행한다.
        startForeground(1, clsBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        template = (Template) intent.getSerializableExtra("template");
        month = intent.getIntExtra("month",0);
        day = intent.getIntExtra("day",0);
        startExamination();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        PendingIntent[] piList = new PendingIntent[]{wakeupSender,sleepSender,walkSender,phoneSender,locationSender1,locationSender2,locationSender3,paySender};
        AlarmManager[] amList = new AlarmManager[]{wakeupAM, sleepAM, walkAM, phoneAM, locationAM1,locationAM2,locationAM3,payAM};

        for(int i=0;i<8;i++)
            if(amList[i] != null) {
                Log.d("알람해제:", String.valueOf(amList[i]));
                amList[i].cancel(piList[i]);
                piList[i].cancel();
            }

        Class[] serviceName = new Class[]{WakeupService.class, SleepService.class, WalkService.class, PhoneService.class,
                LocationService1.class, LocationService2.class, LocationService3.class,PaymentService.class};
        String[] serviceList = {"com.example.ohjeom.services.WakeupService","com.example.ohjeom.services.SleepService","com.example.ohjeom.services.WalkService",
                "com.example.ohjeom.services.PhoneService", "com.example.ohjeom.services.LocationService1",
                "com.example.ohjeom.services.LocationService2", "com.example.ohjeom.services.LocationService3",
                "com.example.ohjeom.services.PaymentService"};

        for(int i=0;i<8;i++){
            if(isServiceRunning(serviceList[i])){
                Log.d("작동중",serviceList[i]);
                Intent intent = new Intent(StartService.this, serviceName[i]);
                stopService(intent);
            }
            else
                Log.d("작동중 아님",serviceList[i]);
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void startExamination() {
        boolean[] components = template.getComponents();
        //기상 검사 시작
        if(components[0]) {
            Calendar wakeupCal = Calendar.getInstance();
            wakeupCal.set(Calendar.MONTH, month);
            wakeupCal.set(Calendar.DAY_OF_MONTH, day);
            wakeupCal.set(Calendar.HOUR_OF_DAY, template.getWakeupHour());
            wakeupCal.set(Calendar.MINUTE,template.getWakeupMin());
            wakeupCal.set(Calendar.SECOND, 0);
            long wakeupTime = wakeupCal.getTimeInMillis();

            Intent wakeupIntent = new Intent(this, WakeupService.class);
            wakeupIntent.putExtra("wakeupTime", wakeupTime);
            wakeupSender = PendingIntent.getService(this, 0, wakeupIntent, 0);

            wakeupAM = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            wakeupAM.setRepeating(AlarmManager.RTC_WAKEUP, wakeupTime, AlarmManager.INTERVAL_DAY,wakeupSender);

            //타이머 설정
            long wakeupStoptime = wakeupTime + (60 * 60 * 1000);
            Date wakeupStop = new Date(wakeupStoptime);

            Timer wakeupTimer = new Timer();
            wakeupTimer.schedule(new WakeupTimer(), wakeupStop,1000*60*60*24);
        }

        //수면 검사 시작
        if(components[1]){
            Calendar sleepCal = Calendar.getInstance();
            sleepCal.set(Calendar.MONTH, month);
            sleepCal.set(Calendar.DAY_OF_MONTH, day);
            sleepCal.set(Calendar.HOUR_OF_DAY, template.getSleepHour());
            sleepCal.set(Calendar.MINUTE, template.getSleepMin());
            sleepCal.set(Calendar.SECOND, 0);

            Calendar wakeupCal = Calendar.getInstance();
            wakeupCal.set(Calendar.MONTH, month);
            wakeupCal.set(Calendar.DAY_OF_MONTH, day);
            wakeupCal.set(Calendar.HOUR_OF_DAY, template.getWakeupHour());
            wakeupCal.set(Calendar.MINUTE, template.getWakeupMin());
            wakeupCal.set(Calendar.SECOND, 0);
            long wakeupTime = wakeupCal.getTimeInMillis();

            Intent sleepIntent = new Intent(this, SleepService.class);
            sleepSender = PendingIntent.getService(this, 0, sleepIntent, 0);
            PendingIntent sleepSender = PendingIntent.getService(this, 0, sleepIntent, 0);

            sleepAM = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            sleepAM.setRepeating(AlarmManager.RTC_WAKEUP, sleepCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY,sleepSender);

            //타이머 설정
            if (sleepCal.getTimeInMillis() - wakeupTime >= 0) {
                wakeupCal.add(Calendar.DATE, 1);
            }

            long sleepStopTime = wakeupCal.getTimeInMillis();

            Date sleepStop = new Date(sleepStopTime);

            Timer sleepTimer = new Timer();
            sleepTimer.schedule(new SleepTimer(), sleepStop,1000*60*60*24);
        }

        //걸음수 검사 시작
        if(components[2]){
            Calendar walkCal = Calendar.getInstance();
            walkCal.set(Calendar.MONTH, month);
            walkCal.set(Calendar.DAY_OF_MONTH, day);
            walkCal.set(Calendar.HOUR_OF_DAY, template.getWalkHour());
            walkCal.set(Calendar.MINUTE, template.getWalkMin());
            walkCal.set(Calendar.SECOND, 0);

            Calendar walkstartCal = Calendar.getInstance();
            walkstartCal.set(Calendar.MONTH, month);
            walkstartCal.set(Calendar.DAY_OF_MONTH, day);
            walkstartCal.set(Calendar.HOUR_OF_DAY, template.getWakeupHour());
            walkstartCal.set(Calendar.MINUTE, template.getWakeupMin());
            walkstartCal.set(Calendar.SECOND, 0);

            Intent walkIntent = new Intent(this, WalkService.class);
            walkIntent.putExtra("walkCount", template.getWalkCount());
            walkSender = PendingIntent.getService(this, 0, walkIntent, 0);

            walkAM = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            walkAM.setRepeating(AlarmManager.RTC_WAKEUP,walkstartCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY,walkSender);

            //타이머 설정
            Date walkStop = new Date(walkCal.getTimeInMillis());

            Timer walkTimer = new Timer();
            walkTimer.schedule(new WalkTimer(), walkStop,1000*60*60*24);
        }

        //핸드폰 사용 검사 시작
        if(components[3]){
            Calendar startCal = Calendar.getInstance();
            startCal.set(Calendar.MONTH, month);
            startCal.set(Calendar.DAY_OF_MONTH, day);
            startCal.set(Calendar.HOUR_OF_DAY, template.getStartHour());
            startCal.set(Calendar.MINUTE, template.getStartMin());
            startCal.set(Calendar.SECOND, 0);

            Calendar stopCal = Calendar.getInstance();
            stopCal.set(Calendar.MONTH, month);
            stopCal.set(Calendar.DAY_OF_MONTH, day);
            stopCal.set(Calendar.HOUR_OF_DAY, template.getStopHour());
            stopCal.set(Calendar.MINUTE, template.getStopMin());
            stopCal.set(Calendar.SECOND, 0);

            long startTime = startCal.getTimeInMillis();
            long stopTime = stopCal.getTimeInMillis();

            Intent phoneIntent = new Intent(this, PhoneService.class);
            phoneIntent.putExtra("appNames", template.getAppNames());
            phoneIntent.putExtra("startTime",startTime);
            phoneIntent.putExtra("stopTime",stopTime);
            phoneSender = PendingIntent.getService(this, 0, phoneIntent, 0);

            phoneAM = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            phoneAM.setRepeating(AlarmManager.RTC_WAKEUP,stopTime, AlarmManager.INTERVAL_DAY,phoneSender);
        }

        //장소 검사 시작
        if(components[4]){

            Calendar locationCal = Calendar.getInstance();
            locationCal.set(Calendar.MONTH, month);
            locationCal.set(Calendar.DAY_OF_MONTH, day);

            long locationTime = locationCal.getTimeInMillis();

            //LocationService locationService = new LocationService(); @@@@@@@@ 안 쓰임
            //locationIntent.putExtra("locations",template.getLocations());
            Intent locationIntent1 = new Intent(this, LocationService1.class);
            Intent locationIntent2 = new Intent(this, LocationService2.class);
            Intent locationIntent3 = new Intent(this, LocationService3.class);

            switch (template.getLocations().size()) {
                case 1:
                    locationIntent1.putExtra("location",template.getLocations().get(0));
                    locationSender1 = PendingIntent.getService(this, 0, locationIntent1, 0);

                    locationAM1 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    locationAM1.setRepeating(AlarmManager.RTC_WAKEUP,locationTime, AlarmManager.INTERVAL_DAY,locationSender1);
                    break;
                case 2:
                    locationIntent1.putExtra("location",template.getLocations().get(0));
                    locationIntent2.putExtra("location",template.getLocations().get(1));

                    locationSender1 = PendingIntent.getService(this, 0, locationIntent1, 0);
                    locationSender2 = PendingIntent.getService(this, 0, locationIntent2, 0);

                    locationAM1 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    locationAM1.setRepeating(AlarmManager.RTC_WAKEUP,locationTime, AlarmManager.INTERVAL_DAY,locationSender1);
                    locationAM2 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    locationAM2.setRepeating(AlarmManager.RTC_WAKEUP,locationTime, AlarmManager.INTERVAL_DAY,locationSender2);
                    break;
                case 3:
                    locationIntent1.putExtra("location",template.getLocations().get(0));
                    locationIntent2.putExtra("location",template.getLocations().get(1));
                    locationIntent3.putExtra("location",template.getLocations().get(2));

                    locationSender1 = PendingIntent.getService(this, 0, locationIntent1, 0);
                    locationSender2 = PendingIntent.getService(this, 0, locationIntent2, 0);
                    locationSender3 = PendingIntent.getService(this, 0, locationIntent3, 0);

                    locationAM1 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    locationAM1.setRepeating(AlarmManager.RTC_WAKEUP,locationTime, AlarmManager.INTERVAL_DAY,locationSender1);
                    locationAM2 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    locationAM2.setRepeating(AlarmManager.RTC_WAKEUP,locationTime, AlarmManager.INTERVAL_DAY,locationSender2);
                    locationAM3 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    locationAM3.setRepeating(AlarmManager.RTC_WAKEUP,locationTime, AlarmManager.INTERVAL_DAY,locationSender3);
                    break;
                default:
                    Log.d(TAG, "SWITCH DEFAULT");
                    break;
            }
        }

        //소비 검사 시작
        if(components[5]) {
            Calendar payCal = Calendar.getInstance();
            payCal.set(Calendar.MONTH, month);
            payCal.set(Calendar.DAY_OF_MONTH, day);
            payCal.set(Calendar.HOUR_OF_DAY, 0);
            payCal.set(Calendar.MINUTE, 0);
            payCal.set(Calendar.SECOND, 0);

            Calendar payStopCal = Calendar.getInstance();
            payStopCal.set(Calendar.MONTH, month);
            payStopCal.set(Calendar.DAY_OF_MONTH, day);
            payStopCal.set(Calendar.HOUR_OF_DAY, 23);
            payStopCal.set(Calendar.MINUTE, 55);
            payStopCal.set(Calendar.SECOND, 0);

            long payTime = payCal.getTimeInMillis();
            Date payStop = new Date(payStopCal.getTimeInMillis());

            Intent payIntent = new Intent(this, PaymentService.class);
            payIntent.putExtra("money", template.getMoney());
            paySender = PendingIntent.getService(this, 0, payIntent, 0);

            payAM = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            payAM.setRepeating(AlarmManager.RTC_WAKEUP,payTime, AlarmManager.INTERVAL_DAY,paySender);

            //타이머 설정
            Timer payTimer = new Timer();
            payTimer.schedule(new PayTimer(),payStop,1000*60*60*24);
        }
    }

    //기상
    class WakeupTimer extends TimerTask {
        @Override
        public void run() {
            while(true) {
                if (isServiceRunning("com.example.ohjeom.services.WakeupService")) {
                    Log.d("wakeup_service 작동 :", "O");
                    Intent intent = new Intent(getApplicationContext(), WakeupService.class); // 이동할 컴포넌트
                    stopService(intent);
                    break;
                }
            }
        }
    }

    //운동
    class WalkTimer extends TimerTask {
        @Override
        public void run() {
            while(true) {
                if (isServiceRunning("com.example.ohjeom.services.WalkService")) {
                    Log.d("walk_service 작동 :", "O");
                    Intent intent = new Intent(getApplicationContext(), WalkService.class); //이동할 컴포넌트
                    stopService(intent);
                    break;
                }
            }
        }
    }

    //수면
    class SleepTimer extends TimerTask {
        @Override
        public void run() {
            while(true) {
                if (isServiceRunning("com.example.ohjeom.services.SleepService")) {
                    Log.d("service 작동 :", "O");
                    Intent intent = new Intent(getApplicationContext(), SleepService.class); // 이동할 컴포넌트
                    stopService(intent);
                    break;
                }
            }
        }
    }

    //소비
    class PayTimer extends TimerTask {
        @Override
        public void run() {
            while(true) {
                if (isServiceRunning("com.example.ohjeom.services.PaymentService")) {
                    Log.d("service 작동 :", "O");
                    Intent intent = new Intent(getApplicationContext(), PaymentService.class); // 이동할 컴포넌트
                    stopService(intent);
                    break;
                }
            }
        }
    }

    //서비스 작동 여부 체크
    public boolean isServiceRunning(String serviceName) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runServiceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(runServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //서비스 리스트 이름 확인
    void getServiceList()	{
        ActivityManager activity_manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> service_info = activity_manager.getRunningServices(100);

        for(int i=0; i<service_info.size(); i++) {
            Log.d("aaaaaaaaaaaaa", "Service: " + service_info.get(i).service.getPackageName() + ", className: " + service_info.get(i).service.getClassName());
            Log.d("aaaaaaaaaaaa", "       PID/UID: " + service_info.get(i).pid + "/" + service_info.get(i).uid + "  process: " + service_info.get(i).process);
        }
    }
}