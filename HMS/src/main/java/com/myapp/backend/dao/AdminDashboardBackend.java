package com.myapp.backend.dao;

import com.myapp.backend.model.User;
import com.myapp.backend.services.ReportGenerator;
import java.util.List;

public class AdminDashboardBackend {
    // Add User
    public static void addUser(String name, String role) {
        if (name == null || name.isEmpty() || role == null || role.isEmpty()) {
            System.out.println("Error: Name and Role must not be empty.");
            return;
        }
        // Implement your user addition logic here or remove if not needed
        System.out.println("User added: " + name + " (" + role + ")");
    }

    // Delete User
    public static void deleteUser(String name) {
        if (name == null || name.isEmpty()) {
            System.out.println("Error: Please provide a valid name.");
            return;
        }
        // Implement your user deletion logic here or remove if not needed
        System.out.println("User deleted: " + name);
    }

    // View Users
    public static void viewUsers() {
        // Implement your user viewing logic here or remove if not needed
        System.out.println("No users available.");
    }

    // Generate Report
    public static void generateReport(String reportType) {
        boolean success = ReportGenerator.generateReport(reportType);
        if (success) {
            System.out.println(reportType + " generated successfully.");
        } else {
            System.out.println("Failed to generate " + reportType + ".");
        }
    }
}