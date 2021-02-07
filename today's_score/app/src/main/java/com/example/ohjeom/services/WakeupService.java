package com.example.ohjeom.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;

import java.util.Calendar;

public class WakeupService extends Service implements SensorEventListener {
    private static final String TAG = "WakeupService";
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private int walkCount = 0;
    private long wakeupTime;

    public WakeupService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent clsIntent = new Intent(WakeupService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(WakeupService.this, 0, clsIntent, 0);

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
        Log.d(TAG + "기상 측정","시작");
        wakeupTime = intent.getLongExtra("wakeupTime",1);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this,stepDetectorSensor,SensorManager.SENSOR_DELAY_UI);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            if(event.values[0] == 1.0f){
                walkCount++;
                Log.d(TAG + "기상 측정:","걸음수 증가,"+walkCount);
            }
            if(walkCount == 50) {
                stopSelf();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onDestroy() {
        //50걸음이 되면 종료 or 기상 시간 + 1시간 되면 종료
        sensorManager.unregisterListener(this);

        Calendar resultCal = Calendar.getInstance();
        long resultTime = resultCal.getTimeInMillis();
        int wakeupScore = ScoreCalculate(resultTime);

        Log.d(TAG + "기상 점수:", String.valueOf(wakeupScore));
        Log.d(TAG + "기상 시간",resultCal.get(Calendar.HOUR_OF_DAY)+"시"+resultCal.get(Calendar.MINUTE)+"분");

        /*
        점수 보내기 : wakeScore && 기상 시간도 보내려면? resultCal.get(Calendar.HOUR_OF_DAY), resultCal.get(Calendar.MINUTE)
         */
        Log.d(TAG + "기상 측정","종료");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //점수 계산
    public int ScoreCalculate(long resultTime) {
        int score = 0;
        long score_time = resultTime/(60*1000)-wakeupTime/(60*1000);

        if(score_time<=10)
            score = 100;
        else if(score_time<=20)
            score = 80;
        else if(score_time<=30)
            score = 60;
        else if(score_time<=40)
            score = 40;
        else if(score_time<=50)
            score = 20;
        else if(score_time>50)
            score = 0;

        return score;
    }
}