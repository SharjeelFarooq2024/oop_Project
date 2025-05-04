package com.myapp.app;

import com.myapp.backend.dao.DoctorDAO;
import com.myapp.backend.model.Doctor;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize data directory and files
        initializeDataDirectory();
        
        // Ensure doctors exist
        createDoctorsIfNeeded();
        
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml")); // path to fxml
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
            String[] jsonFiles = {"Patients.json", "Appointments.json"};
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
    
    private void createDoctorsIfNeeded() {
        try {
            File doctorsFile = new File(System.getProperty("user.dir") + "/data/Doctors.json");
            
            // Create new doctors directly here to ensure they exist
            if (!doctorsFile.exists() || doctorsFile.length() == 0) {
                System.out.println("Creating doctors file at: " + doctorsFile.getAbsolutePath());
                
                List<Doctor> doctors = new ArrayList<>();
                doctors.add(new Doctor("Dr. John Smith", "john@hospital.com", "password", "Cardiology"));
                doctors.add(new Doctor("Dr. Sarah Johnson", "sarah@hospital.com", "password", "Neurology"));
                doctors.add(new Doctor("Dr. Michael Chen", "michael@hospital.com", "password", "Orthopedics"));
                
                // Ensure directory exists
                doctorsFile.getParentFile().mkdirs();
                
                // Write directly using ObjectMapper
                ObjectMapper mapper = new ObjectMapper();
                mapper.writerWithDefaultPrettyPrinter().writeValue(doctorsFile, doctors);
                
                System.out.println("Successfully created doctors data with " + doctors.size() + " doctors");
            } else {
                System.out.println("Doctors file already exists at: " + doctorsFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error creating doctors data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
