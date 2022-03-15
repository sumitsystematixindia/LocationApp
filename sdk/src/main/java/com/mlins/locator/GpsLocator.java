package com.mlins.locator;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;

import java.util.Observable;
import java.util.Observer;

public class GpsLocator extends Observable implements Cleanable {

    private static final long MIN_TIME = 0;
    private static final float MIN_DISTANCE = 0;

    private Location mLocation;
    private gpsListener mListener = new gpsListener();
    private LocationManager mLocMan;
    private boolean mInitialized;

    private GpsLocator() {
        mInitialized = false;
    }

    public static GpsLocator getInstance() {
        return Lookup.getInstance().get(GpsLocator.class);
    }

    public void clean(){
        stop();
    }

    public void init() {
        if (mInitialized)
            return;

        final Context cntxt = PropertyHolder.getInstance().getMlinsContext();
        mLocMan = (LocationManager) cntxt.getSystemService(Context.LOCATION_SERVICE);
        Location location = mLocMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (isBetterLocation(location, mLocation)) {
            setLocation(location);
        }

        if (mLocMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocMan.addGpsStatusListener(mListener);

            mLocMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mListener);
        } else {
//			request user to enable gps?
        }
        mInitialized = true;
    }

    public boolean isBetterLocation(Location location, Location Location2) {
        if (location == null)
            return false;
        if (Location2 == null)
            return true;
        if (location.getAccuracy() > Location2.getAccuracy() && location.getTime() < Location2.getTime())
            return false;
        // TODO Auto-generated method stub
        return true;
    }

    public void stop() {
        if (mInitialized) {
            mLocMan.removeGpsStatusListener(mListener);
            mLocMan.removeUpdates(mListener);
            mLocMan = null;
            deleteObservers();
            mInitialized = false;
        }
    }

    public String getLat() {
        if (mLocation == null)
            return "No GPS location";
        return Location.convert(mLocation.getLatitude(), Location.FORMAT_SECONDS);
    }

    public String getLon() {
        if (mLocation == null)
            return "No GPS location";
        return Location.convert(mLocation.getLongitude(), Location.FORMAT_SECONDS);
    }

    public Location getLocation() {
        return mLocation == null ? null : new Location(mLocation); // Schwiss
    }

    private void setLocation(Location location) {
        mLocation = new Location(location);
        setChanged();
        notifyObservers(new Location(location));
    }

    @Override
    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }


    class gpsListener implements GpsStatus.Listener, LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            if (isBetterLocation(location, mLocation)) {
                setLocation(location);
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onGpsStatusChanged(int event) {
//			GpsStatus status = null;
//			mLocMan.getGpsStatus(status);
            // TODO Auto-generated method stub
        }

    }
}
