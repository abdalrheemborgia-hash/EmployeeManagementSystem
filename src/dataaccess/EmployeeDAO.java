package dataaccess;

import model.Employee;
import exception.EmployeeException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * كائن الوصول إلى بيانات الموظفين (DAO).
 * يُنفّذ جميع عمليات CRUD للموظفين.
 *
 * @author فريق المشروع
 */
public class EmployeeDAO {

    /** يُضيف موظفاً جديداً إلى قاعدة البيانات. */
    public void addEmployee(Employee emp) throws EmployeeException {
        String sql = "INSERT INTO employees (name,email,phone,position,salary,department_name,shift,hire_date,status) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPhone());
            ps.setString(4, emp.getPosition());
            ps.setDouble(5, emp.getSalary());
            ps.setString(6, emp.getDepartmentName());
            ps.setString(7, emp.getShift());
            ps.setString(8, emp.getHireDate());
            ps.setString(9, emp.getStatus());
            ps.executeUpdate(); ps.close();

            // أضف للمستخدمين أيضاً لتمكين تسجيل الدخول
            String userSql = "INSERT OR IGNORE INTO users (name,email,phone,password,role) VALUES (?,?,?,?,?)";
            PreparedStatement up = conn.prepareStatement(userSql);
            up.setString(1, emp.getName());
            up.setString(2, emp.getEmail());
            up.setString(3, emp.getPhone());
            up.setString(4, emp.getPassword() != null ? emp.getPassword() : "emp123");
            up.setString(5, emp.getRole());
            up.executeUpdate(); up.close();

        } catch (SQLException e) {
            throw new EmployeeException("فشل في إضافة الموظف: " + e.getMessage(), e);
        }
    }

    /** يُحدّث بيانات موظف موجود. */
    public void updateEmployee(Employee emp) throws EmployeeException {
        String sql = "UPDATE employees SET name=?,email=?,phone=?,position=?,salary=?,department_name=?,shift=?,hire_date=?,status=? WHERE id=?";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPhone());
            ps.setString(4, emp.getPosition());
            ps.setDouble(5, emp.getSalary());
            ps.setString(6, emp.getDepartmentName());
            ps.setString(7, emp.getShift());
            ps.setString(8, emp.getHireDate());
            ps.setString(9, emp.getStatus());
            ps.setInt(10, emp.getId());
            ps.executeUpdate(); ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في تحديث بيانات الموظف: " + e.getMessage(), e);
        }
    }

    /** يحذف موظفاً حسب المعرف. */
    public void deleteEmployee(int id) throws EmployeeException {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection()
                    .prepareStatement("DELETE FROM employees WHERE id=?");
            ps.setInt(1, id); ps.executeUpdate(); ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في حذف الموظف: " + e.getMessage(), e);
        }
    }

    /** يُرجع قائمة بجميع الموظفين. */
    public List<Employee> getAllEmployees() throws EmployeeException {
        List<Employee> list = new ArrayList<>();
        try {
            Statement stmt = DatabaseConnection.getConnection().createStatement();
            ResultSet rs   = stmt.executeQuery("SELECT * FROM employees ORDER BY name");
            while (rs.next()) {
                Employee emp = new Employee(
                    rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                    rs.getString("phone"), "", rs.getString("position"),
                    rs.getDouble("salary"), rs.getString("department_name"),
                    rs.getString("shift"), rs.getString("hire_date")
                );
                emp.setStatus(rs.getString("status"));
                list.add(emp);
            }
            rs.close(); stmt.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في استرداد الموظفين: " + e.getMessage(), e);
        }
        return list;
    }

    /** يُرجع موظفاً واحداً حسب المعرف. */
    public Employee getEmployeeById(int id) throws EmployeeException {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection()
                    .prepareStatement("SELECT * FROM employees WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Employee emp = new Employee(
                    rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                    rs.getString("phone"), "", rs.getString("position"),
                    rs.getDouble("salary"), rs.getString("department_name"),
                    rs.getString("shift"), rs.getString("hire_date")
                );
                emp.setStatus(rs.getString("status"));
                rs.close(); ps.close();
                return emp;
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في إيجاد الموظف: " + e.getMessage(), e);
        }
        return null;
    }
    // ================================================================
    //  دوال مساعدة (نُقلت من طبقة الواجهة إلى مكانها الصحيح)
    // ================================================================

    /** يُرجع أسماء الموظفين النشطين (لقوائم الاختيار). */
    public List<String> getActiveEmployeeNames() throws EmployeeException {
        List<String> names = new ArrayList<>();
        try {
            Statement s = DatabaseConnection.getConnection().createStatement();
            ResultSet r = s.executeQuery("SELECT name FROM employees WHERE status='نشط' ORDER BY name");
            while (r.next()) names.add(r.getString("name"));
            r.close(); s.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في جلب أسماء الموظفين: " + e.getMessage(), e);
        }
        return names;
    }

    /** يُرجع معرّف الموظف بالاسم (0 إن لم يوجد). */
    public int getEmployeeIdByName(String name) throws EmployeeException {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection()
                    .prepareStatement("SELECT id FROM employees WHERE name=?");
            ps.setString(1, name);
            ResultSet r = ps.executeQuery();
            int id = r.next() ? r.getInt("id") : 0;
            r.close(); ps.close();
            return id;
        } catch (SQLException e) {
            throw new EmployeeException("فشل في إيجاد معرّف الموظف: " + e.getMessage(), e);
        }
    }

    /**
     * يربط حساب المستخدم بسجل الموظف: يبحث بالبريد عبر جدول users ثم بالاسم.
     * يُرجع معرّف الموظف في جدول employees (0 إن لم يوجد).
     */
    public int resolveEmployeeId(String userName, int userId) throws EmployeeException {
        try {
            Connection conn = DatabaseConnection.getConnection();

            // 1) ابحث بالبريد الإلكتروني عبر جدول users
            PreparedStatement ps1 = conn.prepareStatement("SELECT email FROM users WHERE id=?");
            ps1.setInt(1, userId);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                String email = rs1.getString("email");
                rs1.close(); ps1.close();

                PreparedStatement ps2 = conn.prepareStatement("SELECT id FROM employees WHERE email=?");
                ps2.setString(1, email);
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    int empId = rs2.getInt("id");
                    rs2.close(); ps2.close();
                    return empId;
                }
                rs2.close(); ps2.close();
            } else {
                rs1.close(); ps1.close();
            }

            // 2) ابحث بالاسم مباشرة
            return getEmployeeIdByName(userName);

        } catch (SQLException e) {
            throw new EmployeeException("فشل في ربط المستخدم بالموظف: " + e.getMessage(), e);
        }
    }

}
