package com.location.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Datum {
    @SerializedName("id")
    private int id;
    @SerializedName("campus_name")
    private String campusName;
    @SerializedName("floor_url")
    private String floorUrl;
    @SerializedName("icons_rul")
    private String iconsRul;
    @SerializedName("facilities")

    private ArrayList<Facility> facilities = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public String getFloorUrl() {
        return floorUrl;
    }

    public void setFloorUrl(String floorUrl) {
        this.floorUrl = floorUrl;
    }

    public String getIconsRul() {
        return iconsRul;
    }

    public void setIconsRul(String iconsRul) {
        this.iconsRul = iconsRul;
    }

    public ArrayList<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(ArrayList<Facility> facilities) {
        this.facilities = facilities;
    }

}
