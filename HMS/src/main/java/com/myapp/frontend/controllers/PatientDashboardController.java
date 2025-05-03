package com.myapp.frontend.controllers;

import com.myapp.backend.model.Appointment;
import com.myapp.backend.model.Patient;
import com.myapp.backend.services.AppointmentService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PatientDashboardController {

    @FXML
    private Button logoutButton;

    @FXML
    private Button bookAppointmentButton;

    @FXML
    private Button viewReportsButton;

    @FXML
    private Button uploadVitalsButton;

    @FXML
    private Label welcomeLabel;  // Add a label to show the welcome message

    private Patient loggedInPatient;  // This is where we store the logged-in patient

    // This method will be called after the login to pass the logged-in patient
    public void setLoggedInPatient(Patient patient) {
        this.loggedInPatient = patient;
        updateWelcomeMessage();
    }

    @FXML
    public void initialize() {
        setupButtonHoverEffects();
        setupButtonHandlers();
    }

    private void updateWelcomeMessage() {
        if (loggedInPatient != null) {
            // Update the welcome message dynamically with the patient's name
            welcomeLabel.setText("Welcome back, " + loggedInPatient.getName() + "!");
        }
    }

    private void setupButtonHoverEffects() {
        // Setup hover effects for navigation buttons
        bookAppointmentButton.setOnMouseEntered(e -> 
            bookAppointmentButton.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #1a237e; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));
        bookAppointmentButton.setOnMouseExited(e -> 
            bookAppointmentButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #424242; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));

        viewReportsButton.setOnMouseEntered(e -> 
            viewReportsButton.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #1a237e; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));
        viewReportsButton.setOnMouseExited(e -> 
            viewReportsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #424242; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));

        uploadVitalsButton.setOnMouseEntered(e -> 
            uploadVitalsButton.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #1a237e; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));
        uploadVitalsButton.setOnMouseExited(e -> 
            uploadVitalsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #424242; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));

        logoutButton.setOnMouseEntered(e -> 
            logoutButton.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #d32f2f; -fx-font-size: 14px;"));
        logoutButton.setOnMouseExited(e -> 
            logoutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-size: 14px;"));
    }

    private void setupButtonHandlers() {
        logoutButton.setOnAction(this::handleLogout);
        bookAppointmentButton.setOnAction(this::handleBookAppointment);
        viewReportsButton.setOnAction(this::handleViewReports);
        uploadVitalsButton.setOnAction(this::handleUploadVitals);
    }

    private void handleLogout(ActionEvent event) {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

private void handleBookAppointment(ActionEvent event) {
    try {
        Stage stage = (Stage) bookAppointmentButton.getScene().getWindow();
        double width = stage.getWidth();
        double height = stage.getHeight();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BookAppointment.fxml"));
        Parent root = loader.load();

        // Pass the logged-in patient to BookAppointmentController
        BookAppointmentController controller = loader.getController();
        controller.setLoggedInPatient(loggedInPatient);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Book Appointment");
        stage.setWidth(width);
        stage.setHeight(height);
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Error", "Unable to load the appointment booking page.");
    }
}


    

    private void handleViewReports(ActionEvent event) {
        try {
            Stage stage = (Stage) viewReportsButton.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/ViewReports.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("View Reports");
            
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleUploadVitals(ActionEvent event) {
        try {
            Stage stage = (Stage) uploadVitalsButton.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/UploadVitals.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Upload Vitals");
            
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
