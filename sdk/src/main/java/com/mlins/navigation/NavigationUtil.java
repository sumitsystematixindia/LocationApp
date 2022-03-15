package com.mlins.navigation;

import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.aStar.NavigationPath;
import com.mlins.aStar.aStarData;
import com.mlins.instructions.Instruction;
import com.mlins.instructions.InstructionBuilder;
import com.mlins.locationutils.LocationFinder;
import com.mlins.nav.utils.SoundPlayer;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.res.setup.ConfigsLoader;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.PoiData;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.Location;
import com.mlins.utils.logging.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class NavigationUtil {
    private final static String TAG = "com.mlins.navigation.NavigationUtil";
    private static int googleNavDistance = 1000;

    public static void navigate(Location origin, PoiData destination) {
        Log.getInstance().debug(TAG, "Enter, navigate()");

        try {

            aStarData.getInstance().setCurrentCampusPath(null);
            aStarData.getInstance().setCurrentPath(null);
            aStarData.getInstance().setInternalDestination(null);
            aStarData.getInstance().setExternalDestination(null);
            aStarData.getInstance().setFinalDestination((Location) destination.getLocation());
            if (origin != null && destination != null) {
                if ((origin.getType() == Location.TYPE_INTERNAL /*|| PropertyHolder.getInstance().isSdkObserverMode()*/) && destination.getPoiNavigationType().equals("internal") && origin.getFacilityId().equals(destination.getFacilityID())) {
                    indoorToIndoorNavigation(origin, destination);
                } else if ((origin.getType() == Location.TYPE_INTERNAL && destination.getPoiNavigationType().equals("external")) ||
                        (origin.getType() == Location.TYPE_INTERNAL && origin.getFacilityId() != null && !origin.getFacilityId().equals(destination.getFacilityID()))) {
                    //check if its not campus navigation (i.e. between facilities)
                    if ((origin.getType() == Location.TYPE_INTERNAL && destination.getPoiNavigationType().equals("external"))) {
                        indoorToOutdoorNavigation(origin, destination);
                    } else if ((origin.getType() == Location.TYPE_INTERNAL && (destination.getPoiNavigationType().equals("internal") && !origin.getFacilityId().equals(destination.getFacilityID())))) {
                        String campusid = ProjectConf.getInstance().getCampusesMap().keySet().iterator().next();
                        Campus theCampus = (Campus) ProjectConf.getInstance().getCampusesMap().get(campusid);

                        FacilityConf destFac = theCampus.getFacilitiesConfMap().get(destination.getFacilityID());
                        FacilityConf origFac = theCampus.getFacilitiesConfMap().get(origin.getFacilityId());

                        ConfigsLoader.getInstance().loadFacility(campusid, origFac.getId());
                        PropertyHolder.getInstance().setFacilityID(origFac.getId());
                        int index = (int) LocationFinder.getInstance().getCurrentLocation().getZ();

                        FacilityConf cfacility = FacilityContainer.getInstance().getCurrent();
                        if (cfacility != null) {
                            cfacility.setSelected(index);
                        }

                        //FacilityConf.getInstance().setSelected(index);
                        //					LocationFinder.getInstance().resetFirstRun();
                        PoiData destLocation = new PoiData();

                        destLocation.setPoiLatitude(destFac.getCenterLatitude());
                        destLocation.setPoiLongitude(destFac.getCenterLongtitude());

                        indoorToOutdoorNavigation(origin, destLocation);

                        aStarData.getInstance().setInternalDestination(destination);

                    }
                } else if (origin.getType() == Location.TYPE_EXTERNAL && destination.getPoiNavigationType().equals("internal")) {
                    outdoorToIndoorNavigation(origin, destination);
                } else if (origin.getType() == Location.TYPE_EXTERNAL && destination.getPoiNavigationType().equals("external")) {
                    outdoorToOutdoorNavigation(origin, destination);
                }

            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
        Log.getInstance().debug(TAG, "Exit, navigate()");
    }

    public static void navigate(Location origin, Location destination) {
        Log.getInstance().debug(TAG, "Enter, navigate()");
        if (destination.getType() == Location.TYPE_INTERNAL) {
            PointF point = new PointF((float) destination.getX(), (float) destination.getY());
            PoiData indoorpoi = new PoiData(point);
            indoorpoi.setZ(destination.getZ());
            indoorpoi.setCampusID(destination.getCampusId());
            indoorpoi.setFacilityID(destination.getFacilityId());
            navigate(origin, indoorpoi);
        } else if (destination.getType() == Location.TYPE_EXTERNAL) {
            double lat = destination.getLat();
            double lon = destination.getLon();
            PoiData outdoorpoi = new PoiData();
            outdoorpoi.setPoiLatitude(lat);
            outdoorpoi.setPoiLongitude(lon);
            //outdoorpoi.setPoitype("external");
            outdoorpoi.setPoiNavigationType("external");
            navigate(origin, outdoorpoi);
        }
        Log.getInstance().debug(TAG, "Exit, navigate()");
    }

    public static void indoorToIndoorNavigation(Location origin, PoiData destination) {
        Log.getInstance().debug(TAG, "Enter, indoorToIndoorNavigation()");
        Location destloc = new Location(destination);
        aStarData.getInstance().setPoilocation(destloc);
        aStarData.getInstance().setDestination(destloc);
        PathCalculator.calculatePath(origin);
        setInstructions();
        Log.getInstance().debug(TAG, "Exit, indoorToIndoorNavigation()");
    }

    public static void outdoorToOutdoorNavigation(Location origin, PoiData destination) {
        Log.getInstance().debug(TAG, "Enter, outdoorToOutdoorNavigation()");
        aStarData.getInstance().setExternalDestination(destination);
        aStarData.getInstance().setExternalPoi(destination);
//		int navigationmode = PropertyHolder.getInstance().getOutdoorNavigationMode();
//		if(origin!=null && destination!=null && navigationmode == PropertyHolder.MODE_PATH_NAVIGATION){
//			GisPoint startpoint = new GisPoint(origin.getLon(), origin.getLat(), 0);
//			GisPoint endpoint = new GisPoint(destination.getPoiLongitude(), destination.getPoiLatitude(), 0);
//			List<GisLine> l = CampusGisData.getInstance().getLines();
//			aStarData.getInstance().loadData(l);
//			aStarAlgorithm a = new aStarAlgorithm(startpoint, endpoint);
//			List<GisSegment> path = null;
//			path = a.getPath();
//			CampusNavigationPath navpath = new CampusNavigationPath(path);
//			aStarData.getInstance().setCurrentCampusPath(navpath);
//		}
        Log.getInstance().debug(TAG, "Exit, outdoorToOutdoorNavigation()");
    }

    private static void outdoorToIndoorNavigation(Location origin, PoiData destination) {
        Log.getInstance().debug(TAG, "Enter, outdoorToIndoorNavigation()");
        if (origin != null && destination != null) {
            LatLng parking = new LatLng(origin.getLat(), origin.getLon());
            PoiData exitpoi = PoiDataHelper.getInstance().getExitPoi(parking);

            PoiData outdoordestination = exitpoi;
//			outdoorToOutdoorNavigation(origin, outdoordestination);

            aStarData.getInstance().setInternalDestination(destination);
            aStarData.getInstance().setExternalPoi(exitpoi);

            float[] results = new float[1];
            android.location.Location.distanceBetween(parking.latitude, parking.longitude, exitpoi.getPoiLatitude(), exitpoi.getPoiLongitude(), results);
            float distance = results[0];
            if (distance > googleNavDistance) {
                LatLng exitlatlng = new LatLng(exitpoi.getPoiLatitude(), exitpoi.getPoiLongitude());
                GoogleNavigationUtil.getInstance().notifyStart(exitlatlng);
            }

            // Location indoororigin = new Location(exitpoi);
            // indoorToIndoorNavigation(indoororigin, destination);
        }
        Log.getInstance().debug(TAG, "Exit, outdoorToIndoorNavigation()");
    }

    private static void indoorToOutdoorNavigation(Location origin, PoiData destination) {
        Log.getInstance().debug(TAG, "Enter, indoorToOutdoorNavigation()");
        LatLng parking = new LatLng(destination.getPoiLatitude(), destination.getPoiLongitude());
        PoiData exitpoi = PoiDataHelper.getInstance().getExitPoi(parking);

        PoiData indoordestination = exitpoi;

        aStarData.getInstance().setExternalDestination(destination);
        aStarData.getInstance().setExternalPoi(destination);

        indoorToIndoorNavigation(origin, indoordestination);

        // LatLng latlng = new LatLng(exitpoi.getPoiLatitude(),
        // exitpoi.getPoiLongitude());
        // Location outdoororigin = new Location(latlng);
        // outdoorToOutdoorNavigation(outdoororigin, destination);
        Log.getInstance().debug(TAG, "Exit, indoorToOutdoorNavigation()");

    }

    private static void setInstructions() {
        NavigationPath nav = aStarData.getInstance().getCurrentPath();

        if (nav != null) {
            List<Instruction> instructions = InstructionBuilder.getInstance().getInstractions(nav);
            if (instructions != null && instructions.size() > 0) {
                PoiDataHelper.getInstance().setClosePoisForInstructions(instructions);
            }
        }
    }

    // saving values of selected destination //Nir
    public static void savedestination(Location destination) {
        Log.getInstance().debug(TAG, "Enter, savedestination()");

        // File dir = PropertyHolder.getInstance().getAppDir();
        File dir = PropertyHolder.getInstance().getFacilityDir();

        StringBuffer sb = new StringBuffer();
        sb.append(destination.getX() + "\t");
        sb.append(destination.getY() + "\t");
        sb.append(destination.getZ() + "\t");
        // sb.append(destination.getgetIconUri() + "\t");
        // sb.append(destination.getDescription() + "\t");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = "routeresume.txt";
        File pfile = new File(dir, fileName);
        if (pfile.exists()) {
            pfile.delete();
        }
        try {
            pfile.createNewFile();
        } catch (IOException e1) {
            Log.getInstance().error(TAG, e1.getMessage(), e1);
            e1.printStackTrace();
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(pfile, true));
            out.write(sb.toString());
            out.flush();
        } catch (IOException e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            e.toString();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (Exception e2) {
                    Log.getInstance().error(TAG, e2.getMessage(), e2);
                    e2.printStackTrace();
                }
        }
        Log.getInstance().debug(TAG, "Exit, savedestination()");
    }

    public static Instruction playDestinationSound() {
        Log.getInstance().debug(TAG, "Enter, playDestinationSound()");
        List<Instruction> cinstructions = InstructionBuilder.getInstance().getCurrentInstructions();
        Instruction destinstruction = null;
        if (cinstructions != null && cinstructions.size() > 0) {
            destinstruction = cinstructions.get(cinstructions.size() - 1);
        }
        if (destinstruction != null && destinstruction.getType() == Instruction.TYPE_DESTINATION && !destinstruction.hasPlayed()) {
            destinstruction.setPlayed(true);
            if (!PropertyHolder.getInstance().isNavigationInstructionsSoundMute()) {
                SoundPlayer.getInstance().play(destinstruction.getSound());
            }

        }
        Log.getInstance().debug(TAG, "Exit, playDestinationSound()");
        return destinstruction;
    }
}
