package com.myapp.backend.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.myapp.backend.model.Doctor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    private static final String FILE_PATH = System.getProperty("user.dir") + "/data/Doctors.json";
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Doctor> loadDoctors() {
        File file = new File(FILE_PATH);
        System.out.println("Looking for doctor file at: " + FILE_PATH);
        if (!file.exists()) {
            System.out.println("Doctor file not found!");
            return new ArrayList<>();
        }
    
        try {
            List<Doctor> doctors = mapper.readValue(file, new TypeReference<List<Doctor>>() {});
            System.out.println("Loaded " + doctors.size() + " doctors.");
            for (Doctor doc : doctors) {
                System.out.println(doc.getName() + " - " + doc.getSpecialization());
            }
            return doctors;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
}
