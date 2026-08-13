package com.store.model;

import java.util.List;

public class BuyXGetYPromotion extends Promotion {
    private String productId;
    private int buyQty;
    private int freeQty;

    public BuyXGetYPromotion(String productId, int buyQty, int freeQty) {
        this.productId = productId;
        this.buyQty = buyQty;
        this.freeQty = freeQty;
    }

    public double calculateDiscount(Order order) {
        double discount = 0;
        List items = order.getItems();
        for (int i = 0; i < items.size(); i++) {
            OrderItem oi = (OrderItem) items.get(i);
            if (oi.getProduct().getId().equals(productId)) {
                int sets = oi.getQuantity() / (buyQty + freeQty);
                discount = sets * freeQty * oi.getUnitPrice();
                break;
            }
        }
        return discount;
    }
}