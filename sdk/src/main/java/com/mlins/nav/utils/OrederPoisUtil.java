package com.mlins.nav.utils;

import android.content.Context;
import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.locationutils.LocationFinder;
import com.mlins.switchfloor.SwitchFloorHolder;
import com.mlins.switchfloor.SwitchFloorObj;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PoiData;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.Location;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.enums.NavigationType;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.spreosdk.R;

import java.util.ArrayList;
import java.util.List;

public class OrederPoisUtil {

    public static final String parkingId = "parking_poi";

    public static final String switchIdPrefix = "switch_floor_";

    public static List<IPoi> getPoisOrederForNavigation(List<IPoi> plist) {
        return getPoisOrederForNavigation(plist, true);
    }

    public static List<IPoi> getPoisOrederForNavigation(List<IPoi> plist, boolean order) {
        List<IPoi> result = new ArrayList<IPoi>();

        List<IPoi> poilist = new ArrayList<IPoi>();

        List<IPoi> listtoremove = new ArrayList<IPoi>();

        poilist.addAll(plist);

        for (IPoi o : poilist) {
            if ((o.getPoiID().contains("idr") && o.getPoiNavigationType().equals("internal")) || o.getPoiID().equals(parkingId) || o.getPoiID().startsWith(switchIdPrefix)) {
                listtoremove.add(o);
            }
        }

        poilist.removeAll(listtoremove);

        boolean addparkingtomultipois = PropertyHolder.getInstance().isAddParkingToMultiPois();
        boolean addswitchfloorstomultipois = PropertyHolder.getInstance().isAddSwitchFloorsToMultiPois();

        List<IPoi> externalpois = new ArrayList<IPoi>();
        for (IPoi o : poilist) {
            if (o.getPoiNavigationType().equals("external")) {
                externalpois.add(o);
            }
        }

        poilist.removeAll(externalpois);

        ILocation mylocation = LocationFinder.getInstance()
                .getCurrentLocation();
        ILocation origin = null;
        ILocation parkingloc = ParkingUtil.getInstance().getParkingLocation();
        if (mylocation == null
                || mylocation.getLocationType() == LocationMode.OUTDOOR_MODE) {
            if (parkingloc != null && addparkingtomultipois) {

                LatLng outdoorloc = new LatLng(parkingloc.getLat(), parkingloc.getLon());
                PoiData exitpoi = PoiDataHelper.getInstance().getExitPoi(outdoorloc);

                if (exitpoi != null) {
                    origin = new Location(exitpoi);
                    if (origin != null) {
                        try {
                            if (order) {
                                result = MultiNavUtils.getPoiOrder(origin, poilist);
                            } else {
                                result = poilist;
                            }
                            addParkingAndEntrance(result, parkingloc, exitpoi,
                                    false, externalpois);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                IPoi firstpoi = poilist.get(0);
                if (firstpoi != null) {
                    origin = new Location(firstpoi);
                    if (origin != null) {
                        try {
                            if (order) {
                                result = MultiNavUtils.getPoiOrder(origin, poilist);
                            } else {
                                result = poilist;
                            }
                            result.addAll(0, externalpois);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else if (mylocation.getLocationType() == LocationMode.INDOOR_MODE) {
            origin = mylocation;
            if (origin != null) {
                try {
                    if (order) {
                        result = MultiNavUtils.getPoiOrder(origin, poilist);
                    } else {
                        result = poilist;
                    }
                    if (parkingloc != null && addparkingtomultipois) {
                        LatLng outdoorloc = new LatLng(parkingloc.getLat(),
                                parkingloc.getLon());
                        PoiData exitpoi = PoiDataHelper.getInstance()
                                .getExitPoi(outdoorloc);
                        addParkingAndEntrance(result, parkingloc, exitpoi, true, externalpois);
                    } else {
                        result.addAll(externalpois);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (!result.isEmpty()) {
            if (addswitchfloorstomultipois) {
                addFloorsInstructions(result);
            }
            return result;
        }

        return poilist;

    }


    private static void addParkingAndEntrance(List<IPoi> result,
                                              ILocation parkingloc, PoiData exitpoi, boolean isindoor, List<IPoi> externalpois) {

        PoiData parkingpoi = null;

        if (parkingloc != null) {
            parkingpoi = new PoiData();
            parkingpoi.setPoiLatitude(parkingloc.getLat());
            parkingpoi.setPoiLongitude(parkingloc.getLon());
            parkingpoi.setPoiNavigationType("external");
            String poiid = parkingId;
            parkingpoi.setPoiID(poiid);
            String campusid = PropertyHolder.getInstance().getCampusId();
            parkingpoi.setCampusID(campusid);
            String poidescription = "parking";
            parkingpoi.setpoiDescription(poidescription);
        }

        if (!isindoor) {
            if (exitpoi != null) {
                result.add(0, exitpoi);
            }

            result.addAll(0, externalpois);

            if (parkingpoi != null) {
                result.add(0, parkingpoi);
            }
        }

        if (exitpoi != null) {
            result.add(exitpoi);
        }

        if (isindoor) {
            result.addAll(externalpois);
        }

        if (parkingpoi != null) {
            result.add(parkingpoi);
        }

    }

    private static void addFloorsInstructions(List<IPoi> result) {
        Context ctx = PropertyHolder.getInstance().getMlinsContext();
        List<IPoi> tmpList = new ArrayList<IPoi>();
        tmpList.addAll(result);
        for (IPoi o : tmpList) {
            int index = result.indexOf(o);
            if (o != null && o.getPoiNavigationType().equals("internal") && index > 0) {
                IPoi previouspoi = result.get(index - 1);
                if (!previouspoi.getPoiID().startsWith(switchIdPrefix) && previouspoi.getPoiNavigationType().equals("internal")) {
                    int z = (int) o.getZ();
                    int previousz = (int) previouspoi.getZ();
                    if (z != previousz) {
                        SwitchFloorObj switchfloor = getCloseSwitchFloor(
                                previouspoi, z);
                        if (switchfloor != null) {
                            PoiData switchpoi = new PoiData();
                            switchpoi.setPoint(switchfloor.getPoint());
                            switchpoi.setZ(switchfloor.getZ());
                            switchpoi.setPoiNavigationType("internal");
                            String campusid = PropertyHolder.getInstance()
                                    .getCampusId();
                            switchpoi.setCampusID(campusid);
                            String facilityid = FacilityContainer.getInstance()
                                    .getSelected().getId();
                            switchpoi.setFacilityID(facilityid);
                            String poiid = switchIdPrefix + index;
                            switchpoi.setPoiID(poiid);
                            String floortitle = FacilityContainer.getInstance()
                                    .getSelected().getFloorTitle(z);
                            String poidescription = ctx.getResources()
                                    .getString(R.string.elvator_up)
                                    + " "
                                    + floortitle;
                            switchpoi.setpoiDescription(poidescription);
                            if (switchpoi != null) {
                                result.add(index, switchpoi);
                            }
                        }
                    }
                }
            }
        }
    }


    public static SwitchFloorObj getCloseSwitchFloor(PointF poiLoc, int fromz, int endz) {
        PoiData p = new PoiData(poiLoc);
        p.setZ(fromz);
        SwitchFloorObj sw = getCloseSwitchFloor(p, endz);
        return sw;
    }

    private static SwitchFloorObj getCloseSwitchFloor(IPoi poi, int endz) {
        PointF p = poi.getPoint();
        int z = (int) poi.getZ();
        List<SwitchFloorObj> list = getSwitchFloorsByZ(z, endz);
        SwitchFloorObj result = null;
        double mind = 10000000;
        for (SwitchFloorObj o : list) {
            double d = MathUtils.distance(p, o.getPoint());
            if (d < mind) {
                mind = d;
                result = o;
            }
        }
        return result;
    }

    private static List<SwitchFloorObj> getSwitchFloorsByZ(int z, int endz) {
        String facilityid = FacilityContainer.getInstance()
                .getSelected().getId();
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
        return getSwitchFloorsByNavigationType(candidates);
    }

    private static List<SwitchFloorObj> getSwitchFloorsByNavigationType(
            List<SwitchFloorObj> all) {
        List<SwitchFloorObj> result = new ArrayList<SwitchFloorObj>();
        NavigationType navigationtype = PropertyHolder.getInstance()
                .getNavigationType();
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


    public static List<IPoi> getMixedPoisOrderForNavigation(List<IPoi> plist, List<IPoi> exitsAndEntrancesList, boolean order) {

        List<IPoi> result = new ArrayList<IPoi>();
        List<IPoi> poilist = new ArrayList<IPoi>();
        List<IPoi> listtoremove = new ArrayList<IPoi>();


        poilist.addAll(plist);

        for (IPoi o : poilist) {
            if ((o.getPoiID().contains("idr") && o.getPoiNavigationType().equals("internal"))
                    || o.getPoiID().equals(parkingId)
                    || o.getPoiID().startsWith(switchIdPrefix)) {
                listtoremove.add(o);
            }
        }

        poilist.removeAll(listtoremove);

        boolean addparkingtomultipois = PropertyHolder.getInstance().isAddParkingToMultiPois();
        boolean addswitchfloorstomultipois = PropertyHolder.getInstance().isAddSwitchFloorsToMultiPois();

        ILocation mylocation = LocationFinder.getInstance().getCurrentLocation();

        ILocation origin = null;
        ILocation parkingloc = ParkingUtil.getInstance().getParkingLocation();
        if (mylocation == null) {
            IPoi firstpoi = poilist.get(0);
            if (firstpoi != null) {
                origin = new Location(firstpoi);
                if (origin != null) {
                    try {
                        if (order) {
                            result = MultiNavUtils.getMixedPoiOrder(origin, poilist, exitsAndEntrancesList);
                        } else {
                            result = poilist;
                        }
                        if (parkingloc != null && addparkingtomultipois) {
                            addParking(result, parkingloc);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            origin = mylocation;
            if (origin != null) {
                try {
                    if (order) {
                        result = MultiNavUtils.getMixedPoiOrder(origin, poilist, exitsAndEntrancesList);
                    } else {
                        result = poilist;
                    }
                    if (parkingloc != null && addparkingtomultipois) {
                        addParking(result, parkingloc);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (!result.isEmpty()) {
            if (addswitchfloorstomultipois) {
                addFloorsInstructions(result);
            }
            return result;
        }

        return poilist;
    }


    private static void addParking(List<IPoi> result,
                                   ILocation parkingloc) {

        PoiData parkingpoi = null;

        if (parkingloc != null) {
            parkingpoi = new PoiData();
            parkingpoi.setPoiLatitude(parkingloc.getLat());
            parkingpoi.setPoiLongitude(parkingloc.getLon());
            parkingpoi.setPoiNavigationType("external");
            String poiid = parkingId;
            parkingpoi.setPoiID(poiid);
            String campusid = PropertyHolder.getInstance().getCampusId();
            parkingpoi.setCampusID(campusid);
            String poidescription = "parking";
            parkingpoi.setpoiDescription(poidescription);
        }

        if (parkingpoi != null) {
            result.add(parkingpoi);
        }

    }
}
