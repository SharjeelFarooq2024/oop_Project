package com.myapp.backend.services;

import com.myapp.backend.model.AdminDashboardUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages users for the Admin Dashboard
 */
public class UserManager {

    private static final String FILE_PATH = "data/AdminUsers.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Initializes the AdminUsers.json file with default users if it's empty or doesn't exist.
     * The default users are taken from the provided JSON snippet.
     */
    public static void initializeDefaultAdminUsers() {
        File adminUsersFile = new File(FILE_PATH);
        boolean shouldInitialize = false;

        if (!adminUsersFile.exists()) {
            // File doesn't exist. addUser will handle directory and file creation.
            shouldInitialize = true;
            System.out.println(FILE_PATH + " does not exist. Initializing with default admin users.");
        } else if (adminUsersFile.length() == 0) {
            // File exists but is empty.
            shouldInitialize = true;
            System.out.println(FILE_PATH + " is empty. Initializing with default admin users.");
        }

        if (shouldInitialize) {
            List<AdminDashboardUser> defaultUsers = new ArrayList<>();
            defaultUsers.add(new AdminDashboardUser("Smith", "Doctor"));
            defaultUsers.add(new AdminDashboardUser("Bob", "Admin"));
            defaultUsers.add(new AdminDashboardUser("John", "Patient"));

            for (AdminDashboardUser user : defaultUsers) {
                // The addUser method handles file creation if necessary and appends the user
                // in the line-by-line JSON format expected by other UserManager methods.
                addUser(user); 
            }
            System.out.println("Default admin users have been added to " + FILE_PATH);
        } else {
            System.out.println(FILE_PATH + " already exists and is not empty. Skipping default admin user initialization.");
        }
    }

    // Add user to file
    public static void addUser(AdminDashboardUser user) {
        try {
            File file = new File(FILE_PATH);
            // Create directory if it doesn't exist
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            
            // Create file if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
            }
            
            try (FileWriter fileWriter = new FileWriter(FILE_PATH, true)) {
                ObjectNode userJson = mapper.createObjectNode();
                userJson.put("name", user.getName());
                userJson.put("role", user.getRole());
                fileWriter.write(mapper.writeValueAsString(userJson) + "\n");
                System.out.println("User added: " + user.getName() + " (" + user.getRole() + ")");
            }
        } catch (IOException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }

    // Delete user by name
    public static void deleteUser(String nameToDelete) {
        List<AdminDashboardUser> users = getUsers();
        boolean found = false;

        Iterator<AdminDashboardUser> iterator = users.iterator();
        while (iterator.hasNext()) {
            AdminDashboardUser user = iterator.next();
            if (user.getName().equalsIgnoreCase(nameToDelete)) {
                iterator.remove();
                found = true;
                break;
            }
        }

        if (found) {
            overwriteUsersFile(users);
            System.out.println("User deleted: " + nameToDelete);
        } else {
            System.out.println("User not found: " + nameToDelete);
        }
    }

    // Get all users
    public static List<AdminDashboardUser> getUsers() {
        List<AdminDashboardUser> users = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            return users; // Return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    ObjectNode obj = (ObjectNode) mapper.readTree(line);
                    String name = obj.get("name").asText();
                    String role = obj.get("role").asText();
                    users.add(new AdminDashboardUser(name, role));
                } catch (Exception e) {
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users: " + e.getMessage());
        }

        return users;
    }

    // Rewrite entire file with updated users
    private static void overwriteUsersFile(List<AdminDashboardUser> users) {
        try (FileWriter fileWriter = new FileWriter(FILE_PATH, false)) {
            for (AdminDashboardUser user : users) {
                ObjectNode obj = mapper.createObjectNode();
                obj.put("name", user.getName());
                obj.put("role", user.getRole());
                fileWriter.write(mapper.writeValueAsString(obj) + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing users to file: " + e.getMessage());
        }
    }
}