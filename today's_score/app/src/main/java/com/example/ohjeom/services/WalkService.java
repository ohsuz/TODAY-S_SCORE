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

public class WalkService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private int count = 0, walkCount;
    public WalkService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent clsIntent = new Intent(WalkService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(WalkService.this, 0, clsIntent, 0);

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