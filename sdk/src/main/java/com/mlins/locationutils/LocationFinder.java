package com.mlins.locationutils;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.aStar.GisSegment;
import com.mlins.aStar.aStarData;
import com.mlins.enums.ScanMode;
import com.mlins.interfaces.DeliveredLocationType;
import com.mlins.locator.LocationCorrector;
import com.mlins.locator.LocationLocator;
import com.mlins.ndk.wrappers.NdkConversionUtils;
import com.mlins.ndk.wrappers.NdkFloorSelector;
import com.mlins.ndk.wrappers.NdkLocation;
import com.mlins.ndk.wrappers.NdkLocationFinder;
import com.mlins.polygon.FloorPolygonManager;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.recorder.WlBlipsRecorder;
import com.mlins.res.setup.ConfigsLoader;
import com.mlins.scanners.BlipsScanner;
import com.mlins.utils.Cleanable;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.FacilitySelector;
import com.mlins.utils.Lookup;
import com.mlins.utils.MathUtils;
import com.mlins.utils.MatrixDataHelper;
import com.mlins.utils.Objects;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.GisData;
import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.Location;
import com.mlins.utils.logging.Log;
import com.mlins.views.LocationMapView;
import com.mlins.wireless.IResultReceiver;
import com.mlins.wireless.WlBlip;
import com.spreo.enums.LoadStatus;
import com.spreo.geofence.GeoFenceHelper;
import com.spreo.geofence.GeoFenceObject;
import com.spreo.geofence.GeoFenceRect;
import com.spreo.geofence.LocationBeaconsManager;
import com.spreo.geofence.ZoneDetection;
import com.spreo.interfaces.ConfigsLoadListener;
import com.spreo.interfaces.MyConvertedLocationListener;
import com.spreo.interfaces.MyLocationListener;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.enums.ProjectLocationType;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import gps.GMLocationListener;
import gps.GoogleLocationHolder;

public class LocationFinder implements ZoneDetection, GMLocationListener,
        IResultReceiver, /** DownloadingListener, */
        ConfigsLoadListener , Cleanable {

    // location mode variables
    public final static LocationMode INDOOR_MODE = LocationMode.INDOOR_MODE;
    public final static LocationMode OUTDOOR_MODE = LocationMode.OUTDOOR_MODE;
    private static final String UNKNOWN_FACILITY_TAG = "unknown";
    private static final String UNKNOWN_CAMPUS_TAG = "unknown";


    // XXX NDK LOAD LIB
    static {
        System.loadLibrary("MlinsLocationFinderUtils");
    }

    List<MyLocationListener> listeneres = new ArrayList<MyLocationListener>();
    List<String> listeningToZones = new ArrayList<String>();
    private String TAG = "com.mlins.locationutils.LocationFinder";
    private BlipsScanner blipsScanner = null;
    private LatLng googleLocation = null;
    private int lastSelectedFloor = -100;
    private int fSelectionCounter = 0;
    private boolean insideExitZone = false;
    private boolean insideElevatorZone = false;
    private int elevatorZoneCounter = 0;
    private LocationMode currentLocationMode = OUTDOOR_MODE;
    private int outDoorModeCounter = 0;
    // private int outDoorModeConfirmTimes = 3; // XXX ADD TO PROPERTY_HOLDER
    private int inDoorModeCounter = 0;
    // private int inDoorModeConfirmTimes = 3; // XXX ADD TO PROPERTY_HOLDER
    private int enterZoneCounter = 0;
    private boolean checkExitFacility = false;
    private boolean checkEnterFacility = false;
    private Timer exitTimer = null;
    private Timer enterTimer = null;
    private long ENTER_CHECK_INTERVAL = 3000;// XXX ADD TO PROPERTY_HOLDER
    private long EXIT_CHECK_INTERVAL = 60000;// XXX ADD TO PROPERTY_HOLDER
    private boolean isCheckEnteranceToFacility = true; // XXX ADD TOP
    // ROPERTY_HOLDER
    private String currentDetectedFacilityId = null;
    private String currentDetectedCampusId = null;
    private boolean isFirstRun = true;
    private ILocation currentLocation = null;
    private GeoFenceRect currentElevatorZone = null;

    // private String downloadingCampusId = null;
    // private String downloadingfacilityId = null;
    // private ProgressDialog dialog = null;
    private Context context = null;
    private boolean started = false;
    private boolean justEntered = false;
    private int switchFacilityCounter = 0;
    private int switchFacilityTresh = 1;
    private int ndkFiltercounter = 0;
    private int ndkFilterTresh = 10;
    private boolean JustForcedFloor = false;
    private boolean campusAnnounced = false;
    private List<GisSegment> lastSegmentList = new ArrayList<GisSegment>();
    private List<MyConvertedLocationListener> convertedLocationListeneres = new ArrayList<MyConvertedLocationListener>();
    private PointF rubberFilterPoint = null;
    private List<PointF> rubberHistoryList = new ArrayList<PointF>();
    private Location simulatedLocation = null;
    private ArrayList<PointF> locsHistoryList = new ArrayList<PointF>();
    private int locationAveragePointsCount = 1;
    private Thread locationRepeaterThread = null;
    private boolean locationRepeaterThreadRunning = true;
    private Location repeatedLocation = null;
    private boolean isNewFacility = false;
    private Location rawSimulatedLocation = null;

    public static LocationFinder getInstance() {
        //it holds listeners, so cleaning it too
        return Lookup.getInstance().get(LocationFinder.class);
    }


    public void clean(){
        stopLocationService();
    }

    public int getLocationAveragePointsCount() {
        return locationAveragePointsCount;
    }

    public void setLocationAveragePointsCount(int locationAveragePointsCount) {
        this.locationAveragePointsCount = locationAveragePointsCount;
        locsHistoryList.clear();
    }

    private void init(ScanMode scanMode, Context ctx) {
        Log.getInstance().info("com.mlins.locationutils.LocationFinder",
                "init Enter");
        context = ctx;
        if (scanMode == ScanMode.BLE || scanMode == ScanMode.RADIOUS_IBEACON) {
            BlipsScanner.getInstance().initBluetoothAdapter(ctx);
        }
        blipsScanner = BlipsScanner.getInstance();
        blipsScanner.startScanning(scanMode);
        blipsScanner.subscribeForResults(this);
        GoogleLocationHolder.getInstance().subscribeForLocation(this);

        List<String> listeningto = new ArrayList<String>();
        listeningto.add("elevator");
        listeningto.add("exit");
        //listeningto.add("test");

        this.setListeningTo(listeningto);
        GeoFenceHelper.getInstance().subscribeForDetection(this);
        // ProximityHelper.getInstance().subscribeForDetection(this);
        MatrixDataHelper.getInstance();
        isFirstRun = true;
        Log.getInstance().info("com.mlins.locationutils.LocationFinder",
                "init Exit");
    }

    public void subscribeForLocation(MyLocationListener detector) {
        if (!listeneres.contains(detector)) {
            listeneres.add(detector);
            if(currentLocationMode != null)
                detector.onLocationModeChange(currentLocationMode);
        }
    }

    public void unsubscibeForLocation(MyLocationListener detector) {
        if (listeneres.contains(detector)) {
            listeneres.remove(detector);
        }
    }

    public void updatePlayerLocation(final Location loc) {
        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                if (loc != null) {
                    setPlayerLocationMode(loc);

                    if (currentLocationMode == INDOOR_MODE) {
                        PointF p = new PointF((float) loc.getX(),
                                (float) loc.getY());
                        aStarData.getInstance().setMyLocation(p);

                        if (currentLocation != null
                                && currentLocation.getLocationType() == INDOOR_MODE) {
                            int lastz = (int) currentLocation.getZ();
                            int z = (int) loc.getZ();
                            if (z != lastz) {
                                setPlayerFloorNumber(z);
                            }
                        }
                    }

                    if (currentLocationMode == OUTDOOR_MODE) {
                        String currentcampus = checkCampusRadius(loc);
                        loc.setCampusId(currentcampus);
                    }

                    currentLocation = loc;
                    //convertLatLon(loc);
                    notifyLocationListeneres(loc);
                    notifyConvertedLocationListeneres(loc);
                    if (currentLocationMode == INDOOR_MODE) {
                        GeoFenceHelper.getInstance().setLocation(loc);
                    }
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    public void updateSimulatedLocation(final Location loc) {

        if (loc != null) {
            setPlayerLocationMode(loc);

            if (currentLocationMode == INDOOR_MODE) {
                PointF p = new PointF((float) loc.getX(),
                        (float) loc.getY());
                aStarData.getInstance().setMyLocation(p);

                if (currentLocation != null
                        && currentLocation.getLocationType() == INDOOR_MODE) {
                    int lastz = (int) currentLocation.getZ();
                    int z = (int) loc.getZ();
                    if (z != lastz) {
                        setPlayerFloorNumber(z);
                    }
                }
            }

            if (currentLocationMode == OUTDOOR_MODE) {
                String currentcampus = checkCampusRadius(loc);
                loc.setCampusId(currentcampus);
            }

            currentLocation = loc;

            notifyLocationListeneres(loc);
            notifyConvertedLocationListeneres(loc);
            if (currentLocationMode == INDOOR_MODE) {
                GeoFenceHelper.getInstance().setLocation(loc);
            }
        }


    }

    protected void setPlayerLocationMode(Location loc) {
        if (loc != null) {
            LocationMode locmode = loc.getLocationType();
            if (locmode != null && locmode != currentLocationMode
                    || (locmode == INDOOR_MODE && !Objects.equals(loc.getFacilityId(), currentDetectedFacilityId))) { //we need check for locmode == INDOOR_MODE here because outdoor location can have "unknown" facilityID which allows to pass !Objects.equals(loc.getFacilityId(), currentDetectedFacilityId) check
                if (locmode == INDOOR_MODE) {
                    if (loc.getFacilityId() != null) {
                        currentDetectedFacilityId = loc.getFacilityId();
                        currentLocationMode = INDOOR_MODE;
                        PropertyHolder.getInstance().setFacilityByBlips(
                                currentDetectedFacilityId);
                        setCurrentFacility(currentDetectedFacilityId);
                        notifyFacilityRegionEntranceListeneres(
                                currentDetectedCampusId,
                                currentDetectedFacilityId);
                        setPlayerFloorNumber((int) loc.getZ());
                    }
                } else if (locmode == OUTDOOR_MODE) {
                    currentLocationMode = OUTDOOR_MODE;
                    notifyExitFacility(currentDetectedCampusId, currentDetectedFacilityId);
                    currentDetectedFacilityId = null;
                }
            }
        }
    }

    // private ILocation tempsimulation(ILocation loc) {
    // tempcounter++;
    // loc = new Location();
    // if (tempcounter == 1) {
    // loc.setLat(32.771558);
    // loc.setLon(34.967444);
    // } else if(tempcounter == 2) {
    // loc.setLat(32.772036);
    // loc.setLon(34.96704);
    // } else if(tempcounter == 3) {
    // loc.setLat(32.772679);
    // loc.setLon(34.966427);
    // } else if(tempcounter == 4) {
    // loc.setLat(32.773072);
    // loc.setLon(34.965815);
    // } else if(tempcounter == 5) {
    // loc.setLat(32.773362);
    // loc.setLon(34.965007);
    // tempcounter = 0;
    // }
    // loc.setType(OUTDOOR_MODE);
    // loc.setCampusId(currentDetectedCampusId);
    // return loc;
    //
    // }

    public void updateLocation() {
        // XXX MM boolean downloading =
        // PropertyHolder.getInstance().isDownloadingData();
        boolean observer = PropertyHolder.getInstance().isSdkObserverMode();
        boolean simulationstae = PropertyHolder.getInstance()
                .isLocationPlayer();

        if (simulationstae) {
            return;
        }

        if (simulatedLocation != null) {
            updateSimulatedLocation(simulatedLocation);
            return;
        }

        if (PropertyHolder.getInstance().getProjectLocationType() == ProjectLocationType.NO_LOCATION) {
            Campus campus = ProjectConf.getInstance().getSelectedCampus();
            if (campus != null) {
                LatLng defaultlatlng = campus.getDefaultLatlng();
                if (defaultlatlng != null) {
                    Location defaultloc = new Location(defaultlatlng);
                    updateSimulatedLocation(defaultloc);
                    return;
                }
            }
        }

//		if (observer) {
////			if (PropertyHolder.getInstance().isObserverUser()) {
////				currentLocationMode = OUTDOOR_MODE;
////			}
//			return;
//		}
        // XXX MM
        // if (downloading) {
        // return;
        // }

        List<WlBlip> results = blipsScanner.getBlips();


        try {
            GeoFenceHelper.getInstance().updateGeoProximity(results);
            //System.out.println("after geoproximity");
        } catch (Throwable t) {
            t.printStackTrace();
        }


        try {

            checkLocationMode(results);
            // XXX MM
            // if (PropertyHolder.getInstance().isDownloadingData()) {
            // return;
            // }

            if (PropertyHolder.getInstance().isUseExitPoiRangeForExit() && currentLocation != null && outDoorModeCounter != 0) {
                notifyLocationListeneres(currentLocation);
                notifyConvertedLocationListeneres(currentLocation);
                return;
            }

            if (justEntered) {
                justEntered = false;
                return;
            }

            ILocation loc = null;

            // currentLocationMode = OUTDOOR_MODE;

            if (currentLocationMode == INDOOR_MODE) {
                loc = getIndoorLocation(results);
                // change mode to outdoor - plaster!
                if (loc != null) {
                    if (loc.getFacilityId() != null) {
                        if (loc.getFacilityId().equals("unknown") || loc.getZ() == -100) {
                            currentLocationMode = OUTDOOR_MODE;
                            currentDetectedFacilityId = null;
                            loc = getOutdoorLocation();
                        }

                    }
                }

            } else if (currentLocationMode == OUTDOOR_MODE) {
                loc = getOutdoorLocation();
            }

            // loc = tempsimulation(loc);

            if (loc != null) {

                //convertLatLon(loc);

                currentLocation = loc;
                notifyLocationListeneres(loc);
                notifyConvertedLocationListeneres(loc);
                // System.out.println("LocationFinder: " + loc);
            }
        } catch (Throwable t) {
            Log.getInstance().error("com.mlins.locationutils.LocationFinder",
                    t.getMessage(), t);
            t.printStackTrace();
            Log.getInstance().error(TAG, t.getMessage(), t);
        }

        return;
    }


    private boolean inExitRange(boolean considerBridges) {
        boolean result = false;
        try {
            if (currentLocation != null) {
                String campusid = PropertyHolder.getInstance().getCampusId();
                String facid = currentLocation.getFacilityId();
                if (campusid != null && facid != null) {
                    List<? extends IPoi> exits = getFloorExits(campusid, facid, currentLocation.getZ());
                    if(!considerBridges) {
                        exits = ProjectConf.getInstance().getBridges().removeBridgesSides(exits);
                    }
                    if (!exits.isEmpty()) {
                        PointF p1 = new PointF((float)currentLocation.getX(), (float)currentLocation.getY());
                        for (IPoi exit : exits) {
                            PointF p2 = exit.getPoint();
                            double d = MathUtils.distance(p1, p2);
                            Campus campus = ProjectConf.getInstance().getCampus(campusid);
                            if (campus != null) {
                                FacilityConf fac = campus.getFacilityConf(facid);
                                if (fac != null) {
                                    float pixeltometer = fac.getPixelsToMeter();
                                    double distanceinmeters = d / pixeltometer;
                                    if (distanceinmeters <= PropertyHolder.getInstance().getExitPoiRange()) {
                                        result = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }

    private List<IPoi> getFloorExits(String campusid, String facid, double z) {
        List<IPoi> allPoi = ProjectConf.getInstance().getAllFacilityPoisList(campusid, facid);
        List<IPoi> result = new ArrayList<IPoi>();
        for (IPoi o : allPoi) {
            if (o.getPoiID().contains("idr") && o.getPoiNavigationType().equals("internal") && o.getZ() == z) {
                result.add(o);
            }
        }
        return result;
    }

    private ILocation getOutdoorLocation() {
        Log.getInstance().debug(TAG, "Enter, getOutdoorLocation()");
        ILocation result = null;

        if (googleLocation == null) {
            googleLocation = GoogleLocationHolder.getInstance().getGoogleLocation();
        }

        if (googleLocation != null) {
            result = new Location(googleLocation);

            String currentcampusid = checkCampusRadius(result);
            result.setCampusId(currentcampusid);
        }
        Log.getInstance().debug(TAG, "Exit, getOutdoorLocation()");
        return result;
    }

    private String checkCampusRadius(ILocation loc) {
        String result = "unknown";
        Campus campus = ProjectConf.getInstance().getSelectedCampus();
        if (campus != null && loc != null) {
            int radius = campus.getRadius();
            LatLng campusloc = new LatLng(campus.getCenterLatitude(),
                    campus.getCenterLongtitude());
            LatLng myloc = new LatLng(loc.getLat(), loc.getLon());
            double distance = MathUtils.distance(campusloc, myloc);
            if (distance < radius) {
                result = campus.getId();
                if (!campusAnnounced) {
                    notifyCampusRegionEntranceListeneres(result);
                    campusAnnounced = true;
                }
            }
        }

        return result;
    }

    private PointF checkOnSameOrNeighborsSegment(PointF p) {

        if (PropertyHolder.getInstance().isNeighborsSegmentMethod()) {

            if (p != null && currentLocation != null && currentLocation.getLocationType() == INDOOR_MODE) {
                PointF lp = new PointF((float) currentLocation.getX(), (float) currentLocation.getY());

                GisSegment seg = GisData.getInstance().getSegment(p);
                if (seg != null) {

                    if (lastSegmentList.size() > PropertyHolder.getInstance().getSameSegmentThreshold()) {
                        lastSegmentList.remove(0);
                    }

                    lastSegmentList.add(seg);

                }

                if (GisData.getInstance().isLastSegmentsAreSame(lastSegmentList)) {
                    lastSegmentList.clear();
                } else if (!GisData.getInstance().isOnSameOrNeighborsSegment(lp, p)) {
                    p = lp;
                    lastSegmentList.clear();
                }
            }
        }

        return p;
    }

    private ILocation getIndoorLocation(List<WlBlip> results) {
        Log.getInstance().debug(TAG, "Enter, getIndoorLocation()");

        ILocation result = null;
        PointF p = null;

        boolean justSelectedFirstFloor = false;

        if (isFirstRun && currentLocationMode == INDOOR_MODE) {
            setFloorSelection(results);
            justSelectedFirstFloor = true;
        }

        if (PropertyHolder.getInstance().isUseProximityLocation()) {
            p = LocationBeaconsManager.getInstance().getBeaconLocation(results , currentDetectedFacilityId , lastSelectedFloor);
        }


        if (p == null) {
            boolean usendkfilter = false;
            if (!(ndkFiltercounter > ndkFilterTresh)) {
                ndkFiltercounter++;
            }

            if (ndkFiltercounter <= ndkFilterTresh) {
                usendkfilter = true;
            }

            FacilityConf fac = FacilityContainer.getInstance().getCurrent();

            //XXX use the new method -default is true
            if (PropertyHolder.getInstance().isUseLocationInsideGeofenceMethod()) {

                p = LocationLocator.getInstance().findLocationInsideGeofence(results, 0, usendkfilter);
            } else if (fac != null && !fac.isProjectLocation()) {
                p = LocationLocator.getInstance().findUnprojectedLocation(results, 0, usendkfilter);
            } else {
                DeliveredLocationType type = PropertyHolder.getInstance().getDeliveredLocationType();
                if (type == DeliveredLocationType.RF) {
                    p = LocationLocator.getInstance().findRfLocation(results, 0, usendkfilter);
                } else if (type == DeliveredLocationType.PROJECTED_RF) {
                    p = LocationLocator.getInstance().findProjectedRfLocation(results, 0, usendkfilter);
                    p = checkOnSameOrNeighborsSegment(p);
                } else if (type == DeliveredLocationType.SENSOR_FUSION) {
                    if (isNewFacility) {
                        p = LocationLocator.getInstance().findProjectedRfLocation(results, 0, usendkfilter);
                        p = checkOnSameOrNeighborsSegment(p);
                        LocationCorrector.getInstance().setLocationPositive(p);
                        isNewFacility = false;
                    } else {
                        p = LocationLocator.getInstance().findLocation(results, 0, usendkfilter);
                        p = checkOnSameOrNeighborsSegment(p);
                    }

                }

            }

//		calculateRubberFilter(results, usendkfilter);

            if (p == null) {
                return null;
            }

            if (getLocationAveragePointsCount() > 1) {

                addToLocationAvgHistoryList(p);

                PointF avgPt = getLocationAverage();
                if (avgPt != null) {
                    PointF temp = GisData.getInstance().findClosestPointOnLine(avgPt);
                    if (temp != null) {
                        p = temp;
                    }
                }
            }

            if (!JustForcedFloor && !justSelectedFirstFloor) {
                boolean isinsideelevator = autoselectfloor(results, p);
                if (isinsideelevator
                        && !PropertyHolder.getInstance().isLocationPlayer()) {
                    return null;
                }
                if (isInsideElevatorZone()) {
                    elevatorZoneCounter++;
                    if (elevatorZoneCounter >= 2) {
                        PropertyHolder.getInstance().setInsideElevatorZone(true);
                    }
                } else {
                    elevatorZoneCounter = 0;
                }
            }
        }




        result = new Location();
        result.setX(p.x);
        result.setY(p.y);
        result.setZ(lastSelectedFloor);
        result.setType(INDOOR_MODE);
        result.setCampusId(PropertyHolder.getInstance().getCampusId());
        result.setFacilityId(currentDetectedFacilityId);
        try {
            GeoFenceHelper.getInstance().setLocation((Location) result);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        // ProximityHelper.getInstance().setBlips(results);

        checkBlipsrecording(p, results);

        if (!LocationCorrector.getInstance().isCollectorInitialized()) {
            LocationCorrector.getInstance().setLocationPositive(p);
        }

        aStarData.getInstance().setMyLocation(p);
        Log.getInstance().debug(TAG, "Exit, getIndoorLocation()");
        return result;
    }

    private void calculateRubberFilter(List<WlBlip> results, boolean usendkfilter) {
        if (PropertyHolder.getInstance().isDevelopmentMode()) {
            int historysize = 4;
            float newweight = 0.5f;
            PointF rf = LocationLocator.getInstance().findRfLocation(results, 0, usendkfilter);
            if (rf != null) {
                if (rubberHistoryList.size() == historysize) {
                    List<PointF> oldlist = new ArrayList<PointF>();
                    List<PointF> newlist = new ArrayList<PointF>();
                    oldlist.addAll(rubberHistoryList);
                    oldlist.remove(historysize - 1);
                    newlist.addAll(rubberHistoryList);
                    newlist.remove(0);
                    PointF oldavg = getAvg(oldlist);
                    PointF newavg = getAvg(newlist);
                    double radius = MathUtils.distance(oldavg, newavg);
                    double newdistance = MathUtils.distance(rf, newavg);
                    if (newdistance < radius) {
                        rubberFilterPoint = rf;
                    } else {
                        float avx = newweight * rf.x + (1 - newweight) * newavg.x;
                        float avy = newweight * rf.y + (1 - newweight) * newavg.y;
                        rubberFilterPoint = new PointF(avx, avy);
                    }
                }

                rubberHistoryList.add(rf);
                if (rubberHistoryList.size() > historysize) {
                    rubberHistoryList.remove(0);
                }
            }
        }
    }

    private PointF getAvg(List<PointF> list) {
        PointF result = null;
        int size = list.size();
        if (size > 0) {
            float sumx = 0;
            float sumy = 0;
            for (PointF o : list) {
                sumx += o.x;
                sumy += o.y;
            }
            float xavg = sumx / size;
            float yavg = sumy / size;
            result = new PointF(xavg, yavg);
        }
        return result;
    }

    private void notifyLocationListeneres(ILocation location) {
        Log.getInstance().debug(TAG, "Enter, notifyLocationListeneres()");

        ILocation result = new Location(location);

        if (result.getLocationType() == LocationMode.OUTDOOR_MODE) {
            LatLng locLatLng = new LatLng(result.getLat(), result.getLon());

            ILocation locInPolygon = FloorPolygonManager.getInstance().getLocationInPolygon(locLatLng);
            if (locInPolygon != null && locInPolygon instanceof Location) {
                result = new Location(locInPolygon);

            }
        }


        for (MyLocationListener listener : new ArrayList<>(listeneres)) {
            try {
                if (listener != null) {
                    listener.onLocationDelivered(result);
                }
            } catch (Exception e) {
                Log.getInstance().error(
                        "com.mlins.locationutils.LocationFinder",
                        e.getMessage(), e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Log.getInstance().debug(TAG, "Exit, notifyLocationListeneres()");
    }

    private void notifyFacilityRegionEntranceListeneres(
            String currentDetectedCampusId, String currentDetectedFacilityId) {
        Log.getInstance().debug(TAG, "Enter, notifyFacilityRegionEntranceListeneres()");

        for (MyLocationListener listener : new ArrayList<>(listeneres)) {
            try {
                if (listener != null) {
                    listener.onFacilityRegionEntrance(currentDetectedCampusId,
                            currentDetectedFacilityId);
                    listener.onLocationModeChange(LocationMode.INDOOR_MODE);
                }
            } catch (Exception e) {
                Log.getInstance().error(
                        "com.mlins.locationutils.LocationFinder",
                        e.getMessage(), e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Log.getInstance().debug(TAG, "Exit, notifyFacilityRegionEntranceListeneres()");

    }

    private void notifyCampusRegionEntranceListeneres(
            String currentDetectedCampusId) {
        Log.getInstance().debug(TAG, "Enter, notifyCampusRegionEntranceListeneres()");

        for (MyLocationListener listener : new ArrayList<>(listeneres)) {
            try {
                if (listener != null) {
                    listener.onCampusRegionEntrance(currentDetectedCampusId);
                }
            } catch (Exception e) {
                Log.getInstance().error(
                        "com.mlins.locationutils.LocationFinder",
                        e.getMessage(), e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Log.getInstance().debug(TAG, "Exit, notifyCampusRegionEntranceListeneres()");

    }

    @Override
    public void GMlocationChange(LatLng GMLocation) {
        // googleLocation = GMLocation;
        // LatLng gmpoint = CampusGisData.getInstance().findClosestPointOnLine(
        // GMLocation);
        googleLocation = GMLocation;
    }

    private boolean autoselectfloor(List<WlBlip> results, PointF p) {
        Log.getInstance().debug(TAG, "Enter, autoselectfloor()");
//		if (PropertyHolder.getInstance().isAutoSelectFloor()) {

//			if (isInsideElevatorZone() || lastSelectedFloor == -989) {

        // int zbyblips =
        // FloorSelector.getInstance().getFloorByBlips(results);
        // XXX NDK

        List<WlBlip> fresults = filterFloorBeacons(results);
        WlBlip[] res = fresults.toArray(new WlBlip[fresults.size()]);
        int zbyblips = NdkFloorSelector.getInstance().getFloor(res,
                false);

        if (zbyblips == -989) {
            lastSelectedFloor = zbyblips;
            return true;
        }

        FacilityConf facConf = FacilityContainer.getInstance()
                .getCurrent();

        if (facConf != null) {

            int currentz = facConf.getSelectedFloor();

            if (zbyblips != -100 && zbyblips != currentz) {

                fSelectionCounter++;

                int blipsnumber = facConf.getFloorSelectionBlips();
                if (fSelectionCounter >= blipsnumber) {

                    lastSelectedFloor = zbyblips;
                    setFloorNumber(zbyblips);
                    fSelectionCounter = 0;
                    ndkFiltercounter = 0;

                    // reset history points for location average
                    locsHistoryList.clear();

                    String msg = "groups selection " + "f - " + zbyblips;
                    presentToast(msg);
                }
            } else {
                fSelectionCounter = 0;
            }
        }
//			}
//		}

        // if (lastSelectedFloor == -989) {
        // return true;
        // }
        Log.getInstance().debug(TAG, "Exit, autoselectfloor()");
        return false;

    }

    private List<WlBlip> filterFloorBeacons(List<WlBlip> results) {
        int threshold = -80;
        FacilityConf fac = FacilityContainer.getInstance().getCurrent();
        if (fac != null) {
            threshold = fac.getFloorFilterThreshold() + PropertyHolder.getInstance().getFloorFilterSupplement();
        }
        List<WlBlip> list = new ArrayList<WlBlip>();
        for (WlBlip o : results) {
            if (o != null && o.level >= threshold) {
                list.add(o);
            }
        }
        return list;
    }

    public void forceFloorChange(int z) {

        FacilityConf facConf = FacilityContainer.getInstance()
                .getCurrent();

        if (facConf != null && z != facConf.getSelectedFloor()) {
            if (z >= 0 && z <= facConf.getFloorDataList().size() - 1) {
                JustForcedFloor = true;
                lastSelectedFloor = z;
                setFloorNumber(z);
                fSelectionCounter = 0;
                ndkFiltercounter = 0;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        JustForcedFloor = false;
                    }
                }, 15000);
            }
        }

    }

    private void checkBlipsrecording(PointF p, List<WlBlip> results) {
        Log.getInstance().debug(TAG, "Enter, checkBlipsrecording()");
        boolean isRecWlBlips = PropertyHolder.getInstance().isRecWlBlips();
        long lastrecordtime = WlBlipsRecorder.getInstance().getLastRecordTime();
        long currenttime = System.currentTimeMillis();
        long recordinginterval = WlBlipsRecorder.getInstance()
                .getRecordingInterval();
        if (isRecWlBlips) {

            FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
            if (facConf != null) {
                String projectid = PropertyHolder.getInstance().getProjectId();
                String campusid = PropertyHolder.getInstance().getCampusId();
                String facilityid = PropertyHolder.getInstance()
                        .getFacilityID();

                int z = facConf.getSelectedFloor();
                WlBlipsRecorder.getInstance().record(projectid, campusid,
                        facilityid, p, z, results);
            }
        }
        Log.getInstance().debug(TAG, "Exit, checkBlipsrecording()");
    }

    private void setFloorNumber(int nfloor) {

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

        if (facConf != null) {
            if (nfloor != facConf.getSelectedFloor()) {
                // XXX NDK
                // AsociativeMemoryLocator.getInstance().setLastAverage(null);
                NdkLocationFinder.getInstance().resetLastpt();
                facConf.setSelected(nfloor);
                facConf.updateFloorData();

                notifyFloorChange(currentDetectedCampusId, currentDetectedFacilityId, nfloor);
            }
        }
    }

    private void setPlayerFloorNumber(int nfloor) {

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

        if (facConf != null) {
            // if (nfloor !=facConf.getSelectedFloor()) {
            facConf.setSelected(nfloor);
            GisData.getInstance().loadGisLines();

            notifyFloorChange(currentDetectedCampusId, currentDetectedFacilityId, nfloor);
            // }
        }
    }

    @Override
    public void onZoneEnter(GeoFenceObject zone) {

        if (zone.getName().equals("exit")) {
            insideExitZone = true;
        }

        if (zone.getName().equals("elevator")) {
            insideElevatorZone = true;
            currentElevatorZone = (GeoFenceRect) zone;
        }

//		if(zone.getName().equals("test")){
//			Toast.makeText(context, "enter " + zone.getId(), Toast.LENGTH_SHORT).show();
//		}

    }

    @Override
    public void onZoneExit(GeoFenceObject zone) {

        if (zone.getName().equals("exit")) {
            insideExitZone = false;
        }

        if (zone.getName().equals("elevator")) {
            insideElevatorZone = false;
            PropertyHolder.getInstance().setInsideElevatorZone(false);
            currentElevatorZone = null;
        }

//		if(zone.getName().equals("test")){
//			Toast.makeText(context, "exit " + zone.getId(), Toast.LENGTH_SHORT).show();
//		}

    }

    @Override
    public List<String> getListeningTo() {
        return listeningToZones;
    }

    // called every time you get blips from the scanner

    @Override
    public void setListeningTo(List<String> to) {
        listeningToZones = to;

    }

    private void checkLocationMode(List<WlBlip> results) {


        if (currentLocationMode == OUTDOOR_MODE) {
            String facilityid = findFacilityByBlips(results);
            if (facilityid != null && !facilityid.equals("unknown")) {

                outDoorModeCounter = 0;

                if (currentLocationMode == OUTDOOR_MODE) {

                    // enter facility

                    // clear location history

                    switchFacilityCounter = 0;


                    isNewFacility = true;


                    locsHistoryList.clear();

                    Campus campus = ProjectConf.getInstance().getSelectedCampus();
                    if (campus != null) {
                        currentDetectedCampusId = campus.getId();
                    }

                    currentDetectedFacilityId = facilityid;

                    currentLocationMode = INDOOR_MODE;
                    releaseOutdoorMode();
                    PropertyHolder.getInstance().setFacilityByBlips(
                            currentDetectedFacilityId);
                    setCurrentFacility(currentDetectedFacilityId);
                    notifyFacilityRegionEntranceListeneres(currentDetectedCampusId,
                            currentDetectedFacilityId);

                    isFirstRun = true;
                    justEntered = true;

                    String msg = "enter facility - " + facilityid;
                    presentToast(msg);

                    android.util.Log.e("enter facility", facilityid);
                }
            }
        } else if (currentLocationMode == INDOOR_MODE){
            boolean exitapplied = false;
            FacilityConf fac = FacilityContainer.getInstance().getCurrent();
            if (fac != null) {

                int outDoorModeConfirmTimes = fac.getExitNoDetectionCount();

                int exitminimumdevices = fac.getExitMinBleDetectionDevices();

                int exitlevel = fac.getExitMinBleDetectionLevel();

                int blipssize = 0;
                for (WlBlip o : results) {

                    if (o.level > exitlevel) {

                        blipssize++;

                    }

                }

                if (blipssize < exitminimumdevices) {

                    outDoorModeCounter++;

                    if (PropertyHolder.getInstance().isUseExitPoiRangeForExit()) {
                        if (outDoorModeCounter >= outDoorModeConfirmTimes && inExitRange(false)) {
                            applyExit();
                            android.util.Log.e("exit facility", "exit facility");
                        } else if (outDoorModeCounter >= PropertyHolder.getInstance().getConsecutiveCountForExit() ) {
                            applyExit();
                            android.util.Log.e("exit facility", "exit facility");
                        }
                    } else {
                        if (outDoorModeCounter >= outDoorModeConfirmTimes) {
                            // exit facility
                            applyExit();
                            switchFacilityCounter = 0;
                            exitapplied = true;
                            android.util.Log.e("exit facility", "exit facility");
                        }
                    }
                } else {
                    outDoorModeCounter = 0;
                }
            } else {

                outDoorModeCounter = 0;
            }

            if (!exitapplied) {
                String facilityid = null;
                if (PropertyHolder.getInstance().isUseBridgeEntranceParameters()) {
                    facilityid = findBridgeFacilityByBlips(results);
                } else {
                    facilityid = findFacilityByBlips(results);
                }

                if (facilityid != null && !facilityid.equals("unknown")) {

//                    outDoorModeCounter = 0;

                    if (currentDetectedFacilityId != null && !facilityid.equals(currentDetectedFacilityId)) {
                        switchFacilityCounter++;
                        android.util.Log.e("facility counter", String.valueOf(switchFacilityCounter));
                    } else {
                        switchFacilityCounter = 0;
                    }

                    if (switchFacilityCounter >= switchFacilityTresh) {

                        // enter facility

                        // clear location history

                        switchFacilityCounter = 0;

                        outDoorModeCounter = 0;

                        isNewFacility = true;


                        locsHistoryList.clear();

                        Campus campus = ProjectConf.getInstance().getSelectedCampus();
                        if (campus != null) {
                            currentDetectedCampusId = campus.getId();
                        }

                        currentDetectedFacilityId = facilityid;

                        currentLocationMode = INDOOR_MODE;
                        releaseOutdoorMode();
                        PropertyHolder.getInstance().setFacilityByBlips(
                                currentDetectedFacilityId);
                        setCurrentFacility(currentDetectedFacilityId);
                        notifyFacilityRegionEntranceListeneres(currentDetectedCampusId,
                                currentDetectedFacilityId);

                        isFirstRun = true;
                        justEntered = true;

                        String msg = "enter facility - " + facilityid;
                        presentToast(msg);

                        android.util.Log.e("enter facility", facilityid);
                    }
                } else {
                    switchFacilityCounter = 0;
                }
            }
        }



//        String facilityid = findFacilityByBlips(results);
//        if (facilityid != null && !facilityid.equals("unknown")) {
//
//            outDoorModeCounter = 0;
//
//            if (currentDetectedFacilityId != null && !facilityid.equals(currentDetectedFacilityId)) {
//                switchFacilityCounter++;
//                android.util.Log.e("facility counter", String.valueOf(switchFacilityCounter));
//            } else {
//                switchFacilityCounter = 0;
//            }
//
//            if (currentLocationMode == OUTDOOR_MODE || switchFacilityCounter >= switchFacilityTresh) {
//
//                // enter facility
//
//                // clear location history
//
//                switchFacilityCounter = 0;
//
//
//                isNewFacility = true;
//
//
//                locsHistoryList.clear();
//
//                Campus campus = ProjectConf.getInstance().getSelectedCampus();
//                if (campus != null) {
//                    currentDetectedCampusId = campus.getId();
//                }
//
//                currentDetectedFacilityId = facilityid;
//
//                currentLocationMode = INDOOR_MODE;
//                releaseOutdoorMode();
//                PropertyHolder.getInstance().setFacilityByBlips(
//                        currentDetectedFacilityId);
//                setCurrentFacility(currentDetectedFacilityId);
//                notifyFacilityRegionEntranceListeneres(currentDetectedCampusId,
//                        currentDetectedFacilityId);
//
//                isFirstRun = true;
//                justEntered = true;
//
//                String msg = "enter facility - " + facilityid;
//                presentToast(msg);
//
//                android.util.Log.e("enter facility", facilityid);
//            }
//        }  else {
//            switchFacilityCounter = 0;
//            if (currentLocationMode == INDOOR_MODE) {
//
//                FacilityConf fac = FacilityContainer.getInstance().getCurrent();
//
//                if (fac != null) {
//
//                    int outDoorModeConfirmTimes = fac.getExitNoDetectionCount();
//
//                    int exitminimumdevices = fac.getExitMinBleDetectionDevices();
//
//                    int exitlevel = fac.getExitMinBleDetectionLevel();
//
//                    int blipssize = 0;
//                    for (WlBlip o : results) {
//
//                        if (o.level > exitlevel) {
//
//                            blipssize++;
//
//                        }
//
//                    }
//
//                    if (blipssize < exitminimumdevices) {
//
//                        outDoorModeCounter++;
//
//                        if (PropertyHolder.getInstance().isUseExitPoiRangeForExit()) {
//                            if (outDoorModeCounter >= outDoorModeConfirmTimes && inExitRange(false)) {
//                                applyExit();
//                                android.util.Log.e("exit facility", "exit facility");
//                            } else if (outDoorModeCounter >= PropertyHolder.getInstance().getConsecutiveCountForExit() ) {
//                                applyExit();
//                                android.util.Log.e("exit facility", "exit facility");
//                            }
//                        } else {
//                            if (outDoorModeCounter >= outDoorModeConfirmTimes) {
//                                // exit facility
//                                applyExit();
//                                android.util.Log.e("exit facility", "exit facility");
//                            }
//                        }
//                    } else {
//                        outDoorModeCounter = 0;
//                    }
//                } else {
//
//                    outDoorModeCounter = 0;
//
//                }
//            }
//        }



//        switchFacilityCounter++;
//        android.util.Log.e("facility counter", String.valueOf(switchFacilityCounter));
//
//        if (currentLocationMode == OUTDOOR_MODE
//                || switchFacilityCounter >= switchFacilityTresh) {
//
//            String facilityid = findFacilityByBlips(results);
//
//            if (facilityid != null
//                    && !facilityid.equals("unknown")
//                    && ((currentDetectedFacilityId == null) || (!facilityid
//                    .equals(currentDetectedFacilityId)))) {
//
//                if (currentDetectedFacilityId != null && !facilityid.equals(currentDetectedFacilityId)) {
//                    isNewFacility = true;
//                }
//
//                // enter facility
//
//                // clear location history
//                locsHistoryList.clear();
//
//                Campus campus = ProjectConf.getInstance().getSelectedCampus();
//                if (campus != null) {
//                    currentDetectedCampusId = campus.getId();
//                }
//
//                currentDetectedFacilityId = facilityid;
//
//                currentLocationMode = INDOOR_MODE;
//                releaseOutdoorMode();
//                PropertyHolder.getInstance().setFacilityByBlips(
//                        currentDetectedFacilityId);
//                setCurrentFacility(currentDetectedFacilityId);
//                notifyFacilityRegionEntranceListeneres(currentDetectedCampusId,
//                        currentDetectedFacilityId);
//
//                isFirstRun = true;
//                justEntered = true;
//
//                String msg = "enter facility - " + facilityid;
//                presentToast(msg);
//
//                android.util.Log.e("enter facility", facilityid);
//
//                // ConfigsLoader.getInstance().loadFacility(PropertyHolder.getInstance().getCampusId(),
//                // currentDetectedFacilityId);
//
//            }
//
//            if (switchFacilityCounter >= switchFacilityTresh) {
//                switchFacilityCounter = 0;
//            }
//
//        } else if (currentLocationMode == INDOOR_MODE) {
//
//            FacilityConf fac = FacilityContainer.getInstance().getCurrent();
//
//            if (fac != null) {
//
//                int outDoorModeConfirmTimes = fac.getExitNoDetectionCount();
//
//                int exitminimumdevices = fac.getExitMinBleDetectionDevices();
//
//                int exitlevel = fac.getExitMinBleDetectionLevel();
//
//                int blipssize = 0;
//                for (WlBlip o : results) {
//
//                    if (o.level > exitlevel) {
//
//                        blipssize++;
//
//                    }
//
//                }
//
//                if (blipssize < exitminimumdevices) {
//
//                    outDoorModeCounter++;
//
//                    if (PropertyHolder.getInstance().isUseExitPoiRangeForExit()) {
//                        if (outDoorModeCounter >= outDoorModeConfirmTimes && inExitRange(false)) {
//                            applyExit();
//                        } else if (outDoorModeCounter >= PropertyHolder.getInstance().getConsecutiveCountForExit() ) {
//                            applyExit();
//                        }
//                    } else {
//                        if (outDoorModeCounter >= outDoorModeConfirmTimes) {
//                            // exit facility
//                            applyExit();
//                        }
//                    }
//                } else {
//                    outDoorModeCounter = 0;
//                }
//            } else {
//
//                outDoorModeCounter = 0;
//
//            }
//        }

    }

    private void applyExit() {
        currentLocationMode = OUTDOOR_MODE;
        releaseIndoorMode();
        outDoorModeCounter = 0;

        notifyExitFacility(currentDetectedCampusId, currentDetectedFacilityId);

        currentDetectedFacilityId = null;
//						PropertyHolder.getInstance().setFacilityByBlips("unknown");

        String msg = "exit facility";
        presentToast(msg);

        GeoFenceHelper.getInstance().resetLocationGeofenceStates();
    }

    /**
     * checks the facility id according to BSSID file
     *
     * @param results the bssid detected on site
     * @return the facility id detected
     */
    private String findFacilityByBlips(List<WlBlip> results) {
        return FacilitySelector.getInstance().getFacilityByBlips(results);

    }

    private String findBridgeFacilityByBlips(List<WlBlip> results) {
        return FacilitySelector.getInstance().getBridgeFacilityByBlips(results);

    }

    private void releaseIndoorMode() {
        currentDetectedFacilityId = null;
        lastSelectedFloor = -100;
        fSelectionCounter = 0;

        insideExitZone = false;
        insideElevatorZone = false;
        elevatorZoneCounter = 0;

        inDoorModeCounter = 0;
        enterZoneCounter = 0;
        checkExitFacility = false;
        checkEnterFacility = false;

        isFirstRun = true;
        currentLocation = null;
        currentElevatorZone = null;
    }

    private void releaseOutdoorMode() {
        outDoorModeCounter = 0;
        checkExitFacility = false;
        checkEnterFacility = false;
        currentLocation = null;
    }

    private void checkIntentionForExitFacility() {
        Log.getInstance().debug(TAG, "Enter, checkIntentionForExitFacility()");

        boolean showdialog = PropertyHolder.getInstance()
                .isShowFarewellDialog();
        boolean twiceinexitzone = false;
        if (insideExitZone) {
            enterZoneCounter++;
            if (enterZoneCounter >= 2) {
                twiceinexitzone = true;
            }
        } else {
            enterZoneCounter = 0;
        }

        if (twiceinexitzone && showdialog) {

            checkExitFacility = true;

            if (exitTimer != null) {
                exitTimer.cancel();
            }

            exitTimer = new Timer();
            exitTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    checkExitFacility = false;
                    exitTimer = null;
                }
            }, EXIT_CHECK_INTERVAL);
        }
        Log.getInstance().debug(TAG, "Exit, checkIntentionForExitFacility()");
    }

    private void checkIntentionForEnterFacility() {

        if (isCheckEnteranceToFacility) {
            checkEnterFacility = true;
        }

        if (enterTimer != null) {
            enterTimer.cancel();
        }

        enterTimer = new Timer();
        enterTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkEnterFacility = false;
                enterTimer = null;
            }
        }, ENTER_CHECK_INTERVAL);

    }

    @Override
    public void onRecieve(List<WlBlip> results) {

        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                updateLocation();
            }
        };
        mainHandler.post(myRunnable);

    }

    private void loadFacility(String campus_id, String facility_id) {
        Log.getInstance().debug(TAG, "Enter, loadFacility()");
        // XXX here= load the facility
        // XXX MM ConfigurationUtils.getInstance().registerListener(this);

        // downloadingCampusId = campus_id;
        // downloadingfacilityId = facility_id;

        // XXX MM ConfigurationUtils.getInstance().appinit(context);
        ConfigsLoader.getInstance().registerListener(this);
        ConfigsLoader.getInstance().loadFacility(campus_id, facility_id);

        Log.getInstance().debug(TAG, "Exit, loadFacility()");
    }

    private void setFloorSelection(List<WlBlip> mResults) {
        Log.getInstance().debug(TAG, "Enter, setFloorSelection()");

        WlBlip[] res1 = mResults.toArray(new WlBlip[mResults.size()]);
        int groupszbyblips = NdkFloorSelector.getInstance().getFloor(res1,
                false);

        if (groupszbyblips >= 0) {
            Campus ccampus = ProjectConf.getInstance().getSelectedCampus();
            FacilityConf cfacility = null;
            if (currentDetectedFacilityId != null && ccampus != null) {
                Map<String, FacilityConf> campusmap = ccampus
                        .getFacilitiesConfMap();
                cfacility = campusmap.get(currentDetectedFacilityId);
                if (cfacility != null) {
                    cfacility.setSelected(groupszbyblips);
                    cfacility.updateFloorData();
                    ndkFiltercounter = 0;
                    notifyFloorChange(currentDetectedCampusId, currentDetectedFacilityId, groupszbyblips);

                    String msg = "first selection groups " + "f - " + groupszbyblips;
                    presentToast(msg);
                }
            }

            lastSelectedFloor = groupszbyblips;
        } else {
            WlBlip[] res = mResults.toArray(new WlBlip[mResults.size()]);
            int zbyblips = NdkFloorSelector.getInstance().getFloor(res, true);

            if (zbyblips >= 0) {
                Campus ccampus = ProjectConf.getInstance().getSelectedCampus();
                FacilityConf cfacility = null;
                if (currentDetectedFacilityId != null && ccampus != null) {
                    Map<String, FacilityConf> campusmap = ccampus
                            .getFacilitiesConfMap();
                    cfacility = campusmap.get(currentDetectedFacilityId);
                    if (cfacility != null) {
                        cfacility.setSelected(zbyblips);
                        cfacility.updateFloorData();
                        ndkFiltercounter = 0;
                        notifyFloorChange(currentDetectedCampusId, currentDetectedFacilityId, zbyblips);

                        String msg = "first selection matrix " + "f - " + zbyblips;
                        presentToast(msg);
                    }
                }

                lastSelectedFloor = zbyblips;

            } else {
                FacilityConf fac = FacilityContainer.getInstance().getCurrent();
                if (fac != null) {
                    int entrancefloor = fac.getEntranceFloor();
                    fac.setSelected(entrancefloor);
                    notifyFloorChange(currentDetectedCampusId, currentDetectedFacilityId, entrancefloor);

                    String msg = "first selection entrance " + "f - " + zbyblips;
                    presentToast(msg);
                }
                lastSelectedFloor = zbyblips;
            }
        }

        isFirstRun = false;
        Log.getInstance().debug(TAG, "Exit, setFloorSelection()");
    }

    private void loadCampus(String campus_id) {
        Log.getInstance().debug(TAG, "Enter, loadCampus()");
        // XXX MM ConfigurationUtils.getInstance().registerListener(this);
        // downloadingCampusId = campus_id;
        // downloadingfacilityId = null;
        // XXX MM ConfigurationUtils.getInstance().appinit(context);
        ConfigsLoader.getInstance().registerListener(this);
        ConfigsLoader.getInstance().loadCampus(campus_id);

        Log.getInstance().debug(TAG, "Exit, loadCampus()");
    }

    /**
     * public boolean startLocationService(Context ctx, MyLocationListener
     * locationListener, ScanMode scanMode) { ////XXX MM
     * PropertyHolder.getInstance().setDownloadingData(true); context = ctx; //
     * loadDefaultCampus(); init(scanMode, ctx); //XXX MM
     * ConfigurationUtils.getInstance().registerListener(this);
     * ConfigsLoader.getInstance().registerListener(this);
     * subscribeForLocation(locationListener); started = true; return true; }
     */

    public boolean startLocationService(Context ctx, ScanMode scanMode) {
        context = ctx;
        // loadDefaultCampus();
        init(scanMode, ctx);
        // XXX MM ConfigurationUtils.getInstance().registerListener(this);
        ConfigsLoader.getInstance().registerListener(this);
        started = true;
        return true;
    }

    public boolean startLocationService(Context ctx, ILocation location) {
        context = ctx;


        if (location instanceof Location) {

            stopLocationService();

            locationRepeaterThreadRunning = true;

            repeatedLocation = (Location) location;

            locationRepeaterThread = new Thread() {

                @Override
                public void run() {

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            updateSimulatedLocation(repeatedLocation);
                        }
                    };

                    while (locationRepeaterThreadRunning) {
                        //System.out.println(repeatedLocation);
                        mainHandler.post(myRunnable);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                    mainHandler.removeCallbacks(myRunnable);


                }
            };
            locationRepeaterThread.start();
            started = true;

        }


        return true;
    }

    private void loadDefaultCampus() {
        Log.getInstance().debug(TAG, "Enter, loadDefaultCampus()");

        // XXX here= load the first campus in the list of campuses of the user
        List<String> campuses = new ArrayList<String>();
        Map<String, Campus> campusesmap = ProjectConf.getInstance()
                .getCampusesMap();

        for (String key : campusesmap.keySet()) {
            campuses.add(key);
        }

        if (campuses.size() > 0) {
            String campus_id = campuses.get(0);
            PropertyHolder.getInstance().setCampusId(campus_id);
            // XXX MM ConfigurationUtils.getInstance().registerListener(this);
            // downloadingCampusId = campus_id;
            // downloadingfacilityId = null;
            // XXX MM ConfigurationUtils.getInstance().appinit(context);
            ConfigsLoader.getInstance().registerListener(this);
            ConfigsLoader.getInstance().loadCampus(campus_id);
        }
        Log.getInstance().debug(TAG, "Exit, loadDefaultCampus()");
    }

    public boolean stopLocationService() {
        if (blipsScanner != null) {
            blipsScanner.stopScanning();
        }
        setStarted(false);
        isFirstRun = true;
        locationRepeaterThreadRunning = false;

        return true;
    }

    public boolean isInsideElevatorZone() {
        return insideElevatorZone;
    }

    public GeoFenceRect getCurrentElevatorZone() {
        return currentElevatorZone;
    }

    // // XXX NDK LOAD LIB
    // static {
    // System.loadLibrary("MlinsLocationFinderUtils");
    // }

    public ILocation getCurrentLocation() {

        ILocation result = null;

        try {
            result = currentLocation != null ? new Location(currentLocation) : ProjectConf.getInstance().getSelectedCampus().getDefaultCampusLocation();

            if (result.getLocationType() == LocationMode.OUTDOOR_MODE) {
                LatLng locLatLng = new LatLng(result.getLat(), result.getLon());

                ILocation locInPolygon = FloorPolygonManager.getInstance().getLocationInPolygon(locLatLng);
                if (locInPolygon != null && locInPolygon instanceof Location) {
                    result = new Location(locInPolygon);

                }
            }
        } catch (Throwable t) {

        }

        return result;

    }

    public void cleanListeners() {
        listeneres.clear();

    }

    public void cleanMapViewListeners() {
        List<MyLocationListener> listenerstoremove = new ArrayList<MyLocationListener>();
        for (MyLocationListener o : listeneres) {
            if (o != null && o instanceof LocationMapView) {
                listenerstoremove.add(o);
            }
        }

        for (MyLocationListener o : listenerstoremove) {
            if (o != null && listeneres.contains(o)) {
                try {
                    listeneres.remove(o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    @Override
    public void onPreConfigsLoad(LoadStatus loadStatus) {
        // XXX MM PropertyHolder.getInstance().setDownloadingData(true);
    }

    @Override
    public void onPostConfigsLoad(LoadStatus status) {
        // XXX MM PropertyHolder.getInstance().setDownloadingData(false);
    }

    public void resetFirstRun() {
        isFirstRun = true;
    }

    private void setCurrentFacility(String facilityid) {

        Campus ccampus = ProjectConf.getInstance().getSelectedCampus();
        FacilityConf cfacility = null;
        if (ccampus == null) {
            return;
        } else {
            Map<String, FacilityConf> campusmap = ccampus
                    .getFacilitiesConfMap();
            cfacility = campusmap.get(facilityid);
            if (cfacility == null) {
                return;
            }
        }

        FacilityContainer.getInstance().setCurrent(cfacility);
        reloadNdkFloorSelector();

    }

    // start ================= converted lat/lon ================================

    // ================= ndk link ==============
    private void reloadNdkFloorSelector() {

        Campus ccampus = ProjectConf.getInstance().getSelectedCampus();
        // FacilityConf cfacility = null;
        // if (ccampus == null) {
        // return;
        // } else {
        // Map<String, FacilityConf> campusmap = ccampus.getFacilitiesConfMap();
        // cfacility = campusmap.get(facilityid);
        // if (cfacility == null) {
        // return;
        // }
        // }

        FacilityConf cfacility = FacilityContainer.getInstance().getCurrent();

        if (cfacility != null) {
            int floor = cfacility.getSelectedFloor();
            String facility = cfacility.getId(); // currentDetectedFacilityId;
            // //PropertyHolder.getInstance().getFacilityID();
            String campus = ccampus.getId(); // PropertyHolder.getInstance().getCampusId();
            String project = PropertyHolder.getInstance().getProjectId();
            boolean isBin = PropertyHolder.getInstance().isTypeBin();
            String appDirPath = PropertyHolder.getInstance().getAppDir()
                    .getAbsolutePath();
            String scanType = PropertyHolder.getInstance()
                    .getMatrixFilePrefix();
            if (PropertyHolder.useZip) {
                appDirPath = PropertyHolder.getInstance().getZipAppdir().getAbsolutePath();
                scanType = "";
            }

            int locationCloseRange = cfacility.getNdkCloseRange();
            int k = PropertyHolder.getInstance().getK();
            float pixelsToMeter = cfacility.getPixelsToMeter();
            int floorcount = cfacility.getFloorDataList().size(); // FacilityConf.getInstance().getFloorDataList().size();
            int averageRange = PropertyHolder.getInstance().getAverageRange();

            ArrayList<String> filter = PropertyHolder.getInstance()
                    .getSsidFilter();
            String[] ssidfilter = filter.toArray(new String[filter.size()]);

            float closeDevicesThreshold = PropertyHolder.getInstance()
                    .getCloseDeviceThreshold();
            float closeDeviceWeight = PropertyHolder.getInstance()
                    .getCloseDeviceWeight();
            int kTopLevelThr = cfacility.getFloorsTopKlevelsThr();

            int levelLowerBound = cfacility.getFloorselectionLevelLowerBound() + PropertyHolder.getInstance().getLowerBoundSupplement();

            NdkFloorSelector.getInstance().initParams(appDirPath,
                    locationCloseRange, k, pixelsToMeter, averageRange,
                    ssidfilter, floorcount, scanType, closeDevicesThreshold,
                    closeDeviceWeight, kTopLevelThr, levelLowerBound);

            String path = project + "/" + campus + "/" + facility;

            if (PropertyHolder.useZip) {
                path = project + "/" + campus + "/facilities/" + facility + "/" + "floors";
            }

            NdkFloorSelector.getInstance().load(path, floor, isBin);

        }
    }

    private void notifyConvertedLocationListeneres(ILocation location) {

        if (convertedLocationListeneres.size() == 0) {
            return;
        }

        ILocation convertedLoc = convert(location);  // convert x,y coordinates

        Log.getInstance().debug(TAG, "Enter, notifyConvertedLocationListeneres()");
        for (MyConvertedLocationListener listener : convertedLocationListeneres) {
            try {
                if (listener != null) {
                    listener.onConvertedLocationDelivered(convertedLoc);
                }
            } catch (Exception e) {
                Log.getInstance().error(
                        "com.mlins.locationutils.LocationFinder",
                        e.getMessage(), e);

                e.printStackTrace();
            }
        }
        Log.getInstance().debug(TAG, "Exit, notifyConvertedLocationListeneres()");
    }

    public void subscribeForConvertedLocation(MyConvertedLocationListener detector) {
        if (!convertedLocationListeneres.contains(detector)) {
            convertedLocationListeneres.add(detector);
        }
    }

    public void unsubscibeFromConvertedLocation(MyConvertedLocationListener detector) {
        if (convertedLocationListeneres.contains(detector)) {
            convertedLocationListeneres.remove(detector);
        }
    }

    // example of using the translation api
    private ILocation convertLatLon(ILocation location) {

        try {
            //if (location.getLocationType() == INDOOR_MODE) {
            String facid = location.getFacilityId();
            String cid = location.getCampusId();
            Campus c = ProjectConf.getInstance().getCampus(cid);
            if (c != null) {
                Map<String, FacilityConf> facilitiesmap = c.getFacilitiesConfMap();
                FacilityConf fac = facilitiesmap.get(facid);
                if (fac != null) {

                    NdkLocation point = new NdkLocation(location.getX(), location.getY());
                    point.setZ(location.getZ());

                    NdkLocation covertedPoint = new NdkLocation();


                    NdkConversionUtils converter = new NdkConversionUtils();

                    double rotationAngle = fac.getRot_angle();

                    converter.convertPoint(point, fac.getConvRectTLlon(),
                            fac.getConvRectTLlat(), fac.getConvRectTRlon(),
                            fac.getConvRectTRlat(), fac.getConvRectBLlon(),
                            fac.getConvRectBLlat(), fac.getConvRectBRlon(),
                            fac.getConvRectBRlat(), fac.getMapWidth(),
                            fac.getMapHight(), rotationAngle, covertedPoint);


                    NdkLocation covertedLatLonPoint = new NdkLocation();

                    converter.convertLatLonPoint(covertedPoint, fac.getConvRectTLlon(),
                            fac.getConvRectTLlat(), fac.getConvRectTRlon(),
                            fac.getConvRectTRlat(), fac.getConvRectBLlon(),
                            fac.getConvRectBLlat(), fac.getConvRectBRlon(),
                            fac.getConvRectBRlat(), fac.getMapWidth(),
                            fac.getMapHight(), rotationAngle, covertedLatLonPoint);

                    if (covertedPoint != null && covertedLatLonPoint != null) {

                        System.out.println("origin (" + location.getX() + "," + location.getY() + ") converted (" + covertedLatLonPoint.x + "," + covertedLatLonPoint.y + ")");


                        location.setLat(covertedPoint.getLat());
                        location.setLon(covertedPoint.getLon());
                    }
                }
            }
            //}
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return location;
    }

    private ILocation convert(ILocation location) {

        try {
            if (location.getLocationType() == INDOOR_MODE) {
                String facid = location.getFacilityId();
                String cid = location.getCampusId();
                Campus c = ProjectConf.getInstance().getCampus(cid);
                if (c != null) {
                    Map<String, FacilityConf> facilitiesmap = c.getFacilitiesConfMap();
                    FacilityConf fac = facilitiesmap.get(facid);
                    if (fac != null) {

                        NdkLocation point = new NdkLocation(location.getX(), location.getY());
                        point.setZ(location.getZ());

                        NdkLocation covertedPoint = new NdkLocation();

                        NdkConversionUtils converter = new NdkConversionUtils();

                        double rotationAngle = fac.getRot_angle();

                        converter.convertPoint(point, fac.getConvRectTLlon(),
                                fac.getConvRectTLlat(), fac.getConvRectTRlon(),
                                fac.getConvRectTRlat(), fac.getConvRectBLlon(),
                                fac.getConvRectBLlat(), fac.getConvRectBRlon(),
                                fac.getConvRectBRlat(), fac.getMapWidth(),
                                fac.getMapHight(), rotationAngle, covertedPoint);

                        if (covertedPoint != null) {
                            location.setLat(covertedPoint.getLat());
                            location.setLon(covertedPoint.getLon());
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return location;
    }

    private void notifyExitFacility(String campusId, String facilityId) {
        for (MyLocationListener listener : new ArrayList<>(listeneres)) {
            try {
                if (listener != null) {
                    listener.onFacilityRegionExit(campusId,
                            facilityId);
                    listener.onLocationModeChange(LocationMode.OUTDOOR_MODE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //end  ======================== converted lat/lon ====================================

    private void notifyFloorChange(String campusId, String facilityId, int floor) {
        for (MyLocationListener listener : new ArrayList<>(listeneres)) {
            try {
                if (listener != null) {
                    listener.onFloorChange(campusId, facilityId, floor);
                }
            } catch (Exception e) {
                Log.getInstance().error(
                        "com.mlins.locationutils.LocationFinder",
                        e.getMessage(), e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public PointF getRubberFilterPoint() {
        return rubberFilterPoint;
    }

    public void setRubberFilterPoint(PointF rubberFilterPoint) {
        this.rubberFilterPoint = rubberFilterPoint;
    }

    private void presentToast(String msg) {
        try {
            if (PropertyHolder.getInstance().isDevelopmentMode()) {
                if (context != null && msg != null && !msg.isEmpty()) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Throwable t) {

        }
    }

    public Location getSimulatedLocation() {
        return simulatedLocation;
    }


    /**
     * Attention LocationFinder modifies passed location instance several times:
     * 1. inside projectOnGis method
     * 2. inside updateLocation method
     * @param simulatedLocation
     */
    public void setSimulatedLocation(Location simulatedLocation) {
        setSimulatedLocation(simulatedLocation, false);
    }

    public void setSimulatedLocation(Location simulatedLocation, boolean saveRawLocation) {
        if (simulatedLocation != null) {
            if (saveRawLocation) {
                rawSimulatedLocation = new Location(simulatedLocation);
                projectOnGis(simulatedLocation);
            } else {
                projectOnGis(simulatedLocation);
                rawSimulatedLocation = new Location(simulatedLocation);
            }
        } else {
            rawSimulatedLocation = simulatedLocation;
            currentLocation = null;
            currentLocationMode = OUTDOOR_MODE;
        }

        this.simulatedLocation = simulatedLocation;
    }

    private void projectOnGis(Location loc) {
        try {

            List<GisLine> lines = null;
            if (loc.getLocationType() == LocationFinder.INDOOR_MODE) {
                String tmpfacid = loc.getFacilityId();
                int tmpfloor = (int) loc.getZ();
                String facilityid = tmpfacid;
                lines = GisData.getInstance().getLines(facilityid,
                        tmpfloor);
                projectOnGis(loc, lines);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void projectOnGis(Location o, List<GisLine> lines) {
        if (o != null && lines != null && !lines.isEmpty()) {
            PointF p = new PointF((float) o.getX(), (float) o.getY());
            PointF projectedp = GisData.getInstance().findClosestPointOnLine(
                    lines, p);
            o.setX(projectedp.x);
            o.setY(projectedp.y);
        }
    }

    private void addToLocationAvgHistoryList(PointF locPt) {

        if (locsHistoryList != null) {
            PointF p = new PointF(locPt.x, locPt.y);

            if (locsHistoryList.size() < getLocationAveragePointsCount()) {
                locsHistoryList.add(p);
            } else {
                if (locsHistoryList.size() > 0) {

                    locsHistoryList.add(locsHistoryList.size() - 1, p);
                    locsHistoryList.remove(0);
                }
            }
        }
    }

    private PointF getLocationAverage() {
        PointF avgLoc = null;
        if (locsHistoryList.size() >= getLocationAveragePointsCount()
                && locsHistoryList.size() > 0) {
            float sumX = 0;
            float sumY = 0;
            float cnt = locsHistoryList.size();
            for (PointF pt : locsHistoryList) {
                sumX = sumX + pt.x;
                sumY = sumY + pt.y;
            }
            avgLoc = new PointF(sumX / cnt, sumY / cnt);
        }
        return avgLoc;
    }

    public boolean isInCampus(int radiusInMeters) {
        boolean result = false;

        try {
            if (currentLocation != null) {
                if (currentLocation.getLocationType() == LocationMode.INDOOR_MODE) {
                    result = true;
                } else {
                    Campus ccampus = ProjectConf.getInstance().getSelectedCampus();
                    if (ccampus != null) {
                        double clat = ccampus.getCenterLatitude();
                        double clon = ccampus.getCenterLongtitude();
                        LatLng centerloc = new LatLng(clat, clon);
                        double mylat = currentLocation.getLat();
                        double mylon = currentLocation.getLon();
                        LatLng myloc = new LatLng(mylat, mylon);
                        double distance = MathUtils.distance(centerloc, myloc);
                        if (distance < radiusInMeters) {
                            result = true;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }

    public Location getRawSimulatedLocation() {
        return rawSimulatedLocation;
    }

    // ================= end ndk link ==============

}
