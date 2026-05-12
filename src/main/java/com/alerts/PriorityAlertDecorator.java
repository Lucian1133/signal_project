package com.alerts;

/**
 * Decorator that adds priority level to alerts.
 * Dynamically adjusts priority based on alert severity.
 */
public class PriorityAlertDecorator extends AlertDecorator {
    public enum Priority {
        LOW(1),
        MEDIUM(2),
        HIGH(3),
        CRITICAL(4);

        private final int level;

        Priority(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    private Priority priority;

    public PriorityAlertDecorator(Alert decoratedAlert, Priority priority) {
        super(decoratedAlert);
        this.priority = priority;
    }

    @Override
    public void processAlert() {
        String priorityLabel = "[" + priority.name() + "]";
        System.out.println(priorityLabel + " PRIORITY ALERT: Patient " + getPatientId()
                + " - " + getCondition()
                + " at " + getTimestamp());
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
