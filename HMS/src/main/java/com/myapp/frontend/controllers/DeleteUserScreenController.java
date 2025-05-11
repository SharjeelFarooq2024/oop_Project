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
import java.util.List;
import java.util.ResourceBundle;

public class DeleteUserScreenController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private Button deleteButton;
    @FXML private Button backButton;
    @FXML private ListView<String> userListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load users into the list view
        loadUsers();
        
        // Setup button actions
        deleteButton.setOnAction(e -> handleDeleteUser());
        backButton.setOnAction(e -> navigateBack());
        
        // Add selection listener to populate the text field when a user is selected
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                usernameField.setText(newValue.split(" \\(")[0]); // Extract username
            }
        });
    }
      private void loadUsers() {
        userListView.getItems().clear();
        
        // Get all users from the UserManager
        List<AdminDashboardUser> usersList = UserManager.getUsers();
        
        if (usersList.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Users", "There are no users in the system. Default users will be added.");
            // Initialize default users
            UserManager.initializeDefaultAdminUsers();
            // Get users again after initialization
            usersList = UserManager.getUsers();
        }
        
        for (AdminDashboardUser user : usersList) {
            userListView.getItems().add(user.getName() + " (" + user.getRole() + ")");
        }
        
        if (userListView.getItems().isEmpty()) {
            userListView.setPlaceholder(new Label("No users found. Please refresh or contact administrator."));
        }
    }
    
    private void handleDeleteUser() {
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username cannot be empty.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete user '" + username + "'?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                UserManager.deleteUser(username);
                showAlert(Alert.AlertType.INFORMATION, "Success", "User '" + username + "' deleted.");
                loadUsers(); // Refresh the list
                usernameField.clear();
            }
        });
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