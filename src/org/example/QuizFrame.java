package org.example;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class QuizFrame extends JFrame {
    private int userId;
    private String username;
    private String category;
    private List<Question> questions;
    private int currentIndex = 0;
    private int score = 0;

    private JLabel questionLabel, qNumLabel, categoryLabel, timerLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup buttonGroup;
    private JButton prevBtn, nextBtn, submitBtn;
    private Timer quizTimer;
    private int timeRemaining = 600; // 10 minutes in seconds

    public QuizFrame(int userId, String username, String category) {
        this.userId = userId;
        this.username = username;
        this.category = category;

        setTitle(category.toUpperCase() + " Quiz - " + username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        questions = loadQuestions();
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No questions available for this category!",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);

            SwingUtilities.invokeLater(() -> {
                new MenuFrame(userId, username);
                dispose();
            });
            return;
        }

        setupUI();
        startTimer();
        displayQuestion();
        setVisible(true);
    }

    // Main method for testing (run with java QuizFrame)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizFrame(1, "TestUser", "java"));
    }

    private void setupUI() {
        // Create main panel with border layout for overall structure
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel for displaying quiz info like category, question number, and timer
        JPanel topPanel = new JPanel(new GridLayout(2, 3, 10, 5));

        categoryLabel = new JLabel("Category: " + category.toUpperCase(), SwingConstants.CENTER);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        categoryLabel.setForeground(Color.BLUE);

        qNumLabel = new JLabel("Question: 1/" + questions.size(), SwingConstants.CENTER);
        qNumLabel.setFont(new Font("Arial", Font.BOLD, 14));

        timerLabel = new JLabel("Time: 10:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setForeground(Color.RED);

        JLabel userLabel = new JLabel("User: " + username, SwingConstants.CENTER);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel scoreLabel = new JLabel("Score: 0/" + questions.size(), SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        topPanel.add(categoryLabel);
        topPanel.add(qNumLabel);
        topPanel.add(timerLabel);
        topPanel.add(userLabel);
        topPanel.add(scoreLabel);

        // Panel for displaying the current question
        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        questionLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Panel for multiple-choice options
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 15, 15));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        optionButtons = new JRadioButton[4];
        buttonGroup = new ButtonGroup();

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 16));
            optionButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            buttonGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }

        // Panel for navigation and submit buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        prevBtn = createStyledButton("Previous", new Color(70, 130, 180));
        nextBtn = createStyledButton("Next", new Color(60, 179, 113));
        submitBtn = createStyledButton("Submit Quiz", new Color(220, 20, 60));

        prevBtn.addActionListener(e -> previousQuestion());
        nextBtn.addActionListener(e -> nextQuestion());
        submitBtn.addActionListener(e -> submitQuiz());

        buttonPanel.add(prevBtn);
        buttonPanel.add(nextBtn);
        buttonPanel.add(submitBtn);

        // Assemble the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(questionLabel, BorderLayout.CENTER);
        mainPanel.add(optionsPanel, BorderLayout.WEST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Helper method to create styled buttons with consistent appearance
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    // Load questions from the database for the selected category
    private List<Question> loadQuestions() {
        List<Question> qList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return qList;

            String sql = "SELECT * FROM questions WHERE category = ? ORDER BY RAND() LIMIT 10";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, category);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Question q = new Question();
                        q.setId(rs.getInt("id"));
                        q.setCategory(rs.getString("category"));
                        q.setText(rs.getString("question_text"));

                        String[] options = {
                                rs.getString("option_a"),
                                rs.getString("option_b"),
                                rs.getString("option_c"),
                                rs.getString("option_d")
                        };
                        q.setOptions(options);
                        q.setCorrectAnswer(rs.getString("correct_answer").charAt(0));

                        qList.add(q);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading questions: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return qList;
    }

    // Start the countdown timer for the quiz
    private void startTimer() {
        quizTimer = new Timer(1000, e -> {
            timeRemaining--;
            if (timeRemaining <= 0) {
                quizTimer.stop();
                JOptionPane.showMessageDialog(this,
                        "Time's up! Quiz will be submitted automatically.",
                        "Time Over",
                        JOptionPane.WARNING_MESSAGE);
                submitQuiz();
            } else {
                int minutes = timeRemaining / 60;
                int seconds = timeRemaining % 60;
                timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));

                // Change color when less than 2 minutes remain
                if (timeRemaining < 120) {
                    timerLabel.setForeground(Color.RED);
                }
            }
        });
        quizTimer.start();
    }

    // Display the current question and its options
    private void displayQuestion() {
        if (currentIndex >= questions.size()) return;

        Question q = questions.get(currentIndex);
        questionLabel.setText("<html><div style='text-align: center; padding: 10px;'>" +
                (currentIndex + 1) + ". " + q.getText() + "</div></html>");

        qNumLabel.setText("Question: " + (currentIndex + 1) + "/" + questions.size());

        for (int i = 0; i < 4; i++) {
            char optionChar = (char) ('A' + i);
            String optionText = q.getOptions()[i];
            optionButtons[i].setText("<html>" + optionChar + ". " + optionText + "</html>");
            optionButtons[i].setSelected(false);

            // Restore previous answer if exists
            if (q.getUserAnswer() == optionChar) {
                optionButtons[i].setSelected(true);
            }
        }

        // Update button states
        prevBtn.setEnabled(currentIndex > 0);
        nextBtn.setEnabled(currentIndex < questions.size() - 1);
        submitBtn.setEnabled(currentIndex == questions.size() - 1);
    }

    // Save the user's selected answer for the current question
    private void saveCurrentAnswer() {
        Question q = questions.get(currentIndex);
        char selectedAnswer = '\0';

        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected()) {
                selectedAnswer = (char) ('A' + i);
                break;
            }
        }

        q.setUserAnswer(selectedAnswer);
    }

    // Navigate to the previous question
    private void previousQuestion() {
        if (currentIndex > 0) {
            saveCurrentAnswer();
            currentIndex--;
            displayQuestion();
        }
    }

    // Navigate to the next question
    private void nextQuestion() {
        saveCurrentAnswer();
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            displayQuestion();
        }
    }

    // Submit the quiz and calculate results
    private void submitQuiz() {
        quizTimer.stop();
        saveCurrentAnswer();

        // Calculate score
        score = 0;
        for (Question q : questions) {
            if (q.isCorrect()) {
                score++;
            }
        }

        // Save to database
        saveScoreToDB();

        // Show result
        SwingUtilities.invokeLater(() -> {
            new ResultFrame(userId, username, category, score, questions.size(), questions);
            dispose();
        });
    }

    // Save the quiz score to the database
    private void saveScoreToDB() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;

            String sql = """
                INSERT INTO user_scores (user_id, category, score, total_questions, correct_answers) 
                VALUES (?, ?, ?, ?, ?)
                """;

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, category);
                pstmt.setInt(3, score);
                pstmt.setInt(4, questions.size());
                pstmt.setInt(5, score);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}