package com.myapp.frontend.controllers;

import com.myapp.backend.model.Patient;
import com.myapp.backend.services.PatientService;
import java.util.UUID;

public class RegisterController {
    public void handleRegister(String name, String email, int age) {
        String id = UUID.randomUUID().toString(); // Generate a unique ID
        Patient patient = new Patient(id, name, email, age);
        try {
            new PatientService().registerNewPatient(patient);
        } catch (Exception e) {
            e.printStackTrace(); // Handle exception appropriately
        }
    }
}
