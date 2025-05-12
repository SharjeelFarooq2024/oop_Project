package com.myapp.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

// Represents a telemedicine video call session between a doctor and a patient
public class VideoCallSession {
    private String sessionId;      // Unique identifier for the video call session
    private String doctorId;       // ID of the doctor participating in the call
    private String patientId;      // ID of the patient participating in the call
    private LocalDateTime startTime; // When the video call started
    private LocalDateTime endTime;   // When the video call ended
    private boolean active;        // Whether the call is currently in progress
    
    // Default constructor for Jackson JSON serialization
    public VideoCallSession() {
        // Default constructor for Jackson
    }
    
    // Constructor to create a new video call session
    public VideoCallSession(String doctorId, String patientId) {
        this.sessionId = UUID.randomUUID().toString();
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.startTime = LocalDateTime.now();
        this.active = true;  // New session is active by default
    }
    
    // Ends the current video call session and records the end time
    public void endSession() {
        this.endTime = LocalDateTime.now();
        this.active = false;
    }
    
    // Getter for session ID
    public String getSessionId() {
        return sessionId;
    }
    
    // Getter for doctor ID
    public String getDoctorId() {
        return doctorId;
    }
    
    // Getter for patient ID
    public String getPatientId() {
        return patientId;
    }
    
    // Getter for start time
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    // Getter for end time
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public boolean isActive() {
        return active;
    }
    
    // Setters
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public long getDurationInMinutes() {
        if (endTime == null) {
            return 0;
        }
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }
}