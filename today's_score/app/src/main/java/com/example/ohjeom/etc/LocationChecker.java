package com.example.ohjeom.etc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.example.ohjeom.models.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationChecker {
    private static final String TAG = "aaaaaaaaaaaaaaa";

    /*
    getCurrentAddress
    : 현재 위치의 주소명을 리턴함
    getLocationScore
    : 위치 검사 점수를 리턴함
    getDistance
    : 현재 위치와 목표 위치 사이의 거리를 리턴함
    calDistance
    : 두 점 사이 거리를 계산하는 함수
     */

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
            Toast.makeText(mContext, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(mContext, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(mContext, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0);
    }

    public static int getLocationScore(Context mContext, Location real, Location dest, int tryNum) {
        double distance = getDistance(mContext, real, dest);
        int score = 0;
        if (distance > 0 && distance <= 0.001) {
            switch(tryNum) {
                case 0: // 제시간에 도착 시, 100점
                    score = 100;
                    break;
                case 1: // 30분 후에 도착 시, 70점
                    score = 70;
                    break;
                case 2: // 1시간 후에 도착 시, 50점
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

        String address = LocationChecker.getCurrentAddress(mContext, latitude, longitude);

        real.setName(address.replace("대한민국",""));
        real.setLat(Double.parseDouble(String.format("%.4f", latitude)));
        real.setLng(Double.parseDouble(String.format("%.4f", longitude)));

        distance = LocationChecker.calDistance(real.getLat(), real.getLng(), dest.getLat(), dest.getLng());

        Log.i (TAG,"현재 위치: "+real.getName()+" 위도: "+real.getLat()+" 경도: "+real.getLng());
        Log.i (TAG,"목표 위치: "+dest.getName()+" 위도: "+dest.getLat()+" 경도: "+dest.getLng());
        Log.i (TAG, "거리 차: "+distance);

        return distance;
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
