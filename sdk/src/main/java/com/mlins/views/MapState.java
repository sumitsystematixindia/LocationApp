package com.mlins.views;

import android.graphics.Matrix;

import com.spreo.nav.enums.LocationMode;

public class MapState {
    private String campusId = null;
    private String facilityId = null;
    private LocationMode mapMode = null;
    private int mapFloor = -100;
    private Matrix mapMatrix = null;
    private float mapZoom = -100;

    public MapState() {

    }

    public MapState(String campusid, String facilityid, LocationMode mapmode, int mapfloor, Matrix mapmatrix, float mapzoom) {
        campusId = campusid;
        facilityId = facilityid;
        mapMode = mapmode;
        mapFloor = mapfloor;
        mapMatrix = mapmatrix;
        mapZoom = mapzoom;
    }

    public String getCampusId() {
        return campusId;
    }

    public void setCampusId(String campusId) {
        this.campusId = campusId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public LocationMode getMapMode() {
        return mapMode;
    }

    public void setMapMode(LocationMode mapMode) {
        this.mapMode = mapMode;
    }


    public int getMapFloor() {
        return mapFloor;
    }

    public void setMapFloor(int mapFloor) {
        this.mapFloor = mapFloor;
    }

    public Matrix getMapMatrix() {
        return mapMatrix;
    }

    public void setMapMatrix(Matrix mapMatrix) {
        this.mapMatrix = mapMatrix;
    }

    public float getMapZoom() {
        return mapZoom;
    }

    public void setMapZoom(float mapZoom) {
        this.mapZoom = mapZoom;
    }
}
