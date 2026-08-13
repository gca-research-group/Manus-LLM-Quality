package store.report;

import store.model.cashflow.CashflowEntry;
import store.model.cashflow.CashflowType;
import store.model.catalog.Product;
import store.model.order.Order;
import store.model.order.OrderItem;
import store.model.order.OrderStatus;
import store.model.restock.RestockEntry;
import store.model.returns.ReturnItem;
import store.model.returns.ReturnRequest;
import store.service.DataStore;

import java.util.ArrayList;
import java.util.List;

public class ReportService {

    private DataStore dataStore;

    public ReportService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void printStockReport() {
        System.out.println("=== STOCK REPORT ===");
        List products = dataStore.getProducts();
        for (int i = 0; i < products.size(); i++) {
            Product p = (Product) products.get(i);
            System.out.println("  " + p.getId() + " | " + p.getName() + " | Stock: " + p.getStock());
        }
        System.out.println("====================");
    }

    public void printSalesByProductReport() {
        System.out.println("=== SALES BY PRODUCT REPORT ===");
        List products = dataStore.getProducts();
        List orders = dataStore.getOrders();
        for (int i = 0; i < products.size(); i++) {
            Product p = (Product) products.get(i);
            int totalSold = 0;
            for (int j = 0; j < orders.size(); j++) {
                Order o = (Order) orders.get(j);
                if (o.getStatus() == OrderStatus.CANCELLED) {
                    continue;
                }
                List items = o.getItems();
                for (int k = 0; k < items.size(); k++) {
                    OrderItem oi = (OrderItem) items.get(k);
                    if (oi.getProduct().getId().equals(p.getId())) {
                        totalSold += oi.getQuantity();
                    }
                }
            }
            System.out.println("  " + p.getId() + " | " + p.getName() + " | Sold: " + totalSold);
        }
        System.out.println("================================");
    }

    public void printOrdersByStatusReport() {
        System.out.println("=== ORDERS BY STATUS REPORT ===");
        List orders = dataStore.getOrders();
        int pending = 0, paid = 0, cancelled = 0, returned = 0;
        for (int i = 0; i < orders.size(); i++) {
            Order o = (Order) orders.get(i);
            if (o.getStatus() == OrderStatus.PENDING) {
                pending++;
            } else if (o.getStatus() == OrderStatus.PAID) {
                paid++;
            } else if (o.getStatus() == OrderStatus.CANCELLED) {
                cancelled++;
            } else if (o.getStatus() == OrderStatus.RETURNED) {
                returned++;
            }
        }
        System.out.println("  PENDING: " + pending);
        System.out.println("  PAID: " + paid);
        System.out.println("  CANCELLED: " + cancelled);
        System.out.println("  RETURNED: " + returned);
        System.out.println("================================");
    }

    public void printCashflowSummaryReport() {
        System.out.println("=== CASHFLOW SUMMARY REPORT ===");
        List entries = dataStore.getCashflowEntries();
        double totalInflow = 0.0;
        double totalOutflow = 0.0;
        for (int i = 0; i < entries.size(); i++) {
            CashflowEntry e = (CashflowEntry) entries.get(i);
            if (e.getType() == CashflowType.INFLOW) {
                totalInflow += e.getAmount();
            } else {
                totalOutflow += e.getAmount();
            }
        }
        double net = totalInflow - totalOutflow;
        System.out.println("  Total Inflow:  " + String.format("%.2f", totalInflow));
        System.out.println("  Total Outflow: " + String.format("%.2f", totalOutflow));
        System.out.println("  Net Cashflow:  " + String.format("%.2f", net));
        System.out.println("================================");
    }

    public void printReturnsRefundsReport() {
        System.out.println("=== RETURNS/REFUNDS REPORT ===");
        List requests = dataStore.getReturnRequests();
        if (requests.size() == 0) {
            System.out.println("  No return requests.");
        }
        for (int i = 0; i < requests.size(); i++) {
            ReturnRequest rr = (ReturnRequest) requests.get(i);
            System.out.println("  Return: " + rr.getId() + " | Order: " + rr.getOrder().getId()
                    + " | Status: " + rr.getStatus() + " | Refund Total: " + String.format("%.2f", rr.getRefundTotal()));
            List items = rr.getReturnItems();
            for (int j = 0; j < items.size(); j++) {
                ReturnItem ri = (ReturnItem) items.get(j);
                System.out.println("    Product: " + ri.getProduct().getId() + " | Qty: " + ri.getQuantity()
                        + " | Refund: " + String.format("%.2f", ri.getRefundAmount()));
            }
        }
        System.out.println("==============================");
    }

    public void printRestockReport() {
        System.out.println("=== RESTOCKING REPORT ===");
        List entries = dataStore.getRestockEntries();
        if (entries.size() == 0) {
            System.out.println("  No restock entries.");
        }
        for (int i = 0; i < entries.size(); i++) {
            RestockEntry re = (RestockEntry) entries.get(i);
            System.out.println("  Restock: " + re.getId()
                    + " | Product: " + re.getProduct().getId()
                    + " | Supplier: " + re.getSupplier().getId()
                    + " | Qty: " + re.getQuantity()
                    + " | Unit Cost: " + String.format("%.2f", re.getUnitCost())
                    + " | Total Cost: " + String.format("%.2f", re.getTotalCost()));
        }
        System.out.println("=========================");
    }
}
