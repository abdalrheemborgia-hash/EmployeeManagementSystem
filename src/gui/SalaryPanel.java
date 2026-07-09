package gui;

import model.Employee;
import exception.EmployeeException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import service.EmployeeService;
import service.UserService;

import java.awt.*;

/**
 * واجهة الرواتب وحاسبة الراتب الصافي.
 *
 * @author فريق المشروع
 */
public class SalaryPanel extends JFrame {

    private JTable            table;
    private DefaultTableModel tableModel;

    // ✅ طبقة الخدمة (بدل DAO والاتصال المباشر بقاعدة البيانات)
    private final EmployeeService employeeService = new EmployeeService();
    private final UserService     userService     = new UserService();

    private JTextField baseSalaryF, taxF, bonusF, deductF;
    private JLabel     netLabel;

    private final String currentRole;
    private final int    currentUserId;

    public SalaryPanel(String user, String role, int uid) {
        this.currentRole   = role;
        this.currentUserId = uid;
        buildUI();
        loadData();
    }

    private void buildUI() {
        setTitle("الرواتب");
        setSize(950, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppColors.BG_MAIN);

        JPanel header = AppColors.headerBar("💰  الرواتب وحاسبة الراتب الصافي", "");
        header.setBackground(AppColors.TEAL);

        // ---- جدول الرواتب ----
        String[] cols = {"المعرف","الاسم الكامل","المنصب الوظيفي","القسم","الراتب الأساسي","الوردية"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        AppColors.styleTable(table);

        // تلوين بديل للصفوف
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? AppColors.BG_WHITE : AppColors.BG_ROW_ALT);
                return c;
            }
        });

        // عند اختيار صف — نقل الراتب للحاسبة تلقائياً
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                String sal = tableModel.getValueAt(table.getSelectedRow(), 4)
                        .toString().replace(" د.ل", "").replace(",", "");
                baseSalaryF.setText(sal);
                calcNet();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.TEAL, 2));

        JPanel tableOuter = new JPanel(new BorderLayout());
        tableOuter.setBackground(AppColors.BG_MAIN);
        tableOuter.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));
        tableOuter.add(scroll, BorderLayout.CENTER);

        // ---- حاسبة الراتب ----
        JPanel calcCard = new JPanel(new GridBagLayout());
        calcCard.setBackground(AppColors.BG_WHITE);
        calcCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.TEAL, 2),
            BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 10, 8, 10);

        // عنوان
        g.gridx=0; g.gridy=0; g.gridwidth=4;
        JLabel calcTitle = new JLabel("  🧮  حاسبة الراتب الصافي", SwingConstants.RIGHT);
        calcTitle.setFont(new Font("Arial", Font.BOLD, 14));
        calcTitle.setForeground(AppColors.TEXT_WHITE);
        calcTitle.setOpaque(true); calcTitle.setBackground(AppColors.TEAL);
        calcTitle.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        calcCard.add(calcTitle, g); g.gridwidth=1;

        // صف الحقول
        g.gridx=0; g.gridy=1; calcCard.add(AppColors.label("الراتب الأساسي (د.ل):"), g);
        g.gridx=1; baseSalaryF = AppColors.textField("0.00"); calcCard.add(baseSalaryF, g);

        g.gridx=2; calcCard.add(AppColors.label("نسبة الضريبة (%):"), g);
        g.gridx=3; taxF = AppColors.textField("10"); calcCard.add(taxF, g);

        g.gridx=0; g.gridy=2; calcCard.add(AppColors.label("العلاوة (د.ل):"), g);
        g.gridx=1; bonusF = AppColors.textField("0.00"); calcCard.add(bonusF, g);

        g.gridx=2; calcCard.add(AppColors.label("خصومات أخرى (د.ل):"), g);
        g.gridx=3; deductF = AppColors.textField("0.00"); calcCard.add(deductF, g);

        // زر الحساب
        g.gridx=0; g.gridy=3; g.gridwidth=2;
        JButton calcBtn = AppColors.btnTeal("🧮  احسب الراتب الصافي");
        calcBtn.setFont(new Font("Arial", Font.BOLD, 14));
        calcBtn.setBorder(BorderFactory.createEmptyBorder(11, 25, 11, 25));
        calcBtn.addActionListener(e -> calcNet());
        calcCard.add(calcBtn, g);

        // عرض النتيجة
        g.gridx=2; g.gridwidth=2;
        netLabel = new JLabel("الراتب الصافي:  0.00 د.ل", SwingConstants.CENTER);
        netLabel.setFont(new Font("Arial", Font.BOLD, 17));
        netLabel.setForeground(AppColors.TEXT_WHITE);
        netLabel.setOpaque(true);
        netLabel.setBackground(AppColors.TEAL);
        netLabel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        calcCard.add(netLabel, g);

        JPanel calcOuter = new JPanel(new BorderLayout());
        calcOuter.setBackground(AppColors.BG_MAIN);
        calcOuter.setBorder(BorderFactory.createEmptyBorder(6, 12, 12, 12));
        calcOuter.add(calcCard, BorderLayout.CENTER);

        add(header,     BorderLayout.NORTH);
        add(tableOuter, BorderLayout.CENTER);
        add(calcOuter,  BorderLayout.SOUTH);
        setVisible(true);
    }

    private void calcNet() {
        try {
            double base   = Double.parseDouble(baseSalaryF.getText().trim());
            double tax    = Double.parseDouble(taxF.getText().trim());
            double bonus  = Double.parseDouble(bonusF.getText().trim());
            double deduct = Double.parseDouble(deductF.getText().trim());
            double net    = base - (base * tax / 100.0) + bonus - deduct;

            netLabel.setText(String.format("الراتب الصافي:  %.2f د.ل", net));
            netLabel.setBackground(net >= 0 ? AppColors.TEAL : AppColors.DANGER);
        } catch (NumberFormatException ex) {
            warn("يرجى إدخال أرقام صحيحة في جميع الحقول.");
        }
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            String myEmail = currentRole.equals("Employee") ? getEmail(currentUserId) : "";
            for (Employee e : employeeService.getAllEmployees()) {
                if (currentRole.equals("Employee") && !e.getEmail().equals(myEmail)) continue;
                tableModel.addRow(new Object[]{
                    e.getId(), e.getName(), e.getPosition(), e.getDepartmentName(),
                    String.format("%.2f د.ل", e.getSalary()), e.getShift()
                });
            }
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    /** جلب بريد المستخدم عبر طبقة الخدمة (لا SQL في الواجهة). */
    private String getEmail(int uid) {
        try {
            return userService.getEmailById(uid);
        } catch (EmployeeException ex) {
            return "";
        }
    }

    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "تنبيه", JOptionPane.WARNING_MESSAGE); }
    private void err (String m) { JOptionPane.showMessageDialog(this, m, "خطأ",   JOptionPane.ERROR_MESSAGE); }
}