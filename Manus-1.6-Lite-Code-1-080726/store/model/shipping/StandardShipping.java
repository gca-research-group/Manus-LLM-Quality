package store.model.shipping;

public class StandardShipping implements ShippingRule {

    private double cost;

    public StandardShipping(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Shipping value cannot be negative.");
        }
        this.cost = cost;
    }

    public String getType() {
        return "STANDARD";
    }

    public double getCost() {
        return cost;
    }
}
