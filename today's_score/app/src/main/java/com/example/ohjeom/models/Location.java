package com.example.ohjeom.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Locale;

public class Location implements Parcelable {
    private String name;
    private double lat, lng;
    private int locationHour, locationMin;

    public Location(String name, double lat, double lng, int locationHour, int locationMin) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.locationHour = locationHour;
        this.locationMin = locationMin;
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeInt(locationHour);
        parcel.writeInt(locationMin);
    }

    private Location(Parcel source){
        name = source.readString();
        lat = source.readDouble();
        lng = source.readDouble();
        locationHour = source.readInt();
        locationMin = source.readInt();
    }
}