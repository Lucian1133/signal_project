package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated alert events for patients.
 * An alert can be triggered or resolved each time generate() is called.
 * Uses a simple probability model based on the exponential distribution
 * to decide whether a new alert should fire for a given patient.
 */
public class AlertGenerator implements PatientDataGenerator {

    public static final Random randomGenerator = new Random();
    // Changed field name from AlertStates to alertStates to follow camelCase naming (Google Java Style Guide, section 5.2.3)
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Creates an AlertGenerator for the given number of patients.
     * All patients start with no active alert.
     *
     * @param patientCount the total number of patients being simulated
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates alert data for a specific patient and sends it through the output strategy.
     * If the patient already has an active alert there is a 90% chance it gets resolved.
     * Otherwise a new alert may be triggered based on a Poisson-like probability.
     *
     * @param patientId      the ID of the patient to generate data for
     * @param outputStrategy the strategy used to send the output data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Changed variable name from Lambda to lambda to follow camelCase naming (Google Java Style Guide, section 5.2.3)
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
