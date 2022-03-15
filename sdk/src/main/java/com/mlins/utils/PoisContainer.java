package com.mlins.utils;

import com.mlins.kdtree.KDimensionalTree;
import com.mlins.ndk.wrappers.NdkConversionUtils;
import com.mlins.ndk.wrappers.NdkLocation;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PoisContainer {


    // XXX NDK LOAD LIB
    static {
        System.loadLibrary("MlinsLocationFinderUtils");
    }

    private Map<String, List<IPoi>> campuseLevelPoisMap = new HashMap<String, List<IPoi>>();
    private Map<String, List<IPoi>> facilityLevelPoisMap = new HashMap<String, List<IPoi>>();
    private Map<String, List<IPoi>> floorLevelPoisMap = new HashMap<String, List<IPoi>>();
    private Set<PoiType> categories = new HashSet<PoiType>();
    private KDimensionalTree<IPoi> externalPoisTree = new KDimensionalTree<IPoi>(3);

    public boolean addPoi(String campusId, IPoi poi) {
        if (campusId == null || poi == null) {
            return false;
        }

        List<IPoi> campusPois = campuseLevelPoisMap.get(campusId);
        if (campusPois == null) {
            campusPois = new ArrayList<IPoi>();
            campuseLevelPoisMap.put(campusId, campusPois);
        }
        return campusPois.add(poi);

    }

    public void addPois(String campusId, String facilityId, List<IPoi> list) {

        if (list == null || list.size() == 0) {
            return;
        }

        for (IPoi poi : list) {
            if (poi == null) {
                continue;
            }
            addPoi(campusId, facilityId, poi);
        }


    }

    public boolean addPoi(String campusId, String facilityId, IPoi poi) {


        if (addPoi(campusId, poi) && facilityId != null) {

            String fkey = campusId + "_" + facilityId;
            List<IPoi> facilityPois = facilityLevelPoisMap.get(fkey);

            if (facilityPois == null) {
                facilityPois = new ArrayList<IPoi>();
                facilityLevelPoisMap.put(fkey, facilityPois);
            }

            facilityPois.add(poi);


            if (poi.getPoiNavigationType() != null &&
                    poi.getPoiNavigationType().equals("internal")) {
                int floor = (int) poi.getZ(); // floor


                // === convert lat/lon
                Campus c = ProjectConf.getInstance().getSelectedCampus();
                if (c != null) {
                    Map<String, FacilityConf> facilitiesmap = c.getFacilitiesConfMap();
                    FacilityConf fac = facilitiesmap.get(facilityId);
                    if (fac != null) {
                        NdkLocation point = new NdkLocation(poi.getX(), poi.getY());
                        point.setZ(floor);

                        NdkLocation covertedPoint = new NdkLocation();

                        NdkConversionUtils converter = new NdkConversionUtils();

                        double rotationAngle = fac.getRot_angle();
                        if (fac.getMapWidth() != 0 && fac.getMapHight() != 0) {
                            converter.convertPoint(point, fac.getConvRectTLlon(),
                                    fac.getConvRectTLlat(), fac.getConvRectTRlon(),
                                    fac.getConvRectTRlat(), fac.getConvRectBLlon(),
                                    fac.getConvRectBLlat(), fac.getConvRectBRlon(),
                                    fac.getConvRectBRlat(), fac.getMapWidth(),
                                    fac.getMapHight(), rotationAngle, covertedPoint);

                            if (covertedPoint != null) {
                                poi.setPoiLatitude(covertedPoint.getLat());
                                poi.setPoiLongitude(covertedPoint.getLon());
                            }
                        }
                    }
                }
                // === end convertion

                String flkey = campusId + "_" + facilityId + "_" + floor;

                List<IPoi> floorPois = floorLevelPoisMap.get(flkey);
                if (floorPois == null) {
                    floorPois = new ArrayList<IPoi>();
                    floorLevelPoisMap.put(flkey, floorPois);
                }
                floorPois.add(poi);
            }

        }

        return true;

    }

    public List<IPoi> getAllFloorPoisList(String campusId,
                                          String facilityId, int floor) {

        String key = campusId + "_" + facilityId + "_" + floor;
        List<IPoi> iPoisList = floorLevelPoisMap.get(key);

        if (iPoisList == null) {
            iPoisList = new ArrayList<IPoi>();
        }

        return iPoisList;

    }

    public List<IPoi> getAllFacilityPoisList(String campusId,
                                             String facilityId) {
        String key = campusId + "_" + facilityId;
        List<IPoi> iPoisList = facilityLevelPoisMap.get(key);

        if (iPoisList == null) {
            iPoisList = new ArrayList<IPoi>();
        }

        return iPoisList;
    }

    public List<IPoi> getAllCampusPoisList(String campusId) {

        List<IPoi> iPoisList = campuseLevelPoisMap.get(campusId);

        if (iPoisList == null) {
            iPoisList = new ArrayList<IPoi>();
        }

        return iPoisList;
    }

    public List<IPoi> getAllPois() {
        List<IPoi> iPoisList = new ArrayList<IPoi>();
        for (List<IPoi> list : campuseLevelPoisMap.values()) {
            iPoisList.addAll(list);
        }
        return iPoisList;
    }

    public List<IPoi> getAllExternalPois() {
        List<IPoi> iPoisList = new ArrayList<IPoi>();
        for (List<IPoi> list : campuseLevelPoisMap.values()) {
            for (IPoi poi : list) {
                if (poi == null) {
                    continue;
                }
                String type = poi.getPoiNavigationType();
                if (type != null && type.equals("external")) {
                    iPoisList.add(poi);
                }
            }
        }
        return iPoisList;
    }

    public void setVisiblePoisCategories(List<String> categoriesnames) {

        if (categoriesnames == null) {
            return;
        }

        List<IPoi> allPois = getAllPois();

        for (IPoi poi : allPois) {
            if (poi != null) {
                List<String> categories = poi.getPoitype();
                if (!Collections.disjoint(categoriesnames, categories)) {
                    //poi.setShowPoiOnMap(true);
                    poi.setVisible(true);
                } else {
                    //poi.setShowPoiOnMap(false);
                    poi.setVisible(false);
                }
            }
        }

    }

    public List<IPoi> getAllCampusExternalPoisList(String campusId) {

        List<IPoi> iPoisList = campuseLevelPoisMap.get(campusId);

        List<IPoi> externalPois = new ArrayList<IPoi>();

        if (iPoisList != null) {
            for (IPoi poi : iPoisList) {
                if (poi == null) {
                    continue;
                }
                String type = poi.getPoiNavigationType();
             /*String id = poi.getPoiID();*/
                if (type != null && type.equals("external")
					 /*&& id!=null && !id.startsWith("idr")*/) {

                    externalPois.add(poi);
                }
            }
        }

        return externalPois;
    }

    public boolean addCategory(PoiType poitype) {

        if (poitype == null) {
            return false;
        }

        return categories.add(poitype);
    }

    public boolean addCategoryList(List<PoiType> poitypeList) {

        if (poitypeList == null || poitypeList.size() == 0) {
            return false;
        }

        return categories.addAll(poitypeList);
    }

    public List<PoiType> getCategoriesList() {
        return new ArrayList<PoiType>(categories);
    }


    /////============== external POIs kd tree =============

    public List<IPoi> getAllEntrancesAndExits() {
        // get entrances and exits
        List<IPoi> result = new ArrayList<IPoi>();
        List<IPoi> pois = ProjectConf.getInstance().getAllPoisList();
        if (pois == null) {
            return result;
        }
        for (IPoi poi : pois) {
            if (poi == null) {
                continue;
            }
            String id = poi.getPoiID();
            if (id != null && id.startsWith("idr") && "internal".equals(poi.getPoiNavigationType())) {
                result.add(poi);
            }
        }
        return result;

    }

    public void loadExternalPoisKDimensionalTree() {

        try {

            List<IPoi> pois = ProjectConf.getInstance().getAllExternalPois();

            externalPoisTree = new KDimensionalTree<IPoi>(3);

            for (IPoi p : pois) {
                try {

                    Location myLoc = new Location();

                    double latitude = p.getPoiLatitude();
                    double longitude = p.getPoiLongitude();

                    double x = (double) (Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(longitude)));
                    double y = (double) (Math.cos(Math.toRadians(latitude)) * Math.sin(Math.toRadians(longitude)));
                    double z = (double) (Math.sin(Math.toRadians(latitude)));

                    myLoc.setX(x);
                    myLoc.setY(y);
                    myLoc.setZ(z);

                    externalPoisTree.addElement(myLoc, p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public ArrayList<IPoi> getExternalInRangePois(Location currLoc, float rangeInMeters, int maxReturnedCount) {

        try {

            Location myLoc = new Location();
            double latitude = currLoc.getLat();
            double longitude = currLoc.getLon();

            double x = (double) (Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(longitude)));
            double y = (double) (Math.cos(Math.toRadians(latitude)) * Math.sin(Math.toRadians(longitude)));
            double z = (double) (Math.sin(Math.toRadians(latitude)));

            myLoc.setX(x);
            myLoc.setY(y);
            myLoc.setZ(z);

            return (ArrayList<IPoi>) externalPoisTree.nearest(myLoc, maxReturnedCount);
        } catch (Throwable e) {

           // e.printStackTrace();
            return null;
        }

    }
    // ================= end ndk link ==============


}
