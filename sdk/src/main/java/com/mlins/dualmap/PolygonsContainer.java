package com.mlins.dualmap;

import com.mlins.utils.PropertyHolder;
import com.spreo.nav.enums.LocationMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PolygonsContainer {
    File dir = PropertyHolder.getInstance().getProjectDir();
    String fileName = "polygons.json";
    List<PolygonObject> polygons = new ArrayList<>();
    private Map<String, List<PolygonObject>> facilityLevelpolygonsMap = new HashMap<>();
    private Map<String, List<PolygonObject>> floorLevelpolygonsMap = new HashMap<>();
    private List<PolygonObject> externalPolygons = new ArrayList<>();

    public PolygonsContainer() {
        load();
    }

    private void load() {
        File file = new File(dir, fileName);
        if (file.exists()) {
            String content = getFileContent(file);
            JSONArray jsonarray = null;
            try {
                jsonarray = new JSONArray(content);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonarray != null) {
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject json = null;
                    try {
                        json = jsonarray.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (json != null) {
                        PolygonObject p = new PolygonObject(json);
                        addPolygon(p);
                    }

                }
            }
        }
    }

    private void addPolygon(PolygonObject p) {
        polygons.add(p);
        LocationMode type = p.getType();
        if (type != null) {
            if (type == LocationMode.OUTDOOR_MODE) {
                externalPolygons.add(p);
            } else {
                addToFacility(p);
                addToFloor(p);
            }
        }
    }

    private void addToFloor(PolygonObject p) {
        String flkey = p.getFid() + "_" + p.getFloor();
        List<PolygonObject> floorPolygons = floorLevelpolygonsMap.get(flkey);
        if (floorPolygons == null) {
            floorPolygons = new ArrayList<PolygonObject>();
            floorLevelpolygonsMap.put(flkey, floorPolygons);
        }
        floorPolygons.add(p);
    }

    private void addToFacility(PolygonObject p) {
        String fkey = p.getFid();
        if (fkey != null) {
            List<PolygonObject> facilityPolygons = facilityLevelpolygonsMap.get(fkey);

            if (facilityPolygons == null) {
                facilityPolygons = new ArrayList<PolygonObject>();
                facilityLevelpolygonsMap.put(fkey, facilityPolygons);
            }
            facilityPolygons.add(p);
        }
    }

    private String getFileContent(File file) {
        Scanner scanner = null;
        StringBuffer content = new StringBuffer();

        try {
            scanner = new Scanner(file, "UTF-8");
            while (scanner.hasNext()) {
                content.append(scanner.nextLine());
            }

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (scanner != null)
                scanner.close();
        }
        return content.toString();
    }

    public List<PolygonObject> getProjectPolygons() {
        return polygons;
    }

    public List<PolygonObject> getExternalPolygons() {
        return externalPolygons;
    }

    public List<PolygonObject> getFailityPolygons(String failityid) {
        return facilityLevelpolygonsMap.get(failityid);
    }

    public List<PolygonObject> getFloorPolygons(String failityid, int floor) {
        String flkey = failityid + "_" + floor;
        return floorLevelpolygonsMap.get(flkey);
    }

    public void setVisiblePolygons(List<String> ids) {
        for (PolygonObject o : polygons) {
            if (ids.contains(o.getId())) {
                o.setVisible(true);
            } else {
                o.setVisible(false);
            }
        }
    }

    public List<PolygonObject> getPolygonsByParamValue(String param, String value) {
        List<PolygonObject>  result = new ArrayList<>();
        for (PolygonObject o : polygons) {
            HashMap<String, String> params = o.getParams();
            if (params != null) {
                String ovalue = params.get(param);
                if (ovalue != null && ovalue.equals(value)) {
                    result.add(o);
                }
            }
        }
        return result;
    }

}
