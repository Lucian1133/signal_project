package com.alerts;

import com.data_management.PatientRecord;
import java.util.List;

public class HeartRateStrategy implements AlertStrategy {

    @Override
    public boolean checkAlert(String patientId, List<PatientRecord> records) {
        if (records.isEmpty()) {
            return false;
        }

        // Check for ECG abnormalities: reading more than 1.0 above/below 10-reading average
        int windowSize = 10;
        if (records.size() <= windowSize) {
            return false;
        }

        for (int i = windowSize; i < records.size(); i++) {
            double sum = 0;
            for (int j = i - windowSize; j < i; j++) {
                sum += records.get(j).getMeasurementValue();
            }
            double avg = sum / windowSize;
            double current = records.get(i).getMeasurementValue();
            
            if (Math.abs(current - avg) > 1.0) {
                return true;
            }
        }

        return false;
    }
}
