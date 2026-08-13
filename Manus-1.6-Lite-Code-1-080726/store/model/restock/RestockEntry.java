package store.model.restock;

import store.model.catalog.Product;
import store.model.party.Supplier;

public class RestockEntry {

    private String id;
    private Product product;
    private Supplier supplier;
    private int quantity;
    private double unitCost;

    public RestockEntry(String id, Product product, Supplier supplier, int quantity, double unitCost) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive.");
        }
        if (unitCost < 0) {
            throw new IllegalArgumentException("Restock unit cost cannot be negative.");
        }
        this.id = id;
        this.product = product;
        this.supplier = supplier;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }

    public String getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public double getTotalCost() {
        return unitCost * quantity;
    }

    public String toString() {
        return "RestockEntry[id=" + id + ", product=" + product.getId()
                + ", supplier=" + supplier.getId() + ", qty=" + quantity
                + ", unitCost=" + unitCost + ", totalCost=" + getTotalCost() + "]";
    }
}
