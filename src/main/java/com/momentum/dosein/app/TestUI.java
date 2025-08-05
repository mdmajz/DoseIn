package com.momentum.dosein.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class TestUI {
    
    public static void main(String[] args) {
        try {
            // Test loading the set_reminder.fxml
            FXMLLoader loader = new FXMLLoader(
                TestUI.class.getResource("/com/momentum/dosein/fxml/set_reminder.fxml")
            );
            Parent root = loader.load();
            System.out.println("✅ FXML loaded successfully!");
            System.out.println("✅ Set Reminder UI is ready!");
            
        } catch (IOException e) {
            System.err.println("❌ Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}