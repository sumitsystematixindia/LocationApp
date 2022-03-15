package com.mlins.utils.gis;

import static android.content.ContentValues.TAG;

import android.graphics.PointF;
import android.preference.Preference;
import android.util.Log;

import com.mlins.aStar.GisSegment;
import com.mlins.aStar.NavigationPath;
import com.mlins.aStar.aStarData;
import com.mlins.aStar.aStarMath;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.Cleanable;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.FloorData;
import com.mlins.utils.Lookup;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceDownloader;
import com.mlins.utils.ServerConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.prefs.Preferences;

public class GisData implements Cleanable {

    private int SegmentId = 0;
    String gisdataResult = "";
    List<GisLine> lines = new ArrayList<GisLine>();
    private Map<Integer, List<Integer>> Neighbors = new HashMap<Integer, List<Integer>>();
    private Map<Integer, GisSegment> segmentTable = new HashMap<Integer, GisSegment>();

    public static GisData getInstance() {
        return Lookup.getInstance().get(GisData.class);

    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(GisData.class);
    }

    public void clean() {
        lines.clear();
    }

    public void addGisLine(GisLine l) {
        lines.add(l);
    }

    public void loadGisLines() {
        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        if (facConf == null) {
            return;
        }

        int floor = facConf.getSelectedFloor();
        loadGisLines(floor,"");
    }

    public boolean hasGis() {
        return lines != null && lines.size() > 0;
    }




    public void loadGisLines(int floor , String gisData) {
        try {

            String gisuri="";
            boolean islocal = PropertyHolder.getInstance().isLocal();
            if (!islocal) {

                try {
                    String x1="",y1="",x2="",y2="";
                    Log.d(TAG, "loadGisLines: gisData" + gisData);
                    JSONArray jsonArr = new JSONArray(gisData);
                    lines.clear();
                    for (int j = 0; j < jsonArr.length(); j++) {

                        JSONObject jsonObject = jsonArr.getJSONObject(j);
                            x1 = jsonObject.getString("startX");
                            y1 = jsonObject.getString("startY");
                            x2 = jsonObject.getString("endX");
                            y2 = jsonObject.getString("endY");
                            gisuri = "line\t" + x1 + "\t" + y1 + "\t" + x2 + "\t" + y2 + "\t" + floor + "\ttrue\tmajor\n";


                        GisLine l = new GisLine();
                        l.loadLine(gisuri);

                        if (!(l.point1.getX() == l.point2.getX() && l.point1
                                .getY() == l.point2.getY())) {
                            lines.add(l);
                        }




                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            //    Log.d(TAG, "loadGisLines: " + gisdataResult) ;              ;
//                if (data != null) {
//                    gisuri = ServerConnection.getInstance().translateUrl(
//                            data.gis, facConf.getId());
//                }
//                byte[] bytes = ResourceDownloader.getInstance().getUrl(gisuri);
             //   lines.clear();
                //byte[] bytes = gisdataResult.substring(0, gisdataResult.length()-1).getBytes(StandardCharsets.ISO_8859_1);
//                byte[] bytes = gisdataResult.getBytes(StandardCharsets.ISO_8859_1);
//               // gisdataResult ="         ";
//                Log.d(TAG, "loadGisLines: " + bytes.length) ;
//                ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
//                BufferedReader in = null;
//                if (bytes.length > 0) {
//                    try {
//                        in = new BufferedReader(new InputStreamReader(bin));
//                        String line = null;
//                        while ((line = in.readLine()) != null) {
//                            GisLine l = new GisLine();
//                            l.loadLine(line);
//                            Log.d(TAG, "loadGisLines: line" + l);
//                            if (!(l.point1.getX() == l.point2.getX() && l.point1
//                                    .getY() == l.point2.getY())) {
//
//                                lines.add(l);
//                            }
//                        }
//                        in.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                }
            } else {
                // Get from local copy
                String floordir = PropertyHolder.getInstance().getFacilityDir()
                        + "/" + floor;
                File dir = new File(floordir, "gis");
                String filename = "meller1.txt.line";
                File f = new File(dir, filename);
                BufferedReader inlocal = null;
                if (f.exists()) {
                    try {
                        inlocal = new BufferedReader(new FileReader(f));
                        String line = null;
                        while ((line = inlocal.readLine()) != null) {
                            GisLine l = new GisLine();
                            l.loadLine(line);
                            if (!(l.point1.getX() == l.point2.getX() && l.point1
                                    .getY() == l.point2.getY())) {
                                lines.add(l);
                            }
                        }
                        inlocal.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        loadSegmensMap();

    }

    public void loadSelectedGisLines(int floor) {
        try {
            lines.clear();
            boolean islocal = PropertyHolder.getInstance().isLocal();
            if (!islocal) {
                // Get from server:
                FloorData data = FacilityContainer.getInstance().getSelected()
                        .getFloor(floor);
                String gisuri = "";
                if (data != null) {
                    gisuri = ServerConnection.getInstance().translateUrl(
                            data.gis,
                            FacilityContainer.getInstance().getSelected()
                                    .getId());
                }
                byte[] bytes = ResourceDownloader.getInstance().getUrl(gisuri);
                ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
                BufferedReader in = null;
                if (bytes.length > 0) {
                    try {
                        in = new BufferedReader(new InputStreamReader(bin));
                        String line = null;
                        while ((line = in.readLine()) != null) {
                            GisLine l = new GisLine();
                            l.loadLine(line);
                            if (!(l.point1.getX() == l.point2.getX() && l.point1
                                    .getY() == l.point2.getY())) {
                                lines.add(l);
                            }
                        }
                        in.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            } else {
                // Get from local copy
                String floordir = PropertyHolder.getInstance().getFacilityDir()
                        + "/" + floor;
                File dir = new File(floordir, "gis");
                String filename = "meller1.txt.line";
                File f = new File(dir, filename);
                BufferedReader inlocal = null;
                if (f.exists()) {
                    try {
                        inlocal = new BufferedReader(new FileReader(f));
                        String line = null;
                        while ((line = inlocal.readLine()) != null) {
                            GisLine l = new GisLine();
                            l.loadLine(line);
                            if (!(l.point1.getX() == l.point2.getX() && l.point1
                                    .getY() == l.point2.getY())) {
                                lines.add(l);
                            }
                        }
                        inlocal.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<GisLine> getLines(String facilityid, int floor) {
        List<GisLine> result = new ArrayList<GisLine>();
        try {
            boolean islocal = PropertyHolder.getInstance().isLocal();
            if (!islocal) {
                Campus campus = ProjectConf.getInstance().getSelectedCampus();
                FacilityConf cfacility = campus.getFacilitiesConfMap().get(
                        facilityid);
                // Get from server:
                FloorData data = cfacility.getFloor(floor);
                String gisuri = "";
                if (data != null) {
                    gisuri = ServerConnection.getInstance().translateUrl(
                            data.gis, facilityid);
                }
                byte[] bytes = ResourceDownloader.getInstance().getUrl(gisuri);
                ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
                BufferedReader in = null;
                if (bytes.length > 0) {
                    try {
                        in = new BufferedReader(new InputStreamReader(bin));
                        String line = null;
                        while ((line = in.readLine()) != null) {
                            GisLine l = new GisLine();
                            l.loadLine(line);
                            if (!(l.point1.getX() == l.point2.getX() && l.point1
                                    .getY() == l.point2.getY())) {
                                result.add(l);
                            }
                        }
                        in.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            } else {
                // Get from local copy
                String floordir = PropertyHolder.getInstance().getFacilityDir()
                        + "/" + floor;
                File dir = new File(floordir, "gis");
                String filename = "meller1.txt.line";
                File f = new File(dir, filename);
                BufferedReader inlocal = null;
                if (f.exists()) {
                    try {
                        inlocal = new BufferedReader(new FileReader(f));
                        String line = null;
                        while ((line = inlocal.readLine()) != null) {
                            GisLine l = new GisLine();
                            l.loadLine(line);
                            if (!(l.point1.getX() == l.point2.getX() && l.point1
                                    .getY() == l.point2.getY())) {
                                result.add(l);
                            }
                        }
                        inlocal.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<GisLine> getLines() {
        return lines;

    }

    public GisLine findClosestLine(PointF point) {
        GisLine result = null;
        if (lines.size() > 0) {
            double min = Double.MAX_VALUE;
            for (GisLine l : lines) {
                PointF pl = MathUtils.findClosestPointOnSegment(point, l);
                double distance = MathUtils.distance(pl, point);
                if (distance <= min) {
                    result = l;
                    min = distance;
                }
            }
        }
        return result;
    }

    public PointF findClosestPointOnSegment(PointF p, double heading) {
        if (!PropertyHolder.getInstance().isNavigationState()) {
            return null;
        }

        double distTHR = PropertyHolder.getInstance()
                .getTurnToClosestGisLineTHR();
        double howManyToPick = 5;

        heading += 360;
        heading %= 360;
        PriorityQueue<ProjectedGisLine> gisLinesPQ = new PriorityQueue<ProjectedGisLine>();

        List<GisLine> segments = convertsegmenttolines();
        for (GisLine l : segments) {
            gisLinesPQ.offer(new ProjectedGisLine(l, p));
        }

        if (segments.size() == 0) {
            return null;
        }

        PointF resultPoint = null;

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        double p2m = 1;
        if (facConf != null) {
            p2m = facConf.getPixelsToMeter();
        }
        // get the closest howManyToPick gislines to the point
        ProjectedGisLine nearestline = null;
        for (int i = 0; i < howManyToPick; i++) {
            if (i < gisLinesPQ.size()) {
                ProjectedGisLine proLine = gisLinesPQ.poll();

                if (i == 0) {
                    nearestline = proLine;
                }

                if (proLine.getDistance() / p2m > distTHR) {
                    break;
                }

                double angdiff = Math
                        .abs((proLine.getLine().getAngle() - heading)
                                - PropertyHolder.getInstance()
                                .getTurnToClosestGisLineAngle());
                if (angdiff < 60 || (angdiff > 120 && angdiff < 240)) {
                    return proLine.getClosestPointOnSegment();
                }
            }
        }

        if (resultPoint == null && nearestline != null) {
            float pathtreshhold = PropertyHolder.getInstance()
                    .getCloseLineOnPathThreshold();
            if (nearestline.getDistance() / p2m < pathtreshhold) {
                return nearestline.getClosestPointOnSegment();
            }
        }

        return resultPoint;
    }

    private List<GisLine> convertsegmenttolines() {
        List<GisLine> result = new ArrayList<GisLine>();
        NavigationPath nav = aStarData.getInstance().getCurrentPath();
        if (nav != null) {
            FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
            if (facConf != null) {
                List<List<GisSegment>> paths = nav.getPathByZ(facConf
                        .getSelectedFloor());
                if (paths != null) {
                    for (List<GisSegment> p : paths) {
                        for (GisSegment o : p) {
                            GisLine l = o.getLine();
                            if (l != null) {
                                result.add(l);
                            }
                        }
                    }

                }
            }
        }
        return result;
    }

    public PointF findClosestPointOnLine(PointF p, double heading) {

        // System.out.println(heading);

        double distTHR = PropertyHolder.getInstance()
                .getTurnToClosestGisLineTHR();
        double howManyToPick = 5;

        if (lines.size() == 0) {
            return p;
        }

        heading += 360;
        heading %= 360;
        PriorityQueue<ProjectedGisLine> gisLinesPQ = new PriorityQueue<ProjectedGisLine>();

        for (GisLine l : lines) {
            gisLinesPQ.offer(new ProjectedGisLine(l, p));
        }

        PointF resultPoint = null;
        double p2m = 1;
        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        if (facConf != null) {
            p2m = FacilityContainer.getInstance().getCurrent()
                    .getPixelsToMeter();
        }

        // get the closest howManyToPick gislines to the point
        for (int i = 0; i < howManyToPick; i++) {
            if (i < gisLinesPQ.size()) {
                ProjectedGisLine proLine = gisLinesPQ.poll();
                if (i == 0) {
                    resultPoint = proLine.getClosestPointOnSegment();
                }

                if (proLine.getDistance() / p2m > distTHR) {
                    return resultPoint;
                }
                double angdiff = Math
                        .abs((proLine.getLine().getAngle() - heading)
                                - PropertyHolder.getInstance()
                                .getTurnToClosestGisLineAngle());
                if (angdiff < 60 || (angdiff > 120 && angdiff < 240)) {
                    return proLine.getClosestPointOnSegment();
                }
            }
        }

        return resultPoint;

    }

    public PointF findClosestPointOnLine(PointF p) {
        PointF result = null;
        if (lines.size() == 0) {
            return p;
        }

        double min = 1000000.0;
        for (GisLine l : lines) {
            PointF point = MathUtils.findClosestPointOnSegment(p, l);
            double distance = MathUtils.distance(p, point);
            if (distance <= min) {
                result = point;
                min = distance;
            }

        }

        return result;
    }

    public PointF findClosestPointOnLine(List<GisLine> lineslist, PointF p) {
        PointF result = null;
        if (lineslist.size() == 0) {
            return p;
        }

        double min = 1000000.0;
        for (GisLine l : lineslist) {
            PointF point = MathUtils.findClosestPointOnSegment(p, l);
            double distance = MathUtils.distance(p, point);
            if (distance <= min) {
                result = point;
                min = distance;
            }

        }

        return result;
    }

    public GisLine findGisLine(PointF p1) {
        for (GisLine l : lines) {
            PointF point = MathUtils.findClosestPointOnSegment(p1, l);
            double d = MathUtils.distance(p1, point);
            if (d < 1) {
                return l;
            }
        }
        return null;
    }

    public boolean isLocationsOnSameLine(Location p1, Location p2) {
        boolean result = false;
        if (p1 == null || p2 == null || p1.getZ() != p2.getZ()) {
            result = false;
        } else {
            PointF tmp1 = new PointF((float) p1.getX(), (float) p1.getY());
            PointF tmp2 = new PointF((float) p2.getX(), (float) p2.getY());
            if (tmp1.x == tmp2.x && tmp1.y == tmp2.y) {
                return false;
            }
            GisLine l1 = findGisLine(tmp1);
            GisLine l2 = findGisLine(tmp2);
            if (l1 != null && l2 != null && l1.equals(l2)) {
                result = true;
            }
        }
        return result;
    }

    public void loadSegmensMap() {
        SegmentId = 0;
        Neighbors.clear();
        segmentTable.clear();

        for (GisLine l : lines) {
            if (l.isParticipateInNavigation()) {
                int sid = SegmentId++;
                GisSegment segment = new GisSegment(l, sid);
                segmentTable.put(sid, segment);
            }
        }

        for (GisSegment s1 : segmentTable.values()) {
            List<Integer> n = new ArrayList<Integer>();
            for (GisSegment s2 : segmentTable.values()) {
                if (aStarMath.isNeighbors(s1, s2)) {
                    n.add(s2.getId());
                }
            }

            Neighbors.put(s1.getId(), n);

        }
    }

    // public boolean isOnSameOrNeighborLine(PointF p1, PointF p2) {
    // boolean result = false;
    // GisLine l1 = findGisLine(p1);
    // GisLine l2 = findGisLine(p2);
    // if (l1 != null && l2 != null) {
    // if (l1.equals(l2)) {
    // result = true;
    // } else if (isNeighbors(l1, l2)) {
    // result = true;
    // }
    // }
    // return result;
    // }
    //
    // private boolean isNeighbors(GisLine l1, GisLine l2) {
    // GisPoint s1p1 = l1.getPoint1();
    // GisPoint s1p2 = l1.getPoint2();
    // GisPoint s2p1 = l2.getPoint1();
    // GisPoint s2p2 = l2.getPoint2();
    // if (l1 != l2) {
    // if (findDistance(s1p1, s2p1) < 1 || findDistance(s1p1, s2p2) < 1
    // || findDistance(s1p2, s2p1) < 1
    // || findDistance(s1p2, s2p2) < 1) {
    // return true;
    // }
    // }
    // return false;
    // }
    //
    // public static double findDistance(GisPoint p1, GisPoint p2) {
    // double result = 0;
    // double p1x = p1.getX();
    // double p1y = p1.getY();
    // double p2x = p2.getX();
    // double p2y = p2.getY();
    // result = Math.sqrt((p1x - p2x) * (p1x - p2x) + (p1y - p2y)
    // * (p1y - p2y));
    //
    // return result;
    // }

    public boolean isOnSameOrNeighborsSegment(PointF p1, PointF p2) {
        boolean result = false;
        GisSegment s1 = getSegment(p1);
        GisSegment s2 = getSegment(p2);
        if (s1 != null && s2 != null) {
            if (s1.getId() == s2.getId()) {
                return true;
            }

            List<Integer> nlist = Neighbors.get(s1.getId());
            if (nlist.contains(s2.getId())) {
                PointF intersection = aStarMath.getIntersectionPoint(s1, s2);
                if (intersection != null) {
                    double d = MathUtils.distance(p1, intersection);
                    float thresh = PropertyHolder.getInstance()
                            .getDistanceFromIntersection();
                    FacilityConf fac = FacilityContainer.getInstance()
                            .getCurrent();
                    if (fac != null) {
                        double dinmeters = d / fac.getPixelsToMeter();
                        if (dinmeters < thresh) {
                            result = true;
                        }
                    }
                }
            }

            if (hasCommonShortNeighbors(p1, s1, s2)) {
                result = true;
            }

        }

        return result;
    }

    public boolean isLastSegmentsAreSame(List<GisSegment> lastSegmentsList) {

        boolean isLastSegmentsAreSame = true;

        int threshold = PropertyHolder.getInstance().getSameSegmentThreshold();

        if (lastSegmentsList.size() < threshold) {
            isLastSegmentsAreSame = false;
        } else {

            for (int i = 0; i < lastSegmentsList.size() - 1; i++) {

                GisSegment s1 = lastSegmentsList.get(i);
                GisSegment s2 = lastSegmentsList.get(i + 1);

                if (s1.getId() != s2.getId()) {
                    isLastSegmentsAreSame = false;
                    break;
                }
            }
        }

        return isLastSegmentsAreSame;

    }

    private boolean hasCommonShortNeighbors(PointF p1, GisSegment s1,
                                            GisSegment s2) {
        List<Integer> list1 = Neighbors.get(s1.getId());
        List<Integer> list2 = Neighbors.get(s2.getId());
        GisSegment common = null;
        for (Integer o : list1) {
            if (list2.contains(o)) {
                common = segmentTable.get(o);
            }
        }

        if (common != null) {
            double thresh = PropertyHolder.getInstance()
                    .getSmallSegmentLength();
            double w = common.getWeight();
            FacilityConf fac = FacilityContainer.getInstance().getCurrent();
            if (fac != null) {
                double winmeters = w / fac.getPixelsToMeter();
                if (winmeters < thresh) {
                    PointF intersection = aStarMath.getIntersectionPoint(s1,
                            common);
                    if (intersection != null) {
                        double d = MathUtils.distance(p1, intersection);
                        float dthresh = PropertyHolder.getInstance()
                                .getDistanceFromIntersection();
                        double dinmeters = d / fac.getPixelsToMeter();
                        if (dinmeters < dthresh) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public GisSegment getSegment(PointF p) {
        for (GisSegment s : segmentTable.values()) {
            double d = distancefromsegment(p, s);
            if (d == 0) {
                return s;
            }
        }
        return null;
    }

    private double distancefromsegment(PointF p, GisSegment s) {
        double result = 0;
        GisPoint p1 = new GisPoint(p.x, p.y, s.getLine().getZ());
        GisPoint p2 = aStarMath.findClosePointOnSegment(p1, s);
        result = aStarMath.findDistance(p1, p2);
        if (result < 1) {
            result = 0;
        }
        return result;
    }

    private class ProjectedGisLine implements Comparable<ProjectedGisLine> {
        private GisLine line = null;
        private PointF closestPointOnSegment = null;
        private Double distance;

        public ProjectedGisLine(GisLine line, PointF p) {
            super();
            this.line = line;
            closestPointOnSegment = MathUtils.findClosestPointOnSegment(p,
                    this.line);
            distance = MathUtils.distance(p, closestPointOnSegment);
        }

        @Override
        public int compareTo(ProjectedGisLine other) {

            return distance.compareTo(other.distance);
        }

        public GisLine getLine() {
            return line;
        }

        public PointF getClosestPointOnSegment() {
            return closestPointOnSegment;
        }

        public Double getDistance() {
            return distance;
        }

    }

}
