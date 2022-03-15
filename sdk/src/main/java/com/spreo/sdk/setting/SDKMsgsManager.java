package com.spreo.sdk.setting;

import android.content.Context;
import android.location.GpsStatus;
import android.location.LocationManager;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.spreo.sdk.interfaces.SDKMsgsListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SDKMsgsManager is a class that manages the messaged on the system level
 * an event are fired to listeners when event on the system level where changed e.g, GPS provider status
 *
 * @author Spreo
 */
public class SDKMsgsManager implements android.location.GpsStatus.Listener, Cleanable {

    List<SDKMsgsListener> listeneres = Collections
            .synchronizedList(new ArrayList<SDKMsgsListener>());
    LocationManager locationManager = null;
    private Context ctx = null;

    public SDKMsgsManager() {
        super();
    }


    public static SDKMsgsManager getInstance() {
        return Lookup.getInstance().get(SDKMsgsManager.class);
    }

    public void clean(){
        locationManager.removeGpsStatusListener(this);
    }

    /**
     * subscribe for messages manager
     *
     * @param listener
     */
    public void subscribe(SDKMsgsListener listener) {

        if (ctx == null) {
            try {
                ctx = (Context) listener;
                locationManager = (LocationManager) ctx
                        .getSystemService(Context.LOCATION_SERVICE);
                locationManager.addGpsStatusListener(this);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        if (!listeneres.contains(listener)) {
            listeneres.add(listener);
        }

        if (locationManager != null && listener != null) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                notifySingleListenerGPSdisabled(listener);
            }
        }
    }

    private void notifySingleListenerGPSdisabled(SDKMsgsListener listener) {
        if (listener != null) {
            listener.onGPSproviderStatusChanged(false);
        }
    }

    /**
     * unsubscibe from messages manager
     *
     * @param listener
     */
    public void unsubscibe(SDKMsgsListener listener) {
        if (listeneres.contains(listener)) {
            listeneres.remove(listener);
        }
    }

    private void notifyGPSDisabled(boolean isEnabled) {
        for (SDKMsgsListener listener : listeneres) {
            if (listener != null) {
                listener.onGPSproviderStatusChanged(isEnabled);
            }
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {

        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                System.out.println("GPS PRIVDER ENABLED");
                notifyGPSDisabled(true);
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                System.out.println("GPS PROVIDER DISABLED");
                notifyGPSDisabled(false);
                break;
        }
        //System.out.println("OK");

    }

}
