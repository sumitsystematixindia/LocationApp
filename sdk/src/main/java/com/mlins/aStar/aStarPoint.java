//AddCatch
package com.mlins.aStar;

import com.mlins.utils.gis.GisPoint;

import java.util.ArrayList;
import java.util.List;

class aStarPoint extends GisPoint {

    private List<GisSegment> segments = new ArrayList<>();

    aStarPoint(GisPoint p, aStarSegment segment) {
        this(p);
        segments.add(segment);
    }

    public aStarPoint(GisPoint p) {
        super(p);
    }

    void addSegment(GisSegment gisSegment) {
        segments.add(gisSegment);
    }

    public List<GisSegment> getSegments() {
        return segments;
    }
}
