package com.myapp.backend.model;

import java.time.LocalDateTime;

public class VitalSign
{
    private int heartRate;
    private int oxygenLevel;
    private String bloodPressure;
    private double temperature;
    private LocalDateTime timestamp;

    public VitalSign(int heartRate, int oxygenLevel, String bloodPressure, double temperature) {
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.timestamp = LocalDateTime.now();
    }

    public int getHeartRate() {
        return this.heartRate;
    }

    public int getOxygenLevel() {
        return this.oxygenLevel;
    }

    public String getBloodPressure() {
        return this.bloodPressure;
    }

    public double getTemperature() {
        return this.temperature;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }
}