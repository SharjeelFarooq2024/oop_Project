package com.myapp.backend.services;

import com.myapp.backend.model.AdminDashboardUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
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
    }    // Add user to file
    public static void addUser(AdminDashboardUser user) {
        try {
            File file = new File(FILE_PATH);
            // Create directory if it doesn't exist
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            
            List<AdminDashboardUser> users = new ArrayList<>();
            
            // Read existing users if file exists
            if (file.exists() && file.length() > 0) {
                try {
                    users = mapper.readValue(file, 
                        mapper.getTypeFactory().constructCollectionType(List.class, AdminDashboardUser.class));
                } catch (Exception e) {
                    System.out.println("Warning: Could not read existing users. Creating new file: " + e.getMessage());
                    // Continue with empty list if file is corrupted
                }
            }
            
            // Add the new user
            users.add(user);
            
            // Write back the entire list
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, users);
            System.out.println("User added: " + user.getName() + " (" + user.getRole() + ")");
            
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
        }    }    // Get all users
    public static List<AdminDashboardUser> getUsers() {
        List<AdminDashboardUser> users = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            // Initialize with default users if file doesn't exist
            initializeDefaultAdminUsers();
            return getUsers(); // Recursive call after initialization
        }

        try {
            // Read the entire file as a JSON array
            users = mapper.readValue(file, 
                mapper.getTypeFactory().constructCollectionType(List.class, AdminDashboardUser.class));
            
            // If the list is empty or has only one user, add default users
            if (users.size() <= 1) {
                // Add the existing user(s) to our default set if any
                List<AdminDashboardUser> defaultUsers = new ArrayList<>(users);
                
                // Check if Smith exists, if not add
                if (!userExists(users, "Smith")) {
                    defaultUsers.add(new AdminDashboardUser("Smith", "Doctor"));
                }
                
                // Check if Bob exists, if not add
                if (!userExists(users, "Bob")) {
                    defaultUsers.add(new AdminDashboardUser("Bob", "Admin"));
                }
                
                // Check if John exists, if not add
                if (!userExists(users, "John")) {
                    defaultUsers.add(new AdminDashboardUser("John", "Patient"));
                }
                
                // Only update if we added users
                if (defaultUsers.size() > users.size()) {
                    overwriteUsersFile(defaultUsers);
                    users = defaultUsers;
                }
            }
            
            System.out.println("Successfully loaded " + users.size() + " users from file");
        } catch (IOException e) {
            System.out.println("Error reading users: " + e.getMessage());
            // Initialize with default users if there's an error
            initializeDefaultAdminUsers();
            try {
                users = mapper.readValue(file, 
                    mapper.getTypeFactory().constructCollectionType(List.class, AdminDashboardUser.class));
            } catch (IOException ex) {
                System.out.println("Failed to read users after initialization: " + ex.getMessage());
            }
        }

        return users;
    }    
    
    // Rewrite entire file with updated users
    private static void overwriteUsersFile(List<AdminDashboardUser> users) {
        try {
            // Write the entire list as a JSON array
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), users);
            System.out.println("Successfully saved " + users.size() + " users to file");
        } catch (IOException e) {
            System.out.println("Error writing users to file: " + e.getMessage());
        }
    }
    
    // Helper method to check if a user with a given name exists
    private static boolean userExists(List<AdminDashboardUser> users, String name) {
        for (AdminDashboardUser user : users) {
            if (user.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}