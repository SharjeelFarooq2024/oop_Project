package com.myapp.backend.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.myapp.backend.notification.EmailNotification;

public class NotificationService {
    private static final Map<String, List<String>> userNotifications = new HashMap<>();
    private static final EmailNotification emailNotification = new EmailNotification();
    
    // Add the missing method
    public static void sendEmailNotification(String email, String subject, String message) {
        emailNotification.sendEmail(email, subject, message);
    }
    
    // Send a notification to a specific user
    public static void sendNotification(String userId, String message) {
        if (!userNotifications.containsKey(userId)) {
            userNotifications.put(userId, new ArrayList<>());
        }
        
        String timestamp = LocalDateTime.now().toString();
        String formattedMessage = String.format("[%s] %s", timestamp, message);
        userNotifications.get(userId).add(formattedMessage);
        
        System.out.println("Notification sent to user " + userId + ": " + message);
    }
    
    // Get all notifications for a user
    public static List<String> getUserNotifications(String userId) {
        return userNotifications.getOrDefault(userId, new ArrayList<>());
    }
    
    // Clear all notifications for a user
    public static void clearNotifications(String userId) {
        userNotifications.remove(userId);
    }
    
    // Send appointment reminder to a patient
    public static void sendAppointmentReminder(String patientId, String doctorName, String date, String time) {
        String message = String.format("Reminder: You have an appointment with Dr. %s on %s at %s", 
                                       doctorName, date, time);
        sendNotification(patientId, message);
    }
    
    // Send appointment confirmation to a patient
    public static void sendAppointmentConfirmation(String patientId, String doctorName, String date, String time) {
        String message = String.format("Your appointment with Dr. %s on %s at %s has been confirmed", 
                                       doctorName, date, time);
        sendNotification(patientId, message);
    }
    
    // Send prescription notification to a patient
    public static void sendPrescriptionNotification(String patientId, String doctorName) {
        String message = String.format("Dr. %s has prescribed new medication for you. Check your medical records.", 
                                       doctorName);
        sendNotification(patientId, message);
    }
    
    // Send new appointment notification to a doctor
    public static void sendNewAppointmentNotification(String doctorId, String patientName, String date, String time) {
        String message = String.format("New appointment request from patient %s on %s at %s", 
                                       patientName, date, time);
        sendNotification(doctorId, message);
    }
}