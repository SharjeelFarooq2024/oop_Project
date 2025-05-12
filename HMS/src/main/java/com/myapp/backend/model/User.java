package com.myapp.backend.model;

import java.util.UUID;

// Abstract base class for all user types in the Hospital Management System
public abstract class User {
    protected String name;     // User's full name
    protected String id;       // Unique identifier for the user
    protected String email;    // User's email address
    protected String password; // User's password
    protected int age;         // User's age

    // Default constructor that generates a new UUID
    public User() {
        this.id = UUID.randomUUID().toString();
    }

    // Constructor with parameters for creating a new user
    public User(String name, String email, String password) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getName() { return name; }
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public int getAge() { return age; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setId(String id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setAge(int age) { this.age = age; }
}
