package com.myapp.frontend.controllers;

import javafx.scene.control.Alert;

public class RegisterController {
    // Example signup method (replace with your actual method signature)
    public boolean validateSignup(String email, String password) {
        // Email validation
        if (email == null || !email.contains("@") || !email.contains(".com")) {
            showAlert("Invalid email! Email must contain '@' and '.com'.");
            return false;
        }
        // Password validation
        if (password == null || password.length() < 6) {
            showAlert("Password must be at least 6 characters long.");
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            showAlert("Password must contain at least one uppercase letter.");
            return false;
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?].*")) {
            showAlert("Password must contain at least one special character.");
            return false;
        }
        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
