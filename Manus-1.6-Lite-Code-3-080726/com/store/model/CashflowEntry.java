package com.store.model;

import com.store.model.enums.CashflowEntryType;

public class CashflowEntry {
    private String id;
    private CashflowEntryType type;
    private double amount;
    private String description;

    public CashflowEntry(String id, CashflowEntryType type, double amount, String description) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public CashflowEntryType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}