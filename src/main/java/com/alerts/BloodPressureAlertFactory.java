package com.alerts;

/**
 * Factory for creating blood pressure alerts.
 */
public class BloodPressureAlertFactory extends AlertFactory {
    private String pressureType; // "Systolic" or "Diastolic"
    private double readingValue;

    public BloodPressureAlertFactory(String pressureType, double readingValue) {
        this.pressureType = pressureType;
        this.readingValue = readingValue;
    }

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodPressureAlert(patientId, condition, timestamp, pressureType, readingValue);
    }
}
