package com.spreo.geofence;

import android.graphics.RectF;

import com.mlins.wireless.WlBlip;

import java.util.ArrayList;
import java.util.List;

public class GeoFenceProximityObj extends GeoFenceObject {


    private String id = null;
    private String type = null;
    private List<Beacon> beaconsList = null;

    private int enterLevel = 0;
    private int exitLevel = 0;


    public GeoFenceProximityObj() {
        super();
    }


    public GeoFenceProximityObj(String id, String type, String projectId,
                                String campusId, String facilityId) {
        super();
        this.id = id;
        this.type = type;
    }


    public int getEnterLevel() {
        return enterLevel;
    }

    public void setEnterLevel(int enterLevel) {
        this.enterLevel = enterLevel;
    }

    public int getExitLevel() {
        return exitLevel;
    }

    public void setExitLevel(int exitLevel) {
        this.exitLevel = exitLevel;
    }


    public boolean addBeacon(Beacon beacon) {

        if (beacon == null) {
            return false;
        }

        if (beaconsList == null) {
            beaconsList = new ArrayList<Beacon>();
        }

        return beaconsList.add(beacon);
    }

    public List<Beacon> getBeaconsList() {
        return beaconsList;
    }


    public GeoProximityState getState(List<WlBlip> results) {


        int count = 0;

        for (WlBlip blip : results) {

            for (Beacon bec : beaconsList) {

                if (blip.BSSID != null && bec != null && blip.BSSID.equals(bec.getId())) {
                    if (blip.level < exitLevel) {
                        count++;
                    }

                    if (blip.level > enterLevel) {
                        return GeoProximityState.ENTER;
                    }
                }
            }
        }

        if (count <= beaconsList.size()) {
            return GeoProximityState.EXIT;
        }

        return GeoProximityState.UNKNOWN;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return getType();
    }

    @Override
    public Integer getZ() {
        return -1;
    }

    @Override
    public boolean parse(String line) {
        return false;
    }

    @Override
    public boolean isContains(float x, float y) {
        return false;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public RectF getRect() {
        return new RectF(0, 0, 0, 0);
    }

}
