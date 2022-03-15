package com.mlins.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.interfaces.GoogleNavigationListener;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;

import java.util.List;

public class GoogleNavigationUtil {

    private GoogleNavigationListener glistener = null;


    //we have to clear state because we have listeners registered
    public static GoogleNavigationUtil getInstance() {
        return Lookup.getInstance().get(GoogleNavigationUtil.class);
    }

    public void releaseInstance() {
        Lookup.getInstance().remove(GoogleNavigationUtil.class);
    }

    public void registerListener(GoogleNavigationListener listener) {
        glistener = listener;
    }

    public void unregisterListener() {
        glistener = null;
    }

    public void notifyStart(LatLng latlng) {
        if (glistener != null && latlng != null) {
            glistener.onGoogleNavigationOption(latlng);
        }
    }

    public void notifyEnd() {
        if (glistener != null) {
            glistener.onGoogleNavigationEnd();
        }
    }

    public void startGoogleNavigation(Context context, LatLng latlng) {
        PropertyHolder.getInstance().setNotifyGoogleDestination(
                true);
        SetGoogleNavigation(context, latlng.latitude, latlng.longitude);
    }

    private void SetGoogleNavigation(Context context, double latitude,
                                     double longitude) {
        String uri = "google.navigation:ll=%f,%f";
        Intent navintent = new Intent(Intent.ACTION_VIEW, Uri.parse(String
                .format(uri, latitude, longitude)));
        navintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (canHandleIntent(context, navintent)) {
            context.startActivity(navintent);
        } else {
            String label = "";
            String format = "geo:0,0?q=" + Double.toString(latitude) + ","
                    + Double.toString(longitude) + "(" + label + ")";
            Uri curi = Uri.parse(format);
            Intent mapsintent = new Intent(Intent.ACTION_VIEW, curi);
            mapsintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (canHandleIntent(context, mapsintent)) {
                context.startActivity(mapsintent);
            }
        }
    }

    private boolean canHandleIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return activities.size() > 0;
    }
}
