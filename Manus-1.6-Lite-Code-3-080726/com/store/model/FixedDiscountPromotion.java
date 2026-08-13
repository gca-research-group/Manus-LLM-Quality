package com.store.model;

import com.store.model.enums.LoyaltyTier;

public class FixedDiscountPromotion extends Promotion {
    private double discountAmount;
    private double threshold;
    private LoyaltyTier requiredTier;

    public FixedDiscountPromotion(double discountAmount, double threshold, LoyaltyTier requiredTier) {
        this.discountAmount = discountAmount;
        this.threshold = threshold;
        this.requiredTier = requiredTier;
    }

    public double calculateDiscount(Order order) {
        if (order.getSubtotal() >= threshold && order.getCustomer().getLoyaltyTier() == requiredTier) {
            return discountAmount;
        }
        return 0;
    }
}