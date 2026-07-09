package service;

import dataaccess.DepartmentDAO;
import model.Department;
import exception.EmployeeException;

import java.util.List;

/**
 * طبقة منطق العمل الخاصة بالأقسام.
 */
public class DepartmentService {

    private final DepartmentDAO departmentDAO;

    public DepartmentService() {
        this.departmentDAO = new DepartmentDAO();
    }

    /** جلب جميع الأقسام. */
    public List<Department> getAllDepartments() throws EmployeeException {
        return departmentDAO.getAllDepartments();
    }

    /** التحقق من البيانات وإضافة قسم جديد. */
    public void validateAndAddDepartment(Department dept) throws EmployeeException {
        if (dept.getName() == null || dept.getName().trim().isEmpty()) {
            throw new EmployeeException("خطأ: اسم القسم لا يمكن أن يكون فارغاً!");
        }
        departmentDAO.addDepartment(dept);
    }

    /** التحقق من البيانات وتعديل قسم. */
    public void updateDepartment(Department dept) throws EmployeeException {
        if (dept.getName() == null || dept.getName().trim().isEmpty()) {
            throw new EmployeeException("خطأ: اسم القسم لا يمكن أن يكون فارغاً!");
        }
        departmentDAO.updateDepartment(dept);
    }

    /** حذف قسم. */
    public void deleteDepartment(int id) throws EmployeeException {
        if (id <= 0) {
            throw new EmployeeException("خطأ: رقم القسم غير صحيح!");
        }
        departmentDAO.deleteDepartment(id);
    }
    
    /** أسماء جميع الأقسام. */
    public List<String> getAllDepartmentNames() throws EmployeeException {
        return departmentDAO.getAllDepartmentNames();
    }
}