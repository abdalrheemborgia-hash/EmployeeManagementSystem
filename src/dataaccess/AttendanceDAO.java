package dataaccess;

import model.Attendance;
import exception.EmployeeException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * كائن الوصول إلى بيانات الحضور.
 *
 * @author فريق المشروع
 */
public class AttendanceDAO {

    /** يُسجّل سجل حضور جديد. */
    public void recordAttendance(Attendance att) throws EmployeeException {
        String sql = "INSERT INTO attendance (employee_id,employee_name,date,status,check_in,check_out,notes) VALUES (?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, att.getEmployeeId());
            ps.setString(2, att.getEmployeeName());
            ps.setString(3, att.getDate());
            ps.setString(4, att.getStatus());
            ps.setString(5, att.getCheckIn());
            ps.setString(6, att.getCheckOut());
            ps.setString(7, att.getNotes());
            ps.executeUpdate(); ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في تسجيل الحضور: " + e.getMessage(), e);
        }
    }

    /** يُرجع جميع سجلات الحضور. */
    public List<Attendance> getAllAttendance() throws EmployeeException {
        List<Attendance> list = new ArrayList<>();
        try {
            Statement stmt = DatabaseConnection.getConnection().createStatement();
            ResultSet rs   = stmt.executeQuery("SELECT * FROM attendance ORDER BY date DESC");
            while (rs.next()) list.add(mapRow(rs));
            rs.close(); stmt.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في استرداد سجلات الحضور: " + e.getMessage(), e);
        }
        return list;
    }

    /** يُرجع سجلات حضور موظف معين. */
    public List<Attendance> getAttendanceByEmployee(int employeeId) throws EmployeeException {
        List<Attendance> list = new ArrayList<>();
        try {
            PreparedStatement ps = DatabaseConnection.getConnection()
                    .prepareStatement("SELECT * FROM attendance WHERE employee_id=? ORDER BY date DESC");
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            rs.close(); ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في استرداد سجلات الحضور: " + e.getMessage(), e);
        }
        return list;
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        return new Attendance(
            rs.getInt("id"), rs.getInt("employee_id"), rs.getString("employee_name"),
            rs.getString("date"), rs.getString("status"),
            rs.getString("check_in"), rs.getString("check_out"), rs.getString("notes")
        );
    }
}
