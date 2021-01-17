package com.example.ohjeom.models;

import java.util.ArrayList;

public class App {
    private ArrayList<String> name;
    private int startHour, startMin, stopHour, stopMin;

    public ArrayList<String> getName() {
        return name;
    }

    public void setName(ArrayList<String> name) {
        this.name = name;
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
