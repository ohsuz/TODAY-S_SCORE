package com.example.ohjeom.models;

public class Weather {

    private String temp;
    private String clouds;
    private String pop;

    public Weather(String temp, String clouds, String pop) {
        this.temp = temp;
        this.clouds = clouds;
        this.pop = pop;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getClouds() {
        return clouds;
    }

    public void setClouds(String clouds) {
        this.clouds = clouds;
    }

    public String getPop() {
        return pop;
    }

    public void setPop(String pop) {
        this.pop = pop;
    }
}