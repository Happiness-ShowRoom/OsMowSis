package com.osmowsis.entity;

public class ChargingPad {
    private int id;
    private int chargingPadX;
    private int chargingPadY;

    public ChargingPad(int id, int chargingPadX, int chargingPadY) {
        this.id = id;
        this.chargingPadX = chargingPadX;
        this.chargingPadY = chargingPadY;
    }

    public int getX() {
        return chargingPadX;
    }

    public int getY() {
        return chargingPadY;
    }
}
