package com.myapp.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewReportsController {
    @FXML
    private ListView<String> reportListView;
    
    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // For now, just populate with sample data
        reportListView.setItems(FXCollections.observableArrayList(
            "Check-up Report - 2025-04-01",
            "Blood Test Results - 2025-03-15",
            "X-Ray Report - 2025-02-28"
        ));

        backButton.setOnAction(this::handleBack);
    }

    private void handleBack(ActionEvent event) {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/PatientDashboard.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Patient Dashboard");
            
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}