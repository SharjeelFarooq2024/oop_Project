package com.myapp.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.myapp.backend.dao.AppointmentDAO;
import com.myapp.backend.model.Appointment;
import com.myapp.backend.model.Doctor;

import java.io.IOException;
import java.util.List;

public class AppointmentService {
    private static final AppointmentDAO appointmentDAO = new AppointmentDAO();

    // Fetch appointments for a specific patient
    public static List<Appointment> getAppointmentsForPatient(String patientId) {
        return appointmentDAO.findByPatientId(patientId);
    }

    public static boolean bookAppointment(Appointment appointment) {
        try {
            AppointmentDAO.AppointmentStatus status = appointmentDAO.addAppointment(appointment);
            return status == AppointmentDAO.AppointmentStatus.SUCCESS;
        } catch (Exception e) {
            System.err.println("Error booking appointment: " + e.getMessage());
            return false;
        }
    }

    public static void syncAppointments() throws IOException {
        try {
            appointmentDAO.syncAllAppointments();
            System.out.println("Appointment synchronization completed successfully.");
        } catch (IOException e) {
            System.err.println("Error syncing appointments: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // For manual syncing via command line
    public static void main(String[] args) {
        System.out.println("Starting appointment synchronization...");
        try {
            syncAppointments();
        } catch (Exception e) {
            System.err.println("Error during synchronization: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
