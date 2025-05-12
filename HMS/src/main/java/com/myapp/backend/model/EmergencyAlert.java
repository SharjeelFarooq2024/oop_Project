package com.myapp.backend.model;

import java.time.LocalDateTime;

// Represents an emergency alert created by or for a patient
public class EmergencyAlert {
    private String alertId;        // Unique identifier for the emergency alert
    private String patientId;      // ID of the patient who triggered the alert
    private String patientName;    // Name of the patient for quick identification
    private String message;        // Description of the emergency situation
    private LocalDateTime timestamp; // When the alert was created
    private boolean resolved;      // Whether the emergency has been addressed
    
    // Default constructor for Jackson JSON serialization
    public EmergencyAlert() {
        // Default constructor for Jackson
    }
    
    // Constructor with parameters to create a new emergency alert
    public EmergencyAlert(String patientId, String patientName, String message) {
        this.alertId = java.util.UUID.randomUUID().toString();
        this.patientId = patientId;
        this.patientName = patientName;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.resolved = false;  // New alerts are unresolved by default
    }
    
    // Getter for alert ID
    public String getAlertId() {
        return alertId;
    }
    
    // Getter for patient ID
    public String getPatientId() {
        return patientId;
    }
    
    // Getter for patient name
    public String getPatientName() {
        return patientName;
    }
    
    // Getter for emergency message
    public String getMessage() {
        return message;
    }
    
    // Getter for timestamp
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    // Getter for resolution status
    public boolean isResolved() {
        return resolved;
    }
    
    // Setters
    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
    
    public void resolve() {
        this.resolved = true;
    }
}