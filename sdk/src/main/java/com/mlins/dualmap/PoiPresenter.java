package com.mlins.dualmap;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mlins.nav.utils.CustomInfoWindowAdapter;
import com.mlins.utils.PropertyHolder;
import com.spreo.nav.interfaces.IPoi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


class PoiPresenter {

    private final GoogleMap map;

    private final Map<IPoi, Marker> poiMarkerMap = new LinkedHashMap<>();
    private final Map<Marker, IPoi> markerPoiMap = new LinkedHashMap<>();

    private boolean needInvalidate = false;

    private final ZoomBasedRenderStrategy zoomBasedRenderStrategy;

    private IPoi pinnedPOI;

    PoiPresenter(Context context, GoogleMap map) {
        this.map = map;
        zoomBasedRenderStrategy = PropertyHolder.getInstance().displayLabelsForPOIs() ? new IconsWithLabelsRenderStrategy(context) : new IconsRenderStrategy(context);
    }

    /**
     * Visible pois only, in case when you need to change pois visibility use redrawPOIs method in dual map view
     * @param pois
     */
    void addPOIs(List<IPoi> pois) {
        for (IPoi poi : pois) {
            MarkerOptions configuration = zoomBasedRenderStrategy.createMarkerConfiguration(poi);
            configuration.position(new LatLng(poi.getPoiLatitude(), poi.getPoiLongitude()));
            configuration.title(poi.getpoiDescription());
            configuration.visible(false);

            Marker marker = map.addMarker(configuration);
            poiMarkerMap.put(poi, marker);
            markerPoiMap.put(marker, poi);
        }
        needInvalidate = true;
    }

    void removePOIs(List<IPoi> pois){
        for (int i = 0; i < pois.size(); i++) {
            IPoi poi = pois.get(i);
            Marker marker = poiMarkerMap.remove(poi);
            if(marker != null) { //someone can ask us about removing pois which haven't been added before
                markerPoiMap.remove(marker);
                marker.remove();
            }
        }
        needInvalidate = true;
    }

    void removeAllPOIs() {
        zoomBasedRenderStrategy.clear();
        for (Marker marker : markerPoiMap.keySet()) {
            marker.remove();
        }
        markerPoiMap.clear();
        poiMarkerMap.clear();
        needInvalidate = true;
    }

    void onZoomChange(float zoom, float maxZoom){
        if(needInvalidate)
            throw new IllegalStateException("invalidate() call is missed after POIs change");

        zoomBasedRenderStrategy.render(markerPoiMap, zoom, maxZoom);
        ensurePinnedPOIVisible();
    }

    void showInfoWindow(IPoi poi, View customView) {
        Marker marker = getMarker(poi);
        if(checkMarker(marker, poi, "show info window")) {
            if(customView != null)
                map.setInfoWindowAdapter(new CustomInfoWindowAdapter(customView, marker));
            marker.showInfoWindow();
        }
    }

    void hideInfoWindow(IPoi poi){
        Marker marker = getMarker(poi);
        if(checkMarker(marker, poi, "hide info window"))
            marker.hideInfoWindow();
    }

    boolean presentsPOI(IPoi poi){
        return poiMarkerMap.containsKey(poi);
    }

    IPoi getPoiFor(Marker marker) {
        return markerPoiMap.get(marker);
    }

    void pinPOI(IPoi poi) {
        pinnedPOI = poi;
        ensurePinnedPOIVisible();
    }

    /**
     * The main goal of this methods is to update poi marker after poi icon change
     * @param poi
     */
    void updatePoi(IPoi poi) {
        Marker marker = getMarker(poi);
        if(checkMarker(marker, poi, "update poi representation")) {
            MarkersHelper.applyConfiguration(marker, zoomBasedRenderStrategy.createMarkerConfiguration(poi));
        }
    }

    private void ensurePinnedPOIVisible(){
        if(pinnedPOI != null) {
            Marker marker = getMarker(pinnedPOI);
            if(checkMarker(marker, pinnedPOI, "pin marker")) {
                marker.setVisible(true);
            }
        }
    }

    private Marker getMarker(IPoi poi) {
        return poiMarkerMap.get(poi);
    }

    private boolean checkMarker(Marker marker, IPoi poi, String action) {
        if(marker == null)
            Log.e(PoiPresenter.class.getName(), "Can't " + action + " for poi which wasn't presented: " + poi);
        return marker != null;
    }

    void invalidate(){
        zoomBasedRenderStrategy.invalidate(markerPoiMap);
        needInvalidate = false;
    }
}
