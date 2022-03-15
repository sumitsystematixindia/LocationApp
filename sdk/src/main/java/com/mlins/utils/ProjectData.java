package com.mlins.utils;

import com.google.android.gms.maps.model.LatLng;
import com.spreo.nav.interfaces.IProject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class ProjectData implements IProject {

    private String id = null;
    private String name = null;
    private String apikey = null;
    private LatLng location = null;
    private String imageUrl = null;

    public ProjectData() {
        super();

    }

    public static List<IProject> parseJson(String content) {

        List<IProject> list = new ArrayList<IProject>();

        try {
            JSONTokener jsonTokener = new JSONTokener(content);

            JSONObject json = (JSONObject) jsonTokener.nextValue();

            JSONArray jArrObj = json.getJSONArray("projects");

            for (int i = 0; i < jArrObj.length(); i++) {

                try {

                    JSONObject gObj = jArrObj.getJSONObject(i);

                    ProjectData data = new ProjectData();

                    String id = gObj.getString("id");
                    data.setId(id);
                    String name = gObj.getString("name");
                    data.setName(name);
                    String key = gObj.getString("key");
                    data.setApikey(key);

                    String latStr = gObj.getString("center_lat");
                    double latitude = Double.valueOf(latStr);
                    String lonStr = gObj.getString("center_lon");
                    double longitude = Double.valueOf(lonStr);
                    LatLng latLng = new LatLng(latitude, longitude);
                    data.setLocation(latLng);

                    try {
                        String url = gObj.getString("image");
                        if (url != null) {
                            data.setImageUrl(url);
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }

                    list.add(data);

                } catch (Throwable t) {
                    t.printStackTrace();

                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public LatLng getLocation() {
        return this.location;
    }

    public void setLocation(LatLng center) {
        this.location = center;

    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public void setImageUrl(String url) {
        imageUrl = url;
    }

    @Override
    public String toString() {
        return "ProjectData [id=" + id + ", name=" + name + ", apikey="
                + apikey + ", location=(" + location.latitude + "," + location.longitude + ")]";
    }


}
