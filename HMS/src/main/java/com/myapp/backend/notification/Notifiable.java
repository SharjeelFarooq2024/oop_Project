package com.myapp.backend.notification;

public interface Notifiable {
    void sendEmail(String recipient, String subject, String message);
    void sendSMS(String recipient, String message);
}