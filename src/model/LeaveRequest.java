package model;

/**
 * يمثل طلب إجازة مقدَّم من موظف.
 *
 * @author فريق المشروع
 */
public class LeaveRequest {
    private int    id;
    private int    employeeId;
    private String employeeName;
    private String leaveType;   // سنوية، مرضية، طارئة
    private String startDate;
    private String endDate;
    private String reason;
    private String status;      // معلّق، مقبول، مرفوض
    private String reviewedBy;

    public LeaveRequest() { this.status = "معلّق"; }

    public LeaveRequest(int id, int employeeId, String employeeName, String leaveType,
                        String startDate, String endDate, String reason,
                        String status, String reviewedBy) {
        this.id           = id;
        this.employeeId   = employeeId;
        this.employeeName = employeeName;
        this.leaveType    = leaveType;
        this.startDate    = startDate;
        this.endDate      = endDate;
        this.reason       = reason;
        this.status       = status;
        this.reviewedBy   = reviewedBy;
    }

    public int    getId()                   { return id; }
    public void   setId(int id)             { this.id = id; }
    public int    getEmployeeId()           { return employeeId; }
    public void   setEmployeeId(int e)      { this.employeeId = e; }
    public String getEmployeeName()         { return employeeName; }
    public void   setEmployeeName(String n) { this.employeeName = n; }
    public String getLeaveType()            { return leaveType; }
    public void   setLeaveType(String t)    { this.leaveType = t; }
    public String getStartDate()            { return startDate; }
    public void   setStartDate(String d)    { this.startDate = d; }
    public String getEndDate()              { return endDate; }
    public void   setEndDate(String d)      { this.endDate = d; }
    public String getReason()               { return reason; }
    public void   setReason(String r)       { this.reason = r; }
    public String getStatus()               { return status; }
    public void   setStatus(String s)       { this.status = s; }
    public String getReviewedBy()           { return reviewedBy; }
    public void   setReviewedBy(String r)   { this.reviewedBy = r; }
}
