package com.location.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Building {

    @SerializedName("id")
    private int id;
    @SerializedName("facility_id")
    @Expose
    private int facilityId;
    @SerializedName("building_name")
    @Expose
    private String buildingName;
    @SerializedName("building_location")
    @Expose
    private String buildingLocation;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("building_description")
    @Expose
    private String buildingDescription;
    @SerializedName("building_welcome_message")
    @Expose
    private String buildingWelcomeMessage;
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
    @SerializedName("floors")
    @Expose
    private ArrayList<Floor> floors = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingLocation() {
        return buildingLocation;
    }

    public void setBuildingLocation(String buildingLocation) {
        this.buildingLocation = buildingLocation;
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

    public String getBuildingDescription() {
        return buildingDescription;
    }

    public void setBuildingDescription(String buildingDescription) {
        this.buildingDescription = buildingDescription;
    }

    public String getBuildingWelcomeMessage() {
        return buildingWelcomeMessage;
    }

    public void setBuildingWelcomeMessage(String buildingWelcomeMessage) {
        this.buildingWelcomeMessage = buildingWelcomeMessage;
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

    public ArrayList<Floor> getFloors() {
        return floors;
    }

    public void setFloors(ArrayList<Floor> floors) {
        this.floors = floors;
    }
}
