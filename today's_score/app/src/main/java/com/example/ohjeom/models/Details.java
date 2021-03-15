package com.example.ohjeom.models;

public class Details {
    private String component;
    private int score;

    public Details(String component, int score) {
        this.component = component;
        this.score = score;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}