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
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;
import com.example.ohjeom.etc.GpsTracker;
import com.example.ohjeom.models.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService1 extends Service {
    private static final String TAG = "locationService";
    private static final String ERROR_MSG = "왜안돼!!!!!!!!!!!";
    private Location location;
    final private Location real = new Location();
    Handler handler;
    private int tryNum = 0;

    public LocationService1() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent clsIntent = new Intent(LocationService1.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(LocationService1.this, 0, clsIntent, 0);

        NotificationCompat.Builder clsBuilder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "channel_id";
            NotificationChannel clsChannel = new NotificationChannel(CHANNEL_ID, "서비스 앱", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(clsChannel);

            clsBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
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
        Log.d(ERROR_MSG, "LocationService - OnStartCommand");
        location = (Location) intent.getSerializableExtra("location");

        LocationThread thread = new LocationThread();
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("장소 측정", "종료");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class LocationThread extends Thread {
        public void run() {
            Log.d(TAG, "LocationThread - location: " + location);

            Looper.prepare();
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    Log.d(TAG, "LocationThread - handler  - location: " + location.getName() + ", tryNum=" + tryNum);
                    checkScore(location);
                    tryNum++;
                    if (tryNum <= 3) {
                        handler.sendEmptyMessageDelayed(0, 10000);
                    }
                }
            };

            try {
                Thread.sleep(1000);
                handler.sendEmptyMessageDelayed(0, 10000);
            } catch (InterruptedException e) {
            }
        }
    }

    public void checkScore(Location location) {
        int locationScore = getLocationScore(LocationService1.this, real, location, tryNum);

        if (tryNum < 3 && locationScore == 0) {
            Log.d(TAG, "점수: " + locationScore);
        } else {
            Log.d(TAG, "완벽한 " + locationScore + "점");
            // @@@@ 여기서 점수를 보내면 됨
            //Looper.getMainLooper().quitSafely(); // Looper 종료
            //handler.removeMessages(0);
            Log.d(TAG, "측정 종료~");
        }
    }

    public static int getLocationScore(Context mContext, Location real, Location dest, int tryNum) {
        double distance = getDistance(mContext, real, dest);
        int score = 0;
        if (distance > 0 && distance <= 0.002) { //@@@ 0.001로 바꿀예정
            switch (tryNum) {
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
    public static double getDistance(final Context mContext, final Location real, final Location dest) {
        double distance;
        GpsTracker gpsTracker = new GpsTracker(mContext);

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        Log.d(ERROR_MSG, latitude + ", " + longitude);
        String address = getCurrentAddress(mContext, latitude, longitude);

        real.setName(address.replace("대한민국", ""));
        real.setLat(Double.parseDouble(String.format("%.4f", latitude)));
        real.setLng(Double.parseDouble(String.format("%.4f", longitude)));

        distance = calDistance(real.getLat(), real.getLng(), dest.getLat(), dest.getLng());

        Log.i(TAG, "현재 위치: " + real.getName() + " 위도: " + real.getLat() + " 경도: " + real.getLng());
        Log.i(TAG, "목표 위치: " + dest.getName() + " 위도: " + dest.getLat() + " 경도: " + dest.getLng());
        Log.i(TAG, "거리 차: " + distance);

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
        yd = Math.pow((y1 - y), 2);
        xd = Math.pow((x1 - x), 2);
        d = Math.sqrt(yd + xd);
        return Double.parseDouble(String.format("%.4f", d));
    }
}