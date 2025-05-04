package com.myapp.backend.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.myapp.backend.model.VitalSign;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class VitalSignDAO {

    private static final String FILE_PATH = "data/vitals.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    // Save a new VitalSign entry
    public static void saveVital(VitalSign vital) {
        List<VitalSign> vitals = getAllVitals();
        vitals.add(vital);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), vitals);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Optional<VitalSign> getLatestVitalForPatient(String patientId) {
    List<VitalSign> allVitals = getAllVitals();
    return allVitals.stream()
        .filter(v -> v.getPatientId().equals(patientId))
        .max(Comparator.comparing(VitalSign::getTimestamp));
}

    // Get all vitals (used internally)
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
}
