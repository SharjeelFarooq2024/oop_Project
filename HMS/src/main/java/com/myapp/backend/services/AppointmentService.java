package com.myapp.backend.services;

import com.myapp.backend.dao.AppointmentDAO;
import com.myapp.backend.model.Appointment;

import java.io.IOException;
import java.util.List;

public class AppointmentService {
    private static final AppointmentDAO appointmentDAO = new AppointmentDAO();

    // Fetch appointments for a specific patient
    public static List<Appointment> getAppointmentsForPatient(String patientId) {
        List<Appointment> appointments = appointmentDAO.findByPatientId(patientId);
        // Already sorted in descending order by DAO, no need to sort again
        return appointments;
    }

    public static boolean bookAppointment(Appointment appointment) {
        try {
            AppointmentDAO.AppointmentStatus status = appointmentDAO.addAppointment(appointment);
            System.out.println("Appointment booking status: " + status);
            return status == AppointmentDAO.AppointmentStatus.SUCCESS;
        } catch (Exception e) {
            System.err.println("Error booking appointment: " + e.getMessage());
            e.printStackTrace();
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

    // Force sync all appointments
    public static void forceSyncAppointments() {
        try {
            System.out.println("Starting forced appointment synchronization...");
            appointmentDAO.syncAllAppointments();
            System.out.println("Appointment synchronization completed successfully.");
        } catch (Exception e) {
            System.err.println("Error during synchronization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // For manual syncing via command line
    public static void main(String[] args) {
        forceSyncAppointments();
    }
}
