package com.mlins.nav.utils;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.ndk.wrappers.FLocation;
import com.mlins.ndk.wrappers.NdkConversionUtils;
import com.mlins.ndk.wrappers.NdkGeneticPathOrderFinder;
import com.mlins.ndk.wrappers.NdkLocation;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.Location;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiNavUtils {

//	public static List<ILocation> getOrder(ILocation origin, List<ILocation> dests) {
//		List<ILocation> OrderdDests = null;
//		String jsonFormat = convertToJson(origin, dests);
//		MultiNavDestsOrderTask task = new MultiNavDestsOrderTask();
//		try {
//			String result = task.execute(jsonFormat).get(10000, TimeUnit.MILLISECONDS);
//			OrderdDests = parseJson(result);
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//
//		if (OrderdDests == null) {
//			return dests;
//		}
//		return OrderdDests;
//
//	}


    public static List<ILocation> getOrder(ILocation origin, List<ILocation> dests) {


        List<ILocation> orderdPois = null;

        int destSize = dests.size();

        File dir = PropertyHolder.getInstance().getFacilityDir();
        File sfile = new File(dir, "switchfloor.txt");
        String switchFloorFilePath = sfile.getAbsolutePath();
        int generationsCount = -1;

        FLocation fOrigin = convertToFLocation(origin);

        FLocation[] fDests = new FLocation[destSize];
        FLocation[] fullSolutionPath = new FLocation[destSize];

        for (int i = 0; i < destSize; i++) {
            ILocation iloc = dests.get(i);
            fDests[i] = convertToFLocation(iloc);
            fullSolutionPath[i] = new FLocation();
        }


        NdkGeneticPathOrderFinder geneticOrderAlg = new NdkGeneticPathOrderFinder();
        geneticOrderAlg.getPoisOrder(switchFloorFilePath, generationsCount, fOrigin, fDests, fullSolutionPath);


        orderdPois = new ArrayList<ILocation>();
        for (FLocation fPoi : fullSolutionPath) {
            ILocation loc = convertToILocation(fPoi);
            orderdPois.add(loc);
        }

        return orderdPois;

    }

    private static FLocation convertToFLocation(ILocation iloc) {
        if (iloc == null) {
            return new FLocation();
        }
        FLocation loc = new FLocation();
        loc.setX((float) iloc.getX());
        loc.setY((float) iloc.getY());
        loc.setZ((float) iloc.getZ());
        loc.setLat(iloc.getLat());
        loc.setLon(iloc.getLon());
        LocationMode mode = iloc.getLocationType();
        if (mode != null) {
            loc.setType(mode.getValue());
        }
        loc.setFacilityId(iloc.getFacilityId());

        return loc;
    }

    private static ILocation convertToILocation(FLocation floc) {
        if (floc == null) {
            return new Location();
        }
        Location loc = new Location();
        loc.setX((float) floc.getX());
        loc.setY((float) floc.getY());
        loc.setZ((float) floc.getZ());
        loc.setLat(floc.getLat());
        loc.setLon(floc.getLon());
        loc.setType(floc.getType());
        loc.setFacilityId(floc.getFacilityId());
        return loc;
    }


    public static List<IPoi> getPoiOrder(ILocation origin, List<IPoi> dests) {
        List<IPoi> result = new ArrayList<IPoi>();
        HashMap<String, String> poimap = new HashMap<String, String>();
        List<ILocation> locs = new ArrayList<ILocation>();
        for (IPoi o : dests) {
            ILocation loc = new Location(o.getX(), o.getY(), (float) o.getZ());
            String sloc = locationToString(loc);
            locs.add(loc);
            poimap.put(sloc, o.getPoiID());
        }

        List<ILocation> orederedlocs = getOrder(origin, locs);

        for (ILocation o : orederedlocs) {
            String sloc = locationToString(o);
            IPoi poi = getPoiById(poimap.get(sloc));
            if (poi != null) {
                result.add(poi);
            }

        }

        return result;
    }

    public static IPoi getPoiById(String id) {
        IPoi result = null;
        List<IPoi> poiList = ProjectConf.getInstance().getAllPoisList();
        for (IPoi o : poiList) {
            if (o.getPoiID().equals(id)) {
                result = o;
                break;
            }
        }
        return result;
    }

    private static String locationToString(ILocation loc) {
        String result = "";
        result = String.format("%d,%d,%d", (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
//		result += loc.getX();
//		result += loc.getY();
//		result += loc.getZ();
        return result;
    }


    private static String convertToJson(ILocation origin, List<ILocation> dests) {

        String content = null;
        JSONObject jsonObj = null;
        if (dests == null || origin == null) {
            return null;
        }

        if (dests.size() == 0) {
            return null;
        }

        ILocation destLoc = null;

        try {
            jsonObj = new JSONObject();

            int reqCode = 1;
            jsonObj.put("req", reqCode);
            String projectId = PropertyHolder.getInstance().getProjectId();
            jsonObj.put("pid", projectId);

            String campusId = PropertyHolder.getInstance().getCampusId();
            jsonObj.put("cid", campusId);

            String facilityId = PropertyHolder.getInstance().getFacilityID();
            jsonObj.put("fid", facilityId);

            JSONObject originLocJsonObj = new JSONObject();

            originLocJsonObj.put("x", origin.getX());
            originLocJsonObj.put("y", origin.getY());
            originLocJsonObj.put("z", origin.getZ());
            jsonObj.put("origin", originLocJsonObj);

            JSONArray destJsonArray = new JSONArray();

            for (int i = 0; i < dests.size(); i++) {

                destLoc = dests.get(i);
                JSONObject destObj = new JSONObject();
                destObj.put("x", destLoc.getX());
                destObj.put("y", destLoc.getY());
                destObj.put("z", destLoc.getZ());

                destJsonArray.put(destObj);

            }

            jsonObj.put("dests", destJsonArray);

            content = jsonObj.toString(2);

        } catch (Throwable t) {
            t.printStackTrace();
            jsonObj = null;
            content = null;
        }

        return content;

    }

    private static List<ILocation> parseJson(String content) {

        List<ILocation> dests = null;

        try {
            JSONTokener jsonTokener = new JSONTokener(content);
            dests = new ArrayList<ILocation>();
            JSONObject json = (JSONObject) jsonTokener.nextValue();

            double x = -1;
            double y = -1;
            int z = -100;

            // JSONObject origin = json.getJSONObject("origin");
            // x = origin.getDouble("x");
            // y = origin.getDouble("y");
            // z = origin.getInt("z");
            //
            // Location originLoc = new Location((float) x, (float) y, z);
            //
            // dests.add(originLoc);

            JSONArray jDestsObj = json.getJSONArray("dests");

            for (int i = 0; i < jDestsObj.length(); i++) {

                JSONObject dest = jDestsObj.getJSONObject(i);
                x = dest.getDouble("x");
                y = dest.getDouble("y");
                z = dest.getInt("z");
                Location destPoiLoc = new Location((float) x, (float) y, z);
                dests.add(destPoiLoc);

            }

        } catch (Throwable t) {
            dests = null;
            t.printStackTrace();
        }

        return dests;
    }

    // == new method  ===
    public static List<IPoi> getMixedPoiOrder(ILocation origin, List<IPoi> dests, List<IPoi> exitsAndEntrancesList) {
        List<IPoi> result = new ArrayList<IPoi>();


        // convert poi to location objects
        HashMap<String, String> poimap = new HashMap<String, String>();
        List<ILocation> locs = new ArrayList<ILocation>();
        List<ILocation> exits = new ArrayList<ILocation>();
        for (IPoi o : dests) {
            ILocation loc = new Location(o.getX(), o.getY(), (float) o.getZ());
            loc.setLat(o.getPoiLatitude());
            loc.setLon(o.getPoiLongitude());
            loc.setType("internal".equals(o.getPoiNavigationType()) ? LocationMode.INDOOR_MODE : LocationMode.OUTDOOR_MODE);
            String sloc = getLocationKey(loc);
            locs.add(loc);
            poimap.put(sloc, o.getPoiID());
        }

        for (IPoi o : exitsAndEntrancesList) {
            ILocation loc = new Location(o.getX(), o.getY(), (float) o.getZ());
            loc.setLat(o.getPoiLatitude());
            loc.setLon(o.getPoiLongitude());
            loc.setType("internal".equals(o.getPoiNavigationType()) ? LocationMode.INDOOR_MODE : LocationMode.OUTDOOR_MODE);
            String sloc = getLocationKey(loc);
            exits.add(loc);
            poimap.put(sloc, o.getPoiID());
        }


        // prepare for ndk function - convert to arrays []
        File dir = PropertyHolder.getInstance().getFacilityDir();
        File sfile = new File(dir, "switchfloor.txt");
        String switchFloorFilePath = sfile.getAbsolutePath();

        int generationsCount = -1;

        FLocation fOrigin = convertToFLocation(origin);

        FLocation[] fDests = new FLocation[dests.size()];
        FLocation[] fullSolutionPath = new FLocation[dests.size() + exitsAndEntrancesList.size()];

        FLocation[] poisExitsList = new FLocation[exitsAndEntrancesList.size()];

        for (int i = 0; i < dests.size(); i++) {
            ILocation iloc = locs.get(i);
            fDests[i] = convertToFLocation(iloc);
        }

        for (int i = 0; i < exitsAndEntrancesList.size(); i++) {
            ILocation iloc = exits.get(i);
            poisExitsList[i] = convertToFLocation(iloc);
        }

        for (int i = 0; i < fullSolutionPath.length; i++) {
            fullSolutionPath[i] = new FLocation();
        }

        try {
            // call ndk function
            NdkGeneticPathOrderFinder geneticOrderAlg = new NdkGeneticPathOrderFinder();
            geneticOrderAlg.getMixedPoisOrder(switchFloorFilePath, generationsCount, fOrigin, fDests, poisExitsList, fullSolutionPath);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        // convert sorted locations to pois
        List<ILocation> orderdPois = new ArrayList<ILocation>();
        for (FLocation fPoi : fullSolutionPath) {

            ILocation loc = convertToILocation(fPoi);
            orderdPois.add(loc);

        }

        for (ILocation o : orderdPois) {
            String sloc = getLocationKey(o);
            IPoi poi = getPoiById(poimap.get(sloc));
            if (poi != null) {
                result.add(poi);
            }

        }

        return result;
    }

    private static String getLocationKey(ILocation loc) {
        String result = "";
        LocationMode mode = loc.getLocationType();
        if (mode != null && mode.equals(LocationMode.INDOOR_MODE)) {
            result = String.format("%d,%d,%d", (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
        } else if (mode != null && mode.equals(LocationMode.OUTDOOR_MODE)) {
            result = String.format("%f,%f", loc.getLat(), loc.getLon());
        }

        return result;
    }

    public static List<IPoi> getMultiFacilitiesPoiOrder(ILocation origin, List<IPoi> dests) {


        String campusId = null;
        Campus campus = ProjectConf.getInstance().getSelectedCampus();

        if (campus == null) {
            return dests;
        }

        campusId = campus.getId();


        Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();
        List<IPoi> result = new ArrayList<IPoi>();
        List<ILocation> facilitiesCenterList = new ArrayList<ILocation>();
        Set<String> facilitiesSet = new HashSet<String>();

        // convert poi to location objects
        HashMap<String, String> poimap = new HashMap<String, String>();
        List<ILocation> locs = new ArrayList<ILocation>();

        for (IPoi o : dests) {

            if (o == null) {
                continue;
            }

            String facilityId = o.getFacilityID();
            if (facilityId != null && !facilityId.equals("unknown") && !facilityId.equals("")) {
                facilitiesSet.add(facilityId);
            }


            ILocation loc = new Location(o.getX(), o.getY(), (float) o.getZ());
            loc.setLat(o.getPoiLatitude());
            loc.setLon(o.getPoiLongitude());
            loc.setType("internal".equals(o.getPoiNavigationType()) ? LocationMode.INDOOR_MODE : LocationMode.OUTDOOR_MODE);
            loc.setFacilityId(facilityId);
            String sloc = getLocationMultiFacilityKey(loc);
            locs.add(loc);
            poimap.put(sloc, o.getPoiID());
        }


        String originFacilityId = origin.getFacilityId();
        if (originFacilityId != null && !originFacilityId.equals("unknown") && !originFacilityId.equals("")) {
            facilitiesSet.add(originFacilityId);
        }

        // check if all pois from same campus
        if (facilitiesSet.size() > 0) {


            if (facilitiesmap != null && facilitiesmap.size() > 0) {

                for (String facilityId : facilitiesSet) {
                    FacilityConf facConf = facilitiesmap.get(facilityId);
                    if (facConf != null) {
                        double lat = facConf.getCenterLatitude();
                        double lon = facConf.getCenterLongtitude();

                        ILocation center = new Location();
                        center.setFacilityId(facilityId);
                        center.setLat(lat);
                        center.setLon(lon);
                        center.setType(LocationMode.OUTDOOR_MODE);
                        facilitiesCenterList.add(center);
                    }
                }
            }
        }


        // prepare for ndk function - convert to arrays []

        int generationsCount = -1;

        // make convention if origin is indoor
        if (origin.getLocationType() == LocationMode.INDOOR_MODE) {
            String originFacId = origin.getFacilityId();
            FacilityConf originFacConf = facilitiesmap.get(originFacId);
            LatLng covLatLng = convertToLatlng(origin.getX(), origin.getY(), originFacConf);
            origin.setLat(covLatLng.latitude);
            origin.setLon(covLatLng.longitude);
        }

        FLocation fOrigin = convertToFLocation(origin);

        FLocation[] fDests = new FLocation[dests.size()];
        FLocation[] fullSolutionPath = new FLocation[dests.size()];

        FLocation[] centersList = new FLocation[facilitiesCenterList.size()];

        for (int i = 0; i < dests.size(); i++) {
            ILocation iloc = locs.get(i);
            fDests[i] = convertToFLocation(iloc);
        }

        for (int i = 0; i < facilitiesCenterList.size(); i++) {
            ILocation iloc = facilitiesCenterList.get(i);
            centersList[i] = convertToFLocation(iloc);
        }

        for (int i = 0; i < fullSolutionPath.length; i++) {
            fullSolutionPath[i] = new FLocation();
        }

        String appDir = PropertyHolder.getInstance().getAppDir().getAbsolutePath();
        String projectId = PropertyHolder.getInstance().getProjectId();

        try {
            // call ndk function
            NdkGeneticPathOrderFinder geneticOrderAlg = new NdkGeneticPathOrderFinder();
            geneticOrderAlg.getMultiFacilitiesPoisOrder(appDir, projectId, campusId, generationsCount, fOrigin, fDests, centersList, fullSolutionPath);

        } catch (Throwable t) {
            t.printStackTrace();
        }

        // convert sorted locations to pois
        List<ILocation> orderdPois = new ArrayList<ILocation>();
        for (FLocation fPoi : fullSolutionPath) {

            ILocation loc = convertToILocation(fPoi);
            orderdPois.add(loc);

        }

        for (ILocation o : orderdPois) {
            String sloc = getLocationMultiFacilityKey(o);
            IPoi poi = getPoiById(poimap.get(sloc));
            if (poi != null) {
                result.add(poi);
            }

        }

        return result;
    }

    // === multi facility pois order

    private static String getLocationMultiFacilityKey(ILocation loc) {
        String result = "";
        LocationMode mode = loc.getLocationType();
        if (mode != null && mode.equals(LocationMode.INDOOR_MODE)) {
            result = String.format("%s,%d,%d,%d", loc.getFacilityId(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
        } else if (mode != null && mode.equals(LocationMode.OUTDOOR_MODE)) {
            result = String.format("%f,%f", loc.getLat(), loc.getLon());
        }

        return result;
    }

    public static LatLng convertToLatlng(double x, double y, FacilityConf fac) {

        LatLng result = null;

        try {

            if (fac != null) {
                NdkLocation point = new NdkLocation(x, y);
                point.setZ(-1); // non relevant

                NdkLocation covertedPoint = new NdkLocation();

                NdkConversionUtils converter = new NdkConversionUtils();

                double rotationAngle = fac.getRot_angle();

                converter.convertPoint(point, fac.getConvRectTLlon(),
                        fac.getConvRectTLlat(), fac.getConvRectTRlon(),
                        fac.getConvRectTRlat(), fac.getConvRectBLlon(),
                        fac.getConvRectBLlat(), fac.getConvRectBRlon(),
                        fac.getConvRectBRlat(), fac.getMapWidth(),
                        fac.getMapHight(), rotationAngle, covertedPoint);

                if (covertedPoint != null) {
                    result = new LatLng(covertedPoint.getLat(),
                            covertedPoint.getLon());
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }

    private static class MultiNavDestsOrderTask extends AsyncTask<String, String, String> {

        private final String SERVERADDRESS = PropertyHolder.getInstance().getServerName() + "navtopois";

        @Override
        protected String doInBackground(String... req) {
            String result = null;
            try {
                String content = req[0];
                URL obj = new URL(SERVERADDRESS);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // add reuqest header
                con.setRequestMethod("POST");

                con.setRequestProperty("charset", "utf-8");

                con.setRequestProperty("Content-Length", "" + Integer.toString(content.length()));

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(content);
                wr.flush();
                wr.close();

                //int responseCode = con.getResponseCode();
                //System.out.println("\nSending 'POST' request to URL : " + SERVERADDRESS);
                //System.out.println("Post parameters : " + content);
                //System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                result = response.toString();

            } catch (Throwable e) {
                e.printStackTrace();
            }
            return result;
        }

    }

}
