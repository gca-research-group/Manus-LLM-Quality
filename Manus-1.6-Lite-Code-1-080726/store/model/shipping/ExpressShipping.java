package store.model.shipping;

public class ExpressShipping implements ShippingRule {

    private double cost;

    public ExpressShipping(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Shipping value cannot be negative.");
        }
        this.cost = cost;
    }

    public String getType() {
        return "EXPRESS";
    }

    public double getCost() {
        return cost;
    }
}
