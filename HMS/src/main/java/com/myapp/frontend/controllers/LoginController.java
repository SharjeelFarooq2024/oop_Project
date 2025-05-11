package com.myapp.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.myapp.backend.services.SessionManager;
import com.myapp.backend.services.PatientService;
import com.myapp.backend.services.DoctorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.myapp.backend.model.Patient;
import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Admin;

import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private ToggleGroup userTypeToggle;
    @FXML private RadioButton patientRadio;
    @FXML private RadioButton doctorRadio;
    @FXML private RadioButton adminRadio;

    private PatientService patientService = new PatientService();
    private DoctorService doctorService = new DoctorService();

    @FXML
    public void initialize() {
        // Set patient as default login type if not already set
        if (userTypeToggle.getSelectedToggle() == null) {
            patientRadio.setSelected(true);
        }
    }

    @FXML
    private void handleSignUpLinkClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Signup.fxml"));
            Parent signUpRoot = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene signUpScene = new Scene(signUpRoot);
            stage.setScene(signUpScene);
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load sign-up page!\n" + e.getMessage());
        }
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        System.out.println("Login attempt - Email: " + email);

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter email and password.");
            return;
        }

        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();

            // Get selected user type
            RadioButton selectedType = (RadioButton) userTypeToggle.getSelectedToggle();

            if (selectedType == doctorRadio) {
                // Doctor login
                Doctor doctor = doctorService.login(email, password);
                System.out.println("Login result - Doctor object: " + (doctor != null ? "found" : "not found"));
                
                if (doctor != null) {
                    System.out.println("Login successful for doctor: " + doctor.getName());
                    SessionManager.setLoggedInDoctor(doctor);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DoctorDashboard.fxml"));
                    Parent dashboardRoot = loader.load();

                    DoctorDashboardController controller = loader.getController();
                    controller.setLoggedInDoctor(doctor);

                    Scene scene = new Scene(dashboardRoot);
                    stage.setScene(scene);
                    stage.setTitle("Doctor Dashboard");
                    stage.setWidth(width);
                    stage.setHeight(height);
                    stage.show();
                } else {
                    System.out.println("Login failed - Invalid doctor credentials");
                    showAlert("Error", "Invalid doctor credentials.");
                }
            } else if (selectedType == patientRadio) {
                // Patient login
                Patient patient = patientService.login(email, password);
                System.out.println("Login result - Patient object: " + (patient != null ? "found" : "not found"));
                
                if (patient != null) {
                    System.out.println("Login successful for patient: " + patient.getName());
                    SessionManager.setLoggedInPatient(patient);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PatientDashboard.fxml"));
                    Parent dashboardRoot = loader.load();

                    PatientDashboardController controller = loader.getController();
                    controller.setLoggedInPatient(patient);

                    Scene scene = new Scene(dashboardRoot);
                    stage.setScene(scene);
                    stage.setTitle("Patient Dashboard");
                    stage.setWidth(width);
                    stage.setHeight(height);
                    stage.show();
                } else {
                    System.out.println("Login failed - Invalid patient credentials");
                    showAlert("Error", "Invalid patient credentials.");
                }
            } else if (selectedType == adminRadio) {
                // Admin login
                handleAdminLogin(email, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred during login: " + e.getMessage());
        }
    }    private void handleAdminLogin(String email, String password) {
        try {
            System.out.println("DEBUG: Starting admin login for " + email);
            File file = new File("data/Admin.json");
            System.out.println("DEBUG: Admin.json exists? " + file.exists() + ", size: " + (file.exists() ? file.length() : 0));
            
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> admins = new ArrayList<>();
            if (file.exists() && file.length() > 0) {
                System.out.println("DEBUG: Reading Admin.json content");
                admins = mapper.readValue(file, new TypeReference<List<Map<String, Object>>>() {});
                System.out.println("DEBUG: Found " + admins.size() + " admin entries");
                
                for (Map<String, Object> admin : admins) {
                    System.out.println("DEBUG: Checking admin entry: " + admin);
                    boolean emailMatch = admin.get("email") != null && admin.get("email").toString().equalsIgnoreCase(email);
                    boolean passwordMatch = admin.get("password") != null && admin.get("password").toString().equals(password);
                    boolean roleMatch = "Admin".equalsIgnoreCase(String.valueOf(admin.get("role")));
                    
                    System.out.println("DEBUG: Match results - Email: " + emailMatch + ", Password: " + passwordMatch + ", Role: " + roleMatch);
                    
                    if (emailMatch && passwordMatch && roleMatch) {
                        // Successful admin login
                        System.out.println("DEBUG: Admin login successful, loading dashboard");
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminDashboard.fxml"));
                        System.out.println("DEBUG: FXML loader created");
                        
                        try {
                            System.out.println("DEBUG: About to load AdminDashboard.fxml");
                            Parent root = loader.load();
                            System.out.println("DEBUG: AdminDashboard.fxml loaded successfully");
                            
                            AdminDashboardController controller = loader.getController();
                            System.out.println("DEBUG: AdminDashboardController obtained");
                            
                            controller.setPrimaryStage((Stage) loginButton.getScene().getWindow());
                            System.out.println("DEBUG: PrimaryStage set");
                            
                            Scene scene = new Scene(root);
                            Stage stage = (Stage) loginButton.getScene().getWindow();
                            stage.setScene(scene);
                            stage.setTitle("HMS - Admin Dashboard");
                            System.out.println("DEBUG: Scene set, admin dashboard should be visible now");
                            return;
                        } catch (Exception e) {
                            System.err.println("DEBUG: Error loading AdminDashboard.fxml: " + e.getMessage());
                            e.printStackTrace();
                            throw e; // Re-throw to be caught by outer handler
                        }
                    }
                }
            }
            System.out.println("DEBUG: Admin login failed - invalid credentials");
            showAlert("Login Failed", "Invalid admin credentials.");
        } catch (Exception e) {
            System.err.println("DEBUG: Admin login error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Could not load admin dashboard.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
