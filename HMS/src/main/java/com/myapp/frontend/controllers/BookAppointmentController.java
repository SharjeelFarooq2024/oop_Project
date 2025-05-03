package com.myapp.frontend.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.myapp.backend.dao.AppointmentDAO;
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
        Appointment appointment = new Appointment(
            loggedInPatient.getId(),
            selectedDoctor.getId(),
            "Pending", // status
            selectedDate.toString(),
            selectedTime,
            reason
        );
    
        // Save appointment using DAO
        boolean success = appointmentDAO.addAppointment(appointment);
    
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Appointment booked successfully!");
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to book appointment. Please try again.");
        }
    }
    
    
    private void goBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
