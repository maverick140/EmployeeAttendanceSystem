package com.mycompany.employeeattendancesystem;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * The main entry point for the application.
 * This class sets up the FlatLaf theme and the main JFrame
 * which holds the CardLayout for switching between Login and Dashboard.
 */
public class MainApp {

    // --- Your Custom Color Palette ---
    public static final Color COLOR_PRIMARY = new Color(0x7D84B2);   // Cool Gray
    public static final Color COLOR_SECONDARY = new Color(0x8AA399); // Cambridge Blue
    public static final Color COLOR_ACCENT = new Color(0x8FA6CB);    // Vista Blue
    public static final Color COLOR_SUCCESS = new Color(0xDBF4A7);   // Mindaro
    public static final Color COLOR_BACKGROUND = new Color(0xD5F9DE); // Nyanza
    public static final Color COLOR_BACKGROUND_PANEL = Color.WHITE;
    public static final Color COLOR_TEXT = new Color(0x333333);
    public static final Color COLOR_ERROR = new Color(0xE57373); // A standard light red

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;
    private DatabaseManager dbManager;

    public static void main(String[] args) {
        // Set up the modern FlatLaf Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            // --- Global UI Settings (Rounded Corners) ---
            // This sets the corner radius for almost all components
            UIManager.put("Component.arc", 12);
            UIManager.put("Button.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("TabbedPane.arc", 8);

            // Use the new window decorations (rounded corners on the window itself)
            JFrame.setDefaultLookAndFeelDecorated(true);

        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        SwingUtilities.invokeLater(() -> new MainApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        // 1. Initialize Database
        initDatabase();

        // 2. Create Main Frame
        frame = new JFrame("Employee Attendance System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(900, 700));
        frame.setLocationRelativeTo(null); // Center on screen

        // 3. Create Main Panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 4. Create Panels (the "cards")
        loginPanel = new LoginPanel(dbManager, this::showDashboard);
        // Note: DashboardPanel is created *after* login, see showDashboard()

        // 5. Add panels to the CardLayout
        mainPanel.add(loginPanel, "login");
        // We'll add the dashboard panel later

        // 6. Show the Frame
        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private void initDatabase() {
        dbManager = new DatabaseManager();
        // The line "dbManager.initializeDatabase()" was here, but it's not needed
        // because the constructor "new DatabaseManager()" already does the setup.
        // Removing it fixes the first build error.
    }

    /**
     * This method is passed to the LoginPanel.
     * When login is successful, LoginPanel calls this method.
     */
    public void showDashboard() {
        // Create the dashboard panel *only when needed*
        dashboardPanel = new DashboardPanel(dbManager);
        
        // Add it to the main panel
        mainPanel.add(dashboardPanel, "dashboard");
        
        // Switch to the dashboard card
        cardLayout.show(mainPanel, "dashboard");
    }
}

