package com.myapp.backend.services;


import com.myapp.backend.dao.PatientDAO;
import com.myapp.backend.model.Patient;

public class PatientService {
    private PatientDAO dao = new PatientDAO();

    public Patient login(String email, String password) {
        Patient patient = dao.findByEmail(email);
        if (patient != null && patient.getPassword().equals(password)) {
            return patient;
        }
        return null;
    }
    

    public void registerNewPatient(Patient patient) throws Exception {
        if (patient.getName().isBlank()) {
            throw new Exception("Patient name cannot be empty");
        }
        dao.addPatient(patient);
    }
}

