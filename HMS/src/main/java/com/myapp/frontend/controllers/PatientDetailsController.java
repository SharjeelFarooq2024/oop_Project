package com.myapp.frontend.controllers;

import com.myapp.backend.dao.VitalSignDAO;
import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Feedback;
import com.myapp.backend.model.Patient;
import com.myapp.backend.model.VitalSign;
import com.myapp.backend.services.NotificationService;
import com.myapp.backend.services.VitalSignService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.List;
import java.io.IOException;
import java.io.File;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert;

public class PatientDetailsController implements Initializable {

    @FXML private Label patientNameLabel;
    @FXML private Label patientIdLabel;
    @FXML private Label patientEmailLabel;
    @FXML private Label patientAgeLabel;
    
    // Vital signs display
    @FXML private Label heartRateLabel;
    @FXML private Label bloodPressureLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label oxygenLevelLabel;
    @FXML private Label vitalDateLabel;
    
    @FXML private TextArea feedbackTextArea;
    @FXML private TextField medicationField;
    @FXML private TextField testsField; // For prescribing tests
    @FXML private Button submitFeedbackButton;
    @FXML private Button backButton;
    @FXML private Button exportRecordsButton;
    
    @FXML private ListView<String> previousFeedbackListView;
    @FXML private ListView<String> vitalsHistoryListView;
    
    @FXML private TabPane medicalTabPane;
    @FXML private Tab vitalsTab;
    @FXML private Tab historyTab;
    @FXML private Tab newFeedbackTab;
    
    @FXML private TextField searchHistoryField;

    @FXML private BarChart<String, Number> bpChart;
    @FXML private BarChart<String, Number> hrChart;
    @FXML private BarChart<String, Number> tempChart;
    @FXML private BarChart<String, Number> oxyChart;

    private Patient currentPatient;
    private Doctor currentDoctor;
    
    // Modify the initialize method to remove the export records functionality
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        submitFeedbackButton.setOnAction(this::handleSubmitFeedback);
        backButton.setOnAction(this::handleBack);
        
        // Only setup search functionality
        if (searchHistoryField != null) {
            setupSearchFunctionality();
        }
    }
    
    public void setPatientAndDoctor(Patient patient, Doctor doctor) {
        this.currentPatient = patient;
        this.currentDoctor = doctor;
        
        // Update the UI with patient information
        updatePatientInfo();
        loadPatientVitals();
        loadVitalsHistory();
        loadPreviousFeedback();
        updateVitalsCharts();
    }
    
    private void updatePatientInfo() {
        if (currentPatient != null) {
            patientNameLabel.setText(currentPatient.getName());
            patientIdLabel.setText(currentPatient.getId());
            patientEmailLabel.setText(currentPatient.getEmail());
            patientAgeLabel.setText(String.valueOf(currentPatient.getAge()));
        }
    }
    
    private void loadPatientVitals() {
        VitalSign latestVitals = VitalSignDAO.getLatestVitalByPatientId(currentPatient.getId());
        
        if (latestVitals != null) {
            heartRateLabel.setText(String.format("%.1f bpm", latestVitals.getHeartRate()));
            bloodPressureLabel.setText(latestVitals.getBloodPressure());
            temperatureLabel.setText(String.format("%.1f °C", latestVitals.getTemperature()));
            oxygenLevelLabel.setText(String.format("%.1f%%", latestVitals.getOxygenLevel()));
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            vitalDateLabel.setText("Recorded: " + latestVitals.getTimestamp().format(formatter));
        } else {
            heartRateLabel.setText("No data");
            bloodPressureLabel.setText("No data");
            temperatureLabel.setText("No data");
            oxygenLevelLabel.setText("No data");
            vitalDateLabel.setText("No vitals recorded");
        }
    }
    
    private void loadVitalsHistory() {
        List<VitalSign> vitalsHistory = VitalSignDAO.getVitalsByPatientId(currentPatient.getId());
        ObservableList<String> vitalsItems = FXCollections.observableArrayList();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        if (vitalsHistory == null || vitalsHistory.isEmpty()) {
            vitalsItems.add("No vital signs recorded");
        } else {
            for (VitalSign vital : vitalsHistory) {
                String vitalEntry = String.format(
                    "[%s] BP: %s, HR: %.1f bpm, Temp: %.1f °C, O₂: %.1f%%",
                    vital.getTimestamp().format(formatter),
                    vital.getBloodPressure(),
                    vital.getHeartRate(),
                    vital.getTemperature(),
                    vital.getOxygenLevel()
                );
                vitalsItems.add(vitalEntry);
            }
        }
        
        vitalsHistoryListView.setItems(vitalsItems);
    }
    
    private void loadPreviousFeedback() {
        if (currentPatient != null && currentPatient.getFeedbacks() != null) {
            ObservableList<String> feedbackItems = FXCollections.observableArrayList();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            if (currentPatient.getFeedbacks().isEmpty()) {
                feedbackItems.add("No previous medical records available");
            } else {
                for (Feedback feedback : currentPatient.getFeedbacks()) {
                    StringBuilder feedbackBuilder = new StringBuilder();
                    feedbackBuilder.append("Date: ").append(feedback.getTimestamp().format(formatter))
                                  .append("\nDoctor: ").append(feedback.getDoctorName())
                                  .append("\nFeedback: ").append(feedback.getComment());
                    
                    if (feedback.getMedicationPrescribed() != null && !feedback.getMedicationPrescribed().isEmpty()) {
                        feedbackBuilder.append("\nPrescribed Medication: ")
                                      .append(feedback.getMedicationPrescribed());
                    }
                    
                    // Add a separator between entries for better readability
                    feedbackBuilder.append("\n----------------------------------------");
                    
                    feedbackItems.add(feedbackBuilder.toString());
                }
            }
            
            previousFeedbackListView.setItems(feedbackItems);
        }
    }
    
    private void handleSubmitFeedback(ActionEvent event) {
        String feedbackText = feedbackTextArea.getText();
        String medication = medicationField.getText();
        String tests = testsField.getText();
        
        if (feedbackText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Feedback cannot be empty");
            return;
        }
        
        // Add tests to feedback if provided
        if (tests != null && !tests.isEmpty()) {
            feedbackText += "\n\nPrescribed Tests: " + tests;
        }
        
        // Add feedback to patient
        if (currentDoctor != null && currentPatient != null) {
            Feedback feedback = new Feedback(feedbackText, currentDoctor.getName(), medication, LocalDateTime.now());
            currentPatient.addFeedback(feedback);
            
            // Save the updated patient data
            try {
                new com.myapp.backend.dao.PatientDAO().updatePatient(currentPatient);
                
                // Send notification to patient
                NotificationService.sendNotification(currentPatient.getId(), 
                        "New feedback from Dr. " + currentDoctor.getName());
                
                // If patient has email, send email notification too
                if (currentPatient.getEmail() != null && !currentPatient.getEmail().isEmpty()) {
                    String subject = "New Medical Feedback";
                    String message = "Dear " + currentPatient.getName() + ",\n\n" +
                                    "Dr. " + currentDoctor.getName() + " has provided new feedback on your health.\n" +
                                    "Please check your account for details.\n\n" +
                                    "HMS Team";
                    NotificationService.sendEmailNotification(currentPatient.getEmail(), subject, message);
                }
                
                // Refresh the feedback list to show the new entry
                loadPreviousFeedback();
                
                // Clear the input fields
                feedbackTextArea.clear();
                medicationField.clear();
                testsField.clear();
                
                showAlert(Alert.AlertType.INFORMATION, "Feedback submitted successfully");
                
                // Switch to history tab to show the new feedback
                medicalTabPane.getSelectionModel().select(historyTab);
                
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error saving feedback: " + e.getMessage());
            }
        }
    }
    
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DoctorPatients.fxml"));
            Parent root = loader.load();
            DoctorPatientsController controller = loader.getController();
            controller.setLoggedInDoctor(currentDoctor);
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("My Patients");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error returning to patients list: " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Remove the handleExportRecords method entirely, or replace with this simplified version
    private void handleExportRecords() {
        showAlert(Alert.AlertType.INFORMATION, "Medical history export is not available in this version.");
    }

    private void setupSearchFunctionality() {
        searchHistoryField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                loadPreviousFeedback(); // Reset to show all
            } else {
                filterFeedbackHistory(newValue.toLowerCase());
            }
        });
    }

    // Add the implementation for filterFeedbackHistory method
    private void filterFeedbackHistory(String searchTerm) {
        if (currentPatient == null || currentPatient.getFeedbacks() == null) {
            return;
        }
        
        ObservableList<String> filteredItems = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Feedback feedback : currentPatient.getFeedbacks()) {
            // Check if any field contains the search term
            boolean matches = feedback.getComment().toLowerCase().contains(searchTerm) || 
                             feedback.getDoctorName().toLowerCase().contains(searchTerm) ||
                             (feedback.getMedicationPrescribed() != null && 
                              feedback.getMedicationPrescribed().toLowerCase().contains(searchTerm));
            
            if (matches) {
                StringBuilder feedbackBuilder = new StringBuilder();
                feedbackBuilder.append("Date: ").append(feedback.getTimestamp().format(formatter))
                              .append("\nDoctor: ").append(feedback.getDoctorName())
                              .append("\nFeedback: ").append(feedback.getComment());
                
                if (feedback.getMedicationPrescribed() != null && !feedback.getMedicationPrescribed().isEmpty()) {
                    feedbackBuilder.append("\nPrescribed Medication: ")
                                  .append(feedback.getMedicationPrescribed());
                }
                
                feedbackBuilder.append("\n----------------------------------------");
                filteredItems.add(feedbackBuilder.toString());
            }
        }
        
        if (filteredItems.isEmpty()) {
            filteredItems.add("No matching records found.");
        }
        
        previousFeedbackListView.setItems(filteredItems);
    }

    // Add this method to select the feedback tab directly
    public void selectFeedbackTab() {
        // Check to prevent NullPointerException if tabs aren't fully initialized
        if (medicalTabPane != null && newFeedbackTab != null) {
            medicalTabPane.getSelectionModel().select(newFeedbackTab);
        }
    }

    private void updateVitalsCharts() {
        if (bpChart != null) bpChart.getData().clear();
        if (hrChart != null) hrChart.getData().clear();
        if (tempChart != null) tempChart.getData().clear();
        if (oxyChart != null) oxyChart.getData().clear();
        if (currentPatient == null) return;
        java.util.List<VitalSign> vitalsHistory = VitalSignService.getVitalsHistory(currentPatient.getId());
        if (vitalsHistory == null || vitalsHistory.isEmpty()) return;
        XYChart.Series<String, Number> bpSeriesSys = new XYChart.Series<>();
        bpSeriesSys.setName("Systolic");
        XYChart.Series<String, Number> bpSeriesDia = new XYChart.Series<>();
        bpSeriesDia.setName("Diastolic");
        XYChart.Series<String, Number> hrSeries = new XYChart.Series<>();
        hrSeries.setName("Heart Rate");
        XYChart.Series<String, Number> tempSeries = new XYChart.Series<>();
        tempSeries.setName("Temperature");
        XYChart.Series<String, Number> oxySeries = new XYChart.Series<>();
        oxySeries.setName("Oxygen Level");
        for (int i = vitalsHistory.size() - 1; i >= 0; i--) {
            VitalSign v = vitalsHistory.get(i);
            String time = v.getTimestamp().toString();
            try {
                String[] bp = v.getBloodPressure().split("/");
                if (bp.length == 2) {
                    bpSeriesSys.getData().add(new XYChart.Data<>(time, Integer.parseInt(bp[0].trim())));
                    bpSeriesDia.getData().add(new XYChart.Data<>(time, Integer.parseInt(bp[1].trim())));
                }
            } catch (Exception ignored) {}
            hrSeries.getData().add(new XYChart.Data<>(time, v.getHeartRate()));
            tempSeries.getData().add(new XYChart.Data<>(time, v.getTemperature()));
            oxySeries.getData().add(new XYChart.Data<>(time, v.getOxygenLevel()));
        }
        if (bpChart != null) { bpChart.getData().add(bpSeriesSys); bpChart.getData().add(bpSeriesDia); }
        if (hrChart != null) { hrChart.getData().add(hrSeries); }
        if (tempChart != null) { tempChart.getData().add(tempSeries); }
        if (oxyChart != null) { oxyChart.getData().add(oxySeries); }
    }
}