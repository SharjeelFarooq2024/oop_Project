package com.myapp.backend.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.myapp.backend.model.Doctor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    private static final String FILE_PATH = System.getProperty("user.dir") + "/data/Doctors.json";
    private final ObjectMapper mapper;

    public DoctorDAO() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        initializeFile();
    }

    private void initializeFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                saveDoctors(new ArrayList<>());
            }
        } catch (IOException e) {
            System.err.println("Error initializing doctors file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized Doctor findById(String doctorId) {
        List<Doctor> doctors = loadDoctors();
        return doctors.stream()
            .filter(d -> d.getId() != null && d.getId().equals(doctorId))
            .findFirst()
            .orElse(null);
    }

    public List<Doctor> loadDoctors() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<List<Doctor>>() {});
        } catch (IOException e) {
            System.err.println("Error reading doctors: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void createSampleDoctors() {
        try {
            List<Doctor> doctors = new ArrayList<>();
            doctors.add(new Doctor("Dr. John Smith", "john@hospital.com", "password", "Cardiology"));
            doctors.add(new Doctor("Dr. Sarah Johnson", "sarah@hospital.com", "password", "Neurology"));
            doctors.add(new Doctor("Dr. Michael Chen", "michael@hospital.com", "password", "Orthopedics"));
            doctors.add(new Doctor("Dr. Emily Davis", "emily@hospital.com", "password", "Pediatrics"));
            doctors.add(new Doctor("Dr. Robert Wilson", "robert@hospital.com", "password", "Dermatology"));
            
            saveDoctors(doctors);
            System.out.println("Sample doctors created successfully!");
        } catch (IOException e) {
            System.out.println("Failed to create sample doctors: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Doctor findByEmail(String email) {
        List<Doctor> doctors = loadDoctors();
        for (Doctor doctor : doctors) {
            if (email != null && email.equals(doctor.getEmail())) {
                return doctor;
            }
        }
        return null;
    }

    public void addDoctor(Doctor doctor) throws IOException {
        List<Doctor> doctors = loadDoctors();
        doctors.add(doctor);
        saveDoctors(doctors);
    }

    public synchronized void updateDoctor(Doctor doctor) throws IOException {
        if (doctor == null || doctor.getId() == null) {
            throw new IOException("Invalid doctor data");
        }

        List<Doctor> doctors = loadDoctors();
        boolean found = false;

        for (int i = 0; i < doctors.size(); i++) {
            if (doctor.getId() != null && doctor.getId().equals(doctors.get(i).getId())) {
                // Preserve existing lists if they're empty in the update
                Doctor existingDoctor = doctors.get(i);
                if (doctor.getAppointments() == null || doctor.getAppointments().isEmpty()) {
                    doctor.setAppointments(existingDoctor.getAppointments());
                }
                if (doctor.getPatientIds() == null || doctor.getPatientIds().isEmpty()) {
                    doctor.setPatientIds(existingDoctor.getPatientIds());
                }
                doctors.set(i, doctor);
                found = true;
                break;
            }
        }
        

        if (!found) {
            doctors.add(doctor);
        }

        saveDoctors(doctors);
    }

    private synchronized void saveDoctors(List<Doctor> doctors) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, doctors);
    }
}
