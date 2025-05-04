package com.myapp.frontend.controllers;

import com.myapp.backend.model.*;
import com.myapp.backend.services.*;
import com.myapp.backend.dao.AppointmentDAO;
import com.myapp.backend.dao.PatientDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class DoctorAppointmentsController implements Initializable {
    
    @FXML private TableView<AppointmentViewModel> appointmentsTable;
    @FXML private TableColumn<AppointmentViewModel, String> patientNameColumn;
    @FXML private TableColumn<AppointmentViewModel, String> dateColumn;
    @FXML private TableColumn<AppointmentViewModel, String> timeColumn;
    @FXML private TableColumn<AppointmentViewModel, String> statusColumn;
    @FXML private Button backButton;
    
    private Doctor loggedInDoctor;
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private PatientDAO patientDAO = new PatientDAO();
    
    private ObservableList<AppointmentViewModel> appointments = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up table columns
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        appointmentsTable.setItems(appointments);
        
        // Set up back button action
        backButton.setOnAction(this::handleBack);
        
        // Set up context menu for appointments
        ContextMenu contextMenu = new ContextMenu();
        MenuItem viewDetailsItem = new MenuItem("View Details");
        MenuItem approveItem = new MenuItem("Approve");
        MenuItem rejectItem = new MenuItem("Reject");
        MenuItem completeItem = new MenuItem("Mark as Completed");
        
        viewDetailsItem.setOnAction(event -> handleViewDetails());
        approveItem.setOnAction(event -> handleApprove());
        rejectItem.setOnAction(event -> handleReject());
        completeItem.setOnAction(event -> handleComplete());
        
        contextMenu.getItems().addAll(viewDetailsItem, approveItem, rejectItem, completeItem);
        appointmentsTable.setContextMenu(contextMenu);
    }
    
    public void setLoggedInDoctor(Doctor doctor) {
        this.loggedInDoctor = doctor;
        loadAppointments();
    }
    
    private void loadAppointments() {
        if (loggedInDoctor == null) return;
        
        try {
            List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
            appointments.clear();
            
            for (Appointment appointment : allAppointments) {
                if (appointment.getDoctorId().equals(loggedInDoctor.getId())) {
                    Patient patient = patientDAO.findById(appointment.getPatientId());
                    String patientName = patient != null ? patient.getName() : "Unknown Patient";
                    
                    appointments.add(new AppointmentViewModel(
                        appointment.getAppointmentId(),
                        patientName,
                        appointment.getDate(),
                        appointment.getTime(),
                        appointment.getStatus()
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading appointments");
        }
    }
    
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DoctorDashboard.fxml"));
            Parent dashboardRoot = loader.load();
            
            DoctorDashboardController controller = loader.getController();
            controller.setLoggedInDoctor(loggedInDoctor);
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(dashboardRoot);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error returning to dashboard");
        }
    }
    
    private void handleViewDetails() {
        AppointmentViewModel selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an appointment");
            return;
        }
        
        // Get the patient for this appointment
        try {
            Patient patient = patientDAO.findById(getAppointmentPatientId(selectedAppointment.getId()));
            if (patient == null) {
                showAlert(Alert.AlertType.ERROR, "Could not find patient data");
                return;
            }
            
            // Show patient details view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDetails.fxml"));
            Parent patientDetailsRoot = loader.load();
            
            PatientDetailsController controller = loader.getController();
            controller.setPatientAndDoctor(patient, loggedInDoctor);
            
            Stage stage = new Stage();
            stage.setTitle("Patient Details");
            stage.setScene(new Scene(patientDetailsRoot));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error showing patient details");
        }
    }
    
    private void handleApprove() {
        updateAppointmentStatus("Scheduled");
    }
    
    private void handleReject() {
        updateAppointmentStatus("Rejected");
    }
    
    private void handleComplete() {
        updateAppointmentStatus("Completed");
    }
    
    private void updateAppointmentStatus(String status) {
        AppointmentViewModel selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an appointment");
            return;
        }
        
        try {
            List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
            Appointment appointment = null;
            
            for (Appointment a : allAppointments) {
                if (a.getAppointmentId().equals(selectedAppointment.getId())) {
                    appointment = a;
                    break;
                }
            }
            
            if (appointment == null) {
                showAlert(Alert.AlertType.ERROR, "Could not find appointment");
                return;
            }
            
            appointment.setStatus(status);
            appointmentDAO.updateAppointment(appointment);
            
            // Notify patient if needed
            Patient patient = patientDAO.findById(appointment.getPatientId());
            if (patient != null) {
                if ("Scheduled".equals(status)) {
                    NotificationService.sendAppointmentConfirmation(
                        patient.getId(),
                        loggedInDoctor.getName(),
                        appointment.getDate(),
                        appointment.getTime()
                    );
                }
                
                // Send email notification
                if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
                    String subject = "Appointment Update";
                    String message = "Your appointment with Dr. " + loggedInDoctor.getName() +
                            " on " + appointment.getDate() + " at " + appointment.getTime() +
                            " has been " + status.toLowerCase() + ".";
                    
                    NotificationService.sendEmailNotification(patient.getEmail(), subject, message);
                }
            }
            
            // Refresh the appointments list
            loadAppointments();
            
            showAlert(Alert.AlertType.INFORMATION, "Appointment updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error updating appointment");
        }
    }
    
    private String getAppointmentPatientId(String appointmentId) {
        try {
            List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
            
            for (Appointment a : allAppointments) {
                if (a.getAppointmentId().equals(appointmentId)) {
                    return a.getPatientId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // View model for appointments to display in TableView
    public static class AppointmentViewModel {
        private String id;
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
        
        public String getId() { return id; }
        public String getPatientName() { return patientName; }
        public String getDate() { return date; }
        public String getTime() { return time; }
        public String getStatus() { return status; }
        
        public void setId(String id) { this.id = id; }
        public void setPatientName(String patientName) { this.patientName = patientName; }
        public void setDate(String date) { this.date = date; }
        public void setTime(String time) { this.time = time; }
        public void setStatus(String status) { this.status = status; }
    }
}