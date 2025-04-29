package com.myapp.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BookAppointmentController {
    @FXML
    private TextField doctorNameField;

    @FXML
    private DatePicker appointmentDatePicker;

    @FXML
    private Button confirmButton;
    
    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        confirmButton.setOnAction(this::handleConfirmAppointment);
        backButton.setOnAction(this::handleBack);
    }

    private void handleBack(ActionEvent event) {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/PatientDashboard.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Patient Dashboard");
            
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not return to dashboard!");
        }
    }

    private void handleConfirmAppointment(ActionEvent event) {
        String doctorName = doctorNameField.getText();
        if (doctorName.isEmpty() || appointmentDatePicker.getValue() == null) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        // For now, just show success message
        showAlert("Success", "Appointment booked successfully!");
        
        // Return to dashboard
        try {
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/PatientDashboard.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Patient Dashboard");
            
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not return to dashboard!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}