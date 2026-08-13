package store.model.returns;

public final class ReturnStatus {

    public static final ReturnStatus PENDING = new ReturnStatus("PENDING");
    public static final ReturnStatus PROCESSED = new ReturnStatus("PROCESSED");

    private String name;

    private ReturnStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
