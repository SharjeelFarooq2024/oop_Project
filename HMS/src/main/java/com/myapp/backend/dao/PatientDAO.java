package com.myapp.backend.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.backend.model.Patient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    private static final String FILE_PATH = System.getProperty("user.dir") + "/data/Patients.json"; // Absolute path
    private ObjectMapper mapper = new ObjectMapper();

    // Load patients from the file
    private List<Patient> loadPatients() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        return mapper.readValue(file, new TypeReference<List<Patient>>() {});
    }

    // Save patients to the file
    private void savePatients(List<Patient> patients) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();  // Create directory if it doesn't exist
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, patients);
    }

    // Add a patient to the list and save to file
    public void addPatient(Patient patient) throws IOException {
        List<Patient> patients = loadPatients();
        patients.add(patient);
        savePatients(patients);
    }

    // Find a patient by email
    public Patient findByEmail(String email) {
        try {
            List<Patient> patients = loadPatients();
            for (Patient p : patients) {
                if (p.getEmail().equalsIgnoreCase(email)) {
                    return p;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Find a patient by ID
    public Patient findById(String id) {
        try {
            List<Patient> patients = loadPatients();
            for (Patient p : patients) {
                if (p.getId().equals(id)) {
                    return p;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
