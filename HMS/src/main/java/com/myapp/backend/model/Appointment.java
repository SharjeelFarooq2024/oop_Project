package com.myapp.backend.model;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Appointment {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String status;
    private String date;
    private String time;
    private String description;
    private boolean completed;
    private boolean pending;
    private boolean scheduled;

    public Appointment() {
        this.pending = true;
        this.completed = false;
        this.scheduled = false;
        this.status = "Pending";
    }

    public Appointment(String patientId, String doctorId, String status, String date, String time, String description) {
        this.appointmentId = UUID.randomUUID().toString();
        this.patientId = patientId;
        this.doctorId = doctorId;
        setStatus(status); // This will set the status and boolean flags correctly
        this.date = date;
        this.time = time;
        this.description = description;
    }

    // Getters
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public boolean isPending() { return pending; }
    public boolean isScheduled() { return scheduled; }

    // Setters
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    
    public void setStatus(String status) {
        if (status == null) {
            status = "Pending";
        }
        this.status = status;
        this.pending = "Pending".equalsIgnoreCase(status);
        this.completed = "Completed".equalsIgnoreCase(status);
        this.scheduled = "Scheduled".equalsIgnoreCase(status);
    }
    
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setDescription(String description) { this.description = description; }

    // These setters are used by Jackson during deserialization
    public void setCompleted(boolean completed) { 
        this.completed = completed;
        if (completed) {
            this.status = "Completed";
            this.pending = false;
            this.scheduled = false;
        }
    }
    
    public void setPending(boolean pending) { 
        this.pending = pending;
        if (pending) {
            this.status = "Pending";
            this.completed = false;
            this.scheduled = false;
        }
    }
    
    public void setScheduled(boolean scheduled) { 
        this.scheduled = scheduled;
        if (scheduled) {
            this.status = "Scheduled";
            this.completed = false;
            this.pending = false;
        }
    }

    public void markAsScheduled() {
        setStatus("Scheduled");
    }

    public void markAsCompleted() {
        setStatus("Completed");
    }
}
