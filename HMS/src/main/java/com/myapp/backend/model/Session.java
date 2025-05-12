package com.myapp.backend.model;

// Singleton class to manage the current user's session in the application
public class Session {
    private static User currentUser;  // Holds reference to the currently logged-in user

    // Sets the current user for the session
    public static void setUser(User user) {
        currentUser = user;
    }

    // Returns the currently logged-in user
    public static User getUser() {
        return currentUser;
    }

    // Clears the session when user logs out
    public static void clear() {
        currentUser = null;
    }
}
