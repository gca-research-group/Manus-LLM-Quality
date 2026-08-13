package com.store.storage;

import java.util.*;
import com.store.model.*;

public class DataStorage {
    private Map categories = new HashMap();
    private Map products = new HashMap();
    private Map customers = new HashMap();
    private Map suppliers = new HashMap();
    private Map stock = new HashMap();
    private List orders = new ArrayList();
    private List returns = new ArrayList();
    private List restocks = new ArrayList();
    private List cashflow = new ArrayList();

    public void addCategory(Category c) { categories.put(c.getName(), c); }
    public Category getCategory(String name) { return (Category) categories.get(name); }

    public void addProduct(Product p) { products.put(p.getId(), p); }
    public Product getProduct(String id) { return (Product) products.get(id); }
    public Collection getAllProducts() { return products.values(); }

    public void addCustomer(Customer c) { customers.put(c.getId(), c); }
    public Customer getCustomer(String id) { return (Customer) customers.get(id); }
    public int getCustomerCount() { return customers.size(); }

    public void addSupplier(Supplier s) { suppliers.put(s.getId(), s); }
    public Supplier getSupplier(String id) { return (Supplier) suppliers.get(id); }
    public int getSupplierCount() { return suppliers.size(); }

    public void updateStock(String productId, int quantity) {
        Integer current = (Integer) stock.get(productId);
        if (current == null) current = new Integer(0);
        stock.put(productId, new Integer(current.intValue() + quantity));
    }
    public int getStock(String productId) {
        Integer s = (Integer) stock.get(productId);
        return s == null ? 0 : s.intValue();
    }

    public void addOrder(Order o) { orders.add(o); }
    public List getAllOrders() { return orders; }

    public void addReturn(ReturnRequest r) { returns.add(r); }
    public List getAllReturns() { return returns; }

    public void addRestock(RestockOperation r) { restocks.add(r); }
    public List getAllRestocks() { return restocks; }

    public void addCashFlow(CashFlowEntry e) { cashflow.add(e); }
    public List getAllCashFlow() { return cashflow; }
}
