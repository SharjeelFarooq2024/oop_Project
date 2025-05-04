package com.myapp.backend.services;

import com.myapp.backend.model.ChatMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatService {
    // Map of conversation ID to list of messages
    private static final Map<String, List<ChatMessage>> conversations = new HashMap<>();
    
    // Create a unique conversation ID from two user IDs
    public static String createConversationId(String userIdA, String userIdB) {
        // Ensure consistent ordering of IDs to create conversation
        if (userIdA.compareTo(userIdB) < 0) {
            return userIdA + "_" + userIdB;
        } else {
            return userIdB + "_" + userIdA;
        }
    }
    
    // Send a message in a conversation
    public static void sendMessage(String senderId, String receiverId, String content) {
        String conversationId = createConversationId(senderId, receiverId);
        
        if (!conversations.containsKey(conversationId)) {
            conversations.put(conversationId, new ArrayList<>());
        }
        
        ChatMessage message = new ChatMessage(senderId, receiverId, content, LocalDateTime.now());
        conversations.get(conversationId).add(message);
        
        // Optionally notify the receiver
        NotificationService.sendNotification(receiverId, "New message from user ID: " + senderId);
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