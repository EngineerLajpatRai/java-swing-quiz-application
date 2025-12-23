package org.example;


import java.sql.Timestamp;

public class Score {
    private int id;
    private int userId;
    private String category;
    private int score;
    private int totalQuestions;
    private Timestamp takenAt;

    public Score(int userId, String category, int score, int totalQuestions) {
        this.userId = userId;
        this.category = category;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.takenAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and setters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getCategory() { return category; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public Timestamp getTakenAt() { return takenAt; }
    public int getPercentage() {
        return totalQuestions > 0 ? (score * 100) / totalQuestions : 0;
    }

    public void setId(int id) { this.id = id; }
    public void setTakenAt(Timestamp takenAt) { this.takenAt = takenAt; }
}