package com.store.promotion;

import com.store.model.Order;

public class PercentagePromotion implements Promotion {
    private double percentage;
    private double threshold;

    public PercentagePromotion(double percentage, double threshold) {
        this.percentage = percentage;
        this.threshold = threshold;
    }

    public double calculateDiscount(Order order) {
        if (order.calculateSubtotal() >= threshold) {
            return order.calculateSubtotal() * (percentage / 100.0);
        }
        return 0;
    }
}
