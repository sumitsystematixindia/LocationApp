package simulation;

import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.aStar.CampusNavigationPath;
import com.mlins.aStar.FloorNavigationPath;
import com.mlins.aStar.GisSegment;
import com.mlins.aStar.NavigationPath;
import com.mlins.aStar.aStarAlgorithm;
import com.mlins.aStar.aStarData;
import com.mlins.dualmap.DestinationPoi;
import com.mlins.dualmap.RouteCalculationHelper;
import com.mlins.locationutils.LocationFinder;
import com.mlins.navigation.PathCalculator;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PoiData;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.GisData;
import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.GisPoint;
import com.mlins.utils.gis.Location;
import com.mlins.utils.logging.Log;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.List;

import gps.CampusGisData;

public class SimulationPlayer implements Cleanable{

    private final static String TAG = "simulation.SimulationPlayer";

    SimulationThread pThread;
    int mCounter = 0;
    List<Location> data = new ArrayList<Location>();
    private boolean repeatData = false;

    public static SimulationPlayer getInstance() {
        return Lookup.getInstance().get(SimulationPlayer.class);
    }

    public void clean(){
        if (pThread != null) {
            pThread.mRunning = false;
        }
        stopPlaying();
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(SimulationPlayer.class);
    }

    public void load(NavigationPath navpath, String campusid, String facilityid) {
        Log.getInstance().debug(TAG, "Enter, NavigationPath navpath()");

        repeatData = false;

        List<Location> locations = divideFullPath(navpath);
        if (pThread != null) {
            pThread.setRunning(false);
        }

        initThread();

        data.clear();

        for (Location o : locations) {
            o.setCampusId(campusid);
            o.setFacilityId(facilityid);
            o.setType(LocationFinder.INDOOR_MODE);
            data.add(o);
        }

        pThread.setPlayingData(data);
        pThread.setRunning(true);
        pThread.resetPlayData();
        Log.getInstance().debug(TAG, "Exit, NavigationPath navpath()");
    }

    public void load(NavigationPath navpath, String campusid, String facilityid, Location origin, IPoi poi) {
        Log.getInstance().debug(TAG, "Enter, NavigationPath navpath()");


        if (pThread != null) {
            pThread.setRunning(false);
        }

        initThread();

        data.clear();

        if (origin != null && poi != null) {
            try {
                if (origin.getLocationType() == LocationFinder.INDOOR_MODE && poi.getPoiNavigationType().equals("internal")) {
                    List<Location> indoorlocations = getIndoorLocations(navpath, campusid, facilityid);
                    data.addAll(indoorlocations);
                } else if (origin.getLocationType() == LocationFinder.OUTDOOR_MODE && poi.getPoiNavigationType().equals("external")) {
                    List<Location> outdoorlocations = getOutdoorLocations(origin, poi);
                    data.addAll(outdoorlocations);
                } else if (origin.getLocationType() == LocationFinder.INDOOR_MODE && poi.getPoiNavigationType().equals("external")) {
                    List<Location> indoortooutdoorlocations = getIndoorToOutdoorLocations(origin, poi, campusid, facilityid, navpath);
                    data.addAll(indoortooutdoorlocations);
                } else if (origin.getLocationType() == LocationFinder.OUTDOOR_MODE && poi.getPoiNavigationType().equals("internal")) {
                    List<Location> outdoortoindoorlocations = getOutdoorToIndoorLocations(origin, poi, campusid, facilityid);
                    data.addAll(outdoortoindoorlocations);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }


        pThread.setPlayingData(data);
        pThread.setRunning(true);
        pThread.resetPlayData();
        Log.getInstance().debug(TAG, "Exit, NavigationPath navpath()");
    }

    public void loadFullRoute() {
        if (pThread != null) {
            pThread.setRunning(false);
        }

        initThread();

        data.clear();

        List<Object> paths = RouteCalculationHelper.getInstance().getFullRoute();

        for (Object o: paths) {
            if (o instanceof FloorNavigationPath) {
                FloorNavigationPath fnavpath = (FloorNavigationPath) o;
                List<Location> floorlocations = divideFloorPath(fnavpath);
                if (floorlocations != null && floorlocations.size() > 0) {
                    for (Location fl : floorlocations) {
                        fl.setCampusId(fnavpath.getCampusId());
                        fl.setFacilityId(fnavpath.getFacilityId());
                        fl.setType(LocationFinder.INDOOR_MODE);
                    }
                    data.addAll(floorlocations);
                }
            } else if (o instanceof CampusNavigationPath) {
                for (GisSegment cp : ((CampusNavigationPath) o).getPath()) {
                    double lat1 = cp.getLine().getPoint1().getY();
                    double lon1 = cp.getLine().getPoint1().getX();
                    double lat2 = cp.getLine().getPoint2().getY();
                    double lon2 = cp.getLine().getPoint2().getX();
                    LatLng l1 = new LatLng(lat1, lon1);
                    LatLng l2 = new LatLng(lat2, lon2);
                    List<Location> tmp = SimulationUtils.divideLine(l1, l2, 5);
                    data.addAll(tmp);
                }
            }
        }

        pThread.setPlayingData(data);
        pThread.setRunning(true);
        pThread.resetPlayData();
        Log.getInstance().debug(TAG, "Exit, NavigationPath navpath()");
    }

    public void load(Location origin, List<DestinationPoi> destpois) {
        if (pThread != null) {
            pThread.setRunning(false);
        }

        initThread();

        data.clear();


        if (destpois != null && !destpois.isEmpty()) {
            Location start = origin;
            PoiData lastpoi = null;
            for (DestinationPoi o : destpois) {
                if (o != null) {
                    if (o.getMode() == LocationMode.INDOOR_MODE) {
                        if (lastpoi != null) {
                            start = new Location(lastpoi.getX(), lastpoi.getY(), (float) lastpoi.getZ());
                            start.setFacilityId(lastpoi.getFacilityID());
                            start.setCampusId(lastpoi.getCampusID());
                        }
                        Location destloc = new Location(o.getPoi());
                        NavigationPath shortPath = PathCalculator.navigateFromTo(
                                start, destloc);
                        if (shortPath != null && destloc.getFacilityId() != null && destloc.getFacilityId().equals(start.getFacilityId())) {
                            List<Location> indoorlocations = getIndoorLocations(shortPath, o.getPoi().getCampusID(), o.getFacilityId());
                            data.addAll(indoorlocations);
                        }
                        lastpoi = o.getPoi();
                    } else if (o.getMode() == LocationMode.OUTDOOR_MODE) {
                        if (lastpoi != null) {
                            LatLng latlng = new LatLng(lastpoi.getPoiLatitude(), lastpoi.getPoiLongitude());
                            start = new Location(latlng);
                        }
                        List<Location> outdoorlocations = getKmlOutdoorLocations(start, o.getPoi());
                        data.addAll(outdoorlocations);
                        lastpoi = o.getPoi();
                    }
                }
            }
        }

        pThread.setPlayingData(data);
        pThread.setRunning(true);
        pThread.resetPlayData();
        Log.getInstance().debug(TAG, "Exit, NavigationPath navpath()");
    }

    private List<Location> getIndoorToOutdoorLocations(Location origin, IPoi poi, String campusid, String facilityid, NavigationPath navpath) {
        List<Location> result = new ArrayList<Location>();
        List<Location> indoorlocations = getIndoorLocations(navpath, campusid, facilityid);
        result.addAll(indoorlocations);

        LatLng l2 = new LatLng(poi.getPoiLatitude(), poi.getPoiLongitude());
        PoiData from = PoiDataHelper.getInstance().getExitPoi(l2);
        if (from != null) {
            LatLng l1 = new LatLng(from.getPoiLatitude(),
                    from.getPoiLongitude());
            List<Location> outdoorlocations = SimulationUtils.divideLine(l1,
                    l2, 10);
            result.addAll(outdoorlocations);
        }
        return result;
    }

    private List<Location> getOutdoorToIndoorLocations(Location origin, IPoi poi, String campusid, String facilityid) {
        List<Location> result = new ArrayList<Location>();
        PoiData dest = aStarData.getInstance().getExternalPoi();
        if (dest != null) {
            LatLng l1 = new LatLng(origin.getLat(), origin.getLon());
            LatLng l2 = new LatLng(dest.getPoiLatitude(),
                    dest.getPoiLongitude());
            List<Location> outdoorlocations = SimulationUtils.divideLine(l1,
                    l2, 10);
            result.addAll(outdoorlocations);
        }

        Location from = new Location(dest);
        Location to = new Location(poi);
        NavigationPath npath = PathCalculator.getNavigationPath(from, to);
        List<Location> indoorlocations = getIndoorLocations(npath, campusid, facilityid);
        result.addAll(indoorlocations);

        return result;
    }

    private List<Location> getOutdoorLocations(Location origin, IPoi poi) {
        List<Location> result = new ArrayList<Location>();
        LatLng l1 = new LatLng(origin.getLat(), origin.getLon());
        LatLng l2 = new LatLng(poi.getPoiLatitude(), poi.getPoiLongitude());
        result = SimulationUtils.divideLine(l1, l2, 5);
        return result;
    }

    private List<Location> getKmlOutdoorLocations(Location origin, IPoi poi) {
        List<Location> result = new ArrayList<Location>();
        List<GisLine> l = CampusGisData.getInstance().getLines();
        LatLng mylatlng = new LatLng(origin.getLat(), origin.getLon());
        if (l != null && !l.isEmpty() && isCloseToKml(mylatlng)) {
            List<GisSegment> cpath = getCampusNavPath(origin, poi, l);
            if (cpath != null && !cpath.isEmpty()) {
                for (GisSegment o : cpath) {
                    double lat1 = o.getLine().getPoint1().getY();
                    double lon1 = o.getLine().getPoint1().getX();
                    double lat2 = o.getLine().getPoint2().getY();
                    double lon2 = o.getLine().getPoint2().getX();
                    LatLng l1 = new LatLng(lat1, lon1);
                    LatLng l2 = new LatLng(lat2, lon2);
                    List<Location> tmp = SimulationUtils.divideLine(l1, l2, 5);
                    result.addAll(tmp);
                }
            } else {
                result = getOutdoorLocations(origin, poi);
            }
        } else {
            result = getOutdoorLocations(origin, poi);
        }
        return result;
    }

    private boolean isCloseToKml(LatLng mylatlng) {
        boolean result = false;
        Campus campus = ProjectConf.getInstance().getSelectedCampus();
        double distanceFromKml = 10;
        if (campus != null) {
            distanceFromKml = campus.getDistance_from_kml();
        }
        LatLng projectedloc = CampusGisData.getInstance().findClosestPointOnLine(mylatlng);
        double dfromloc = MathUtils.distance(mylatlng, projectedloc);
        if (dfromloc < distanceFromKml) {
            result = true;
        }
        return result;
    }

    private List<GisSegment> getCampusNavPath(Location myloc, IPoi dest, List<GisLine> l) {
        List<GisSegment> result = null;
        if (myloc != null && dest != null) {
            double mylocationlat = myloc.getLat();
            double mylocationlon = myloc.getLon();
            LatLng mylatlng = new LatLng(mylocationlat, mylocationlon);
            double destlat = dest.getPoiLatitude();
            double destlon = dest.getPoiLongitude();
            LatLng destLatLng = new LatLng(destlat, destlon);
            aStarData.getInstance().cleanAStar();
            GisPoint startpoint = new GisPoint(mylocationlon,
                    mylocationlat, 0);
            GisPoint endpoint = new GisPoint(destlon, destlat, 0);
            aStarData.getInstance().loadData(startpoint, endpoint, l);
            aStarAlgorithm a = new aStarAlgorithm(startpoint, endpoint);
            List<GisSegment> path = null;
            List<GisSegment> navpath = a.getPath();
            if (navpath != null && !navpath.isEmpty()) {
                result = addEdges(mylatlng, destLatLng, navpath);
            } else {
                result = navpath;
            }
        }
        return result;
    }

    private List<GisSegment> addEdges(LatLng mylatlng, LatLng destLatLng, List<GisSegment> navpath) {
        List<GisSegment> result = new ArrayList<GisSegment>();
        LatLng projectedloc = CampusGisData.getInstance().findClosestPointOnLine(mylatlng, navpath);
        LatLng projecteddest = CampusGisData.getInstance().findClosestPointOnLine(destLatLng, navpath);
        double myz = 0;
        double myx1 = mylatlng.longitude;
        double myy1 = mylatlng.latitude;
        double myx2 = projectedloc.longitude;
        double myy2 = projectedloc.latitude;
        GisPoint myp1 = new GisPoint(myx1, myy1, myz);
        GisPoint myp2 = navpath.get(0).getLine().getPoint1(); //new GisPoint(myx2, myy2, myz);
        GisLine mytmpline = new GisLine(myp1, myp2, myz);
        int mysid = 999;
        GisSegment mys = new GisSegment(mytmpline, mysid);
        result.add(mys);

        result.addAll(navpath);

        double destz = 0;
        double destx1 = destLatLng.longitude;
        double desty1 = destLatLng.latitude;
        double destx2 = projecteddest.longitude;
        double desty2 = projecteddest.latitude;
        GisPoint destp1 = navpath.get(navpath.size() - 1).getLine().getPoint2(); //new GisPoint(destx2, desty2, destz);
        GisPoint destp2 = new GisPoint(destx1, desty1, destz);
        GisLine desttmpline = new GisLine(destp1, destp2, destz);
        int destsid = 888;
        GisSegment dests = new GisSegment(desttmpline, destsid);
        result.add(dests);

        return result;
    }

    private List<Location> getIndoorLocations(NavigationPath navpath, String campusid, String facilityid) {
        List<Location> result = new ArrayList<Location>();
        if (navpath != null && campusid != null && facilityid != null) {
            List<Location> indoorlocations = divideFullPath(navpath);
            for (Location o : indoorlocations) {
                o.setCampusId(campusid);
                o.setFacilityId(facilityid);
                o.setType(LocationFinder.INDOOR_MODE);
                result.add(o);
            }
        }
        return result;
    }

    private List<Location> divideFullPath(NavigationPath navpath) {
        List<Location> result = new ArrayList<Location>();
        List<FloorNavigationPath> fullpath = navpath.getFullPath();
        for (FloorNavigationPath o : fullpath) {
            if (o != null) {
                List<Location> floorlocations = divideFloorPath(o);
                if (floorlocations != null && floorlocations.size() > 0) {
                    result.addAll(floorlocations);
                }
            }

        }
        return result;
    }

    private List<Location> divideFloorPath(FloorNavigationPath floorpath) {
        List<Location> result = new ArrayList<Location>();
        List<GisSegment> segments = floorpath.getPath();
        result = SimulationUtils.getPathAsPoints(segments);
        return result;
    }

    void initThread() {
        pThread = new SimulationThread();
        pThread.state = SimulationThread.Stop;
    }

    public void play() {
        PropertyHolder.getInstance().setLocationPlayer(true);
        if (pThread == null) {
            // XXX: use property holder.
            initThread();
        }

        if (pThread.state == SimulationThread.Play) {
            return;
        }

        if (!pThread.getIsStarted()) {
            pThread.start();
        }

        pThread.state = SimulationThread.Play;

    }

    public void stopPlaying() {
        if (pThread == null) {
            initThread();
        } else {
            pThread.resetPlayData();
        }

        pThread.state = SimulationThread.Stop;
//		PropertyHolder.getInstance().setSdkObserverMode(true);
		PropertyHolder.getInstance().setLocationPlayer(false);

    }

    public void terminate() {
        if (pThread != null) {
            pThread.setRunning(false);
        }
        pThread = null;
    }

    public Location getCurrentPoint() {
        if (pThread != null) {
            return pThread.getCurrentPoint();
        } else {
            return new Location();
        }

    }


    public void SetFixedLocations(List<Location> fixedlocations, boolean repeat) {
        repeatData = repeat;
        if (fixedlocations == null || fixedlocations.isEmpty()) {
            repeatData = false;
            stopPlaying();
            PropertyHolder.getInstance().setLocationPlayer(false);
        } else {
            initThread();
            data.clear();

            projectOnGis(fixedlocations);

            data.addAll(fixedlocations);
            pThread.setPlayingData(data);
            pThread.setRunning(true);
            pThread.resetPlayData();
        }
    }

    private void projectOnGis(List<Location> list) {
        try {
            String facilityid = null;
            int floor = -999;
            List<GisLine> lines = null;
            for (Location o : list) {
                if (o.getLocationType() == LocationFinder.INDOOR_MODE) {
                    String tmpfacid = o.getFacilityId();
                    int tmpfloor = (int) o.getZ();
                    if ((facilityid == null || floor == -999) && tmpfacid != null) {
                        facilityid = tmpfacid;
                        floor = tmpfloor;
                        lines = GisData.getInstance().getLines(facilityid,
                                floor);
                    } else if (facilityid != null && tmpfacid != null && (!facilityid.equals(tmpfacid) || floor != tmpfloor)) {
                        facilityid = tmpfacid;
                        floor = tmpfloor;
                        lines = GisData.getInstance().getLines(facilityid,
                                floor);
                    }
                    projectOnGis(o, lines);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void projectOnGis(Location o, List<GisLine> lines) {
        if (o != null && lines != null && !lines.isEmpty()) {
            PointF p = new PointF((float) o.getX(), (float) o.getY());
            PointF projectedp = GisData.getInstance().findClosestPointOnLine(
                    lines, p);
            o.setX(projectedp.x);
            o.setY(projectedp.y);
        }
    }

    public boolean isRepeatData() {
        return repeatData;
    }

    public void setRepeatData(boolean repeatData) {
        this.repeatData = repeatData;
    }
}
