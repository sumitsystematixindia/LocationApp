package com.spreo.sdk.location;

import android.content.Context;

import com.mlins.enums.ScanMode;
import com.mlins.locationutils.LocationFinder;
import com.mlins.utils.gis.Location;
import com.spreo.interfaces.MyConvertedLocationListener;
import com.spreo.interfaces.MyLocationListener;
import com.spreo.nav.interfaces.ILocation;

import java.util.ArrayList;
import java.util.List;

import simulation.SimulationPlayer;

/**
 * the class implements Campus Location & Outdoor Navigation API
 *
 * @author Spreo
 */
public class SpreoLocationProvider {

    public static SpreoLocationProvider instance = null;

    /**
     * Retrieves the singleton instance
     *
     * @return the singleton instance
     */
    //it doesn't hold any data, no need to rework
    public static SpreoLocationProvider getInstance() {
        if (instance == null) {
            instance = new SpreoLocationProvider();
        }
        return instance;
    }

    /**
     * starts the location service.
     *
     * @param ctx
     * @return true if succeeded. otherwise, returns false.
     */
    public boolean startLocationService(Context ctx) {
        return LocationFinder.getInstance().startLocationService(ctx, ScanMode.BLE);
    }

    /**
     * Stops location service
     *
     * @return true if succeeded. otherwise, returns false.
     */
    public boolean stopLocationService() {
        return LocationFinder.getInstance().stopLocationService();
    }

//	/**
//	 * Subscribe for location service under the campus level
//	 * @param appContext - application context
//	 * @param locationListener - the listener of the location service. e.g. an activity or a view that implements the interface MyLocationListener @see com.mlins.locationutils.MyLocationListener
//	 * @param scanMode - @see com.mlins.enums.ScanMode
//	 * @param campusId - the campus id object which contains the id of the campus
//	 * @return true if succeeded to subscribe
//	 */
//	public boolean subscribeForLocationService(Context appContext, MyLocationListener locationListener, ScanMode scanMode /*, CampusID campusId*/) {
//		return LocationFinder.getInstance().startLocationService(appContext, locationListener, scanMode /*, campusId.getId()*/);
//	}

    /**
     * Subscribe for location service under the facility level
     * @param appContext- application context
     * @param locationListener- the listener of the location service. e.g. an activity or a view that implements the interface MyLocationListener @see com.mlins.locationutils.MyLocationListener
     * @param scanMode - @see com.mlins.enums.ScanMode
     * @param campusId - the campus id object which contains the id of the campus
     * @param facilityId - - the facility id object which contains the id of the facility
     * @return true if succeeded to subscribe

    public boolean subscribeForLocationService(Context appContext, MyLocationListener locationListener, ScanMode scanMode, CampusID campusId, FacilityID facilityId) {
    return LocationFinder.getInstance().startLocationService(appContext, locationListener, scanMode, campusId.getId(), facilityId.getId());
    }
     */

    /**
     * Subscribe for location service under the default facility in your campus
     * @param appContext - application context
     * @param locationListener -  the listener of the location service. e.g. an activity or a view that implements the interface MyLocationListener @see com.mlins.locationutils.MyLocationListener
     * @param scanMode - @see com.mlins.enums.ScanMode
     * @return true if succeeded to subscribe

    public boolean subscribeForLocationService(Context appContext, MyLocationListener locationListener, ScanMode scanMode) {
    return LocationFinder.getInstance().startLocationService(appContext, locationListener, scanMode);
    }
     */

    /**
     * unSubscribe for location service
     * <pre>
     *  example:
     * <code>
     * public class LocationListener implements MyLocationListener {
     * 		SpreoLocationProvider.getInstance().subscribeForLocation(this);
     * 		//rest of your code here...
     * }
     * </code>
     * </pre>
     *
     * @param locationListener -  the listener of the location service. e.g. an activity or a view that implements the interface MyLocationListener
     */
    public void subscribeForLocation(MyLocationListener locationListener) {
        LocationFinder.getInstance().subscribeForLocation(locationListener);
    }

    /**
     * unSubscribe from location service
     *
     * @param locationListener
     */
    public void unSubscribeFromLocationService(MyLocationListener locationListener) {
        LocationFinder.getInstance().unsubscibeForLocation(locationListener);
    }

    /**
     * check if location service is tracking user location
     *
     * @return true if is tracking user location. Otherwise, returns false.
     */
    public boolean isTrackingUserLocation() {
        return LocationFinder.getInstance().isStarted();
    }

    /**
     * Gets user current location
     *
     * @return ILocation object containing the location info of the user
     */
    public ILocation getUserLocation() {
        return LocationFinder.getInstance().getCurrentLocation();
    }

    /**
     * simulate user location. set empty list to stop simulation.
     *
     * @param userLocations
     */
    public void setUserLocation(List<ILocation> userLocations, boolean repeat) {
        List<Location> locs = new ArrayList<Location>();
        if (userLocations != null) {
            for (ILocation o : userLocations) {
                Location loc = new Location(o);
                locs.add(loc);
            }
            SimulationPlayer.getInstance().SetFixedLocations(locs, repeat);
            if (!userLocations.isEmpty()) {
                SimulationPlayer.getInstance().play();
            }
        }
    }

    /**
     * force floor change
     *
     * @param floor
     */
    public void forceFloorChange(int floor) {
        LocationFinder.getInstance().forceFloorChange(floor);
    }

    /**
     * subscribe for location updates converted to LatLng
     *
     * @param convertedLocationListener
     */
    public void subscribeForConvertedLocation(MyConvertedLocationListener convertedLocationListener) {
        LocationFinder.getInstance().subscribeForConvertedLocation(convertedLocationListener);
    }

    /**
     * unsubscribe form location updates converted to LatLng
     *
     * @param convertedLocationListener
     */
    public void unsubscibeFromConvertedLocation(MyConvertedLocationListener convertedLocationListener) {
        LocationFinder.getInstance().unsubscibeFromConvertedLocation(convertedLocationListener);
    }

    /**
     * get simulated location
     *
     * @return
     */
    public ILocation getSimulatedLocation() {
        return LocationFinder.getInstance().getSimulatedLocation();
    }

    /**
     * sets simulated location
     *
     * @param location
     */
    public void setSimulatedLocation(ILocation location) {
        Location loc = null;
        if (location != null) {
            loc = new Location(location);
        }
        LocationFinder.getInstance().setSimulatedLocation(loc);
    }

    public void setSimulatedLocation(ILocation location, boolean saveRawLocation) {
        Location loc = null;
        if (location != null) {
            loc = new Location(location);
        }
        LocationFinder.getInstance().setSimulatedLocation(loc, saveRawLocation);
    }

    public boolean startLocationService(Context ctx, ILocation location) {
        return LocationFinder.getInstance().startLocationService(ctx, location);
    }

    /**
     * check if the user is located inside the campus
     * @param radiusInMeters
     */
    public boolean isInCampus(int radiusInMeters) {
        return LocationFinder.getInstance().isInCampus(radiusInMeters);
    }

}
