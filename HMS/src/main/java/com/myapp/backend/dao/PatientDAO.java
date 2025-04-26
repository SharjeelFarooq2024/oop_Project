package com.myapp.backend.dao;

import com.myapp.backend.model.Patient;
import com.myapp.backend.db.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    public void addPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (name, age) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getName());
            stmt.setInt(2, patient.getAge());
            stmt.executeUpdate();
        }
    }

    // Similar for fetchAll(), updatePatient(), deletePatient() etc.
}

