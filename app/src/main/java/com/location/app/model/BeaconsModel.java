package com.location.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BeaconsModel {


    @SerializedName("id")
    @Expose
    private Integer id;


    @SerializedName("floor_id")
    @Expose
    private Integer floor_id;

    @SerializedName("beacon_id")
    @Expose
    private String beacon_id;
    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("lons")
    @Expose
    private String lons;

    @SerializedName("TxPower")
    @Expose
    private Integer TxPower;
    @SerializedName("major")
    @Expose
    private Integer major;
    @SerializedName("minor")
    @Expose
    private Integer minor;
    @SerializedName("interval")
    @Expose
    private Long interval;
    @SerializedName("x")
    @Expose
    private String x;
    @SerializedName("y")
    @Expose
    private String y;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFloor_id() {
        return floor_id;
    }

    public void setFloor_id(Integer floor_id) {
        this.floor_id = floor_id;
    }

    public String getBeacon_id() {
        return beacon_id;
    }

    public void setBeacon_id(String beacon_id) {
        this.beacon_id = beacon_id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLons() {
        return lons;
    }

    public void setLons(String lons) {
        this.lons = lons;
    }

    public Integer getTxPower() {
        return TxPower;
    }

    public void setTxPower(Integer txPower) {
        TxPower = txPower;
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
