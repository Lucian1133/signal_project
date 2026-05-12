package com.alerts;

import com.data_management.PatientRecord;
import java.util.List;

/**
 * Strategy for checking oxygen saturation related alerts.
 */
public class OxygenSaturationStrategy implements AlertStrategy {

    @Override
    public boolean checkAlert(String patientId, List<PatientRecord> records) {
        if (records.isEmpty()) {
            return false;
        }

        long tenMinutes = 10 * 60 * 1000L;

        // Check for low saturation (< 92%)
        for (PatientRecord record : records) {
            if (record.getMeasurementValue() < 92) {
                return true;
            }
        }

        // Check for rapid saturation drop (>= 5% within 10 minutes)
        for (int i = 0; i < records.size(); i++) {
            PatientRecord baseline = records.get(i);
            for (int j = i + 1; j < records.size(); j++) {
                PatientRecord later = records.get(j);
                if (later.getTimestamp() - baseline.getTimestamp() > tenMinutes) {
                    break;
                }
                if (baseline.getMeasurementValue() - later.getMeasurementValue() >= 5) {
                    return true;
                }
            }
        }

        return false;
    }
}
