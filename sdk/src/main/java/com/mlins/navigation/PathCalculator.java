package com.mlins.navigation;

import android.graphics.PointF;

import com.mlins.aStar.FloorNavigationPath;
import com.mlins.aStar.GisSegment;
import com.mlins.aStar.NavigationPath;
import com.mlins.aStar.aStarAlgorithm;
import com.mlins.aStar.aStarData;
import com.mlins.locationutils.LocationFinder;
import com.mlins.nav.utils.OrederPoisUtil;
import com.mlins.switchfloor.SwitchFloorHolder;
import com.mlins.switchfloor.SwitchFloorObj;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
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
import com.spreo.nav.enums.NavigationType;
import com.spreo.nav.enums.SwitchFloorSelectionType;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.List;

public class PathCalculator {
    private final static String TAG = "com.mlins.navigation.NavigationUtil";
    private static double newZ;
    private static List<SwitchFloorObj> startfloorswitchfloors = new ArrayList<SwitchFloorObj>();

    public static void calculatePath() {
        Log.getInstance().debug(TAG, "Enter, calculatePath()");
        Location mylock = (Location) LocationFinder.getInstance().getCurrentLocation();
        calculatePath(mylock);
        Log.getInstance().debug(TAG, "Exit, calculatePath()");
    }

    public static NavigationPath navigateFromTo(Location from, Location to) {
        Log.getInstance().debug(TAG, "Enter, navigateFromTo()");

        if(from.getFacilityId() != null && !from.getFacilityId().equals(to.getFacilityId())){
            NavigationPath result = new NavigationPath();

            List<GisSegment> list = new ArrayList<>();
            list.add(
                    new GisSegment(
                            new GisLine(
                                    Location.getPoint(from),
                                    Location.getPoint(to),
                                    from.getZ(),
                                    true
                            ), 999));
            result.addFloorNavigationPath(from.getZ(), list);
            return result;
        }

        NavigationPath result = new NavigationPath();
        String facilityid = from.getFacilityId();
        if (facilityid == null) {
            facilityid = to.getFacilityId();
        }
        List<Integer> floors = getFromToFloor(facilityid, new ArrayList<Integer>(), (int) from.getZ(), (int) to.getZ(), 0);
        Location origin = from;
        GisPoint start = new GisPoint(from);
        for (int i = 1; i < floors.size(); i++) {
            Integer tofloor = floors.get(i);

            //find the switchfloor that gets from to the floor

            List<SwitchFloorObj> candidate = getSwitchFloorsByZ(facilityid, (int) origin.getZ(), tofloor);

            double minlength = Double.MAX_VALUE;
            List<GisSegment> minPath = null;
            SwitchFloorObj bestsf = null;

            if (PropertyHolder.getInstance().getSwitchFloorSelectionType() == SwitchFloorSelectionType.SHORTEST || PropertyHolder.getInstance().isChooseShortestRoute()) {
                for (SwitchFloorObj swobj : candidate) {
                    PointF pt = swobj.getPoint();
                    List<GisSegment> p = getTmpPath(origin, start, from, pt);
                    if (p != null && !p.isEmpty()) {
                        double length = 0;
                        for (GisSegment gisSegment : p) {
                            length += gisSegment.calcweight();
                        }
                        if (length < minlength) {
                            minlength = length;
                            minPath = p;
                            bestsf = swobj;
                        }
                    }
                }
            } else if (PropertyHolder.getInstance().getSwitchFloorSelectionType() == SwitchFloorSelectionType.CLOSE_TO_DESTINATION) {
                for (SwitchFloorObj swobj : candidate) {
                    PointF pt = swobj.getPoint();
                    PointF tmpdest = new PointF((float)to.getX(), (float)to.getY());
                    double d = MathUtils.distance(pt, tmpdest);
                    if (d < minlength) {
                        minlength = d;
                        bestsf = swobj;
                    }
                }

                if (bestsf != null) {
                    PointF pt = bestsf.getPoint();
                    minPath = getTmpPath(origin, start, from, pt);
                }
            } else if (PropertyHolder.getInstance().getSwitchFloorSelectionType() == SwitchFloorSelectionType.CLOSE_TO_ORIGIN) {
                for (SwitchFloorObj swobj : candidate) {
                    PointF pt = swobj.getPoint();
                    PointF tmporigin = new PointF((float)from.getX(), (float)from.getY());
                    double d = MathUtils.distance(pt, tmporigin);
                    if (d < minlength) {
                        minlength = d;
                        bestsf = swobj;
                    }
                }

                if (bestsf != null) {
                    PointF pt = bestsf.getPoint();
                    minPath = getTmpPath(origin, start, from, pt);
                }
            }

            result.addFloorNavigationPath(origin.getZ(), minPath);

            start = getSwitchFloorById(facilityid, bestsf.getId(), tofloor);
            origin = new Location((float) start.getX(), (float) start.getY(), (float) start.getZ());

        }
        aStarData.getInstance().cleanAStar();
//		GisData.getInstance().loadGisLines((int)origin.getZ());
        List<GisLine> l = GisData.getInstance().getLines(from.getFacilityId(), (int) origin.getZ());
        GisPoint end = new GisPoint(to);
        if (l == null || l.isEmpty()) {
//			l = buildSegmentsWithoutGis(origin, to);
            GisLine Straightline = new GisLine(start, end, origin.getZ(), true);
            Log.d("GISURI", Straightline.toString());
            l = new ArrayList<GisLine>();
            l.add(Straightline);
        }
        //XXX MB aStarData.getInstance().loadData(l);
        aStarData.getInstance().loadData(start, end, l);

        aStarAlgorithm a = new aStarAlgorithm(start, end);
        List<GisSegment> p = a.getPath();
        result.addFloorNavigationPath(origin.getZ(), p);
//		GisData.getInstance().loadGisLines();
        Log.getInstance().debug(TAG, "Exit, navigateFromTo()");
        return result;
    }

    private static List<GisSegment> getTmpPath(Location origin, GisPoint start, Location from, PointF pt) {
        List<GisSegment> result = null;
        aStarData.getInstance().cleanAStar();
//				GisData.getInstance().loadGisLines((int)origin.getZ());
        List<GisLine> l = GisData.getInstance().getLines(from.getFacilityId(), (int) origin.getZ());

        GisPoint end = new GisPoint(pt.x, pt.y, origin.getZ());
        if (l == null || l.isEmpty()) {
//					l = buildSegmentsWithoutGis(from, to);
            GisLine Straightline = new GisLine(start, end, origin.getZ(), true);
            l = new ArrayList<GisLine>();
            l.add(Straightline);
        }


        //XXX MB aStarData.getInstance().loadData(l);
        aStarData.getInstance().loadData(start, end, l);


        aStarAlgorithm a = new aStarAlgorithm(start, end);

        result = a.getPath();

        return result;
    }

    public static List<Integer> getFromToFloor(String facilityid, List<Integer> oldroute, int fromZ,
                                               int toZ, int counter) {
        Log.getInstance().debug(TAG, "Enter, getFromToFloor()");
        List<Integer> result = new ArrayList<Integer>();

        // recursion end
        if (fromZ == toZ) {
            result.add(fromZ);
            return result;
        }
        if (counter > 2) {
            return result;
        }
        List<SwitchFloorObj> sw = SwitchFloorHolder.getInstance()
                .getSwichFloorPoints(facilityid);

        List<SwitchFloorObj> fromsw = getFromZ(sw, fromZ);
        if (fromsw == null) {
            // no available sw that reaches the floor return empty list
            return result;
        }

        // we dont have any match find the closest floor and redo the function
        int mindiff = Integer.MAX_VALUE;
        List<Integer> route = new ArrayList<Integer>();
        List<Integer> temproute = new ArrayList<Integer>();
        if (oldroute != null) {
            temproute.addAll(oldroute);
        }
        temproute.add(fromZ);
        for (SwitchFloorObj switchFloorObj : fromsw) {
            List<Integer> tofloors = switchFloorObj.getToFloor();
            for (Integer integer : tofloors) {
                if (integer != fromZ && !oldroute.contains(integer)) // avoid
                // route
                // to
                // same
                // floor
                {

                    List<Integer> candidate = getFromToFloor(facilityid, temproute,
                            integer, toZ, counter + 1);

                    if (candidate.size() > 0 && candidate.size() < mindiff) {
                        route = candidate;
                        mindiff = candidate.size();
                    }
                }

            }
        }
        if (route.size() > 0) {
            result.add(fromZ);
            result.addAll(route);
        }
        Log.getInstance().debug(TAG, "Exit, getFromToFloor()");
        return result;
    }

    private static List<SwitchFloorObj> getFromZ(List<SwitchFloorObj> sw, int fromZ) {
        Log.getInstance().debug(TAG, "Enter, getFromZ()");
        List<SwitchFloorObj> result = new ArrayList<SwitchFloorObj>();
        for (SwitchFloorObj switchFloorObj : sw) {
            if (switchFloorObj.getFromFloor().contains(fromZ)) {
                result.add(switchFloorObj);
            }
        }
        Log.getInstance().debug(TAG, "Exit, getFromZ()");
        return result;
    }

    public static List<NavigationPath> getMultiPoiNavigationPaths(ILocation myloc, List<IPoi> poilist) {
        List<NavigationPath> result = new ArrayList<NavigationPath>();

        List<IPoi> pois = new ArrayList<IPoi>();

        for (IPoi o : poilist) {
            if (o.getPoiNavigationType().equals("internal")) {
                pois.add(o);
            }
        }

        if (pois != null && !pois.isEmpty()) {

            Location from = null;
            Location to = null;

            if (myloc != null && myloc.getLocationType() == LocationMode.INDOOR_MODE) {
                from = new Location(myloc);
                to = new Location(pois.get(0));
                NavigationPath tmppath = getNavigationPath(from, to);
                if (tmppath != null) {
                    result.add(tmppath);
                }
            }

            for (IPoi o : pois) {
                int index = pois.indexOf(o);
                if (index > 0) {
                    from = new Location(pois.get(index - 1));
                    to = new Location(o);
                }
                if (from != null && to != null) {
                    NavigationPath tmppath = getNavigationPath(from, to);
                    if (tmppath != null) {
                        result.add(tmppath);
                    }
                }
            }

        }

        return result;
    }

    public static NavigationPath getNavigationPath(Location from, Location to) {
        NavigationPath result = null;
        try {
            result = navigateFromTo(from, to);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void calculatePath(Location mylocation) {
        Log.getInstance().debug(TAG, "Enter, calculatePath()");

        Location from = null;
        if (mylocation == null || PropertyHolder.getInstance().isObserverUser()) {
            PoiData epoi = PoiDataHelper.getInstance().getExitPoi();
            if (epoi != null) {
                PointF mp = epoi.getPoint();
                aStarData.getInstance().setMyLocation(mp);
                int entrancefloor = (int) epoi.getZ();
                FacilityContainer.getInstance().getSelected().setSelected(entrancefloor);
                //FacilityConf.getInstance().setSelected(entrancefloor);
                from = new Location(mp.x, mp.y, entrancefloor);
                String campusId = epoi.getCampusID();
                from.setCampusId(campusId);
                String facilityId = epoi.getFacilityID();
                from.setFacilityId(facilityId);
            } else {
                from = new Location(0, 0, 0);
            }

        } else {
            from = mylocation;
        }

        Location poiloc = aStarData.getInstance().getPoilocation();

        if (poiloc != null && from != null) {
            long startnavigationtime = System.currentTimeMillis();
            PropertyHolder.getInstance().setStartNavigationTime(
                    startnavigationtime);

            NavigationPath shortPath = navigateFromTo(from, poiloc);
            if (shortPath != null) {
                aStarData.getInstance().setCurrentPath(shortPath);
            }

        }
        Log.getInstance().debug(TAG, "Exit, calculatePath()");
    }


    private static List<GisLine> buildSegmentsWithoutGis(Location from, Location to) {
        // no Gis found
        List<GisLine> list = new ArrayList<GisLine>();

        //if(!GisData.getInstance().hasGis()){

        PointF myLoc = aStarData.getInstance().getMyLocation();

        Location destPoi = aStarData.getInstance().getPoilocation();
        PointF destLoc = new PointF((float) destPoi.getX(), (float) destPoi.getY());

        FacilityConf FacilityConf = FacilityContainer.getInstance().getSelected();
        int currentFloor = FacilityConf.getSelectedFloor();
        int destFloor = (int) to.getZ();

        if (from.getZ() == currentFloor && destFloor != currentFloor) {
            SwitchFloorObj sw = OrederPoisUtil.getCloseSwitchFloor(myLoc, currentFloor, destFloor);
            destLoc = sw.getPoint();
        }

        if (currentFloor != from.getZ() && destFloor != currentFloor) {
            SwitchFloorObj sw = OrederPoisUtil.getCloseSwitchFloor(destLoc, destFloor, destFloor);
            myLoc = sw.getPoint();
        }


        GisLine l = new GisLine(myLoc, destLoc, currentFloor, true);

        list.add(l);
        //}

        return list;
    }

    private static NavigationPath getShortPath(List<NavigationPath> pathoptions) {
        if (pathoptions.size() == 1) {
            return pathoptions.get(0);
        }
        NavigationPath result = null;
        double minlength = Double.MAX_VALUE;
        for (NavigationPath o : pathoptions) {
            double length = calculateLength(o);
            if (length != 0 && length < minlength) {
                minlength = length;
                result = o;
            }
        }

        return result;
    }

    private static double calculateLength(NavigationPath path) {
        Log.getInstance().debug(TAG, "Enter, calculateLength()");
        double result = 0;
        if (path != null) {
            List<FloorNavigationPath> allfloorspath = path.getFullPath();
            for (FloorNavigationPath f : allfloorspath) {
                List<GisSegment> floorpath = f.getPath();
                for (GisSegment s : floorpath) {
                    double w = s.getWeight();
                    result += w;
                }
            }
        }
        Log.getInstance().debug(TAG, "Exit, calculateLength()");
        return result;
    }

    private static List<SwitchFloorObj> getSwitchFloorsByZ(String facilityid, int z, int endz) {
        Log.getInstance().debug(TAG, "Enter, getSwitchFloorsByZ()");
        List<SwitchFloorObj> alllist = SwitchFloorHolder.getInstance()
                .getSwichFloorPoints(facilityid);
        List<SwitchFloorObj> listbyz = new ArrayList<SwitchFloorObj>();
        for (SwitchFloorObj o : alllist) {
            if (o.getZ() == z) {
                listbyz.add(o);
            }
        }
        if (z == endz) {
            return getSwitchFloorsByNavigationType(listbyz);
        }
        List<SwitchFloorObj> candidates = new ArrayList<SwitchFloorObj>();
        for (SwitchFloorObj o : listbyz) {
            List<Integer> floors = o.getToFloor();
            for (Integer f : floors) {
                if (f == endz) {
                    candidates.add(o);
                    continue;
                }
            }
        }
        Log.getInstance().debug(TAG, "Exit, getSwitchFloorsByZ()");
        return getSwitchFloorsByNavigationType(candidates);
    }

    private static List<SwitchFloorObj> getSwitchFloorsByNavigationType(List<SwitchFloorObj> all) {
        List<SwitchFloorObj> result = new ArrayList<SwitchFloorObj>();
        NavigationType navigationtype = PropertyHolder.getInstance().getNavigationType();
        if (navigationtype == NavigationType.DEFAULT) {
            result = all;
        } else if (navigationtype == NavigationType.DISABLED) {
            for (SwitchFloorObj o : all) {
                if (o.getType().equals("elevator")) {
                    result.add(o);
                }
            }
        } else if (navigationtype == NavigationType.ESCALATORS) {
            for (SwitchFloorObj o : all) {
                if (o.getType().equals("escalators")) {
                    result.add(o);
                }
            }
        }

        if (result.isEmpty()) {
            result = all;
        }

        return result;
    }

    private static GisPoint getSwitchFloorById(String facilityid, double id, double z) {
        Log.getInstance().debug(TAG, "Enter, getSwitchFloorById()");
        GisPoint result = null;
        List<SwitchFloorObj> list = SwitchFloorHolder.getInstance()
                .getSwichFloorPoints(facilityid);
        for (SwitchFloorObj o : list) {
            if (o.getZ() == z && o.getId() == id) {
                result = new GisPoint(o.getPoint().x, o.getPoint().y, o.getZ());
                break;
            }
        }
        Log.getInstance().debug(TAG, "Exit, getSwitchFloorById()");
        return result;
    }

//	private static SwitchFloorObj getBestSwitchFloor(GisPoint start,
//			GisPoint end) {
//		SwitchFloorObj result = null;
//		PointF sp = new PointF((float) start.getX(), (float) start.getY());
//		List<SwitchFloorObj> alllist = SwitchFloorHolder.getInstance()
//				.getSwichFloorPoints();
//		List<SwitchFloorObj> listbyz = new ArrayList<SwitchFloorObj>();
//		for (SwitchFloorObj o : alllist) {
//			if (o.getZ() == start.getZ() && startfloorswitchfloors.contains(o)) {
//				listbyz.add(o);
//			}
//		}
//
//		List<SwitchFloorObj> candidates = new ArrayList<SwitchFloorObj>();
//		for (SwitchFloorObj o : listbyz) {
//			List<Integer> floors = o.getToFloor();
//			for (Integer f : floors) {
//				if (f == end.getZ()) {
//					candidates.add(o);
//					continue;
//				}
//			}
//		}
//		if (candidates != null && candidates.size() > 0) {
//			newZ = end.getZ();
//			result = getCloseSwitchFloor(sp, candidates);
//			return result;
//		}
//
//		return result;
//	}

    private static SwitchFloorObj getCloseSwitchFloor(PointF p,
                                                      List<SwitchFloorObj> list) {
        Log.getInstance().debug(TAG, "Enter, getCloseSwitchFloor()");
        SwitchFloorObj result = null;
        double mind = 10000000;
        for (SwitchFloorObj o : list) {
            double d = MathUtils.distance(p, o.getPoint());
            if (d < mind) {
                mind = d;
                result = o;
            }
        }
        Log.getInstance().debug(TAG, "Exit, getCloseSwitchFloor()");
        return result;
    }

    private static List<SwitchFloorObj> getCandidatesByDifference(
            List<SwitchFloorObj> list, double endz) {
        List<SwitchFloorObj> result = new ArrayList<SwitchFloorObj>();
        setMinDifferences(list, endz);
        int minDifference = 1000;
        for (SwitchFloorObj o : list) {
            if (o.getMinFloorDifference() < minDifference) {
                minDifference = o.getMinFloorDifference();
            }
        }

        for (SwitchFloorObj o : list) {
            if (o.getMinFloorDifference() > 0
                    && o.getMinFloorDifference() == minDifference) {
                result.add(o);
            }
        }

        if (result.size() == 0) {
            for (SwitchFloorObj o : list) {
                if (o.getMinFloorDifference() == minDifference) {
                    result.add(o);
                }
            }
        }

        return result;
    }

    public static void setMinDifferences(List<SwitchFloorObj> list, double endz) {
        Log.getInstance().debug(TAG, "Enter, setMinDifferences()");
        for (SwitchFloorObj o : list) {
            int tof = -1000;
            int minfloorsd = 1000;
            List<Integer> floors = o.getToFloor();
            for (Integer f : floors) {
                int floorsd = (int) (f - endz);
                if (floorsd < minfloorsd) {
                    minfloorsd = floorsd;
                    tof = f;
                }
            }
            o.setGoingToFloor(tof);
            o.setMinFloorDifference(minfloorsd);
        }
        Log.getInstance().debug(TAG, "Exit, setMinDifferences()");
    }

}
