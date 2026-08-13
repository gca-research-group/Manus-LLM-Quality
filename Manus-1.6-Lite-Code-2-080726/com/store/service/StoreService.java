package com.store.service;

import java.util.*;
import com.store.model.*;
import com.store.enums.*;
import com.store.storage.DataStorage;
import com.store.exception.StoreException;
import com.store.promotion.Promotion;
import com.store.shipping.ShippingRule;

public class StoreService {
    private DataStorage storage;

    public StoreService(DataStorage storage) {
        this.storage = storage;
    }

    // Catalog
    public void addProduct(Product p) throws StoreException {
        if (storage.getProduct(p.getId()) != null) throw new StoreException("Duplicate product ID: " + p.getId());
        if (p.getUnitPrice() < 0) throw new StoreException("Price cannot be negative");
        storage.addProduct(p);
    }

    public Product findProduct(String id) { return storage.getProduct(id); }

    // Customers/Suppliers
    public void registerCustomer(Customer c) throws StoreException {
        if (storage.getCustomer(c.getId()) != null) throw new StoreException("Duplicate customer ID: " + c.getId());
        storage.addCustomer(c);
    }

    public void registerSupplier(Supplier s) throws StoreException {
        if (storage.getSupplier(s.getId()) != null) throw new StoreException("Duplicate supplier ID: " + s.getId());
        storage.addSupplier(s);
    }

    // Cart
    public Cart createCart(Customer c) { return new Cart(c); }

    public void addToCart(Cart cart, Product p, int qty) throws StoreException {
        if (!p.isActive()) throw new StoreException("Product is inactive");
        if (qty <= 0) throw new StoreException("Quantity must be positive");
        
        List items = cart.getItems();
        for (int i = 0; i < items.size(); i++) {
            CartItem item = (CartItem) items.get(i);
            if (item.getProduct().getId().equals(p.getId())) {
                item.setQuantity(item.getQuantity() + qty);
                return;
            }
        }
        items.add(new CartItem(p, qty));
    }

    public void updateCartQuantity(Cart cart, String productId, int qty) throws StoreException {
        if (qty <= 0) throw new StoreException("Quantity must be positive");
        List items = cart.getItems();
        for (int i = 0; i < items.size(); i++) {
            CartItem item = (CartItem) items.get(i);
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(qty);
                return;
            }
        }
    }

    public void removeFromCart(Cart cart, String productId) {
        List items = cart.getItems();
        for (Iterator it = items.iterator(); it.hasNext();) {
            CartItem item = (CartItem) it.next();
            if (item.getProduct().getId().equals(productId)) {
                it.remove();
                return;
            }
        }
    }

    // Orders
    public Order createOrder(String id, Cart cart) throws StoreException {
        if (cart.getItems().isEmpty()) throw new StoreException("Cart is empty");
        
        Order order = new Order(id, cart.getCustomer());
        List cartItems = cart.getItems();
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem ci = (CartItem) cartItems.get(i);
            if (storage.getStock(ci.getProduct().getId()) < ci.getQuantity()) {
                throw new StoreException("Insufficient stock for " + ci.getProduct().getName());
            }
            order.addItem(new OrderItem(ci.getProduct(), ci.getQuantity()));
            storage.updateStock(ci.getProduct().getId(), -ci.getQuantity());
        }
        storage.addOrder(order);
        return order;
    }

    public void applyPromotion(Order order, Promotion p) {
        order.setPromotionDiscount(p.calculateDiscount(order));
    }

    public void applyShipping(Order order, ShippingRule s) {
        order.setShippingCost(s.calculateShipping(order));
    }

    public void payOrder(Order order) throws StoreException {
        if (order.getStatus() == OrderStatus.CANCELLED) throw new StoreException("Cannot pay cancelled order");
        if (order.calculateTotal() < 0) throw new StoreException("Payment value cannot be negative");
        
        order.setStatus(OrderStatus.PAID);
        storage.addCashFlow(new CashFlowEntry("Payment for Order " + order.getId(), order.calculateTotal(), CashFlowType.INFLOW));
    }

    public void cancelOrder(Order order) throws StoreException {
        if (order.getStatus() == OrderStatus.PAID) throw new StoreException("Cannot cancel paid order");
        
        order.setStatus(OrderStatus.CANCELLED);
        List items = order.getItems();
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = (OrderItem) items.get(i);
            storage.updateStock(item.getProductId(), item.getQuantity());
        }
    }

    // Returns
    public ReturnRequest createReturn(String id, Order order) throws StoreException {
        if (order.getStatus() != OrderStatus.PAID) throw new StoreException("Only paid orders can be returned");
        return new ReturnRequest(id, order);
    }

    public void addReturnItem(ReturnRequest req, String productId, int qty) throws StoreException {
        if (qty <= 0) throw new StoreException("Return quantity must be positive");
        
        OrderItem original = null;
        List orderItems = req.getOrder().getItems();
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = (OrderItem) orderItems.get(i);
            if (item.getProductId().equals(productId)) {
                original = item;
                break;
            }
        }
        
        if (original == null) throw new StoreException("Product not in order");
        if (qty > original.getQuantity()) throw new StoreException("Cannot return more than bought");
        
        double refund = (original.getUnitPrice() * qty);
        req.addItem(new ReturnItem(productId, qty, refund));
    }

    public void processRefund(ReturnRequest req) throws StoreException {
        double totalRefund = req.calculateTotalRefund();
        if (totalRefund < 0) throw new StoreException("Refund value cannot be negative");
        
        req.getOrder().setStatus(OrderStatus.RETURNED);
        List items = req.getItems();
        for (int i = 0; i < items.size(); i++) {
            ReturnItem item = (ReturnItem) items.get(i);
            storage.updateStock(item.getProductId(), item.getQuantity());
        }
        storage.addReturn(req);
        storage.addCashFlow(new CashFlowEntry("Refund for Return " + req.getId(), totalRefund, CashFlowType.OUTFLOW));
    }

    // Restocking
    public void restock(String id, Product p, Supplier s, int qty, double cost) throws StoreException {
        if (qty <= 0) throw new StoreException("Restock quantity must be positive");
        if (cost < 0) throw new StoreException("Restock cost cannot be negative");
        
        RestockOperation op = new RestockOperation(id, p, s, qty, cost);
        storage.updateStock(p.getId(), qty);
        storage.addRestock(op);
        storage.addCashFlow(new CashFlowEntry("Restock " + id, op.calculateTotalCost(), CashFlowType.OUTFLOW));
    }

    public void addManualOutflow(String desc, double amount) throws StoreException {
        if (amount < 0) throw new StoreException("Amount cannot be negative");
        storage.addCashFlow(new CashFlowEntry(desc, amount, CashFlowType.OUTFLOW));
    }
}
