package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ResultFrame extends JFrame {
    private int userId;
    private String username;
    private String category;
    private int score;
    private int total;
    private List<Question> questions;

    public ResultFrame(int userId, String username, String category, int score, int total, List<Question> questions) {
        this.userId = userId;
        this.username = username;
        this.category = category;
        this.score = score;
        this.total = total;
        this.questions = questions;

        setupUI();
    }

    private void setupUI() {
        setTitle("Quiz Results - " + username);
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Result header
        JPanel headerPanel = createHeaderPanel();

        // Tabbed pane for review
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📊 Summary", createSummaryPanel());
        tabbedPane.addTab("📝 Detailed Review", createDetailedReviewPanel());
        tabbedPane.addTab("📈 Statistics", createStatisticsPanel());

        // Button panel
        JPanel buttonPanel = createButtonPanel();

        // Add components
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel congratsLabel = new JLabel("🎉 Quiz Completed! 🎉", SwingConstants.CENTER);
        congratsLabel.setFont(new Font("Arial", Font.BOLD, 28));
        congratsLabel.setForeground(new Color(0, 100, 0));

        JLabel categoryLabel = new JLabel("Category: " + category.toUpperCase(), SwingConstants.CENTER);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 20));
        categoryLabel.setForeground(Color.BLUE);

        int percentage = total > 0 ? (score * 100) / total : 0;

        JLabel scoreLabel = new JLabel("Score: " + score + "/" + total, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(new Color(139, 0, 139));

        JLabel percentLabel = new JLabel("Percentage: " + percentage + "%", SwingConstants.CENTER);
        percentLabel.setFont(new Font("Arial", Font.BOLD, 20));

        headerPanel.add(congratsLabel);
        headerPanel.add(categoryLabel);
        headerPanel.add(scoreLabel);
        headerPanel.add(percentLabel);

        return headerPanel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String summaryText = generateSummaryText();

        JTextArea textArea = new JTextArea(summaryText);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setMargin(new Insets(10, 10, 10, 10));

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDetailedReviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String reviewText = generateDetailedReview();

        JTextArea textArea = new JTextArea(reviewText);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setMargin(new Insets(10, 10, 10, 10));

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String statsText = generateStatistics();

        JTextArea textArea = new JTextArea(statsText);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setMargin(new Insets(10, 10, 10, 10));

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    private String generateSummaryText() {
        StringBuilder summary = new StringBuilder();
        summary.append("QUIZ SUMMARY\n");
        summary.append("============\n\n");

        int correct = 0, incorrect = 0, unanswered = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            if (q.getUserAnswer() == '\0') {
                unanswered++;
            } else if (q.isCorrect()) {
                correct++;
            } else {
                incorrect++;
            }
        }

        summary.append("Total Questions:  ").append(questions.size()).append("\n");
        summary.append("Correct Answers:  ").append(correct).append("\n");
        summary.append("Incorrect Answers:").append(incorrect).append("\n");
        summary.append("Unanswered:       ").append(unanswered).append("\n");
        summary.append("\nQUESTION STATUS:\n");
        summary.append("---------------\n");

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            summary.append(String.format("Q%02d: ", i + 1));

            if (q.getUserAnswer() == '\0') {
                summary.append("[✗] Not Answered");
            } else if (q.isCorrect()) {
                summary.append("[✓] Correct");
            } else {
                summary.append("[✗] Incorrect");
            }
            summary.append("\n");
        }

        return summary.toString();
    }

    private String generateDetailedReview() {
        StringBuilder review = new StringBuilder();
        review.append("DETAILED QUESTION REVIEW\n");
        review.append("========================\n\n");

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            review.append("QUESTION ").append(i + 1).append(":\n");
            review.append("═══════════\n");
            review.append(q.getText()).append("\n\n");

            for (int j = 0; j < 4; j++) {
                char optionChar = (char) ('A' + j);
                String optionText = q.getOptions()[j];

                review.append("  ").append(optionChar).append(". ").append(optionText);

                if (optionChar == q.getCorrectAnswer()) {
                    review.append("  ← CORRECT ANSWER");
                }
                if (q.getUserAnswer() == optionChar) {
                    review.append("  ← YOUR ANSWER");
                }
                review.append("\n");
            }

            review.append("\nRESULT: ");
            if (q.getUserAnswer() == '\0') {
                review.append("NOT ANSWERED");
            } else if (q.isCorrect()) {
                review.append("CORRECT! 🎉");
            } else {
                review.append("INCORRECT ✗ (Your: ").append(q.getUserAnswer())
                        .append(", Correct: ").append(q.getCorrectAnswer()).append(")");
            }
            review.append("\n\n");
            review.append("─────────────────────────────────────────────────────\n\n");
        }

        return review.toString();
    }

    private String generateStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("QUIZ STATISTICS\n");
        stats.append("===============\n\n");

        int correct = 0, incorrect = 0, unanswered = 0;
        int percentage = total > 0 ? (score * 100) / total : 0;

        for (Question q : questions) {
            if (q.getUserAnswer() == '\0') {
                unanswered++;
            } else if (q.isCorrect()) {
                correct++;
            } else {
                incorrect++;
            }
        }

        stats.append("Performance Metrics:\n");
        stats.append("───────────────────\n");
        stats.append(String.format("Accuracy:          %6.1f%%\n", (correct * 100.0) / total));
        stats.append(String.format("Completion Rate:   %6.1f%%\n", ((total - unanswered) * 100.0) / total));
        stats.append(String.format("Correct Rate:      %6.1f%%\n", (correct * 100.0) / (total - unanswered)));
        stats.append("\n");

        stats.append("Score Distribution:\n");
        stats.append("───────────────────\n");
        stats.append(String.format("Correct:           %3d/%3d\n", correct, total));
        stats.append(String.format("Incorrect:         %3d/%3d\n", incorrect, total));
        stats.append(String.format("Unanswered:        %3d/%3d\n", unanswered, total));
        stats.append("\n");

        stats.append("Performance Level:  ");
        if (percentage >= 90) stats.append("Excellent ★★★★★");
        else if (percentage >= 75) stats.append("Very Good ★★★★");
        else if (percentage >= 60) stats.append("Good ★★★");
        else if (percentage >= 50) stats.append("Average ★★");
        else stats.append("Needs Improvement ★");

        return stats.toString();
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton newQuizBtn = createStyledButton("Take Another Quiz", new Color(70, 130, 180));
        JButton viewScoresBtn = createStyledButton("View All Scores", new Color(60, 179, 113));
        JButton menuBtn = createStyledButton("Back to Menu", new Color(218, 165, 32));
        JButton exitBtn = createStyledButton("Exit", new Color(220, 20, 60));

        newQuizBtn.addActionListener(e -> takeAnotherQuiz());
        viewScoresBtn.addActionListener(e -> viewAllScores());
        menuBtn.addActionListener(e -> backToMenu());
        exitBtn.addActionListener(e -> System.exit(0));

        buttonPanel.add(newQuizBtn);
        buttonPanel.add(viewScoresBtn);
        buttonPanel.add(menuBtn);
        buttonPanel.add(exitBtn);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void takeAnotherQuiz() {
        SwingUtilities.invokeLater(() -> {
            new MenuFrame(userId, username);
            dispose();
        });
    }

    private void viewAllScores() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed!");
                return;
            }

            String sql = """
                SELECT category, score, total_questions, taken_at 
                FROM user_scores 
                WHERE user_id = ? 
                ORDER BY taken_at DESC
                """;

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    StringBuilder scores = new StringBuilder();
                    scores.append("YOUR COMPLETE QUIZ HISTORY\n");
                    scores.append("==========================\n\n");

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    boolean hasScores = false;
                    int quizCount = 0;
                    int totalScore = 0;
                    int totalQuestions = 0;

                    while (rs.next()) {
                        hasScores = true;
                        quizCount++;
                        int qScore = rs.getInt("score");
                        int qTotal = rs.getInt("total_questions");

                        totalScore += qScore;
                        totalQuestions += qTotal;

                        int percentage = qTotal > 0 ? (qScore * 100) / qTotal : 0;

                        scores.append("Quiz #").append(quizCount).append("\n");
                        scores.append("────────────\n");
                        scores.append("Category:    ").append(rs.getString("category").toUpperCase()).append("\n");
                        scores.append("Score:       ").append(qScore).append("/").append(qTotal).append("\n");
                        scores.append("Percentage:  ").append(percentage).append("%\n");
                        scores.append("Date:        ").append(sdf.format(rs.getTimestamp("taken_at"))).append("\n");
                        scores.append("\n");
                    }

                    if (!hasScores) {
                        scores.append("No quiz history found!");
                    } else {
                        scores.append("OVERALL STATISTICS:\n");
                        scores.append("───────────────────\n");
                        scores.append("Total Quizzes:      ").append(quizCount).append("\n");
                        scores.append("Overall Score:      ").append(totalScore).append("/").append(totalQuestions).append("\n");
                        scores.append("Overall Percentage: ").append(totalQuestions > 0 ? (totalScore * 100) / totalQuestions : 0).append("%\n");
                        scores.append("Average Score:      ").append(String.format("%.1f", (float) totalScore / quizCount)).append("\n");
                    }

                    JTextArea textArea = new JTextArea(scores.toString());
                    textArea.setEditable(false);
                    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(600, 450));

                    JOptionPane.showMessageDialog(this, scrollPane,
                            "Complete Quiz History", JOptionPane.PLAIN_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading scores: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void backToMenu() {
        SwingUtilities.invokeLater(() -> {
            new MenuFrame(userId, username);
            dispose();
        });
    }
}