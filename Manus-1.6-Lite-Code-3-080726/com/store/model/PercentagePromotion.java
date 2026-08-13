package com.store.model;

public class PercentagePromotion extends Promotion {
    private double percentage;
    private double threshold;

    public PercentagePromotion(double percentage, double threshold) {
        this.percentage = percentage;
        this.threshold = threshold;
    }

    public double calculateDiscount(Order order) {
        if (order.getSubtotal() >= threshold) {
            return order.getSubtotal() * (percentage / 100.0);
        }
        return 0;
    }
}