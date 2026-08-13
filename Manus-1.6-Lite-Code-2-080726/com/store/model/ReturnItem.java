package com.store.model;

public class ReturnItem {
    private String productId;
    private int quantity;
    private double refundAmount;

    public ReturnItem(String productId, int quantity, double refundAmount) {
        this.productId = productId;
        this.quantity = quantity;
        this.refundAmount = refundAmount;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getRefundAmount() {
        return refundAmount;
    }
}
