package com.myapp.backend.model;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)


public class Appointment {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String status; // "Pending", "Scheduled", "Completed"
    private String date; // Optional: Date of the appointment
    private String time; // Optional: Time of the appointment
    private String description;

    public Appointment() {
        // no-arg constructor for Jackson
    }
    

    // Constructor
    public Appointment(String patientId, String doctorId, String status, String date, String time, String description) {
        this.appointmentId = UUID.randomUUID().toString(); // Generates a unique ID
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.status = status;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    // Getters

    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public String getDescription() {
        return description;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    // Convenience methods
    public void markAsScheduled() {
        this.status = "Scheduled";
    }

    public void markAsCompleted() {
        this.status = "Completed";
    }

    public boolean isPending() {
        return "Pending".equalsIgnoreCase(this.status);
    }

    public boolean isScheduled() {
        return "Scheduled".equalsIgnoreCase(this.status);
    }

    public boolean isCompleted() {
        return "Completed".equalsIgnoreCase(this.status);
    }
}
