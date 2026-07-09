package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import service.EmployeeService;

public class MainDashboard extends JFrame {

    private final String user;
    private final String role;
    private final int    uid;
    private final EmployeeService employeeService;

    private int    totalEmployees   = 0;
    private int    totalDepartments = 0;
    private double totalSalaries    = 0.0;

    private JPanel statsBar;   // شريط الإحصائيات (KPI)
    private JPanel gridPanel;  // شبكة أزرار التنقّل

    public MainDashboard(String user, String role, int uid) {
        this.user = user;
        this.role = role;
        this.uid  = uid;
        this.employeeService = new EmployeeService();

        buildUI();

        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                refreshDashboardData();
            }
        });
    }

    private void refreshDashboardData() {
        try {
            this.totalEmployees   = employeeService.getTotalEmployeesCount();
            this.totalDepartments = employeeService.getTotalDepartmentsCount();
            this.totalSalaries    = employeeService.getTotalSalaryBudget();
        } catch (Exception e) {
            System.err.println("خطأ تحديث الإحصائيات: " + e.getMessage());
        }
        // إعادة بناء شريط الإحصائيات (للمدير/المشرف فقط)
        if (statsBar != null) {
            statsBar.removeAll();
            addStatCards(statsBar);
            statsBar.revalidate();
            statsBar.repaint();
        }
    }

    private void buildUI() {
        setTitle("نظام إدارة الموظفين — لوحة التحكم الإحصائية");
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppColors.BG_MAIN);
        setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // ================= شريط العنوان =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.BG_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel titleLbl = new JLabel("👔  نظام إدارة الموظفين المتطور");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 19));
        titleLbl.setForeground(AppColors.TEXT_WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        userPanel.setOpaque(false);
        JLabel userLbl = new JLabel("مرحباً،  " + user + "   |   " + arabicRole());
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLbl.setForeground(new Color(200, 220, 255));
        JButton logoutBtn = AppColors.btnDanger("   خروج   ");
        logoutBtn.addActionListener(e -> logout());
        userPanel.add(userLbl);
        userPanel.add(logoutBtn);

        header.add(titleLbl,  BorderLayout.EAST);
        header.add(userPanel, BorderLayout.WEST);

        // ================= المحتوى الرئيسي =================
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(AppColors.BG_MAIN);
        body.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ---- عنوان قسم الإحصائيات ----
        if (isPrivileged()) {
            JLabel statsTitle = sectionTitle("📊  نظرة عامة إحصائية");
            body.add(statsTitle);
            body.add(Box.createVerticalStrut(12));

            // ---- شريط بطاقات KPI ----
            statsBar = new JPanel(new GridLayout(1, 3, 18, 0));
            statsBar.setBackground(AppColors.BG_MAIN);
            statsBar.setAlignmentX(Component.RIGHT_ALIGNMENT);
            statsBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            addStatCards(statsBar);
            body.add(statsBar);
            body.add(Box.createVerticalStrut(26));
        }

        // ---- عنوان قسم التنقّل ----
        JLabel navTitle = sectionTitle("🗂  الوصول السريع");
        body.add(navTitle);
        body.add(Box.createVerticalStrut(12));

        // ---- شبكة أزرار التنقّل ----
        gridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        gridPanel.setBackground(AppColors.BG_MAIN);
        gridPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        addNavCards(gridPanel);
        body.add(gridPanel);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(AppColors.BG_MAIN);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        setVisible(true);
    }

    // ================================================================
    //  بطاقات الإحصائيات (KPI) — أرقام بارزة وواضحة
    // ================================================================
    private void addStatCards(JPanel bar) {
        bar.add(statCard("👥", "إجمالي الموظفين", String.valueOf(totalEmployees),
                AppColors.PRIMARY));
        bar.add(statCard("🏢", "عدد الأقسام", String.valueOf(totalDepartments),
                AppColors.PURPLE));
        bar.add(statCard("💰", "ميزانية الرواتب (د.ل)", String.format("%,.0f", totalSalaries),
                AppColors.TEAL));
    }

    private JPanel statCard(String icon, String title, String value, Color accent) {
        RoundedPanel card = new RoundedPanel(Color.WHITE, accent);
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        // الأيقونة (يمين)
        JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
        iconLbl.setForeground(accent);
        iconLbl.setPreferredSize(new Dimension(56, 56));

        // النص (يسار): الرقم فوق العنوان
        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLbl.setForeground(new Color(33, 37, 41));
        valueLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLbl.setForeground(new Color(110, 118, 129));
        titleLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        txt.add(valueLbl);
        txt.add(Box.createVerticalStrut(2));
        txt.add(titleLbl);

        card.add(iconLbl, BorderLayout.EAST);
        card.add(txt,     BorderLayout.CENTER);
        return card;
    }

    // ================================================================
    //  أزرار التنقّل — أيقونة + اسم فقط (نظيفة بلا أرقام)
    // ================================================================
    private void addNavCards(JPanel p) {
        if (isPrivileged()) {
            navCard(p, "👥", "إدارة الموظفين", AppColors.PRIMARY, AppColors.PRIMARY_DARK,
                    e -> new EmployeeManagementForm(user, role, uid));
            navCard(p, "🏢", "إدارة الأقسام", AppColors.PURPLE, AppColors.PURPLE_DARK,
                    e -> new DepartmentForm(user, role));
        }
        navCard(p, "📋", role.equals("Employee") ? "حضوري وغيابي" : "سجلات الحضور",
                AppColors.SUCCESS, AppColors.SUCCESS_DARK,
                e -> new AttendanceForm(user, role, uid, user));
        navCard(p, "🏖", "طلبات الإجازة", AppColors.WARNING, AppColors.WARNING_DARK,
                e -> new LeaveRequestForm(user, role, uid));
        navCard(p, "💰", "الرواتب", AppColors.TEAL, AppColors.TEAL_DARK,
                e -> new SalaryPanel(user, role, uid));
        if (role.equals("Admin")) {
            navCard(p, "👤", "إدارة المستخدمين", AppColors.DARK, AppColors.DARK_HOVER,
                    e -> new UserManagementForm(user));
            navCard(p, "📊", "التقارير", AppColors.DANGER, AppColors.DANGER_DARK,
                    e -> new ReportsPanel(user));
        }
        navCard(p, "🚪", "تسجيل الخروج", new Color(90, 96, 110), new Color(50, 54, 66),
                e -> logout());
    }

    private void navCard(JPanel panel, String icon, String label,
                         Color bg, Color hover, ActionListener action) {
        RoundedPanel card = new RoundedPanel(bg, hover);
        card.setLayout(new GridBagLayout());
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(250, 115));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        iconLbl.setForeground(AppColors.TEXT_WHITE);
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLbl = new JLabel(label, SwingConstants.CENTER);
        textLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        textLbl.setForeground(AppColors.TEXT_WHITE);
        textLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(iconLbl);
        inner.add(Box.createVerticalStrut(10));
        inner.add(textLbl);
        card.add(inner);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { action.actionPerformed(null); }
            public void mouseEntered(java.awt.event.MouseEvent e) { card.setHovered(true); }
            public void mouseExited (java.awt.event.MouseEvent e) { card.setHovered(false); }
        });

        panel.add(card);
    }

    // ================================================================
    //  عنوان قسم
    // ================================================================
    private JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(AppColors.PRIMARY_DARK);
        lbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        return lbl;
    }

    // ================================================================
    //  مكوّن اللوحة ذات الزوايا الدائرية (يُستخدم للإحصائيات والتنقّل)
    // ================================================================
    private static class RoundedPanel extends JPanel {
        private final Color baseColor;
        private final Color hoverColor;
        private boolean hovered = false;

        RoundedPanel(Color base, Color hover) {
            this.baseColor  = base;
            this.hoverColor = hover;
            setOpaque(false);
        }

        void setHovered(boolean h) { this.hovered = h; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = 22, w = getWidth(), h = getHeight();
            // ظل خفيف
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(4, 5, w - 5, h - 5, arc, arc);
            // جسم اللوحة
            g2.setColor(hovered ? hoverColor : baseColor);
            g2.fillRoundRect(0, 0, w - 5, h - 7, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
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