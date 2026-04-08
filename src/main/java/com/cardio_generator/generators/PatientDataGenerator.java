package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface for all patient data generators in the simulator.
 * Each generator is responsible for producing one type of health data
 * (e.g. ECG, blood pressure) for a given patient.
 */
public interface PatientDataGenerator {

    /**
     * Generates one data point for the specified patient and passes it
     * to the given output strategy.
     *
     * @param patientId      the ID of the patient to generate data for
     * @param outputStrategy the strategy that will handle the generated data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
