package com.myapp.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Patient extends User {
    private int age;
    private List<Feedback> feedbacks;

    public Patient(String id, String name, String email, int age) {
        super(id, name, email);
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
}
