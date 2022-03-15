package com.location.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Facility {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("campus_id")
    @Expose
    private int campusId;
    @SerializedName("facility_name")
    @Expose
    private String facilityName;
    @SerializedName("facility_location")
    @Expose
    private String facilityLocation;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("facility_description")
    @Expose
    private String facilityDescription;
    @SerializedName("facility_welcome_message")
    @Expose
    private String facilityWelcomeMessage;
    @SerializedName("outdoor_map_overlay_image")
    @Expose
    private String outdoorMapOverlayImage;
    @SerializedName("image_overlay_campus")
    @Expose
    private String imageOverlayCampus;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("map_image")
    @Expose
    private String mapImage;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("buildings")
    @Expose
    private ArrayList<Building> buildings = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCampusId() {
        return campusId;
    }

    public void setCampusId(Integer campusId) {
        this.campusId = campusId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityLocation() {
        return facilityLocation;
    }

    public void setFacilityLocation(String facilityLocation) {
        this.facilityLocation = facilityLocation;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getFacilityDescription() {
        return facilityDescription;
    }

    public void setFacilityDescription(String facilityDescription) {
        this.facilityDescription = facilityDescription;
    }

    public String getFacilityWelcomeMessage() {
        return facilityWelcomeMessage;
    }

    public void setFacilityWelcomeMessage(String facilityWelcomeMessage) {
        this.facilityWelcomeMessage = facilityWelcomeMessage;
    }

    public String getOutdoorMapOverlayImage() {
        return outdoorMapOverlayImage;
    }

    public void setOutdoorMapOverlayImage(String outdoorMapOverlayImage) {
        this.outdoorMapOverlayImage = outdoorMapOverlayImage;
    }

    public String getImageOverlayCampus() {
        return imageOverlayCampus;
    }

    public void setImageOverlayCampus(String imageOverlayCampus) {
        this.imageOverlayCampus = imageOverlayCampus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMapImage() {
        return mapImage;
    }

    public void setMapImage(String mapImage) {
        this.mapImage = mapImage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ArrayList<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }

}
