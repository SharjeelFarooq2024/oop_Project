package com.myapp.backend.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Patient class representing a patient in the hospital system
@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient extends User {
    private List<Feedback> feedbacks;   // Feedback provided by or about this patient
    private List<VitalSign> vitalSigns; // Patient's vital signs history

    // Default constructor
    public Patient() {
        super(); // This will generate the ID
        this.feedbacks = new ArrayList<>();
        this.vitalSigns = new ArrayList<>();
    }

    // Constructor with parameters
    public Patient(String name, String email, String password) {
        super(name, email, password); // This will generate the ID
        this.feedbacks = new ArrayList<>();
        this.vitalSigns = new ArrayList<>();
    }

    // Get patient's vital signs history
    public List<VitalSign> getVitalSigns() {
        return vitalSigns;
    }

    // Set patient's vital signs
    public void setVitalSigns(List<VitalSign> vitalSigns) {
        this.vitalSigns = vitalSigns;
    }

    // Add new feedback to patient's record
    public void addFeedback(Feedback feedback) {
        if (this.feedbacks == null) {
            this.feedbacks = new ArrayList<>();
        }
        this.feedbacks.add(feedback);
    }

    // Get all feedbacks for this patient
    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    // Set all feedbacks at once
    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    // Add a new vital sign reading to patient's record
    public void addVitalSign(VitalSign vitalSign) {
        if (this.vitalSigns == null) {
            this.vitalSigns = new ArrayList<>();
        }
        this.vitalSigns.add(vitalSign);
    }

    public VitalSign getLatestVitalSign() {
        if (vitalSigns == null || vitalSigns.isEmpty()) {
            return null;
        }
        // Sort by timestamp (newest first) and return the first one
        return vitalSigns.stream()
                .sorted((v1, v2) -> v2.getTimestamp().compareTo(v1.getTimestamp()))
                .findFirst()
                .orElse(null);
    }

    public List<String> getPrescribedMedications() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return new ArrayList<>();
        }
        
        return feedbacks.stream()
                .filter(f -> f.getMedicationPrescribed() != null && !f.getMedicationPrescribed().isEmpty())
                .map(Feedback::getMedicationPrescribed)
                .collect(java.util.stream.Collectors.toList());
    }
}
