package com.osmowsis.dto;

import com.osmowsis.entity.ChargingPad;
import com.osmowsis.entity.Gopher;
import com.osmowsis.entity.Mower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Turn {

    List<Mower> mowers;
    List<Gopher> gophers;
    List<ChargingPad> chargingPads;

    Map<Integer, String> mowersActions = new HashMap<>();
    Map<Integer, String[][]> mowersAfterActionLawnState = new HashMap<>();

    Map<Integer, String> gophersActions = new HashMap<>();
    Map<Integer, String[][]> gophersAfterActionLawnState = new HashMap<>();

    public Turn(List<Mower> mowers, List<Gopher> gophers, List<ChargingPad> chargingPads) {
        copyMowersInfo(mowers);
        copyGophersInfo(gophers);
        copyChargingPadsInfo(chargingPads);
    }

    /**
     * Taking snapshot of mowers list at the beginning of each turn
     * @param mowers
     */
    private void copyMowersInfo(List<Mower> mowers) {
        this.mowers = new ArrayList<>(mowers.size());
        for (int i = 0; i < mowers.size(); i++) {
            Mower mowerOrigin = mowers.get(i);
            Mower mowerCopy = new Mower(i, mowerOrigin.getX(), mowerOrigin.getY(), mowerOrigin.getMowerDirection(), mowerOrigin.getStrategy(), mowerOrigin.getSharedInfo());
            mowerCopy.setEnergyLevel(mowerOrigin.getEnergyLevel());
            mowerCopy.setMowerStatus(mowerOrigin.getMowerStatus());
            mowerCopy.setAction(mowerOrigin.getAction());
            mowerCopy.setTrackNewAction(mowerOrigin.getTrackNewAction());
            mowerCopy.setTrackNewDirection(mowerOrigin.getTrackNewDirection());
            mowerCopy.setTrackScanResults(mowerOrigin.getTrackScanResults());
            this.mowers.add(mowerCopy);
        }
    }

    /**
     * Taking snapshot of gophers list at the beginning of each turn
     * @param gophers
     */
    private void copyGophersInfo(List<Gopher> gophers) {
        this.gophers = new ArrayList<>(gophers.size());
        for (int i = 0; i < gophers.size(); i++) {
            Gopher gopher = gophers.get(i);
            this.gophers.add(new Gopher(i, gopher.getGopherX(), gopher.getGopherY()));
        }
    }

    /**
     * Taking snapshot of gophers list at the beginning of each turn
     * @param chargingPads
     */
    private void copyChargingPadsInfo(List<ChargingPad> chargingPads) {
        this.chargingPads = new ArrayList<>(chargingPads.size());
        for (int i = 0; i < chargingPads.size(); i++) {
            ChargingPad chargingPad = chargingPads.get(i);
            this.chargingPads.add(new ChargingPad(i, chargingPad.getX(), chargingPad.getY()));
        }
    }

    public Map<Integer, String> getMowersActions() {
        return mowersActions;
    }

    public Map<Integer, String[][]> getMowersAfterActionLawnState() {
        return mowersAfterActionLawnState;
    }

    public Map<Integer, String> getGophersActions() {
        return gophersActions;
    }

    public Map<Integer, String[][]> getGophersAfterActionLawnState() {
        return gophersAfterActionLawnState;
    }

    public List<Mower> getMowers() {
        return mowers;
    }

    public List<Gopher> getGophers() {
        return gophers;
    }

    public List<ChargingPad> getChargingPads() {
        return chargingPads;
    }
}
