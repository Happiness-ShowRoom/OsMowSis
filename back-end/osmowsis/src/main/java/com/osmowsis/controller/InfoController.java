package com.osmowsis.controller;

import com.osmowsis.dto.DTO;
import com.osmowsis.entity.Mower;
import com.osmowsis.enums.Direction;
import com.osmowsis.enums.Strategy;
import com.osmowsis.simulator.Simulator;
import com.osmowsis.utils.FileReader;
import com.osmowsis.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.osmowsis.utils.FileReader.checkInputFilePresence;

@RestController
@RequestMapping("/osmowsis")
public class InfoController {

    @Autowired
    private Simulator simulator;

    @GetMapping("/map")
    public List<Mower> listRecords() {

        List<Mower> list = new ArrayList<>();
        list.add(new Mower(0, 1, 2, Direction.NORTH, Strategy.CUSTOM, simulator.getSharedInfo()));
        list.add(new Mower(1, 1, 3, Direction.SOUTH, Strategy.RANDOM, simulator.getSharedInfo()));
        return list;
    }

    @GetMapping("/start")
    public void startSimulation(@RequestParam("inputFile") String inputFile) {

        // process get request with parameters
        // localhost8080:osmowsis/start?inputFile=gopher_scenario0.csv -v

        simulator.cleanUp();
        FileReader fileReader = new FileReader(simulator);

        try {
            checkInputFilePresence(inputFile.split(" "));
            fileReader.uploadStartingFile("src/main/resources/static/scenarios/" + inputFile.split(" ")[0]);
        } catch (Exception e) {
            System.out.println();
            System.out.println("ERROR: Test scenario file name not found. Please try again");
            return;
        }

        simulator.mainLoop();
        simulator.generateFinalReport();
    }

    @GetMapping("/report")
    public DTO generateReport() {
        return Utilities.getDto();
    }
}
