package com.myapp.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import com.myapp.backend.services.PatientService;
import com.myapp.backend.services.DoctorService;

import java.io.IOException;

public class SignupController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button signupButton;
    @FXML private ToggleGroup userTypeToggle;
    @FXML private RadioButton patientRadio;
    @FXML private RadioButton doctorRadio;
    @FXML private RadioButton adminRadio;
    @FXML private VBox specializationBox;
    @FXML private TextField specializationField;

    private PatientService patientService = new PatientService();
    private DoctorService doctorService = new DoctorService();

    @FXML
    public void initialize() {
        // Add listener to userTypeToggle to show/hide specialization field
        userTypeToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            boolean isDoctorSelected = doctorRadio.equals(newValue);
            specializationBox.setVisible(isDoctorSelected);
            specializationBox.setManaged(isDoctorSelected);
        });
    }

    private boolean validateSignup(String email, String password) {
        if (email == null || !email.contains("@") || !email.contains(".com")) {
            showAlert("Error", "Invalid email! Email must contain '@' and '.com'.");
            return false;
        }
        if (password == null || password.length() < 6) {
            showAlert("Error", "Password must be at least 6 characters long.");
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            showAlert("Error", "Password must contain at least one uppercase letter.");
            return false;
        }
        // Removed special character requirement
        return true;
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
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

    @FXML
    private void handleSignUp(ActionEvent event) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }
        // Email and password validation
        if (!validateSignup(email, password)) {
            return;
        }

        try {
            // Register based on selected user type
            RadioButton selectedType = (RadioButton) userTypeToggle.getSelectedToggle();
            boolean success = false;

            if (selectedType == patientRadio) {
                success = patientService.registerNewPatient(name, email, password);
            } else if (selectedType == doctorRadio) {
                String specialization = specializationField.getText();
                if (specialization.isEmpty()) {
                    showAlert("Error", "Specialization is required for doctors.");
                    return;
                }
                success = doctorService.registerNewDoctor(name, email, password, specialization);
            } else if (selectedType == adminRadio) {
                // Allow admin registration: append to Admin.json if email not already present, with auto-generated id
                success = registerNewAdmin(name, email, password);
            }

            if (success) {
                showAlert("Success", "Registration successful! Please log in.");
                goToLogin(event);  // Navigate back to login screen
            } else {
                showAlert("Error", "Registration failed. Email may already be in use.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred during registration: " + e.getMessage());
        }
    }

    // Add this method to allow admin registration with auto-generated id
    private boolean registerNewAdmin(String name, String email, String password) {
        try {
            java.io.File file = new java.io.File("data/Admin.json");
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.List<java.util.Map<String, Object>> admins = new java.util.ArrayList<>();
            if (file.exists() && file.length() > 0) {
                admins = mapper.readValue(file, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.util.Map<String, Object>>>() {});
                for (java.util.Map<String, Object> admin : admins) {
                    if (admin.get("email") != null && admin.get("email").toString().equalsIgnoreCase(email)) {
                        return false; // Email already in use
                    }
                }
            }
            // Generate a random UUID for admin id
            String id = java.util.UUID.randomUUID().toString();
            java.util.Map<String, Object> newAdmin = new java.util.HashMap<>();
            newAdmin.put("id", id);
            newAdmin.put("name", name);
            newAdmin.put("email", email);
            newAdmin.put("password", password);
            newAdmin.put("role", "Admin");
            admins.add(newAdmin);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, admins);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.contains("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
