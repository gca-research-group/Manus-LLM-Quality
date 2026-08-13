package store.model.order;

import store.model.party.Customer;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private String id;
    private Customer customer;
    private List items;
    private OrderStatus status;
    private double promotionDiscount;
    private double shippingCost;
    private String shippingType;

    public Order(String id, Customer customer, List items) {
        if (items == null || items.size() == 0) {
            throw new IllegalArgumentException("An order must contain at least one item.");
        }
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList(items);
        this.status = OrderStatus.PENDING;
        this.promotionDiscount = 0.0;
        this.shippingCost = 0.0;
        this.shippingType = "NONE";
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double getPromotionDiscount() {
        return promotionDiscount;
    }

    public void setPromotionDiscount(double promotionDiscount) {
        if (promotionDiscount < 0) {
            throw new IllegalArgumentException("Discount value cannot be negative.");
        }
        this.promotionDiscount = promotionDiscount;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        if (shippingCost < 0) {
            throw new IllegalArgumentException("Shipping value cannot be negative.");
        }
        this.shippingCost = shippingCost;
    }

    public String getShippingType() {
        return shippingType;
    }

    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
    }

    public double getSubtotal() {
        double total = 0.0;
        for (int i = 0; i < items.size(); i++) {
            total += ((OrderItem) items.get(i)).getSubtotal();
        }
        return total;
    }

    public double getFinalTotal() {
        double total = getSubtotal() - promotionDiscount + shippingCost;
        if (total < 0) {
            return 0.0;
        }
        return total;
    }

    public String toString() {
        return "Order[id=" + id + ", customer=" + customer.getId()
                + ", status=" + status + ", subtotal=" + getSubtotal()
                + ", discount=" + promotionDiscount + ", shipping=" + shippingCost
                + ", total=" + getFinalTotal() + "]";
    }
}
