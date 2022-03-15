package com.location.app.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class Floor {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("building_id")
    @Expose
    private Integer buildingId;
    @SerializedName("floor_name")
    @Expose
    private String floorName;
    @SerializedName("floor_index")
    @Expose
    private Integer floorIndex;
    @SerializedName("floor_location")
    @Expose
    private String floorLocation;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("floor_description")
    @Expose
    private String floorDescription;
    @SerializedName("floor_welcome_message")
    @Expose
    private String floorWelcomeMessage;
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
    @SerializedName("floor_image_pois")
    @Expose
    private ArrayList<FloorImagePoi> floorImagePois = new ArrayList<>();
    @SerializedName("floor_image_overlay")
    @Expose
    private ArrayList<FloorImageOverlay> floorImageOverlay = new ArrayList<>();

    public ArrayList<BeaconsModel> getBeacons() {
        return beacons;
    }

    public void setBeacons(ArrayList<BeaconsModel> beacons) {
        this.beacons = beacons;
    }

    @SerializedName("beacons")
    @Expose
    private ArrayList<BeaconsModel> beacons = new ArrayList<>();

    @SerializedName("indoor_pathways")
    @Expose
    private ArrayList<IndoorPathway> indoorPathways = new ArrayList<>();

    public ArrayList<IndoorPathway> getIndoorPathways() {
        return indoorPathways;
    }

    public void setIndoorPathways(ArrayList<IndoorPathway> indoorPathways) {
        this.indoorPathways = indoorPathways;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public Integer getFloorIndex() {
        return floorIndex;
    }

    public void setFloorIndex(Integer floorIndex) {
        this.floorIndex = floorIndex;
    }

    public String getFloorLocation() {
        return floorLocation;
    }

    public void setFloorLocation(String floorLocation) {
        this.floorLocation = floorLocation;
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

    public String getFloorDescription() {
        return floorDescription;
    }

    public void setFloorDescription(String floorDescription) {
        this.floorDescription = floorDescription;
    }

    public String getFloorWelcomeMessage() {
        return floorWelcomeMessage;
    }

    public void setFloorWelcomeMessage(String floorWelcomeMessage) {
        this.floorWelcomeMessage = floorWelcomeMessage;
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

    public ArrayList<FloorImagePoi> getFloorImagePois() {
        return floorImagePois;
    }

    public void setFloorImagePois(ArrayList<FloorImagePoi> floorImagePois) {
        this.floorImagePois = floorImagePois;
    }

    public ArrayList<FloorImageOverlay> getFloorImageOverlay() {
        return floorImageOverlay;
    }

    public void setFloorImageOverlay(ArrayList<FloorImageOverlay> floorImageOverlay) {
        this.floorImageOverlay = floorImageOverlay;
    }
}
