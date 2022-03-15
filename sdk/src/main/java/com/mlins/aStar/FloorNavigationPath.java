package com.mlins.aStar;

import com.mlins.instructions.NavInstruction;
import com.spreo.nav.interfaces.INavInstruction;

import java.util.ArrayList;
import java.util.List;

public class FloorNavigationPath {
    private double Z = -100;
    private List<GisSegment> path = new ArrayList<GisSegment>();
    private String CampusId = null;
    private String facilityId = null;
    private NavInstruction simplifiedInstruction = null;



    public FloorNavigationPath() {

    }

    public FloorNavigationPath(double floor, List<GisSegment> p) {
        Z = floor;
        path = p;
    }

    public double getZ() {
        return Z;
    }

    public void setZ(double z) {
        Z = z;
    }

    public List<GisSegment> getPath() {
        return path;
    }

    public void setPath(List<GisSegment> path) {
        this.path = path;
    }

    public String getCampusId() {
        return CampusId;
    }

    public void setCampusId(String campusId) {
        CampusId = campusId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public NavInstruction getSimplifiedInstruction() {
        return simplifiedInstruction;
    }

    public void setSimplifiedInstruction(NavInstruction simplifiedInstruction) {
        this.simplifiedInstruction = simplifiedInstruction;
    }
}
