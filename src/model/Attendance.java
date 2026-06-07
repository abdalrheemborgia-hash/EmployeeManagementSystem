package model;

/**
 * يمثل سجل حضور لموظف.
 *
 * @author فريق المشروع
 */
public class Attendance {
    private int    id;
    private int    employeeId;
    private String employeeName;
    private String date;
    private String status;    // حاضر، غائب، متأخر
    private String checkIn;
    private String checkOut;
    private String notes;

    public Attendance() {}

    public Attendance(int id, int employeeId, String employeeName, String date,
                      String status, String checkIn, String checkOut, String notes) {
        this.id           = id;
        this.employeeId   = employeeId;
        this.employeeName = employeeName;
        this.date         = date;
        this.status       = status;
        this.checkIn      = checkIn;
        this.checkOut     = checkOut;
        this.notes        = notes;
    }

    public int    getId()                   { return id; }
    public void   setId(int id)             { this.id = id; }
    public int    getEmployeeId()           { return employeeId; }
    public void   setEmployeeId(int e)      { this.employeeId = e; }
    public String getEmployeeName()         { return employeeName; }
    public void   setEmployeeName(String n) { this.employeeName = n; }
    public String getDate()                 { return date; }
    public void   setDate(String d)         { this.date = d; }
    public String getStatus()               { return status; }
    public void   setStatus(String s)       { this.status = s; }
    public String getCheckIn()              { return checkIn; }
    public void   setCheckIn(String c)      { this.checkIn = c; }
    public String getCheckOut()             { return checkOut; }
    public void   setCheckOut(String c)     { this.checkOut = c; }
    public String getNotes()                { return notes; }
    public void   setNotes(String n)        { this.notes = n; }
}
