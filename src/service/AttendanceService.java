package service;

import dataaccess.AttendanceDAO;
import model.Attendance;
import exception.EmployeeException;

import java.util.List;

/**
 * طبقة منطق العمل الخاصة بالحضور والغياب.
 */
public class AttendanceService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }

    /** التحقق من البيانات وتسجيل حضور جديد. */
    public void recordAttendance(Attendance att) throws EmployeeException {
        if (att.getEmployeeName() == null || att.getEmployeeName().trim().isEmpty()) {
            throw new EmployeeException("خطأ: يجب اختيار موظف!");
        }
        if (att.getDate() == null || att.getDate().trim().isEmpty()) {
            throw new EmployeeException("خطأ: يجب إدخال التاريخ!");
        }
        attendanceDAO.recordAttendance(att);
    }

    /** جلب جميع سجلات الحضور. */
    public List<Attendance> getAllAttendance() throws EmployeeException {
        return attendanceDAO.getAllAttendance();
    }

    /** جلب سجلات حضور موظف معيّن. */
    public List<Attendance> getAttendanceByEmployee(int employeeId) throws EmployeeException {
        return attendanceDAO.getAttendanceByEmployee(employeeId);
    }
}