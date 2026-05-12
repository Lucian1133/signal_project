package com.data_management;

import java.io.IOException;

public interface DataReader {
    /**
     * Reads data from a source and stores it in the data storage.
     *
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Connects to a real-time data source such as a WebSocket server.
     * Default implementation does nothing — file-based readers don't need it.
     *
     * @param serverUri the URI of the server to connect to
     * @throws IOException if the connection cannot be established
     */
    default void connect(String serverUri) throws IOException {
        // not needed for file-based readers
    }
}
