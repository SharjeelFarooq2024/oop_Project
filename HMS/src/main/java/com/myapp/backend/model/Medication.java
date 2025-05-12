package com.myapp.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

// Represents a medication prescribed to a patient
public class Medication {
    private String id;              // Unique identifier for the medication record
    private String name;            // Name of the medication
    private String dosage;          // Dosage amount (e.g., "10mg")
    private String frequency;       // How often to take (e.g., "twice daily")
    private String duration;        // Duration of prescription (e.g., "7 days")
    private String notes;           // Additional instructions or notes
    private LocalDateTime prescribedDate; // When the medication was prescribed
    
    // Default constructor that generates a unique ID and sets current timestamp
    public Medication() {
        this.id = UUID.randomUUID().toString();
        this.prescribedDate = LocalDateTime.now();
    }
    
    // Constructor and getters/setters
}