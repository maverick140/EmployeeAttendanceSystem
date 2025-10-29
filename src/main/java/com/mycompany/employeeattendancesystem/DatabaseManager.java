package com.mycompany.employeeattendancesystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Manages all database operations (SQLite).
 * This includes creating tables, managing employees, and logging attendance.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:attendance.db";

    public DatabaseManager() {
        createTables();
        // Ensure the admin user exists
        addAdminUserIfNotExists();
    }

    /**
     * Creates the necessary tables if they don't already exist.
     */
    private void createTables() {
        // SQL statement for employees table
        String sqlEmployees = "CREATE TABLE IF NOT EXISTS employees ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " name TEXT NOT NULL,"
                + " position TEXT NOT NULL,"
                + " email TEXT UNIQUE NOT NULL"
                + ");";

        // SQL statement for admin table
        String sqlAdmin = "CREATE TABLE IF NOT EXISTS admin ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " username TEXT UNIQUE NOT NULL,"
                + " password TEXT NOT NULL" // Note: In a real app, hash this!
                + ");";

        // SQL statement for attendance table
        String sqlAttendance = "CREATE TABLE IF NOT EXISTS attendance ("
                + " attendance_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " employee_id INTEGER NOT NULL,"
                + " date TEXT NOT NULL," // This is the corrected column name
                + " status TEXT NOT NULL,"
                + " FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,"
                + " UNIQUE(employee_id, date)" // This constraint prevents duplicate entries per day
                + ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            // Create tables
            stmt.execute(sqlEmployees);
            stmt.execute(sqlAdmin);
            stmt.execute(sqlAttendance);
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    /**
     * Adds the default 'admin' user if no admin user exists.
     */
    private void addAdminUserIfNotExists() {
        String sqlCheck = "SELECT COUNT(*) FROM admin";
        String sqlInsert = "INSERT INTO admin(username, password) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlCheck)) {

            // Check if any admin users exist
            if (rs.next() && rs.getInt(1) == 0) {
                // No admin users, let's insert the default one
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, "admin123"); // Default password
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking/adding admin user: " + e.getMessage());
        }
    }

    /**
     * Validates admin login credentials.
     */
    public boolean validateAdmin(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // True if a matching record is found
            }
        } catch (SQLException e) {
            System.err.println("Error validating admin: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds a new employee to the database.
     */
    public boolean addEmployee(String name, String position, String email) {
        String sql = "INSERT INTO employees(name, position, email) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, position);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes an employee from the database.
     */
    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            return false;
        }
    }

    /**
     * Populates a DefaultTableModel with all employees.
     */
    public void loadEmployeesToTable(DefaultTableModel model) {
        model.setRowCount(0); // Clear existing data
        String sql = "SELECT id, name, position, email FROM employees";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("position"),
                    rs.getString("email")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error loading employees: " + e.getMessage());
        }
    }

    /**
     * Fetches all employees for the attendance combo box.
     */
    public List<Employee> getAllEmployeesForAttendance() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, name FROM employees";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(new Employee(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employees for attendance: " + e.getMessage());
        }
        return employees;
    }

    /**
     * Marks an employee's attendance.
     * Uses ON CONFLICT to update if an entry for that day already exists.
     */
    public boolean markAttendance(int employeeId, String date, String status) {
        // This query inserts a new record.
        // If a record with the same (employee_id, date) already exists (due to UNIQUE constraint),
        // it will update the 'status' of the existing record instead.
        String sql = "INSERT INTO attendance (employee_id, date, status) VALUES (?, ?, ?)"
                + " ON CONFLICT(employee_id, date) DO UPDATE SET status = excluded.status";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, date);
            pstmt.setString(3, status);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error marking attendance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Populates a DefaultTableModel with an attendance report for a specific date.
     */
    public void loadAttendanceReport(DefaultTableModel model, String date) {
        model.setRowCount(0); // Clear existing data

        // SQL to get all employees and JOIN their attendance status for the given date
        // Use LEFT JOIN to include employees who have NOT been marked yet (status will be NULL)
        String sql = "SELECT e.name, a.status FROM employees e "
                + " LEFT JOIN attendance a ON e.id = a.employee_id AND a.date = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    if (status == null) {
                        status = "Not Marked"; // Default for employees with no entry
                    }
                    model.addRow(new Object[]{
                        rs.getString("name"),
                        status
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching attendance report: " + e.getMessage());
        }
    }

    /**
     * A simple helper class to store Employee ID and Name for ComboBoxes.
     */
    public static class Employee {

        private int id;
        private String name;

        public Employee(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        // This is crucial: JComboBox uses toString() to display the item name
        @Override
        public String toString() {
            return name;
        }
    }
}

