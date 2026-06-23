package service;

import dataaccess.EmployeeDAO; 
import model.Employee;
import exception.EmployeeException;
import java.util.List;

public class EmployeeService {
    
    private EmployeeDAO employeeDAO; 

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }

    public boolean validateAndAddEmployee(Employee emp) throws EmployeeException {
        if (emp.getName() == null || emp.getName().trim().isEmpty()) {
            throw new EmployeeException("خطأ: اسم الموظف لا يمكن أن يكون فارغاً!");
        }
        employeeDAO.addEmployee(emp); 
        return true;
    }

    // الجسور لتمرير بقية العمليات للـ DAO
    public void updateEmployee(Employee emp) throws EmployeeException {
        employeeDAO.updateEmployee(emp);
    }

    public void deleteEmployee(int id) throws EmployeeException {
        employeeDAO.deleteEmployee(id);
    }

    public List<Employee> getAllEmployees() throws EmployeeException {
        return employeeDAO.getAllEmployees();
    }

    public Employee getEmployeeById(int id) throws EmployeeException {
        return employeeDAO.getEmployeeById(id);
    }
}