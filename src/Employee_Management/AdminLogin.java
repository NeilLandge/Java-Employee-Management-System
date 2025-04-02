package Employee_Management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public AdminLogin() {
        setTitle("Admin Login");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(30, 30, 30)); // Dark background
        setLocationRelativeTo(null);

        // Panel for login form
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBounds(40, 40, 320, 220);
        loginPanel.setBackground(new Color(50, 50, 50)); // Dark grey
        loginPanel.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));

        // Username Label & Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(20, 20, 100, 25);
        usernameLabel.setForeground(Color.WHITE);
        usernameField = new JTextField();
        usernameField.setBounds(20, 45, 280, 30);
        styleTextField(usernameField);

        // Password Label & Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 85, 100, 25);
        passwordLabel.setForeground(Color.WHITE);
        passwordField = new JPasswordField();
        passwordField.setBounds(20, 110, 280, 30);
        styleTextField(passwordField);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(20, 160, 280, 35);
        loginButton.setBackground(new Color(70, 130, 180)); // Steel blue
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect for button
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(100, 149, 237)); // Lighter blue
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(70, 130, 180)); // Original blue
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                authenticateAdmin();
            }
        });

        // Adding Components to Panel
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);

        // Adding Panel to Frame
        add(loginPanel);
        setVisible(true);
    }

    // Method to style text fields
    private void styleTextField(JTextField field) {
        field.setBackground(new Color(70, 70, 70));
        field.setForeground(Color.WHITE);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void authenticateAdmin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM admin WHERE username = ? AND password = ?")) {

            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose(); // Close login window
                new AdminDashboard(); // Open dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new AdminLogin();
    }
}
