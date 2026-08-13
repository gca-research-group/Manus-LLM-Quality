package store.model.payment;

public final class PaymentMethod {

    public static final PaymentMethod CASH = new PaymentMethod("CASH");
    public static final PaymentMethod CREDIT_CARD = new PaymentMethod("CREDIT_CARD");
    public static final PaymentMethod BANK_TRANSFER = new PaymentMethod("BANK_TRANSFER");

    private String name;

    private PaymentMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
