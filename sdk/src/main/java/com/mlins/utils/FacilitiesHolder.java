package com.mlins.utils;

import android.graphics.PointF;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class FacilitiesHolder {

    //	private final String FACILITIES_CONF_FILE_NAME="facilities_test.txt";
//	private final String FACILITIES_CONF_FILE_NAME="facilities_home.txt";
    private final String FACILITIES_CONF_FILE_NAME = "facilities.txt";
    private HashMap<String, FacilityObj> facilities = null;

    public FacilitiesHolder() {
        super();
        facilities = new HashMap<String, FacilityObj>();
        loadFacilitiesConfData();

    }

    public static FacilitiesHolder getInstance() {
        return Lookup.getInstance().get(FacilitiesHolder.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(FacilitiesHolder.class);
    }


    private void loadFacilitiesConfData() {


        File f = new File(PropertyHolder.getInstance().getCampusDir(), FACILITIES_CONF_FILE_NAME);

        if (f.exists() && f.isFile()) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(f);
                String line = "";
                String[] splitedLine = null;

                String idName = null;
                double lat = 0;  // gps latitude
                double lon = 0;    // gps longitude
                int floorsCount = 0;
                float angle = 0; // angle to be displayed on google map
                float width = 0;  // width to be displayed on google map
                double NWLat = 0;  // NW latitude
                double NWLon = 0;    // NW longitude
                double NELat = 0;  // NE latitude
                double NELon = 0;    // NE longitude
                double SWLat = 0;  // SW latitude
                double SWLon = 0;    // SW longitude
                double SELat = 0;  // SE latitude
                double SELon = 0;    // SE longitude
                double boundsOffset = 0;    // boundsOffset
                while (scanner.hasNext()) {
                    line = scanner.nextLine();
                    splitedLine = line.split("\t");

                    if (splitedLine.length > 0) {

                        idName = splitedLine[0];

                        try {  // lat
                            lat = Double.parseDouble(splitedLine[1]);
                        } catch (Exception e) {
                            lat = 0;
                        }


                        try {  // lon
                            lon = Double.parseDouble(splitedLine[2]);
                        } catch (Exception e) {
                            lon = 0;
                        }

                        try {  // floorsCount
                            floorsCount = Integer.parseInt(splitedLine[3]);
                        } catch (Exception e) {
                            floorsCount = 0;
                        }

                        try {  // angle
                            angle = Float.parseFloat(splitedLine[4]);
                        } catch (Exception e) {
                            angle = 0;
                        }

                        try {  // width
                            width = Float.parseFloat(splitedLine[5]);
                        } catch (Exception e) {
                            width = 0;
                        }

                        try {  // NWlat
                            NWLat = Double.parseDouble(splitedLine[6]);
                        } catch (Exception e) {
                            NWLat = 0;
                        }


                        try {  // NWlon
                            NWLon = Double.parseDouble(splitedLine[7]);
                        } catch (Exception e) {
                            NWLon = 0;
                        }
                        try {  // NElat
                            NELat = Double.parseDouble(splitedLine[8]);
                        } catch (Exception e) {
                            NELat = 0;
                        }


                        try {  // NElon
                            NELon = Double.parseDouble(splitedLine[9]);
                        } catch (Exception e) {
                            NELon = 0;
                        }
                        try {  // SWlat
                            SWLat = Double.parseDouble(splitedLine[10]);
                        } catch (Exception e) {
                            SWLat = 0;
                        }


                        try {  // SWlon
                            SWLon = Double.parseDouble(splitedLine[11]);
                        } catch (Exception e) {
                            SWLon = 0;
                        }
                        try {  // SElat
                            SELat = Double.parseDouble(splitedLine[12]);
                        } catch (Exception e) {
                            SELat = 0;
                        }


                        try {  // SElon
                            SELon = Double.parseDouble(splitedLine[13]);
                        } catch (Exception e) {
                            SELon = 0;
                        }

                        try {  // facilityBoundsOffset
                            boolean considerBoundsOffset = PropertyHolder.getInstance().isGeoAutoDetect();

                            if (considerBoundsOffset) {
                                boundsOffset = PropertyHolder.getInstance().getBoundsOffset();
                            } else {
                                boundsOffset = Double.parseDouble(splitedLine[14]);
                            }
                        } catch (Exception e) {
                            boundsOffset = 0;
                        }


                        FacilityObj facObj = new FacilityObj(idName, lat, lon, floorsCount, angle, width, NWLat, NWLon, NELat, NELon, SWLat, SWLon, SELat, SELon, boundsOffset);
                        facObj.setNewBoundsToFacility();
                        facilities.put(facObj.getIdName(), facObj);
                    }


                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
            }
        }

    }


    //service functions


    public FacilityObj getFacilityByIdName(String idName) {
        if (idName == null || idName == "")
            return null;

        FacilityObj facObj = facilities.get(idName);

        return facObj;

    }


    public double getFacilityGPSLat(String idName) {
        FacilityObj facObj = getFacilityByIdName(idName);

        if (facObj != null)
            return facObj.getLat();

        return 0.0;
    }


    public double getFacilityGPSLon(String idName) {
        FacilityObj facObj = getFacilityByIdName(idName);

        if (facObj != null)
            return facObj.getLon();

        return 0.0;
    }


    public int getFacilityFloorsCount(String idName) {
        FacilityObj facObj = getFacilityByIdName(idName);

        if (facObj != null)
            return facObj.getFloorsCount();

        return 0;
    }


    public float getFacilityAngle(String idName) {
        FacilityObj facObj = getFacilityByIdName(idName);

        if (facObj != null)
            return facObj.getAngle();

        return 0;
    }


    public float getFacilityWidth(String idName) {
        FacilityObj facObj = getFacilityByIdName(idName);

        if (facObj != null)
            return facObj.getWidth();

        return 0;
    }


    public ArrayList<FacilityObj> getAllFacilitiesAsList() {
        return new ArrayList<FacilityObj>(facilities.values());
    }


    public String getEntranceFacilityByLocationProvider(PointF location) {
        String facilityIDname = "unknown";
        ArrayList<FacilityObj> allFacilities = getAllFacilitiesAsList();
        for (FacilityObj fac : allFacilities) {

            if (location.x <= (float) fac.getEntranceRight() &&
                    location.x >= (float) fac.getEntranceLeft() &&
                    location.y <= (float) fac.getEntranceUp() &&
                    location.y >= (float) fac.getEntranceDown()) {
                facilityIDname = fac.getIdName();
                break;
            }

        }
        return facilityIDname;
    }

    public String getExitFacilityByLocationProvider(PointF location) {
        String facilityIDname = "unknown";
        ArrayList<FacilityObj> allFacilities = getAllFacilitiesAsList();
        for (FacilityObj fac : allFacilities) {

            if (location.x <= (float) fac.getExitRight() &&
                    location.x >= (float) fac.getExitLeft() &&
                    location.y <= (float) fac.getExitUp() &&
                    location.y >= (float) fac.getExitDown()) {
                facilityIDname = fac.getIdName();
                break;
            }

        }
        return facilityIDname;
    }


    public boolean reloadFacilities() {
        facilities.clear();
        loadFacilitiesConfData();
        return true;
    }

}
