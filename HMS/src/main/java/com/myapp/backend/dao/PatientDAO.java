package com.myapp.backend.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.myapp.backend.model.Patient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    private static final String FILE_PATH = "data" + File.separator + "Patients.json";
    private ObjectMapper mapper;

    public PatientDAO() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

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
            System.out.println("PatientDAO.findByEmail() - Loading patients from file: " + FILE_PATH);
            List<Patient> patients = loadPatients();
            System.out.println("PatientDAO.findByEmail() - Loaded " + patients.size() + " patients");
            
            for (Patient p : patients) {
                System.out.println("PatientDAO.findByEmail() - Comparing with patient email: " + p.getEmail());
                if (p.getEmail().equalsIgnoreCase(email)) {
                    System.out.println("PatientDAO.findByEmail() - Found matching patient: " + p.getName());
                    return p;
                }
            }
            System.out.println("PatientDAO.findByEmail() - No matching patient found");
        } catch (IOException e) {
            System.err.println("PatientDAO.findByEmail() - Error loading patients: " + e.getMessage());
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
