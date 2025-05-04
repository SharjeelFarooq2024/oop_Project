package com.myapp.backend.services;

import com.myapp.backend.model.Patient;
import com.myapp.backend.model.Doctor;

public class SessionManager {
    private static Patient loggedInPatient;
    private static Doctor loggedInDoctor;

    // Set the logged-in patient
    public static void setLoggedInPatient(Patient patient) {
        loggedInPatient = patient;
    }

    // Get the logged-in patient
    public static Patient getLoggedInPatient() {
        return loggedInPatient;
    }

    // Set the logged-in doctor
    public static void setLoggedInDoctor(Doctor doctor) {
        loggedInDoctor = doctor;
    }

    // Get the logged-in doctor
    public static Doctor getLoggedInDoctor() {
        return loggedInDoctor;
    }

    // Clear session when logging out
    public static void clearSession() {
        loggedInPatient = null;
        loggedInDoctor = null;
    }
}
