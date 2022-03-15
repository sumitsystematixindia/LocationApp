package com.mlins.dualmap;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Collection;
import java.util.List;

class MarkersHelper {

    static void setVisibility(Collection<Marker> markers, boolean visible) {
        for (Marker marker : markers) {
            marker.setVisible(visible);
        }
    }

    static void remove(List<Marker> markers) {
        for (Marker marker : markers) {
            marker.remove();
        }
    }

    static void applyConfiguration(Marker marker, MarkerOptions options) {
        marker.setIcon(options.getIcon());
        marker.setAnchor(options.getAnchorU(), options.getAnchorV());
    }

}
