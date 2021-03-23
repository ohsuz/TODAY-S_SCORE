package com.example.ohjeom.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;
import com.example.ohjeom.etc.GpsTracker;
import com.example.ohjeom.models.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class WeatherService extends Service {
    private static final String TAG = "WeatherService";
    private int startTime;
    private int stopTime;
    private Map<Integer, Weather> weatherList = new HashMap<>();

    public WeatherService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent clsIntent = new Intent(WeatherService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(WeatherService.this, 0, clsIntent, 0);

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
        startTime = intent.getIntExtra("walkStartTime",0);
        stopTime = intent.getIntExtra("walkStopTime",0);

        GpsTracker gpsTracker = new GpsTracker(WeatherService.this);
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        getWeathterData(latitude, longitude);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void getWeathterData(double latitude, double longitude) {
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longitude + "&exclude=current,minutely,daily,alerts&appid=304de19ef60e6ac7a80efb43d6d3fc3e";
        ReceiveWeatherTask receiveUseTask = new ReceiveWeatherTask();
        receiveUseTask.execute(url);
    }

    private class ReceiveWeatherTask extends AsyncTask<String,Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Log.i(TAG + "가져온 결과값",result.toString());

            try {
                JSONArray hourly = (JSONArray) result.get("hourly");
                for (int i=0;i<hourly.length();i++){
                    JSONObject tmp = (JSONObject) hourly.get(i);
                    String dt = tmp.get("dt").toString();
                    Date date = new Date();
                    date.setTime(Long.parseLong(dt)*1000);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);

                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    if (hour < startTime) {
                        continue;
                    }

                    if (hour == stopTime) {
                        if (startTime != stopTime)
                            break;
                    }

                    if (hour > stopTime) {
                        if (startTime == stopTime) {
                            break;
                        }
                    }

                    String temp = String.valueOf((Double.parseDouble(String.valueOf(tmp.get("temp")))-273.15));
                    String clouds = tmp.get("clouds").toString();
                    String pop =  tmp.get("pop").toString();

                    weatherList.put(hour,new Weather(temp,clouds,pop));

                    Log.i(TAG + "가져온 시간", String.valueOf(hour));
                    Log.i(TAG + "가져온 기온", temp);
                    Log.i(TAG + "가져온 햇빛", clouds);
                    Log.i(TAG + "가져온 강수확률", pop);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getGoodWeather();
            super.onPostExecute(result);
        }

        @Override
        protected JSONObject doInBackground(String... datas) {
            try{
                HttpURLConnection conn = (HttpURLConnection) new URL(datas[0]).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    InputStream is = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(is);
                    BufferedReader in = new BufferedReader(reader);

                    String readed;
                    while ((readed=in.readLine()) != null){
                        JSONObject jObect = new JSONObject(readed);
                        return jObect;
                    }
                }
                else {
                    return null;
                }
                return null;
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private void getGoodWeather() {
        Map<Integer, Weather> popList = calculateRain();
        Map<Integer, Weather> tempList = calculateTemp(popList);
        Map<Integer, Weather> goodWeatherList = calculateClouds(tempList);

        Set set = goodWeatherList.keySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            int time = (int) iterator.next();
            Log.d(TAG + "추천 시간대", String.valueOf(time)+"시");
            NotificationWeather(time,goodWeatherList.get(time));
            break;
        }
    }

    private Map<Integer, Weather> calculateRain() {
        int[] rain = {0, 20, 40, 100};
        Set set = weatherList.keySet();
        Iterator iterator = set.iterator();
        for (int i = 0; i < 4; i++) {
            Map<Integer, Weather> popList = new HashMap<>();
            while (iterator.hasNext()) {
                int time = (int) iterator.next();
                if (Double.parseDouble(weatherList.get(time).getPop()) <= rain[i]) {
                    popList.put(time, weatherList.get(time));
                }
            }
            if (!popList.isEmpty())
                return popList;
        }
        return weatherList;
    }

    private Map<Integer, Weather> calculateTemp(Map<Integer,Weather> popList) {
        int[] temp = {5, 0, -10, -100};
        Set set = popList.keySet();
        Iterator iterator = set.iterator();
        for (int i = 0; i < 4; i++) {
            Map<Integer, Weather> tempList = new HashMap<>();
            while (iterator.hasNext()) {
                int time = (int) iterator.next();
                if (Double.parseDouble(popList.get(time).getTemp()) >= temp[i]) {
                    tempList.put(time, popList.get(time));
                }
            }
            if (!tempList.isEmpty())
                return tempList;
        }
        return weatherList;
    }

    private Map<Integer, Weather> calculateClouds(Map<Integer,Weather> tempList) {
        int[] clouds = {0, 30, 60, 90, 100};
        Set set = tempList.keySet();
        Iterator iterator = set.iterator();
        for (int i = 0; i < 4; i++) {
            Map<Integer, Weather> goodWeahterList = new HashMap<>();
            while (iterator.hasNext()) {
                int time = (int) iterator.next();
                if (Double.parseDouble(tempList.get(time).getClouds()) >= clouds[i]
                    || Double.parseDouble(tempList.get(time).getClouds()) < clouds[i+1]) {
                    goodWeahterList.put(time, tempList.get(time));
                }
            }
            if (!goodWeahterList.isEmpty())
                return goodWeahterList;
        }
        return weatherList;
    }

    public void NotificationWeather(int time, Weather weather) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_weather")
                .setSmallIcon(R.drawable.icon_school) //BitMap 이미지 요구
                .setContentTitle("오늘의 점수 : 걷기 좋은 시간대")
                .setContentText("오늘은 " + time + "시에 걷기 좋아요!")
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.icon_school);
            NotificationChannel channel = new NotificationChannel("channel_weather", "오늘의 점수 알리미", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("시간대 추천");
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }else builder.setSmallIcon(R.drawable.icon_school);

        assert notificationManager != null;
        notificationManager.notify(3, builder.build());
    }
}