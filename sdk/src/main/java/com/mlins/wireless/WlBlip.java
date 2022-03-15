package com.mlins.wireless;

import android.annotation.SuppressLint;
import android.net.wifi.ScanResult;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Describes information about a detected wireless beacon.
 */
public class WlBlip {
    /**
     * The network name.
     */
    public String SSID;

    /**
     * The address of the wireless beacon.
     */
    public String BSSID;

    /**
     * The detected signal level in dBm. At least those are the units used by
     * the TI driver.
     */
    public int level;

    /**
     * The frequency in MHz of the channel over which the client is communicating
     * with the wireless beacon.
     */
    public int frequency;

    /**
     * Time Synchronization Function (tsf) timestamp in microseconds when
     * this result was last seen.
     */
    public long timestamp = -1;


    public WlBlip(String SSID, String BSSID, int level, int frequency, long tsf) {
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.level = level;
        this.frequency = frequency;
        this.timestamp = tsf;
    }


    public WlBlip() {

    }

    /**
     * copy constructor
     */
    public WlBlip(WlBlip source) {
        if (source != null) {
            SSID = source.SSID;
            BSSID = source.BSSID;
            level = source.level;
            frequency = source.frequency;
            timestamp = source.timestamp;
        }
    }

    @SuppressLint("NewApi")
    public WlBlip(ScanResult source) {
        if (source != null) {
            SSID = source.SSID;
            BSSID = source.BSSID;
            level = source.level;
            frequency = source.frequency;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                timestamp = source.timestamp;
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String none = "<none>";

        sb.append("SSID: ").
                append(SSID).
                append(", BSSID: ").
                append(BSSID == null ? none : BSSID).
                append(", capabilities: ").
                append(", level: ").
                append(level).
                append(", frequency: ").
                append(frequency).
                append(", timestamp: ").
                append(timestamp);

        return sb.toString();
    }

    public JSONObject toJson() {
//        StringBuilder json = new StringBuilder();
//        // TODO: format as JSON.
//    	return json.toString();
        JSONObject json = new JSONObject();
        try {
            json.put("bssid", BSSID);
            json.put("ssid", SSID);
            json.put("level", level);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    public String getFileSavingFormat() {

        StringBuffer sb = new StringBuffer();
        sb.append(SSID).
                append("\t").
                append(BSSID).
                append("\t").
                append(level).
                append("\t").
                append(frequency).
                append("\t").
                append(timestamp).
                append("\n");
        return sb.toString();
    }

}
