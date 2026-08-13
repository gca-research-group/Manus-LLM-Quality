package com.store.model;

import com.store.model.enums.LoyaltyTier;

public class Customer {
    private String id;
    private String name;
    private LoyaltyTier loyaltyTier;

    public Customer(String id, String name, LoyaltyTier loyaltyTier) {
        this.id = id;
        this.name = name;
        this.loyaltyTier = loyaltyTier;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LoyaltyTier getLoyaltyTier() {
        return loyaltyTier;
    }
}