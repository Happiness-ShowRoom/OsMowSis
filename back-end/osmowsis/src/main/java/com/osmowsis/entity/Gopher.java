package com.osmowsis.entity;

import com.osmowsis.enums.Action;
import com.osmowsis.enums.MowerStatus;
import com.osmowsis.simulator.Simulator;

import java.util.List;

public class Gopher {
    private int id;
    private int gopherX;
    private int gopherY;
    private int proposedX;
    private int proposedY;
    private Mower targetedMower;
    private Action action;

    public Gopher(int id, int gopherX, int gopherY) {
        this.id = id;
        this.gopherX = gopherX;
        this.gopherY = gopherY;
    }

    /**
     * Gopher uses this method to pass if there's already another gopher targeting the same mower
     */
    public void pass() {

    }

    /**
     * Gopher uses this method to come up with proposed coordinates where it would like to move.
     * These coordinates will be subsequently validated by the simulator and, if validation passes, its current coordinates will be updated.
     */
    public void move() {
        gopherX = proposedX;
        gopherY = proposedY;
    }
    
    public void determineClosestMowerAndProposedCoordinates(List<Mower> mowers){
        int distanceToClosestMower = Integer.MAX_VALUE;

        for (Mower mower: mowers) {
            if(mower.getMowerStatus() == MowerStatus.CRASH || mower.getMowerStatus() == MowerStatus.DISABLED) {
                continue;
            }
            int xDistance = Math.abs(mower.getX() - gopherX);
            int yDistance = Math.abs(mower.getY() - gopherY);
            int distanceToCurrentMower = xDistance + yDistance;

            if(distanceToCurrentMower < distanceToClosestMower || (xDistance == 1 && yDistance == 1)) {
                distanceToClosestMower = distanceToCurrentMower;
                targetedMower = mower;
            }
        }

        if (targetedMower != null) {
            if (targetedMower.getX() == gopherX) {
                proposedX = gopherX;
            } else if (targetedMower.getX() > gopherX) {
                proposedX = gopherX + 1;
            } else if (targetedMower.getX() < gopherX) {
                proposedX = gopherX - 1;
            }

            if (targetedMower.getY() == gopherY) {
                proposedY = gopherY;
            } else if (targetedMower.getY() > gopherY) {
                proposedY = gopherY + 1;
            } else if (targetedMower.getY() < gopherY) {
                proposedY = gopherY - 1;
            }
        }
    }

    public void determineAction(List<Gopher> gophers) {
        for (Gopher gopher : gophers) {
            if(gopher == this) {
                continue;
            }

            if(gopher.getProposedX() == proposedX && gopher.getProposedY() == proposedY) {
                if(gopher.getAction() == Action.PASS) {
                    action = Action.MOVE;
                } else {
                    action = Action.PASS;
                }
            } else {
                action = Action.MOVE;
            }
        }
    }

    public Mower getTargetedMower() {
        return targetedMower;
    }

    public int getGopherX() {
        return gopherX;
    }

    public int getGopherY() {
        return gopherY;
    }

    public int getProposedX() {
        return proposedX;
    }

    public int getProposedY() {
        return proposedY;
    }

    public Action getAction() {
        return action;
    }

    public int getId() {
        return id;
    }
}

