package com.mlins.dualmap;

import android.content.Context;
import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.aStar.CampusNavigationPath;
import com.mlins.aStar.GisSegment;
import com.mlins.aStar.NavigationPath;
import com.mlins.instructions.Instruction;
import com.mlins.instructions.InstructionBuilder;
import com.mlins.instructions.Instructionobject;
import com.mlins.nav.utils.SoundPlayer;
import com.mlins.navigation.GoogleNavigationUtil;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.project.bridges.Bridge;
import com.mlins.project.bridges.BridgeData;
import com.mlins.project.bridges.BridgeSelectionType;
import com.mlins.utils.ExitsSelectionManager;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PoiData;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.Location;
import com.mlins.utils.logging.Log;
import com.spreo.nav.enums.ExitsSelectionType;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DualMapNavUtil {
    private final static String TAG = "com.mlins.navigation.DualMapNavUtil";
    public static Location navOrigin = null;
    private static int googleNavDistance = 1000;
    private static List<DestinationPoi> destinations = new ArrayList<DestinationPoi>();
    private static HashMap<IPoi, String> exits = new HashMap<>();
    private static List<Bridge> relevantBridges = new ArrayList<>();
    private static List<Bridge> bridgeExits = new ArrayList<Bridge>();


    public static void buildDestination(Location origin, PoiData destination) {
        Log.getInstance().debug(TAG, "Enter, navigate()");
        try {

            if (origin != null && destination != null) {

                navOrigin = new Location(origin);

                destinations.clear();
                relevantBridges = new ArrayList<>();
                relevantBridges.addAll(ProjectConf.getInstance().getBridges().getBridges());
                if ((origin.getType() == Location.TYPE_INTERNAL /*|| PropertyHolder.getInstance().isSdkObserverMode()*/) && destination.getPoiNavigationType().equals("internal") && origin.getFacilityId().equals(destination.getFacilityID())) {
                    indoorToIndoorNav(origin, destination);
                } else if ((origin.getType() == Location.TYPE_INTERNAL && destination.getPoiNavigationType().equals("external")) ||
                        (origin.getType() == Location.TYPE_INTERNAL && origin.getFacilityId() != null && !origin.getFacilityId().equals(destination.getFacilityID()))) {
                    //check if its not campus navigation (i.e. between facilities)
                    if ((origin.getType() == Location.TYPE_INTERNAL && destination.getPoiNavigationType().equals("external"))) {
                            indoorToOutdoorNav(origin, destination);
                    } else if ((origin.getType() == Location.TYPE_INTERNAL && (destination.getPoiNavigationType().equals("internal") && !origin.getFacilityId().equals(destination.getFacilityID())))) {
                            indoorToOtherIndoorNav(origin, destination);
                    }
                } else if (origin.getType() == Location.TYPE_EXTERNAL && destination.getPoiNavigationType().equals("internal")) {
                        outdoorToIndoorNav(origin, destination);
                } else if (origin.getType() == Location.TYPE_EXTERNAL && destination.getPoiNavigationType().equals("external")) {
                    outdoorToOutdoorNav(origin, destination);
                }

            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
        Log.getInstance().debug(TAG, "Exit, navigate()");
    }

    public static void buildDestination(Location origin, Location destination) {
        Log.getInstance().debug(TAG, "Enter, navigate()");
        try {
            if (destination.getType() == Location.TYPE_INTERNAL) {
                PointF point = new PointF((float) destination.getX(), (float) destination.getY());
                PoiData indoorpoi = new PoiData(point);
                indoorpoi.setZ(destination.getZ());
                indoorpoi.setCampusID(destination.getCampusId());
                indoorpoi.setFacilityID(destination.getFacilityId());
                buildDestination(origin, indoorpoi);
            } else if (destination.getType() == Location.TYPE_EXTERNAL) {
                double lat = destination.getLat();
                double lon = destination.getLon();
                PoiData outdoorpoi = new PoiData();
                outdoorpoi.setPoiLatitude(lat);
                outdoorpoi.setPoiLongitude(lon);
                //outdoorpoi.setPoitype("external");
                outdoorpoi.setPoiNavigationType("external");
                buildDestination(origin, outdoorpoi);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Log.getInstance().debug(TAG, "Exit, navigate()");
    }

    private static void indoorToOtherIndoorNav(Location origin, PoiData destination) {
        ExitsSelectionAlgorithm a = new ExitsSelectionAlgorithm();
        destinations = a.indoorToOtherIndoor(origin, destination);
        exits = a.getExits();
        bridgeExits = a.getBridgeExits();
    }

    private static void outdoorToIndoorNav(Location origin, PoiData destination) {
        ExitsSelectionAlgorithm a = new ExitsSelectionAlgorithm();
        destinations = a.OutdoorToIndoor(origin, destination);
        exits = a.getExits();
        bridgeExits = a.getBridgeExits();
    }

    private static void indoorToOutdoorNav(Location origin, PoiData destination) {
        ExitsSelectionAlgorithm a = new ExitsSelectionAlgorithm();
        destinations = a.indoorToOutdoor(origin, destination);
        exits = a.getExits();
        bridgeExits = a.getBridgeExits();
    }

    private static void outdoorToOutdoorNav(Location origin, PoiData destination) {
        ExitsSelectionAlgorithm a = new ExitsSelectionAlgorithm();
        DestinationPoi outdoordestination = a.outdoorToOutdoorNavigation(origin, destination);
        destinations.add(outdoordestination);
    }

    private static void indoorToIndoorNav(Location origin, PoiData destination) {
        ExitsSelectionAlgorithm a = new ExitsSelectionAlgorithm();
        DestinationPoi indoordestination = a.indoorToIndoorNavigation(origin, destination);
        destinations.add(indoordestination);
    }


    private static void outdoorToIndoorWithBridges(Location origin, PoiData destination) {
        LatLng originlatlng = new LatLng(origin.getLat(), origin.getLon());
        PoiData exitpoi = null;
        BridgeData bd = ProjectConf.getInstance().getBridges();
        if (bd.hasBridge(destination.getFacilityID())) {
            exitpoi = getClosetExit(originlatlng);
        }
        if (exitpoi != null) {
            exits.put(exitpoi, RouteCalculationHelper.TYPE_ENTRANCE);
            DestinationPoi outdest = new DestinationPoi(exitpoi,LocationMode.OUTDOOR_MODE);
            DestinationPoi indest = new DestinationPoi(destination,LocationMode.INDOOR_MODE);
            destinations.add(outdest);
//            destinations.add(indest);
            Location newloc = new Location(exitpoi);
            bridgesNaviagtion(newloc, destination);
        } else {
            outdoorToIndoorNavigation(origin, destination);
        }
    }

    private static void indoorToOutdoorWithbridges(Location origin, PoiData destination) {
        LatLng destlatlng = new LatLng(destination.getPoiLatitude(), destination.getPoiLongitude());
        PoiData exitpoi = null;
        BridgeData bd = ProjectConf.getInstance().getBridges();
        if (bd.hasBridge(origin.getFacilityId())) {
            exitpoi = getClosetExit(destlatlng);
        }
        if (exitpoi != null) {
            bridgesNaviagtion(origin, exitpoi);
            exits.put(exitpoi, RouteCalculationHelper.TYPE_EXIT);
            DestinationPoi outdest = new DestinationPoi(destination,
                    LocationMode.OUTDOOR_MODE);
            destinations.add(outdest);
        } else {
           indoorToOutdoorNavigation(origin, destination);
        }
    }

    private static PoiData getClosetExit(LatLng destlatlng) {
        PoiData result = null;
        try {
            List<IPoi> allPoi = ProjectConf.getInstance().getAllPoisList();
            List<IPoi> all = new ArrayList<IPoi>();
            for (IPoi o : allPoi) {
                if (o.getPoiID().contains("idr") && o.getPoiNavigationType().equals("internal")) {
                    String fac = o.getFacilityID();
                    BridgeData bd = ProjectConf.getInstance().getBridges();
                    if (fac != null && bd.hasBridge(fac)) {
                        all.add(o);
                    }
                }
            }
            List<? extends IPoi> exitpois = ProjectConf.getInstance().getBridges().removeBridgesSides(all);
            if (exitpois.size() > 0) {
                IPoi tmp = findCloseExit(destlatlng, exitpois);
                if (tmp instanceof PoiData) {
                    result = (PoiData) tmp;
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void bridgesNaviagtion(Location origin, PoiData destination) {
        String originfac = origin.getFacilityId();
        String destfac = destination.getFacilityID();
        double floor = origin.getZ();
        if (originfac == null || destfac == null) {
            return;
        }

        if (originfac.equals(destfac)) {
            indoorToIndoorNavigation(origin, destination);
            return;
        }

        try {
            if (relevantBridges == null  || relevantBridges.isEmpty()) {
                indoorToOtherIndoorNavigation(origin, destination);
            } else {
                List<Bridge> candidates = getCandidtes(originfac, floor);
                if (candidates == null || candidates.isEmpty()) {
                    indoorToOtherIndoorNavigation(origin, destination);
                } else {
                    List<Bridge> connectingbridges = getConnectingBridge(candidates, originfac, destfac);
                    if (connectingbridges != null && !connectingbridges.isEmpty()) {
                        Bridge bestbridge = findBestBridge(connectingbridges, origin, destination, true);
                        addBridge(bestbridge, originfac);
                        indoorToIndoorNavigation(origin, destination);
                    } else {
                        Bridge bestbridge = findBestBridge(candidates, origin, destination, false);
                        if (bestbridge == null) {
                            indoorToOtherIndoorNavigation(origin, destination);
                        } else {
                            addBridge(bestbridge, originfac);
                            Bridge orderedbestbridge = getcorrectBridgeOrder(bestbridge, originfac);
                            Location neworigin = new Location(orderedbestbridge.getPoint2());
                            removeBridges(originfac);
                            bridgesNaviagtion(neworigin, destination);
                        }
                    }

                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private static void removeBridges(String fac) {
        List<Bridge> toremove = new ArrayList<>();
        try {
            for (Bridge bridge : relevantBridges) {
                if (bridge.isConnectToFacility(fac)) {
                    toremove.add(bridge);
                }
            }
            relevantBridges.removeAll(toremove);
        } catch (Throwable t){
            t.printStackTrace();
        }
    }

    public static List<Bridge> getBridgesInfloor(String fac, double z) {
        List<Bridge> result = new ArrayList<>();
        try{
            for (Bridge bridge : relevantBridges) {
                if (bridge.isConnectToFacility(fac) && bridge.isInFloor(fac, z)) {
                    result.add(bridge);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public static List<Bridge> getBridgesInfacility(String fac) {
        List<Bridge> result = new ArrayList<>();
        try{
            for (Bridge bridge : relevantBridges) {
                if (bridge.isConnectToFacility(fac)) {
                    result.add(bridge);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private static List<Bridge> getCandidtes(String originfac, double floor) {
        List<Bridge> result = new ArrayList<>();
        try {
            if (PropertyHolder.getInstance().getBridgeSelectionType() == BridgeSelectionType.SAME_FLOOR) {
                result = getBridgesInfloor(originfac, floor);
            } else if (PropertyHolder.getInstance().getBridgeSelectionType() == BridgeSelectionType.ALL) {
                result = getBridgesInfacility(originfac);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private static Bridge getcorrectBridgeOrder(Bridge bridge, String firstfacility) {
        Bridge result = bridge;
        if (firstfacility.equals(bridge.getPoint2().getFacilityID())) {
           result = new Bridge(bridge.getID(), bridge.getPoint2(), bridge.getPoint1());
        }
        return result;
    }

    private static void addBridge(Bridge bridge, String fac) {
        Bridge orderedbridge = getcorrectBridgeOrder(bridge, fac);
        bridgeExits.add(orderedbridge);
        DestinationPoi p1 = new DestinationPoi(orderedbridge.getPoint1(),
                LocationMode.INDOOR_MODE);
        DestinationPoi p2 = new DestinationPoi(orderedbridge.getPoint2(),
                LocationMode.INDOOR_MODE);
        destinations.add(p1);
        destinations.add(p2);
    }

    private static Bridge findBestBridge(List<Bridge> bridges, Location origin, PoiData destination, boolean connectingbridges) {
        Bridge result = null;
        try {
            Double originz = origin.getZ();
            double destz = destination.getZ();
            LatLng destloc =  ConvertingUtils.convertToLatlng(destination.getX(), destination.getY(), (int)destination.getZ(), destination.getFacilityID());
            if (destloc != null && bridges != null) {
                double mind = Double.MAX_VALUE;
                for (Bridge bridge : bridges) {
                    LatLng p1 = ConvertingUtils.convertToLatlng(bridge.getPoint1().getX(), bridge.getPoint1().getY(), (int)bridge.getPoint1().getZ(), bridge.getPoint1().getFacilityID());
                    LatLng p2 = ConvertingUtils.convertToLatlng(bridge.getPoint2().getX(), bridge.getPoint2().getY(), (int)bridge.getPoint2().getZ(), bridge.getPoint2().getFacilityID());
                    String originfac = origin.getFacilityId();
//                    String destfac = destination.getFacilityID();

                    Bridge tmp = getcorrectBridgeOrder(bridge, originfac);
                    double p1z = tmp.getPoint1().getZ();
                    double p2z = tmp.getPoint2().getZ();
//                    String p1fac = tmp.getPoint1().getFacilityID();
//                    String p2fac = tmp.getPoint2().getFacilityID();

                    int floorweight = 0;
                    if (originz != p1z) {
                        floorweight += 50;
                    }
                    if (destz != p2z) {
                        floorweight += 25;
                    }
                    if (p1 != null) {
                        double d = 0;
                        if (connectingbridges) {
                            d = getEuclideanDistance(origin, p1, destloc);
                        } else {
                            d = MathUtils.distance(p1, destloc);
                        }
                        d += floorweight;
                        if (d < mind) {
                            mind = d;
                            result = bridge;
                        }
                    }
                    if (p2 != null) {
                        double d = 0;
                        if (connectingbridges) {
                            d = getEuclideanDistance(origin, p2, destloc);
                        } else {
                            d = MathUtils.distance(p2, destloc);
                        }
                        d += floorweight;
                        if (d < mind) {
                            mind = d;
                            result = bridge;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private static double getEuclideanDistance(Location origin, LatLng bridgepoint, LatLng destloc) {
        double result = 0;
        try{
            LatLng lorigin = getLatLng(origin);
            List<LatLng> llist = new ArrayList<>();
            llist.add(lorigin);
            llist.add(bridgepoint);
            llist.add(destloc);
            double d = euclideanDistanceSum(llist);
        } catch (Throwable t) {
            result = Double.MAX_VALUE;
        }
        return result;
    }

    private static LatLng getLatLng(PoiData poi) {
        LatLng result = null;
        try {
            result = ConvertingUtils.convertToLatlng(poi.getX(), poi.getY(), (int)poi.getZ(), poi.getFacilityID());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private static LatLng getLatLng(IPoi poi) {
        LatLng result = null;
        try {
            result = ConvertingUtils.convertToLatlng(poi.getX(), poi.getY(), (int)poi.getZ(), poi.getFacilityID());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private static LatLng getLatLng(Location loc) {
        LatLng result = null;
        try {
            result = ConvertingUtils.convertToLatlng(loc.getX(), loc.getY(), (int)loc.getZ(), loc.getFacilityId());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private static List<Bridge> getConnectingBridge(List<Bridge> bridges, String fac1, String fac2) {
        List<Bridge> result = new ArrayList<>();
        try {
            for (Bridge bridge : bridges) {
                if (bridge.isBetween(fac1, fac2)) {
                    result.add(bridge);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }

    private static void indoorToOtherIndoorNavigation(Location origin, PoiData destination) {
        try {
            String fac2id = destination.getFacilityID();
            if (fac2id != null) {
//				Campus campus = ProjectConf.getInstance().getSelectedCampus();
//				FacilityConf fac2 = campus.getFacilityConf(fac2id);
//				LatLng fac2latlng = new LatLng(fac2.getCenterLatitude(), fac2.getCenterLongtitude()); 
//				PoiData exitpoi = getExitPoi(fac2latlng, origin.getCampusId(), origin.getFacilityId());
//				LatLng fac1latlng = new LatLng(exitpoi.getPoiLatitude(), exitpoi.getPoiLongitude());
//				PoiData enterancepoi = getExitPoi(fac1latlng, destination.getCampusID(), destination.getFacilityID());

                PoiData exitpoi = null;
                PoiData enterancepoi = null;

                if (PropertyHolder.getInstance().isChooseShortestRoute()) {
                    List<PoiData> tmp = getShortestExitAndEnterPoints(origin, destination);
                    if (tmp != null && tmp.size() > 1){
                        exitpoi = tmp.get(0);
                        enterancepoi = tmp.get(1);
                    }
                } else {
                    String facid = origin.getFacilityId();
                    ExitsSelectionType exitstype = ExitsSelectionManager.getInstance().getExitSelectionType(facid);
                    if (exitstype == ExitsSelectionType.CLOSE_TO_DESTINATION) {
                        enterancepoi = getExitCloseTo(destination, false, origin.getFacilityId());
                        exitpoi = getOppositeSideForRouteBetween(enterancepoi, origin.getCampusId(), origin.getFacilityId());
                    } else {
                        exitpoi = getExitCloseTo(origin, false, destination.getFacilityID());
                        enterancepoi = getOppositeSideForRouteBetween(exitpoi, destination.getCampusID(), destination.getFacilityID());
                    }
                }

                if (exitpoi != null && enterancepoi != null) {
                    exits.put(exitpoi, RouteCalculationHelper.TYPE_EXIT);
                    exits.put(enterancepoi, RouteCalculationHelper.TYPE_ENTRANCE);
                    DestinationPoi indest1 = new DestinationPoi(exitpoi,
                            LocationMode.INDOOR_MODE);

//                    Bridge bridge = ProjectConf.getInstance().getBridges().getBridge(enterancepoi);
//
//                    LocationMode entranceLocationMode = // using INDOOR_INDOOR if this exit belongs to bridge
//                            bridge != null ? LocationMode.INDOOR_MODE : LocationMode.OUTDOOR_MODE;

                    DestinationPoi outdest = new DestinationPoi(enterancepoi,
                            LocationMode.OUTDOOR_MODE);

                    DestinationPoi indest2 = new DestinationPoi(destination,
                            LocationMode.INDOOR_MODE);
                    destinations.add(indest1);
                    destinations.add(outdest);
                    destinations.add(indest2);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private static List<PoiData> getShortestExitAndEnterPoints(Location origin, PoiData destination) {
        List<PoiData> result = new ArrayList<>();
        try {
            HashMap<Double, List<IPoi>> map = new HashMap<>();
            LatLng lorigin = getLatLng(origin);
            LatLng ldestination = getLatLng(destination);
            List<IPoi> lexits = getExits(origin.getCampusId(), origin.getFacilityId());
            List<IPoi> lentrances = getExits(destination.getCampusID(), destination.getFacilityID());
            for (IPoi ex : lexits) {
                LatLng lex = getLatLng(ex);
                for (IPoi en : lentrances) {
                    LatLng len = getLatLng(en);
                    List<LatLng> llist = new ArrayList<>();
                    llist.add(lorigin);
                    llist.add(lex);
                    llist.add(len);
                    llist.add(ldestination);
                    double d = euclideanDistanceSum(llist);
                    List<IPoi> tmp = new ArrayList<>();
                    tmp.add(ex);
                    tmp.add(en);
                    map.put(d, tmp);
                }
            }
            if (!map.isEmpty()) {
                List<IPoi> tmp = null;
                if (map.size() > 1) {
                    while (map.size() > 2) {
                        double max = Collections.max(map.keySet());
                        map.remove(max);
                    }
                    HashMap<Double, List<IPoi>> newmap = new HashMap<>();
                    for (List<IPoi> l : map.values()) {
                        IPoi p1 = l.get(0);
                        IPoi p2 = l.get(1);
                        double d = getKMLLength(p1, p2);
                        newmap.put(d, l);
                    }
                    double min = Collections.min(newmap.keySet());
                    tmp = newmap.get(min);
                } else {
                    double min = Collections.min(map.keySet());
                    tmp = map.get(min);
                }

                if (tmp != null) {
                    for (IPoi o : tmp) {
                        if (o instanceof PoiData) {
                            PoiData poi = (PoiData) o;
                            result.add(poi);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private static double getKMLLength(IPoi p1, IPoi p2) {
        double result = 0;
        try {
            LatLng lp1 = null;
            LatLng lp2 = null;
            if (p1.getPoiNavigationType().equals("internal") ) {
                lp1 = getLatLng(p1);
            } else {
                lp1 = new LatLng(p1.getPoiLatitude(), p1.getPoiLongitude());
            }

            if (p2.getPoiNavigationType().equals("internal") ) {
                lp2 = getLatLng(p2);
            } else {
                lp2 = new LatLng(p2.getPoiLatitude(), p2.getPoiLongitude());
            }

            result = getKMLLength(lp1, lp2);

        } catch (Throwable t) {
            result = Double.MAX_VALUE;
            t.printStackTrace();
        }
        return result;
    }

    private static double getKMLLength(LatLng lp1, LatLng lp2) {
        double result = 0;
        try {
            CampusNavigationPath cpath = RouteCalculationHelper.getInstance().calculateCampusPath(lp1, lp2);
            for (GisSegment s : cpath.getPath()) {
                LatLng l1 = new LatLng(s.getLine().getPoint1().getY(), s.getLine().getPoint1().getX());
                LatLng l2 = new LatLng(s.getLine().getPoint2().getY(), s.getLine().getPoint2().getX());
                double d = MathUtils.distance(l1, l2);
                result += d;
            }
        } catch (Throwable t) {
            result = Double.MAX_VALUE;
            t.printStackTrace();
        }
        return result;
    }

    private static List<IPoi> getExits(String campusId, String facilityId) {
        List<IPoi> result = new ArrayList<IPoi>();
        List<IPoi> allPoi = ProjectConf.getInstance().getAllFacilityPoisList(campusId, facilityId);
        for (IPoi o : allPoi) {
            if (o.getPoiID().contains("idr") && o.getPoiNavigationType().equals("internal")) {
                result.add(o);
            }
        }
        return result;
    }

    private static PoiData getExitCloseTo(IPoi poi, boolean considerBridges, String destinationFacilityID) {
        PoiData result = getExitPoi(poi.getPoint(), poi.getZ(), poi.getCampusID(), poi.getFacilityID(), considerBridges);
        if(considerBridges && result != null && !leadsToOutdoorOrSpecifiedFacility(result, destinationFacilityID)) {
            return getExitCloseTo(poi, false, destinationFacilityID);
        }
        return result;
    }

    private static PoiData getExitCloseTo(ILocation location, boolean considerBridges, String destinationFacilityID) {
        PoiData result = getExitPoi(new PointF((float) location.getX(), (float) location.getY()), location.getZ(), location.getCampusId(), location.getFacilityId(), considerBridges);
        if(considerBridges && result != null && !leadsToOutdoorOrSpecifiedFacility(result, destinationFacilityID)) {
            return getExitCloseTo(location, false, destinationFacilityID);
        }
        return result;
    }

    private static boolean leadsToOutdoorOrSpecifiedFacility(IPoi poi, String facility) {
        Bridge bridge = ProjectConf.getInstance().getBridges().getBridge(poi);
        return bridge == null // it's not an bridge side
                || bridge.isBetween(poi.getFacilityID(), facility);
    }

    public static void indoorToIndoorNavigation(Location origin, PoiData destination) {
        Log.getInstance().debug(TAG, "Enter, indoorToIndoorNavigation()");
        try {
            DestinationPoi dest = new DestinationPoi(destination, LocationMode.INDOOR_MODE);
            destinations.add(dest);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Log.getInstance().debug(TAG, "Exit, indoorToIndoorNavigation()");
    }

    public static void outdoorToOutdoorNavigation(Location origin, PoiData destination) {
        Log.getInstance().debug(TAG, "Enter, outdoorToOutdoorNavigation()");
        try {
            DestinationPoi dest = new DestinationPoi(destination, LocationMode.OUTDOOR_MODE);
            destinations.add(dest);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Log.getInstance().debug(TAG, "Exit, outdoorToOutdoorNavigation()");
    }

    private static void outdoorToIndoorNavigation(Location origin, PoiData destination) {
        Log.getInstance().debug(TAG, "Enter, outdoorToIndoorNavigation()");
        try {
            if (origin != null && destination != null) {
                PoiData exitpoi = null;
                LatLng originlatlng = new LatLng(origin.getLat(), origin.getLon());
                if (PropertyHolder.getInstance().isChooseShortestRoute()) {
                    exitpoi = getShortestExitPoint(origin, destination, destination.getCampusID(), destination.getFacilityID());
                } else {
                    String facid = "spreo_outdoor";
                    ExitsSelectionType exitstype = ExitsSelectionManager.getInstance().getExitSelectionType(facid);
                    if (exitstype == ExitsSelectionType.CLOSE_TO_DESTINATION) {
                        exitpoi = getExitPoi(destination.getPoint(), destination.getZ(), destination.getCampusID(), destination.getFacilityID(), false);
                    } else {
                        exitpoi = getExitPoi(originlatlng, destination.getCampusID(), destination.getFacilityID());
                    }
                }


                if (exitpoi != null) {
                    exits.put(exitpoi, RouteCalculationHelper.TYPE_ENTRANCE);
                    DestinationPoi outdest = new DestinationPoi(exitpoi,
                            LocationMode.OUTDOOR_MODE);
                    DestinationPoi indest = new DestinationPoi(destination,
                            LocationMode.INDOOR_MODE);
                    destinations.add(outdest);
                    destinations.add(indest);


                    float[] results = new float[1];
                    android.location.Location.distanceBetween(originlatlng.latitude, originlatlng.longitude, exitpoi.getPoiLatitude(), exitpoi.getPoiLongitude(), results);
                    float distance = results[0];
                    if (distance > googleNavDistance) {
                        LatLng exitlatlng = new LatLng(exitpoi.getPoiLatitude(), exitpoi.getPoiLongitude());
                        GoogleNavigationUtil.getInstance().notifyStart(exitlatlng);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Log.getInstance().debug(TAG, "Exit, outdoorToIndoorNavigation()");
    }

    private static void indoorToOutdoorNavigation(Location origin, PoiData destination) {
        Log.getInstance().debug(TAG, "Enter, indoorToOutdoorNavigation()");
        try {
            LatLng destlatlng = new LatLng(destination.getPoiLatitude(), destination.getPoiLongitude());
            PoiData exitpoi = null;
            if (PropertyHolder.getInstance().isChooseShortestRoute()) {
                exitpoi = getShortestExitPoint(origin, destination, origin.getCampusId(), origin.getFacilityId());
            } else {
                String facid = origin.getFacilityId();
                ExitsSelectionType exitstype = ExitsSelectionManager.getInstance().getExitSelectionType(facid);
                if (exitstype == ExitsSelectionType.CLOSE_TO_DESTINATION) {
                    exitpoi = getExitPoi(destlatlng, origin.getCampusId(), origin.getFacilityId());
                } else {
                    PointF originpoint = new PointF((float) origin.getX(), (float) origin.getY());
                    exitpoi = getExitPoi(originpoint, origin.getZ(), origin.getCampusId(), origin.getFacilityId(), false);
                }
            }



            if (exitpoi != null) {
                exits.put(exitpoi, RouteCalculationHelper.TYPE_EXIT);
                DestinationPoi indest = new DestinationPoi(exitpoi,
                        LocationMode.INDOOR_MODE);
                DestinationPoi outdest = new DestinationPoi(destination,
                        LocationMode.OUTDOOR_MODE);
                destinations.add(indest);
                destinations.add(outdest);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Log.getInstance().debug(TAG, "Exit, indoorToOutdoorNavigation()");

    }

    private static PoiData getShortestExitPoint(Location origin, PoiData destination, String campusid, String facilityid) {
        PoiData result = null;
        try {
            LatLng lorigin = null;
            LatLng ldestination = null;
            HashMap<Double, IPoi> map = new HashMap<>();
            boolean outdoororigin = false;
            if (origin.getLocationType() == LocationMode.INDOOR_MODE) {
                lorigin = getLatLng(origin);
            } else {
                lorigin = new LatLng(origin.getLat(), origin.getLon());
                outdoororigin = true;
            }
            if (destination.getPoiNavigationType().equals("internal") ) {
                ldestination = getLatLng(destination);
            } else {
                ldestination = new LatLng(destination.getPoiLatitude(), destination.getPoiLongitude());
            }

            List<IPoi> lexits = getExits(campusid, facilityid);
            for (IPoi ex : lexits) {
                LatLng lex = getLatLng(ex);
                    List<LatLng> llist = new ArrayList<>();
                    llist.add(lorigin);
                    llist.add(lex);
                    llist.add(ldestination);
                    double d = euclideanDistanceSum(llist);
                    map.put(d, ex);
            }
            if (!map.isEmpty()) {
                IPoi tmp = null;
                if (map.size() > 1) {
                    while (map.size() > 2) {
                        double max = Collections.max(map.keySet());
                        map.remove(max);
                    }
                    HashMap<Double, IPoi> newmap = new HashMap<>();
                    for (IPoi poi : map.values()) {
                        if (outdoororigin) {
                            LatLng l2 = getLatLng(poi);
                            double d = getKMLLength(lorigin, l2);
                            newmap.put(d, poi) ;
                        } else {
                            double d = getKMLLength(poi, destination);
                            newmap.put(d, poi) ;
                        }
                    }
                    double min = Collections.min(newmap.keySet());
                    tmp = newmap.get(min);
                } else {
                    double min = Collections.min(map.keySet());
                    tmp = map.get(min);
                }

                if (tmp != null && tmp instanceof PoiData) {
                    result = (PoiData) tmp;
                }

            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public static Instruction playDestinationSound() {
        Log.getInstance().debug(TAG, "Enter, playDestinationSound()");
        List<Instruction> cinstructions = InstructionBuilder.getInstance().getCurrentInstructions();
        Instruction destinstruction = null;
        try {
            if (cinstructions != null && cinstructions.size() > 0) {
                destinstruction = cinstructions.get(cinstructions.size() - 1);
            }
            if (destinstruction != null && destinstruction.getType() == Instruction.TYPE_DESTINATION && !destinstruction.hasPlayed()) {
                destinstruction.setPlayed(true);
                if (!PropertyHolder.getInstance().isNavigationInstructionsSoundMute()) {
                    SoundPlayer.getInstance().play(destinstruction.getSound());
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Log.getInstance().debug(TAG, "Exit, playDestinationSound()");
        return destinstruction;
    }

    public static List<DestinationPoi> getDestinations() {
        return destinations;
    }

    public static void clearDestinations() {
        destinations.clear();
        navOrigin = null;
        exits.clear();
        bridgeExits.clear();
    }

    public static PoiData getFacilityDestination(String facid) {
        PoiData result = null;
        try {
            if (facid != null && destinations != null && !destinations.isEmpty()) {
                for (DestinationPoi o : destinations) {
                    if (o != null && o.getMode() == LocationMode.INDOOR_MODE) {
                        if (o.getPoi() != null && o.getFacilityId() != null && o.getFacilityId().equals(facid)) {
                            result = o.getPoi();
                            break;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public static PoiData getOutDoorDestination() {
        PoiData result = null;
        try {
            if (destinations != null && !destinations.isEmpty()) {
                for (DestinationPoi o : destinations) {
                    if (o != null && o.getMode() == LocationMode.OUTDOOR_MODE) {
                        result = o.getPoi();
                        break;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    private static PoiData getOppositeSideForRouteBetween(PoiData entranceOrExit, String campusID, String facilityID){
        BridgeData bridges = ProjectConf.getInstance().getBridges();
        Bridge bridge = bridges.getBridge(entranceOrExit);
        if(bridge != null)
            return bridge.getOppositePoint(entranceOrExit);

        LatLng latlng = new LatLng(entranceOrExit.getPoiLatitude(), entranceOrExit.getPoiLongitude());
        return getExitPoi(latlng, campusID, facilityID);
    }

    private static PoiData getExitPoi(PointF p, double z, String campusId, String facilityId, boolean considerBridges) {
        PoiData result = null;
        try {
            List<IPoi> allPoi = ProjectConf.getInstance().getAllFacilityPoisList(campusId, facilityId);
            List<IPoi> exitpois = new ArrayList<IPoi>();
            for (IPoi o : allPoi) {
                if (o.getPoiID().contains("idr") && o.getPoiNavigationType().equals("internal")) {
                    exitpois.add(o);
                }
            }
            if (exitpois.size() > 0) {
                IPoi tmp = findCloseExit(p, z, exitpois, considerBridges);
                if (tmp instanceof PoiData) {
                    result = (PoiData) tmp;
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public static IPoi findCloseExit(PointF p, double z, List<? extends IPoi> exitpois, boolean considerBridges) {
        if(!considerBridges) {
            exitpois = ProjectConf.getInstance().getBridges().removeBridgesSides(exitpois);
        }

        List<IPoi> sameFloorExits = new ArrayList<>();
        for (IPoi exit : exitpois) {
            if(Math.abs(exit.getZ() - z) < 0.5d) { // the same floor
                sameFloorExits.add(exit);
            }
        }

        if(sameFloorExits.size() > 0)
            return findCloseExit(p, sameFloorExits);
        else
            return findCloseExit(p, exitpois);
    }

    private static IPoi findCloseExit(PointF p, List<? extends IPoi> exitpois) {
        IPoi result = null;
        try {
            double mind = Double.MAX_VALUE;
            for (IPoi o : exitpois) {
                PointF exitp = o.getPoint();
                double distance = MathUtils.distance(p, exitp);
                if (distance < mind) {
                    mind = distance;
                    result = o;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public static PoiData getExitPoi(LatLng latlng, String campusId, String facilityId) {
        PoiData result = null;
        try {
            List<IPoi> allPoi = ProjectConf.getInstance().getAllFacilityPoisList(campusId, facilityId);
            List<IPoi> exitpois = new ArrayList<IPoi>();
            for (IPoi o : allPoi) {
                if (o.getPoiID().contains("idr") && o.getPoiNavigationType().equals("internal")) {
                    exitpois.add(o);
                }
            }
            if (exitpois.size() > 0) {
                IPoi tmp = findCloseExit(latlng, exitpois);
                if (tmp instanceof PoiData) {
                    result = (PoiData) tmp;
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public static IPoi findCloseExit(LatLng latlng, List<? extends IPoi> exitpois) {
        return findCloseExit(latlng, exitpois, true);
    }

    public static IPoi findCloseExit(LatLng latlng, List<? extends IPoi> exitpois, boolean removeBridges) {
        if (removeBridges) {
            exitpois = ProjectConf.getInstance().getBridges().removeBridgesSides(exitpois);
        }
        IPoi result = null;
        try {
            float mind = Float.MAX_VALUE;
            float[] resultsCache = new float[1];
            if (latlng != null) {
                for (IPoi o : exitpois) {
                    android.location.Location.distanceBetween(
                            o.getPoiLatitude(),
                            o.getPoiLongitude(),
                            latlng.latitude,
                            latlng.longitude,
                            resultsCache);
                    float distance = resultsCache[0];
                    if (distance < mind) {
                        mind = distance;
                        result = o;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public static HashMap<IPoi, String> getExits() {
        return exits;
    }

    public static List<Bridge> getBridgeExits() {
        return bridgeExits;
    }

    public static Location getOrigin() {
        return navOrigin;
    }

    public static double euclideanDistanceSum(List<LatLng> list) {
        double result = 0;
        try {
            for (LatLng l : list) {
                if (list.indexOf(l) < list.size() - 1) {
                    LatLng l2 = list.get(list.indexOf(l) + 1);
                    double d = MathUtils.distance(l, l2);
                    result += d;
                }
            }
        } catch (Throwable t) {
            result = Double.MAX_VALUE;
            t.printStackTrace();
        }
        return result;
    }

}
