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
import com.myapp.backend.model.Patient;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private PatientService patientService = new PatientService();

    @FXML
    private void handleSignUpLinkClick(ActionEvent event) {
        try {
            // Load the sign-up page FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Signup.fxml"));
            Parent signUpRoot = loader.load();
            
            // Get the current stage and set the new scene
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene signUpScene = new Scene(signUpRoot);
            stage.setScene(signUpScene);
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load sign-up page!\n" + e.getMessage());
        }
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter email and password.");
            return;
        }

        // Attempt to log in the patient
        Patient patient = patientService.login(email, password);
        if (patient != null) {
            try {
                // Get the current stage and remember its size
                Stage stage = (Stage) loginButton.getScene().getWindow();
                double width = stage.getWidth();
                double height = stage.getHeight();

                // Load the PatientDashboard.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDashboard.fxml"));
                Parent dashboardRoot = loader.load();

                // Get the PatientDashboardController and set the logged-in patient
                PatientDashboardController controller = loader.getController();
                controller.setLoggedInPatient(patient); // Pass the logged-in patient

                // Set the scene and keep the current stage size
                Scene scene = new Scene(dashboardRoot);
                stage.setScene(scene);
                stage.setTitle("Patient Dashboard");
                stage.setWidth(width);
                stage.setHeight(height);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Could not load dashboard!\n" + e.getMessage());
            }
        } else {
            showAlert("Error", "Invalid credentials.");
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
