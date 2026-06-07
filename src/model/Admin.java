package model;

/**
 * يمثل مستخدم المدير (Admin) في النظام.
 *
 * @author فريق المشروع
 * @version 1.0
 */
public class Admin extends Person {

    private String adminLevel;

    public Admin() { super(); this.adminLevel = "مدير"; }

    public Admin(int id, String name, String email, String phone,
                 String password, String adminLevel) {
        super(id, name, email, phone, password);
        this.adminLevel = adminLevel;
    }

    @Override
    public String getRole() { return "مدير النظام"; }

    @Override
    public String getInfo() {
        return super.getInfo() + " | المستوى: " + adminLevel;
    }

    public String getAdminLevel()           { return adminLevel; }
    public void   setAdminLevel(String lvl) { this.adminLevel = lvl; }
}
