package geofence;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.Location;
import com.mlins.utils.logging.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeoFenceHelper implements Cleanable {

    private HashMap<String, List<GeoFenceObject>> specialZones = new HashMap<String, List<GeoFenceObject>>();
    List<ZoneDetection> listeneres = new ArrayList<ZoneDetection>();
    private List<GeoFenceObject> specialZonesEntered = new ArrayList<GeoFenceObject>();
    private HashMap<ZoneDetection, List<String>> zonesToListenersMap = new HashMap<ZoneDetection, List<String>>();

    private GeoFenceHelper(){
        Load();
    }

    public static GeoFenceHelper getInstance() {
        return Lookup.getInstance().get(GeoFenceHelper.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(GeoFenceHelper.class);
    }

    public void Load() {
        File dir = PropertyHolder.getInstance().getFacilityDir();
        String filename = "geofence.txt";
        File file = new File(dir, filename);

        if (!file.exists()) {
            return;
        } else {
            specialZones.clear();
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = in.readLine()) != null) {
                    GeoFenceObject zone = GeoFenceObject
                            .getObjectFromLine(line);
                    if (zone != null) {
                        List<GeoFenceObject> list = specialZones.get(zone
                                .getName());
                        if (list == null) {
                            list = new ArrayList<GeoFenceObject>();
                        }
                        list.add(zone);
                        specialZones.put(zone.getName(), list);
                    }
                }
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
    }

    public void clean() {
        specialZones.clear();
        specialZonesEntered.clear();
        zonesToListenersMap.clear();
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
        if (!listeneres.contains(detector)) {
            listeneres.add(detector);
            zonesToListenersMap.put(detector, geoListenTo);
        }
        return true;
    }

    //XXX use this method instead of unsubscibeForDetection
    public boolean unsubscibeFromService(ZoneDetection detector) {
        if (listeneres.contains(detector)) {
            listeneres.remove(detector);
            zonesToListenersMap.remove(detector);
        }
        return true;
    }

    public void setLocation(Location loc) {
        float x = (float) loc.getX();
        float y = (float) loc.getY();
        List<GeoFenceObject> geotoremove = new ArrayList<GeoFenceObject>();
        for (GeoFenceObject sp : specialZonesEntered) {
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
            specialZonesEntered.removeAll(geotoremove);
        }

        for (List<GeoFenceObject> data : specialZones.values()) {
            for (GeoFenceObject o : data) {
                if (o.getZ() == loc.getZ() && o.isContains(x, y)) {
                    GeoFenceObject currentzone = o;
                    if (currentzone != null
                            && !specialZonesEntered.contains(currentzone)) {
                        for (ZoneDetection l : listeneres) {
                            if (isListeningTo(l, currentzone) || isListeningToGeoFenceZone(l, currentzone)) { //XXX if (isListeningToGeoFenceZone(l, currentzone)) {
                                l.onZoneEnter(currentzone);
                            }
                        }
                        specialZonesEntered.add(currentzone);
                    }
                }
            }

        }

    }

    public List<GeoFenceObject> getZonesByZ(int z) {
        List<GeoFenceObject> result = new ArrayList<GeoFenceObject>();
        for (List<GeoFenceObject> l : specialZones.values()) {
            for (GeoFenceObject o : l) {
                if (o.getZ() == z) {
                    result.add(o);
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

}
