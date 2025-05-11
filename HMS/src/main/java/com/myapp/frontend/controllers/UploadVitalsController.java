package com.myapp.frontend.controllers;

import java.io.IOException;
import java.time.LocalDateTime;

import com.myapp.backend.model.Patient;
import com.myapp.backend.model.VitalSign;
import com.myapp.backend.services.EmergencyAlertService;
import com.myapp.backend.services.NotificationService;
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
                showAlert("Error", "No patient is logged in.");
                return;
            }

            String bloodPressure = bloodPressureField.getText().trim();
            double heartRate = Double.parseDouble(heartRateField.getText().trim());
            double temperature = Double.parseDouble(temperatureField.getText().trim());
            double oxygenLevel = Double.parseDouble(oxygenField.getText().trim());

            if (bloodPressure.isEmpty()) {
                showAlert("Error", "Please enter blood pressure.");
                return;
            }

            // Create the new VitalSign object with the data
            VitalSign vitals = new VitalSign(loggedInPatient.getId(), heartRate, oxygenLevel, bloodPressure, temperature, LocalDateTime.now());

            // Add the vitals to the patient's vitalSigns array
            loggedInPatient.getVitalSigns().add(vitals);

            // Add the vitals to the service
            VitalSignService.addVitals(loggedInPatient.getId(), vitals);
            
            // Check if vitals are in emergency range
            if (isEmergencyVitals(vitals)) {
                // Generate emergency message
                String emergencyMessage = generateEmergencyMessage(vitals);
                
                // Create emergency alert for all doctors
                EmergencyAlertService.createEmergencyAlert(loggedInPatient, emergencyMessage);
                
                // Send email notification to the patient
                String emailSubject = "URGENT: Abnormal Vital Signs Detected";
                String emailMessage = 
                    "Dear " + loggedInPatient.getName() + ",\n\n" +
                    "Our system has detected potentially concerning vital signs in your recent submission:\n\n" +
                    "- Heart Rate: " + heartRate + " bpm\n" +
                    "- Oxygen Level: " + oxygenLevel + "%\n" +
                    "- Blood Pressure: " + bloodPressure + "\n" +
                    "- Temperature: " + temperature + "°C\n\n" +
                    "Please seek immediate medical attention if you are experiencing any concerning symptoms.\n" +
                    "Your healthcare providers have been notified of these readings.\n\n" +
                    "Regards,\nHMS Team";
                
                NotificationService.sendEmailNotification(loggedInPatient.getEmail(), emailSubject, emailMessage);
                
                // Show alert to the patient
                showAlert("Medical Alert", "Your vital signs indicate a potential emergency situation. " +
                         "Your doctors have been notified. Please seek medical attention if you are feeling unwell.");
            }
            
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
            // No need to call updateVitalsDisplay() here; setLoggedInPatient already updates vitals and chart
    
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

    /**
     * Checks if vital signs exceed emergency thresholds
     */
    private boolean isEmergencyVitals(VitalSign vitals) {
        // Check heart rate
        if (vitals.getHeartRate() < 40 || vitals.getHeartRate() > 130) {
            return true;
        }
        
        // Check oxygen level
        if (vitals.getOxygenLevel() < 90) {
            return true;
        }
        
        // Check blood pressure (assuming format is "120/80")
        try {
            String[] bpParts = vitals.getBloodPressure().split("/");
            if (bpParts.length == 2) {
                int systolic = Integer.parseInt(bpParts[0].trim());
                int diastolic = Integer.parseInt(bpParts[1].trim());
                
                if (systolic > 180 || diastolic > 120) {
                    return true;
                }
            }
        } catch (Exception e) {
            // If we can't parse the blood pressure, log error
            System.err.println("Error parsing blood pressure: " + vitals.getBloodPressure());
        }
        
        // Check temperature
        if (vitals.getTemperature() >= 39.4) {
            return true;
        }
        
        return false;
    }

    /**
     * Generates a descriptive emergency message based on which vitals are abnormal
     */
    private String generateEmergencyMessage(VitalSign vitals) {
        StringBuilder message = new StringBuilder("EMERGENCY ALERT: ");
        
        if (vitals.getHeartRate() < 40) {
            message.append("Low heart rate (").append(vitals.getHeartRate()).append(" bpm). ");
        } else if (vitals.getHeartRate() > 130) {
            message.append("High heart rate (").append(vitals.getHeartRate()).append(" bpm). ");
        }
        
        if (vitals.getOxygenLevel() < 90) {
            message.append("Low oxygen saturation (").append(vitals.getOxygenLevel()).append("%). ");
        }
        
        try {
            String[] bpParts = vitals.getBloodPressure().split("/");
            if (bpParts.length == 2) {
                int systolic = Integer.parseInt(bpParts[0].trim());
                int diastolic = Integer.parseInt(bpParts[1].trim());
                
                if (systolic > 180) {
                    message.append("High systolic pressure (").append(systolic).append(" mmHg). ");
                }
                if (diastolic > 120) {
                    message.append("High diastolic pressure (").append(diastolic).append(" mmHg). ");
                }
            }
        } catch (Exception e) {
            // Skip blood pressure message if we can't parse it
        }
        
        if (vitals.getTemperature() >= 39.4) {
            message.append("High fever (").append(vitals.getTemperature()).append("°C). ");
        }
        
        message.append("Immediate medical attention may be required.");
        
        return message.toString();
    }
}

