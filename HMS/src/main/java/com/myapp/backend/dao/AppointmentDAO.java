package com.myapp.backend.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.backend.model.Appointment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private static final String FILE_PATH = System.getProperty("user.dir") + "/data/Appointments.json"; // Absolute path
    private ObjectMapper mapper = new ObjectMapper();

    // Load appointments from the file
    private List<Appointment> loadAppointments() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<Appointment>>() {});
        } catch (IOException e) {
            throw new IOException("Error reading appointments from file.", e);
        }
    }

    // Save appointments to the file
    private void saveAppointments(List<Appointment> appointments) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();  // Create directory if it doesn't exist
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, appointments);
        } catch (IOException e) {
            throw new IOException("Error saving appointments to file.", e);
        }
    }

    // Add an appointment to the list and save to file
    public boolean addAppointment(Appointment appointment) {
        try {
            List<Appointment> appointments = loadAppointments();
    
            // Check for double booking
            for (Appointment a : appointments) {
                if (a.getDoctorId().equals(appointment.getDoctorId()) &&
                    a.getDate().equals(appointment.getDate()) &&
                    a.getTime().equals(appointment.getTime())) {
                    System.err.println("Doctor already has an appointment at this time.");
                    return false;
                }
            }
    
            appointments.add(appointment);
            saveAppointments(appointments);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving appointment: " + e.getMessage());
            return false;
        }
    }
    
    

    // Find appointments by patient ID
    public List<Appointment> findByPatientId(String patientId) {
        try {
            List<Appointment> appointments = loadAppointments();
            List<Appointment> patientAppointments = new ArrayList<>();
            for (Appointment a : appointments) {
                if (a.getPatientId().equals(patientId)) {
                    patientAppointments.add(a);
                }
            }
            return patientAppointments;
        } catch (IOException e) {
            System.err.println("Error loading appointments: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Additional helper method to ensure appointments are loaded once and used for various operations
    public List<Appointment> getAllAppointments() {
        try {
            return loadAppointments();
        } catch (IOException e) {
            System.err.println("Error loading all appointments: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
