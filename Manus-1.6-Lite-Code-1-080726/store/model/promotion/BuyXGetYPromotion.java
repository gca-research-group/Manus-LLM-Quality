package store.model.promotion;

import store.model.order.Order;
import store.model.order.OrderItem;

import java.util.List;

public class BuyXGetYPromotion implements Promotion {

    private String id;
    private String description;
    private String productId;
    private int buyQuantity;
    private int freeQuantity;

    public BuyXGetYPromotion(String id, String productId, int buyQuantity, int freeQuantity) {
        this.id = id;
        this.productId = productId;
        this.buyQuantity = buyQuantity;
        this.freeQuantity = freeQuantity;
        this.description = "Buy " + buyQuantity + " get " + freeQuantity + " free for product " + productId;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isApplicable(Order order) {
        List items = order.getItems();
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = (OrderItem) items.get(i);
            if (item.getProduct().getId().equals(productId) && item.getQuantity() >= buyQuantity) {
                return true;
            }
        }
        return false;
    }

    public double calculateDiscount(Order order) {
        if (!isApplicable(order)) {
            return 0.0;
        }
        List items = order.getItems();
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = (OrderItem) items.get(i);
            if (item.getProduct().getId().equals(productId)) {
                int sets = item.getQuantity() / buyQuantity;
                int totalFree = sets * freeQuantity;
                return totalFree * item.getUnitPrice();
            }
        }
        return 0.0;
    }
}
