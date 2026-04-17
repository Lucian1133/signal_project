package data_management;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Patient class, covering getRecords filtering and addRecord behaviour.
 */
class PatientTest {

    @Test
    void testGetRecordsReturnsRecordsWithinRange() {
        Patient patient = new Patient(1);
        patient.addRecord(100.0, "HeartRate", 1000L);
        patient.addRecord(110.0, "HeartRate", 2000L);
        patient.addRecord(120.0, "HeartRate", 3000L);

        List<PatientRecord> result = patient.getRecords(1000L, 2000L);

        assertEquals(2, result.size());
        assertEquals(100.0, result.get(0).getMeasurementValue(), 0.001);
        assertEquals(110.0, result.get(1).getMeasurementValue(), 0.001);
    }

    @Test
    void testGetRecordsExcludesOutOfRangeRecords() {
        Patient patient = new Patient(2);
        patient.addRecord(50.0, "HeartRate", 500L);
        patient.addRecord(60.0, "HeartRate", 1500L);
        patient.addRecord(70.0, "HeartRate", 3000L);

        List<PatientRecord> result = patient.getRecords(1000L, 2000L);

        assertEquals(1, result.size());
        assertEquals(60.0, result.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testGetRecordsReturnsEmptyListWhenNoRecords() {
        Patient patient = new Patient(3);

        List<PatientRecord> result = patient.getRecords(0L, Long.MAX_VALUE);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRecordsReturnsEmptyListWhenNoneInRange() {
        Patient patient = new Patient(4);
        patient.addRecord(80.0, "HeartRate", 5000L);
        patient.addRecord(90.0, "HeartRate", 6000L);

        List<PatientRecord> result = patient.getRecords(1000L, 2000L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRecordsIncludesBoundaryTimestamps() {
        Patient patient = new Patient(5);
        patient.addRecord(100.0, "HeartRate", 1000L);
        patient.addRecord(200.0, "HeartRate", 5000L);

        List<PatientRecord> result = patient.getRecords(1000L, 5000L);

        assertEquals(2, result.size());
    }

    @Test
    void testAddRecordIncreasesRecordCount() {
        Patient patient = new Patient(6);
        patient.addRecord(75.0, "BloodPressure", 1000L);
        patient.addRecord(80.0, "BloodPressure", 2000L);

        List<PatientRecord> result = patient.getRecords(0L, Long.MAX_VALUE);

        assertEquals(2, result.size());
    }

    @Test
    void testGetPatientIdReturnsCorrectId() {
        Patient patient = new Patient(42);

        assertEquals(42, patient.getPatientId());
    }

    @Test
    void testGetRecordsRecordTypeIsPreserved() {
        Patient patient = new Patient(7);
          patient.addRecord(98.6, "Temperature", 1000L);

         List<PatientRecord> result = patient.getRecords(0L, Long.MAX_VALUE);

           assertEquals("Temperature", result.get(0).getRecordType());
          assertEquals(7, result.get(0).getPatientId());
    }
}
