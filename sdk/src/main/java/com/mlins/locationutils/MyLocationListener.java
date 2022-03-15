package com.mlins.locationutils;

import com.spreo.nav.interfaces.ILocation;


public interface MyLocationListener {
    void onLocationDelivered(ILocation location);

    /**
     * An event is triggered to listener about campus region entrance
     *
     * @param campusId
     */
    void onCampusRegionEntrance(String campusId);

    /**
     * An event is triggered to listener about facility region entrance
     *
     * @param campusId
     * @param facilityId
     */
    void onFacilityRegionEntrance(String campusId, String facilityId);
}
