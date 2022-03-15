package com.mlins.aStar;

import com.mlins.instructions.NavInstruction;

import java.util.ArrayList;
import java.util.List;

public class CampusNavigationPath {
    private List<GisSegment> path = new ArrayList<GisSegment>();
    private NavInstruction simplifiedInstruction = null;

    public CampusNavigationPath(List<GisSegment> p) {
        path = p;
    }

    public List<GisSegment> getPath() {
        return path;
    }

    public void setPath(List<GisSegment> path) {
        this.path = path;
    }

    public void addPath(List<GisSegment> path) {
        if (this.path == null) {
            this.path = new ArrayList<GisSegment>();
        }
        this.path.addAll(path);
    }

    public NavInstruction getSimplifiedInstruction() {
        return simplifiedInstruction;
    }

    public void setSimplifiedInstruction(NavInstruction simplifiedInstruction) {
        this.simplifiedInstruction = simplifiedInstruction;
    }
}
