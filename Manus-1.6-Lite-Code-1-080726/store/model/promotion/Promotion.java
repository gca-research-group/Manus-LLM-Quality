package store.model.promotion;

import store.model.order.Order;

public interface Promotion {

    String getId();

    String getDescription();

    boolean isApplicable(Order order);

    double calculateDiscount(Order order);
}
