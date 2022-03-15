package com.spreo.interfaces;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.spreo.nav.enums.LocationMode;

public interface ICustomMarker {
    static final String navPrefix = "nav_";

    void SetIcon(Bitmap icon);

    String getId();

    void setId(String Id);

    String getProjectId();

    void setProjectId(String projectId);

    String getCampusId();

    void setCampusId(String campusId);

    String getFacilityId();

    void setFacilityId(String facilityId);

    LocationMode getLocationMode();

    void setLocationMode(LocationMode mode);

    float getX();

    void setX(float x);

    float getY();

    void setY(float y);

    int getFloor();

    void setFloor(int floor);

    LatLng getLatLng();

    void setLatLng(LatLng latlng);

    Bitmap getIcon();
}
