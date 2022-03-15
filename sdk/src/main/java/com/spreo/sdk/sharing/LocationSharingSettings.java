package com.spreo.sdk.sharing;

import com.mlins.nav.location.sharing.LocationSharingManager;

/**
 * This class manage the location sharing
 *
 * @author Spreo
 */

public class LocationSharingSettings {

    /**
     * start the location sharing
     */

    public static void start() {
        LocationSharingManager.getInstance().start();
    }

    /**
     * stop the location sharing
     */

    public static void stop() {
        LocationSharingManager.getInstance().stop();
    }

    /**
     * set the user id for the location sharing
     *
     * @param userId
     */

    public static void setUserId(String userId) {
        LocationSharingManager.getInstance().setUserId(userId);
    }

    /**
     * set the interval for the location sharing (in milliseconds).
     *
     * @param intervalInMilliseconds
     */

    public static void setInterval(long intervalInMilliseconds) {
        LocationSharingManager.getInstance().setInterval(intervalInMilliseconds);
    }
}
