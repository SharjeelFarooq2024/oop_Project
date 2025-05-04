package com.myapp.frontend.controllers;

import java.io.IOException;
import java.time.LocalDateTime;

import com.myapp.backend.model.Patient;
import com.myapp.backend.model.VitalSign;
import com.myapp.backend.services.SessionManager;
import com.myapp.backend.services.VitalSignService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Parent;
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
        System.out.println("submitVitalsButton: " + submitVitalsButton);

        // Button actions
        submitVitalsButton.setOnAction(event -> {
            try {
                handleSubmit();
            } catch (Exception e) {
                e.printStackTrace();  // Add logging for debugging
            }
        });

        clearVitalsButton.setOnAction(event -> handleClear());
        backButton.setOnAction(event -> handleBack());
    }

    private void handleSubmit() {
        try {
            String bloodPressure = bloodPressureField.getText().trim();
            double heartRate = Double.parseDouble(heartRateField.getText().trim());
            double temperature = Double.parseDouble(temperatureField.getText().trim());
            double oxygenLevel = Double.parseDouble(oxygenField.getText().trim());

            if (bloodPressure.isEmpty()) {
                showAlert("Validation Error", "Blood Pressure cannot be empty.");
                return;
            }

            // Create the new VitalSign object with the data
            VitalSign vitals = new VitalSign(SessionManager.getLoggedInPatient().getId(), heartRate, oxygenLevel, bloodPressure, temperature, LocalDateTime.now());

            // Add the vitals to the service
            VitalSignService.addVitals(SessionManager.getLoggedInPatient().getId(), vitals);
            showAlert("Success", "Vitals submitted successfully.");
            clearForm();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for heart rate, temperature, and oxygen level.");
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace to see the detailed error
            showAlert("Error", "Something went wrong while saving vitals.");
        }
    }

    private void handleClear() {
        clearForm();
    }

    private void handleBack() {
        try {
            // Navigate to the Dashboard screen when "Back" is clicked
            Stage stage = (Stage) backButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/myapp/frontend/views/Dashboard.fxml")); // Path to the Dashboard FXML
            Parent root = loader.load();
            Scene dashboardScene = new Scene(root);
            stage.setScene(dashboardScene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the dashboard.");
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
