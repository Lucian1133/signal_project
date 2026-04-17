package data_management;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FileDataReader, verifying that files in the output format produced
 * by FileOutputStrategy are correctly parsed and stored in DataStorage.
 */
class FileDataReaderTest {

    @Test
    void testReadDataParsesNumericValues(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("ECG.txt");
         try (PrintWriter pw = new PrintWriter(file.toFile())) {
              pw.println("Patient ID: 1, Timestamp: 1000, Label: ECG, Data: 0.45");
              pw.println("Patient ID: 1, Timestamp: 2000, Label: ECG, Data: -0.30");
         }

        DataStorage storage = new DataStorage();
        new FileDataReader(tempDir.toString()).readData(storage);

          List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
         assertEquals(2, records.size());
         assertEquals(0.45, records.get(0).getMeasurementValue(), 0.001);
          assertEquals(-0.30, records.get(1).getMeasurementValue(), 0.001);
    }

      @Test
     void testReadDataParsesTriggeredAsOne(@TempDir Path tempDir) throws IOException {
         Path file = tempDir.resolve("Alert.txt");
         try (PrintWriter pw = new PrintWriter(file.toFile())) {
            pw.println("Patient ID: 2, Timestamp: 5000, Label: Alert, Data: triggered");
        }

        DataStorage storage = new DataStorage();
        new FileDataReader(tempDir.toString()).readData(storage);

        List<PatientRecord> records = storage.getRecords(2, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(1.0, records.get(0).getMeasurementValue(), 0.001);
    }

      @Test
      void testReadDataParsesResolvedAsZero(@TempDir Path tempDir) throws IOException {
          Path file = tempDir.resolve("Alert.txt");
         try (PrintWriter pw = new PrintWriter(file.toFile())) {
            pw.println("Patient ID: 3, Timestamp: 6000, Label: Alert, Data: resolved");
          }

        DataStorage storage = new DataStorage();
         new FileDataReader(tempDir.toString()).readData(storage);

           List<PatientRecord> records = storage.getRecords(3, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
          assertEquals(0.0, records.get(0).getMeasurementValue(), 0.001);
    }
  
     @Test
      void testReadDataSkipsMalformedLines(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("Saturation.txt");
           try (PrintWriter pw = new PrintWriter(file.toFile())) {
              pw.println("this is not a valid line");
            pw.println("Patient ID: 4, Timestamp: 7000, Label: Saturation, Data: 95.0");
         }

          DataStorage storage = new DataStorage();
          new FileDataReader(tempDir.toString()).readData(storage);
  
        List<PatientRecord> records = storage.getRecords(4, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(95.0, records.get(0).getMeasurementValue(), 0.001);
    }

    @Test
       void testReadDataThrowsForInvalidDirectory() {
        FileDataReader reader = new FileDataReader("/nonexistent/path/that/does/not/exist");
        assertThrows(IOException.class, () -> reader.readData(new DataStorage()));
    }

     @Test
     void testReadDataHandlesMultipleFiles(@TempDir Path tempDir) throws IOException {
            Path ecgFile = tempDir.resolve("ECG.txt");
        try (PrintWriter pw = new PrintWriter(ecgFile.toFile())) {
            pw.println("Patient ID: 5, Timestamp: 1000, Label: ECG, Data: 0.5");
        }
        Path satFile = tempDir.resolve("Saturation.txt");
         try (PrintWriter pw = new PrintWriter(satFile.toFile())) {
             pw.println("Patient ID: 5, Timestamp: 2000, Label: Saturation, Data: 97.0");
        }

        DataStorage storage = new DataStorage();
         new FileDataReader(tempDir.toString()).readData(storage);
 
      List<PatientRecord> all = storage.getRecords(5, 0, Long.MAX_VALUE);
        assertEquals(2, all.size());
    }

    @Test
     void testReadDataStoresCorrectLabel(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("SystolicPressure.txt");
         try (PrintWriter pw = new PrintWriter(file.toFile())) {
              pw.println("Patient ID: 6, Timestamp: 3000, Label: SystolicPressure, Data: 120.0");
         }

         DataStorage storage = new DataStorage();
        new FileDataReader(tempDir.toString()).readData(storage);
       List<PatientRecord> records = storage.getRecords(6, 0, Long.MAX_VALUE);
          assertEquals(1, records.size());
        assertEquals("SystolicPressure", records.get(0).getRecordType());
    }
}
