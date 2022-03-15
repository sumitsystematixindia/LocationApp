package com.mlins.nav.utils;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.mlins.utils.PropertyHolder;
import com.mlins.utils.logging.Log;

public class LatLongProvider extends Service implements LocationListener {

    private final static String TAG = "ccom.mlins.nav.utils.LatLongProvider";
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1; // 10 seconds
    // Declaring a Location Manager
    protected LocationManager locationManager;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    private Context mContext;
    private Handler handler;


    public LatLongProvider() {
        this.mContext = PropertyHolder.getInstance().getMlinsContext();
        getLocation();


    }

    public Location getLocation() {
        Log.getInstance().debug(TAG, "Enter, getLocation()");
        try {
            location = null;
            //handler = new Handler(mContext.getMainLooper());
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {


                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // first if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
//                        locationManager.requestLocationUpdates(
//                                LocationManager.GPS_PROVIDER,
//                                MIN_TIME_BW_UPDATES,
//                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        //Toast.makeText(mContext, "GPS Enabled", Toast.LENGTH_LONG).show();
                        if (locationManager != null) {

                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {

                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                return location;
                            }
                        }
                    }
                }

                // if gps is not enabled get location from Network Provider
                if (isNetworkEnabled) {
//                    locationManager.requestLocationUpdates(
//                            LocationManager.NETWORK_PROVIDER,
//                            MIN_TIME_BW_UPDATES,
//                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    //Toast.makeText(mContext, "Network Enabled", Toast.LENGTH_LONG).show();
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            Log.getInstance().debug(TAG, "exit, getLocation()");
                            return location;
                        }
                    }
                }


            }

        } catch (Exception e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        Log.getInstance().debug(TAG, "exit, getLocation()");
        return location;
    }


    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }


    /**
     * Function to check if best network provider
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert() {

        /**
         final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

         // Setting Dialog Title
         alertDialog.setTitle("GPS is settings");

         // Setting Dialog Message
         alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

         // Setting Icon to Dialog
         //alertDialog.setIcon(R.drawable.delete);

         // On pressing Settings button
         alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog,int which) {
         Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
         mContext.startActivity(intent);
         }
         });

         // on pressing cancel button
         alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int which) {
         dialog.cancel();
         }
         });

         // Showing Alert Message

         //alertDialog.show();
         *
         */


        Toast.makeText(mContext, "Turn On GPS Location On Your Divce", Toast.LENGTH_LONG).show();

    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(LatLongProvider.this);
        }
    }


    private class InnerTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.getInstance().debug(TAG, "Enter, doInBackground()");
            getLocation();
            Log.getInstance().debug(TAG, "Exit, doInBackground()");
            return null;
        }

    }


}
