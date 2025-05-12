package com.myapp.backend.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

// Class to ignore unknown properties during serialization/deserialization
@JsonIgnoreProperties(ignoreUnknown = true)

// Class representing a patient's vital signs
public class VitalSign
{
    private String vitalId;        // Unique identifier for the vital sign record
    private String patientId;      // ID of the patient these vitals belong to
    private double heartRate;      // Heart rate in beats per minute
    private double oxygenLevel;    // Oxygen saturation level in percentage
    private String bloodPressure;  // Blood pressure reading (systolic/diastolic)
    private double temperature;    // Body temperature in degrees Celsius
    private LocalDateTime timestamp; // When this vital sign was recorded

    // No-argument constructor for Jackson JSON serialization
    public VitalSign() {
        // no-arg constructor for Jackson
    }

    // Constructor with parameters to create a new vital sign record
    public VitalSign(String patientId, double heartRate, double oxygenLevel, String bloodPressure, double temperature, LocalDateTime timestamp) {
        this.vitalId = UUID.randomUUID().toString();
        this.patientId = patientId;
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.timestamp = LocalDateTime.now();
    }

    // Getter for the vital sign ID
    public String getVitalId() {
        return this.vitalId;
    }
    
    // Getter for the patient ID
    public String getPatientId() {
        return this.patientId;
    }
    
    // Setter for the patient ID
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    // Setter for the vital sign ID
    public void setVitalId(String vitalId) {
        this.vitalId = vitalId;
    }
    
    // Setter for the heart rate
    public void setHeartRate(double heartRate) {
        this.heartRate = heartRate;
    }
    
    // Getter for the heart rate
    public double getHeartRate() {
        return this.heartRate;
    }

    public double getOxygenLevel() {
        return this.oxygenLevel;
    }
    public void setOxygenLevel(double oxygenLevel) {
        this.oxygenLevel = oxygenLevel;
    }

    public String getBloodPressure() {
        return this.bloodPressure;
    }
    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public double getTemperature() {
        return this.temperature;
    }
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}