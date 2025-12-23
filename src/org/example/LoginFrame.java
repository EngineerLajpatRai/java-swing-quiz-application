package org.example;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        // Initialize the database if needed
        // initializeDatabase();
        setupUI();
    }

    private void initializeDatabase() {
        DBInitializer.initializeDatabase();
    }

    private void setupUI() {
        setTitle("Quiz App - Login");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set a light background for the frame
        getContentPane().setBackground(new Color(245, 250, 255));

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(new Color(245, 250, 255));

        // Title label with icon
        JLabel titleLabel = new JLabel("QUIZ APPLICATION", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 150, 243));
        ImageIcon icon = new ImageIcon("src/resources/quiz_icon.png");
        titleLabel.setIcon(icon);
        titleLabel.setIconTextGap(10);

        // Form panel for input fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));
        formPanel.setBackground(new Color(245, 250, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username label and field
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 30));
        usernameField.setBackground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(33, 150, 243), 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        formPanel.add(usernameField, gbc);

        // Password label and field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(33, 150, 243), 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        formPanel.add(passwordField, gbc);

        // Info label
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JLabel infoLabel = new JLabel("<html><i>Try: admin/admin123, john/john123, jane/jane123</i></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(149, 165, 166));
        formPanel.add(infoLabel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 250, 255));
        JButton loginBtn = new JButton("Login");
        JButton testBtn = new JButton("Test DB");
        JButton exitBtn = new JButton("Exit");

        loginBtn.setPreferredSize(new Dimension(120, 35));
        testBtn.setPreferredSize(new Dimension(120, 35));
        exitBtn.setPreferredSize(new Dimension(120, 35));
        // Improve button visibility with darker backgrounds and bold fonts
        loginBtn.setBackground(new Color(34, 139, 34));  // Dark green
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        testBtn.setBackground(new Color(25, 25, 112));  // Dark blue
        testBtn.setForeground(Color.WHITE);
        testBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exitBtn.setBackground(new Color(139, 0, 0));  // Dark red
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        loginBtn.addActionListener(e -> login());
        testBtn.addActionListener(e -> testDatabase());
        exitBtn.addActionListener(e -> System.exit(0));

        buttonPanel.add(loginBtn);
        buttonPanel.add(testBtn);
        buttonPanel.add(exitBtn);

        // Assemble the main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void testDatabase() {
        DatabaseConnection.testConnection();
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password!",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showConnectionError();
                return;
            }

            String sql = "SELECT id, username FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("id");
                        String dbUsername = rs.getString("username");

                        JOptionPane.showMessageDialog(this,
                                "Login Successful!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);

                        SwingUtilities.invokeLater(() -> {
                            new MenuFrame(userId, dbUsername);
                            dispose();
                        });
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Invalid username or password!",
                                "Login Failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showConnectionError() {
        String message = """
            <html><b>Database Connection Failed!</b><br><br>
            Please ensure:<br>
            1. MySQL server is running<br>
            2. Database 'quizdb' exists<br>
            3. Credentials are correct in DatabaseConnection.java<br><br>
            Click 'Test DB' button to diagnose.</html>
            """;

        JOptionPane.showMessageDialog(this, message, "Connection Error", JOptionPane.ERROR_MESSAGE);
    }
}