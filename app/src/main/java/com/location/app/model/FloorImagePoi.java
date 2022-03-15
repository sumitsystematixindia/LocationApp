package com.location.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FloorImagePoi {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("floor_id")
    @Expose
    private Integer floorId;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("icon_id")
    @Expose
    private Integer iconId;
    @SerializedName("poi_name")
    @Expose
    private String poiName;
    @SerializedName("poi_title")
    @Expose
    private String poiTitle;
    @SerializedName("floor_index")
    @Expose
    private Integer floorIndex;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("x")
    @Expose
    private Integer x;
    @SerializedName("y")
    @Expose
    private Integer y;
    @SerializedName("poisettings")
    @Expose
    private String poisettings;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("keywords")
    @Expose
    private String keywords;
    @SerializedName("poiPhone1")
    @Expose
    private String poiPhone1;
    @SerializedName("poiPhone2")
    @Expose
    private String poiPhone2;
    @SerializedName("poiPhoneHours")
    @Expose
    private String poiPhoneHours;
    @SerializedName("poiContactEmails")
    @Expose
    private String poiContactEmails;
    @SerializedName("poiUrl")
    @Expose
    private String poiUrl;
    @SerializedName("poiUrlTitle")
    @Expose
    private String poiUrlTitle;
    @SerializedName("poiActiveHours")
    @Expose
    private String poiActiveHours;
    @SerializedName("poiMediaUrl")
    @Expose
    private String poiMediaUrl;
    @SerializedName("poiOfficeDetails")
    @Expose
    private String poiOfficeDetails;
    @SerializedName("status")
    @Expose
    private String status;
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getIconId() {
        return iconId;
    }

    public void setIconId(Integer iconId) {
        this.iconId = iconId;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getPoiTitle() {
        return poiTitle;
    }

    public void setPoiTitle(String poiTitle) {
        this.poiTitle = poiTitle;
    }

    public Integer getFloorIndex() {
        return floorIndex;
    }

    public void setFloorIndex(Integer floorIndex) {
        this.floorIndex = floorIndex;
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

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String getPoisettings() {
        return poisettings;
    }

    public void setPoisettings(String poisettings) {
        this.poisettings = poisettings;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getPoiPhone1() {
        return poiPhone1;
    }

    public void setPoiPhone1(String poiPhone1) {
        this.poiPhone1 = poiPhone1;
    }

    public String getPoiPhone2() {
        return poiPhone2;
    }

    public void setPoiPhone2(String poiPhone2) {
        this.poiPhone2 = poiPhone2;
    }

    public String getPoiPhoneHours() {
        return poiPhoneHours;
    }

    public void setPoiPhoneHours(String poiPhoneHours) {
        this.poiPhoneHours = poiPhoneHours;
    }

    public String getPoiContactEmails() {
        return poiContactEmails;
    }

    public void setPoiContactEmails(String poiContactEmails) {
        this.poiContactEmails = poiContactEmails;
    }

    public String getPoiUrl() {
        return poiUrl;
    }

    public void setPoiUrl(String poiUrl) {
        this.poiUrl = poiUrl;
    }

    public String getPoiUrlTitle() {
        return poiUrlTitle;
    }

    public void setPoiUrlTitle(String poiUrlTitle) {
        this.poiUrlTitle = poiUrlTitle;
    }

    public String getPoiActiveHours() {
        return poiActiveHours;
    }

    public void setPoiActiveHours(String poiActiveHours) {
        this.poiActiveHours = poiActiveHours;
    }

    public String getPoiMediaUrl() {
        return poiMediaUrl;
    }

    public void setPoiMediaUrl(String poiMediaUrl) {
        this.poiMediaUrl = poiMediaUrl;
    }

    public String getPoiOfficeDetails() {
        return poiOfficeDetails;
    }

    public void setPoiOfficeDetails(String poiOfficeDetails) {
        this.poiOfficeDetails = poiOfficeDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
