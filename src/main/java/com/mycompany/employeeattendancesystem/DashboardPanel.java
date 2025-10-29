package com.mycompany.employeeattendancesystem;

// Import the static color constants from MainApp for easy access
import static com.mycompany.employeeattendancesystem.MainApp.*;

import com.mycompany.employeeattendancesystem.DatabaseManager.Employee;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable; // This line is now fixed
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.event.ActionEvent;

/**
 * The main Dashboard panel.
 * This is the second "card" shown in MainApp, after a successful login.
 * It contains the JTabbedPane for all application functionality.
 */
public class DashboardPanel extends JPanel {

    private DatabaseManager dbManager;

    // --- Employee Management Tab ---
    private JTable employeeTable;
    private DefaultTableModel employeeTableModel;
    private JTextField nameField;
    private JTextField positionField;
    private JTextField emailField;

    // --- Mark Attendance Tab ---
    private JComboBox<Employee> employeeComboBox;
    private JComboBox<String> statusComboBox;
    private JLabel attendanceDateLabel;
    private String todayDate;

    // --- Attendance Report Tab ---
    private JTable reportTable;
    private DefaultTableModel reportTableModel;
    private JLabel reportDateLabel;

    public DashboardPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;

        // Get today's date in SQL-friendly format (YYYY-MM-DD)
        this.todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BACKGROUND); // Nyanza

        // --- Create Header ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        headerPanel.setBackground(COLOR_BACKGROUND_PANEL); // White
        headerPanel.putClientProperty("FlatLaf.style", "arc: 0"); // No rounding
        
        JLabel titleLabel = new JLabel("Employee Attendance System");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_PRIMARY); // Cool Gray
        headerPanel.add(titleLabel);
        
        add(headerPanel, BorderLayout.NORTH);

        // --- Create Tabbed Pane ---
        // FIX: Removed the duplicate "JTab"
        JTabbedPane tabbedPane = new JTabbedPane(); 
        tabbedPane.setFont(new Font("Inter", Font.BOLD, 14));
        
        // Add padding around the content of the tabs
        tabbedPane.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Create the three tabs
        tabbedPane.addTab("Employee Management", createEmployeeManagementTab());
        tabbedPane.addTab("Mark Attendance", createMarkAttendanceTab());
        tabbedPane.addTab("Attendance Report", createAttendanceReportTab());

        add(tabbedPane, BorderLayout.CENTER);

        // --- Load initial data ---
        refreshEmployeeManagementTab();
        refreshAttendanceTab();
        refreshReportTab();
    }

    // =========================================================================
    // TAB 1: EMPLOYEE MANAGEMENT
    // =========================================================================
    private JPanel createEmployeeManagementTab() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(COLOR_BACKGROUND); // Nyanza
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Left Panel: Add/Edit Form ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_BACKGROUND_PANEL); // White
        formPanel.putClientProperty("FlatLaf.style", "arc: 12");
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form Title
        JLabel formTitle = new JLabel("Add New Employee");
        formTitle.setFont(new Font("Inter", Font.BOLD, 18));
        formTitle.setForeground(COLOR_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);

        // Name
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Position
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Position:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        positionField = new JTextField(20);
        formPanel.add(positionField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Add Button
        JButton addButton = new JButton("Add Employee");
        addButton.setBackground(COLOR_PRIMARY); // Cool Gray
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Inter", Font.BOLD, 14));
        addButton.putClientProperty("FlatLaf.style", "defaultButton: true");
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(addButton, gbc);

        // --- Right Panel: Employee List ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(COLOR_BACKGROUND_PANEL); // White
        tablePanel.putClientProperty("FlatLaf.style", "arc: 12");
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel tableTitle = new JLabel("Current Employees");
        tableTitle.setFont(new Font("Inter", Font.BOLD, 18));
        tableTitle.setForeground(COLOR_PRIMARY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Name", "Position", "Email"};
        employeeTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        employeeTable = new JTable(employeeTableModel);
        setupTableStyle(employeeTable);
        
        // Don't show ID column
        employeeTable.getColumnModel().getColumn(0).setMinWidth(0);
        employeeTable.getColumnModel().getColumn(0).setMaxWidth(0);
        employeeTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Delete Button
        JButton deleteButton = new JButton("Delete Selected Employee");
        deleteButton.setBackground(COLOR_ERROR); // Red
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Inter", Font.BOLD, 14));
        deleteButton.setBorder(new EmptyBorder(10, 0, 0, 0));
        tablePanel.add(deleteButton, BorderLayout.SOUTH);

        // --- Add sub-panels to main panel ---
        panel.add(formPanel, BorderLayout.WEST);
        panel.add(tablePanel, BorderLayout.CENTER);

        // --- Action Listeners for this tab ---
        addButton.addActionListener(this::addEmployee);
        deleteButton.addActionListener(this::deleteEmployee);

        return panel;
    }

    // =========================================================================
    // TAB 2: MARK ATTENDANCE
    // =========================================================================
    private JPanel createMarkAttendanceTab() {
        // Use GridBagLayout to center the content
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BACKGROUND); // Nyanza
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Content Box (white) ---
        JPanel contentBox = new JPanel(new GridBagLayout());
        contentBox.setBackground(COLOR_BACKGROUND_PANEL); // White
        contentBox.putClientProperty("FlatLaf.style", "arc: 12");
        contentBox.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Mark Today's Attendance");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 20));
        titleLabel.setForeground(COLOR_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentBox.add(titleLabel, gbc);

        // Date Label
        attendanceDateLabel = new JLabel("Date: " + todayDate);
        attendanceDateLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        gbc.gridy = 1;
        contentBox.add(attendanceDateLabel, gbc);

        // Employee Label
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        contentBox.add(new JLabel("Select Employee:"), gbc);

        // Employee ComboBox
        employeeComboBox = new JComboBox<>();
        employeeComboBox.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentBox.add(employeeComboBox, gbc);

        // Status Label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        contentBox.add(new JLabel("Mark Status:"), gbc);

        // Status ComboBox
        String[] statuses = {"Present", "Absent", "On Leave"};
        statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentBox.add(statusComboBox, gbc);

        // Submit Button
        JButton submitButton = new JButton("Submit Attendance");
        submitButton.setBackground(COLOR_PRIMARY);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Inter", Font.BOLD, 14));
        submitButton.putClientProperty("FlatLaf.style", "defaultButton: true");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        contentBox.add(submitButton, gbc);

        // --- Add content box to the main panel ---
        panel.add(contentBox, new GridBagConstraints());
        
        // --- Action Listeners for this tab ---
        // FIX: Removed the extra dot
        submitButton.addActionListener(this::markAttendance); 

        return panel;
    }

    // =========================================================================
    // TAB 3: ATTENDANCE REPORT
    // =========================================================================
    private JPanel createAttendanceReportTab() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(COLOR_BACKGROUND_PANEL); // White
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.putClientProperty("FlatLaf.style", "arc: 12");

        // --- Header Panel (for date) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setOpaque(false); // Make transparent
        
        JLabel titleLabel = new JLabel("Daily Attendance Report");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_PRIMARY);
        headerPanel.add(titleLabel);
        
        // We'll show today's report by default
        reportDateLabel = new JLabel("| Date: " + todayDate);
        reportDateLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        headerPanel.add(reportDateLabel);
        
        // (Future enhancement: Add a JDatePicker here to select date)

        panel.add(headerPanel, BorderLayout.NORTH);

        // --- Report Table ---
        String[] columnNames = {"Employee Name", "Status"};
        reportTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable
            }
        };
        reportTable = new JTable(reportTableModel);
        setupTableStyle(reportTable);

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // --- Refresh Button ---
        JButton refreshButton = new JButton("Refresh Report");
        refreshButton.setBackground(COLOR_ACCENT); // Vista Blue
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Inter", Font.BOLD, 14));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // --- Action Listeners for this tab ---
        refreshButton.addActionListener(e -> refreshReportTab());

        return panel;
    }

    // =========================================================================
    // HELPER METHODS (Styling)
    // =========================================================================
    
    /**
     * Applies a consistent visual style to a JTable.
     */
    private void setupTableStyle(JTable table) {
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 14));
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true); // Fills empty space

        // Center align text in all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnName(i).equals("Name") || table.getColumnName(i).equals("Email")) {
                 // Left-align text columns
                DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
                leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
                table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            } else {
                 table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }

    // =========================================================================
    // DATA REFRESH METHODS
    // =========================================================================
    
    /**
     * Clears the form and reloads the employee table from the database.
     */
    private void refreshEmployeeManagementTab() {
        // Clear form
        nameField.setText("");
        positionField.setText("");
        emailField.setText("");

        // Reload table
        dbManager.loadEmployeesToTable(employeeTableModel);
    }

    /**
     * Reloads the employee list in the attendance combo box.
     */
    private void refreshAttendanceTab() {
        employeeComboBox.removeAllItems();
        // --- THIS IS THE FIX ---
        // The method in DatabaseManager is "getAllEmployeesForAttendance"
        List<Employee> employees = dbManager.getAllEmployeesForAttendance();
        for (Employee emp : employees) {
            employeeComboBox.addItem(emp);
        }
    }
    
    /**
     * Reloads the attendance report for today's date.
     */
    private void refreshReportTab() {
        // --- THIS IS THE FIX ---
        // The method in DatabaseManager is "loadAttendanceReport"
        dbManager.loadAttendanceReport(reportTableModel, todayDate);
    }

    // =========================================================================
    // ACTION LISTENER METHODS
    // =========================================================================
    
    /**
     * Called when the "Add Employee" button is clicked.
     */
    private void addEmployee(ActionEvent e) {
        String name = nameField.getText();
        String position = positionField.getText();
        String email = emailField.getText();

        if (name.isEmpty() || position.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (dbManager.addEmployee(name, position, email)) {
            JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshEmployeeManagementTab(); // Refresh table
            refreshAttendanceTab();     // Refresh combo box
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add employee. Email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Called when the "Delete Selected Employee" button is clicked.
     */
    private void deleteEmployee(ActionEvent e) {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the ID from the (hidden) first column
        int id = (int) employeeTableModel.getValueAt(selectedRow, 0);
        String name = (String) employeeTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete " + name + "?\nThis will also delete all their attendance records.",
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (dbManager.deleteEmployee(id)) {
                JOptionPane.showMessageDialog(this, "Employee deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshEmployeeManagementTab(); // Refresh table
                refreshAttendanceTab();     // Refresh combo box
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete employee.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Called when the "Submit Attendance" button is clicked.
     */
    private void markAttendance(ActionEvent e) {
        Employee selectedEmployee = (Employee) employeeComboBox.getSelectedItem();
        String status = (String) statusComboBox.getSelectedItem();

        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (dbManager.markAttendance(selectedEmployee.getId(), todayDate, status)) {
            JOptionPane.showMessageDialog(this, "Attendance marked for " + selectedEmployee.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
            // Auto-refresh the report tab so it's up to date
            refreshReportTab();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to mark attendance.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

