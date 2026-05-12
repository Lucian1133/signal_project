package alerts;

import com.alerts.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Factory Method Pattern implementation.
 */
public class AlertFactoryTest {

    private BloodPressureAlertFactory bpFactory;
    private BloodOxygenAlertFactory oxygenFactory;
    private ECGAlertFactory ecgFactory;

    @BeforeEach
    public void setUp() {
        bpFactory = new BloodPressureAlertFactory("Systolic", 190);
        oxygenFactory = new BloodOxygenAlertFactory(88.5, -5.2);
        ecgFactory = new ECGAlertFactory(105, "Tachycardia");
    }

    @Test
    public void testBloodPressureAlertFactoryCreation() {
        Alert alert = bpFactory.createAlert("Patient1", "Critical Systolic High", System.currentTimeMillis());
        assertNotNull(alert);
        assertInstanceOf(BloodPressureAlert.class, alert);
        assertEquals("Patient1", alert.getPatientId());
        assertEquals("Critical Systolic High", alert.getCondition());
    }

    @Test
    public void testBloodPressureAlertFactoryProperties() {
        BloodPressureAlert alert = (BloodPressureAlert) bpFactory.createAlert("Patient1", "Critical Systolic High", System.currentTimeMillis());
        assertEquals("Systolic", alert.getPressureType());
        assertEquals(190, alert.getReadingValue());
    }

    @Test
    public void testBloodOxygenAlertFactoryCreation() {
        Alert alert = oxygenFactory.createAlert("Patient2", "Low Saturation", System.currentTimeMillis());
        assertNotNull(alert);
        assertInstanceOf(BloodOxygenAlert.class, alert);
        assertEquals("Patient2", alert.getPatientId());
    }

    @Test
    public void testBloodOxygenAlertFactoryProperties() {
        BloodOxygenAlert alert = (BloodOxygenAlert) oxygenFactory.createAlert("Patient2", "Low Saturation", System.currentTimeMillis());
        assertEquals(88.5, alert.getSaturationLevel());
        assertEquals(-5.2, alert.getSaturationChange());
    }

    @Test
    public void testECGAlertFactoryCreation() {
        Alert alert = ecgFactory.createAlert("Patient3", "ECG Abnormality", System.currentTimeMillis());
        assertNotNull(alert);
        assertInstanceOf(ECGAlert.class, alert);
        assertEquals("Patient3", alert.getPatientId());
    }

    @Test
    public void testECGAlertFactoryProperties() {
        ECGAlert alert = (ECGAlert) ecgFactory.createAlert("Patient3", "ECG Abnormality", System.currentTimeMillis());
        assertEquals(105, alert.getHeartRate());
        assertEquals("Tachycardia", alert.getRhythmAbnormality());
    }

    @Test
    public void testMultipleFactoriesCreatingDifferentAlerts() {
        Alert bp = bpFactory.createAlert("P1", "BP Alert", 1000L);
        Alert oxygen = oxygenFactory.createAlert("P2", "O2 Alert", 2000L);
        Alert ecg = ecgFactory.createAlert("P3", "ECG Alert", 3000L);

        assertInstanceOf(BloodPressureAlert.class, bp);
        assertInstanceOf(BloodOxygenAlert.class, oxygen);
        assertInstanceOf(ECGAlert.class, ecg);
        
        assertNotEquals(bp.getPatientId(), oxygen.getPatientId());
        assertNotEquals(oxygen.getPatientId(), ecg.getPatientId());
    }
}
