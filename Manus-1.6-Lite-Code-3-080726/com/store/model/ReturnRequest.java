package com.store.model;

import java.util.ArrayList;
import java.util.List;

public class ReturnRequest {
    private String id;
    private Order order;
    private List items;
    private double refundTotal;

    public ReturnRequest(String id, Order order) {
        this.id = id;
        this.order = order;
        this.items = new ArrayList();
    }

    public String getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public List getItems() {
        return items;
    }

    public void addItem(ReturnItem item) {
        items.add(item);
    }

    public double getRefundTotal() {
        return refundTotal;
    }

    public void setRefundTotal(double refundTotal) {
        this.refundTotal = refundTotal;
    }
}