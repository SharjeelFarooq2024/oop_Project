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
    private static final String FILE_PATH = "data/Appointments.json";
    private final ObjectMapper mapper;
    private final DoctorDAO doctorDAO;

    public AppointmentDAO() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        doctorDAO = new DoctorDAO();
        initializeFile();
    }

    private void initializeFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                saveAppointments(new ArrayList<>());
            }
        } catch (IOException e) {
            System.err.println("Error initializing appointments file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private synchronized List<Appointment> loadAppointments() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<Appointment>>() {});
        } catch (IOException e) {
            System.err.println("Error reading appointments: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private synchronized void saveAppointments(List<Appointment> appointments) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, appointments);
        // Sync doctor data after saving appointments
        syncAllAppointments();
    }

    public synchronized AppointmentStatus addAppointment(Appointment appointment) {
        try {
            // Validate appointment data
            if (appointment == null) {
                System.err.println("Error: Appointment object is null");
                return AppointmentStatus.ERROR;
            }
            if (appointment.getDoctorId() == null) {
                System.err.println("Error: Doctor ID is null");
                return AppointmentStatus.ERROR;
            }
            if (appointment.getPatientId() == null) {
                System.err.println("Error: Patient ID is null");
                return AppointmentStatus.ERROR;
            }

            List<Appointment> appointments = loadAppointments();
            
            // Check for duplicate appointments
            for (Appointment a : appointments) {
                if (a.getDoctorId().equals(appointment.getDoctorId()) &&
                    a.getDate().equals(appointment.getDate()) &&
                    a.getTime().equals(appointment.getTime()) &&
                    (a.isPending() || a.isScheduled())) {
                    System.out.println("Duplicate appointment found for doctor " + appointment.getDoctorId() + 
                        " at " + appointment.getDate() + " " + appointment.getTime());
                    return AppointmentStatus.DUPLICATE;
                }
            }

            // Add new appointment at the beginning of the list
            appointments.add(0, appointment);
            
            // Save appointments first
            try {
                saveAppointments(appointments);
            } catch (IOException e) {
                System.err.println("Error saving appointments: " + e.getMessage());
                e.printStackTrace();
                return AppointmentStatus.ERROR;
            }

            // Update doctor's data
            Doctor doctor = doctorDAO.findById(appointment.getDoctorId());
            if (doctor != null) {
                System.out.println("Processing appointment for doctor: " + doctor.getName() + " (ID: " + doctor.getId() + ")");
                System.out.println("Patient ID being added: " + appointment.getPatientId());
                System.out.println("Current patient IDs: " + (doctor.getPatientIds() != null ? doctor.getPatientIds().toString() : "null"));
                
                // Add appointment to doctor
                doctor.addAppointment(appointment);
                // Always add patientId to doctor's patientIds list
                doctor.addPatientId(appointment.getPatientId());
                System.out.println("[DEBUG] Before updateDoctor: " + doctor.getPatientIds());
                try {
                    doctorDAO.updateDoctor(doctor);
                    Doctor updatedDoctor = doctorDAO.findById(doctor.getId());
                    System.out.println("[DEBUG] After updateDoctor: " + updatedDoctor.getPatientIds());
                    if (!updatedDoctor.getPatientIds().contains(appointment.getPatientId())) {
                        System.err.println("[ERROR] Patient ID was NOT saved in doctor's patientIds! Trying again...");
                        updatedDoctor.addPatientId(appointment.getPatientId());
                        doctorDAO.updateDoctor(updatedDoctor);
                        Doctor reloadedDoctor = doctorDAO.findById(doctor.getId());
                        System.out.println("[DEBUG] After retry: " + reloadedDoctor.getPatientIds());
                    }
                } catch (IOException e) {
                    System.err.println("Error updating doctor data: " + e.getMessage());
                    e.printStackTrace();
                    return AppointmentStatus.ERROR;
                }
            } else {
                System.err.println("Error: Doctor not found with ID: " + appointment.getDoctorId());
                return AppointmentStatus.ERROR;
            }

            return AppointmentStatus.SUCCESS;
        } catch (Exception e) {
            System.err.println("Unexpected error in addAppointment: " + e.getMessage());
            e.printStackTrace();
            return AppointmentStatus.ERROR;
        }
    }

    public List<Appointment> findByPatientId(String patientId) {
        try {
            List<Appointment> appointments = loadAppointments();
            List<Appointment> patientAppointments = new ArrayList<>();
            
            for (Appointment a : appointments) {
                if (a.getPatientId().equals(patientId)) {
                    patientAppointments.add(a);
                }
            }
            
            // Sort appointments by date and time in ascending order (latest dates first)
            patientAppointments.sort((a1, a2) -> {
                int dateCompare = a2.getDate().compareTo(a1.getDate()); // Reversed comparison
                return dateCompare != 0 ? dateCompare : a2.getTime().compareTo(a1.getTime()); // Also reverse time comparison
            });
            
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
    
    public List<Appointment> getAllAppointments() {
        try {
            List<Appointment> appointments = loadAppointments();
            // Sort appointments by date and time in ascending order (latest dates first)
            appointments.sort((a1, a2) -> {
                int dateCompare = a2.getDate().compareTo(a1.getDate()); // Reversed comparison
                return dateCompare != 0 ? dateCompare : a2.getTime().compareTo(a1.getTime()); // Also reverse time comparison
            });
            return appointments;
        } catch (IOException e) {
            System.err.println("Error loading all appointments: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public synchronized void updateAppointment(Appointment updatedAppointment) throws IOException {
        if (updatedAppointment == null || updatedAppointment.getAppointmentId() == null) {
            throw new IOException("Invalid appointment data");
        }

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

    public synchronized void syncAllAppointments() throws IOException {
        List<Appointment> allAppointments = loadAppointments();
        List<Doctor> doctors = doctorDAO.loadDoctors();
        
        // First reset all doctors' appointment lists but preserve patient IDs
        for (Doctor doctor : doctors) {
            if (doctor.getId() != null) {
                doctor.setAppointments(new ArrayList<>());
            }
        }
        
        // Add appointments to corresponding doctors and update patient IDs
        for (Appointment appointment : allAppointments) {
            if (appointment.getDoctorId() != null) {
                for (Doctor doctor : doctors) {
                    if (doctor.getId() != null && doctor.getId().equals(appointment.getDoctorId())) {
                        doctor.addAppointment(appointment);
                        if (!"Rejected".equals(appointment.getStatus())) {
                            doctor.addPatientId(appointment.getPatientId());
                        }
                        break;
                    }
                }
            }
        }
        
        // Save all doctors with updated appointments and patient IDs
        for (Doctor doctor : doctors) {
            if (doctor.getId() != null) {
                doctorDAO.updateDoctor(doctor);
            }
        }
    }
}
