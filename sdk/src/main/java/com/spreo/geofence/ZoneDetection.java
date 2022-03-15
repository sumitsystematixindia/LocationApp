package com.spreo.geofence;

import java.util.List;

public interface ZoneDetection {
    void onZoneEnter(GeoFenceObject zone);

    void onZoneExit(GeoFenceObject zone);

    List<String> getListeningTo();

    void setListeningTo(List<String> to);
}
