package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dataaccess.DatabaseConnection;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * شاشة تسجيل الدخول الرئيسية — واجهة كاملة واحترافية.
 *
 * @author فريق المشروع
 */
public class LoginForm extends JFrame {

    private JTextField     emailField;
    private JPasswordField passField;
    private JLabel         statusLabel;
    private JButton        loginBtn;

    public LoginForm() {
        buildUI();
    }

    private void buildUI() {
        setTitle("نظام إدارة الموظفين — تسجيل الدخول");
        setSize(980, 640);
        setMinimumSize(new Dimension(900, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // الحاوية الرئيسية: عمودان — يسار (تزييني) | يمين (نموذج)
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildLeftPanel());
        root.add(buildRightPanel());

        setContentPane(root);
        setVisible(true);
    }

    // ================================================================
    //  اللوحة اليسارية — خلفية زرقاء داكنة + معلومات النظام
    // ================================================================
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // تدرج من الأزرق الداكن إلى الأزرق المتوسط
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(10, 50, 130),
                    getWidth(), getHeight(), new Color(0, 90, 180)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill  = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10, 30, 10, 30);

        // أيقونة كبيرة
        g.gridy = 0; g.insets = new Insets(40, 30, 10, 30);
        JLabel iconLbl = new JLabel("", SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        panel.add(iconLbl, g);

        // اسم النظام
        g.gridy = 1; g.insets = new Insets(8, 30, 6, 30);
        JLabel systemName = new JLabel("نظام إدارة الموظفين", SwingConstants.CENTER);
        systemName.setFont(new Font("Arial", Font.BOLD, 26));
        systemName.setForeground(Color.WHITE);
        panel.add(systemName, g);

        // وصف النظام
        g.gridy = 2;
        JLabel desc = new JLabel(
            "<html><div style='text-align:center; line-height:1.7'>" +
            "نظام متكامل لإدارة الموظفين<br>" +
            "والحضور والرواتب والإجازات<br>" +
            "والأقسام والتقارير" +
            "</div></html>", SwingConstants.CENTER);
        desc.setFont(new Font("Arial", Font.PLAIN, 15));
        desc.setForeground(new Color(180, 215, 255));
        panel.add(desc, g);

        // فاصل أبيض
        g.gridy = 3; g.insets = new Insets(24, 30, 24, 30);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(100, 150, 220));
        sep.setBackground(new Color(100, 150, 220));
        panel.add(sep, g);

        // ميزات النظام
        g.gridy = 4; g.insets = new Insets(0, 30, 8, 30);
        String[] features = {
            "✅  إدارة بيانات الموظفين",
            "✅  تسجيل الحضور والغياب",
            "✅  إدارة طلبات الإجازة",
            "✅  احتساب الرواتب تلقائياً",
            "✅  تقارير شاملة ومفصّلة",
            "✅  إدارة الأقسام والمستخدمين"
        };
        JPanel featuresPanel = new JPanel(new GridLayout(features.length, 1, 0, 8));
        featuresPanel.setOpaque(false);
        for (String f : features) {
            JLabel fl = new JLabel(f, SwingConstants.RIGHT);
            fl.setFont(new Font("Arial", Font.PLAIN, 14));
            fl.setForeground(new Color(210, 235, 255));
            featuresPanel.add(fl);
        }
        panel.add(featuresPanel, g);

        // نص التذييل
        g.gridy = 5; g.insets = new Insets(30, 30, 30, 30);
     

        return panel;
    }

    // ================================================================
    //  اللوحة اليمينية — نموذج تسجيل الدخول
    // ================================================================
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(248, 250, 255));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill  = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        // ---- عنوان النموذج ----
        g.gridy = 0; g.insets = new Insets(50, 50, 6, 50);
        JLabel welcomeTitle = new JLabel("مرحباً بك", SwingConstants.RIGHT);
        welcomeTitle.setFont(new Font("Arial", Font.BOLD, 30));
        welcomeTitle.setForeground(AppColors.PRIMARY);
        panel.add(welcomeTitle, g);

        g.gridy = 1; g.insets = new Insets(0, 50, 35, 50);
        JLabel welcomeSub = new JLabel("سجّل دخولك للوصول إلى النظام", SwingConstants.RIGHT);
        welcomeSub.setFont(new Font("Arial", Font.PLAIN, 15));
        welcomeSub.setForeground(AppColors.TEXT_MUTED);
        panel.add(welcomeSub, g);

        // ---- حقل اسم المستخدم ----
        g.gridy = 2; g.insets = new Insets(0, 50, 6, 50);
        JLabel emailLbl = new JLabel(" اسم المستخدم ", SwingConstants.RIGHT);
        emailLbl.setFont(new Font("Arial", Font.BOLD, 14));
        emailLbl.setForeground(AppColors.TEXT_DARK);
        panel.add(emailLbl, g);

        g.gridy = 3; g.insets = new Insets(0, 50, 18, 50);
        emailField = new JTextField("admin");
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(0, 46));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        emailField.setBackground(Color.WHITE);
        emailField.setForeground(AppColors.TEXT_DARK);
        // تأثير التركيز
        emailField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                emailField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.PRIMARY, 2, true),
                    BorderFactory.createEmptyBorder(7, 13, 7, 13)));
            }
            public void focusLost(FocusEvent e) {
                emailField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(8, 14, 8, 14)));
            }
        });
        panel.add(emailField, g);

        // ---- حقل كلمة المرور ----
        g.gridy = 4; g.insets = new Insets(0, 50, 6, 50);
        JLabel passLbl = new JLabel("كلمة المرور", SwingConstants.RIGHT);
        passLbl.setFont(new Font("Arial", Font.BOLD, 14));
        passLbl.setForeground(AppColors.TEXT_DARK);
        panel.add(passLbl, g);

        g.gridy = 5; g.insets = new Insets(0, 50, 8, 50);
        passField = new JPasswordField("admin123");
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setPreferredSize(new Dimension(0, 46));
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        passField.setBackground(Color.WHITE);
        passField.setForeground(AppColors.TEXT_DARK);
        passField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                passField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.PRIMARY, 2, true),
                    BorderFactory.createEmptyBorder(7, 13, 7, 13)));
            }
            public void focusLost(FocusEvent e) {
                passField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(8, 14, 8, 14)));
            }
        });
        panel.add(passField, g);

        // ---- رسالة الخطأ ----
        g.gridy = 6; g.insets = new Insets(0, 50, 12, 50);
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statusLabel.setForeground(AppColors.DANGER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(255, 240, 240));
        statusLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(statusLabel, g);

        // ---- زر تسجيل الدخول ----
        g.gridy = 7; g.insets = new Insets(0, 50, 18, 50);
        loginBtn = new JButton("تسجيل الدخول");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setBackground(AppColors.PRIMARY);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setPreferredSize(new Dimension(0, 50));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { loginBtn.setBackground(AppColors.PRIMARY_DARK); }
            public void mouseExited (MouseEvent e) { loginBtn.setBackground(AppColors.PRIMARY); }
        });
        loginBtn.addActionListener(e -> doLogin());
        passField.addActionListener(e -> doLogin());
        panel.add(loginBtn, g);
		return panel;}


    // ================================================================
    //  منطق تسجيل الدخول
    // ================================================================
    private void doLogin() {
        String email = emailField.getText().trim();
        String pass  = new String(passField.getPassword());

        // إظهار حالة التحميل
        loginBtn.setText("جارٍ التحقق...");
        loginBtn.setEnabled(false);

        if (email.isEmpty() || pass.isEmpty()) {
            showError("⚠  يرجى ملء جميع الحقول");
            return;
        }

        try {
            PreparedStatement ps = DatabaseConnection.getConnection()
                    .prepareStatement("SELECT * FROM users WHERE email=? AND password=?");
            ps.setString(1, email);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                String name = rs.getString("name");
                int    uid  = rs.getInt("id");
                rs.close(); ps.close();
                new MainDashboard(name, role, uid);
                dispose();
            } else {
                showError("✗  اسم المستخدم أو كلمة مرور غير صحيحة");
                passField.setText("");
                rs.close(); ps.close();
            }

        } catch (SQLException ex) {
            showError("خطأ في الاتصال بقاعدة البيانات");
            JOptionPane.showMessageDialog(this,
                "تعذّر الاتصال بقاعدة البيانات.\n" +
                "تأكد من وجود ملف sqlite-jdbc.jar في مجلد lib.\n\n" +
                "تفاصيل الخطأ: " + ex.getMessage(),
                "خطأ في قاعدة البيانات", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        loginBtn.setText("تسجيل الدخول");
        loginBtn.setEnabled(true);
    }
}
