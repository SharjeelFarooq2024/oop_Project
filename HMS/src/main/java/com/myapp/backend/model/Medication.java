package com.myapp.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Medication {
    private String id;
    private String name;
    private String dosage;
    private String frequency;
    private String duration;
    private String notes;
    private LocalDateTime prescribedDate;
    
    public Medication() {
        this.id = UUID.randomUUID().toString();
        this.prescribedDate = LocalDateTime.now();
    }
    
    // Constructor and getters/setters
}