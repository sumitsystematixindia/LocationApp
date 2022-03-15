package com.spreo.sdk.zones;

import android.content.Context;

import com.mlins.zones.ZoneDetectionManager;
import com.spreo.enums.Zone;
import com.spreo.interfaces.ZoneDetectionListener;

/**
 * This Class manage the zone detection
 *
 * @author Spreo
 */

public class SpreoZoneDetectionManager {
    private static SpreoZoneDetectionManager instance = null;

    //doesn't hold any data, no need to update
    public static SpreoZoneDetectionManager getInstance() {
        if (instance == null) {
            instance = new SpreoZoneDetectionManager();

        }
        return instance;
    }

    /**
     * This method starts the detection service.
     *
     * @param context
     */

    public void startDetection(Context context) {
        ZoneDetectionManager.getInstance().startDetection(context);
    }

    /**
     * This method stops the detection service. Note that it also stops the ble scanner.
     */

    public void stopDetection() {
        ZoneDetectionManager.getInstance().stopDetection();
    }

    /**
     * This method starts the initial detection.
     */

    public void startInitialDetection() {
        ZoneDetectionManager.getInstance().startInitialDetection();
    }

    /**
     * This method returns the theft protection	state.
     */

    public boolean isTheftProtectionVaild() {
        return ZoneDetectionManager.getInstance().isTheftProtectionVaild();
    }

    /**
     * This method returns the current zone.
     */

    public Zone getZone() {
        return ZoneDetectionManager.getInstance().getZone();
    }

    /**
     * use this this method to set the zone.
     *
     * @param zone
     */

    public void setZone(Zone zone) {
        ZoneDetectionManager.getInstance().setZone(zone);
    }

    /**
     * use this this method to register for detection events.
     *
     * @param listener
     */

    public void registerListener(ZoneDetectionListener listener) {
        ZoneDetectionManager.getInstance().registerListener(listener);
    }

    /**
     * // use this this method to unregister from detection events.
     *
     * @param listener
     */

    public void unregisterListener(ZoneDetectionListener listener) {
        ZoneDetectionManager.getInstance().unregisterListener(listener);
    }
}
