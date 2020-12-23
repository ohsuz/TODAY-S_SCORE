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

public class walkService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private int count = 0, walkCount;
    public walkService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("운동 측정","시작");
        walkCount=intent.getIntExtra("walkCount",1);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this,stepDetectorSensor,SensorManager.SENSOR_DELAY_UI);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            if(event.values[0]==1.0f){
                count++;
                Log.d("운동 측정","걸음 수 증가"+count);
            }

            if(count==walkCount) {
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

        int walk_score = count/walkCount*100;

        Log.d("측정 걸음수:", String.valueOf(count));
        Log.d("운동 점수:", String.valueOf(walk_score));

        /*
        점수 보내기
         */
        Log.d("운동 측정","종료");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}