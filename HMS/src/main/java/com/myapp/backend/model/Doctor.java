package com.myapp.backend.model;

import java.util.ArrayList;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Doctor extends User {
    private String specialization;
    private ArrayList<String> patientIds;
    private ArrayList<Appointment> appointments;
    private ArrayList<EmergencyAlert> emergencyAlerts;
    
    public Doctor() {
        super();
        initializeLists();
    }

    public Doctor(String name, String email, String password, String specialization) {
        super(name, email, password);
        this.specialization = specialization;
        initializeLists();
    }

    private void initializeLists() {
        this.patientIds = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.emergencyAlerts = new ArrayList<>();
    }

    public void addPatientId(String patientId) {
        if (this.patientIds == null) {
            this.patientIds = new ArrayList<>();
        }
        if (!this.patientIds.contains(patientId)) {
            patientIds.add(patientId);
        }
    }

    public void removePatientId(String patientId) {
        if (patientIds != null) {
            patientIds.remove(patientId);
        }
    }

    public void approveAppointment(String appointmentId) {
        if (appointments != null) {
            for (Appointment appointment : appointments) {
                if (appointment.getAppointmentId().equals(appointmentId)) {
                    appointment.markAsScheduled();
                    System.out.println("Appointment with ID " + appointmentId + " has been approved.");
                    return;
                }
            }
        }
        System.out.println("Appointment with ID " + appointmentId + " not found.");
    }

    public void rejectAppointment(String appointmentId) {
        if (appointments != null) {
            for (Appointment appointment : appointments) {
                if (appointment.getAppointmentId().equals(appointmentId)) {
                    appointment.setStatus("Rejected");
                    System.out.println("Appointment with ID " + appointmentId + " has been rejected.");
                    return;
                }
            }
        }
        System.out.println("Appointment with ID " + appointmentId + " not found.");
    }

    public void giveFeedback(Patient patient, String comment, String medicationPrescribed) {
        Feedback feedback = new Feedback(comment, this.getName(), medicationPrescribed, LocalDateTime.now());
        patient.addFeedback(feedback);
    }

    public void receiveEmergencyAlert(EmergencyAlert alert) {
        if (this.emergencyAlerts == null) {
            this.emergencyAlerts = new ArrayList<>();
        }
        this.emergencyAlerts.add(alert);
        System.out.println("Emergency alert received from patient: " + alert.getPatientName());
    }

    public void addAppointment(Appointment appointment) {
        if (appointment == null || appointment.getDoctorId() == null || 
            !appointment.getDoctorId().equals(this.getId())) {
            return;
        }
        
        // Initialize appointments list if null
        if (this.appointments == null) {
            this.appointments = new ArrayList<>();
        }
        
        // Remove existing appointment if it exists
        appointments.removeIf(existingAppointment -> 
            existingAppointment != null && 
            existingAppointment.getAppointmentId() != null &&
            existingAppointment.getAppointmentId().equals(appointment.getAppointmentId())
        );
        
        // Add the new appointment
        appointments.add(appointment);

        // Initialize patientIds list if null
        if (this.patientIds == null) {
            this.patientIds = new ArrayList<>();
        }
        
        // Add patient ID if not already present and the appointment is not rejected
        if (appointment.getPatientId() != null && 
            !this.patientIds.contains(appointment.getPatientId()) && 
            !"Rejected".equalsIgnoreCase(appointment.getStatus())) {
            this.patientIds.add(appointment.getPatientId());
        }
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public ArrayList<String> getPatientIds() {
        return patientIds;
    }

    public void setPatientIds(ArrayList<String> patientIds) {
        this.patientIds = patientIds;
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
        if (emergencyAlerts != null) {
            emergencyAlerts.remove(alert);
        }
    }

    public void displayUserInfo() {
        System.out.printf("Doctor Name: %s%nDoctor ID: %s%nDoctor Email: %s%nSpecialization: %s%n", 
            getName(), getId(), getEmail(), getSpecialization());
    }
}
