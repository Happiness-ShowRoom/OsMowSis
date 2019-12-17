package com.osmowsis.utils;

import com.osmowsis.simulator.Simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static com.osmowsis.utils.Utilities.renderLawn;

public class FileReader {

    private Simulator simulator;

    public FileReader(Simulator simulator) {
        this.simulator = simulator;
    }

    /**
     * This method checks if the input file was provided and verbose option was requested.
     * @param args
     */
    public static void checkInputFilePresence(String[] args){

        // check for the test scenario file name
        if (args.length < 1) {
            System.out.println("ERROR: Test scenario file name not found.");
        }

        if (args.length >= 2 && (args[1].equals("-v") || args[1].equals("-verbose"))) {
            Utilities.showState = Boolean.TRUE;
        }
    }

    /**
     * This method is responsible for reading and parsing all the information contained in the input file.
     * @param testFileName
     */
    public void uploadStartingFile(String testFileName) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(testFileName));

        simulator.initializeLawn(scanner);
        simulator.initializeMowers(scanner);
        simulator.initializeGophers(scanner);
        simulator.initializeGopherPeriod(scanner);
        simulator.initializeTurnLimit(scanner);

        scanner.close();
    }
}
