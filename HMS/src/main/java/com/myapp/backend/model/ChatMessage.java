package com.myapp.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

// Represents a message sent between users in the chat functionality
public class ChatMessage {
    private String messageId;      // Unique identifier for the message
    private String senderId;       // ID of the user who sent the message
    private String receiverId;     // ID of the user who will receive the message
    private String content;        // Text content of the message
    private LocalDateTime timestamp; // When the message was sent
    private boolean read;          // Whether the message has been read by the recipient
    
    // Default constructor for Jackson JSON serialization
    public ChatMessage() {
        // Default constructor for Jackson
    }
    
    // Constructor with parameters to create a new chat message
    public ChatMessage(String senderId, String receiverId, String content, LocalDateTime timestamp) {
        this.messageId = UUID.randomUUID().toString();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.read = false;  // New messages are unread by default
    }
    
    // Getter for message ID
    public String getMessageId() {
        return messageId;
    }
    
    // Getter for sender ID
    public String getSenderId() {
        return senderId;
    }
    
    // Getter for receiver ID
    public String getReceiverId() {
        return receiverId;
    }
    
    // Getter for message content
    public String getContent() {
        return content;
    }
    
    // Getter for timestamp
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    // Getter for read status
    public boolean isRead() {
        return read;
    }
    
    // Setters
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setRead(boolean read) {
        this.read = read;
    }
    
    public void markAsRead() {
        this.read = true;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s", timestamp.toString(), senderId, content);
    }
}