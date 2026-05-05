package alerts;

import com.alerts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Decorator Pattern implementation.
 */
public class AlertDecoratorTest {

    private Alert baseAlert;
    private long timestamp;

    @BeforeEach
    public void setUp() {
        timestamp = System.currentTimeMillis();
        baseAlert = new Alert("Patient1", "Test Alert", timestamp);
    }

    @Test
    public void testRepeatedAlertDecoratorCreation() {
        RepeatedAlertDecorator decorator = new RepeatedAlertDecorator(baseAlert, 1, 3);
        assertNotNull(decorator);
        assertEquals("Patient1", decorator.getPatientId());
        assertEquals("Test Alert", decorator.getCondition());
    }

    @Test
    public void testRepeatedAlertDecoratorProperties() {
        RepeatedAlertDecorator decorator = new RepeatedAlertDecorator(baseAlert, 2, 5);
        assertEquals(2, decorator.getIntervalSeconds());
        assertEquals(5, decorator.getMaxRepetitions());
        assertEquals(0, decorator.getCurrentRepetitions());
    }

    @Test
    public void testPriorityAlertDecoratorCreation() {
        PriorityAlertDecorator decorator = new PriorityAlertDecorator(baseAlert, PriorityAlertDecorator.Priority.HIGH);
        assertNotNull(decorator);
        assertEquals("Patient1", decorator.getPatientId());
        assertEquals(PriorityAlertDecorator.Priority.HIGH, decorator.getPriority());
    }

    @Test
    public void testPriorityLevels() {
        assertEquals(1, PriorityAlertDecorator.Priority.LOW.getLevel());
        assertEquals(2, PriorityAlertDecorator.Priority.MEDIUM.getLevel());
        assertEquals(3, PriorityAlertDecorator.Priority.HIGH.getLevel());
        assertEquals(4, PriorityAlertDecorator.Priority.CRITICAL.getLevel());
    }

    @Test
    public void testPriorityAlertDecoratorChangePriority() {
        PriorityAlertDecorator decorator = new PriorityAlertDecorator(baseAlert, PriorityAlertDecorator.Priority.MEDIUM);
        assertEquals(PriorityAlertDecorator.Priority.MEDIUM, decorator.getPriority());
        
        decorator.setPriority(PriorityAlertDecorator.Priority.CRITICAL);
        assertEquals(PriorityAlertDecorator.Priority.CRITICAL, decorator.getPriority());
    }

    @Test
    public void testDecoratorPreservesOriginalAlert() {
        RepeatedAlertDecorator decorator = new RepeatedAlertDecorator(baseAlert, 1, 2);
        Alert decorated = decorator.getDecoratedAlert();
        
        assertEquals(baseAlert.getPatientId(), decorated.getPatientId());
        assertEquals(baseAlert.getCondition(), decorated.getCondition());
        assertEquals(baseAlert.getTimestamp(), decorated.getTimestamp());
    }

    @Test
    public void testMultipleDecorators() {
        Alert alert = new Alert("Patient2", "Multi-Decorator Alert", timestamp);
        
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(alert, 1, 2);
        PriorityAlertDecorator priority = new PriorityAlertDecorator(repeated, PriorityAlertDecorator.Priority.CRITICAL);
        
        assertEquals("Patient2", priority.getPatientId());
        assertEquals(PriorityAlertDecorator.Priority.CRITICAL, priority.getPriority());
        assertEquals(repeated, priority.getDecoratedAlert());
    }

    @Test
    public void testRepeatedAlertDecoratorProcessing() {
        RepeatedAlertDecorator decorator = new RepeatedAlertDecorator(baseAlert, 1, 1);
        // Just verify it doesn't throw an exception
        assertDoesNotThrow(() -> decorator.processAlert());
    }

    @Test
    public void testAlertDecoratorInheritance() {
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(baseAlert, 1, 1);
        PriorityAlertDecorator priority = new PriorityAlertDecorator(baseAlert, PriorityAlertDecorator.Priority.MEDIUM);
        
        assertInstanceOf(AlertDecorator.class, repeated);
        assertInstanceOf(AlertDecorator.class, priority);
        assertInstanceOf(Alert.class, repeated);
        assertInstanceOf(Alert.class, priority);
    }
}
