package com.myapp.backend.model;

import java.util.ArrayList;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.myapp.backend.services.NotificationService;

// Doctor class representing a medical doctor in the system
@JsonIgnoreProperties(ignoreUnknown = true)
public class Doctor extends User {
    private String specialization;                     // Doctor's medical specialization
    private ArrayList<String> patientIds;              // List of patient IDs assigned to the doctor
    private ArrayList<Appointment> appointments;       // Doctor's appointments
    private ArrayList<EmergencyAlert> emergencyAlerts; // Alerts for emergencies related to this doctor
    
    // Default constructor
    public Doctor() {
        super();
        initializeLists();
    }

    // Constructor with parameters
    public Doctor(String name, String email, String password, String specialization) {
        super(name, email, password);
        this.specialization = specialization;
        initializeLists();
    }

    // Initialize all required lists
    private void initializeLists() {
        this.patientIds = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.emergencyAlerts = new ArrayList<>();
    }

    // Add a patient to this doctor's patient list
    public void addPatientId(String patientId) {
        if (patientId == null) {
            System.out.println("Warning: Attempted to add null patientId");
            return;
        }

        // Initialize the list if null
        if (this.patientIds == null) {
            this.patientIds = new ArrayList<>();
            System.out.println("Initialized new patientIds list for doctor: " + this.getName());
        }

        // Add the patient ID if not already present
        if (!this.patientIds.contains(patientId)) {
            this.patientIds.add(patientId);
            System.out.println("Added patient ID " + patientId + " to doctor: " + this.getName());
        } else {
            System.out.println("Patient ID " + patientId + " already exists for doctor: " + this.getName());
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

    public void giveFeedback(Patient patient, String comment, String medicationPrescribed, String recommendedTests) {
        Feedback feedback = new Feedback(comment, this.getName(), medicationPrescribed, recommendedTests, LocalDateTime.now());
        patient.addFeedback(feedback);
        
        // Ensure this doctor has the patient in their list
        this.addPatientId(patient.getId());
        
        // Send notification to the patient
        NotificationService.sendPrescriptionNotification(patient.getId(), this.getName());
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
        
        // Initialize lists if null
        if (this.appointments == null) {
            this.appointments = new ArrayList<>();
        }
        if (this.patientIds == null) {
            this.patientIds = new ArrayList<>();
        }
        
        // Remove existing appointment if it exists
        appointments.removeIf(existingAppointment -> 
            existingAppointment != null && 
            existingAppointment.getAppointmentId() != null &&
            existingAppointment.getAppointmentId().equals(appointment.getAppointmentId())
        );
        
        // Add the new appointment
        appointments.add(appointment);

        // Always add patient ID for pending appointments
        if (appointment.getPatientId() != null && appointment.isPending()) {
            if (!patientIds.contains(appointment.getPatientId())) {
                patientIds.add(appointment.getPatientId());
                System.out.println("Added patient ID " + appointment.getPatientId() + " to doctor's patients list");
            }
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
