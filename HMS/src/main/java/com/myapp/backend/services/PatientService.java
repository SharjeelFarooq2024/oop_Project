package com.myapp.backend.services;


import com.myapp.backend.dao.PatientDAO;
import com.myapp.backend.model.Patient;

public class PatientService {
    private PatientDAO dao = new PatientDAO();

    

    // Inside PatientService.java
    public Patient login(String email, String password) {
    if (email.equals("patient@example.com") && password.equals("password")) {
        return new Patient(email, password); // Dummy Patient object
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

