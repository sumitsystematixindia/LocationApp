package com.mlins.project.bridges;

import com.mlins.utils.PoiData;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.List;

public class Bridge {

    private final String id;
    private final PoiData point1Poi;
    private final PoiData point2Poi;

    public Bridge(String id, PoiData point1Poi, PoiData point2Poi) {
        this.id = id;
        this.point1Poi = point1Poi;
        this.point2Poi = point2Poi;
    }

    public boolean contains(String poiID){
        return point1Poi.getPoiID().equals(poiID) || point2Poi.getPoiID().equals(poiID);
    }

    public PoiData getOppositePoint(IPoi entranceOrExit) {
        String oppositeID = entranceOrExit.getPoiID();
        if(point1Poi.getPoiID().equals(oppositeID)) {
            return point2Poi;
        } else if(point2Poi.getPoiID().equals(oppositeID)) {
            return point1Poi;
        } else
            throw new IllegalArgumentException("Specified poi is not a bridge side.");
    }

    public boolean isBetween(String facility1ID, String facility2ID){
        String firstSideFacilityID = point1Poi.getFacilityID();
        String secondSideFacilityID = point2Poi.getFacilityID();

        return facility1ID.equals(firstSideFacilityID) && facility2ID.equals(secondSideFacilityID) ||
                facility1ID.equals(secondSideFacilityID) && facility2ID.equals(firstSideFacilityID);
    }

    static Bridge getBridge(List<Bridge> bridges, IPoi poi){
        String poiID = poi.getPoiID();
        for (Bridge bridge : bridges) {
            if(bridge.contains(poiID))
                return bridge;
        }
        return null;
    }

    static List<? extends IPoi> removeBridgeSides(List<Bridge> bridges, List<? extends IPoi> exitpois){
        List<IPoi> result = new ArrayList<>(exitpois.size());

        for (IPoi exit : exitpois) {
            Bridge bridge = getBridge(bridges, exit);
            if(bridge == null)
                result.add(exit);
        }

        return result;
    }

    public boolean isConnectToFacility(String fac) {
        boolean result = false;
        if (fac.equals(point1Poi.getFacilityID()) || fac.equals(point2Poi.getFacilityID())) {
            result = true;
        }
        return result;
    }

    public PoiData getPoint1() {
        return point1Poi;
    }

    public PoiData getPoint2() {
        return point2Poi;
    }

    public boolean isInFloor(String fac, double z) {
        boolean result = false;
        if (fac.equals(point1Poi.getFacilityID()) && z == point1Poi.getZ()) {
            result = true;
        } else if (fac.equals(point2Poi.getFacilityID()) && z == point2Poi.getZ()) {
            result = true;
        }
        return result;
    }

    public String getID() {
        return id;
    }
}
