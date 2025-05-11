package com.myapp.frontend.controllers;

import com.myapp.backend.model.AdminDashboardUser;
import com.myapp.backend.services.UserManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ViewUsersScreenController implements Initializable {

    @FXML private TableView<AdminDashboardUser> usersTable;
    @FXML private TableColumn<AdminDashboardUser, String> nameColumn;
    @FXML private TableColumn<AdminDashboardUser, String> roleColumn;
    @FXML private Button backButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configure table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        loadUsers();

        backButton.setOnAction(e -> navigateBack());
    }

    private void loadUsers() {
        List<AdminDashboardUser> userList = UserManager.getUsers();
        ObservableList<AdminDashboardUser> observableUserList = FXCollections.observableArrayList(userList);
        usersTable.setItems(observableUserList);

        if (userList.isEmpty()) {
            usersTable.setPlaceholder(new javafx.scene.control.Label("No users found in the system."));
        }
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