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
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;
import com.example.ohjeom.models.User;
import com.example.ohjeom.retrofit.ScoreFunctions;

import java.util.Calendar;

public class SleepService extends Service implements SensorEventListener {
    private static final String TAG = "SleepService";
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private int phone = 0, light = 0;
    private Calendar phoneOnCal = null, phoneOffCal = null;
    private Calendar lightOnCal = null, lightOffCal = null;
    private long phoneTime = 0, lightTime = 0;
    private PowerManager pm;
    private boolean run = true;

    public SleepService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent clsIntent = new Intent(SleepService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(SleepService.this, 0, clsIntent, 0);

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
        Log.d(TAG + "수면 측정","시작");

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        ScreenThread thread = new ScreenThread();
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    //화면 On/Off 시간 계산
    private class ScreenThread extends Thread {
        public void run(){
            while (run) {
                boolean isScreenOn = pm.isInteractive();
                if (isScreenOn) {
                    if (phone == 0) {
                        phone = 1;
                        phoneOnCal = Calendar.getInstance();
                        Log.d(TAG + "수면 측정(화면)","켜짐");
                    } else if (phone == 1)
                        phone = 1;
                }
                else {
                    if (phone == 1) {
                        phone = 0;
                        phoneOffCal = Calendar.getInstance();
                        phoneTime += phoneOffCal.getTimeInMillis() / (60 * 1000) - phoneOnCal.getTimeInMillis() / (60 * 1000);
                        Log.d(TAG + "수면 측정(화면)","꺼짐");
                    } else if (phone == 0)
                        phone = 0;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        //기상 시간 설정시에는 기상시간이 되면 종료, 미설정시에는 취침시간 +4시간까지만 측정 후 종료
        run = false;
        sensorManager.unregisterListener(this);

        if(phoneOnCal==null) { //화면이 한번도 안켜진경우
            phoneTime=0;
        } else {
            if(phone ==1) //화면이 켜진채로 측정이 종료된경우
            {
                phoneOffCal = Calendar.getInstance();
                phoneTime += phoneOffCal.getTimeInMillis() / (60 * 1000) - phoneOnCal.getTimeInMillis() / (60 * 1000);
            }
        }

        if (lightOnCal==null) { //빛이 한번도 감지X
            lightTime=0;
        } else {
            if(light ==1) //빛이 감지된 채로 측정이 끝난 경우
            {
                lightOffCal = Calendar.getInstance();
                lightTime += lightOffCal.getTimeInMillis() / (60 * 1000) - lightOnCal.getTimeInMillis() / (60 * 1000);
            }
        }

        int sleepScore = ScoreCalculate(phoneTime,lightTime);

        Log.d(TAG + "화면 켜진 시간:",String.valueOf(phoneTime));
        Log.d(TAG + "빛 감지 시간:", String.valueOf(lightTime));
        Log.d(TAG + "수면 점수", String.valueOf(sleepScore));

        /*
        점수보내기 : sleepScore , 화면 On 총 시간 : phoneTime, 빛 감지 총 시간 : lightTime
         */
        ScoreFunctions scoreFunctions = new ScoreFunctions();
        scoreFunctions.addSleepScore(sleepScore, phoneTime, lightTime);

        /* 업데이트된 점수 가져오기 to HomeAdapter 업데이트*/
        String userID = getSharedPreferences("user", MODE_PRIVATE).getString("id", "aaa");
        ScoreFunctions.getScores(userID, ScoreFunctions.getDate()); // 서버에서 오늘의 날짜에 해당하는 점수 정보를 얻어와서 score 변수에 저장됨
        User.setIsInitialized(true);
        Log.d(TAG + "수면 측정","종료");
        super.onDestroy();
    }

    //빛 감지 시간 계산
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (event.values[0] > 30) {
                if(light == 0) {
                    Log.d(TAG + "수면 측정(빛)", "빛 감지" + String.valueOf(event.values[0]));
                    light = 1;
                    lightOnCal = Calendar.getInstance();
                }
            }
            else{
                if(light == 1){
                    Log.d(TAG + "수면 측정(빛)", "빛 감지" + String.valueOf(event.values[0]));
                    light = 0;
                    lightOffCal = Calendar.getInstance();
                    lightTime += lightOffCal.getTimeInMillis()/ (60 * 1000) - lightOnCal.getTimeInMillis()/ (60 * 1000);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //점수 계산
    public int ScoreCalculate(long phoneTime, long lightTime) {
        int score = 0;
        if(phoneTime <20)
            score += 50;
        else if(20<= phoneTime && phoneTime <40)
            score += 40;
        else if(40<= phoneTime && phoneTime <60)
            score += 30;
        else if(60<= phoneTime && phoneTime <80)
            score += 20;
        else if(80<= phoneTime && phoneTime <120)
            score += 10;
        else
            score += 0;

        if(lightTime <20)
            score += 50;
        else if(20<= lightTime && lightTime <40)
            score += 40;
        else if(40<= lightTime && lightTime <60)
            score += 30;
        else if(60<= lightTime && lightTime <80)
            score += 20;
        else if(80<= lightTime && lightTime <120)
            score += 10;
        else
            score += 0;

        return score;
    }
}