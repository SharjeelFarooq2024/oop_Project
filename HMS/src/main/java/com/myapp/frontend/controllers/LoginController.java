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

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private PatientService patientService = new PatientService();

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter email and password.");
            return;
        }

        Patient patient = patientService.login(email, password);
        if (patient != null) {
            try {
                Stage stage = (Stage) loginButton.getScene().getWindow();
                double width = stage.getWidth();
                double height = stage.getHeight();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDashboard.fxml"));
                Parent dashboardRoot = loader.load();
                Scene scene = new Scene(dashboardRoot);
                
                stage.setScene(scene);
                stage.setTitle("Patient Dashboard");
                stage.setWidth(width);
                stage.setHeight(height);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();  // print error to console
                showAlert("Error", "Could not load dashboard!\n" + e.getMessage());
            }
        } else {
            showAlert("Error", "Invalid credentials.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // changed to ERROR type
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
