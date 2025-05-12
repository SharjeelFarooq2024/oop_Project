package com.myapp.frontend.controllers;

// Standard Java imports
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// JavaFX imports
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.util.Callback;
import javafx.stage.Stage;

// Application-specific imports
import com.myapp.backend.model.Appointment;
import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Patient; // Assuming Patient model might be needed for patient names
import com.myapp.backend.model.EmergencyAlert; // For emergency alerts list
import com.myapp.backend.dao.AppointmentDAO;
import com.myapp.backend.dao.PatientDAO;
import com.myapp.backend.services.SessionManager; // For logout or getting current session

// Controller for the Doctor Dashboard screen
public class DoctorDashboardController implements Initializable {

    private Doctor loggedInDoctor;                       // Current logged-in doctor
    private AppointmentDAO appointmentDAO = new AppointmentDAO(); // Data access for appointments
    private PatientDAO patientDAO = new PatientDAO();    // Data access for patients

    // UI Labels for doctor information
    @FXML private Label doctorNameLabel;        // Shows doctor's name
    @FXML private Label specializationLabel;    // Shows doctor's specialization
    @FXML private Label welcomeLabel;           // Welcome message
    @FXML private Label statusLabel;            // Status messages for operations
    @FXML private Label totalPatientsLabel;     // Shows total number of patients
    @FXML private Label upcomingAppointmentsLabel; // Shows number of upcoming appointments

    // Navigation buttons
    @FXML private Button logoutButton;          // Button to log out
    @FXML private Button dashboardButton;       // Button to navigate to dashboard
    @FXML private Button appointmentsButton;    // Button to navigate to appointments view
    @FXML private Button patientsButton;        // Button to navigate to patients view
    @FXML private Button emergencyAlertsButton; // Button to navigate to emergency alerts
    @FXML private Button chatButton;            // Button to navigate to chat interface

    // Appointment requests (pending appointments) UI components
    @FXML private TableView<AppointmentViewModel> pendingAppointmentsTable;
    @FXML private TableColumn<AppointmentViewModel, String> pendingPatientColumn;
    @FXML private TableColumn<AppointmentViewModel, String> pendingDateColumn;
    @FXML private TableColumn<AppointmentViewModel, String> pendingTimeColumn;
    @FXML private TableColumn<AppointmentViewModel, Void> pendingActionsColumn; // Actions like approve/reject

    // Scheduled appointments UI components
    @FXML private TableView<AppointmentViewModel> scheduledAppointmentsTable;
    @FXML private TableColumn<AppointmentViewModel, String> scheduledPatientColumn;
    @FXML private TableColumn<AppointmentViewModel, String> scheduledDateColumn;
    @FXML private TableColumn<AppointmentViewModel, String> scheduledTimeColumn;
    @FXML private TableColumn<AppointmentViewModel, String> scheduledStatusColumn;

    // Today's appointments UI components
    @FXML private TableView<AppointmentViewModel> todaysAppointmentsTable;
    @FXML private TableColumn<AppointmentViewModel, String> todayPatientColumn;
    @FXML private TableColumn<AppointmentViewModel, String> todayTimeColumn;
    @FXML private TableColumn<AppointmentViewModel, String> todayStatusColumn;

    // Emergency alerts list
    @FXML private ListView<String> emergencyAlertsList;

    // Observable lists to store and display data in tables
    private ObservableList<AppointmentViewModel> pendingAppointmentsData = FXCollections.observableArrayList();
    private ObservableList<AppointmentViewModel> scheduledAppointmentsData = FXCollections.observableArrayList();
    private ObservableList<AppointmentViewModel> todaysAppointmentsData = FXCollections.observableArrayList();
    private ObservableList<String> emergencyAlertsData = FXCollections.observableArrayList();

    // Initialize the controller
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup table columns for pending appointments
        pendingPatientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        pendingDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        pendingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        setupPendingActionsColumn(); // Add action buttons to the pending appointments table
        pendingAppointmentsTable.setItems(pendingAppointmentsData);

        // Setup table columns for scheduled appointments
        scheduledPatientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        scheduledDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        scheduledTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        scheduledStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        scheduledAppointmentsTable.setItems(scheduledAppointmentsData);

        // Setup table columns for today's appointments
        todayPatientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        todayTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        todayStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        todaysAppointmentsTable.setItems(todaysAppointmentsData);

        // Setup emergency alerts list
        emergencyAlertsList.setItems(emergencyAlertsData);

        // Setup button actions
        logoutButton.setOnAction(this::handleLogout);
        appointmentsButton.setOnAction(this::navigateToAppointments);
        patientsButton.setOnAction(this::navigateToPatients);
        emergencyAlertsButton.setOnAction(this::navigateToEmergencyAlerts);
        chatButton.setOnAction(event -> openChatInterface());
        dashboardButton.setOnAction(event -> {
            if (loggedInDoctor != null) {
                loadDashboardData(); // Refresh data on dashboard button click
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Not Logged In", "Please log in to view dashboard details.");
            }
        });

        statusLabel.setText("Status: Initializing...");
        
        // Add this code to handle patient navigation
        patientsButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DoctorPatients.fxml"));
                Parent patientsView = loader.load();
                DoctorPatientsController controller = loader.getController();
                controller.setLoggedInDoctor(loggedInDoctor);
                
                Stage stage = (Stage) patientsButton.getScene().getWindow();
                stage.setScene(new Scene(patientsView));
                stage.setTitle("My Patients");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Failed to open patients view: " + e.getMessage());
            }
        });
    }

    private void setupPendingActionsColumn() {
        pendingActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button acceptButton = new Button("Accept");
            private final Button rejectButton = new Button("Reject");
            private final HBox pane = new HBox(5, acceptButton, rejectButton);
            
            {
                pane.setAlignment(Pos.CENTER);
                acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                rejectButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                
                acceptButton.setOnAction(event -> {
                    AppointmentViewModel appointment = getTableView().getItems().get(getIndex());
                    acceptAppointment(appointment);
                });
                
                rejectButton.setOnAction(event -> {
                    AppointmentViewModel appointment = getTableView().getItems().get(getIndex());
                    rejectAppointment(appointment);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void acceptAppointment(AppointmentViewModel appointmentVM) {
        try {
            Appointment appointment = appointmentDAO.getAllAppointments()
                .stream()
                .filter(a -> a.getAppointmentId().equals(appointmentVM.getAppointmentId()))
                .findFirst()
                .orElse(null);
                
            if (appointment != null) {
                // Update status
                appointment.setStatus("Scheduled");
                appointment.setPending(false);
                appointment.setScheduled(true);
                
                // Update in database
                appointmentDAO.updateAppointment(appointment);
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Appointment Accepted", 
                    "The appointment has been scheduled successfully.");
                
                // Refresh data
                loadDashboardData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                "Failed to accept appointment: " + e.getMessage());
        }
    }

    private void rejectAppointment(AppointmentViewModel appointmentVM) {
        try {
            Appointment appointment = appointmentDAO.getAllAppointments()
                .stream()
                .filter(a -> a.getAppointmentId().equals(appointmentVM.getAppointmentId()))
                .findFirst()
                .orElse(null);
                
            if (appointment != null) {
                // Update status
                appointment.setStatus("Rejected");
                appointment.setPending(false);
                appointment.setScheduled(false);
                
                // Update in database
                appointmentDAO.updateAppointment(appointment);
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Appointment Rejected", 
                    "The appointment has been rejected.");
                
                // Refresh data
                loadDashboardData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                "Failed to reject appointment: " + e.getMessage());
        }
    }
    
    public void setLoggedInDoctor(Doctor doctor) {
        this.loggedInDoctor = doctor;
        SessionManager.setLoggedInDoctor(doctor); 
        updateDoctorInfo();
        loadDashboardData();
    }

    private void updateDoctorInfo() {
        if (loggedInDoctor != null) {
            doctorNameLabel.setText(loggedInDoctor.getName());
            specializationLabel.setText(loggedInDoctor.getSpecialization());
            welcomeLabel.setText("Welcome, " + loggedInDoctor.getName());
            statusLabel.setText("Status: Online");
        } else {
            doctorNameLabel.setText("N/A");
            specializationLabel.setText("N/A");
            welcomeLabel.setText("Welcome");
            statusLabel.setText("Status: Offline");
             // Clear data if no doctor is logged in
            pendingAppointmentsData.clear();
            scheduledAppointmentsData.clear();
            todaysAppointmentsData.clear();
            emergencyAlertsData.clear();
            totalPatientsLabel.setText("0");
            upcomingAppointmentsLabel.setText("0");
        }
    }

    private void loadDashboardData() {
        if (loggedInDoctor == null) {
            showAlert(Alert.AlertType.WARNING, "Data Load Error", "No doctor logged in. Cannot load dashboard data.");
            return;
        }
        statusLabel.setText("Status: Loading data...");
        loadPendingAppointments();
        loadScheduledAppointments();
        loadTodaysAppointments();
        loadEmergencyAlerts();
        updateSummaryMetrics();
        statusLabel.setText("Status: Online");
    }

    private void loadPendingAppointments() {
        pendingAppointmentsData.clear();
        if (loggedInDoctor != null) {
            List<Appointment> appointments = appointmentDAO.getAllAppointments().stream()
                    .filter(app -> loggedInDoctor.getId().equals(app.getDoctorId()) && "Pending".equalsIgnoreCase(app.getStatus()))
                    .collect(Collectors.toList());
            appointments.forEach(app -> {
                Patient patient = patientDAO.findById(app.getPatientId());
                String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
                pendingAppointmentsData.add(new AppointmentViewModel(app, patientName));
            });
        }
    }

    private void loadScheduledAppointments() {
        scheduledAppointmentsData.clear();
        if (loggedInDoctor != null) {
            List<Appointment> appointments = appointmentDAO.getAllAppointments().stream()
                    .filter(app -> loggedInDoctor.getId().equals(app.getDoctorId()) && 
                           "Scheduled".equalsIgnoreCase(app.getStatus()))
                    .collect(Collectors.toList());
            
            for (Appointment app : appointments) {
                Patient patient = patientDAO.findById(app.getPatientId());
                String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
                scheduledAppointmentsData.add(new AppointmentViewModel(app, patientName));
            }
        }
    }

    private void loadTodaysAppointments() {
        todaysAppointmentsData.clear();
        if (loggedInDoctor != null) {
            String todayDateStr = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            List<Appointment> appointments = appointmentDAO.getAllAppointments().stream()
                    .filter(app -> loggedInDoctor.getId().equals(app.getDoctorId()) && todayDateStr.equals(app.getDate()))
                    .collect(Collectors.toList());
            appointments.forEach(app -> {
                Patient patient = patientDAO.findById(app.getPatientId());
                String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
                todaysAppointmentsData.add(new AppointmentViewModel(app, patientName));
            });
        }
    }

    private void loadEmergencyAlerts() {
        emergencyAlertsData.clear();
        if (loggedInDoctor != null && loggedInDoctor.getEmergencyAlerts() != null) {
             loggedInDoctor.getEmergencyAlerts().stream()
                .filter(alert -> !alert.isResolved()) 
                .forEach(alert -> emergencyAlertsData.add(
                    String.format("From %s: %s (%s)",
                        alert.getPatientName(),
                        alert.getMessage(),
                        alert.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                ));
        }
        if (emergencyAlertsData.isEmpty()) {
            emergencyAlertsData.add("No new emergency alerts.");
        }
    }

    private void updateSummaryMetrics() {
        if (loggedInDoctor != null) {
            totalPatientsLabel.setText(String.valueOf(loggedInDoctor.getPatientIds() != null ? loggedInDoctor.getPatientIds().size() : 0));
            
            long upcomingCount = appointmentDAO.getAllAppointments().stream()
                .filter(app -> loggedInDoctor.getId().equals(app.getDoctorId()) &&
                               ("Pending".equalsIgnoreCase(app.getStatus()) || "Scheduled".equalsIgnoreCase(app.getStatus())) &&
                               LocalDate.parse(app.getDate()).isAfter(LocalDate.now().minusDays(1)) 
                )
                .count();
            upcomingAppointmentsLabel.setText(String.valueOf(upcomingCount));
        } else {
            totalPatientsLabel.setText("0");
            upcomingAppointmentsLabel.setText("0");
        }
    }

    private void navigateToAppointments(ActionEvent event) {
        if (loggedInDoctor == null) {
            // Attempt to retrieve from SessionManager if it might have been set elsewhere
            loggedInDoctor = SessionManager.getLoggedInDoctor(); 
            if (loggedInDoctor == null) {
                showAlert(Alert.AlertType.ERROR, "Authentication Error", "No doctor is currently logged in. Please log in again.");
                return;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DoctorAppointments.fxml"));
            Parent root = loader.load();
            
            DoctorAppointmentsController controller = loader.getController();
            if (controller == null) {
                showAlert(Alert.AlertType.ERROR, "Controller Error", "Could not load the controller for the appointments page.");
                return;
            }
            controller.setLoggedInDoctor(loggedInDoctor); // Pass the logged-in doctor

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Manage Appointments");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the appointments page. Please check if the FXML file exists and is correctly named.\nDetails: " + e.getMessage());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "FXML Error", "Error loading FXML for appointments page. Ensure the FXML is valid.\nDetails: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unexpected Error", "An unexpected error occurred while navigating to appointments: " + e.getMessage());
        }
    }

    private void navigateToPatients(ActionEvent event) {
        navigateTo(event, "/fxml/DoctorPatients.fxml", "My Patients", true);
    }

    private void navigateToEmergencyAlerts(ActionEvent event) {
        navigateTo(event, "/fxml/EmergencyAlerts.fxml", "Emergency Alerts", true);
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
            showAlert(Alert.AlertType.ERROR, "Logout Failed", "Could not return to login page.");
        }
    }

    private void navigateTo(ActionEvent event, String fxmlFile, String title, boolean passDoctorContext) {
        if (passDoctorContext && loggedInDoctor == null) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Logged in doctor information is missing. Please log in again.");
            // Optionally redirect to login
            // handleLogout(event); 
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            if (passDoctorContext) {
                Object controller = loader.getController();
                // Using reflection to call setLoggedInDoctor if it exists, to avoid instanceof chain for many controllers
                try {
                    controller.getClass().getMethod("setLoggedInDoctor", Doctor.class).invoke(controller, loggedInDoctor);
                } catch (NoSuchMethodException e) {
                    // Method doesn't exist, controller might not need it or uses a different way
                    System.out.println("Controller " + controller.getClass().getSimpleName() + " does not have setLoggedInDoctor(Doctor) method.");
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.WARNING, "Controller Setup Error", "Could not set doctor for " + title);
                }
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the page: " + title + "\nDetails: " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "FXML file not found: " + fxmlFile + "\nDetails: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openChatInterface() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatView.fxml"));
            Parent chatRoot = loader.load();
            
            ChatViewController controller = loader.getController();
            controller.setCurrentUser(loggedInDoctor, true);
            
            Stage stage = (Stage) chatButton.getScene().getWindow();
            stage.setScene(new Scene(chatRoot));
            stage.setTitle("HMS - Doctor Chat");
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open chat interface.");
        }
    }

    public static class AppointmentViewModel {
        private final SimpleStringProperty appointmentId;
        private final SimpleStringProperty patientName;
        private final SimpleStringProperty date;
        private final SimpleStringProperty time;
        private final SimpleStringProperty status;
        private final SimpleStringProperty description;

        public AppointmentViewModel(Appointment appointment, String patientName) {
            this.appointmentId = new SimpleStringProperty(appointment.getAppointmentId());
            this.patientName = new SimpleStringProperty(patientName);
            this.date = new SimpleStringProperty(appointment.getDate());
            this.time = new SimpleStringProperty(appointment.getTime());
            this.status = new SimpleStringProperty(appointment.getStatus());
            this.description = new SimpleStringProperty(appointment.getDescription());
        }

        public String getAppointmentId() { return appointmentId.get(); }
        public String getPatientName() { return patientName.get(); }
        public String getDate() { return date.get(); }
        public String getTime() { return time.get(); }
        public String getStatus() { return status.get(); }
        public String getDescription() { return description.get(); }

        public SimpleStringProperty appointmentIdProperty() { return appointmentId; }
        public SimpleStringProperty patientNameProperty() { return patientName; }
        public SimpleStringProperty dateProperty() { return date; }
        public SimpleStringProperty timeProperty() { return time; }
        public SimpleStringProperty statusProperty() { return status; }
        public SimpleStringProperty descriptionProperty() { return description; }
    }
}