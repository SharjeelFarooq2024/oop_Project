package com.myapp.frontend.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.myapp.backend.dao.AppointmentDAO;
import com.myapp.backend.dao.AppointmentDAO.AppointmentStatus;
import com.myapp.backend.dao.DoctorDAO;
import com.myapp.backend.model.Appointment;
import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Patient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class BookAppointmentController {

    @FXML
    private ComboBox<Doctor> doctorNameField;

    @FXML
    private DatePicker appointmentDatePicker;

    @FXML
    private ComboBox<String> timeSlotComboBox;

    @FXML
    private TextArea reasonField;

    @FXML
    private Button clearButton;

    @FXML
    private Button confirmButton;

    @FXML
    private Button backButton;

    private Patient loggedInPatient;
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public void setLoggedInPatient(Patient patient) {
        this.loggedInPatient = patient;
        loadDoctors(); // Load doctors when patient is set
    }
    

    @FXML
    public void initialize() {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        loadTimeSlots();
        confirmButton.setOnAction(e -> handleAppointmentBooking());
        clearButton.setOnAction(e -> clearForm());
        backButton.setOnAction(e -> goBack());
    }

    private void loadDoctors() {
        List<Doctor> doctors = doctorDAO.loadDoctors();
        System.out.println("Doctors loaded from DAO: " + doctors.size());

        if (doctors.isEmpty()) {
            System.out.println("No doctors found. Check Doctors.json content or path.");
        }

        ObservableList<Doctor> observableDoctors = FXCollections.observableArrayList(doctors);
        doctorNameField.setItems(observableDoctors);

        // Visual rendering in dropdown
        doctorNameField.setCellFactory(lv -> new ListCell<Doctor>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);
                if (empty || doctor == null) {
                    setText(null);
                } else {
                    setText(doctor.getName() + " - " + doctor.getSpecialization());
                }
            }
        });

        doctorNameField.setButtonCell(new ListCell<Doctor>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);
                if (empty || doctor == null) {
                    setText(null);
                } else {
                    setText(doctor.getName() + " - " + doctor.getSpecialization());
                }
            }
        });
    }

    private void loadTimeSlots() {
        ObservableList<String> timeSlots = FXCollections.observableArrayList(
            "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
            "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM"
        );
        timeSlotComboBox.setItems(timeSlots);
    }

    


    private void clearForm() {
        doctorNameField.getSelectionModel().clearSelection();
        appointmentDatePicker.setValue(null);
        timeSlotComboBox.getSelectionModel().clearSelection();
        reasonField.clear();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleAppointmentBooking() {
        Doctor selectedDoctor = doctorNameField.getValue();
        LocalDate selectedDate = appointmentDatePicker.getValue();
        String selectedTime = timeSlotComboBox.getValue();
        String reason = reasonField.getText();

        try {
            // Validate input with specific error messages
            if (selectedDoctor == null) {
                showAlert(Alert.AlertType.ERROR, "Please select a doctor.");
                return;
            }
            if (selectedDate == null) {
                showAlert(Alert.AlertType.ERROR, "Please select an appointment date.");
                return;
            }
            if (selectedTime == null) {
                showAlert(Alert.AlertType.ERROR, "Please select an appointment time.");
                return;
            }
            if (reason == null || reason.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Please provide a reason for the appointment.");
                return;
            }

            if (selectedDate.isBefore(LocalDate.now())) {
                showAlert(Alert.AlertType.ERROR, "Cannot book appointments for past dates.");
                return;
            }

            // Create and validate Appointment object
            if (loggedInPatient == null || loggedInPatient.getId() == null) {
                showAlert(Alert.AlertType.ERROR, "Session error: Patient information not found. Please try logging in again.");
                return;
            }

            // Create Appointment object with proper initialization
            Appointment appointment = new Appointment();
            appointment.setAppointmentId(UUID.randomUUID().toString());
            appointment.setPatientId(loggedInPatient.getId());
            appointment.setDoctorId(selectedDoctor.getId());
            appointment.setStatus("Pending");
            appointment.setDate(selectedDate.toString());
            appointment.setTime(selectedTime);
            appointment.setDescription(reason);

            // Save appointment and handle response
            AppointmentStatus status = appointmentDAO.addAppointment(appointment);
            
            switch (status) {
                case SUCCESS:
                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("Appointment Booked Successfully!");
                    successAlert.setContentText(String.format(
                        "Appointment Details:\n\n" +
                        "Doctor: %s\n" +
                        "Date: %s\n" +
                        "Time: %s\n" +
                        "Status: Pending (Awaiting doctor's confirmation)\n\n" +
                        "You will be notified once the doctor confirms your appointment.",
                        selectedDoctor.getName(),
                        selectedDate.toString(),
                        selectedTime
                    ));
                    
                    clearForm();
                    successAlert.showAndWait();
                    goBack();
                    break;
                    
                case DUPLICATE:
                    showAlert(Alert.AlertType.WARNING, 
                        String.format("Dr. %s is already booked at %s on %s.\nPlease select a different time slot.", 
                            selectedDoctor.getName(), selectedTime, selectedDate));
                    break;
                    
                case ERROR:
                    showAlert(Alert.AlertType.ERROR, 
                        "An error occurred while booking the appointment. Please try again.\n" +
                        "If the problem persists, please contact support.");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, 
                "An unexpected error occurred: " + e.getMessage() + "\n" +
                "Please try again or contact support if the problem persists.");
        }
    }
    
    
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDashboard.fxml"));
            Parent dashboardRoot = loader.load();
    
            // Pass the logged-in patient to the dashboard controller
            PatientDashboardController controller = loader.getController();
            controller.setLoggedInPatient(loggedInPatient); // You need to implement this method in that controller
    
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.setTitle("Patient Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to return to dashboard.");
        }
    }

    
}
