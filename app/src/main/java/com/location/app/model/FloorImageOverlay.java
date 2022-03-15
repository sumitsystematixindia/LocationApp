package com.location.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FloorImageOverlay {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("floor_id")
    @Expose
    private Integer floorId;
    @SerializedName("top_left_latitude")
    @Expose
    private String topLeftLatitude;
    @SerializedName("top_left_longitude")
    @Expose
    private String topLeftLongitude;
    @SerializedName("top_right_latitude")
    @Expose
    private String topRightLatitude;
    @SerializedName("top_right_longitude")
    @Expose
    private String topRightLongitude;
    @SerializedName("bottom_left_latitude")
    @Expose
    private String bottomLeftLatitude;
    @SerializedName("bottom_left_longitude")
    @Expose
    private String bottomLeftLongitude;
    @SerializedName("bottom_right_latitude")
    @Expose
    private String bottomRightLatitude;
    @SerializedName("bottom_right_longitude")
    @Expose
    private String bottomRightLongitude;
    @SerializedName("overlay_image_opacity")
    @Expose
    private float overlayImageOpacity;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFloorId() {
        return floorId;
    }

    public void setFloorId(Integer floorId) {
        this.floorId = floorId;
    }

    public String getTopLeftLatitude() {
        return topLeftLatitude;
    }

    public void setTopLeftLatitude(String topLeftLatitude) {
        this.topLeftLatitude = topLeftLatitude;
    }

    public String getTopLeftLongitude() {
        return topLeftLongitude;
    }

    public void setTopLeftLongitude(String topLeftLongitude) {
        this.topLeftLongitude = topLeftLongitude;
    }

    public String getTopRightLatitude() {
        return topRightLatitude;
    }

    public void setTopRightLatitude(String topRightLatitude) {
        this.topRightLatitude = topRightLatitude;
    }

    public String getTopRightLongitude() {
        return topRightLongitude;
    }

    public void setTopRightLongitude(String topRightLongitude) {
        this.topRightLongitude = topRightLongitude;
    }

    public String getBottomLeftLatitude() {
        return bottomLeftLatitude;
    }

    public void setBottomLeftLatitude(String bottomLeftLatitude) {
        this.bottomLeftLatitude = bottomLeftLatitude;
    }

    public String getBottomLeftLongitude() {
        return bottomLeftLongitude;
    }

    public void setBottomLeftLongitude(String bottomLeftLongitude) {
        this.bottomLeftLongitude = bottomLeftLongitude;
    }

    public String getBottomRightLatitude() {
        return bottomRightLatitude;
    }

    public void setBottomRightLatitude(String bottomRightLatitude) {
        this.bottomRightLatitude = bottomRightLatitude;
    }

    public String getBottomRightLongitude() {
        return bottomRightLongitude;
    }

    public void setBottomRightLongitude(String bottomRightLongitude) {
        this.bottomRightLongitude = bottomRightLongitude;
    }

    public float getOverlayImageOpacity() {
        return overlayImageOpacity;
    }

    public void setOverlayImageOpacity(float overlayImageOpacity) {
        this.overlayImageOpacity = overlayImageOpacity;
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
}
