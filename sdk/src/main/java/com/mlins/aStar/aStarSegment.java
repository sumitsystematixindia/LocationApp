package com.mlins.aStar;

import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.GisPoint;

import java.util.ArrayList;
import java.util.List;

class aStarSegment extends GisSegment {

    private static int aStarSegmentID;

    final aStarPoint starPoint1;
    final aStarPoint starPoint2;

    private aStarSegment(GisLine l, List<aStarSegment> possibleNeigbours) {
        super(l, aStarSegmentID++);

        GisPoint p1 = l.point1;
        GisPoint p2 = l.point2;

        setWeight(p1.distanceTo(p2));

        //Variables for pre-calculation, want to have final starPoint* fields
        aStarPoint starPoint1 = null;
        aStarPoint starPoint2 = null;

        for (aStarSegment possibleNeigbour : possibleNeigbours) {
            aStarPoint possibleNeigbourPoint1 = possibleNeigbour.starPoint1;
            aStarPoint possibleNeigbourPoint2 = possibleNeigbour.starPoint2;

            if(starPoint1 == null) {
                if(possibleNeigbourPoint1.sameAs(p1)) {
                    //android.util.Log.d(TAG, "segment " + possibleNeigbour.getId() + " setted as neighbor for : " + sid);
                    starPoint1 = possibleNeigbourPoint1;
                    starPoint1.addSegment(this);
                }

                if(possibleNeigbourPoint2.sameAs(p1)){
                    //android.util.Log.d(TAG, "segment " + possibleNeigbour.getId() + " setted as neighbor for : " + sid);
                    starPoint1 = possibleNeigbourPoint2;
                    starPoint1.addSegment(this);
                }
            }

            if(starPoint2 == null) {
                if(possibleNeigbourPoint1.sameAs(p2)) {
                    //android.util.Log.d(TAG, "segment " + possibleNeigbour.getId() + " setted as neighbor for : " + sid);
                    starPoint2 = possibleNeigbourPoint1;
                    starPoint2.addSegment(this);
                }

                if(possibleNeigbourPoint2.sameAs(p2)) {
                    //android.util.Log.d(TAG, "segment " + possibleNeigbour.getId() + " setted as neighbor for : " + sid);
                    starPoint2 = possibleNeigbourPoint2;
                    starPoint2.addSegment(this);
                }
            }

            if(starPoint1 != null && starPoint2 != null)
                break;
        }

        if(starPoint1 == null) {
            starPoint1 = new aStarPoint(p1, this);
        }

        if(starPoint2 == null) {
            starPoint2 = new aStarPoint(p2, this);
        }

        this.starPoint1 = starPoint1;
        this.starPoint2 = starPoint2;
    }

    //method is overridden to improve aStar graph calculation speed (by avoiding extra getLine().getPoint().getX() methods calls)
    @Override
    public double calcweight() {
        //disabling weight calc from superClass constructornike
        if(starPoint1 == null || starPoint2 == null)
            return 0;

        return starPoint1.distanceTo(starPoint2);
    }

    List<GisSegment> getNeighbours() {
        if(starPoint1 == null && starPoint2 == null)
            return null;

        List<GisSegment> point1Part = starPoint1.getSegments();
        List<GisSegment> point2Part = starPoint2.getSegments();

        List<GisSegment> result = new ArrayList<>(point1Part.size() + point2Part.size());
        result.addAll(point1Part);
        result.addAll(point2Part);

        while(result.remove(this));

        return result;
    }


    /**
     * Prepares data structure for aStarAlgorithm class by creating "aStarSegment" segments
     * for each line passed in lines parameter and joining them together in a single graph.
     * Puts all created segments in ArrayList specified by segmentsGraph parameter.
     *
     * @param segmentsGraph array list to put all segments from the graph
     * @param lines source line for the graph
     */

    static void buildSegmentsGraph(ArrayList<aStarSegment> segmentsGraph, List<GisLine> lines){
        segmentsGraph.clear();
        segmentsGraph.ensureCapacity(lines.size());
        for (GisLine l : lines) {
            if (l.isParticipateInNavigation()) {
                aStarSegment segment = new aStarSegment(l, segmentsGraph);
                segmentsGraph.add(segment);
            }
        }
    }

}
