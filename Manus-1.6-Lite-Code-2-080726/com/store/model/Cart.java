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

    public double calculateSubtotal() {
        double subtotal = 0;
        for (int i = 0; i < items.size(); i++) {
            CartItem item = (CartItem) items.get(i);
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }
}
