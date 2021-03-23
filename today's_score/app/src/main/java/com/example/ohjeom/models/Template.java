package com.example.ohjeom.models;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Template implements Parcelable {
    JsonParser parser = new JsonParser();
    /* 받아온 결과 */
    private String nameResult;
    private String isSelectedResult;
    private String moneyResult;
    private String componentsResult;
    private String walkResult;
    private String sleepResult;
    private String locationResult;
    private String appResult;
    private String wakeResult;

    /* 결과들을 각 정보로 파싱 */
    private String[] components; // 기상, 수면, 걸음수, 핸드폰, 장소, 소비
    private int walkHour, walkMin, walkCount;
    private int wakeupHour, wakeupMin;
    private int sleepHour, sleepMin;
    private ArrayList<Location> locations = new ArrayList<>();
    private String[] appNames;
    private int startHour, startMin, stopHour, stopMin;

    public Template() {
    }

    public Template(Parcel in) {
        nameResult = in.readString();
        isSelectedResult = in.readString();
        moneyResult = in.readString();
        componentsResult = in.readString();
        walkResult = in.readString();
        sleepResult = in.readString();
        locationResult = in.readString();
        appResult = in.readString();
        wakeResult = in.readString();
        components = in.createStringArray();
        walkHour = in.readInt();
        walkMin = in.readInt();
        walkCount = in.readInt();
        wakeupHour = in.readInt();
        wakeupMin = in.readInt();
        sleepHour = in.readInt();
        sleepMin = in.readInt();
        appNames = in.createStringArray();
        startHour = in.readInt();
        startMin = in.readInt();
        stopHour = in.readInt();
        stopMin = in.readInt();
        in.readTypedList(locations, Location.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameResult);
        dest.writeString(isSelectedResult);
        dest.writeString(moneyResult);
        dest.writeString(componentsResult);
        dest.writeString(walkResult);
        dest.writeString(sleepResult);
        dest.writeString(locationResult);
        dest.writeString(appResult);
        dest.writeString(wakeResult);
        dest.writeStringArray(components);
        dest.writeInt(walkHour);
        dest.writeInt(walkMin);
        dest.writeInt(walkCount);
        dest.writeInt(wakeupHour);
        dest.writeInt(wakeupMin);
        dest.writeInt(sleepHour);
        dest.writeInt(sleepMin);
        dest.writeStringArray(appNames);
        dest.writeInt(startHour);
        dest.writeInt(startMin);
        dest.writeInt(stopHour);
        dest.writeInt(stopMin);
        dest.writeTypedList(locations);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Template> CREATOR = new Creator<Template>() {
        @Override
        public Template createFromParcel(Parcel in) {
            return new Template(in);
        }

        @Override
        public Template[] newArray(int size) {
            return new Template[size];
        }
    };

    public void parseInfo() {
        components = componentsResult.substring(1, componentsResult.length()-1).split(","); // 기상 수면 걸음수 핸드폰사용량 장소도착

        for (int i=0; i < components.length; i++) {
            components[i] = components[i].trim();
        }

        if (components[0].equals("true")) {
            JsonObject wakeObj = (JsonObject)parser.parse(this.wakeResult);
            this.wakeupHour = wakeObj.get("h").getAsInt();
            this.wakeupMin = wakeObj.get("m").getAsInt();
        }
        if (components[1].equals("true")) {
            JsonObject sleepObj = (JsonObject)parser.parse(this.sleepResult);
            this.sleepHour = sleepObj.get("h").getAsInt();
            this.sleepMin = sleepObj.get("m").getAsInt();
        }
        if (components[2].equals("true")) {
            JsonObject walkObj = (JsonObject)parser.parse(this.walkResult);
            this.walkCount = walkObj.get("goal").getAsInt();
            this.walkHour = walkObj.get("h").getAsInt();
            this.walkMin = walkObj.get("m").getAsInt();
        }
        if (components[3].equals("true")) {
            JsonObject appObj = (JsonObject)parser.parse(this.appResult);
            this.appNames = appObj.get("appNames").getAsString().substring(1, appObj.get("appNames").getAsString().length()-1).split(",");
            for (int i=0; i < appNames.length; i++) {
                appNames[i] = appNames[i].trim();
            }
            this.startHour = appObj.get("startH").getAsInt();
            this.startMin = appObj.get("startM").getAsInt();
            this.stopHour = appObj.get("stopH").getAsInt();
            this.stopMin = appObj.get("stopM").getAsInt();
        }
        if (components[4].equals("true")) {
            JsonArray locationArr = (JsonArray)parser.parse(this.locationResult);
            for (int i = 0; i < locationArr.size(); i++) {
                JsonObject locationObj = (JsonObject) locationArr.get(i);
                Location location = new Location(locationObj.get("locationName").getAsString().trim(),locationObj.get("lat").getAsDouble(),
                        locationObj.get("lng").getAsDouble(),locationObj.get("h").getAsInt(),locationObj.get("m").getAsInt());
                this.locations.add(location);
            }
        }
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public String[] getAppNames() {
        return appNames;
    }

    public String getNameResult() {
        return nameResult;
    }

    public void setNameResult(String nameResult) {
        this.nameResult = nameResult;
    }

    public String getIsSelectedResult() {
        return isSelectedResult;
    }

    public void setIsSelectedResult(String isSelectedResult) {
        this.isSelectedResult = isSelectedResult;
    }

    public String getMoneyResult() {
        return moneyResult;
    }

    public void setMoneyResult(String moneyResult) {
        this.moneyResult = moneyResult;
    }

    public String getComponentsResult() {
        return componentsResult;
    }

    public void setComponentsResult(String componentsResult) {
        this.componentsResult = componentsResult;
    }

    public String getWalkResult() {
        return walkResult;
    }

    public void setWalkResult(String walkResult) {
        this.walkResult = walkResult;
    }

    public String getSleepResult() {
        return sleepResult;
    }

    public void setSleepResult(String sleepResult) {
        this.sleepResult = sleepResult;
    }

    public String getLocationResult() {
        return locationResult;
    }

    public void setLocationResult(String locationResult) {
        this.locationResult = locationResult;
    }

    public String getAppResult() {
        return appResult;
    }

    public void setAppResult(String appResult) {
        this.appResult = appResult;
    }

    public String[] getComponents() {
        return components;
    }


    public int getWalkHour() {
        return walkHour;
    }

    public void setWalkHour(int walkHour) {
        this.walkHour = walkHour;
    }

    public int getWalkMin() {
        return walkMin;
    }

    public void setWalkMin(int walkMin) {
        this.walkMin = walkMin;
    }

    public int getWalkCount() {
        return walkCount;
    }

    public void setWalkCount(int walkCount) {
        this.walkCount = walkCount;
    }

    public int getWakeupHour() {
        return wakeupHour;
    }

    public void setWakeupHour(int wakeupHour) {
        this.wakeupHour = wakeupHour;
    }

    public int getWakeupMin() {
        return wakeupMin;
    }

    public void setWakeupMin(int wakeupMin) {
        this.wakeupMin = wakeupMin;
    }

    public int getSleepHour() {
        return sleepHour;
    }

    public void setSleepHour(int sleepHour) {
        this.sleepHour = sleepHour;
    }

    public int getSleepMin() {
        return sleepMin;
    }

    public void setSleepMin(int sleepMin) {
        this.sleepMin = sleepMin;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getStopHour() {
        return stopHour;
    }

    public void setStopHour(int stopHour) {
        this.stopHour = stopHour;
    }

    public int getStopMin() {
        return stopMin;
    }

    public void setStopMin(int stopMin) {
        this.stopMin = stopMin;
    }
}
