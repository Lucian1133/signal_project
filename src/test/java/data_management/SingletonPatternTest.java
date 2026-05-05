package data_management;

import com.data_management.DataStorage;
import com.cardio_generator.HealthDataSimulator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Singleton Pattern implementation.
 */
public class SingletonPatternTest {

    @Test
    public void testDataStorageSingletonInstance() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }

    @Test
    public void testDataStorageSingletonConsistency() {
        DataStorage storage1 = DataStorage.getInstance();
        
        storage1.addPatientData(1, 120, "SystolicPressure", System.currentTimeMillis());
        
        DataStorage storage2 = DataStorage.getInstance();
        
        assertEquals(storage1.getAllPatients().size(), storage2.getAllPatients().size());
    }

    @Test
    public void testHealthDataSimulatorSingletonInstance() {
        HealthDataSimulator instance1 = HealthDataSimulator.getInstance();
        HealthDataSimulator instance2 = HealthDataSimulator.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }

    @Test
    public void testMultipleThreadsGetSameSingletonInstance() throws InterruptedException {
        DataStorage[] instances = new DataStorage[5];
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                instances[index] = DataStorage.getInstance();
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        DataStorage firstInstance = instances[0];
        for (int i = 1; i < 5; i++) {
            assertSame(firstInstance, instances[i]);
        }
    }

    @Test
    public void testSingletonNotNull() {
        assertNotNull(DataStorage.getInstance());
        assertNotNull(HealthDataSimulator.getInstance());
    }

    @Test
    public void testDataStorageSingletonSharedState() {
        DataStorage storage1 = DataStorage.getInstance();
        storage1.addPatientData(100, 75.5, "HeartRate", System.currentTimeMillis());
        
        DataStorage storage2 = DataStorage.getInstance();
        assertFalse(storage2.getAllPatients().isEmpty());
    }
}
