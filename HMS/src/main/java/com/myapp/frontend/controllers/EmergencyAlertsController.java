package com.myapp.frontend.controllers;

import com.myapp.backend.model.*;
import com.myapp.backend.services.*;
import com.myapp.backend.dao.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ArrayList;

public class EmergencyAlertsController implements Initializable {
    
    @FXML private ListView<String> alertsListView;
    @FXML private ToggleButton showAllToggle;
    @FXML private ToggleButton showUnresolvedToggle;
    @FXML private Button refreshButton;
    @FXML private Button resolveButton;
    @FXML private Button contactPatientButton;
    @FXML private Button startEmergencyCallButton;
    @FXML private Button backButton;
    
    private Doctor loggedInDoctor;
    private ObservableList<String> alerts = FXCollections.observableArrayList();
    private List<EmergencyAlert> alertObjects;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup the ListView
        alertsListView.setItems(alerts);
        
        // Setup toggle buttons
        showAllToggle.setOnAction(e -> {
            showUnresolvedToggle.setSelected(!showAllToggle.isSelected());
            loadAlerts();
        });
        
        showUnresolvedToggle.setOnAction(e -> {
            showAllToggle.setSelected(!showUnresolvedToggle.isSelected());
            loadAlerts();
        });
        
        // Setup other buttons
        refreshButton.setOnAction(e -> loadAlerts());
        resolveButton.setOnAction(this::handleResolve);
        contactPatientButton.setOnAction(this::handleContactPatient);
        startEmergencyCallButton.setOnAction(this::handleEmergencyCall);
        backButton.setOnAction(this::handleBack);
    }
    
    public void setLoggedInDoctor(Doctor doctor) {
        this.loggedInDoctor = doctor;
        loadAlerts();
    }
    
    private void loadAlerts() {
        if (loggedInDoctor == null) return;
        
        alerts.clear();
        
        if (showAllToggle.isSelected()) {
            // Load all alerts for this doctor
            alertObjects = EmergencyAlertService.getDoctorAlerts(loggedInDoctor.getId());
        } else {
            // Load only unresolved alerts
            alertObjects = EmergencyAlertService.getDoctorUnresolvedAlerts(loggedInDoctor.getId());
        }
        
        // If no alerts, add sample data
        if (alertObjects == null || alertObjects.isEmpty()) {
            // Add sample alerts
            EmergencyAlert sampleAlert = new EmergencyAlert("p123", "Ali Khan", "Patient reporting severe chest pain");
            EmergencyAlert sampleAlert2 = new EmergencyAlert("p456", "Maria Garcia", "High fever and difficulty breathing");
            
            List<EmergencyAlert> newAlerts = new ArrayList<>();
            alertObjects = newAlerts;
            
            alertObjects.add(sampleAlert);
            alertObjects.add(sampleAlert2);
            
            // Add the alerts to the doctor
            ArrayList<EmergencyAlert> doctorAlerts = loggedInDoctor.getEmergencyAlerts();
            if (doctorAlerts == null) {
                doctorAlerts = new ArrayList<>();
                loggedInDoctor.setEmergencyAlerts(doctorAlerts);
            }
            doctorAlerts.add(sampleAlert);
            doctorAlerts.add(sampleAlert2);
        }
        
        // Add alert strings to the observable list
        for (EmergencyAlert alert : alertObjects) {
            String status = alert.isResolved() ? "[RESOLVED] " : "[URGENT] ";
            alerts.add(status + alert.getPatientName() + ": " + alert.getMessage());
        }
    }
    
    private void handleResolve(ActionEvent event) {
        int selectedIndex = alertsListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= alertObjects.size()) {
            showAlert(Alert.AlertType.WARNING, "Please select an alert to resolve");
            return;
        }
        
        EmergencyAlert selectedAlert = alertObjects.get(selectedIndex);
        selectedAlert.resolve();
        
        showAlert(Alert.AlertType.INFORMATION, "Alert marked as resolved");
        loadAlerts(); // Refresh the list
    }
    
    private void handleContactPatient(ActionEvent event) {
        int selectedIndex = alertsListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= alertObjects.size()) {
            showAlert(Alert.AlertType.WARNING, "Please select an alert first");
            return;
        }
        
        EmergencyAlert selectedAlert = alertObjects.get(selectedIndex);
        
        // Create a dialog to send message
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Contact Patient");
        dialog.setHeaderText("Send a message to " + selectedAlert.getPatientName());
        dialog.setContentText("Message:");
        
        dialog.showAndWait().ifPresent(message -> {
            // In a real app, we would send this message to the patient
            NotificationService.sendNotification(selectedAlert.getPatientId(), 
                    "Message from Dr. " + loggedInDoctor.getName() + ": " + message);
            
            showAlert(Alert.AlertType.INFORMATION, "Message sent to " + selectedAlert.getPatientName());
        });
    }
    
    private void handleEmergencyCall(ActionEvent event) {
        int selectedIndex = alertsListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= alertObjects.size()) {
            showAlert(Alert.AlertType.WARNING, "Please select an alert first");
            return;
        }
        
        EmergencyAlert selectedAlert = alertObjects.get(selectedIndex);
        
        try {
            // Create a temporary patient for the video call
            Patient patient = new Patient();
            patient.setId(selectedAlert.getPatientId());
            patient.setName(selectedAlert.getPatientName());
            
            // Load the video call view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VideoCall.fxml"));
            Parent videoCallRoot = loader.load();
            
            VideoCallController controller = loader.getController();
            controller.startCall(loggedInDoctor, patient);
            
            Stage stage = new Stage();
            stage.setTitle("Emergency Call with " + patient.getName());
            stage.setScene(new Scene(videoCallRoot));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error starting emergency call: " + e.getMessage());
        }
    }
    
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DoctorDashboard.fxml"));
            Parent dashboardRoot = loader.load();
            
            DoctorDashboardController controller = loader.getController();
            controller.setLoggedInDoctor(loggedInDoctor);
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.setTitle("Doctor Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error returning to dashboard");
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