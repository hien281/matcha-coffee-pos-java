import java.time.LocalDateTime;

public class ShiftLog {
    private String employeeId;
    private String employeeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalRevenue;
    private long totalDurationMinutes;

    // Constructor
    public ShiftLog(String employeeId, String employeeName, LocalDateTime startTime,
                    LocalDateTime endTime, double totalRevenue, long totalDurationMinutes) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalRevenue = totalRevenue;
        this.totalDurationMinutes = totalDurationMinutes;
    }

    // Getters
    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public double getTotalRevenue() { return totalRevenue; }

    // Phương thức tạo chuỗi hiển thị cho báo cáo lịch sử
    public String toReportString() {
        java.text.NumberFormat currencyFormat = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
        long hours = totalDurationMinutes / 60;
        long minutes = totalDurationMinutes % 60;

        return String.format("ID: %s | Name: %s\n" +
                        "Revenue: %s | Orders: %d min\n" + // Cần đếm số đơn hàng nếu bạn muốn hiển thị
                        "Time: %s to %s\n",
                employeeId, employeeName,
                currencyFormat.format(totalRevenue), totalDurationMinutes,
                startTime.toLocalTime().toString(), endTime.toLocalTime().toString());
    }
}
