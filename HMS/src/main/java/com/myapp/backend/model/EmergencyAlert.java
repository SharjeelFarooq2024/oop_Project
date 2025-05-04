package com.myapp.backend.model;

import java.time.LocalDateTime;

public class EmergencyAlert {
    private String alertId;
    private String patientId;
    private String patientName;
    private String message;
    private LocalDateTime timestamp;
    private boolean resolved;
    
    public EmergencyAlert() {
        // Default constructor for Jackson
    }
    
    public EmergencyAlert(String patientId, String patientName, String message) {
        this.alertId = java.util.UUID.randomUUID().toString();
        this.patientId = patientId;
        this.patientName = patientName;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.resolved = false;
    }
    
    // Getters
    public String getAlertId() {
        return alertId;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public String getMessage() {
        return message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
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