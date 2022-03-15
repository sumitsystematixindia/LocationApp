package com.spreo.geofence;

import com.mlins.wireless.WlBlip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by user on 15/08/2017.
 */

public class LocationBeacon {
    private String id = null;
    private String campus = null;
    private String facility = null;
    private double x = 0;
    private double y = 0;
    private double z = 0;
    private int enter_level = 0;
    private int exit_level = 0;
    private boolean inState = false;
    private int currentLevel = -127;
    private int counter = 0;


    public LocationBeacon() {

    }

    public boolean isInstate(List<WlBlip> results, int counterthresh) {
        boolean result = false;
        boolean recevied = false;
        for (WlBlip blip: results) {
            if (blip.BSSID.equals(id)) {
                currentLevel = blip.level;
                recevied = true;
                if (blip.level > enter_level) {
                    result = true;
                    counter = 0;
                } else if(inState && blip.level > exit_level) {
                    result = true;
                    counter = 0;
                } else if(inState && blip.level < exit_level) {
                    counter++;
                    if (counter < counterthresh) {
                        result = true;
                    }
                } else {
                    counter = 0;
                }
            }
        }

        if (!recevied && inState) {
            currentLevel = -127;
            counter++;
            if (counter < counterthresh) {
                result = true;
            }
        }

        inState = result;
        return inState;
    }

    public boolean parse(JSONObject json) {
        boolean result = false;
        try {
            id = json.getString("id");
            campus = json.getString("campus");
            facility = json.getString("facility");
            x = json.getDouble("x");
            y = json.getDouble("y");
            z = json.getDouble("z");
            enter_level = json.getInt("enter_level");
            exit_level = json.getInt("exit_level");
            result = true;
        } catch (JSONException e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public int getEnter_level() {
        return enter_level;
    }

    public void setEnter_level(int enter_level) {
        this.enter_level = enter_level;
    }

    public int getExit_level() {
        return exit_level;
    }

    public void setExit_level(int exit_level) {
        this.exit_level = exit_level;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }


}
