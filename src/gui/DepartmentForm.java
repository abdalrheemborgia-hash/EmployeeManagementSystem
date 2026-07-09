package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import service.DepartmentService;
import model.Department;
import exception.EmployeeException;

import java.awt.*;
import java.util.List;

/**
 * واجهة إدارة الأقسام.
 *
 * @author فريق المشروع
 */
public class DepartmentForm extends JFrame {

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        nameF, descF, managerF;
    private JLabel            idLbl;

    // ✅ طبقة الخدمة (بدل الاتصال المباشر بقاعدة البيانات)
    private final DepartmentService departmentService = new DepartmentService();

    public DepartmentForm(String user, String role) {
        buildUI();
        loadData();
    }

    private void buildUI() {
        setTitle("إدارة الأقسام");
        setSize(860, 540);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppColors.BG_MAIN);

        JPanel header = AppColors.headerBar("🏢  إدارة الأقسام", "");
        header.setBackground(AppColors.PURPLE);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(500);
        split.setBorder(null);

        split.setLeftComponent(buildTable());   // الجدول يسار
        split.setRightComponent(buildForm());   // الإدخال يمين

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
            BorderFactory.createLineBorder(AppColors.PURPLE, 2),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 5, 7, 5);

        // عنوان
        g.gridx=0; g.gridy=0; g.gridwidth=2;
        JLabel t = new JLabel("  بيانات القسم", SwingConstants.RIGHT);
        t.setFont(new Font("Arial", Font.BOLD, 14));
        t.setForeground(AppColors.TEXT_WHITE);
        t.setOpaque(true); t.setBackground(AppColors.PURPLE);
        t.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        card.add(t, g); g.gridwidth=1;

        idLbl   = new JLabel("تلقائي");
        nameF   = AppColors.textField();
        descF   = AppColors.textField();
        managerF= AppColors.textField();

        int row = 1;
        row = fRow(card, g, row, "المشرف المسؤول:",     managerF, true);
        row = fRow(card, g, row, "الوصف:",              descF,    true);
        row = fRow(card, g, row, "اسم القسم: *",       nameF,    true);
        row = fRow(card, g, row, "المعرّف:",           idLbl,    false);

        // فاصل
        g.gridx=0; g.gridy=row; g.gridwidth=2;
        card.add(new JSeparator(), g); row++;

        // أزرار
        g.gridy=row; g.gridwidth=2;
        JPanel btns = new JPanel(new GridLayout(2, 2, 8, 8));
        btns.setOpaque(false);
        JButton addBtn = AppColors.btnSuccess("➕  إضافة قسم");
        JButton updBtn = AppColors.btnPurple ("✏  تعديل القسم");
        JButton delBtn = AppColors.btnDanger ("🗑  حذف القسم");
        JButton clrBtn = AppColors.btnDark   ("↺  مسح الحقول");
        addBtn.addActionListener(e -> add());
        updBtn.addActionListener(e -> update());
        delBtn.addActionListener(e -> delete());
        clrBtn.addActionListener(e -> clear());
        btns.add(addBtn); btns.add(updBtn); btns.add(delBtn); btns.add(clrBtn);
        card.add(btns, g);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildTable() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(AppColors.BG_MAIN);
        outer.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));

        String[] cols = {"المعرف","اسم القسم","الوصف","المشرف المسؤول"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        AppColors.styleTable(table);
        table.getTableHeader().setBackground(AppColors.PURPLE);

        // تلوين بديل
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? AppColors.BG_WHITE : new Color(245, 240, 255));
                return c;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.getSelectedRow();
                idLbl.setText(tableModel.getValueAt(r, 0).toString());
                nameF.setText(tableModel.getValueAt(r, 1).toString());
                descF.setText(tableModel.getValueAt(r, 2).toString());
                managerF.setText(tableModel.getValueAt(r, 3).toString());
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.PURPLE, 2));
        outer.add(scroll, BorderLayout.CENTER);
        return outer;
    }

    // ================================================================
    //  العمليات — عبر طبقة الخدمة (لا SQL في الواجهة)
    // ================================================================
    private void add() {
        if (nameF.getText().trim().isEmpty()) { warn("اسم القسم مطلوب."); return; }
        try {
            Department d = new Department();
            d.setName(nameF.getText().trim());
            d.setDescription(descF.getText().trim());
            d.setManagerName(managerF.getText().trim());
            departmentService.validateAndAddDepartment(d);
            ok("✔  تمت إضافة القسم بنجاح!"); loadData(); clear();
        } catch (EmployeeException ex) { err("خطأ: " + ex.getMessage()); }
    }

    private void update() {
        if (table.getSelectedRow() == -1) { warn("يرجى تحديد قسم."); return; }
        try {
            Department d = new Department();
            d.setId(Integer.parseInt(idLbl.getText()));
            d.setName(nameF.getText().trim());
            d.setDescription(descF.getText().trim());
            d.setManagerName(managerF.getText().trim());
            departmentService.updateDepartment(d);
            ok("✔  تم تعديل القسم!"); loadData();
        } catch (NumberFormatException ex) { warn("يرجى تحديد قسم صحيح من الجدول."); }
          catch (EmployeeException ex)     { err("خطأ: " + ex.getMessage()); }
    }

    private void delete() {
        if (table.getSelectedRow() == -1) { warn("يرجى تحديد قسم."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "هل تريد حذف هذا القسم؟",
                "تأكيد الحذف", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                departmentService.deleteDepartment(Integer.parseInt(idLbl.getText()));
                ok("✔  تم حذف القسم."); loadData(); clear();
            } catch (NumberFormatException ex) { warn("يرجى تحديد قسم صحيح من الجدول."); }
              catch (EmployeeException ex)     { err("خطأ: " + ex.getMessage()); }
        }
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<Department> list = departmentService.getAllDepartments();
            for (Department d : list)
                tableModel.addRow(new Object[]{
                    d.getId(), d.getName(), d.getDescription(), d.getManagerName()
                });
        } catch (EmployeeException ex) { err("خطأ: " + ex.getMessage()); }
    }

    private void clear() {
        idLbl.setText("تلقائي"); nameF.setText(""); descF.setText(""); managerF.setText("");
        table.clearSelection();
    }

    private int fRow(JPanel p, GridBagConstraints g, int row, String lbl, Component field, boolean font) {

        g.gridwidth = 1;
        g.gridy = row;

        // الحقل أولاً (يسار)
        g.gridx = 0;
        g.weightx = 0.62;
        if (font) field.setFont(new Font("Arial", Font.PLAIN, 13));
        p.add(field, g);

        // التسمية ثانياً (يمين)
        g.gridx = 1;
        g.weightx = 0.38;
        p.add(AppColors.label(lbl), g);

        return row + 1;
    }

    private void ok  (String m) { JOptionPane.showMessageDialog(this, m, "نجاح",  JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "تنبيه", JOptionPane.WARNING_MESSAGE); }
    private void err (String m) { JOptionPane.showMessageDialog(this, m, "خطأ",   JOptionPane.ERROR_MESSAGE); }
}