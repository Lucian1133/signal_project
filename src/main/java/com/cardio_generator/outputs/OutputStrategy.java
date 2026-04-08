package com.cardio_generator.outputs;

/**
 * Defines the interface for output strategies used by the simulator.
 * Any class that wants to handle simulator output (e.g. writing to a file,
 * printing to console, streaming over a network) needs to implement this.
 */
public interface OutputStrategy {

    /**
     * Sends a single data record to whatever output target this strategy uses.
     *
     * @param patientId the ID of the patient this data belongs to
     * @param timestamp the time the data was recorded, in milliseconds since epoch
     * @param label     the type of data being recorded (e.g. "ECG", "Saturation")
     * @param data      the value of the data as a string
     */
    void output(int patientId, long timestamp, String label, String data);
}
