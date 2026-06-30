import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.Dimension;
import java.awt.Component;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class CoffeePOSApp extends JFrame {

    private MenuManager menuManager;
    private RevenueManager revenueManager;
    private Order currentOrder;

    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private JPanel menuContainer;
    private JTextField customerNameField;

    // Colors / fonts
    private final Color PRIMARY_COLOR = new Color(255, 140, 0);
    private final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private final String FONT_NAME = "Segoe UI";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);
            if (SessionManager.getInstance().isLoggedIn()) {
                // Nếu đăng nhập thành công, khởi tạo và hiển thị ứng dụng POS chính
                CoffeePOSApp coffeePOSApp = new CoffeePOSApp();
                coffeePOSApp.setVisible(true);
                SwingUtilities.invokeLater(() -> {
                    coffeePOSApp.getContentPane().requestFocusInWindow();
                });
            } else {
                // Nếu đăng nhập thất bại (nhấn Cancel hoặc sai thông tin), thoát ứng dụng
                System.exit(0);
            }
        });
    }

    public CoffeePOSApp() {
        this.menuManager = new MenuManager();
        this.revenueManager = new RevenueManager();
        this.currentOrder = new Order("O001");

        setTitle("Matcha' Coffee POS System");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);

        JPanel categoryPanel = createCategoryPanel();
        mainPanel.add(categoryPanel, BorderLayout.NORTH);

        menuContainer = createMenuContainer();

        JPanel menuWrapper = new JPanel(new BorderLayout());
        menuWrapper.setBackground(BACKGROUND_COLOR);
        // add right padding of 12 px (increase number to push further)
        menuWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        menuWrapper.add(menuContainer, BorderLayout.CENTER);

        // Wrap into scrollpane with only vertical scroll
        JScrollPane menuScrollPane = new JScrollPane(menuContainer,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        menuScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        menuScrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        menuScrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        menuScrollPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        menuScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        menuScrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        menuScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        menuScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 12));

        JPanel centerLayout = new JPanel(new GridBagLayout());
        centerLayout.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.65;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 0, 2);
        centerLayout.add(menuScrollPane, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.35;
        gbc.weighty = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        JPanel orderArea = createOrderPanel();
        orderArea.setPreferredSize(new Dimension(360, 700));
        centerLayout.add(orderArea, gbc);

        mainPanel.add(centerLayout, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        JPanel managementPanel = createManagementPanel();
        add(managementPanel, BorderLayout.SOUTH);

        loadProductCards(menuManager.getAllProducts(), menuContainer);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Header
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel logoLabel = new JLabel("Matcha' Coffee");
        logoLabel.setFont(new Font(FONT_NAME, Font.BOLD, 26));
        logoLabel.setForeground(PRIMARY_COLOR);
        header.add(logoLabel, BorderLayout.WEST);

        JTextField searchField = new JTextField("Search", 30);
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JButton searchBtn = createRoundedButton("\uD83D\uDD0D", PRIMARY_COLOR, Color.WHITE);
        searchBtn.setPreferredSize(new Dimension(50, 30));

        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().equals("Search")
                    ? ""
                    : searchField.getText();
            List<Product> results = menuManager.searchProducts(keyword);
            loadProductCards(results, menuContainer);
        });

        JPanel searchContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        searchContainer.setBackground(Color.WHITE);
        searchContainer.add(searchField);
        searchContainer.add(searchBtn);

        header.add(searchContainer, BorderLayout.CENTER);

        String empName = "Guest";
        if (SessionManager.getInstance().isLoggedIn()) {
            empName = SessionManager.getInstance().getCurrentEmployee().getName();
        }

        JLabel helloLabel = new JLabel("<html>Hello, <font color='#FF8C00'>" + empName + "</font></html>");
        helloLabel.setFont(new Font(FONT_NAME, Font.BOLD, 16));
        helloLabel.setForeground(new Color(80, 80, 80)); // Màu xám đậm

        // Thêm khoảng cách lề trái cho label để không dính vào ô tìm kiếm
        helloLabel.setBorder(new EmptyBorder(0, 20, 0, 0));

        header.add(helloLabel, BorderLayout.EAST);

        return header;
    }

    // Category bar
    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panel.setBackground(BACKGROUND_COLOR);

        String[] categories = {"All Items", "Coffee", "Tea", "Smoothie"};
        List<JButton> allCategoryButtons = new ArrayList<>();

        ActionListener listener = e -> {
            JButton clicked = (JButton) e.getSource();

            // reset toàn bộ nút
            for (JButton b : allCategoryButtons) {
                b.setBackground(Color.WHITE);
                b.setForeground(Color.DARK_GRAY);
            }

            // set màu cho nút được chọn
            clicked.setBackground(new Color(255, 140, 0));
            clicked.setForeground(Color.WHITE);

            // load lại danh sách
            String cat = clicked.getText();
            List<Product> filtered = cat.equals("All Items")
                    ? menuManager.getAllProducts()
                    : menuManager.getAllProducts().stream()
                    .filter(p -> p.getCategory().equals(cat))
                    .toList();

            loadProductCards(filtered, menuContainer);
            menuContainer.revalidate();
            menuContainer.repaint();
        };

        for (String cat : categories) {
            JButton b = createSimpleRoundedButton(cat, Color.WHITE, Color.DARK_GRAY);
            b.setFont(new Font(FONT_NAME, Font.BOLD, 14));
            b.addActionListener(listener);
            allCategoryButtons.add(b);
            panel.add(b);
        }

        // set default All Items
        JButton first = allCategoryButtons.get(0);
        first.setBackground(new Color(255,140,0));
        first.setForeground(Color.WHITE);

        return panel;
    }

    // Menu Container using GridLayout with 2 columns (fixed)
    private JPanel createMenuContainer() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        // GridLayout rows=0 so it grows vertically, columns=2 for fixed 2-column
        //panel.setLayout(new GridLayout(0, 2, 10, 10));
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 0, 15, 0));
        return panel;
    }

    private void loadProductCards(List<Product> products, JPanel targetPanel) {
        targetPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH; // Căn chỉnh các item lên phía trên
        gbc.fill = GridBagConstraints.HORIZONTAL; // Cho phép các item lấp đầy chiều ngang (trong ô GBC)
        gbc.weightx = 1.0; // Chia đều không gian ngang
        gbc.weighty = 0.0; // QUAN TRỌNG: Không cho item giãn theo chiều dọc
        gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách giữa các ô (tương đương gap 20)

        if (products.isEmpty()) {
            JLabel notFoundLabel = new JLabel("No items found matching your search.", SwingConstants.CENTER);
            notFoundLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));
            notFoundLabel.setForeground(Color.GRAY);

            // Thiết lập GridBagConstraints để căn giữa thông báo
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2; // Chiếm cả 2 cột
            gbc.weighty = 1.0; // Đẩy toàn bộ không gian thừa xuống dưới
            gbc.fill = GridBagConstraints.BOTH; // Cho phép label lấp đầy không gian

            // Đặt kích thước tối thiểu cho Label để nó chiếm đủ không gian trên màn hình
            notFoundLabel.setPreferredSize(new Dimension(300, 100));

            targetPanel.add(notFoundLabel, gbc);

        } else {
            // --- LOGIC HIỂN THỊ SẢN PHẨM (giữ nguyên) ---
            int row = 0;
            int col = 0;

            for (Product product : products) {
                gbc.gridx = col;
                gbc.gridy = row;
                gbc.gridwidth = 1; // Chỉ chiếm 1 cột cho thẻ sản phẩm
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weighty = 0.0; // QUAN TRỌNG: Không cho item giãn theo chiều dọc

                targetPanel.add(createProductCard(product), gbc);
                col++;
                if (col == 2) {
                    col = 0;
                    row++;
                }
            }
        // if odd number of products, add an empty filler panel to keep grid alignment
            if (products.size() % 2 != 0) {
                gbc.gridx = col;
                gbc.gridy = row;
                gbc.weighty = 1.0;

                JPanel filler = new JPanel();
                filler.setBackground(BACKGROUND_COLOR);
                targetPanel.add(filler,gbc);
            } else {
                // Nếu số lượng chẵn, vẫn cần một ô đệm trống ở hàng dưới cùng
                gbc.gridx = 0;
                gbc.gridy = row;
                gbc.gridwidth = 2; // Ô trống này chiếm 2 cột
                gbc.weighty = 1.0;

                JPanel filler = new JPanel();
                filler.setBackground(BACKGROUND_COLOR);
                targetPanel.add(filler, gbc);
            }
        }
        // --- KẾT THÚC LOGIC THÔNG BÁO TÌM KIẾM ---

        targetPanel.revalidate();
        targetPanel.repaint();
    }

    // Create product card with vertical image, rounded components, qty controls under image,
    // size selection, add to cart beside qty controls
    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false); // QUAN TRỌNG: Tắt nền mặc định của JPanel để không bị vệt trắng ở góc
        card.setPreferredSize(new Dimension(380, 260));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, new Color(230, 230, 230)),
                new EmptyBorder(12, 12, 12, 12)));
        // Top: name and price in a horizontal box
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));

        String priceStr = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(product.getPrice());
        final JLabel priceLabel = new JLabel(priceStr.replace("₫", " VND"));
        priceLabel.setFont(new Font(FONT_NAME, Font.BOLD, 17));
        priceLabel.setForeground(PRIMARY_COLOR);

        topRow.add(nameLabel, BorderLayout.WEST);
        topRow.add(priceLabel, BorderLayout.EAST);
        topRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0)); // <-- THÊM DÒNG NÀY (8px khoảng cách dưới)

        card.add(topRow, BorderLayout.NORTH);

        // Center: image (vertical) on left and description/size on right
        JPanel center = new JPanel(new BorderLayout(20, 0));
        center.setBackground(Color.WHITE);

        // Image panel
        JLabel imgLabel;
        try {
            // Nạp ảnh bằng Resource URL
            java.net.URL imgURL = getClass().getResource(product.getImagePath());

            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                Image img = originalIcon.getImage().getScaledInstance(120, 180, Image.SCALE_SMOOTH);
                imgLabel = new JLabel(new ImageIcon(img));
            } else {
                imgLabel = new JLabel("No Image");
                System.err.println("Could not find file: " + product.getImagePath());
            }
        } catch (Exception ex) {
            imgLabel = new JLabel("Error");
        }

        // Wrap image in a panel to give rounded effect
        JPanel imgWrap = new JPanel(new BorderLayout());
        imgWrap.setBackground(BACKGROUND_COLOR);
        imgWrap.setBorder(new RoundedBorder(10, new Color(245, 245, 245)));
        imgWrap.add(imgLabel, BorderLayout.CENTER);
        imgWrap.setPreferredSize(new Dimension(130, 200));

        center.add(imgWrap, BorderLayout.WEST);


        // Right side: description + size options
        JPanel rightSide = new JPanel(new BorderLayout());
        rightSide.setOpaque(false);
        rightSide.setBackground(new Color(0,0,0,0));

        // Center (Size selection + Customization)
        JPanel centerRight = new JPanel();
        centerRight.setLayout(new BoxLayout(centerRight, BoxLayout.Y_AXIS));
        // Make this panel opaque white so transparent children (combos) pick up correct bg
        centerRight.setOpaque(false);
        centerRight.setBackground(new Color(0,0,0,0));

        // Size selection (compact toggles)
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 4));
        sizePanel.setOpaque(false);
        sizePanel.setBackground(new Color(0,0,0,0));
        sizePanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
        JToggleButton smallBtn = createSizeToggle("S");
        JToggleButton mediumBtn = createSizeToggle("M");
        JToggleButton largeBtn = createSizeToggle("L");
        ButtonGroup sizeGroup = new ButtonGroup();
        List<JToggleButton> sizeButtons = List.of(smallBtn, mediumBtn, largeBtn);


        JPanel customizationPanel = new JPanel();
        // Make customization area opaque white so transparent combo boxes don't reveal odd background
        customizationPanel.setOpaque(false);
        customizationPanel.setBackground(new Color(0,0,0,0));
        customizationPanel.setLayout(new BoxLayout(customizationPanel, BoxLayout.Y_AXIS));
        customizationPanel.setBorder(BorderFactory.createEmptyBorder(6, 5, 0, 0));
        // Sugar
        JPanel sugarRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        sugarRow.setOpaque(false);
        sugarRow.setBackground(new Color(0,0,0,0));
        sugarRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        JLabel sugarLabel = new JLabel("Sugar:");
        sugarLabel.setFont(new Font(FONT_NAME, Font.BOLD, 11));
        String[] sugarLevels = {"0%", "30%", "50%", "70%", "100%"};
        JComboBox<String> sugarCombo = new JComboBox<>(sugarLevels);
        sugarCombo.setSelectedIndex(0);
        sugarCombo.setUI(new ModernComboBoxUI());
        sugarCombo.setRenderer(new ModernComboRenderer());

        Object popup = sugarCombo.getUI().getAccessibleChild(sugarCombo, 0);
        if (popup instanceof BasicComboPopup p) {
            // null-check: getList() có thể trả về null trong một vài L&F / thời điểm khởi tạo
            JList<?> list = p.getList();
            if (list != null) {
                // làm trong suốt để tránh JList vẽ nền chữ nhật
                list.setOpaque(false);
                list.setBackground(new Color(0, 0, 0, 0));
                // đảm bảo dùng renderer không-opaque (renderer tự vẽ selection bo tròn)
                list.setCellRenderer(new ModernComboRenderer());
                // selection colors: để chữ chọn cùng tông và nền selection trong suốt
                list.setSelectionForeground(new Color(60, 60, 60));
                list.setSelectionBackground(new Color(0, 0, 0, 0));
            }
        }
        // Use non-opaque combo so renderer/ModernComboBoxUI can draw rounded selection correctly
        sugarCombo.setOpaque(false);
        sugarCombo.setBackground(new Color(0, 0, 0, 0));
        sugarCombo.setPreferredSize(new Dimension(75, 22));
        sugarCombo.setBorder(BorderFactory.createEmptyBorder());
        sugarCombo.setFocusable(false);
        sugarCombo.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
        sugarRow.add(sugarLabel);
        sugarRow.add(sugarCombo);

         // Ice
        JPanel iceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        iceRow.setOpaque(false);
        iceRow.setBackground(new Color(0,0,0,0));
        iceRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        JLabel iceLabel = new JLabel("Ice:");
        iceLabel.setFont(new Font(FONT_NAME, Font.BOLD, 11));
        String[] iceLevels = {"0%", "50%", "100%"};
        JComboBox<String> iceCombo = new JComboBox<>(iceLevels);
        iceCombo.setUI(new ModernComboBoxUI());
        iceCombo.setRenderer(new ModernComboRenderer());

         Object icePopup = iceCombo.getUI().getAccessibleChild(iceCombo, 0);
         if (icePopup instanceof BasicComboPopup p) {
             // mirror sugar handling với null-check và selection colors
             JList<?> list = p.getList();
             if (list != null) {
                 list.setOpaque(false);
                 list.setBackground(new Color(0, 0, 0, 0));
                 list.setCellRenderer(new ModernComboRenderer());
                 list.setSelectionForeground(new Color(60, 60, 60));
                 list.setSelectionBackground(new Color(0, 0, 0, 0));
             }
         }
         iceCombo.setOpaque(false);
         iceCombo.setBackground(new Color(0, 0, 0, 0));
         iceCombo.setPreferredSize(new Dimension(75, 22));
         iceCombo.setBorder(BorderFactory.createEmptyBorder());
         iceCombo.setFont(new Font(FONT_NAME, Font.PLAIN, 11));
         iceRow.add(iceLabel);
         iceRow.add(iceCombo);

        // Topping
        JPanel toppingRow = new JPanel();
        // 1. Dùng BoxLayout: Chấp nhận bị che chứ KHÔNG BAO GIỜ xuống dòng
        toppingRow.setLayout(new BoxLayout(toppingRow, BoxLayout.X_AXIS));
        toppingRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        toppingRow.setOpaque(false);
        toppingRow.setBackground(new Color(0,0,0,0));
        toppingRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        toppingRow.add(Box.createHorizontalStrut(5));
        JLabel toppingLabel = new JLabel("Topping:");
        toppingLabel.setFont(new Font(FONT_NAME, Font.BOLD, 11));
        toppingLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        //toppingLabel.setBorder(BorderFactory.createEmptyBorder(0,1,0,5));
        toppingRow.add(toppingLabel);
        toppingRow.add(Box.createHorizontalStrut(5));

        String[] toppingOptions = {
                "Pearl (+5K)",
                "Pudding (+5K)",
                "Jelly (+5K)",
                "Cheese Foam (+8K)"
        };
        MultiSelectComboBox toppingCombo = new MultiSelectComboBox(toppingOptions);
        toppingCombo.setFont(new Font(FONT_NAME, Font.PLAIN, 11));

        Dimension comboSize = new Dimension(139, 22);
        toppingCombo.setPreferredSize(comboSize);
        toppingCombo.setMaximumSize(comboSize); // <--- LỆNH NÀY CẤM NÓ GIÃN TO
        toppingCombo.setMinimumSize(comboSize);
        toppingCombo.setAlignmentY(Component.CENTER_ALIGNMENT);

        toppingRow.add(toppingCombo);
        toppingRow.add(Box.createHorizontalGlue());

        customizationPanel.add(sugarRow);
        customizationPanel.add(iceRow);
        customizationPanel.add(toppingRow);

        // HÀM CẬP NHẬT GIÁ TIỀN TRÊN CARD
        Runnable updatePriceDisplay = () -> {
            // Lấy size đã chọn
            String sizeChoice = smallBtn.isSelected() ? "S"
                    : mediumBtn.isSelected() ? "M"
                    : largeBtn.isSelected() ? "L" : "S";
            double sizeCost = product.getPriceIncreaseBySize(sizeChoice);

            List<String> chosenToppings = toppingCombo.getSelectedItems();
            double toppingCost = 0;

            for (String topping : chosenToppings) {
                String digits = topping.replaceAll("[^0-9]", "");
                if (!digits.isEmpty()) {
                    try {
                        double price = Double.parseDouble(digits);
                        // Nếu chuỗi chứa "K", ta nhân với 1000
                        if (topping.toUpperCase().contains("K")) {
                            price *= 1000;
                        }
                        toppingCost += price;
                    } catch (NumberFormatException ex) { }
                }
            }

            double finalPrice = product.getPrice() + sizeCost + toppingCost;
            String newPriceStr = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(finalPrice);
            priceLabel.setText(newPriceStr.replace("₫", " VND"));
        };

        // Add ItemListeners to change bg/fg when selected/deselected
        for (JToggleButton b : sizeButtons) {
            b.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    b.setBackground(new Color(255, 180, 80));
                    b.setForeground(Color.WHITE);
                } else {
                    b.setBackground(new Color(240, 240, 240));
                    b.setForeground(Color.BLACK);
                }
                // repaint to ensure RoundedBorder reads new background
                b.repaint();
                // THÊM DÒNG NÀY: Cập nhật giá khi Size thay đổi
                updatePriceDisplay.run(); // <--- THÊM DÒNG NÀY
            });
        }
        toppingCombo.addActionListener(e -> {
            updatePriceDisplay.run(); // <--- THÊM CODE NÀY
        });

        sizeGroup.add(smallBtn);
        sizeGroup.add(mediumBtn);
        sizeGroup.add(largeBtn);
        smallBtn.setSelected(true);
        // initial coloring
        smallBtn.setBackground(new Color(255, 180, 80));
        smallBtn.setForeground(Color.WHITE);
        mediumBtn.setBackground(new Color(240, 240, 240));
        mediumBtn.setForeground(Color.BLACK);
        largeBtn.setBackground(new Color(240, 240, 240));
        largeBtn.setForeground(Color.BLACK);

        JLabel sizeLabel = new JLabel("Size");
        sizeLabel.setFont(new Font(FONT_NAME, Font.BOLD, 11));
        sizePanel.add(sizeLabel);
        sizePanel.add(smallBtn);
        sizePanel.add(mediumBtn);
        sizePanel.add(largeBtn);

        rightSide.add(sizePanel, BorderLayout.CENTER);
        rightSide.add(sizePanel, BorderLayout.NORTH);
        rightSide.add(customizationPanel, BorderLayout.CENTER);

        center.add(rightSide, BorderLayout.CENTER);

        center.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(center, BorderLayout.CENTER);

        // Bottom: qty controls and add-to-cart
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        qtyPanel.setBackground(Color.WHITE);

        JButton minusBtn = createCircleButton("-", new Color(200, 200, 200), Color.BLACK);
        JLabel qtyLabel = new JLabel("0", SwingConstants.CENTER);
        qtyLabel.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        qtyLabel.setPreferredSize(new Dimension(30, 30));
        JButton plusBtn  = createCircleButton("+", new Color(255, 140, 0), Color.WHITE);

        minusBtn.addActionListener(e -> {
            int q = Integer.parseInt(qtyLabel.getText());
            if (q > 0) qtyLabel.setText(String.valueOf(q - 1));
        });
        plusBtn.addActionListener(e -> {
            int q = Integer.parseInt(qtyLabel.getText());
            qtyLabel.setText(String.valueOf(q + 1));
        });

        qtyPanel.add(minusBtn);
        qtyPanel.add(qtyLabel);
        qtyPanel.add(plusBtn);

        bottom.add(qtyPanel, BorderLayout.WEST);

        JButton addToCartBtn = createRoundedButton("Add to Cart", PRIMARY_COLOR, Color.WHITE);
        addToCartBtn.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        addToCartBtn.setPreferredSize(new Dimension(140, 36));

        addToCartBtn.addActionListener(e -> {
            int quantity = Integer.parseInt(qtyLabel.getText().trim());
            if (quantity > 0) {
                String sizeChoice = smallBtn.isSelected() ? "S" : mediumBtn.isSelected() ? "M" : "L";
                String sugarChoice = (String) sugarCombo.getSelectedItem();
                String iceChoice = (String) iceCombo.getSelectedItem();

                List<String> chosenToppingsFull = toppingCombo.getSelectedItems(); // Lấy list full string
                List<String> chosenToppings = new ArrayList<>();

                for (String fullText : chosenToppingsFull) {
                    String nameOnly = fullText.split(" \\(")[0]; // "Pearl (+5.000)" -> "Pearl"
                    chosenToppings.add(nameOnly);
                }

                String toppingString = chosenToppings.isEmpty() ? "No topping" : String.join(", ", chosenToppings);
                String details = "Size " + sizeChoice +
                        ", Sugar " + sugarChoice +
                        ", Ice " + iceChoice +
                        ", Topping: " + toppingString;
                OrderItem newItem = new OrderItem(product, quantity, details, sizeChoice, chosenToppings);
                currentOrder.getItems().add(newItem);
                updateOrderTable();
                qtyLabel.setText("0");
            } else {
                showCustomMessageDialog("Error", "Quantity must be greater than 0.", Color.RED, true);
            }
        });

        JPanel addWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        addWrap.setBackground(Color.WHITE);
        addWrap.add(addToCartBtn);

        bottom.add(addWrap, BorderLayout.EAST);

        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    // Order / Cart panel
    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Cart Details"));
        panel.setBackground(Color.WHITE);

        // --- FORCE CART PANEL WIDTH ~ 3 cm ---
        panel.setPreferredSize(new Dimension(70, panel.getPreferredSize().height));

        // ===========================
        //     CUSTOMER NAME INPUT
        // ===========================
        JPanel customerPanel = new JPanel(new BorderLayout(5, 5));
        customerPanel.setBackground(Color.WHITE);
        customerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Padding dưới

        JLabel nameLabel = new JLabel("Customer Name:");
        nameLabel.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        customerPanel.add(nameLabel, BorderLayout.NORTH);

        customerNameField = new JTextField("Walk-in Customer");
        customerNameField.setForeground(Color.GRAY);
        customerNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (customerNameField.getText().equals("Walk-in Customer")) {
                    customerNameField.setText("");
                    customerNameField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (customerNameField.getText().isEmpty()) {
                    customerNameField.setText("Walk-in Customer");
                    customerNameField.setForeground(Color.GRAY);
                }
            }
        });
        customerNameField.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
        customerNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(4, 8, 4, 8)
        ));
        customerPanel.add(customerNameField, BorderLayout.CENTER);

        // SỬA: Thay đổi vị trí thêm các component vào panel chính
        // Thêm customerPanel vào vị trí NORTH của order panel
        panel.add(customerPanel, BorderLayout.NORTH);

        // ===========================
        //         TABLE
        // ===========================
        String[] columnNames = {"Item", "Qty", "Amount", "Remove"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        JTable orderTable = new JTable(tableModel);
        orderTable.setPreferredScrollableViewportSize(new Dimension(80, 150));
        orderTable.setFillsViewportHeight(true);

        // Simple remove click
        orderTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = orderTable.rowAtPoint(evt.getPoint());
                int col = orderTable.columnAtPoint(evt.getPoint());
                if (col == 3 && row >= 0) {
                    if (row < currentOrder.getItems().size()) {
                        currentOrder.getItems().remove(row);
                        updateOrderTable();
                    }
                }
            }
        });

        // ScrollPane also must match width
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setPreferredSize(new Dimension(80, 150));
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 12));

        panel.add(scrollPane, BorderLayout.CENTER);

        // ===========================
        //     PAYMENT PANEL
        // ===========================
        JPanel paymentPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        paymentPanel.setBorder(new EmptyBorder(20, 6, 6, 6));
        paymentPanel.setBackground(Color.WHITE);

        totalLabel = new JLabel("0 VND", SwingConstants.RIGHT);
        totalLabel.setFont(new Font(FONT_NAME, Font.BOLD, 15));

        JLabel totalText = new JLabel("TOTAL DUE:", SwingConstants.RIGHT);
        totalText.setFont(new Font(FONT_NAME, Font.BOLD, 12));

        paymentPanel.add(totalText);
        paymentPanel.add(totalLabel);

        JButton payButton = createRoundedButton("Pay", PRIMARY_COLOR, Color.WHITE);
        payButton.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        payButton.setPreferredSize(new Dimension(70, 40));
        paymentPanel.add(payButton);

        payButton.addActionListener(e -> {
            if (currentOrder.getItems().isEmpty()) {
                showCustomMessageDialog("Error", "Cart is empty! Please add items.", Color.RED, true);
                return;
            }
            currentOrder.calculateTotalAmount();
            double totalAmount = currentOrder.getTotalAmount();

            PaymentDialog dialog = new PaymentDialog(this, totalAmount);
            dialog.setVisible(true);

            if (dialog.isPaymentConfirmed()) {
                // 3. Lấy thông tin thanh toán
                double cashReceived = dialog.getCashReceived();
                double change = dialog.getChange();

                String customerName =
                        customerNameField.getText().equals("Walk-in Customer")
                                ? ""
                                : customerNameField.getText();
                if (customerName.isEmpty()) {
                    customerName = "Walk-in Customer";
                }
                currentOrder.setCustomerName(customerName);
                completeOrderProcess(currentOrder, cashReceived, change);
            } else {
                // Hủy bỏ thanh toán: Không làm gì cả, dialog đã đóng
            }
        });

        panel.add(paymentPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void completeOrderProcess(Order order, double cashReceived, double change) {
        // Lấy code cũ từ processPayment() đưa vào đây
        String invoiceContent = order.generateInvoiceContent();

        // Thêm thông tin tiền mặt và tiền thối vào thông báo
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        // Thêm dòng ngăn cách và thông tin thanh toán vào cuối chuỗi hóa đơn
        String paymentDetails = "\n"
                + "=================================================\n"
                + String.format("%-30s %15s\n", "CASH RECEIVED:", currencyFormat.format(cashReceived))
                + String.format("%-30s %15s\n", "CHANGE:", currencyFormat.format(change))
                + "=================================================\n";

        invoiceContent += paymentDetails; // Gắn thêm chi tiết thanh toán vào hóa đơn
        JTextArea reportArea = new JTextArea(invoiceContent);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JPanel confirmationPanel = new JPanel(new BorderLayout(10, 10));
        JScrollPane invoiceScrollPane = new JScrollPane(reportArea);
        // Áp dụng cho thanh dọc
        invoiceScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        invoiceScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        // Áp dụng cho thanh ngang (Để khi phóng to/thu nhỏ nó hiện ra đúng mẫu)
        invoiceScrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        invoiceScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 12));
        confirmationPanel.add(invoiceScrollPane, BorderLayout.CENTER);

        int result = showCustomOkCancelDialog("Confirm Order and Print Receipt", confirmationPanel);

        if (result == JOptionPane.OK_OPTION) {
            order.setStatus("Completed");
            revenueManager.addCompletedOrder(order);

            // TÍCH HỢP TÍNH NĂNG NHÂN VIÊN (Phần 2)
            if (SessionManager.getInstance().isLoggedIn()) {
                SessionManager.getInstance().recordTransaction(order.getTotalAmount());
            }

            invoiceContent = order.generateInvoiceContent();
            boolean printed = order.printInvoiceToFile(invoiceContent);
            String statusMsg = printed ? " Receipt has been saved to file: Invoice_" + order.getOrderId() + ".txt" : "\nError: Could not save receipt file!";

            showCustomMessageDialog("Transaction Complete", "Payment successful!" + statusMsg, PRIMARY_COLOR, false);

            // Tạo đơn hàng mới
            int nextOrderId = Integer.parseInt(order.getOrderId().substring(1)) + 1;
            currentOrder = new Order("O" + String.format("%03d", nextOrderId));
            updateOrderTable();
            customerNameField.setText(""); // Xóa tên khách hàng cũ
        }
    }

    private void updateOrderTable() {
        tableModel.setRowCount(0);
        currentOrder.calculateTotalAmount();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (OrderItem item : currentOrder.getItems()) {
            String displayInfo = item.getProduct().getName();
            if (item.getCustomizationDetails() != null && !item.getCustomizationDetails().equals("Mặc định")) {
                displayInfo += " (" + item.getCustomizationDetails() + ")";
            }
            Object[] row = {
                    displayInfo,
                    item.getQuantity(),
                    currencyFormat.format(item.calculateSubtotal()),
                    "Remove"
            };
            tableModel.addRow(row);
        }

        totalLabel.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(currentOrder.getTotalAmount()));
    }

    private JPanel createManagementPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Management Functions"));

        JButton revenueStatsBtn = createRoundedButton("Revenue Statistics", new Color(173, 216, 230), Color.BLACK);
        revenueStatsBtn.addActionListener(e -> displayRevenueStatistics());
        panel.add(revenueStatsBtn);
        JButton historyBtn = createRoundedButton("Shift History", new Color(200, 200, 255), Color.BLACK);
        historyBtn.addActionListener(e -> displayShiftHistory());
        panel.add(historyBtn);
        // >>> THÊM NÚT ĐĂNG XUẤT (LOGOUT) <<<
        JButton logoutBtn = createRoundedButton("Logout", new Color(240, 128, 128), Color.WHITE); // Màu đỏ nhạt
        logoutBtn.addActionListener(e -> handleLogout());
        panel.add(logoutBtn);
        panel.setBackground(BACKGROUND_COLOR);
        return panel;
    }
    private void displayShiftHistory() {
        List<ShiftLog> history = SessionManager.getInstance().getShiftHistory();

        if (history.isEmpty()) {
            showCustomMessageDialog("Error", "No employee is currently logged in.", Color.RED, true);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- COMPLETED SHIFT LOGS ---\n\n");

        // Hiển thị lịch sử từ mới nhất đến cũ nhất
        for (int i = history.size() - 1; i >= 0; i--) {
            ShiftLog log = history.get(i);
            sb.append("============================\n");
            sb.append(log.toReportString());
        }

        JTextArea historyArea = new JTextArea(sb.toString());
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        historyArea.setEditable(false);
        historyArea.setPreferredSize(new Dimension(500, 400));

        JScrollPane historyScrollPane = new JScrollPane(historyArea);
        historyScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        historyScrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        historyScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        historyScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 12));

        showCustomReportDialog("Completed Shift History", historyScrollPane);
    }
    private void handleLogout() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            showCustomMessageDialog("Error", "No employee is currently logged in.", Color.RED, true);
            return;
        }
        int confirm = showCustomConfirmDialog("Confirm Logout", "Are you sure you want to log out and end your shift?");

        if (confirm == JOptionPane.YES_OPTION) {
            // 1. LẤY VÀ HIỂN THỊ BÁO CÁO CA LÀM VIỆC
            String reportContent = SessionManager.getInstance().getShiftReport();
            JTextArea reportArea = new JTextArea(reportContent);
            reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            reportArea.setEditable(false);

            // Cố định kích thước khung báo cáo
            reportArea.setPreferredSize(new Dimension(350, 250));

            // Hiển thị báo cáo trước khi đăng xuất
            showCustomReportDialog("Shift Summary for " + SessionManager.getInstance().getCurrentEmployee().getName(), new JScrollPane(reportArea));

            // 2. RESET/ĐĂNG XUẤT phiên làm việc sau khi hiển thị báo cáo
            SessionManager.getInstance().resetShift();

            // 3. Ẩn cửa sổ POS hiện tại và gọi màn hình đăng nhập
            this.setVisible(false);
            this.dispose();

            SwingUtilities.invokeLater(() -> {
                LoginDialog login = new LoginDialog(null);
                login.setVisible(true);

                if (SessionManager.getInstance().isLoggedIn()) {
                    new CoffeePOSApp();
                } else {
                    System.exit(0);
                }
            });
        }
    }

    private void displayRevenueStatistics() {
        String finalReportContent;
        String title;
        int reportWidth = 500;
        int reportHeight = 400; // Chiều cao mặc định cho báo cáo tổng hợp

        // Lấy báo cáo doanh thu chung (luôn cần thiết)
        String generalReport = revenueManager.generateSimpleRevenueReport();
        title = "All-Time Revenue Statistics Report";
        finalReportContent = generalReport;

        // 1. Kiểm tra nếu có nhân viên đang đăng nhập
        if (SessionManager.getInstance().isLoggedIn()) {
            // Lấy báo cáo ca làm việc hiện tại
            String shiftReport = SessionManager.getInstance().getShiftReport();
            // Ghép hai báo cáo lại với nhau
            title = "Combined Revenue Summary";
            finalReportContent = "--- CURRENT SHIFT SUMMARY ---\n"
                    + shiftReport
                    + "\n\n"
                    + "--- TOTAL SYSTEM REVENUE ---\n"
                    + generalReport;
            // Tăng chiều rộng để dễ đọc hơn nếu ghép 2 báo cáo
            reportWidth = 600;
            reportHeight = 550;
        }
        // 2. HIỂN THỊ KẾT QUẢ
        JTextArea reportArea = new JTextArea(finalReportContent);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        // Đặt kích thước cho cửa sổ hiển thị
        reportArea.setPreferredSize(new Dimension(reportWidth, reportHeight));
        JScrollPane reportScrollPane = new JScrollPane(reportArea);
        // Áp dụng UI mới
        reportScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        reportScrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        reportScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        reportScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 12));

        showCustomReportDialog(title, reportScrollPane);
    }

    private JButton createSimpleRoundedButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setForeground(fg);
        btn.setBackground(bg);

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // KHÔNG CÓ MouseListener (không có hover/unhover)

        return btn;
    }

    // --- Helpers: rounded buttons, toggles, rounded border ---
    private JButton createRoundedButton(String text, Color bg, Color fg) {
        final Color originalBg = new Color(bg.getRGB());
        JButton btn = new JButton(text) {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Vẽ nền bo góc
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                g2.dispose();

                // VẼ TEXT SAU CÙNG (luôn giữ chữ không bị che)
                super.paintComponent(g);
            }
        };

        btn.setForeground(fg);
        btn.setBackground(bg);

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false); // cho phép custom paint
        btn.setOpaque(false);            // để không vẽ nền vuông mặc định
        btn.setBorder(new EmptyBorder(10, 16, 10, 16)); // padding đẹp hơn
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // THÊM ĐOẠN CODE NÀY: MouseListener để thay đổi màu khi hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                Color currentColor = btn.getBackground();
                // Tính toán màu đậm hơn (giảm giá trị RGB đi 30 đơn vị)
                int r = Math.max(0, currentColor.getRed() - 30);
                int g = Math.max(0, currentColor.getGreen() - 30);
                int b = Math.max(0, currentColor.getBlue() - 30);
                Color hoverColor = new Color(r, g, b);
                // Đặt màu nền mới (sẽ được paintComponent sử dụng)
                btn.setBackground(hoverColor);
                btn.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Trở về màu nền ban đầu (originalBg)
                btn.setBackground(originalBg);
                btn.repaint();
            }
        });
        return btn;
    }

    private JToggleButton createSizeToggle(String text) {
        JToggleButton btn = new JToggleButton(text) {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = isSelected() ? new Color(90, 90, 90) : new Color(230, 230, 230);
                Color fgColor = isSelected() ? Color.WHITE : Color.BLACK;

                // vẽ nền bo góc
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                // vẽ viền mảnh
                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

                g2.dispose();

                setForeground(fgColor);

                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private JToggleButton createTogglePill(String text) {
        JToggleButton t = new JToggleButton(text);
        t.setFocusPainted(false);
        t.setBorder(new RoundedBorder(16, new Color(240, 240, 240)));
        t.setBackground(new Color(240, 240, 240));
        t.setOpaque(false); // keep border painting approach
        t.setPreferredSize(new Dimension(72, 28));
        return t;
    }

    // compact toggle used in the new card

    private JButton createCircleButton(String text, Color bg, Color fg) {
            final Color originalBg = new Color(bg.getRGB());
            JButton btn = new JButton(text) {
                @Override
                public boolean isOpaque() {
                    return false;
                }

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int diameter = Math.min(getWidth(), getHeight());

                    // Vẽ nền tròn
                    g2.setColor(getBackground());
                    g2.fillOval(1, 1, diameter - 3, diameter - 3);

                    g2.dispose();

                    // BẮT BUỘC: đặt alignment về center
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setVerticalAlignment(SwingConstants.CENTER);

                    // Chỉ vẽ TEXT, KHÔNG vẽ background của JButton nữa
                    Graphics2D gTxt = (Graphics2D) g.create();
                    gTxt.setColor(fg);
                    gTxt.setFont(getFont());

                    FontMetrics fm = gTxt.getFontMetrics();
                    int tx = (getWidth() - fm.stringWidth(text)) / 2;
                    int ty = (getHeight() + fm.getAscent()) / 2 - 4;

                    gTxt.drawString(text, tx, ty);
                    gTxt.dispose();
                }
            };

        // Remove mọi thứ gây clipping
        btn.setBorder(null);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBackground(bg);

        btn.setPreferredSize(new Dimension(28, 28));
        btn.setFont(new Font(FONT_NAME, Font.BOLD, 18)); // chữ + - to
        btn.setForeground(fg);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                Color currentColor = btn.getBackground();

                // Tính toán màu đậm hơn (giảm 30 đơn vị)
                int r = Math.max(0, currentColor.getRed() - 30);
                int g = Math.max(0, currentColor.getGreen() - 30);
                int b = Math.max(0, currentColor.getBlue() - 30);

                Color hoverColor = new Color(r, g, b);

                btn.setBackground(hoverColor);
                btn.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Trở về màu nền ban đầu (originalBg)
                btn.setBackground(originalBg);
                btn.repaint();
            }
        });
        return btn;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        final Color originalBg = new Color(bg.getRGB());

        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int radius = 10;
                // Lấy màu từ getBackground() (màu nền hiện tại, có thể là màu hover)
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

                // Vẽ viền nhẹ cho nút Cancel/Secondary
                if (text.equals("Cancel") || text.equals("No") || text.equals("OK")) {
                    g2.setColor(new Color(200, 200, 200));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                }
                g2.dispose();
                // Đảm bảo chữ được vẽ lên trên cùng
                super.paintComponent(g);
            }
        };

        btn.setForeground(fg);
        btn.setBackground(bg); // Thiết lập màu nền ban đầu
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            // Sử dụng originalBg đã được định nghĩa là final
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                Color currentColor = btn.getBackground();
                // Tính toán màu đậm hơn (giảm giá trị RGB đi 30 đơn vị)
                int r = Math.max(0, currentColor.getRed() - 30);
                int g = Math.max(0, currentColor.getGreen() - 30);
                int b = Math.max(0, currentColor.getBlue() - 30);
                Color hoverColor = new Color(r, g, b);
                // Đặt màu nền mới (sẽ được paintComponent sử dụng)
                btn.setBackground(hoverColor);
                btn.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Trở về màu nền ban đầu (originalBg)
                btn.setBackground(originalBg);
                btn.repaint();
            }
        });
        return btn;
    }

    // --- HÀM TIỆN ÍCH HIỂN THỊ THÔNG BÁO TÙY CHỈNH (MỘT NÚT OK) ---
    private void showCustomMessageDialog(String title, String message, Color color, boolean isError) {
        // 1. MÀU SẮC
        final Color errorBtnColor = new Color(240, 128, 128); // Đỏ nhạt cho nút (giống PaymentDialog)
        final Color successBtnColor = new Color(255, 140, 0); // Cam cho Transaction Complete

        final Color mainBtnColor = isError ? errorBtnColor : successBtnColor;

        // 2. TẠO NÚT OK
        JButton okButton = new JButton("OK") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        okButton.setBackground(mainBtnColor);
        okButton.setPreferredSize(new Dimension(85, 35));
        okButton.setBorderPainted(false);
        okButton.setContentAreaFilled(false);
        okButton.setFocusPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // HIỆN NGÓN TAY

        // Hiệu ứng Hover cho nút
        okButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                Color c = okButton.getBackground();
                okButton.setBackground(new Color(Math.max(0, c.getRed()-30), Math.max(0, c.getGreen()-30), Math.max(0, c.getBlue()-30)));
                okButton.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                okButton.setBackground(mainBtnColor);
                okButton.repaint();
            }
        });

        // 3. KHỞI TẠO DIALOG
        JDialog dialog = new JDialog(this, title, true);
        dialog.setResizable(false);
        JPanel contentPanel = new JPanel(new BorderLayout(10, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        // 4. NỘI DUNG TIN NHẮN - CHỮ PHẢI LÀ REDDD (NẾU LÀ LỖI)
        String labelText;
        if (isError) {
            // Chữ Đỏ đậm (Red), viết hoa hoặc in đậm để cực kỳ nổi bật
            labelText = "<html><center><font color='red'><b>" + message + "</b></font></center></html>";
        } else {
            // Chữ đen bình thường cho thành công
            labelText = "<html><center><b>" + message + "</b></center></html>";
        }

        JLabel messageLabel = new JLabel(labelText, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Ép Font BOLD cho chữ đỏ

        contentPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> dialog.dispose());
        dialog.setContentPane(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // --- HÀM TIỆN ÍCH HIỂN THỊ HỘP THOẠI XÁC NHẬN TÙY CHỈNH (YES/NO) ---
    private int showCustomConfirmDialog(String title, String message) {
        // TẠO NÚT YES/NO BO GÓC
        JButton yesButton = createStyledButton("Yes", PRIMARY_COLOR, Color.WHITE); // Yes màu cam
        JButton noButton = createStyledButton("No", new Color(220, 220, 220), Color.BLACK); // No màu xám

        final int[] result = {JOptionPane.CANCEL_OPTION};

        JDialog dialog = new JDialog(this, title, true);
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 15, 30));
        contentPanel.setBackground(Color.WHITE);

        String formattedMessage = "<html><center><font size='+1'><b>" + message + "</b></font></center></html>";

        JLabel messageLabel = new JLabel(formattedMessage, SwingConstants.CENTER);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện click
        yesButton.addActionListener(e -> {
            result[0] = JOptionPane.YES_OPTION;
            dialog.dispose();
        });
        noButton.addActionListener(e -> {
            result[0] = JOptionPane.NO_OPTION;
            dialog.dispose();
        });

        dialog.getContentPane().add(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return result[0];
    }
    private int showCustomOkCancelDialog(String title, JComponent messageComponent) {
        // TẠO NÚT OK và CANCEL BO GÓC
        JButton okButton = createStyledButton("OK", PRIMARY_COLOR, Color.WHITE); // OK màu cam
        JButton cancelButton = createStyledButton("Cancel", new Color(220, 220, 220), Color.BLACK); // Cancel màu xám

        // Custom return values
        final int[] result = {JOptionPane.CANCEL_OPTION};

        JDialog dialog = new JDialog(this, title, true);
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));
        contentPanel.setBackground(Color.WHITE);
        messageComponent.setPreferredSize(new Dimension(550, 480));

        // Thêm nội dung hóa đơn (JTextArea/JScrollPane) vào trung tâm
        contentPanel.add(messageComponent, BorderLayout.CENTER);

        // Panel nút bấm (dưới)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện click
        okButton.addActionListener(e -> {
            result[0] = JOptionPane.OK_OPTION;
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> {
            result[0] = JOptionPane.CANCEL_OPTION;
            dialog.dispose();
        });

        dialog.getContentPane().add(contentPanel);
        dialog.pack();
        // Đặt kích thước tối thiểu sau khi pack (nếu cần)
        dialog.setMinimumSize(new Dimension(600, 550));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return result[0];
    }
    private void showCustomReportDialog(String title, JComponent messageComponent) {
        // TẠO NÚT OK BO GÓC
        JButton okButton = createStyledButton("OK", PRIMARY_COLOR, Color.WHITE); // OK màu cam
        okButton.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        okButton.setPreferredSize(new Dimension(80, 40));

        JDialog dialog = new JDialog(this, title, true);
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // dialog.setResizable(true); // Có thể cho phép resize nếu báo cáo quá dài

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(Color.WHITE);

        // Thêm nội dung báo cáo (JScrollPane/JTextArea) vào trung tâm
        contentPanel.add(messageComponent, BorderLayout.CENTER);

        // Panel nút bấm (dưới)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện click
        okButton.addActionListener(e -> dialog.dispose());

        dialog.getContentPane().add(contentPanel);

        // Đặt kích thước lớn cho báo cáo
        dialog.setSize(new Dimension(650, 550));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            if (scrollbar != null) {
                scrollbar.setOpaque(false);
            }
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return new ScrollArrowButton(orientation);
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return new ScrollArrowButton(orientation);
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // Track trong suốt hoàn toàn
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(180, 180, 180));

            int x = thumbBounds.x;
            int y = thumbBounds.y;
            int w = thumbBounds.width;
            int h = thumbBounds.height;

            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                x += 2; w -= 4; y += 2; h -= 4;
                g2.fillRoundRect(x, y, w, h, w, w);
            } else {
                y += 2; h -= 4; x += 2; w -= 4;
                g2.fillRoundRect(x, y, w, h, h, h);
            }
            g2.dispose();
        }

        // --- LỚP VẼ MŨI TÊN ĐẦU BẸT CHO KÍCH THƯỚC 12 ---
        private static class ScrollArrowButton extends JButton {
            private final int orientation;
            private final Color NORMAL_COLOR = new Color(160, 160, 160);
            private final Color PRESSED_COLOR = new Color(90, 90, 90); // Đậm rõ rệt khi bấm

            public ScrollArrowButton(int orientation) {
                this.orientation = orientation;
                setContentAreaFilled(false);
                setBorder(null);
                setFocusable(false);
                setPreferredSize(new Dimension(12, 12)); // Cố định kích thước 12
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(PRESSED_COLOR);
                } else {
                    g2.setColor(NORMAL_COLOR);
                }

                // Tính toán tam giác cân góc đỉnh bẹt (Đáy rộng, chiều cao thấp)
                // Với kích thước 12, đáy 8 và cao 4 là tỷ lệ bẹt đẹp nhất
                int triW = 8;
                int triH = 4;
                int x = (getWidth() - triW) / 2;
                int y = (getHeight() - triH) / 2;

                int[] px = new int[3];
                int[] py = new int[3];

                if (orientation == NORTH) { // Lên
                    px = new int[]{x, x + triW, x + triW / 2};
                    py = new int[]{y + triH, y + triH, y};
                } else if (orientation == SOUTH) { // Xuống
                    px = new int[]{x, x + triW, x + triW / 2};
                    py = new int[]{y, y, y + triH};
                } else if (orientation == WEST) { // Trái (Xoay tọa độ)
                    int tx = (getWidth() - triH) / 2;
                    int ty = (getHeight() - triW) / 2;
                    px = new int[]{tx + triH, tx + triH, tx};
                    py = new int[]{ty, ty + triW, ty + triW / 2};
                } else if (orientation == EAST) { // Phải (Xoay tọa độ)
                    int tx = (getWidth() - triH) / 2;
                    int ty = (getHeight() - triW) / 2;
                    px = new int[]{tx, tx, tx + triH};
                    py = new int[]{ty, ty + triW, ty + triW / 2};
                }

                g2.fillPolygon(px, py, 3);
                g2.dispose();
            }
        }
    }

    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color fill; // default fill if component bg is null

        // Backwards-compatible constructor (radius + fill)
        public RoundedBorder(int radius, Color fill) {
            this.radius = radius;
            this.fill = fill;
        }

        // Optional convenience constructor (radius only)
        public RoundedBorder(int radius) {
            this(radius, null);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // tighten the rectangle a bit so stroke doesn't overflow and won't cover text
            float pad = 0.5f;
            RoundRectangle2D round = new RoundRectangle2D.Float(
                    x + pad,
                    y + pad,
                    width - 1f - pad * 2f,
                    height - 1f - pad * 2f,
                    radius, radius
            );

            // Do NOT fill the whole area here (filling can cover children/text).
            // Instead, use the component background (component should be opaque when
            // you want a filled look), then draw stroke.
            Color bg = c.getBackground();
            if (bg == null && fill != null) bg = fill;

            Color borderColor;
            if (bg != null) {
                int brightness = (bg.getRed() + bg.getGreen() + bg.getBlue()) / 3;
                borderColor = (brightness > 180) ? new Color(200, 200, 200) : new Color(235, 235, 235);
            } else {
                borderColor = new Color(220, 220, 220);
            }

            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(round);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            int pad = Math.max(4, radius / 3);
            return new Insets(pad, pad, pad, pad);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            int pad = Math.max(4, radius / 3);
            insets.left = insets.top = insets.right = insets.bottom = pad;
            return insets;
        }
    }
    static class ModernComboBoxUI extends BasicComboBoxUI {
        private final Color BACKGROUND = new Color(245, 245, 245);
        private final Color BORDER = new Color(220, 220, 220);

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            c.setOpaque(false);
        }
        // ĐÈ LÊN CƠ CHẾ VẼ MẶC ĐỊNH ĐỂ XÓA VỆT TRẮNG
        @Override
        public void update(Graphics g, JComponent c) {
            // Không gọi super.update(g, c) vì nó sẽ vẽ nền trắng hình chữ nhật
            paint(g, c);
        }
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int size = 3;
                    int x = (getWidth() - size * 2) / 2;
                    int y = (getHeight() - size) / 2;
                    g2.setColor(new Color(120, 120, 120));
                    g2.fillPolygon(new int[]{x, x + size * 2, x + size}, new int[]{y, y, y + size}, 3);
                    g2.dispose();
                }
            };
            button.setBorder(null);
            button.setContentAreaFilled(false);
            button.setOpaque(false);
            return button;
        }
        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 1. Vẽ đè nền xám bo tròn lên trên cùng
            g2.setColor(BACKGROUND);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
            // 2. Vẽ viền
            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 10, 10);
            g2.dispose();
            // 3. Chỉ vẽ nội dung (chữ) mà thôi
            Rectangle r = rectangleForCurrentValue();
            paintCurrentValue(g, r, hasFocus);
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // Để trống để chặn hoàn toàn vệt trắng hình chữ nhật bên trong chữ
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox) {
                @Override
                protected JScrollPane createScroller() {
                    JScrollPane sp = super.createScroller();
                    sp.setOpaque(false);
                    sp.getViewport().setOpaque(false);
                    sp.setBorder(BorderFactory.createEmptyBorder());
                    return sp;
                }
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2.setColor(new Color(220, 220, 220));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                    g2.dispose();
                }
            };
            popup.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            // null-check trước khi truy cập JList bên trong popup
            JList<?> list = popup.getList();
            if (list != null) {
                list.setOpaque(false);
                list.setBackground(new Color(0, 0, 0, 0));
                list.setCellRenderer(new ModernComboRenderer());
                // selection: để chữ nổi bật và nền selection trong suốt
                list.setSelectionForeground(new Color(60, 60, 60));
                list.setSelectionBackground(new Color(0, 0, 0, 0));
            }
            return popup;
        }
    }

    static class ModernComboRenderer extends JLabel implements ListCellRenderer<Object> {

        private boolean selected;

        public ModernComboRenderer() {
            setFont(new Font("Segoe UI", Font.PLAIN, 11));
            setBorder(BorderFactory.createEmptyBorder(0, 8, 2, 8));
            setOpaque(false); // để tự vẽ bo tròn
        }

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            this.selected = isSelected && index != -1;
            setText(value != null ? value.toString() : "0%");
            setForeground(new Color(60, 60, 60));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (selected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(225, 225, 225));
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}
