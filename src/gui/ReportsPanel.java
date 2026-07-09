package gui;

import model.*;
import exception.EmployeeException;

import service.EmployeeService;
import service.AttendanceService;
import service.LeaveRequestService;
import service.DepartmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.MessageFormat;
import java.util.List;

/**
 * واجهة التقارير المطورة — تُولّد تقارير شاملة متوافقة تماماً مع معمارية الطبقات الأربع.
 *
 * @author فريق المشروع
 */
public class ReportsPanel extends JFrame {

    private JTable            reportTable;
    private DefaultTableModel tableModel;
    private JLabel            reportTitleLbl;
    private JLabel            summaryLbl;
    private final String      currentUser;
    private String            currentReportType = "";

    // ✅ جميع الخدمات — لا DAO ولا DatabaseConnection في الواجهة
    private final EmployeeService     employeeService     = new EmployeeService();
    private final AttendanceService   attendanceService   = new AttendanceService();
    private final LeaveRequestService leaveRequestService = new LeaveRequestService();
    private final DepartmentService   departmentService   = new DepartmentService();

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

        // ---- شريط أزرار التقارير ----
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

        // ---- الجدول ----
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
        scroll.setBorder(BorderFactory.createLineBorder(AppColors.DANGER, 2));

        // ---- شريط الملخص والطباعة ----
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(AppColors.BG_MAIN);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        summaryLbl = new JLabel("جاهز لتوليد التقارير...");
        summaryLbl.setFont(new Font("Arial", Font.BOLD, 13));
        summaryLbl.setForeground(AppColors.TEXT_MUTED);
        summaryLbl.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JButton printBtn = AppColors.btnPrimary("📄 طباعة التقرير الحالي");
        printBtn.addActionListener(e -> printCurrentReport());

        bottomPanel.add(summaryLbl, BorderLayout.LINE_START);
        bottomPanel.add(printBtn,   BorderLayout.LINE_END);

        // ---- تجميع ----
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(AppColors.BG_MAIN);
        mainContent.add(btnBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(AppColors.BG_MAIN);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        centerPanel.add(reportTitleLbl, BorderLayout.NORTH);
        centerPanel.add(scroll,         BorderLayout.CENTER);

        mainContent.add(centerPanel, BorderLayout.CENTER);
        mainContent.add(bottomPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(header,      BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);

        setVisible(true);
    }

    // ================================================================
    //  توليد التقارير — كلها عبر طبقة الخدمة (لا DAO ولا SQL في الواجهة)
    // ================================================================

    private void empReport() {
        try {
            currentReportType = "تقرير الموظفين الشامل";
            List<Employee> list = employeeService.getAllEmployees();

            String[] columns = {"المعرف", "الاسم", "اسم المستخدم", "الهاتف", "المنصب", "القسم", "الراتب", "الحالة"};
            tableModel.setColumnIdentifiers(columns);
            tableModel.setRowCount(0);

            double totalSalary = 0;
            for (Employee e : list) {
                tableModel.addRow(new Object[]{
                    e.getId(), e.getName(), e.getEmail(), e.getPhone(),
                    e.getPosition(), e.getDepartmentName(),
                    String.format("%.2f د.ل", e.getSalary()), e.getStatus()
                });
                totalSalary += e.getSalary();
            }

            reportTitleLbl.setText("📊 تقرير الموظفين الكامل — صادر بواسطة: " + currentUser);
            summaryLbl.setText("عدد الموظفين: " + list.size()
                    + "  |  إجمالي الرواتب الشهرية: " + String.format("%,.2f", totalSalary) + " د.ل");
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private void attReport() {
        try {
            currentReportType = "تقرير الحضور والغياب العام";
            // ✅ عبر الخدمة بدل new AttendanceDAO()
            List<Attendance> list = attendanceService.getAllAttendance();

            String[] columns = {"رقم السجل", "اسم الموظف", "التاريخ", "الحالة", "وقت الدخول", "وقت الخروج"};
            tableModel.setColumnIdentifiers(columns);
            tableModel.setRowCount(0);

            int p = 0, a = 0, l = 0;
            for (Attendance at : list) {
                tableModel.addRow(new Object[]{
                    at.getId(), at.getEmployeeName(), at.getDate(),
                    at.getStatus(), at.getCheckIn(), at.getCheckOut()
                });
                if ("حاضر".equals(at.getStatus())) p++;
                else if ("غائب".equals(at.getStatus())) a++;
                else l++;
            }

            reportTitleLbl.setText("📊 تقرير سجلات الحضور والغياب — صادر بواسطة: " + currentUser);
            summaryLbl.setText("إجمالي السجلات: " + list.size()
                    + " [ حاضر: " + p + " | غائب: " + a + " | متأخر: " + l + " ]");
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private void leaveReport() {
        try {
            currentReportType = "تقرير طلبات الإجازات";
            // ✅ عبر الخدمة بدل new LeaveRequestDAO()
            List<LeaveRequest> list = leaveRequestService.getAllLeaveRequests();

            String[] columns = {"رقم الطلب", "الموظف", "نوع الإجازة", "تاريخ البدء", "تاريخ الانتهاء", "السبب", "الحالة"};
            tableModel.setColumnIdentifiers(columns);
            tableModel.setRowCount(0);

            int pend = 0, app = 0, rej = 0;
            for (LeaveRequest r : list) {
                tableModel.addRow(new Object[]{
                    r.getId(), r.getEmployeeName(), r.getLeaveType(),
                    r.getStartDate(), r.getEndDate(), r.getReason(), r.getStatus()
                });
                if ("معلّق".equals(r.getStatus())) pend++;
                else if ("مقبول".equals(r.getStatus())) app++;
                else rej++;
            }

            reportTitleLbl.setText("📊 تقرير متابعة طلبات الإجازات — صادر بواسطة: " + currentUser);
            summaryLbl.setText("إجمالي الطلبات: " + list.size()
                    + " [ مقبول: " + app + " | مرفوض: " + rej + " | معلق: " + pend + " ]");
        } catch (EmployeeException ex) { err(ex.getMessage()); }
    }

    private void deptReport() {
        try {
            currentReportType = "تقرير الهيكل التنظيمي للأقسام";

            String[] columns = {"اسم القسم", "اسم المشرف / المدير", "الوصف والتفاصيل"};
            tableModel.setColumnIdentifiers(columns);
            tableModel.setRowCount(0);

            // ✅ عبر الخدمة بدل SQL الخام
            List<Department> list = departmentService.getAllDepartments();
            for (Department d : list) {
                tableModel.addRow(new Object[]{
                    d.getName(), d.getManagerName(), d.getDescription()
                });
            }

            reportTitleLbl.setText("📊 تقرير الأقسام الإدارية — صادر بواسطة: " + currentUser);
            summaryLbl.setText("إجمالي الأقسام المسجلة في النظام: " + list.size());
        } catch (EmployeeException ex) { err("خطأ أثناء جلب الأقسام: " + ex.getMessage()); }
    }

    private void clearReport() {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{});
        currentReportType = "";
        reportTitleLbl.setText("اختر تقريراً من الأزرار أعلاه لعرض البيانات");
        summaryLbl.setText("جاهز لتوليد التقارير...");
    }

    private void printCurrentReport() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "تنبيه: لا توجد بيانات في الجدول لطباعتها! يرجى اختيار تقرير أولاً.",
                "جدول فارغ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
            job.setJobName(currentReportType);

            // ✅ ورقة A4 بأبعاد النقاط: 595 × 842 (عمودي)
            // نجعلها عريضة فيزيائياً (842 × 595) ونُبقي الاتجاه PORTRAIT لتفادي الدوران
            java.awt.print.PageFormat pf = new java.awt.print.PageFormat();
            java.awt.print.Paper paper = new java.awt.print.Paper();

            double a4Long  = 842; // الطول الأكبر لـ A4 بالنقاط
            double a4Short = 595; // الطول الأصغر لـ A4 بالنقاط
            double margin  = 36;  // هامش نصف إنش

            paper.setSize(a4Long, a4Short); // ورقة عريضة (Landscape شكلاً)
            paper.setImageableArea(margin, margin,
                    a4Long - 2 * margin, a4Short - 2 * margin);

            pf.setPaper(paper);
            pf.setOrientation(java.awt.print.PageFormat.PORTRAIT); // ⚠️ PORTRAIT لمنع الدوران المزدوج

            job.setPrintable(new ReportPrintable(), pf);

            if (job.printDialog()) {
                job.print();
                JOptionPane.showMessageDialog(this,
                    "✔ تم إرسال التقرير إلى الطابعة / أو حفظه كـ PDF بنجاح.",
                    "نجاح العملية", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "حدث خطأ أثناء محاولة الطباعة: " + e.getMessage(),
                "خطأ في الطباعة", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void err(String m) {
        JOptionPane.showMessageDialog(this, m, "خطأ في النظام", JOptionPane.ERROR_MESSAGE);
    }
    // ================================================================
    //  مكوّن طباعة مخصص — تقرير احترافي منسّق (بلا مكتبات خارجية)
    // ================================================================
    private class ReportPrintable implements java.awt.print.Printable {

        private final Color HEADER_BG = new Color(183, 28, 28);   // أحمر الترويسة
        private final Color TABLE_HEAD = new Color(60, 70, 90);    // رأس الجدول
        private final Color ROW_ALT    = new Color(245, 247, 250); // صف متناوب
        private final Color BORDER     = new Color(200, 205, 212);
        private final Color PAGE_FRAME = new Color(120, 130, 145); // إطار الصفحة

        private final int PAD = 18; // هامش داخلي على الجانبين والأعلى/الأسفل

        @Override
        public int print(Graphics g, java.awt.print.PageFormat pf, int pageIndex) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.translate(pf.getImageableX(), pf.getImageableY());

            double pageW = pf.getImageableWidth();
            double pageH = pf.getImageableHeight();

            Font titleFont = new Font("Arial", Font.BOLD,  16);
            Font metaFont  = new Font("Arial", Font.PLAIN, 10);
            Font headFont  = new Font("Arial", Font.BOLD,  10);
            Font cellFont  = new Font("Arial", Font.PLAIN, 10);
            Font footFont  = new Font("Arial", Font.PLAIN, 9);

            int cols = tableModel.getColumnCount();
            int rows = tableModel.getRowCount();
            int rowH = 22;
            int tableHeadH = 26;

            // ✅ منطقة المحتوى الفعلية بعد ترك هامش داخلي (PAD) على كل الجوانب
            int contentX = PAD;
            int contentW = (int) pageW - 2 * PAD;
            int contentTop = PAD;
            int contentBottom = (int) pageH - PAD;

            int headerBandH = 70;   // ارتفاع ترويسة الصفحة (داخل المحتوى)
            int footerH     = 24;

            int usableH = (contentBottom - contentTop) - headerBandH - tableHeadH - footerH;
            int rowsPerPage = Math.max(1, usableH / rowH);
            int totalPages  = (int) Math.ceil((double) rows / rowsPerPage);
            if (pageIndex >= totalPages) return NO_SUCH_PAGE;

            // ===== 0) إطار الصفحة الكامل =====
            g2.setColor(PAGE_FRAME);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(contentX, contentTop, contentW, contentBottom - contentTop);
            g2.setStroke(new BasicStroke(1f));

            // ===== 1) ترويسة الصفحة الملوّنة (داخل الهامش) =====
            g2.setColor(HEADER_BG);
            g2.fillRect(contentX + 1, contentTop + 1, contentW - 1, 46);
            g2.setColor(Color.WHITE);
            g2.setFont(titleFont);
            drawCentered(g2, currentReportType, contentX, contentTop + 31, contentW);

            // معلومات التقرير أسفل الترويسة (بهامش داخلي إضافي)
            int metaY = contentTop + 62;
            int metaPad = contentX + 10;
            g2.setColor(new Color(70, 70, 70));
            g2.setFont(metaFont);
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd  HH:mm")
                    .format(new java.util.Date());
            drawRight(g2, "صادر بواسطة: " + currentUser, contentX + contentW - 10, metaY);
            g2.drawString("التاريخ: " + date + "     |     عدد السجلات: " + rows, metaPad, metaY);

            // ===== 2) عرض الأعمدة =====
            int[] w = new int[cols];
            for (int c = 0; c < cols; c++) w[c] = contentW / cols;

            int startRow = pageIndex * rowsPerPage;
            int endRow   = Math.min(startRow + rowsPerPage, rows);
            int y = contentTop + headerBandH;
            int tableRight = contentX + contentW; // الحافة اليمنى للجدول

            // ===== 3) رأس الجدول (RTL) =====
            g2.setColor(TABLE_HEAD);
            g2.fillRect(contentX, y, contentW, tableHeadH);
            g2.setFont(headFont);
            g2.setColor(Color.WHITE);
            int x = tableRight;
            for (int c = 0; c < cols; c++) {
                x -= w[c];
                String h = String.valueOf(tableModel.getColumnName(c));
                drawCentered(g2, h, x, y + 17, w[c]);
            }
            y += tableHeadH;

            // ===== 4) صفوف البيانات =====
            g2.setFont(cellFont);
            int tableTop = contentTop + headerBandH + tableHeadH;
            for (int r = startRow; r < endRow; r++) {
                if ((r - startRow) % 2 == 1) {
                    g2.setColor(ROW_ALT);
                    g2.fillRect(contentX, y, contentW, rowH);
                }
                g2.setColor(new Color(33, 37, 41));
                x = tableRight;
                for (int c = 0; c < cols; c++) {
                    x -= w[c];
                    Object val = tableModel.getValueAt(r, c);
                    String s = val == null ? "" : val.toString();
                    drawRight(g2, s, x + w[c] - 8, y + 15);
                }
                g2.setColor(BORDER);
                g2.drawLine(contentX, y, tableRight, y);
                y += rowH;
            }

            // إطار الجدول + خطوطه
            g2.setColor(BORDER);
            g2.drawLine(contentX, y, tableRight, y);
            g2.drawRect(contentX, contentTop + headerBandH, contentW, y - (contentTop + headerBandH));
            x = tableRight;
            for (int c = 0; c < cols - 1; c++) {
                x -= w[c];
                g2.drawLine(x, contentTop + headerBandH, x, y);
            }

            // ===== 5) التذييل (داخل الهامش) =====
            g2.setFont(footFont);
            g2.setColor(new Color(120, 120, 120));
            int footY = contentBottom - 8;
            g2.drawString("نظام إدارة الموظفين المطوّر 2026", metaPad, footY);
            drawRight(g2, "صفحة " + (pageIndex + 1) + " من " + totalPages,
                      contentX + contentW - 10, footY);

            return PAGE_EXISTS;
        }

        /** يرسم نصاً محاذى لليمين بحيث تنتهي نهايته عند rightX. */
        private void drawRight(Graphics2D g2, String s, int rightX, int y) {
            int sw = g2.getFontMetrics().stringWidth(s);
            g2.drawString(s, rightX - sw, y);
        }

        /** يرسم نصاً متمركزاً داخل عرض width يبدأ عند startX. */
        private void drawCentered(Graphics2D g2, String s, int startX, int y, int width) {
            int sw = g2.getFontMetrics().stringWidth(s);
            g2.drawString(s, startX + (width - sw) / 2, y);
        }
    }
}