package com.mlins.nav.utils;

import android.graphics.PointF;
import android.graphics.RectF;

import com.mlins.utils.logging.Log;

public class ExternalPoisParamsHolder {

    private final static String TAG = "ccom.mlins.nav.utils.ExternalPoisParamsHolder";
    /**
     * single tone support
     */
    private static ExternalPoisParamsHolder instance = null;
    private String waysLat;
    private String waysLon;
    private double currentLat;
    private double currentLon;
    private boolean externalPoiServiceIsOn = false;
    private double boundsDistInMeters = 1000.0;

    //no usages for now, will need to rework this class
    public static ExternalPoisParamsHolder getInstance() {
        if (instance == null) {
            instance = new ExternalPoisParamsHolder();
        }
        return instance;
    }

    public static void releaseInstance() {
        if (instance != null) {
            instance = null;
        }
    }

    public String getWaysLat() {
        return waysLat;
    }

    public void setWaysLat(String waysLat) {
        this.waysLat = waysLat;
    }

    public String getWaysLon() {
        return waysLon;
    }

    public void setWaysLon(String waysLon) {
        this.waysLon = waysLon;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLon() {
        return currentLon;
    }

    public void setCurrentLon(double currentLon) {
        this.currentLon = currentLon;
    }

    public boolean isMyLocationCloseToWAYSDestination(double myLat, double myLon) {
        Log.getInstance().debug(TAG, "Enter, isMyLocationCloseToWAYSDestination()");
        boolean result = false;
        RectF areaAroundWaysDest = new RectF();
        PointF waysDest = waysDestination();
        float left = (float) (waysDest.x - offsetLong());
        float top = (float) (waysDest.y + offsetLat());
        float right = (float) (waysDest.x + offsetLong());
        float bottom = (float) (waysDest.y - offsetLat());

        areaAroundWaysDest.set(left, top, right, bottom);
        if (myLon >= left &&
                myLon <= right &&
                myLat >= bottom &&
                myLat <= top) {
            result = true;
        }


        Log.getInstance().debug(TAG, "Exit, isMyLocationCloseToWAYSDestination()");
        return result;
    }

    private PointF waysDestination() {
        //X is long,Y is lat
        float destX = Float.parseFloat(getWaysLon());
        float destY = Float.parseFloat(getWaysLat());
        return new PointF(destX, destY);
    }

    public float offsetLat() {
        return (float) (boundsDistInMeters * (1.0 / 110540.0));
    }

    public float offsetLong() {
        return (float) (boundsDistInMeters * ((1.0 / 111320.0)));
    }

    public boolean isExternalPoiServiceIsOn() {
        return externalPoiServiceIsOn;
    }

    public void setExternalPoiServiceIsOn(boolean externalPoiServiceIsOn) {
        this.externalPoiServiceIsOn = externalPoiServiceIsOn;
    }
}
