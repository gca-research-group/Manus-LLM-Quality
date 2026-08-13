package store.export;

import store.model.cashflow.CashflowEntry;
import store.model.catalog.Product;
import store.service.DataStore;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvExportService {

    private DataStore dataStore;

    public CsvExportService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void exportInventory(String filename) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(filename));
            writer.println("ProductId,ProductName,Category,UnitPrice,Stock,Active");
            List products = dataStore.getProducts();
            for (int i = 0; i < products.size(); i++) {
                Product p = (Product) products.get(i);
                writer.println(
                        escapeCsv(p.getId()) + ","
                        + escapeCsv(p.getName()) + ","
                        + escapeCsv(p.getCategory().getName()) + ","
                        + p.getUnitPrice() + ","
                        + p.getStock() + ","
                        + p.isActive()
                );
            }
            System.out.println("Inventory exported to: " + filename);
        } catch (IOException e) {
            System.out.println("Error exporting inventory: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void exportCashflow(String filename) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(filename));
            writer.println("EntryId,Type,Amount,Description");
            List entries = dataStore.getCashflowEntries();
            for (int i = 0; i < entries.size(); i++) {
                CashflowEntry e = (CashflowEntry) entries.get(i);
                writer.println(
                        escapeCsv(e.getId()) + ","
                        + escapeCsv(e.getType().getName()) + ","
                        + String.format("%.2f", e.getAmount()) + ","
                        + escapeCsv(e.getDescription())
                );
            }
            System.out.println("Cashflow exported to: " + filename);
        } catch (IOException e) {
            System.out.println("Error exporting cashflow: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
