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

// Controller for the screen that allows administrators to generate various system reports
public class GenerateReportScreenController implements Initializable {

    @FXML private ComboBox<String> reportTypeComboBox; // Dropdown for selecting report type
    @FXML private Button generateButton;               // Button to generate the selected report
    @FXML private Button backButton;                   // Button to return to admin dashboard

    // Initialize the controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the ComboBox with available report types
        reportTypeComboBox.getItems().addAll(
            "Vitals History",     // Report for patient vital signs history
            "Feedback",           // Report for patient feedback
            "Prescriptions",      // Report for medication prescriptions
            "Medical History"     // Report for patient medical history
        );
        reportTypeComboBox.setValue("Vitals History"); // Set default selection
        
        // Set up button actions
        generateButton.setOnAction(e -> handleGenerateReport());
        backButton.setOnAction(e -> handleBack());
    }
    
    // Handle the report generation process
    private void handleGenerateReport() {
        String selectedReportType = reportTypeComboBox.getValue();
        // Validate that a report type is selected
        if (selectedReportType == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a report type.");
            return;
        }
        
        // Generate the selected report using the ReportGenerator service
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