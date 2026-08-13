package store.model.party;

public final class LoyaltyTier {

    public static final LoyaltyTier REGULAR = new LoyaltyTier("REGULAR");
    public static final LoyaltyTier SILVER = new LoyaltyTier("SILVER");
    public static final LoyaltyTier GOLD = new LoyaltyTier("GOLD");

    private String name;

    private LoyaltyTier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
