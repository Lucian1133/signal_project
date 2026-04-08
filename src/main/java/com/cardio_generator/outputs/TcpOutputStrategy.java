package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Streams patient data to a TCP client over a socket connection.
 * When this strategy is created it starts a server on the given port and
 * waits for one client to connect in a background thread.
 * Once a client connects, each call to output() sends a formatted line to it.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Starts a TCP server on the specified port and waits for a client to connect.
     * The connection is accepted in a separate thread so the main thread is not blocked.
     *
     * @param port the port number to listen on
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends one data record to the connected TCP client.
     * If no client has connected yet, this method does nothing.
     *
     * @param patientId the ID of the patient the data belongs to
     * @param timestamp the time the data was recorded, in milliseconds since epoch
     * @param label     the type of data (e.g. "ECG", "Alert")
     * @param data      the data value as a string
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
