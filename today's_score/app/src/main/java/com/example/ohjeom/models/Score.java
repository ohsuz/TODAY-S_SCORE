package com.example.ohjeom.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Score {
    JsonParser parser = new JsonParser();
    /* 받아온 결과 */
    private String userID;
    private String templateName;
    private String date;
    private String walkResult;
    private String wakeResult;
    private String sleepResult;
    private String phoneUsageResult;
    private String locationResult1;
    private String locationResult2;
    private String paymentResult;
    private int avgScore;

    /* 결과들을 각 정보로 파싱 */
    private int walkScore, walkGoal, walkReal;
    private int wakeScore;
    private String wakeupTime;
    private int sleepScore, phoneTime, lightTime;
    private int phoneUsageScore, totalTime, usageTime;
    private int locationScore, locationScore1, locationScore2;
    private String location1, location2;
    private int paymentScore, paymentGoal, paymentReal;

    /* 기타 */
    private String[] components = new String[] {"false", "false", "false", "false", "false", "false"};
    private int[] scores = new int[] {0, 0, 0, 0, 0, 0};
    public static String[] componentNames = {"기상 검사", "수면 검사", "걸음수 검사", "핸드폰 사용량 검사", "장소 도착 검사","소비 검사"};


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
        if (locationResult1 != null) {
            this.components[4] = "true";
            JsonObject locationObj = (JsonObject)parser.parse(this.locationResult1);
            this.locationScore1 = locationObj.get("score").getAsInt();
            this.location1 = locationObj.get("location").getAsString();
            int cnt = 1;
            this.locationScore = locationScore1;
            if (locationResult2 != null) {
                JsonObject locationObj2 = (JsonObject)parser.parse(this.locationResult2);
                this.locationScore2 = locationObj2.get("score").getAsInt();
                this.location2 = locationObj2.get("location").getAsString();
                this.locationScore += this.locationScore2;
                cnt ++;
            }
            Log.d("0323 - Score", locationScore+", "+cnt);
            this.scores[4]  = locationScore / cnt;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWalkResult() {
        return walkResult;
    }

    public void setWalkResult(String walkResult) {
        this.walkResult = walkResult;
    }

    public String getWakeResult() {
        return wakeResult;
    }

    public void setWakeResult(String wakeResult) {
        this.wakeResult = wakeResult;
    }

    public String getSleepResult() {
        return sleepResult;
    }

    public void setSleepResult(String sleepResult) {
        this.sleepResult = sleepResult;
    }

    public String getPhoneUsageResult() {
        return phoneUsageResult;
    }

    public void setPhoneUsageResult(String phoneUsageResult) {
        this.phoneUsageResult = phoneUsageResult;
    }

    public String getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(String paymentResult) {
        this.paymentResult = paymentResult;
    }

    public int getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(int avgScore) {
        this.avgScore = avgScore;
    }

    public int getWakeScore() {
        return wakeScore;
    }

    public void setWakeScore(int wakeScore) {
        this.wakeScore = wakeScore;
    }

    public String getWakeupTime() {
        return wakeupTime;
    }

    public void setWakeupTime(String wakeupTime) {
        this.wakeupTime = wakeupTime;
    }

    public int getSleepScore() {
        return sleepScore;
    }

    public void setSleepScore(int sleepScore) {
        this.sleepScore = sleepScore;
    }

    public int getPhoneTime() {
        return phoneTime;
    }

    public void setPhoneTime(int phoneTime) {
        this.phoneTime = phoneTime;
    }

    public int getLightTime() {
        return lightTime;
    }

    public void setLightTime(int lightTime) {
        this.lightTime = lightTime;
    }

    public int getPhoneUsageScore() {
        return phoneUsageScore;
    }

    public void setPhoneUsageScore(int phoneUsageScore) {
        this.phoneUsageScore = phoneUsageScore;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(int usageTime) {
        this.usageTime = usageTime;
    }

    public int getPaymentScore() {
        return paymentScore;
    }

    public void setPaymentScore(int paymentScore) {
        this.paymentScore = paymentScore;
    }

    public int getPaymentGoal() {
        return paymentGoal;
    }

    public void setPaymentGoal(int paymentGoal) {
        this.paymentGoal = paymentGoal;
    }

    public int getPaymentReal() {
        return paymentReal;
    }

    public void setPaymentReal(int paymentReal) {
        this.paymentReal = paymentReal;
    }

    public void setComponents(String[] components) {
        this.components = components;
    }

    public void setScores(int[] scores) {
        this.scores = scores;
    }
}
