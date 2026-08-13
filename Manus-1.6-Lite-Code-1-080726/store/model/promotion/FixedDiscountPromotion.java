package store.model.promotion;

import store.model.order.Order;
import store.model.party.LoyaltyTier;

public class FixedDiscountPromotion implements Promotion {

    private String id;
    private String description;
    private double discountAmount;
    private double subtotalThreshold;
    private LoyaltyTier requiredTier;

    public FixedDiscountPromotion(String id, double discountAmount, double subtotalThreshold, LoyaltyTier requiredTier) {
        if (discountAmount < 0) {
            throw new IllegalArgumentException("Discount value cannot be negative.");
        }
        this.id = id;
        this.discountAmount = discountAmount;
        this.subtotalThreshold = subtotalThreshold;
        this.requiredTier = requiredTier;
        this.description = "Fixed discount of " + discountAmount + " for "
                + (requiredTier != null ? requiredTier.getName() : "any") + " customers with subtotal >= " + subtotalThreshold;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isApplicable(Order order) {
        if (order.getSubtotal() < subtotalThreshold) {
            return false;
        }
        if (requiredTier != null) {
            return order.getCustomer().getLoyaltyTier().getName().equals(requiredTier.getName());
        }
        return true;
    }

    public double calculateDiscount(Order order) {
        if (!isApplicable(order)) {
            return 0.0;
        }
        return discountAmount;
    }
}
