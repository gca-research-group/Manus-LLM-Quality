package com.store.main;

import com.store.model.*;
import com.store.model.enums.*;
import com.store.service.StoreManager;
import java.util.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        StoreManager store = new StoreManager();

        // 1. Register Categories
        store.addCategory("Office");
        store.addCategory("Accessories");
        store.addCategory("Bags");

        // 2. Register Products
        store.addProduct("P1", "Notebook", 15.0, "Office");
        store.addProduct("P2", "Pen", 5.0, "Office");
        store.addProduct("P3", "Mug", 20.0, "Accessories");
        store.addProduct("P4", "Backpack", 80.0, "Bags");
        store.addProduct("P5", "Bottle", 25.0, "Accessories");

        // 3. Initialize Stock
        store.updateStock("P1", 100);
        store.updateStock("P2", 200);
        store.updateStock("P3", 40);
        store.updateStock("P4", 15);
        store.updateStock("P5", 30);

        // 4. Register Customers
        store.registerCustomer("CU1", "Ana", LoyaltyTier.REGULAR);
        store.registerCustomer("CU2", "Bruno", LoyaltyTier.GOLD);
        store.registerCustomer("CU3", "Carla", LoyaltyTier.SILVER);

        // 5. Register Suppliers
        store.registerSupplier("SU1", "Alpha Supplies");
        store.registerSupplier("SU2", "Beta Wholesale");

        // 6. Promotions
        Promotion p10pct = new PercentagePromotion(10.0, 200.0);
        Promotion pFixed = new FixedDiscountPromotion(5.0, 50.0, LoyaltyTier.GOLD);
        Promotion pBuyXGetY = new BuyXGetYPromotion("P2", 2, 1);

        // 7. Shipping Rules
        ShippingRule standard = new FlatRateShipping(12.0);
        ShippingRule express = new FlatRateShipping(25.0);
        ShippingRule pickup = new FlatRateShipping(0.0);

        System.out.println("--- System Initialized ---");
        System.out.println("Registered Products: " + store.getAllProducts().size());
        System.out.println("Registered Customers: " + store.getAllCustomers().size());
        System.out.println("Registered Suppliers: " + store.getAllSuppliers().size());

        // 8. Scenario: Cart for CU2
        System.out.println("\n--- Cart Scenario for CU2 ---");
        store.addToCart("CU2", "P4", 1);
        store.addToCart("CU1", "P1", 2);
        store.addToCart("CU2", "P1", 10);
        store.addToCart("CU2", "P2", 3);
        store.addToCart("CU2", "P5", 2);
        
        Cart cartCU2 = store.getOrCreateCart("CU2");
        System.out.println("Cart subtotal before changes: " + cartCU2.calculateSubtotal());
        
        store.updateCartQuantity("CU2", "P1", 5);
        System.out.println("Cart subtotal after quantity change (P1 to 5): " + cartCU2.calculateSubtotal());
        
        store.removeFromCart("CU2", "P5");
        System.out.println("Cart subtotal after item removal (P5): " + cartCU2.calculateSubtotal());

        // 9. Order ORD1
        System.out.println("\n--- Order ORD1 Scenario ---");
        Order ord1 = store.createOrder("CU2", "ORD1");
        System.out.println("Order ORD1 Subtotal: " + ord1.getSubtotal());
        
        double discount = p10pct.calculateDiscount(ord1);
        ord1.setPromotionDiscount(discount);
        System.out.println("Order ORD1 Promotion Discount (10% over 200): " + ord1.getPromotionDiscount());
        
        double ship = express.calculateShipping(ord1);
        ord1.setShippingCost(ship);
        System.out.println("Order ORD1 Shipping Cost (EXPRESS): " + ord1.getShippingCost());
        
        ord1.setFinalTotal(ord1.getSubtotal() - ord1.getPromotionDiscount() + ord1.getShippingCost());
        System.out.println("Order ORD1 Final Total: " + ord1.getFinalTotal());
        
        store.registerPayment("ORD1");
        System.out.println("Order ORD1 Final Status: " + ord1.getStatus());

        // 10. Order ORD2 (Cancellation)
        System.out.println("\n--- Order ORD2 Scenario (Cancellation) ---");
        store.addToCart("CU1", "P3", 2);
        store.addToCart("CU1", "P4", 1);
        Order ord2 = store.createOrder("CU1", "ORD2");
        System.out.println("Order ORD2 Subtotal: " + ord2.getSubtotal());
        
        ord2.setShippingCost(standard.calculateShipping(ord2));
        ord2.setFinalTotal(ord2.getSubtotal() + ord2.getShippingCost());
        System.out.println("Order ORD2 Final Total: " + ord2.getFinalTotal());
        
        store.cancelOrder("ORD2");
        System.out.println("Order ORD2 Final Status: " + ord2.getStatus());

        // 11. Return Request
        System.out.println("\n--- Return Scenario ---");
        ReturnRequest ret = store.createReturn("RET1", "ORD1");
        store.addReturnItem("RET1", "P4", 1);
        System.out.println("Return Request ID: " + ret.getId());
        System.out.println("Returned Product: P4, Qty: 1");
        
        store.processRefund("RET1");
        System.out.println("Refund Total: " + ret.getRefundTotal());

        // 12. Restocking
        System.out.println("\n--- Restocking Scenario ---");
        store.restock("RST1", "P1", "SU1", 50, 10.0);
        RestockEntry re = (RestockEntry) store.getAllRestocks().iterator().next();
        System.out.println("Restock Entry ID: " + re.getId());
        System.out.println("Restocked Product: " + re.getProduct().getId() + ", Qty: " + re.getQuantity());
        System.out.println("Restock Total Cost: " + re.getTotalCost());

        // 13. Manual Cashflow
        store.recordCashflow("MAN1", CashflowEntryType.OUTFLOW, 100.0, "Office Rent");

        // 14. Invalid Operation
        System.out.println("\n--- Invalid Operation Attempt ---");
        try {
            store.updateStock("P5", -1000);
        } catch (RuntimeException e) {
            System.out.println("Confirmation that the invalid operation was rejected: " + e.getMessage());
        }

        // 15. Export CSV
        System.out.println("\n--- CSV Export ---");
        try {
            store.exportInventory("inventory.csv");
            store.exportCashflow("cashflow.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 16. Reports
        System.out.println("\n--- Reports ---");
        
        System.out.println("\n[Stock Report]");
        for (Iterator it = store.getAllProducts().iterator(); it.hasNext(); ) {
            Product p = (Product) it.next();
            System.out.println("Product: " + p.getId() + ", Stock: " + p.getStockQuantity());
        }

        System.out.println("\n[Sales by Product Report]");
        Map sales = store.getProductSales();
        for (Iterator it = sales.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            System.out.println("Product: " + entry.getKey() + ", Sold: " + entry.getValue());
        }

        System.out.println("\n[Orders by Status Report]");
        Map statusCounts = store.getOrdersByStatusCount();
        for (Iterator it = statusCounts.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            System.out.println("Status: " + entry.getKey() + ", Count: " + entry.getValue());
        }

        System.out.println("\n[Cashflow Summary Report]");
        double[] summary = store.getCashflowSummary();
        System.out.println("Total Inflow: " + summary[0]);
        System.out.println("Total Outflow: " + summary[1]);
        System.out.println("Net Cashflow: " + summary[2]);

        System.out.println("\n[Returns/Refunds Report]");
        for (Iterator it = store.getAllReturns().iterator(); it.hasNext(); ) {
            ReturnRequest rr = (ReturnRequest) it.next();
            System.out.println("Return ID: " + rr.getId() + ", Refund Total: " + rr.getRefundTotal());
        }

        System.out.println("\n[Restocking Report]");
        for (Iterator it = store.getAllRestocks().iterator(); it.hasNext(); ) {
            RestockEntry r = (RestockEntry) it.next();
            System.out.println("Restock ID: " + r.getId() + ", Product: " + r.getProduct().getId() + 
                ", Supplier: " + r.getSupplier().getId() + ", Qty: " + r.getQuantity() + ", Total Cost: " + r.getTotalCost());
        }

        System.out.println("\nFinal Stock values of P1, P2, P3, P4, P5:");
        System.out.println("P1: " + store.getProduct("P1").getStockQuantity());
        System.out.println("P2: " + store.getProduct("P2").getStockQuantity());
        System.out.println("P3: " + store.getProduct("P3").getStockQuantity());
        System.out.println("P4: " + store.getProduct("P4").getStockQuantity());
        System.out.println("P5: " + store.getProduct("P5").getStockQuantity());
    }
}