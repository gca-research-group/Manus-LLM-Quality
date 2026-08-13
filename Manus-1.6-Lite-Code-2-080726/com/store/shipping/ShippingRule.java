package com.store.shipping;

import com.store.model.Order;

public interface ShippingRule {
    double calculateShipping(Order order);
}
