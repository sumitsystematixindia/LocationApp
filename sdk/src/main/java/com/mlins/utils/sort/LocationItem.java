package com.mlins.utils.sort;

import android.graphics.PointF;


import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.utils.MathUtils;
import com.mlins.utils.gis.Location;

abstract class LocationItem extends SortResultItem {

    private static final float[] OUTDOOR_WEIGHT_RESULT = new float[1];

    private final int mode;

    LocationItem(PointF point, double x, double y) {
        super(weightToIndoorLocation(point, x, y));
        mode = Location.TYPE_INTERNAL;
    }

    LocationItem(LatLng itemLocation, LatLng locationToWeight) {
        this(itemLocation.latitude, itemLocation.longitude, locationToWeight);
    }

    LocationItem(double itemLatitude, double itemLongitude, LatLng locationToWeight) {
        this(itemLatitude, itemLongitude, locationToWeight.latitude, locationToWeight.longitude);
    }

    LocationItem(
            double itemLocationLatitude,
            double itemLocationLongitude,
            double locationToWeightLatitude,
            double locationToWeightLongitude) {

        super(weightToOutdoorLocation(itemLocationLatitude, itemLocationLongitude, locationToWeightLatitude, locationToWeightLongitude));

        mode = Location.TYPE_EXTERNAL;
    }


    static double weightToIndoorLocation(PointF point, double x, double y){
        if(point == null)
            throw new NullPointerException("point == null");

        return MathUtils.getNavigationWeight(point.x, point.y, x, y);
    }

    static double weightToOutdoorLocation(
            double itemLocationLatitude,
            double itemLocationLongitude,
            double locationToWeightLatitude,
            double locationToWeightLongitude){

        android.location.Location.distanceBetween(
                itemLocationLatitude,
                itemLocationLongitude,
                locationToWeightLatitude,
                locationToWeightLongitude,
                OUTDOOR_WEIGHT_RESULT
        );

        return OUTDOOR_WEIGHT_RESULT[0];
    }

    public int compareTo(@NonNull WeightedItem o) {
        if(mode != ((LocationItem) o).mode)
            throw new IllegalStateException("Can't compare items for indoor and outdoor locations");
        return super.compareTo(o);
    }
}
