package com.spreo.sdk.setting;

import android.graphics.Bitmap;

/**
 * Class that holds the options values for GUI of multi-Pois navigation
 *
 * @author Spreo
 */
public class MultiPoiUIOptions {

    private boolean includeEntrances = false;
    private boolean includeSwitchFloors = false;
    private String multiPoisPointColor = "#ffa500";
    private String multiPoisPointNumberColor = "#ffffff";
    private String multiPoisVisitedPointColor = "#ffa500";
    private String multiPoisVisitedPointNumberColor = "#ffffff";
    private Bitmap iconForMultiPointExit = null;
    private Bitmap iconForMultiPointSwitchFloor = null;


    /**
     * Constructor
     *
     * @param includeEntrances
     * @param includeSwitchFloors
     * @param multiPoisRouteColor
     * @param multiPoisPointColor
     * @param multiPoisPointNumberColor
     * @param multiPoisvisitedPointColor
     * @param multiPoisVisitedPointNumberColor
     * @param iconForMultiPointExit
     * @param iconForMultiPointSwitchFloor
     */
    public MultiPoiUIOptions(boolean includeEntrances, boolean includeSwitchFloors,
                             String multiPoisPointColor, String multiPoisPointNumberColor,
                             String multiPoisvisitedPointColor, String multiPoisVisitedPointNumberColor,
                             Bitmap iconForMultiPointExit, Bitmap iconForMultiPointSwitchFloor) {
        super();
        this.includeEntrances = includeEntrances;
        this.includeSwitchFloors = includeSwitchFloors;
        this.multiPoisPointColor = multiPoisPointColor;
        this.multiPoisVisitedPointColor = multiPoisvisitedPointColor;
        this.multiPoisPointNumberColor = multiPoisPointNumberColor;
        this.multiPoisVisitedPointNumberColor = multiPoisVisitedPointNumberColor;
        this.iconForMultiPointExit = iconForMultiPointExit;
        this.iconForMultiPointSwitchFloor = iconForMultiPointSwitchFloor;
    }


    /**
     * Constructor
     */
    public MultiPoiUIOptions() {
        super();
    }


    /**
     * @return true if entrances are included. otherwise, return false
     */
    public boolean isIncludeEntrances() {
        return includeEntrances;
    }

    /**
     * set whether to include entrances
     *
     * @param includeEntrances
     */
    public void setIncludeEntrances(boolean includeEntrances) {
        this.includeEntrances = includeEntrances;
    }

    /**
     * @return true if switch floors are included. otherwise, return false
     */
    public boolean isIncludeSwitchFloors() {
        return includeSwitchFloors;
    }

    /**
     * set whether to include switch floors
     *
     * @param includeSwitchFloorsInPoisNumbering
     */
    public void setIncludeSwitchFloors(boolean includeSwitchFloors) {
        this.includeSwitchFloors = includeSwitchFloors;
    }

    /**
     * @return the color of the multi pois point  (in Hex)
     */
    public String getMultiPoisPointColor() {
        return multiPoisPointColor;
    }

    /**
     * sets the color of the multi pois point  (in Hex)
     *
     * @param multiPoisPointColor
     */
    public void setMultiPoisPointColor(String multiPoisPointColor) {
        this.multiPoisPointColor = multiPoisPointColor;
    }

    /**
     * @return the color of the multi pois point number  (in Hex)
     */
    public String getMultiPoisPointNumberColor() {
        return multiPoisPointNumberColor;
    }

    /**
     * sets the color of the multi pois point number  (in Hex)
     *
     * @param multiPoisPointNumberColor
     */
    public void setMultiPoisPointNumberColor(String multiPoisPointNumberColor) {
        this.multiPoisPointNumberColor = multiPoisPointNumberColor;
    }

    /**
     * @return the color of the multi pois visited points  (in Hex)
     */
    public String getMultiPoisvisitedPointColor() {
        return multiPoisVisitedPointColor;
    }

    /**
     * sets the color of the multi pois visited points  (in Hex)
     *
     * @param multiPoisvisitedPointColor
     */
    public void setMultiPoisvisitedPointColor(String multiPoisvisitedPointColor) {
        this.multiPoisVisitedPointColor = multiPoisvisitedPointColor;
    }


    public String getMultiPoisVisitedPointNumberColor() {
        return multiPoisVisitedPointNumberColor;
    }


    public void setMultiPoisVisitedPointNumberColor(
            String multiPoisVisitedPointNumberColor) {
        this.multiPoisVisitedPointNumberColor = multiPoisVisitedPointNumberColor;
    }


    public Bitmap getIconForMultiPointExit() {
        return iconForMultiPointExit;
    }


    public void setIconForMultiPointExit(Bitmap iconForMultiPointExit) {
        this.iconForMultiPointExit = iconForMultiPointExit;
    }


    public Bitmap getIconForMultiPointSwitchFloor() {
        return iconForMultiPointSwitchFloor;
    }


    public void setIconForMultiPointSwitchFloor(
            Bitmap iconForMultiPointSwitchFloor) {
        this.iconForMultiPointSwitchFloor = iconForMultiPointSwitchFloor;
    }


}
