package com.osmowsis.simulator;

import com.osmowsis.entity.*;
import com.osmowsis.enums.GridSquareState;
import com.osmowsis.enums.*;

import java.util.*;

import com.osmowsis.dto.Turn;
import org.springframework.stereotype.Component;

import static com.osmowsis.utils.Utilities.populateDirectoryMap;
import static com.osmowsis.enums.Action.*;
import static com.osmowsis.enums.GridSquareState.*;
import static com.osmowsis.enums.MowerStatus.*;
import static com.osmowsis.utils.Utilities.*;

@Component
public class Simulator {

    private String trackMoveCheck;
    private Integer turnLimit;
    private Integer gopherPeriod;
    private int trackTurnsCompleted = 0;

    private Lawn lawn;
    private List<Mower> mowers;
    private List<Gopher> gophers;

    private SharedInformation sharedInfo;

    public Simulator() {
        this.gophers = new ArrayList<>();
        this.mowers = new ArrayList<>();
        this.lawn = new Lawn();
        this.sharedInfo = new SharedInformation();
        populateDirectoryMap();
    }

    /**
     * This method reads in the lawn information from the input file using provided scanner
     * @param scanner
     */
    public void initializeLawn(Scanner scanner) {
        String[] tokens;
        tokens = scanner.nextLine().split(",");
        int width = Integer.parseInt(tokens[0]);
        tokens = scanner.nextLine().split(",");
        int height = Integer.parseInt(tokens[0]);
        lawn.setLawnWidth(width);
        lawn.setLawnHeight(height);

        lawn.setTotalGrassCount(width * height);

        // generate the lawn information
        lawn.lawnInfo = new GridSquareState[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                lawn.lawnInfo[i][j] = GridSquareState.GRASS;
            }
        }
    }

    /**
     * This method reads in the information about mowers from the input file using provided scanner
     * @param scanner
     */
    public void initializeMowers(Scanner scanner) {
        // read in the lawnmower starting information
        String[] tokens = scanner.nextLine().split(",");
        int numberOfMowers = Integer.parseInt(tokens[0]);

        for (int k = 0; k < numberOfMowers; k++) {
            tokens = scanner.nextLine().split(",");
            int x = Integer.parseInt(tokens[0]);
            int y = Integer.parseInt(tokens[1]);
            Mower m = new Mower(k, x, y, Direction.valueOf(tokens[2].toUpperCase()), Strategy.valueOf(Integer.parseInt(tokens[3]) == 0 ? "RANDOM": "CUSTOM"), sharedInfo);
            mowers.add(m);
            sharedInfo.addChargingPad(new ChargingPad(k, x, y));
            lawn.lawnInfo[x][y] = EMPTY;
        }

        tokens = scanner.nextLine().split(",");
        Iterator<Mower> iterator = mowers.iterator();
        while(iterator.hasNext()) {
            iterator.next().setEnergyCapacity(Integer.parseInt(tokens[0]));
        }
    }

    /**
     * This method reads in the information about gophers from the input file using provided scanner.
     * @param scanner
     */
    public void initializeGophers(Scanner scanner) {
        // read in the crater information
        String[] tokens = scanner.nextLine().split(",");
        int numberOfGophers = Integer.parseInt(tokens[0]);
        for (int k = 0; k < numberOfGophers; k++) {
            tokens = scanner.nextLine().split(",");
            int x = Integer.parseInt(tokens[0]);
            int y = Integer.parseInt(tokens[1]);

            gophers.add(new Gopher(k, x, y));
        }
    }

    /**
     * This method reads in the gopher movement period information from the input file using provided scanner
     * @param scanner
     */
    public void initializeGopherPeriod(Scanner scanner) {
        String[] tokens;
        tokens = scanner.nextLine().split(",");
        gopherPeriod = Integer.parseInt(tokens[0]);
    }

    /**
     * This method reads in the max turn limit information from the input file using provided scanner
     * @param scanner
     */
    public void initializeTurnLimit(Scanner scanner) {
        String[] tokens;
        tokens = scanner.nextLine().split(",");
        turnLimit = Integer.parseInt(tokens[0]);
    }

    /**
     * Main loop of the application
     */
    public void mainLoop() {
        // run the simulation for a fixed number of steps
        while (trackTurnsCompleted < simulationDuration() && !mowersAllStopped() && lawn.getCutGrassCount() < lawn.getTotalGrassCount()) {

            Turn turn = new Turn(mowers, gophers, sharedInfo.getChargingPads());

            for (int k = 0; k < mowers.size(); k++) {

                if (mowerCrashed(mowers.get(k))) { continue; }

                pollMowerForAction(mowers.get(k));
                validateMowerAction(mowers.get(k));
                displayMowersActionAndResponses(mowers.get(k), trackMoveCheck, turn);
                String[][] lawnResult = renderLawnResult(lawn, mowers, gophers, sharedInfo.getChargingPads());
                turn.getMowersAfterActionLawnState().put(k, lawnResult);

                // render the state of the lawn after each command
                if (showState) {
                    renderLawn(lawn, mowers, gophers, sharedInfo.getChargingPads());
//                     for debug printing
//                    for (int x = 0; x < lawn.DEFAULT_HEIGHT+2; x++) {
//                        System.out.println(Arrays.toString(lawnResult[x]));
//                    }
                }
            }

            if((trackTurnsCompleted + 1) % gopherPeriod == 0) {
                for (int i = 0; i < gophers.size(); i++) {
                    Gopher gopher = gophers.get(i);
                    gopher.determineClosestMowerAndProposedCoordinates(mowers);
                    gopher.determineAction(gophers);
                    validateGopherAction(gopher);

                    displayGophersActionAndResponses(gopher, turn);
                    String[][] lawnResult = renderLawnResult(lawn, mowers, gophers, sharedInfo.getChargingPads());
                    turn.getGophersAfterActionLawnState().put(i, lawnResult);

                    // render the state of the lawn after each command
                    if (showState) {
                        renderLawn(lawn, mowers, gophers, sharedInfo.getChargingPads());
//                     for debug printing
//                        for (int x = 0; x < lawn.DEFAULT_HEIGHT+2; x++) {
//                            System.out.println(Arrays.toString(lawnResult[x]));
//                        }
                    }
                }
            }
            getDto().getStepsInfo().put(trackTurnsCompleted, turn);
            trackTurnsCompleted++;
        }
    }

    /**
     * This method polls the given mower for its proposed action.
     * Action is dependent on the strategy of the given mower.
     * @param mower
     */
    private void pollMowerForAction(Mower mower) {

        if(mowerStopped(mower)) {
            mower.setAction(PASS);
            return;
        }

        if (mower.getStrategy() == Strategy.RANDOM) {
            mower.randomAction();
        } else {
            mower.customAction();
        }
    }

    /**
     * This method validates the action that a given mower has proposed.
     * @param mower
     */
    private void validateMowerAction(Mower mower) {

        if (mower.getAction() == CSCAN) {
            // in the case of a scan, return the information for the eight surrounding squares
            // always use a northbound orientation
            mower.scan(provideScanResults(mower.getX(), mower.getY()));
            trackMoveCheck = "ok";
        } else if (mower.getAction() == LSCAN){
            mower.lscan(provideLscanResults(mower.getX(), mower.getY(), mower.getMowerDirection()));
            trackMoveCheck = "ok";
        } else if (mower.getAction() == PASS) {
            mower.pass();
            trackMoveCheck = "ok";

        } else if (mower.getAction() == STEER) {
            // check if direction is valid
            if (isDirectionValid(mower.getTrackNewDirection())) {
                mower.steer(mower.getTrackNewDirection());
                trackMoveCheck = "ok";
            } else {
                trackMoveCheck = "crash";
                mower.setMowerStatus(CRASH);
            }

        } else if (mower.getAction() == MOVE) {
            mower.move();
            if (mower.getProposedX() < 0 || mower.getProposedX() >= lawn.getLawnWidth() || mower.getProposedY() < 0 || mower.getProposedY() >= lawn.getLawnHeight()) {
                // mower hit a fence
                mower.setMowerStatus(CRASH);
                trackMoveCheck = "crash";
            } else if (isMowerThere(mower.getProposedX(), mower.getProposedY())) {
                // mower collided with the other mower
                for (Mower otherMower : mowers) {
                    if (otherMower.getMowerStatus() == OK && otherMower.getX() == mower.getProposedX() && otherMower.getY() == mower.getProposedY()) {
                        otherMower.setMowerStatus(CRASH);
                        break;
                    }
                }
                mower.setMowerStatus(CRASH);
                trackMoveCheck = "crash";
            } else if (isGopherThere(mower.getProposedX(), mower.getProposedY())) {
                // mower stepped into the square where gopher is
                mower.setMowerStatus(DISABLED);
                lawn.lawnInfo[mower.getProposedX()][mower.getProposedY()] = EMPTY;
                trackMoveCheck = "ok";
            } else {
                // mower move is successful
                mower.setX(mower.getProposedX());
                mower.setY(mower.getProposedY());
                // update lawn status
                lawn.lawnInfo[mower.getProposedX()][mower.getProposedY()] = EMPTY;
                trackMoveCheck = "ok";
            }
        } else {
            mower.setMowerStatus(CRASH);
            trackMoveCheck = "crash";
        }

        if (trackMoveCheck == "ok" && mower.getEnergyLevel() <= 0) {
            trackMoveCheck = "disable";
        }
    }

    /**
     * This method validates the action that a given gopher has proposed.
     * @param gopher
     */
    private void validateGopherAction(Gopher gopher) {
        if(gopher.getAction() == MOVE) {
            gopher.move();
            for (Mower mower : mowers) {
                if(mower.getMowerStatus() != CRASH) {
                    if(mower.getX() == gopher.getGopherX() && mower.getY() == gopher.getGopherY()) {
                        mower.setMowerStatus(DISABLED);
                    }
                }
            }
        } else {
            gopher.pass();
        }
    }

    /**
     * This method provides the textual representation of the scan of surrounding 8 squares, based on the current coordinates of the given mower.
     * @param targetX
     * @param targetY
     * @return
     */
    public String provideScanResults(int targetX, int targetY) {
        String nextSquare, resultString = "";

        for (int k = 0; k < Direction.values().length; k++) {
            Direction direction = Direction.values()[k];
            int offsetX = xDIR_MAP.get(direction);
            int offsetY = yDIR_MAP.get(direction);

            int checkX = targetX + offsetX;
            int checkY = targetY + offsetY;

            nextSquare = getSpecificSquareInfo(checkX, checkY);

            if (resultString.isEmpty()) { resultString = nextSquare; }
            else { resultString = resultString + "," + nextSquare; }
        }

        return resultString;
    }

    public String provideLscanResults(int checkX, int checkY, Direction direction) {
        String nextSquare = "";
        StringBuilder result = new StringBuilder();

        while(nextSquare !=  "fence") {
            checkX += xDIR_MAP.get(direction);
            checkY += yDIR_MAP.get(direction);

            nextSquare = getSpecificSquareInfo(checkX, checkY);
            result.append(nextSquare).append(",");
        }
        String resultString = result.toString();
        return resultString.substring(0, resultString.length()-1);
    }

    private String getSpecificSquareInfo(int x, int y) {
        String square = "";
        if (x < 0 || x >= lawn.getLawnWidth() || y < 0 || y >= lawn.getLawnHeight()) {
            square = "fence";
        } else if (isMowerThere(x, y)) {
            square = "mower";
        } else {
            switch (lawn.lawnInfo[x][y]) {
                case EMPTY:
                    if(isGopherThere(x, y)) {
                        square = "gopher_empty";
                    } else {
                        square = "empty";
                    }
                    break;
                case GRASS:
                    if(isGopherThere(x, y)) {
                        square = "gopher_grass";
                    } else {
                        square = "grass";
                    }
                    break;
                default:
                    square = "unknown";
                    break;
            }
        }
        return square;
    }

    /**
     * This method lets us know if all mowers have been stopped.
     * @return
     */
    private Boolean mowersAllStopped() {
        for (Mower mower : mowers) {
            if (mower.getMowerStatus() == OK) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * This method lets us know if the particular mower has crashed.
     * @param mower
     * @return
     */
    private Boolean mowerCrashed(Mower mower) {
        return mower.getMowerStatus() == CRASH;
    }

    /**
     * This method lets us know if the particular mower is disabled either due to gopher attack or battery discharge.
     * @param mower
     * @return
     */
    private Boolean mowerStopped(Mower mower) {
        return mower.getMowerStatus() == DISABLED;
    }

    /**
     * This method requests final results from the respective method in Utilities class
     */
    public void generateFinalReport() {
        generateReport(lawn, trackTurnsCompleted);
    }

    /**
     * This method provides the max simulation duration
     * @return
     */
    private Integer simulationDuration() {
        return turnLimit;
    }

    /**
     * This method lets us know if there's a mower at the particular X and Y coordinates.
     * @param checkX
     * @param checkY
     * @return
     */
    private boolean isMowerThere(int checkX, int checkY) {
        // determines if there's a mower at the particular X and Y position
        for (Mower mower : mowers) {
            if (mower.getMowerStatus() == OK && mower.getX() == checkX && mower.getY() == checkY) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method lets us know if there's a gopher at the particular X and Y coordinates.
     * @param checkX
     * @param checkY
     * @return
     */
    private boolean isGopherThere(int checkX, int checkY) {
        // determines if there's a gopher at the particular X and Y position
        for (Gopher gopher : gophers) {
            if (gopher.getGopherX() == checkX && gopher.getGopherY() == checkY) {
                return true;
            }
        }
        return false;
    }

    public SharedInformation getSharedInfo() {
        return sharedInfo;
    }

    public void cleanUp() {
        mowers.clear();
        gophers.clear();
        sharedInfo.getChargingPads().clear();
    }
}
