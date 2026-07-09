package dataaccess;

import model.Department;
import exception.EmployeeException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * كائن الوصول إلى بيانات الأقسام (DAO).
 * يُنفّذ جميع عمليات CRUD للأقسام.
 */
public class DepartmentDAO {

    /** يُضيف قسماً جديداً. */
    public void addDepartment(Department dept) throws EmployeeException {
        String sql = "INSERT INTO departments (name,description,manager_name) VALUES (?,?,?)";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getDescription());
            ps.setString(3, dept.getManagerName());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في إضافة القسم: " + e.getMessage(), e);
        }
    }

    /** يُحدّث بيانات قسم موجود. */
    public void updateDepartment(Department dept) throws EmployeeException {
        String sql = "UPDATE departments SET name=?,description=?,manager_name=? WHERE id=?";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getDescription());
            ps.setString(3, dept.getManagerName());
            ps.setInt(4, dept.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في تحديث القسم: " + e.getMessage(), e);
        }
    }

    /** يحذف قسماً حسب المعرف. */
    public void deleteDepartment(int id) throws EmployeeException {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection()
                    .prepareStatement("DELETE FROM departments WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في حذف القسم: " + e.getMessage(), e);
        }
    }

    /** يُرجع قائمة بجميع الأقسام. */
    public List<Department> getAllDepartments() throws EmployeeException {
        List<Department> list = new ArrayList<>();
        try {
            Statement s = DatabaseConnection.getConnection().createStatement();
            ResultSet r = s.executeQuery("SELECT * FROM departments ORDER BY name");
            while (r.next()) {
                Department d = new Department();
                d.setId(r.getInt("id"));
                d.setName(r.getString("name"));
                d.setDescription(r.getString("description"));
                d.setManagerName(r.getString("manager_name"));
                list.add(d);
            }
            r.close();
            s.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في استرداد الأقسام: " + e.getMessage(), e);
        }
        return list;
    }
    
    /** يُرجع أسماء جميع الأقسام (لقوائم الاختيار). */
    public List<String> getAllDepartmentNames() throws EmployeeException {
        List<String> names = new ArrayList<>();
        try {
            Statement s = DatabaseConnection.getConnection().createStatement();
            ResultSet r = s.executeQuery("SELECT name FROM departments ORDER BY name");
            while (r.next()) names.add(r.getString("name"));
            r.close(); s.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في جلب أسماء الأقسام: " + e.getMessage(), e);
        }
        return names;
    }
}