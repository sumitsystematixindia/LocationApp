//AddCatchAdded
package com.mlins.aStar;

import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.GisPoint;
import com.mlins.utils.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class aStarAlgorithm {
    private static final String TAG = aStarAlgorithm.class.getName();
    public Map<Integer, GisSegment> parents = new HashMap<Integer, GisSegment>();
    List<GisSegment> open = new ArrayList<GisSegment>();
    List<GisSegment> closed = new ArrayList<GisSegment>();
    private aStarPoint startPoint;
    private aStarPoint endPoint;
    private GisPoint realStart;
    private GisPoint realEnd;

    public aStarAlgorithm(GisPoint s, GisPoint e) {
        realStart = s;
        realEnd = e;
        startPoint = aStarMath.findPoint(s);
        endPoint = aStarMath.findPoint(e);
    }

    public List<GisSegment> getPath() {

        System.out.println("Start A*");

        List<GisSegment> path = new ArrayList<GisSegment>();
        // Collections.sort(open, new CustomComparator());
        GisSegment startSegment = findStartLowF(getStartPoint().getSegments(),
                getEndPoint());
        System.out.println("Stat Segment ID:" + startSegment.getId());

        open.add(startSegment);
        // List<GisSegment> neighbors =
        // aStarData.getInstance().Neighbors.get(startSegment.getId());
        // open.addAll(neighbors);
        //
        //
        // for (GisSegment s : neighbors) {
        // s.setParent(startSegment.getId());
        // parents.put(s.getId(), startSegment);
        // }
        System.out.println("Begine Parents:" + parents);

        // open.remove(startSegment);
        // closed.add(startSegment);

        System.out.println("Begine open:" + open);
        System.out.println("Begine closed:" + closed);

        path = buildpaths(startSegment);
        // aStarData.getInstance().cleanAStar();
        return path;

    }

    private List<GisSegment> buildpaths(GisSegment startSegment) {
        Log.getInstance().debug(TAG,
                "Enter, List<GisSegment> buildpaths(GisSegment startSegment)");
        List<GisSegment> minpath = new ArrayList<GisSegment>();
        GisSegment Segment = startSegment;
        try {
            while (!(open.size() == 0)) {
                Segment = findLowF(open, getEndPoint());

                System.out.println("Find Next Candidate ID:" + Segment.getId());
                if (aStarMath.calcH(Segment, endPoint) == 0) {
                    System.out.println("We are at the END :-)");

                    // done return path
                    minpath.add(Segment);
                    GisSegment currentsegment = Segment;
                    GisSegment psegment = currentsegment;
                    while (currentsegment != startSegment) {
                        psegment = parents.get(currentsegment.getId());
                        GisPoint p1 = currentsegment.getLine().getPoint1();
                        GisPoint p2 = currentsegment.getLine().getPoint2();
                        GisPoint startp = getStartPoint();
                        // if((psegment == null) || (p1 == startp) || (p2 ==
                        // startp)) {
                        if ((psegment == null)
                                || (aStarMath.findDistance(p1, startp) == 0)
                                || (aStarMath.findDistance(p2, startp) == 0)) {
                            break;
                        }
                        minpath.add(0, psegment);
                        currentsegment = psegment;
                    }
                    System.out.println("!!!!!!!!!!!!!!!!!!");
                    System.out.println("!!!!!!!!!!!!!!!!!!");
                    System.out.println("!!!!!!!!!!!!!!!!!!");
                    System.out.println("Best path");
                    System.out.println(minpath);
                    System.out.println("!!!!!!!!!!!!!!!!!!");
                    System.out.println("!!!!!!!!!!!!!!!!!!");

                    minpath = setSegmentsDirection(minpath);

                    System.out.println("fix direction");
                    System.out.println(minpath);

                    //minpath = fixEdges(minpath);
                    return minpath;

                }
                // else if (open.size() == 0) {
                // // done return null
                // minpath = null;
                // }
                // else {
                open.remove(Segment);
                closed.add(Segment);
                List<GisSegment> neighbors = Segment.getNeighbours();
                System.out.println("Iterating throught neighbors: ");
                System.out.println(neighbors);
                // open.addAll(neighbors);
                for (GisSegment s : neighbors) {
                    double g = s.getG();
                    double tentative_g_score = Segment.getG() + s.getWeight();
                    if (closed.contains(s) && tentative_g_score >= g) {
                        continue;
                        // s.setG(currentg);
                        // s.setParent(Segment.getId());
                        // parents.put(s.getId(), Segment);
                    } else if (!open.contains(s) || tentative_g_score < g) {
                        s.setG(tentative_g_score);

                        s.setParent(Segment.getId());
                        parents.put(s.getId(), Segment);

                        if (!open.contains(s)) {
                            open.add(s);
                            // s.setG(g);

                        }
                    }
                    // s.setParent(Segment.getId());
                    // parents.put(s.getId(), Segment);
                }
                // }

            }
            Log.getInstance()
                    .debug(TAG,
                            "Exit, List<GisSegment> buildpaths(GisSegment startSegment)");
        } catch (Throwable t) {

            Log.getInstance().error(TAG, t.getMessage(), t);
        }

        return minpath;

        // GisSegment newsegment = findLowF(neighbors, getEndPoint());

    }

    private GisSegment findLowF(List<GisSegment> open, aStarPoint epoint) {

        double minf = 10000000;
        GisSegment segment = null;
        GisSegment parent = null;
        try {
            for (GisSegment s : open) {
                Log.getInstance()
                        .debug(TAG,
                                "Enter, findLowF(List<GisSegment> open, aStarPoint epoint)");
                parent = parents.get(s.getId());
                double g = 0;
                if (parent != null) {
                    g = parent.getG() + s.getWeight();
                } else {
                    g = s.getWeight();
                }

                // s.setG(g);
                double h = aStarMath.calcH(s, epoint);
                double f = g + h;
                if (f < minf) {
                    minf = f;
                    segment = s;
                }
                Log.getInstance()
                        .debug(TAG,
                                "Exit, findLowF(List<GisSegment> open, aStarPoint epoint)");
            }
        } catch (Throwable t) {

            Log.getInstance().error(TAG, t.getMessage(), t);
        }

        return segment;
    }

    private GisSegment findStartLowF(List<GisSegment> segments,
                                     aStarPoint epoint) {
        double minf = 10000000;
        GisSegment segment = null;
        try {
            Log.getInstance()
                    .debug(TAG,
                            "Enter, GisSegment findStartLowF(List<GisSegment> segments)");
            for (GisSegment s : segments) {
                double g = s.getWeight();
                s.setG(g);
                double h = aStarMath.calcH(s, epoint);
                double f = g + h;
                if (f < minf) {
                    minf = f;
                    segment = s;
                }
                Log.getInstance()
                        .debug(TAG,
                                "Exit, GisSegment findStartLowF(List<GisSegment> segments)");
            }
        } catch (Throwable t) {

            Log.getInstance().error(TAG, t.getMessage(), t);
        }
        return segment;
    }

    private List<GisSegment> fixEdges(List<GisSegment> path) {
        List<GisSegment> result = path;
        GisSegment starts = aStarMath.findCloseSegment(realStart);
        GisSegment ends = aStarMath.findCloseSegment(realEnd);
        GisPoint startp = aStarMath.findClosePointOnSegment(realStart, starts);
        GisPoint endp = aStarMath.findClosePointOnSegment(realEnd, ends);
        boolean samefakepoint = isSameFakePoint();
        if (!starts.equals(result.get(0))) {
            if (samefakepoint && result.size() == 1) {
                result.clear();
            }
            result.add(0, starts);
        }
//		if (result.size() == 1) {
//			GisLine l = new GisLine(startp, endp, result.get(0).getLine().getZ());
//			result.get(0).setLine(l);
//			result.get(0).setWeight(result.get(0).calcweight());
//		}

        if (!ends.equals(result.get(result.size() - 1))) {
            ends.setParent(result.get(result.size() - 1).getId());
            parents.put(ends.getId(), result.get(result.size() - 1));
            result.add(result.size(), ends);
        }
        result = setSegmentsDirection(result);


//		GisLine l1 = null;
//		if (result.size() == 2) {
//			l1 = new GisLine(startp, result.get(0).getLine().getPoint2(),  result.get(0).getLine().getZ());
//		} else {
//			l1 = new GisLine(startp, result.get(1).getLine().getPoint1(),  result.get(0).getLine().getZ());
//		}


        if (result.size() > 1) {
            GisLine l1 = new GisLine(startp, result.get(1).getLine().getPoint1(), result.get(0).getLine().getZ());
//			GisLine l1 = new GisLine(startp, result.get(0).getLine().getPoint2(),  result.get(0).getLine().getZ()); 
//			result.get(0).getLine().setPoint2(result.get(1).getLine().getPoint1());
//			result.get(0).getLine().setPoint1(startp);
            result.get(0).setLine(l1);
            result.get(0).setWeight(result.get(0).calcweight());

            GisLine l2 = new GisLine(result.get(result.size() - 2).getLine()
                    .getPoint2(), endp, result.get(result.size() - 1).getLine()
                    .getZ());
            //		result.get(result.size() - 1).getLine()
            //				.setPoint1(result.get(result.size() - 2).getLine().getPoint2());
            //		result.get(result.size() - 1).getLine().setPoint2(endp);
            result.get(result.size() - 1).setLine(l2);
            result.get(result.size() - 1).setWeight(
                    result.get(result.size() - 1).calcweight());
        } else if (result.size() == 1) {
            GisLine l = new GisLine(startp, endp, result.get(0).getLine().getZ());
            result.get(0).setLine(l);
            result.get(0).setWeight(result.get(0).calcweight());
        }
        return result;
    }

    private boolean isSameFakePoint() {
        boolean result = false;
        if (startPoint.getX() == endPoint.getX() && startPoint.getY() == endPoint.getY()) {
            result = true;
        }
        return result;
    }

    private List<GisSegment> setSegmentsDirection(List<GisSegment> minpath) {
        List<GisSegment> result = new ArrayList<GisSegment>();
        try {
            Log.getInstance()
                    .debug(TAG,
                            "Enter, List<GisSegment> setSegmentsDirection(List<GisSegment> minpath)");
            for (GisSegment s : minpath) {
                GisPoint s1 = s.getLine().getPoint1();
                GisPoint s2 = s.getLine().getPoint2();
                if (parents.get(s.getId()) != null) {
                    GisSegment parent = parents.get(s.getId());
                    GisPoint p1 = parent.getLine().getPoint1();
                    GisPoint p2 = parent.getLine().getPoint2();
                    if ((aStarMath.findDistance(s2, p1) == 0)
                            || (aStarMath.findDistance(s2, p2) == 0)) {
                        s.getLine().setPoint1(s2);
                        s.getLine().setPoint2(s1);
                    }
                } else {
                    GisPoint start1 = getStartPoint();
                    if ((aStarMath.findDistance(s2, start1) == 0)) {
                        s.getLine().setPoint1(s2);
                        s.getLine().setPoint2(s1);
                    }
                }
                result.add(s);
                Log.getInstance()
                        .debug(TAG,
                                "Exit, List<GisSegment> setSegmentsDirection(List<GisSegment> minpath)");
            }
        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);
        }
        return result;
    }

    // public class CustomComparator implements Comparator<GisSegment>
    // {
    // @Override
    // public int compare(GisSegment o1, GisSegment o2) {
    // Integer w1 = (int) o1.getWeight();
    // int w2 = (int) o2.getWeight();
    // return w1.compareTo(w2);
    // }
    // }

    public aStarPoint getStartPoint() {
        return startPoint;
    }



    public aStarPoint getEndPoint() {
        return endPoint;
    }


    // private List<GisSegment> buildpaths(GisSegment startSegment) {
    // ArrayList<GisSegment> minpath = new ArrayList<GisSegment>();
    // GisSegment Segment = startSegment;
    // while (!(aStarMath.calcH(Segment, endPoint) == 0)) {
    // Segment = findLowF(open, getEndPoint());
    //
    // System.out.println("Find Next Candidate ID:" + Segment.getId());
    // if (aStarMath.calcH(Segment, endPoint) == 0) {
    // System.out.println("We are at the END :-)");
    //
    // //done return path
    // minpath.add(Segment);
    // GisSegment currentsegment = Segment;
    // GisSegment psegment = currentsegment;
    // while(currentsegment != startSegment){
    // psegment = parents.get(currentsegment.getId());
    // if(psegment == null)
    // break;
    // minpath.add(0, psegment);
    // currentsegment = psegment;
    // }
    // System.out.println("!!!!!!!!!!!!!!!!!!");
    // System.out.println("!!!!!!!!!!!!!!!!!!");
    // System.out.println("!!!!!!!!!!!!!!!!!!");
    // System.out.println("Best path");
    // System.out.println(minpath);
    // System.out.println("!!!!!!!!!!!!!!!!!!");
    // System.out.println("!!!!!!!!!!!!!!!!!!");
    // return minpath;
    // } else if (open.size() == 0) {
    // // done return null
    // minpath = null;
    // }
    // else {
    // open.remove(Segment);
    // closed.add(Segment);
    // List<GisSegment> neighbors =
    // aStarData.getInstance().Neighbors.get(Segment.getId());
    // System.out.println("Iterating throught neighbors: ");
    // System.out.println(neighbors);
    // // open.addAll(neighbors);
    // for (GisSegment s : neighbors) {
    // double g = s.getG();
    // double tentative_g_score = Segment.getG() + s.getWeight();
    // if (closed.contains(s) && tentative_g_score >= g ) {
    // continue;
    // // s.setG(currentg);
    // // s.setParent(Segment.getId());
    // // parents.put(s.getId(), Segment);
    // } else if (!open.contains(s) || tentative_g_score < g){
    // s.setG(tentative_g_score);
    //
    // s.setParent(Segment.getId());
    // parents.put(s.getId(), Segment);
    //
    // if (!open.contains(s))
    // {
    // open.add(s);
    // // s.setG(g);
    //
    // }
    // }
    // // s.setParent(Segment.getId());
    // // parents.put(s.getId(), Segment);
    // }
    // }
    //
    // }
    //
    // return minpath;
    //
    // // GisSegment newsegment = findLowF(neighbors, getEndPoint());
    //
    // }
    //AllNullHandled

}
