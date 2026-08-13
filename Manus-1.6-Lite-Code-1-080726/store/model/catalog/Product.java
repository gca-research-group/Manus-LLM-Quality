package store.model.catalog;

public class Product {

    private String id;
    private String name;
    private double unitPrice;
    private Category category;
    private boolean active;
    private int stock;

    public Product(String id, String name, double unitPrice, Category category) {
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative.");
        }
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.category = category;
        this.active = true;
        this.stock = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getStock() {
        return stock;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to increase must be positive.");
        }
        this.stock += quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to decrease must be positive.");
        }
        if (this.stock - quantity < 0) {
            throw new IllegalStateException("Stock cannot become negative for product " + id + ".");
        }
        this.stock -= quantity;
    }

    public String toString() {
        return "Product[id=" + id + ", name=" + name + ", price=" + unitPrice
                + ", category=" + category.getName() + ", active=" + active + ", stock=" + stock + "]";
    }
}
