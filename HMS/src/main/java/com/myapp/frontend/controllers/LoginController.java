package com.myapp.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import com.myapp.backend.services.PatientService;
import com.myapp.backend.model.Patient;

public class LoginController
{

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private PatientService patientService = new PatientService(); // create service object

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter email and password.");
            return;
        }

        Patient patient = patientService.login(email, password); // Assuming you have a login() method
        if (patient != null) {
            showAlert("Success", "Login successful!");
            // TODO: open dashboard.fxml screen here
        } else {
            showAlert("Error", "Invalid credentials.");
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


