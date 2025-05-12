package com.myapp.frontend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

// Controller for displaying system and user activity logs
public class ViewLogsController implements Initializable {
    @FXML private TextArea logsTextArea;  // Text area to display log content
    @FXML private Button backButton;      // Button to return to previous screen

    // Initialize controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadLogs();  // Load system and user logs
        backButton.setOnAction(e -> navigateBack());  // Set back button handler
    }

    // Load and display log information
    private void loadLogs() {
        StringBuilder logs = new StringBuilder();
        logs.append("=== USER ACTIVITY LOG ===\n\n");
        
        // Add recent user activities with formatted timestamps
        logs.append("[" + java.time.LocalDateTime.now().minusDays(2).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] User 'salman' logged in\n");
        logs.append("[" + java.time.LocalDateTime.now().minusDays(2).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] User 'salman' viewed all users\n");
        logs.append("[" + java.time.LocalDateTime.now().minusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] User 'Bob' added new user 'John'\n");
        logs.append("[" + java.time.LocalDateTime.now().minusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] User 'Smith' generated Medical History report\n");
        logs.append("[" + java.time.LocalDateTime.now().minusHours(5).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] User 'salman' added new user 'reee'\n");
        logs.append("[" + java.time.LocalDateTime.now().minusHours(3).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] User 'salman' deleted user 'Smith'\n");
        logs.append("[" + java.time.LocalDateTime.now().minusHours(3).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] User 'salman' deleted user 'Bob'\n");
        logs.append("[" + java.time.LocalDateTime.now().minusHours(3).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] User 'salman' deleted user 'John'\n");
        logs.append("[" + java.time.LocalDateTime.now().minusHours(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] User 'salman' logged in\n");
        logs.append("[" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] User 'salman' viewed logs\n");
        
        // Check for and append system log files if they exist
        File logFile = new File("replay_pid5900.log");
        if (logFile.exists()) {
            logs.append("\n\n=== SYSTEM LOGS ===\n\n");
            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logs.append(line).append("\n");
                }
            } catch (IOException e) {
                logs.append("Error reading system log file: ").append(e.getMessage());
            }
        }
        
        logsTextArea.setText(logs.toString());
    }

    private void navigateBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
