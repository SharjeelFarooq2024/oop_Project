package com.myapp.frontend.controllers;

import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Feedback;
import com.myapp.backend.model.Patient;
import com.myapp.backend.model.VitalSign;
import com.myapp.backend.services.NotificationService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PatientDetailsController implements Initializable {

    @FXML private Label patientNameLabel;
    @FXML private Label patientAgeLabel;
    @FXML private Label patientIdLabel;
    @FXML private Label patientEmailLabel;
    
    @FXML private TextArea feedbackTextArea;
    @FXML private TextField medicationField;
    @FXML private Button submitFeedbackButton;
    @FXML private Button backButton;
    
    @FXML private ListView<String> previousFeedbackListView;
    
    private Patient currentPatient;
    private Doctor currentDoctor;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        submitFeedbackButton.setOnAction(this::handleSubmitFeedback);
        backButton.setOnAction(this::handleBack);
    }
    
    public void setPatientAndDoctor(Patient patient, Doctor doctor) {
        this.currentPatient = patient;
        this.currentDoctor = doctor;
        
        // Update the UI with patient information
        updatePatientInfo();
        loadPreviousFeedback();
    }
    
    private void updatePatientInfo() {
        if (currentPatient != null) {
            patientNameLabel.setText(currentPatient.getName());
            patientAgeLabel.setText(String.valueOf(currentPatient.getAge()));
            patientIdLabel.setText(currentPatient.getId());
            patientEmailLabel.setText(currentPatient.getEmail());
        }
    }
    
    private void loadPreviousFeedback() {
        if (currentPatient != null && currentPatient.getFeedbacks() != null) {
            ObservableList<String> feedbackItems = FXCollections.observableArrayList();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (Feedback feedback : currentPatient.getFeedbacks()) {
                String formattedTime = feedback.getTimestamp().format(formatter);
                String medicationInfo = feedback.getMedicationPrescribed() != null ? 
                        " - Medication: " + feedback.getMedicationPrescribed() : "";
                
                feedbackItems.add(formattedTime + " - Dr. " + feedback.getDoctorName() + medicationInfo +
                        "\n" + feedback.getComment());
            }
            
            previousFeedbackListView.setItems(feedbackItems);
        }
    }
    
    private void handleSubmitFeedback(ActionEvent event) {
        String feedbackText = feedbackTextArea.getText();
        String medication = medicationField.getText();
        
        if (feedbackText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Feedback cannot be empty");
            return;
        }
        
        // Add feedback to patient
        if (currentDoctor != null && currentPatient != null) {
            currentDoctor.giveFeedback(currentPatient, feedbackText, medication);
            
            // Send notification to patient
            NotificationService.sendNotification(currentPatient.getId(), 
                    "New feedback from Dr. " + currentDoctor.getName());
            
            // If patient has email, send email notification too
            if (currentPatient.getEmail() != null && !currentPatient.getEmail().isEmpty()) {
                NotificationService.sendEmailNotification(
                    currentPatient.getEmail(),
                    "New Medical Feedback",
                    "Dear " + currentPatient.getName() + ",\n\n" +
                    "Dr. " + currentDoctor.getName() + " has provided new feedback on your medical condition.\n\n" +
                    "Please log in to view the details.\n\n" +
                    "Best regards,\nHospital Management System"
                );
            }
            
            // Refresh the feedback list
            loadPreviousFeedback();
            
            // Clear the input fields
            feedbackTextArea.clear();
            medicationField.clear();
            
            showAlert(Alert.AlertType.INFORMATION, "Feedback submitted successfully");
        }
    }
    
    private void handleBack(ActionEvent event) {
        try {
            // Close this window and return to doctor dashboard
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error returning to dashboard: " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}