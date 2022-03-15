package com.mlins.nav.utils;

import android.content.Context;
import android.graphics.PointF;

import com.mlins.locationutils.LocationFinder;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.switchfloor.SwitchFloorHolder;
import com.mlins.switchfloor.SwitchFloorObj;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PoiData;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.Location;
import com.spreo.nav.enums.NavigationType;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.spreosdk.R;

import java.util.ArrayList;
import java.util.List;

public class DualMapOrderPoiUtil {
    public static final String parkingId = "parking_poi";

    public static final String switchIdPrefix = "switch_floor_";

    public static List<IPoi> getDualMapPoisOrder(List<IPoi> poilist, boolean order) {

        List<IPoi> filteredList = new ArrayList<IPoi>();

        // filter out parking switchfloor and exits.
        for (IPoi o : poilist) {
            if (o == null) {
                continue;
            }
            if (!((o.getPoiID().contains("idr") && o.getPoiNavigationType().equals("internal"))
                    || o.getPoiID().equals(parkingId)
                    || o.getPoiID().startsWith(switchIdPrefix))) {
                filteredList.add(o);
            }
        }

        ILocation mylocation = LocationFinder.getInstance().getCurrentLocation();
        List<IPoi> result = filteredList;
        try {

            // add exits/switchFloor/parking to the correct order place
            boolean addparkingtomultipois = PropertyHolder.getInstance().isAddParkingToMultiPois();
            boolean addswitchfloorstomultipois = PropertyHolder.getInstance().isAddSwitchFloorsToMultiPois();
            boolean addEntrancestomultipois = PropertyHolder.getInstance().isAddEntranceToMultiPois();

            ILocation origin = null;
            ILocation parkingloc = ParkingUtil.getInstance().getParkingLocation();
            if (mylocation == null) {
                IPoi firstpoi = result.get(0);
                if (firstpoi != null) {
                    origin = new Location(firstpoi);
                    if (origin != null) {
                        try {
                            if (order) {
                                List<IPoi> list = MultiNavUtils.getMultiFacilitiesPoiOrder(origin, filteredList);
                                if (list != null && !list.isEmpty()) {
                                    result = list;
                                }
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
                            List<IPoi> list = MultiNavUtils.getMultiFacilitiesPoiOrder(origin, filteredList);
                            if (list != null && !list.isEmpty()) {
                                result = list;
                            }
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

                if (addEntrancestomultipois) {
                    addEntrancesAndExists(result);
                }

                if (addswitchfloorstomultipois) {
                    addSwitchFloors(result);
                }

                return result;
            }


        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    // ===== help utilities methods =====


    private static void addEntrancesAndExists(List<IPoi> list) {

        List<IPoi> exists = null;
        List<IPoi> result = new ArrayList<IPoi>();

        if (list == null || list.size() == 0) {
            return;
        }

        exists = ProjectConf.getInstance().getAllEntrancesAndExits();
        if (exists == null || exists.size() == 0) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            IPoi currPoi = list.get(i);
            if (i > 0) {
                String currFacId = currPoi.getFacilityID();
                String currPoiType = currPoi.getPoiNavigationType();

                IPoi prevPoi = list.get(i - 1);
                String prevFacId = prevPoi.getFacilityID();
                String prevPoiType = prevPoi.getPoiNavigationType();


                if (prevPoiType.equals("external")) {
                    if (currPoiType.equals("internal")) {
                        IPoi closestEntrance = getClosestExitEntrance(currPoi, exists);
                        if (closestEntrance != null) {
                            result.add(closestEntrance);
                        }
                    }
                } else if (prevPoiType.equals("internal")) {
                    if (currPoiType.equals("external")) {
                        IPoi closestExit = getClosestExitEntrance(prevPoi, exists);
                        if (closestExit != null) {
                            result.add(closestExit);
                        }
                    } else {
                        if (!currFacId.equals(prevFacId)) {
                            IPoi closestExit = getClosestExitEntrance(prevPoi, exists);
                            if (closestExit != null) {
                                result.add(closestExit);
                            }
                            IPoi closestEnter = getClosestExitEntrance(currPoi, exists);
                            if (closestEnter != null) {
                                result.add(closestEnter);
                            }
                        }
                    }
                }


            }
            result.add(currPoi);
        }

        if (result != null && result.size() > 0) {
            list.clear();
            list.addAll(result);
        }

    }


    private static IPoi getClosestExitEntrance(IPoi poi, List<IPoi> exists) {

        if (poi == null || exists == null || exists.size() == 0) {
            return null;
        }

        String type = poi.getPoiNavigationType();
        if (type == null || !type.equals("internal")) {
            return null;
        }

        PointF p = poi.getPoint();

        IPoi result = null;
        double mind = 10000000;
        String pCampusId = poi.getCampusID();
        String pFacilityId = poi.getFacilityID();

        for (IPoi o : exists) {
            if (o == null) {
                continue;
            }
            String campusId = o.getCampusID();
            String facilityId = o.getFacilityID();
            if (pCampusId != null
                    && pFacilityId != null
                    && campusId != null
                    && facilityId != null
                    && pCampusId.equals(campusId)
                    && pFacilityId.equals(facilityId)) {
                double d = MathUtils.distance(p, o.getPoint());
                if (d < mind) {
                    mind = d;
                    result = o;
                }
            }
        }
        return result;
    }

    private static void addSwitchFloors(List<IPoi> result) {

        Context ctx = PropertyHolder.getInstance().getMlinsContext();
        List<IPoi> tmpList = new ArrayList<IPoi>();
        tmpList.addAll(result);

        for (IPoi o : tmpList) {

            int index = result.indexOf(o);

            if (o != null && o.getPoiNavigationType().equals("internal") && index > 0) {

                IPoi previouspoi = result.get(index - 1);

                if (!previouspoi.getPoiID().startsWith(switchIdPrefix)
                        && previouspoi.getPoiNavigationType().equals("internal")) {

                    String campusId = o.getCampusID();
                    String facilityId = o.getFacilityID();
                    String prevCampusId = previouspoi.getCampusID();
                    String prevFacilityId = previouspoi.getFacilityID();

                    if (campusId != null && facilityId != null &&
                            campusId.equals(prevCampusId) &&
                            facilityId.equals(prevFacilityId)) {

                        int z = (int) o.getZ();
                        int previousz = (int) previouspoi.getZ();
                        if (z != previousz) {

                            SwitchFloorObj switchfloor = getCloseSwitchFloor(previouspoi, z);

                            if (switchfloor != null) {
                                PoiData switchpoi = new PoiData();
                                switchpoi.setPoint(switchfloor.getPoint());
                                switchpoi.setZ(switchfloor.getZ());
                                switchpoi.setPoiNavigationType("internal");
                                //String campusid = PropertyHolder.getInstance().getCampusId();
                                switchpoi.setCampusID(campusId);
                                //String facilityid = FacilityContainer.getInstance().getSelected().getId();
                                switchpoi.setFacilityID(facilityId);
                                String poiid = switchIdPrefix + index;
                                switchpoi.setPoiID(poiid);
                                //String floortitle = FacilityContainer.getInstance().getSelected().getFloorTitle(z);
                                String floortitle = getFloorTitle(campusId, facilityId, z);
                                String poidescription = ctx.getResources().getString(R.string.elvator_up) + " " + floortitle;
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
    }

    private static String getFloorTitle(String campusId, String facilityId, int z) {
        String floorTitle = "";
        Campus campus = ProjectConf.getInstance().getCampus(campusId);
        if (campus != null) {
            FacilityConf facConf = campus.getFacilityConf(facilityId);
            if (facConf != null) {
                floorTitle = facConf.getFloorTitle(z);
            }
        }
        return floorTitle;
    }


//	private static SwitchFloorObj getCloseSwitchFloor(PointF poiLoc, int fromz, int endz) {
//		PoiData p = new PoiData(poiLoc);
//		p.setZ(fromz);
//		SwitchFloorObj sw = getCloseSwitchFloor(p, endz);
//		return sw;
//	}

    private static SwitchFloorObj getCloseSwitchFloor(IPoi poi, int endz) {


        SwitchFloorObj result = null;

        if (poi == null) {
            return result;
        }

        PointF p = poi.getPoint();
        int z = (int) poi.getZ();
        List<SwitchFloorObj> list = getSwitchFloorsByZ(poi.getFacilityID(), z, endz);

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

    private static List<SwitchFloorObj> getSwitchFloorsByZ(String facilityid, int z, int endz) {

        //String facilityid = FacilityContainer.getInstance().getSelected().getId();
        List<SwitchFloorObj> alllist = SwitchFloorHolder.getInstance().getSwichFloorPoints(facilityid);
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


    // add parking to order list
    private static void addParking(List<IPoi> result, ILocation parkingloc) {

        PoiData parkingpoi = null;

        if (parkingloc != null) {
            parkingpoi = new PoiData();
            parkingpoi.setPoiLatitude(parkingloc.getLat());
            parkingpoi.setPoiLongitude(parkingloc.getLon());
            parkingpoi.setPoiNavigationType("external");
            String poiid = parkingId;
            parkingpoi.setPoiID(poiid);
            String campusid = parkingloc.getCampusId(); //PropertyHolder.getInstance().getCampusId();
            parkingpoi.setCampusID(campusid);
            String poidescription = "parking";
            parkingpoi.setpoiDescription(poidescription);
        }

        if (parkingpoi != null) {
            result.add(parkingpoi);
        }

    }


}
