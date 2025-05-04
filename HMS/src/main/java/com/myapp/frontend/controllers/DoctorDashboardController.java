package com.myapp.frontend.controllers;

import java.util.ArrayList;
import com.myapp.backend.model.*;
import com.myapp.backend.services.*;
import com.myapp.backend.dao.AppointmentDAO;
import com.myapp.backend.dao.PatientDAO;  // Add this import
// Remove the problematic import and reference the class with full package path when needed

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// Add this import at the top with your other imports
import com.myapp.frontend.controllers.DoctorPatientsController;

public class DoctorDashboardController implements Initializable {

    private Doctor loggedInDoctor;
    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    @FXML private Label doctorNameLabel;
    @FXML private Label specializationLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Label totalPatientsLabel;
    @FXML private Label upcomingAppointmentsLabel;
    
    @FXML private Button logoutButton;
    @FXML private Button dashboardButton;
    @FXML private Button appointmentsButton;
    @FXML private Button patientsButton;
    @FXML private Button emergencyAlertsButton;
    @FXML private Button videoCallButton;
    
    @FXML private TableView<AppointmentViewModel> pendingAppointmentsTable;
    @FXML private TableColumn<AppointmentViewModel, String> pendingPatientColumn;
    @FXML private TableColumn<AppointmentViewModel, String> pendingDateColumn;
    @FXML private TableColumn<AppointmentViewModel, String> pendingTimeColumn;
    
    @FXML private TableView<AppointmentViewModel> todaysAppointmentsTable;
    @FXML private TableColumn<AppointmentViewModel, String> todayPatientColumn;
    @FXML private TableColumn<AppointmentViewModel, String> todayTimeColumn;
    @FXML private TableColumn<AppointmentViewModel, String> todayStatusColumn;
    
    @FXML private ListView<String> emergencyAlertsList;
    
    private ObservableList<AppointmentViewModel> pendingAppointments = FXCollections.observableArrayList();
    private ObservableList<AppointmentViewModel> todaysAppointments = FXCollections.observableArrayList();
    private ObservableList<String> emergencyAlerts = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize table columns for pending appointments
        if (pendingPatientColumn != null) {
            pendingPatientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
            pendingDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            pendingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
            pendingAppointmentsTable.setItems(pendingAppointments);
        }
        
        // Initialize table columns for today's appointments
        if (todayPatientColumn != null) {
            todayPatientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
            todayTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
            todayStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            todaysAppointmentsTable.setItems(todaysAppointments);
        }
        
        // Set up emergency alerts
        if (emergencyAlertsList != null) {
            emergencyAlertsList.setItems(emergencyAlerts);
        }
        
        // Add context menu to pending appointments for approve/reject
        setupPendingAppointmentContextMenu();
        
        // Add context menu to emergency alerts for resolving
        setupEmergencyAlertsContextMenu();
        
        // Set up button actions - Make sure this line is present
        setupButtonActions();
    }
    
    public void setLoggedInDoctor(Doctor doctor) {
        this.loggedInDoctor = doctor;
        updateDoctorInfo();
        loadPendingAppointments();
        loadTodaysAppointments();
        loadEmergencyAlerts();
        updateStatistics();
    }
    
    private void updateDoctorInfo() {
        if (loggedInDoctor != null) {
            doctorNameLabel.setText(loggedInDoctor.getName());
            specializationLabel.setText(loggedInDoctor.getSpecialization());
            welcomeLabel.setText("Welcome back, " + loggedInDoctor.getName().split(" ")[1] + "!");
        }
    }
    
    // Fix loadPendingAppointments method
    private void loadPendingAppointments() {
        if (loggedInDoctor == null) return;
        
        try {
            List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
            pendingAppointments.clear();
            
            for (Appointment appointment : allAppointments) {
                if (appointment.getDoctorId().equals(loggedInDoctor.getId()) && 
                    appointment.isPending()) {
                    
                    Patient patient = getPatientById(appointment.getPatientId());
                    String patientName = patient != null ? patient.getName() : "Unknown Patient";
                    
                    pendingAppointments.add(new AppointmentViewModel(
                        appointment.getAppointmentId(),
                        appointment.getPatientId(), 
                        patientName, 
                        appointment.getDate(), 
                        appointment.getTime(),
                        appointment.getStatus()
                    ));
                }
            }
            
            System.out.println("Loaded " + pendingAppointments.size() + " pending appointments");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading pending appointments");
        }
    }
    
    // Fix loadTodaysAppointments method
    private void loadTodaysAppointments() {
        if (loggedInDoctor == null) return;
        
        try {
            List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
            todaysAppointments.clear();
            
            String today = java.time.LocalDate.now().toString();
            
            for (Appointment appointment : allAppointments) {
                if (appointment.getDoctorId().equals(loggedInDoctor.getId()) && 
                    appointment.getDate().equals(today)) {
                    
                    Patient patient = getPatientById(appointment.getPatientId());
                    String patientName = patient != null ? patient.getName() : "Unknown Patient";
                    todaysAppointments.add(new AppointmentViewModel(
                        appointment.getAppointmentId(),
                        appointment.getPatientId(), 
                        patientName, 
                        appointment.getDate(), 
                        appointment.getTime(),
                        appointment.getStatus()
                    ));
                }
            }
            
            System.out.println("Loaded " + todaysAppointments.size() + " appointments for today");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading today's appointments");
        }
    }
    
    // Fix loadEmergencyAlerts method
    private void loadEmergencyAlerts() {
        if (loggedInDoctor == null) return;
        
        try {
            List<EmergencyAlert> alerts = EmergencyAlertService.getDoctorUnresolvedAlerts(loggedInDoctor.getId());
            emergencyAlerts.clear();
            
            // If no alerts found, add some sample data for demonstration
            if (alerts.isEmpty()) {
                // Create a sample emergency alert
                EmergencyAlert sampleAlert = new EmergencyAlert(
                    "p123", "Ali Khan", "Patient reporting severe chest pain"
                );
                alerts.add(sampleAlert);
                
                // Send to the doctor's alerts
                List<EmergencyAlert> doctorAlerts = loggedInDoctor.getEmergencyAlerts();
                if (doctorAlerts == null) {
                    doctorAlerts = new ArrayList<>(); // This requires java.util.ArrayList import
                    loggedInDoctor.setEmergencyAlerts(new ArrayList<>(doctorAlerts));
                }
                doctorAlerts.add(sampleAlert);
            }
            
            for (EmergencyAlert alert : alerts) {
                if (!alert.isResolved()) {
                    String alertText = String.format("[URGENT] %s: %s", alert.getPatientName(), alert.getMessage());
                    emergencyAlerts.add(alertText);
                }
            }
            
            System.out.println("Loaded " + emergencyAlerts.size() + " emergency alerts");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading emergency alerts: " + e.getMessage());
        }
    }
    
    // Fix updateStatistics method
    private void updateStatistics() {
        if (loggedInDoctor == null) return;
        
        try {
            // Count total patients
            int totalPatients = loggedInDoctor.getPatients() != null ? loggedInDoctor.getPatients().size() : 0;
            
            // Count upcoming appointments
            int upcomingAppointments = 0;
            List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
            String today = java.time.LocalDate.now().toString();
            
            for (Appointment appointment : allAppointments) {
                if (appointment.getDoctorId().equals(loggedInDoctor.getId()) && 
                    (appointment.isScheduled() || appointment.isPending()) &&
                    appointment.getDate().compareTo(today) >= 0) {
                    upcomingAppointments++;
                }
            }
            
            // Update UI if the labels exist
            if (totalPatientsLabel != null) {
                totalPatientsLabel.setText("Total Patients: " + totalPatients);
            }
            
            if (upcomingAppointmentsLabel != null) {
                upcomingAppointmentsLabel.setText("Upcoming: " + upcomingAppointments);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error updating statistics");
        }
    }
    
    private Patient getPatientById(String patientId) {
        try {
            // First try from the doctor's patients list
            if (loggedInDoctor.getPatients() != null) {
                for (Patient patient : loggedInDoctor.getPatients()) {
                    if (patient.getId().equals(patientId)) {
                        return patient;
                    }
                }
            }
            
            // If not found, try loading from PatientDAO
            return new PatientDAO().findById(patientId);
        } catch (Exception e) {
            System.err.println("Error finding patient by ID: " + e.getMessage());
            return null;
        }
    }
    
    private void handleApproveAppointment(AppointmentViewModel appointment) {
        try {
            List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
            Appointment selectedAppointment = null;
            
            for (Appointment a : allAppointments) {
                if (a.getAppointmentId().equals(appointment.getId())) {
                    selectedAppointment = a;
                    break;
                }
            }
            
            if (selectedAppointment == null) {
                showAlert(Alert.AlertType.ERROR, "Appointment not found");
                return;
            }
            
            selectedAppointment.markAsScheduled();
            appointmentDAO.updateAppointment(selectedAppointment);
            
            Patient patient = getPatientById(selectedAppointment.getPatientId());
            if (patient != null && patient.getEmail() != null) {
                NotificationService.sendEmailNotification(
                    patient.getEmail(),
                    "Appointment Confirmed",
                    "Dear " + patient.getName() + ",\n\n" +
                    "Your appointment with Dr. " + loggedInDoctor.getName() + " has been confirmed for " +
                    selectedAppointment.getDate() + " at " + selectedAppointment.getTime() + ".\n\n" +
                    "Please arrive 15 minutes early.\n\n" +
                    "Best regards,\nHospital Management System"
                );
                
                NotificationService.sendAppointmentConfirmation(
                    patient.getId(), 
                    loggedInDoctor.getName(),
                    selectedAppointment.getDate(),
                    selectedAppointment.getTime()
                );
            }
            
            loadPendingAppointments();
            loadTodaysAppointments();
            updateStatistics();
            
            showAlert(Alert.AlertType.INFORMATION, "Appointment approved successfully");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error approving appointment: " + e.getMessage());
        }
    }
    
    private void handleRejectAppointment(AppointmentViewModel appointment) {
        try {
            List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
            Appointment selectedAppointment = null;
            
            for (Appointment a : allAppointments) {
                if (a.getAppointmentId().equals(appointment.getId())) {
                    selectedAppointment = a;
                    break;
                }
            }
            
            if (selectedAppointment == null) {
                showAlert(Alert.AlertType.ERROR, "Appointment not found");
                return;
            }
            
            selectedAppointment.setStatus("Rejected");
            appointmentDAO.updateAppointment(selectedAppointment);
            
            Patient patient = getPatientById(selectedAppointment.getPatientId());
            if (patient != null && patient.getEmail() != null) {
                NotificationService.sendEmailNotification(
                    patient.getEmail(),
                    "Appointment Rejected",
                    "Dear " + patient.getName() + ",\n\n" +
                    "Unfortunately, your appointment with Dr. " + loggedInDoctor.getName() + " for " +
                    selectedAppointment.getDate() + " at " + selectedAppointment.getTime() + 
                    " cannot be accommodated at this time.\n\n" +
                    "Please book another time slot.\n\n" +
                    "Best regards,\nHospital Management System"
                );
            }
            
            loadPendingAppointments();
            updateStatistics();
            
            showAlert(Alert.AlertType.INFORMATION, "Appointment rejected");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error rejecting appointment: " + e.getMessage());
        }
    }
    
    private void handleResolveAlert(String alertText) {
        try {
            List<EmergencyAlert> alerts = EmergencyAlertService.getDoctorUnresolvedAlerts(loggedInDoctor.getId());
            
            for (EmergencyAlert alert : alerts) {
                String formattedAlert = String.format("[URGENT] %s: %s", alert.getPatientName(), alert.getMessage());
                if (formattedAlert.equals(alertText)) {
                    EmergencyAlertService.resolveAlert(alert.getAlertId(), loggedInDoctor.getId());
                    loadEmergencyAlerts();
                    showAlert(Alert.AlertType.INFORMATION, "Alert marked as resolved");
                    return;
                }
            }
            
            showAlert(Alert.AlertType.ERROR, "Alert not found");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error resolving alert: " + e.getMessage());
        }
    }
    
    private void setupButtonActions() {
        logoutButton.setOnAction(this::handleLogout);
        dashboardButton.setOnAction(e -> {/* No action needed, already on dashboard */});
        appointmentsButton.setOnAction(e -> showAppointmentsView());
        patientsButton.setOnAction(e -> showPatientsView());
        emergencyAlertsButton.setOnAction(e -> showEmergencyAlertsView());
        videoCallButton.setOnAction(e -> showVideoCallView());
        
        // Add hover effects
        setupHoverEffects(dashboardButton);
        setupHoverEffects(appointmentsButton);
        setupHoverEffects(patientsButton);
        setupHoverEffects(emergencyAlertsButton);
        setupHoverEffects(videoCallButton);
    }
    
    private void setupHoverEffects(Button button) {
        button.setOnMouseEntered(e -> {
            if (!button.getStyle().contains("-fx-background-color: #1a237e")) {
                button.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #1a237e; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;");
            }
        });
        
        button.setOnMouseExited(e -> {
            if (!button.getStyle().contains("-fx-background-color: #1a237e")) {
                button.setStyle("-fx-background-color: transparent; -fx-text-fill: #424242; -fx-font-size: 14px; -fx-padding: 10 15; -fx-background-radius: 5;");
            }
        });
    }
    
    private void handleLogout(ActionEvent event) {
        try {
            // Clear session
            SessionManager.clearSession();
            
            // Navigate to login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error logging out: " + e.getMessage());
        }
    }
    
    private void showAppointmentsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DoctorAppointments.fxml"));
            Parent appointmentsView = loader.load();
            
            DoctorAppointmentsController controller = loader.getController();
            controller.setLoggedInDoctor(loggedInDoctor);
            
            Stage stage = (Stage) appointmentsButton.getScene().getWindow();
            stage.setScene(new Scene(appointmentsView));
            stage.setTitle("Doctor Appointments");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading appointments view: " + e.getMessage());
        }
    }

    private void showPatientsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DoctorPatients.fxml"));
            Parent patientsView = loader.load();
            
            Object controller = loader.getController();
            // Use reflection to call setLoggedInDoctor method
            if (controller != null) {
                try {
                    java.lang.reflect.Method method = controller.getClass().getMethod("setLoggedInDoctor", Doctor.class);
                    method.invoke(controller, loggedInDoctor);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.err.println("Error setting logged in doctor: " + ex.getMessage());
                }
            }
            
            Stage stage = (Stage) patientsButton.getScene().getWindow();
            stage.setScene(new Scene(patientsView));
            stage.setTitle("My Patients");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading patients view: " + e.getMessage());
        }
    }

    private void showEmergencyAlertsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EmergencyAlerts.fxml"));
            Parent alertsView = loader.load();
            
            EmergencyAlertsController controller = loader.getController();
            controller.setLoggedInDoctor(loggedInDoctor);
            
            Stage stage = (Stage) emergencyAlertsButton.getScene().getWindow();
            stage.setScene(new Scene(alertsView));
            stage.setTitle("Emergency Alerts");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading emergency alerts view: " + e.getMessage());
        }
    }
    
    private void showVideoCallView() {
        try {
            if (loggedInDoctor == null) return;
            
            // Get all patients of this doctor
            List<Patient> patients = loggedInDoctor.getPatients();
            if (patients == null || patients.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "You don't have any patients to call");
                return;
            }
            
            // Create a dialog to select a patient
            Dialog<Patient> dialog = new Dialog<>();
            dialog.setTitle("Select Patient for Video Call");
            dialog.setHeaderText("Choose a patient to start a video call with:");
            
            // Set the button types
            ButtonType startCallButtonType = new ButtonType("Start Call", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(startCallButtonType, ButtonType.CANCEL);
            
            // Create a ListView with all patients
            ListView<Patient> patientListView = new ListView<>();
            ObservableList<Patient> patientList = FXCollections.observableArrayList(patients);
            patientListView.setItems(patientList);
            
            // Set the cell factory to display patient names
            patientListView.setCellFactory(lv -> new ListCell<Patient>() {
                @Override
                protected void updateItem(Patient patient, boolean empty) {
                    super.updateItem(patient, empty);
                    if (empty || patient == null) {
                        setText(null);
                    } else {
                        setText(patient.getName());
                    }
                }
            });
            
            // Enable the start call button only when a patient is selected
            Node startCallButton = dialog.getDialogPane().lookupButton(startCallButtonType);
            startCallButton.setDisable(true);
            
            patientListView.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> startCallButton.setDisable(newValue == null));
            
            // Add the ListView to the dialog
            dialog.getDialogPane().setContent(patientListView);
            
            // Convert the result to a patient when the start call button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == startCallButtonType) {
                    return patientListView.getSelectionModel().getSelectedItem();
                }
                return null;
            });
            
            // Show the dialog and wait for result
            Optional<Patient> result = dialog.showAndWait();
            
            // Then continue with your existing code
            result.ifPresent(patient -> {
                try {
                    // Load the video call view
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VideoCall.fxml"));
                    Parent videoCallRoot = loader.load();
                    
                    VideoCallController controller = loader.getController();
                    controller.startCall(loggedInDoctor, patient);
                    
                    Stage videoCallStage = new Stage();
                    videoCallStage.setTitle("Video Call with " + patient.getName());
                    videoCallStage.setScene(new Scene(videoCallRoot));
                    videoCallStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error starting video call: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error preparing video call");
        }
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Add patient details view functionality
    private void showPatientDetails(Patient patient) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDetails.fxml"));
            Parent patientDetailsRoot = loader.load();
            
            PatientDetailsController controller = loader.getController();
            controller.setPatientAndDoctor(patient, loggedInDoctor);
            
            Stage patientDetailsStage = new Stage();
            patientDetailsStage.setTitle("Patient Details - " + patient.getName());
            patientDetailsStage.setScene(new Scene(patientDetailsRoot));
            patientDetailsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error showing patient details: " + e.getMessage());
        }
    }

    private void setupPendingAppointmentContextMenu() {
        if (pendingAppointmentsTable == null) return;
        
        ContextMenu appointmentContextMenu = new ContextMenu();
        MenuItem approveItem = new MenuItem("Approve");
        MenuItem rejectItem = new MenuItem("Reject");
        MenuItem viewDetailsItem = new MenuItem("View Details");
        
        approveItem.setOnAction(event -> {
            AppointmentViewModel selectedAppointment = pendingAppointmentsTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                handleApproveAppointment(selectedAppointment);
            }
        });
        
        rejectItem.setOnAction(event -> {
            AppointmentViewModel selectedAppointment = pendingAppointmentsTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                handleRejectAppointment(selectedAppointment);
            }
        });
        
        viewDetailsItem.setOnAction(event -> {
            AppointmentViewModel selectedAppointment = pendingAppointmentsTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                showAppointmentDetails(selectedAppointment);
            }
        });
        
        appointmentContextMenu.getItems().addAll(viewDetailsItem, approveItem, rejectItem);
        pendingAppointmentsTable.setContextMenu(appointmentContextMenu);
    }

    private void setupEmergencyAlertsContextMenu() {
        if (emergencyAlertsList == null) return;
        
        ContextMenu alertContextMenu = new ContextMenu();
        MenuItem resolveItem = new MenuItem("Mark as Resolved");
        MenuItem contactItem = new MenuItem("Contact Patient");
        
        resolveItem.setOnAction(e -> {
            String selectedAlert = emergencyAlertsList.getSelectionModel().getSelectedItem();
            if (selectedAlert != null) {
                handleResolveAlert(selectedAlert);
            }
        });
        
        contactItem.setOnAction(e -> {
            String selectedAlert = emergencyAlertsList.getSelectionModel().getSelectedItem();
            if (selectedAlert != null) {
                // Extract patient name from alert text
                String[] parts = selectedAlert.split(":");
                if (parts.length > 0) {
                    String patientName = parts[0].replace("[URGENT]", "").trim();
                    showContactDialog(patientName);
                }
            }
        });
        
        alertContextMenu.getItems().addAll(resolveItem, contactItem);
        emergencyAlertsList.setContextMenu(alertContextMenu);
    }

    // You'll also need this helper method
    private void showContactDialog(String patientName) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Contact Patient");
        dialog.setHeaderText("Send message to " + patientName);
        dialog.setContentText("Message:");
        
        dialog.showAndWait().ifPresent(message -> {
            showAlert(Alert.AlertType.INFORMATION, "Message sent to " + patientName);
        });
    }

    // Add a showAppointmentDetails method
    private void showAppointmentDetails(AppointmentViewModel appointment) {
        try {
            // Find the actual Patient object
            Patient patient = getPatientById(appointment.getPatientId());
            
            if (patient != null) {
                showPatientDetails(patient);
            } else {
                showAlert(Alert.AlertType.ERROR, "Could not find patient details");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error showing appointment details: " + e.getMessage());
        }
    }
    // View model for appointments to display in TableViews
    public static class AppointmentViewModel {
        private String id;
        private String patientId;
        private String patientName;
        private String date;
        private String time;
        private String status;
        
        public AppointmentViewModel(String id, String patientName, String date, String time, String status) {
            this.id = id;
            this.patientName = patientName;
            this.date = date;
            this.time = time;
            this.status = status;
        }
        
        public AppointmentViewModel(String id, String patientId, String patientName, String date, String time, String status) {
            this.id = id;
            this.patientId = patientId;
            this.patientName = patientName;
            this.date = date;
            this.time = time;
            this.status = status;
        }
        
        public String getId() { return id; }
        public String getPatientId() { return patientId; }
        public String getPatientName() { return patientName; }
        public String getDate() { return date; }
        public String getTime() { return time; }
        public String getStatus() { return status; }
    }

}