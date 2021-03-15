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
import com.example.ohjeom.models.User;
import com.example.ohjeom.retrofit.ScoreFunctions;

public class WalkService extends Service implements SensorEventListener {
    private static final String TAG = "WalkService";
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
        Log.d(TAG + "운동 측정","시작");
        walkCount=intent.getIntExtra("walkCount",1);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this,stepDetectorSensor,SensorManager.SENSOR_DELAY_UI);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            if(event.values[0] == 1.0f){
                count++;
                Log.d(TAG + "운동 측정","걸음 수 증가"+count);
            }
            if(count == walkCount) {
                stopSelf();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onDestroy() {
        //측정 종료 시간이 되면 종료
        sensorManager.unregisterListener(this);
        int walkScore = Math.round(count / walkCount * 100);

        Log.d(TAG + "측정 걸음수:", String.valueOf(count));
        Log.d(TAG + "운동 점수:", String.valueOf(walkScore));

        /* 서버에 점수 저장 */
        ScoreFunctions scoreFunctions = new ScoreFunctions();
        scoreFunctions.addWalkScore(walkScore, walkCount, count);

        /* 업데이트된 점수 가져오기 to HomeAdapter 업데이트*/
        String userID = getSharedPreferences("user", MODE_PRIVATE).getString("id", "aaa");
        ScoreFunctions.getScores(userID, ScoreFunctions.getDate()); // 서버에서 오늘의 날짜에 해당하는 점수 정보를 얻어와서 score 변수에 저장됨
        User.setIsInitialized(true);
        Log.d(TAG + "운동 측정","종료");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}