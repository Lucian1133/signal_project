package com.alerts;

/**
 * Abstract decorator for adding additional functionality to alerts.
 * Implements the Decorator Pattern.
 */
public abstract class AlertDecorator extends Alert {
    protected Alert decoratedAlert;

    public AlertDecorator(Alert decoratedAlert) {
        super(decoratedAlert.getPatientId(), decoratedAlert.getCondition(), decoratedAlert.getTimestamp());
        this.decoratedAlert = decoratedAlert;
    }

    public Alert getDecoratedAlert() {
        return decoratedAlert;
    }

    /**
     * Abstract method for decorators to implement additional alert processing.
     */
    public abstract void processAlert();
}
