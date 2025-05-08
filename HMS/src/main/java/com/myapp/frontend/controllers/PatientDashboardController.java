package com.myapp.frontend.controllers;

import java.util.List;

import com.myapp.backend.dao.DoctorDAO;
import com.myapp.backend.model.Appointment;
import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Patient;
import com.myapp.backend.model.VitalSign;
import com.myapp.backend.services.AppointmentService;
import com.myapp.backend.services.SessionManager;
import com.myapp.backend.services.VitalSignService;

import javafx.application.Platform;
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

    // This method will be called after the login to pass the logged-in patient
    public void setLoggedInPatient(Patient patient) {
        this.loggedInPatient = patient;
        SessionManager.setLoggedInPatient(patient);  // Ensure patient is stored in session
        
        // Initialize the UI with patient data
        Platform.runLater(() -> {
            updateWelcomeMessage();
            loadAppointments();
            loadLatestVitals();
            updateVitalsDisplay();
        });
    }

    private void loadAppointments() {
        if (loggedInPatient != null) {
            // Fetch appointments for the logged-in patient
            List<Appointment> appointments = AppointmentService.getAppointmentsForPatient(loggedInPatient.getId());
            
            // Sort appointments by date and time in descending order (latest first)
            appointments.sort((a1, a2) -> {
                // First compare dates in descending order
                int dateCompare = a2.getDate().compareTo(a1.getDate());
                if (dateCompare != 0) {
                    return dateCompare;
                }
                // If dates are equal, compare times in descending order
                return a2.getTime().compareTo(a1.getTime());
            });
            
            // Clear the current list of appointments
            appointmentsVBox.getChildren().clear();
            
            DoctorDAO doctorDAO = new DoctorDAO();
            List<Doctor> allDoctors = doctorDAO.loadDoctors();
            
            // Display each appointment in the VBox
            for (Appointment appointment : appointments) {
                String doctorId = appointment.getDoctorId();
                String doctorName = "Unknown";
                
                // Find the doctor's name from the doctor list
                for (Doctor doctor : allDoctors) {
                    if (doctor.getId() != null && doctor.getId().equals(doctorId)) {
                        doctorName = doctor.getName() + " - " + doctor.getSpecialization();
                        break;
                    }
                }
                
                // Create a label for each appointment and add it to the VBox
                VBox appointmentBox = new VBox();
                appointmentBox.setSpacing(5);
                appointmentBox.getStyleClass().add("appointment-box");
                
                Text doctorText = new Text("Doctor: " + doctorName);
                Text dateText = new Text("Date: " + appointment.getDate());
                Text timeText = new Text("Time: " + appointment.getTime());
                Text statusText = new Text("Status: " + appointment.getStatus());
                Text descText = new Text("Reason: " + appointment.getDescription());
                
                appointmentBox.getChildren().addAll(doctorText, dateText, timeText, statusText, descText);
                appointmentsVBox.getChildren().add(appointmentBox);
            }
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
            // Store current window size
            double width = stage.getWidth();
            double height = stage.getHeight();
    
            // Load the UploadVitals FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UploadVitals.fxml"));
            Parent root = loader.load();
    
            // Get the controller of UploadVitals
            UploadVitalsController controller = loader.getController();
            controller.setLoggedInPatient(loggedInPatient);
    
            // Create and set the scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Upload Vitals");

            // Force a layout pass before showing
            root.applyCss();
            root.layout();

            // Preserve window size
            stage.setWidth(width);
            stage.setHeight(height);
            
            // Show the stage
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open Upload Vitals page");
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
        if (doctorId == null) return "Unknown";
        
        DoctorDAO doctorDAO = new DoctorDAO();
        List<Doctor> doctors = doctorDAO.loadDoctors();
        
        for (Doctor doctor : doctors) {
            if (doctor.getId() != null && doctor.getId().equals(doctorId)) {
                return doctor.getName();
            }
        }
        
        return "Unknown";
    }
}
