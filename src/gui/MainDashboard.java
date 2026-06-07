package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * لوحة التحكم الرئيسية — تعرض الأزرار حسب صلاحية المستخدم.
 *
 * @author فريق المشروع
 */
public class MainDashboard extends JFrame {

    private final String user;
    private final String role;
    private final int    uid;

    public MainDashboard(String user, String role, int uid) {
        this.user = user; this.role = role; this.uid = uid;
        buildUI();
    }

    private void buildUI() {
        setTitle("نظام إدارة الموظفين — لوحة التحكم");
        setSize(860, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppColors.BG_MAIN);

        // ---- شريط العنوان العلوي ----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.BG_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        JLabel titleLbl = new JLabel("  👔  نظام إدارة الموظفين");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 20));
        titleLbl.setForeground(AppColors.TEXT_WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        userPanel.setOpaque(false);
        JLabel userLbl = new JLabel("مرحباً،  " + user + "   |   " + arabicRole());
        userLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        userLbl.setForeground(new Color(200, 220, 255));
        JButton logoutBtn = AppColors.btnDanger("  خروج  ");
        logoutBtn.addActionListener(e -> logout());
        userPanel.add(userLbl);
        userPanel.add(logoutBtn);

        header.add(titleLbl, BorderLayout.WEST);
        header.add(userPanel, BorderLayout.EAST);

        // ---- شريط الترحيب ----
        JPanel welcome = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        welcome.setBackground(new Color(220, 232, 255));
        welcome.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, AppColors.BORDER_COLOR));
        JLabel wLbl = new JLabel("اختر الخدمة التي تريدها ⬇   ");
        wLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        wLbl.setForeground(AppColors.PRIMARY_DARK);
        welcome.add(wLbl);

        // ---- شبكة البطاقات ----
        JPanel grid = new JPanel(new GridLayout(0, 3, 18, 18));
        grid.setBackground(AppColors.BG_MAIN);
        grid.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));
        addCards(grid);

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(AppColors.BG_MAIN);

        JPanel top = new JPanel(new BorderLayout());
        top.add(header,  BorderLayout.NORTH);
        top.add(welcome, BorderLayout.SOUTH);

        add(top,    BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        setVisible(true);
    }

    private void addCards(JPanel p) {
        if (isPrivileged()) {
            card(p, "👥", "إدارة الموظفين",   AppColors.PRIMARY,  AppColors.PRIMARY_DARK,  e -> new EmployeeManagementForm(user, role, uid));
            card(p, "🏢", "إدارة الأقسام",    AppColors.PURPLE,   AppColors.PURPLE_DARK,   e -> new DepartmentForm(user, role));
        }
        // زر الحضور لجميع الأدوار — الواجهة تتحكم بما يُعرض حسب الدور

        card(p, "📋", role.equals("Employee") ? "حضوري وغيابي" : "سجلات الحضور",
             AppColors.SUCCESS, AppColors.SUCCESS_DARK,
             e -> new AttendanceForm(user, role, uid, user));
        card(p, "🏖", "طلبات الإجازة",    AppColors.WARNING,  AppColors.WARNING_DARK,  e -> new LeaveRequestForm(user, role, uid));
        card(p, "💰", "الرواتب",           AppColors.TEAL,     AppColors.TEAL_DARK,     e -> new SalaryPanel(user, role, uid));
        if (role.equals("Admin")) {
            card(p, "👤", "إدارة المستخدمين", AppColors.DARK,     AppColors.DARK_HOVER,    e -> new UserManagementForm(user));
            card(p, "📊", "التقارير",         AppColors.DANGER,   AppColors.DANGER_DARK,   e -> new ReportsPanel(user));
        }
        card(p, "🚪", "تسجيل الخروج",     new Color(80,80,90), new Color(30,30,40),   e -> logout());
    }

    private void card(JPanel panel, String icon, String label,
                      Color bg, Color hover, ActionListener action) {
        JButton btn = new JButton(
            "<html><center><font size='+3'>" + icon + "</font><br><br>" + label + "</center></html>");
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(AppColors.TEXT_WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(210, 100));
        btn.addActionListener(action);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(bg); }
        });
        panel.add(btn);
    }

    private void logout() {
        int ok = JOptionPane.showConfirmDialog(this,
            "هل تريد تسجيل الخروج؟", "تأكيد", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) { new LoginForm(); dispose(); }
    }

    private boolean isPrivileged() { return role.equals("Admin") || role.equals("Manager"); }

    private String arabicRole() {
        return switch (role) {
            case "Admin"   -> "مدير النظام";
            case "Manager" -> "مشرف";
            default        -> "موظف";
        };
    }
}
