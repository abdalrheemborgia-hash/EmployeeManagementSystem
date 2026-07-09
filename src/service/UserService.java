package service;

import dataaccess.UserDAO;
import model.Person;
import java.util.List;
import exception.EmployeeException;

/**
 * طبقة منطق العمل الخاصة بالمستخدمين وتسجيل الدخول.
 */
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * التحقق من المدخلات ثم محاولة تسجيل الدخول.
     * @return كائن Person (متعدد الأشكال) عند النجاح، أو null إذا كانت البيانات غير صحيحة.
     */
    public Person login(String email, String password) throws EmployeeException {
        if (email == null || email.trim().isEmpty()) {
            throw new EmployeeException("يرجى إدخال اسم المستخدم.");
        }
        if (password == null || password.isEmpty()) {
            throw new EmployeeException("يرجى إدخال كلمة المرور.");
        }
        return userDAO.authenticate(email.trim(), password);
    }
    /** جلب جميع المستخدمين. */
    public List<Person> getAllUsers() throws EmployeeException {
        return userDAO.getAllUsers();
    }

    /** البحث عن المستخدمين. */
    public List<Person> searchUsers(String keyword) throws EmployeeException {
        return userDAO.searchUsers(keyword);
    }

    /** التحقق وإضافة مستخدم جديد. */
    public void addUser(String name, String email, String phone, String password, String role) throws EmployeeException {
        if (name == null || name.trim().isEmpty())   throw new EmployeeException("يرجى إدخال الاسم الكامل.");
        if (email == null || email.trim().isEmpty())  throw new EmployeeException("يرجى إدخال اسم المستخدم.");
        if (password == null || password.trim().isEmpty()) throw new EmployeeException("يرجى إدخال كلمة المرور.");
        userDAO.addUser(name.trim(), email.trim(), phone, password, role);
    }

    /**
     * التحقق وتعديل مستخدم.
     * إذا كانت كلمة المرور فارغة → لا تُغيَّر.
     */
    public void updateUser(int id, String name, String email, String phone, String password, String role) throws EmployeeException {
        if (id <= 0) throw new EmployeeException("يرجى تحديد مستخدم صحيح.");
        if (name == null || name.trim().isEmpty())  throw new EmployeeException("يرجى إدخال الاسم الكامل.");
        if (email == null || email.trim().isEmpty()) throw new EmployeeException("يرجى إدخال اسم المستخدم.");
        if (password == null || password.trim().isEmpty()) {
            userDAO.updateUser(id, name.trim(), email.trim(), phone, role);
        } else {
            userDAO.updateUserWithPassword(id, name.trim(), email.trim(), phone, password, role);
        }
    }

    /** حذف مستخدم. */
    public void deleteUser(int id) throws EmployeeException {
        if (id <= 0) throw new EmployeeException("يرجى تحديد مستخدم صحيح.");
        userDAO.deleteUser(id);
    }

    /** جلب البريد الإلكتروني بالمعرّف. */
    public String getEmailById(int id) throws EmployeeException {
        return userDAO.getEmailById(id);
    }
}