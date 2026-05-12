package data_management;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.WebSocketDataReader;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for WebSocketDataReader.
 * Includes unit tests for message parsing and an integration test with a real server.
 * Each test uses a unique patient ID so tests don't interfere with each other.
 */
class WebSocketDataReaderTest {

    //  Unit tests for parseAndStore 

    @Test
    void testParseValidNumericMessage() {
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:9090");
        DataStorage storage = DataStorage.getInstance();

        reader.parseAndStore("200,1000,ECG,0.5", storage);

        List<PatientRecord> records = storage.getRecords(200, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(0.5, records.get(0).getMeasurementValue(), 0.001);
        assertEquals("ECG", records.get(0).getRecordType());
    }

    @Test
    void testParseTriggeredStoresOne() {
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:9090");
        DataStorage storage = DataStorage.getInstance();

        reader.parseAndStore("201,2000,Alert,triggered", storage);

        List<PatientRecord> records = storage.getRecords(201, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(1.0, records.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testParseResolvedStoresZero() {
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:9090");
        DataStorage storage = DataStorage.getInstance();

        reader.parseAndStore("202,3000,Alert,resolved", storage);

        List<PatientRecord> records = storage.getRecords(202, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(0.0, records.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testMalformedMessageDoesNotThrow() {
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:9090");
        DataStorage storage = DataStorage.getInstance();

        // All of these should be silently skipped without throwing
        assertDoesNotThrow(() -> reader.parseAndStore("bad message", storage));
        assertDoesNotThrow(() -> reader.parseAndStore("1,2,label", storage));
        assertDoesNotThrow(() -> reader.parseAndStore("", storage));
    }

    @Test
    void testNonNumericDataValueDoesNotThrow() {
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:9090");
        DataStorage storage = DataStorage.getInstance();

        // "notanumber" cannot be parsed as a double, should be skipped gracefully
        assertDoesNotThrow(() -> reader.parseAndStore("203,4000,ECG,notanumber", storage));
    }

    @Test
    void testMultipleMessagesStoreMultipleRecords() {
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:9090");
        DataStorage storage = DataStorage.getInstance();

        reader.parseAndStore("204,1000,Saturation,95.0", storage);
        reader.parseAndStore("204,2000,Saturation,96.0", storage);
        reader.parseAndStore("204,3000,Saturation,97.0", storage);

        List<PatientRecord> records = storage.getRecords(204, 0, Long.MAX_VALUE);
        assertEquals(3, records.size());
    }

    @Test
    void testCorrectTimestampIsStored() {
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:9090");
        DataStorage storage = DataStorage.getInstance();

        reader.parseAndStore("205,9999,BloodPressure,120.0", storage);

        List<PatientRecord> records = storage.getRecords(205, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(9999, records.get(0).getTimestamp());
    }

    // --- Error handling tests ---

    @Test
    void testInvalidUriThrowsIOException() {
        // URI with a space is illegal and must trigger URISyntaxException -> IOException
        WebSocketDataReader reader = new WebSocketDataReader("ws://invalid uri");
        assertThrows(IOException.class, () -> reader.readData(DataStorage.getInstance()));
    }

    @Test
    void testDisconnectWithNoConnectionDoesNotThrow() {
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:9090");
        // disconnect before connecting should not crash
        assertDoesNotThrow(() -> reader.disconnect());
    }

    // --- Integration test with a real WebSocket server ---

    @Test
    void testIntegrationWithRealServer() throws Exception {
        int port = 9095;
        CountDownLatch serverReady = new CountDownLatch(1);

        // A simple test server that sends one message as soon as a client connects
        WebSocketServer testServer = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                conn.send("210,5000,Saturation,97.0");
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {}

            @Override
            public void onMessage(WebSocket conn, String message) {}

            @Override
            public void onError(WebSocket conn, Exception ex) {}

            @Override
            public void onStart() {
                // Signal that the server is bound and ready to accept connections
                serverReady.countDown();
            }
        };
        testServer.start();

        // Wait up to 2 seconds for the server to be ready before connecting
        assertTrue(serverReady.await(2, TimeUnit.SECONDS), "Server did not start in time");

        DataStorage storage = DataStorage.getInstance();
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:" + port);
        reader.readData(storage);

        // Give the client time to connect and receive the message
        Thread.sleep(1000);

        List<PatientRecord> records = storage.getRecords(210, 0, Long.MAX_VALUE);
        assertFalse(records.isEmpty(), "Should have received at least one record from server");
        assertEquals(97.0, records.get(0).getMeasurementValue(), 0.001);

        reader.disconnect();
        testServer.stop();
    }
}
