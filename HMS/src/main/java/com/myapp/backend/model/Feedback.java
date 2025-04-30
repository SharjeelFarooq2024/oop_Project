package com.myapp.backend.model;

public class Feedback {
    private String comment;
    private String doctorName;

    public Feedback(String comment, String doctorName) {
        this.comment = comment;
        this.doctorName = doctorName;
    }

    public String getComment() {
        return comment;
    }

    public String getDoctorName() {
        return doctorName;
    }
}