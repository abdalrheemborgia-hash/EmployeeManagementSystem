package model;

/**
 * الفئة الأساسية المجردة التي تمثل شخصاً في النظام.
 * <p>
 * مفاهيم البرمجة الكائنية:
 * - التجريد (Abstract Class) - طريقة مجردة يجب تنفيذها في الفئات الفرعية
 * - التغليف (Encapsulation) - حقول خاصة مع getters وsetters
 * - الوراثة (Inheritance) - تُوسَّع من قِبَل Employee وManager وAdmin
 * </p>
 *
 * @author فريق المشروع
 * @version 1.0
 */
public abstract class Person {

    // ============================================================
    // مفهوم التغليف - حقول خاصة
    // ============================================================
    private int    id;
    private String name;
    private String email;
    private String phone;
    private String password;

    /** المُنشئ الافتراضي */
    public Person() {}

    /**
     * مُنشئ كامل للشخص.
     *
     * @param id       المعرف الفريد
     * @param name     الاسم الكامل
     * @param email    اسم المستخدم
     * @param phone    رقم الهاتف
     * @param password كلمة المرور
     */
    public Person(int id, String name, String email, String phone, String password) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.phone    = phone;
        this.password = password;
    }

    // ============================================================
    // مفهوم التجريد - الفئات الفرعية يجب أن تُنفّذ هذه الطريقة
    // ============================================================

    /**
     * يُرجع دور هذا الشخص (مدير، مشرف، موظف).
     * كل فئة فرعية يجب أن تُقدّم تنفيذها الخاص.
     *
     * @return نص يصف الدور
     */
    public abstract String getRole();

    /**
     * يعرض معلومات الشخص.
     * يظهر تعدد الأشكال عند التجاوز في الفئات الفرعية.
     *
     * @return نص المعلومات المنسق
     */
    public String getInfo() {
        return "المعرف: " + id + " | الاسم: " + name + " | الدور: " + getRole();
    }

    // ============================================================
    // Getters و Setters (التغليف)
    // ============================================================

    public int    getId()       { return id; }
    public void   setId(int id) { this.id = id; }

    public String getName()          { return name; }
    public void   setName(String n)  { this.name = n; }

    public String getEmail()           { return email; }
    public void   setEmail(String e)   { this.email = e; }

    public String getPhone()           { return phone; }
    public void   setPhone(String p)   { this.phone = p; }

    public String getPassword()        { return password; }
    public void   setPassword(String p){ this.password = p; }

    @Override
    public String toString() { return getInfo(); }
}
