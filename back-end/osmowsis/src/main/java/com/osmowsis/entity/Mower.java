package com.osmowsis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.osmowsis.enums.Strategy;
import com.osmowsis.enums.*;

import java.util.*;

import static com.osmowsis.enums.Direction.*;
import static com.osmowsis.enums.MowerStatus.OK;
import static com.osmowsis.enums.Action.*;
import static com.osmowsis.utils.Utilities.xDIR_MAP;
import static com.osmowsis.utils.Utilities.yDIR_MAP;

public class Mower {

    private int id;
    private int mowerX;
    private int mowerY;
    private int proposedX;
    private int proposedY;
    private int energyCapacity;
    private int energyLevel;
    private int relativeToChargingPadX = 0;
    private int relativeToChargingPadY = 0;
    private boolean returnToChargingPad = false;
    private List<Action> actionsBack;
    private List<Direction> directionsBack;
    private Direction mowerDirection;
    private Direction trackNewDirection;
    private MowerStatus mowerStatus = OK;
    private Strategy strategy;
    private Action action;
    private Action trackNewAction;
    private String trackScanResults;
    @JsonIgnore
    private SharedInformation sharedInfo;

    public Mower(int id, int mowerX, int mowerY, Direction mowerDirection, Strategy strategy, SharedInformation sharedInfo) {
        this.id = id;
        this.mowerX = mowerX;
        this.mowerY = mowerY;
        this.mowerDirection = mowerDirection;
        this.strategy = strategy;
        this.sharedInfo = sharedInfo;
        this.actionsBack = new LinkedList<Action>();
        this.directionsBack = new LinkedList<Direction>();
    }

    public void lscan(String result) {
        trackScanResults = result;
    }

    /**
     *  Mower uses this method to check for surroundings.
     *  Textual representation is delivered by the simulator the mower is attached to.
     */
    public void scan(String result) {
        trackScanResults = result;
    }

    /**
     * Mower uses this method to update its current direction.
     * @param trackNewDirection
     */
    public void steer(Direction trackNewDirection) {
        mowerDirection = trackNewDirection;
    }

    /**
     * Mower uses this method to pass.
     */
    public void pass() {

    }

    /**
     * Mower uses this method to come up with proposed coordinates where it would like to move.
     * These coordinates will be subsequently validated by the simulator and, if validation passes, its current coordinates will be updated.
     */
    public void move() {

        int xOrientation, yOrientation;

        xOrientation = xDIR_MAP.get(mowerDirection);
        yOrientation = yDIR_MAP.get(mowerDirection);

        proposedX = mowerX + xOrientation;
        proposedY = mowerY + yOrientation;

        relativeToChargingPadX = relativeToChargingPadX + xOrientation;
        relativeToChargingPadY = relativeToChargingPadY + yOrientation;
    }

    /**
     * This method is used to come up with mower's current and subsequent actions.
     * Strategy: mower implements the "scan, than move" strategy where the preference will be given to a current,
     * already set direction, to avoid unnecessary steering (provided there are no obstacles in front). So as long as there's grass in front,
     * the action will be "scan-move-scan-move...". Once an obstacle is met, the preference will be given to the next clockwise direction
     * corresponding to a grass square. If there are no grass squares left around, the preference will be given to an empty square corresponding
     * to a current, already set direction, followed by the next clockwise direction corresponding to an empty square in case there are obstacles in front.
     * Strategy for the cases where mower is surrounded by craters or other mowers is omitted.
     * Mower does not accumulate knowledge of the lawn at this point, which is sufficient to pass all the provided test scenarios.
     */
    public void customAction() {
        int minEnergyBack = getPathToChargingPad();
        if (energyLevel < (minEnergyBack + 3)) {
            returnToChargingPad = true;
        }

        if (returnToChargingPad == true && !actionsBack.isEmpty()) {
            action = actionsBack.remove(0);
            if (action == STEER) {
                trackNewDirection = directionsBack.remove(0);
            }
            updateEnergyStatus();
            return;
        }

        if(trackNewAction != null) {
            action = trackNewAction;
            trackNewAction = null;
            trackScanResults = null;
            updateEnergyStatus();
            getPathToChargingPad();
            return;
        }

        if(trackScanResults == null) {
            action = CSCAN;
        } else {
            String[] scanArray = trackScanResults.split(",");
            // if the square at the default direction is GRASS, then we move there and scan on the following turn
            if(scanArray[mowerDirection.ordinal()].equals("grass")) {
                action = MOVE;
                trackNewAction = CSCAN;
            } else if (grassCountAround(trackScanResults) > 0){
                for (int i = 1; i < Direction.values().length; i++) {
                    int index = (mowerDirection.ordinal() + i) % Direction.values().length;
                    if(scanArray[index].equals("grass")) {
                        trackNewDirection = Direction.values()[index];
                        break;
                    }
                }
                action = STEER;
                trackNewAction = MOVE;
            } else {
                if (scanArray[mowerDirection.ordinal()].equals("empty")) {
                    action = MOVE;
                    trackNewAction = CSCAN;
                } else {
                    for (int i = 1; i < Direction.values().length; i++) {
                        int index = (mowerDirection.ordinal() + i) % Direction.values().length;
                        if(scanArray[index].equals("empty")) {
                            trackNewDirection = Direction.values()[index];
                            break;
                        }
                    }
                    action = STEER;
                    trackNewAction = MOVE;
                }
            }
            checkGopherPresenceAround();
        }
        updateEnergyStatus();
    }

    private void checkGopherPresenceAround() {
        String[] scanArray = trackScanResults.split(",");
        Set<Direction> dangerAreaSet = getDangerAreaSet(findRelativeGopherLocations());
        int safeSquaresCounter = 0;

        for (int i = 0; i < scanArray.length; i++) {
            String state = scanArray[i];
            if(!state.equals("fence") && !state.equals("mower") && !dangerAreaSet.contains(Direction.values()[i])) {
                safeSquaresCounter++;
            }
        }

        if(safeSquaresCounter == 0) {
            if(scanArray[trackNewDirection.ordinal()].equals("grass") || scanArray[trackNewDirection.ordinal()].equals("empty")) {
                action = MOVE;
                trackNewAction = CSCAN;
            } else {
                // if the square where we are running away to is occupied by another mower - find another direction.
                while (!scanArray[trackNewDirection.ordinal()].equals("grass") || !scanArray[trackNewDirection.ordinal()].equals("empty")) {
                    trackNewDirection = Direction.values()[(trackNewDirection.ordinal() + 1) % Direction.values().length];
                }
                action = STEER;
                trackNewAction = MOVE;
            }
        } else {
            if(dangerAreaSet.contains(trackNewDirection)) {
                action = STEER;
                trackNewAction = MOVE;

                for (int i = 0; i < scanArray.length; i++) {
                    String state = scanArray[i];
                    if(state.equals("grass") && !dangerAreaSet.contains(Direction.values()[i])) {
                        trackNewDirection = Direction.values()[i];
                        return;
                    }
                }
                for (int i = 0; i < scanArray.length; i++) {
                    String state = scanArray[i];
                    if(state.equals("empty") && !dangerAreaSet.contains(Direction.values()[i])) {
                        trackNewDirection = Direction.values()[i];
                        return;
                    }
                }
            }
        }
    }

    private Set<Direction> getDangerAreaSet(List<Integer> relativeGopherLocations) {
        Set<Direction> dangerArea = new HashSet<>();
        for (Integer i : relativeGopherLocations) {
            switch (i){
                case 0:
                    dangerArea.add(NORTH);
                    dangerArea.add(NORTHEAST);
                    dangerArea.add(EAST);
                    dangerArea.add(WEST);
                    dangerArea.add(NORTHWEST);
                    break;
                case 1:
                    dangerArea.add(NORTH);
                    dangerArea.add(NORTHEAST);
                    dangerArea.add(EAST);
                    break;
                case 2:
                    dangerArea.add(NORTH);
                    dangerArea.add(NORTHEAST);
                    dangerArea.add(EAST);
                    dangerArea.add(SOUTHEAST);
                    dangerArea.add(SOUTH);
                    break;
                case 3:
                    dangerArea.add(EAST);
                    dangerArea.add(SOUTHEAST);
                    dangerArea.add(SOUTH);
                    break;
                case 4:
                    dangerArea.add(EAST);
                    dangerArea.add(SOUTHEAST);
                    dangerArea.add(SOUTH);
                    dangerArea.add(SOUTHWEST);
                    dangerArea.add(WEST);
                    break;
                case 5:
                    dangerArea.add(SOUTH);
                    dangerArea.add(SOUTHWEST);
                    dangerArea.add(WEST);
                    break;
                case 6:
                    dangerArea.add(NORTH);
                    dangerArea.add(SOUTH);
                    dangerArea.add(SOUTHWEST);
                    dangerArea.add(WEST);
                    dangerArea.add(NORTHWEST);
                    break;
                case 7:
                    dangerArea.add(NORTH);
                    dangerArea.add(WEST);
                    dangerArea.add(NORTHWEST);
                    break;
            }
        }
        return dangerArea;
    }

    private List<Integer> findRelativeGopherLocations() {
        String[] states = getTrackScanResults().split(",");
        ArrayList<Integer> relativeGopherLocation = new ArrayList<>();
        for (int i = 0; i < states.length; i++) {
            String state = states[i];
            if(state.equals("gopher_grass") || state.equals("gopher_empty")) {
                relativeGopherLocation.add(i);
            }
        }
        return relativeGopherLocation;
    }

    /**
     * This method is used to come up with mower's random actions
     */
    public void randomAction() {
        Random randGenerator = new Random();
        int moveRandomChoice = randGenerator.nextInt(100);
        if (moveRandomChoice < 10) {
            // do nothing
            action = PASS;
        } else if (moveRandomChoice < 15) {
            // check your surroundings
            action = CSCAN;
        } else if (moveRandomChoice < 45) {
            action = LSCAN;
        } else if (moveRandomChoice < 60) {
            // change direction
            action = STEER;
        } else {
            // move forward
            action = MOVE;
        }

        // determine a new direction
        moveRandomChoice = randGenerator.nextInt(100);
        if (action == STEER && moveRandomChoice < 85) {
            int ptr = 0;
            while(!mowerDirection.equals(Direction.values()[ptr]) && ptr < Direction.values().length) {
                ptr++;
            }
            trackNewDirection = Direction.values()[(ptr + 1) % Direction.values().length];
        } else {
            trackNewDirection = mowerDirection;
        }

        updateEnergyStatus();
    }

    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * This method is used to count the number of grass squares around based on previous scan.
     * @param trackScanResults
     * @return int
     */
    private int grassCountAround(String trackScanResults){
        int counter = 0;
        String[] scanArray = trackScanResults.split(",");
        for (String s : scanArray) {
            if (s.equals("grass"))
                counter++;
        }
        return counter;
    }

    public void setEnergyCapacity(int energyCapacity) {
        this.energyCapacity = energyCapacity;
        this.energyLevel = energyCapacity;
    }

    private void updateEnergyStatus() {

        checkRecharge();
        switch (action) {
            case MOVE:
                energyLevel -= 2;
                break;
            case STEER:
            case CSCAN:
                energyLevel -= 1;
                break;
            case LSCAN:
                energyLevel -= 3;
                break;
        }
        checkEnergyLevel();
    }

    private void checkEnergyLevel() {
        if (energyLevel <= 0) {
            mowerStatus = MowerStatus.DISABLED;
        }
    }

    private void checkRecharge() {
        for(ChargingPad chargingPad: sharedInfo.getChargingPads()) {
            if(chargingPad.getX() == mowerX && chargingPad.getY() == mowerY) {
                energyLevel = energyCapacity;

                // reset nearest charging pad location
                relativeToChargingPadX = 0;
                relativeToChargingPadY = 0;
                returnToChargingPad = false;
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return mowerX;
    }

    public int getY() {
        return mowerY;
    }

    public void setX(int mowerX) {
        this.mowerX = mowerX;
    }

    public void setY(int mowerY) {
        this.mowerY = mowerY;
    }

    public void resetEnergyLevel(int energyLevel) {
        this.energyLevel=energyCapacity;
    }

    public Direction getMowerDirection() {
        return mowerDirection;
    }

    public MowerStatus getMowerStatus() {
        return mowerStatus;
    }

    public void setMowerStatus(MowerStatus mowerStatus) {
        this.mowerStatus = mowerStatus;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public Action getAction() {
        return action;
    }

    public Direction getTrackNewDirection() {
        return trackNewDirection;
    }

    public String getTrackScanResults() {
        return trackScanResults;
    }

    public int getProposedX() {
        return proposedX;
    }

    public int getProposedY() {
        return proposedY;
    }

    public int getEnergyLevel() {
        return energyLevel;
    }

    public Action getTrackNewAction() {
        return trackNewAction;
    }

    public void setProposedX(int proposedX) {
        this.proposedX = proposedX;
    }

    public void setProposedY(int proposedY) {
        this.proposedY = proposedY;
    }

    public void setEnergyLevel(int energyLevel) {
        this.energyLevel = energyLevel;
    }

    public void setTrackNewDirection(Direction trackNewDirection) {
        this.trackNewDirection = trackNewDirection;
    }

    public void setTrackNewAction(Action trackNewAction) {
        this.trackNewAction = trackNewAction;
    }

    public void setTrackScanResults(String trackScanResults) {
        this.trackScanResults = trackScanResults;
    }

    public SharedInformation getSharedInfo() {
        return sharedInfo;
    }

    public int getPathToChargingPad() {
        actionsBack.clear();
        directionsBack.clear();
        int energyConsumption = 0;

        int xDistance = Math.abs(relativeToChargingPadX);
        int yDistance = Math.abs(relativeToChargingPadY);

        // already at a charging pad location
        if (xDistance == 0 && yDistance == 0) {
            return energyConsumption;
        }

        int diff = Math.abs(xDistance - yDistance);
        Direction direction = null;
        int stepNum = diff;
        Direction diagonalDirection = null;
        int diagonalStepNum = 0;

        if (xDistance > yDistance) {
            if (relativeToChargingPadX > 0) {
                direction = WEST;
            } else if (relativeToChargingPadX < 0) {
                direction = EAST;
            }
            diagonalStepNum = yDistance;
        } else if (xDistance < yDistance) {
            if (relativeToChargingPadY > 0) {
                direction = SOUTH;
            } else if (relativeToChargingPadY < 0) {
                direction = NORTH;
            }
            diagonalStepNum = xDistance;
        } else {
            diagonalStepNum = xDistance;
        }

        if (relativeToChargingPadX > 0 && relativeToChargingPadY > 0) {
            diagonalDirection = SOUTHWEST;
        } else if (relativeToChargingPadX > 0 && relativeToChargingPadY < 0) {
            diagonalDirection = NORTHWEST;
        } else if (relativeToChargingPadX < 0 && relativeToChargingPadY > 0) {
            diagonalDirection = SOUTHEAST;
        } else if (relativeToChargingPadX < 0 && relativeToChargingPadY < 0) {
            diagonalDirection = NORTHEAST;
        }

        if (direction == mowerDirection && direction != null) {
            for(int i = 0; i < stepNum; i++) {
                actionsBack.add(MOVE);
                energyConsumption += 2;
            }

            if (diagonalDirection != null) {
                actionsBack.add(STEER);
                energyConsumption += 1;
                directionsBack.add(diagonalDirection);
                for(int i = 0; i < diagonalStepNum; i++) {
                    actionsBack.add(MOVE);
                    energyConsumption += 2;
                }
            }
        } else if (diagonalDirection == mowerDirection && diagonalDirection != null) {
            for(int i = 0; i < diagonalStepNum; i++) {
                actionsBack.add(MOVE);
                energyConsumption += 2;
            }

            if (direction != null) {
                actionsBack.add(STEER);
                energyConsumption += 1;
                directionsBack.add(direction);
                for(int i = 0; i < stepNum; i++) {
                    actionsBack.add(MOVE);
                    energyConsumption += 2;
                }
            }
        } else {
            if (direction != null) {
                actionsBack.add(STEER);
                energyConsumption += 1;
                directionsBack.add(direction);
                for (int i = 0; i < stepNum; i++) {
                    actionsBack.add(MOVE);
                    energyConsumption += 2;
                }
            }

            if (diagonalDirection != null) {
                actionsBack.add(STEER);
                energyConsumption += 1;
                directionsBack.add(diagonalDirection);
                for (int i = 0; i < diagonalStepNum; i++) {
                    actionsBack.add(MOVE);
                    energyConsumption += 2;
                }
            }
        }

//        System.out.println("relativeToChargingPad: x=" + relativeToChargingPadX + ", y=" + relativeToChargingPadY);
//        System.out.println(Arrays.toString(actionsBack.toArray()));
//        System.out.println(Arrays.toString(directionsBack.toArray()));
//        System.out.println("minEnergyBack: " + energyConsumption + ", energyLevel: " + energyLevel);
        return energyConsumption;
    }

    @Override
    public String toString() {
        return "Mower{" +
                "id=" + id +
                ", mowerX=" + mowerX +
                ", mowerY=" + mowerY +
                ", mowerDirection=" + mowerDirection +
                ", mowerStatus=" + mowerStatus +
                ", strategy=" + strategy +
                ", energy=" + energyLevel +
                ", returnToChargingPad=" + returnToChargingPad +
                '}';
    }
}
