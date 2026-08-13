package com.store.shipping;

import com.store.model.Order;

public class PickupShippingRule implements ShippingRule {
    public double calculateShipping(Order order) {
        return 0.0;
    }
}
