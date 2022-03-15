package com.mlins.dualmap;

import com.mlins.utils.PoiData;
import com.spreo.nav.enums.LocationMode;

public class DestinationPoi {
    private PoiData poi = null;
    private LocationMode mode = null;
    private String facilityId = null;

    public DestinationPoi() {

    }

    public DestinationPoi(PoiData poi, LocationMode mode) {
        this.poi = poi;
        this.mode = mode;
        if (mode == LocationMode.INDOOR_MODE && poi != null && poi.getFacilityID() != null) {
            facilityId = poi.getFacilityID();
        }
    }

    public PoiData getPoi() {
        return poi;
    }

    public void setPoi(PoiData poi) {
        this.poi = poi;
    }

    public LocationMode getMode() {
        return mode;
    }

    public void setMode(LocationMode mode) {
        this.mode = mode;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
}
