package com.myapp.backend.services;

import com.myapp.backend.dao.PatientDAO;
import com.myapp.backend.model.Patient;
import java.util.UUID;

public class PatientService {
    private PatientDAO dao = new PatientDAO();

    public Patient login(String email, String password) {
        if (email.equals("patient@example.com") && password.equals("password")) {
            // Create a dummy patient with required fields
            return new Patient(UUID.randomUUID().toString(), "Dummy Patient", email, 30);
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

