package Employee_Management;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private Connection conn;

    public EmployeeDAO() {
        conn = DBConnection.getConnection();
    }

    // 1️⃣ Add Employee
    public boolean addEmployee(String name, String department, String email, String phone, String address, String ctc) {
        String query = "INSERT INTO employees (name, department, email, phone, address, ctc) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, name);
            pst.setString(2, department);
            pst.setString(3, email);
            pst.setString(4, phone);
            pst.setString(5, address);
            pst.setString(6, ctc);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2️⃣ Get All Employees
    public List<String[]> getAllEmployees() {
        List<String[]> employees = new ArrayList<>();
        
        // ✅ Query to fetch employees with their attendance percentage
        String query = "SELECT e.emp_id, e.name, e.department, e.email, e.phone, e.address, e.ctc, " +
                " (COUNT(a.status) / (SELECT COUNT(*) FROM attendance WHERE emp_id = e.emp_id)) * 100 AS attendance_percentage " +
                " FROM employees e " +
                " LEFT JOIN attendance a ON e.emp_id = a.emp_id AND a.status = 'Present' " +
                " GROUP BY e.emp_id";

    
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                employees.add(new String[]{
                    String.valueOf(rs.getInt("emp_id")),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("ctc"),
                    String.format("%.2f", rs.getDouble("attendance_percentage")) // ✅ Convert attendance to 2 decimal places
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }
    

    // 3️⃣ Update Employee
    public boolean updateEmployee(int emp_id, String name, String department, String email, String phone, String address, String ctc) {
        String query = "UPDATE employees SET name=?, department=?, email=?, phone=?, address=?, ctc=? WHERE emp_id=?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, name);
            pst.setString(2, department);
            pst.setString(3, email);
            pst.setString(4, phone);
            pst.setString(5, address);
            pst.setString(6, ctc);
            pst.setInt(7, emp_id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4️⃣ Delete Employee
    public boolean deleteEmployee(int emp_id) {
        String query = "DELETE FROM employees WHERE emp_id=?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, emp_id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // ✅ Update Employee Salary
public boolean updateSalary(int emp_id, double ctc, double salary) {
    String query = "UPDATE employees SET ctc=?, salary=? WHERE emp_id=?";
    try (PreparedStatement pst = conn.prepareStatement(query)) {
        pst.setDouble(1, ctc);
        pst.setDouble(2, salary);
        pst.setInt(3, emp_id);
        return pst.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

}
