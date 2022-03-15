package com.mlins.campaign;

import android.graphics.RectF;

import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.ILocation;

public class BannersLocationArea {

    private Location upLeft;
    private Location downRight;
    private double zVal;
    private RectF bannerRect;

    public BannersLocationArea(Location upLeft, Location downRight) {
        super();
        this.upLeft = upLeft;
        this.downRight = downRight;
        bannerRect = new RectF((float) upLeft.getX(), (float) upLeft.getY(), (float) downRight.getX(), (float) downRight.getY());
        setzVal(downRight.getZ());
    }

    public RectF getBannerRect() {
        return bannerRect;
    }

    public Location getUpLeft() {
        return upLeft;
    }

    public void setUpLeft(Location upLeft) {
        this.upLeft = upLeft;
    }

    public Location getDownRight() {
        return downRight;
    }

    public void setDownRight(Location downRight) {
        this.downRight = downRight;
    }

    public double getzVal() {
        return zVal;
    }

    public void setzVal(double zVal) {
        this.zVal = zVal;
    }

    public boolean isInside(ILocation loc) {
        boolean result = false;
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();


        if (bannerRect.contains((float) x, (float) y) && (z == zVal)) {

            result = true;
        }

        return result;

    }

}
