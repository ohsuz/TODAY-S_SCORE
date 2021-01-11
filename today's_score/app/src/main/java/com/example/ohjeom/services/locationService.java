package com.example.ohjeom.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;
import com.example.ohjeom.etc.GpsTracker;
import com.example.ohjeom.models.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class locationService extends Service {

    private static final String TAG = "장소";

    private int month, day;
    private ArrayList<Location> locations;
    private Location real = new Location();

    public locationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent clsIntent = new Intent(locationService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(locationService.this, 0, clsIntent, 0);

        NotificationCompat.Builder clsBuilder;
        if( Build.VERSION.SDK_INT >= 26 )
        {
            String CHANNEL_ID = "channel_id";
            NotificationChannel clsChannel = new NotificationChannel(CHANNEL_ID, "서비스 앱", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(clsChannel);

            clsBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
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
        Log.d("야!!","왜안돼요!!");
        locations = (ArrayList<Location>) intent.getSerializableExtra("locations");
        month = intent.getIntExtra("month",0);
        day = intent.getIntExtra("day",0);
        LocationThread thread = new LocationThread();
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("장소 측정","종료");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class LocationThread extends Thread {
        private static final String TAG = "Location Thread";
        public void run(){
            for(Location s:locations){

                Calendar locationCal = Calendar.getInstance();
                locationCal.set(Calendar.HOUR_OF_DAY, s.getLocationHour());
                locationCal.set(Calendar.MINUTE, s.getLocationMin());
                locationCal.set(Calendar.SECOND, 0);

                Timer l_timer = new Timer();

                //타이머 설정
                Date locationTime = new Date(locationCal.getTimeInMillis());

                l_timer.schedule(new LocationTimer(l_timer,s),locationTime,10000);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    class LocationTimer extends TimerTask {

        private Timer timer;
        private Location location;
        private int tryNum;

        LocationTimer(Timer timer,Location location)
        {
            this.timer = timer;
            tryNum = 0;
            this.location = location;
        }

        @Override
        public void run(){
            int locationScore = getLocationScore(locationService.this, real, location, tryNum);

            if (tryNum <2 && locationScore == 0) {
                Log.d(TAG, "점수: "+locationScore);
                tryNum++;
            } else {
                Log.d(TAG, "완벽한 "+locationScore+"점");
                // @@@@ 여기서 점수를 보내면 됨
                timer.cancel();
                Log.d(TAG, "측정 종료~");
            }
        }
    }

    public static int getLocationScore(Context mContext, Location real, Location dest, int tryNum) {
        double distance = getDistance(mContext, real, dest);
        int score = 0;
        if (distance > 0 && distance <= 0.002) { //@@@ 0.001로 바꿀예정
            switch(tryNum) {
                case 0: //제시간에 도착 시, 100점
                    score = 100;
                    break;
                case 1: //30분 후에 도착 시, 70점
                    score = 70;
                    break;
                case 2: //1시간 후에 도착 시, 50점
                    score = 50;
                    break;
            }
        } else {
            score = 0;
        }
        return score;
    }

    @SuppressLint("DefaultLocale")
    public static double getDistance(Context mContext, Location real, Location dest) {
        double distance;
        GpsTracker gpsTracker = new GpsTracker(mContext);

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        Log.d("ㅡㅡ",latitude+","+longitude);
        String address = getCurrentAddress(mContext, latitude, longitude);

        real.setName(address.replace("대한민국",""));
        real.setLat(Double.parseDouble(String.format("%.4f", latitude)));
        real.setLng(Double.parseDouble(String.format("%.4f", longitude)));

        distance = calDistance(real.getLat(), real.getLng(), dest.getLat(), dest.getLng());

        Log.i (TAG,"현재 위치: "+real.getName()+" 위도: "+real.getLat()+" 경도: "+real.getLng());
        Log.i (TAG,"목표 위치: "+dest.getName()+" 위도: "+dest.getLat()+" 경도: "+dest.getLng());
        Log.i (TAG, "거리 차: "+distance);

        return distance;
    }

    public static String getCurrentAddress(Context mContext, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            //Toast.makeText(mContext, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            //Toast.makeText(mContext, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            //Toast.makeText(mContext, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0);
    }

    @SuppressLint("DefaultLocale")
    static double calDistance(double x, double y, double x1, double y1) {
        double d;
        double xd, yd;
        yd = Math.pow((y1-y),2);
        xd = Math.pow((x1-x),2);
        d = Math.sqrt(yd+xd);
        return Double.parseDouble(String.format("%.4f", d));
    }
}