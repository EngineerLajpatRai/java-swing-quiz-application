package org.example;


public class Question {
    private int id;
    private String category;
    private String text;
    private String[] options;
    private char correctAnswer;
    private char userAnswer;

    public Question() {
        options = new String[4];
        userAnswer = '\0';
    }

    public int getId() { return id; }
    public String getCategory() { return category; }
    public String getText() { return text; }
    public String[] getOptions() { return options; }
    public char getCorrectAnswer() { return correctAnswer; }
    public char getUserAnswer() { return userAnswer; }

    public void setId(int id) { this.id = id; }
    public void setCategory(String category) { this.category = category; }
    public void setText(String text) { this.text = text; }
    public void setOptions(String[] options) { this.options = options; }
    public void setCorrectAnswer(char correctAnswer) { this.correctAnswer = correctAnswer; }
    public void setUserAnswer(char userAnswer) { this.userAnswer = userAnswer; }

    public boolean isCorrect() {
        return userAnswer == correctAnswer;
    }
}