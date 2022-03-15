package com.mlins.nav.utils;

import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mlins.utils.FacilityObj;
import com.mlins.utils.logging.Log;

public class FacilityMapOverlay {

    private final static String TAG = "ccom.mlins.nav.utils.FacilityMapOverlay";
    FacilityObj facObj = null;
    private Bitmap bitmap = null;
    private BitmapDescriptor mapImage = null;
    private float angle = 0;
    private double xpixels = 0;
    private double ypixels = 0;
    private GroundOverlay groundOverlay = null;
    private double southwestLongitude = 0;
    private double northeastLongitude;
    private double southwestLatitude;
    private double northeastLatitude;
    private Marker marker = null;
    private LatLng southwest = null;
    private LatLng northeast = null;
    private LatLng northwest = null;
    private LatLng southeast = null;


    public FacilityMapOverlay() {

    }

    public FacilityObj getFacObj() {
        return facObj;
    }

    public void setFacObj(FacilityObj facObj) {
        this.facObj = facObj;
    }


    public void createOvarlay(GoogleMap map) {
        Log.getInstance().debug(TAG, "Enter, createOvarlay()");

        if (mapImage == null)
            return;

        groundOverlay = map.addGroundOverlay(new GroundOverlayOptions()
                .image(mapImage).position(getCenter(), getWidth())
                .bearing(getAngle()).transparency((float) 0.1));

        if (groundOverlay != null) {
            southwest = groundOverlay.getBounds().southwest;
            northeast = groundOverlay.getBounds().northeast;
            southwestLongitude = southwest.longitude;
            northeastLongitude = northeast.longitude;
            southwestLatitude = southwest.latitude;
            northeastLatitude = northeast.latitude;

            northwest = new LatLng(southwestLongitude, northeastLatitude);
            southeast = new LatLng(northeastLongitude, southwestLatitude);
        }

        Log.getInstance().debug(TAG, "Exit, createOvarlay()");
    }

    public LatLng getSouthwest() {
        return southwest;
    }

    public LatLng getNortheast() {
        return northeast;
    }

    public LatLng getNorthwest() {
        return northwest;
    }

    public LatLng getSoutheast() {
        return southeast;
    }

    public void removeOvarlay() {
        if (groundOverlay != null)
            groundOverlay.remove();
    }

    public LatLng convertToLatlng(double x, double y) {
        LatLng result = null;

        if (groundOverlay != null) {

            double xdiff = southwestLongitude - northeastLongitude;
            double xpixeltolat = xdiff / xpixels;

            double ydiff = southwestLatitude - northeastLatitude;
            double ypixeltolat = ydiff / ypixels;

            double l1 = northeastLatitude + (y * ypixeltolat);
            double l2 = southwestLongitude - (x * xpixeltolat);
            result = new LatLng(l1, l2);

        }
        return result;
    }

    public LatLng getRotetedLatLng(LatLng l) {
        LatLng result = null;

        double lat = getCenter().latitude
                + (Math.cos(Math.toRadians(groundOverlay.getBearing()))
                * (l.latitude - getCenter().latitude) - Math.sin(Math
                .toRadians(groundOverlay.getBearing()))
                * (l.longitude - getCenter().longitude));

        double lon = getCenter().longitude
                + (Math.sin(Math.toRadians(groundOverlay.getBearing()))
                * (l.latitude - getCenter().latitude) + Math.cos(Math
                .toRadians(groundOverlay.getBearing()))
                * (l.longitude - getCenter().longitude));
        result = new LatLng(lat, lon);
        return result;

    }

    public String getFacilityId() {
        return (facObj == null) ? null : facObj.getIdName();
    }

    public void setFacilityId(String facilityId) {
        if (facObj != null && facilityId != null) {
            facObj.setIdName(facilityId);
        }
    }

    public LatLng getCenter() {
        LatLng center = null;
        if (facObj != null) {
            center = new LatLng(facObj.getLat(), facObj.getLon());

        }
        return center;
    }


    public boolean changeFloor(int f, GoogleMap gm) {
        //
        // if (groundOverlay != null) {
        // groundOverlay.remove();
        // }
        //
        // if (MapProvider.getInstance().getCurrentZoom() > MapProvider
        // .getInstance().getActionZoomLevel()) {
        //
        // //if (currFloor != f || currFloor == -99) {
        //
        // floor = f;
        // // currFloor=f;
        // bitmap = MapProvider.getInstance().getMapImage(facilityId,
        // floor);
        // if (bitmap != null) {
        // mapImage = BitmapDescriptorFactory.fromBitmap(bitmap);
        // xpixels = bitmap.getWidth();
        // ypixels = bitmap.getHeight();
        // bitmap = null;
        // }
        //
        //
        // if (getFacilityId() != null && floor != -99 /* && bitmap != null */
        // && mapImage != null && getCenter() != null
        // && width != -99) {
        // createOvarlay(gm);
        // return true;
        // }
        //
        // }
        //
        //
        //
        // // }
        //
        return false;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public BitmapDescriptor getMapImage() {
        return mapImage;
    }

    public void setMapImage(BitmapDescriptor mapImage) {
        this.mapImage = mapImage;
    }

    public float getWidth() {
        float width = 0;
        if (facObj != null) {
            width = facObj.getWidth();
        }
        return width;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public double getXpixels() {
        return xpixels;
    }

    public void setXpixels(double xpixels) {
        this.xpixels = xpixels;
    }

    public double getYpixels() {
        return ypixels;
    }

    public void setYpixels(double ypixels) {
        this.ypixels = ypixels;
    }

    public GroundOverlay getGroundOverlay() {
        return groundOverlay;
    }

    public void setGroundOverlay(GroundOverlay groundOverlay) {
        this.groundOverlay = groundOverlay;
    }

    public double getSouthwestLongitude() {
        return southwestLongitude;
    }

    public void setSouthwestLongitude(double southwestLongitude) {
        this.southwestLongitude = southwestLongitude;
    }

    public double getNortheastLongitude() {
        return northeastLongitude;
    }

    public void setNortheastLongitude(double northeastLongitude) {
        this.northeastLongitude = northeastLongitude;
    }

    public double getSouthwestLatitude() {
        return southwestLatitude;
    }

    public void setSouthwestLatitude(double southwestLatitude) {
        this.southwestLatitude = southwestLatitude;
    }

    public double getNortheastLatitude() {
        return northeastLatitude;
    }

    public void setNortheastLatitude(double northeastLatitude) {
        this.northeastLatitude = northeastLatitude;
    }

}
