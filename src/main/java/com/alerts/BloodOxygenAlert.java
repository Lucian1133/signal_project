package com.alerts;

/**
 * Represents a blood oxygen saturation alert.
 */
public class BloodOxygenAlert extends Alert {
    private double saturationLevel;
    private double saturationChange; // percentage change

    public BloodOxygenAlert(String patientId, String condition, long timestamp,
                            double saturationLevel, double saturationChange) {
        super(patientId, condition, timestamp);
        this.saturationLevel = saturationLevel;
        this.saturationChange = saturationChange;
    }

    public double getSaturationLevel() {
        return saturationLevel;
    }

    public double getSaturationChange() {
        return saturationChange;
    }
}
