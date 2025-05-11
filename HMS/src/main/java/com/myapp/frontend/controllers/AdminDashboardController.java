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

public class AdminDashboardController implements Initializable {

    @FXML private Button viewUsersBtn;
    @FXML private Button addUserBtn;
    @FXML private Button deleteUserBtn;
    @FXML private Button generateReportBtn;
    @FXML private Button backToLoginBtn;
    @FXML private Label title;

    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up button actions
        viewUsersBtn.setOnAction(e -> showViewUsersScreen());
        addUserBtn.setOnAction(e -> showAddUserScreen());
        deleteUserBtn.setOnAction(e -> showDeleteUserScreen());
        generateReportBtn.setOnAction(e -> showGenerateReportScreen());
        
        if (backToLoginBtn != null) {
            backToLoginBtn.setOnAction(e -> navigateToLogin());
        }
        
        // Apply styles to buttons
        applyButtonStyles(viewUsersBtn);
        applyButtonStyles(addUserBtn);
        applyButtonStyles(deleteUserBtn);
        applyButtonStyles(generateReportBtn);
        if (backToLoginBtn != null) {
            applyButtonStyles(backToLoginBtn);
        }
    }
    
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    private void applyButtonStyles(Button button) {
        button.setStyle("-fx-background-color: #1e1e2f; -fx-text-fill: white; -fx-font-size: 14px;" +
                        "-fx-background-radius: 8px; -fx-padding: 10 20;");
        button.setPrefWidth(200);
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #333354; -fx-text-fill: white; -fx-font-size: 14px;" +
                                                     "-fx-background-radius: 8px; -fx-padding: 10 20;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #1e1e2f; -fx-text-fill: white; -fx-font-size: 14px;" +
                                                     "-fx-background-radius: 8px; -fx-padding: 10 20;"));
    }
    
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
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}