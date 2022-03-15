package com.mlins.utils.sort;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.dualmap.DualMapNavUtil;
import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.PoiData;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.sdk.data.SpreoDataProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacilityHelper {

    private final FacilityConf facilityConf;
    private final LatLng center;

    private final List<FloorHelper> floors;

    public FacilityHelper(String campusID, String facilityID){
        this(getFacilityFloors(campusID, facilityID), ProjectConf.getInstance().getFacilityConfById(campusID, facilityID));
    }

    public FacilityHelper(ILocation location){
        this(getFacilityFloors(location), ProjectConf.getInstance().getFacilityConf(location));
    }

    //package access because it's used in tests
    FacilityHelper(List<FloorHelper> floors, FacilityConf conf){
        this.facilityConf = conf;
        this.floors = Collections.unmodifiableList(floors);
        this.center = new LatLng(facilityConf.getCenterLatitude(), facilityConf.getCenterLongtitude());
    }

    private FacilityHelper(String campusID, String facilityID, List<IPoi> pois){
        this(FloorHelper.getFloorsFor(campusID, facilityID, pois), ProjectConf.getInstance().getFacilityConfById(campusID, facilityID));
    }

    public static List<FloorHelper> getFacilityFloors(ILocation location){
        Location.ensureInDoor(location);
        return getFacilityFloors(location.getCampusId(), location.getFacilityId());
    }

    public static List<FloorHelper> getFacilityFloors(String campusID, String facilityID) {
        List<Integer> floorIds = SpreoDataProvider.getFacilityFloorIDs(campusID, facilityID);

        List<FloorHelper> floors = new ArrayList<>();
        for (Integer floorId : floorIds)
            floors.add(new FloorHelper(campusID, facilityID, floorId));
        return floors;
    }

    public String getID() {
        return facilityConf.getId();
    }


    public List<FloorHelper> getFloors() {
        return floors;
    }


    public LatLng getCenter() {
        return center;
    }

    /**
     *
     * @return All exists for the facility, no matter which pois where passed or not passed to constructor
     */
    public List<IPoi> getExits(){
        return PoiData.getExits(ProjectConf.getInstance().getAllFacilityPoisList(facilityConf.getCampusID(), facilityConf.getId()));
    }


    public IPoi getExitClosestTo(LatLng latLng){
        return DualMapNavUtil.findCloseExit(latLng, getExits(), false);
    }


    public boolean isInside(ILocation location) {
        return getID().equals(location.getFacilityId());
    }

    @Override
    public String toString() {
        return getID()+ '@' + super.toString();
    }

    public static PoisSplitResult splitByFacilities(String campusID, List<IPoi> pois){
        List<IPoi> external = new ArrayList<>();

        Map<String, List<IPoi>> facilitiesMap = new HashMap<>();
        for (IPoi poi : pois) {
            if(PoiData.isExternal(poi)){
                external.add(poi);
            } else {
                String facilityID = poi.getFacilityID();

                List<IPoi> list = facilitiesMap.get(facilityID);
                if(list == null) {
                    list = new ArrayList<>();
                    facilitiesMap.put(facilityID, list);
                }

                list.add(poi);
            }
        }
        Map<String, FacilityHelper> resultMap = new HashMap<>();
        for (String facilityID : facilitiesMap.keySet()) {
            resultMap.put(facilityID, new FacilityHelper(campusID, facilityID, facilitiesMap.get(facilityID)));
        }

        return new PoisSplitResult(resultMap, external);
    }

    public static class PoisSplitResult {

        public final Map<String, FacilityHelper> facilities;
        public final List<IPoi> outdoorPois;

        public PoisSplitResult(Map<String, FacilityHelper> facilities, List<IPoi> outdoorPois) {
            this.facilities = facilities;
            this.outdoorPois = outdoorPois;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj))
            return true;

        if(!(obj instanceof FacilityHelper))
            return false;

        return getID().equals(((FacilityHelper) obj).getID());
    }

}
