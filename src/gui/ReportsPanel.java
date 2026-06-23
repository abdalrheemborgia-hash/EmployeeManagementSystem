package gui;

import model.*;
import exception.EmployeeException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dataaccess.*;
import java.awt.*;
import java.sql.*;
import java.text.MessageFormat; // تم إضافة الاستيراد المفقود هنا لحل مشكلة الانهيار
import java.util.List;

/**
 * واجهة التقارير المطورة — تُولّد تقارير شاملة داخل جداول رسومية مع ميزة الطباعة المتناسقة.
 *
 * @author فريق المشروع
 */
public class ReportsPanel extends JFrame {

    private JTable         reportTable;
    private DefaultTableModel tableModel;
    private JLabel         reportTitleLbl;
    private JLabel         summaryLbl; 
    private final String   currentUser;
    private String         currentReportType = ""; 

    public ReportsPanel(String user) {
        this.currentUser = user;
        buildUI();
    }

    private void buildUI() {
        setTitle("نظام التقارير المتقدم");
        setSize(950, 650); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppColors.BG_MAIN);

        JPanel header = AppColors.headerBar("📊  إدارة التقارير المتقدمة", "مدير النظام: " + currentUser);
        header.setBackground(AppColors.DANGER);

        // ---- شريط أزرار التقارير العلوي ----
        JPanel btnBar = new JPanel(new GridLayout(1, 5, 10, 0));
        btnBar.setBackground(new Color(235, 245, 255));
        btnBar.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JButton empBtn  = AppColors.btnPrimary("👥  تقرير الموظفين");
        JButton attBtn  = AppColors.btnSuccess("📋  تقرير الحضور");
        JButton levBtn  = AppColors.btnWarning("🏖  تقرير الإجازات");
        JButton dptBtn  = AppColors.btnPurple ("🏢  تقرير الأقسام");
        JButton clrBtn  = AppColors.btnDark   ("↺  مسح");

        empBtn.addActionListener(e -> empReport());
        attBtn.addActionListener(e -> attReport());
        levBtn.addActionListener(e -> leaveReport());
        dptBtn.addActionListener(e -> deptReport());
        clrBtn.addActionListener(e -> clearReport());

        btnBar.add(empBtn); btnBar.add(attBtn); btnBar.add(levBtn);
        btnBar.add(dptBtn); btnBar.add(clrBtn);

        // ---- عنوان التقرير الحالي ----
        reportTitleLbl = new JLabel("اختر تقريراً من الأزرار أعلاه لعرض البيانات", SwingConstants.CENTER);
        reportTitleLbl.setFont(new Font("Arial", Font.BOLD, 15));
        reportTitleLbl.setForeground(AppColors.TEXT_DARK);
        reportTitleLbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // ---- إعداد الجدول الرسومي (JTable) ----
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } 
        };
        reportTable = new JTable(tableModel);
        reportTable.setRowHeight(28);
        reportTable.setFont(new Font("Arial", Font.PLAIN, 13));
        reportTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        reportTable.getTableHeader().setBackground(new Color(230, 235, 240));
        reportTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT); 

        JScrollPane scroll = new JScrollPane(reportTable);
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.DANGER, 2)); // متناسق مع لون الهيدر الأحمر

        // ---- شريط الملخص والطباعة السفلي وتنسيق الزر ----
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(AppColors.BG_MAIN);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        summaryLbl = new JLabel("جاهز لتوليد التقارير...");
        summaryLbl.setFont(new Font("Arial", Font.BOLD, 13));
        summaryLbl.setForeground(AppColors.TEXT_MUTED);
        summaryLbl.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // تعديل الزر ليتوافق تماماً مع الهوية البصرية الحالية لمشروعك وثيم الألوان (استخدام نفس ستايل btnPrimary/btnDark المدمج لديك)
        JButton printBtn = AppColors.btnPrimary("📄 طباعة التقرير الحالي");
        
        printBtn.addActionListener(e -> printCurrentReport());

        bottomPanel.add(summaryLbl, BorderLayout.LINE_START);
        bottomPanel.add(printBtn, BorderLayout.LINE_END);

        // ---- تجميع المكونات ----
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(AppColors.BG_MAIN);
        mainContent.add(btnBar, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(AppColors.BG_MAIN);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        centerPanel.add(reportTitleLbl, BorderLayout.NORTH);
        centerPanel.add(scroll, BorderLayout.CENTER);
        
        mainContent.add(centerPanel, BorderLayout.CENTER);
        mainContent.add(bottomPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);

        setVisible(true);
    }

    // ================================================================
    //  توليد التقارير داخل الـ JTable
    // ================================================================

    private void empReport() {
        try {
            currentReportType = "تقرير الموظفين الشامل";
            List<Employee> list = new EmployeeDAO().getAllEmployees();
            
            String[] columns = {"المعرف", "الاسم", "البريد الإلكتروني", "الهاتف", "المنصب", "القسم", "الراتب", "الحالة"};
            tableModel.setColumnIdentifiers(columns);
            tableModel.setRowCount(0); 

            double totalSalary = 0;
            for (Employee e : list) {
                tableModel.addRow(new Object[]{
                    e.getId(), e.getName(), e.getEmail(), e.getPhone(),
                    e.getPosition(), e.getDepartmentName(), e.getSalary() + " د.ل", e.getStatus()
                });
                totalSalary += e.getSalary();
            }

            reportTitleLbl.setText("📊 تقرير الموظفين الكامل — صادر بواسطة: " + currentUser);
            summaryLbl.setText("عدد الموظفين: " + list.size() + "  |  إجمالي الرواتب الشهرية: " + String.format("%.2f", totalSalary) + " د.ل");
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private void attReport() {
        try {
            currentReportType = "تقرير الحضور والغياب العام";
            List<Attendance> list = new AttendanceDAO().getAllAttendance();
            
            String[] columns = {"رقم السجل", "اسم الموظف", "التاريخ", "الحالة", "وقت الدخول", "وقت الخروج"};
            tableModel.setColumnIdentifiers(columns);
            tableModel.setRowCount(0);

            int p = 0, a = 0, l = 0;
            for (Attendance at : list) {
                tableModel.addRow(new Object[]{
                    at.getId(), at.getEmployeeName(), at.getDate(), at.getStatus(), at.getCheckIn(), at.getCheckOut()
                });
                if ("حاضر".equals(at.getStatus())) p++;
                else if ("غائب".equals(at.getStatus())) a++;
                else l++;
            }

            reportTitleLbl.setText("📊 تقرير سجلات الحضور والغياب — صادر بواسطة: " + currentUser);
            summaryLbl.setText("إجمالي السجلات: " + list.size() + " [ حاضر: " + p + " | غائب: " + a + " | متأخر: " + l + " ]");
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private void leaveReport() {
        try {
            currentReportType = "تقرير طلبات الإجازات";
            List<LeaveRequest> list = new LeaveRequestDAO().getAllLeaveRequests();
            
            String[] columns = {"رقم الطلب", "الموظف", "نوع الإجازة", "تاريخ البدء", "تاريخ الانتهاء", "السبب", "الحالة"};
            tableModel.setColumnIdentifiers(columns);
            tableModel.setRowCount(0);

            int pend = 0, app = 0, rej = 0;
            for (LeaveRequest r : list) {
                tableModel.addRow(new Object[]{
                    r.getId(), r.getEmployeeName(), r.getLeaveType(), r.getStartDate(), r.getEndDate(), r.getReason(), r.getStatus()
                });
                if ("معلّق".equals(r.getStatus())) pend++;
                else if ("مقبول".equals(r.getStatus())) app++;
                else rej++;
            }

            reportTitleLbl.setText("📊 تقرير متابعة طلبات الإجازات — صادر بواسطة: " + currentUser);
            summaryLbl.setText("إجمالي الطلبات: " + list.size() + " [ مقبول: " + app + " | مرفوض: " + rej + " | معلق: " + pend + " ]");
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private void deptReport() {
        try {
            currentReportType = "تقرير الهيكل التنظيمي للأقسام";
            Connection conn = DatabaseConnection.getConnection();
            Statement s = conn.createStatement();
            ResultSet r = s.executeQuery("SELECT * FROM departments ORDER BY name");
            
            String[] columns = {"اسم القسم", "اسم المشرف / المدير", "الوصف والتفاصيل"};
            tableModel.setColumnIdentifiers(columns);
            tableModel.setRowCount(0);

            int count = 0;
            while (r.next()) {
                count++;
                tableModel.addRow(new Object[]{
                    r.getString("name"), r.getString("manager_name"), r.getString("description")
                });
            }
            r.close(); s.close();

            reportTitleLbl.setText("📊 تقرير الأقسام الإدارية — صادر بواسطة: " + currentUser);
            summaryLbl.setText("إجمالي الأقسام المسجلة في النظام: " + count);
        } catch (SQLException ex) { err("خطأ أثناء جلب الأقسام: " + ex.getMessage()); }
    }

    private void clearReport() {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{});
        currentReportType = "";
        reportTitleLbl.setText("اختر تقريراً من الأزرار أعلاه لعرض البيانات");
        summaryLbl.setText("جاهز لتوليد التقارير...");
    }

    // ================================================================
    //  ميزة الطباعة الرسمية
    // ================================================================
    private void printCurrentReport() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "تنبيه: لا توجد بيانات في الجدول لطباعتها! يرجى اختيار تقرير أولاً.", "جدول فارغ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String subTitle = "";
            if (reportTitleLbl.getText().contains("—")) {
                subTitle = " (" + reportTitleLbl.getText().split("—")[1].trim() + ")";
            }
            
            MessageFormat headerFormat = new MessageFormat(currentReportType + subTitle);
            MessageFormat footerFormat = new MessageFormat("صفحة {0}             نظام إدارة الموظفين المطور 2026");

            boolean complete = reportTable.print(JTable.PrintMode.FIT_WIDTH, headerFormat, footerFormat);
            
            if (complete) {
                JOptionPane.showMessageDialog(this, "✔ تم إرسال التقرير إلى الطابعة / أو حفظه كـ PDF بنجاح.", "نجاح العملية", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "حدث خطأ أثناء محاولة الطباعة: " + e.getMessage(), "خطأ في الطباعة", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void err(String m) { 
        JOptionPane.showMessageDialog(this, m, "خطأ في النظام", JOptionPane.ERROR_MESSAGE); 
    }
}