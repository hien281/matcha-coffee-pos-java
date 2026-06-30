import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MultiSelectComboBox extends JComboBox<String> {
    private final List<String> selectedItems = new ArrayList<>();
    private final String ALL_ITEMS = "None";

    public MultiSelectComboBox(String[] items) {
        super(items);
        this.insertItemAt(ALL_ITEMS, 0);

        // 1. Setup UI cơ bản
        setUI(new CoffeePOSApp.ModernComboBoxUI());
        setRenderer(new MultiSelectRenderer());
        setSelectedItem(ALL_ITEMS);
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));

        // 2. Xử lý Popup (Làm trong suốt, bỏ viền, thêm padding)
        Object child = getUI().getAccessibleChild(this, 0);
        if (child instanceof BasicComboPopup popup) {

            // --- SỬA LỖI SÁT CHÂN CHỮ CHEESE FOAM ---
            // Thêm padding dưới đáy (bottom = 10) để dòng cuối thoáng hơn
            popup.setBorder(new EmptyBorder(5, 5, 5, 5));

            popup.setOpaque(false);
            popup.setBackground(new Color(0, 0, 0, 0));

            JList<?> list = popup.getList();
            if (list != null) {
                list.setSelectionBackground(new Color(0, 0, 0, 0));
                list.setSelectionForeground(list.getForeground());
                list.setOpaque(false);
                list.setBackground(new Color(0, 0, 0, 0));

                // Logic click chọn (giữ nguyên logic đã fix ở bước trước)
                list.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        int index = list.locationToIndex(e.getPoint());
                        if (index >= 0) {
                            String item = getItemAt(index);
                            if (ALL_ITEMS.equals(item)) {
                                selectedItems.clear();
                            } else {
                                if (selectedItems.contains(item)) selectedItems.remove(item);
                                else selectedItems.add(item);
                            }
                            Rectangle rect = list.getCellBounds(index, index);
                            if (rect != null) list.repaint(rect);
                            MultiSelectComboBox.this.repaint();
                            fireActionEvent();
                        }
                    }
                });
            }

            // Làm trong suốt ScrollPane
            for (Component c : popup.getComponents()) {
                if (c instanceof JScrollPane scrollPane) {
                    scrollPane.setOpaque(false);
                    scrollPane.getViewport().setOpaque(false);
                    scrollPane.setBorder(null);
                }
            }
        }
    }

    // Giữ popup mở khi chọn
    @Override
    public void setPopupVisible(boolean v) {
        if (v) {
            super.setPopupVisible(true);
        } else {
            Point p = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(p, this);
            if (!contains(p)) {
                Object child = getUI().getAccessibleChild(this, 0);
                if (child instanceof BasicComboPopup popup) {
                    Point listP = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(listP, popup.getList());
                    if (!popup.getList().contains(listP)) {
                        super.setPopupVisible(false);
                    }
                } else {
                    super.setPopupVisible(false);
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(140, 22);
    }

    public List<String> getSelectedItems() {
        return selectedItems;
    }

    // --- RENDERER MỚI: VẼ NỀN XÁM BO TRÒN GIỐNG ICE/SUGAR ---
    private class MultiSelectRenderer extends JPanel implements ListCellRenderer<Object> {
        private final JCheckBox checkBox;
        private final JLabel label; // Dùng cho mục None hoặc hiển thị Text trên ComboBox
        private boolean isHovered = false;

        public MultiSelectRenderer() {
            setLayout(new BorderLayout());
            setOpaque(false); // Để tự vẽ background bo tròn
            // Tăng chiều cao mỗi dòng lên một chút cho thoáng (Padding trên dưới)
            setBorder(new EmptyBorder(3, 5, 3, 5));

            checkBox = new JCheckBox();
            checkBox.setOpaque(false);
            checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            // Bỏ border mặc định của checkbox để căn lề đẹp hơn
            checkBox.setBorder(null);

            label = new JLabel();
            label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            label.setOpaque(false);
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            removeAll();
            String text = (value == null) ? "" : value.toString();
            if (index == -1) {
                this.isHovered = false;
            } else {
                this.isHovered = isSelected;
            }

            // 1. Hiển thị trên thanh ComboBox (khi chưa xổ xuống)
            if (index == -1) {
                int n = selectedItems.size();
                label.setText(n > 0 ? "Chosen (" + n + ")" : ALL_ITEMS);
                label.setForeground(new Color(60, 60, 60));
                // Reset border để căn giữa đẹp hơn khi hiển thị ở trên
                setBorder(new EmptyBorder(0, 5, 1, 5));
                add(label, BorderLayout.CENTER);
                return this;
            }

            // Reset lại border cho các item trong danh sách (tạo độ thoáng)
            setBorder(new EmptyBorder(4, 8, 4, 8));

            // 2. Mục "None"
            if (ALL_ITEMS.equals(text)) {
                label.setText(text);
                label.setForeground(Color.BLACK);
                add(label, BorderLayout.CENTER);
            }
            // 3. Các mục Topping (Checkbox)
            else {
                checkBox.setText(text);
                checkBox.setSelected(selectedItems.contains(text));
                checkBox.setForeground(Color.BLACK);
                add(checkBox, BorderLayout.CENTER);
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            // VẼ NỀN MÀU XÁM BO TRÒN KHI HOVER (isSelected = true)
            if (isHovered) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Màu xám nhạt giống Sugar/Ice (khoảng 225-230)
                g2.setColor(new Color(225, 225, 225));

                // Vẽ bo tròn, trừ đi 1 chút lề để không bị dính vào nhau
                g2.fillRoundRect(2, 1, getWidth() - 4, getHeight() - 2, 8, 8);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}