package com.spreo.geofence;

import android.graphics.RectF;

import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceDownloader;
import com.mlins.utils.ServerConnection;
import com.mlins.utils.gis.Location;
import com.mlins.utils.logging.Log;
import com.mlins.wireless.WlBlip;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoFenceHelper {

    private List<String> alltypes = new ArrayList<String>();
    private HashMap<String, List<GeoFenceObject>> proximityGeofenceMap = new HashMap<String, List<GeoFenceObject>>();
    List<ZoneDetection> listeneres = new ArrayList<ZoneDetection>();
    private String uri = "geofence.json";
    private HashMap<ZoneDetection, List<String>> zonesToListenersMap = new HashMap<ZoneDetection, List<String>>();
    private List<GeoFenceObject> proximityEnteredList = new ArrayList<GeoFenceObject>();


    private Map<String, HashMap<String, List<GeoFenceObject>>> facilityLocationGeofenceMap = new HashMap<String, HashMap<String, List<GeoFenceObject>>>();
    private List<GeoFenceObject> locationGeofenceEnteredList = new ArrayList<GeoFenceObject>();
    private static final String MUTE_TYPE = "mute";
    private static final String NO_REROUTE = "no_reroute";

    public static GeoFenceHelper getInstance() {
        return Lookup.getInstance().get(GeoFenceHelper.class);
    }

//	public static void releaseInstance() {
//		if (instance != null) {
//			instance.clean();
//			instance = null;
//		}
//	}

//	private void clean() {
//		proximityGeofenceMap.clear();
//		locationGeofenceEnteredList.clear();
//		zonesToListenersMap.clear();
//		facilityLocationGeofenceMap.clear();
//		proximityEnteredList.clear();
//	}

    /**
     * parse proximity geofence json settings
     */
    private void parseProximityGeofence(String campusId, String facilityId, String content) {

        try {

            JSONArray jgeoObj;
            try {
                JSONTokener jsonTokener = new JSONTokener(content);

                JSONObject json = (JSONObject) jsonTokener.nextValue();

                jgeoObj = json.getJSONArray("beacons_geofence");
            } catch (Exception e1) {
                return;
            }

            for (int i = 0; i < jgeoObj.length(); i++) {

                JSONObject gObj = jgeoObj.getJSONObject(i);

                GeoFenceProximityObj geofence = new GeoFenceProximityObj();

                try {
                    String id = gObj.getString("name");
                    geofence.setId(id);
                } catch (Exception e) {
                    System.out.println("Geofence:parseAsList error key name");
                    e.printStackTrace();
                }

                try {
                    String type = gObj.getString("type");
                    geofence.setType(type);
                    addToAllTypes(type);
                } catch (Exception e) {
                    System.out.println("Geofence:parseAsList error key type");
                    e.printStackTrace();
                }


                try {
                    int enterLevel = gObj.getInt("enter_level");
                    geofence.setEnterLevel(enterLevel);
                } catch (Exception e) {
                    System.out.println("Geofence:parseAsList error key enter_level");
                    e.printStackTrace();
                }


                try {
                    int exitLevel = gObj.getInt("exit_level");
                    geofence.setExitLevel(exitLevel);
                } catch (Exception e) {
                    System.out.println("Geofence:parseAsList error key exit_level");
                    e.printStackTrace();
                }


                JSONArray jbeaconsObj = gObj.getJSONArray("beacons");


                for (int j = 0; j < jbeaconsObj.length(); j++) {
                    JSONObject b = jbeaconsObj.getJSONObject(j);

                    String id = b.getString("id");
                    String uuid = b.getString("uuid");
                    int major = b.getInt("major");
                    int minor = b.getInt("minor");
                    Beacon bec = new Beacon(id, uuid, major, minor);
                    geofence.addBeacon(bec);
                }


                if (geofence != null) {

                    List<GeoFenceObject> list = proximityGeofenceMap.get(geofence.getId());
                    if (list == null) {
                        list = new ArrayList<GeoFenceObject>();
                    }
                    list.add(geofence);
                    proximityGeofenceMap.put(geofence.getId(), list);
                }

            }

        } catch (Throwable t) {
            t.printStackTrace();
        }


    }

    private void addToAllTypes(String type) {
        if (type != null && !type.isEmpty() && !alltypes.contains(type)) {
            alltypes.add(type);
        }
    }

    public boolean subscribeForDetection(ZoneDetection detector) {
        if (!listeneres.contains(detector)) {
            listeneres.add(detector);

        }
        return true;
    }

    public boolean unsubscibeForDetection(ZoneDetection detector) {
        if (listeneres.contains(detector)) {
            listeneres.remove(detector);
        }
        return true;
    }

    //XXX  use this method instead of subscribeForDetection
    public boolean subscribeForService(ZoneDetection detector, List<String> geoListenTo) {
        if (detector != null && !listeneres.contains(detector)) {
            listeneres.add(detector);
            zonesToListenersMap.put(detector, geoListenTo);
            for (GeoFenceObject o : locationGeofenceEnteredList) {
                if (isListeningTo(detector, o) || isListeningToGeoFenceZone(detector, o)) {
                    detector.onZoneEnter(o);
                }
            }

            for (GeoFenceObject o : proximityEnteredList) {
                if (isListeningTo(detector, o) || isListeningToGeoFenceZone(detector, o)) {
                    detector.onZoneEnter(o);
                }
            }
        }
        return true;
    }

//	public static void Load() {
//		File dir = PropertyHolder.getInstance().getFacilityDir();
//		String filename = "geofence.json";
//		File file = new File(dir, filename);
//		StringBuffer sb = new StringBuffer();
//		
//		if (!file.exists()) {
//			return;
//		} else {
//			specialZones.clear();
//			BufferedReader in = null;
//			try {
//				in = new BufferedReader(new FileReader(file));
//				String line = null;
//				while ((line = in.readLine()) != null) {
//					sb.append(line);
//				}
//				
//				String content = sb.toString();
//				parseGeofence(content);
//				parseProximityGeofence( content);
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				if (in != null)
//					try {
//						in.close();
//					} catch (Exception e2) {
//						Log.getInstance().error("", e2.getMessage());
//						e2.printStackTrace();
//					}
//			}
//		}
//	}

    //XXX use this method instead of unsubscibeForDetection
    public boolean unsubscibeFromService(ZoneDetection detector) {
        if (listeneres.contains(detector)) {
            listeneres.remove(detector);
            zonesToListenersMap.remove(detector);
        }
        return true;
    }

    public void resetLocationGeofenceStates() {

        for (GeoFenceObject sp : locationGeofenceEnteredList) {

            if (sp instanceof GeoFenceProximityObj) {
                continue;
            }

            for (ZoneDetection l : listeneres) {
                if (isListeningTo(l, sp) || isListeningToGeoFenceZone(l, sp)) {
                    l.onZoneExit(sp);
                }
            }
        }

        locationGeofenceEnteredList.clear();
    }

    public boolean addZipFacilityData(String campusId, String facilityId) {
        boolean isSucceded = false;
        try {
            String url = ServerConnection.getBaseUrl() + "res/" + PropertyHolder.getInstance().getProjectId()
                    + "/" + campusId + "/" + facilityId + "/" + uri;
            byte[] bytes = ResourceDownloader.getInstance().getUrl(url);
            if (bytes == null || bytes.length == 0) {
                return isSucceded;
            }

            String content = new String(bytes);

            parseGeofence(campusId, facilityId, content);
            parseProximityGeofence(campusId, facilityId, content);
            isSucceded = true;

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return isSucceded;
    }

    public boolean addFacilityData(String campusId, String facilityId) {
        boolean isSucceded = false;

        try {

            if (PropertyHolder.useZip) {
                return addZipFacilityData(campusId, facilityId);
            }


            File root = PropertyHolder.getInstance().getProjectDir();
            File campusdir = new File(root, campusId);
            File facilitycDir = new File(campusdir, facilityId);
            File file = new File(facilitycDir, uri);

            StringBuffer sb = new StringBuffer();

            if (!file.exists()) {
                return false;
            } else {

                BufferedReader in = null;
                try {
                    in = new BufferedReader(new FileReader(file));
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }

                    String content = sb.toString();
                    parseGeofence(campusId, facilityId, content);
                    parseProximityGeofence(campusId, facilityId, content);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (in != null)
                        try {
                            in.close();
                        } catch (Exception e2) {
                            Log.getInstance().error("", e2.getMessage());
                            e2.printStackTrace();
                        }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return isSucceded;
    }

    /**
     * parse geofence rect json settings
     */
    private void parseGeofence(String campusId, String facilityId, String content) {

        try {

            JSONTokener jsonTokener = new JSONTokener(content);

            JSONObject json = (JSONObject) jsonTokener.nextValue();

            JSONArray jgeoObj = json.getJSONArray("geofence");

            for (int i = 0; i < jgeoObj.length(); i++) {

                JSONObject gObj = jgeoObj.getJSONObject(i);

                GeoFenceRect geofence = new GeoFenceRect();

                try {
                    String type = gObj.getString("type");
                    geofence.setName(type);
                    addToAllTypes(type);
                } catch (Exception e) {
                    System.out.println("Geofence:parseAsList error key name");
                    e.printStackTrace();
                }

                try {
                    String id = gObj.getString("name");
                    geofence.setId(id);
                } catch (Exception e) {
                    System.out.println("Geofence:parseAsList error key id");
                    e.printStackTrace();
                }

                try {

                    int top = (int) gObj.getDouble("top");
                    int left = (int) gObj.getDouble("left");

                    int right = (int) gObj.getDouble("right");
                    int bottom = (int) gObj.getDouble("bottom");
                    RectF rect = new RectF(left, top, right, bottom);
                    geofence.setRect(rect);

                } catch (Exception e) {
                    System.out.println("Geofence:parseAsList error keys top/left/bottom/right");
                    e.printStackTrace();
                }


                try {
                    int floor = gObj.getInt("floor");
                    geofence.setZ(floor);
                } catch (Exception e) {
                    System.out.println("Geofence:parseAsList error key floor");
                    e.printStackTrace();
                }


                if (geofence != null) {
                    //System.out.println(geofence);
//					List<GeoFenceObject> list = specialZones.get(geofence.getName());
//					if (list == null) {
//						list = new ArrayList<GeoFenceObject>();
//					}
//					list.add(geofence);
//					specialZones.put(geofence.getName(), list);
                    addZoneGeofence(campusId, facilityId, geofence);
                }

            }

        } catch (Throwable t) {
            t.printStackTrace();
        }


    }

//	public static void oldLoad() {
//		File dir = PropertyHolder.getInstance().getFacilityDir();
//		String filename = "geofence.txt";
//		File file = new File(dir, filename);
//
//		if (!file.exists()) {
//			return;
//		} else {
//			specialZones.clear();
//			BufferedReader in = null;
//			try {
//				in = new BufferedReader(new FileReader(file));
//				String line = null;
//				while ((line = in.readLine()) != null) {
//					GeoFenceObject zone = GeoFenceObject
//							.getObjectFromLine(line);
//					if (zone != null) {
//						System.out.println(zone);
//						List<GeoFenceObject> list = specialZones.get(zone
//								.getName());
//						if (list == null) {
//							list = new ArrayList<GeoFenceObject>();
//						}
//						list.add(zone);
//						specialZones.put(zone.getName(), list);
//					}
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				if (in != null)
//					try {
//						in.close();
//					} catch (Exception e2) {
//						Log.getInstance().error("", e2.getMessage());
//						e2.printStackTrace();
//					}
//			}
//		}
//	}

    public void addZoneGeofence(String campusId, String facilityId, GeoFenceObject geofence) {


        if (campusId != null && facilityId != null && geofence != null) {

            String fkey = campusId + "_" + facilityId;
            HashMap<String, List<GeoFenceObject>> facilityGeosMap = facilityLocationGeofenceMap.get(fkey);

            if (facilityGeosMap == null) {
                facilityGeosMap = new HashMap<String, List<GeoFenceObject>>();
                List<GeoFenceObject> geosList = new ArrayList<GeoFenceObject>();
                facilityGeosMap.put(geofence.getName(), geosList);

                facilityLocationGeofenceMap.put(fkey, facilityGeosMap);

            }

            List<GeoFenceObject> list = facilityGeosMap.get(geofence.getName());
            if (list == null) {
                list = new ArrayList<GeoFenceObject>();
            }
            list.add(geofence);
            facilityGeosMap.put(geofence.getName(), list);

        }

    }

    public void updateGeoProximity(List<WlBlip> results) {

        List<GeoFenceObject> geotoremove = new ArrayList<GeoFenceObject>();
        for (GeoFenceObject sp : proximityEnteredList) {

            if (!(sp instanceof GeoFenceProximityObj)) {
                continue;
            }

            if (((GeoFenceProximityObj) sp).getState(results) == GeoProximityState.EXIT) {
                for (ZoneDetection l : listeneres) {
                    if (isListeningTo(l, sp) || isListeningToGeoFenceZone(l, sp)) {
                        l.onZoneExit(sp);
                    }
                }
                geotoremove.add(sp);
            }
        }

        if (!geotoremove.isEmpty()) {
            proximityEnteredList.removeAll(geotoremove);
        }

        for (List<GeoFenceObject> data : proximityGeofenceMap.values()) {
            for (GeoFenceObject o : data) {

                if (!(o instanceof GeoFenceProximityObj)) {
                    continue;
                }

                if (((GeoFenceProximityObj) o).getState(results) == GeoProximityState.ENTER) {
                    GeoFenceObject currentzone = o;
                    if (currentzone != null
                            && !proximityEnteredList.contains(currentzone)) {
                        for (ZoneDetection l : listeneres) {
                            if (isListeningTo(l, currentzone) || isListeningToGeoFenceZone(l, currentzone)) {
                                l.onZoneEnter(currentzone);
                            }
                        }
                        proximityEnteredList.add(currentzone);
                    }
                }
            }

        }


    }

    public void setLocation(Location loc) {
        float x = (float) loc.getX();
        float y = (float) loc.getY();

        String campusId = loc.getCampusId();
        String facilityId = loc.getFacilityId();
        String fkey = campusId + "_" + facilityId;

        List<GeoFenceObject> geotoremove = new ArrayList<GeoFenceObject>();
        for (GeoFenceObject sp : locationGeofenceEnteredList) {

            if (sp instanceof GeoFenceProximityObj) {
                continue;
            }

            if (!(sp.getZ() == loc.getZ()) || !(sp.isContains(x, y))) {
                for (ZoneDetection l : listeneres) {
                    if (isListeningTo(l, sp) || isListeningToGeoFenceZone(l, sp)) {  //XXX if (isListeningToGeoFenceZone(l, sp)) {
                        l.onZoneExit(sp);
                    }
                }
                geotoremove.add(sp);
            }
        }

        if (!geotoremove.isEmpty()) {
            locationGeofenceEnteredList.removeAll(geotoremove);
        }

        Map<String, List<GeoFenceObject>> locationGeofenceMap = facilityLocationGeofenceMap.get(fkey);
        if (locationGeofenceMap == null) {
            return;
        }

        for (List<GeoFenceObject> data : locationGeofenceMap.values()) {
            for (GeoFenceObject o : data) {

                if (o instanceof GeoFenceProximityObj) {
                    continue;
                }

                if (o.getZ() == loc.getZ() && o.isContains(x, y)) {
                    GeoFenceObject currentzone = o;
                    if (currentzone != null
                            && !locationGeofenceEnteredList.contains(currentzone)) {
                        for (ZoneDetection l : listeneres) {
                            if (isListeningTo(l, currentzone) || isListeningToGeoFenceZone(l, currentzone)) { //XXX if (isListeningToGeoFenceZone(l, currentzone)) {
                                l.onZoneEnter(currentzone);
                            }
                        }
                        locationGeofenceEnteredList.add(currentzone);
                    }
                }
            }

        }

    }

    public List<GeoFenceObject> getZonesByZ(int z) {
        List<GeoFenceObject> result = new ArrayList<GeoFenceObject>();
        for (List<GeoFenceObject> l : proximityGeofenceMap.values()) {
            for (GeoFenceObject o : l) {
                if (o.getZ() == z) {
                    result.add(o);
                }
            }
        }

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        if (facConf != null) {

            Campus campus = ProjectConf.getInstance().getSelectedCampus();
            if (campus != null) {
                String facilityId = PropertyHolder.getInstance().getFacilityID();
                String fkey = campus.getId() + "_" + facilityId;

                Map<String, List<GeoFenceObject>> locationGeofenceMap = facilityLocationGeofenceMap.get(fkey);
                if (locationGeofenceMap != null) {
                    for (List<GeoFenceObject> l : locationGeofenceMap.values()) {
                        for (GeoFenceObject o : l) {
                            if (o.getZ() == z) {
                                result.add(o);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    //XXX to be removed - use isListeningToGeoFenceZone method instead
    private boolean isListeningTo(ZoneDetection l, GeoFenceObject currentzone) {
        boolean result = false;
        if (l.getListeningTo() != null) {
            for (String o : l.getListeningTo()) {
                if (o.equals(currentzone.getName())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private boolean isListeningToGeoFenceZone(ZoneDetection l, GeoFenceObject currentzone) {
        boolean result = false;
        List<String> zones = zonesToListenersMap.get(l);
        if (zones != null) {
            for (String o : zones) {
                if (o.equals(currentzone.getName())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    public RectF getGeofenceRectById(String geofenceId, String type) {

        RectF rect = null;

        if (geofenceId == null || type == null) {
            return rect;
        }

        List<GeoFenceObject> gList = proximityGeofenceMap.get(type);

        if (gList != null && gList.size() == 0) {

            for (GeoFenceObject g : gList) {
                if (g == null) {
                    continue;
                }

                String id = g.getId();
                if (geofenceId.equals(id)) {
                    rect = g.getRect();
                    return rect;
                }
            }
        }

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        if (facConf != null) {


            Campus campus = ProjectConf.getInstance().getSelectedCampus();
            if (campus != null) {
                String facilityId = PropertyHolder.getInstance().getFacilityID();
                String fkey = campus.getId() + "_" + facilityId;
                Map<String, List<GeoFenceObject>> locationGeofenceMap = facilityLocationGeofenceMap.get(fkey);
                if (locationGeofenceMap != null) {
                    for (List<GeoFenceObject> list : locationGeofenceMap.values()) {
                        if (list == null) {
                            continue;
                        }

                        for (GeoFenceObject g : list) {
                            if (g == null) {
                                continue;
                            }

                            String id = g.getId();
                            if (geofenceId.equals(id)) {
                                rect = g.getRect();
                                return rect;
                            }
                        }
                    }
                }
            }
        }
        return rect;

    }

    public List<String> getAlltypes() {
        return alltypes;
    }

    public void setAlltypes(List<String> alltypes) {
        this.alltypes = alltypes;
    }

    public List<GeoFenceObject> getMuteGeofences(String facilityId, int z) {
        List<GeoFenceObject> result = new ArrayList<GeoFenceObject>();
        Campus campus = ProjectConf.getInstance().getSelectedCampus();
        if (campus != null) {
            String fkey = campus.getId() + "_" + facilityId;
            Map<String, List<GeoFenceObject>> locationGeofenceMap = facilityLocationGeofenceMap.get(fkey);
            if (locationGeofenceMap != null) {
                for (List<GeoFenceObject> l : locationGeofenceMap.values()) {
                    for (GeoFenceObject o : l) {
                        if (o.getZ() == z && o.getName().equalsIgnoreCase(MUTE_TYPE)) {
                            result.add(o);
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<GeoFenceObject> getNoRerouteGeofences(String facilityId, int z) {
        List<GeoFenceObject> result = new ArrayList<GeoFenceObject>();
        Campus campus = ProjectConf.getInstance().getSelectedCampus();
        if (campus != null) {
            String fkey = campus.getId() + "_" + facilityId;
            Map<String, List<GeoFenceObject>> locationGeofenceMap = facilityLocationGeofenceMap.get(fkey);
            if (locationGeofenceMap != null) {
                for (List<GeoFenceObject> l : locationGeofenceMap.values()) {
                    for (GeoFenceObject o : l) {
                        if (o.getZ() == z && o.getName().equalsIgnoreCase(NO_REROUTE)) {
                            result.add(o);
                        }
                    }
                }
            }
        }
        return result;
    }

}
