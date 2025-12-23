package org.example;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new LoginFrame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}