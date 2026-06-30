import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentDialog extends JDialog {
    private boolean paymentConfirmed = false;
    private double cashReceived = 0.0;
    private double change = 0.0;
    private final double totalDue;

    private JTextField cashField;
    private JLabel changeLabel;

    private final Color PRIMARY_COLOR = new Color(255, 140, 0);

    public PaymentDialog(JFrame parent, double totalDue) {
        super(parent, "Payment", true);
        this.totalDue = totalDue;

        setTitle("Confirm Payment");
        // ĐÃ SỬA: Tăng kích thước tổng thể để tránh cắt xén
        setSize(480, 300);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE); // Thiết lập màu nền

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // --- Panel Tổng tiền ---
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(Color.WHITE);
        JLabel totalText = new JLabel("TOTAL DUE:", SwingConstants.CENTER);
        totalText.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel totalLabel = new JLabel(currencyFormat.format(totalDue), SwingConstants.CENTER);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLabel.setForeground(PRIMARY_COLOR);

        topPanel.add(totalText);
        topPanel.add(totalLabel);
        add(topPanel, BorderLayout.NORTH);

        // --- Panel Input tiền mặt (Dùng GridBagLayout để kiểm soát bố cục) ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new EmptyBorder(5, 20, 5, 20));
        inputPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5); // Tăng padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Hàng 1: Cash Received (Label) ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        JLabel cashLabel = new JLabel("Cash Received:", SwingConstants.RIGHT);
        cashLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputPanel.add(cashLabel, gbc);

        // --- Hàng 1: Cash Field (Input bo góc & Placeholder) ---
        cashField = createRoundedInputField("Cash Received");
        // Gán listener và document listener
        cashField.addActionListener(e -> updateChange());
        cashField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateChange(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateChange(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateChange(); }
        });

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Cho phép ô nhập giãn nở
        inputPanel.add(cashField, gbc);


        // --- Hàng 2: Change (Label) ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel changeText = new JLabel("Change:", SwingConstants.RIGHT);
        changeText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputPanel.add(changeText, gbc);

        // --- Hàng 2: Change Value ---
        changeLabel = new JLabel(currencyFormat.format(0), SwingConstants.LEFT);
        changeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        changeLabel.setForeground(new Color(0, 150, 0));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        inputPanel.add(changeLabel, gbc);

        add(inputPanel, BorderLayout.CENTER);

        // --- Panel Nút (Đã sử dụng createStyledButton) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(Color.WHITE);

        // Nút Confirm Payment (Màu chính, bo góc, hover)
        JButton confirmButton = createStyledButton("Confirm Payment", PRIMARY_COLOR, Color.WHITE);
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmButton.addActionListener(e -> handleConfirm());

        // Nút Cancel (Màu xám nhạt, bo góc, hover)
        JButton cancelButton = createStyledButton("Cancel", new Color(220, 220, 220), Color.BLACK);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.addActionListener(e -> {
            paymentConfirmed = false;
            dispose();
        });

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // --- HÀM TẠO INPUT FIELD CÓ BO GÓC VÀ PLACEHOLDER (FIXED LỖI NHẤP CHUỘT) ---
    private JTextField createRoundedInputField(String placeholder) {
        final Color placeholderColor = new Color(150, 150, 150);
        JTextField field = new JTextField();

        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setOpaque(true);
        field.setBackground(Color.WHITE);
        field.setBorder(new RoundedTextFieldBorder(10, new Color(220, 220, 220)));

        // Đặt chiều cao mặc định cho field (để giúp GridBagLayout)
        field.setPreferredSize(new Dimension(200, 35));

        // Thiết lập Placeholder ban đầu
        field.setText(placeholder);
        field.setForeground(placeholderColor);
        field.setCaretColor(new Color(0, 0, 0, 0)); // ẨN CON TRỎ
        //field.setCaretPosition(0); // Đặt con trỏ ở đầu

        field.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                field.requestFocusInWindow();
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setCaretColor(Color.BLACK);
                }
            }
        });

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setCaretColor(Color.BLACK); // HIỆN CON TRỎ
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(placeholderColor);
                    field.setCaretColor(new Color(0, 0, 0, 0)); // ẨN CON TRỎ
                }
            }
        });

        return field;
    }

    private void updateChange() {
        try {
            // Loại bỏ dấu phẩy/chấm phân cách hàng ngàn để parse
            String text = cashField.getText().replaceAll("[^\\d\\.]", "");
            if (text.isEmpty() || text.equals("Enter Cash Received...")) { // Kiểm tra cả placeholder
                cashReceived = 0.0;
            } else {
                cashReceived = Double.parseDouble(text);
            }

            change = cashReceived - totalDue;

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            changeLabel.setText(currencyFormat.format(change));
            changeLabel.setForeground(change >= 0 ? new Color(0, 150, 0) : Color.RED); // Đổi màu Change

        } catch (NumberFormatException ex) {
            changeLabel.setText("Invalid Input");
            changeLabel.setForeground(Color.RED);
            cashReceived = 0.0;
            change = 0.0;
        }
    }

    private void handleConfirm() {
        // Kiểm tra placeholder trước khi updateChange (dù updateChange đã kiểm tra)
        if (cashField.getText().equals("Cash Received") || cashField.getText().isEmpty()) {
            showCustomErrorDialog("Error", "Please enter the amount received.");
            return;
        }

        updateChange(); // Tính toán lại lần cuối
        if (cashReceived < totalDue) {
            showCustomErrorDialog("Error", "Cash received is less than the total amount.");
        } else {
            paymentConfirmed = true;
            dispose();
        }
    }

    public boolean isPaymentConfirmed() {
        return paymentConfirmed;
    }

    public double getCashReceived() {
        return cashReceived;
    }

    public double getChange() {
        return change;
    }
    private void showCustomErrorDialog(String title, String message) {
        // 1. TẠO NÚT OK BO GÓC (Màu đỏ nhạt cho lỗi)
        Color errorBg = new Color(240, 128, 128);
        JButton okButton = createStyledButton("OK", errorBg, Color.WHITE);
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setPreferredSize(new Dimension(80, 40));

        // 2. Xây dựng Dialog tùy chỉnh
        JDialog dialog = new JDialog(this, title, true);
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        // Tạo panel chứa nội dung
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 15, 30));
        contentPanel.setBackground(Color.WHITE);

        // Nội dung (giữa)
        String formattedMessage = "<html><center><font size='+1' color='red'><b>" + message + "</b></font></center></html>";
        JLabel messageLabel = new JLabel(formattedMessage, SwingConstants.CENTER);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        // Panel nút bấm (dưới)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Bắt sự kiện click để đóng dialog
        okButton.addActionListener(e -> dialog.dispose());

        dialog.getContentPane().add(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    // --- HÀM TẠO BUTTON CÓ BO GÓC VÀ HOVER (ĐỊNH NGHĨA LẠI TRONG PaymentDialog) ---
    private JButton createStyledButton(String text, Color bg, Color fg) {
        final Color originalBg = bg;

        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Vẽ nền bo góc (radius 10)
                int radius = 10;
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

                // Vẽ viền nhẹ cho nút Cancel
                if (text.equals("Cancel")) {
                    g2.setColor(new Color(200, 200, 200));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // THÊM LOGIC HOVER ĐỔI MÀU
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color normalBg = originalBg;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                Color currentColor = btn.getBackground();

                int r = Math.max(0, currentColor.getRed() - 30);
                int g = Math.max(0, currentColor.getGreen() - 30);
                int b = Math.max(0, currentColor.getBlue() - 30);

                Color hoverColor = new Color(r, g, b);
                btn.setBackground(hoverColor);
                btn.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(normalBg);
                btn.repaint();
            }
        });

        return btn;
    }

    // --- CUSTOM BORDER CHO TEXT FIELD (ĐÃ SỬA LỖI ĐỊNH NGHĨA LỚP) ---
    static class RoundedTextFieldBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        public RoundedTextFieldBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color);
            g2.setStroke(new BasicStroke(1));
            g2.draw(new RoundRectangle2D.Float(
                    x, y, width - 1, height - 1, radius, radius
            ));

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }
    }
}