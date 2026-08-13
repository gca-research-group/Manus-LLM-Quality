package com.store.model;

public class FlatRateShipping extends ShippingRule {
    private double rate;

    public FlatRateShipping(double rate) {
        this.rate = rate;
    }

    public double calculateShipping(Order order) {
        return rate;
    }
}