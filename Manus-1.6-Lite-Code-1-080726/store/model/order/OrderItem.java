package store.model.order;

import store.model.catalog.Product;

public class OrderItem {

    private Product product;
    private int quantity;
    private double unitPrice;

    public OrderItem(Product product, int quantity, double unitPrice) {
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Order item quantity must be positive.");
        }
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    public String toString() {
        return "OrderItem[product=" + product.getId() + ", qty=" + quantity
                + ", unitPrice=" + unitPrice + ", subtotal=" + getSubtotal() + "]";
    }
}
