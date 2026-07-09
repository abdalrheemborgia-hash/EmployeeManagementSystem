package gui;

import model.Attendance;
import exception.EmployeeException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import service.AttendanceService;
import service.EmployeeService;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * واجهة تسجيل وعرض سجلات الحضور والغياب.
 * <p>
 * - الموظف: يرى سجلاته الشخصية فقط (لا يستطيع التسجيل)
 * - المشرف والمدير: يرون جميع السجلات ويمكنهم التسجيل
 * </p>
 *
 * @author فريق المشروع
 */
public class AttendanceForm extends JFrame {

    private JTable            table;
    private DefaultTableModel tableModel;

    // ✅ طبقة الخدمة (بدل DAO والاتصال المباشر بقاعدة البيانات)
    private final AttendanceService attendanceService = new AttendanceService();
    private final EmployeeService   employeeService   = new EmployeeService();

    // حقول التسجيل (للمشرف والمدير فقط)
    private JComboBox<String> empCombo, statusCombo;
    private JTextField        dateF, checkInF, checkOutF, notesF;

    // بيانات المستخدم الحالي
    private final String currentUser;
    private final String currentRole;
    private final int    currentEmpId;   // معرّف الموظف في جدول employees
    private final int    currentUserId;  // معرّف المستخدم في جدول users

    // إحصائيات
    private JLabel statPresent, statAbsent, statLate, statTotal;

    /**
     * @param user        اسم المستخدم
     * @param role        دور المستخدم (Admin / Manager / Employee)
     * @param userId      معرّف المستخدم في جدول users
     * @param filterName  اسم للعنوان (غير مستخدم حالياً)
     */
    public AttendanceForm(String user, String role, int userId, String filterName) {
        this.currentUser   = user;
        this.currentRole   = role;
        this.currentUserId = userId;
        this.currentEmpId  = resolveEmployeeId(user, userId);
        buildUI();
        loadData();
    }

    // ================================================================
    //  بناء الواجهة
    // ================================================================
    private void buildUI() {
        boolean isPrivileged = !currentRole.equals("Employee");

        setTitle(isPrivileged
                ? "سجلات الحضور والغياب — جميع الموظفين"
                : "سجلات حضوري وغيابي — " + currentUser);
        setSize(1020, isPrivileged ? 640 : 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppColors.BG_MAIN);

        // شريط العنوان
        String headerSub = isPrivileged
                ? "المشرف / مدير النظام"
                : "عرض السجلات الشخصية فقط";
        JPanel header = AppColors.headerBar("📋  الحضور والغياب", headerSub);
        header.setBackground(AppColors.SUCCESS);

        // الجزء العلوي: إحصائيات سريعة
        JPanel statsBar = buildStatsBar();

        // الجزء الأوسط: نموذج التسجيل (للمشرف/مدير فقط)
        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(AppColors.BG_MAIN);

        if (isPrivileged) {
            JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            split.setDividerLocation(195);
            split.setBorder(null);
            split.setTopComponent(buildInputPanel());
            split.setBottomComponent(buildTablePanel(isPrivileged));
            content.add(split, BorderLayout.CENTER);
        } else {
            // الموظف يرى الجدول فقط مع رسالة توضيحية
            content.add(buildEmployeeInfoBanner(), BorderLayout.NORTH);
            content.add(buildTablePanel(false),    BorderLayout.CENTER);
        }

        JPanel top = new JPanel(new BorderLayout());
        top.add(header,   BorderLayout.NORTH);
        top.add(statsBar, BorderLayout.SOUTH);

        add(top,     BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        setVisible(true);
    }

    // ---- شريط الإحصائيات ----
    private JPanel buildStatsBar() {
        JPanel bar = new JPanel(new GridLayout(1, 4, 1, 0));
        bar.setBackground(new Color(220, 235, 220));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, AppColors.SUCCESS));

        statTotal   = statCard("إجمالي السجلات", "0",  new Color(13,  71,  161));
        statPresent = statCard("حاضر",           "0",  new Color(27,  94,  32));
        statAbsent  = statCard("غائب",            "0",  new Color(183, 28,  28));
        statLate    = statCard("متأخر",           "0",  new Color(230, 81,  0));

        bar.add(statTotal);
        bar.add(statPresent);
        bar.add(statAbsent);
        bar.add(statLate);
        return bar;
    }

    private JLabel statCard(String title, String value, Color color) {
        JLabel lbl = new JLabel(
            "<html><div style='text-align:center'>" +
            "<font size='+1'><b>" + value + "</b></font><br>" +
            "<small>" + title + "</small></div></html>",
            SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setForeground(color);
        lbl.setOpaque(true);
        lbl.setBackground(Color.WHITE);
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 220, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return lbl;
    }

    private void updateStats(List<Attendance> list) {
        int present = 0, absent = 0, late = 0;
        for (Attendance a : list) {
            switch (a.getStatus()) {
                case "حاضر"  -> present++;
                case "غائب"  -> absent++;
                case "متأخر" -> late++;
            }
        }
        int total = list.size();
        statTotal.setText(statHtml("إجمالي السجلات", String.valueOf(total),   new Color(13,  71,  161)));
        statPresent.setText(statHtml("حاضر",          String.valueOf(present), new Color(27,  94,  32)));
        statAbsent.setText(statHtml("غائب",           String.valueOf(absent),  new Color(183, 28,  28)));
        statLate.setText(statHtml("متأخر",            String.valueOf(late),    new Color(230, 81,  0)));
    }

    private String statHtml(String title, String value, Color color) {
        return "<html><div style='text-align:center'>" +
               "<font size='+2'><b><font color='#" + colorHex(color) + "'>" + value +
               "</font></b></font><br><small>" + title + "</small></div></html>";
    }

    private String colorHex(Color c) {
        return String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    // ---- بانر توضيحي للموظف ----
    private JPanel buildEmployeeInfoBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(232, 245, 232));
        banner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, AppColors.SUCCESS),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JLabel msg = new JLabel(
            "<html><b>👤 " + currentUser + "</b> — هذه الصفحة تعرض سجلات حضورك وغيابك الشخصية فقط.</html>",
            SwingConstants.RIGHT);
        msg.setFont(new Font("Arial", Font.PLAIN, 14));
        msg.setForeground(AppColors.SUCCESS);
        banner.add(msg, BorderLayout.CENTER);
        return banner;
    }

    // ---- لوحة التسجيل (مدير / مشرف فقط) ----
    private JPanel buildInputPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(AppColors.BG_MAIN);
        outer.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppColors.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.SUCCESS, 2),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 7, 6, 7);

        // عنوان القسم
        g.gridx=0; g.gridy=0; g.gridwidth=6;
        JLabel secTitle = new JLabel("  تسجيل حضور موظف", SwingConstants.RIGHT);
        secTitle.setFont(new Font("Arial", Font.BOLD, 13));
        secTitle.setForeground(AppColors.TEXT_WHITE);
        secTitle.setOpaque(true);
        secTitle.setBackground(AppColors.SUCCESS);
        secTitle.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        card.add(secTitle, g);
        g.gridwidth = 1;

        // صف 1: موظف + تاريخ + حالة
        g.gridx=0; g.gridy=1; card.add(AppColors.label("الموظف:"), g);
        g.gridx=1; empCombo = new JComboBox<>(loadEmpNames());
        AppColors.styleCombo(empCombo); card.add(empCombo, g);

        g.gridx=2; card.add(AppColors.label("التاريخ:"), g);
        g.gridx=3; dateF = AppColors.textField(LocalDate.now().toString()); card.add(dateF, g);

        g.gridx=4; card.add(AppColors.label("الحالة:"), g);
        g.gridx=5; statusCombo = new JComboBox<>(new String[]{"حاضر","غائب","متأخر"});
        AppColors.styleCombo(statusCombo); card.add(statusCombo, g);

        // صف 2: وقت الدخول + الخروج + ملاحظات
        g.gridx=0; g.gridy=2; card.add(AppColors.label("وقت الدخول:"), g);
        g.gridx=1; checkInF = AppColors.textField("08:00"); card.add(checkInF, g);

        g.gridx=2; card.add(AppColors.label("وقت الخروج:"), g);
        g.gridx=3; checkOutF = AppColors.textField("17:00"); card.add(checkOutF, g);

        g.gridx=4; card.add(AppColors.label("ملاحظات:"), g);
        g.gridx=5; notesF = AppColors.textField(); card.add(notesF, g);

        // زر التسجيل
        g.gridx=0; g.gridy=3; g.gridwidth=6;
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        bp.setOpaque(false);
        JButton btn = AppColors.btnSuccess("✔  تسجيل الحضور");
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        btn.addActionListener(e -> record());
        bp.add(btn);
        card.add(bp, g);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    // ---- لوحة الجدول ----
    private JPanel buildTablePanel(boolean isPrivileged) {
        JPanel outer = new JPanel(new BorderLayout(0, 6));
        outer.setBackground(AppColors.BG_MAIN);
        outer.setBorder(BorderFactory.createEmptyBorder(6, 12, 12, 12));

        // عنوان الجدول
        String tableTitle = isPrivileged ? "جميع سجلات الحضور" : "سجلات حضوري الشخصية";
        JLabel tableLbl = new JLabel("  " + tableTitle, SwingConstants.RIGHT);
        tableLbl.setFont(new Font("Arial", Font.BOLD, 13));
        tableLbl.setForeground(AppColors.SUCCESS);
        tableLbl.setBorder(BorderFactory.createEmptyBorder(4, 0, 6, 0));

        // أعمدة الجدول
        String[] cols = {"#", "اسم الموظف", "التاريخ", "الحالة", "وقت الدخول", "وقت الخروج", "ملاحظات"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        AppColors.styleTable(table);

        // ضبط عرض الأعمدة
        table.getColumnModel().getColumn(0).setPreferredWidth(45);   // رقم
        table.getColumnModel().getColumn(1).setPreferredWidth(160);  // الموظف
        table.getColumnModel().getColumn(2).setPreferredWidth(110);  // التاريخ
        table.getColumnModel().getColumn(3).setPreferredWidth(80);   // الحالة
        table.getColumnModel().getColumn(4).setPreferredWidth(90);   // دخول
        table.getColumnModel().getColumn(5).setPreferredWidth(90);   // خروج
        table.getColumnModel().getColumn(6).setPreferredWidth(180);  // ملاحظات

        // تلوين الصفوف حسب الحالة
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(col == 0 ? SwingConstants.CENTER : SwingConstants.RIGHT);
                if (!sel) {
                    String st = tableModel.getValueAt(row, 3) != null
                            ? tableModel.getValueAt(row, 3).toString() : "";
                    c.setBackground(switch (st) {
                        case "حاضر"  -> AppColors.ROW_PRESENT;
                        case "غائب"  -> AppColors.ROW_ABSENT;
                        case "متأخر" -> AppColors.ROW_LATE;
                        default      -> AppColors.BG_WHITE;
                    });
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.SUCCESS, 2));

        // شريط أسفل: وسيلة إيضاح + زر تحديث
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(AppColors.BG_MAIN);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 2));
        legend.setBackground(AppColors.BG_MAIN);
        legend.add(dot(new Color(27, 120, 27),  "حاضر"));
        legend.add(dot(new Color(180, 30, 30),   "غائب"));
        legend.add(dot(new Color(180, 120, 0),   "متأخر"));

        JButton refreshBtn = AppColors.btnTeal("↻  تحديث");
        refreshBtn.addActionListener(e -> loadData());
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        refreshPanel.setBackground(AppColors.BG_MAIN);
        refreshPanel.add(refreshBtn);

        bottomBar.add(refreshPanel, BorderLayout.WEST);
        bottomBar.add(legend,       BorderLayout.EAST);

        outer.add(tableLbl,  BorderLayout.NORTH);
        outer.add(scroll,    BorderLayout.CENTER);
        outer.add(bottomBar, BorderLayout.SOUTH);
        return outer;
    }

    // ================================================================
    //  العمليات — عبر طبقة الخدمة (لا SQL في الواجهة)
    // ================================================================

    /** تسجيل حضور موظف (متاح للمشرف والمدير فقط) */
    private void record() {
        try {
            String emp = (String) empCombo.getSelectedItem();
            if (emp == null) { warn("يرجى اختيار موظف."); return; }

            String date = dateF.getText().trim();
            if (date.isEmpty()) { warn("يرجى إدخال التاريخ."); return; }

            Attendance a = new Attendance();
            a.setEmployeeId(getEmpId(emp));
            a.setEmployeeName(emp);
            a.setDate(date);
            a.setStatus((String) statusCombo.getSelectedItem());
            a.setCheckIn(checkInF.getText().trim());
            a.setCheckOut(checkOutF.getText().trim());
            a.setNotes(notesF.getText().trim());

            attendanceService.recordAttendance(a);
            ok("✔  تم تسجيل حضور " + emp + " بنجاح!");
            loadData();

        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    /** تحميل البيانات — الموظف يرى سجلاته فقط، المشرف/المدير يرى الكل */
    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<Attendance> list;

            if (currentRole.equals("Employee")) {
                if (currentEmpId > 0) {
                    list = attendanceService.getAttendanceByEmployee(currentEmpId);
                } else {
                    tableModel.addRow(new Object[]{"—", "لا توجد سجلات مرتبطة بحسابك.", "", "", "", "", ""});
                    updateStats(new java.util.ArrayList<>());
                    return;
                }
            } else {
                list = attendanceService.getAllAttendance();
            }

            int counter = 1;
            for (Attendance a : list) {
                tableModel.addRow(new Object[]{
                    counter++,
                    a.getEmployeeName(),
                    a.getDate(),
                    a.getStatus(),
                    a.getCheckIn(),
                    a.getCheckOut(),
                    a.getNotes()
                });
            }

            updateStats(list);

        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    // ================================================================
    //  مساعدات تعتمد على طبقة الخدمة (لا SQL هنا)
    // ================================================================

    /** يحدّد معرّف الموظف المرتبط بالمستخدم الحالي عبر طبقة الخدمة. */
    private int resolveEmployeeId(String userName, int userId) {
        try {
            return employeeService.resolveEmployeeId(userName, userId);
        } catch (EmployeeException ex) {
            return 0; // لم يُعثر
        }
    }

    /** أسماء الموظفين النشطين لقائمة الاختيار عبر طبقة الخدمة. */
    private String[] loadEmpNames() {
        try {
            List<String> names = employeeService.getActiveEmployeeNames();
            if (names.isEmpty()) return new String[]{"لا يوجد موظفون"};
            return names.toArray(new String[0]);
        } catch (EmployeeException ex) {
            return new String[]{"لا يوجد موظفون"};
        }
    }

    /** الحصول على معرّف الموظف بالاسم عبر طبقة الخدمة. */
    private int getEmpId(String name) {
        try {
            return employeeService.getEmployeeIdByName(name);
        } catch (EmployeeException ex) {
            return 0;
        }
    }

    // ================================================================
    //  مساعدات الواجهة
    // ================================================================
    private JPanel dot(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel d = new JLabel("■");
        d.setFont(new Font("Arial", Font.BOLD, 14));
        d.setForeground(c);
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        l.setForeground(AppColors.TEXT_MUTED);
        p.add(d); p.add(l);
        return p;
    }

    private void ok  (String m) { JOptionPane.showMessageDialog(this, m, "نجاح",  JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "تنبيه", JOptionPane.WARNING_MESSAGE); }
    private void err (String m) { JOptionPane.showMessageDialog(this, m, "خطأ",   JOptionPane.ERROR_MESSAGE); }
}