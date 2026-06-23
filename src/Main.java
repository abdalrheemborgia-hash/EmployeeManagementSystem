import gui.LoginForm;

import javax.swing.*;

import dataaccess.DatabaseConnection;

/**
 * نقطة الدخول الرئيسية لنظام إدارة الموظفين.
 * <p>
 * تُهيّئ قاعدة البيانات وتُطلق الواجهة الرسومية.
 * </p>
 *
 * @author فريق المشروع
 * @version 1.0
 */
public class Main {

    /**
     * نقطة بداية التطبيق.
     *
     * @param args معطيات سطر الأوامر (غير مستخدمة)
     */
    public static void main(String[] args) {

        // استخدام مظهر النظام للحصول على شكل أفضل
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("سيُستخدم المظهر الافتراضي.");
        }

        // تهيئة قاعدة البيانات (إنشاء الجداول وإدراج البيانات التجريبية)
        DatabaseConnection.initializeDatabase();

        // تشغيل نافذة تسجيل الدخول على خيط EDT
        SwingUtilities.invokeLater(LoginForm::new);
    }
}
