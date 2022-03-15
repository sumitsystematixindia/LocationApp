package com.mlins.wireless.filePlayer;

import com.mlins.wireless.WlBlip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecordStep {
    public static final String TIMESTAMP = "TIME";

    public static final String BLIPS = "BLIPS";

    List<WlBlip> blips = new ArrayList<WlBlip>();
    long rectime = 0;

    public RecordStep() {
        // TODO Auto-generated constructor stub
    }

    public RecordStep(long timeStamp, List<WlBlip> blips) {
        rectime = timeStamp;
        this.blips = blips;
    }

    public String toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put(TIMESTAMP, rectime);
            obj.put(BLIPS, new JSONArray());
            for (WlBlip blip : blips) {
                JSONObject jsblip = new JSONObject();
                jsblip.put("SSID", blip.SSID);
                jsblip.put("BSSID", blip.BSSID);
                jsblip.put("LEVEL", blip.level);
                jsblip.put("FREQUENCY", blip.frequency);
                jsblip.put("TIMESTEMP", blip.timestamp);
                obj.accumulate("BLIPS", jsblip);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return obj.toString();
    }

    public void Parse(JSONObject obj) {
        if (obj != null) {
            try {
                rectime = obj.getLong(TIMESTAMP);
                JSONArray jsblips = obj.getJSONArray(BLIPS);
                for (int i = 0; i < jsblips.length(); i++) {
                    JSONObject jsblip = jsblips.getJSONObject(i);
                    WlBlip theblip = new WlBlip(/*SSID*/ "", /*BSSID*/"", /*level*/-1, /*frequency*/-1, /*tsf*/-1);
                    theblip.SSID = jsblip.getString("SSID");
                    theblip.BSSID = jsblip.getString("BSSID");
                    theblip.level = jsblip.getInt("LEVEL");
                    theblip.frequency = jsblip.getInt("FREQUENCY");
                    theblip.timestamp = jsblip.getLong("TIMESTEMP");
                    blips.add(theblip);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public List<WlBlip> getData() {
        return blips;
    }


    public long getTimeStamp() {
        return rectime;
    }
}
