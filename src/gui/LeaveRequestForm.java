package gui;

import model.LeaveRequest;
import exception.EmployeeException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import dataaccess.LeaveRequestDAO;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * واجهة طلبات الإجازة — تقديم وموافقة ورفض.
 *
 * @author فريق المشروع
 */
public class LeaveRequestForm extends JFrame {

    private JTable            table;
    private DefaultTableModel tableModel;
    private final LeaveRequestDAO dao;

    private JComboBox<String> typeCombo;
    private JTextField        startF, endF, reasonF;

    private final String currentUser;
    private final String currentRole;
    private final int    currentUserId;

    public LeaveRequestForm(String user, String role, int uid) {
        this.currentUser   = user;
        this.currentRole   = role;
        this.currentUserId = uid;
        this.dao           = new LeaveRequestDAO();
        buildUI();
        loadData();
    }

    private void buildUI() {
        setTitle("طلبات الإجازة");
        setSize(980, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppColors.BG_MAIN);

        JPanel header = AppColors.headerBar("🏖  طلبات الإجازة", "");
        header.setBackground(AppColors.WARNING);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setDividerLocation(175);
        split.setBorder(null);
        split.setTopComponent(buildForm());
        split.setBottomComponent(buildTable());

        add(header, BorderLayout.NORTH);
        add(split,  BorderLayout.CENTER);
        setVisible(true);
    }

    // ---- نموذج التقديم ----
    private JPanel buildForm() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(AppColors.BG_MAIN);
        outer.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppColors.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.WARNING, 2),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 7, 6, 7);

        g.gridx=0; g.gridy=0; card.add(AppColors.label("نوع الإجازة:"), g);
        g.gridx=1; typeCombo = new JComboBox<>(new String[]{"سنوية","مرضية","طارئة","أمومة","أخرى"});
        AppColors.styleCombo(typeCombo); card.add(typeCombo, g);

        g.gridx=2; card.add(AppColors.label("تاريخ البدء:"), g);
        g.gridx=3; startF = AppColors.textField(LocalDate.now().toString()); card.add(startF, g);

        g.gridx=4; card.add(AppColors.label("تاريخ الانتهاء:"), g);
        g.gridx=5; endF = AppColors.textField(LocalDate.now().plusDays(1).toString()); card.add(endF, g);

        g.gridx=0; g.gridy=1; card.add(AppColors.label("سبب الإجازة:"), g);
        g.gridx=1; g.gridwidth=4; reasonF = AppColors.textField(); card.add(reasonF, g); g.gridwidth=1;

        g.gridx=5; g.gridy=1;
        JButton submitBtn = AppColors.btnWarning("  تقديم الطلب  ");
        submitBtn.setFont(new Font("Arial", Font.BOLD, 13));
        submitBtn.addActionListener(e -> submit());
        card.add(submitBtn, g);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    // ---- جدول الطلبات ----
    private JPanel buildTable() {
        JPanel outer = new JPanel(new BorderLayout(0, 6));
        outer.setBackground(AppColors.BG_MAIN);
        outer.setBorder(BorderFactory.createEmptyBorder(6, 12, 12, 12));

        String[] cols = {"المعرف","الموظف","النوع","البدء","الانتهاء","السبب","الحالة","المراجع"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        AppColors.styleTable(table);

        // تلوين حسب الحالة
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String st = tableModel.getValueAt(row, 6) != null
                            ? tableModel.getValueAt(row, 6).toString() : "";
                    c.setBackground(switch (st) {
                        case "مقبول"  -> AppColors.ROW_APPROVED;
                        case "مرفوض"  -> AppColors.ROW_REJECTED;
                        default        -> AppColors.ROW_PENDING;
                    });
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.WARNING, 2));

        // شريط الإجراءات (للمشرف والمدير فقط)
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(AppColors.BG_MAIN);

        // وسيلة إيضاح
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4));
        legend.setBackground(AppColors.BG_MAIN);
        legend.add(dot(new Color(40,160,40),  "مقبول"));
        legend.add(dot(new Color(200,40,40),  "مرفوض"));
        legend.add(dot(new Color(190,140,0),  "معلّق"));
        bottom.add(legend, BorderLayout.EAST);

        if (!currentRole.equals("Employee")) {
            JPanel actBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
            actBar.setBackground(AppColors.BG_MAIN);
            actBar.add(AppColors.label("بعد تحديد الطلب:"));
            JButton apBtn = AppColors.btnSuccess("✔  قبول الطلب");
            JButton rjBtn = AppColors.btnDanger ("✘  رفض الطلب");
            apBtn.addActionListener(e -> updateStatus("مقبول"));
            rjBtn.addActionListener(e -> updateStatus("مرفوض"));
            actBar.add(apBtn); actBar.add(rjBtn);
            bottom.add(actBar, BorderLayout.WEST);
        }

        outer.add(scroll,  BorderLayout.CENTER);
        outer.add(bottom,  BorderLayout.SOUTH);
        return outer;
    }

    private void submit() {
        if (reasonF.getText().trim().isEmpty()) { warn("يرجى إدخال سبب الإجازة."); return; }
        try {
            LeaveRequest r = new LeaveRequest();
            r.setEmployeeId(currentUserId); r.setEmployeeName(currentUser);
            r.setLeaveType((String) typeCombo.getSelectedItem());
            r.setStartDate(startF.getText().trim()); r.setEndDate(endF.getText().trim());
            r.setReason(reasonF.getText().trim()); r.setStatus("معلّق"); r.setReviewedBy("");
            dao.submitLeaveRequest(r);
            ok("✔  تم تقديم طلب الإجازة بنجاح!");
            reasonF.setText(""); loadData();
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private void updateStatus(String newStatus) {
        if (table.getSelectedRow() == -1) { warn("يرجى تحديد طلب من الجدول."); return; }
        try {
            dao.updateLeaveStatus((int) tableModel.getValueAt(table.getSelectedRow(), 0),
                                  newStatus, currentUser);
            ok("✔  تم " + ("مقبول".equals(newStatus) ? "قبول" : "رفض") + " الطلب.");
            loadData();
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<LeaveRequest> list = currentRole.equals("Employee")
                    ? dao.getLeaveByEmployee(currentUserId) : dao.getAllLeaveRequests();
            for (LeaveRequest r : list)
                tableModel.addRow(new Object[]{
                    r.getId(), r.getEmployeeName(), r.getLeaveType(),
                    r.getStartDate(), r.getEndDate(), r.getReason(),
                    r.getStatus(), r.getReviewedBy()
                });
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private JPanel dot(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)); p.setOpaque(false);
        JLabel d = new JLabel("■"); d.setFont(new Font("Arial", Font.BOLD, 14)); d.setForeground(c);
        JLabel l = new JLabel(text); l.setFont(new Font("Arial", Font.PLAIN, 12)); l.setForeground(AppColors.TEXT_MUTED);
        p.add(d); p.add(l); return p;
    }

    private void ok  (String m) { JOptionPane.showMessageDialog(this, m, "نجاح",  JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "تنبيه", JOptionPane.WARNING_MESSAGE); }
    private void err (String m) { JOptionPane.showMessageDialog(this, m, "خطأ",   JOptionPane.ERROR_MESSAGE); }
}
