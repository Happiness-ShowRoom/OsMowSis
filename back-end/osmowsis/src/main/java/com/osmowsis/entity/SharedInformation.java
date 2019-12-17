package com.osmowsis.entity;

import java.util.ArrayList;
import java.util.List;

public class SharedInformation {
    private List<ChargingPad> chargingPads;
    public Lawn estimatedLawn;

    public SharedInformation() {
        this.chargingPads = new ArrayList<>();
        estimatedLawn = new Lawn();
        estimatedLawn.setUnknownArea();
    }

    public void addChargingPad(ChargingPad chargingPad) {
        chargingPads.add(chargingPad);
    }

    public List<ChargingPad> getChargingPads(){
        return chargingPads;
    }
}
