package com.myapp.frontend.controllers;

import java.io.IOException;
import java.time.LocalDateTime;

import com.myapp.backend.model.Patient;
import com.myapp.backend.model.VitalSign;
import com.myapp.backend.services.SessionManager;
import com.myapp.backend.services.VitalSignService;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class UploadVitalsController {
    private Patient loggedInPatient;

    // Method to set the logged-in patient
    public void setLoggedInPatient(Patient patient) {
        this.loggedInPatient = patient;
    }

    @FXML
    private TextField bloodPressureField;

    @FXML
    private TextField heartRateField;

    @FXML
    private TextField temperatureField;

    @FXML
    private TextField oxygenField;

    @FXML
    private Button submitVitalsButton;

    @FXML
    private Button clearVitalsButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Set up button handlers
        submitVitalsButton.setOnAction(event -> {
            try {
                handleSubmit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        clearVitalsButton.setOnAction(event -> handleClear());
        backButton.setOnAction(event -> handleBack());

        // Force layout refresh after initialization
        Platform.runLater(() -> {
            if (submitVitalsButton.getScene() != null) {
                submitVitalsButton.requestLayout();
                clearVitalsButton.requestLayout();
            }
        });
    }

    private void handleSubmit() {
        try {
            if (loggedInPatient == null) {
                showAlert("Error", "No patient logged in.");
                return;
            }

            String bloodPressure = bloodPressureField.getText().trim();
            double heartRate = Double.parseDouble(heartRateField.getText().trim());
            double temperature = Double.parseDouble(temperatureField.getText().trim());
            double oxygenLevel = Double.parseDouble(oxygenField.getText().trim());

            if (bloodPressure.isEmpty()) {
                showAlert("Validation Error", "Blood Pressure cannot be empty.");
                return;
            }

            // Create the new VitalSign object with the data
            VitalSign vitals = new VitalSign(loggedInPatient.getId(), heartRate, oxygenLevel, bloodPressure, temperature, LocalDateTime.now());

            // Add the vitals to the patient's vitalSigns array
            loggedInPatient.getVitalSigns().add(vitals);

            // Add the vitals to the service
            VitalSignService.addVitals(loggedInPatient.getId(), vitals);
            showAlert("Success", "Vitals submitted successfully.");
            clearForm();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for heart rate, temperature, and oxygen level.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Something went wrong while saving vitals.");
        }
    }

    private void handleClear() {
        clearForm();
    }

    private void handleBack() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDashboard.fxml"));
            Parent dashboardRoot = loader.load();
    
            // Pass the logged-in patient to the dashboard controller
            PatientDashboardController controller = loader.getController();
            controller.setLoggedInPatient(loggedInPatient);
    
            Scene scene = new Scene(dashboardRoot);
            stage.setScene(scene);
            stage.setTitle("Patient Dashboard");

            // Preserve window size
            stage.setWidth(width);
            stage.setHeight(height);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("ERROR", "Failed to return to dashboard.");
        }
    }

    private void clearForm() {
        bloodPressureField.clear();
        heartRateField.clear();
        temperatureField.clear();
        oxygenField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

