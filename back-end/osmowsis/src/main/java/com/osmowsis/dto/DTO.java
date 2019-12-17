package com.osmowsis.dto;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DTO {

    private Map<Integer, Turn> stepsInfo = new HashMap<>();

    private int lawnSize;
    private int potentialCut;
    private int actualCut;
    private int turnsCompleted;


    public int getLawnSize() {
        return lawnSize;
    }

    public void setLawnSize(int lawnSize) {
        this.lawnSize = lawnSize;
    }

    public int getPotentialCut() {
        return potentialCut;
    }

    public void setPotentialCut(int potentialCut) {
        this.potentialCut = potentialCut;
    }

    public int getActualCut() {
        return actualCut;
    }

    public void setActualCut(int actualCut) {
        this.actualCut = actualCut;
    }

    public int getTurnsCompleted() {
        return turnsCompleted;
    }

    public void setTurnsCompleted(int turnsCompleted) {
        this.turnsCompleted = turnsCompleted;
    }

    public Map<Integer, Turn> getStepsInfo() {
        return stepsInfo;
    }
}
