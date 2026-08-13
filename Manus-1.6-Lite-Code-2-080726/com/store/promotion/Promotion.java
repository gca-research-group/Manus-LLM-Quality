package com.store.promotion;

import com.store.model.Order;

public interface Promotion {
    double calculateDiscount(Order order);
}
