package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/quizdb";
    private static final String USER = "root";
    private static final String PASS = "root";

    static {
        try {
           Class.forName("com.mysql.cj.jdbc.Driver");  
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            showConnectionHelp();
            return null;
        }
    }

    private static void showConnectionHelp() {
        System.out.println("\n DATABASE SETUP REQUIRED ");
        System.out.println("==============================");
        System.out.println("1. Start MySQL server");
        System.out.println("2. Create database: CREATE DATABASE quizdb;");
        System.out.println("3. Use database: USE quizdb;");
        System.out.println("4. Import quizdb.sql from resources folder");
        System.out.println("5. Update credentials in DatabaseConnection.java if needed");
        System.out.println("==============================\n");
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                JOptionPane.showMessageDialog(null,
                        " Database connection successful!",
                        "Connection Test",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Connection failed: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}