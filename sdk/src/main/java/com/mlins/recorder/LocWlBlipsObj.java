package com.mlins.recorder;

import android.graphics.PointF;
import android.text.format.DateFormat;

import com.mlins.wireless.WlBlip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class LocWlBlipsObj {

    private int floor = -1000;
    private String facilityId = "";
    private PointF pLoc = null;
    private List<WlBlip> blips = null;
    private String timestamp = null;
    private String campusId = null;
    private String projectId = null;


    public LocWlBlipsObj(String projectid, String campusid, String facilityid, PointF pLoc, int floor,
                         List<WlBlip> blips) {
        super();
        this.pLoc = pLoc;
        this.blips = blips;
        Date d = new Date();
        CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
        timestamp = s.toString();
        this.facilityId = facilityid;
        this.floor = floor;
        this.projectId = projectid;
        this.campusId = campusid;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facId) {
        this.facilityId = facId;
    }

    public PointF getpLoc() {
        return pLoc;
    }

    public void setpLoc(PointF pLoc) {
        this.pLoc = pLoc;
    }

    public List<WlBlip> getBlips() {
        return blips;
    }

    public void setBlips(List<WlBlip> blips) {
        this.blips = blips;
    }


    @Override
    public String toString() {

        JSONObject jsonObj = new JSONObject();

        try {

            jsonObj.put("ts", timestamp);
            jsonObj.put("projectId", projectId);
            jsonObj.put("campusId", campusId);
            jsonObj.put("facilityId", facilityId);
            jsonObj.put("z", floor);
            jsonObj.put("x", pLoc.x);
            jsonObj.put("y", pLoc.y);

            JSONArray blipsArray = new JSONArray();

            for (WlBlip bl : blips) {
                blipsArray.put(bl.toJson());
            }

            jsonObj.put("blips", blipsArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj.toString();

    }

    public JSONObject toJson() {

        JSONObject jsonObj = new JSONObject();

        try {

            jsonObj.put("ts", timestamp);
            jsonObj.put("projectId", projectId);
            jsonObj.put("campusId", campusId);
            jsonObj.put("facilityId", facilityId);
            jsonObj.put("z", floor);
            jsonObj.put("x", pLoc.x);
            jsonObj.put("y", pLoc.y);

            JSONArray blipsArray = new JSONArray();

            for (WlBlip bl : blips) {
                blipsArray.put(bl.toJson());
            }

            jsonObj.put("blips", blipsArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    public String getCampusId() {
        return campusId;
    }

    public void setCampusId(String campusId) {
        this.campusId = campusId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }


}
