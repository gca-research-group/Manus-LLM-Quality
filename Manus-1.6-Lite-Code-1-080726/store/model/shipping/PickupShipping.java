package store.model.shipping;

public class PickupShipping implements ShippingRule {

    public String getType() {
        return "PICKUP";
    }

    public double getCost() {
        return 0.0;
    }
}
