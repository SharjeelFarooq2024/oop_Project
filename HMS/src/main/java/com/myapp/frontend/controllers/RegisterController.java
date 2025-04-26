package com.myapp.frontend.controllers;

import com.myapp.backend.model.Patient;
import com.myapp.backend.services.PatientService;

public class RegisterController {
    public void handleRegister(String name, int age) {
        Patient patient = new Patient(name, age);
        try {
            new PatientService().registerNewPatient(patient);
        } catch (Exception e) {
            e.printStackTrace(); // Handle exception appropriately
        }
    }
}
