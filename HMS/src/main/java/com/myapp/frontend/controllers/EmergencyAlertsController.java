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
import java.time.format.DateTimeFormatter;

public class EmergencyAlertsController implements Initializable {
    
    @FXML private ListView<String> alertsListView;
    @FXML private ToggleButton showAllToggle;
    @FXML private ToggleButton showUnresolvedToggle;
    @FXML private Button refreshButton;
    @FXML private Button resolveButton;
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
            if (showAllToggle.isSelected()) {
                showUnresolvedToggle.setSelected(false);
                loadAlerts();
            } else if (!showUnresolvedToggle.isSelected()) {
                showAllToggle.setSelected(true);
            }
        });
        
        showUnresolvedToggle.setOnAction(e -> {
            if (showUnresolvedToggle.isSelected()) {
                showAllToggle.setSelected(false);
                loadAlerts();
            } else if (!showAllToggle.isSelected()) {
                showUnresolvedToggle.setSelected(true);
            }
        });
        
        // Setup other buttons
        refreshButton.setOnAction(e -> loadAlerts());
        resolveButton.setOnAction(this::handleResolve);
        backButton.setOnAction(this::handleBack);
    }
    
    public void setLoggedInDoctor(Doctor doctor) {
        this.loggedInDoctor = doctor;
        loadAlerts();
    }
    
    private void loadAlerts() {
        if (loggedInDoctor == null) {
            return;
        }
        
        alerts.clear();
        
        if (showAllToggle.isSelected()) {
            alertObjects = EmergencyAlertService.getDoctorAlerts(loggedInDoctor.getId());
        } else {
            alertObjects = EmergencyAlertService.getDoctorUnresolvedAlerts(loggedInDoctor.getId());
        }
        
        // If no alerts, add sample data
        if (alertObjects == null || alertObjects.isEmpty()) {
            alertObjects = new ArrayList<>();
        }
        
        // Sort alerts by timestamp (newest first)
        alertObjects.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        
        // Add alert strings to the observable list with priority formatting for vital sign emergencies
        for (EmergencyAlert alert : alertObjects) {
            String alertStatus = alert.isResolved() ? "[RESOLVED] " : "[ACTIVE] ";
            String alertType = alert.getMessage().startsWith("EMERGENCY ALERT:") ? 
                "[CRITICAL VITALS] " : "";
            
            String formattedTimestamp = alert.getTimestamp().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            String alertText = alertStatus + alertType + 
                formattedTimestamp + " - Patient: " + alert.getPatientName() + 
                "\n" + alert.getMessage();
            
            alerts.add(alertText);
        }
    }
    
    private void handleResolve(ActionEvent event) {
        int selectedIndex = alertsListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= alertObjects.size()) {
            showAlert(Alert.AlertType.WARNING, "No Alert Selected", "Please select an alert to resolve.");
            return;
        }
        
        EmergencyAlert selectedAlert = alertObjects.get(selectedIndex);
        
        // Skip if already resolved
        if (selectedAlert.isResolved()) {
            showAlert(Alert.AlertType.INFORMATION, "Already Resolved", 
                     "This alert has already been marked as resolved.");
            return;
        }
        
        // Mark as resolved in service
        EmergencyAlertService.resolveAlert(selectedAlert.getAlertId(), loggedInDoctor.getId());
        
        // Mark as resolved in local object
        selectedAlert.resolve();
        
        showAlert(Alert.AlertType.INFORMATION, "Success", "Alert marked as resolved successfully.");
        loadAlerts(); // Refresh the list
    }
    
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DoctorDashboard.fxml"));
            Parent root = loader.load();
            
            DoctorDashboardController controller = loader.getController();
            controller.setLoggedInDoctor(loggedInDoctor);
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Doctor Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not return to dashboard.");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}