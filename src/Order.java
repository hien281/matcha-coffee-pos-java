import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

public class Order {
    private String orderId;
    private Date orderDate;
    private List<OrderItem> items; // Danh sách các món đã chọn
    private double discountRate; // Giảm giá (%)
    private double totalAmount; // Tổng tiền cuối cùng
    private String status; // Trạng thái: Pending, Completed, Cancelled
    private String customerName;

    // Constructor
    public Order(String orderId) {
        this.orderId = orderId;
        this.orderDate = new Date();
        this.items = new ArrayList<>();
        this.discountRate = 0.0;
        this.status = "Pending";
        this.customerName = "Walk in customer ";
        calculateTotalAmount();
    }

    // --- Phương Thức Quản Lý Mục Đơn Hàng (OrderItem) ---

    /** Thêm/Cập nhật một mục vào đơn hàng */
    public void addItem(Product product, int quantity) {
        this.items.add(new OrderItem(product, quantity));
        calculateTotalAmount();
    }

    /** Tính tổng tiền trước giảm giá */
    public double calculateSubtotal() {
        double subtotal = 0;
        for (OrderItem item : items) {
            subtotal += item.calculateSubtotal();
        }
        return subtotal;
    }

    /** Tính và cập nhật Tổng tiền cuối cùng */
    public void calculateTotalAmount() {
        double subtotal = calculateSubtotal();
        double discountAmount = subtotal * (this.discountRate / 100.0);
        this.totalAmount = subtotal - discountAmount;
    }

    // --- Phương Thức Xử Lý Hóa Đơn (Đã tích hợp vào logic ứng dụng) ---

    public String generateInvoiceContent() {
        double subtotal = calculateSubtotal();
        double discountAmount = subtotal * (this.discountRate / 100.0);

        StringBuilder invoice = new StringBuilder();
        invoice.append("====================================\n");
        invoice.append("         SALES RECEIPT              \n");
        invoice.append("====================================\n");
        invoice.append("Order ID: ").append(orderId).append("\n");
        invoice.append("Customer: ").append(customerName).append("\n");
        invoice.append("Time: ").append(orderDate.toString()).append("\n");
        invoice.append("------------------------------------\n");
        invoice.append(String.format("%-25s %5s %10s\n", "Item", "Qty", "Amount"));
        invoice.append("------------------------------------\n");

        for (OrderItem item : items) {
            String itemName = item.getProduct().getName();
            if (item.getCustomizationDetails() != null && !item.getCustomizationDetails().equals("Mặc định")) {
                itemName += " (" + item.getCustomizationDetails() + ")";
            }

            String line = String.format("%-25s %5d %10.0f\n",
                    itemName,
                    item.getQuantity(),
                    item.calculateSubtotal());
            invoice.append(line);
        }

        invoice.append("------------------------------------\n");
        invoice.append(String.format("Subtotal: %23.0f VND\n", subtotal));
        invoice.append(String.format("Discount (%.0f%%): %17.0f VND\n", this.discountRate, discountAmount));
        invoice.append("------------------------------------\n");
        invoice.append(String.format("TOTAL DUE: %22.0f VND\n", this.totalAmount));
        invoice.append("====================================\n");

        return invoice.toString();
    }

    public boolean printInvoiceToFile(String invoiceContent) {
        String fileName = "Invoice_" + this.orderId + ".txt";
        try (FileWriter writer = new FileWriter(fileName)) {
            // 2. Ghi nội dung hóa đơn vào file
            writer.write(invoiceContent);

            // 3. Thông báo lưu thành công
            System.out.println("Receipt saved to: " + fileName);
            return true;

        } catch (IOException e) {
            // 4. Báo lỗi nếu không lưu được
            System.err.println("Error saving invoice file: " + e.getMessage());
            return false;
        }
    }


    public String getOrderId() { return orderId; }
    public List<OrderItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
        calculateTotalAmount();
    }
    public String getStatus() { return status; }
    public void setCustomerName(String name) {
        this.customerName = name;
    }
    public String getCustomerName() {
        return customerName;
    }
}
