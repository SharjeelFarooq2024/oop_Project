package com.myapp.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class VideoCallSession {
    private String sessionId;
    private String doctorId;
    private String patientId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean active;
    
    public VideoCallSession() {
        // Default constructor for Jackson
    }
    
    public VideoCallSession(String doctorId, String patientId) {
        this.sessionId = UUID.randomUUID().toString();
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.startTime = LocalDateTime.now();
        this.active = true;
    }
    
    public void endSession() {
        this.endTime = LocalDateTime.now();
        this.active = false;
    }
    
    // Getters
    public String getSessionId() {
        return sessionId;
    }
    
    public String getDoctorId() {
        return doctorId;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
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