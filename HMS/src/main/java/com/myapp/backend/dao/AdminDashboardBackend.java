package backend;

import java.util.List;

public class AdminDashboardBackend {

    // Add User
    public static void addUser(String name, String role) {
        if (name == null || name.isEmpty() || role == null || role.isEmpty()) {
            System.out.println("Error: Name and Role must not be empty.");
            return;
        }
        User user = new User(name, role);
        UserManager.addUser(user);
        System.out.println("User added: " + user);
    }

    // Delete User
    public static void deleteUser(String name) {
        if (name == null || name.isEmpty()) {
            System.out.println("Error: Please provide a valid name.");
            return;
        }
        UserManager.deleteUser(name);
    }

    // View Users
    public static void viewUsers() {
        List<User> users = UserManager.getUsers();
        if (users.isEmpty()) {
            System.out.println("No users available.");
        } else {
            for (User user : users) {
                System.out.println(user);
            }
        }
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