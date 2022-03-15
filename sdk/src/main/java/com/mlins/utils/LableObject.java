package com.mlins.utils;

import android.graphics.PointF;

import com.mlins.utils.gis.Location;

import org.json.JSONException;
import org.json.JSONObject;

public class LableObject {
    public PointF point;
    Location location = null;
    String txt = "";

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public JSONObject getAsJson() {
        JSONObject result = new JSONObject();
        try {
            result.put("lable_txt", txt);
            result.put("x_position", location.getX());
            result.put("y_position", location.getY());
            result.put("z_position", location.getZ());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

}
