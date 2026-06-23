package dataaccess;

import java.sql.*;

/**
 * يُدير اتصال قاعدة البيانات SQLite باستخدام نمط التصميم Singleton.
 * يُنشئ جميع الجداول المطلوبة ويُدرج بيانات تجريبية عند أول تشغيل.
 *
 * @author فريق المشروع
 */
public class DatabaseConnection {

    private static final String DB_URL    = "jdbc:sqlite:employee_management.db";
    
    // 1. متغير ساكن (static) يحفظ النسخة الوحيدة المشتركة من الاتصال
    private static Connection   connection = null;

    // 2. جعل المشيّد خاصاً (private) لمنع إنشاء كائنات جديدة بـ new من خارج الكلاس
    private DatabaseConnection() {
    }

    /**
     * يُرجع اتصال قاعدة البيانات المشترك (تطبيق نمط الـ Singleton).
     * @return كائن Connection النشط والوحيد
     * @throws SQLException إذا فشل الاتصال
     */
    public static Connection getConnection() throws SQLException {
        // 3. التحقق مما إذا كان الاتصال غير موجود أو مغلق، لإنشائه مرة واحدة فقط طوال دورة حياة البرنامج
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("✔ [Singleton] تم إنشاء وتأمين الاتصال الموحد بقاعدة البيانات بنجاح.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("لم يُعثر على مشغّل SQLite JDBC.أضف sqlite-jdbc.jar إلى مجلد lib.", e);
            }
        }
        return connection;
    }

    /**
     * يُنشئ جميع الجداول ويُدرج بيانات تجريبية.
     * يُستدعى مرة واحدة عند بدء التطبيق.
     */
    public static void initializeDatabase() {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();

            // ---- إنشاء الجداول ----

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  id      INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name    TEXT NOT NULL," +
                "  email   TEXT UNIQUE NOT NULL," +
                "  phone   TEXT," +
                "  password TEXT NOT NULL," +
                "  role    TEXT NOT NULL" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS departments (" +
                "  id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name        TEXT UNIQUE NOT NULL," +
                "  description TEXT," +
                "  manager_name TEXT" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS employees (" +
                "  id               INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name            TEXT NOT NULL," +
                "  email            TEXT UNIQUE NOT NULL," +
                "  phone            TEXT," +
                "  position         TEXT," +
                "  salary           REAL," +
                "  department_name  TEXT," +
                "  shift            TEXT," +
                "  hire_date        TEXT," +
                "  status           TEXT DEFAULT 'نشط'" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS attendance (" +
                "  id           INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  employee_id  INTEGER NOT NULL," +
                "  employee_name TEXT," +
                "  date         TEXT NOT NULL," +
                "  status       TEXT NOT NULL," +
                "  check_in     TEXT," +
                "  check_out    TEXT," +
                "  notes        TEXT" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS leave_requests (" +
                "  id           INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  employee_id  INTEGER NOT NULL," +
                "  employee_name TEXT," +
                "  leave_type   TEXT," +
                "  start_date   TEXT," +
                "  end_date     TEXT," +
                "  reason       TEXT," +
                "  status       TEXT DEFAULT 'معلّق'," +
                "  reviewed_by  TEXT" +
                ")"
            );

            System.out.println("✔ تم إنشاء جميع الجداول بنجاح.");
            insertSampleData(stmt);
            stmt.close();

        } catch (SQLException e) {
            System.err.println("خطأ في تهيئة قاعدة البيانات: " + e.getMessage());
        }
    }

    /**
     * يُدرج بيانات تجريبية (يتخطى إذا كانت البيانات موجودة بالفعل).
     */
    private static void insertSampleData(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
        if (rs.next() && rs.getInt(1) > 0) { rs.close(); return; }
        rs.close();

        // مستخدمو النظام
        stmt.execute("INSERT INTO users (name,email,phone,password,role) VALUES (' المدير','admin','0501111111','admin','Admin')");
    }

    /** يُغلق اتصال قاعدة البيانات. */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✔ تم إغلاق اتصال قاعدة البيانات الوحيد.");
            }
        } catch (SQLException e) {
            System.err.println("خطأ أثناء إغلاق الاتصال: " + e.getMessage());
        }
    }
}