package com.mlins.dualmap;

import com.mlins.project.bridges.Bridge;
import com.spreo.nav.interfaces.IPoi;

public class PossibleExit {
    private IPoi poi = null;
    private double distance = Double.MAX_VALUE;
    private String tofacility = null;
    private int fromFloor = -1;
    private int toFloor = -1;
    private Bridge bridge = null;

    public PossibleExit(IPoi poi) {
        this.poi = poi;
        this.fromFloor = (int)poi.getZ();
    }

    public PossibleExit(Bridge bridge, String failityid) {
        this.bridge = bridge;
        if (bridge.getPoint1().getFacilityID().equals(failityid)) {
            this.poi = bridge.getPoint1();
            this.fromFloor = (int)bridge.getPoint1().getZ();
            this.toFloor = (int)bridge.getPoint2().getZ();
            this.tofacility = bridge.getPoint2().getFacilityID();
        } else if (bridge.getPoint2().getFacilityID().equals(failityid)) {
            this.poi = bridge.getPoint2();
            this.toFloor = (int)bridge.getPoint1().getZ();
            this.fromFloor = (int)bridge.getPoint2().getZ();
            this.tofacility = bridge.getPoint1().getFacilityID();
        }
    }


    public IPoi getPoi() {
        return poi;
    }

    public void setPoi(IPoi poi) {
        this.poi = poi;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getToFacility() {
        return tofacility;
    }

    public void setToFacility(String facility) {
        this.tofacility = facility;
    }

    public int getFromFloor() {
        return fromFloor;
    }

    public void setFromFloor(int floor) {
        this.fromFloor = floor;
    }

    public int getToFloor() {
        return toFloor;
    }

    public void setToFloor(int floor) {
        this.toFloor = floor;
    }

    public Bridge getBridge() {
        return bridge;
    }

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }
}
