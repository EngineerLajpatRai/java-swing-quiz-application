package org.example;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;

public class MenuFrame extends JFrame {
    private int userId;
    private String username;

    public MenuFrame(int userId, String username) {
        this.userId = userId;
        this.username = username;
        setupUI();
    }

    // Main method for testing (run with java MenuFrame)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuFrame(1, "TestUser"));
    }

    private void setupUI() {
        setTitle("Quiz Menu - Welcome " + username);
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set a clean, light background for better visibility
        getContentPane().setBackground(new Color(248, 250, 252));  // Slightly off-white for contrast

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(248, 250, 252));

        // Welcome panel with enhanced colors
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(248, 250, 252));
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));  // Slightly larger for prominence
        welcomeLabel.setForeground(new Color(25, 118, 210));  // Deeper blue for better contrast
        JLabel instructionLabel = new JLabel("Select a quiz category to begin:", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));  // Larger font for readability
        instructionLabel.setForeground(new Color(55, 71, 79));  // Darker gray for visibility
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomePanel.add(instructionLabel, BorderLayout.SOUTH);

        // Category buttons panel with vibrant, accessible colors
        JPanel categoryPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        categoryPanel.setBackground(new Color(248, 250, 252));
        String[] categories = {"Java Programming", "General Knowledge", "Mathematics"};
        String[] categoryKeys = {"java", "gk", "math"};
        Color[] categoryColors = {new Color(56, 142, 60), new Color(21, 101, 192), new Color(255, 152, 0)};  // Brighter green, blue, orange for high visibility

        for (int i = 0; i < categories.length; i++) {
            JButton categoryButton = createCategoryButton(categories[i], categoryColors[i]);
            String key = categoryKeys[i];
            categoryButton.addActionListener(e -> startQuiz(key));
            categoryPanel.add(categoryButton);
        }

        // Action buttons panel with improved colors
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(new Color(248, 250, 252));
        JButton viewScoresButton = createStyledButton("View Scores", new Color(63, 81, 181));  // Indigo blue for distinction
        JButton logoutButton = createStyledButton("Logout", new Color(211, 47, 47));  // Crimson red for alert
        viewScoresButton.addActionListener(e -> viewScores());
        logoutButton.addActionListener(e -> logout());
        actionPanel.add(viewScoresButton);
        actionPanel.add(logoutButton);

        // Assemble the main panel
        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(categoryPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JButton createCategoryButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);  // White text for contrast
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());  // Subtle 3D effect for visibility
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker().darker());  // Stronger hover effect
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);  // White text for contrast
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());  // Consistent border
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void startQuiz(String category) {
        SwingUtilities.invokeLater(() -> {
            new QuizFrame(userId, username, category);
            dispose();
        });
    }

    private void viewScores() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed!");
                return;
            }

            String sql = "SELECT category, score, total_questions, taken_at FROM user_scores WHERE user_id = ? ORDER BY taken_at DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    StringBuilder history = new StringBuilder();
                    history.append("YOUR QUIZ HISTORY\n");
                    history.append("=================\n\n");
                    boolean hasData = false;
                    int quizNumber = 1;

                    while (rs.next()) {
                        hasData = true;
                        int score = rs.getInt("score");
                        int total = rs.getInt("total_questions");
                        int percentage = total > 0 ? score * 100 / total : 0;
                        history.append("Quiz #").append(quizNumber++).append("\n");
                        history.append("Category:    ").append(rs.getString("category").toUpperCase()).append("\n");
                        history.append("Score:       ").append(score).append("/").append(total).append("\n");
                        history.append("Percentage:  ").append(percentage).append("%\n");
                        history.append("Date:        ").append(rs.getTimestamp("taken_at")).append("\n");
                        history.append("-----------------------------\n");
                    }

                    if (!hasData) {
                        history.append("No quiz history found!\nTake a quiz to see your scores here.");
                    }

                    JTextArea textArea = new JTextArea(history.toString());
                    textArea.setEditable(false);
                    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(500, 400));
                    JOptionPane.showMessageDialog(this, scrollPane, "Your Quiz History", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading scores: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                new LoginFrame();
                dispose();
            });
        }
    }
}