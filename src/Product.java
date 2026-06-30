import java.net.URL;
public class Product {
    private final String id;
    private String name;
    private final double price;
    private final String imagePath;
    private String category;

    public Product(String id, String name, double price, String imagePath, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.category = category;
    }

    public double getPriceIncreaseBySize(String size) {
        // S = 0 VND, M = +5000 VND, L = +10000 VND
        return switch (size) {
            case "M" -> 5000;
            case "L" -> 10000;
            default -> 0; // "S"
        };
    }

    public double getToppingPrice(String toppingName) {
        switch (toppingName.toLowerCase()) {
            case "pearl": return 5000;
            case "pudding": return 5000;
            case "jelly": return 5000;
            case "cheese":
            case "cheese foam": return 8000;
        }
        return 0;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImagePath() { return imagePath; }
    public String getCategory() { return category; }

    // Setters (Chỉ cho những trường không phải final)
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
}