package com.mlins.utils.distance;

import android.graphics.PointF;
import android.util.Log;

import com.mlins.dualmap.DualMapNavUtil;
import com.mlins.project.ProjectConf;
import com.mlins.switchfloor.SwitchFloorHolder;
import com.mlins.switchfloor.SwitchFloorObj;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PoiData;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.List;

public class RoughRouteCalculator {

    
    public static List<ILocation> getRoutePoints(ILocation origin, ILocation destination) {
        List<ILocation> result = new ArrayList<>();
        try {
            result.add(origin);
            do {
                addRoutePoints(origin, destination, result);
                origin = result.get(result.size() - 1);
            } while (!Location.areEqual(origin, destination));
        } catch (Exception e) {
            Log.e(RoughRouteCalculator.class.getName(), "Exception while building route", e);
        }
        return result;
    }
    
    public static void addRoutePoints(ILocation origin, ILocation destination, List<ILocation> points) throws NoRouteException {
        if(Location.isInDoor(origin))
            addIndoorRoutePoints(origin, destination, points);
        else
            addOutDoorRoutePoints(origin, destination, points);
    }

    private static void addOutDoorRoutePoints(ILocation origin, ILocation destination, List<ILocation> points) throws NoRouteException {
        Location.ensureOutdoor(origin);
        if(Location.isOutDoor(destination)){
            points.add(destination);
        } else {
            IPoi exit = DualMapNavUtil.getExitPoi(Location.getLatLng(origin), destination.getCampusId(), destination.getFacilityId());

            ensureExit(origin, destination, exit);

            points.add(new Location(PoiData.getLatLng(exit)));
            points.add(new Location(exit));
        }
    }

    private static void addIndoorRoutePoints(ILocation origin, ILocation destination, List<ILocation> points) throws NoRouteException {
        Location.ensureInDoor(origin);

        if(!Location.inTheSameFacility(origin, destination)) {

            List<IPoi> exits = PoiData.getExits(
                    ProjectConf.getInstance().getAllFacilityPoisList(
                            origin.getCampusId(),
                            origin.getFacilityId())
            );

            IPoi exit = DualMapNavUtil.findCloseExit(Location.getPoint(origin), origin.getZ(), exits, false);

            ensureExit(origin, destination, exit);

            addIndoorRoutePoints(origin, new Location(exit), points);

            ILocation exitOutDoorLocation = new Location(PoiData.getLatLng(exit));
            points.add(exitOutDoorLocation);
        } else {
            if(!Location.onTheSameFloor(origin, destination)) {
                addFloorSwitchesLocations(origin, destination, points);
            }
            points.add(destination);
        }

    }

    private static void ensureExit(ILocation origin, ILocation destination, IPoi exit) throws NoRouteException {
        if(exit == null)
            throw new NoRouteException("can't find exit from: " + origin + ", to: " + destination);
    }

    private static void addFloorSwitchesLocations(ILocation origin, ILocation destination, List<ILocation> points) throws NoRouteException {
        String originFacilityID = origin.getFacilityId();
        String originCampusId = origin.getCampusId();

        List<SwitchFloorObj> floorSwitches = SwitchFloorHolder.getInstance().getSwichFloorPoints(originFacilityID);

        List<PointF> switchFloorPoints = new ArrayList<>();

        int originFloor = (int) origin.getZ();
        int destinationFloor = (int) destination.getZ();

        for (SwitchFloorObj floorSwitch : floorSwitches) {
            if(floorSwitch.getFromFloor().contains(originFloor)
                    && floorSwitch.getToFloor().contains(destinationFloor))
                switchFloorPoints.add(floorSwitch.getPoint());
        }

        PointF originPoint = Location.getPoint(origin);

        PointF closest = null;
        double distanceToClosest = Double.MAX_VALUE;

        for (PointF curPoint : switchFloorPoints) {
            double distance = MathUtils.getNavigationWeight(originPoint, curPoint);
            if(distance < distanceToClosest) {
                distanceToClosest = distance;
                closest = curPoint;
            }
        }

        if(closest == null)
            throw new NoRouteException("can't find switch floor object to for route from: " + origin + " to: " + destination);

        points.add(new Location(originFacilityID, originCampusId, closest.x, closest.y, (float) origin.getZ()));
        points.add(new Location(originFacilityID, originCampusId, closest.x, closest.y, (float) destination.getZ()));
    }

    public static class NoRouteException extends Exception {

        NoRouteException(String message) {
            super(message);
        }
    }
}
