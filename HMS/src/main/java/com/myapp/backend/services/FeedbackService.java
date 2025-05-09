package com.myapp.backend.services;

import com.myapp.backend.dao.PatientDAO;
import com.myapp.backend.dao.DoctorDAO;
import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Patient;
import com.myapp.backend.model.Feedback;

import java.io.IOException;
import java.time.LocalDateTime;

public class FeedbackService {
    
    /**
     * Add doctor's feedback to a patient and save to database
     */
    public static boolean addFeedbackToPatient(
            String doctorId, String patientId, 
            String feedbackText, String medication, String tests) {
        
        try {
            DoctorDAO doctorDAO = new DoctorDAO();
            PatientDAO patientDAO = new PatientDAO();
            
            Doctor doctor = doctorDAO.findById(doctorId);
            Patient patient = patientDAO.findById(patientId);
            
            if (doctor == null || patient == null) {
                return false;
            }
            
            // Create feedback object
            Feedback feedback = new Feedback(feedbackText, doctor.getName(), medication, LocalDateTime.now());
            // Add test information if provided
            if (tests != null && !tests.isEmpty()) {
                feedback.setRecommendedTests(tests);
            }
            
            // Add feedback to patient
            patient.addFeedback(feedback);
            
            // Add patient to doctor's list if not already there
            doctor.addPatientId(patientId);
            
            // Save updated records
            patientDAO.updatePatient(patient);
            doctorDAO.updateDoctor(doctor);
            
            // Send notification
            NotificationService.sendPrescriptionNotification(patientId, doctor.getName());
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all feedback for a specific patient
     */
    public static java.util.List<Feedback> getPatientFeedback(String patientId) {
        PatientDAO patientDAO = new PatientDAO();
        Patient patient = patientDAO.findById(patientId);
        
        if (patient != null && patient.getFeedbacks() != null) {
            return patient.getFeedbacks();
        }
        
        return new java.util.ArrayList<>();
    }
}