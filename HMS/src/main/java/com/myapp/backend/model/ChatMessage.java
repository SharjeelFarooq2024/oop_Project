package com.myapp.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatMessage {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;
    private boolean read;
    
    public ChatMessage() {
        // Default constructor for Jackson
    }
    
    public ChatMessage(String senderId, String receiverId, String content, LocalDateTime timestamp) {
        this.messageId = UUID.randomUUID().toString();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.read = false;
    }
    
    // Getters
    public String getMessageId() {
        return messageId;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public String getReceiverId() {
        return receiverId;
    }
    
    public String getContent() {
        return content;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
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