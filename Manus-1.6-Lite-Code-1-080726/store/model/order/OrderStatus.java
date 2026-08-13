package store.model.order;

public final class OrderStatus {

    public static final OrderStatus PENDING = new OrderStatus("PENDING");
    public static final OrderStatus PAID = new OrderStatus("PAID");
    public static final OrderStatus CANCELLED = new OrderStatus("CANCELLED");
    public static final OrderStatus RETURNED = new OrderStatus("RETURNED");

    private String name;

    private OrderStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
