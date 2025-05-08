package com.myapp.app;

import com.myapp.backend.dao.DoctorDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize data directory and files
        initializeDataDirectory();
        
        // Create/initialize doctors
        DoctorDAO doctorDAO = new DoctorDAO();
        if (doctorDAO.loadDoctors().isEmpty()) {
            doctorDAO.createSampleDoctors();
        }
        
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        primaryStage.setTitle("Hospital Management System - Login");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    private void initializeDataDirectory() {
        try {
            // Create data directory if it doesn't exist
            File dataDir = new File(System.getProperty("user.dir") + "/data");
            if (!dataDir.exists()) {
                dataDir.mkdir();
                System.out.println("Created data directory at: " + dataDir.getAbsolutePath());
            }
            
            // Initialize empty JSON files if they don't exist
            String[] jsonFiles = {"Patients.json", "Appointments.json", "Vitals.json"};
            for (String file : jsonFiles) {
                File jsonFile = new File(dataDir, file);
                if (!jsonFile.exists()) {
                    Files.write(Paths.get(jsonFile.getPath()), "[]".getBytes());
                    System.out.println("Created empty JSON file: " + jsonFile.getPath());
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing data directory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
