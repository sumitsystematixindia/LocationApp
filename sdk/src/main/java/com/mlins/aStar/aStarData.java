//AddCatch
package com.mlins.aStar;

import android.graphics.PointF;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PoiData;
import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.GisPoint;
import com.mlins.utils.gis.Location;
import com.mlins.utils.logging.Log;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.List;

public class aStarData implements Cleanable {

    private static final String TAG = aStarData.class.getName();

    public ArrayList<aStarSegment> segmentTable = new ArrayList<>();
    public Location poilocation = null;
    ILocation finalDestination = null;
    private PointF myLocation = null;
    private NavigationPath currentPath = null;
    private CampusNavigationPath currentCampusPath = null;
    private Location Destination = null;
    private PoiData internalDestination = null;
    private PoiData externalDestination = null;
    private PoiData externalPoi = null;
    private List<IPoi> multiNavigationPois = null;

    public static aStarData getInstance() {
        aStarData instance = null;
        try {
            Log.getInstance().debug(TAG, "Enter, aStarData getInstance()");
            instance = Lookup.getInstance().get(aStarData.class);
            Log.getInstance().debug(TAG, "Exit, aStarData getInstance()");
        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);
        }
        return instance;
    }

    public static void releaseInstance() {
        Log.getInstance().debug(TAG, "Exit, releaseInstance()");
        try {
            Lookup.getInstance().remove(aStarData.class);
            Log.getInstance().debug(TAG, "Exit, releaseInstance()");
        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);
        }
    }

    public static GisLine findCloseLine(List<GisLine> linesList, GisPoint p1) {

        GisPoint point = null;
        GisLine line = null;
        double distance = 0;
        double mindistance = 1000000;
        try {
            for (GisLine s : linesList) {
                point = aStarMath.findClosePointOnLine(p1, s);
                distance = aStarMath.findDistance(p1, point);
                if (distance < mindistance) {
                    line = s;
                    mindistance = distance;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return line;
    }

    public ILocation getFinalDestination() {
        return finalDestination;
    }

    public void setFinalDestination(Location finalDestination) {
        this.finalDestination = finalDestination;
    }

    public void clean() {
        segmentTable.clear();
    }

    public void loadData(GisPoint start, GisPoint end, List<GisLine> lines) {

//		SegmentId = 0;

        List<GisLine> linesList = new ArrayList<GisLine>();
        for (GisLine l : lines) {
            if (l != null && l.isParticipateInNavigation()) {
                GisLine line = new GisLine();
                line.setLine(l.getPoint1(), l.getPoint2(), l.getZ());
                linesList.add(line);
            }
        }


        //find the segments of start and end
        GisLine startSegment = findCloseLine(linesList, start);

        // Projection of start point on gis line
        PointF startP = MathUtils.findClosestPointOnSegment(start.asPointF(), startSegment);
        start.setX(startP.x);
        start.setY(startP.y);

        // split start line into two lines

        GisLine startLine = startSegment;
        GisLine startLine1 = new GisLine();
        startLine1.setLine(startLine.getPoint1(), start, startLine.getZ());
        GisLine startLine2 = new GisLine();
        startLine2.setLine(start, startLine.getPoint2(), startLine.getZ());

        linesList.remove(startSegment);

        linesList.add(startLine1);
        linesList.add(startLine2);


        GisLine endSegment = findCloseLine(linesList, end);
        // Projection of end point on gis line
        PointF endP = MathUtils.findClosestPointOnSegment(end.asPointF(), endSegment);
        end.setX(endP.x);
        end.setY(endP.y);

        GisLine endLine = endSegment;

        GisLine endLine1 = new GisLine();
        endLine1.setLine(endLine.getPoint1(), end, endLine.getZ());

        GisLine endLine2 = new GisLine();
        endLine2.setLine(end, endLine.getPoint2(), endLine.getZ());
        linesList.remove(endSegment);
        linesList.add(endLine1);
        linesList.add(endLine2);

        aStarSegment.buildSegmentsGraph(segmentTable, linesList);
    }

    public Location getPoilocation() {
        return poilocation;
    }

    public void setPoilocation(Location poilocation) {
        this.poilocation = poilocation;
    }

    public PointF getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(PointF myLocation) {
        this.myLocation = myLocation;
    }

    public NavigationPath getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(NavigationPath currentPath) {
        this.currentPath = currentPath;
    }

    public void cleanAStar() {
        segmentTable.clear();
        // SegmentId = 0;
    }

    public Location getDestination() {
        return Destination;
    }

    public void setDestination(Location destination) {
        Destination = destination;
    }

    public CampusNavigationPath getCurrentCampusPath() {
        return currentCampusPath;
    }

    public void setCurrentCampusPath(CampusNavigationPath currentCampusPath) {
        this.currentCampusPath = currentCampusPath;
    }

    public PoiData getInternalDestination() {
        return internalDestination;
    }

    public void setInternalDestination(PoiData internalDestination) {
        this.internalDestination = internalDestination;
    }

    public PoiData getExternalDestination() {
        return externalDestination;
    }

    public void setExternalDestination(PoiData externalDestination) {
        this.externalDestination = externalDestination;
    }

    public PoiData getExternalPoi() {
        return externalPoi;
    }

    public void setExternalPoi(PoiData externalPoi) {
        this.externalPoi = externalPoi;
    }

    public List<IPoi> getMultiNavigationPois() {
        return multiNavigationPois;
    }

    public void setMultiNavigationPois(List<IPoi> multiNavigationPois) {
        this.multiNavigationPois = multiNavigationPois;
    }
}
//AddCatchAdded
//AllNullHandled
