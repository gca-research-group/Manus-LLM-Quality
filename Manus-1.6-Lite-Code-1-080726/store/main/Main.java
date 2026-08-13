package store.main;

import store.export.CsvExportService;
import store.model.cart.Cart;
import store.model.order.Order;
import store.model.party.LoyaltyTier;
import store.model.payment.PaymentMethod;
import store.model.promotion.BuyXGetYPromotion;
import store.model.promotion.FixedDiscountPromotion;
import store.model.promotion.PercentagePromotion;
import store.model.restock.RestockEntry;
import store.model.returns.ReturnRequest;
import store.model.shipping.ExpressShipping;
import store.model.shipping.PickupShipping;
import store.model.shipping.StandardShipping;
import store.report.ReportService;
import store.service.DataStore;
import store.service.StoreService;

public class Main {

    public static void main(String[] args) {

        DataStore dataStore = new DataStore();
        StoreService service = new StoreService(dataStore);
        ReportService reportService = new ReportService(dataStore);
        CsvExportService csvExportService = new CsvExportService(dataStore);

        System.out.println("========================================");
        System.out.println("  STORE MANAGEMENT SYSTEM - DEMO SCENARIO");
        System.out.println("========================================");

        // --- Categories ---
        System.out.println("\n--- Registering Categories ---");
        service.addCategory("C1", "Office");
        service.addCategory("C2", "Accessories");
        service.addCategory("C3", "Bags");
        System.out.println("Categories registered: C1=Office, C2=Accessories, C3=Bags");

        // --- Products ---
        System.out.println("\n--- Registering Products ---");
        service.addProduct("P1", "Notebook", 15.0, "C1");
        service.addProduct("P2", "Pen", 5.0, "C1");
        service.addProduct("P3", "Mug", 20.0, "C2");
        service.addProduct("P4", "Backpack", 80.0, "C3");
        service.addProduct("P5", "Bottle", 25.0, "C2");
        System.out.println("Number of registered products: " + dataStore.getProducts().size());

        // --- Initial Stock ---
        System.out.println("\n--- Initializing Stock ---");
        service.setInitialStock("P1", 100);
        service.setInitialStock("P2", 200);
        service.setInitialStock("P3", 40);
        service.setInitialStock("P4", 15);
        service.setInitialStock("P5", 30);
        System.out.println("Stock initialized for all products.");

        // --- Customers ---
        System.out.println("\n--- Registering Customers ---");
        service.registerCustomer("CU1", "Ana", LoyaltyTier.REGULAR);
        service.registerCustomer("CU2", "Bruno", LoyaltyTier.GOLD);
        service.registerCustomer("CU3", "Carla", LoyaltyTier.SILVER);
        System.out.println("Number of registered customers: " + dataStore.getCustomers().size());

        // --- Suppliers ---
        System.out.println("\n--- Registering Suppliers ---");
        service.registerSupplier("SU1", "Alpha Supplies");
        service.registerSupplier("SU2", "Beta Wholesale");
        System.out.println("Number of registered suppliers: " + dataStore.getSuppliers().size());

        // --- Promotions ---
        System.out.println("\n--- Registering Promotions ---");
        service.registerPromotion(new PercentagePromotion("PROMO1", 10.0, 200.0));
        service.registerPromotion(new FixedDiscountPromotion("PROMO2", 5.0, 50.0, LoyaltyTier.GOLD));
        service.registerPromotion(new BuyXGetYPromotion("PROMO3", "P2", 2, 1));
        System.out.println("Promotions registered: PROMO1 (10% off >= 200), PROMO2 (fixed 5 for GOLD >= 50), PROMO3 (buy 2 get 1 free for P2)");

        // --- Shipping Rules ---
        System.out.println("\n--- Registering Shipping Rules ---");
        service.registerShippingRule(new StandardShipping(12.0));
        service.registerShippingRule(new ExpressShipping(25.0));
        service.registerShippingRule(new PickupShipping());
        System.out.println("Shipping rules registered: STANDARD=12.0, EXPRESS=25.0, PICKUP=0.0");

        // --- Cart for CU2 (Bruno, GOLD) ---
        System.out.println("\n--- Cart Operations for CU2 (Bruno) ---");
        Cart cart1 = service.createCart("CU2");
        service.addToCart(cart1, "P4", 1);   // Backpack x1 = 80
        service.addToCart(cart1, "P1", 3);   // Notebook x3 = 45
        service.addToCart(cart1, "P2", 4);   // Pen x4 = 20
        service.addToCart(cart1, "P5", 2);   // Bottle x2 = 50
        // Initial: P4(80) + P1(45) + P2(20) + P5(50) = 195
        System.out.println("Cart subtotal (initial): " + String.format("%.2f", cart1.getSubtotal()));

        service.updateCartItemQuantity(cart1, "P1", 6); // Notebook x6 = 90; total = 80+90+20+50 = 240
        System.out.println("Cart subtotal after updating P1 quantity to 6: " + String.format("%.2f", cart1.getSubtotal()));

        service.removeFromCart(cart1, "P5"); // Remove Bottle; total = 80+90+20 = 190 -> add P5 back after
        System.out.println("Cart subtotal after removing P5: " + String.format("%.2f", cart1.getSubtotal()));
        // Re-add P5 to ensure subtotal >= 200 for PROMO1 demonstration
        service.addToCart(cart1, "P5", 2);   // Bottle x2 = 50; total = 80+90+20+50 = 240
        System.out.println("Cart subtotal after re-adding P5 (x2): " + String.format("%.2f", cart1.getSubtotal()));

        // --- Order ORD1 from cart1 ---
        System.out.println("\n--- Creating Order ORD1 ---");
        Order ord1 = service.createOrderFromCartWithId(cart1, "ORD1");
        System.out.println("ORD1 subtotal: " + String.format("%.2f", ord1.getSubtotal()));

        service.applyPromotion(ord1, "PROMO1");
        System.out.println("ORD1 promotion discount (PROMO1): " + String.format("%.2f", ord1.getPromotionDiscount()));

        service.applyShipping(ord1, "STANDARD");
        System.out.println("ORD1 shipping cost (STANDARD): " + String.format("%.2f", ord1.getShippingCost()));

        System.out.println("ORD1 final total: " + String.format("%.2f", ord1.getFinalTotal()));

        // --- Payment for ORD1 ---
        System.out.println("\n--- Registering Payment for ORD1 ---");
        service.registerPayment("ORD1", PaymentMethod.CREDIT_CARD);
        System.out.println("ORD1 final status: " + ord1.getStatus());

        // --- Cart for CU1 (Ana, REGULAR) ---
        System.out.println("\n--- Cart Operations for CU1 (Ana) ---");
        Cart cart2 = service.createCart("CU1");
        service.addToCart(cart2, "P3", 2);  // Mug x2 = 40
        service.addToCart(cart2, "P2", 3);  // Pen x3 = 15
        System.out.println("Cart2 subtotal: " + String.format("%.2f", cart2.getSubtotal()));

        // --- Order ORD2 from cart2 ---
        System.out.println("\n--- Creating Order ORD2 ---");
        Order ord2 = service.createOrderFromCartWithId(cart2, "ORD2");
        System.out.println("ORD2 subtotal: " + String.format("%.2f", ord2.getSubtotal()));

        service.applyShipping(ord2, "EXPRESS");
        System.out.println("ORD2 shipping cost (EXPRESS): " + String.format("%.2f", ord2.getShippingCost()));
        System.out.println("ORD2 promotion discount: " + String.format("%.2f", ord2.getPromotionDiscount()));
        System.out.println("ORD2 final total: " + String.format("%.2f", ord2.getFinalTotal()));

        // --- Cancel ORD2 ---
        System.out.println("\n--- Cancelling ORD2 (before payment) ---");
        service.cancelOrder("ORD2");
        System.out.println("ORD2 final status: " + ord2.getStatus());

        // --- Return for ORD1 ---
        System.out.println("\n--- Creating Return for ORD1 ---");
        ReturnRequest ret1 = service.createReturnRequest("ORD1");
        System.out.println("Return request identifier: " + ret1.getId());

        service.addReturnItem(ret1, "P1", 2);
        System.out.println("Returned product P1, quantity: 2");

        System.out.println("Refund total: " + String.format("%.2f", ret1.getRefundTotal()));

        service.processRefund(ret1);
        System.out.println("Refund processed for return " + ret1.getId());

        // --- Restock ---
        System.out.println("\n--- Restocking Operation ---");
        RestockEntry restock1 = service.performRestock("P3", "SU1", 20, 10.0);
        System.out.println("Restock entry identifier: " + restock1.getId());
        System.out.println("Restocked product: " + restock1.getProduct().getId() + ", quantity: " + restock1.getQuantity());
        System.out.println("Restock total cost: " + String.format("%.2f", restock1.getTotalCost()));

        // --- Manual Cashflow Outflow ---
        System.out.println("\n--- Manual Cashflow Outflow ---");
        service.registerManualOutflow(150.0, "Office rent - July");
        System.out.println("Manual outflow registered: 150.00 - Office rent - July");

        // --- CSV Export ---
        System.out.println("\n--- CSV Export ---");
        csvExportService.exportInventory("inventory.csv");
        csvExportService.exportCashflow("cashflow.csv");

        // --- Invalid Operation Demonstration ---
        System.out.println("\n--- Attempting Invalid Operation (pay cancelled order ORD2) ---");
        try {
            service.registerPayment("ORD2", PaymentMethod.CASH);
            System.out.println("ERROR: Should have been rejected!");
        } catch (IllegalStateException e) {
            System.out.println("Invalid operation correctly rejected: " + e.getMessage());
        }

        // --- Final Stock Values ---
        System.out.println("\n--- Final Stock Values ---");
        System.out.println("P1 stock: " + dataStore.findProductById("P1").getStock());
        System.out.println("P2 stock: " + dataStore.findProductById("P2").getStock());
        System.out.println("P3 stock: " + dataStore.findProductById("P3").getStock());
        System.out.println("P4 stock: " + dataStore.findProductById("P4").getStock());
        System.out.println("P5 stock: " + dataStore.findProductById("P5").getStock());

        // --- Reports ---
        System.out.println();
        reportService.printStockReport();
        System.out.println();
        reportService.printSalesByProductReport();
        System.out.println();
        reportService.printOrdersByStatusReport();
        System.out.println();
        reportService.printCashflowSummaryReport();
        System.out.println();
        reportService.printReturnsRefundsReport();
        System.out.println();
        reportService.printRestockReport();

        System.out.println("\n========================================");
        System.out.println("  SCENARIO COMPLETE");
        System.out.println("========================================");
    }
}
