package com.myapp.backend.model;

// Represents an administrator user in the Hospital Management System
public class Admin extends User {
    private String role; // The role of the admin in the system

    // No-arg constructor for Jackson serialization
    public Admin() {
        super();
        this.role = "Administrator";
    }

    // Constructor with parameters for creating a new admin
    public Admin(String name, String email, String password) {
        super(name, email, password);
        this.role = "Administrator";
    }

    // Get the admin's role
    public String getRole() {
        return role;
    }

    // Set the admin's role
    public void setRole(String role) {
        this.role = role;
    }

    // Display admin information to console
    public void displayUserInfo() {
        System.out.printf("Admin Name: %s\nAdmin ID: %s\nAdmin Email: %s\nRole: %s\n", 
            this.getName(), this.getId(), this.getEmail(), this.getRole());
    }
}