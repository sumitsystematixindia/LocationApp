package com.mlins.dualmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.TypedValue;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mlins.utils.PoiData;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class IconsWithLabelsRenderStrategy implements ZoomBasedRenderStrategy {

    private static final int LABEL_WIDTH_DP = 80;

    private final Context context;
    private final int labelWidthPX;
    private final IconsRenderStrategy iconsRenderStrategy;

    private float currentZoom;

    private MarkersDistanceCalculationResult markersProximityCalculation;

    private boolean maxZoomReached = false;
    private List<Marker> labelsHidedForMarkers;

    private AsyncTask<Void, Void, MarkersDistanceCalculationResult> invalidateTask;

    IconsWithLabelsRenderStrategy(Context context) {
        this.context = context;
        iconsRenderStrategy = new IconsRenderStrategy(context);
        labelWidthPX = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LABEL_WIDTH_DP, context.getResources().getDisplayMetrics()));
    }

    @Override
    public MarkerOptions createMarkerConfiguration(IPoi poi) {
        MarkerOptions options = new MarkerOptions();
        Bitmap icon = PoiData.getIcon(context, poi);
        if(poi.shouldDisplayLabel()) {
            Bitmap iconWithLabel = IconWithLabelGenerator.generate(icon, poi.getpoiDescription(), labelWidthPX);
            options.anchor(0.5f, icon.getHeight()/2.0f/iconWithLabel.getHeight());
            icon.recycle();
            icon = iconWithLabel;
        } else {
            options.anchor(0.5f, 0.5f);
        }

        options.icon(BitmapDescriptorFactory.fromBitmap(icon));
        return options;
    }

    @Override
    public void render(Map<Marker, IPoi> markersPoiMap, float zoom, float maxZoom) {
        maxZoomReached = zoom >= maxZoom;
        currentZoom = zoom;
        updateMarkersVisibility(markersPoiMap);
    }


    private void updateMarkersVisibility(Map<Marker, IPoi> markersPoiMap){
        if(markersProximityCalculation != null) { // has to display markers
            double minDistanceBetweenMarkersMeters = LABEL_WIDTH_DP * 156543.03392 * (Math.sqrt(2)/2) / Math.pow(2, currentZoom);
            List<Marker> markersToShow = new ArrayList<>();
            List<Marker> markersToHide = new ArrayList<>();
            markersProximityCalculation.splitMarkersByDistance(minDistanceBetweenMarkersMeters, markersToHide, markersToShow);

            if(maxZoomReached) {
                hideLabelsFor(markersToHide, markersPoiMap);
                MarkersHelper.setVisibility(markersToShow, true);
                MarkersHelper.setVisibility(markersToHide, true);
            } else {
                restoreHidedLabels(markersPoiMap);
                MarkersHelper.setVisibility(markersToShow, true);
                MarkersHelper.setVisibility(markersToHide, false);
            }
        }
    }

    private void restoreHidedLabels(Map<Marker, IPoi> markersPoiMap) {
        if(labelsHidedForMarkers != null) {
            for (Marker marker : labelsHidedForMarkers) {
                if(markersPoiMap.containsKey(marker)) //we do this check because we also call this method from invalidate method and at thant moment some hided markers might be already removed from the map
                    MarkersHelper.applyConfiguration(marker, createMarkerConfiguration(markersPoiMap.get(marker)));
            }
            labelsHidedForMarkers = null;
        }
    }

    private void hideLabelsFor(List<Marker> markersToHideLabels, Map<Marker, IPoi> markersPoiMap) {
        if(labelsHidedForMarkers == null) {
            for (Marker marker : markersToHideLabels) {
                MarkersHelper.applyConfiguration(marker, iconsRenderStrategy.createMarkerConfiguration(markersPoiMap.get(marker)));
            }
            labelsHidedForMarkers = markersToHideLabels;
        }
    }


    @Override
    public void invalidate(Map<Marker, IPoi> markersPoiMap) {

        stopInvalidateTask();

        if(markersPoiMap.size() != 0) {

            final Map<Marker, IPoi> localMap = new HashMap<>(markersPoiMap);
            final Marker[] markers = localMap.keySet().toArray(new Marker[localMap.size()]);
            final LatLng[] positions = new LatLng[markers.length];
            for (int i = 0; i < markers.length; i++) {
                positions[i] = markers[i].getPosition();
            }

            invalidateTask = new AsyncTask<Void, Void, MarkersDistanceCalculationResult>() {

                @Override
                protected MarkersDistanceCalculationResult doInBackground(Void... voids) {
                    return new MarkersDistanceCalculationResult(markers, positions);
                }

                @Override
                protected void onCancelled(MarkersDistanceCalculationResult calculationResult) {
                    markersProximityCalculation = null;
                    cleanUpMemory();
                }

                @Override
                protected void onPostExecute(MarkersDistanceCalculationResult calculationResult) {
                    markersProximityCalculation = calculationResult;
                    restoreHidedLabels(localMap); //may be we don't have to hide labels for some of the markers after markers set change, so restoring all labels and hiding them again in updateMarkersVisibility method
                    updateMarkersVisibility(localMap);
                    cleanUpMemory();
                }

                private void cleanUpMemory() {
                    invalidateTask = null;
                }

            };

            invalidateTask.execute();
        } else markersProximityCalculation = null;
    }

    private void stopInvalidateTask() {
        if(invalidateTask != null) {
            invalidateTask.cancel(true);
            invalidateTask = null;
        }
    }

    @Override
    public void clear() {
        stopInvalidateTask();
        markersProximityCalculation = null;
        labelsHidedForMarkers = null;
    }

}
