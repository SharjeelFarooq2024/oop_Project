package com.myapp.frontend.controllers;

import com.myapp.backend.services.ReportGenerator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GenerateReportScreenController implements Initializable {

    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private Button generateButton;
    @FXML private Button backButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the ComboBox with report types
        reportTypeComboBox.getItems().addAll(
            "Vitals History", 
            "Feedback", 
            "Prescriptions", 
            "Medical History"
        );
        reportTypeComboBox.setValue("Vitals History");
        
        // Set up button actions
        generateButton.setOnAction(e -> handleGenerateReport());
        backButton.setOnAction(e -> handleBack());
    }
    
    private void handleGenerateReport() {
        String selectedReportType = reportTypeComboBox.getValue();
        if (selectedReportType == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a report type.");
            return;
        }
        
        boolean success = ReportGenerator.generateReport(selectedReportType);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Report generated successfully and saved in the reports directory.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate report. Please check logs for details.");
        }
    }
    
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not return to Admin Dashboard.");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}