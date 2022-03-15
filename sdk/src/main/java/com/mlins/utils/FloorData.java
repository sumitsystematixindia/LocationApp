package com.mlins.utils;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meir
 *         <p>
 *         the class holds floor data structure in facility
 */
public class FloorData implements Comparable<FloorData> {

    public String mapuri = "";
    public String thumburi = "";
//	private String file = null;
//	private List<SwitchFloorPoint> switchFloorPointsList = new ArrayList<SwitchFloorPoint>();
//	private List<GisLine> gisList = new ArrayList<GisLine>();
//	private List<Poi> poisList = new ArrayList<Poi>();
//	private List<GeoFence> geoFencesList = new ArrayList<GeoFence>();
    public String title = "";
    public String gis = "";
    public String poi = "";
    public String poiar = "";
    public String poihe = "";
    public String poien = "";
    public String poiru = "";
    public String poies = "";
    public float pixelsToMeter;
    public float rotation;
    public float stickyRadius = -1;
    public String matrix = "";
    private String id = null;
    private int index = -100;
    private List<LatLng> polygon = new ArrayList<LatLng>();

    public FloorData(String nmap, String nthumb, String ntitle, String gisdata) {
        mapuri = nmap;
        thumburi = nthumb;
        title = ntitle;
        gis = gisdata;


    }

    public FloorData(int index, String map, String thumb, String title, float p2m, float rot, String gisdata) {
        this.index = index;
        mapuri = map;
        thumburi = thumb;
        this.title = title;
        pixelsToMeter = p2m;
        rotation = rot;
        gis = gisdata;

    }

    public FloorData(String id, int index, String title) {
        super();
        this.id = id;
        this.index = index;
        this.title = title;
    }

    public String getMapuri() {
        return mapuri;
    }

    public void setMapuri(String mapuri) {
        this.mapuri = mapuri;
    }

    public String getThumburi() {
        return thumburi;
    }

    public void setThumburi(String thumburi) {
        this.thumburi = thumburi;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGis() {
        return gis;
    }

    public void setGis(String gis) {
        this.gis = gis;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public String getPoiar() {
        return poiar;
    }

    public void setPoiar(String poiar) {
        this.poiar = poiar;
    }

    public String getPoihe() {
        return poihe;
    }

    public void setPoihe(String poihe) {
        this.poihe = poihe;
    }

    public String getPoien() {
        return poien;
    }

    public void setPoien(String poien) {
        this.poien = poien;
    }

    public String getPoiru() {
        return poiru;
    }

    public void setPoiru(String poiru) {
        this.poiru = poiru;
    }

    public float getPixelsToMeter() {
        return pixelsToMeter;
    }

    public void setPixelsToMeter(float pixelsToMeter) {
        this.pixelsToMeter = pixelsToMeter;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rot) {
        rotation = rot;

    }

    public void setPixelToMeters(float p2m) {
        pixelsToMeter = p2m;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + index;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FloorData other = (FloorData) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (index != other.index)
            return false;
        return true;
    }

    public JSONObject getAsJson() {
        JSONObject floorObj = new JSONObject();

        try {
            try {
                floorObj.put("id", id);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                floorObj.put("name", title);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                floorObj.put("index", index);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                floorObj.put("file", mapuri);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Throwable t) {
            t.printStackTrace();
        }

        return floorObj;
    }

    @Override
    public int compareTo(FloorData other) {
        Integer ind = index;
        Integer oind = other.index;
        return ind.compareTo(oind);
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public String getPoies() {
        return poies;
    }

    public void setPoies(String poies) {
        this.poies = poies;
    }

    public List<LatLng> getPolygon() {
        return polygon;
    }

    public void setPolygon(List<LatLng> polygon) {
        this.polygon = polygon;
    }


}