package store.service;

import store.model.cart.Cart;
import store.model.cart.CartItem;
import store.model.cashflow.CashflowEntry;
import store.model.cashflow.CashflowType;
import store.model.catalog.Category;
import store.model.catalog.Product;
import store.model.order.Order;
import store.model.order.OrderItem;
import store.model.order.OrderStatus;
import store.model.party.Customer;
import store.model.party.Supplier;
import store.model.payment.Payment;
import store.model.payment.PaymentMethod;
import store.model.promotion.Promotion;
import store.model.restock.RestockEntry;
import store.model.returns.ReturnItem;
import store.model.returns.ReturnRequest;
import store.model.returns.ReturnStatus;
import store.model.shipping.ShippingRule;

import java.util.ArrayList;
import java.util.List;

public class StoreService {

    private DataStore dataStore;

    public StoreService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public DataStore getDataStore() {
        return dataStore;
    }

    public Category addCategory(String id, String name) {
        Category category = new Category(id, name);
        dataStore.getCategories().add(category);
        return category;
    }

    public Product addProduct(String id, String name, double unitPrice, String categoryId) {
        if (dataStore.findProductById(id) != null) {
            throw new IllegalArgumentException("Duplicate product identifier: " + id);
        }
        Category category = dataStore.findCategoryById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found: " + categoryId);
        }
        Product product = new Product(id, name, unitPrice, category);
        dataStore.getProducts().add(product);
        return product;
    }

    public Product findProductById(String id) {
        return dataStore.findProductById(id);
    }

    public List findProductsByName(String name) {
        List result = new ArrayList();
        List products = dataStore.getProducts();
        for (int i = 0; i < products.size(); i++) {
            Product p = (Product) products.get(i);
            if (p.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(p);
            }
        }
        return result;
    }

    public List findProductsByCategory(String categoryId) {
        List result = new ArrayList();
        List products = dataStore.getProducts();
        for (int i = 0; i < products.size(); i++) {
            Product p = (Product) products.get(i);
            if (p.getCategory().getId().equals(categoryId)) {
                result.add(p);
            }
        }
        return result;
    }

    public void activateProduct(String productId) {
        Product p = requireProduct(productId);
        p.setActive(true);
    }

    public void deactivateProduct(String productId) {
        Product p = requireProduct(productId);
        p.setActive(false);
    }

    public void setInitialStock(String productId, int quantity) {
        Product p = requireProduct(productId);
        p.increaseStock(quantity);
    }

    public Customer registerCustomer(String id, String name, store.model.party.LoyaltyTier tier) {
        if (dataStore.findCustomerById(id) != null) {
            throw new IllegalArgumentException("Duplicate customer identifier: " + id);
        }
        Customer customer = new Customer(id, name, tier);
        dataStore.getCustomers().add(customer);
        return customer;
    }

    public Supplier registerSupplier(String id, String name) {
        if (dataStore.findSupplierById(id) != null) {
            throw new IllegalArgumentException("Duplicate supplier identifier: " + id);
        }
        Supplier supplier = new Supplier(id, name);
        dataStore.getSuppliers().add(supplier);
        return supplier;
    }

    public void registerPromotion(Promotion promotion) {
        dataStore.getPromotions().add(promotion);
    }

    public void registerShippingRule(ShippingRule rule) {
        dataStore.getShippingRules().add(rule);
    }

    public Cart createCart(String customerId) {
        Customer customer = requireCustomer(customerId);
        String cartId = dataStore.nextCartId();
        return new Cart(cartId, customer);
    }

    public void addToCart(Cart cart, String productId, int quantity) {
        Product product = requireProduct(productId);
        cart.addItem(product, quantity);
    }

    public void updateCartItemQuantity(Cart cart, String productId, int quantity) {
        cart.updateItemQuantity(productId, quantity);
    }

    public void removeFromCart(Cart cart, String productId) {
        cart.removeItem(productId);
    }

    public Order createOrderFromCart(Cart cart) {
        List cartItems = cart.getItems();
        if (cartItems.size() == 0) {
            throw new IllegalStateException("Cannot create an order from an empty cart.");
        }
        List orderItems = new ArrayList();
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem ci = (CartItem) cartItems.get(i);
            Product p = ci.getProduct();
            if (!p.isActive()) {
                throw new IllegalStateException("Product " + p.getId() + " is no longer active.");
            }
            if (p.getStock() < ci.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product " + p.getId() + ".");
            }
            orderItems.add(new OrderItem(p, ci.getQuantity(), p.getUnitPrice()));
        }
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem oi = (OrderItem) orderItems.get(i);
            oi.getProduct().decreaseStock(oi.getQuantity());
        }
        String orderId = dataStore.nextOrderId();
        Order order = new Order(orderId, cart.getCustomer(), orderItems);
        dataStore.getOrders().add(order);
        return order;
    }

    public Order createOrderFromCartWithId(Cart cart, String orderId) {
        List cartItems = cart.getItems();
        if (cartItems.size() == 0) {
            throw new IllegalStateException("Cannot create an order from an empty cart.");
        }
        List orderItems = new ArrayList();
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem ci = (CartItem) cartItems.get(i);
            Product p = ci.getProduct();
            if (!p.isActive()) {
                throw new IllegalStateException("Product " + p.getId() + " is no longer active.");
            }
            if (p.getStock() < ci.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product " + p.getId() + ".");
            }
            orderItems.add(new OrderItem(p, ci.getQuantity(), p.getUnitPrice()));
        }
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem oi = (OrderItem) orderItems.get(i);
            oi.getProduct().decreaseStock(oi.getQuantity());
        }
        Order order = new Order(orderId, cart.getCustomer(), orderItems);
        dataStore.getOrders().add(order);
        return order;
    }

    public void applyPromotion(Order order, String promotionId) {
        List promotions = dataStore.getPromotions();
        Promotion found = null;
        for (int i = 0; i < promotions.size(); i++) {
            Promotion p = (Promotion) promotions.get(i);
            if (p.getId().equals(promotionId)) {
                found = p;
                break;
            }
        }
        if (found == null) {
            throw new IllegalArgumentException("Promotion not found: " + promotionId);
        }
        if (!found.isApplicable(order)) {
            System.out.println("  [INFO] Promotion " + promotionId + " is not applicable to order " + order.getId() + ".");
            return;
        }
        double discount = found.calculateDiscount(order);
        order.setPromotionDiscount(discount);
    }

    public void applyShipping(Order order, String shippingType) {
        ShippingRule rule = dataStore.findShippingRuleByType(shippingType);
        if (rule == null) {
            throw new IllegalArgumentException("Shipping rule not found: " + shippingType);
        }
        order.setShippingCost(rule.getCost());
        order.setShippingType(rule.getType());
    }

    public Payment registerPayment(String orderId, PaymentMethod method) {
        Order order = requireOrder(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay a cancelled order: " + orderId);
        }
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Order is already paid: " + orderId);
        }
        double amount = order.getFinalTotal();
        String paymentId = dataStore.nextPaymentId();
        Payment payment = new Payment(paymentId, orderId, amount, method);
        dataStore.getPayments().add(payment);
        order.setStatus(OrderStatus.PAID);
        String cfId = dataStore.nextCashflowId();
        CashflowEntry entry = new CashflowEntry(cfId, CashflowType.INFLOW, amount,
                "Payment for order " + orderId);
        dataStore.getCashflowEntries().add(entry);
        return payment;
    }

    public void cancelOrder(String orderId) {
        Order order = requireOrder(orderId);
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Cannot cancel a paid order: " + orderId);
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled: " + orderId);
        }
        List items = order.getItems();
        for (int i = 0; i < items.size(); i++) {
            OrderItem oi = (OrderItem) items.get(i);
            oi.getProduct().increaseStock(oi.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
    }

    public ReturnRequest createReturnRequest(String orderId) {
        Order order = requireOrder(orderId);
        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Only paid orders can be returned. Order status: " + order.getStatus());
        }
        String returnId = dataStore.nextReturnId();
        ReturnRequest request = new ReturnRequest(returnId, order);
        dataStore.getReturnRequests().add(request);
        return request;
    }

    public void addReturnItem(ReturnRequest request, String productId, int quantity) {
        Order order = request.getOrder();
        List orderItems = order.getItems();
        OrderItem foundItem = null;
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem oi = (OrderItem) orderItems.get(i);
            if (oi.getProduct().getId().equals(productId)) {
                foundItem = oi;
                break;
            }
        }
        if (foundItem == null) {
            throw new IllegalArgumentException("Product " + productId + " was not in order " + order.getId());
        }
        int alreadyReturned = 0;
        List existingItems = request.getReturnItems();
        for (int i = 0; i < existingItems.size(); i++) {
            ReturnItem ri = (ReturnItem) existingItems.get(i);
            if (ri.getProduct().getId().equals(productId)) {
                alreadyReturned += ri.getQuantity();
            }
        }
        if (alreadyReturned + quantity > foundItem.getQuantity()) {
            throw new IllegalArgumentException("Returned quantity exceeds originally bought quantity for product " + productId);
        }
        Product product = requireProduct(productId);
        ReturnItem returnItem = new ReturnItem(product, quantity, foundItem.getUnitPrice());
        request.addReturnItem(returnItem);
    }

    public void processRefund(ReturnRequest request) {
        if (request.getReturnItems().size() == 0) {
            throw new IllegalStateException("A return must contain at least one returned item.");
        }
        if (request.getStatus() == ReturnStatus.PROCESSED) {
            throw new IllegalStateException("Return request " + request.getId() + " has already been processed.");
        }
        List returnItems = request.getReturnItems();
        for (int i = 0; i < returnItems.size(); i++) {
            ReturnItem ri = (ReturnItem) returnItems.get(i);
            ri.getProduct().increaseStock(ri.getQuantity());
        }
        double refundTotal = request.getRefundTotal();
        if (refundTotal < 0) {
            throw new IllegalArgumentException("Refund value cannot be negative.");
        }
        String cfId = dataStore.nextCashflowId();
        CashflowEntry entry = new CashflowEntry(cfId, CashflowType.OUTFLOW, refundTotal,
                "Refund for return " + request.getId());
        dataStore.getCashflowEntries().add(entry);
        request.setStatus(ReturnStatus.PROCESSED);
        request.getOrder().setStatus(OrderStatus.RETURNED);
    }

    public RestockEntry performRestock(String productId, String supplierId, int quantity, double unitCost) {
        Product product = requireProduct(productId);
        Supplier supplier = requireSupplier(supplierId);
        String restockId = dataStore.nextRestockId();
        RestockEntry entry = new RestockEntry(restockId, product, supplier, quantity, unitCost);
        product.increaseStock(quantity);
        double totalCost = entry.getTotalCost();
        String cfId = dataStore.nextCashflowId();
        CashflowEntry cfEntry = new CashflowEntry(cfId, CashflowType.OUTFLOW, totalCost,
                "Restock " + restockId + " for product " + productId);
        dataStore.getCashflowEntries().add(cfEntry);
        dataStore.getRestockEntries().add(entry);
        return entry;
    }

    public CashflowEntry registerManualOutflow(double amount, String description) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cashflow amount cannot be negative.");
        }
        String cfId = dataStore.nextCashflowId();
        CashflowEntry entry = new CashflowEntry(cfId, CashflowType.OUTFLOW, amount, description);
        dataStore.getCashflowEntries().add(entry);
        return entry;
    }

    private Product requireProduct(String id) {
        Product p = dataStore.findProductById(id);
        if (p == null) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        return p;
    }

    private Customer requireCustomer(String id) {
        Customer c = dataStore.findCustomerById(id);
        if (c == null) {
            throw new IllegalArgumentException("Customer not found: " + id);
        }
        return c;
    }

    private Supplier requireSupplier(String id) {
        Supplier s = dataStore.findSupplierById(id);
        if (s == null) {
            throw new IllegalArgumentException("Supplier not found: " + id);
        }
        return s;
    }

    private Order requireOrder(String id) {
        Order o = dataStore.findOrderById(id);
        if (o == null) {
            throw new IllegalArgumentException("Order not found: " + id);
        }
        return o;
    }
}
