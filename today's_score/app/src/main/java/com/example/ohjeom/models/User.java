package com.example.ohjeom.models;

public class User {
    private String id="aaa";
    private static Template curTemplate;
    private static boolean isInitialized = false;
    private static String[] components;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Template getCurTemplate() {
        return curTemplate;
    }

    public static void setCurTemplate(Template curTemplate) {
        User.curTemplate = curTemplate;
    }

    public static boolean isIsInitialized() {
        return isInitialized;
    }

    public static void setIsInitialized(boolean isInitialized) {
        User.isInitialized = isInitialized;
    }

    public static String[] getComponents() {
        return components;
    }

    public static void setComponents(String[] components) {
        User.components = components;
    }
}
