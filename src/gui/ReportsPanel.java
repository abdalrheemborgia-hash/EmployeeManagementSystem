package gui;

import database.*;
import model.*;
import exception.EmployeeException;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;

/**
 * واجهة التقارير — تُولّد تقارير شاملة للنظام.
 *
 * @author فريق المشروع
 */
public class ReportsPanel extends JFrame {

    private JTextArea  reportArea;
    private JLabel     reportTitleLbl;
    private final String currentUser;

    public ReportsPanel(String user) {
        this.currentUser = user;
        buildUI();
    }

    private void buildUI() {
        setTitle("التقارير");
        setSize(800, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppColors.BG_MAIN);

        JPanel header = AppColors.headerBar("📊  التقارير", "مدير النظام: " + currentUser);
        header.setBackground(AppColors.DANGER);

        // ---- شريط أزرار التقارير ----
        JPanel btnBar = new JPanel(new GridLayout(1, 5, 10, 0));
        btnBar.setBackground(new Color(220, 235, 255));
        btnBar.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JButton empBtn  = AppColors.btnPrimary("👥  تقرير الموظفين");
        JButton attBtn  = AppColors.btnSuccess("📋  تقرير الحضور");
        JButton levBtn  = AppColors.btnWarning("🏖  تقرير الإجازات");
        JButton dptBtn  = AppColors.btnPurple ("🏢  تقرير الأقسام");
        JButton clrBtn  = AppColors.btnDark   ("↺  مسح");

        empBtn.addActionListener(e -> { setTitle("تقرير الموظفين");  empReport(); });
        attBtn.addActionListener(e -> { setTitle("تقرير الحضور");    attReport(); });
        levBtn.addActionListener(e -> { setTitle("تقرير الإجازات"); leaveReport(); });
        dptBtn.addActionListener(e -> { setTitle("تقرير الأقسام");  deptReport(); });
        clrBtn.addActionListener(e -> { reportArea.setText(""); reportTitleLbl.setText("اختر تقريراً من الأزرار أعلاه"); });

        btnBar.add(empBtn); btnBar.add(attBtn); btnBar.add(levBtn);
        btnBar.add(dptBtn); btnBar.add(clrBtn);

        // ---- عنوان التقرير ----
        reportTitleLbl = new JLabel("اختر تقريراً من الأزرار أعلاه", SwingConstants.CENTER);
        reportTitleLbl.setFont(new Font("Arial", Font.BOLD, 14));
        reportTitleLbl.setForeground(AppColors.TEXT_MUTED);
        reportTitleLbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        reportTitleLbl.setBackground(AppColors.BG_MAIN);
        reportTitleLbl.setOpaque(true);

        // ---- منطقة التقرير ----
        reportArea = new JTextArea();
        reportArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        reportArea.setEditable(false);
        reportArea.setBackground(new Color(252, 253, 255));
        reportArea.setForeground(AppColors.TEXT_DARK);
        reportArea.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        reportArea.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(reportArea);
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.DANGER, 2));

        // ---- تجميع ----
        JPanel center = new JPanel(new BorderLayout(0, 4));
        center.setBackground(AppColors.BG_MAIN);
        center.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        center.add(reportTitleLbl, BorderLayout.NORTH);
        center.add(scroll,        BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(btnBar, BorderLayout.CENTER);
        add(center, BorderLayout.SOUTH);

        // اجعل منطقة التقرير تأخذ المساحة الأكبر
        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(0, 0));
        mainContent.setBackground(AppColors.BG_MAIN);
        mainContent.add(btnBar, BorderLayout.NORTH);
        mainContent.add(center, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        setVisible(true);
    }

    // ================================================================
    //  التقارير
    // ================================================================

    /** تقرير الموظفين — يستخدم generateReport() من واجهة Reportable */
    private void empReport() {
        try {
            List<Employee> list = new EmployeeDAO().getAllEmployees();
            StringBuilder sb = new StringBuilder();
            line(sb, "═", 50);
            center(sb, "تقرير الموظفين الكامل", 50);
            line(sb, "═", 50);
            sb.append("صادر بواسطة: ").append(currentUser).append("\n");
            sb.append("عدد الموظفين: ").append(list.size()).append("\n");
            line(sb, "─", 50);

            double total = 0;
            for (Employee e : list) {
                // استدعاء generateReport() — تعدد الأشكال (Polymorphism)
                sb.append(e.generateReport()).append("\n");
                line(sb, "─", 50);
                total += e.getSalary();
            }
            sb.append("\n💰 إجمالي الرواتب الشهرية: ")
              .append(String.format("%.2f", total)).append(" د.ل\n");

            reportTitleLbl.setText("📊 تقرير الموظفين — " + list.size() + " موظف");
            reportArea.setText(sb.toString());
            reportArea.setCaretPosition(0);
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private void attReport() {
        try {
            List<Attendance> list = new AttendanceDAO().getAllAttendance();
            int p=0, a=0, l=0;
            StringBuilder sb = new StringBuilder();
            line(sb, "═", 55);
            center(sb, "تقرير الحضور والغياب", 55);
            line(sb, "═", 55);
            sb.append("صادر بواسطة: ").append(currentUser).append("\n");
            sb.append("إجمالي السجلات: ").append(list.size()).append("\n");
            line(sb, "─", 55);
            sb.append(String.format("%-5s %-18s %-12s %-10s %-8s %-8s\n",
                    "رقم","الموظف","التاريخ","الحالة","دخول","خروج"));
            line(sb, "─", 55);
            for (Attendance at : list) {
                sb.append(String.format("%-5d %-18s %-12s %-10s %-8s %-8s\n",
                    at.getId(), at.getEmployeeName(), at.getDate(), at.getStatus(),
                    at.getCheckIn(), at.getCheckOut()));
                if ("حاضر".equals(at.getStatus())) p++;
                else if ("غائب".equals(at.getStatus())) a++;
                else l++;
            }
            line(sb, "─", 55);
            sb.append("✅ حاضر:  ").append(p).append("  |  ");
            sb.append("❌ غائب:  ").append(a).append("  |  ");
            sb.append("⚠ متأخر: ").append(l).append("\n");

            reportTitleLbl.setText("📊 تقرير الحضور — " + list.size() + " سجل");
            reportArea.setText(sb.toString());
            reportArea.setCaretPosition(0);
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private void leaveReport() {
        try {
            List<LeaveRequest> list = new LeaveRequestDAO().getAllLeaveRequests();
            int pend=0, app=0, rej=0;
            StringBuilder sb = new StringBuilder();
            line(sb, "═", 60);
            center(sb, "تقرير طلبات الإجازة", 60);
            line(sb, "═", 60);
            sb.append("صادر بواسطة: ").append(currentUser).append("\n");
            sb.append("إجمالي الطلبات: ").append(list.size()).append("\n");
            line(sb, "─", 60);
            for (LeaveRequest r : list) {
                sb.append("# ").append(r.getId()).append("  ").append(r.getEmployeeName())
                  .append("  |  ").append(r.getLeaveType())
                  .append("  |  ").append(r.getStartDate()).append(" ~ ").append(r.getEndDate())
                  .append("  |  ").append(r.getStatus()).append("\n");
                sb.append("   السبب: ").append(r.getReason()).append("\n");
                line(sb, "·", 60);
                if ("معلّق".equals(r.getStatus())) pend++;
                else if ("مقبول".equals(r.getStatus())) app++;
                else rej++;
            }
            line(sb, "─", 60);
            sb.append("✅ مقبول: ").append(app).append("  |  ");
            sb.append("❌ مرفوض: ").append(rej).append("  |  ");
            sb.append("⏳ معلّق: ").append(pend).append("\n");

            reportTitleLbl.setText("📊 تقرير الإجازات — " + list.size() + " طلب");
            reportArea.setText(sb.toString());
            reportArea.setCaretPosition(0);
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private void deptReport() {
        try {
            Statement s = DatabaseConnection.getConnection().createStatement();
            ResultSet r = s.executeQuery("SELECT * FROM departments ORDER BY name");
            StringBuilder sb = new StringBuilder();
            line(sb, "═", 50);
            center(sb, "تقرير الأقسام", 50);
            line(sb, "═", 50);
            sb.append("صادر بواسطة: ").append(currentUser).append("\n");
            line(sb, "─", 50);
            int count = 0;
            while (r.next()) {
                count++;
                sb.append("🏢 ").append(r.getString("name")).append("\n");
                sb.append("   المشرف: ").append(r.getString("manager_name")).append("\n");
                sb.append("   الوصف:  ").append(r.getString("description")).append("\n");
                line(sb, "─", 50);
            }
            sb.append("إجمالي الأقسام: ").append(count).append("\n");
            r.close(); s.close();

            reportTitleLbl.setText("📊 تقرير الأقسام — " + count + " أقسام");
            reportArea.setText(sb.toString());
            reportArea.setCaretPosition(0);
        } catch (SQLException ex) { err("خطأ: " + ex.getMessage()); }
    }

    // ---- مساعدات التنسيق ----
    private void line(StringBuilder sb, String ch, int len) {
        sb.append(ch.repeat(len)).append("\n");
    }

    private void center(StringBuilder sb, String text, int width) {
        int pad = Math.max(0, (width - text.length()) / 2);
        sb.append(" ".repeat(pad)).append(text).append("\n");
    }

    private void err(String m) { JOptionPane.showMessageDialog(this, m, "خطأ", JOptionPane.ERROR_MESSAGE); }
}
