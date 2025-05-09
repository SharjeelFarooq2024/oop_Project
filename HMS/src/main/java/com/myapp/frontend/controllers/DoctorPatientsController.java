package com.myapp.frontend.controllers;

import com.myapp.backend.model.*;
import com.myapp.backend.dao.PatientDAO;
import com.myapp.frontend.controllers.PatientDetailsController;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DoctorPatientsController implements Initializable {
    
    @FXML private TableView<PatientViewModel> patientsTable;
    @FXML private TableColumn<PatientViewModel, String> nameColumn;
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
    private ObservableList<PatientViewModel> patients = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up table columns
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        
        // Setup button actions - This is crucial!
        viewDetailsButton.setOnAction(e -> handleViewDetails());
        addFeedbackButton.setOnAction(e -> handleAddFeedback());
        backButton.setOnAction(this::handleBack);
        refreshButton.setOnAction(e -> loadPatients());
        searchButton.setOnAction(this::handleSearch);
        startVideoCallButton.setOnAction(e -> handleStartVideoCall());
        
        // Initialize table data
        patientsTable.setItems(patients);
    }
    
    public void setLoggedInDoctor(Doctor doctor) {
        this.loggedInDoctor = doctor;
        loadPatients();
    }
    
    private void loadPatients() {
        if (loggedInDoctor == null) {
            // Handle case where doctor is not logged in or not set
            return;
        }
    
        patients.clear();
        PatientDAO patientDAO = new PatientDAO();
        ArrayList<String> patientIds = loggedInDoctor.getPatientIds();
        
        if (patientIds == null || patientIds.isEmpty()) {
            // No patients assigned or list is null
            patientsTable.setPlaceholder(new Label("No patients assigned to you."));
        } else {
            for (String patientId : patientIds) {
                Patient patient = patientDAO.findById(patientId);
                if (patient != null) {
                    patients.add(new PatientViewModel(
                        patient.getId(), 
                        patient.getName(), 
                        patient.getEmail(), 
                        "N/A"  // Phone not available in current model
                    ));
                }
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
        PatientViewModel selectedPatientViewModel = patientsTable.getSelectionModel().getSelectedItem();
        if (selectedPatientViewModel == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a patient first");
            return;
        }

        try {
            // Get the full patient object
            PatientDAO patientDAO = new PatientDAO();
            Patient selectedPatient = patientDAO.findById(selectedPatientViewModel.getId());
            
            // Load the PatientDetails view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDetails.fxml"));
            Parent root = loader.load();
            
            // Pass data to the controller
            PatientDetailsController controller = loader.getController();
            controller.setPatientAndDoctor(selectedPatient, loggedInDoctor);
            
            // Show the view
            Scene scene = new Scene(root);
            Stage stage = (Stage) viewDetailsButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading patient details: " + e.getMessage());
        }
    }
    
    private void handleAddFeedback() {
        PatientViewModel selectedPatientViewModel = patientsTable.getSelectionModel().getSelectedItem();
        if (selectedPatientViewModel == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a patient first");
            return;
        }

        try {
            // Get the full patient object using the ID from the view model
            PatientDAO patientDAO = new PatientDAO();
            Patient selectedPatient = patientDAO.findById(selectedPatientViewModel.getId());
            
            if (selectedPatient == null) {
                showAlert(Alert.AlertType.ERROR, "Could not load patient data");
                return;
            }

            // Load the PatientDetails view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDetails.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pass the patient and doctor data
            PatientDetailsController controller = loader.getController();
            controller.setPatientAndDoctor(selectedPatient, loggedInDoctor);
            controller.selectFeedbackTab(); // This method will select the feedback tab directly
            
            // Show the PatientDetails view
            Scene scene = new Scene(root);
            Stage stage = (Stage) addFeedbackButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Add Feedback - " + selectedPatient.getName());
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading patient details: " + e.getMessage());
        }
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
        private final SimpleStringProperty id; 
        private final SimpleStringProperty name;
        private final SimpleStringProperty email;
        private final SimpleStringProperty phone; 

        public PatientViewModel(String id, String name, String email, String phone) {
            this.id = new SimpleStringProperty(id); 
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
            this.phone = new SimpleStringProperty(phone);
        }

        public String getId() { return id.get(); } 
        public String getName() { return name.get(); }
        public String getEmail() { return email.get(); }
        public String getPhone() { return phone.get(); }
        
        // Property getters for TableView
        public SimpleStringProperty idProperty() { return id; } 
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty emailProperty() { return email; }
        public SimpleStringProperty phoneProperty() { return phone; }
    }
}