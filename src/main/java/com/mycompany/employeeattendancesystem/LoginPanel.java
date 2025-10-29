package com.mycompany.employeeattendancesystem;

// Import the static color constants from MainApp for easy access
import static com.mycompany.employeeattendancesystem.MainApp.*;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.JOptionPane;

/**
 * The Login Panel.
 * This is the first "card" shown in MainApp.
 */
public class LoginPanel extends JPanel {

    private DatabaseManager dbManager;
    private Runnable onLoginSuccess; // A "callback" to tell MainApp to switch cards

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;

    public LoginPanel(DatabaseManager dbManager, Runnable onLoginSuccess) {
        this.dbManager = dbManager;
        this.onLoginSuccess = onLoginSuccess;

        initComponents();
    }

    private void initComponents() {
        // Use GridBagLayout to center the login box in the middle of the panel
        setLayout(new GridBagLayout());
        setBackground(COLOR_BACKGROUND); // Nyanza

        // --- Create the white login box ---
        JPanel loginBox = new JPanel(new GridBagLayout());
        loginBox.setBackground(COLOR_BACKGROUND_PANEL); // White
        
        // This makes the panel rounded
        loginBox.putClientProperty("FlatLaf.style", "arc: 12");

        // Add padding inside the white box
        Border outerBorder = BorderFactory.createEmptyBorder();
        Border innerBorder = new EmptyBorder(40, 40, 40, 40);
        loginBox.setBorder(new CompoundBorder(outerBorder, innerBorder));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components fill horizontally

        // 1. Title Label
        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_PRIMARY); // Cool Gray
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span 2 columns
        loginBox.add(titleLabel, gbc);

        // 2. Username Label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reset to 1 column
        gbc.anchor = GridBagConstraints.LINE_END; // Right-align label
        gbc.fill = GridBagConstraints.NONE;
        loginBox.add(userLabel, gbc);

        // 3. Username Field
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // Left-align field
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginBox.add(usernameField, gbc);

        // 4. Password Label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        loginBox.add(passLabel, gbc);

        // 5. Password Field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Inter", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginBox.add(passwordField, gbc);
        
        // Add action listener to login on "Enter" key press
        passwordField.addActionListener(this::login);

        // 6. Error Label (for bad login)
        errorLabel = new JLabel(" "); // Empty space
        errorLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        errorLabel.setForeground(COLOR_ERROR); // Red
        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginBox.add(errorLabel, gbc);

        // 7. Login Button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Inter", Font.BOLD, 14));
        loginButton.setBackground(COLOR_PRIMARY); // Cool Gray
        loginButton.setForeground(Color.WHITE);
        
        // This makes it the "default" button (often highlighted)
        loginButton.putClientProperty("FlatLaf.style", "defaultButton: true");
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; // Don't stretch button
        gbc.anchor = GridBagConstraints.CENTER;
        loginBox.add(loginButton, gbc);
        
        // Add action listener for button click
        loginButton.addActionListener(this::login);

        // --- Add the white loginBox to the main green panel ---
        // This single GridBagConstraints() centers the box.
        add(loginBox, new GridBagConstraints());
    }

    /**
     * This method is called when the Login button is clicked
     * or "Enter" is pressed in the password field.
     */
    private void login(ActionEvent e) {
        String user = usernameField.getText();
        String pass = new String(passwordField.getPassword());
        
        // --- THIS IS THE FIX ---
        // The method in DatabaseManager is "validateAdmin", not "validateAdminLogin".
        if (dbManager.validateAdmin(user, pass)) {
            // Login successful!
            errorLabel.setText(" "); // Clear error
            
            // Call the 'onLoginSuccess' function that was passed in from MainApp
            onLoginSuccess.run();
            
        } else {
            // Login failed
            errorLabel.setText("Invalid username or password.");
        }
    }
}

