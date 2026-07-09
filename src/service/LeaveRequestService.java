package service;

import dataaccess.LeaveRequestDAO;
import model.LeaveRequest;
import exception.EmployeeException;

import java.util.List;

/**
 * طبقة منطق العمل الخاصة بطلبات الإجازة.
 */
public class LeaveRequestService {

    private final LeaveRequestDAO leaveRequestDAO;

    public LeaveRequestService() {
        this.leaveRequestDAO = new LeaveRequestDAO();
    }

    /** التحقق من البيانات وتقديم طلب إجازة جديد. */
    public void submitLeaveRequest(LeaveRequest req) throws EmployeeException {
        if (req.getReason() == null || req.getReason().trim().isEmpty()) {
            throw new EmployeeException("خطأ: يجب إدخال سبب الإجازة!");
        }
        if (req.getStartDate() == null || req.getStartDate().trim().isEmpty()) {
            throw new EmployeeException("خطأ: يجب إدخال تاريخ البدء!");
        }
        if (req.getEndDate() == null || req.getEndDate().trim().isEmpty()) {
            throw new EmployeeException("خطأ: يجب إدخال تاريخ الانتهاء!");
        }
        leaveRequestDAO.submitLeaveRequest(req);
    }

    /** تحديث حالة الطلب (قبول / رفض). */
    public void updateLeaveStatus(int id, String status, String reviewedBy) throws EmployeeException {
        if (id <= 0) {
            throw new EmployeeException("خطأ: رقم الطلب غير صحيح!");
        }
        leaveRequestDAO.updateLeaveStatus(id, status, reviewedBy);
    }

    /** جلب جميع طلبات الإجازة. */
    public List<LeaveRequest> getAllLeaveRequests() throws EmployeeException {
        return leaveRequestDAO.getAllLeaveRequests();
    }

    /** جلب طلبات إجازة موظف معيّن. */
    public List<LeaveRequest> getLeaveByEmployee(int employeeId) throws EmployeeException {
        return leaveRequestDAO.getLeaveByEmployee(employeeId);
    }
}