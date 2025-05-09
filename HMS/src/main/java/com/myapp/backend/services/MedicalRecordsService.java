package com.myapp.backend.services;

import com.myapp.backend.dao.PatientDAO;
import com.myapp.backend.dao.VitalSignDAO;
import com.myapp.backend.model.Patient;
import com.myapp.backend.model.VitalSign;
import com.myapp.backend.model.Feedback;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MedicalRecordsService {
    
    /**
     * Get a complete medical summary for a patient
     */
    public static MedicalSummary getPatientMedicalSummary(String patientId) {
        PatientDAO patientDAO = new PatientDAO();
        Patient patient = patientDAO.findById(patientId);
        
        if (patient == null) {
            return null;
        }
        
        MedicalSummary summary = new MedicalSummary();
        summary.setPatient(patient);
        
        // Get latest vitals
        summary.setLatestVitals(VitalSignDAO.getLatestVitalByPatientId(patientId));
        
        // Get vitals history
        summary.setVitalsHistory(VitalSignDAO.getVitalsByPatientId(patientId));
        
        // Get feedback/consultation history
        summary.setConsultationHistory(patient.getFeedbacks());
        
        return summary;
    }
    
    public static class MedicalSummary {
        private Patient patient;
        private VitalSign latestVitals;
        private List<VitalSign> vitalsHistory;
        private List<Feedback> consultationHistory;
        
        // Getters and setters
        public Patient getPatient() { return patient; }
        public void setPatient(Patient patient) { this.patient = patient; }
        
        public VitalSign getLatestVitals() { return latestVitals; }
        public void setLatestVitals(VitalSign latestVitals) { this.latestVitals = latestVitals; }
        
        public List<VitalSign> getVitalsHistory() { return vitalsHistory; }
        public void setVitalsHistory(List<VitalSign> vitalsHistory) { this.vitalsHistory = vitalsHistory; }
        
        public List<Feedback> getConsultationHistory() { return consultationHistory; }
        public void setConsultationHistory(List<Feedback> consultationHistory) { this.consultationHistory = consultationHistory; }
    }
}