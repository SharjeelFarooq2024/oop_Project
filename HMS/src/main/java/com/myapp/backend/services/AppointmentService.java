package com.myapp.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.backend.model.Appointment;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class AppointmentService {

    private static final String APPOINTMENT_FILE_PATH = "path/to/bookappointment.json"; // Specify the path to your JSON file

    // Method to save appointment
    public static void saveAppointmentToJson(Appointment appointment) {
        try {
            // Create ObjectMapper to convert the object to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            
            // Read the current list of appointments from the file
            List<Appointment> appointments = objectMapper.readValue(new File(APPOINTMENT_FILE_PATH), objectMapper.getTypeFactory().constructCollectionType(List.class, Appointment.class));
            
            // Add the new appointment to the list
            appointments.add(appointment);
            
            // Write the updated list of appointments back to the file
            objectMapper.writeValue(new File(APPOINTMENT_FILE_PATH), appointments);
            
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the case where the file cannot be read or written
        }
    }
}
