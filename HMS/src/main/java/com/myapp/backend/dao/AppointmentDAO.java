package com.myapp.backend.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.myapp.backend.model.Appointment;
import com.myapp.backend.model.Doctor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private static final String FILE_PATH = System.getProperty("user.dir") + "/data/Appointments.json";
    private final ObjectMapper mapper;

    public AppointmentDAO() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    // Load appointments from the file
    private List<Appointment> loadAppointments() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            mapper.writeValue(file, new ArrayList<>());
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<Appointment>>() {});
        } catch (IOException e) {
            System.err.println("Error reading appointments: " + e.getMessage());
            e.printStackTrace();
            throw e; // Rethrow to handle in calling methods
        }
    }

    // Save appointments to the file
    private void saveAppointments(List<Appointment> appointments) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, appointments);
        } catch (IOException e) {
            System.err.println("Error saving appointments: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Add an appointment to the list and save to file
    public AppointmentStatus addAppointment(Appointment appointment) {
        try {
            List<Appointment> appointments = loadAppointments();
    
            // Check for duplicate appointments
            for (Appointment a : appointments) {
                if (a.getDoctorId().equals(appointment.getDoctorId()) &&
                    a.getDate().equals(appointment.getDate()) &&
                    a.getTime().equals(appointment.getTime())) {
                    return AppointmentStatus.DUPLICATE;
                }
            }
    
            appointments.add(appointment);
            saveAppointments(appointments);
            
            // Update doctor's appointments list
            try {
                DoctorDAO doctorDAO = new DoctorDAO();
                List<Doctor> doctors = doctorDAO.loadDoctors();
                for (Doctor doctor : doctors) {
                    if (doctor.getId().equals(appointment.getDoctorId())) {
                        doctor.addAppointment(appointment);
                        doctorDAO.updateDoctor(doctor);
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Warning: Could not update doctor's appointment list: " + e.getMessage());
                // Don't return error since appointment was still saved
            }
            
            return AppointmentStatus.SUCCESS;
    
        } catch (IOException e) {
            System.err.println("Error saving appointment: " + e.getMessage());
            e.printStackTrace();
            return AppointmentStatus.ERROR;
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
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public enum AppointmentStatus {
        SUCCESS,
        DUPLICATE,
        ERROR
    }
    
    // Get all appointments
    public List<Appointment> getAllAppointments() {
        try {
            return loadAppointments();
        } catch (IOException e) {
            System.err.println("Error loading all appointments: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Update an existing appointment
    public void updateAppointment(Appointment updatedAppointment) throws IOException {
        List<Appointment> appointments = loadAppointments();
        boolean found = false;
        
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getAppointmentId().equals(updatedAppointment.getAppointmentId())) {
                appointments.set(i, updatedAppointment);
                found = true;
                break;
            }
        }
        
        if (!found) {
            throw new IOException("Appointment not found with ID: " + updatedAppointment.getAppointmentId());
        }
        
        saveAppointments(appointments);
    }
}
