package com.myapp.frontend.controllers;

import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Patient;
import com.myapp.backend.services.VideoCallService;
import com.myapp.backend.services.NotificationService;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class VideoCallController implements Initializable {

    @FXML private Label callTimeLabel;
    @FXML private Label remoteParticipantName;
    @FXML private ImageView remoteVideoPlaceholder;
    @FXML private ImageView localVideoPlaceholder;  
    @FXML private Button micButton;
    @FXML private Button videoButton;
    @FXML private Button endCallButton;
    
    // Default Zoom meeting link - this should be updated with the user's actual link
    private String zoomMeetingLink = "https://us05web.zoom.us/j/86573083865?pwd=2f3nVb2EDpaSz4P5OouLIaKoCuepHj.1";
    
    private Doctor callingDoctor; 
    private Patient patientInCall; 

    private String sessionId;
    private boolean isMuted = false;
    private boolean isVideoOn = true;
    private Timeline callTimer;
    private int seconds = 0;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        micButton.setOnAction(event -> toggleMute());
        videoButton.setOnAction(event -> toggleVideo());
        endCallButton.setOnAction(event -> endCall());
        
        callTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            seconds++;
            updateCallTimeDisplay();
        }));
        callTimer.setCycleCount(Animation.INDEFINITE);
    }
    
    public void setZoomMeetingLink(String link) {
        if (link != null && !link.trim().isEmpty()) {
            this.zoomMeetingLink = link;
        }
    }
    
    public void setupCall(Doctor doctor, Patient patient) {
        this.callingDoctor = doctor;
        this.patientInCall = patient;

        if (this.patientInCall != null) {
            remoteParticipantName.setText(this.patientInCall.getName());
        } else {
            remoteParticipantName.setText("Unknown Participant");
        }

        if (this.callingDoctor != null && this.patientInCall != null) {
            sessionId = VideoCallService.initiateCall(this.callingDoctor, this.patientInCall);
            System.out.println("Starting video call between Dr. " + this.callingDoctor.getName() + 
                              " and patient " + this.patientInCall.getName() + ". Session ID: " + sessionId);

            // Send email notification to the patient with the Zoom link
            if (this.patientInCall.getEmail() != null && !this.patientInCall.getEmail().isEmpty()) {
                String emailSubject = "Video Consultation Started - HMS";
                String emailMessage = String.format(
                    "Dear %s,\n\nDr. %s has started a video consultation with you.\n\n" +
                    "Join the call using this Zoom link: %s\n\n" +
                    "Session ID: %s\n\nRegards,\nHMS Team",
                    this.patientInCall.getName(),
                    this.callingDoctor.getName(),
                    zoomMeetingLink,
                    sessionId
                );
                NotificationService.sendEmailNotification(this.patientInCall.getEmail(), emailSubject, emailMessage);
                System.out.println("Video call notification email sent to: " + this.patientInCall.getEmail());
            }

            // Open the Zoom link automatically
            openZoomMeeting();
        } else {
            System.err.println("Cannot start video call: Doctor or Patient data is missing.");
            showAlert(Alert.AlertType.ERROR, "Call Error", "Could not initiate call due to missing participant data.");
            if (endCallButton != null && endCallButton.getScene() != null) {
                 Stage stage = (Stage) endCallButton.getScene().getWindow();
                 stage.close(); 
            }
            return;
        }
        
        seconds = 0; 
        updateCallTimeDisplay(); 
        callTimer.play();
    }
    
    private void openZoomMeeting() {
        try {
            System.out.println("Opening Zoom meeting link: " + zoomMeetingLink);
            Desktop.getDesktop().browse(new URI(zoomMeetingLink));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Failed to open Zoom meeting: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Zoom Error", 
                     "Could not open Zoom meeting. Please use this link manually: " + zoomMeetingLink);
        }
    }
    
    private void toggleMute() {
        isMuted = !isMuted;
        micButton.setText(isMuted ? "Unmute" : "Mute");
        System.out.println("Microphone " + (isMuted ? "muted" : "unmuted"));
    }
    
    private void toggleVideo() {
        isVideoOn = !isVideoOn;
        videoButton.setText(isVideoOn ? "Stop Video" : "Start Video");
        localVideoPlaceholder.setVisible(isVideoOn); 
        System.out.println("Video " + (isVideoOn ? "on" : "off"));
    }
    
    private void endCall() {
        if (callTimer != null) {
            callTimer.stop();
        }

        if (sessionId != null) {
            VideoCallService.endCall(sessionId);
            System.out.println("Video call session " + sessionId + " ended.");
            sessionId = null; 
        }

        try {
            if (callingDoctor != null) {
                // Return to appropriate screen based on who initiated the call
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DoctorPatients.fxml"));
                Parent doctorPatientsRoot = loader.load();

                DoctorPatientsController controller = loader.getController();
                controller.setLoggedInDoctor(this.callingDoctor);
                
                Stage stage = (Stage) endCallButton.getScene().getWindow();
                stage.setScene(new Scene(doctorPatientsRoot));
                stage.setTitle("My Patients");
            } else {
                // Return to patient dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDashboard.fxml"));
                Parent patientDashboardRoot = loader.load();

                PatientDashboardController controller = loader.getController();
                controller.setLoggedInPatient(this.patientInCall);
                
                Stage stage = (Stage) endCallButton.getScene().getWindow();
                stage.setScene(new Scene(patientDashboardRoot));
                stage.setTitle("Patient Dashboard");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                     "Failed to return to previous screen: " + e.getMessage());
        }
    }
    
    private void updateCallTimeDisplay() {
        long minutes = seconds / 60;
        long secs = seconds % 60;
        callTimeLabel.setText(String.format("%02d:%02d", minutes, secs));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}