package com.myapp.backend.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Patient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
        if (!file.exists() || file.length() == 0) { // Handle empty file
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<Patient>>() {});
        } catch (IOException e) {
            System.err.println("Error reading patients file: " + FILE_PATH + ". Returning empty list. Error: " + e.getMessage());
            // Optionally, create an empty file or return an empty list to prevent further errors.
            // For now, just returning an empty list.
            return new ArrayList<>();
        }
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

    public void updatePatient(Patient updatedPatient) throws IOException {
        if (updatedPatient == null || updatedPatient.getId() == null) {
            throw new IllegalArgumentException("Invalid patient data for update");
        }

        List<Patient> patients = loadPatients();
        boolean found = false;

        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getId().equals(updatedPatient.getId())) {
                patients.set(i, updatedPatient);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Patient with ID " + updatedPatient.getId() + " not found");
        }

        savePatients(patients);
    }

    /**
     * Retrieves all patients from the data source.
     * @return A list of all patients.
     */
    public List<Patient> getAllPatients() {
        try {
            return loadPatients();
        } catch (IOException e) {
            System.err.println("Failed to load all patients: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList(); // Return an empty list on error
        }
    }
}
