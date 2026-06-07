package exception;

/**
 * استثناء مخصص لأخطاء نظام إدارة الموظفين.
 * <p>
 * مفهوم البرمجة الكائنية: معالجة الاستثناءات - فئة استثناء مخصصة.
 * </p>
 *
 * @author فريق المشروع
 */
public class EmployeeException extends Exception {

    /**
     * يُنشئ استثناءً برسالة خطأ.
     * @param message رسالة الخطأ
     */
    public EmployeeException(String message) {
        super(message);
    }

    /**
     * يُنشئ استثناءً برسالة وسبب.
     * @param message رسالة الخطأ
     * @param cause   السبب الجذري
     */
    public EmployeeException(String message, Throwable cause) {
        super(message, cause);
    }
}
