package com.store.model;

public abstract class ShippingRule {
    public abstract double calculateShipping(Order order);
}