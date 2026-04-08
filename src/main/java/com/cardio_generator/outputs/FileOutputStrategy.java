package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements OutputStrategy by writing patient data to text files.
 * Each data label gets its own file inside the specified base directory.
 * Uses a ConcurrentHashMap to keep track of file paths per label so we
 * don't have to recompute the path every time output is called.
 */
public class FileOutputStrategy implements OutputStrategy {

    // Changed field name from BaseDirectory to baseDirectory to follow camelCase naming (Google Java Style Guide, section 5.2.3)
    private String baseDirectory;

    // Changed field name from file_map to fileMap to follow camelCase naming instead of snake_case (Google Java Style Guide, section 5.2.3)
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Creates a FileOutputStrategy that writes files to the given directory.
     *
     * @param baseDirectory the path of the directory where output files will be stored
     */
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }

    /**
     * Writes a single data entry to a file named after the label.
     * The file is created if it does not exist and data is appended otherwise.
     *
     * @param patientId the ID of the patient the data belongs to
     * @param timestamp the time the data was recorded, in milliseconds since epoch
     * @param label     the category of the data (e.g. "ECG", "Saturation")
     * @param data      the actual data value as a string
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Changed local variable name from FilePath to filePath to follow camelCase naming (Google Java Style Guide, section 5.2.3)
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}
