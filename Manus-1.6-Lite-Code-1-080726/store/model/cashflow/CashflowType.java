package store.model.cashflow;

public final class CashflowType {

    public static final CashflowType INFLOW = new CashflowType("INFLOW");
    public static final CashflowType OUTFLOW = new CashflowType("OUTFLOW");

    private String name;

    private CashflowType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
