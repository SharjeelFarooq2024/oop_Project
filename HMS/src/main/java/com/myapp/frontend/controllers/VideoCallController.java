package com.myapp.frontend.controllers;

import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Patient;
import com.myapp.backend.model.VideoCallSession;
import com.myapp.backend.services.VideoCallService;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    
    private VideoCallSession currentSession;
    private Doctor doctor;
    private Patient patient;
    private String sessionId;
    private boolean isMuted = false;
    private boolean isVideoOn = true;
    private Timeline callTimer;
    private int seconds = 0;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup button actions
        micButton.setOnAction(event -> toggleMute());
        videoButton.setOnAction(event -> toggleVideo());
        endCallButton.setOnAction(event -> endCall());
        
        // Setup call timer
        callTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            seconds++;
            updateCallTimeDisplay();
        }));
        callTimer.setCycleCount(Animation.INDEFINITE);
    }
    
    public void startCall(Doctor doctor, Patient patient) {
        this.doctor = doctor;
        this.patient = patient;
        
        // Start the call and get session ID
        sessionId = VideoCallService.initiateCall(doctor, patient);
        
        // Set participant name
        if (doctor != null) {
            remoteParticipantName.setText(patient.getName());
        } else {
            remoteParticipantName.setText(doctor.getName());
        }
        
        // Start the call timer
        callTimer.play();
        
        // In a real implementation, this would initialize actual video streaming
        System.out.println("Starting video call between " + doctor.getName() + " and " + patient.getName());
    }
    
    private void toggleMute() {
        isMuted = !isMuted;
        micButton.setText(isMuted ? "Unmute" : "Mute");
        // In a real implementation, this would mute/unmute the audio
        System.out.println("Microphone " + (isMuted ? "muted" : "unmuted"));
    }
    
    private void toggleVideo() {
        isVideoOn = !isVideoOn;
        videoButton.setText(isVideoOn ? "Stop Video" : "Start Video");
        // In a real implementation, this would toggle the camera
        System.out.println("Video " + (isVideoOn ? "started" : "stopped"));
    }
    
    private void endCall() {
        // End the call session
        if (sessionId != null) {
            boolean success = VideoCallService.endCall(sessionId);
            if (success) {
                System.out.println("Call ended successfully");
            } else {
                System.out.println("Problem ending call");
            }
        }
        
        // Stop the timer
        callTimer.stop();
        
        // Close the window
        Stage stage = (Stage) endCallButton.getScene().getWindow();
        stage.close();
    }
    
    private void updateCallTimeDisplay() {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        callTimeLabel.setText(String.format("%02d:%02d", minutes, secs));
    }
}