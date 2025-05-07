package com.myapp.backend.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.myapp.backend.model.Appointment;
import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Patient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private static final String FILE_PATH = System.getProperty("user.dir") + "/data/Appointments.json";
    private final ObjectMapper mapper;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;

    public AppointmentDAO() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        doctorDAO = new DoctorDAO();
        patientDAO = new PatientDAO();
    }

    // Load appointments from the file
    private synchronized List<Appointment> loadAppointments() throws IOException {
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
            throw e;
        }
    }

    // Save appointments to the file
    private synchronized void saveAppointments(List<Appointment> appointments) throws IOException {
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
    public synchronized AppointmentStatus addAppointment(Appointment appointment) {
        try {
            List<Appointment> appointments = loadAppointments();
            
            // Check for duplicate appointments - only consider pending or scheduled appointments
            for (Appointment a : appointments) {
                if (a.getDoctorId().equals(appointment.getDoctorId()) &&
                    a.getDate().equals(appointment.getDate()) &&
                    a.getTime().equals(appointment.getTime()) &&
                    (a.getStatus().equalsIgnoreCase("Pending") || 
                     a.getStatus().equalsIgnoreCase("Scheduled"))) {
                    return AppointmentStatus.DUPLICATE;
                }
            }

            appointments.add(appointment);
            saveAppointments(appointments);

            // Update doctor's appointments and patient list
            updateDoctorWithNewPatient(appointment);
            
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
    public synchronized void updateAppointment(Appointment updatedAppointment) throws IOException {
        // First update the main appointments file
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
        updateDoctorWithNewPatient(updatedAppointment);
    }

    // Helper method to update doctor's appointments and patient list
    private synchronized void updateDoctorWithNewPatient(Appointment appointment) throws IOException {
        List<Doctor> doctors = doctorDAO.loadDoctors();
        for (Doctor doctor : doctors) {
            if (doctor.getId().equals(appointment.getDoctorId())) {
                // Update appointments list
                ArrayList<Appointment> doctorAppointments = doctor.getAppointments();
                if (doctorAppointments == null) {
                    doctorAppointments = new ArrayList<>();
                }
                doctorAppointments.add(appointment);
                doctor.setAppointments(doctorAppointments);

                // Add patient ID to doctor's patient list if not already present
                Patient patient = patientDAO.findById(appointment.getPatientId());
                if (patient != null) {
                    ArrayList<String> doctorPatients = doctor.getPatientIds();
                    if (doctorPatients == null) {
                        doctorPatients = new ArrayList<>();
                    }
                    if (!doctorPatients.contains(patient.getId())) {
                        doctorPatients.add(patient.getId());
                        doctor.setPatientIds(doctorPatients);
                    }
                }

                doctorDAO.updateDoctor(doctor);
                break;
            }
        }
    }

    // Sync method to fix any inconsistencies between appointments and doctor records
    public synchronized void syncAllAppointments() throws IOException {
        List<Appointment> allAppointments = loadAppointments();
        List<Doctor> doctors = doctorDAO.loadDoctors();
        
        // Clear all doctors' appointment lists first
        for (Doctor doctor : doctors) {
            doctor.setAppointments(new ArrayList<>());
        }
        
        // Add appointments to corresponding doctors
        for (Appointment appointment : allAppointments) {
            for (Doctor doctor : doctors) {
                if (doctor.getId().equals(appointment.getDoctorId())) {
                    doctor.addAppointment(appointment);
                    break;
                }
            }
        }
        
        // Save all doctors with updated appointments
        for (Doctor doctor : doctors) {
            doctorDAO.updateDoctor(doctor);
        }
    }
}
