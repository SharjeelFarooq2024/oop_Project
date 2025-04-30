package com.myapp.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends User {
    private List<Patient> patients;
    private List<Appointment> appointments;

    public Doctor(String name, String id, String email) {
        super(id, name, email);
        this.patients = new ArrayList<>();
        this.appointments = new ArrayList<>();
    }

    public void addPatient(Patient patient) {
        this.patients.add(patient);
    }

    public void approveAppointment(String appointmentId) {
        if (appointments.isEmpty()) {
            System.out.println("No appointments found for approval.");
            return;
        }

        System.out.println("\nCurrent Appointments:");
        for (Appointment appointment : appointments) {
            System.out.println("ID: " + appointment.getAppointmentId() + 
                             " | Patient: " + appointment.getPatientId() + 
                             " | Status: " + (appointment.isApproved() ? "Approved" : "Pending"));
        }

        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                appointment.approveAppointment();
                System.out.println("Appointment with ID " + appointmentId + " has been approved.");
                return;
            }
        }

        System.out.println("Appointment with ID " + appointmentId + " not found.");
    }

    public void giveFeedback(Patient patient, String comment) {
        Feedback feedback = new Feedback(comment, getName());
        patient.addFeedback(feedback);
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void displayUserInfo() {
        System.out.printf("The name of the Doctor is %s%n" +
                         "The ID of the doctor is %s%n" +
                         "The email of the doctor is %s%n",
                         getName(), getId(), getEmail());
    }
}