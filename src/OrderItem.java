import java.util.ArrayList;
import java.util.List;

public class OrderItem {
    private Product product;
    private int quantity;
    private String customizationDetails;
    private String size;
    private List<String> toppings;

    // Constructor mới để nhận chi tiết tùy chỉnh
    public OrderItem(Product product, int quantity, String customizationDetails, String size, List<String> toppings) {
        this.product = product;
        this.quantity = quantity;
        this.customizationDetails = customizationDetails;
        this.size = size;     // <-- LƯU TRỮ SIZE ĐÃ CHỌN
        this.toppings = new ArrayList<>(toppings);
    }

    // Constructor cũ (nếu không có chi tiết tùy chỉnh)
    public OrderItem(Product p, int q) {
        this(p, q, "Default", "S", new ArrayList<>());
    }

    // --- GETTERS ĐÃ ĐƯỢC THÊM ĐỂ KHẮC PHỤC LỖI TRONG CoffeePOSApp.java ---

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    // Getter cho chi tiết tùy chỉnh (Đã có sẵn)
    public String getCustomizationDetails() {
        return customizationDetails;
    }

    //tính tiền
    public double calculateSubtotal() {
        double base = product.getPrice();
        double sizeCost = product.getPriceIncreaseBySize(size);

        double toppingCost = 0;
        for (String t : toppings) {
            toppingCost += product.getToppingPrice(t);
        }

        double finalPrice = base + sizeCost + toppingCost;

        return finalPrice * quantity;
    }
}