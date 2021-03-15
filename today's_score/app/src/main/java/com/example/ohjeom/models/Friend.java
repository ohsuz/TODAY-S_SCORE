package com.example.ohjeom.models;

public class Friend {
    private int photo;
    private String name;
    private String intro;
    private boolean use;

    public Friend(int photo, String name, String intro, boolean use) {
        this.photo = photo;
        this.name = name;
        this.intro = intro;
        this.use = use;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }
}
