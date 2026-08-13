package com.store.promotion;

import com.store.model.Order;
import com.store.model.OrderItem;
import java.util.List;

public class BuyXGetYPromotion implements Promotion {
    private String productId;
    private int x;
    private int y;

    public BuyXGetYPromotion(String productId, int x, int y) {
        this.productId = productId;
        this.x = x;
        this.y = y;
    }

    public double calculateDiscount(Order order) {
        double discount = 0;
        List items = order.getItems();
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = (OrderItem) items.get(i);
            if (item.getProductId().equals(productId)) {
                int freeSets = item.getQuantity() / (x + y);
                discount += freeSets * y * item.getUnitPrice();
            }
        }
        return discount;
    }
}
