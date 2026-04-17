package com.cardio_generator;

import com.data_management.DataStorage;
import java.io.IOException;

/**
 * Entry point that dispatches to either DataStorage or HealthDataSimulator
 * depending on the first command-line argument.
 * Run with argument "DataStorage" to start the data storage main method,
 * or with no argument to start the health data simulator.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            DataStorage.main(new String[]{});
        } else {
            HealthDataSimulator.main(args);
        }
    }
}
