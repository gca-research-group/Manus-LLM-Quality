package com.store.model;

import com.store.enums.OrderStatus;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String id;
    private Customer customer;
    private List items;
    private OrderStatus status;
    private double promotionDiscount;
    private double shippingCost;

    public Order(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList();
        this.status = OrderStatus.NEW;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double getPromotionDiscount() {
        return promotionDiscount;
    }

    public void setPromotionDiscount(double promotionDiscount) {
        this.promotionDiscount = promotionDiscount;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public double calculateSubtotal() {
        double subtotal = 0;
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = (OrderItem) items.get(i);
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public double calculateTotal() {
        return calculateSubtotal() - promotionDiscount + shippingCost;
    }
}
