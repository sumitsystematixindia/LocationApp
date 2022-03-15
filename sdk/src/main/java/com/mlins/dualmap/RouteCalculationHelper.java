package com.mlins.dualmap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.aStar.CampusNavigationPath;
import com.mlins.aStar.FloorNavigationPath;
import com.mlins.aStar.GisSegment;
import com.mlins.aStar.NavigationPath;
import com.mlins.aStar.aStarAlgorithm;
import com.mlins.aStar.aStarData;
import com.mlins.instructions.Instruction;
import com.mlins.instructions.InstructionBuilder;
import com.mlins.instructions.Instructionobject;
import com.mlins.instructions.NavInstruction;
import com.mlins.navigation.PathCalculator;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.project.bridges.Bridge;
import com.mlins.utils.Cleanable;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.Lookup;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PoiData;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceTranslator;
import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.GisPoint;
import com.mlins.utils.gis.Location;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.INavInstruction;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.sdk.data.SpreoDataProvider;
import com.spreo.spreosdk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gps.CampusGisData;

public class RouteCalculationHelper implements Cleanable {

    private HashMap<String, NavigationPath> indoorNavPaths = new HashMap<String, NavigationPath>();
    private List<CampusNavigationPath> outdoorNavPath = new ArrayList<>();
    private List<Instruction> CombinedInstructions = new ArrayList<Instruction>();
    private HashMap<Location, String> exitPoints = new HashMap<>();
    private List<List<Location>> bridgesPoints = new ArrayList<>();
    private List<List<Location>> switchFloorPoints = new ArrayList<>();
    private Location origin = null;
    private Location destination = null;
    private List<Object> fullRoute = new ArrayList<>();
    private double routeDistance = 0;
    private double timeEstimation = 0;
    public final static String TYPE_EXIT = "exit";
    public final static String TYPE_ENTRANCE = "entrance";
    private PoiData finalDestination = null;
    private GisLine currentClosestNavLine = null;
    private int onSameNavLineCounter = 0;
    private double lastBearing = 0;
    private List<INavInstruction> combinedSimplifiedInstructions = new ArrayList<INavInstruction>();
    private int simplifiedCombinedInstructionId = 0;
    private static final int WALK_INSTRUCTION_TYPE = 1;
    private static final int DESTINATION_INSTRUCTION_TYPE = 5;

    public List<List<Location>> getBridgesPoints() {
        return bridgesPoints;
    }

    public HashMap<Location, String> getExitPoints() {
        return exitPoints;
    }

    public List<Object> getFullRoute() {
        return fullRoute;
    }


    public static RouteCalculationHelper getInstance() {
        return Lookup.getInstance().get(RouteCalculationHelper.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(RouteCalculationHelper.class);
    }

    public void clean() {
        indoorNavPaths.clear();
        outdoorNavPath.clear();
        CombinedInstructions.clear();
        fullRoute.clear();
        exitPoints.clear();
        bridgesPoints.clear();
        switchFloorPoints.clear();
        origin = null;
        destination = null;
        routeDistance = 0;
        timeEstimation = 0;
        finalDestination = null;
        combinedSimplifiedInstructions.clear();
        simplifiedCombinedInstructionId = 0;
    }

    public void cleanNavRotationParams() {
        currentClosestNavLine = null;
        onSameNavLineCounter = 0;
        lastBearing = 0;
    }

    public void calculatePathFromServer(String response, PoiData destinationpoi, boolean isReroute) {
        clean();

        if (!isReroute) {
            cleanNavRotationParams();
        }

        LocationMode destmode = destinationpoi.getLocation().getLocationType();
        DestinationPoi destpoi = new DestinationPoi(destinationpoi,
                destmode);
        List<DestinationPoi> destpois = new ArrayList<>();
        destpois.add(destpoi);

        finalDestination = destinationpoi;

        int idcounter = 0;
        int parent = -1;
        Location tmporigin = null;
        Location tmpdestination = null;


        try {
            JSONObject data = new JSONObject(response);

            setNavigationPoints(data);

            JSONArray pathsarray = data.getJSONArray("navigation_paths");
            for (int i = 0; i < pathsarray.length(); i++) {
                JSONObject p = pathsarray.getJSONObject(i);
                int mode = p.getInt("mode");
                String fid = p.getString("fid");
                double z = 0;
                if (mode == 0) {
                    z = p.getInt("floor");
                } else {
                    z = getEntranceFloor();
                }

                JSONArray sarray = p.getJSONArray("segments");
                List<GisSegment> segments = new ArrayList<>();
                List<INavInstruction> simplifiedlist = new ArrayList<>();
                for (int j = 0; j < sarray.length(); j++) {
                    JSONObject s = sarray.getJSONObject(j);

                    try {
                        JSONObject simplifiedins = s.getJSONObject("simplifiedInstruction");
                        INavInstruction ins = getSimplifiedInstruction(simplifiedins);
                        if (ins != null) {
                            combinedSimplifiedInstructions.add(ins);
                            simplifiedlist.add(ins);
                        }
                    } catch (JSONException e) {

                    }

                    boolean ignore = s.getBoolean("bridgeConnector");
                    if (!ignore || sarray.length() == 1) {
                        if (mode == 1) {
                            idcounter++;
                            JSONObject start = s.getJSONObject("start");
                            JSONObject end = s.getJSONObject("end");
                            double sx = start.getDouble("longitude");
                            double sy = start.getDouble("latitude");
                            double ex = end.getDouble("longitude");
                            double ey = end.getDouble("latitude");
                            GisPoint p1 = new GisPoint(sx, sy, z);
                            GisPoint p2 = new GisPoint(ex, ey, z);
                            ;
                            GisLine gl = new GisLine(p1, p2, z);
                            GisSegment gs = new GisSegment(gl, idcounter);
                            gs.setParent(parent);
                            segments.add(gs);
                            parent = gs.getId();
                            if (tmporigin == null) {
                                String campuid = start.getString("cid");
                                tmporigin = new Location(new LatLng(sy, sx));
                                tmporigin.setCampusId(campuid);
                            }
                            String campuid = start.getString("cid");
                            tmpdestination = new Location(new LatLng(ey, ex));
                            tmpdestination.setCampusId(campuid);
                        } else {
                            Boolean virtual = s.getBoolean("virtual");
                            if (!virtual || PropertyHolder.getInstance().isDrawRouteTails() || sarray.length() == 1) {
                                idcounter++;
                                JSONObject start = s.getJSONObject("start");
                                JSONObject end = s.getJSONObject("end");
                                double sx = start.getDouble("x");
                                double sy = start.getDouble("y");
                                double ex = end.getDouble("x");
                                double ey = end.getDouble("y");
                                GisPoint p1 = new GisPoint(sx, sy, z);
                                GisPoint p2 = new GisPoint(ex, ey, z);
                                ;
                                GisLine gl = new GisLine(p1, p2, z);
                                GisSegment gs = new GisSegment(gl, idcounter);
                                gs.setParent(parent);
                                segments.add(gs);
                                parent = gs.getId();

                                if (tmporigin == null) {
                                    String campuid = start.getString("cid");
                                    String facilityid = start.getString("fid");
                                    tmporigin = new Location(facilityid, campuid, (float) sx, (float) sy, (float) z);
                                }
                                String campuid = start.getString("cid");
                                String facilityid = start.getString("fid");
                                tmpdestination = new Location(facilityid, campuid, (float) ex, (float) ey, (float) z);
                            }

                        }
                    }


                }
                if (!segments.isEmpty()) {
                    if (mode == 1) {
                        CampusNavigationPath cpath = new CampusNavigationPath(segments);
                        outdoorNavPath.add(cpath);
                        double outDoorDistance = 0;
                        for (GisSegment segment : segments) {
                            outDoorDistance += segment.getLine().getDistanceBetweenPoints();
                        }
                        boolean isdetination = false;
                        if (i == pathsarray.length() - 1) {
                            isdetination = true;
                        }
                        Instruction instruction = getOutdoorInstruction(outDoorDistance, isdetination);
                        if (instruction != null) {
                            CombinedInstructions.add(instruction);
                        }

                        NavInstruction sins = getMeregedInstruction(simplifiedlist);
                        if (sins != null) {
                            cpath.setSimplifiedInstruction(sins);
                        }

                        fullRoute.add(cpath);
                    } else {
                        String facilityid = p.getString("fid");
                        String campusid = PropertyHolder.getInstance().getCampusId();
                        NavigationPath inpath = indoorNavPaths.get(facilityid);
                        if (inpath == null) {
                            inpath = new NavigationPath();
                            inpath.setFacilityId(facilityid);
                            indoorNavPaths.put(facilityid, inpath);
                        }

                        FloorNavigationPath fpath = new FloorNavigationPath(z, segments);
                        fpath.setCampusId(campusid);
                        fpath.setFacilityId(facilityid);
                        inpath.addFloorNavigationPath(fpath);
                        fullRoute.add(fpath);

                        NavInstruction sins = getMeregedInstruction(simplifiedlist);
                        if (sins != null) {
                            fpath.setSimplifiedInstruction(sins);
                        }

                        if (i >= pathsarray.length() - 1 || !isTheSameFacility(pathsarray.getJSONObject(i + 1), facilityid)) {
                            List<Instruction> instructions = InstructionBuilder.getInstance().getInstractions(inpath);
                            setClosePoisForInstructions(instructions, facilityid);
                            if (instructions != null && !instructions.isEmpty()) {
                                for (Instruction o : instructions) {
                                    if (o.getType() == Instruction.TYPE_DESTINATION) {
//                                boolean isFinalDest = destpois.indexOf(dest) == destpois.size() - 1;
//                                Instruction ins = getExitInstruction(o, facid, isFinalDest);
                                        Instruction ins = getExitInstruction(o, facilityid);
                                        CombinedInstructions.add(ins);
                                    } else {
                                        CombinedInstructions.add(o);
                                    }
                                }
                            }
                        }

                    }
                }
            }

            origin = tmporigin;
            destination = tmpdestination;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private NavInstruction getMeregedInstruction(List<INavInstruction> simplifiedlist) {
        NavInstruction result = null;
        try {
            if (simplifiedlist.size() == 2) {
                INavInstruction ins1 = simplifiedlist.get(0);
                INavInstruction ins2 = simplifiedlist.get(1);
                int id = ins1.getId();
                int type = ins2.getType();
                int distance = (int)Math.ceil(ins1.getDistance());
                String distancetxet = getDistanceText(distance);
                String text1 = ins1.getText();
                String text2 = adjustCase(ins2.getText());
                String msg = "";
                if (type == DESTINATION_INSTRUCTION_TYPE) {
                    msg = text1 + " " + distancetxet + " " + getToText() + " " + text2;
                    msg = msg.replace(getArriveAtText() + " ", "");
                } else {
                    msg = text1 + " " + distancetxet + " " + getThenText() + " " + text2;
                }
                Bitmap bmp = ins2.getSignBitmap();
                result = new NavInstruction(id, msg, bmp, distance);
                result.setType(type);
            }
        } catch (Throwable t) {

        }
        return result;
    }

    private String adjustCase(String txt) {
        String result = txt;
        try{
            int i = txt.indexOf(' ');
            String first = txt.substring(0, i);
            String rest = txt.substring(i);
            first = first.toLowerCase();
            result = first + rest;
        } catch (Throwable t) {

        }
        return result;
    }


    private String getToText() {
        String result = "";
        int tmp = ResourceTranslator.getInstance().getTranslatedResourceId("string", "to");
        Resources res = PropertyHolder.getInstance().getMlinsContext().getResources();
        result = res.getString(tmp);
        return result;
    }

    private String getThenText() {
        String result = "";
        int tmp = ResourceTranslator.getInstance().getTranslatedResourceId("string", "then");
        Resources res = PropertyHolder.getInstance().getMlinsContext().getResources();
        result = res.getString(tmp);
        return result;
    }

    private String getArriveAtText() {
        String result = "";
        int tmp = ResourceTranslator.getInstance().getTranslatedResourceId("string", "Arrive_at");
        Resources res = PropertyHolder.getInstance().getMlinsContext().getResources();
        result = res.getString(tmp);
        return result;
    }

    private String getDistanceText(int distance) {
        String result = "";
        int unitres = ResourceTranslator.getInstance().getTranslatedResourceId("string", "distance_unit");
        Resources res = PropertyHolder.getInstance().getMlinsContext().getResources();
        String unit = res.getString(unitres);
        result = distance + " " + unit;
        return result;
    }

    private INavInstruction getSimplifiedInstruction(JSONObject simplifiedins) {
        INavInstruction result = null;
        try {
            int type = 0;
            String msg = "";
            double distance = 0;
            try {
                type = simplifiedins.getInt("type");
            } catch (JSONException e) {

            }
            try {
                msg = simplifiedins.getString("msg");
            } catch (JSONException e) {

            }
            try {
                if (type == WALK_INSTRUCTION_TYPE) {
                    distance = simplifiedins.getLong("distance");
                }
            } catch (JSONException e) {

            }
            Resources res = PropertyHolder.getInstance().getMlinsContext().getResources();
            int resid = ResourceTranslator.getInstance().getTranslatedResourceId("drawable", "type_" + type);
            Bitmap bmp = BitmapFactory.decodeResource(res, resid);
            simplifiedCombinedInstructionId++;
            int id = simplifiedCombinedInstructionId;
            result = new NavInstruction(id, msg, bmp, distance);
            result.setType(type);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private void setNavigationPoints(JSONObject json) {

        try {
            JSONArray exitsarray = json.getJSONArray("exits");
            for (int i = 0; i < exitsarray.length(); i++) {
                JSONObject exitjson = exitsarray.getJSONObject(i);
                Location exitloc = getLocationFromJson(exitjson);
                exitPoints.put(exitloc, TYPE_EXIT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray entrancesarray = json.getJSONArray("entrances");
            for (int i = 0; i < entrancesarray.length(); i++) {
                JSONObject entrancejson = entrancesarray.getJSONObject(i);
                Location entranceloc = getLocationFromJson(entrancejson);
                exitPoints.put(entranceloc, TYPE_ENTRANCE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray bridgesarray = json.getJSONArray("bridges");
            for (int i = 0; i < bridgesarray.length(); i++) {
                JSONObject bridgejson = bridgesarray.getJSONObject(i);
                JSONObject from = bridgejson.getJSONObject("from");
                JSONObject to = bridgejson.getJSONObject("to");
                List<Location> bridges = new ArrayList<>();
                Location brdigefrom = getLocationFromJson(from);
                bridges.add(brdigefrom);
                Location brdigeto = getLocationFromJson(to);
                bridges.add(brdigeto);
                bridgesPoints.add(bridges);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray switcharray = json.getJSONArray("switch_floors");
            for (int i = 0; i < switcharray.length(); i++) {
                JSONObject switchjson = switcharray.getJSONObject(i);
                JSONObject from = switchjson.getJSONObject("from");
                JSONObject to = switchjson.getJSONObject("to");
                List<Location> switches = new ArrayList<>();
                Location switchfrom = getLocationFromJson(from);
                switches.add(switchfrom);
                Location switchto = getLocationFromJson(to);
                switches.add(switchto);
                switchFloorPoints.add(switches);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            routeDistance = json.getDouble("distance");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            timeEstimation = json.getDouble("time_estimation");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Location getLocationFromJson(JSONObject json) {
        Location result = null;
        try {
            int mode = json.getInt("mode");
            if (mode == 0) {
                double x = json.getDouble("x");
                double y = json.getDouble("y");
                double z = json.getDouble("floor");
                String campuid = json.getString("cid");
                String facilityid = json.getString("fid");
                result = new Location(facilityid, campuid, (float)x, (float)y, (float)z);
            } else {
                double lat = json.getDouble("longitude");
                double lon = json.getDouble("latitude");
                String campuid = json.getString("cid");
                result = new Location(new LatLng(lat, lon));
                result.setCampusId(campuid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean isTheSameFacility(JSONObject path, String fid) {
        boolean result = false;
        try {
            int mode = path.getInt("mode");
            if (mode == 0) {
                String nextfid = path.getString("fid");
                if (fid != null && nextfid != null && fid.equals(nextfid)) {
                    result = true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Location getEndLocation(JSONObject path) {
        Location result = null;
        try {
            if (path.length() > 0) {
                JSONArray sarray = path.getJSONArray("segments");
                JSONObject s = sarray.getJSONObject(sarray.length() - 1);
                JSONObject end = s.getJSONObject("end");
                double ex = end.getDouble("x");
                double ey = end.getDouble("y");
                double ez = end.getDouble("floor");
                String campuid = end.getString("cid");
                String facilityid = end.getString("fid");
                result = new Location(facilityid, campuid, (float)ex, (float)ey, (float)ez);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Location getStartLocation(JSONObject path) {
        Location result = null;
        try {
            if (path.length() > 0) {
                JSONArray sarray = path.getJSONArray("segments");
                JSONObject s = sarray.getJSONObject(0);
                JSONObject start = s.getJSONObject("start");
                double sx = start.getDouble("x");
                double sy = start.getDouble("y");
                double sz = start.getDouble("floor");
                String campuid = start.getString("cid");
                String facilityid = start.getString("fid");
                result = new Location(facilityid, campuid, (float)sx, (float)sy, (float)sz);
            }
        } catch (JSONException e) {
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

    private JSONObject reorderSegment(JSONObject s, JSONObject last) {
        JSONObject result = null;


            try {
                JSONObject start = s.getJSONObject("start");
                JSONObject end = s.getJSONObject("end");
                JSONObject lastend = last.getJSONObject("end");
                double ex = lastend.getDouble("longitude");
                double ey = lastend.getDouble("latitude");
                double sx = start.getDouble("longitude");
                double sy = start.getDouble("latitude");
                if (!(ex == sx && ey == sy)) {
                    s.put("start", end);
                    s.put("end", start);
                }
                result = s;
            } catch (JSONException e) {
                e.printStackTrace();
            }


        return result;
    }

    public void calculateCombinedPath(ILocation origin, List<DestinationPoi> destpois) {
        indoorNavPaths.clear();
        CombinedInstructions.clear();
        outdoorNavPath = null;
        try {
            if (destpois != null && !destpois.isEmpty()) {
                Location start = new Location(origin);
                PoiData lastpoi = null;
                for (DestinationPoi dest : destpois) {
                    if (dest != null) {
                        if (dest.getMode() == LocationMode.INDOOR_MODE) {
                            if (lastpoi != null) {
                                start = new Location(lastpoi.getX(), lastpoi.getY(), (float) lastpoi.getZ());
                                start.setFacilityId(lastpoi.getFacilityID());
                                start.setCampusId(lastpoi.getCampusID());
                            }
                            Location destloc = new Location(dest.getPoi());
                            if (start.getFacilityId().equals(dest.getFacilityId())) {
                                NavigationPath shortPath = PathCalculator.navigateFromTo(
                                        start, destloc);
                                if (shortPath != null) {
                                    String facid = dest.getFacilityId();
                                    if (facid != null) {
                                        shortPath.setFacilityId(facid);
                                        indoorNavPaths.put(facid, shortPath);
                                        List<Instruction> instructions = InstructionBuilder.getInstance().getInstractions(shortPath);
                                        setClosePoisForInstructions(instructions, facid);
                                        if (instructions != null && !instructions.isEmpty()) {
                                            for (Instruction i : instructions) {
                                                if (i.getType() == Instruction.TYPE_DESTINATION) {
                                                    boolean isFinalDest = destpois.indexOf(dest) == destpois.size() - 1;
                                                    Instruction ins = getExitInstruction(i, facid, isFinalDest);
                                                    CombinedInstructions.add(ins);
                                                } else {
                                                    CombinedInstructions.add(i);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            lastpoi = dest.getPoi();
                        } else if (dest.getMode() == LocationMode.OUTDOOR_MODE) {
                            if (lastpoi != null) {
                                LatLng latlng = new LatLng(lastpoi.getPoiLatitude(), lastpoi.getPoiLongitude());
                                start = new Location(latlng);
                            }

                            CampusNavigationPath tmp = calculateCampusPath(start, dest.getPoi());
                            if (tmp != null && tmp.getPath() != null && !tmp.getPath().isEmpty()) {
                                if (outdoorNavPath == null) {
                                    outdoorNavPath.add(new CampusNavigationPath(tmp.getPath()));
                                } else {
//                                    outdoorNavPath.addPath(tmp.getPath());
                                }

                                double outDoorDistance = 0;

                                List<GisSegment> segments = new ArrayList<>(outdoorNavPath.get(0).getPath());
                                for (GisSegment segment : segments) {
                                    outDoorDistance += segment.getLine().getDistanceBetweenPoints();
                                }

                                Instruction instruction = getOutdoorInstruction(outDoorDistance);
                                if (instruction != null) {
                                    CombinedInstructions.add(instruction);
                                }
                            }

                            lastpoi = dest.getPoi();
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public CampusNavigationPath calculateCampusPath(ILocation myloc, PoiData outdoorpoi) {
        CampusNavigationPath result = null;
        try {
            PoiData dest = outdoorpoi;
            if (myloc != null && dest != null) {
                double mylocationlat = myloc.getLat();
                double mylocationlon = myloc.getLon();
                LatLng mylatlng = new LatLng(mylocationlat, mylocationlon);
                double destlat = dest.getPoiLatitude();
                double destlon = dest.getPoiLongitude();
                LatLng destLatLng = new LatLng(destlat, destlon);
                List<GisLine> l = CampusGisData.getInstance().getLines();
                if (l != null && !l.isEmpty() && isCloseToKml(mylatlng)) {
                    aStarData.getInstance().cleanAStar();
                    GisPoint startpoint = new GisPoint(mylocationlon,
                            mylocationlat, 0);
                    GisPoint endpoint = new GisPoint(destlon, destlat, 0);
                    aStarData.getInstance().loadData(startpoint, endpoint, l);
                    aStarAlgorithm a = new aStarAlgorithm(startpoint, endpoint);
                    List<GisSegment> path = null;
                    path = a.getPath();
                    if (path != null && !path.isEmpty()) {
                        List<GisSegment> finalpath = getPathWithEdges(mylatlng, destLatLng, path);
                        result = new CampusNavigationPath(finalpath);
                    } else {
                        result = getLineRoute(mylatlng, destLatLng);
                    }
                } else {
                    result = getLineRoute(mylatlng, destLatLng);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }

    public CampusNavigationPath calculateCampusPath(LatLng p1, LatLng p2) {
        CampusNavigationPath result = null;
        try {
                List<GisLine> l = CampusGisData.getInstance().getLines();
                if (l != null && !l.isEmpty() && isCloseToKml(p1)) {
                    aStarData.getInstance().cleanAStar();
                    GisPoint startpoint = new GisPoint(p1.longitude,
                            p1.latitude, 0);
                    GisPoint endpoint = new GisPoint(p2.longitude, p2.latitude, 0);
                    aStarData.getInstance().loadData(startpoint, endpoint, l);
                    aStarAlgorithm a = new aStarAlgorithm(startpoint, endpoint);
                    List<GisSegment> path = null;
                    path = a.getPath();
                    if (path != null && !path.isEmpty()) {
                        List<GisSegment> finalpath = getPathWithEdges(p1, p2, path);
                        result = new CampusNavigationPath(finalpath);
                    } else {
                        result = getLineRoute(p1, p2);
                    }
                } else {
                    result = getLineRoute(p1, p2);
                }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }

    private boolean isCloseToKml(LatLng mylatlng) {
        boolean result = false;
        try {
            Campus campus = ProjectConf.getInstance().getSelectedCampus();
            double distanceFromKml = 10;
            if (campus != null) {
                distanceFromKml = campus.getDistance_from_kml();
            }
            LatLng projectedloc = CampusGisData.getInstance().findClosestPointOnLine(mylatlng);
            double dfromloc = MathUtils.distance(mylatlng, projectedloc);
            if (dfromloc < distanceFromKml) {
                result = true;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private List<GisSegment> getPathWithEdges(LatLng mylatlng, LatLng destLatLng, List<GisSegment> navpath) {
        List<GisSegment> result = new ArrayList<GisSegment>();
        try {
            LatLng projectedloc = CampusGisData.getInstance().findClosestPointOnLine(mylatlng, navpath);
            LatLng projecteddest = CampusGisData.getInstance().findClosestPointOnLine(destLatLng, navpath);
            double myz = 0;
            double myx1 = mylatlng.longitude;
            double myy1 = mylatlng.latitude;
            double myx2 = projectedloc.longitude;
            double myy2 = projectedloc.latitude;
            GisPoint myp1 = new GisPoint(myx1, myy1, myz);
            GisPoint myp2 = navpath.get(0).getLine().getPoint1(); //new GisPoint(myx2, myy2, myz);
            GisLine mytmpline = new GisLine(myp1, myp2, myz);
            int mysid = 999;
            GisSegment mys = new GisSegment(mytmpline, mysid);
            result.add(mys);

            result.addAll(navpath);

            double destz = 0;
            double destx1 = destLatLng.longitude;
            double desty1 = destLatLng.latitude;
            double destx2 = projecteddest.longitude;
            double desty2 = projecteddest.latitude;
            GisPoint destp1 = navpath.get(navpath.size() - 1).getLine().getPoint2(); //new GisPoint(destx2, desty2, destz);
            GisPoint destp2 = new GisPoint(destx1, desty1, destz);
            GisLine desttmpline = new GisLine(destp1, destp2, destz);
            int destsid = 888;
            GisSegment dests = new GisSegment(desttmpline, destsid);
            result.add(dests);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    private CampusNavigationPath getLineRoute(LatLng mylatlng, LatLng destLatLng) {
        CampusNavigationPath result = null;
        try {
            List<GisSegment> finalpath = new ArrayList<GisSegment>();
            double z = 0;
            double myx = mylatlng.longitude;
            double myy = mylatlng.latitude;
            double destx = destLatLng.longitude;
            double desty = destLatLng.latitude;
            GisPoint myp = new GisPoint(myx, myy, z);
            GisPoint destp = new GisPoint(destx, desty, z);
            GisLine line = new GisLine(myp, destp, z);
            int sid = 999;
            GisSegment s = new GisSegment(line, sid);
            finalpath.add(s);
            result = new CampusNavigationPath(finalpath);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public HashMap<String, NavigationPath> getIndoorNavPaths() {
        return indoorNavPaths;
    }

    public void setIndoorNavPaths(HashMap<String, NavigationPath> indoorNavPaths) {
        this.indoorNavPaths = indoorNavPaths;
    }

    public List<CampusNavigationPath> getOutdoorNavPath() {
        return outdoorNavPath;
    }

    public void setOutdoorNavPath(List<CampusNavigationPath> outdoorNavPath) {
        this.outdoorNavPath = outdoorNavPath;
    }

    public NavigationPath getFacilityPath(String facilityId) {
        return indoorNavPaths.get(facilityId);
    }

    public List<Instruction> getCombinedInstructions() {
        return CombinedInstructions;
    }

    public void setCombinedInstructions(List<Instruction> combinedInstructions) {
        CombinedInstructions = combinedInstructions;
    }

    private Instruction getExitInstruction(Instruction ins, String facid) {
        Instruction result = ins;
        try {
            if (ins != null && facid != null) {


                String bridgeto = null;
                for (List<Location> o : bridgesPoints) {
                    if (o != null && o.size() > 1) {
                        Location point1 = o.get(0);
                        Location point2 = o.get(1);
                        if (point1 != null && point2 != null) {
                            if (point1.getFacilityId().equals(facid)) {
                                FacilityConf fac = ProjectConf.getInstance().getFacilityConf(point2);
                                if (fac != null) {
                                    bridgeto =  fac.getName();
                                    break;
                                }
                            }
                        }
                    }
                }
                if (bridgeto != null) {
                    result = new Instructionobject(ins.getDistance());
                    result.setLocation(ins.getLocation());
                    result.setSegment(ins.getSegment());
                    int image = R.drawable.outdoor_instruction_sign;
                    int text = ResourceTranslator.getInstance().getTranslatedResourceId("string", "simplified");
                    result.addImage(image);
                    result.addText(text);
                    result.setType(Instruction.TYPE_EXIT);
                    result.setToFacilty(bridgeto);
                    NavInstruction simplifiedins = ins.getSimplifiedInstruction();
                    if (PropertyHolder.getInstance().isSimplifiedInstruction() && simplifiedins != null) {
                        result.setSimplifiedInstruction(simplifiedins);
                    }
                } else {
                    PoiData poi = getFinalDestination();
                    if (poi != null) {
                        if (poi.getpoiDescription() != null && !poi.getpoiDescription().isEmpty()) {
                            ins.setDestinationName(poi.getpoiDescription());
                        }
                        if (poi.getPoiNavigationType().equals("external") || poi.getFacilityID() == null || poi.getFacilityID().equals("unknown") || !poi.getFacilityID().equals(facid)) {
                            result = new Instructionobject(ins.getDistance());
                            result.setLocation(ins.getLocation());
                            result.setSegment(ins.getSegment());
                            int image = R.drawable.mp_exit;
                            int text = ResourceTranslator.getInstance().getTranslatedResourceId("string", "exit_building");
                            result.addImage(image);
                            result.addText(text);
                            result.setType(Instruction.TYPE_EXIT);
                            NavInstruction simplifiedins = ins.getSimplifiedInstruction();
                            if (PropertyHolder.getInstance().isSimplifiedInstruction() && simplifiedins != null) {
                                result.setSimplifiedInstruction(simplifiedins);
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            result = ins;
        }
        return result;
    }

    private Instruction getExitInstruction(Instruction ins, String facid, boolean isfinalfest) {
        Instruction result = ins;
        try {
            if (ins != null && facid != null) {

                String bridgeto = null;
                for (List<Location> o : bridgesPoints) {
                    if (o != null && o.size() > 1) {
                        Location point1 = o.get(0);
                        Location point2 = o.get(1);
                        if (point1 != null && point2 != null) {
                            if (point1.getFacilityId().equals(facid)) {
                                FacilityConf fac = ProjectConf.getInstance().getFacilityConf(point2);
                                if (fac != null) {
                                    bridgeto =  fac.getName();
                                    break;
                                }
                            }
                        }
                    }
                }
                if (bridgeto != null) {
                    result = new Instructionobject(ins.getDistance());
                    result.setLocation(ins.getLocation());
                    result.setSegment(ins.getSegment());
                    int image = R.drawable.outdoor_instruction_sign;
                    int text = ResourceTranslator.getInstance().getTranslatedResourceId("string", "simplified");
                    result.addImage(image);
                    result.addText(text);
                    result.setType(Instruction.TYPE_EXIT);
                    result.setToFacilty(bridgeto);
                } else {
                    PoiData poi = getFinalDestination();
                    if (poi != null) {
                        if (poi.getpoiDescription() != null && !poi.getpoiDescription().isEmpty()) {
                            ins.setDestinationName(poi.getpoiDescription());
                        }
                        if (poi.getPoiNavigationType().equals("external") || poi.getFacilityID() == null || poi.getFacilityID().equals("unknown") || !poi.getFacilityID().equals(facid) || !isfinalfest) {
                            result = new Instructionobject(ins.getDistance());
                            ins.getLocation().setFacilityId(facid);
                            result.setLocation(ins.getLocation());
                            result.setSegment(ins.getSegment());
                            int image = R.drawable.outdoor_instruction_sign;
                            int text = ResourceTranslator.getInstance().getTranslatedResourceId("string", "exit_building");
                            result.addImage(image);
                            result.addText(text);
                            result.setType(Instruction.TYPE_EXIT);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            result = ins;
        }
        return result;
    }

    private Instruction getOutdoorInstruction(double distance) {
        return getOutdoorInstruction(distance, false);
    }

    private Instruction getOutdoorInstruction(double distance, boolean isdestination) {
        Instruction result = null;
        Context ctx = PropertyHolder.getInstance().getMlinsContext();
        if (ctx != null) {
            int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "outdoorInstruction");
            result = new Instructionobject(distance);
            result.setID(999);
            result.addText(tmptxt);
            if (isdestination) {
                result.addImage(R.drawable.destination);
            } else {
                result.addImage(R.drawable.outdoor_instruction_sign);
            }
        }
        return result;
    }

    public void setInstructions(NavigationPath nav, String facilityid) {
        try {
            if (nav != null) {
                List<Instruction> instructions = InstructionBuilder.getInstance().getInstractions(nav);
                if (instructions != null && instructions.size() > 0) {
                    if (!PropertyHolder.getInstance().isSimplifiedInstruction()) {
                        setClosePoisForInstructions(instructions, facilityid);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setClosePoisForInstructions(List<Instruction> instructions, String facilityid) {
        try {
            Campus campus = ProjectConf.getInstance().getSelectedCampus();
            if (campus != null) {
                String campusid = campus.getId();
                if (campusid != null && facilityid != null) {
                    FacilityConf fac = campus.getFacilityConf(facilityid);
                    if (fac != null) {
                        float pixeltometer = fac.getPixelsToMeter();
                        float radius = fac.getPoiForInstructionRadius();
                        for (Instruction o : instructions) {
                            ClosePois(campusid, facilityid, pixeltometer, radius, o);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void ClosePois(String campusid, String facilityid, float pixeltometer, float radius, Instruction instruction) {
        try {
            if (instruction != null
                    && !(instruction.getType() == Instruction.TYPE_DESTINATION)
                    && !(instruction.getType() == Instruction.TYPE_EXIT)) {
                double z = instruction.getLocation().getZ();
                float x = (float) instruction.getLocation().getX();
                float y = (float) instruction.getLocation().getY();
                PointF instructionpoint = new PointF(x, y);
                double mindistance = Double.MAX_VALUE;
                String poiname = "";
                List<IPoi> pois = ProjectConf.getInstance().getAllFloorPoisList(campusid, facilityid, (int) z);
                if (pois != null && !pois.isEmpty()) {
                    for (IPoi poi : pois) {
                        if (poi.isInstructionsParticipate()) {
                            if (poi.getZ() == z) {
                                double d = MathUtils.distance(instructionpoint,
                                        poi.getPoint());
                                d = d / pixeltometer;
                                if (d < radius && d < mindistance) {
                                    mindistance = d;
                                    poiname = poi.getpoiDescription();
                                }
                            }
                        }
                    }
                    if (!poiname.equals("")) {
                        instruction.setPoiName(poiname);
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void simplify(List<Instruction> instructions,
                                 String facilityid) {
        HashMap<Instruction, List<Integer>> map = new HashMap<Instruction, List<Integer>>();
        Context ctx = PropertyHolder.getInstance().getMlinsContext();
        if (ctx != null) {
            List<Integer> tmp = new ArrayList<Integer>();
            for (Instruction o : instructions) {
                tmp.add(instructions.indexOf(o));
                if (o.getType() == Instruction.TYPE_SWITCH_FLOOR) {
                    List<Integer> l = new ArrayList<Integer>();
                    l.addAll(tmp);
                    map.put(o, l);
                    tmp = new ArrayList<Integer>();
                } else if (o.getType() == Instruction.TYPE_EXIT) {
                    List<Integer> l = new ArrayList<Integer>();
                    l.addAll(tmp);
                    map.put(o, l);
                    tmp = new ArrayList<Integer>();
                } else if (o.getType() == Instruction.TYPE_DESTINATION) {
                    List<Integer> l = new ArrayList<Integer>();
                    l.addAll(tmp);
                    map.put(o, l);
                }
            }
        }

        List<Instruction> simplified = new ArrayList<Instruction>();
        for (Instruction instruction : instructions) {
            int index = instructions.indexOf(instruction);
            for (Instruction simpleinstruction : map.keySet()) {
                List<Integer> list = map.get(simpleinstruction);
                if (list.contains(index)) {

                    int id = instruction.getID();
                    GisSegment segment = instruction.getSegment();
                    Location location = instruction.getLocation();
                    List<Integer> texts = simpleinstruction.getText();
                    List<Integer> images = simpleinstruction.getImage();
                    List<String> sounds = new ArrayList<String>();
                    String floor = simpleinstruction.getTofloor();
                    int type = simpleinstruction.getType();

                    List<Instruction> fullInstructionsList = new ArrayList<>();
                    for (Integer fullInstructionIndex : list) {
                        fullInstructionsList.add(instructions.get(fullInstructionIndex));
                    }

                    Instructionobject ins = new Instructionobject(fullInstructionsList);
                    ins.setID(id);
                    ins.setSegment(segment);
                    ins.setLocation(location);
                    ins.setTexts(texts);
                    ins.setImages(images);
                    ins.setSounds(sounds);
                    ins.setTofloor(floor);
                    ins.setType(type);
                    simplified.add(ins);
                }
            }
        }

        InstructionBuilder.getInstance().setSimplifiedInstructions(simplified);

    }

    public List<List<Location>> getSwitchFloorPoints() {
        return switchFloorPoints;
    }

    public Location getOrigin() {
        return origin;
    }

    public Location getDestination() {
        return destination;
    }

    public double getRouteDistance() {
        return routeDistance;
    }

    public void setRouteDistance(double routeDistance) {
        this.routeDistance = routeDistance;
    }

    public double getTimeEstimation() {
        return timeEstimation;
    }

    public void setTimeEstimation(double timeEstimation) {
        this.timeEstimation = timeEstimation;
    }

    public PoiData getFinalDestination() {
        return finalDestination;
    }


    public GisLine getCurrentClosestNavLine() {
        return currentClosestNavLine;
    }

    public void setCurrentClosestNavLine(GisLine currentClosestNavLine) {
        this.currentClosestNavLine = currentClosestNavLine;
    }

    public int getOnSameNavLineCounter() {
        return onSameNavLineCounter;
    }

    public void setOnSameNavLineCounter(int onSameNavLineCounter) {
        this.onSameNavLineCounter = onSameNavLineCounter;
    }

    public double getLastBearing() {
        return lastBearing;
    }

    public void setLastBearing(double lastBearing) {
        this.lastBearing = lastBearing;
    }

    public List<INavInstruction> getCombinedSimplifiedInstructions() {
        return combinedSimplifiedInstructions;
    }
}
