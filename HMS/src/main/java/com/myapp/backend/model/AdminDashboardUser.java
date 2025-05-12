package com.myapp.backend.model;

/**
 * Simplified User class specifically for Admin Dashboard operations
 * This class represents users in the Hospital Management System admin dashboard.
 * It stores basic user information including name and role.
 * Used for managing users through the admin interface.
 */
public class AdminDashboardUser {
    /**
     * The name of the user
     */
    private String name;
    
    /**
     * The role of the user in the system (e.g., Admin, Doctor, Patient)
     */
    private String role;

    /**
     * Constructs a new AdminDashboardUser with the specified name and role
     *
     * @param name The name of the user
     * @param role The role of the user in the system
     */
    public AdminDashboardUser(String name, String role) {
        this.name = name;
        this.role = role;
    }

    /**
     * Gets the name of the user
     *
     * @return The user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user
     *
     * @param name The new name for the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the role of the user
     *
     * @return The user's role (e.g., Admin, Doctor, Patient)
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the user
     *
     * @param role The new role for the user
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns a string representation of the user
     *
     * @return A string containing the name and role of the user
     */
    @Override
    public String toString() {
        return "User{name='" + name + "', role='" + role + "'}";
    }
}