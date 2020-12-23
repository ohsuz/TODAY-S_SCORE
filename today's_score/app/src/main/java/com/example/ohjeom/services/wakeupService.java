package com.example.ohjeom.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

public class wakeupService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private int walkCount = 0;
    private long wakeupTime;

    public wakeupService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("기상 측정","시작");
        wakeupTime = intent.getLongExtra("wakeupTime",1);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this,stepDetectorSensor,SensorManager.SENSOR_DELAY_UI);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent event){

        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            if(event.values[0]==1.0f){
                walkCount++;
                Log.d("기상 측정:","걸음수 증가,"+walkCount);
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
        sensorManager.unregisterListener(this);

        Calendar resultCal = Calendar.getInstance();
        long resultTime = resultCal.getTimeInMillis();
        int wakeupScore = ScoreCalculate(resultTime);

        Log.d("기상 완료 시간:",resultCal.get(Calendar.HOUR_OF_DAY)+"시"+resultCal.get(Calendar.MINUTE)+"분");
        Log.d("기상 점수:", String.valueOf(wakeupScore));

        /*
        점수 보내기
         */

        Log.d("기상 측정","종료");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //점수 계산
    public int ScoreCalculate(long resultTime){
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