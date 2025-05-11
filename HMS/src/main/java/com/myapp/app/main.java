package com.myapp.app;

import com.myapp.backend.dao.DoctorDAO;
import com.myapp.backend.services.UserManager; // Import UserManager
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
        primaryStage.setScene(new Scene(root, 800, 600)); // Adjusted size for better view
        primaryStage.show();
    }

    private void initializeDataDirectory() {
        try {
            // Create data directory if it doesn't exist
            File dataDir = new File(System.getProperty("user.dir") + File.separator + "data");
            if (!dataDir.exists()) {
                dataDir.mkdirs(); // Use mkdirs to create parent directories if needed
                System.out.println("Created data directory at: " + dataDir.getAbsolutePath());
            }
            
            // Initialize empty JSON files if they don't exist for general data
            String[] jsonFiles = {"Patients.json", "Appointments.json", "Vitals.json", "Doctors.json"}; // Added Doctors.json for consistency
            for (String fileName : jsonFiles) {
                File jsonFile = new File(dataDir, fileName);
                if (!jsonFile.exists()) {
                    // Default to empty JSON array "[]" for list-based JSON files
                    // or empty JSON object "{}" if appropriate for that file's structure.
                    // For AdminUsers.json, it's handled by UserManager.initializeDefaultAdminUsers
                    if (!fileName.equals("AdminUsers.json")) { // AdminUsers.json is handled separately
                         Files.write(Paths.get(jsonFile.getPath()), "[]".getBytes());
                         System.out.println("Created empty JSON file: " + jsonFile.getPath());
                    }
                }
            }

            // Initialize AdminUsers.json with default users if it's empty or doesn't exist
            UserManager.initializeDefaultAdminUsers();

        } catch (Exception e) {
            System.err.println("Error initializing data directory or files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
