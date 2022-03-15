package com.spreo.geofencing;

import com.spreo.geofence.GeofenceContent;
import com.spreo.geofence.GeofenceContentManager;

import java.util.ArrayList;
import java.util.List;

import geofence.GeoFenceHelper;
import geofence.ZoneDetection;

public class GeoFencingUtils {

    /**
     * Subscribe for GeoFencing service
     *
     * @param zoneDetectionListener   - the listener of the geoFencing service. e.g. an activity or a view that implements
     *                                the interface ZoneDetectionListener @see geofence.ZoneDetection
     * @param geoFenceingListenToList - the list of zones that the zoneDetectionListener will notified on entrance or exit the zones
     * @return true if succeeded to subscribe
     */
    public static boolean subscribeToService(ZoneDetection zoneDetectionListener, List<String> geoFenceingListenToList) {
        List<String> geoListenTo = new ArrayList<String>();
        for (String g : geoFenceingListenToList) {
            geoListenTo.add(g);
        }
        return GeoFenceHelper.getInstance().subscribeForService(zoneDetectionListener, geoListenTo);
    }

    /**
     * unSubscribe from GeoFencing service
     *
     * @param zoneDetectionListener - the listener of the geoFencing service. e.g. an activity or a view that implements the interface ZoneDetectionListener @see geofence.ZoneDetection
     * @return true if succeeded to unSubscribe
     */
    public static boolean unSubscribeFromService(ZoneDetection zoneDetectionListener) {
        return GeoFenceHelper.getInstance().unsubscibeFromService(zoneDetectionListener);
    }


    /**
     * get geofence related content list
     *
     * @param geofenceId
     * @return list of geofence content
     */
    public static List<GeofenceContent> getGeofenceContent(String geofenceId) {
        return GeofenceContentManager.getInstance().getContentByTriggerId(geofenceId);
    }


}
