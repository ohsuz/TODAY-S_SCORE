package com.example.ohjeom.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Score implements Parcelable {
    JsonParser parser = new JsonParser();
    /* 받아온 결과 */
    private String userID;
    private String templateName;
    private String date;
    private String walkResult;
    private String wakeResult;
    private String sleepResult;
    private String phoneUsageResult;
    private String locationResult;
    private String paymentResult;
    private int avgScore;

    /* 결과들을 각 정보로 파싱 */
    private int walkScore, walkGoal, walkReal;
    private int wakeScore;
    private String wakeupTime;
    private int sleepScore, phoneTime, lightTime;
    private int phoneUsageScore, totalTime, usageTime;
    private int locationScore;
    private String location;
    private int paymentScore, paymentGoal, paymentReal;

    /* 기타 */
    private String[] components = new String[] {"false", "false", "false", "false", "false", "false"};
    private int[] scores = new int[] {0, 0, 0, 0, 0, 0};
    public static String[] componentNames = {"기상 검사", "수면 검사", "걸음수 검사", "핸드폰 사용량 검사", "장소 도착 검사","소비 검사"};


    protected Score(Parcel in) {
        userID = in.readString();
        templateName = in.readString();
        date = in.readString();
        walkResult = in.readString();
        wakeResult = in.readString();
        sleepResult = in.readString();
        phoneUsageResult = in.readString();
        locationResult = in.readString();
        paymentResult = in.readString();
        avgScore = in.readInt();
        walkScore = in.readInt();
        walkGoal = in.readInt();
        walkReal = in.readInt();
        wakeScore = in.readInt();
        wakeupTime = in.readString();
        sleepScore = in.readInt();
        phoneTime = in.readInt();
        lightTime = in.readInt();
        phoneUsageScore = in.readInt();
        totalTime = in.readInt();
        usageTime = in.readInt();
        locationScore = in.readInt();
        location = in.readString();
        paymentScore = in.readInt();
        paymentGoal = in.readInt();
        paymentReal = in.readInt();
        components = in.createStringArray();
        scores = in.createIntArray();
    }

    public static final Creator<Score> CREATOR = new Creator<Score>() {
        @Override
        public Score createFromParcel(Parcel in) {
            return new Score(in);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userID);
        parcel.writeString(templateName);
        parcel.writeString(date);
        parcel.writeString(walkResult);
        parcel.writeString(wakeResult);
        parcel.writeString(sleepResult);
        parcel.writeString(phoneUsageResult);
        parcel.writeString(locationResult);
        parcel.writeString(paymentResult);
        parcel.writeInt(avgScore);
        parcel.writeInt(walkScore);
        parcel.writeInt(walkGoal);
        parcel.writeInt(walkReal);
        parcel.writeInt(wakeScore);
        parcel.writeString(wakeupTime);
        parcel.writeInt(sleepScore);
        parcel.writeInt(phoneTime);
        parcel.writeInt(lightTime);
        parcel.writeInt(phoneUsageScore);
        parcel.writeInt(totalTime);
        parcel.writeInt(usageTime);
        parcel.writeInt(locationScore);
        parcel.writeString(location);
        parcel.writeInt(paymentScore);
        parcel.writeInt(paymentGoal);
        parcel.writeInt(paymentReal);
        parcel.writeStringArray(components);
        parcel.writeIntArray(scores);
    }


    public void parseInfo() {
        Log.d("@@@@@@@ Score: ", userID + templateName + date);
        if (wakeResult != null) {
            this.components[0] = "true";
            JsonObject wakeObj = (JsonObject)parser.parse(this.wakeResult);
            this.scores[0] = wakeObj.get("score").getAsInt();
            this.wakeScore = wakeObj.get("score").getAsInt();
            this.wakeupTime = wakeObj.get("wakeupTime").getAsString();
        }
        if (sleepResult != null) {
            this.components[1] = "true";
            JsonObject sleepObj = (JsonObject)parser.parse(this.sleepResult);
            this.scores[1] = sleepObj.get("score").getAsInt();
            this.sleepScore = sleepObj.get("score").getAsInt();
            this.phoneTime = sleepObj.get("phoneTime").getAsInt();
            this.lightTime = sleepObj.get("lightTime").getAsInt();
        }
        if (walkResult != null) {
            this.components[2] = "true";
            JsonObject walkObj = (JsonObject)parser.parse(this.walkResult);
            this.scores[2] = walkObj.get("score").getAsInt();
            this.walkScore = walkObj.get("score").getAsInt();
            this.walkGoal = walkObj.get("goal").getAsInt();
            this.walkReal = walkObj.get("real").getAsInt();
        }
        if (phoneUsageResult != null) {
            this.components[3] = "true";
            JsonObject phoneUsageObj = (JsonObject)parser.parse(this.phoneUsageResult);
            this.scores[3] = phoneUsageObj.get("score").getAsInt();
            this.phoneUsageScore = phoneUsageObj.get("score").getAsInt();
            this.totalTime = phoneUsageObj.get("totalTime").getAsInt();
            this.usageTime = phoneUsageObj.get("usageTime").getAsInt();
        }
        if (locationResult != null) {
            this.components[4] = "true";
            JsonObject locationObj = (JsonObject)parser.parse(this.locationResult);
            this.scores[4] = locationObj.get("score").getAsInt();
            this.locationScore = locationObj.get("score").getAsInt();
            this.location = locationObj.get("location").getAsString();
        }
        if (paymentResult != null) {
            this.components[5] = "true";
            JsonObject paymentObj = (JsonObject)parser.parse(this.paymentResult);
            this.scores[5] = paymentObj.get("score").getAsInt();
            this.paymentScore = paymentObj.get("score").getAsInt();
            this.paymentGoal = paymentObj.get("goal").getAsInt();
            this.paymentReal = paymentObj.get("real").getAsInt();
        }
    }

    public int getWalkScore() {
        return walkScore;
    }

    public void setWalkScore(int walkScore) {
        this.walkScore = walkScore;
    }

    public int getWalkGoal() {
        return walkGoal;
    }

    public void setWalkGoal(int walkGoal) {
        this.walkGoal = walkGoal;
    }

    public int getWalkReal() {
        return walkReal;
    }

    public void setWalkReal(int walkReal) {
        this.walkReal = walkReal;
    }

    public String[] getComponents() {
        return components;
    }

    public static String[] getComponentNames() {
        return componentNames;
    }

    public int[] getScores() {
        return scores;
    }
}
