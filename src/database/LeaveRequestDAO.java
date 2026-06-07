package database;

import model.LeaveRequest;
import exception.EmployeeException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * كائن الوصول إلى بيانات طلبات الإجازة.
 *
 * @author فريق المشروع
 */
public class LeaveRequestDAO {

    /** يُقدّم طلب إجازة جديد. */
    public void submitLeaveRequest(LeaveRequest req) throws EmployeeException {
        String sql = "INSERT INTO leave_requests (employee_id,employee_name,leave_type,start_date,end_date,reason,status,reviewed_by) VALUES (?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, req.getEmployeeId());
            ps.setString(2, req.getEmployeeName());
            ps.setString(3, req.getLeaveType());
            ps.setString(4, req.getStartDate());
            ps.setString(5, req.getEndDate());
            ps.setString(6, req.getReason());
            ps.setString(7, req.getStatus());
            ps.setString(8, req.getReviewedBy());
            ps.executeUpdate(); ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في تقديم طلب الإجازة: " + e.getMessage(), e);
        }
    }

    /** يُحدّث حالة طلب إجازة (قبول أو رفض). */
    public void updateLeaveStatus(int id, String status, String reviewedBy) throws EmployeeException {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection()
                    .prepareStatement("UPDATE leave_requests SET status=?,reviewed_by=? WHERE id=?");
            ps.setString(1, status);
            ps.setString(2, reviewedBy);
            ps.setInt(3, id);
            ps.executeUpdate(); ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في تحديث حالة الطلب: " + e.getMessage(), e);
        }
    }

    /** يُرجع جميع طلبات الإجازة. */
    public List<LeaveRequest> getAllLeaveRequests() throws EmployeeException {
        List<LeaveRequest> list = new ArrayList<>();
        try {
            Statement stmt = DatabaseConnection.getConnection().createStatement();
            ResultSet rs   = stmt.executeQuery("SELECT * FROM leave_requests ORDER BY id DESC");
            while (rs.next()) list.add(mapRow(rs));
            rs.close(); stmt.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في استرداد طلبات الإجازة: " + e.getMessage(), e);
        }
        return list;
    }

    /** يُرجع طلبات إجازة موظف معين. */
    public List<LeaveRequest> getLeaveByEmployee(int employeeId) throws EmployeeException {
        List<LeaveRequest> list = new ArrayList<>();
        try {
            PreparedStatement ps = DatabaseConnection.getConnection()
                    .prepareStatement("SELECT * FROM leave_requests WHERE employee_id=? ORDER BY id DESC");
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            rs.close(); ps.close();
        } catch (SQLException e) {
            throw new EmployeeException("فشل في استرداد طلبات الإجازة: " + e.getMessage(), e);
        }
        return list;
    }

    private LeaveRequest mapRow(ResultSet rs) throws SQLException {
        return new LeaveRequest(
            rs.getInt("id"), rs.getInt("employee_id"), rs.getString("employee_name"),
            rs.getString("leave_type"), rs.getString("start_date"), rs.getString("end_date"),
            rs.getString("reason"), rs.getString("status"), rs.getString("reviewed_by")
        );
    }
}
