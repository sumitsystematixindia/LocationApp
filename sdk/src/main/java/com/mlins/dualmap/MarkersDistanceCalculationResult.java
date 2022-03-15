package com.mlins.dualmap;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mlins.utils.MathUtils;

import java.util.Arrays;
import java.util.List;

class MarkersDistanceCalculationResult {

    private static final String TAG = MarkersDistanceCalculationResult.class.getName();

    private final Marker[] sortedMarkers;
    private final double[] sortedDistances;

    MarkersDistanceCalculationResult(Marker[] markers, LatLng[] markersPositions){
        int count = markers.length;

        if(count == 0)
            throw new IllegalArgumentException("Empty markers list is not supported");

        Log.d(TAG, "Recalculating, total markers: " + count);

        double[][] distances = new double[count-1][];
        for (int rowIndex = 0; rowIndex < count-1; rowIndex++){
            distances[rowIndex] = new double[rowIndex+1];
        }

//        String markersLine = "";
//
//        Log.d(TAG, "Makers");
//        for (Marker marker : markers) {
//            markersLine += marker.getTitle() + "\t\t";
//        }
//        Log.d(TAG, markersLine);

        for (int firstMarkerIndex = 1; firstMarkerIndex < count; firstMarkerIndex++) {
            LatLng firstMarkerPosition = markersPositions[firstMarkerIndex];

            //String distanceLine = "";

            for (int secondMarkerIndex = 0; secondMarkerIndex < firstMarkerIndex; secondMarkerIndex++) {
                double distance = MathUtils.distance(firstMarkerPosition, markersPositions[secondMarkerIndex]);

                //distanceLine += String.format("%1$.2f\t\t\t\t", distance);

                distances[firstMarkerIndex-1][secondMarkerIndex] = distance;
            }

            //Log.d(TAG, distanceLine);
        }

        Log.d(TAG, "Distance calculation finished");

        sortedMarkers = new Marker[count];
        sortedDistances = new double[count];

        for (int sortedMarkerIndex = 0; sortedMarkerIndex < count-1; sortedMarkerIndex++) {
            double minDistanceToOther = Double.MAX_VALUE;
            int closestMarkerIndex = -1;

            for (int i = 0; i < count-1; i++) {
                double[] distancesToOther = distances[i];
                if(distancesToOther != null) {
                    for (int k = 0; k <= i; k++) {
                        double curDistanceToOther = distancesToOther[k];
                        if (minDistanceToOther > curDistanceToOther
                                && (k == 0 || distances[k-1] != null))
                        {
                            minDistanceToOther = curDistanceToOther;
                            closestMarkerIndex = i + 1;
                        }
                    }
                }
            }
            distances[closestMarkerIndex-1] = null;
            sortedMarkers[sortedMarkerIndex] = markers[closestMarkerIndex];
            sortedDistances[sortedMarkerIndex] = minDistanceToOther;
        }

        sortedMarkers[count-1] = markers[0];
        sortedDistances[count-1] = Double.MAX_VALUE;

        Log.d(TAG, "Recalculation finished");
    }

    void splitMarkersByDistance(double distanceThreshold, List<Marker> nearByMarkers, List<Marker> farAwayMarkers){
        int splitVisibilityIndex = Arrays.binarySearch(sortedDistances, distanceThreshold);
        if (splitVisibilityIndex < 0) {
            splitVisibilityIndex = -splitVisibilityIndex - 1;
        }

        Marker[] farAwayMarkersArray = new Marker[sortedMarkers.length - splitVisibilityIndex];
        System.arraycopy(sortedMarkers, splitVisibilityIndex, farAwayMarkersArray, 0, farAwayMarkersArray.length);

        farAwayMarkers.addAll(Arrays.asList(farAwayMarkersArray));

        if(splitVisibilityIndex > 0) {
            Marker[] nearByMarkersArray = new Marker[splitVisibilityIndex-1];
            System.arraycopy(sortedMarkers, 0, nearByMarkersArray, 0, nearByMarkersArray.length);
            nearByMarkers.addAll(Arrays.asList(nearByMarkersArray));
        }

    }

}
