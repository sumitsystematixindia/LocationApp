package com.mlins.nav.location.sharing;

import android.text.format.DateFormat;

import com.mlins.utils.PropertyHolder;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;

import org.json.JSONObject;

import java.util.Date;

public class SharedLocation implements ILocation {

    public static final int TYPE_INTERNAL = 0;
    public static final int TYPE_EXTERNAL = 1;
    public static final String UNKNOWN_TAG = "unknown";
    private String userId = null;
    private String campusId = null;
    private String facilityId = null;
    private String projectId = null;
    private double x;
    private double y;
    private double z;
    private double lat;
    private double lon;
    private int type = TYPE_EXTERNAL;
    private Date reportedTs = null;

    public SharedLocation(String userId, ILocation location) {
        super();
        if (location != null) {
            this.userId = userId;
            setCampusId(location.getCampusId());
            setFacilityId(location.getFacilityId());

            this.projectId = PropertyHolder.getInstance().getProjectId();
            this.x = location.getX();
            this.y = location.getY();
            this.z = (int) location.getZ();
            this.lat = location.getLat();
            this.lon = location.getLon();
            LocationMode locMode = location.getLocationType();
            setType(locMode);

            reportedTs = new Date(System.currentTimeMillis());
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getAsJsonString() {
        JSONObject json = getAsJson();
        return json.toString();
    }

    @Override
    public JSONObject getAsJson() {

        JSONObject json = new JSONObject();

        try {
            json.put("id", userId);
        } catch (Exception e) {
            System.out.println(" error key id");
            //e.printStackTrace();
        }

        try {
            json.put("project", projectId);

        } catch (Exception e) {
            System.out.println(" error key project");
            //e.printStackTrace();
        }

        try {
            json.put("campus", campusId);

        } catch (Exception e) {
            System.out.println(" error key campus");
            //e.printStackTrace();
        }

        try {
            json.put("facility", facilityId);
        } catch (Exception e) {
            System.out.println(" error key facility");
            e.printStackTrace();
        }

        try {
            json.put("x", x);
        } catch (Exception e) {
            System.out.println(" error key x");
            //e.printStackTrace();
        }

        try {
            json.put("y", y);
        } catch (Exception e) {
            System.out.println(" error key y");
            //e.printStackTrace();
        }

        try {
            json.put("floor", (int) z);

        } catch (Exception e) {
            System.out.println(" error key floor");
            //e.printStackTrace();
        }


        try {
            json.put("lat", lat);
        } catch (Exception e) {
            System.out.println(" error key lat");
            //e.printStackTrace();
        }

        try {
            json.put("lon", lon);
            setLon(lon);
        } catch (Exception e) {
            System.out.println(" error key lon");
            //e.printStackTrace();
        }


        try {
            json.put("mode", type);
        } catch (Exception e) {
            System.out.println(" error key mode");
            //e.printStackTrace();
        }

        try {
            Date d = new Date();
            CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
            String timestamp = s.toString();
            json.put("ts", timestamp);
        } catch (Exception e) {

        }


//		try{
//			Calendar c = Calendar.getInstance();
//			c.setTime(reportedTs);
//			
//			JSONObject dateJobj = new JSONObject();
//			 dateJobj.put("year",c.get(Calendar.YEAR));
//			 dateJobj.put("month", c.get(Calendar.MONTH) +1);
//			 dateJobj.put("day",c.get(Calendar.DAY_OF_YEAR));
//			 dateJobj.put("hour",c.get(Calendar.HOUR_OF_DAY));
//			 dateJobj.put("minute", c.get(Calendar.MINUTE));
//			 dateJobj.put("second", c.get(Calendar.SECOND));
//			 json.put("date",dateJobj);
//		}
//		catch(Exception e){
//			
//		}

        return json;
    }

    @Override
    public void parse(JSONObject jsonobject) {
        // TODO Auto-generated method stub
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public void setX(double x) {
        // TODO Auto-generated method stub

    }

    @Override
    public double getY() {
        // TODO Auto-generated method stub
        return y;
    }

    @Override
    public void setY(double y) {
        // TODO Auto-generated method stub

    }

    @Override
    public double getZ() {
        // TODO Auto-generated method stub
        return z;
    }

    @Override
    public void setZ(double z) {
        // TODO Auto-generated method stub

    }

    @Override
    public double getLat() {
        // TODO Auto-generated method stub
        return lat;
    }

    @Override
    public void setLat(double lat) {
        // TODO Auto-generated method stub

    }

    @Override
    public double getLon() {
        // TODO Auto-generated method stub
        return lon;
    }

    @Override
    public void setLon(double lon) {
        // TODO Auto-generated method stub

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
    public String getCampusId() {
        return campusId;
    }

    @Override
    public void setCampusId(String campusId) {
        if (campusId == null) {
            this.campusId = UNKNOWN_TAG;
        } else {
            this.campusId = campusId;
        }
    }

    @Override
    public String getFacilityId() {
        return facilityId;
    }

    @Override
    public void setFacilityId(String facilityId) {
        if (facilityId == null) {
            this.facilityId = UNKNOWN_TAG;
        } else {
            this.facilityId = facilityId;
        }

    }

    @Override
    public void setType(LocationMode locationMode) {
        this.type = locationMode.getValue();
    }


}
