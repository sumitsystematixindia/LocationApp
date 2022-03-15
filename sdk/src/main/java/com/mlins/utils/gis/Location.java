package com.mlins.utils.gis;

import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.ndk.wrappers.NdkConversionUtils;
import com.mlins.ndk.wrappers.NdkLocation;
import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.MathUtils;
import com.mlins.utils.Objects;
import com.mlins.utils.PoiData;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

import org.json.JSONException;
import org.json.JSONObject;

public class Location implements ILocation {
    public static final int TYPE_INTERNAL = 0;
    public static final int TYPE_EXTERNAL = 1;
    double x;
    double y;
    double z;
    private double lat;
    private double lon;
    private int type = TYPE_EXTERNAL;
    private String campusId = null;
    private String facilityId = "unknown";
    //	String iconUri;
//	String description;
//	List<String> keywordslist = new ArrayList<String>();
//	
    private PoiData poi = null;

    //	public List<String> getKeywords()
//	{
//		return keywordslist;
//		
//	}
    public Location() {
        // TODO Auto-generated constructor stub
    }

    public Location(ILocation iloc) {
        setType(iloc.getLocationType());
        setX(iloc.getX());
        setY(iloc.getY());
        setZ(iloc.getZ());
        setLat(iloc.getLat());
        setLon(iloc.getLon());
        setCampusId(iloc.getCampusId());
        setFacilityId(iloc.getFacilityId());
    }

    public Location(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = TYPE_INTERNAL;

    }

    public Location(String facility, String campus, float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.campusId = campus;
        this.facilityId = facility;
        this.type = TYPE_INTERNAL;

    }

    public Location(LatLng latlng) {
        this.lat = latlng.latitude;
        this.lon = latlng.longitude;
        this.type = TYPE_EXTERNAL;
    }

    public Location(PoiData data) {
        if (data.getPoiNavigationType().equals("external")) {
            this.lat = data.getPoiLatitude();
            this.lon = data.getPoiLongitude();
            this.type = TYPE_EXTERNAL;
            z = data.getZ();
            this.campusId = data.getCampusID();
        } else {
            poi = data;
            x = data.getPoint().x;
            y = data.getPoint().y;
            z = data.getZ();
            this.facilityId = poi.getFacilityID();
            this.campusId = poi.getCampusID();
            this.type = TYPE_INTERNAL;
        }

//		description = data.getpoiDescription();
//		iconUri = data.getPoiuri();
//		
//		keywordslist = data.getPoiKeywords();


    }

    public Location(IPoi data) {
        if (data.getPoiNavigationType().equals("external")) {
            this.lat = data.getPoiLatitude();
            this.lon = data.getPoiLongitude();
            this.type = TYPE_EXTERNAL;
            z = data.getZ();
            this.campusId = data.getCampusID();
        } else {
            x = data.getPoint().x;
            y = data.getPoint().y;
            z = data.getZ();
            this.facilityId = data.getFacilityID();
            this.campusId = data.getCampusID();
            this.type = TYPE_INTERNAL;
        }

    }

    public void parse(JSONObject loc) {
        try {
            type = loc.getInt("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            campusId = loc.getString("campusid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            facilityId = loc.getString("facilityid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            x = loc.getDouble("x");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            y = loc.getDouble("y");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            z = loc.getDouble("z");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            lat = loc.getDouble("lat");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            lon = loc.getDouble("lon");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public JSONObject getAsJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            try {
                jsonObj.put("type", type);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("campusid", campusId);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("facilityid", facilityId);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("x", x);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("y", y);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("z", z);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("lat", lat);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("lon", lon);
            } catch (Throwable t) {
                t.printStackTrace();
            }


        } catch (Throwable t) {
            t.printStackTrace();
        }

        return jsonObj;
    }

    public PoiData getPoi() {
        return poi;
    }

    public void setPoi(PoiData poi) {
        this.poi = poi;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getType() {
        return type;
    }

    public void setType(LocationMode locationMode) {
        this.type = locationMode.getValue();
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCampusId() {
        return campusId;
    }

    public void setCampusId(String campusId) {
        this.campusId = campusId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    @Override
    public LocationMode getLocationType() {
        LocationMode l = LocationMode.OUTDOOR_MODE;
        if (type == 0) {
            l = LocationMode.INDOOR_MODE;
        }
        return l;
    }

    @Override
    public String toString() {
        return "Location [x=" + x + ", y=" + y + ", z=" + z + ", lat=" + lat + ", lon=" + lon + ", \n" +
                " type=" + type + ", campusId=" + campusId + ", facilityId=" + facilityId + ",\n poi=" + poi + "]";
    }

    public static LatLng getLatLng(ILocation location){
        if(isOutDoor(location)) {
            return new LatLng(location.getLat(), location.getLon());
        } else {
            return getLatLng(location.getX(), location.getY(), location.getCampusId(), location.getFacilityId());
        }
    }

    public static LatLng getLatLng(double x, double y, String facilityId) {
        return getLatLng(x, y, ProjectConf.getInstance().getSelectedCampus().getId(), facilityId);
    }


    public static LatLng getLatLng(double x, double y, String campusID, String facilityId) {
        FacilityConf facilityConf = ProjectConf.getInstance().getFacilityConfById(campusID, facilityId);

        NdkLocation point = new NdkLocation(x, y);
        point.setZ(-1); // non relevant

        NdkLocation covertedPoint = new NdkLocation();

        NdkConversionUtils converter = new NdkConversionUtils();

        double rotationAngle = facilityConf.getRot_angle();

        converter.convertPoint(point, facilityConf.getConvRectTLlon(),
                facilityConf.getConvRectTLlat(), facilityConf.getConvRectTRlon(),
                facilityConf.getConvRectTRlat(), facilityConf.getConvRectBLlon(),
                facilityConf.getConvRectBLlat(), facilityConf.getConvRectBRlon(),
                facilityConf.getConvRectBRlat(), facilityConf.getMapWidth(),
                facilityConf.getMapHight(), rotationAngle, covertedPoint);


        return new LatLng(covertedPoint.getLat(),
                covertedPoint.getLon());
    }

    public static PointF getPoint(ILocation location) {
        ensureInDoor(location);
        return new PointF((float) location.getX(), (float) location.getY());
    }

    public static boolean isInDoor(ILocation location){
        return location.getLocationType() == LocationMode.INDOOR_MODE;
    }

    public static boolean isOutDoor(ILocation location){
        return location.getLocationType() == LocationMode.OUTDOOR_MODE;
    }


    public static void ensureInDoor(ILocation location){
        if(Location.isOutDoor(location))
            throw new IllegalArgumentException("expected indoor location but got: " + location);

        if(location.getFacilityId() == null
                || location.getCampusId() == null)
            throw new NullPointerException("location.getFacilityId() == null || location.getCampusId() == null");
    }

    public static void ensureOutdoor(ILocation location){
        if(Location.isInDoor(location))
            throw new IllegalArgumentException("expected indoor location but got: " + location);
    }

    public static boolean onTheSameFloor(ILocation a, ILocation b){
        return inTheSameFacility(a, b) && onTheSameLevel(a, b);
    }

    private static boolean onTheSameLevel(ILocation a, ILocation b) {
        return Math.abs(a.getZ()-b.getZ()) < 0.01d;
    }

    public static boolean inTheSameFacility(ILocation a, ILocation b){
        return Objects.equals(a.getFacilityId(), b.getFacilityId());
    }

    public static double getOutDoorDistance(ILocation a, ILocation b){
        return MathUtils.distance(Location.getLatLng(a), Location.getLatLng(b));
    }

    public static boolean areEqual(ILocation a, ILocation b){
        if (a == b)
            return true;

        if (a.getLocationType() != b.getLocationType())
            return false;

        if(a.getLocationType() == LocationMode.INDOOR_MODE) {
            return Objects.equals(a.getFacilityId(), b.getFacilityId())
                    && Objects.equals(a.getCampusId(), b.getCampusId())
                    && (int) a.getX() == (int) b.getX()
                    && (int) a.getY() == (int) b.getY()
                    && (int) a.getZ() == (int) b.getZ();
        } else {
            return a.getLat() == b.getLat()
                    && a.getLon() == b.getLon();
        }
    }


//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	}

//	public String getIconUri() {
//		return iconUri;
//	}
//
//	public void setIconUri(String iconUri) {
//		this.iconUri = iconUri;
//	}
//
//	public String toListString() {
//		return description;
//	}

}
