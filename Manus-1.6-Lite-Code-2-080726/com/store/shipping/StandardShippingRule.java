package com.store.shipping;

import com.store.model.Order;

public class StandardShippingRule implements ShippingRule {
    private double cost;

    public StandardShippingRule(double cost) {
        this.cost = cost;
    }

    public double calculateShipping(Order order) {
        return cost;
    }
}
