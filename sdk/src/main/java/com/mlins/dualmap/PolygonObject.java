package com.mlins.dualmap;

import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;
import com.spreo.nav.enums.LocationMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PolygonObject {

    private String id = null;
    private String name = null;
    private LocationMode type = null;
    private String pid = null;
    private String cid = null;
    private String fid = null;
    private int floor = 0;
    private List<LatLng> polygon = new ArrayList<>();
    private List<PointF> xyPolygon = new ArrayList<>();
    private HashMap<String, String> params = new HashMap<>();
    private float strokeWidth = 1;

    private boolean visible = false;
    private String fillColor = null;
    private String borderColor = null;

    public PolygonObject() {

    }

    public PolygonObject(JSONObject json) {
        parse(json);
    }

    public void parse(JSONObject json) {
        try {
            id = String.valueOf(json.getInt("id"));
            name = json.getString("name");
            int mode = json.getInt("type");
            if (mode == 0) {
                type = LocationMode.INDOOR_MODE;
            } else {
                type = LocationMode.OUTDOOR_MODE;
            }
            pid = json.getString("pid");
            cid = json.getString("cid");
            fid = json.getString("fid");
            floor = json.getInt("floor");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray poly = null;
        try {
            poly = json.getJSONArray("polygon");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (poly != null) {
            for (int i = 0; i < poly.length(); i++) {
                JSONObject latLngJson = null;
                try {
                    latLngJson = poly.getJSONObject(i);
                    double latitude = latLngJson.getDouble("lat");
                    double longitude = latLngJson.getDouble("lon");
                    LatLng locNode = new LatLng(latitude, longitude);
                    polygon.add(locNode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (type == LocationMode.INDOOR_MODE) {
                    try {
                        latLngJson = poly.getJSONObject(i);
                        Float x = (float) latLngJson.getDouble("x");
                        Float y = (float) latLngJson.getDouble("y");
                        PointF p = new PointF(x, y);
                        xyPolygon.add(p);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        try {
            JSONArray paramsarry = json.getJSONArray("params");
            for (int i = 0; i < paramsarry.length(); i++) {
                JSONObject obj = paramsarry.getJSONObject(i);
                String key = obj.getString("name");
                String value = obj.getString("value");
                params.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationMode getType() {
        return type;
    }

    public void setType(LocationMode type) {
        this.type = type;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public List<LatLng> getPolygon() {
        return polygon;
    }

    public void setPolygon(List<LatLng> polygon) {
        this.polygon = polygon;
    }

    public List<PointF> getXyPolygon() {
        return xyPolygon;
    }

    public void setXyPolygon(List<PointF> xyPolygon) {
        this.xyPolygon = xyPolygon;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
}
