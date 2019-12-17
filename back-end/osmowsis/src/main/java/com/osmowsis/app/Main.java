package com.osmowsis.app;

import com.osmowsis.simulator.Simulator;
import com.osmowsis.utils.FileReader;

import java.io.FileNotFoundException;

import static com.osmowsis.utils.FileReader.checkInputFilePresence;


public class Main {

    public static void main(String[] args) throws Exception {

        Simulator simulator = new Simulator();
        FileReader fileReader = new FileReader(simulator);

        try {
            checkInputFilePresence(args);
            fileReader.uploadStartingFile(args[0]);
        } catch (FileNotFoundException e) {
            System.out.println();
            System.out.println("ERROR: Test scenario file name not found. Please try again");
            return;
        }

        simulator.mainLoop();
        simulator.generateFinalReport();
    }
}
