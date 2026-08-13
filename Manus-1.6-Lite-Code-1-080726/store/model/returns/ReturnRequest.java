package store.model.returns;

import store.model.order.Order;

import java.util.ArrayList;
import java.util.List;

public class ReturnRequest {

    private String id;
    private Order order;
    private List returnItems;
    private ReturnStatus status;

    public ReturnRequest(String id, Order order) {
        this.id = id;
        this.order = order;
        this.returnItems = new ArrayList();
        this.status = ReturnStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public List getReturnItems() {
        return returnItems;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public void addReturnItem(ReturnItem item) {
        returnItems.add(item);
    }

    public double getRefundTotal() {
        double total = 0.0;
        for (int i = 0; i < returnItems.size(); i++) {
            total += ((ReturnItem) returnItems.get(i)).getRefundAmount();
        }
        return total;
    }

    public String toString() {
        return "ReturnRequest[id=" + id + ", orderId=" + order.getId()
                + ", status=" + status + ", refundTotal=" + getRefundTotal() + "]";
    }
}
