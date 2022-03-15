package com.spreo.sdk.data;

import com.mlins.nav.utils.AnalyticsData;

public class Analytics {

    private static Analytics instance = null;

    //no need to rework, doesn't hold any project-related state or listeners
    public static Analytics getInstance() {
        if (instance == null) {
            instance = new Analytics();
        }
        return instance;
    }

    public static void releaseInstance() {
        if (instance != null) {
            instance = null;
        }
    }

    /**
     * The function send analytic data to server.
     *
     * @param action-   Type of action for example: "Search" or "Navigation"
     * @param data-     User data.
     * @param campus-   Campus ID
     * @param facility- Facility ID
     */

    public void sendReport(String action, String data, String campus, String facility) {
        new AnalyticsData(action, data, campus, facility);
    }

}
