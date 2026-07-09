package service;

import dataaccess.DatabaseConnection;
import dataaccess.EmployeeDAO;
import model.Employee;
import exception.EmployeeException;
import java.util.List;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * طبقة منطق العمل (Business Logic Layer) الشاملة والنهائية للمشروع.
 */
public class EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }

    // =========================================================================
    // 1. دوال إدارة الموظفين الأساسية (CRUD Operations)
    // =========================================================================

    /**
     * جلب قائمة جميع الموظفين
     */
    public List<Employee> getAllEmployees() throws EmployeeException {
        return employeeDAO.getAllEmployees();
    }

    /**
     * التحقق من البيانات وإضافة موظف جديد
     */
    public void validateAndAddEmployee(Employee emp) throws EmployeeException {
        if (emp.getName() == null || emp.getName().trim().isEmpty()) {
            throw new EmployeeException("خطأ: اسم الموظف لا يمكن أن يكون فارغاً!");
        }
        if (emp.getSalary() <= 0) {
            throw new EmployeeException("خطأ: راتب الموظف يجب أن يكون أكبر من صفر!");
        }
        employeeDAO.addEmployee(emp);
    }

    /**
     * جلب موظف معين عن طريق الرقم التعريفي (ID)
     */
    public Employee getEmployeeById(int id) throws EmployeeException {
        // إذا كان الـ DAO يحتوي على دالة جلب الموظف بالـ ID استخدمها مباشرة:
        try {
            return employeeDAO.getEmployeeById(id);
        } catch (Exception e) {
            // حل بديل في حال عدم وجود الدالة بالـ DAO: البحث داخل القائمة الشاملة
            return employeeDAO.getAllEmployees().stream()
                    .filter(emp -> emp.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new EmployeeException("الموظف غير موجود!"));
        }
    }

    /**
     * حذف موظف نهائياً من قاعدة البيانات بواسطة الـ ID
     */
    public void deleteEmployee(int id) throws EmployeeException {
        if (id <= 0) {
            throw new EmployeeException("خطأ: رقم الموظف غير صحيح!");
        }
        // تمرير الأمر لطبقة البيانات للتنفيذ الفعلي في MySQL
        employeeDAO.deleteEmployee(id);
    }


    // =========================================================================
    // 2. دوال لوحة التحكم الإحصائية الديناميكية (Dashboard Statistics)
    // =========================================================================

    /**
     * الإحصائية الأولى: حساب عدد الموظفين الفعلي الحقيقي من قاعدة البيانات
     */
    public int getTotalEmployeesCount() {
        int count = 0;
        String query = "SELECT COUNT(*) AS total FROM employees";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (Exception e) {
            System.err.println("خطأ أثناء جلب عدد الموظفين: " + e.getMessage());
            try { return employeeDAO.getAllEmployees().size(); } catch (Exception ex) { return 0; }
        }
        return count;
    }

    /**
     * الإحصائية الثانية: حساب عدد الأقسام الفعلي المسجل في النظام
     */
    public int getTotalDepartmentsCount() {
        int count = 0;
        String query = "SELECT COUNT(*) AS total FROM departments";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (Exception e) {
            System.err.println("خطأ أثناء جلب عدد الأقسام: " + e.getMessage());
            return 0;
        }
        return count;
    }

    /**
     * الإحصائية الثالثة: حساب مجموع الرواتب الحية للموظفين في الوقت الحالي
     */
    public double getTotalSalaryBudget() {
        double sum = 0.0;
        String query = "SELECT SUM(salary) AS total_salaries FROM employees";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                sum = rs.getDouble("total_salaries");
            }
        } catch (Exception e) {
            System.err.println("خطأ أثناء جلب مجموع ميزانية الرواتب: " + e.getMessage());
            return 0.0;
        }
        return sum;
    }
    public void updateEmployee(Employee emp) throws EmployeeException {
        // استدعاء دالة التعديل من الـ DAO لتحديث البيانات في قاعدة البيانات
        employeeDAO.updateEmployee(emp);
    }
    // ================================================================
    //  دوال مساعدة تُمرَّر إلى الواجهات عبر طبقة الخدمة
    // ================================================================

    /** أسماء الموظفين النشطين. */
    public List<String> getActiveEmployeeNames() throws EmployeeException {
        return employeeDAO.getActiveEmployeeNames();
    }

    /** معرّف الموظف بالاسم. */
    public int getEmployeeIdByName(String name) throws EmployeeException {
        return employeeDAO.getEmployeeIdByName(name);
    }

    /** ربط المستخدم الحالي بسجل الموظف. */
    public int resolveEmployeeId(String userName, int userId) throws EmployeeException {
        return employeeDAO.resolveEmployeeId(userName, userId);
    }
}