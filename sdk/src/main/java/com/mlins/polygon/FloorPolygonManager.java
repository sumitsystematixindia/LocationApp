package com.mlins.polygon;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.dualmap.ConvertingUtils;
import com.mlins.project.ProjectConf;
import com.mlins.utils.Cleanable;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceDownloader;
import com.mlins.utils.ServerConnection;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FloorPolygonManager implements Cleanable {

    private final static String uri = "spreo_polygons.json";
    private List<FloorPolygon> polygons = new ArrayList<FloorPolygon>();

    public static FloorPolygonManager getInstance() {
        return Lookup.getInstance().get(FloorPolygonManager.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(FloorPolygonManager.class);
    }

    public void clean() {
        polygons.clear();
    }

    public boolean loadData() {
        boolean isSucceded = false;

        try {
            clean();

            String content = "";
            if (PropertyHolder.useZip) {
                String url = ServerConnection.getProjectResourcesUrl() + uri;
                byte[] bytes = ResourceDownloader.getInstance().getUrl(url);
                if (bytes != null) {
                    content = new String(bytes);
                }
            } else {
                File root = PropertyHolder.getInstance().getProjectDir();
                File file = new File(root, uri);
                content = getFileContent(file);
            }

            if (content != null && !content.equals("")) {
                parseJson(content);
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return isSucceded;

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

    public void parseJson(String content) {

        try {

            JSONTokener jsonTokener = new JSONTokener(content);

            JSONObject json = (JSONObject) jsonTokener.nextValue();

            JSONArray jArrObj = json.getJSONArray("floor_polygons");

            for (int i = 0; i < jArrObj.length(); i++) {

                try {

                    JSONObject gObj = jArrObj.getJSONObject(i);

                    FloorPolygon elmObj = new FloorPolygon();

                    int autoIndex = gObj.getInt("index");
                    elmObj.setAutoIndex(autoIndex);

                    String campusId = gObj.getString("campus");
                    elmObj.setCampusId(campusId);

                    String facility = gObj.getString("facility");
                    elmObj.setFacility(facility);

                    int floor = gObj.getInt("floor");
                    elmObj.setFloor(floor);

                    JSONArray polyJsonArr = gObj.getJSONArray("polygon");
                    elmObj.setPolygonListFromJsonArray(polyJsonArr);

                    polygons.add(elmObj);

                } catch (Throwable t) {
                    t.printStackTrace();
                }

            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    public ILocation getLocationInPolygon(LatLng latlng) {

        ILocation result = null;

        try {
            if (latlng != null && polygons != null && polygons.size() != 0) {
                for (FloorPolygon o : polygons) {
                    if (o != null) {
                        List<LatLng> poly = o.getPolygon();
                        if (poly != null && !poly.isEmpty()) {
                            if (ConvertingUtils.isPointInPolygon(poly, latlng)) {
                                FacilityConf facConf = ProjectConf.getInstance().getFacilityConfById(o.getCampusId(), o.getFacility());
                                result = ConvertingUtils.convertToXY(latlng, facConf);
                                if (result != null) {
                                    result.setCampusId(o.getCampusId());
                                    result.setFacilityId(o.getFacility());
                                    result.setZ(o.getFloor());
                                    result.setType(LocationMode.INDOOR_MODE);
                                    break;
                                }
                            }
                        }
                    }
                }


            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }

}
