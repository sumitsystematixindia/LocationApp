package com.mlins.custom.markers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mlins.nav.utils.CustomInfoWindowAdapter;
import com.spreo.interfaces.ICustomMarker;
import com.spreo.spreosdk.R;

public class CustomMarkerObject {

    //above start/stop navigation markers with Z == 6 but below user location icon with Z  == 10
    private static final int MAP_Z_LEVEL = 9;

    private GoogleMap mGooglemap;
    private Context context;
    private Marker marker;
    private ICustomMarker customMarker;

    public CustomMarkerObject(ICustomMarker custommarker, Context ctx,
                              GoogleMap googlemap) {
        context = ctx;
        mGooglemap = googlemap;
        if (custommarker != null) {
            customMarker = custommarker;
            LatLng loc = customMarker.getLatLng();
            Bitmap bm = customMarker.getIcon();
            if (bm == null) {
                bm = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.defualtpoiicon);
            }
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bm);
            marker = mGooglemap.addMarker(new MarkerOptions().position(loc)
                    .title(customMarker.getId()).anchor(0.5f, 0.5f).icon(icon).zIndex(MAP_Z_LEVEL));
        }
    }

    public CustomMarkerObject(ICustomMarker custommarker, Context ctx,
                              GoogleMap googlemap, LatLng latlng) {
        context = ctx;
        mGooglemap = googlemap;
        if (custommarker != null) {
            customMarker = custommarker;
            Bitmap bm = customMarker.getIcon();
            if (bm == null) {
                bm = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.defualtpoiicon);
            }
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bm);
            marker = mGooglemap.addMarker(new MarkerOptions().position(latlng)
                    .title(customMarker.getId()).anchor(0.5f, 0.5f).icon(icon).zIndex(MAP_Z_LEVEL));
        }
    }

    public CustomMarkerObject(ICustomMarker custommarker, Context ctx,
                              GoogleMap googlemap, LatLng latlng, float alpha) {
        context = ctx;
        mGooglemap = googlemap;
        if (custommarker != null) {
            customMarker = custommarker;
            Bitmap bm = customMarker.getIcon();
            if (bm == null) {
                bm = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.defualtpoiicon);
            }
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bm);
            marker = mGooglemap.addMarker(new MarkerOptions().position(latlng)
                    .title(customMarker.getId()).anchor(0.5f, 0.5f).icon(icon).zIndex(MAP_Z_LEVEL));
            marker.setAlpha(alpha);
        }
    }

    public void showBaubble() {
        marker.showInfoWindow();
    }

    public void removeMarkerFromMap() {
        marker.remove();
    }

    public Marker getMarker() {
        return marker;
    }

    public void closeBubble() {
        if (marker != null) {
            marker.hideInfoWindow();
        }
    }

    public void setVisible(boolean visible) {
        if (marker != null) {
            marker.setVisible(visible);
        }
    }

    public void setBubbleView(View v) {
        if (v != null && marker != null && mGooglemap != null) {
            mGooglemap.setInfoWindowAdapter(new CustomInfoWindowAdapter(v, marker));
        }
    }
}
