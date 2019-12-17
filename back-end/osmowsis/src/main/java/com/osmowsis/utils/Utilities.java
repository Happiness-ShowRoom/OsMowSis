package com.osmowsis.utils;

import com.osmowsis.dto.DTO;
import com.osmowsis.dto.Turn;
import com.osmowsis.entity.*;
import com.osmowsis.enums.Direction;
import com.osmowsis.enums.MowerStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.osmowsis.enums.Action.*;
import static com.osmowsis.enums.Direction.*;
import static com.osmowsis.enums.GridSquareState.*;
import static com.osmowsis.enums.MowerStatus.*;

public class Utilities {

    public static Boolean showState = Boolean.FALSE;

    public static HashMap<Direction, Integer> xDIR_MAP;
    public static HashMap<Direction, Integer> yDIR_MAP;

    private static DTO dto = new DTO();

    /**
     * This method draws horizontal bar using standard output
     * @param size
     */
    private static void renderHorizontalBar(int size) {
        System.out.print(" ");
        for (int k = 0; k < size; k++) {
            System.out.print("-");
        }
        System.out.println("");
    }

    /**
     * This method generates a final report for the application
     * @param lawn
     * @param trackTurnsCompleted
     */
    public static void generateReport(Lawn lawn, int trackTurnsCompleted) {
        int lawnSize = lawn.getLawnWidth() * lawn.getLawnHeight();
        int numGrass = 0;
        for (int i = 0; i < lawn.getLawnWidth(); i++) {
            for (int j = 0; j < lawn.getLawnHeight(); j++) {
//                if (lawn.lawnInfo[i][j] == CRATER) { numCraters++; }
                if (lawn.lawnInfo[i][j] == GRASS) { numGrass++; }
            }
        }
        int potentialCut = lawnSize;
        int actualCut = potentialCut - numGrass;
        System.out.println(String.valueOf(lawnSize) + "," + String.valueOf(potentialCut) + "," + String.valueOf(actualCut) + "," + String.valueOf(trackTurnsCompleted));
        dto.setLawnSize(lawnSize);
        dto.setPotentialCut(potentialCut);
        dto.setActualCut(actualCut);
        dto.setTurnsCompleted(trackTurnsCompleted);
    }

    /**
     * This method renders the lawn using standard output in case -v or -verbose option was selected.
     * @param lawn
     * @param mowers
     */
    public static void renderLawn(Lawn lawn, List<Mower> mowers, List<Gopher> gophers, List<ChargingPad> chargingPads) {
        int i, j;
        int charWidth = 5 * lawn.getLawnWidth() + 2;

        // display the rows of the lawn from top to bottom
        for (j = lawn.getLawnHeight() - 1; j >= 0; j--) {
            Utilities.renderHorizontalBar(charWidth);

            // display the Y-direction identifier
            System.out.print(j);

            // display the contents of each square on this row
            for (i = 0; i < lawn.getLawnWidth(); i++) {
                System.out.print("|");

                String state = "";
                switch (lawn.lawnInfo[i][j]) {
                    case EMPTY:
                        state = "e";
                        break;
                    case GRASS:
                        state = "g";
                        break;
                    default:
                        break;
                }


                for (int id = 0; id < chargingPads.size(); id++) {
                    if (chargingPads.get(id).getX() == i && chargingPads.get(id).getY() == j) {
                        state = "c" + state;
                    }
                }

                // mower overrides charging pad
                for (int id = 0; id < mowers.size(); id++) {
                    if ((mowers.get(id).getMowerStatus() == OK || mowers.get(id).getMowerStatus() == DISABLED) && mowers.get(id).getX() == i && mowers.get(id).getY() == j) {
                        state = "m" + String.valueOf(id);
                    }
                }

                // gopher overrides both charging pad and mower
                for (int id = 0; id < gophers.size(); id++) {
                    if (gophers.get(id).getGopherX() == i && gophers.get(id).getGopherY() == j) {
                        state = "g" + String.valueOf(id) + state;
                    }
                }
                System.out.print(String.format("%1$4s", state));
            }
            System.out.println("|");
        }
        Utilities.renderHorizontalBar(charWidth);

        // display the column X-direction identifiers
        System.out.print(" ");
        for (i = 0; i < lawn.getLawnWidth(); i++) {
            if (i < 10) {
                System.out.print("    " + i);
            } else {
                System.out.print("   " + i);
            }
        }
        System.out.println("");

        // display the mower's directions
        for(int k = 0; k < mowers.size(); k++) {
            if (mowers.get(k).getMowerStatus() == MowerStatus.CRASH) { continue; }
            System.out.println(mowers.get(k));
        }
        System.out.println("");
    }

    public static String[][] renderLawnResult(Lawn lawn, List<Mower> mowers, List<Gopher> gophers, List<ChargingPad> chargingPads) {
        int resultHeight = lawn.DEFAULT_HEIGHT+2;
        int resultWidth = lawn.DEFAULT_WIDTH+2;
        String[][] lawnResult = new String[resultHeight][resultWidth];

        // fence
        for (int i = 0; i < lawn.getLawnWidth()+2; i++) {
            lawnResult[0][i] = "fence"; // top
            lawnResult[lawn.getLawnHeight()+1][i] = "fence"; // bottom
        }

        // display the rows of the lawn from top to bottom
        for (int j = 0; j < lawn.getLawnHeight(); j++) {
            int resultY = lawn.getLawnHeight() - j;
            // left fence
            lawnResult[resultY][0] = "fence";
            //right fence
            lawnResult[resultY][lawn.getLawnWidth()+1] = "fence";

            // display the contents of each square on this row
            for (int i = 0; i < lawn.getLawnWidth(); i++) {
                String state = "";
                switch (lawn.lawnInfo[i][j]) {
                    case EMPTY:
                    case GOPHER_EMPTY:
                        lawnResult[resultY][i+1] = "empty";
                        break;
                    case GRASS:
                    case GOPHER_GRASS:
                        lawnResult[resultY][i+1] = "grass";
                        break;
                    default:
                        break;
                }
            }
        }

        // the mower overrides all other contents
        for (int id = 0; id < mowers.size(); id++) {
            Mower mower = mowers.get(id);
            if (mower.getMowerStatus() == OK) {
                lawnResult[lawn.getLawnHeight() - mower.getY()][mower.getX()+1] = "mower" + id + "_" + mower.getMowerDirection() + "_" + mower.getEnergyLevel();
            } else if (mower.getMowerStatus() == DISABLED){
                lawnResult[lawn.getLawnHeight() - mower.getY()][mower.getX()+1] = "mower_disabled";
            }
        }

        // gopher information
        for (int id = 0; id < gophers.size(); id++) {
            Gopher gopher = gophers.get(id);
            lawnResult[lawn.getLawnHeight() - gopher.getGopherY()][gopher.getGopherX()+1] = "gopher_" + lawnResult[gopher.getGopherY()+1][gopher.getGopherX()+1];
        }

        // charging pad
        for (int i = 0; i < chargingPads.size(); i++) {
            ChargingPad chargingPad = chargingPads.get(i);
            int x = chargingPad.getX();
            int y = chargingPad.getY();
            if (lawnResult[lawn.getLawnHeight() - y][x+1] == "empty") {
                lawnResult[lawn.getLawnHeight() - y][x+1] = "chargingpad";
            }
        }
        return lawnResult;
    }

    /**
     * This method displays the mower's proposed actions and validation results.
     * @param mower
     * @param trackMoveCheck
     * @param turn
     */
    public static void displayMowersActionAndResponses(Mower mower, String trackMoveCheck, Turn turn) {
        // display the mower's actions
        String actionAndResponses = "m" + String.valueOf(mower.getId()) + "," + mower.getAction().toString().toLowerCase();
        if (mower.getAction() == STEER) {
            actionAndResponses += "," + mower.getTrackNewDirection().toString().toLowerCase();
        }
        System.out.print(actionAndResponses);
        System.out.println();
        actionAndResponses += "\n";

        // display the simulation checks and/or responses
        if (mower.getAction() == MOVE || mower.getAction() == STEER || mower.getAction() == PASS) {
            actionAndResponses += trackMoveCheck + "\n";
            System.out.println(trackMoveCheck);
        } else if (mower.getAction() == CSCAN) {
            actionAndResponses += mower.getTrackScanResults() + "\n";
            System.out.println(mower.getTrackScanResults());
        } else if (mower.getAction() == LSCAN) {
            System.out.println(mower.getTrackScanResults());
        } else {
            System.out.println("action not recognized");
        }

        turn.getMowersActions().put(mower.getId(), actionAndResponses);
    }

    /**
     * This method displays gopher's actions.
     * @param gopher
     * @param turn
     */
    public static void displayGophersActionAndResponses(Gopher gopher, Turn turn) {
        // display the gopher's actions
        String actionAndResponses = "g" + String.valueOf(gopher.getId()) + ",m" + gopher.getTargetedMower().getId();
        if(gopher.getAction() == MOVE) {
            actionAndResponses += "," + gopher.getProposedX() + "," + gopher.getProposedY();
        } else {
            actionAndResponses += "," + gopher.getGopherX() + "," + gopher.getGopherY();
        }
        System.out.println(actionAndResponses);

//        turn.getMowersActions().put(gopher.getId(), actionAndResponses);
    }

    /**
     * This method populates auxiliary map used to translate direction into coordinates.
     */
    public static void populateDirectoryMap() {
        xDIR_MAP = new HashMap<>();
        xDIR_MAP.put(NORTH, 0);
        xDIR_MAP.put(NORTHEAST, 1);
        xDIR_MAP.put(EAST, 1);
        xDIR_MAP.put(SOUTHEAST, 1);
        xDIR_MAP.put(SOUTH, 0);
        xDIR_MAP.put(SOUTHWEST, -1);
        xDIR_MAP.put(WEST, -1);
        xDIR_MAP.put(NORTHWEST, -1);

        yDIR_MAP = new HashMap<>();
        yDIR_MAP.put(NORTH, 1);
        yDIR_MAP.put(NORTHEAST, 1);
        yDIR_MAP.put(EAST, 0);
        yDIR_MAP.put(SOUTHEAST, -1);
        yDIR_MAP.put(SOUTH, -1);
        yDIR_MAP.put(SOUTHWEST, -1);
        yDIR_MAP.put(WEST, 0);
        yDIR_MAP.put(NORTHWEST, 1);
    }

    /**
     * This method is used to validate if the mower's proposed direction is valid
     * @param proposedDirection
     * @return
     */
    public static boolean isDirectionValid(Direction proposedDirection) {
        for (Direction direction : Direction.values()) {
            if (proposedDirection == direction) {
                return true;
            }
        }
        return false;
    }

    public static DTO getDto() {
        return dto;
    }
}
