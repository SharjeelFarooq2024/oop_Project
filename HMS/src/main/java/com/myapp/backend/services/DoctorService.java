package com.myapp.backend.services;

import com.myapp.backend.dao.DoctorDAO;
import com.myapp.backend.model.Doctor;

public class DoctorService {
    private DoctorDAO dao = new DoctorDAO();

    // Logs in a doctor by matching email and password
    public Doctor login(String email, String password) {
        Doctor doctor = dao.findByEmail(email);
        if (doctor != null && doctor.getPassword().equals(password)) {
            return doctor;
        }
        return null;
    }

    // Get doctor by id
    public Doctor getDoctorById(String id) {
        for (Doctor doctor : dao.loadDoctors()) {
            if (doctor.getId().equals(id)) {
                return doctor;
            }
        }
        return null;
    }

    // Get logged-in doctor
    public static Doctor getLoggedInDoctor() {
        return SessionManager.getLoggedInDoctor();
    }
}