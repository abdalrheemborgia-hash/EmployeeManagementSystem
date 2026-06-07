package model;

/**
 * يمثل موظفاً عادياً في النظام.
 * <p>
 * مفاهيم البرمجة الكائنية:
 * - الوراثة: يمتد من Person
 * - تجاوز الطرق: يُجاوز getRole() وgetInfo()
 * - التغليف: حقول خاصة مع getters/setters
 * - تطبيق واجهة Reportable
 * </p>
 *
 * @author فريق المشروع
 * @version 1.0
 */
public class Employee extends Person implements Reportable {

    private String position;
    private double salary;
    private String departmentName;
    private String shift;
    private String hireDate;
    private String status;

    /** المُنشئ الافتراضي */
    public Employee() {
        super();
        this.status = "نشط";
    }

    /**
     * مُنشئ كامل للموظف.
     */
    public Employee(int id, String name, String email, String phone, String password,
                    String position, double salary, String departmentName,
                    String shift, String hireDate) {
        super(id, name, email, phone, password);
        this.position       = position;
        this.salary         = salary;
        this.departmentName = departmentName;
        this.shift          = shift;
        this.hireDate       = hireDate;
        this.status         = "نشط";
    }

    // ============================================================
    // تجاوز الطرق (Method Overriding)
    // ============================================================

    @Override
    public String getRole() { return "موظف"; }

    @Override
    public String getInfo() {
        return super.getInfo() + " | المنصب: " + position + " | القسم: " + departmentName;
    }

    // ============================================================
    // تطبيق واجهة Reportable
    // ============================================================

    @Override
    public String generateReport() {
        return "=== تقرير الموظف ===\n"
             + "المعرف: "        + getId()  + "\n"
             + "الاسم: "         + getName() + "\n"
             + "اسم المستخدم: "        + getEmail() + "\n"
             + "الهاتف: "        + getPhone() + "\n"
             + "المنصب: "        + position  + "\n"
             + "القسم: "         + departmentName + "\n"
             + "الراتب: "        + String.format("%.2f", salary) + " د.ل\n"
             + "الوردية: "       + shift     + "\n"
             + "تاريخ التعيين: " + hireDate  + "\n"
             + "الحالة: "        + status;
    }

    @Override
    public String getReportTitle() { return "تقرير الموظف"; }

    // Getters & Setters
    public String getPosition()              { return position; }
    public void   setPosition(String p)      { this.position = p; }

    public double getSalary()                { return salary; }
    public void   setSalary(double s)        { this.salary = s; }

    public String getDepartmentName()        { return departmentName; }
    public void   setDepartmentName(String d){ this.departmentName = d; }

    public String getShift()                 { return shift; }
    public void   setShift(String s)         { this.shift = s; }

    public String getHireDate()              { return hireDate; }
    public void   setHireDate(String d)      { this.hireDate = d; }

    public String getStatus()                { return status; }
    public void   setStatus(String s)        { this.status = s; }
}
