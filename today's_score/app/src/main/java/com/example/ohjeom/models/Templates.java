package com.example.ohjeom.models;

import java.util.ArrayList;

public class Templates {
    private static ArrayList<Template> templates = new ArrayList<>();

    public static ArrayList<Template> getTemplates() {
        return templates;
    }

    public void setPrivateTemplates(ArrayList<Template> templates) {
        this.templates = templates;
    }
}
