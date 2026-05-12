package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Reads patient data in real-time from a WebSocket server.
 * Connects to the server and stores each incoming message in DataStorage.
 * Message format expected: patientId,timestamp,label,data
 */
public class WebSocketDataReader implements DataReader {

    private String serverUri;
    private WebSocketClient client;

    /**
     * @param serverUri the URI of the WebSocket server, e.g. "ws://localhost:8080"
     */
    public WebSocketDataReader(String serverUri) {
        this.serverUri = serverUri;
    }

    /**
     * Connects to the WebSocket server and starts listening for incoming data.
     * Each message is parsed and stored in the given DataStorage instance.
     *
     * @param dataStorage where incoming patient records will be saved
     * @throws IOException if the URI is invalid
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        try {
            client = new WebSocketClient(new URI(serverUri)) {

                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connected to WebSocket server: " + serverUri);
                }

                @Override
                public void onMessage(String message) {
                    // Each message from the server is parsed and stored
                    parseAndStore(message, dataStorage);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Disconnected from server. Reason: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("WebSocket error: " + ex.getMessage());
                }
            };
            client.connect();
        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebSocket URI: " + serverUri, e);
        }
    }

    /**
     * Updates the server URI and connects to the new address.
     *
     * @param serverUri the new server URI to connect to
     * @throws IOException if the connection fails
     */
    @Override
    public void connect(String serverUri) throws IOException {
        this.serverUri = serverUri;
        readData(DataStorage.getInstance());
    }

    /**
     * Parses a message in the format "patientId,timestamp,label,data"
     * and adds it to the data storage.
     * Malformed or unreadable messages are skipped with an error message.
     *
     * @param message     the raw message string from the WebSocket server
     * @param dataStorage where the parsed record will be stored
     */
    public void parseAndStore(String message, DataStorage dataStorage) {
        try {
            String[] parts = message.split(",");
            if (parts.length != 4) {
                System.err.println("Skipping malformed message: " + message);
                return;
            }

            int patientId = Integer.parseInt(parts[0].trim());
            long timestamp = Long.parseLong(parts[1].trim());
            String label = parts[2].trim();
            String dataStr = parts[3].trim();

            // "triggered" and "resolved" come from alert generators
            double value;
            if ("triggered".equalsIgnoreCase(dataStr)) {
                value = 1.0;
            } else if ("resolved".equalsIgnoreCase(dataStr)) {
                value = 0.0;
            } else {
                value = Double.parseDouble(dataStr);
            }

            dataStorage.addPatientData(patientId, value, label, timestamp);
        } catch (Exception e) {
            System.err.println("Error parsing message: " + message);
        }
    }

    /**
     * Closes the WebSocket connection if one is open.
     */
    public void disconnect() {
        if (client != null) {
            client.close();
        }
    }
}
