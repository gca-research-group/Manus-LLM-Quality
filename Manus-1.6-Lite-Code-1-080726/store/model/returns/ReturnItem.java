package store.model.returns;

import store.model.catalog.Product;

public class ReturnItem {

    private Product product;
    private int quantity;
    private double unitPrice;

    public ReturnItem(Product product, int quantity, double unitPrice) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Returned quantity must be positive.");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative.");
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

    public double getRefundAmount() {
        return unitPrice * quantity;
    }

    public String toString() {
        return "ReturnItem[product=" + product.getId() + ", qty=" + quantity + ", refund=" + getRefundAmount() + "]";
    }
}
