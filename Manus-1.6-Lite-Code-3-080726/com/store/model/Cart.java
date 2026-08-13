package com.store.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private Customer customer;
    private List items;

    public Cart(Customer customer) {
        this.customer = customer;
        this.items = new ArrayList();
    }

    public Customer getCustomer() {
        return customer;
    }

    public List getItems() {
        return items;
    }

    public void addItem(CartItem item) {
        items.add(item);
    }

    public void removeItem(CartItem item) {
        items.remove(item);
    }

    public double calculateSubtotal() {
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            CartItem item = (CartItem) items.get(i);
            total += item.getSubtotal();
        }
        return total;
    }
}