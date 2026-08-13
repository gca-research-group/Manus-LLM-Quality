package com.store.model;

import com.store.enums.CashFlowType;

public class CashFlowEntry {
    private String description;
    private double amount;
    private CashFlowType type;

    public CashFlowEntry(String description, double amount, CashFlowType type) {
        this.description = description;
        this.amount = amount;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public CashFlowType getType() {
        return type;
    }
}
