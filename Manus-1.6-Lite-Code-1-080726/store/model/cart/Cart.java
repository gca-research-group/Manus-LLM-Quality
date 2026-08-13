package store.model.cart;

import store.model.catalog.Product;
import store.model.party.Customer;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private String id;
    private Customer customer;
    private List items;

    public Cart(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList();
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

    public void addItem(Product product, int quantity) {
        if (!product.isActive()) {
            throw new IllegalArgumentException("Cannot add inactive product " + product.getId() + " to cart.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Cart item quantity must be positive.");
        }
        for (int i = 0; i < items.size(); i++) {
            CartItem existing = (CartItem) items.get(i);
            if (existing.getProduct().getId().equals(product.getId())) {
                existing.setQuantity(existing.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    public void updateItemQuantity(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Cart item quantity must be positive.");
        }
        for (int i = 0; i < items.size(); i++) {
            CartItem item = (CartItem) items.get(i);
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
        throw new IllegalArgumentException("Product " + productId + " not found in cart.");
    }

    public void removeItem(String productId) {
        for (int i = 0; i < items.size(); i++) {
            CartItem item = (CartItem) items.get(i);
            if (item.getProduct().getId().equals(productId)) {
                items.remove(i);
                return;
            }
        }
        throw new IllegalArgumentException("Product " + productId + " not found in cart.");
    }

    public double getSubtotal() {
        double total = 0.0;
        for (int i = 0; i < items.size(); i++) {
            total += ((CartItem) items.get(i)).getSubtotal();
        }
        return total;
    }

    public String toString() {
        return "Cart[id=" + id + ", customer=" + customer.getId() + ", items=" + items.size() + ", subtotal=" + getSubtotal() + "]";
    }
}
