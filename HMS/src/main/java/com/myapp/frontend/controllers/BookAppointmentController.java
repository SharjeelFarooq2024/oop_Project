package com.myapp.frontend.controllers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.myapp.backend.dao.AppointmentDAO;
import com.myapp.backend.dao.AppointmentDAO.AppointmentStatus;
import com.myapp.backend.dao.DoctorDAO;
import com.myapp.backend.model.Appointment;
import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Patient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
        List<Doctor> doctors = doctorDAO.loadDoctors(); // Load from doctors.json
        System.out.println("Doctors loaded from DAO: " + doctors.size());

        if (doctors.isEmpty()) {
            System.out.println("No doctors found. Check Doctors.json content or path.");
        }

        ObservableList<Doctor> observableDoctors = FXCollections.observableArrayList(doctors);
        doctorNameField.setItems(observableDoctors);

        // Visual rendering in dropdown
        doctorNameField.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);
                setText(empty || doctor == null ? null : doctor.getName() + " - " + doctor.getSpecialization());
            }
        });

        doctorNameField.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);
                setText(empty || doctor == null ? null : doctor.getName() + " - " + doctor.getSpecialization());
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
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleAppointmentBooking() {
        Doctor selectedDoctor = doctorNameField.getValue();
        LocalDate selectedDate = appointmentDatePicker.getValue();
        String selectedTime = timeSlotComboBox.getValue();
        String reason = reasonField.getText();
    
        // Validate input
        if (selectedDoctor == null || selectedDate == null || selectedTime == null || reason.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all fields before booking.");
            return;
        }
    
        // Create Appointment object
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(UUID.randomUUID().toString());
        appointment.setPatientId(loggedInPatient.getId());
        appointment.setDoctorId(selectedDoctor.getId());
        appointment.setStatus("Pending");
        appointment.setDate(selectedDate.toString());
        appointment.setTime(selectedTime);
        appointment.setDescription(reason);


        // Save appointment using DAO
        AppointmentStatus status = appointmentDAO.addAppointment(appointment);
    
        switch (status) {
            case SUCCESS:
                showAlert(Alert.AlertType.INFORMATION, 
                    String.format("Appointment booked successfully!\n\nDetails:\nDoctor: %s\nDate: %s\nTime: %s\n\nStatus: Pending (Awaiting doctor's confirmation)",
                    selectedDoctor.getName(),
                    selectedDate.toString(),
                    selectedTime));
                clearForm();
                break;
            case DUPLICATE:
                showAlert(Alert.AlertType.WARNING, "This doctor is already booked at the selected date and time.");
                break;
            case ERROR:
                showAlert(Alert.AlertType.ERROR, "An unexpected error occurred while booking the appointment.");
                break;
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
