package com.alerts;

/**
 * Represents a blood pressure-specific alert.
 */
public class BloodPressureAlert extends Alert {
    private String pressureType; // "Systolic" or "Diastolic"
    private double readingValue;

    public BloodPressureAlert(String patientId, String condition, long timestamp, 
                              String pressureType, double readingValue) {
        super(patientId, condition, timestamp);
        this.pressureType = pressureType;
        this.readingValue = readingValue;
    }

    public String getPressureType() {
        return pressureType;
    }

    public double getReadingValue() {
        return readingValue;
    }
}
