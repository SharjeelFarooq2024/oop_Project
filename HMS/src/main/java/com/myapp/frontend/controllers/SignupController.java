package com.myapp.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.myapp.backend.services.PatientService;

import java.io.IOException;  // Import IOException to handle file-related exceptions

public class SignupController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField ageField;

    @FXML
    private Button signupButton;

    private PatientService patientService = new PatientService();

    @FXML
    private void goToLogin(ActionEvent event) {
    try {
        // Load the login page FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent loginRoot = loader.load();
        
        // Get the current stage and set the new scene (switch to login page)
        Stage stage = (Stage) signupButton.getScene().getWindow();
        Scene loginScene = new Scene(loginRoot);
        stage.setScene(loginScene);
        stage.setTitle("Login");
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error", "Could not load login page!\n" + e.getMessage());
    }
}
    @FXML
    private void handleSignUp(ActionEvent event) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        int age = Integer.parseInt(ageField.getText());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || ageField.getText().isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        // Call register method in PatientService
        patientService.registerNewPatient(name, email, password, age);

        // If registration is successful, go to login page
        try {
            // Switch to the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) signupButton.getScene().getWindow();
            Scene loginScene = new Scene(loginRoot);
            stage.setScene(loginScene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load login page!\n" + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
