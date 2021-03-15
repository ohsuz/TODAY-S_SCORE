package com.example.ohjeom.models;

import java.util.Date;

public class SelectedDate {
    public static Date selectedDate;

    public SelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    public static Date getSelectedDate() {
        return selectedDate;
    }

    public static void setSelectedDate(Date selectedDate) {
        SelectedDate.selectedDate = selectedDate;
    }
}
