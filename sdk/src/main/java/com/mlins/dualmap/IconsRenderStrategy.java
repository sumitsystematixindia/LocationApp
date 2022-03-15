package com.mlins.dualmap;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mlins.utils.PoiData;
import com.mlins.utils.PropertyHolder;
import com.spreo.nav.interfaces.IPoi;

import java.util.Map;

class IconsRenderStrategy implements ZoomBasedRenderStrategy {

    private final float hidingPoiZoom = PropertyHolder.getInstance().getHidingPoisZoomLevel();

    private final Context context;

    private float currentZoom;

    IconsRenderStrategy(Context context) {
        this.context = context;
    }

    @Override
    public MarkerOptions createMarkerConfiguration(IPoi poi) {
        MarkerOptions configuration = new MarkerOptions();
        Bitmap icon = PoiData.getIcon(context, poi);
        configuration.icon(BitmapDescriptorFactory.fromBitmap(icon));
        configuration.anchor(0.5f, 0.5f);
        return configuration;
    }

    @Override
    public void render(Map<Marker, IPoi> markersPoiMap, float zoom, float maxZoom) {
        if(zoom != currentZoom) {
            boolean needUpdate = Math.signum(zoom - hidingPoiZoom) != Math.signum(currentZoom - hidingPoiZoom);
            currentZoom = zoom;
            if(needUpdate) {
                updateMarkersVisibility(markersPoiMap);
            }
        }
    }

    private void updateMarkersVisibility(Map<Marker, IPoi> markerIPoiMap) {
        boolean makeVisible = currentZoom > hidingPoiZoom;

        for (Marker marker : markerIPoiMap.keySet()) {
            IPoi poi = markerIPoiMap.get(marker);
            if (!poi.isShowOnZoomLevel())
                marker.setVisible(makeVisible);
        }
    }

    @Override
    public void invalidate(Map<Marker, IPoi> markersPoiMap) {
        MarkersHelper.setVisibility(markersPoiMap.keySet(), true);
        updateMarkersVisibility(markersPoiMap);
    }

    @Override
    public void clear() {}
}
