import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuManager {
    private final Map<String, Product> productMap;

    public MenuManager() {
        this.productMap = new HashMap<>();
        // Khởi tạo dữ liệu mẫu khi ứng dụng chạy
        initializeSampleData();
    }

    private void initializeSampleData() {
        //coffee
        addProduct(new Product("C01", "Espresso", 25000, "/images/C1.png", "Coffee"));
        addProduct(new Product("C02", "Matcha Latte", 48000, "/images/C2.png", "Coffee"));
        addProduct(new Product("C03", "Americano",35000 , "/images/C3.png", "Coffee"));
        addProduct(new Product("C04", "Cappuccino",45000 , "/images/C4.png", "Coffee" ));
        addProduct(new Product("C05", "Latte",45000 , "/images/C5.png", "Coffee"));
        addProduct(new Product("C06", "Mocha",50000 , "/images/C6.png", "Coffee"));
        addProduct(new Product("C07", "Cold Brew",40000 , "/images/C7.png", "Coffee"));
        addProduct(new Product("C08", "Flat White",48000 , "/images/C8.png", "Coffee"));
        addProduct(new Product("C09", "Macchiato",42000 , "/images/C9.png", "Coffee"));
        addProduct(new Product("C10", "Irish Coffee",60000 , "/images/C10.png", "Coffee"));

        //tea
        addProduct(new Product("T02", "Peach Tea", 35000, "/images/T2.png", "Tea"));
        addProduct(new Product("T01", "Bubble Tea", 39000, "/images/T1.png", "Tea"));
        addProduct(new Product("T03", "Oolong Tea",30000 , "/images/T3.png", "Tea"));
        addProduct(new Product("T04", "Green Tea", 30000, "/images/T4.png", "Tea"));
        addProduct(new Product("T05", "Black Tea",32000 , "/images/T5.png", "Tea"));
        addProduct(new Product("T06", "Lychee Tea",38000 ,"/images/T6.png", "Tea"));
        addProduct(new Product("T07", "Mango Tea",40000 , "/images/T7.png", "Tea"));


        //smoothie
        addProduct(new Product("S01", "Kiwi Smoothie", 55000, "/images/S1.png", "Smoothie"));
        addProduct(new Product("S02", "Strawberry Smoothie",58000, "/images/S2.png", "Smoothie"));
        addProduct(new Product("S03", "Mango Smoothie", 57000, "/images/S3.png", "Smoothie"));
        addProduct(new Product("S04", "Banana Smoothie",52000, "/images/S4.png", "Smoothie"));
        addProduct(new Product("S05","Avocado Smoothie", 60000, "/images/S5.png", "Smoothie"));
        addProduct(new Product("S06","Berry Mix Smoothie", 62000, "/images/S6.png", "Smoothie"));
        addProduct(new Product("S07","Pineapple Smoothie", 55000, "/images/S7.png", "Smoothie"));
        addProduct(new Product("S08","Taro Smoothie", 54000, "/images/S8.png", "Smoothie"));
        addProduct(new Product("S09","Chocolate Smoothie", 59000, "/images/S9.png", "Smoothie"));
    }

    // --- Chức năng Thêm/Sửa/Xóa ---

    public void addProduct(Product product) {
        productMap.put(product.getId(), product);
    }

    public void removeProduct(String productId) {
        productMap.remove(productId);
    }

    public Product getProductById(String productId) {
        return productMap.get(productId);
    }

    /**
     * Tìm kiếm sản phẩm theo tên (không phân biệt chữ hoa/thường).
     */
    public List<Product> searchProducts(String keyword) {
        List<Product> results = new ArrayList<>();
        String lowerCaseKeyword = keyword.toLowerCase();

        for (Product product : productMap.values()) {
            if (product.getName().toLowerCase().contains(lowerCaseKeyword) ||
                    product.getCategory().toLowerCase().contains(lowerCaseKeyword)) {
                results.add(product);
            }
        }
        return results;
    }

    /** Trả về danh sách tất cả sản phẩm */
    public List<Product> getAllProducts() {
        return new ArrayList<>(productMap.values());
    }
}
