package proximity;

import com.mlins.interfaces.ProximityDetection;
import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.WlBlip;

import java.util.ArrayList;
import java.util.List;

public class ProximityHelper implements Cleanable {

    List<ProximityDetection> listeners = new ArrayList<ProximityDetection>();
    private List<ProximityObj> zones = new ArrayList<ProximityObj>();

    private ProximityHelper(){
        load();
    }

    public static ProximityHelper getInstance() {
        return Lookup.getInstance().get(ProximityHelper.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(ProximityHelper.class);
    }

    public void load() {
        zones.clear();
        String bssid = "E6F748296ABC";
        String msg = "Welcome to the kitchen";
        ProximityObj po = new ProximityObj(bssid, msg);
        zones.add(po);
    }

    public void clean() {
        zones.clear();
        listeners.clear();
    }

    public void setBlips(List<WlBlip> blips) {
        if (blips == null || blips.isEmpty()) {
            return;
        }
        int threshold = PropertyHolder.getInstance().getProximityThreshold();
        List<ProximityObj> result = new ArrayList<ProximityObj>();
        for (WlBlip b : blips) {
            if (b.level > threshold && b.level < 0) {
                ProximityObj po = getProximityObj(b.BSSID);
                if (po != null && !result.contains(po)) {
                    result.add(po);
                }
            }
        }

        if (!result.isEmpty()) {
            notifiyListeners(result);
        }
    }

    private void notifiyListeners(List<ProximityObj> pzones) {
        for (ProximityDetection o : listeners) {
            try {
                o.onProximityDetected(pzones);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private ProximityObj getProximityObj(String bssid) {
        for (ProximityObj o : zones) {
            if (o.getBSSID().equals(bssid)) {
                return o;
            }
        }
        return null;

    }

    public boolean subscribeForDetection(ProximityDetection detector) {
        if (!listeners.contains(detector)) {
            listeners.add(detector);

        }
        return true;
    }

    public boolean unsubscibeForDetection(ProximityDetection detector) {
        if (listeners.contains(detector)) {
            listeners.remove(detector);
        }
        return true;
    }

    public List<ProximityObj> getZones() {
        return zones;
    }

    public void setZones(List<ProximityObj> zones) {
        this.zones = zones;
    }
}
