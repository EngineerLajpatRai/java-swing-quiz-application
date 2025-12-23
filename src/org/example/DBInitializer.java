package org.example;

import java.sql.*;

public class DBInitializer {
    public static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println(" Cannot connect to database. Please check MySQL is running.");
                return;
            }

            System.out.println(" Connected to database successfully!");
            System.out.println("Checking database structure...");

            // Simply verify tables exist and have basic structure
            verifyTables(conn);

            System.out.println(" Database check completed!");

        } catch (SQLException e) {
            System.err.println(" Database initialization failed: " + e.getMessage());
            System.err.println("\n IMPORTANT: Please run the following steps:");
            System.err.println("1. Start MySQL server");
            System.err.println("2. Execute: CREATE DATABASE IF NOT EXISTS quizdb;");
            System.err.println("3. Import the quizdb.sql file");
            System.err.println("4. Check credentials in DatabaseConnection.java");
        }
    }

    private static void verifyTables(Connection conn) throws SQLException {
        String[] tables = {"users", "quizzes", "questions", "user_scores"};
        DatabaseMetaData meta = conn.getMetaData();

        for (String table : tables) {
            try (ResultSet rs = meta.getTables(null, null, table, null)) {
                if (rs.next()) {
                    System.out.println(" Table '" + table + "' exists");
                } else {
                    System.err.println(" Table '" + table + "' is missing!");
                    System.err.println("   Please run the quizdb.sql script to create the database.");
                }
            }
        }

        // Check if there are any users
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(" Users in database: " + count);
            }
        }

        // Check if there are any questions
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM questions")) {
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Questions in database: " + count);
            }
        }
    }
}