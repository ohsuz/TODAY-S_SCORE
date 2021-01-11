package com.example.ohjeom.models;

import java.util.ArrayList;

public class AccountSummary {
    private int total;
    private ArrayList<Payment> details;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<Payment> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<Payment> details) {
        this.details = details;
    }
}
