package com.myapp.backend.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)

public class VitalSign
{
    private String vitalId;
    private String patientId;
    private double heartRate;
    private double oxygenLevel;
    private String bloodPressure;
    private double temperature;
    private LocalDateTime timestamp;

    public VitalSign() {
        // no-arg constructor for Jackson
    }

    public VitalSign(String patientId, double heartRate, double oxygenLevel, String bloodPressure, double temperature, LocalDateTime timestamp) {
        this.vitalId = UUID.randomUUID().toString();
        this.patientId = patientId;
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.timestamp = LocalDateTime.now();
    }

    public String getVitalId() {
        return this.vitalId;
    }
    public String getPatientId() {
        return this.patientId;
    }
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    public void setVitalId(String vitalId) {
        this.vitalId = vitalId;
    }
    public void setHeartRate(double heartRate) {
        this.heartRate = heartRate;
    }
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