package gui;

import database.DatabaseConnection;
import database.EmployeeDAO;
import model.Employee;
import exception.EmployeeException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

/**
 * واجهة إدارة الموظفين — إضافة وتعديل وحذف وبحث.
 *
 * @author فريق المشروع
 */
public class EmployeeManagementForm extends JFrame {

    private JTable            table;
    private DefaultTableModel tableModel;
    private final EmployeeDAO dao;

    private JTextField   nameF, emailF, phoneF, positionF, salaryF, hireDateF;
    private JComboBox<String> deptCombo, shiftCombo, statusCombo;
    private JLabel       idLbl;

    public EmployeeManagementForm(String user, String role, int uid) {
        this.dao = new EmployeeDAO();
        buildUI();
        loadTable();
    }

    private void buildUI() {
        setTitle("إدارة الموظفين");
        setSize(1060, 660);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppColors.BG_MAIN);

        JPanel header = AppColors.headerBar("👥  إدارة الموظفين", "");
        header.setBackground(AppColors.PRIMARY);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(370);
        split.setBorder(null);
        split.setLeftComponent(buildForm());
        split.setRightComponent(buildTable());

        add(header, BorderLayout.NORTH);
        add(split,  BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel buildForm() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(AppColors.BG_MAIN);
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 6));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppColors.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.PRIMARY, 2),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 4, 5, 4);

        // عنوان
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        JLabel t = new JLabel("  بيانات الموظف", SwingConstants.RIGHT);
        t.setFont(new Font("Arial", Font.BOLD, 14));
        t.setForeground(AppColors.TEXT_WHITE);
        t.setOpaque(true); t.setBackground(AppColors.PRIMARY);
        t.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        card.add(t, g);
        g.gridwidth = 1;

        // حقول
        idLbl     = new JLabel("تلقائي");
        nameF     = AppColors.textField();
        emailF    = AppColors.textField();
        phoneF    = AppColors.textField();
        positionF = AppColors.textField();
        salaryF   = AppColors.textField("0.00");
        hireDateF = AppColors.textField("2024-01-01");
        deptCombo  = new JComboBox<>(loadDepts()); AppColors.styleCombo(deptCombo);
        shiftCombo = new JComboBox<>(new String[]{"صباحية","مسائية","ليلية"}); AppColors.styleCombo(shiftCombo);
        statusCombo= new JComboBox<>(new String[]{"نشط","غير نشط"}); AppColors.styleCombo(statusCombo);

        int row = 1;
        row = fRow(card, g, row, "المعرّف:",          idLbl,      false);
        row = fRow(card, g, row, "الاسم الكامل: *",  nameF,      true);
        row = fRow(card, g, row, "اسم المستخدم: *", emailF, true);
        row = fRow(card, g, row, "الهاتف:",           phoneF,     true);
        row = fRow(card, g, row, "المنصب الوظيفي:",   positionF,  true);
        row = fRow(card, g, row, "الراتب (د.ل):",     salaryF,    true);
        row = fRow(card, g, row, "القسم:",             deptCombo,  true);
        row = fRow(card, g, row, "الوردية:",           shiftCombo, true);
        row = fRow(card, g, row, "تاريخ التعيين:",     hireDateF,  true);
        row = fRow(card, g, row, "الحالة:",            statusCombo,true);

        // فاصل
        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        card.add(new JSeparator(), g); row++;

        // الأزرار
        g.gridy = row; g.gridwidth = 2;
        JPanel btns = new JPanel(new GridLayout(2, 2, 8, 8));
        btns.setOpaque(false);
        JButton addBtn = AppColors.btnSuccess("➕  إضافة موظف");
        JButton updBtn = AppColors.btnPrimary("✏  تعديل البيانات");
        JButton delBtn = AppColors.btnDanger ("🗑  حذف الموظف");
        JButton clrBtn = AppColors.btnDark   ("↺  مسح الحقول");
        addBtn.addActionListener(e -> addEmp());
        updBtn.addActionListener(e -> updateEmp());
        delBtn.addActionListener(e -> deleteEmp());
        clrBtn.addActionListener(e -> clearForm());
        btns.add(addBtn); btns.add(updBtn); btns.add(delBtn); btns.add(clrBtn);
        card.add(btns, g);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildTable() {
        JPanel outer = new JPanel(new BorderLayout(0, 8));
        outer.setBackground(AppColors.BG_MAIN);
        outer.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));

        // شريط البحث
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBar.setBackground(AppColors.BG_MAIN);
        JTextField searchF = AppColors.textField();
        searchF.setPreferredSize(new Dimension(200, 32));
        JButton sb = AppColors.btnPrimary("🔍  بحث");
        JButton ab = AppColors.btnTeal("عرض الكل");
        sb.addActionListener(e -> search(searchF.getText().trim()));
        ab.addActionListener(e -> loadTable());
        searchBar.add(ab); searchBar.add(sb); searchBar.add(searchF);
        searchBar.add(AppColors.label("البحث:"));

        // الجدول
        String[] cols = {"المعرف","الاسم","اسم المستخدم","المنصب","القسم","الراتب","الوردية","الحالة"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        AppColors.styleTable(table);

        // تلوين صفوف الحالة
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String status = tableModel.getValueAt(row, 7) != null
                            ? tableModel.getValueAt(row, 7).toString() : "";
                    c.setBackground("نشط".equals(status)
                            ? (row % 2 == 0 ? AppColors.BG_WHITE : AppColors.BG_ROW_ALT)
                            : new Color(255, 235, 235));
                }
                return c;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillForm();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.PRIMARY, 2));

        outer.add(searchBar,  BorderLayout.NORTH);
        outer.add(scroll,     BorderLayout.CENTER);
        return outer;
    }

    // ================================================================
    //  العمليات
    // ================================================================
    private void addEmp() {
        try {
            if (nameF.getText().trim().isEmpty() || emailF.getText().trim().isEmpty()) {
                warn("الاسم واسم المستخدم حقول مطلوبة!"); return;
            }
            dao.addEmployee(buildEmp());
            ok("✔  تمت إضافة الموظف بنجاح!");
            loadTable(); clearForm();
        } catch (NumberFormatException ex) { warn("يرجى إدخال قيمة راتب صحيحة."); }
          catch (EmployeeException ex)     { err(ex.getMessage()); }
    }

    private void updateEmp() {
        if (table.getSelectedRow() == -1) { warn("يرجى تحديد موظف من الجدول."); return; }
        try {
            Employee e = buildEmp();
            e.setId((int) tableModel.getValueAt(table.getSelectedRow(), 0));
            dao.updateEmployee(e);
            ok("✔  تم تعديل بيانات الموظف!");
            loadTable();
        } catch (NumberFormatException ex) { warn("يرجى إدخال قيمة راتب صحيحة."); }
          catch (EmployeeException ex)     { err(ex.getMessage()); }
    }

    private void deleteEmp() {
        if (table.getSelectedRow() == -1) { warn("يرجى تحديد موظف من الجدول."); return; }
        String name = tableModel.getValueAt(table.getSelectedRow(), 1).toString();
        int ok = JOptionPane.showConfirmDialog(this,
            "هل تريد حذف الموظف: " + name + "؟", "تأكيد الحذف",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                dao.deleteEmployee((int) tableModel.getValueAt(table.getSelectedRow(), 0));
                ok("✔  تم حذف الموظف.");
                loadTable(); clearForm();
            } catch (EmployeeException ex) { err(ex.getMessage()); }
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        try {
            Employee e = dao.getEmployeeById((int) tableModel.getValueAt(row, 0));
            if (e == null) return;
            idLbl.setText(String.valueOf(e.getId()));
            nameF.setText(e.getName()); emailF.setText(e.getEmail()); phoneF.setText(e.getPhone());
            positionF.setText(e.getPosition()); salaryF.setText(String.valueOf(e.getSalary()));
            hireDateF.setText(e.getHireDate());
            deptCombo.setSelectedItem(e.getDepartmentName());
            shiftCombo.setSelectedItem(e.getShift());
            statusCombo.setSelectedItem(e.getStatus());
        } catch (EmployeeException ex) { /* تجاهل */ }
    }

    private void clearForm() {
        idLbl.setText("تلقائي"); nameF.setText(""); emailF.setText(""); phoneF.setText("");
        positionF.setText(""); salaryF.setText("0.00"); hireDateF.setText("2024-01-01");
        deptCombo.setSelectedIndex(0); shiftCombo.setSelectedIndex(0); statusCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    private void loadTable() {
        try {
            tableModel.setRowCount(0);
            for (Employee e : dao.getAllEmployees())
                tableModel.addRow(new Object[]{
                    e.getId(), e.getName(), e.getEmail(), e.getPosition(),
                    e.getDepartmentName(), String.format("%.2f د.ل", e.getSalary()),
                    e.getShift(), e.getStatus()
                });
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private void search(String kw) {
        try {
            tableModel.setRowCount(0);
            for (Employee e : dao.getAllEmployees())
                if (e.getName().contains(kw) || e.getPosition().contains(kw)
                        || e.getDepartmentName().contains(kw))
                    tableModel.addRow(new Object[]{
                        e.getId(), e.getName(), e.getEmail(), e.getPosition(),
                        e.getDepartmentName(), String.format("%.2f د.ل", e.getSalary()),
                        e.getShift(), e.getStatus()
                    });
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private Employee buildEmp() {
        Employee e = new Employee();
        e.setName(nameF.getText().trim()); e.setEmail(emailF.getText().trim());
        e.setPhone(phoneF.getText().trim()); e.setPosition(positionF.getText().trim());
        e.setSalary(Double.parseDouble(salaryF.getText().trim()));
        e.setDepartmentName((String) deptCombo.getSelectedItem());
        e.setShift((String) shiftCombo.getSelectedItem());
        e.setHireDate(hireDateF.getText().trim());
        e.setStatus((String) statusCombo.getSelectedItem());
        return e;
    }

    private String[] loadDepts() {
        java.util.List<String> d = new java.util.ArrayList<>();
        try {
            Statement s = DatabaseConnection.getConnection().createStatement();
            ResultSet r = s.executeQuery("SELECT name FROM departments ORDER BY name");
            while (r.next()) d.add(r.getString("name"));
            r.close(); s.close();
        } catch (SQLException ex) { d.add("عام"); }
        if (d.isEmpty()) d.add("عام");
        return d.toArray(new String[0]);
    }

    // مساعد إضافة صف
    private int fRow(JPanel p, GridBagConstraints g, int row, String lbl, Component field, boolean font) {
        g.gridwidth = 1; g.gridx = 0; g.gridy = row; g.weightx = 0.38;
        p.add(AppColors.label(lbl), g);
        g.gridx = 1; g.weightx = 0.62;
        if (font) field.setFont(new Font("Arial", Font.PLAIN, 13));
        p.add(field, g);
        return row + 1;
    }

    private void ok  (String m) { JOptionPane.showMessageDialog(this, m, "نجاح",  JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "تنبيه", JOptionPane.WARNING_MESSAGE); }
    private void err (String m) { JOptionPane.showMessageDialog(this, m, "خطأ",   JOptionPane.ERROR_MESSAGE); }
}
