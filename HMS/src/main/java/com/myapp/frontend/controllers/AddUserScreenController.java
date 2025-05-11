package com.myapp.frontend.controllers;

import com.myapp.backend.model.AdminDashboardUser;
import com.myapp.backend.services.UserManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddUserScreenController implements Initializable {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button addUserButton;
    @FXML private Button backButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize role ComboBox
        roleComboBox.getItems().addAll("Admin", "Doctor", "Patient");
        roleComboBox.setValue("Doctor"); // Default selection

        // Set button actions
        addUserButton.setOnAction(e -> handleAddUser());
        backButton.setOnAction(e -> navigateBack());
    }

    private void handleAddUser() {
        String name = nameField.getText().trim();
        String role = roleComboBox.getValue();

        if (name.isEmpty() || role == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Name and Role cannot be empty.");
            return;
        }

        AdminDashboardUser newUser = new AdminDashboardUser(name, role);
        UserManager.addUser(newUser); // Using the UserManager service

        showAlert(Alert.AlertType.INFORMATION, "Success", "User '" + name + "' added successfully as " + role + ".");
        nameField.clear(); // Clear field after adding
    }

    private void navigateBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not return to Admin Dashboard.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}