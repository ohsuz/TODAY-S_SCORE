package com.example.ohjeom.models;

public class FriendScore {
    private int component;
    private int score;

    public FriendScore(int component, int score) {
        this.component = component;
        this.score = score;
    }

    public int getComponent() {
        return component;
    }

    public void setComponent(int component) {
        this.component = component;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
