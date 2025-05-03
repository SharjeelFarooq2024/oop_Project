package com.myapp.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Patient extends User {
    private int age;
    private List<Feedback> feedbacks;

    public Patient() {
        super(); // make sure User also has a default constructor
        this.feedbacks = new ArrayList<>();
    }
    

    public Patient(String name, String email, String password, int age) {
        super(name, email, password);
        this.age = age;
        this.feedbacks = new ArrayList<>();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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
