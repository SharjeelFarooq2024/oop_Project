package com.myapp.backend.model;

public class Admin extends User {
    private String role;

    // No-arg constructor for Jackson serialization
    public Admin() {
        super();
        this.role = "Administrator";
    }

    public Admin(String name, String email, String password) {
        super(name, email, password);
        this.role = "Administrator";
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void displayUserInfo() {
        System.out.printf("Admin Name: %s\nAdmin ID: %s\nAdmin Email: %s\nRole: %s\n", 
            this.getName(), this.getId(), this.getEmail(), this.getRole());
    }
}