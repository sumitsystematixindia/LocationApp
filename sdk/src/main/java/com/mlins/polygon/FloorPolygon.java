package com.mlins.polygon;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FloorPolygon {

    private int autoIndex = -1;

    private String projectId = null;
    private String campusId = null;
    private String facility = null;
    private int floor = -100;

    private List<LatLng> polygon = null;

    public FloorPolygon() {

    }

    public FloorPolygon(int autoIndex, String projectId, String campusId,
                        String facility, int floor) {
        super();
        this.autoIndex = autoIndex;
        this.projectId = projectId;
        this.campusId = campusId;
        this.facility = facility;
        this.floor = floor;
    }


    public List<LatLng> getPolygon() {
        return polygon;
    }


    public int getAutoIndex() {
        return autoIndex;
    }

    public void setAutoIndex(int autoIndex) {
        this.autoIndex = autoIndex;
    }


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCampusId() {
        return campusId;
    }

    public void setCampusId(String campusId) {
        this.campusId = campusId;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }


    public void setPolygonListFromJsonArray(JSONArray polyJsonArr) {

        try {
            if (polygon == null) {
                polygon = new ArrayList<LatLng>();
            }

            polygon.clear();

            for (int i = 0; i < polyJsonArr.length(); i++) {
                JSONObject latLngJson = polyJsonArr.getJSONObject(i);
                double latitude = latLngJson.getDouble("lat");
                double longitude = latLngJson.getDouble("lon");
                LatLng locNode = new LatLng(latitude, longitude);
                polygon.add(locNode);
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }


}

