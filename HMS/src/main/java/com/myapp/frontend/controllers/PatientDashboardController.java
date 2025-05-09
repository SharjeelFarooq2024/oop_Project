package com.myapp.frontend.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Add these imports at the top of the file with your other imports
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

import com.myapp.backend.dao.DoctorDAO;
import com.myapp.backend.model.Appointment;
import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Patient;
import com.myapp.backend.model.VitalSign;
import com.myapp.backend.services.AppointmentService;
import com.myapp.backend.services.SessionManager;
import com.myapp.backend.services.VitalSignService;

// Add these imports at the top of the file if not already present
import com.myapp.backend.services.EmergencyAlertService;
import com.myapp.backend.services.NotificationService;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextInputDialog;
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

    @FXML 
    private Button chatButton; // Add this to your existing FXML button declarations
    
    @FXML
    private Button videoCallButton; // Button for video calls

    @FXML private Button panicButton; // Add this field with other FXML button declarations

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
            // Clear existing appointments
            appointmentsVBox.getChildren().clear();
            
            // Add a header for appointments
            Text headerText = new Text("Your Appointments");
            headerText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            appointmentsVBox.getChildren().add(headerText);
            
            // Fetch appointments for the logged-in patient
            List<Appointment> appointments = AppointmentService.getAppointmentsForPatient(loggedInPatient.getId());
            
            // Already sorted in descending order by AppointmentService
            
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
                
                // Create a styled box for each appointment
                VBox appointmentBox = new VBox();
                appointmentBox.setSpacing(5);
                appointmentBox.setPadding(new javafx.geometry.Insets(10));
                appointmentBox.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");
                
                Text doctorText = new Text("Doctor: " + doctorName);
                doctorText.setStyle("-fx-font-weight: bold;");
                
                Text dateText = new Text("Date: " + appointment.getDate());
                Text timeText = new Text("Time: " + appointment.getTime());
                
                Text statusText = new Text("Status: " + appointment.getStatus());
                statusText.setStyle("-fx-font-weight: bold; " + 
                    (appointment.getStatus().equals("Scheduled") ? "-fx-fill: green;" :
                    appointment.getStatus().equals("Pending") ? "-fx-fill: orange;" :
                    appointment.getStatus().equals("Rejected") ? "-fx-fill: red;" : ""));
                
                Text descText = new Text("Reason: " + appointment.getDescription());
                
                appointmentBox.getChildren().addAll(doctorText, dateText, timeText, statusText, descText);
                
                // Add spacing between appointments
                VBox.setMargin(appointmentBox, new javafx.geometry.Insets(0, 0, 10, 0));
                appointmentsVBox.getChildren().add(appointmentBox);
            }
            
            if (appointments.isEmpty()) {
                Text noAppointmentsText = new Text("No appointments scheduled");
                noAppointmentsText.setStyle("-fx-font-style: italic;");
                appointmentsVBox.getChildren().add(noAppointmentsText);
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
        chatButton.setOnAction(event -> openChatInterface());
        videoCallButton.setOnAction(event -> handleStartVideoCall());
        
        // Add panic button handler
        panicButton.setOnAction(event -> handlePanicButton());
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

        videoCallButton.setOnMouseEntered(e -> 
            videoCallButton.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #1a237e; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));
        videoCallButton.setOnMouseExited(e -> 
            videoCallButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #424242; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;"));
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
        // Placeholder method - feature not yet implemented
        showAlert("Coming Soon", "The medical reports feature is not yet available.");
        
        // Alternatively, you could disable this button entirely in the initialize() method:
        // viewReportsButton.setDisable(true);
    }

    private void openChatInterface() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatView.fxml"));
            Parent chatRoot = loader.load();
            
            ChatViewController controller = loader.getController();
            controller.setCurrentUser(loggedInPatient, false);
            
            Stage stage = (Stage) chatButton.getScene().getWindow();
            stage.setScene(new Scene(chatRoot));
            stage.setTitle("HMS - Patient Chat");
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open chat interface: " + e.getMessage());
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

    private void handleStartVideoCall() {
        try {
            // Get the list of doctors assigned to this patient
            List<Doctor> assignedDoctors = new ArrayList<>();
            DoctorDAO doctorDAO = new DoctorDAO();
            
            for (Doctor doctor : doctorDAO.loadDoctors()) {
                if (doctor.getPatientIds() != null && doctor.getPatientIds().contains(loggedInPatient.getId())) {
                    assignedDoctors.add(doctor);
                }
            }
            
            if (assignedDoctors.isEmpty()) {
                showAlert("No Doctors Available", "You have no assigned doctors to call.");
                return;
            }
            
            // Create doctor selection dialog
            ChoiceDialog<Doctor> dialog = new ChoiceDialog<>(assignedDoctors.get(0), assignedDoctors);
            dialog.setTitle("Select Doctor");
            dialog.setHeaderText("Select a doctor to call");
            dialog.setContentText("Doctor:");
            
            // Set a custom string converter to display doctor names properly
            ComboBox<Doctor> comboBox = (ComboBox<Doctor>) dialog.getDialogPane().lookup(".combo-box");
            if (comboBox != null) {
                comboBox.setCellFactory(lv -> new ListCell<Doctor>() {
                    @Override
                    protected void updateItem(Doctor doctor, boolean empty) {
                        super.updateItem(doctor, empty);
                        if (empty || doctor == null) {
                            setText(null);
                        } else {
                            setText("Dr. " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
                        }
                    }
                });
                
                comboBox.setButtonCell(new ListCell<Doctor>() {
                    @Override
                    protected void updateItem(Doctor doctor, boolean empty) {
                        super.updateItem(doctor, empty);
                        if (empty || doctor == null) {
                            setText(null);
                        } else {
                            setText("Dr. " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
                        }
                    }
                });
            }
            
            // Fixed zoom link
            String zoomUrl = "https://us05web.zoom.us/j/86573083865?pwd=2f3nVb2EDpaSz4P5OouLIaKoCuepHj.1";
            
            // Show the dialog and handle the result
            dialog.showAndWait().ifPresent(selectedDoctor -> {
                try {
                    // Open Zoom link directly in browser
                    try {
                        Desktop.getDesktop().browse(new URI(zoomUrl));
                    } catch (Exception ex) {
                        System.err.println("Failed to open Zoom link: " + ex.getMessage());
                    }

                    // Then load the video call UI
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VideoCall.fxml"));
                    Parent videoCallRoot = loader.load();

                    VideoCallController controller = loader.getController();
                    controller.setZoomMeetingLink(zoomUrl);
                    controller.setupCall(selectedDoctor, loggedInPatient);

                    Stage stage = (Stage) videoCallButton.getScene().getWindow();
                    stage.setScene(new Scene(videoCallRoot));
                    stage.setTitle("Video Call with Dr. " + selectedDoctor.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to start video call: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    // Add this new method to handle panic button press
    private void handlePanicButton() {
        if (loggedInPatient == null) {
            showAlert("Error", "You must be logged in to use the panic button");
            return;
        }
        
        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Emergency Panic Button");
        confirmAlert.setHeaderText("Are you sure you want to send an emergency alert?");
        confirmAlert.setContentText("This will notify all doctors that you need immediate medical assistance.");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Create emergency message
            String message = "EMERGENCY ALERT: Patient " + loggedInPatient.getName() + 
                             " has pressed the panic button and needs immediate medical assistance!";
            
            // Use EmergencyAlertService to create an alert for all doctors
            EmergencyAlertService.createEmergencyAlert(loggedInPatient, message);
            
            // Send email notification if patient has provided email
            if (loggedInPatient.getEmail() != null && !loggedInPatient.getEmail().isEmpty()) {
                String subject = "HMS Emergency Alert Confirmation";
                String emailContent = 
                    "Dear " + loggedInPatient.getName() + ",\n\n" +
                    "This is to confirm that your emergency alert has been sent to all available doctors.\n\n" +
                    "A medical professional should contact you shortly.\n\n" +
                    "HMS Emergency Response System";
                
                NotificationService.sendEmailNotification(loggedInPatient.getEmail(), subject, emailContent);
            }
            
            // Show confirmation to the user
            showAlert("Alert Sent", "Your emergency alert has been sent to all doctors. " +
                     "A medical professional will contact you as soon as possible.");
        }
    }
}
