package com.location.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class IndoorPathway {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("floor_id")
@Expose
private Integer floorId;
@SerializedName("weight_multiplier")
@Expose
private Integer weightMultiplier;
@SerializedName("staff_only")
@Expose
private String staffOnly;
@SerializedName("handicap_accessible")
@Expose
private String handicapAccessible;
@SerializedName("latLons")
@Expose
private String latLons;
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

public Integer getWeightMultiplier() {
return weightMultiplier;
}

public void setWeightMultiplier(Integer weightMultiplier) {
this.weightMultiplier = weightMultiplier;
}

public String getStaffOnly() {
return staffOnly;
}

public void setStaffOnly(String staffOnly) {
this.staffOnly = staffOnly;
}

public String getHandicapAccessible() {
return handicapAccessible;
}

public void setHandicapAccessible(String handicapAccessible) {
this.handicapAccessible = handicapAccessible;
}

public String getLatLons() {
return latLons;
}

public void setLatLons(String latLons) {
this.latLons = latLons;
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
