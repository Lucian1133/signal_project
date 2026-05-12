package com.alerts;

/**
 * Decorator that adds repeated alert functionality.
 * Triggers alert rechecking at specified intervals.
 */
public class RepeatedAlertDecorator extends AlertDecorator {
    private int intervalSeconds;
    private int maxRepetitions;
    private int currentRepetitions;

    public RepeatedAlertDecorator(Alert decoratedAlert, int intervalSeconds, int maxRepetitions) {
        super(decoratedAlert);
        this.intervalSeconds = intervalSeconds;
        this.maxRepetitions = maxRepetitions;
        this.currentRepetitions = 0;
    }

    @Override
    public void processAlert() {
        currentRepetitions = 0;
        while (currentRepetitions < maxRepetitions) {
            System.out.println("REPEATED ALERT: Patient " + getPatientId()
                    + " - " + getCondition()
                    + " (Repetition " + (currentRepetitions + 1) + "/"
                    + maxRepetitions + ")");
            currentRepetitions++;
            
            if (currentRepetitions < maxRepetitions) {
                try {
                    Thread.sleep(intervalSeconds * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public int getMaxRepetitions() {
        return maxRepetitions;
    }

    public int getCurrentRepetitions() {
        return currentRepetitions;
    }
}
