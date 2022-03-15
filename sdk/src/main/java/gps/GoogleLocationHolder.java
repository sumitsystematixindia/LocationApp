package gps;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
//import android.support.annotation.NonNull;

import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mlins.locationutils.LocationFinder;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.spreo.interfaces.MyLocationListener;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;

import java.util.ArrayList;
import java.util.List;

public class GoogleLocationHolder implements Cleanable{

    private static final String TAG = GoogleLocationHolder.class.getSimpleName();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    private static final long INDOOR_UPDATE_INTERVAL_MULTIPLIER = 15;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;



    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;


    private Context ctx = null;
    private LatLng googleLocation = null;
    private List<GMLocationListener> listeners = new ArrayList<GMLocationListener>();

    private GoogleLocationHolder(){
        init();
    }

    //holds references to listeners, so requires cleanup
    public static GoogleLocationHolder getInstance() {
        return Lookup.getInstance().get(GoogleLocationHolder.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(GoogleLocationHolder.class);
    }

    public void clean(){
        stopLocationUpdates();
    }

    private void init() {
        if (ctx == null) {
            ctx = PropertyHolder.getInstance().getMlinsContext();
        }

        setDefaultLocation();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean valid = checkPermissions();
            if (!valid) {
                return;
            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };


		updateLocationRequest(LocationMode.OUTDOOR_MODE);

        LocationFinder.getInstance().subscribeForLocation(new MyLocationListener() {
            @Override
            public void onLocationDelivered(ILocation location) {}

            @Override
            public void onCampusRegionEntrance(String campusId) {}

            @Override
            public void onFacilityRegionEntrance(String campusId, String facilityId) {}

            @Override
            public void onFacilityRegionExit(String campusId, String facilityId) {}

            @Override
            public void onFloorChange(String campusId, String facilityId, int floor) {}

            @Override
            public void onLocationModeChange(LocationMode locationMode) {
                updateLocationRequest(locationMode);
            }
        });



	}

    private void updateLocationRequest(LocationMode mode) {
        stopLocationUpdates();

        boolean outdoor = mode == LocationMode.OUTDOOR_MODE;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(outdoor ? UPDATE_INTERVAL_IN_MILLISECONDS : UPDATE_INTERVAL_IN_MILLISECONDS*INDOOR_UPDATE_INTERVAL_MULTIPLIER);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(outdoor ? LocationRequest.PRIORITY_HIGH_ACCURACY : LocationRequest.PRIORITY_LOW_POWER);

        startLocationUpdates();
    }


    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermissions() {
        boolean hasPermission = (ctx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        return hasPermission;
    }

    private void setDefaultLocation() {
        Campus campus = ProjectConf.getInstance().getSelectedCampus();
        if (campus != null) {
            googleLocation = campus.getDefaultLatlng();
        }

    }


    public void subscribeForLocation(GMLocationListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unsubscribeForLocationn(GMLocationListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public void onNewLocation(Location loc) {
        setGoogleLocation(new LatLng(loc.getLatitude(), loc.getLongitude()));
        for (GMLocationListener o : listeners) {
            try {
                o.GMlocationChange(googleLocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    protected void startLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
//        Utils.setRequestingLocationUpdates(this, true);

        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
//            Utils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    private void stopLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//            Utils.setRequestingLocationUpdates(this, false);

        } catch (SecurityException unlikely) {
//            Utils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }


    public LatLng getGoogleLocation() {
        return googleLocation;
    }

    public void setGoogleLocation(LatLng googleLocation) {
        this.googleLocation = googleLocation;
    }

    public List<GMLocationListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<GMLocationListener> listeners) {
        this.listeners = listeners;
    }

//    private void getLastLocation() {
//        try {
//            mFusedLocationClient.getLastLocation()
//                    .addOnCompleteListener(new OnCompleteListener<Location>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Location> task) {
//                            if (task.isSuccessful() && task.getResult() != null) {
////                                mLocation = task.getResult();
//                            } else {
//                                Log.w(TAG, "Failed to get location.");
//                            }
//                        }
//                    });
//        } catch (SecurityException unlikely) {
//            Log.e(TAG, "Lost location permission." + unlikely);
//        }
//    }


}