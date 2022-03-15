package com.mlins.nav.utils;

import android.view.View;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements InfoWindowAdapter {

    private View view;
    private Marker marker;

    public CustomInfoWindowAdapter(View v, Marker mark) {
        view = v;
        marker = mark;
    }

    @Override
    public View getInfoContents(Marker marker) {

        return null;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        if (marker.equals(this.marker)) {
            return view;
        }
        return null;
    }

}
