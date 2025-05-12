package com.myapp.backend.model;

import java.time.LocalDateTime;

// Represents feedback from a doctor about a patient's condition or treatment
public class Feedback {
    private String comment;                // Doctor's comments about the patient
    private String doctorName;             // Name of the doctor providing feedback
    private String medicationPrescribed;   // Medications prescribed during the visit
    private LocalDateTime timestamp;       // When the feedback was created
    private String recommendedTests;       // Tests recommended by the doctor

    // Default constructor for Jackson JSON serialization
    public Feedback() {
        // Default constructor for Jackson
    }

    // Constructor with minimal required parameters
    public Feedback(String comment, String doctorName) {
        this.comment = comment;
        this.doctorName = doctorName;
        this.timestamp = LocalDateTime.now();
    }
    
    // Constructor with medication information
    public Feedback(String comment, String doctorName, String medicationPrescribed, LocalDateTime timestamp) {
        this.comment = comment;
        this.doctorName = doctorName;
        this.medicationPrescribed = medicationPrescribed;
        this.timestamp = timestamp;
    }

    // Constructor with full information including recommended tests
    public Feedback(String comment, String doctorName, String medicationPrescribed, String recommendedTests, LocalDateTime timestamp) {
        this.comment = comment;
        this.doctorName = doctorName;
        this.medicationPrescribed = medicationPrescribed;
        this.recommendedTests = recommendedTests;
        this.timestamp = timestamp;
    }

    // Getter for doctor's comment
    public String getComment() {
        return comment;
    }

    // Getter for doctor's name
    public String getDoctorName() {
        return doctorName;
    }
    
    // Getter for prescribed medication
    public String getMedicationPrescribed() {
        return medicationPrescribed;
    }
    
    // Getter for timestamp
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