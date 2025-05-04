package com.myapp.backend.services;

import com.myapp.backend.model.Patient;

public class SessionManager {
    private static Patient loggedInPatient;

    // Set the logged-in patient
    public static void setLoggedInPatient(Patient patient) {
        loggedInPatient = patient;
    }

    // Get the logged-in patient
    public static Patient getLoggedInPatient() {
        return loggedInPatient;
    }

    // Clear session when logging out
    public static void clearSession() {
        loggedInPatient = null;
    }
}
