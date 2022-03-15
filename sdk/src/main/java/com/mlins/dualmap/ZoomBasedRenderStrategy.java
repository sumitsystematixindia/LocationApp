package com.mlins.dualmap;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.spreo.nav.interfaces.IPoi;

import java.util.Map;

interface ZoomBasedRenderStrategy {

    MarkerOptions createMarkerConfiguration(IPoi poi);
    void render(Map<Marker, IPoi> markersPoiMap, float zoom, float maxZoom);
    void invalidate(Map<Marker, IPoi> markersPoiMap);
    void clear();

}
