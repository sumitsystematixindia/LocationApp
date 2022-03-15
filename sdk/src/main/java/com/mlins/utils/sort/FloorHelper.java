package com.mlins.utils.sort;

import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FloorData;
import com.mlins.utils.MathUtils;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.sdk.data.SpreoDataProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FloorHelper {

    private final String campusID;
    private final String facilityID;
    private final int floorID;
    private final List<IPoi> pois;

    public FloorHelper(String campusID, String facilityID, int floorID) {
        this(campusID, facilityID, floorID, ProjectConf.getInstance().getAllFloorPoisList(campusID, facilityID, floorID));
        //TODO ask meir about marking PoisUtils.getAllFloorPoisList as depricated
    }

    // This constructor is used in test so it has package-private acceess
    FloorHelper(String campusID, String facilityID, int floorID, List<IPoi> pois) {
        this.campusID = campusID;
        this.facilityID = facilityID;
        this.floorID = floorID;
        this.pois = Collections.unmodifiableList(pois);
    }

    public int getID() {
        return floorID;
    }

    public List<IPoi> getPOIs() {
        return pois;
    }

    public String getTitle(){
        return SpreoDataProvider.getFloorTitle(campusID, facilityID, floorID);
    }

    static List<FloorHelper> getFloorsFor(String campusID, String facilityID, List<IPoi> list){
        List<FloorHelper> floors = new ArrayList<>();
        Map<Integer, List<IPoi>> floorsMap = splitByFloors(list);
        for (Integer floorID : floorsMap.keySet()) {
            floors.add(new FloorHelper(campusID, facilityID, floorID, floorsMap.get(floorID)));
        }
        return floors;
    }

    static Map<Integer, List<IPoi>> splitByFloors(List<IPoi> all){
        Map<Integer, List<IPoi>> result = new HashMap<>();
        for (IPoi poi : all) {
            int floorID = (int) poi.getZ();

            List<IPoi> curFloorList = result.get(floorID);
            if(curFloorList == null) {
                curFloorList = new ArrayList<>();
                result.put(floorID, curFloorList);
            }
            curFloorList.add(poi);
        }
        return result;
    }

    public static double getDistance(ILocation a, ILocation b) {
        if(!Location.inTheSameFacility(a, b) || !Location.onTheSameFloor(a, b))
            throw new IllegalArgumentException("Locations has to be in the same facility and on the same floor");

        FacilityConf facility = ProjectConf.getInstance().getFacilityConf(a);
        FloorData floorData = facility.getFloorDataList().get((int) a.getZ());

        return MathUtils.distance(Location.getPoint(a), Location.getPoint(b)) / floorData.getPixelsToMeter();
    }

    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj))
            return true;

        if(!(obj instanceof FloorHelper))
            return false;

        return getID() == ((FloorHelper) obj).getID();
    }
}
