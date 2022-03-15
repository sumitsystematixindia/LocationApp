package com.mlins.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.mlins.aStar.NavigationPath;
import com.mlins.aStar.aStarData;
import com.mlins.enums.ScanMode;
import com.mlins.locationutils.LocationFinder;
import com.mlins.nav.utils.MultiNavUtils;
import com.mlins.nav.utils.ParkingUtil;
import com.mlins.nav.utils.SoundPlayer;
import com.mlins.navigation.NavigationUtil;
import com.mlins.navigation.PathCalculator;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.res.setup.ConfigsLoader;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.PoiData;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.Location;
import com.mlins.utils.logging.Log;
import com.spreo.enums.LoadStatus;
import com.spreo.interfaces.ConfigsLoadListener;
import com.spreo.interfaces.MyLocationListener;
import com.spreo.interfaces.SpreoMapViewListener;
import com.spreo.interfaces.SpreoNavigationListener;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.enums.NavigationState;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.INavInstruction;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.spreosdk.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import simulation.SimulationPlayer;

public class LocationMapView extends FrameLayout implements MyLocationListener, ConfigsLoadListener
        /**DownloadingListener*/
{
    private final static String TAG = "ccom.com.mlins.views.LocationMapView";
    private Context ctx = null;
    private LocationMode mapMode = null;
    private ILocation myLocation = null;
    private OutdoorMapView outdoormap;
    private IndoorMapView indoormap;
    private List<SpreoMapViewListener> listeners = new ArrayList<SpreoMapViewListener>();
    private List<SpreoNavigationListener> navigationListeners = new ArrayList<SpreoNavigationListener>();

    public LocationMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.location_map_view, this, true);

    }

    public void init(Bundle savedInstanceState) {
        Log.getInstance().debug(TAG, "Enter, init()");

        if (!LocationFinder.getInstance().isStarted()) {
            LocationFinder.getInstance().startLocationService(ctx, ScanMode.BLE);
        }
        LocationFinder.getInstance().cleanMapViewListeners();
        LocationFinder.getInstance().subscribeForLocation(this);

        loadParkingLocation();

        outdoormap = (OutdoorMapView) findViewById(R.id.outdoorMap);
        indoormap = (IndoorMapView) findViewById(R.id.indoorMap);

        outdoormap.init(savedInstanceState);
        indoormap.init();

        SoundPlayer.getInstance().setContext(ctx);

        if (PropertyHolder.getInstance().isLockedOnFacility()) {
            String facid = PropertyHolder.getInstance().getLockedFacility();
            if (facid != null) {
                String campusId = PropertyHolder.getInstance().getCampusId();
                presentLockedFacility(campusId, facid);
                indoormap.resetMapRotation();
            }
        }

        Log.getInstance().debug(TAG, "Exit, init()");


    }


    private void loadParkingLocation() {
        try {
            ParkingUtil.getInstance().load();
        } catch (Exception e) {

        }
    }

    private void applyMapMode(LocationMode mode) {
        Log.getInstance().debug(TAG, "Enter, applyMapMode()");
        if (mode == LocationFinder.INDOOR_MODE) {
            setFacility(myLocation);
            showIndoor();
        } else if (mode == LocationFinder.OUTDOOR_MODE) {
            mapMode = LocationFinder.OUTDOOR_MODE;
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    showOutdoor();

                }
            };
            post(r);
        }
        Log.getInstance().debug(TAG, "Exit, applyMapMode()");
    }

    public void refreshPois() {
        indoormap.refreshPois();
    }

    private void showOutdoor() {
        Log.getInstance().debug(TAG, "Enter, showOutdoor()");
        for (SpreoMapViewListener o : listeners) {
            try {
                o.mapWillSwapTo(LocationFinder.OUTDOOR_MODE);
            } catch (Exception e) {
                Log.getInstance().error(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }

        indoormap.setVisibility(View.GONE);
        outdoormap.setVisibility(View.VISIBLE);
        mapMode = LocationFinder.OUTDOOR_MODE;

//		onResume();

        PoiData destination = aStarData.getInstance().getExternalDestination();
        if (destination != null) {
            Location origin = (Location) myLocation;
            NavigationUtil.outdoorToOutdoorNavigation(origin, destination);
        }
//		outdoormap.setNavigation();

        for (SpreoMapViewListener o : listeners) {
            try {
                o.mapDidSwapTo(LocationFinder.OUTDOOR_MODE);
            } catch (Exception e) {
                Log.getInstance().error(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        outdoormap.showFacilityMarkers();
        outdoormap.drawPois();
        Log.getInstance().debug(TAG, "Exit, showOutdoor()");
    }


    private void showIndoor(ILocation location) {
        // set the selected facility
        // if the facility is valid
        if (location != null && location.getFacilityId() != null && !location.getFacilityId().equals("unknown")) {
            setFacility(location);
        }
        showIndoor();

    }

    private void showIndoor() {
        Log.getInstance().debug(TAG, "Enter, showIndoor()");
        for (SpreoMapViewListener o : listeners) {
            try {
                o.mapWillSwapTo(LocationFinder.INDOOR_MODE);
            } catch (Exception e) {
                Log.getInstance().error(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }

        outdoormap.setVisibility(View.GONE);
        indoormap.setVisibility(View.VISIBLE);
        mapMode = LocationFinder.INDOOR_MODE;
//		onPause();
        PoiData destination = aStarData.getInstance().getInternalDestination();
        if (destination != null && myLocation != null && myLocation.getFacilityId().equals(destination.getFacilityID())) {
            aStarData.getInstance().setExternalDestination(null);
            aStarData.getInstance().setExternalPoi(null);
            Location origin = (Location) myLocation;
            NavigationUtil.indoorToIndoorNavigation(origin, destination);
        }
        indoormap.setNavigationPath();

        for (SpreoMapViewListener o : listeners) {
            try {
                o.mapDidSwapTo(LocationFinder.INDOOR_MODE);
            } catch (Exception e) {
                Log.getInstance().error(TAG, e.getMessage(), e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Log.getInstance().debug(TAG, "Exit, showIndoor()");
    }

    @Override
    public void onLocationDelivered(ILocation location) {
        Log.getInstance().debug(TAG, "Enter, onLocationDelivered()");

        if (location == null) {
            return;
        }


        if (!PropertyHolder.getInstance().isLockedOnFacility()) {
            if (PropertyHolder.getInstance().isSdkObserverMode()) {
                return;
            }
            locationUpdate(location);
            return;
        }

        String locfacid = location.getFacilityId();
        if (locfacid == null || location.getLocationType() == LocationMode.OUTDOOR_MODE || !PropertyHolder.getInstance().getLockedFacility().equals(locfacid)) {
//			if (PropertyHolder.getInstance().isSdkObserverMode()) {
//				return;
//			}
//			PropertyHolder.getInstance().setSdkObserverMode(true);
            indoormap.hideLocationMark();
            indoormap.resetMapRotation();
            return;
        }

        indoormap.showLocationMark(location);

//		if (PropertyHolder.getInstance().isSdkObserverMode()) {
//			return;
//		}

        locationUpdate(location);


        Log.getInstance().debug(TAG, "Exit, onLocationDelivered()");
    }

    private void locationUpdate(ILocation mLocation) {
        if (mLocation != null) {

            myLocation = mLocation;

            if (mapMode != myLocation.getLocationType()) {
                applyMapMode(myLocation.getLocationType());
            }

            if (mapMode == LocationFinder.INDOOR_MODE) {
                indoormap.applyIndoorLocation(myLocation);
            } else if (mapMode == LocationFinder.OUTDOOR_MODE) {
                outdoormap.applyOutdoorLocation(myLocation);
            }

            if (PropertyHolder.getInstance().isNavigationState()) {
                notifyOnNavigationStateChanged(NavigationState.NAVIGATE);
            } else {
                notifyOnNavigationStateChanged(NavigationState.IDLE);
            }
        }

    }

    public void setOnPoiClickListener(SpreoMapViewListener listener) {
        indoormap.setOnPoiClickListener(listener);
        outdoormap.setOnPoiClickListener(listener);
    }

    public void navigateTo(IPoi poi) {
        Log.getInstance().debug(TAG, "Enter, navigateTo()");
        PoiData destination = (PoiData) poi;
        Location origin = (Location) myLocation;
        if (origin == null) {
            origin = (Location) LocationFinder.getInstance().getCurrentLocation();

        }

        if (PropertyHolder.getInstance().isLockedOnFacility()) {
            String locakedid = PropertyHolder.getInstance().getLockedFacility();
            if (locakedid != null && (origin == null || origin.getFacilityId() == null || !locakedid.equals(origin.getFacilityId()))) {
                return;
            }
        }

        NavigationUtil.navigate(origin, destination);
        notifyOnNavigationStateChanged(NavigationState.STARTED);
//		mapMode = LocationFinder.INDOOR_MODE;
        if (mapMode == LocationFinder.INDOOR_MODE) {
            indoormap.setNavigationPath();
        } else if (mapMode == LocationFinder.OUTDOOR_MODE) {
//			outdoormap.setNavigation();
        }

//		if (PropertyHolder.getInstance().isObserverUser() && (mapMode == null || mapMode == LocationFinder.OUTDOOR_MODE)) {
//			showIndoor();
//		}
        Log.getInstance().debug(TAG, "Exit, navigateTo()");
    }

    public void navigateTo(ILocation destination) {
        Log.getInstance().debug(TAG, "Enter, navigateTo()");
        Location destloc = (Location) destination;
        Location origin = (Location) myLocation;
        if (origin == null) {
            origin = (Location) LocationFinder.getInstance().getCurrentLocation();
        }

        if (PropertyHolder.getInstance().isLockedOnFacility()) {
            String locakedid = PropertyHolder.getInstance().getLockedFacility();
            if (locakedid != null && (origin == null || origin.getFacilityId() == null || !locakedid.equals(origin.getFacilityId()))) {
                return;
            }
        }

        NavigationUtil.navigate(origin, destloc);
        notifyOnNavigationStateChanged(NavigationState.STARTED);
        if (mapMode == LocationFinder.INDOOR_MODE) {
            indoormap.setNavigationPath();
        } else if (mapMode == LocationFinder.OUTDOOR_MODE) {
//			outdoormap.setNavigation();
        }

        if (PropertyHolder.getInstance().isObserverUser() && (mapMode == null || mapMode == LocationFinder.OUTDOOR_MODE)) {
            showIndoor();
        }
        Log.getInstance().debug(TAG, "Exit, navigateTo()");
    }

    public void navigateTo(List<IPoi> poislist) {
        Location origin = (Location) myLocation;
        if (origin == null) {
            origin = (Location) LocationFinder.getInstance().getCurrentLocation();
        }
        List<IPoi> orderedpois = MultiNavUtils.getPoiOrder(origin, poislist);
        if (orderedpois != null && !orderedpois.isEmpty()) {
            aStarData.getInstance().setMultiNavigationPois(orderedpois);
            IPoi dest = orderedpois.get(0);
            if (dest != null) {
                navigateTo(dest);
            }
        }

    }

    public void simulateNavigationTo(ILocation origin, List<IPoi> poislist) {
        if (origin == null) {
            return;
        }
        List<IPoi> orderedpois = MultiNavUtils.getPoiOrder(origin, poislist);
        if (orderedpois != null && !orderedpois.isEmpty()) {
            aStarData.getInstance().setMultiNavigationPois(orderedpois);
            IPoi dest = orderedpois.get(0);
            if (dest != null) {
                simulateNavigationTo(origin, dest);
            }
        }
    }

    public void continueSimulateNavigationTo(IPoi origin, List<IPoi> poislist) {
        if (origin == null) {
            return;
        }
        ILocation originloc = origin.getLocation();
        if (poislist != null && !poislist.isEmpty()) {
            aStarData.getInstance().setMultiNavigationPois(poislist);
            IPoi dest = poislist.get(0);
            if (dest != null) {
                simulateNavigationTo(originloc, dest);
            }
        }
    }

    public void continueNavigationTo(List<IPoi> poislist) {
        if (poislist != null && !poislist.isEmpty()) {
            aStarData.getInstance().setMultiNavigationPois(poislist);
            IPoi dest = poislist.get(0);
            if (dest != null) {
                navigateTo(dest);
            }
        }
    }

    public void simulateNavigationTo(IPoi poi) {
        PoiData destination = (PoiData) poi;
        PoiData epoi = PoiDataHelper.getInstance().getExitPoi();
        if (epoi == null) {
            return;
        }
        PointF entrancepoint = epoi.getPoint();
        int entrancefloor = (int) epoi.getZ();
        String campusid = PropertyHolder.getInstance().getCampusId();
        String facilityid = PropertyHolder.getInstance().getFacilityID();
        Location origin = new Location(facilityid, campusid, entrancepoint.x, entrancepoint.y, entrancefloor);
        origin.setType(LocationFinder.INDOOR_MODE);
        NavigationUtil.navigate(origin, destination);
        notifyOnNavigationStateChanged(NavigationState.STARTED);
        NavigationPath path = aStarData.getInstance().getCurrentPath();
        if (path != null) {
            PropertyHolder.getInstance().setSdkObserverMode(false);
            SimulationPlayer.getInstance().load(path, campusid, facilityid);
            SimulationPlayer.getInstance().play();
        }
        if (mapMode == LocationFinder.INDOOR_MODE) {
            indoormap.setNavigationPath();
        }
//		if (mapMode == LocationFinder.OUTDOOR_MODE) {
//			showIndoor();
//		}
//		indoormap.setNavigationPath();
    }

    public void simulateNavigationTo(ILocation origin, IPoi poi) {
        if (origin == null) {
            return;
        }
//		PointF entrancepoint = new PointF((float)origin.getX(), (float)origin.getY());
//		int entrancefloor = (int) origin.getZ();
        String campusid = PropertyHolder.getInstance().getCampusId();
        String facilityid = PropertyHolder.getInstance().getFacilityID();
        Location lorigin = new Location(origin); //new Location(facilityid, campusid, (float)origin.getX(), (float)origin.getY(), (float)origin.getZ());
        PoiData destination = (PoiData) poi;
//		origin.setType(LocationFinder.INDOOR_MODE);
        notifyOnNavigationStateChanged(NavigationState.STARTED);
        PropertyHolder.getInstance().setLocationPlayer(true);
        NavigationUtil.navigate(lorigin, destination);
        NavigationPath path = aStarData.getInstance().getCurrentPath();
//		if (path != null) {
        PropertyHolder.getInstance().setSdkObserverMode(false);
        SimulationPlayer.getInstance().load(path, campusid, facilityid, lorigin, poi);
        SimulationPlayer.getInstance().play();
//		}

        if (mapMode == LocationFinder.INDOOR_MODE) {
            indoormap.setNavigationPath();
        }
    }

    public void simulateNavigationToParking(ILocation origin) {
        ILocation ploc = ParkingUtil.getInstance().getParkingLocation();
        if (ploc != null) {
            PoiData poi = new PoiData();
            poi.setLocation(ploc);
            simulateNavigationTo(origin, poi);
        }
    }

    public void stopNavigation() {
//		if (PropertyHolder.getInstance().isLocationPlayer()) {
//			SimulationPlayer.getInstance().stopPlaying();
//		}

        indoormap.stopNavigation();
        outdoormap.stopNaviagtion();

    }


    public void onCreate(Bundle savedInstanceState) {
        init(savedInstanceState);
    }

    public void onResume() {
        outdoormap.onResume();
        indoormap.resetAnimations();
    }

    public void onDestroy() {
        outdoormap.onDestroy();
    }

    public void onPause() {
        outdoormap.onPause();
    }

    public void onLowMemory() {
        outdoormap.onLowMemory();
    }

    public void onSaveInstanceState(Bundle outState) {
        outdoormap.onSaveInstanceState(outState);
    }

    public List<SpreoMapViewListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<SpreoMapViewListener> listeners) {
        this.listeners = listeners;
    }

    public void registerListener(SpreoMapViewListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            indoormap.registerListener(listener);
            outdoormap.registerListener(listener);
        }
    }

    public void unregisterListener(SpreoMapViewListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
            indoormap.unregisterListener(listener);
            outdoormap.unregisterListener(listener);
        }
    }

    public void showFloorWithId(int floorId) {
        if (indoormap != null && mapMode == LocationMode.INDOOR_MODE) {
            indoormap.showFloorWithId(floorId, false);
        }
    }

    public void showPoi(IPoi poi) {
        if (poi != null) {
            if (!PropertyHolder.getInstance().isLockedOnFacility()) {
                PropertyHolder.getInstance().setSdkObserverMode(true);
                if (indoormap != null) {
                    String navigationtype = poi.getPoiNavigationType();
                    if (navigationtype.equals("external")) {
                        if (mapMode != LocationMode.OUTDOOR_MODE) {
                            showOutdoor();
                        }
                        outdoormap.showPoi(poi);
                    } else {
                        if (mapMode != LocationMode.INDOOR_MODE) {
                            ILocation loc = new Location(poi);
                            showIndoor(loc);
                        }
                        indoormap.showPoi(poi);
                    }

                }
            } else {
                String facid = PropertyHolder.getInstance().getLockedFacility();
                String navigationtype = poi.getPoiNavigationType();
                if (navigationtype.equals("external") || !poi.getFacilityID().equals(facid)) {
                    indoormap.resetMapRotation();
                    return;
                }
                PropertyHolder.getInstance().setSdkObserverMode(true);
                indoormap.showPoi(poi);
                indoormap.resetMapRotation();
            }

        }
    }

    public void showPoi(IPoi poi, String bubbletext) {
        if (poi != null) {
            if (!PropertyHolder.getInstance().isLockedOnFacility()) {
                PropertyHolder.getInstance().setSdkObserverMode(true);
                if (indoormap != null) {
                    String navigationtype = poi.getPoiNavigationType();
                    if (navigationtype.equals("external")) {
                        if (mapMode != LocationMode.OUTDOOR_MODE) {
                            showOutdoor();
                        }
                        outdoormap.showPoi(poi);
                    } else {
                        if (mapMode != LocationMode.INDOOR_MODE) {
                            showIndoor();
                        }
                        indoormap.showPoi(poi, bubbletext);
                    }

                }
            } else {
                String facid = PropertyHolder.getInstance().getLockedFacility();
                String navigationtype = poi.getPoiNavigationType();
                if (navigationtype.equals("external") || !poi.getFacilityID().equals(facid)) {
                    indoormap.resetMapRotation();
                    return;
                }
                PropertyHolder.getInstance().setSdkObserverMode(true);
                indoormap.showPoi(poi, bubbletext);
                indoormap.resetMapRotation();
            }
        }
    }

    public void presentLocation(ILocation location) {
        if (location != null) {
            if (!PropertyHolder.getInstance().isLockedOnFacility()) {
                PropertyHolder.getInstance().setSdkObserverMode(true);
                if (indoormap != null && location.getLocationType() == LocationMode.INDOOR_MODE) {
                    if (mapMode != LocationMode.INDOOR_MODE) {
                        showIndoor(location);
                    }
                    indoormap.presentLocation(location);
                } else {
                    if (outdoormap != null && location.getLocationType() == LocationMode.OUTDOOR_MODE && !PropertyHolder.getInstance().isLockedOnFacility()) {
                        if (mapMode != LocationMode.OUTDOOR_MODE) {
                            showOutdoor();
                        }
                        LatLng latlng = new LatLng(location.getLat(), location.getLon());
                        outdoormap.presentLocation(latlng);
                    }
                }
            } else {
                String facid = PropertyHolder.getInstance().getLockedFacility();
                LocationMode mode = location.getLocationType();
                if (mode == LocationMode.OUTDOOR_MODE || !location.getFacilityId().equals(facid)) {
                    indoormap.resetMapRotation();
                    return;
                }
                PropertyHolder.getInstance().setSdkObserverMode(true);
                indoormap.presentLocation(location);
                indoormap.resetMapRotation();
            }
        }
    }

    public void presentFacility(String campusId, String facilityId) {
        Log.getInstance().debug(TAG, "Enter, presentFacility()");

        String currentcampus = PropertyHolder.getInstance().getCampusId();
        String currentfacility = PropertyHolder.getInstance().getFacilityID();
        PropertyHolder.getInstance().setSdkObserverMode(true);
        if (!currentfacility.equals(facilityId)) {
//			PropertyHolder.getInstance().setSdkObserverMode(true);
            if (currentcampus.equals(campusId)) {
                //XXX MM ConfigurationUtils.getInstance().downloadFacilityData(ctx,facilityId);
                ConfigsLoader.getInstance().loadFacility(currentcampus, facilityId);
            } else {
                //ConfigurationUtils.getInstance().downloadCampusAndFacilityData(campusId, facilityId);
                ConfigsLoader.getInstance().loadCampus(campusId);
                ConfigsLoader.getInstance().loadFacility(campusId, facilityId);
            }
            //XXX MM ConfigsLoader.getInstance().registerListener(this);
            ConfigsLoader.getInstance().registerListener(this);
        } else if (mapMode == null || mapMode == LocationMode.OUTDOOR_MODE) {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    showIndoor();
                    indoormap.showEntranceFloor();
                }
            };
            post(r);
        }
        Log.getInstance().debug(TAG, "Exit, presentFacility()");
    }


    public void presentLockedFacility(String campusId, String facilityId) {
        Log.getInstance().debug(TAG, "Enter, presentFacility()");

        String currentcampus = PropertyHolder.getInstance().getCampusId();
        String currentfacility = PropertyHolder.getInstance().getFacilityID();
//		PropertyHolder.getInstance().setSdkObserverMode(true);
        if (!currentfacility.equals(facilityId)) {
//			PropertyHolder.getInstance().setSdkObserverMode(true);
            if (currentcampus.equals(campusId)) {
                //XXX MM ConfigurationUtils.getInstance().downloadFacilityData(ctx,facilityId);
                ConfigsLoader.getInstance().loadFacility(currentcampus, facilityId);
            } else {
                //ConfigurationUtils.getInstance().downloadCampusAndFacilityData(campusId, facilityId);
                ConfigsLoader.getInstance().loadCampus(campusId);
                ConfigsLoader.getInstance().loadFacility(campusId, facilityId);
            }
            //XXX MM ConfigsLoader.getInstance().registerListener(this);
            ConfigsLoader.getInstance().registerListener(this);
        } else if (mapMode == null || mapMode == LocationMode.OUTDOOR_MODE) {
//			
            showIndoor();
            indoormap.showEntranceFloor();

        }
        Log.getInstance().debug(TAG, "Exit, presentFacility()");
    }

    public void presentCampus(String CampusId) {
        if (PropertyHolder.getInstance().isLockedOnFacility()) {
            return;
        }
        Campus campus = ProjectConf.getInstance().getCampus(CampusId);
        if (campus != null) {
            final LatLng campusLatlng = new LatLng(campus.getCenterLatitude(), campus.getCenterLongtitude());
            PropertyHolder.getInstance().setSdkObserverMode(true);
            if (mapMode == null || mapMode == LocationMode.INDOOR_MODE) {
                showOutdoor();
            }

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        outdoormap.presentLocation(campusLatlng);
                    }
                };

                @Override
                public void run() {
                    post(r);
                }
            }, 500);
        }
    }

    public void recoverMapStae(final MapState mstate) {
        Log.getInstance().debug(TAG, "Enter, recoverMapStae()");
        if (mstate == null || !PropertyHolder.getInstance().isSdkObserverMode()) {
            return;
        }
        String currentcampus = PropertyHolder.getInstance().getCampusId();
        String currentfacility = PropertyHolder.getInstance().getFacilityID();
        PropertyHolder.getInstance().setSdkObserverMode(true);
        if (!currentfacility.equals(mstate.getFacilityId())) {
//			PropertyHolder.getInstance().setSdkObserverMode(true);
            if (currentcampus.equals(mstate.getCampusId())) {
                //XXX MM ConfigurationUtils.getInstance().downloadFacilityData(ctx,mstate.getFacilityId());
                ConfigsLoader.getInstance().loadFacility(currentcampus, mstate.getFacilityId());

            } else {
                // XXX MM ConfigurationUtils.getInstance().downloadCampusAndFacilityData(mstate.getCampusId(), mstate.getFacilityId());
                ConfigsLoader.getInstance().loadCampus(mstate.getCampusId());
                ConfigsLoader.getInstance().loadFacility(mstate.getCampusId(), mstate.getFacilityId());

            }
            // XXX ConfigurationUtils.getInstance().registerListener(this);
            ConfigsLoader.getInstance().registerListener(this);

        } else if (mapMode == null || mapMode == LocationMode.OUTDOOR_MODE) {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    showIndoor();
//					indoormap.showFloorWithId(floorid);
                    indoormap.recoverMapStae(mstate);
                }
            };
            post(r);
        }
        Log.getInstance().debug(TAG, "Exit, recoverMapStae()");
    }

    /**
     @Override public void appInitFinished() {
     // TODO Auto-generated method stub

     }

     */
    /**
     @Override public void downloadFacilityFinished() {
     Log.getInstance().debug(TAG, "Enter, downloadFacilityFinished()");
     if (PropertyHolder.getInstance().isSdkObserverMode()) {
     Runnable r = new Runnable() {
     @Override public void run() {
     if (mapMode == LocationMode.OUTDOOR_MODE) {
     showIndoor();
     }
     indoormap.showEntranceFloor();
     }
     };
     post(r);
     }
     ConfigurationUtils.getInstance().unregisterListener(this);
     Log.getInstance().debug(TAG, "Exit, downloadFacilityFinished()");
     }
     */

    /**
     @Override public void downloadCampusFinished() {
     // TODO Auto-generated method stub

     }

     */
    /**
     * @Override public void downloadCampusAndFacilityFinished() {
     * Log.getInstance().debug(TAG, "Enter, downloadCampusAndFacilityFinished()");
     * if (PropertyHolder.getInstance().isSdkObserverMode()) {
     * Runnable r = new Runnable() {
     * @Override public void run() {
     * if (mapMode == LocationMode.OUTDOOR_MODE) {
     * showIndoor();
     * }
     * indoormap.showEntranceFloor();
     * }
     * };
     * post(r);
     * }
     * ConfigurationUtils.getInstance().unregisterListener(this);
     * Log.getInstance().debug(TAG, "Exit, downloadCampusAndFacilityFinished()");
     * }
     */

    public void showMyLocation() {
        ILocation cloc = LocationFinder.getInstance().getCurrentLocation();

        if (cloc == null) {
            return;
        }

        if (PropertyHolder.getInstance().isLockedOnFacility()) {
            String facid = cloc.getFacilityId();
            String locfacid = PropertyHolder.getInstance().getLockedFacility();
            if (locfacid != null) {
                if (facid == null || cloc.getLocationType() == LocationMode.OUTDOOR_MODE || !facid.equals(locfacid)) {
                    return;
                }
            }
        }

        PropertyHolder.getInstance().setSdkObserverMode(false);
        setFacility(cloc);
        if (cloc != null && cloc.getZ() != -100) {
            FacilityConf facilityConf = FacilityContainer.getInstance().getSelected();
            facilityConf.setSelected((int) cloc.getZ());
        }

//		FacilityConf currentfac = FacilityContainer.getInstance().getCurrent();
//		FacilityContainer.getInstance().setSelected(currentfac);

        indoormap.showMyLocation();
        if (mapMode != null && mapMode == LocationFinder.OUTDOOR_MODE) {
            outdoormap.showMyLocation();
        }

    }

    private void setFacility(ILocation cloc) {

        if (cloc instanceof Location) {
            Location loc = (Location) cloc;

            FacilityConf facilityConf = FacilityContainer.getInstance().getSelected();

            String locfacid = loc.getFacilityId();

            if (PropertyHolder.getInstance().isLockedOnFacility()) {
                String lockedfacid = PropertyHolder.getInstance().getLockedFacility();
                if (lockedfacid != null && locfacid != null && !lockedfacid.equals(locfacid)) {
                    return;
                }
            }

            if (locfacid != null && !locfacid.equals("unknown") && !facilityConf.getId().equals(locfacid)) {
                //do load
                for (SpreoMapViewListener o : listeners) {
                    try {
                        o.mapWillSwapTo(LocationFinder.INDOOR_MODE);
                    } catch (Exception e) {
                        Log.getInstance().error(TAG, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }

                ConfigsLoader.getInstance().loadFacility(loc.getCampusId(), loc.getFacilityId());
                PropertyHolder.getInstance().setFacilityID(loc.getFacilityId());

                for (SpreoMapViewListener o : listeners) {
                    try {
                        o.mapDidSwapTo(LocationFinder.INDOOR_MODE);
                    } catch (Exception e) {
                        Log.getInstance().error(TAG, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }

//				indoormap.setFloorNumber((int)loc.getZ());
                facilityConf.setSelected((int) loc.getZ());
                indoormap.setFloorMap();
                indoormap.reDrawPois();
                indoormap.resetMapRotation();
                indoormap.resetAnimations();

                PoiData destination = aStarData.getInstance().getInternalDestination();
                if (destination != null && myLocation != null && myLocation.getFacilityId().equals(destination.getFacilityID())) {
                    aStarData.getInstance().setExternalDestination(null);
                    aStarData.getInstance().setExternalPoi(null);
                    Location origin = (Location) myLocation;
                    NavigationUtil.indoorToIndoorNavigation(origin, destination);
                }
            }
        }

    }

    public void mapZoomIn() {
        if (mapMode == LocationFinder.INDOOR_MODE) {
            indoormap.mapZoomIn();
        } else if (mapMode == LocationFinder.OUTDOOR_MODE) {
            outdoormap.mapZoomIn();
        }
    }

    public void mapZoomOut() {
        if (mapMode == LocationFinder.INDOOR_MODE) {
            indoormap.mapZoomOut();
        } else if (mapMode == LocationFinder.OUTDOOR_MODE) {
            outdoormap.mapZoomOut();
        }
    }


//	public void returnToMyLocation() {
//		indoormap.returnToMyLocation();
//	}

    public MapState getMapState() {
        MapState result = null;

        String campusid = PropertyHolder.getInstance().getCampusId();
        String facilityid = PropertyHolder.getInstance().getFacilityID();
        Matrix mapmatrix = null;
        int mapfloor = -100;
        float mapzoom = -100;
        if (mapMode == LocationFinder.INDOOR_MODE && indoormap != null) {
            mapmatrix = indoormap.getMapMatrix();
            FacilityConf facilityConf = FacilityContainer.getInstance().getSelected();
            mapfloor = facilityConf.getSelectedFloor();
            mapzoom = indoormap.getMapZoom();
        }
        result = new MapState(campusid, facilityid, mapMode, mapfloor, mapmatrix, mapzoom);
        return result;

    }

    public int getPresentedFloorId() {
        int floor = -1;
        if (mapMode != null && mapMode == LocationFinder.INDOOR_MODE) {
            if (indoormap != null) {
                floor = indoormap.getPresentedFloorId();
            }
        }
        return floor;
    }

    public String getFloorTitleForFloorId(int floorId) {
        FacilityConf facilityConf = FacilityContainer.getInstance().getSelected();
        return facilityConf.getFloorTitle(floorId);
    }

    public void setMapBackgroundColor(int color) {
        if (indoormap != null) {
            indoormap.setBackgroundColor(color);
        }
    }

    public void setUserIcon(Bitmap userBitmap) {

        if (outdoormap != null) {
            outdoormap.setUserIcon(userBitmap);
        }

        if (indoormap != null) {
            indoormap.setUserIcon(userBitmap);
        }
    }

    public boolean isNavigationState() {
//		boolean navstate = false;
//		if (indoormap != null && indoormap.isNavigationState()) {
//			navstate = true;
//		} else if (outdoormap != null && outdoormap.isNavigationState()) {
//			navstate = true;
//		}
//		return navstate;
        return PropertyHolder.getInstance().isNavigationState();
    }

    @Override
    public void onPreConfigsLoad(LoadStatus loadStatus) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPostConfigsLoad(LoadStatus status) {

        if (status.equals(LoadStatus.LOAD_FACILITY_SUCCES) || status.equals(LoadStatus.LOAD_CAMPUS_SUCCES)) {

            if (PropertyHolder.getInstance().isSdkObserverMode()) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        if (mapMode == LocationMode.OUTDOOR_MODE) {
                            showIndoor();
                        }
                        indoormap.showEntranceFloor();
                    }
                };
                post(r);
            }
            ConfigsLoader.getInstance().unregisterListener(this);

        }


    }

    public void setCurrentLocationAsParking() {
        if (mapMode != null && mapMode == LocationMode.OUTDOOR_MODE) {
            if (outdoormap != null) {
                outdoormap.setCurrentLocationAsParking();
            }
        } else if (mapMode != null && mapMode == LocationMode.INDOOR_MODE) {
            if (indoormap != null) {
                indoormap.setCurrentLocationAsParking();
            }
        }
    }

    public void removeParkingLocation() {
        if (outdoormap != null) {
            outdoormap.removeParkingLocation();
        }
        if (indoormap != null) {
            indoormap.removeParkingLocation();

        }
    }

    public void navigateToParking() {
        ILocation parkingloc = ParkingUtil.getInstance().getParkingLocation();
        if (parkingloc != null) {
            IPoi parkingpoi = new PoiData();
            parkingpoi.setLocation(parkingloc);
            navigateTo(parkingpoi);
        }
    }

    public void reloadMapData() throws Exception {
        throw new Exception("not implemented yet");
    }

    @Override
    public void onCampusRegionEntrance(String campusId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFacilityRegionEntrance(String campusId, String facilityId) {
        // display welcome banner
        //Toast.makeText(ctx, campusId+" "+ facilityId+ " just entered", Toast.LENGTH_SHORT).show();

    }

    public void registerNavigationListener(SpreoNavigationListener listener) {
        if (!navigationListeners.contains(listener)) {
            navigationListeners.add(listener);
            indoormap.registerNavigationListener(listener);
            outdoormap.registerNavigationListener(listener);
        }
    }

    public void unregisterNavigationListener(SpreoNavigationListener listener) {
        if (navigationListeners.contains(listener)) {
            navigationListeners.remove(listener);
            indoormap.unregisterNavigationListener(listener);
            outdoormap.unregisterNavigationListener(listener);
        }
    }

    public void notifyOnNavigationStateChanged(NavigationState navigationState) {
        //android.util.Log.i("notifyOnNavigationStateChanged",navigationState.toString());
//		if(navigationState!=NavigationState.NAVIGATE && navigationState!=NavigationState.IDLE &&
//				navigationState!=NavigationState.SILENT_REROUTE){
//			Toast.makeText(this.getContext(), navigationState.toString(), Toast.LENGTH_LONG).show();
//		}
        for (SpreoNavigationListener listener : navigationListeners) {
            if (listener != null) {
                listener.onNavigationStateChanged(navigationState);
            }
        }
    }

    public void notifyOnNavigationArriveToPoi(IPoi arrivedToPoi, List<IPoi> nextpois) {

        for (SpreoNavigationListener listener : navigationListeners) {
            if (listener != null) {
                listener.onNavigationArriveToPoi(arrivedToPoi, nextpois);
            }
        }
    }

    public void notifyOnNavigationInstructionChanged(INavInstruction instruction) {
        for (SpreoNavigationListener listener : navigationListeners) {
            if (listener != null) {
                listener.OnNavigationInstructionChanged(instruction);
            }
        }
    }


    public void notifyOnNavigationInstructionRangeEntered(INavInstruction instruction) {

        if (instruction == null) {
            return;
        }

        for (SpreoNavigationListener listener : navigationListeners) {
            if (listener != null) {
                listener.onInstructionRangeEntered(instruction);
            }
        }
    }


    public void reDrawPois() {
        if (indoormap != null) {
            indoormap.reDrawPois();
        }
    }


    public void setOverlayForCampus(String campusId, BitmapDescriptor planeImage, LatLng center,
                                    float height, float width,
                                    float bearingAngle, float transparency) {
        if (outdoormap != null) {
            outdoormap.setOverlayForCampus(campusId, planeImage, center, height, width, bearingAngle, transparency);
        }
    }

    public void setOverlayForFacility(String campusId, String facilityId, BitmapDescriptor planeImage, LatLng center,
                                      float height, float width,
                                      float bearingAngle, float transparency) {

        if (outdoormap != null) {
            outdoormap.setOverlayForFacility(campusId, facilityId, planeImage, center, height, width, bearingAngle, transparency);
        }

    }

    public void openPoiBubble(IPoi poi) {
        if (poi.getPoiNavigationType().equals("external")) {
            if (outdoormap != null) {
                outdoormap.showPoiBubble(poi);
            }
        } else if (indoormap != null) {
            indoormap.openPoiBubble(poi);
        }
    }

    public void openPoiBubble(IPoi poi, String customtext) {
        if (poi.getPoiNavigationType().equals("external")) {
            if (outdoormap != null) {
                outdoormap.showPoiBubble(poi);
            }
        } else if (indoormap != null) {
            indoormap.openPoiBubble(poi, customtext);
        }
    }

    public void presentMultiPoiRoute(List<IPoi> poilist, List<IPoi> visitedPoiList) {

        if (poilist != null && !poilist.isEmpty()) {
            PropertyHolder.getInstance().setMultiPoiList(poilist);
            if (visitedPoiList != null && !visitedPoiList.isEmpty()) {
                PropertyHolder.getInstance().setVisitedPoiList(visitedPoiList);
            }
            List<NavigationPath> paths = null;
            ILocation loc = LocationFinder.getInstance().getCurrentLocation();
            paths = PathCalculator.getMultiPoiNavigationPaths(loc, poilist);
            if (paths != null && !paths.isEmpty()) {
                PropertyHolder.getInstance().setMultiPoiNavRoute(paths);
                if (indoormap != null && mapMode == LocationFinder.INDOOR_MODE) {
                    indoormap.drawMultiPoiRoute();
                }
            }
        }

    }

    public void removeMultiPoiRoute() {
        PropertyHolder.getInstance().setMultiPoiList(null);
        PropertyHolder.getInstance().setVisitedPoiList(null);
        PropertyHolder.getInstance().setMultiPoiNavRoute(null);
        if (indoormap != null && mapMode == LocationFinder.INDOOR_MODE) {
            indoormap.removeMultiPoiRoute();
        }
    }


    public void closeBubble(IPoi poi) {
        if (poi.getPoiNavigationType().equals("external")) {
            if (outdoormap != null) {
                outdoormap.closeBubble(poi);
            }
        } else if (indoormap != null) {
            indoormap.closeBubble(poi);
        }
    }


    public void openMyParkingMarkerBubble() {
        if (outdoormap != null) {
            outdoormap.openMyParkingMarkerBubble();
        }
    }

    public void closeMyParkingMarkerBubble() {
        if (outdoormap != null) {
            outdoormap.closeMyParkingMarkerBubble();
        }
    }

    public void setMyParkingBubbleText(String txt) {
        if (outdoormap != null) {
            outdoormap.setMyParkingBubbleText(txt);
        }
    }

    public void setMyParkingMarkerIcon(Bitmap icon) {
        if (outdoormap != null) {
            outdoormap.setMyParkingMarkerIcon(icon);
        }
    }


    public void closeAllPoiBubbles() {
        if (indoormap != null) {
            indoormap.closeAllPoiBubbules();
        }
    }

    public void openUserBubble(View view) {
        if (outdoormap != null) {
            outdoormap.ShowUserBubble(view);
        }
    }

    public void closeUserBubble() {
        if (outdoormap != null) {
            outdoormap.hideUserBubble();
        }
    }

    public void stopSimulation() {
        SimulationPlayer.getInstance().stopPlaying();
        //PropertyHolder.getInstance().setLocationPlayer(false);
    }

    /**
     * display or hide pois on map
     *
     * @param display true for display pois, false for hiding pois
     */
    public void displayPois(boolean display) {
        if (indoormap != null) {
            indoormap.displayPois(display);
        }
    }

    @Override
    public void onFacilityRegionExit(String campusId, String facilityId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFloorChange(String campusId, String facilityId, int floor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationModeChange(LocationMode locationMode) {}


}
