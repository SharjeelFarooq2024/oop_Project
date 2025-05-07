package com.myapp.backend.services;

import java.io.IOException;

import com.myapp.backend.dao.PatientDAO;
import com.myapp.backend.model.Patient;

public class PatientService {
    private PatientDAO dao = new PatientDAO(); // Instance of PatientDAO

    // Logs in a patient by matching email and password in the JSON file
    public Patient login(String email, String password) {
        System.out.println("PatientService.login() - Attempting login with email: " + email);
        Patient patient = dao.findByEmail(email); // Use the instance of PatientDAO
        System.out.println("PatientService.login() - Patient found: " + (patient != null ? "yes" : "no"));
        
        if (patient != null) {
            System.out.println("PatientService.login() - Comparing passwords");
            if (patient.getPassword().equals(password)) {
                System.out.println("PatientService.login() - Password match successful");
                return patient;
            }
            System.out.println("PatientService.login() - Password match failed");
        }
        return null;
    }

    // Registers a new patient after validation
    public boolean registerNewPatient(String name, String email, String password) {
        // Check if the patient already exists using the PatientDAO instance
        if (dao.findByEmail(email) != null) { // Use the instance of PatientDAO
            System.out.println("Patient with this email already exists.");
            return false;
        }

        // Create a new patient and add to the file
        Patient newPatient = new Patient(name, email, password);
        
        try {
            dao.addPatient(newPatient); // Use the instance of PatientDAO
            System.out.println("New patient registered successfully.");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error registering patient: " + e.getMessage());
            return false;
        }
    }

    // Method to get the logged-in patient
    public static Patient getLoggedInPatient() {
        return SessionManager.getLoggedInPatient();
    }
}
