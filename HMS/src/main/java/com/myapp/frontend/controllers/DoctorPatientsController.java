package com.myapp.frontend.controllers;

import com.myapp.backend.model.*;
import com.myapp.backend.services.*;
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
import java.util.List;
import java.util.ResourceBundle;

public class DoctorPatientsController implements Initializable {
    
    @FXML private TableView<PatientViewModel> patientsTable;
    @FXML private TableColumn<PatientViewModel, String> nameColumn;
    @FXML private TableColumn<PatientViewModel, Integer> ageColumn;
    @FXML private TableColumn<PatientViewModel, String> emailColumn;
    @FXML private TableColumn<PatientViewModel, String> phoneColumn;
    
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button backButton;
    @FXML private Button viewDetailsButton;
    @FXML private Button addFeedbackButton;
    @FXML private Button startVideoCallButton;
    
    private Doctor loggedInDoctor;
    private PatientDAO patientDAO = new PatientDAO();
    private ObservableList<PatientViewModel> patients = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        patientsTable.setItems(patients);
        
        // Setup button actions
        backButton.setOnAction(this::handleBack);
        refreshButton.setOnAction(e -> loadPatients());
        searchButton.setOnAction(this::handleSearch);
        viewDetailsButton.setOnAction(e -> handleViewDetails());
        addFeedbackButton.setOnAction(e -> handleAddFeedback());
        startVideoCallButton.setOnAction(e -> handleStartVideoCall());
    }
    
    public void setLoggedInDoctor(Doctor doctor) {
        this.loggedInDoctor = doctor;
        loadPatients();
    }
    
    private void loadPatients() {
        if (loggedInDoctor == null) return;
        
        patients.clear();
        
        if (loggedInDoctor.getPatients() == null || loggedInDoctor.getPatients().isEmpty()) {
            // Add sample data if no patients
            patients.add(new PatientViewModel("John Doe", 35, "john@example.com", "555-1234"));
            patients.add(new PatientViewModel("Jane Smith", 42, "jane@example.com", "555-5678"));
            patients.add(new PatientViewModel("Ali Khan", 28, "ali@example.com", "555-9012"));
        } else {
            for (Patient patient : loggedInDoctor.getPatients()) {
                patients.add(new PatientViewModel(
                    patient.getName(),
                    patient.getAge(),
                    patient.getEmail(),
                    "N/A" // Phone not implemented in Patient model
                ));
            }
        }
    }
    
    private void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadPatients();
            return;
        }
        
        ObservableList<PatientViewModel> filteredList = FXCollections.observableArrayList();
        for (PatientViewModel patient : patients) {
            if (patient.getName().toLowerCase().contains(searchTerm) || 
                patient.getEmail().toLowerCase().contains(searchTerm)) {
                filteredList.add(patient);
            }
        }
        
        patientsTable.setItems(filteredList);
    }
    
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DoctorDashboard.fxml"));
            Parent dashboardRoot = loader.load();
            
            DoctorDashboardController controller = loader.getController();
            controller.setLoggedInDoctor(loggedInDoctor);
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.setTitle("Doctor Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error returning to dashboard");
        }
    }
    
    private void handleViewDetails() {
        PatientViewModel selectedPatient = patientsTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a patient");
            return;
        }
        
        try {
            // Find the actual Patient object
            Patient patient = null;
            if (loggedInDoctor.getPatients() != null) {
                for (Patient p : loggedInDoctor.getPatients()) {
                    if (p.getName().equals(selectedPatient.getName()) && 
                        p.getEmail().equals(selectedPatient.getEmail())) {
                        patient = p;
                        break;
                    }
                }
            }
            
            // If patient not found in doctor's list, create one for demo
            if (patient == null) {
                patient = new Patient();
                patient.setName(selectedPatient.getName());
                patient.setEmail(selectedPatient.getEmail());
                patient.setAge(selectedPatient.getAge());
                patient.setId("p" + System.currentTimeMillis());
            }
            
            // Load the patient details view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDetails.fxml"));
            Parent patientDetailsRoot = loader.load();
            
            PatientDetailsController controller = loader.getController();
            controller.setPatientAndDoctor(patient, loggedInDoctor);
            
            Stage stage = new Stage();
            stage.setTitle("Patient Details - " + patient.getName());
            stage.setScene(new Scene(patientDetailsRoot));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error showing patient details: " + e.getMessage());
        }
    }
    
    private void handleAddFeedback() {
        // Same as view details, then focus on feedback tab
        handleViewDetails();
    }
    
    private void handleStartVideoCall() {
        PatientViewModel selectedPatient = patientsTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a patient");
            return;
        }
        
        try {
            // Find the actual Patient object or create a placeholder
            Patient patient = new Patient();
            patient.setName(selectedPatient.getName());
            patient.setEmail(selectedPatient.getEmail());
            patient.setAge(selectedPatient.getAge());
            patient.setId("p" + System.currentTimeMillis());
            
            // Load the video call view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VideoCall.fxml"));
            Parent videoCallRoot = loader.load();
            
            VideoCallController controller = loader.getController();
            controller.startCall(loggedInDoctor, patient);
            
            Stage stage = new Stage();
            stage.setTitle("Video Call with " + patient.getName());
            stage.setScene(new Scene(videoCallRoot));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error starting video call: " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // View model for patients to display in TableView
    public static class PatientViewModel {
        private String name;
        private int age;
        private String email;
        private String phone;
        
        public PatientViewModel(String name, int age, String email, String phone) {
            this.name = name;
            this.age = age;
            this.email = email;
            this.phone = phone;
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        
        public void setName(String name) { this.name = name; }
        public void setAge(int age) { this.age = age; }
        public void setEmail(String email) { this.email = email; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}