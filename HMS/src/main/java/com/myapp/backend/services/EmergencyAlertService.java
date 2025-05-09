package com.myapp.backend.services;

import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.EmergencyAlert;
import com.myapp.backend.model.Patient;
import com.myapp.backend.dao.DoctorDAO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmergencyAlertService {
    private static final Map<String, List<EmergencyAlert>> doctorAlerts = new HashMap<>();
    private static final List<EmergencyAlert> allAlerts = new ArrayList<>();
    private static final DoctorDAO doctorDAO = new DoctorDAO();
    
    // Create and send an emergency alert from a patient
    public static EmergencyAlert createEmergencyAlert(Patient patient, String message) {
        EmergencyAlert alert = new EmergencyAlert(patient.getId(), patient.getName(), message);
        allAlerts.add(alert);
        
        // Send the alert to all registered doctors
        List<Doctor> doctors = doctorDAO.loadDoctors();
        for (Doctor doctor : doctors) {
            if (!doctorAlerts.containsKey(doctor.getId())) {
                doctorAlerts.put(doctor.getId(), new ArrayList<>());
            }
            doctorAlerts.get(doctor.getId()).add(alert);
            
            // Send a notification to the doctor
            NotificationService.sendNotification(doctor.getId(), 
                "EMERGENCY ALERT: Patient " + patient.getName() + " requires immediate attention!");
            
            // Send email notification to doctors if they have email addresses
            if (doctor.getEmail() != null && !doctor.getEmail().isEmpty()) {
                String subject = "URGENT: Patient " + patient.getName() + " - Critical Vital Signs";
                String emailContent = 
                    "Dear Dr. " + doctor.getName() + ",\n\n" +
                    "An emergency alert has been generated for patient " + patient.getName() + ":\n\n" +
                    message + "\n\n" +
                    "Please log in to the HMS system to view details and take appropriate action.\n\n" +
                    "This is an automated alert generated at " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n" +
                    "Regards,\nHMS Alert System";
                
                NotificationService.sendEmailNotification(doctor.getEmail(), subject, emailContent);
            }
        }
        
        System.out.println("Emergency alert created for patient " + patient.getName());
        return alert;
    }
    
    // Get all alerts for a specific doctor
    public static List<EmergencyAlert> getDoctorAlerts(String doctorId) {
        return doctorAlerts.getOrDefault(doctorId, new ArrayList<>());
    }
    
    // Get all unresolved alerts for a specific doctor
    public static List<EmergencyAlert> getDoctorUnresolvedAlerts(String doctorId) {
        List<EmergencyAlert> unresolvedAlerts = new ArrayList<>();
        List<EmergencyAlert> alerts = doctorAlerts.getOrDefault(doctorId, new ArrayList<>());
        
        for (EmergencyAlert alert : alerts) {
            if (!alert.isResolved()) {
                unresolvedAlerts.add(alert);
            }
        }
        
        return unresolvedAlerts;
    }
    
    // Mark an emergency alert as resolved
    public static void resolveAlert(String alertId, String doctorId) {
        if (doctorAlerts.containsKey(doctorId)) {
            for (EmergencyAlert alert : doctorAlerts.get(doctorId)) {
                if (alert.getAlertId().equals(alertId)) {
                    alert.resolve();
                    System.out.println("Alert resolved by doctor ID: " + doctorId);
                    
                    // Also update in the master list
                    for (EmergencyAlert masterAlert : allAlerts) {
                        if (masterAlert.getAlertId().equals(alertId)) {
                            masterAlert.resolve();
                            break;
                        }
                    }
                    
                    break;
                }
            }
        }
    }
    
    // Get all active emergency alerts in the system
    public static List<EmergencyAlert> getAllActiveAlerts() {
        List<EmergencyAlert> activeAlerts = new ArrayList<>();
        for (EmergencyAlert alert : allAlerts) {
            if (!alert.isResolved()) {
                activeAlerts.add(alert);
            }
        }
        return activeAlerts;
    }
}