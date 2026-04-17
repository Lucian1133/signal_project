package alerts;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AlertGenerator covering all alert types defined in the requirements:
 * blood pressure trends, critical thresholds, low saturation, rapid saturation drop,
 * hypotensive hypoxemia, ECG anomalies, and manual triggered alerts.
 */
class AlertGeneratorTest {

    private AlertGenerator alertGenerator;

    @BeforeEach
    void setUp() {
        alertGenerator = new AlertGenerator(new DataStorage());
    }


    // Blood Pressure – Increasing Trend


    @Test
    void testSystolicIncreasingTrendTriggersAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(100.0, "SystolicPressure", 1000L);
        patient.addRecord(115.0, "SystolicPressure", 2000L);
        patient.addRecord(130.0, "SystolicPressure", 3000L);

        alertGenerator.evaluateData(patient);

        assertTrue(containsCondition(alertGenerator.getTriggeredAlerts(),
                "SystolicPressure Increasing Trend"));
    }

    @Test
    void testDiastolicIncreasingTrendTriggersAlert() {
        Patient patient = new Patient(2);
        patient.addRecord(70.0, "DiastolicPressure", 1000L);
        patient.addRecord(85.0, "DiastolicPressure", 2000L);
        patient.addRecord(100.0, "DiastolicPressure", 3000L);

        alertGenerator.evaluateData(patient);

        assertTrue(containsCondition(alertGenerator.getTriggeredAlerts(),
                "DiastolicPressure Increasing Trend"));
    }


    // Blood Pressure – Decreasing Trend


    @Test
    void testSystolicDecreasingTrendTriggersAlert() {
        Patient patient = new Patient(3);
        patient.addRecord(150.0, "SystolicPressure", 1000L);
        patient.addRecord(135.0, "SystolicPressure", 2000L);
        patient.addRecord(120.0, "SystolicPressure", 3000L);

        alertGenerator.evaluateData(patient);

        assertTrue(containsCondition(alertGenerator.getTriggeredAlerts(),
                "SystolicPressure Decreasing Trend"));
    }

    @Test
    void testDiastolicDecreasingTrendTriggersAlert() {
        Patient patient = new Patient(4);
        patient.addRecord(90.0, "DiastolicPressure", 1000L);
        patient.addRecord(75.0, "DiastolicPressure", 2000L);
        patient.addRecord(60.0, "DiastolicPressure", 3000L);

        alertGenerator.evaluateData(patient);

        assertTrue(containsCondition(alertGenerator.getTriggeredAlerts(),
                "DiastolicPressure Decreasing Trend"));
    }

    @Test
    void testNoBPTrendAlertWhenChangeLessThan10() {
        Patient patient = new Patient(5);
        patient.addRecord(100.0, "SystolicPressure", 1000L);
        patient.addRecord(108.0, "SystolicPressure", 2000L);
        patient.addRecord(116.0, "SystolicPressure", 3000L);

        alertGenerator.evaluateData(patient);

        assertFalse(containsCondition(alertGenerator.getTriggeredAlerts(),
                "SystolicPressure Increasing Trend"));
    }


    // Blood Pressure – Critical Thresholds


    @Test
    void testCriticalSystolicHighTriggersAlert() {
        Patient patient = new Patient(6);
        patient.addRecord(185.0, "SystolicPressure", 1000L);

        alertGenerator.evaluateData(patient);

        assertTrue(containsCondition(alertGenerator.getTriggeredAlerts(),
                "Critical Systolic High"));
    }

    @Test
    void testCriticalSystolicLowTriggersAlert() {
        Patient patient = new Patient(7);
        patient.addRecord(85.0, "SystolicPressure", 1000L);

        alertGenerator.evaluateData(patient);

        assertTrue(containsCondition(alertGenerator.getTriggeredAlerts(),
                "Critical Systolic Low"));
    }

    @Test
    void testCriticalDiastolicHighTriggersAlert() {
        Patient patient = new Patient(8);
        patient.addRecord(125.0, "DiastolicPressure", 1000L);

        alertGenerator.evaluateData(patient);

        assertTrue(containsCondition(alertGenerator.getTriggeredAlerts(),
                "Critical Diastolic High"));
    }

    @Test
    void testCriticalDiastolicLowTriggersAlert() {
        Patient patient = new Patient(9);
        patient.addRecord(55.0, "DiastolicPressure", 1000L);

        alertGenerator.evaluateData(patient);

        assertTrue(containsCondition(alertGenerator.getTriggeredAlerts(),
                "Critical Diastolic Low"));
    }

    @Test
    void testNoCriticalAlertForNormalBP() {
        Patient patient = new Patient(10);
        patient.addRecord(120.0, "SystolicPressure", 1000L);
        patient.addRecord(80.0, "DiastolicPressure", 1000L);

        alertGenerator.evaluateData(patient);

        assertTrue(alertGenerator.getTriggeredAlerts().isEmpty());
    }


    // Blood Saturation – Low Saturation


    @Test
    void testLowSaturationTriggersAlert() {
        Patient patient = new Patient(11);
        patient.addRecord(91.0, "Saturation", 1000L);

        alertGenerator.evaluateData(patient);

        assertTrue(containsCondition(alertGenerator.getTriggeredAlerts(), "Low Saturation"));
    }

    @Test
    void testNoAlertWhenSaturationExactly92() {
        Patient patient = new Patient(12);
        patient.addRecord(92.0, "Saturation", 1000L);

        alertGenerator.evaluateData(patient);

        assertFalse(containsCondition(alertGenerator.getTriggeredAlerts(), "Low Saturation"));
    }


    // Blood Saturation – Rapid Drop


    @Test
    void testRapidSaturationDropTriggersAlert() {
        Patient patient = new Patient(13);
        long base = 1000L;
        patient.addRecord(98.0, "Saturation", base);
        patient.addRecord(92.0, "Saturation", base + 5 * 60 * 1000L); // 5 min later, drop of 6

        alertGenerator.evaluateData(patient);

        assertTrue(containsCondition(alertGenerator.getTriggeredAlerts(), "Rapid Saturation Drop"));
    }

    @Test
    void testNoRapidDropAlertWhenDropBeyond10Minutes() {
        Patient patient = new Patient(14);
        long base = 1000L;
        patient.addRecord(98.0, "Saturation", base);
        patient.addRecord(92.0, "Saturation", base + 11 * 60 * 1000L); // 11 min later

        alertGenerator.evaluateData(patient);

        assertFalse(containsCondition(alertGenerator.getTriggeredAlerts(), "Rapid Saturation Drop"));
    }

    @Test
    void testNoRapidDropAlertWhenDropLessThan5() {
         Patient patient = new Patient(15);
        long base = 1000L;
         patient.addRecord(96.0, "Saturation", base);
         patient.addRecord(93.0, "Saturation", base + 2 * 60 * 1000L); // drop of 3

        alertGenerator.evaluateData(patient);

        assertFalse(containsCondition(alertGenerator.getTriggeredAlerts(), "Rapid Saturation Drop"));
    }


    // Combined: Hypotensive Hypoxemia


    @Test
    void testHypotensiveHypoxemiaTriggersAlert() {
        Patient patient = new Patient(16);
         patient.addRecord(85.0, "SystolicPressure", 1000L);
        patient.addRecord(91.0, "Saturation", 1000L);

        alertGenerator.evaluateData(patient);

         assertTrue(containsCondition(alertGenerator.getTriggeredAlerts(), "Hypotensive Hypoxemia"));
    }

     @Test
     void testNoHypoxemiaAlertWhenOnlyLowBP() {
        Patient patient = new Patient(17);
        patient.addRecord(85.0, "SystolicPressure", 1000L);
        patient.addRecord(95.0, "Saturation", 1000L);

        alertGenerator.evaluateData(patient);

        assertFalse(containsCondition(alertGenerator.getTriggeredAlerts(), "Hypotensive Hypoxemia"));
    }

    @Test
     void testNoHypoxemiaAlertWhenOnlyLowSaturation() {
        Patient patient = new Patient(18);
        patient.addRecord(110.0, "SystolicPressure", 1000L);
        patient.addRecord(91.0, "Saturation", 1000L);

        alertGenerator.evaluateData(patient);

        assertFalse(containsCondition(alertGenerator.getTriggeredAlerts(), "Hypotensive Hypoxemia"));
    }


    // ECG Abnormal Peak


    @Test
    void testECGAbnormalPeakTriggersAlert() {
         Patient patient = new Patient(19);
        // 10 baseline readings near 0, then a large spike
        for (int i = 0; i < 10; i++) {
             patient.addRecord(0.1, "ECG", 1000L + i * 100L);
        }
         patient.addRecord(3.0, "ECG", 2000L); // |3.0 - 0.1| = 2.9 > 1.0

        alertGenerator.evaluateData(patient);

        assertTrue(containsCondition(alertGenerator.getTriggeredAlerts(), "ECG Abnormal Peak"));
    }

      @Test
     void testNoECGAlertForNormalReadings() {
         Patient patient = new Patient(20);
        for (int i = 0; i < 15; i++) {
            patient.addRecord(0.5, "ECG", 1000L + i * 100L);
        }

        alertGenerator.evaluateData(patient);

        assertFalse(containsCondition(alertGenerator.getTriggeredAlerts(), "ECG Abnormal Peak"));
    }

     @Test
     void testNoECGAlertWithFewerThan10Readings() {
        Patient patient = new Patient(21);
          for (int i = 0; i < 9; i++) {
            patient.addRecord(0.1, "ECG", 1000L + i * 100L);
   }

        alertGenerator.evaluateData(patient);
  
        assertFalse(containsCondition(alertGenerator.getTriggeredAlerts(), "ECG Abnormal Peak"));
    }  


    // Manual Triggered Alert

   
        @Test
      void testManualAlertTriggeredWhenValueIs1() {
           Patient patient = new Patient(22);
        patient.addRecord(1.0, "Alert", 1000L);

           alertGenerator.evaluateData(patient);
   
            assertTrue(containsCondition(alertGenerator.getTriggeredAlerts(), "Manual Alert Triggered"));
    }

    @Test
     void testNoManualAlertWhenValueIs0() {
          Patient patient = new Patient(23);
          patient.addRecord(0.0, "Alert", 1000L);

           alertGenerator.evaluateData(patient);

           assertFalse(containsCondition(alertGenerator.getTriggeredAlerts(), "Manual Alert Triggered"));
    }


    // Helper


     private boolean containsCondition(List<Alert> alerts, String condition) {
           for (Alert a : alerts) {
             if (a.getCondition().equals(condition)) {
                return true;
               }
           }
         return false;
     }
 }
