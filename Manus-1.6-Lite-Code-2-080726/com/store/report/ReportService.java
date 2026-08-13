package com.store.report;

import java.io.*;
import java.util.*;
import com.store.model.*;
import com.store.enums.*;
import com.store.storage.DataStorage;

public class ReportService {
    private DataStorage storage;

    public ReportService(DataStorage storage) {
        this.storage = storage;
    }

    public void printStockReport() {
        System.out.println("--- STOCK REPORT ---");
        Collection products = storage.getAllProducts();
        for (Iterator it = products.iterator(); it.hasNext();) {
            Product p = (Product) it.next();
            System.out.println("Product: " + p.getId() + " | Stock: " + storage.getStock(p.getId()));
        }
    }

    public void printSalesReport() {
        System.out.println("--- SALES BY PRODUCT REPORT ---");
        Map sales = new HashMap();
        List orders = storage.getAllOrders();
        for (int i = 0; i < orders.size(); i++) {
            Order o = (Order) orders.get(i);
            if (o.getStatus() == OrderStatus.PAID || o.getStatus() == OrderStatus.RETURNED) {
                List items = o.getItems();
                for (int j = 0; j < items.size(); j++) {
                    OrderItem item = (OrderItem) items.get(j);
                    Integer current = (Integer) sales.get(item.getProductId());
                    if (current == null) current = new Integer(0);
                    sales.put(item.getProductId(), new Integer(current.intValue() + item.getQuantity()));
                }
            }
        }
        Collection products = storage.getAllProducts();
        for (Iterator it = products.iterator(); it.hasNext();) {
            Product p = (Product) it.next();
            Integer qty = (Integer) sales.get(p.getId());
            System.out.println("Product: " + p.getId() + " | Sold: " + (qty == null ? 0 : qty.intValue()));
        }
    }

    public void printOrdersByStatusReport() {
        System.out.println("--- ORDERS BY STATUS REPORT ---");
        Map counts = new HashMap();
        OrderStatus[] statuses = OrderStatus.values();
        for (int i = 0; i < statuses.length; i++) counts.put(statuses[i], new Integer(0));

        List orders = storage.getAllOrders();
        for (int i = 0; i < orders.size(); i++) {
            Order o = (Order) orders.get(i);
            Integer c = (Integer) counts.get(o.getStatus());
            counts.put(o.getStatus(), new Integer(c.intValue() + 1));
        }

        for (int i = 0; i < statuses.length; i++) {
            System.out.println("Status: " + statuses[i] + " | Count: " + counts.get(statuses[i]));
        }
    }

    public void printCashFlowReport() {
        System.out.println("--- CASHFLOW SUMMARY REPORT ---");
        double inflow = 0, outflow = 0;
        List entries = storage.getAllCashFlow();
        for (int i = 0; i < entries.size(); i++) {
            CashFlowEntry e = (CashFlowEntry) entries.get(i);
            if (e.getType() == CashFlowType.INFLOW) inflow += e.getAmount();
            else outflow += e.getAmount();
        }
        System.out.println("Total Inflow: " + inflow);
        System.out.println("Total Outflow: " + outflow);
        System.out.println("Net Cashflow: " + (inflow - outflow));
    }

    public void printReturnsReport() {
        System.out.println("--- RETURNS/REFUNDS REPORT ---");
        List returns = storage.getAllReturns();
        for (int i = 0; i < returns.size(); i++) {
            ReturnRequest r = (ReturnRequest) returns.get(i);
            System.out.println("Return: " + r.getId() + " | Refund: " + r.calculateTotalRefund());
        }
    }

    public void printRestockingReport() {
        System.out.println("--- RESTOCKING REPORT ---");
        List restocks = storage.getAllRestocks();
        for (int i = 0; i < restocks.size(); i++) {
            RestockOperation r = (RestockOperation) restocks.get(i);
            System.out.println("Restock: " + r.getId() + " | Product: " + r.getProduct().getId() + " | Supplier: " + r.getSupplier().getId() + " | Qty: " + r.getQuantity() + " | Total: " + r.calculateTotalCost());
        }
    }

    public void exportInventoryCSV() throws IOException {
        String filename = "inventory.csv";
        PrintWriter writer = new PrintWriter(new FileWriter(filename));
        writer.println("ProductID,ProductName,Category,Stock");
        Collection products = storage.getAllProducts();
        for (Iterator it = products.iterator(); it.hasNext();) {
            Product p = (Product) it.next();
            writer.println(p.getId() + "," + p.getName() + "," + p.getCategory().getName() + "," + storage.getStock(p.getId()));
        }
        writer.close();
        System.out.println("Exported: " + filename);
    }

    public void exportCashFlowCSV() throws IOException {
        String filename = "cashflow.csv";
        PrintWriter writer = new PrintWriter(new FileWriter(filename));
        writer.println("Description,Amount,Type");
        List entries = storage.getAllCashFlow();
        for (int i = 0; i < entries.size(); i++) {
            CashFlowEntry e = (CashFlowEntry) entries.get(i);
            writer.println(e.getDescription() + "," + e.getAmount() + "," + e.getType());
        }
        writer.close();
        System.out.println("Exported: " + filename);
    }
}
