package com.myapp.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Patient extends User {
    private List<Feedback> feedbacks;
    private List<VitalSign> vitalSigns;

    public Patient() {
        super();
        this.feedbacks = new ArrayList<>();
        this.vitalSigns = new ArrayList<>();
    }

    public Patient(String name, String email, String password) {
        super(name, email, password);
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
        this.feedbacks.add(feedback);
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
}
