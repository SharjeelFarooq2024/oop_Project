package com.myapp.backend.model;

public class Appointment {
    private String appointmentId;
    private String patientId;
    private boolean approved;

    public Appointment(String appointmentId, String patientId) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.approved = false;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public boolean isApproved() {
        return approved;
    }

    public void approveAppointment() {
        this.approved = true;
    }
}