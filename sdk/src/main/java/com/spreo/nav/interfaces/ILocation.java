package com.spreo.nav.interfaces;

import com.spreo.nav.enums.LocationMode;

import org.json.JSONObject;

public interface ILocation {

    JSONObject getAsJson();

    void parse(JSONObject jsonobject);

    double getX();

    void setX(double x);

    double getY();

    void setY(double y);

    double getZ();

    void setZ(double z);

    double getLat();

    void setLat(double lat);

    double getLon();

    void setLon(double lon);

    LocationMode getLocationType();

    String getCampusId();

    void setCampusId(String campusId);

    String getFacilityId();

    void setFacilityId(String facilityId);

    void setType(LocationMode locationMode);

}
