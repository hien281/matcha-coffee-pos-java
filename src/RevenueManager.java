import java.util.ArrayList;
import java.util.List;

public class RevenueManager {
    private List<Order> completedOrders;

    public RevenueManager() {
        this.completedOrders = new ArrayList<>();
    }

    /**
     * Thêm đơn hàng đã hoàn thành vào danh sách thống kê.
     */
    public void addCompletedOrder(Order order) {
        if ("Completed".equals(order.getStatus())) {
            completedOrders.add(order);
        }
    }

    /**
     * Tính tổng doanh thu từ tất cả các đơn hàng đã hoàn thành.
     */
    public double calculateTotalRevenue() {
        return completedOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    /**
     * Lấy danh sách doanh thu theo ngày (Đây là logic thống kê đơn giản nhất).
     */
    public String generateSimpleRevenueReport() {
        double total = calculateTotalRevenue();
        int count = completedOrders.size();

        StringBuilder report = new StringBuilder();
        report.append("=========================================\n");
        report.append("        SIMPLE REVENUE REPORT            \n");
        report.append("=========================================\n");
        report.append(String.format("Total Completed Orders: %d\n", count));
        report.append(String.format("TOTAL REVENUE: %,.0f VND\n", total));
        report.append("=========================================\n");

        return report.toString();
    }
}
