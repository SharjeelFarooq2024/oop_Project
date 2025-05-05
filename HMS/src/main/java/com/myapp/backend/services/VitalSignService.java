package com.myapp.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.myapp.backend.model.VitalSign;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VitalSignService {

    private static final String VITALS_FILE_PATH = "data/Vitals.json";  // Path to the Vitals.json file

    // Method to retrieve the latest vitals for a given patient ID
    public static VitalSign getLatestVitals(String patientId) {
        try {
            // Load vitals data from JSON file
            File file = new File(VITALS_FILE_PATH);
            if (!file.exists()) {
                return null;  // If the file doesn't exist, return null
            }

            FileInputStream inputStream = new FileInputStream(file);
            ObjectMapper mapper = new ObjectMapper();

            // Read the data into a list of VitalSign objects
            List<VitalSign> vitalsList = mapper.readValue(inputStream, mapper.getTypeFactory().constructCollectionType(List.class, VitalSign.class));

            // Filter the vitals by the patient ID and sort them by timestamp to get the latest vitals
            return vitalsList.stream()
                    .filter(vital -> vital.getPatientId().equals(patientId))
                    .max((v1, v2) -> v1.getTimestamp().compareTo(v2.getTimestamp())) // Compare by timestamp
                    .orElse(null);  // Return null if no vitals are found

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to retrieve the vitals history for a given patient ID
    public static List<VitalSign> getVitalsHistory(String patientId) {
        try {
            // Load vitals data from JSON file
            File file = new File(VITALS_FILE_PATH);
            if (!file.exists()) {
                return null;  // If the file doesn't exist, return null
            }

            FileInputStream inputStream = new FileInputStream(file);
            ObjectMapper mapper = new ObjectMapper();

            // Read the data into a list of VitalSign objects
            List<VitalSign> vitalsList = mapper.readValue(inputStream, mapper.getTypeFactory().constructCollectionType(List.class, VitalSign.class));

            // Filter the vitals by the patient ID and return the result
            return vitalsList.stream()
                    .filter(vital -> vital.getPatientId().equals(patientId))
                    .sorted((v1, v2) -> v2.getTimestamp().compareTo(v1.getTimestamp())) // Sort by timestamp (descending)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to add a new vital sign entry to the JSON file
    public static void addVitals(String patientId, VitalSign vitals) {
        try {
            // Load current vitals data from the JSON file
            File file = new File(VITALS_FILE_PATH);
            List<VitalSign> vitalsList;

            // Check if the file exists, and read the data if it does
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                ObjectMapper mapper = new ObjectMapper();

                // Deserialize the file content into a list of VitalSign objects
                vitalsList = mapper.readValue(inputStream, mapper.getTypeFactory().constructCollectionType(List.class, VitalSign.class));
            } else {
                // If file doesn't exist, initialize an empty list
                vitalsList = new ArrayList<>();
            }

            // Add the new vital sign
            vitalsList.add(vitals);

            // Write the updated list back to the JSON file
            ObjectMapper mapper = new ObjectMapper();
            FileOutputStream outputStream = new FileOutputStream(file);
            mapper.writeValue(outputStream, vitalsList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
