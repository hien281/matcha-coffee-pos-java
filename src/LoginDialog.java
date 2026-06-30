import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.border.*;


public class LoginDialog extends JDialog {
        private final Color PRIMARY_COLOR = new Color(255, 140, 0);
        private final String FONT_NAME = "Segoe UI";

        private JTextField idField;
        private JPasswordField passwordField;

        public LoginDialog(JFrame parent) {
            super(parent, "Employee Login", true);


            // --- CẤU HÌNH CƠ BẢN (Kích thước lớn hơn, dạng dọc) ---
            setTitle("Matcha' Coffee - Employee Login");
            // Kích thước dạng dọc (Rộng 450, Cao 550)
            setSize(450, 550);
            setLayout(new BorderLayout());
            setResizable(false);
            setLocationRelativeTo(parent);
            getContentPane().setBackground(Color.WHITE);

            // --- 1. HEADER (Logo + Tiêu đề) ---
            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
            headerPanel.setBackground(Color.WHITE);
            headerPanel.setBorder(new EmptyBorder(30, 0, 20, 0));
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/logo.png"));
            Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);

            // Icon (Thay bằng chữ cho đơn giản, bạn có thể thay bằng ImageIcon nếu có file)
            JLabel iconLabel = new JLabel(new ImageIcon(img));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            iconLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 60));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel logoLabel = new JLabel("Matcha' Coffee");
            logoLabel.setFont(new Font(FONT_NAME, Font.BOLD, 28));
            logoLabel.setForeground(Color.BLACK);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subLabel = new JLabel("Employee Login");
            subLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
            subLabel.setForeground(Color.DARK_GRAY);
            subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            headerPanel.add(iconLabel);
            headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            headerPanel.add(logoLabel);
            headerPanel.add(subLabel);

            // THÊM 3 DÒNG GHI CHÚ
            headerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            JLabel lblNote = new JLabel("(Dành cho giảng viên)");
            lblNote.setFont(new Font(FONT_NAME, Font.ITALIC, 11));
            lblNote.setForeground(new Color(150, 150, 150));
            lblNote.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblId = new JLabel("ID: TI0003");
            lblId.setFont(new Font(FONT_NAME, Font.PLAIN, 12));
            lblId.setForeground(new Color(150, 150, 150));
            lblId.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblPass = new JLabel("Pass: 1234");
            lblPass.setFont(new Font(FONT_NAME, Font.PLAIN, 12));
            lblPass.setForeground(new Color(150, 150, 150));
            lblPass.setAlignmentX(Component.CENTER_ALIGNMENT);

            headerPanel.add(iconLabel);
            headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            headerPanel.add(logoLabel);
            headerPanel.add(subLabel); // Chữ Employee Login hiện ở đây
            headerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Khoảng cách nhỏ
            headerPanel.add(lblNote);
            headerPanel.add(lblId);
            headerPanel.add(lblPass);

            add(headerPanel, BorderLayout.NORTH);


            // --- 2. FORM INPUTS (2 ô) ---
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(new EmptyBorder(0, 50, 20, 50)); // Padding hai bên

            // Employee ID (Dòng 64)
            // Gọi hàm và gán cho JTextField
            idField = createRoundedInputField("Employee ID", false);
            idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // Chiều cao cố định
            formPanel.add(idField);
            formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            // Password (Dòng 70)
            // Gọi hàm, ép kiểu về JPasswordField
            passwordField = (JPasswordField) createRoundedInputField("Password", true);
            passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // Chiều cao cố định
            formPanel.add(passwordField);

            add(formPanel, BorderLayout.CENTER);


            // --- 3. FOOTER (Buttons) ---
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            buttonPanel.setBackground(Color.WHITE);

            // Login Button
            JButton loginButton = createStyledButton("Login", PRIMARY_COLOR, Color.WHITE);
            loginButton.setFont(new Font(FONT_NAME, Font.BOLD, 18));
            loginButton.setPreferredSize(new Dimension(150, 45));
            loginButton.addActionListener(e -> handleLogin());

            // Cancel Button (Sử dụng màu xám nhạt, viền nhẹ)
            JButton cancelButton = createStyledButton("Cancel", new Color(220, 220, 220), Color.BLACK);
            cancelButton.setFont(new Font(FONT_NAME, Font.BOLD, 14));
            cancelButton.setPreferredSize(new Dimension(100, 45));
            cancelButton.addActionListener(e -> dispose());

            buttonPanel.add(loginButton);
            buttonPanel.add(cancelButton);
            add(buttonPanel, BorderLayout.SOUTH);

            getRootPane().setDefaultButton(loginButton);
            setFocusable(true);
            requestFocusInWindow();
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    getContentPane().requestFocusInWindow();
                }
            });
        }

    private void showCustomMessageDialog(String title, String messageHtml, Color messageColor) {
        // 1. TẠO NÚT OK BO GÓC BẰNG HÀM createStyledButton
        JButton okButton = createStyledButton("OK", new Color(220, 220, 220), Color.BLACK);
        okButton.setFont(new Font(FONT_NAME, Font.BOLD, 14));
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
        JLabel messageLabel = new JLabel("<html><body style='font-family:Segoe UI; font-weight:bold; font-size:18pt;'>" +
                "<center>" + messageHtml + "</center></body></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font(FONT_NAME, Font.BOLD, 16));
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
        dialog.setSize(new Dimension(380, dialog.getHeight())); // Kích thước đẹp
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleLogin() {
        boolean idPlaceholder =
                Boolean.TRUE.equals(idField.getClientProperty("isPlaceholder"));
        boolean pwPlaceholder =
                Boolean.TRUE.equals(passwordField.getClientProperty("isPlaceholder"));

        if (idPlaceholder || pwPlaceholder) {
            showCustomMessageDialog("Error: Missing Credentials", "ID and Password <font color='red'>cannot be empty.</font>", Color.RED);
            return;
        }

        String id = idField.getText();
        String password = new String(passwordField.getPassword());
        String passwordTrimmed = password.trim();

        // ----------------------------------------------------
        // 1. Xử lý ID and Password cannot be empty
        // ----------------------------------------------------
        if (id.isEmpty() || id.equals("Employee ID") || passwordTrimmed.isEmpty()) {

            String emptyMessage = "<html><center><font size='+1'><b>ID and Password</b></font><font color='red' size='+1'><b> cannot be empty.</b></font></center></html>";

            showCustomMessageDialog(
                    "Error: Missing Credentials",
                    emptyMessage,
                    Color.RED
            );
            return;
        }

        // Logic đăng nhập
        if (SessionManager.getInstance().login(id, passwordTrimmed)) {
            // ----------------------------------------------------
            // 2. Xử lý Login Successful
            // ----------------------------------------------------
            String employeeName = SessionManager.getInstance().getCurrentEmployee().getName();

            String successMessage = "<html><center><font size='+2'><b>Welcome, </b></font><font color='#FF8C00' size='+2'><b>"
                    + employeeName
                    + "</b></font><font size='+2'><b>!</b></font></center></html>";

            showCustomMessageDialog(
                    "Matcha' Coffee - Login Successful",
                    successMessage,
                    new Color(255, 140, 0) // Màu cam
            );

            dispose();
        } else {
            // ----------------------------------------------------
            // 3. Xử lý Login Failed
            // ----------------------------------------------------
            String failedMessage = "<html><center><font size='+1'><b>Invalid Employee ID</b></font><font color='red' size='+1'><b> or Password.</b></font></center></html>";

            showCustomMessageDialog(
                    "Login Failed",
                    failedMessage,
                    Color.RED
            );
            passwordField.setText(""); // Xóa mật khẩu đã nhập
            resetToPlaceholder(idField, "Employee ID", false);
            resetToPlaceholder(passwordField, "Password", true);
        }
    }


    // --- HÀM TẠO BUTTON CÓ BO GÓC ---
    private JButton createStyledButton(String text, Color bg, Color fg) {
        final Color originalBg = bg;

        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Vẽ nền bo góc (radius 10)
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Vẽ viền nhẹ cho nút Cancel
                if (text.equals("Cancel")) {
                    g2.setColor(new Color(200, 200, 200));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
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
        // Tùy chỉnh padding để chữ không bị sát lề
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ===================================
        // THÊM LOGIC HOVER ĐỔI MÀU
        // ===================================
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color normalBg = originalBg; // Lưu màu nền ban đầu

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                Color currentColor = btn.getBackground();

                // Tính toán màu đậm hơn (giảm giá trị RGB đi 20-30 đơn vị)
                int r = Math.max(0, currentColor.getRed() - 30);
                int g = Math.max(0, currentColor.getGreen() - 30);
                int b = Math.max(0, currentColor.getBlue() - 30);

                Color hoverColor = new Color(r, g, b);
                btn.setBackground(hoverColor);
                btn.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Trở về màu ban đầu
                btn.setBackground(normalBg);
                btn.repaint();
            }

            // Đảm bảo màu nền được lưu đúng (ví dụ nếu màu bị thay đổi bởi code khác)
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                // Cập nhật lại màu nền ban đầu khi nhả chuột (để giữ màu hover nếu chuột vẫn trong vùng)
                normalBg = originalBg;
            }
        });

        return btn;
    }


    // --- HÀM TẠO INPUT FIELD CÓ BO GÓC VÀ PLACEHOLDER (FIXED) ---
    private JTextField createRoundedInputField(String placeholder, boolean isPassword) {
        Color placeholderColor = new Color(150, 150, 150);
        JTextField field;

        if (isPassword) {
            JPasswordField pf = new JPasswordField();
            pf.setEchoChar((char) 0);
            field = pf;
        } else {
            field = new JTextField();
        }

        field.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
        field.setOpaque(true);
        field.setBackground(Color.WHITE);
        field.setBorder(new RoundedTextFieldBorder(10, new Color(220, 220, 220)));

        field.setText(placeholder);
        field.setForeground(placeholderColor);
        field.setCaretColor(new Color(0, 0, 0, 0)); // an caret
        field.setCaretPosition(0);
        field.putClientProperty("isPlaceholder", true);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setCaretColor(Color.BLACK);
                    field.putClientProperty("isPlaceholder", false);

                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('●');
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(placeholderColor);
                    field.setCaretColor(new Color(0, 0, 0, 0)); // an caret
                    field.putClientProperty("isPlaceholder", true);

                    if (isPassword) {
                        JPasswordField pf = (JPasswordField) field;
                        pf.setEchoChar((char) 0);
                        pf.setFont(new Font(FONT_NAME, Font.PLAIN, 16)); // Trả về font nhỏ cho chữ Password
                        pf.setBorder(new RoundedTextFieldBorder(10, new Color(220, 220, 220)));
                    }
                }
            }
        });

        field.setFocusable(true);
        return field;
    }
    private void resetToPlaceholder(JTextField field, String placeholder, boolean isPassword) {
        Color placeholderColor = new Color(150, 150, 150);

        field.setText(placeholder);
        field.setForeground(placeholderColor);
        field.setCaretColor(new Color(0, 0, 0, 0));
        field.putClientProperty("isPlaceholder", true);

        if (isPassword && field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0);
        }
    }

    // --- CUSTOM BORDER CHO TEXT FIELD (FIXED) ---
    static class RoundedTextFieldBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        public RoundedTextFieldBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }
    }
}