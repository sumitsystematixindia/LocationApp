package com.mlins.utils.distance;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.project.ProjectConf;
import com.mlins.project.bridges.BridgeData;
import com.mlins.utils.PoiData;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ProjectConfMock extends ProjectConf {

    private final Map<String, List<IPoi>> facilities = new HashMap<>();
    private final BridgeData bridges = new BridgeData() {
        @Override
        protected void loadBridges() {}
    };

    public static void setup(ProjectConf mock){
        PowerMockito.mockStatic(ProjectConf.class);

        PowerMockito.when(ProjectConf.getInstance()).thenReturn(mock);
    }

    @Override
    public BridgeData getBridges() {
        return bridges;
    }

    @Override
    public List<IPoi> getAllFacilityPoisList(String campusId, String facilityId) {
        return facilities.get(facilityId);
    }

    public void addExit(String campusID, String facilityID, ILocation location, LatLng latLng){
        List<IPoi> facilityPois = facilities.get(facilityID);
        if(facilityPois == null) {
            facilityPois = new ArrayList<>();
            facilities.put(facilityID, facilityPois);
        }

        PoiData exit = new PoiData(Location.getPoint(location));
        exit.setFacilityID(facilityID);
        exit.setCampusID(campusID);
        exit.setPoiID("idr" + System.identityHashCode(exit));
        exit.setZ(location.getZ());
        exit.setPoiLatitude(latLng.latitude);
        exit.setPoiLongitude(latLng.longitude);

        facilityPois.add(exit);
    }
}
