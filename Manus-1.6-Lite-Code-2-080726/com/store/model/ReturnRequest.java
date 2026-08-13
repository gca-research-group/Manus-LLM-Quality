package com.store.model;

import java.util.ArrayList;
import java.util.List;

public class ReturnRequest {
    private String id;
    private Order order;
    private List items;

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

    public double calculateTotalRefund() {
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            ReturnItem item = (ReturnItem) items.get(i);
            total += item.getRefundAmount();
        }
        return total;
    }
}
