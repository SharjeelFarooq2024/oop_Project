package com.myapp.backend.model;

import java.util.ArrayList;
import java.time.LocalDateTime;

public class Doctor extends User {
    private String specialization;
    private ArrayList<Patient> patients;
    private ArrayList<Appointment> appointments;
    private ArrayList<EmergencyAlert> emergencyAlerts;
    
    // No-arg constructor for Jackson serialization
    public Doctor() {
        super();
        this.patients = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.emergencyAlerts = new ArrayList<>();
    }

    // Keep only this constructor - remove the duplicate one
    public Doctor(String name, String email, String password, String specialization) {
        super(name, email, password);
        this.specialization = specialization;
        this.patients = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.emergencyAlerts = new ArrayList<>();
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
    }

    public void removePatient(String patientId) {
        patients.removeIf(patient -> patient.getId().equals(patientId));
    }

    public void approveAppointment(String appointmentId) {
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                appointment.markAsScheduled();
                System.out.println("Appointment with ID " + appointmentId + " has been approved.");
                return;
            }
        }
        System.out.println("Appointment with ID " + appointmentId + " not found.");
    }

    public void rejectAppointment(String appointmentId) {
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                appointment.setStatus("Rejected");
                System.out.println("Appointment with ID " + appointmentId + " has been rejected.");
                return;
            }
        }
        System.out.println("Appointment with ID " + appointmentId + " not found.");
    }

    // This method call assumes the Doctor can add a feedback to Patient
    public void giveFeedback(Patient patient, String comment, String medicationPrescribed) {
        Feedback feedback = new Feedback(comment, this.getName(), medicationPrescribed, LocalDateTime.now());
        patient.addFeedback(feedback); // Make sure this method exists in Patient class
    }

    public void receiveEmergencyAlert(EmergencyAlert alert) {
        this.emergencyAlerts.add(alert);
        System.out.println("Emergency alert received from patient: " + alert.getPatientName());
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public ArrayList<Patient> getPatients() {
        return patients;
    }

    public void setPatients(ArrayList<Patient> patients) {
        this.patients = patients;
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(ArrayList<Appointment> appointments) {
        this.appointments = appointments;
    }
    
    public ArrayList<EmergencyAlert> getEmergencyAlerts() {
        return emergencyAlerts;
    }

    public void setEmergencyAlerts(ArrayList<EmergencyAlert> emergencyAlerts) {
        this.emergencyAlerts = emergencyAlerts;
    }

    public void clearEmergencyAlert(EmergencyAlert alert) {
        emergencyAlerts.remove(alert);
    }

    // Remove @Override since User class doesn't have this method
    public void displayUserInfo() {
        System.out.printf("Doctor Name: %s\nDoctor ID: %s\nDoctor Email: %s\n", 
                this.getName(), this.getId(), this.getEmail());
    }
}
