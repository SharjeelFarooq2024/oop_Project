package com.myapp.backend.model;

import java.time.LocalDateTime;

public class Feedback {
    private String comment;
    private String doctorName;
    private String medicationPrescribed;
    private LocalDateTime timestamp;
    private String recommendedTests;

    public Feedback() {
        // Default constructor for Jackson
    }

    public Feedback(String comment, String doctorName) {
        this.comment = comment;
        this.doctorName = doctorName;
        this.timestamp = LocalDateTime.now();
    }
    
    public Feedback(String comment, String doctorName, String medicationPrescribed, LocalDateTime timestamp) {
        this.comment = comment;
        this.doctorName = doctorName;
        this.medicationPrescribed = medicationPrescribed;
        this.timestamp = timestamp;
    }

    public Feedback(String comment, String doctorName, String medicationPrescribed, String recommendedTests, LocalDateTime timestamp) {
        this.comment = comment;
        this.doctorName = doctorName;
        this.medicationPrescribed = medicationPrescribed;
        this.recommendedTests = recommendedTests;
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public String getDoctorName() {
        return doctorName;
    }
    
    public String getMedicationPrescribed() {
        return medicationPrescribed;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getRecommendedTests() {
        return recommendedTests;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public void setMedicationPrescribed(String medicationPrescribed) {
        this.medicationPrescribed = medicationPrescribed;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setRecommendedTests(String recommendedTests) {
        this.recommendedTests = recommendedTests;
    }
}