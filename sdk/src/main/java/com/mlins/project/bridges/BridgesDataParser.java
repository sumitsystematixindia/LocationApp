package com.mlins.project.bridges;

import android.util.Log;

import com.mlins.project.ProjectConf;
import com.mlins.utils.PoiData;
import com.spreo.nav.interfaces.IPoi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class BridgesDataParser {

    static List<Bridge> parseBridgesData(JSONObject bridgeDataJSON) throws JSONException {
        String campusID = bridgeDataJSON.getString("campus_id");
        JSONArray bridgesArray = bridgeDataJSON.getJSONArray("bridges");
        return parseBridgesArray(campusID, bridgesArray);
    }

    private static List<Bridge> parseBridgesArray(String campusID, JSONArray bridgesArray) throws JSONException {
        ArrayList<Bridge> result = new ArrayList<>();
        for (int i = 0; i < bridgesArray.length(); i++) {
            Bridge b = parseBridge(campusID, bridgesArray.getJSONObject(i));
            if(b != null)
                result.add(b);
        }
        return result;
    }

    private static Bridge parseBridge(String campusID, JSONObject bridgeJSON) throws JSONException {
        JSONArray exitsIDsArray = bridgeJSON.getJSONArray("matching_exits");
        try {
            return new Bridge(
                    bridgeJSON.getString("id"),
                    findBridgeSide(campusID, exitsIDsArray.getString(0)),
                    findBridgeSide(campusID, exitsIDsArray.getString(1))
            );

        } catch (IllegalArgumentException e) {
            Log.e(BridgesDataParser.class.getName(), e.getMessage(), e);
            return null;
        }
    }

    private static PoiData findBridgeSide(String campusID, String bridgeSideID){
        List<IPoi> pois = ProjectConf.getInstance().getAllCampusPoisList(campusID);
        for (IPoi poi : pois) {
            if(bridgeSideID.equals(poi.getPoiID()))
                return (PoiData) poi;
        }
        throw new IllegalArgumentException("Can't find bridge side poi with specified id: " + bridgeSideID);
    }
}
