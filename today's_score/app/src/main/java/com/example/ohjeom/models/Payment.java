package com.example.ohjeom.models;

public class Payment {
    private String usage;
    private int amount;

    public Payment(String usage, int amount) {
        this.usage = usage;
        this.amount = amount;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
