package com.spreo.geofence;

import android.graphics.PointF;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.WlBlip;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 15/08/2017.
 */

public class LocationBeaconsManager implements Cleanable {

    private String fileName = "location_beacons.json";
    private List<LocationBeacon> beacons = new ArrayList<>();
    private Map<String , HashMap<Integer , List<String>>> halfNavMap = new HashMap<String , HashMap<Integer , List<String>>>();
    private int halfNavThresh = -80;
    private String halfNavFileName = "half_nav_settings.json";
    private int proximityCounterThresh = 1;

    private LocationBeaconsManager(){
        load();
    }

    public static LocationBeaconsManager getInstance() {
        return Lookup.getInstance().get(LocationBeaconsManager.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(LocationBeaconsManager.class);
    }

    public void clean() {
        beacons.clear();
        halfNavMap.clear();
    }

    public void loadFacilityHalfNav(String campusId, String facilityId) {
        File dir = PropertyHolder.getInstance().getProjectDir();
        String filePath = dir.getAbsolutePath() + "/" + campusId + "/" + "facilities" + "/" + facilityId + "/" + "floors" + "/" + halfNavFileName;

        String content = getJsonString(filePath);
        if (content != null && !content.isEmpty()) {
            parseHalfNav(facilityId , content);
        }
    }

    private void parseHalfNav(String facilityId, String content) {
        HashMap<Integer , List<String>> facmap = null;
        facmap = halfNavMap.get(facilityId);
        if (facmap == null) {
            facmap = new HashMap<Integer , List<String>>();
        }

        if (content != null) {
            JSONArray array;
            try {
                JSONTokener jsonTokener = new JSONTokener(content);

                JSONObject json = (JSONObject) jsonTokener.nextValue();

                array = json.getJSONArray("beacons_location");

                for (int i = 0; i < array.length(); i++){

                    JSONObject gObj = array.getJSONObject(i);

                    int floor = gObj.getInt("floor");
                    String id = gObj.getString("id");

                    List<String> floorhalfnav = facmap.get(floor);
                    if (floorhalfnav == null) {
                        floorhalfnav = new ArrayList<String>();
                    }
                    floorhalfnav.add(id);
                    facmap.put(floor , floorhalfnav);
                }
            } catch (Exception e1) {
                return;
            }
        }

        halfNavMap.put(facilityId , facmap);

    }

    public PointF getBeaconLocation(List<WlBlip> results, String facilityid , int floor) {
        PointF result = null;
        LocationBeacon lb = findLocationBeacon(results , facilityid , floor);
        if (lb != null) {
            result = new PointF((float)lb.getX(), (float)lb.getY());
        }

//        ILocation result = null;
//        LocationBeacon lb = findLocationBeacon(results);
//        if (lb != null) {
//            result = new Location();
//            result.setX(lb.getX());
//            result.setY(lb.getY());
//            result.setZ(lb.getZ());
//            result.setType(LocationMode.INDOOR_MODE);
//            result.setCampusId(lb.getCampus());
//            result.setFacilityId(lb.getFacility());
//        }

        return result;
    }

    private LocationBeacon findLocationBeacon(List<WlBlip> results, String facilityid , int floor) {

        if (results.isEmpty()) {
            return null;
        }

        List<WlBlip> blips = new ArrayList<WlBlip>();
        blips.addAll(results);
        LocationBeacon result = null;
        int maxlevel = -150;
        for (LocationBeacon o: beacons) {
            if (o.isInstate(blips, proximityCounterThresh)) {
               if (o.getCurrentLevel() > maxlevel) {
                    result = o;
                    maxlevel = o.getCurrentLevel();
                }
            }
        }

        if (result != null) {
            List<String> blist = getFloorHalfNav(facilityid , floor);
            if (blist != null) {
                for (WlBlip blip: blips) {
                    if (blip != null && blip.level > halfNavThresh) {
                        String bid = blip.BSSID;
                        if (blist.contains(bid)) {
                            result = null;
                            break;
                        }
                    }
                }
            }

        }

        return result;
    }

    private void load() {
        try {
            String result = null;
            File dir = PropertyHolder.getInstance().getProjectDir();
            File file = new File(dir, fileName);
            if (!file.exists()) {
                return;
            }
            String content = getJsonString(file.getAbsolutePath());
            if (content != null) {
                PropertyHolder.getInstance().setUseProximityLocation(true);
                JSONArray array;
                try {
                    JSONTokener jsonTokener = new JSONTokener(content);

                    JSONObject json = (JSONObject) jsonTokener.nextValue();

                    array = json.getJSONArray("beacons");

                    for (int i = 0; i < array.length(); i++){

                        JSONObject gObj = array.getJSONObject(i);

                        LocationBeacon lb = new LocationBeacon();
                        if (lb.parse(gObj)) {
                            beacons.add(lb);
                        }
                    }

                    try {
                        halfNavThresh = json.getInt("halfNavThresh");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        proximityCounterThresh = json.getInt("proximityCounterThresh");
                        int a = 5;
                        int b = a +5;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e1) {
                    return;
                }

            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private String getJsonString(String filepath) {
        String result = null;
        File file = new File(filepath);
        if (!file.exists()) {
            return null;
        } else {

            StringBuffer sb = new StringBuffer();

            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                result = sb.toString();


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
            }
        }
        return result;
    }

    private List<String> getFloorHalfNav(String facilityid, int floor) {
        List<String> result = new ArrayList<>();
        try {
            if (facilityid != null) {
                HashMap<Integer , List<String>> facilitymap = halfNavMap.get(facilityid);
                if (facilitymap != null) {
                    List<String> list = facilitymap.get(floor);
                    if (list != null) {
                        result.addAll(list);
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }
}
