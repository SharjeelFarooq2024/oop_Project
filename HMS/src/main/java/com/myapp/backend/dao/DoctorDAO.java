package com.myapp.backend.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.myapp.backend.model.Doctor;
import com.myapp.backend.util.ErrorHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DoctorDAO {
    private static final String FILE_PATH = System.getProperty("user.dir") + "/data/Doctors.json";
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Doctor> loadDoctors() {
        File file = new File(FILE_PATH);
        System.out.println("Looking for doctor file at: " + FILE_PATH);
        if (!file.exists()) {
            System.out.println("Doctor file not found! Creating sample doctors...");
            createSampleDoctors();
        }
    
        try {
            List<Doctor> doctors = mapper.readValue(file, new TypeReference<List<Doctor>>() {});
            System.out.println("Loaded " + doctors.size() + " doctors.");
            for (Doctor doc : doctors) {
                System.out.println(doc.getName() + " - " + doc.getSpecialization());
            }
            return doctors;
        } catch (IOException e) {
            ErrorHandler.handleException(
                e, 
                false, 
                "Data Loading Error", 
                "Failed to load doctors data. Creating sample data instead."
            );
            createSampleDoctors();
            return loadDoctors(); // Try again after creating sample data
        }
    }

    public void createSampleDoctors() {
        try {
            List<Doctor> doctors = new ArrayList<>();
            doctors.add(new Doctor("Dr. John Smith", "john@hospital.com", "password", "Cardiology"));
            doctors.add(new Doctor("Dr. Sarah Johnson", "sarah@hospital.com", "password", "Neurology"));
            doctors.add(new Doctor("Dr. Michael Chen", "michael@hospital.com", "password", "Orthopedics"));
            doctors.add(new Doctor("Dr. Emily Davis", "emily@hospital.com", "password", "Pediatrics"));
            doctors.add(new Doctor("Dr. Robert Wilson", "robert@hospital.com", "password", "Dermatology"));
            
            // Ensure directory exists
            File file = new File(FILE_PATH);
            file.getParentFile().mkdirs();
            
            // Save doctors to file
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, doctors);
            System.out.println("Sample doctors created successfully!");
        } catch (IOException e) {
            System.out.println("Failed to create sample doctors: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Doctor findByEmail(String email) {
        List<Doctor> doctors = loadDoctors();
        for (Doctor doctor : doctors) {
            if (doctor.getEmail().equals(email)) {
                return doctor;
            }
        }
        return null;
    }
}
