package com.myapp.backend.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.myapp.backend.model.Patient;
import com.myapp.backend.model.VitalSign;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// Data Access Object for VitalSign operations
// Handles storage and retrieval of patient vital signs
public class VitalSignDAO {

    private static final String FILE_PATH = "data/vitals.json"; // Path to JSON file storing vital signs
    private static final ObjectMapper mapper = new ObjectMapper(); // Jackson JSON mapper (static for efficiency)

    // Static initializer block to configure the mapper
    static { 
        mapper.registerModule(new JavaTimeModule()); // Add support for Java 8 date/time types
    }

    // Save a new vital sign record to the database
    // Adds the vital sign to the existing list and writes to file
    public static void saveVital(VitalSign vital) {
        List<VitalSign> vitals = getAllVitals();
        vitals.add(vital);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), vitals);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get the most recent vital sign record for a specific patient
    // Returns an Optional that may contain the latest vital sign
    public static Optional<VitalSign> getLatestVitalForPatient(String patientId) {
        List<VitalSign> allVitals = getAllVitals();
        return allVitals.stream()
            .filter(v -> v.getPatientId().equals(patientId)) // Filter by patient ID
            .max(Comparator.comparing(VitalSign::getTimestamp)); // Find the most recent by timestamp
    }

    // Get all vital signs from the database
    // Creates file if it doesn't exist
    public static List<VitalSign> getAllVitals() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // Ensure directory exists
                file.createNewFile();
                return new ArrayList<>();
            }

            return mapper.readValue(file, new TypeReference<List<VitalSign>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Get vitals for a specific patient
    public static List<VitalSign> getVitalsByPatientId(String patientId) {
        List<VitalSign> result = new ArrayList<>();
        for (VitalSign v : getAllVitals()) {
            if (v.getPatientId().equals(patientId)) {
                result.add(v);
            }
        }
        return result;
    }

    // Get the latest vital for a patient (optional convenience method)
    public static VitalSign getLatestVitalByPatientId(String patientId) {
        List<VitalSign> vitals = getVitalsByPatientId(patientId);
        if (vitals.isEmpty()) return null;
        vitals.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp())); // descending
        return vitals.get(0);
    }

    public static void saveVitalSignForPatient(String patientId, VitalSign vitalSign) {
        // Save to vitals.json
        saveVital(vitalSign);
        
        // Update the patient object directly
        try {
            PatientDAO patientDAO = new PatientDAO();
            Patient patient = patientDAO.findById(patientId);
            if (patient != null) {
                patient.addVitalSign(vitalSign); // Make sure Patient class has addVitalSign
                patientDAO.updatePatient(patient);
            }
        } catch (IOException e) {
            System.err.println("Failed to update patient with new vital sign: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
