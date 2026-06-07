package gui;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * واجهة إدارة المستخدمين — متاحة لمدير النظام فقط.
 * تتيح إضافة وتعديل وحذف حسابات المستخدمين.
 *
 * @author فريق المشروع
 */
public class UserManagementForm extends JFrame {

    private JTable            table;
    private DefaultTableModel tableModel;

    // حقول النموذج
    private JTextField     nameF, emailF, phoneF;
    private JPasswordField passF;
    private JComboBox<String> roleCombo;
    private JLabel         idLbl;

    private final String currentUser;

    public UserManagementForm(String currentUser) {
        this.currentUser = currentUser;
        buildUI();
        loadData();
    }

    private void buildUI() {
        setTitle("إدارة المستخدمين");
        setSize(1020, 640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppColors.BG_MAIN);

        // ---- شريط العنوان ----
        JPanel header = AppColors.headerBar("👤  إدارة المستخدمين", "مدير النظام: " + currentUser);
        header.setBackground(AppColors.DARK);

        // ---- محتوى رئيسي ----
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(360);
        split.setBorder(null);
        split.setBackground(AppColors.BG_MAIN);
        split.setLeftComponent(buildFormPanel());
        split.setRightComponent(buildTablePanel());

        add(header, BorderLayout.NORTH);
        add(split,  BorderLayout.CENTER);
        setVisible(true);
    }

    // ================================================================
    //  لوحة النموذج (يسار)
    // ================================================================
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(AppColors.BG_MAIN);
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 6));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppColors.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.DARK, 2),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 4, 6, 4);
        g.gridwidth = 2;

        // عنوان البطاقة
        g.gridx = 0; g.gridy = 0;
        JLabel cardTitle = new JLabel("  بيانات المستخدم", SwingConstants.RIGHT);
        cardTitle.setFont(new Font("Arial", Font.BOLD, 15));
        cardTitle.setForeground(AppColors.TEXT_WHITE);
        cardTitle.setOpaque(true);
        cardTitle.setBackground(AppColors.DARK);
        cardTitle.setBorder(BorderFactory.createEmptyBorder(9, 10, 9, 10));
        card.add(cardTitle, g);

        g.gridwidth = 1;
        int row = 1;

        // المعرف
        row = addRow(card, g, row, "المعرّف:", idLbl = new JLabel("تلقائي"), false);

        // الاسم
        nameF = AppColors.textField();
        row = addRow(card, g, row, "الاسم الكامل: *", nameF, true);

        // البريد
        emailF = AppColors.textField();
        row = addRow(card, g, row, "اسم المستخدم: *", emailF, true);

        // الهاتف
        phoneF = AppColors.textField();
        row = addRow(card, g, row, "رقم الهاتف:", phoneF, true);

        // كلمة المرور
        passF = new JPasswordField();
        AppColors.styleField(passF);
        row = addRow(card, g, row, "كلمة المرور: *", passF, true);

        // الدور
        roleCombo = new JComboBox<>(new String[]{"Employee", "Manager", "Admin"});
        AppColors.styleCombo(roleCombo);
        row = addRow(card, g, row, "الدور:", roleCombo, true);

        // فاصل
        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        card.add(new JSeparator(), g);
        row++;

        // ---- أزرار العمليات ----
        g.gridy = row; g.gridwidth = 2;
        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        btnGrid.setOpaque(false);

        JButton addBtn  = AppColors.btnSuccess("➕  إضافة مستخدم");
        JButton updBtn  = AppColors.btnPrimary("✏  تعديل البيانات");
        JButton delBtn  = AppColors.btnDanger ("🗑  حذف المستخدم");
        JButton clrBtn  = AppColors.btnDark   ("↺  مسح الحقول");

        addBtn.addActionListener(e -> addUser());
        updBtn.addActionListener(e -> updateUser());
        delBtn.addActionListener(e -> deleteUser());
        clrBtn.addActionListener(e -> clearForm());

        btnGrid.add(addBtn); btnGrid.add(updBtn);
        btnGrid.add(delBtn); btnGrid.add(clrBtn);
        card.add(btnGrid, g);
        row++;

        // تلميح
        g.gridy = row;
        JLabel tip = new JLabel(
            "<html><small>* الحقول المميزة بـ (*) مطلوبة</small></html>",
            SwingConstants.CENTER);
        tip.setForeground(AppColors.TEXT_MUTED);
        card.add(tip, g);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    // ================================================================
    //  لوحة الجدول (يمين)
    // ================================================================
    private JPanel buildTablePanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 8));
        outer.setBackground(AppColors.BG_MAIN);
        outer.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));

        // شريط البحث
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBar.setBackground(AppColors.BG_MAIN);
        JLabel searchLbl = AppColors.label("🔍  بحث:");
        JTextField searchF = AppColors.textField();
        searchF.setPreferredSize(new Dimension(200, 32));
        JButton searchBtn  = AppColors.btnPrimary("بحث");
        JButton showAllBtn = AppColors.btnTeal("عرض الكل");
        searchBtn.addActionListener(e -> search(searchF.getText().trim()));
        showAllBtn.addActionListener(e -> loadData());
        searchBar.add(showAllBtn); searchBar.add(searchBtn);
        searchBar.add(searchF);   searchBar.add(searchLbl);

        // الجدول
        String[] cols = {"المعرف", "الاسم", "اسم المستخدم", "الهاتف", "الدور"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        AppColors.styleTable(table);

        // تلوين الصفوف حسب الدور
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    String dole = tableModel.getValueAt(row, 4) != null
                                  ? tableModel.getValueAt(row, 4).toString() : "";
                    switch (dole) {
                        case "Admin"   -> c.setBackground(new Color(255, 235, 235));
                        case "Manager" -> c.setBackground(new Color(235, 235, 255));
                        default        -> c.setBackground(row % 2 == 0
                                            ? AppColors.BG_WHITE : AppColors.BG_ROW_ALT);
                    }
                }
                return c;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillForm();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.DARK, 2));

        // وسيلة إيضاح الألوان
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4));
        legend.setBackground(AppColors.BG_MAIN);
        legend.add(colorDot(new Color(220, 30, 30),   "مدير النظام"));
        legend.add(colorDot(new Color(60,  60,  180), "مشرف"));
        legend.add(colorDot(new Color(60,  130, 60),  "موظف"));

        JPanel tableCard = new JPanel(new BorderLayout(0, 6));
        tableCard.setBackground(AppColors.BG_MAIN);
        tableCard.add(searchBar, BorderLayout.NORTH);
        tableCard.add(scroll,    BorderLayout.CENTER);
        tableCard.add(legend,    BorderLayout.SOUTH);

        outer.add(tableCard, BorderLayout.CENTER);
        return outer;
    }

    // ================================================================
    //  العمليات
    // ================================================================

    private void addUser() {
        String name  = nameF.getText().trim();
        String email = emailF.getText().trim();
        String pass  = new String(passF.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            warn("يرجى ملء جميع الحقول المطلوبة (*)"); return;
        }
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "INSERT INTO users (name, email, phone, password, role) VALUES (?,?,?,?,?)");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phoneF.getText().trim());
            ps.setString(4, pass);
            ps.setString(5, (String) roleCombo.getSelectedItem());
            ps.executeUpdate(); ps.close();
            ok("✔  تمت إضافة المستخدم بنجاح!");
            loadData(); clearForm();
        } catch (SQLException ex) {
            if (ex.getMessage().contains("UNIQUE"))
                err("اسم المستخدم مسجّل مسبقاً. اختر اسم مستخدم آخر.");
            else err("خطأ: " + ex.getMessage());
        }
    }

    private void updateUser() {
        if (table.getSelectedRow() == -1) { warn("يرجى تحديد مستخدم من الجدول."); return; }
        try {
            int id = Integer.parseInt(idLbl.getText());
            String pass = new String(passF.getPassword()).trim();

            // إذا تُرك حقل كلمة المرور فارغاً — لا نُغيّرها
            if (pass.isEmpty()) {
                PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                    "UPDATE users SET name=?, email=?, phone=?, role=? WHERE id=?");
                ps.setString(1, nameF.getText().trim());
                ps.setString(2, emailF.getText().trim());
                ps.setString(3, phoneF.getText().trim());
                ps.setString(4, (String) roleCombo.getSelectedItem());
                ps.setInt(5, id);
                ps.executeUpdate(); ps.close();
            } else {
                PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                    "UPDATE users SET name=?, email=?, phone=?, password=?, role=? WHERE id=?");
                ps.setString(1, nameF.getText().trim());
                ps.setString(2, emailF.getText().trim());
                ps.setString(3, phoneF.getText().trim());
                ps.setString(4, pass);
                ps.setString(5, (String) roleCombo.getSelectedItem());
                ps.setInt(6, id);
                ps.executeUpdate(); ps.close();
            }
            ok("✔  تم تعديل بيانات المستخدم!");
            loadData();
        } catch (SQLException ex) { err("خطأ: " + ex.getMessage()); }
    }

    private void deleteUser() {
        if (table.getSelectedRow() == -1) { warn("يرجى تحديد مستخدم من الجدول."); return; }
        String name = tableModel.getValueAt(table.getSelectedRow(), 1).toString();

        // منع حذف المدير الحالي
        if (name.equals(currentUser)) {
            warn("لا يمكنك حذف حسابك الخاص!"); return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "هل أنت متأكد من حذف المستخدم: " + name + "؟\nهذا الإجراء لا يمكن التراجع عنه!",
            "تأكيد الحذف", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            int id = Integer.parseInt(idLbl.getText());
            PreparedStatement ps = DatabaseConnection.getConnection()
                    .prepareStatement("DELETE FROM users WHERE id=?");
            ps.setInt(1, id); ps.executeUpdate(); ps.close();
            ok("✔  تم حذف المستخدم.");
            loadData(); clearForm();
        } catch (SQLException ex) { err("خطأ: " + ex.getMessage()); }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        idLbl.setText(tableModel.getValueAt(row, 0).toString());
        nameF.setText(tableModel.getValueAt(row, 1).toString());
        emailF.setText(tableModel.getValueAt(row, 2).toString());
        phoneF.setText(tableModel.getValueAt(row, 3).toString());
        roleCombo.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        passF.setText(""); // لا نعرض كلمة المرور
    }

    private void clearForm() {
        idLbl.setText("تلقائي");
        nameF.setText(""); emailF.setText(""); phoneF.setText(""); passF.setText("");
        roleCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            Statement stmt = DatabaseConnection.getConnection().createStatement();
            ResultSet rs   = stmt.executeQuery("SELECT id,name,email,phone,role FROM users ORDER BY role,name");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"),
                    rs.getString("email"), rs.getString("phone"),
                    rs.getString("role")
                });
            }
            rs.close(); stmt.close();
        } catch (SQLException ex) { err("خطأ في تحميل البيانات: " + ex.getMessage()); }
    }

    private void search(String kw) {
        try {
            tableModel.setRowCount(0);
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "SELECT id,name,email,phone,role FROM users WHERE name LIKE ? OR email LIKE ? OR role LIKE ?");
            String q = "%" + kw + "%";
            ps.setString(1, q); ps.setString(2, q); ps.setString(3, q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"),
                    rs.getString("email"), rs.getString("phone"),
                    rs.getString("role")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException ex) { err("خطأ: " + ex.getMessage()); }
    }

    // ================================================================
    //  مساعدات UI
    // ================================================================

    /** يضيف صفاً من تسمية + حقل إلى اللوحة */
    private int addRow(JPanel p, GridBagConstraints g, int row,
                       String labelText, Component field, boolean isComp) {
        g.gridwidth = 1;
        g.gridx = 0; g.gridy = row; g.weightx = 0.35;
        p.add(AppColors.label(labelText), g);
        g.gridx = 1; g.weightx = 0.65;
        if (isComp) field.setFont(new Font("Arial", Font.PLAIN, 13));
        p.add(field, g);
        return row + 1;
    }

    /** نقطة لوسيلة الإيضاح */
    private JPanel colorDot(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel dot = new JLabel("■");
        dot.setFont(new Font("Arial", Font.BOLD, 14));
        dot.setForeground(c);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(AppColors.TEXT_MUTED);
        p.add(dot); p.add(lbl);
        return p;
    }

    private void ok  (String msg) { JOptionPane.showMessageDialog(this, msg, "نجاح",  JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String msg) { JOptionPane.showMessageDialog(this, msg, "تنبيه", JOptionPane.WARNING_MESSAGE); }
    private void err (String msg) { JOptionPane.showMessageDialog(this, msg, "خطأ",   JOptionPane.ERROR_MESSAGE); }
}
