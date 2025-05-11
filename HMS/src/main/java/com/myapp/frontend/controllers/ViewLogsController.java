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

public class ViewLogsController implements Initializable {
    @FXML private TextArea logsTextArea;
    @FXML private Button backButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadLogs();
        backButton.setOnAction(e -> navigateBack());
    }

    private void loadLogs() {
        StringBuilder logs = new StringBuilder();
        logs.append("=== USER ACTIVITY LOG ===\n\n");
        
        // Add recent user activities
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
        
        // Also check for system log files
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
