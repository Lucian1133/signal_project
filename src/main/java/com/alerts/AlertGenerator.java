package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {

    private final DataStorage dataStorage;
    private final List<Alert> triggeredAlerts = new ArrayList<>();

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     *
     * @param dataStorage the data storage system that provides access to patient data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. Checks blood pressure trends and thresholds, blood oxygen saturation,
     * the combined hypotensive hypoxemia condition, ECG anomalies, and manual alerts.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> all = patient.getRecords(0, Long.MAX_VALUE);
        String patientId = String.valueOf(patient.getPatientId());

        List<PatientRecord> systolic = filterByType(all, "SystolicPressure");
        List<PatientRecord> diastolic = filterByType(all, "DiastolicPressure");
        List<PatientRecord> saturation = filterByType(all, "Saturation");
        List<PatientRecord> ecg = filterByType(all, "ECG");
        List<PatientRecord> alertRecords = filterByType(all, "Alert");

        checkBloodPressureTrend(patientId, systolic, "SystolicPressure");
        checkBloodPressureTrend(patientId, diastolic, "DiastolicPressure");
        checkBloodPressureThreshold(patientId, systolic, diastolic);
        checkLowSaturation(patientId, saturation);
        checkRapidSaturationDrop(patientId, saturation);
        checkHypotensiveHypoxemia(patientId, systolic, saturation);
        checkECGAbnormality(patientId, ecg);
        checkTriggeredAlert(patientId, alertRecords);
    }

    /**
     * Returns all alerts that have been triggered during this session.
     *
     * @return a copy of the list of triggered alerts
     */
    public List<Alert> getTriggeredAlerts() {
        return new ArrayList<>(triggeredAlerts);
    }

    /**
     * Triggers an alert by storing it and printing to the console.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        triggeredAlerts.add(alert);
        System.out.println("ALERT: Patient " + alert.getPatientId()
                + " - " + alert.getCondition()
                + " at " + alert.getTimestamp());
    }





    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        List<PatientRecord> result = new ArrayList<>();
        for (PatientRecord r : records) {
            if (type.equals(r.getRecordType())) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Triggers an alert when three consecutive readings of the same type each
     * increase or decrease by more than 10 mmHg.
     */
    private void checkBloodPressureTrend(String patientId, List<PatientRecord> records, String type) {
        if (records.size() < 3) {
            return;
        }
        int n = records.size();
        PatientRecord r1 = records.get(n - 3);
        PatientRecord r2 = records.get(n - 2);
        PatientRecord r3 = records.get(n - 1);
        double v1 = r1.getMeasurementValue();
        double v2 = r2.getMeasurementValue();
        double v3 = r3.getMeasurementValue();

        if (v2 - v1 > 10 && v3 - v2 > 10) {
            triggerAlert(new Alert(patientId, type + " Increasing Trend", r3.getTimestamp()));
        }
        if (v1 - v2 > 10 && v2 - v3 > 10) {
            triggerAlert(new Alert(patientId, type + " Decreasing Trend", r3.getTimestamp()));
        }
    }

    /**
     * Triggers an alert when systolic BP exceeds 180 or drops below 90 mmHg,
     * or diastolic BP exceeds 120 or drops below 60 mmHg.
     */
    private void checkBloodPressureThreshold(String patientId,
            List<PatientRecord> systolic, List<PatientRecord> diastolic) {
        for (PatientRecord r : systolic) {
            double v = r.getMeasurementValue();
            if (v > 180) {
                triggerAlert(new Alert(patientId, "Critical Systolic High", r.getTimestamp()));
            } else if (v < 90) {
                triggerAlert(new Alert(patientId, "Critical Systolic Low", r.getTimestamp()));
            }
        }
        for (PatientRecord r : diastolic) {
            double v = r.getMeasurementValue();
            if (v > 120) {
                triggerAlert(new Alert(patientId, "Critical Diastolic High", r.getTimestamp()));
            } else if (v < 60) {
                triggerAlert(new Alert(patientId, "Critical Diastolic Low", r.getTimestamp()));
            }
        }
    }


     //Triggers an alert when blood oxygen saturation falls below 92%.

    private void checkLowSaturation(String patientId, List<PatientRecord> records) {
        for (PatientRecord r : records) {
            if (r.getMeasurementValue() < 92) {
                triggerAlert(new Alert(patientId, "Low Saturation", r.getTimestamp()));
            }
        }
    }


      //Triggers an alert when saturation drops by 5% or more within a 10-minute window.

    private void checkRapidSaturationDrop(String patientId, List<PatientRecord> records) {
        long tenMinutes = 10 * 60 * 1000L;
        for (int i = 0; i < records.size(); i++) {
            PatientRecord baseline = records.get(i);
            for (int j = i + 1; j < records.size(); j++) {
                PatientRecord later = records.get(j);
                if (later.getTimestamp() - baseline.getTimestamp() > tenMinutes) {
                    break;
                }
                if (baseline.getMeasurementValue() - later.getMeasurementValue() >= 5) {
                    triggerAlert(new Alert(patientId, "Rapid Saturation Drop", later.getTimestamp()));
                    break;
                }
            }
        }
    }

    /**
     * Triggers an alert when systolic BP is below 90 mmHg AND saturation is below 92%
     * (based on the most recent readings of each type).
     */
    private void checkHypotensiveHypoxemia(String patientId,
            List<PatientRecord> systolic, List<PatientRecord> saturation) {
        if (systolic.isEmpty() || saturation.isEmpty()) {
            return;
        }
        PatientRecord latestSys = systolic.get(systolic.size() - 1);
        PatientRecord latestSat = saturation.get(saturation.size() - 1);
        if (latestSys.getMeasurementValue() < 90 && latestSat.getMeasurementValue() < 92) {
            long ts = Math.max(latestSys.getTimestamp(), latestSat.getTimestamp());
            triggerAlert(new Alert(patientId, "Hypotensive Hypoxemia", ts));
        }
    }

    /**
     * Triggers an alert when an ECG reading is more than 1.0 above or below the
     * sliding-window average of the preceding 10 readings.
     */
    private void checkECGAbnormality(String patientId, List<PatientRecord> records) {
        int windowSize = 10;
        if (records.size() <= windowSize) {
            return;
        }
        for (int i = windowSize; i < records.size(); i++) {
            double sum = 0;
            for (int j = i - windowSize; j < i; j++) {
                sum += records.get(j).getMeasurementValue();
            }
            double avg = sum / windowSize;
            double current = records.get(i).getMeasurementValue();
            if (Math.abs(current - avg) > 1.0) {
                triggerAlert(new Alert(patientId, "ECG Abnormal Peak", records.get(i).getTimestamp()));
            }
        }
    }

    /**
     * Triggers an alert when the most recent Alert record has value 1.0 (triggered).
     */
    private void checkTriggeredAlert(String patientId, List<PatientRecord> records) {
        if (records.isEmpty()) {
            return;
        }
        PatientRecord latest = records.get(records.size() - 1);
        if (latest.getMeasurementValue() == 1.0) {
            triggerAlert(new Alert(patientId, "Manual Alert Triggered", latest.getTimestamp()));
        }
    }
}
