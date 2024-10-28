import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.DecimalFormat;

class Asset {
    String type;
    String name;
    double quantity;
    double value;

    Asset(String type, String name, double quantity) {
        this.type = type;
        this.name = name;
        this.quantity = quantity;
    }
}

class Stock extends Asset {
    String symbol;
    double currentPrice;
    
    Stock(String name, String symbol, double quantity) {
        super("STOCK", name, quantity);
        this.symbol = symbol;
    }
}

class PreciousMetal extends Asset {
    double currentPrice;
    
    PreciousMetal(String type, double quantity) {
        super(type, type, quantity);
    }
}

class FixedDeposit extends Asset {
    double principal;
    double rate;
    int tenure;

    FixedDeposit(double principal, double rate, int tenure) {
        super("FD", "Fixed Deposit", 1);
        this.principal = principal;
        this.rate = rate;
        this.tenure = tenure;
    }
}

public class NetWorthCalculator {
    private List<Asset> assets;
    private DecimalFormat df = new DecimalFormat("#,##,###.##");

    public NetWorthCalculator() {
        assets = new ArrayList<>();
    }

    public void addStock(String name, String symbol, double quantity) {
        assets.add(new Stock(name, symbol, quantity));
    }

    public void addGold(double quantity) {
        assets.add(new PreciousMetal("GOLD", quantity));
    }

    public void addSilver(double quantity) {
        assets.add(new PreciousMetal("SILVER", quantity));
    }

    public void addFixedDeposit(double principal, double rate, int tenure) {
        assets.add(new FixedDeposit(principal, rate, tenure));
    }

    // Simulated stock prices for demonstration
    private double getStockPrice(String symbol) {
        Map<String, Double> mockPrices = new HashMap<>();
        mockPrices.put("RELIANCE", 2432.55);
        mockPrices.put("TCS", 3891.45);
        mockPrices.put("HDFCBANK", 1521.30);
        mockPrices.put("INFY", 1432.75);
        mockPrices.put("BHARTIARTL", 1123.45);
        return mockPrices.getOrDefault(symbol, 0.0);
    }

    // Simulated metal prices for demonstration
    private double getMetalPrice(String metal) {
        if (metal.equals("GOLD")) {
            return 5525.75; // Price per gram in INR
        } else {
            return 65.32; // Price per gram in INR
        }
    }

    private double calculateFDValue(FixedDeposit fd) {
        double interest = (fd.principal * fd.rate * fd.tenure) / (12 * 100);
        return fd.principal + interest;
    }

    public void displayDetailedNetWorth() {
        System.out.println("\n============== DETAILED NET WORTH STATEMENT ==============\n");
        
        double totalStockValue = 0;
        double totalGoldValue = 0;
        double totalSilverValue = 0;
        double totalFDValue = 0;

        // Display Stocks
        System.out.println("STOCKS:");
        System.out.println("----------------------------------------");
        System.out.printf("%-15s %-10s %-12s %-15s%n", "Company", "Quantity", "Price(₹)", "Value(₹)");
        System.out.println("----------------------------------------");
        
        for (Asset asset : assets) {
            if (asset instanceof Stock) {
                Stock stock = (Stock) asset;
                stock.currentPrice = getStockPrice(stock.symbol);
                stock.value = stock.currentPrice * stock.quantity;
                totalStockValue += stock.value;
                
                System.out.printf("%-15s %-10.0f %-12s %-15s%n", 
                    stock.symbol, 
                    stock.quantity, 
                    df.format(stock.currentPrice), 
                    df.format(stock.value));
            }
        }
        System.out.println("----------------------------------------");
        System.out.println("Total Stock Value: ₹" + df.format(totalStockValue));
        
        // Display Precious Metals
        System.out.println("\nPRECIOUS METALS:");
        System.out.println("----------------------------------------");
        System.out.printf("%-15s %-10s %-12s %-15s%n", "Metal", "Grams", "Price/g(₹)", "Value(₹)");
        System.out.println("----------------------------------------");
        
        for (Asset asset : assets) {
            if (asset instanceof PreciousMetal) {
                PreciousMetal metal = (PreciousMetal) asset;
                metal.currentPrice = getMetalPrice(metal.type);
                metal.value = metal.currentPrice * metal.quantity;
                
                if (metal.type.equals("GOLD")) {
                    totalGoldValue += metal.value;
                } else {
                    totalSilverValue += metal.value;
                }
                
                System.out.printf("%-15s %-10.2f %-12s %-15s%n", 
                    metal.type, 
                    metal.quantity, 
                    df.format(metal.currentPrice), 
                    df.format(metal.value));
            }
        }
        System.out.println("----------------------------------------");
        System.out.println("Total Precious Metals Value: ₹" + df.format(totalGoldValue + totalSilverValue));
        
        // Display Fixed Deposits
        System.out.println("\nFIXED DEPOSITS:");
        System.out.println("----------------------------------------------------------------");
        System.out.printf("%-15s %-15s %-10s %-15s %-15s%n", "Principal(₹)", "Rate(%)", "Tenure(M)", "Interest(₹)", "Value(₹)");
        System.out.println("----------------------------------------------------------------");
        
        for (Asset asset : assets) {
            if (asset instanceof FixedDeposit) {
                FixedDeposit fd = (FixedDeposit) asset;
                double interest = (fd.principal * fd.rate * fd.tenure) / (12 * 100);
                fd.value = calculateFDValue(fd);
                totalFDValue += fd.value;
                
                System.out.printf("%-15s %-15.2f %-10d %-15s %-15s%n", 
                    df.format(fd.principal), 
                    fd.rate, 
                    fd.tenure, 
                    df.format(interest), 
                    df.format(fd.value));
            }
        }
        System.out.println("----------------------------------------------------------------");
        System.out.println("Total FD Value: ₹" + df.format(totalFDValue));
        
        // Display Total Net Worth
        double totalNetWorth = totalStockValue + totalGoldValue + totalSilverValue + totalFDValue;
        System.out.println("\n================== NET WORTH SUMMARY ==================");
        System.out.printf("Stocks:          ₹%s (%d%%)\n", df.format(totalStockValue), (int)((totalStockValue/totalNetWorth) * 100));
        System.out.printf("Gold:            ₹%s (%d%%)\n", df.format(totalGoldValue), (int)((totalGoldValue/totalNetWorth) * 100));
        System.out.printf("Silver:          ₹%s (%d%%)\n", df.format(totalSilverValue), (int)((totalSilverValue/totalNetWorth) * 100));
        System.out.printf("Fixed Deposits:  ₹%s (%d%%)\n", df.format(totalFDValue), (int)((totalFDValue/totalNetWorth) * 100));
        System.out.println("-------------------------------------------------------");
        System.out.println("TOTAL NET WORTH: ₹" + df.format(totalNetWorth));
        System.out.println("=======================================================");
    }

    public static void main(String[] args) {
        NetWorthCalculator calculator = new NetWorthCalculator();
        
        // Adding sample stocks
        calculator.addStock("Reliance Industries", "RELIANCE", 100);
        calculator.addStock("Tata Consultancy Services", "TCS", 50);
        calculator.addStock("HDFC Bank", "HDFCBANK", 200);
        calculator.addStock("Infosys", "INFY", 150);
        calculator.addStock("Bharti Airtel", "BHARTIARTL", 300);
        
        // Adding precious metals (in grams)
        calculator.addGold(100);    // 100 grams of gold
        calculator.addSilver(1000); // 1 kg of silver
        
        // Adding FDs (principal, interest rate, tenure in months)
        calculator.addFixedDeposit(100000, 6.5, 12);  // 1 lakh for 1 year at 6.5%
        calculator.addFixedDeposit(200000, 7.0, 24);  // 2 lakhs for 2 years at 7%
        calculator.addFixedDeposit(500000, 7.5, 36);  // 5 lakhs for 3 years at 7.5%
        
        // Display detailed net worth
        calculator.displayDetailedNetWorth();
    }
}