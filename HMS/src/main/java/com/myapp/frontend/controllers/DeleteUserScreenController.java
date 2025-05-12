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

// Controller for the screen that allows administrators to delete users from the system
public class DeleteUserScreenController implements Initializable {

    @FXML private TextField usernameField; // Field for entering the username to delete
    @FXML private Button deleteButton;     // Button to confirm user deletion
    @FXML private Button backButton;       // Button to return to admin dashboard
    @FXML private ListView<String> userListView; // List view showing all available users

    // Initialize the controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load users into the list view for selection
        loadUsers();
        
        // Setup button actions for delete and back operations
        deleteButton.setOnAction(e -> handleDeleteUser());
        backButton.setOnAction(e -> navigateBack());
        
        // Add selection listener to populate the text field when a user is selected from the list
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                usernameField.setText(newValue.split(" \\(")[0]); // Extract username from display string
            }
        });
    }
    
    // Load all users from the system into the list view
    private void loadUsers() {
        userListView.getItems().clear();
        
        // Get all users from the UserManager
        List<AdminDashboardUser> usersList = UserManager.getUsers();
        
        // If no users exist, add default users
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