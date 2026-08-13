package com.store.service;

import com.store.model.*;
import com.store.model.enums.*;
import java.util.*;
import java.io.*;

public class StoreManager {
    private Map categories = new HashMap();
    private Map products = new HashMap();
    private Map customers = new HashMap();
    private Map suppliers = new HashMap();
    private Map carts = new HashMap();
    private Map orders = new HashMap();
    private Map returns = new HashMap();
    private Map restocks = new HashMap();
    private List cashflow = new ArrayList();

    // Catalog & Products
    public void addCategory(String name) {
        categories.put(name, new Category(name));
    }

    public void addProduct(String id, String name, double price, String categoryName) {
        if (products.containsKey(id)) throw new RuntimeException("Duplicate product ID: " + id);
        if (price < 0) throw new RuntimeException("Price cannot be negative");
        Category cat = (Category) categories.get(categoryName);
        products.put(id, new Product(id, name, price, cat));
    }

    public Product getProduct(String id) {
        return (Product) products.get(id);
    }

    public List getProductsByCategory(String categoryName) {
        List result = new ArrayList();
        for (Iterator it = products.values().iterator(); it.hasNext(); ) {
            Product p = (Product) it.next();
            if (p.getCategory().getName().equals(categoryName)) result.add(p);
        }
        return result;
    }

    // Stock
    public void updateStock(String productId, int delta) {
        Product p = getProduct(productId);
        if (p.getStockQuantity() + delta < 0) throw new RuntimeException("Stock cannot be negative");
        p.setStockQuantity(p.getStockQuantity() + delta);
    }

    // Customers & Suppliers
    public void registerCustomer(String id, String name, LoyaltyTier tier) {
        if (customers.containsKey(id)) throw new RuntimeException("Duplicate customer ID: " + id);
        customers.put(id, new Customer(id, name, tier));
    }

    public Customer getCustomer(String id) {
        return (Customer) customers.get(id);
    }

    public void registerSupplier(String id, String name) {
        if (suppliers.containsKey(id)) throw new RuntimeException("Duplicate supplier ID: " + id);
        suppliers.put(id, new Supplier(id, name));
    }

    public Supplier getSupplier(String id) {
        return (Supplier) suppliers.get(id);
    }

    // Cart
    public Cart getOrCreateCart(String customerId) {
        if (!carts.containsKey(customerId)) {
            carts.put(customerId, new Cart(getCustomer(customerId)));
        }
        return (Cart) carts.get(customerId);
    }

    public void addToCart(String customerId, String productId, int qty) {
        if (qty <= 0) throw new RuntimeException("Quantity must be positive");
        Product p = getProduct(productId);
        if (!p.isActive()) throw new RuntimeException("Product is inactive");
        Cart cart = getOrCreateCart(customerId);
        
        CartItem existing = null;
        for (int i = 0; i < cart.getItems().size(); i++) {
            CartItem item = (CartItem) cart.getItems().get(i);
            if (item.getProduct().getId().equals(productId)) {
                existing = item;
                break;
            }
        }
        
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + qty);
        } else {
            cart.addItem(new CartItem(p, qty));
        }
    }

    public void updateCartQuantity(String customerId, String productId, int qty) {
        if (qty <= 0) throw new RuntimeException("Quantity must be positive");
        Cart cart = getOrCreateCart(customerId);
        for (int i = 0; i < cart.getItems().size(); i++) {
            CartItem item = (CartItem) cart.getItems().get(i);
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(qty);
                return;
            }
        }
    }

    public void removeFromCart(String customerId, String productId) {
        Cart cart = getOrCreateCart(customerId);
        for (int i = 0; i < cart.getItems().size(); i++) {
            CartItem item = (CartItem) cart.getItems().get(i);
            if (item.getProduct().getId().equals(productId)) {
                cart.removeItem(item);
                return;
            }
        }
    }

    // Orders
    public Order createOrder(String customerId, String orderId) {
        Cart cart = getOrCreateCart(customerId);
        if (cart.getItems().isEmpty()) throw new RuntimeException("Cart is empty");
        
        Order order = new Order(orderId, cart.getCustomer());
        double subtotal = 0;
        for (int i = 0; i < cart.getItems().size(); i++) {
            CartItem ci = (CartItem) cart.getItems().get(i);
            // Reserve stock
            updateStock(ci.getProduct().getId(), -ci.getQuantity());
            OrderItem oi = new OrderItem(ci.getProduct(), ci.getQuantity(), ci.getProduct().getUnitPrice());
            order.addItem(oi);
            subtotal += oi.getSubtotal();
        }
        order.setSubtotal(subtotal);
        order.setFinalTotal(subtotal);
        orders.put(orderId, order);
        carts.remove(customerId);
        return order;
    }

    public Order getOrder(String id) {
        return (Order) orders.get(id);
    }

    public void cancelOrder(String orderId) {
        Order order = getOrder(orderId);
        if (order.getStatus() == OrderStatus.PAID) throw new RuntimeException("Paid orders cannot be cancelled");
        if (order.getStatus() == OrderStatus.CANCELLED) return;
        
        // Restore stock
        for (int i = 0; i < order.getItems().size(); i++) {
            OrderItem oi = (OrderItem) order.getItems().get(i);
            updateStock(oi.getProduct().getId(), oi.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
    }

    // Payments
    public void registerPayment(String orderId) {
        Order order = getOrder(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) throw new RuntimeException("Cancelled orders cannot be paid");
        order.setStatus(OrderStatus.PAID);
        recordCashflow("PAY-" + orderId, CashflowEntryType.INFLOW, order.getFinalTotal(), "Payment for order " + orderId);
    }

    // Returns
    public ReturnRequest createReturn(String returnId, String orderId) {
        Order order = getOrder(orderId);
        if (order.getStatus() != OrderStatus.PAID) throw new RuntimeException("Only paid orders can be returned");
        ReturnRequest req = new ReturnRequest(returnId, order);
        returns.put(returnId, req);
        return req;
    }

    public void addReturnItem(String returnId, String productId, int qty) {
        if (qty <= 0) throw new RuntimeException("Quantity must be positive");
        ReturnRequest req = (ReturnRequest) returns.get(returnId);
        OrderItem originalItem = null;
        for (int i = 0; i < req.getOrder().getItems().size(); i++) {
            OrderItem oi = (OrderItem) req.getOrder().getItems().get(i);
            if (oi.getProduct().getId().equals(productId)) {
                originalItem = oi;
                break;
            }
        }
        if (originalItem == null) throw new RuntimeException("Product not in original order");
        if (qty > originalItem.getQuantity()) throw new RuntimeException("Cannot return more than bought");
        
        req.addItem(new ReturnItem(originalItem.getProduct(), qty, originalItem.getUnitPrice()));
    }

    public void processRefund(String returnId) {
        ReturnRequest req = (ReturnRequest) returns.get(returnId);
        double totalRefund = 0;
        for (int i = 0; i < req.getItems().size(); i++) {
            ReturnItem ri = (ReturnItem) req.getItems().get(i);
            totalRefund += ri.getSubtotal();
            updateStock(ri.getProduct().getId(), ri.getQuantity());
        }
        req.setRefundTotal(totalRefund);
        req.getOrder().setStatus(OrderStatus.RETURNED);
        recordCashflow("REF-" + returnId, CashflowEntryType.OUTFLOW, totalRefund, "Refund for return " + returnId);
    }

    // Restocking
    public void restock(String restockId, String productId, String supplierId, int qty, double unitCost) {
        if (qty <= 0) throw new RuntimeException("Quantity must be positive");
        if (unitCost < 0) throw new RuntimeException("Cost cannot be negative");
        Product p = getProduct(productId);
        Supplier s = getSupplier(supplierId);
        RestockEntry entry = new RestockEntry(restockId, p, s, qty, unitCost);
        restocks.put(restockId, entry);
        updateStock(productId, qty);
        recordCashflow("RST-" + restockId, CashflowEntryType.OUTFLOW, entry.getTotalCost(), "Restock " + restockId);
    }

    // Cashflow
    public void recordCashflow(String id, CashflowEntryType type, double amount, String desc) {
        if (amount < 0) throw new RuntimeException("Amount cannot be negative");
        cashflow.add(new CashflowEntry(id, type, amount, desc));
    }

    public double[] getCashflowSummary() {
        double inflow = 0, outflow = 0;
        for (int i = 0; i < cashflow.size(); i++) {
            CashflowEntry e = (CashflowEntry) cashflow.get(i);
            if (e.getType() == CashflowEntryType.INFLOW) inflow += e.getAmount();
            else outflow += e.getAmount();
        }
        return new double[]{inflow, outflow, inflow - outflow};
    }

    // CSV Export
    public void exportInventory(String filename) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(filename));
        pw.println("ProductID,ProductName,Category,StockQuantity,UnitPrice");
        for (Iterator it = products.values().iterator(); it.hasNext(); ) {
            Product p = (Product) it.next();
            pw.println(p.getId() + "," + p.getName() + "," + p.getCategory().getName() + "," + p.getStockQuantity() + "," + p.getUnitPrice());
        }
        pw.close();
        System.out.println("Exported inventory to " + filename);
    }

    public void exportCashflow(String filename) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(filename));
        pw.println("EntryID,Type,Amount,Description");
        for (int i = 0; i < cashflow.size(); i++) {
            CashflowEntry e = (CashflowEntry) cashflow.get(i);
            pw.println(e.getId() + "," + e.getType() + "," + e.getAmount() + "," + e.getDescription());
        }
        pw.close();
        System.out.println("Exported cashflow to " + filename);
    }

    // Reports helper methods
    public Map getProductSales() {
        Map sales = new HashMap();
        for (Iterator it = products.keySet().iterator(); it.hasNext(); ) sales.put(it.next(), new Integer(0));
        for (Iterator it = orders.values().iterator(); it.hasNext(); ) {
            Order o = (Order) it.next();
            if (o.getStatus() == OrderStatus.PAID || o.getStatus() == OrderStatus.RETURNED) {
                for (int i = 0; i < o.getItems().size(); i++) {
                    OrderItem oi = (OrderItem) o.getItems().get(i);
                    String pid = oi.getProduct().getId();
                    int current = ((Integer) sales.get(pid)).intValue();
                    sales.put(pid, new Integer(current + oi.getQuantity()));
                }
            }
        }
        return sales;
    }

    public Map getOrdersByStatusCount() {
        Map counts = new HashMap();
        OrderStatus[] statuses = OrderStatus.values();
        for (int i = 0; i < statuses.length; i++) counts.put(statuses[i], new Integer(0));
        for (Iterator it = orders.values().iterator(); it.hasNext(); ) {
            Order o = (Order) it.next();
            int current = ((Integer) counts.get(o.getStatus())).intValue();
            counts.put(o.getStatus(), new Integer(current + 1));
        }
        return counts;
    }

    public Collection getAllProducts() { return products.values(); }
    public Collection getAllCustomers() { return customers.values(); }
    public Collection getAllSuppliers() { return suppliers.values(); }
    public Collection getAllReturns() { return returns.values(); }
    public Collection getAllRestocks() { return restocks.values(); }
}