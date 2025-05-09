package com.myapp.backend.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient extends User {
    private List<Feedback> feedbacks;
    private List<VitalSign> vitalSigns;

    public Patient() {
        super(); // This will generate the ID
        this.feedbacks = new ArrayList<>();
        this.vitalSigns = new ArrayList<>();
    }

    public Patient(String name, String email, String password) {
        super(name, email, password); // This will generate the ID
        this.feedbacks = new ArrayList<>();
        this.vitalSigns = new ArrayList<>();
    }

    public List<VitalSign> getVitalSigns() {
        return vitalSigns;
    }

    public void setVitalSigns(List<VitalSign> vitalSigns) {
        this.vitalSigns = vitalSigns;
    }

    public void addFeedback(Feedback feedback) {
        if (this.feedbacks == null) {
            this.feedbacks = new ArrayList<>();
        }
        this.feedbacks.add(feedback);
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

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
