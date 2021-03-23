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
import com.example.ohjeom.models.Storage;
import com.example.ohjeom.models.Template;
import com.example.ohjeom.models.Weather;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StartService extends Service {
    private static final String TAG = "StartService";
    private Template template;
    private int month, day;
    private PendingIntent wakeupSender, sleepSender, walkSender, weatherSender, phoneSender, locationSender1, locationSender2, locationSender3, paySender;
    private AlarmManager wakeupAM, sleepAM, walkAM, weatherAM, phoneAM, locationAM1, locationAM2, locationAM3, payAM;
    private Timer wakeupTimer, sleepTimer, walkTimer, phoneTimer, payTimer;
    private String[] components = new String[]{};

    public StartService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent clsIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, clsIntent, 0);

        NotificationCompat.Builder clsBuilder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "channel_id";
            NotificationChannel clsChannel = new NotificationChannel(CHANNEL_ID, "서비스 앱", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(clsChannel);

            clsBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
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
        template = Storage.getTemplate();
        month = intent.getIntExtra("month", 0);
        day = intent.getIntExtra("day", 0);
        Log.d(TAG + "측정 시작", template.getNameResult());
        startExamination();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        PendingIntent[] piList = new PendingIntent[]{wakeupSender, sleepSender, walkSender, phoneSender, locationSender1, locationSender2, locationSender3, paySender};
        AlarmManager[] amList = new AlarmManager[]{wakeupAM, sleepAM, walkAM, phoneAM, locationAM1, locationAM2, locationAM3, payAM};

        for (int i = 0; i < 8; i++)
            if (amList[i] != null) {
                Log.d(TAG + "알람해제:", String.valueOf(amList[i]));
                amList[i].cancel(piList[i]);
                piList[i].cancel();
            }

        Class[] serviceName = new Class[]{WakeupService.class, SleepService.class, WalkService.class, WeatherService.class, PhoneService.class,
                LocationService1.class, LocationService2.class, LocationService3.class, PaymentService.class};
        String[] serviceList = {"com.example.ohjeom.services.WakeupService", "com.example.ohjeom.services.SleepService", "com.example.ohjeom.services.WalkService",
                "com.example.ohjeom.services.WalkService", "com.example.ohjeom.services.PhoneService", "com.example.ohjeom.services.LocationService1",
                "com.example.ohjeom.services.LocationService2", "com.example.ohjeom.services.LocationService3", "com.example.ohjeom.services.PaymentService"};
        Timer[] timers = new Timer[]{wakeupTimer, sleepTimer, walkTimer, phoneTimer, payTimer};
        for (int i = 0; i < 9; i++) {
            Log.d(TAG + "서비스해제: ", String.valueOf(serviceName[i]));
            if (isServiceRunning(serviceList[i])) {
                Log.d(TAG + "작동중", serviceList[i]);
                Intent intent = new Intent(StartService.this, serviceName[i]);
                stopService(intent);
            } else
                Log.d(TAG + "작동중 아님", serviceList[i]);
        }

        for (int i = 0; i < 5; i++) {
            if (timers[i] != null)
                timers[i].cancel();
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void startExamination() {
        components = template.getComponents();
        Log.d("#######", components[0]);
        Log.d("#######", components[1]);
        Log.d("#######", components[2]);
        Log.d("#######", components[3]);
        Log.d("#######", components[4]);
        Log.d("#######", components[5]);

        if (components[0].equals("true")) {
            startWakeUpExamination();
        }
        if (components[1].equals("true")) {
            startSleepExamination();
        }
        if (components[2].equals("true")) {
            startWalkExamination();
        }
        if (components[3].equals("true")) {
            startPhoneUsageExamination();
        }
        if (components[4].equals("true")) {
            startLocationExamination();
        }
        if (components[5].equals("true")) {
            startPaymentExamination();
        }
    }

    public void startWakeUpExamination() {
        Calendar wakeupCal = Calendar.getInstance();
        wakeupCal.set(Calendar.MONTH, month);
        wakeupCal.set(Calendar.DAY_OF_MONTH, day);
        wakeupCal.set(Calendar.HOUR_OF_DAY, template.getWakeupHour());
        wakeupCal.set(Calendar.MINUTE, template.getWakeupMin());
        wakeupCal.set(Calendar.SECOND, 0);
        long wakeupTime = wakeupCal.getTimeInMillis();

        Intent wakeupIntent = new Intent(this, WakeupService.class);
        wakeupIntent.putExtra("wakeupTime", wakeupTime);
        wakeupSender = PendingIntent.getService(this, 0, wakeupIntent, 0);

        wakeupAM = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        /*
        wakeupAM.setRepeating(AlarmManager.RTC_WAKEUP, wakeupTime, AlarmManager.INTERVAL_DAY, wakeupSender);
        */

        wakeupAM.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,wakeupTime,wakeupSender);

        //타이머 설정
        long wakeupStoptime = wakeupTime + (60 * 60 * 1000);
        Date wakeupStop = new Date(wakeupStoptime);

        wakeupTimer = new Timer();
        wakeupTimer.schedule(new WakeupTimer(), wakeupStop, 1000 * 60 * 60 * 24);
    }

    public void startSleepExamination() {
        Calendar sleepCal = Calendar.getInstance();
        sleepCal.set(Calendar.MONTH, month);
        sleepCal.set(Calendar.DAY_OF_MONTH, day);
        sleepCal.set(Calendar.HOUR_OF_DAY, template.getSleepHour());
        sleepCal.set(Calendar.MINUTE, template.getSleepMin());
        sleepCal.set(Calendar.SECOND, 0);

        Calendar wakeupCal = Calendar.getInstance();
        long wakeupTime;
        if (components[0].equals("true")) {
            wakeupCal.set(Calendar.MONTH, month);
            wakeupCal.set(Calendar.DAY_OF_MONTH, day);
            wakeupCal.set(Calendar.HOUR_OF_DAY, template.getWakeupHour());
            wakeupCal.set(Calendar.MINUTE, template.getWakeupMin());
            wakeupCal.set(Calendar.SECOND, 0);
            if (sleepCal.getTimeInMillis() - wakeupCal.getTimeInMillis() >= 0) {
                wakeupCal.add(Calendar.DATE, 1);
            }
            wakeupTime = wakeupCal.getTimeInMillis();
        } else {
            wakeupTime = sleepCal.getTimeInMillis() + 1000 * 60 * 4;
        }

        Intent sleepIntent = new Intent(this, SleepService.class);
        sleepSender = PendingIntent.getService(this, 0, sleepIntent, 0);
        PendingIntent sleepSender = PendingIntent.getService(this, 0, sleepIntent, 0);

        sleepAM = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        /*
        sleepAM.setRepeating(AlarmManager.RTC_WAKEUP, sleepCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sleepSender);
        */

        sleepAM.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,sleepCal.getTimeInMillis(),sleepSender);
        long sleepStopTime = wakeupTime;
        Date sleepStop = new Date(sleepStopTime);
        sleepTimer = new Timer();
        sleepTimer.schedule(new SleepTimer(), sleepStop, 1000 * 60 * 60 * 24);
    }

    public void startWalkExamination() {
        Calendar walkCal = Calendar.getInstance();
        walkCal.set(Calendar.MONTH, month);
        walkCal.set(Calendar.DAY_OF_MONTH, day);
        walkCal.set(Calendar.HOUR_OF_DAY, template.getWalkHour());
        walkCal.set(Calendar.MINUTE, template.getWalkMin());
        walkCal.set(Calendar.SECOND, 0);

        Calendar walkstartCal = Calendar.getInstance();

        if (components[0].equals("true")) {
            walkstartCal.set(Calendar.MONTH, month);
            walkstartCal.set(Calendar.DAY_OF_MONTH, day);
            walkstartCal.set(Calendar.HOUR_OF_DAY, template.getWakeupHour());
            walkstartCal.set(Calendar.MINUTE, template.getWakeupMin());
            walkstartCal.set(Calendar.SECOND, 0);
        } else {
            walkstartCal.set(Calendar.MONTH, month);
            walkstartCal.set(Calendar.DAY_OF_MONTH, day);
            walkstartCal.set(Calendar.HOUR_OF_DAY, 0);
            walkstartCal.set(Calendar.MINUTE, 0);
            walkstartCal.set(Calendar.SECOND, 0);
        }

        Intent walkIntent = new Intent(this, WalkService.class);
        walkIntent.putExtra("walkCount", template.getWalkCount());
        walkSender = PendingIntent.getService(this, 0, walkIntent, 0);

        walkAM = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        /*
        walkAM.setRepeating(AlarmManager.RTC_WAKEUP, walkstartCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, walkSender);
        */
        walkAM.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, walkstartCal.getTimeInMillis(), walkSender);
        
        //날씨 서비스 설정
        Intent weatherIntent = new Intent(this, WeatherService.class);
        weatherIntent.putExtra("walkStartTime", walkstartCal.get(Calendar.HOUR_OF_DAY));
        weatherIntent.putExtra("walkStopTime", template.getWalkHour());
        weatherSender = PendingIntent.getService(this, 0, weatherIntent, 0);

        weatherAM = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        /*
        weatherAM.setRepeating(AlarmManager.RTC_WAKEUP, walkstartCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, weatherSender);
         */
        
        weatherAM.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, walkstartCal.getTimeInMillis(), weatherSender);
        Date walkStop = new Date(walkCal.getTimeInMillis());
        walkTimer = new Timer();
        walkTimer.schedule(new WalkTimer(), walkStop, 1000 * 60 * 60 * 24);
    }

    public void startLocationExamination() {
        Calendar locationCal = Calendar.getInstance();
        locationCal.set(Calendar.MONTH, month);
        locationCal.set(Calendar.DAY_OF_MONTH, day);

        long locationTime = locationCal.getTimeInMillis();

        Intent locationIntent1 = new Intent(this, LocationService1.class);
        Intent locationIntent2 = new Intent(this, LocationService2.class);
        Intent locationIntent3 = new Intent(this, LocationService3.class);

        switch (template.getLocations().size()) {
            case 1:
                locationSender1 = PendingIntent.getService(this, 0, locationIntent1, 0);

                locationAM1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                /*
                locationAM1.setRepeating(AlarmManager.RTC_WAKEUP, locationTime, AlarmManager.INTERVAL_DAY, locationSender1);
                */
                locationAM1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, locationTime, locationSender1);
                break;
            case 2:
                locationIntent1.putExtra("location", template.getLocations().get(0));
                locationIntent2.putExtra("location", template.getLocations().get(1));

                locationSender1 = PendingIntent.getService(this, 0, locationIntent1, 0);
                locationSender2 = PendingIntent.getService(this, 0, locationIntent2, 0);

                locationAM1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                locationAM1.setRepeating(AlarmManager.RTC_WAKEUP, locationTime, AlarmManager.INTERVAL_DAY, locationSender1);
                locationAM2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                locationAM2.setRepeating(AlarmManager.RTC_WAKEUP, locationTime, AlarmManager.INTERVAL_DAY, locationSender2);
                break;
            case 3:
                locationIntent1.putExtra("location", template.getLocations().get(0));
                locationIntent2.putExtra("location", template.getLocations().get(1));
                locationIntent3.putExtra("location", template.getLocations().get(2));

                locationSender1 = PendingIntent.getService(this, 0, locationIntent1, 0);
                locationSender2 = PendingIntent.getService(this, 0, locationIntent2, 0);
                locationSender3 = PendingIntent.getService(this, 0, locationIntent3, 0);

                locationAM1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                locationAM1.setRepeating(AlarmManager.RTC_WAKEUP, locationTime, AlarmManager.INTERVAL_DAY, locationSender1);
                locationAM2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                locationAM2.setRepeating(AlarmManager.RTC_WAKEUP, locationTime, AlarmManager.INTERVAL_DAY, locationSender2);
                locationAM3 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                locationAM3.setRepeating(AlarmManager.RTC_WAKEUP, locationTime, AlarmManager.INTERVAL_DAY, locationSender3);
                break;
            default:
                Log.d(TAG, "SWITCH DEFAULT");
                break;
        }
    }

    public void startPhoneUsageExamination() {
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
        Date phoneStop = new Date(stopTime);

        Intent phoneIntent = new Intent(this, PhoneService.class);
        phoneIntent.putExtra("appNames", template.getAppNames());
        phoneIntent.putExtra("startTime", startTime);
        phoneIntent.putExtra("stopTime", stopTime);
        phoneSender = PendingIntent.getService(this, 0, phoneIntent, 0);

        phoneAM = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        /*
        phoneAM.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY, phoneSender);
         */
        phoneAM.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime, phoneSender);

        phoneTimer = new Timer();
        phoneTimer.schedule(new PhoneTimer(), phoneStop, 1000 * 60 * 60 * 24);
    }

    public void startPaymentExamination() {
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
        payIntent.putExtra("money", template.getMoneyResult());
        paySender = PendingIntent.getService(this, 0, payIntent, 0);

        payAM = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        /*
        payAM.setRepeating(AlarmManager.RTC_WAKEUP, payTime, AlarmManager.INTERVAL_DAY, paySender);
        */
        payAM.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, payTime, paySender);
        payTimer = new Timer();
        payTimer.schedule(new PayTimer(), payStop, 1000 * 60 * 60 * 24);
    }

    /*
    Timer for examination
    */
    class WakeupTimer extends TimerTask {
        @Override
        public void run() {
            while (true) {
                if (isServiceRunning("com.example.ohjeom.services.WakeupService")) {
                    Log.d(TAG, "Wakeup service 작동 : O");
                    Intent intent = new Intent(StartService.this, WakeupService.class); // 이동할 컴포넌트
                    stopService(intent);
                    break;
                }
            }
        }
    }

    class WalkTimer extends TimerTask {
        @Override
        public void run() {
            while (true) {
                if (isServiceRunning("com.example.ohjeom.services.WalkService")) {
                    Log.d(TAG, "Walk service 작동 : O");
                    Intent intent = new Intent(StartService.this, WalkService.class); //이동할 컴포넌트
                    stopService(intent);
                    break;
                }
            }
        }
    }

    class SleepTimer extends TimerTask {
        @Override
        public void run() {
            while (true) {
                if (isServiceRunning("com.example.ohjeom.services.SleepService")) {
                    Log.d(TAG, "Sleep service 작동 : O");
                    Intent intent = new Intent(StartService.this, SleepService.class); // 이동할 컴포넌트
                    stopService(intent);
                    break;
                }
            }
        }
    }

    class PhoneTimer extends TimerTask {
        @Override
        public void run() {
            while (true) {
                if (isServiceRunning("com.example.ohjeom.services.PhoneService")) {
                    Log.d(TAG, "Phone service 작동 : O");
                    Intent intent = new Intent(StartService.this, PhoneService.class); // 이동할 컴포넌트
                    stopService(intent);
                    break;
                }
            }
        }
    }

    class PayTimer extends TimerTask {
        @Override
        public void run() {
            while (true) {
                if (isServiceRunning("com.example.ohjeom.services.PaymentService")) {
                    Log.d(TAG, "Payment service 작동 : O");
                    Intent intent = new Intent(StartService.this, PaymentService.class); // 이동할 컴포넌트
                    stopService(intent);
                    break;
                }
            }
        }
    }

    // Check if service is running correctly
    public boolean isServiceRunning(String serviceName) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runServiceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(runServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}