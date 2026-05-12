package com.alerts;

import com.data_management.PatientRecord;
import java.util.List;

public class BloodPressureStrategy implements AlertStrategy {

    @Override
    public boolean checkAlert(String patientId, List<PatientRecord> records) {
        if (records.isEmpty()) {
            return false;
        }

        // Check for critical thresholds
        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();
            String type = record.getRecordType();
            
            if ("SystolicPressure".equals(type)) {
                if (value > 180 || value < 90) {
                    return true;
                }
            } else if ("DiastolicPressure".equals(type)) {
                if (value > 120 || value < 60) {
                    return true;
                }
            }
        }

        // Check for trends (3 consecutive increases/decreases > 10 mmHg)
        int n = records.size();
        if (n >= 3) {
            PatientRecord r1 = records.get(n - 3);
            PatientRecord r2 = records.get(n - 2);
            PatientRecord r3 = records.get(n - 1);
            
            double v1 = r1.getMeasurementValue();
            double v2 = r2.getMeasurementValue();
            double v3 = r3.getMeasurementValue();

            if ((v2 - v1 > 10 && v3 - v2 > 10) || (v1 - v2 > 10 && v2 - v3 > 10)) {
                return true;
            }
        }

        return false;
    }
}
