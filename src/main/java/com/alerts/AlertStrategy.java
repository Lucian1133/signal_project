package com.alerts;

import com.data_management.PatientRecord;
import java.util.List;

/**
 * Strategy interface for alert checking.
 * Implements the Strategy Pattern for different monitoring strategies.
 */
public interface AlertStrategy {
    
    /**
     * Checks if alert conditions are met based on the strategy.
     *
     * @param patientId the patient ID to check
     * @param records the list of patient records to evaluate
     * @return true if alert condition is triggered, false otherwise
     */
    boolean checkAlert(String patientId, List<PatientRecord> records);
}
