package backend;

public class AdminDashboardBackendTest {
    public static void main(String[] args) {
        AdminDashboardBackend.addUser("Alice", "Doctor");
        AdminDashboardBackend.addUser("Bob", "Admin");

        System.out.println("\nAll users:");
        AdminDashboardBackend.viewUsers();

        AdminDashboardBackend.deleteUser("Alice");

        System.out.println("\nUsers after deletion:");
        AdminDashboardBackend.viewUsers();

        AdminDashboardBackend.generateReport("Feedback");
    }
}