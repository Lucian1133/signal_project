package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated blood oxygen saturation (SpO2) data for patients.
 * Each patient starts with a baseline saturation between 95% and 100%.
 * Every time generate() is called the value changes slightly to simulate
 * realistic fluctuations, and it is always kept within 90-100%.
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;

    /**
     * Creates a BloodSaturationDataGenerator for the given number of patients.
     * Initializes each patient with a random baseline saturation between 95 and 100.
     *
     * @param patientCount the number of patients being simulated
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * Generates a blood saturation reading for the given patient and outputs it.
     * The new value is the previous value adjusted by -1, 0, or +1, then clamped
     * to the range [90, 100] so it stays within realistic bounds.
     *
     * @param patientId      the ID of the patient to generate data for
     * @param outputStrategy the strategy used to send the generated data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
