package com.alerts;

public class BloodOxygenAlertFactory extends AlertFactory {
    private double saturationLevel;
    private double saturationChange;

    public BloodOxygenAlertFactory(double saturationLevel, double saturationChange) {
        this.saturationLevel = saturationLevel;
        this.saturationChange = saturationChange;
    }

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodOxygenAlert(patientId, condition, timestamp, saturationLevel, saturationChange);
    }
}
