package store.model.promotion;

import store.model.order.Order;

public class PercentagePromotion implements Promotion {

    private String id;
    private String description;
    private double percentage;
    private double subtotalThreshold;

    public PercentagePromotion(String id, double percentage, double subtotalThreshold) {
        this.id = id;
        this.percentage = percentage;
        this.subtotalThreshold = subtotalThreshold;
        this.description = percentage + "% discount for orders with subtotal >= " + subtotalThreshold;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isApplicable(Order order) {
        return order.getSubtotal() >= subtotalThreshold;
    }

    public double calculateDiscount(Order order) {
        if (!isApplicable(order)) {
            return 0.0;
        }
        return order.getSubtotal() * (percentage / 100.0);
    }
}
