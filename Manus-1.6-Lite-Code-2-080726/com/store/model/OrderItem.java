package com.store.model;

public class OrderItem {
    private String productId;
    private String productName;
    private double unitPrice;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.unitPrice = product.getUnitPrice();
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }
}
