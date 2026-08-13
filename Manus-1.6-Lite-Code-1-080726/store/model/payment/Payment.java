package store.model.payment;

public class Payment {

    private String id;
    private String orderId;
    private double amount;
    private PaymentMethod method;

    public Payment(String id, String orderId, double amount, PaymentMethod method) {
        if (amount < 0) {
            throw new IllegalArgumentException("Payment value cannot be negative.");
        }
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public String toString() {
        return "Payment[id=" + id + ", orderId=" + orderId + ", amount=" + amount + ", method=" + method + "]";
    }
}
