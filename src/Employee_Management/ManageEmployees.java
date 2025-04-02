package Employee_Management;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageEmployees extends JFrame {
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, departmentField, emailField, phoneField, addressField, ctcField;
    private JButton addButton, updateButton, deleteButton, backButton;

    public ManageEmployees() {
        setTitle("Manage Employees");
        setSize(1180, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 🔹 Dark Mode Theme
        Color bgColor = new Color(40, 40, 40);  // Dark Gray Background
        Color panelColor = new Color(55, 55, 55);  // Slightly Lighter Gray
        Color buttonColor = new Color(70, 130, 180);  // Steel Blue
        Color textColor = Color.WHITE;

        getContentPane().setBackground(bgColor);
        Font emojiFont;
        try {
            emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 15);  // Windows users
        } catch (Exception e) {
            emojiFont = new Font("SansSerif", Font.PLAIN, 15);  // Fallback font
        }

        // 🔹 Navigation Bar with Buttons
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(panelColor);

        Font buttonFont = new Font("Segoe UI Emoji", Font.BOLD, 16);
        Dimension buttonSize = new Dimension(100, 40);  // Bigger buttons

        addButton = createStyledButton("➕ Add Employee", buttonColor, textColor);
        addButton.setFont(buttonFont);
        addButton.setPreferredSize(buttonSize);

        updateButton = createStyledButton("✏️ Update Employee", buttonColor, textColor);
        updateButton.setFont(buttonFont);
        updateButton.setPreferredSize(buttonSize);

        deleteButton = createStyledButton("🗑 Delete Employee", buttonColor, textColor);
        deleteButton.setFont(buttonFont);
        deleteButton.setPreferredSize(buttonSize);

        backButton = createStyledButton("🔙 Back", buttonColor, textColor);
        backButton.setFont(buttonFont);
        backButton.setPreferredSize(buttonSize);


        menuBar.add(addButton);
        menuBar.add(updateButton);
        menuBar.add(deleteButton);
        menuBar.add(backButton);
        setJMenuBar(menuBar);
        
        addButton.setMargin(new Insets(20, 20, 20, 20));  // Top, Left, Bottom, Right
        updateButton.setMargin(new Insets(20, 20, 20, 20));
        deleteButton.setMargin(new Insets(20, 20, 20, 20));
        backButton.setMargin(new Insets(20, 20, 20, 20));


        // 🔹 Table Styling
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Department", "Email", "Phone", "Address", "CTC"}, 0);
        employeeTable = new JTable(tableModel);
        employeeTable.setFillsViewportHeight(true);
        employeeTable.setBackground(panelColor);
        employeeTable.setForeground(textColor);
        employeeTable.setFont(new Font("Arial", Font.PLAIN, 15));
        employeeTable.setRowHeight(25);

        refreshTable();

        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
        tableScrollPane.getViewport().setBackground(panelColor);
        add(tableScrollPane, BorderLayout.CENTER);

        // 🔹 Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(panelColor);
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Employee Details"));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        Font fieldFont = new Font("Arial", Font.PLAIN, 16);

        nameField = createStyledTextField(fieldFont);
        departmentField = createStyledTextField(fieldFont);
        emailField = createStyledTextField(fieldFont);
        phoneField = createStyledTextField(fieldFont);
        addressField = createStyledTextField(fieldFont);
        ctcField = createStyledTextField(fieldFont);

        formPanel.add(createStyledLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(createStyledLabel("Department:"));
        formPanel.add(departmentField);
        formPanel.add(createStyledLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(createStyledLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(createStyledLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(createStyledLabel("CTC:"));
        formPanel.add(ctcField);

        add(formPanel, BorderLayout.SOUTH);

        // 🔹 Auto-Fill on Row Selection
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow != -1) {
                nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                departmentField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                emailField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                phoneField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                addressField.setText(tableModel.getValueAt(selectedRow, 5).toString());
                ctcField.setText(tableModel.getValueAt(selectedRow, 6).toString());
            }
        });

        // 🔹 Button Actions
        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        backButton.addActionListener(e -> {
            new AdminDashboard();
            dispose();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color fgColor, Font font) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50)));
        return button;
    }


    private void refreshTable() {
        tableModel.setRowCount(0);
        List<String[]> employees = employeeDAO.getAllEmployees();
        for (String[] employee : employees) {
            tableModel.addRow(employee);
        }
    }

    private void addEmployee() {
        if (!validateFields()) return;

        String name = nameField.getText().trim();
        String department = departmentField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String ctc = ctcField.getText().trim();

        if (employeeDAO.addEmployee(name, department, email, phone, address, ctc)) {
            JOptionPane.showMessageDialog(this, "Employee Added Successfully!");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to Add Employee!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validateFields()) return;

        int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String name = nameField.getText().trim();
        String department = departmentField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String ctc = ctcField.getText().trim();

        if (employeeDAO.updateEmployee(id, name, department, email, phone, address, ctc)) {
            JOptionPane.showMessageDialog(this, "Employee Updated Successfully!");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to Update Employee!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

        if (employeeDAO.deleteEmployee(id)) {
            JOptionPane.showMessageDialog(this, "Employee Deleted Successfully!");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to Delete Employee!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateFields() {
    	String name = nameField.getText().trim();
        String department = departmentField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String ctc = ctcField.getText().trim();
        
        if (name.isEmpty() || department.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || ctc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Email must contain '@' character!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // 🔹 Helper Methods for Styling
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    private JTextField createStyledTextField(Font font) {
        JTextField field = new JTextField();
        field.setFont(font);
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        return button;
    }

    public static void main(String[] args) {
        new ManageEmployees();
    }
}
