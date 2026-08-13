package store.service;

import store.model.cashflow.CashflowEntry;
import store.model.catalog.Category;
import store.model.catalog.Product;
import store.model.order.Order;
import store.model.party.Customer;
import store.model.party.Supplier;
import store.model.payment.Payment;
import store.model.promotion.Promotion;
import store.model.restock.RestockEntry;
import store.model.returns.ReturnRequest;
import store.model.shipping.ShippingRule;

import java.util.ArrayList;
import java.util.List;

public class DataStore {

    private List categories;
    private List products;
    private List customers;
    private List suppliers;
    private List orders;
    private List payments;
    private List returnRequests;
    private List restockEntries;
    private List cashflowEntries;
    private List promotions;
    private List shippingRules;

    private int orderCounter;
    private int paymentCounter;
    private int returnCounter;
    private int restockCounter;
    private int cashflowCounter;
    private int cartCounter;

    public DataStore() {
        categories = new ArrayList();
        products = new ArrayList();
        customers = new ArrayList();
        suppliers = new ArrayList();
        orders = new ArrayList();
        payments = new ArrayList();
        returnRequests = new ArrayList();
        restockEntries = new ArrayList();
        cashflowEntries = new ArrayList();
        promotions = new ArrayList();
        shippingRules = new ArrayList();
        orderCounter = 1;
        paymentCounter = 1;
        returnCounter = 1;
        restockCounter = 1;
        cashflowCounter = 1;
        cartCounter = 1;
    }

    public String nextOrderId() {
        return "ORD" + (orderCounter++);
    }

    public String nextPaymentId() {
        return "PAY" + (paymentCounter++);
    }

    public String nextReturnId() {
        return "RET" + (returnCounter++);
    }

    public String nextRestockId() {
        return "RST" + (restockCounter++);
    }

    public String nextCashflowId() {
        return "CF" + (cashflowCounter++);
    }

    public String nextCartId() {
        return "CART" + (cartCounter++);
    }

    public List getCategories() {
        return categories;
    }

    public List getProducts() {
        return products;
    }

    public List getCustomers() {
        return customers;
    }

    public List getSuppliers() {
        return suppliers;
    }

    public List getOrders() {
        return orders;
    }

    public List getPayments() {
        return payments;
    }

    public List getReturnRequests() {
        return returnRequests;
    }

    public List getRestockEntries() {
        return restockEntries;
    }

    public List getCashflowEntries() {
        return cashflowEntries;
    }

    public List getPromotions() {
        return promotions;
    }

    public List getShippingRules() {
        return shippingRules;
    }

    public Category findCategoryById(String id) {
        for (int i = 0; i < categories.size(); i++) {
            Category c = (Category) categories.get(i);
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public Product findProductById(String id) {
        for (int i = 0; i < products.size(); i++) {
            Product p = (Product) products.get(i);
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public Customer findCustomerById(String id) {
        for (int i = 0; i < customers.size(); i++) {
            Customer c = (Customer) customers.get(i);
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public Supplier findSupplierById(String id) {
        for (int i = 0; i < suppliers.size(); i++) {
            Supplier s = (Supplier) suppliers.get(i);
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    public Order findOrderById(String id) {
        for (int i = 0; i < orders.size(); i++) {
            Order o = (Order) orders.get(i);
            if (o.getId().equals(id)) {
                return o;
            }
        }
        return null;
    }

    public ShippingRule findShippingRuleByType(String type) {
        for (int i = 0; i < shippingRules.size(); i++) {
            ShippingRule r = (ShippingRule) shippingRules.get(i);
            if (r.getType().equals(type)) {
                return r;
            }
        }
        return null;
    }
}
