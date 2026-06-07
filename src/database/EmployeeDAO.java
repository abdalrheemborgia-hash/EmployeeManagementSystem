package database;

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
}
