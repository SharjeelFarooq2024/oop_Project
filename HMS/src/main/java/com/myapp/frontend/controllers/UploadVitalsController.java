package com.myapp.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import com.myapp.backend.model.Patient;
import com.myapp.backend.services.PatientService; // assuming this exists
import com.myapp.backend.services.SessionManager;

public class UploadVitalsController {
    @FXML
    private TextField systolicField;
    @FXML
    private TextField diastolicField;
    @FXML
    private TextField heartRateField;
    @FXML
    private TextField temperatureField;
    @FXML
    private TextField oxygenField;
    @FXML
    private TextField respiratoryField;
    @FXML
    private TextField weightField;
    @FXML
    private TextArea notesField;
    @FXML
    private Button clearVitalsButton;
    @FXML
    private Button submitVitalsButton;
    @FXML
    private Button backButton;

    private Patient loggedInPatient; // Store logged-in patient information

    // This method allows you to pass the logged-in patient to this controller
    public void setLoggedInPatient(Patient patient) {
        this.loggedInPatient = patient;
    }

    @FXML
    public void initialize() {
        // You can skip retrieving logged-in patient here if you are passing it via `setLoggedInPatient`
        clearVitalsButton.setOnAction(this::handleClear);
        submitVitalsButton.setOnAction(this::handleSubmit);
        backButton.setOnAction(this::handleBack);
    }

    private void handleClear(ActionEvent event) {
        systolicField.clear();
        diastolicField.clear();
        heartRateField.clear();
        temperatureField.clear();
        oxygenField.clear();
        respiratoryField.clear();
        weightField.clear();
        notesField.clear();
    }

    private void handleSubmit(ActionEvent event) {
        // TODO: Add validation logic
        if (validateFields()) {
            // TODO: Save vitals to database (You may use a service or DAO for that)
            showAlert("Success", "Vitals uploaded successfully!", Alert.AlertType.INFORMATION);
            navigateBack();
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        try {
            if (!systolicField.getText().isEmpty()) {
                int systolic = Integer.parseInt(systolicField.getText());
                if (systolic < 70 || systolic > 200) {
                    errors.append("Invalid systolic pressure (70-200 mmHg)\n");
                }
            }
            if (!diastolicField.getText().isEmpty()) {
                int diastolic = Integer.parseInt(diastolicField.getText());
                if (diastolic < 40 || diastolic > 130) {
                    errors.append("Invalid diastolic pressure (40-130 mmHg)\n");
                }
            }
            if (!heartRateField.getText().isEmpty()) {
                int heartRate = Integer.parseInt(heartRateField.getText());
                if (heartRate < 40 || heartRate > 200) {
                    errors.append("Invalid heart rate (40-200 bpm)\n");
                }
            }
            if (!temperatureField.getText().isEmpty()) {
                double temperature = Double.parseDouble(temperatureField.getText());
                if (temperature < 35 || temperature > 42) {
                    errors.append("Invalid body temperature (35-42°C)\n");
                }
            }
            if (!oxygenField.getText().isEmpty()) {
                double oxygen = Double.parseDouble(oxygenField.getText());
                if (oxygen < 85 || oxygen > 100) {
                    errors.append("Invalid oxygen level (85-100%)\n");
                }
            }
            if (!respiratoryField.getText().isEmpty()) {
                int respiratory = Integer.parseInt(respiratoryField.getText());
                if (respiratory < 12 || respiratory > 30) {
                    errors.append("Invalid respiratory rate (12-30 breaths/min)\n");
                }
            }
            if (!weightField.getText().isEmpty()) {
                double weight = Double.parseDouble(weightField.getText());
                if (weight < 30 || weight > 200) {
                    errors.append("Invalid weight (30-200 kg)\n");
                }
            }
        } catch (NumberFormatException e) {
            errors.append("Please enter valid numbers for all measurements\n");
        }

        if (errors.length() > 0) {
            showAlert("Validation Error", errors.toString(), Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void handleBack(ActionEvent event) {
        navigateBack();
    }

    private void navigateBack() {
        try {
            // Retrieve logged-in patient and pass it to the dashboard
            Stage stage = (Stage) backButton.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            // Load the Patient Dashboard and pass the logged-in patient
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDashboard.fxml"));
            Parent root = loader.load();
            
            // Access PatientDashboardController to set the logged-in patient
            PatientDashboardController dashboardController = loader.getController();
            dashboardController.setLoggedInPatient(loggedInPatient);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Patient Dashboard");
            
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not return to dashboard!", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
