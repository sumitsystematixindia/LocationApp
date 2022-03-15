package com.mlins.ndk.wrappers;

public class FLocation {

    public float x = 0f;
    public float y = 0f;
    public float z = 0f;
    public double lat = -1;
    public double lon = -1;
    public String poiId; // related poi id if exists
    public String facilityId;  // related facility id
    public int type = -1; // 0 means indoor 1 means outdoor

    public FLocation(float x, float y, float z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public FLocation(float x, float y) {
        super();
        this.x = x;
        this.y = y;
    }

    public FLocation() {
        super();

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }


    public float getZ() {
        return z;
    }

    public void setZ(float z) {
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

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    @Override
    public String toString() {
        return "FLocation [x=" + x + ", y=" + y + ", z=" + z + ", lat=" + lat
                + ", lon=" + lon + ", poiId=" + poiId + ", facilityId="
                + facilityId + "]";
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public boolean isValid() {
        return x != 0 && y != 0 && lat != -1 && lon != -1;
    }


}
