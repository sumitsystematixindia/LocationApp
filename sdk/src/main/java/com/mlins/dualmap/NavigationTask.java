package com.mlins.dualmap;

import android.content.Context;
import android.os.AsyncTask;

import com.mlins.locationutils.LocationFinder;
import com.mlins.utils.PoiData;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.ILocation;

import java.util.List;

public class NavigationTask extends AsyncTask<String, String, String> {

    private Context ctx = null;
    private NavCalculationListener navCalculationListener = null;
    private Location origin = null;
    private PoiData destination = null;
    private Location destloc = null;

    public NavigationTask(Context ctx, NavCalculationListener navCalculationListener, Location origin, PoiData destination) {
        super();
        this.ctx = ctx;
        this.navCalculationListener = navCalculationListener;
        this.origin = origin;
        this.destination = destination;
    }

    public NavigationTask(Context ctx, NavCalculationListener navCalculationListener, Location origin, Location destloc) {
        super();
        this.ctx = ctx;
        this.navCalculationListener = navCalculationListener;
        this.origin = origin;
        this.destloc = destloc;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... req) {

        if (destloc != null) {
            DualMapNavUtil.buildDestination(origin, destloc);
        } else {
            DualMapNavUtil.buildDestination(origin, destination);
        }
        ILocation myloc = LocationFinder.getInstance().getCurrentLocation();
        if (myloc != null) {
            List<DestinationPoi> destpois = DualMapNavUtil.getDestinations();
            if (destpois != null && !destpois.isEmpty()) {
                RouteCalculationHelper.getInstance().calculateCombinedPath(myloc, destpois);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
        if (navCalculationListener != null) {
            try {
//                navCalculationListener.OnNavigationCalculationFinished(false, 0, false);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}