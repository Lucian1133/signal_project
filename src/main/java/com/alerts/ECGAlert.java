package com.alerts;

public class ECGAlert extends Alert {
    private double heartRate;
    private String rhythmAbnormality;

    public ECGAlert(String patientId, String condition, long timestamp,
                    double heartRate, String rhythmAbnormality) {
        super(patientId, condition, timestamp);
        this.heartRate = heartRate;
        this.rhythmAbnormality = rhythmAbnormality;
    }

    public double getHeartRate() {
        return heartRate;
    }

    public String getRhythmAbnormality() {
        return rhythmAbnormality;
    }
}
