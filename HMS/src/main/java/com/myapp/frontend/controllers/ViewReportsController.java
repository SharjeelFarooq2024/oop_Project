package com.myapp.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.myapp.backend.services.SessionManager;
import com.myapp.backend.model.Patient;

public class ViewReportsController {
    @FXML
    private ListView<String> reportListView;
    
    @FXML
    private Button backButton;

    private Patient loggedInPatient;

    @FXML
    public void initialize() {
        // Retrieve logged-in patient from SessionManager
        loggedInPatient = SessionManager.getLoggedInPatient();

        // For now, just populate with sample data
        reportListView.setItems(FXCollections.observableArrayList(
            "Check-up Report - 2025-04-01",
            "Blood Test Results - 2025-03-15",
            "X-Ray Report - 2025-02-28"
        ));

        backButton.setOnAction(this::handleBack);
    }

    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Pass the logged-in patient to the PatientDashboardController
            PatientDashboardController controller = loader.getController();
            controller.setLoggedInPatient(loggedInPatient); // Ensure this is correctly set

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.setTitle("Patient Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to return to dashboard.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
