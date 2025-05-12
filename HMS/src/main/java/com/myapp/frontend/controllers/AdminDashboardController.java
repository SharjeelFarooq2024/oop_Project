package com.myapp.frontend.controllers;

import com.myapp.backend.dao.AdminDashboardBackend;
import com.myapp.backend.model.AdminDashboardUser;
import com.myapp.backend.services.ReportGenerator;
import com.myapp.backend.services.UserManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

// Controller for the Admin Dashboard screen
public class AdminDashboardController implements Initializable {

    // UI elements from the FXML file
    @FXML private Button viewUsersBtn;      // Button to view all users
    @FXML private Button addUserBtn;        // Button to add a new user
    @FXML private Button deleteUserBtn;     // Button to delete a user
    @FXML private Button generateReportBtn; // Button to generate reports
    @FXML private Button backToLoginBtn;    // Button to return to login screen
    @FXML private Button viewLogsBtn;       // Button to view system logs
    @FXML private Label title;              // Dashboard title label

    private Stage primaryStage; // Reference to the main application window

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up button actions
        viewUsersBtn.setOnAction(e -> showViewUsersScreen());
        addUserBtn.setOnAction(e -> showAddUserScreen());
        deleteUserBtn.setOnAction(e -> showDeleteUserScreen());
        generateReportBtn.setOnAction(e -> showGenerateReportScreen());
        viewLogsBtn.setOnAction(e -> showViewLogsScreen());
        
        if (backToLoginBtn != null) {
            backToLoginBtn.setOnAction(e -> navigateToLogin());
        }
          // Apply styles to buttons
        applyButtonStyles(viewUsersBtn);
        applyButtonStyles(addUserBtn);
        applyButtonStyles(deleteUserBtn);
        applyButtonStyles(generateReportBtn);
        applyButtonStyles(viewLogsBtn);
        if (backToLoginBtn != null) {
            applyButtonStyles(backToLoginBtn);
        }
    }
    
    // Set the main stage for the application
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    // Apply consistent styling to all dashboard buttons
    private void applyButtonStyles(Button button) {
        button.setStyle("-fx-background-color: #1e1e2f; -fx-text-fill: white; -fx-font-size: 14px;" +
                        "-fx-background-radius: 8px; -fx-padding: 10 20;");
        button.setPrefWidth(200);
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #333354; -fx-text-fill: white; -fx-font-size: 14px;" +
                                                     "-fx-background-radius: 8px; -fx-padding: 10 20;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #1e1e2f; -fx-text-fill: white; -fx-font-size: 14px;" +
                                                     "-fx-background-radius: 8px; -fx-padding: 10 20;"));
    }
      // Navigate to the View Users screen
    private void showViewUsersScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewUsers.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) viewUsersBtn.getScene().getWindow(); // Or primaryStage if viewUsersBtn is null
             if (stage == null && primaryStage != null) {
                stage = primaryStage;
            } else if (stage == null) {
                System.err.println("Error: Could not get stage for ViewUsersScreen.");
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not determine the current stage.");
                return;
            }

            stage.setScene(new Scene(root));
            stage.setTitle("View All Users");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load view users screen.");
        }
    }
    
    // Navigate to the Add User screen
    private void showAddUserScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddUser.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) addUserBtn.getScene().getWindow(); // Or primaryStage if addUserBtn is null
            if (stage == null && primaryStage != null) {
                stage = primaryStage;
            } else if (stage == null) {
                System.err.println("Error: Could not get stage for AddUserScreen.");
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not determine the current stage.");
                return;
            }
            
            stage.setScene(new Scene(root));
            stage.setTitle("Add New User");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load add user screen.");
        }
    }
      // Navigate to the Delete User screen
    private void showDeleteUserScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DeleteUser.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) deleteUserBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Delete User");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load delete user screen.");
        }
    }
    
    // Navigate to the Generate Report screen
    private void showGenerateReportScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerateReport.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) generateReportBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Generate Reports");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load report generation screen.");
        }
    }
    
    // Navigate to the View Logs screen
    private void showViewLogsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewLogs.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) viewLogsBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("System Logs");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load logs screen.");
        }
    }
      // Navigate back to the login screen
    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) backToLoginBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("HMS - Login");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to login screen.");
        }
    }
    
    // Display an alert dialog with the specified type, title, and content
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}