package alerts;

import com.alerts.*;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Strategy Pattern implementation.
 */
public class AlertStrategyTest {

    private BloodPressureStrategy bpStrategy;
    private HeartRateStrategy hrStrategy;
    private OxygenSaturationStrategy oxygenStrategy;
    private List<PatientRecord> records;

    @BeforeEach
    public void setUp() {
        bpStrategy = new BloodPressureStrategy();
        hrStrategy = new HeartRateStrategy();
        oxygenStrategy = new OxygenSaturationStrategy();
        records = new ArrayList<>();
    }

    @Test
    public void testBloodPressureStrategyHighSystolic() {
        records.add(new PatientRecord(1, 185, "SystolicPressure", System.currentTimeMillis()));
        assertTrue(bpStrategy.checkAlert("1", records));
    }

    @Test
    public void testBloodPressureStrategyLowSystolic() {
        records.add(new PatientRecord(1, 85, "SystolicPressure", System.currentTimeMillis()));
        assertTrue(bpStrategy.checkAlert("1", records));
    }

    @Test
    public void testBloodPressureStrategyNormalSystolic() {
        records.add(new PatientRecord(1, 120, "SystolicPressure", System.currentTimeMillis()));
        assertFalse(bpStrategy.checkAlert("1", records));
    }

    @Test
    public void testBloodPressureStrategyHighDiastolic() {
        records.add(new PatientRecord(1, 125, "DiastolicPressure", System.currentTimeMillis()));
        assertTrue(bpStrategy.checkAlert("1", records));
    }

    @Test
    public void testBloodPressureStrategyIncreasingTrend() {
        long now = System.currentTimeMillis();
        records.add(new PatientRecord(1, 110, "SystolicPressure", now));
        records.add(new PatientRecord(1, 125, "SystolicPressure", now + 1000));
        records.add(new PatientRecord(1, 140, "SystolicPressure", now + 2000));
        assertTrue(bpStrategy.checkAlert("1", records));
    }

    @Test
    public void testHeartRateStrategyNormalECG() {
        for (int i = 0; i < 11; i++) {
            records.add(new PatientRecord(1, 0.5, "ECG", System.currentTimeMillis() + i * 1000));
        }
        assertFalse(hrStrategy.checkAlert("1", records));
    }

    @Test
    public void testHeartRateStrategyAbnormalECG() {
        for (int i = 0; i < 10; i++) {
            records.add(new PatientRecord(1, 0.5, "ECG", System.currentTimeMillis() + i * 1000));
        }
        // Add abnormal reading
        records.add(new PatientRecord(1, 2.0, "ECG", System.currentTimeMillis() + 10000));
        assertTrue(hrStrategy.checkAlert("1", records));
    }

    @Test
    public void testOxygenSaturationStrategyLowSaturation() {
        records.add(new PatientRecord(1, 90, "Saturation", System.currentTimeMillis()));
        assertTrue(oxygenStrategy.checkAlert("1", records));
    }

    @Test
    public void testOxygenSaturationStrategyNormalSaturation() {
        records.add(new PatientRecord(1, 95, "Saturation", System.currentTimeMillis()));
        assertFalse(oxygenStrategy.checkAlert("1", records));
    }

    @Test
    public void testOxygenSaturationStrategyRapidDrop() {
        long now = System.currentTimeMillis();
        records.add(new PatientRecord(1, 98, "Saturation", now));
        records.add(new PatientRecord(1, 92, "Saturation", now + 5 * 60 * 1000)); // 5 minutes, 6% drop
        assertTrue(oxygenStrategy.checkAlert("1", records));
    }

    @Test
    public void testOxygenSaturationStrategySlowDrop() {
        long now = System.currentTimeMillis();
        records.add(new PatientRecord(1, 98, "Saturation", now));
        records.add(new PatientRecord(1, 96, "Saturation", now + 5 * 60 * 1000)); // 5 minutes, 2% drop
        assertFalse(oxygenStrategy.checkAlert("1", records));
    }

    @Test
    public void testStrategyIndependence() {
        records.add(new PatientRecord(1, 185, "SystolicPressure", System.currentTimeMillis()));
        
        assertTrue(bpStrategy.checkAlert("1", records));
        assertFalse(hrStrategy.checkAlert("1", records));
        assertFalse(oxygenStrategy.checkAlert("1", records));
    }
}
