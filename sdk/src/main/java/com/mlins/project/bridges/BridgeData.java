package com.mlins.project.bridges;

import android.util.Log;

import com.mlins.utils.FileUtils;
import com.mlins.utils.PropertyHolder;
import com.spreo.nav.interfaces.IPoi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class BridgeData {

    private static final String TAG = BridgeData.class.getName();

    private final List<Bridge> bridges = new ArrayList<>();

    public BridgeData(){
        loadBridges();
    }

    protected void loadBridges(){
        File dir = PropertyHolder.getInstance().getProjectDir();
        File file = new File(dir, "bridges.json");

        try {
            JSONObject json = new JSONObject(FileUtils.getFileContentWithException(file));
            bridges.addAll(BridgesDataParser.parseBridgesData(json));
        } catch (JSONException e) {
            Log.e(TAG, "Can't parse bridges data", e);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "We don't have bridges data for this project");
        }
    }

    public Bridge getBridge(IPoi entranceOrExit) {
        return Bridge.getBridge(bridges, entranceOrExit);
    }

    public List<? extends IPoi> removeBridgesSides(List<? extends IPoi> exitpois) {
        return Bridge.removeBridgeSides(bridges, exitpois);
    }

    public Bridge getBridgeBetween(String facility1ID, String facility2ID){
        for (Bridge bridge : bridges) {
            if(bridge.isBetween(facility1ID, facility2ID))
                return bridge;
        }
        return null;
    }

    public List<Bridge> getBridgesBetween(String facility1ID, String facility2ID){
        List<Bridge> result = new ArrayList<>();
        for (Bridge bridge : bridges) {
            if (bridge.isBetween(facility1ID, facility2ID)) {
                result.add(bridge);
            }
        }
        return result;
    }

    public boolean isFacilitesConnected(String fac1, String fac2) {
        boolean result = false;
        try{
            for (Bridge bridge : bridges) {
                if(bridge.isBetween(fac1, fac2))
                    return true;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public boolean hasBridge(String fac) {
        boolean result = false;
        try{
            for (Bridge bridge : bridges) {
                if (bridge.isConnectToFacility(fac)) {
                    return true;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }



    public List<Bridge> getBridges() {
        return bridges;
    }

}

