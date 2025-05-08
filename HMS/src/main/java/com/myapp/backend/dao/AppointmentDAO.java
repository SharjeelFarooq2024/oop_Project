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
            if (appointment == null || appointment.getDoctorId() == null) {
                return AppointmentStatus.ERROR;
            }

            List<Appointment> appointments = loadAppointments();
            
            // Sort appointments before checking for duplicates
            appointments.sort((a1, a2) -> {
                int dateCompare = a2.getDate().compareTo(a1.getDate());
                return dateCompare != 0 ? dateCompare : a2.getTime().compareTo(a1.getTime());
            });

            // Check for duplicate appointments
            for (Appointment a : appointments) {
                if (a.getDoctorId().equals(appointment.getDoctorId()) &&
                    a.getDate().equals(appointment.getDate()) &&
                    a.getTime().equals(appointment.getTime()) &&
                    (a.isPending() || a.isScheduled())) {
                    return AppointmentStatus.DUPLICATE;
                }
            }

            // Add new appointment at the beginning of the list
            appointments.add(0, appointment);
            saveAppointments(appointments);

            // Update doctor's data
            Doctor doctor = doctorDAO.findById(appointment.getDoctorId());
            if (doctor != null) {
                doctor.addAppointment(appointment);
                doctor.addPatientId(appointment.getPatientId());
                doctorDAO.updateDoctor(doctor);
            }
            
            return AppointmentStatus.SUCCESS;
        } catch (IOException e) {
            System.err.println("Error saving appointment: " + e.getMessage());
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
            
            // Sort appointments by date and time
            patientAppointments.sort((a1, a2) -> {
                int dateCompare = a2.getDate().compareTo(a1.getDate());
                return dateCompare != 0 ? dateCompare : a2.getTime().compareTo(a1.getTime());
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
            // Sort appointments by date and time
            appointments.sort((a1, a2) -> {
                int dateCompare = a2.getDate().compareTo(a1.getDate());
                return dateCompare != 0 ? dateCompare : a2.getTime().compareTo(a1.getTime());
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
        
        // First reset all doctors' appointment lists
        for (Doctor doctor : doctors) {
            if (doctor.getId() != null) {
                doctor.setAppointments(new ArrayList<>());
            }
        }
        
        // Add appointments to corresponding doctors
        for (Appointment appointment : allAppointments) {
            if (appointment.getDoctorId() != null) {
                for (Doctor doctor : doctors) {
                    if (doctor.getId() != null && doctor.getId().equals(appointment.getDoctorId())) {
                        doctor.addAppointment(appointment);
                        if (!appointment.getStatus().equals("Rejected")) {
                            doctor.addPatientId(appointment.getPatientId());
                        }
                        break;
                    }
                }
            }
        }
        
        // Save all doctors with updated appointments
        for (Doctor doctor : doctors) {
            if (doctor.getId() != null) {
                doctorDAO.updateDoctor(doctor);
            }
        }
    }
}
