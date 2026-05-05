package com.alerts;

/**
 * Abstract factory class for creating different types of alerts.
 * This implements the Factory Method Pattern.
 */
public abstract class AlertFactory {
    
    /**
     * Creates an alert based on the alert type specified.
     * Subclasses must implement this method to return appropriate alert instances.
     */
    public abstract Alert createAlert(String patientId, String condition, long timestamp);
}
