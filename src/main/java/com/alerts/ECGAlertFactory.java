package com.alerts;

public class ECGAlertFactory extends AlertFactory {
    private double heartRate;
    private String rhythmAbnormality;

    public ECGAlertFactory(double heartRate, String rhythmAbnormality) {
        this.heartRate = heartRate;
        this.rhythmAbnormality = rhythmAbnormality;
    }

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new ECGAlert(patientId, condition, timestamp, heartRate, rhythmAbnormality);
    }
}
