package com.myapp.backend.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.myapp.backend.model.Appointment;
import com.myapp.backend.model.Doctor;

public class AppointmentCleanup {
    private static final String APPOINTMENTS_FILE = "data/Appointments.json";
    private static final String DOCTORS_FILE = "data/Doctors.json";
    private static final String BACKUP_FILE = "data/Appointments_backup.json";

    public static void main(String[] args) {
        try {
            System.out.println("Starting appointment cleanup process...");
            
            // Create object mapper
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            // Create backup of appointments file
            File appointmentsFile = new File(APPOINTMENTS_FILE);
            File backupFile = new File(BACKUP_FILE);
            if (appointmentsFile.exists()) {
                System.out.println("Creating backup of appointments file...");
                java.nio.file.Files.copy(
                    appointmentsFile.toPath(), 
                    backupFile.toPath(), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
            }
            
            // Load all doctors and create a set of doctor IDs
            Set<String> validDoctorIds = new HashSet<>();
            List<Doctor> doctors = mapper.readValue(new File(DOCTORS_FILE), new TypeReference<List<Doctor>>() {});
            for (Doctor doctor : doctors) {
                validDoctorIds.add(doctor.getId());
            }
            System.out.println("Found " + validDoctorIds.size() + " valid doctor IDs");
            
            // Load all appointments
            List<Appointment> appointments = mapper.readValue(new File(APPOINTMENTS_FILE), new TypeReference<List<Appointment>>() {});
            System.out.println("Loaded " + appointments.size() + " appointments");
            
            // Filter out appointments with invalid doctor IDs
            List<Appointment> validAppointments = new ArrayList<>();
            List<Appointment> invalidAppointments = new ArrayList<>();
            
            for (Appointment appointment : appointments) {
                if (validDoctorIds.contains(appointment.getDoctorId())) {
                    validAppointments.add(appointment);
                } else {
                    invalidAppointments.add(appointment);
                }
            }
            
            System.out.println("Found " + invalidAppointments.size() + " invalid appointments");
            System.out.println("Keeping " + validAppointments.size() + " valid appointments");
            
            // Save the filtered appointments back to the file
            mapper.writeValue(new File(APPOINTMENTS_FILE), validAppointments);
            
            // Print details of removed appointments
            System.out.println("\nRemoved appointments:");
            for (Appointment app : invalidAppointments) {
                System.out.println("- Appointment ID: " + app.getAppointmentId() +
                                 ", Doctor ID: " + app.getDoctorId() +
                                 ", Status: " + app.getStatus() +
                                 ", Date: " + app.getDate());
            }
            
            System.out.println("\nCleanup completed successfully!");
            System.out.println("A backup of the original appointments file was saved to: " + BACKUP_FILE);
            
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
