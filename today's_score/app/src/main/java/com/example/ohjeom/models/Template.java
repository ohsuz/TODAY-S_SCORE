package com.example.ohjeom.models;

import android.content.Context;
import android.content.pm.ResolveInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Template implements Serializable {
    private String templateName;
    private boolean isSelected = false;
    private boolean[] components; // 기상, 수면, 걸음수, 핸드폰, 장소
    public static String[] componentNames = {"기상 검사", "수면 검사", "걸음수 검사", "핸드폰 사용량 검사", "장소 도착 검사","소비 검사"};
    private int walkHour, walkMin, walkCount;
    private int wakeupHour, wakeupMin;
    private int sleepHour, sleepMin;
    private ArrayList<Location> locations;
    private ArrayList<String> appNames;
    private int startHour, startMin, stopHour, stopMin;
    private int money;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public boolean[] getComponents() {
        return components;
    }

    public void setComponents(boolean[] components) {
        this.components = components;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public ArrayList<String> getAppNames() {
        return appNames;
    }

    public void setAppNames(ArrayList<String> appNames) {
        this.appNames = appNames;
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
