package com.mlins.dualmap;

import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.aStar.CampusNavigationPath;
import com.mlins.aStar.GisSegment;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.project.bridges.Bridge;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PoiData;
import com.mlins.utils.gis.Location;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.sdk.data.SpreoDataProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ExitsSelectionAlgorithm {
    public List<Bridge> getBridgeExits() {
        return bridgeExits;
    }

    public void setBridgeExits(List<Bridge> bridgeExits) {
        this.bridgeExits = bridgeExits;
    }

    public HashMap<IPoi, String> getExits() {
        return exits;
    }

    public void setExits(HashMap<IPoi, String> exits) {
        this.exits = exits;
    }

    // the bridge exits
    private List<Bridge> bridgeExits = new ArrayList<Bridge>();
    // the regular exits (not bridges)
    private HashMap<IPoi, String> exits = new HashMap<>();
    // all the relevant bridges for the search
    private List<Bridge> relevantBridges = new ArrayList<>();
    // the exits when the search start from the origin
    private List<DestinationPoi> originsidedest = new ArrayList<>();
    // the exits when the search start from the destination
    private List<DestinationPoi> destinationsidedest = new ArrayList<>();

    // the last regular exit (not bridge) and the relevant location
    PossibleExit lastRealExit = null;
    Location lastRealExitOrigin = null;

    public ExitsSelectionAlgorithm() {
        //add all the bridges to the search
        relevantBridges.addAll(ProjectConf.getInstance().getBridges().getBridges());
    }

    public List<DestinationPoi> OutdoorToIndoor(Location origin, PoiData destination) {
        List<DestinationPoi> result = new ArrayList<>();
        PoiData originpoi = convertToPoi(origin);
        Location destloc = new Location(destination.getLocation());
        // start the search from the destination
        Location destsideloc = indoorToOutdoorWithBridges(destloc, originpoi, false);
        if (destsideloc != null) {
            PoiData destpoi = convertToPoi(destsideloc);
            // indoor to outdoor without bridges
            PoiData exitpoi = getShortestExitPoint(origin, destpoi, destpoi.getCampusID(), destpoi.getFacilityID());
            if (exitpoi != null) {
                exits.put(exitpoi, RouteCalculationHelper.TYPE_ENTRANCE);
                DestinationPoi outdest = new DestinationPoi(exitpoi,
                        LocationMode.OUTDOOR_MODE);
                DestinationPoi indest = new DestinationPoi(destpoi,
                        LocationMode.INDOOR_MODE);
                result.add(outdest);
                result.add(indest);

            }

            if (!destinationsidedest.isEmpty()) {
                destinationsidedest.remove(destinationsidedest.get(destinationsidedest.size() - 1));
                // reverse the order of the results because we started from the destination
                Collections.reverse(destinationsidedest);
                result.addAll(destinationsidedest);
                DestinationPoi indest = new DestinationPoi(destination,
                        LocationMode.INDOOR_MODE);
                result.add(indest);
            }
        }
        return result;
    }

    public List<DestinationPoi> indoorToOutdoor(Location origin, PoiData destination) {
        List<DestinationPoi> result = new ArrayList<>();
        // start the search from the origin
        Location originsideloc = indoorToOutdoorWithBridges(origin, destination, true);
        if (originsideloc != null) {
            if (!originsidedest.isEmpty()) {
                result.addAll(originsidedest);
            }
        }

        // indoor to outdoor without bridges
        PoiData exitpoi = getShortestExitPoint(originsideloc, destination, originsideloc.getCampusId(), originsideloc.getFacilityId());
        if (exitpoi != null) {
            exits.put(exitpoi, RouteCalculationHelper.TYPE_EXIT);
            DestinationPoi indest = new DestinationPoi(exitpoi,
                    LocationMode.INDOOR_MODE);
            DestinationPoi outdest = new DestinationPoi(destination,
                    LocationMode.OUTDOOR_MODE);
            result.add(indest);
            result.add(outdest);
        }
        return result;
    }


    public List<DestinationPoi> indoorToOtherIndoor(Location origin, PoiData destination) {
        List<DestinationPoi> result = new ArrayList<>();
        // start the search from the origin
        Location originsideloc = indoorToOtherIndoorWithBridges(origin, destination, true);
        if (originsideloc != null) {
            if (!originsidedest.isEmpty()) {
                result.addAll(originsidedest);
            }
            String onsesidefac = originsideloc.getFacilityId();
            String originfac = origin.getFacilityId();
            String destfac = destination.getFacilityID();
            if (onsesidefac != null && destfac != null) {
                if (onsesidefac.equals(destfac)) {
                    // we found connection to the destination facility
                    DestinationPoi indest = indoorToIndoorNavigation(originsideloc, destination);
                    if (indest != null) {
                        result.add(indest);
                    }
                } else {
                    // we didn't find connection to the destination facility
                    PoiData originpoi = convertToPoi(origin);
                    Location destloc = new Location(destination.getLocation());
                    // reset the relevant bridges
                    relevantBridges.clear();
                    relevantBridges.addAll(ProjectConf.getInstance().getBridges().getBridges());
                    // start the search from the destination
                    Location destinationsideloc = indoorToOtherIndoorWithBridges(destloc, originpoi, false);
                    if (destinationsideloc != null) {
                        if (!destinationsidedest.isEmpty()) {
                            if (destinationsideloc.getFacilityId().equals(originfac)) {
                                // we found connection to the origin facility.
                                // clear the result of the previous search from the origin.
                               result.clear();
                            } else {
                                // we didn't find connection to the origin facility
                                PoiData destpoi = convertToPoi(destinationsideloc);
                                if (destpoi != null) {
                                    // indoor to other indoor without bridges
                                    List<DestinationPoi> tmp = indoorToOtherIndoorWithoutBridges(originsideloc, destpoi);
                                    result.addAll(tmp);

                                    destinationsidedest.remove(destinationsidedest.get(destinationsidedest.size() - 1));
                                }
                            }

                            // reverse the order of the results because we started from the destination
                            Collections.reverse(destinationsidedest);
                            result.addAll(destinationsidedest);
                            DestinationPoi indest = new DestinationPoi(destination,
                                    LocationMode.INDOOR_MODE);
                            result.add(indest);
                        } else {
                            // indoor to other indoor without bridges
                            List<DestinationPoi> tmp = indoorToOtherIndoorWithoutBridges(originsideloc, destination);
                            result.addAll(tmp);
                        }

                    } else {
                        // indoor to other indoor without bridges
                        List<DestinationPoi> tmp = indoorToOtherIndoorWithoutBridges(origin, destination);
                        result.addAll(tmp);
                    }
                }
            }
        }
        return result;
    }

    private Location indoorToOutdoorWithBridges(Location origin, PoiData destination, boolean isoriginside) {

        String originfac = origin.getFacilityId();
        double originfloor = origin.getZ();
        if (originfac == null) {
            return null;
        }

        try {
            if (relevantBridges == null  || relevantBridges.isEmpty()) {
                // if we don't have relevant bridges for the search we stop the search.
            } else {
                // all the bridges in the facility
                List<Bridge> candidates = getBridgesInfacility(originfac);
                if (candidates == null || candidates.isEmpty()) {
                    // if we don't have bridges in the facility we stop the search.
                } else {
                    // find the best exit
                    List<PossibleExit> possibleexit = getBestNextExit(origin, destination, origin.getCampusId(), origin.getFacilityId(), candidates, false);
                    if (possibleexit != null && !possibleexit.isEmpty()) {
                        PossibleExit shortest = possibleexit.get(0);
                        if (shortest != null) {
                            if (shortest.getBridge() != null) {
                                // if the best exit is a bridge we search for the best bridge according to the floors
                                PossibleExit best = getBestBridgeAccordingToFloors(possibleexit, shortest, (int)originfloor, getEntranceFloor());
                                if (lastRealExit == null || best.getDistance() < lastRealExit.getDistance() ) {
                                    // if the last regular exit (not bridge) is not closer to the destination
                                    Bridge bestbridge = best.getBridge();
                                    Bridge orderedbridge = getcorrectBridgeOrder(bestbridge, originfac);
                                    if (isoriginside) {
                                        // if we started the search from origin
                                        bridgeExits.add(orderedbridge);
                                    } else {
                                        // if we started the search from destination
                                        bridgeExits.add(reverseBridgeOrder(orderedbridge));
                                    }
                                    DestinationPoi p1 = new DestinationPoi(orderedbridge.getPoint1(),
                                            LocationMode.INDOOR_MODE);
                                    DestinationPoi p2 = new DestinationPoi(orderedbridge.getPoint2(),
                                            LocationMode.INDOOR_MODE);
                                    if (isoriginside) {
                                        // if we started the search from origin
                                        // add the bridge to the result
                                        originsidedest.add(p1);
                                        originsidedest.add(p2);
                                    } else {
                                        // if we started the search from destination
                                        // add the bridge to the result
                                        destinationsidedest.add(p1);
                                        destinationsidedest.add(p2);
                                    }
                                    Bridge orderedbestbridge = getcorrectBridgeOrder(bestbridge, originfac);
                                    // update the origin to the new point
                                    Location neworigin = new Location(orderedbestbridge.getPoint2());
                                    // remove the bridges that connects to this facility from the relevant bridges
                                    removeBridges(originfac);

                                    // save the best regular exit and the relevant location
                                    lastRealExit = getShortestWithoutBridges(possibleexit);
                                    lastRealExitOrigin = origin;

                                    // continue the search
                                    return indoorToOutdoorWithBridges(neworigin, destination, isoriginside);
                                } else {
                                    // if the last regular exit (not bridge) is closer to the destination
                                    // we go back to the previous step. remove the last bridge from the result.
                                    if (isoriginside) {
                                        // if we started the search from origin
                                        if (originsidedest.size() >= 2) {
                                            originsidedest.remove(originsidedest.size() - 1);
                                            originsidedest.remove(originsidedest.size() - 1);
                                        }
                                    } else {
                                        // if we started the search from destination
                                        if (destinationsidedest.size() >= 2) {
                                            destinationsidedest.remove(destinationsidedest.size() - 1);
                                            destinationsidedest.remove(destinationsidedest.size() - 1);
                                        }
                                    }
                                    if (lastRealExitOrigin != null) {
                                        // return the privious location before the last step
                                        return lastRealExitOrigin;
                                    }
                                }
                            } else {
                                if (lastRealExit != null && lastRealExit.getDistance() < shortest.getDistance()) {
                                    // if the last regular exit (not bridge) is closer to the destination
                                    // we go back to the previous step. remove the last bridge from the result.
                                    if (isoriginside) {
                                        // if we started the search from origin
                                        if (originsidedest.size() >= 2) {
                                            originsidedest.remove(originsidedest.size() - 1);
                                            originsidedest.remove(originsidedest.size() - 1);
                                        }
                                    } else {
                                        // if we started the search from destination
                                        if (destinationsidedest.size() >= 2) {
                                            destinationsidedest.remove(destinationsidedest.size() - 1);
                                            destinationsidedest.remove(destinationsidedest.size() - 1);
                                        }
                                    }
                                    if (lastRealExitOrigin != null) {
                                        // return the privious location before the last step
                                        return lastRealExitOrigin;
                                    }
                                }
                            }
                        }
                    }

                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return origin;
    }

    private PossibleExit getShortestWithoutBridges(List<PossibleExit> possibleexit) {
        PossibleExit result = null;
        for (PossibleExit o : possibleexit) {
            if (o.getBridge() == null) {
                result = o;
                break;
            }
        }
        return  result;
    }


    private Location indoorToOtherIndoorWithBridges(Location origin, PoiData destination, boolean isoriginside) {

        String originfac = origin.getFacilityId();
        String destfac = destination.getFacilityID();
        double originfloor = origin.getZ();
        double destinationfloor = destination.getZ();
        if (originfac == null || destfac == null) {
            return null;
        }

        if (originfac.equals(destfac)) {
        // if we found connection to the destination facility we stop the search.
            return origin;
        }

        try {
            if (relevantBridges == null  || relevantBridges.isEmpty()) {
                // if we don't have relevant bridges for the search we stop the search.
            } else {
                List<Bridge> candidates = getBridgesInfacility(originfac);
                if (candidates == null || candidates.isEmpty()) {
                    // if we don't have bridges in the facility we stop the search.
                } else {
                    // find the best exit
                    // check if we have bridges that connects to the destination
                    List<Bridge> connectingbridges = getConnectingBridge(candidates, originfac, destfac);
                    if (connectingbridges != null && !connectingbridges.isEmpty()) {
                        // if we have bridges that connects to the destination
                        // find the best exit
                        List<PossibleExit> possibleexit = getBestNextExit(origin, destination, origin.getCampusId(), origin.getFacilityId(), connectingbridges, true);
                        if (possibleexit != null && !possibleexit.isEmpty()) {
                            PossibleExit shortest = possibleexit.get(0);
                            if (shortest != null) {
                                if (shortest.getBridge() != null) {
                                    // if the best exit is a bridge we search for the best bridge according to the floors
                                    PossibleExit best = getBestBridgeAccordingToFloors(possibleexit, shortest, (int)originfloor, (int)destinationfloor);
                                    Bridge bestbridge = best.getBridge();
                                    Bridge orderedbridge = getcorrectBridgeOrder(bestbridge, originfac);
                                    if (isoriginside) {
                                        // if we started the search from origin
                                        bridgeExits.add(orderedbridge);
                                    } else {
                                        // if we started the search from destination
                                        bridgeExits.add(reverseBridgeOrder(orderedbridge));
                                    }
                                    DestinationPoi p1 = new DestinationPoi(orderedbridge.getPoint1(),
                                            LocationMode.INDOOR_MODE);
                                    DestinationPoi p2 = new DestinationPoi(orderedbridge.getPoint2(),
                                            LocationMode.INDOOR_MODE);
                                    if (isoriginside) {
                                        // if we started the search from origin
                                        // add the bridge to the result
                                        originsidedest.add(p1);
                                        originsidedest.add(p2);
                                    } else {
                                        // if we started the search from destination
                                        // add the bridge to the result
                                        destinationsidedest.add(p1);
                                        destinationsidedest.add(p2);
                                    }

                                    // update the origin to the new point
                                    origin =  new Location(orderedbridge.getPoint2());
                                } else {
                                    // if the best exit is not a bridge we stop the search
                                }

                            }
                            // stop the search
                        }
                        // stop the search

                    } else {
                        // if we don't have bridges that connects to the destination
                        // find the best exit
                        List<PossibleExit> possibleexit = getBestNextExit(origin, destination, origin.getCampusId(), origin.getFacilityId(), candidates, false);
                        if (possibleexit != null && !possibleexit.isEmpty()) {
                            PossibleExit shortest = possibleexit.get(0);
                            if (shortest != null) {
                                if (shortest.getBridge() != null) {
                                    // if the best exit is a bridge we search for the best bridge according to the floors
                                    PossibleExit best = getBestBridgeAccordingToFloors(possibleexit, shortest, (int)originfloor, (int)destinationfloor);
                                    Bridge bestbridge = best.getBridge();
                                    Bridge orderedbridge = getcorrectBridgeOrder(bestbridge, originfac);
                                    if (isoriginside) {
                                        // if we started the search from origin
                                        bridgeExits.add(orderedbridge);
                                    } else {
                                        // if we started the search from destination
                                        bridgeExits.add(reverseBridgeOrder(orderedbridge));
                                    }
                                    DestinationPoi p1 = new DestinationPoi(orderedbridge.getPoint1(),
                                            LocationMode.INDOOR_MODE);
                                    DestinationPoi p2 = new DestinationPoi(orderedbridge.getPoint2(),
                                            LocationMode.INDOOR_MODE);
                                    if (isoriginside) {
                                        // if we started the search from origin
                                        // add the bridge to the result
                                        originsidedest.add(p1);
                                        originsidedest.add(p2);
                                    } else {
                                        // if we started the search from destination
                                        // add the bridge to the result
                                        destinationsidedest.add(p1);
                                        destinationsidedest.add(p2);
                                    }
                                    Bridge orderedbestbridge = getcorrectBridgeOrder(bestbridge, originfac);
                                    // update the origin for the next step
                                    Location neworigin = new Location(orderedbestbridge.getPoint2());
                                    // remove the bridges that connects to this facility from the relevant bridges
                                    removeBridges(originfac);
                                    // continue the search
                                    return indoorToOtherIndoorWithBridges(neworigin, destination, isoriginside);
                                } else {
                                    // if the best exit is not a bridge we stop the search
                                }
                            }
                            // stop the search
                        }
                        // stop the search

                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return origin;
    }

    private List<DestinationPoi> indoorToOtherIndoorWithoutBridges(Location origin, PoiData destination) {
        List<DestinationPoi> result = new ArrayList<>();
        try {
            String fac2id = destination.getFacilityID();
            if (fac2id != null) {
                PoiData exitpoi = null;
                PoiData enterancepoi = null;

                List<PoiData> tmp = getShortestExitAndEnterPoints(origin, destination);
                if (tmp != null && tmp.size() > 1){
                    exitpoi = tmp.get(0);
                    enterancepoi = tmp.get(1);
                }

                if (exitpoi != null && enterancepoi != null) {
                    exits.put(exitpoi, RouteCalculationHelper.TYPE_EXIT);
                    exits.put(enterancepoi, RouteCalculationHelper.TYPE_ENTRANCE);
                    DestinationPoi indest1 = new DestinationPoi(exitpoi,
                            LocationMode.INDOOR_MODE);

                    DestinationPoi outdest = new DestinationPoi(enterancepoi,
                            LocationMode.OUTDOOR_MODE);

                    DestinationPoi indest2 = new DestinationPoi(destination,
                            LocationMode.INDOOR_MODE);

                    result.add(indest1);
                    result.add(outdest);
                    result.add(indest2);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return result;

    }

    public int getEntranceFloor() {
        int result = 0;
        Campus campus = ProjectConf.getInstance().getSelectedCampus();
        if (campus != null) {
            String facilityId = SpreoDataProvider.getFloorPickerFacilityId();
            FacilityConf fac = campus.getFacilityConf(facilityId);
            if (fac != null) {
                result = fac.getEntranceFloor();
            }
        }
        return result;
    }

    private PoiData convertToPoi(Location loc) {
        PoiData result = null;
        if (loc.getType() == Location.TYPE_INTERNAL) {
            PointF point = new PointF((float) loc.getX(), (float) loc.getY());
            result = new PoiData(point);
            result.setZ(loc.getZ());
            result.setCampusID(loc.getCampusId());
            result.setFacilityID(loc.getFacilityId());
        } else if (loc.getType() == Location.TYPE_EXTERNAL) {
            double lat = loc.getLat();
            double lon = loc.getLon();
            result = new PoiData();
            result.setPoiLatitude(lat);
            result.setPoiLongitude(lon);
            result.setPoiNavigationType("external");
        }
        return  result;
    }

    private  List<PoiData> getShortestExitAndEnterPoints(Location origin, PoiData destination) {
        List<PoiData> result = new ArrayList<>();
        try {
            HashMap<Double, List<IPoi>> map = new HashMap<>();
            LatLng lorigin = getLatLng(origin);
            LatLng ldestination = getLatLng(destination);
            List<? extends IPoi> lexits = getExits(origin.getCampusId(), origin.getFacilityId());
            lexits = ProjectConf.getInstance().getBridges().removeBridgesSides(lexits);
            List<? extends IPoi> lentrances = getExits(destination.getCampusID(), destination.getFacilityID());
            lentrances = ProjectConf.getInstance().getBridges().removeBridgesSides(lentrances);
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
                    while (map.size() > 3) {
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

    private PoiData getShortestExitPoint(Location origin, PoiData destination, String campusid, String facilityid) {
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

            List<? extends IPoi> lexits = getExits(campusid, facilityid);
            lexits = ProjectConf.getInstance().getBridges().removeBridgesSides(lexits);
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
                    while (map.size() > 3) {
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

    private double getKMLLength(IPoi p1, IPoi p2) {
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

    private double getKMLLength(LatLng lp1, LatLng lp2) {
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

    private void removeBridges(String fac) {
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

    public List<Bridge> getBridgesInfacility(String fac) {
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

    private List<Bridge> getConnectingBridge(List<Bridge> bridges, String fac1, String fac2) {
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

    private List<PossibleExit> getBestNextExit(Location origin, PoiData destination, String campusid, String facilityid, List<Bridge> bridges, boolean isconnected) {
        List<PossibleExit> result = new ArrayList<PossibleExit>();
        try {


            for (Bridge b : bridges) {
                PossibleExit pe = new PossibleExit(b, facilityid);
                if (pe.getPoi() != null) {
                    result.add(pe);
                }
            }

            LatLng lorigin = null;
            LatLng ldestination = null;
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

            if (!isconnected) {
                List<? extends IPoi> lexits = getExits(campusid, facilityid);
                lexits = ProjectConf.getInstance().getBridges().removeBridgesSides(lexits);

                for (IPoi o : lexits) {
                    PossibleExit pe = new PossibleExit(o);
                    result.add(pe);
                }
            }

            for (PossibleExit o : result) {
                IPoi ex = o.getPoi();
                LatLng lex = getLatLng(ex);
                List<LatLng> llist = new ArrayList<>();
//                llist.add(lorigin);
                llist.add(lex);
                llist.add(ldestination);
                double d = euclideanDistanceSum(llist);
                o.setDistance(d);
            }

            Collections.sort(result, new Comparator<PossibleExit>() {
                @Override
                public int compare(PossibleExit p1, PossibleExit p2) {
                    double d1 = p1.getDistance();
                    double d2 = p2.getDistance();
                    return Double.compare(d1, d2);
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private LatLng getLatLng(PoiData poi) {
        LatLng result = null;
        try {
            result = ConvertingUtils.convertToLatlng(poi.getX(), poi.getY(), (int)poi.getZ(), poi.getFacilityID());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private LatLng getLatLng(IPoi poi) {
        LatLng result = null;
        try {
            result = ConvertingUtils.convertToLatlng(poi.getX(), poi.getY(), (int)poi.getZ(), poi.getFacilityID());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private LatLng getLatLng(Location loc) {
        LatLng result = null;
        try {
            result = ConvertingUtils.convertToLatlng(loc.getX(), loc.getY(), (int)loc.getZ(), loc.getFacilityId());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public double euclideanDistanceSum(List<LatLng> list) {
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

    private List<IPoi> getExits(String campusId, String facilityId) {
        List<IPoi> result = new ArrayList<IPoi>();
        List<IPoi> allPoi = ProjectConf.getInstance().getAllFacilityPoisList(campusId, facilityId);
        for (IPoi o : allPoi) {
            if (o.getPoiID().contains("idr") && o.getPoiNavigationType().equals("internal")) {
                result.add(o);
            }
        }
        return result;
    }

    private Bridge reverseBridgeOrder(Bridge bridge) {
        Bridge result = bridge;
            result = new Bridge(bridge.getID(), bridge.getPoint2(), bridge.getPoint1());
        return result;
    }

    private Bridge getcorrectBridgeOrder(Bridge bridge, String firstfacility) {
        Bridge result = bridge;
        if (firstfacility.equals(bridge.getPoint2().getFacilityID())) {
            result = new Bridge(bridge.getID(), bridge.getPoint2(), bridge.getPoint1());
        }
        return result;
    }

    private PossibleExit getBestBridgeAccordingToFloors(List<PossibleExit> possibleexit, PossibleExit selected, int originfloor, int destinationfloor) {
        PossibleExit result = selected;
        try {

                List<PossibleExit> origintodest = new ArrayList<>();
                List<PossibleExit> orgintoorigin = new ArrayList<>();
                List<PossibleExit> desttosest = new ArrayList<>();
                List<PossibleExit> fromorigin = new ArrayList<>();
                List<PossibleExit> todest = new ArrayList<>();


                for (PossibleExit o : possibleexit) {
                    if (o.getBridge() != null && o.getToFacility().equals(selected.getToFacility())) {
                        if (o.getFromFloor() == originfloor && o.getToFloor() == destinationfloor) {
                            origintodest.add(o);
                        } else if (o.getFromFloor() == originfloor && o.getToFloor() == originfloor) {
                            orgintoorigin.add(o);
                        } else if (o.getFromFloor() == destinationfloor && o.getToFloor() == destinationfloor) {
                            desttosest.add(o);
                        } else if (o.getFromFloor() == originfloor) {
                            fromorigin.add(o);
                        } else if (o.getToFloor() == destinationfloor) {
                            todest.add(o);
                        }
                    }
                }

                if (!origintodest.isEmpty()) {
                    Collections.sort(origintodest, new Comparator<PossibleExit>() {
                        @Override
                        public int compare(PossibleExit p1, PossibleExit p2) {
                            double d1 = p1.getDistance();
                            double d2 = p2.getDistance();
                            return Double.compare(d1, d2);
                        }
                    });
                    result = origintodest.get(0);
                } else if (!orgintoorigin.isEmpty()) {
                    Collections.sort(orgintoorigin, new Comparator<PossibleExit>() {
                        @Override
                        public int compare(PossibleExit p1, PossibleExit p2) {
                            double d1 = p1.getDistance();
                            double d2 = p2.getDistance();
                            return Double.compare(d1, d2);
                        }
                    });
                    result = orgintoorigin.get(0);
                } else if (!desttosest.isEmpty()) {
                    Collections.sort(desttosest, new Comparator<PossibleExit>() {
                        @Override
                        public int compare(PossibleExit p1, PossibleExit p2) {
                            double d1 = p1.getDistance();
                            double d2 = p2.getDistance();
                            return Double.compare(d1, d2);
                        }
                    });
                    result = desttosest.get(0);
                } else if (!fromorigin.isEmpty()) {
                    Collections.sort(fromorigin, new Comparator<PossibleExit>() {
                        @Override
                        public int compare(PossibleExit p1, PossibleExit p2) {
                            double d1 = p1.getDistance();
                            double d2 = p2.getDistance();
                            return Double.compare(d1, d2);
                        }
                    });
                    result = fromorigin.get(0);
                } else if (!todest.isEmpty()) {
                    Collections.sort(todest, new Comparator<PossibleExit>() {
                        @Override
                        public int compare(PossibleExit p1, PossibleExit p2) {
                            double d1 = p1.getDistance();
                            double d2 = p2.getDistance();
                            return Double.compare(d1, d2);
                        }
                    });
                    result = todest.get(0);
                }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public DestinationPoi indoorToIndoorNavigation(Location origin, PoiData destination) {
        DestinationPoi result = null;
        try {
            result = new DestinationPoi(destination, LocationMode.INDOOR_MODE);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public DestinationPoi outdoorToOutdoorNavigation(Location origin, PoiData destination) {
        DestinationPoi result = null;
        try {
            result = new DestinationPoi(destination, LocationMode.OUTDOOR_MODE);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }
}
