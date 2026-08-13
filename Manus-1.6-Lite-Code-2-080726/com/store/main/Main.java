package com.store.main;

import com.store.model.*;
import com.store.enums.*;
import com.store.service.StoreService;
import com.store.storage.DataStorage;
import com.store.report.ReportService;
import com.store.promotion.*;
import com.store.shipping.*;
import com.store.exception.StoreException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        DataStorage storage = new DataStorage();
        StoreService service = new StoreService(storage);
        ReportService reportService = new ReportService(storage);

        try {
            // Setup
            Category c1 = new Category("Office");
            Category c2 = new Category("Accessories");
            Category c3 = new Category("Bags");
            storage.addCategory(c1);
            storage.addCategory(c2);
            storage.addCategory(c3);

            Product p1 = new Product("P1", "Notebook", 15.0, c1);
            Product p2 = new Product("P2", "Pen", 5.0, c1);
            Product p3 = new Product("P3", "Mug", 20.0, c2);
            Product p4 = new Product("P4", "Backpack", 80.0, c3);
            Product p5 = new Product("P5", "Bottle", 25.0, c2);

            service.addProduct(p1);
            service.addProduct(p2);
            service.addProduct(p3);
            service.addProduct(p4);
            service.addProduct(p5);

            storage.updateStock("P1", 100);
            storage.updateStock("P2", 200);
            storage.updateStock("P3", 40);
            storage.updateStock("P4", 15);
            storage.updateStock("P5", 30);

            Customer cu1 = new Customer("CU1", "Ana", LoyaltyTier.REGULAR);
            Customer cu2 = new Customer("CU2", "Bruno", LoyaltyTier.GOLD);
            Customer cu3 = new Customer("CU3", "Carla", LoyaltyTier.SILVER);
            service.registerCustomer(cu1);
            service.registerCustomer(cu2);
            service.registerCustomer(cu3);

            Supplier su1 = new Supplier("SU1", "Alpha Supplies");
            Supplier su2 = new Supplier("SU2", "Beta Wholesale");
            service.registerSupplier(su1);
            service.registerSupplier(su2);

            Promotion promo1 = new PercentagePromotion(10.0, 200.0);
            Promotion promo2 = new FixedDiscountPromotion(5.0, 50.0, LoyaltyTier.GOLD);
            Promotion promo3 = new BuyXGetYPromotion("P2", 2, 1);

            System.out.println("Registered Products: " + storage.getAllProducts().size());
            System.out.println("Registered Customers: " + storage.getCustomerCount());
            System.out.println("Registered Suppliers: " + storage.getSupplierCount());

            // Cart 1 for CU2
            Cart cart1 = service.createCart(cu2);
            service.addToCart(cart1, p4, 1); // 80
            service.addToCart(cart1, p1, 2); // 30
            service.addToCart(cart1, p2, 5); // 25
            service.addToCart(cart1, p5, 1); // 25
            System.out.println("Cart 1 subtotal (initial): " + cart1.calculateSubtotal());

            service.updateCartQuantity(cart1, "P1", 3); // 80 + 45 + 25 + 25 = 175
            System.out.println("Cart 1 subtotal (after update): " + cart1.calculateSubtotal());

            service.removeFromCart(cart1, "P5"); // 175 - 25 = 150
            System.out.println("Cart 1 subtotal (after removal): " + cart1.calculateSubtotal());

            // Order 1
            Order ord1 = service.createOrder("ORD1", cart1);
            System.out.println("Order ORD1 subtotal: " + ord1.calculateSubtotal());
            
            service.applyPromotion(ord1, promo2);
            System.out.println("Order ORD1 promotion discount: " + ord1.getPromotionDiscount());

            service.applyShipping(ord1, new StandardShippingRule(12.0));
            System.out.println("Order ORD1 shipping cost: " + ord1.getShippingCost());

            System.out.println("Order ORD1 final total: " + ord1.calculateTotal());
            service.payOrder(ord1);
            System.out.println("Order ORD1 final status: " + ord1.getStatus());

            // Cart 2 for CU1
            Cart cart2 = service.createCart(cu1);
            service.addToCart(cart2, p3, 2);
            service.addToCart(cart2, p4, 1);
            Order ord2 = service.createOrder("ORD2", cart2);
            System.out.println("Order ORD2 subtotal: " + ord2.calculateSubtotal());
            service.applyShipping(ord2, new StandardShippingRule(25.0));
            System.out.println("Order ORD2 shipping cost: " + ord2.getShippingCost());
            System.out.println("Order ORD2 final total: " + ord2.calculateTotal());
            service.cancelOrder(ord2);
            System.out.println("Order ORD2 final status: " + ord2.getStatus());

            // Return
            ReturnRequest ret1 = service.createReturn("RET1", ord1);
            service.addReturnItem(ret1, "P4", 1);
            System.out.println("Return request identifier: " + ret1.getId());
            System.out.println("Returned products: P4 (Qty: 1)");
            System.out.println("Refund total: " + ret1.calculateTotalRefund());
            service.processRefund(ret1);

            // Restock
            service.restock("REST1", p3, su1, 10, 15.0);
            System.out.println("Restock entry identifier: REST1");
            System.out.println("Restocked product: P3 (Qty: 10)");
            System.out.println("Restock total cost: " + (10 * 15.0));

            // Manual Outflow
            service.addManualOutflow("Office Rent", 500.0);

            // CSV Export
            reportService.exportInventoryCSV();
            reportService.exportCashFlowCSV();

            // Invalid Operation
            try {
                service.addToCart(cart1, p1, -1);
            } catch (StoreException e) {
                System.out.println("Confirmation that the invalid operation was rejected: " + e.getMessage());
            }

            // Final Stock
            System.out.println("Final stock P1: " + storage.getStock("P1"));
            System.out.println("Final stock P2: " + storage.getStock("P2"));
            System.out.println("Final stock P3: " + storage.getStock("P3"));
            System.out.println("Final stock P4: " + storage.getStock("P4"));
            System.out.println("Final stock P5: " + storage.getStock("P5"));

            // Reports
            reportService.printStockReport();
            reportService.printSalesReport();
            reportService.printOrdersByStatusReport();
            reportService.printCashFlowReport();
            reportService.printReturnsReport();
            reportService.printRestockingReport();

        } catch (StoreException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        }
    }
}
