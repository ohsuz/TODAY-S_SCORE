package com.example.ohjeom.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Location implements Serializable {
    private String name;
    private double lat;
    private double lng;
    private int locationHour, locationMin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getLocationHour() {
        return locationHour;
    }

    public void setLocationHour(int locationHour) {
        this.locationHour = locationHour;
    }

    public int getLocationMin() {
        return locationMin;
    }

    public void setLocationMin(int locationMin) {
        this.locationMin = locationMin;
    }
}