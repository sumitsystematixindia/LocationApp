package com.mlins.ndk.wrappers;

public class NdkLocation {

    public double x = 0f;
    public double y = 0f;
    public double z = 0f;
    public double lat = -1;
    public double lon = -1;


    public NdkLocation(double x, double y) {
        super();
        this.x = x;
        this.y = y;

    }

    public NdkLocation(double x, double y, double z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public NdkLocation(double x, double y, double z, double lat, double lon) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
        this.lat = lat;
        this.lon = lon;
    }

    public NdkLocation() {

    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "NdkLocation [x=" + x + ", y=" + y + ", z=" + z + ", lat=" + lat
                + ", lon=" + lon + "]";
    }


}
