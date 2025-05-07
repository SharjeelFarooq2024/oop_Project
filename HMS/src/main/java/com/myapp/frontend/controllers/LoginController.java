package com.myapp.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.myapp.backend.services.SessionManager;
import com.myapp.backend.services.PatientService;
import com.myapp.backend.services.DoctorService;
import com.myapp.backend.model.Patient;
import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Admin;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private ToggleGroup userTypeToggle;
    @FXML private RadioButton patientRadio;
    @FXML private RadioButton doctorRadio;
    @FXML private RadioButton adminRadio;

    private PatientService patientService = new PatientService();
    private DoctorService doctorService = new DoctorService();

    @FXML
    public void initialize() {
        // Set patient as default login type if not already set
        if (userTypeToggle.getSelectedToggle() == null) {
            patientRadio.setSelected(true);
        }
    }

    @FXML
    private void handleSignUpLinkClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Signup.fxml"));
            Parent signUpRoot = loader.load();
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

        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();

            // Get selected user type
            RadioButton selectedType = (RadioButton) userTypeToggle.getSelectedToggle();

            if (selectedType == doctorRadio) {
                // Doctor login
                Doctor doctor = doctorService.login(email, password);
                if (doctor != null) {
                    SessionManager.setLoggedInDoctor(doctor);
                    
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DoctorDashboard.fxml"));
                    Parent dashboardRoot = loader.load();

                    DoctorDashboardController controller = loader.getController();
                    controller.setLoggedInDoctor(doctor);

                    Scene scene = new Scene(dashboardRoot);
                    stage.setScene(scene);
                    stage.setTitle("Doctor Dashboard");
                    stage.setWidth(width);
                    stage.setHeight(height);
                    stage.show();
                } else {
                    showAlert("Error", "Invalid doctor credentials.");
                }
            } else if (selectedType == adminRadio) {
                // Admin login
               // Admin admin = adminService.login(email, password);
               // if (admin != null) {
                 //   SessionManager.setLoggedInAdmin(admin);
                    
                    //FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                    //Parent dashboardRoot = loader.load();

                    //AdminDashboardController controller = loader.getController();
                   // controller.setLoggedInAdmin(admin);

                //     Scene scene = new Scene(dashboardRoot);
                //     stage.setScene(scene);
                //     stage.setTitle("Admin Dashboard");
                //     stage.setWidth(width);
                //     stage.setHeight(height);
                //     stage.show();
                // } else {
                //     showAlert("Error", "Invalid administrator credentials.");
                // }
            } else {
                // Patient login (default)
                Patient patient = patientService.login(email, password);
                if (patient != null) {
                    SessionManager.setLoggedInPatient(patient);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDashboard.fxml"));
                    Parent dashboardRoot = loader.load();

                    PatientDashboardController controller = loader.getController();
                    controller.setLoggedInPatient(patient);

                    Scene scene = new Scene(dashboardRoot);
                    stage.setScene(scene);
                    stage.setTitle("Patient Dashboard");
                    stage.setWidth(width);
                    stage.setHeight(height);
                    stage.show();
                } else {
                    showAlert("Error", "Invalid patient credentials.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Login failed: " + e.getMessage());
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
