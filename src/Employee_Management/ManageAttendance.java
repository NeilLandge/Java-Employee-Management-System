package Employee_Management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class ManageAttendance extends JFrame {
    private DefaultTableModel tableModel;
    private JTable attendanceTable;
    private JDateChooser dateChooser;
    private JButton updateAttendanceButton, viewAttendanceButton, backButton;
    private Connection conn;

    public ManageAttendance() {
        setTitle("Manage Attendance");
        setSize(1280, 650);  // Enlarged Window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(30, 30, 30));  // Darker Background

        // Database Connection
        connectToDatabase();

        // 🔹 Top Panel - Select Date Section (Larger)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        topPanel.setBackground(new Color(50, 50, 50));

        JLabel dateLabel = new JLabel("📅 Select Date:");
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18)); // Larger font for visibility

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setPreferredSize(new Dimension(150, 35)); // Enlarged Box
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        topPanel.add(dateLabel);
        topPanel.add(dateChooser);
        add(topPanel, BorderLayout.NORTH);

        // 🔹 Attendance Table with Checkboxes (Bigger)
        tableModel = new DefaultTableModel(new String[]{"ID", "Employee Name", "Status", "Present"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 3) ? Boolean.class : String.class;
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setFont(new Font("Arial", Font.PLAIN, 16)); // Bigger Font
        attendanceTable.setRowHeight(28);
        attendanceTable.setBackground(new Color(40, 40, 40)); // Darker Gray
        attendanceTable.setForeground(Color.WHITE);
        attendanceTable.setSelectionBackground(new Color(100, 100, 100));

        JScrollPane tableScrollPane = new JScrollPane(attendanceTable);
        tableScrollPane.setPreferredSize(new Dimension(1100, 350));  // Enlarged Table
        add(tableScrollPane, BorderLayout.CENTER);

        // 🔹 Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(50, 50, 50));

        updateAttendanceButton = createStyledButton("\u2714 Update Attendance");  // ✔
        viewAttendanceButton = createStyledButton("\uD83D\uDCCA View Attendance");  // 📊
        backButton = createStyledButton("\uD83D\uDD19 Back to Dashboard");  // 🔙

        viewAttendanceButton.setEnabled(false);

        buttonPanel.add(updateAttendanceButton);
        buttonPanel.add(viewAttendanceButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 🔹 Event Listeners
        dateChooser.addPropertyChangeListener("date", e -> loadAttendanceRecords());
        updateAttendanceButton.addActionListener(e -> updateAttendance());
        viewAttendanceButton.addActionListener(e -> viewEmployeeAttendance());

        attendanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && attendanceTable.getSelectedRow() != -1) {
                viewAttendanceButton.setEnabled(true);
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            new AdminDashboard();
        });

        // 🔹 Initial Load
        loadAttendanceRecords();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Object EmployeeAttendance() {
        return null;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));  // Supports Emojis
        button.setBackground(new Color(75, 110, 175));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/practice", "root", "Mahi1310kulkarni");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadAttendanceRecords() {
        if (dateChooser.getDate() == null) return;

        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(dateChooser.getDate());

        try (PreparedStatement stmt = conn.prepareStatement("SELECT e.emp_id, e.name, COALESCE(a.status, 'Absent') AS status FROM employees e LEFT JOIN attendance a ON e.emp_id = a.emp_id AND a.date = ?")) {
            stmt.setString(1, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("emp_id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("status"));
                row.add(rs.getString("status").equals("Present"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load attendance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateAttendance() {
        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please select a date!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(dateChooser.getDate());

        try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM attendance WHERE date = ?")) {
            deleteStmt.setString(1, date);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO attendance (emp_id, date, status) VALUES (?, ?, ?)")) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int empId = (int) tableModel.getValueAt(i, 0);
                boolean isPresent = (boolean) tableModel.getValueAt(i, 3);
                String status = isPresent ? "Present" : "Absent";

                insertStmt.setInt(1, empId);
                insertStmt.setString(2, date);
                insertStmt.setString(3, status);
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
            JOptionPane.showMessageDialog(this, "Attendance Updated Successfully!");
            loadAttendanceRecords();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update attendance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void viewEmployeeAttendance() {
        int selectedRow = attendanceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int empId = (int) tableModel.getValueAt(selectedRow, 0);
        String empName = (String) tableModel.getValueAt(selectedRow, 1);

        JFrame attendanceFrame = new JFrame("Attendance for " + empName);
        attendanceFrame.setSize(600, 450);
        attendanceFrame.setLayout(new BorderLayout());
        attendanceFrame.getContentPane().setBackground(new Color(30, 30, 30));

        // 🔹 Title Label
        JLabel titleLabel = new JLabel("📅 Attendance for " + empName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        attendanceFrame.add(titleLabel, BorderLayout.NORTH);

        // 🔹 Table UI
        DefaultTableModel attendanceModel = new DefaultTableModel(new String[]{"Date", "Status"}, 0);
        JTable attendanceDetailsTable = new JTable(attendanceModel);
        attendanceDetailsTable.setFont(new Font("Arial", Font.PLAIN, 16));
        attendanceDetailsTable.setRowHeight(28);
        attendanceDetailsTable.setBackground(new Color(40, 40, 40));
        attendanceDetailsTable.setForeground(Color.WHITE);
        attendanceDetailsTable.setSelectionBackground(new Color(100, 100, 100));

        JScrollPane scrollPane = new JScrollPane(attendanceDetailsTable);
        scrollPane.setPreferredSize(new Dimension(550, 300));
        attendanceFrame.add(scrollPane, BorderLayout.CENTER);

        int totalDays = 0;
        int presentDays = 0;

        try (PreparedStatement stmt = conn.prepareStatement("SELECT date, status FROM attendance WHERE emp_id = ? ORDER BY date ASC")) {
            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("date"));
                String status = rs.getString("status");
                row.add(status);
                attendanceModel.addRow(row);

                totalDays++;
                if (status.equals("Present")) {
                    presentDays++;
                }
            }

            double percentage = (totalDays > 0) ? ((double) presentDays / totalDays) * 100 : 0;
            JLabel percentageLabel = new JLabel("✅ Attendance Percentage: " + String.format("%.2f", percentage) + "%", SwingConstants.CENTER);
            percentageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            percentageLabel.setForeground(Color.WHITE);
            percentageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            attendanceFrame.add(percentageLabel, BorderLayout.SOUTH);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load employee attendance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        attendanceFrame.setLocationRelativeTo(this);
        attendanceFrame.setVisible(true);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageAttendance::new);
    }
}
