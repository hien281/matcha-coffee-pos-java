import java.util.HashMap;
import java.util.Map;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class SessionManager {
    private static SessionManager instance = new SessionManager();
    private Employee currentEmployee;
    private long shiftStartTime;
    private double shiftRevenue;
    private LocalDateTime shiftStartTimeDateTime;
    private List<ShiftLog> shiftHistory;

    // Giả lập danh sách nhân viên (thực tế sẽ lấy từ Database)
    private final Map<String, Employee> employeeDatabase;

    private SessionManager() {
        employeeDatabase = new HashMap<>();
        // Dữ liệu mẫu
        employeeDatabase.put("TI0001", new Employee("TI0001", "Hien", "2811"));
        employeeDatabase.put("TI0002", new Employee("TI0002", "Quyt", "2709"));
        employeeDatabase.put("TI0003", new Employee("TI0003", "Mr. Le", "1234"));
        shiftHistory = new ArrayList<>(); // Khởi tạo danh sách lịch sử
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public boolean login(String id, String password) {
        Employee employee = employeeDatabase.get(id);
        // Kiểm tra xem ID có tồn tại VÀ mật khẩu có khớp không
        if (employee != null && employee.checkPassword(password)) {
            // *** THIẾT LẬP PHIÊN LÀM VIỆC TẠI ĐÂY ***
            this.currentEmployee = employee;
            this.shiftStartTime = System.currentTimeMillis();
            // Lấy thời gian bắt đầu phiên làm việc dưới dạng LocalDateTime
            this.shiftStartTimeDateTime = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(this.shiftStartTime), ZoneId.systemDefault());
            this.shiftRevenue = 0.0;
            return true;
        }
        // Đăng nhập thất bại
        this.currentEmployee = null;
        return false;
    }

    public String getShiftReport() {
        if (this.currentEmployee == null) {
            return "No active shift found.";
        }
        // Tạo nội dung báo cáo
        String report = "--- SHIFT REPORT ---\n"
                + "Employee: " + this.currentEmployee.getName() + "\n"
                + "Employee ID: " + this.currentEmployee.getEmployeeId() + "\n"
                + "----------------------------------\n";
        // Định dạng tiền tệ
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        report += String.format("%-20s %s\n", "Total Revenue:", currencyFormat.format(this.shiftRevenue));
        long durationMillis = System.currentTimeMillis() - this.shiftStartTime;
        long hours = durationMillis / (60 * 60 * 1000);
        long minutes = (durationMillis % (60 * 60 * 1000)) / (60 * 1000);
        report += String.format("Duration: %d hours %d minutes\n", hours, minutes);
        report += "----------------------------------";

        // KHÔNG reset/logout ở đây, chỉ trả về báo cáo
        return report;
    }
    public List<ShiftLog> getShiftHistory() {
        return shiftHistory;
    }
    public void resetShift() {
        if (this.currentEmployee == null) return; // Không làm gì nếu không có ai đăng nhập

        // 1. Tính toán thời gian kết thúc và duration
        LocalDateTime endTime = LocalDateTime.now();
        long durationMinutes = ChronoUnit.MINUTES.between(this.shiftStartTimeDateTime, endTime);

        // 2. TẠO VÀ LƯU LOG CA LÀM VIỆC ĐÃ HOÀN THÀNH
        ShiftLog log = new ShiftLog(
                this.currentEmployee.getEmployeeId(),
                this.currentEmployee.getName(),
                this.shiftStartTimeDateTime,
                endTime,
                this.shiftRevenue,
                durationMinutes
        );
        shiftHistory.add(log); // Thêm vào lịch sử

        // 3. Reset phiên làm việc hiện tại
        this.currentEmployee = null;
        this.shiftRevenue = 0.0;
        this.shiftStartTime = 0;
        this.shiftStartTimeDateTime = null;
    }

    public void recordTransaction(double amount) {
        if (currentEmployee != null) {
            this.shiftRevenue += amount;
        }
    }

    public boolean isLoggedIn() {
        return currentEmployee != null;
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }
}