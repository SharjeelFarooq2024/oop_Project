package main.java.com.myapp.backend.services;


import main.java.com.myapp.backend.dao.PatientDAO;
import main.java.com.myapp.backend.model.Patient;

public class PatientService {
    private PatientDAO dao = new PatientDAO();

    public void registerNewPatient(Patient patient) throws Exception {
        if (patient.getName().isBlank()) {
            throw new Exception("Patient name cannot be empty");
        }
        dao.addPatient(patient);
    }
}

