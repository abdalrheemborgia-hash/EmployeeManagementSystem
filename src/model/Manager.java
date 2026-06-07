package model;

/**
 * يمثل مشرفاً (مديراً) في النظام.
 * <p>
 * مفاهيم البرمجة الكائنية:
 * - الوراثة: يمتد من Employee (الذي يمتد من Person)
 * - تجاوز الطرق: يُجاوز getRole()
 * - تعدد الأشكال: يُعامَل كـ Employee أو Person عند الحاجة
 * </p>
 *
 * @author فريق المشروع
 * @version 1.0
 */
public class Manager extends Employee {

    private String managedDepartment;

    public Manager() { super(); }

    public Manager(int id, String name, String email, String phone, String password,
                   String position, double salary, String departmentName,
                   String shift, String hireDate, String managedDepartment) {
        super(id, name, email, phone, password, position, salary, departmentName, shift, hireDate);
        this.managedDepartment = managedDepartment;
    }

    // ============================================================
    // تجاوز الطرق - تعدد الأشكال
    // ============================================================

    @Override
    public String getRole() { return "مشرف"; }

    @Override
    public String getInfo() {
        return super.getInfo() + " | يُشرف على: " + managedDepartment;
    }

    @Override
    public String generateReport() {
        return super.generateReport() + "\nالقسم المُشرَف عليه: " + managedDepartment;
    }

    public String getManagedDepartment()          { return managedDepartment; }
    public void   setManagedDepartment(String d)  { this.managedDepartment = d; }
}
