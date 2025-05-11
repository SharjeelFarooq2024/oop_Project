package com.myapp.backend.model;

/**
 * Simplified User class specifically for Admin Dashboard operations
 */
public class AdminDashboardUser {
    private String name;
    private String role;

    public AdminDashboardUser(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', role='" + role + "'}";
    }
}