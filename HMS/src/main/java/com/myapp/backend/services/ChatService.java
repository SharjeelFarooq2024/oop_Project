package com.myapp.backend.services;

import com.myapp.backend.model.ChatMessage;
import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Patient;
import com.myapp.backend.dao.DoctorDAO;
import com.myapp.backend.dao.PatientDAO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatService {
    // Map of conversation ID to list of messages
    private static final Map<String, List<ChatMessage>> conversations = new HashMap<>();
    private static final DoctorDAO doctorDAO = new DoctorDAO();
    private static final PatientDAO patientDAO = new PatientDAO();
    
    // Create a unique conversation ID from two user IDs
    public static String createConversationId(String userIdA, String userIdB) {
        // Ensure consistent ordering of IDs to create conversation
        if (userIdA.compareTo(userIdB) < 0) {
            return userIdA + "_" + userIdB;
        } else {
            return userIdB + "_" + userIdA;
        }
    }
    
    // Send a message in a conversation with option to send email notification
    public static void sendMessage(String senderId, String receiverId, String content, boolean sendEmailNotification) {
        String conversationId = createConversationId(senderId, receiverId);
        
        if (!conversations.containsKey(conversationId)) {
            conversations.put(conversationId, new ArrayList<>());
        }
        
        ChatMessage message = new ChatMessage(senderId, receiverId, content, LocalDateTime.now());
        conversations.get(conversationId).add(message);
        
        // Notify the receiver through the app
        NotificationService.sendNotification(receiverId, "New message from user ID: " + senderId);
        
        // Send email notification if requested
        if (sendEmailNotification) {
            sendEmailForMessage(senderId, receiverId, content);
        }
    }
    
    // Original method for backward compatibility
    public static void sendMessage(String senderId, String receiverId, String content) {
        sendMessage(senderId, receiverId, content, false);
    }
    
    private static void sendEmailForMessage(String senderId, String receiverId, String content) {
        // Get sender and receiver information
        String senderName = "User";
        String receiverEmail = null;
        
        // Identify if sender is doctor or patient
        Doctor doctor = doctorDAO.findById(senderId);
        if (doctor != null) {
            senderName = "Dr. " + doctor.getName();
            Patient patient = patientDAO.findById(receiverId);
            if (patient != null) {
                receiverEmail = patient.getEmail();
            }
        } else {
            Patient patient = patientDAO.findById(senderId);
            if (patient != null) {
                senderName = patient.getName();
                Doctor receiverDoctor = doctorDAO.findById(receiverId);
                if (receiverDoctor != null) {
                    receiverEmail = receiverDoctor.getEmail();
                }
            }
        }
        
        // Send email if we have receiver email
        if (receiverEmail != null && !receiverEmail.isEmpty()) {
            String subject = "New HMS Message from " + senderName;
            String emailContent = 
                "Hello,\n\n" +
                "You have received a new message from " + senderName + " in the Hospital Management System:\n\n" +
                "\"" + content + "\"\n\n" +
                "Please log in to the HMS system to respond.\n\n" +
                "Regards,\nHMS Team";
                
            NotificationService.sendEmailNotification(receiverEmail, subject, emailContent);
        }
    }
    
    // Get all messages for a conversation between two users
    public static List<ChatMessage> getConversation(String userIdA, String userIdB) {
        String conversationId = createConversationId(userIdA, userIdB);
        return conversations.getOrDefault(conversationId, new ArrayList<>());
    }
    
    // Get conversations for a specific user
    public static Map<String, List<ChatMessage>> getUserConversations(String userId) {
        Map<String, List<ChatMessage>> userConversations = new HashMap<>();
        
        for (Map.Entry<String, List<ChatMessage>> entry : conversations.entrySet()) {
            String conversationId = entry.getKey();
            
            if (conversationId.contains(userId)) {
                userConversations.put(conversationId, entry.getValue());
            }
        }
        
        return userConversations;
    }
    
    // Mark all messages as read for a conversation
    public static void markConversationAsRead(String userId, String otherUserId) {
        String conversationId = createConversationId(userId, otherUserId);
        
        if (conversations.containsKey(conversationId)) {
            List<ChatMessage> messages = conversations.get(conversationId);
            
            for (ChatMessage message : messages) {
                if (message.getReceiverId().equals(userId) && !message.isRead()) {
                    message.markAsRead();
                }
            }
        }
    }
    
    // Get unread message count for a user
    public static int getUnreadMessageCount(String userId) {
        int count = 0;
        
        for (List<ChatMessage> messages : conversations.values()) {
            for (ChatMessage message : messages) {
                if (message.getReceiverId().equals(userId) && !message.isRead()) {
                    count++;
                }
            }
        }
        
        return count;
    }
}