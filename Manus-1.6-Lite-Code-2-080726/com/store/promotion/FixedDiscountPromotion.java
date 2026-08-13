package com.store.promotion;

import com.store.model.Order;
import com.store.enums.LoyaltyTier;

public class FixedDiscountPromotion implements Promotion {
    private double discount;
    private double threshold;
    private LoyaltyTier requiredTier;

    public FixedDiscountPromotion(double discount, double threshold, LoyaltyTier requiredTier) {
        this.discount = discount;
        this.threshold = threshold;
        this.requiredTier = requiredTier;
    }

    public double calculateDiscount(Order order) {
        if (order.calculateSubtotal() >= threshold && order.getCustomer().getLoyaltyTier() == requiredTier) {
            return discount;
        }
        return 0;
    }
}
