package Employee_Management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageSalary extends JFrame {
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private JTable salaryTable;
    private DefaultTableModel tableModel;
    private JTextField ctcField, travelDeductionField, taxDeductionField;
    private JButton updateButton, backButton;

    public ManageSalary() {
        setTitle("Manage Employee Salary");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 🎨 Theme Colors
        Color bgColor = new Color(40, 40, 40);  // Dark Gray Background
        Color panelColor = new Color(55, 55, 55);  // Slightly Lighter Gray
        Color buttonColor = new Color(70, 130, 180);  // Steel Blue
        Color textColor = Color.WHITE;

        // ✅ Apply Background Colors
        getContentPane().setBackground(bgColor);

        // ✅ Emoji-Compatible Font
        Font emojiFont = new Font("Segoe UI Emoji", Font.BOLD, 16);

        // ✅ Navbar Menu with Colored Buttons
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        menuPanel.setBackground(panelColor);

        updateButton = new JButton("💰 Update Salary");
        backButton = new JButton("🔙 Back to Dashboard");

        styleButton(updateButton, buttonColor, textColor, emojiFont);
        styleButton(backButton, buttonColor, textColor, emojiFont);

        menuPanel.add(updateButton);
        menuPanel.add(backButton);
        add(menuPanel, BorderLayout.NORTH);

        // ✅ Salary Table with Dark Theme
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "CTC", "Attendance %", "Salary"}, 0);
        salaryTable = new JTable(tableModel);
        salaryTable.setRowHeight(30);
        salaryTable.setBackground(panelColor);
        salaryTable.setForeground(textColor);
        salaryTable.setGridColor(Color.GRAY);
        salaryTable.setSelectionBackground(buttonColor);
        salaryTable.setSelectionForeground(Color.BLACK);

        JScrollPane tableScrollPane = new JScrollPane(salaryTable);
        tableScrollPane.getViewport().setBackground(bgColor);
        add(tableScrollPane, BorderLayout.CENTER);
        refreshTable();
        

     // ✅ Salary Update Panel with GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(buttonColor, 2), 
            "Update Salary Details", 
            0, 0, 
            new Font("Segoe UI", Font.BOLD, 18), 
            buttonColor
        ));
        formPanel.setBackground(panelColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        ctcField = createStyledTextField1();
        travelDeductionField = createStyledTextField1();
        taxDeductionField = createStyledTextField1();

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(createStyledLabel("New CTC:", textColor, 16), gbc);
        gbc.gridx = 1; formPanel.add(ctcField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(createStyledLabel("Travel Deduction:", textColor, 16), gbc);
        gbc.gridx = 1; formPanel.add(travelDeductionField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(createStyledLabel("Tax Deduction:", textColor, 16), gbc);
        gbc.gridx = 1; formPanel.add(taxDeductionField, gbc);

        add(formPanel, BorderLayout.SOUTH);

        

        // ✅ Auto-Fill Fields on Table Row Selection
        salaryTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = salaryTable.getSelectedRow();
            if (selectedRow != -1) {
                ctcField.setText(tableModel.getValueAt(selectedRow, 2).toString()); // CTC
                travelDeductionField.setText("5000"); // Default Travel Deduction
                taxDeductionField.setText(String.valueOf(Double.parseDouble(ctcField.getText()) * 0.1)); // 10% Tax Deduction
            }
        });

        // ✅ Button Actions
        updateButton.addActionListener(e -> updateSalary());
        backButton.addActionListener(e -> {
            new AdminDashboard();
            dispose();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
    
 // 🔹 Styled TextField Method (Bigger Size)
    private JTextField createStyledTextField1() {
        JTextField field = new JTextField(14); // Increased width
        field.setBackground(Color.DARK_GRAY);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Increased Font Size
        field.setPreferredSize(new Dimension(200, 30)); // Enlarged Box
        return field;
    }

    // 🔹 Styled Label Method (Custom Font Size)
    private JLabel createStyledLabel(String text, Color fg, int fontSize) {
        JLabel label = new JLabel(text);
        label.setForeground(fg);
        label.setFont(new Font("Segoe UI", Font.BOLD, fontSize)); // Custom Font Size
        return label;
    }


    private void refreshTable() {
        tableModel.setRowCount(0);
        List<String[]> employees = employeeDAO.getAllEmployees();

        for (String[] employee : employees) {
            try {
                double ctc = Double.parseDouble(employee[6].trim());
                double attendancePercent = employee[7] != null ? Double.parseDouble(employee[7].trim()) : 0;
                double travelDeduction = 5000;
                double taxDeduction = ctc * 0.1;
                double salary = attendancePercent > 0 ? (ctc - travelDeduction - taxDeduction) * (attendancePercent / 100) : 0;

                tableModel.addRow(new String[]{employee[0], employee[1], employee[6], String.format("%.2f", attendancePercent), String.format("%.2f", salary)});
            } catch (NumberFormatException e) {
                System.err.println("Invalid CTC format for employee ID: " + employee[0] + " | CTC Value: " + employee[6]);
                JOptionPane.showMessageDialog(this, "Invalid CTC value detected. Please check database entries.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateSalary() {
        int selectedRow = salaryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            double newCTC = Double.parseDouble(ctcField.getText());
            double travelDeduction = Double.parseDouble(travelDeductionField.getText());
            double taxDeduction = Double.parseDouble(taxDeductionField.getText());
            double attendancePercent = Double.parseDouble(tableModel.getValueAt(selectedRow, 3).toString()) / 100;

            double newSalary = attendancePercent > 0 ? (newCTC - travelDeduction - taxDeduction) * attendancePercent : 0;

            if (employeeDAO.updateSalary(id, newCTC, newSalary)) {
                JOptionPane.showMessageDialog(this, "Salary Updated Successfully!");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to Update Salary!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 🔹 Styled Button Method
    private void styleButton(JButton button, Color bg, Color fg, Font font) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(font);
        button.setPreferredSize(new Dimension(200, 40));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    // 🔹 Styled TextField Method
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(12);
        field.setBackground(Color.DARK_GRAY);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return field;
    }

    // 🔹 Styled Label Method
    private JLabel createStyledLabel(String text, Color fg) {
        JLabel label = new JLabel(text);
        label.setForeground(fg);
        return label;
    }

    public static void main(String[] args) {
        new ManageSalary();
    }
}
