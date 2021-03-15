package com.example.ohjeom.models;

public class Storage {
    private static Diary diary;
    private static Score score;
    private static boolean isScored = false;
    private static boolean isSelected = false;
    private static String[] components = new String[] {"false", "false", "false", "false", "false", "false"};

    public static Diary getDiary() {
        return diary;
    }

    public static void setDiary(Diary diary) {
        Storage.diary = diary;
    }

    public static Score getScore() {
        return score;
    }

    public static void setScore(Score score) {
        Storage.score = score;
    }

    public static boolean isIsScored() {
        return isScored;
    }

    public static void setIsScored(boolean isScored) {
        Storage.isScored = isScored;
    }

    public static boolean isIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(boolean isSelected) {
        Storage.isSelected = isSelected;
    }

    public static String[] getComponents() {
        return components;
    }

    public static void setComponents(String[] components) {
        Storage.components = components;
    }
}
