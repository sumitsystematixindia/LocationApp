package com.mlins.utils;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Collections;

public class FacilityObj {

    private String idName = null;
    private double centerLat = 0;  // gps latitude
    private double centerLon = 0;    // gps longitude

    private double NWLat = 0;  // NW latitude
    private double NWLon = 0;    // NW longitude
    private double NELat = 0;  // NE latitude
    private double NELon = 0;    // NE longitude
    private double SWLat = 0;  // SW latitude
    private double SWLon = 0;    // SW longitude
    private double SELat = 0;  // SE latitude
    private double SELon = 0;    // SE longitude

    private double entranceLeft;
    private double entranceRight;
    private double entranceUp;
    private double entranceDown;

    private double exitLeft;
    private double exitRight;
    private double exitUp;
    private double exitDown;

    private RectF facilityEntranceBounds;
    private RectF facilityExitBounds;
    private PointF centerPoint;

    private int floorsCount = 0;
    private float angle = 0; // angle to be displayed on google map
    private float width = 0;  // width to be displayed on google map

    private double boundsOffset;// = PropertyHolder.getInstance().getBoundsOffset();//


    public FacilityObj(String idName, double lat, double lon) {
        super();
        this.idName = idName;
        this.centerLat = lat;
        this.centerLon = lon;
        facilityEntranceBounds = new RectF();
        setFacilityExitBounds(new RectF());
        centerPoint = new PointF((float) lon, (float) lat);
    }


    public FacilityObj(String idName, double lat, double lon, int floorsCount,
                       float angle, float width, double NWlat, double NWlon, double NElat, double NElon, double SWlat, double SWlon, double SElat, double SElon, double boundsoffset) {
        super();
        this.idName = idName;
        this.centerLat = lat;
        this.centerLon = lon;
        this.floorsCount = floorsCount;
        this.angle = angle;
        this.width = width;
        this.NWLat = NWlat;
        this.NWLon = NWlon;
        this.NELat = NElat;
        this.NELon = NElon;
        this.SWLat = SWlat;
        this.SWLon = SWlon;
        this.SELat = SElat;
        this.SELon = SElon;
        this.boundsOffset = boundsoffset;
        facilityEntranceBounds = new RectF();
        setFacilityExitBounds(new RectF());
        centerPoint = new PointF((float) lon, (float) lat);
    }

    public FacilityObj(String idName, double lat, double lon, int floorsCount,
                       float angle, float width) {
        super();
        this.idName = idName;
        this.centerLat = lat;
        this.centerLon = lon;
        this.floorsCount = floorsCount;
        this.angle = angle;
        this.width = width;
        facilityEntranceBounds = new RectF();
        setFacilityExitBounds(new RectF());
        centerPoint = new PointF((float) lon, (float) lat);

    }

    public String getIdName() {
        return idName;
    }


    public void setIdName(String idName) {
        this.idName = idName;
    }


    public double getLat() {
        return centerLat;
    }


    public void setLat(double lat) {
        this.centerLat = lat;
    }


    public double getLon() {
        return centerLon;
    }


    public void setLon(double lon) {
        this.centerLon = lon;
    }


    public int getFloorsCount() {
        return floorsCount;
    }


    public void setFloorsCount(int floorsCount) {
        this.floorsCount = floorsCount;
    }


    public float getAngle() {
        return angle;
    }


    public void setAngle(float angle) {
        this.angle = angle;
    }


    public float getWidth() {
        return width;
    }


    public void setWidth(float width) {
        this.width = width;
    }


    public double getCenterLat() {
        return centerLat;
    }


    public void setCenterLat(double centerLat) {
        this.centerLat = centerLat;
    }


    public double getCenterLon() {
        return centerLon;
    }


    public void setCenterLon(double centerLon) {
        this.centerLon = centerLon;
    }


    public double getNWLat() {
        return NWLat;
    }


    public void setNWLat(double nWLat) {
        NWLat = nWLat;
    }


    public double getNWLon() {
        return NWLon;
    }


    public void setNWLon(double nWLon) {
        NWLon = nWLon;
    }


    public double getNELat() {
        return NELat;
    }


    public void setNELat(double nELat) {
        NELat = nELat;
    }


    public double getNELon() {
        return NELon;
    }


    public void setNELon(double nELon) {
        NELon = nELon;
    }


    public double getSWLat() {
        return SWLat;
    }


    public void setSWLat(double sWLat) {
        SWLat = sWLat;
    }


    public double getSWLon() {
        return SWLon;
    }


    public void setSWLon(double sWLon) {
        SWLon = sWLon;
    }


    public double getSELat() {
        return SELat;
    }


    public void setSELat(double sELat) {
        SELat = sELat;
    }


    public double getSELon() {
        return SELon;
    }


    public void setSELon(double sELon) {
        SELon = sELon;
    }


    public double getEntranceLeft() {
        return entranceLeft;
    }


    public void setEntranceLeft(double left) {
        this.entranceLeft = left;
    }


    public double getEntranceRight() {
        return entranceRight;
    }


    public void setEntranceRight(double right) {
        this.entranceRight = right;
    }


    public double getEntranceUp() {
        return entranceUp;
    }


    public void setEntranceUp(double up) {
        this.entranceUp = up;
    }


    public double getEntranceDown() {
        return entranceDown;
    }


    public void setEntranceDown(double down) {
        this.entranceDown = down;
    }


    public RectF getFacilityEntranceBounds() {
        return facilityEntranceBounds;
    }


    public void setFacilityEntranceBounds(RectF facilityEntranceBounds) {
        this.facilityEntranceBounds = facilityEntranceBounds;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idName == null) ? 0 : idName.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FacilityObj other = (FacilityObj) obj;
        if (idName == null) {
            if (other.idName != null)
                return false;
        } else if (!idName.equals(other.idName))
            return false;
        return true;
    }


    @Override
    public String toString() {
        return "FacilityObj [idName=" + idName + ", lat=" + centerLat + ", lon="
                + centerLon + ", floorsCount=" + floorsCount + ", angle=" + angle
                + ", width=" + width + "]";
    }

    public void setNewBoundsToFacility() {
        if (NWLat == 0 ||  // NW latitude
                NWLon == 0 ||    // NW longitude
                NELat == 0 ||  // NE latitude
                NELon == 0 ||    // NE longitude
                SWLat == 0 ||  // SW latitude
                SWLon == 0 ||    // SW longitude
                SELat == 0 ||  // SE latitude
                SELon == 0) {
            return;
        } else {
            ArrayList<Double> lonList = new ArrayList<Double>();
            ArrayList<Double> latList = new ArrayList<Double>();
            lonList.add(NWLon);
            lonList.add(NELon);
            lonList.add(SWLon);
            lonList.add(SELon);
            latList.add(NWLat);
            latList.add(NELat);
            latList.add(SWLat);
            latList.add(SELat);

            //calculate the minMax values with the offset values
            double minEntranceLat = Collections.min(latList) - offsetLat();
            double maxEntranceLat = Collections.max(latList) + offsetLat();
            double minEntranceLon = Collections.min(lonList) - offsetLong(minEntranceLat);
            double maxEntranceLon = Collections.max(lonList) + offsetLong(maxEntranceLat);

            setEntranceDown(minEntranceLat);
            setEntranceUp(maxEntranceLat);
            setEntranceLeft(minEntranceLon);
            setEntranceRight(maxEntranceLon);

            double minEXITLat = Collections.min(latList) - offsetLat() - (offsetLat() / 3);
            double maxEXITLat = Collections.max(latList) + offsetLat() + (offsetLat() / 3);
            double minEXITLon = Collections.min(lonList) - offsetLong(minEXITLat) - (offsetLong(minEXITLat) / 3);
            double maxEXITLon = Collections.max(lonList) + offsetLong(maxEXITLat) + (offsetLong(maxEXITLat) / 3);

            setExitDown(minEXITLat);
            setExitUp(maxEXITLat);
            setExitLeft(minEXITLon);
            setExitRight(maxEXITLon);

            facilityEntranceBounds.set((float) getEntranceLeft(), (float) getEntranceUp(), (float) getEntranceRight(), (float) getEntranceDown());
            facilityExitBounds.set((float) getExitLeft(), (float) getExitUp(), (float) getExitRight(), (float) getExitDown());
        }

    }

    public double offsetLat() {
        return boundsOffset * (1.0 / 110540.0);
    }

    public double offsetLong(double lat) {
        //return boundsOffset*((1.0/111320.0)*Math.cos(lat));
        return boundsOffset * ((1.0 / 111320.0));
    }


    public PointF getCenterPoint() {
        return centerPoint;
    }


    public void setCenterPoint(PointF centerPoint) {
        this.centerPoint = centerPoint;
    }


    public double getBoundsOffset() {
        return boundsOffset;
    }


    public void setBoundsOffset(double boundsOffset) {
        this.boundsOffset = boundsOffset;
    }


    public double getExitLeft() {
        return exitLeft;
    }


    public void setExitLeft(double exitLeft) {
        this.exitLeft = exitLeft;
    }


    public double getExitRight() {
        return exitRight;
    }


    public void setExitRight(double exitRight) {
        this.exitRight = exitRight;
    }


    public double getExitUp() {
        return exitUp;
    }


    public void setExitUp(double exitUp) {
        this.exitUp = exitUp;
    }


    public double getExitDown() {
        return exitDown;
    }


    public void setExitDown(double exitDown) {
        this.exitDown = exitDown;
    }


    public RectF getFacilityExitBounds() {
        return facilityExitBounds;
    }


    public void setFacilityExitBounds(RectF facilityExitBounds) {
        this.facilityExitBounds = facilityExitBounds;
    }

}

