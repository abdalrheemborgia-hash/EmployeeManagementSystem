package dataaccess;

import model.Person;
import model.Admin;
import model.Manager;
import model.Employee;
import exception.EmployeeException;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

/**
 * كائن الوصول إلى بيانات المستخدمين (DAO).
 * يتحقق من بيانات الدخول ويُرجع الكائن المناسب (Admin / Manager / Employee)
 * مستغلاً هرمية الوراثة وتعدد الأشكال.
 */
public class UserDAO {

    /**
     * يتحقق من بيانات الدخول.
     * @return كائن Person (Admin/Manager/Employee) عند النجاح، أو null إن كانت البيانات خاطئة.
     */
    public Person authenticate(String email, String password) throws EmployeeException {
        String sql = "SELECT * FROM users WHERE email=? AND password=?";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            Person person = null;
            if (rs.next()) {
                int    id    = rs.getInt("id");
                String name  = rs.getString("name");
                String mail  = rs.getString("email");
                String phone = rs.getString("phone");
                String role  = rs.getString("role");

                // ✅ تعدد الأشكال: إنشاء الكائن المناسب حسب الدور
                person = createPersonByRole(id, name, mail, phone, role);
            }
            rs.close();
            ps.close();
            return person; // null تعني بيانات غير صحيحة
        } catch (SQLException e) {
            throw new EmployeeException("فشل الاتصال بقاعدة البيانات أثناء تسجيل الدخول: " + e.getMessage(), e);
        }
    }

    /**
     * دالة مساعدة: تُنشئ الكائن المناسب بناءً على قيمة الدور القادمة من قاعدة البيانات.
     * تعتمد على المُنشئ الافتراضي + الـ setters لضمان التوافق مع جدول users.
     */
    private Person createPersonByRole(int id, String name, String email, String phone, String role) {
        Person person;
        switch (role) {
            case "Admin" -> {
                Admin admin = new Admin();
                admin.setAdminLevel("مدير");
                person = admin;
            }
            case "Manager" -> {
                Manager manager = new Manager();
                manager.setManagedDepartment("");
                person = manager;
            }
            default -> person = new Employee(); // Employee أو أي دور آخر
        }
        // تعبئة الحقول المشتركة (الموروثة من Person)
        person.setId(id);
        person.setName(name);
        person.setEmail(email);
        person.setPhone(phone);
        return person;
    }
    // ================================================================
    //  عمليات إدارة المستخدمين (CRUD) — نُقلت من الواجهة إلى مكانها الصحيح
    // ================================================================

    /** يُرجع جميع المستخدمين (يستغل هرمية Person وتعدد الأشكال). */
    public List<Person> getAllUsers() throws EmployeeException {
        List<Person> list = new ArrayList<>();
        String sql = "SELECT id,name,email,phone,role FROM users ORDER BY role,name";
        try {
            Statement stmt = DatabaseConnection.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(createPersonByRole(
                    rs.getInt("id"), rs.getString("name"),
                    rs.getString("email"), rs.getString("phone"),
                    rs.getString("role")));
            }
            rs.close(); stmt.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في استرداد المستخدمين: " + e.getMessage(), e);
        }
        return list;
    }

    /** يبحث عن المستخدمين بالاسم أو اسم المستخدم أو الدور. */
    public List<Person> searchUsers(String keyword) throws EmployeeException {
        List<Person> list = new ArrayList<>();
        String sql = "SELECT id,name,email,phone,role FROM users WHERE name LIKE ? OR email LIKE ? OR role LIKE ?";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            String q = "%" + keyword + "%";
            ps.setString(1, q); ps.setString(2, q); ps.setString(3, q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(createPersonByRole(
                    rs.getInt("id"), rs.getString("name"),
                    rs.getString("email"), rs.getString("phone"),
                    rs.getString("role")));
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في البحث عن المستخدمين: " + e.getMessage(), e);
        }
        return list;
    }

    /** يُضيف مستخدماً جديداً. */
    public void addUser(String name, String email, String phone, String password, String role) throws EmployeeException {
        String sql = "INSERT INTO users (name,email,phone,password,role) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setString(1, name); ps.setString(2, email); ps.setString(3, phone);
            ps.setString(4, password); ps.setString(5, role);
            ps.executeUpdate(); ps.close();
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE"))
                throw new EmployeeException("اسم المستخدم مسجّل مسبقاً. اختر اسم مستخدم آخر.", e);
            throw new EmployeeException("فشل في إضافة المستخدم: " + e.getMessage(), e);
        }
    }

    /** يُحدّث بيانات مستخدم بدون تغيير كلمة المرور. */
    public void updateUser(int id, String name, String email, String phone, String role) throws EmployeeException {
        String sql = "UPDATE users SET name=?, email=?, phone=?, role=? WHERE id=?";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setString(1, name); ps.setString(2, email); ps.setString(3, phone);
            ps.setString(4, role); ps.setInt(5, id);
            ps.executeUpdate(); ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في تحديث المستخدم: " + e.getMessage(), e);
        }
    }

    /** يُحدّث بيانات مستخدم مع تغيير كلمة المرور. */
    public void updateUserWithPassword(int id, String name, String email, String phone, String password, String role) throws EmployeeException {
        String sql = "UPDATE users SET name=?, email=?, phone=?, password=?, role=? WHERE id=?";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setString(1, name); ps.setString(2, email); ps.setString(3, phone);
            ps.setString(4, password); ps.setString(5, role); ps.setInt(6, id);
            ps.executeUpdate(); ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في تحديث المستخدم: " + e.getMessage(), e);
        }
    }

    /** يحذف مستخدماً حسب المعرف. */
    public void deleteUser(int id) throws EmployeeException {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("DELETE FROM users WHERE id=?");
            ps.setInt(1, id); ps.executeUpdate(); ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في حذف المستخدم: " + e.getMessage(), e);
        }
    }

    /** يُرجع البريد الإلكتروني لمستخدم حسب معرّفه (لاستخدامه في لوحة الرواتب). */
    public String getEmailById(int id) throws EmployeeException {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT email FROM users WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            String email = rs.next() ? rs.getString("email") : "";
            rs.close(); ps.close();
            return email;
        } catch (SQLException e) {
            throw new EmployeeException("فشل في جلب البريد الإلكتروني: " + e.getMessage(), e);
        }
    }
}