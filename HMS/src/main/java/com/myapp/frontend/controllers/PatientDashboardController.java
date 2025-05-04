package com.myapp.frontend.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.backend.model.Appointment;
import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Patient;
import com.myapp.backend.model.VitalSign; // Assuming you have a Vitals model
import com.myapp.backend.services.AppointmentService;
import com.myapp.backend.services.PatientService;
import com.myapp.backend.services.SessionManager;
import com.myapp.backend.services.VitalSignService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PatientDashboardController {

    @FXML
    private VBox appointmentsVBox; // Declare the VBox for displaying appointments

    @FXML
    private Button logoutButton;

    @FXML
    private Button bookAppointmentButton;

    @FXML
    private Button viewReportsButton;

    @FXML
    private Button uploadVitalsButton;

    @FXML
    private Label welcomeLabel;  // Add a label to show the welcome message
    @FXML
    private Text bloodPressureLabel; // Add labels for vitals
    @FXML
    private Text heartRateLabel;
    @FXML
    private Text temperatureLabel;
    @FXML
    private Text latestVitalsLabel;
    @FXML
    private Text oxygenLevelLabel;

    private Patient loggedInPatient;  // This is where we store the logged-in patient
    private PatientService patientService = new PatientService();  // Assuming you have a PatientService for data management

    // This method will be called after the login to pass the logged-in patient
    public void setLoggedInPatient(Patient patient) {
        this.loggedInPatient = patient;
        updateWelcomeMessage();
        loadAppointments();
        loadLatestVitals(); // Load the latest vitals for the patient
        updateVitalsDisplay();
    }

    private void loadAppointments() {
        if (loggedInPatient != null) {
            // Fetch appointments for the logged-in patient
            List<Appointment> appointments = AppointmentService.getAppointmentsForPatient(loggedInPatient.getId());
        
            // Clear the current list of appointments before adding new ones
            appointmentsVBox.getChildren().clear();
        
            // Display each appointment in the VBox
            for (Appointment appointment : appointments) {
                String doctorId = appointment.getDoctorId();  // Fetch doctorId
                String doctorName = getDoctorNameById(doctorId);  // Fetch doctor name
       
                String appointmentTime = appointment.getTime().toString();
                String status = appointment.getStatus().toString();
        
                // Create a label for each appointment and add it to the VBox
                VBox appointmentBox = new VBox();
                appointmentBox.setSpacing(5);
                appointmentBox.getChildren().add(new Text("Doctor: " + doctorName));  // Display doctor's name
                appointmentBox.getChildren().add(new Text("Time: " + appointmentTime));
                appointmentBox.getChildren().add(new Text("Status: " + status));
                appointmentsVBox.getChildren().add(appointmentBox);
            }
        
            // Now fetch and display the latest vitals
            
        }
    }
    
    

    // Method to update the latest vitals display
    private void updateVitalsDisplay() {
        if (loggedInPatient != null) {
            VitalSign latestVitals = VitalSignService.getLatestVitals(loggedInPatient.getId());
            if (latestVitals != null) {
                latestVitalsLabel.setText("Latest Vitals:\nBlood Pressure: " + latestVitals.getBloodPressure() +
                                          "\nHeart Rate: " + latestVitals.getHeartRate() +
                                          "\nTemperature: " + latestVitals.getTemperature() +
                                          "\nOxygen Level: " + latestVitals.getOxygenLevel() +
                                          "\nTimestamp: " + latestVitals.getTimestamp());
            } else {
                latestVitalsLabel.setText("No vitals available.");
            }
        }
    }
    
    private void loadLatestVitals() {
        if (loggedInPatient != null) {
            List<VitalSign> vitalsHistory = VitalSignService.getVitalsHistory(loggedInPatient.getId());
            loggedInPatient.setVitalSigns(vitalsHistory);  // Store in patient object (optional)
    
            if (vitalsHistory != null && !vitalsHistory.isEmpty()) {
                updateVitalsDisplay(); // refresh the UI
            } else {
                latestVitalsLabel.setText("No vitals available.");
            }
        }
    }
    
    
    
    

    private void updateWelcomeMessage() {
        if (loggedInPatient != null) {
            // Update the welcome message dynamically with the patient's name
            welcomeLabel.setText("Welcome back, " + loggedInPatient.getName() + "!");
        }
    }

    @FXML
    public void initialize() {
        setupButtonHoverEffects();
        setupButtonHandlers();
    }

    private void setupButtonHoverEffects() {
        // Setup hover effects for navigation buttons
        bookAppointmentButton.setOnMouseEntered(e -> 
            bookAppointmentButton.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #1a237e; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));
        bookAppointmentButton.setOnMouseExited(e -> 
            bookAppointmentButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #424242; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));

        viewReportsButton.setOnMouseEntered(e -> 
            viewReportsButton.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #1a237e; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));
        viewReportsButton.setOnMouseExited(e -> 
            viewReportsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #424242; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));

        uploadVitalsButton.setOnMouseEntered(e -> 
            uploadVitalsButton.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #1a237e; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));
        uploadVitalsButton.setOnMouseExited(e -> 
            uploadVitalsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #424242; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));

        logoutButton.setOnMouseEntered(e -> 
            logoutButton.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #d32f2f; -fx-font-size: 14px;"));
        logoutButton.setOnMouseExited(e -> 
            logoutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-size: 14px;"));
    }

    private void setupButtonHandlers() {
        logoutButton.setOnAction(this::handleLogout);
        bookAppointmentButton.setOnAction(this::handleBookAppointment);
        viewReportsButton.setOnAction(this::handleViewReports);
        uploadVitalsButton.setOnAction(this::handleUploadVitals);
    }

    private void handleLogout(ActionEvent event) {
        try {
            // Clear the session
            SessionManager.clearSession();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleBookAppointment(ActionEvent event) {
        try {
            Stage stage = (Stage) bookAppointmentButton.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BookAppointment.fxml"));
            Parent root = loader.load();

            // Pass the logged-in patient to BookAppointmentController
            BookAppointmentController controller = loader.getController();
            controller.setLoggedInPatient(loggedInPatient);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Book Appointment");
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the appointment booking page.");
        }
    }

    private void handleUploadVitals(ActionEvent event) {
        try {
            Stage stage = (Stage) uploadVitalsButton.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
    
            // Load the UploadVitals FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UploadVitals.fxml"));
            Parent root = loader.load();
    
            // Get the controller of UploadVitals
            UploadVitalsController controller = loader.getController();
            
            // Pass the logged-in patient to the UploadVitalsController
            controller.setLoggedInPatient(loggedInPatient);
    
            // Set the scene and show the stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Upload Vitals");
    
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private void handleViewReports(ActionEvent event) {
        try {
            // Handle the "view reports" action
            System.out.println("View Reports clicked!");
            // For example, navigate to the reports page or show reports to the user
            // You can use the same structure as other methods to load the next screen
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the reports page.");
        }
    }

    private String getDoctorNameById(String doctorId) {
        try {
            // Load doctor list from JSON file
            File file = new File("data/Doctors.json");

            if (!file.exists()) {
                return "Unknown Doctor";
            }
    
            InputStream input = new FileInputStream(file);
            ObjectMapper mapper = new ObjectMapper();
            List<Doctor> doctors = Arrays.asList(mapper.readValue(input, Doctor[].class));
    
            // Find doctor with matching ID
            for (Doctor doctor : doctors) {
                if (doctor.getId().equals(doctorId)) {
                    return doctor.getName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Doctor";  // Fallback if doctor not found
    }
}
