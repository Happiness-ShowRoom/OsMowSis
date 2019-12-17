package com.osmowsis.entity;

import com.osmowsis.enums.GridSquareState;

public class Lawn {

    public static final int DEFAULT_WIDTH = 15;
    public static final int DEFAULT_HEIGHT = 10;

    private Integer lawnWidth;
    private Integer lawnHeight;
    private int totalGrassCount;
    public GridSquareState[][] lawnInfo;

    public Lawn() {
        this.lawnWidth = DEFAULT_WIDTH;
        this.lawnHeight = DEFAULT_HEIGHT;
    }

    /**
     * This method is used at the beginning of each iteration to calculate the current number of grass squares.
     * @return
     */
    public int getCutGrassCount() {
        int count = 0;

        for (int i = 0; i < lawnInfo.length; i++) {
            for (int j = 0; j < lawnInfo[0].length; j++) {
                if(lawnInfo[i][j] == GridSquareState.EMPTY || lawnInfo[i][j] == GridSquareState.MOWER || lawnInfo[i][j] == GridSquareState.GOPHER_EMPTY) {
                    count++;
                }
            }
        }
        return count;
    }

    public Integer getLawnWidth() {
        return lawnWidth;
    }

    public void setLawnWidth(Integer lawnWidth) {
        this.lawnWidth = lawnWidth;
    }

    public Integer getLawnHeight() {
        return lawnHeight;
    }

    public void setLawnHeight(Integer lawnHeight) {
        this.lawnHeight = lawnHeight;
    }

    public int getTotalGrassCount() {
        return totalGrassCount;
    }

    public void setTotalGrassCount(int totalGrassCount) {
        this.totalGrassCount = totalGrassCount;
    }

    public void setUnknownArea() {
        lawnInfo = new GridSquareState[lawnWidth][lawnHeight];
        for (int i = 0; i < lawnInfo.length; i++) {
            for (int j = 0; j < lawnInfo[0].length; j++) {
                lawnInfo[i][j] = GridSquareState.UNKNOWN;
            }
        }
    }
}
