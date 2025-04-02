package Employee_Management;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(1180, 550); // Keeping original frame size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar Panel (Navigation)
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(30, 30, 30)); // Dark sidebar
        sidebar.setLayout(new GridLayout(4, 1, 10, 20)); // Spaced buttons
        sidebar.setPreferredSize(new Dimension(250, getHeight())); // Keeping sidebar size

        // Sidebar Buttons
        JButton manageEmployees = new JButton("Manage Employees");
        JButton manageAttendance = new JButton("Manage Attendance");
        JButton manageSalary = new JButton("Manage Salary");
        JButton logout = new JButton("Logout");

        // Styling Buttons
        JButton[] buttons = {manageEmployees, manageAttendance, manageSalary, logout};
        for (JButton button : buttons) {
            button.setBackground(new Color(50, 50, 50));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setFocusPainted(false);
            sidebar.add(button);
        }

        // Main Panel (Expanded to Fill Space)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(245, 245, 245));

        // Dashboard Image (Resized to Fit)
        ImageIcon dashboardImage = new ImageIcon("C:\\Users\\Mahi Kulkarni\\OneDrive\\Desktop\\Java project\\dashboard.jpeg");
        Image img = dashboardImage.getImage().getScaledInstance(850, 450, Image.SCALE_SMOOTH); // Adjusted size
        JLabel imageLabel = new JLabel(new ImageIcon(img));
        imageLabel.setBounds(30, 20, 850, 450); // Adjusted positioning
        mainPanel.add(imageLabel);

        // Adding Panels to Frame
        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // ✅ Action Listeners
        manageEmployees.addActionListener(e -> {
            new ManageEmployees();
            dispose();
        });

        manageAttendance.addActionListener(e -> {
            new ManageAttendance();
            dispose();
        });

        manageSalary.addActionListener(e -> {
            new ManageSalary();
            dispose();
        });

        logout.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logged Out!");
            dispose();
            new AdminLogin();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new AdminDashboard();
    }
}
