package com.mlins.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mlins.aStar.CampusNavigationPath;
import com.mlins.aStar.GisSegment;
import com.mlins.aStar.aStarAlgorithm;
import com.mlins.aStar.aStarData;
import com.mlins.instructions.NavInstruction;
import com.mlins.nav.utils.CustomInfoWindowAdapter;
import com.mlins.nav.utils.ParkingUtil;
import com.mlins.nav.utils.SoundPlayer;
import com.mlins.navigation.GoogleNavigationUtil;
import com.mlins.objects.MarkerObject;
import com.mlins.objects.ParkingMarker;
import com.mlins.orientation.OrientationMonitor;
import com.mlins.overlay.CampusOverlay;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.GMapOverlyData;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PoiData;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceTranslator;
import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.GisPoint;
import com.mlins.utils.logging.Log;
import com.spreo.interfaces.SpreoMapViewListener;
import com.spreo.interfaces.SpreoNavigationListener;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.enums.MapRotationType;
import com.spreo.nav.enums.NavigationState;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.INavInstruction;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.spreosdk.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gps.CampusGisData;
import gps.GoogleLocationHolder;

public class OutdoorMapView extends FrameLayout {
    private final static String TAG = "com.mlins.nav.utils.EmailUtil";
    private Context ctx = null;
    private MapView mMapView;
    private GoogleMap googlemap;
    private Marker gmLoc = null;
    private float defaultZoom = 17;
    private boolean followMeMode = true;
    private FrameLayout dummyLayoutFollowMeModeDisable;
    private ILocation myLocation = null;
    private Polyline campusPath = null;
    private boolean navigationState = false;
    private List<SpreoMapViewListener> listeners = new ArrayList<SpreoMapViewListener>();
    private List<SpreoNavigationListener> navigationListeners = new ArrayList<SpreoNavigationListener>();
    private HashMap<String, String> facilityNameIdMap = new HashMap<String, String>();
    private Bitmap locbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mylocaion);
    private ParkingMarker parkingmarker = null;
    private Polyline navarrow = null;
    private GroundOverlay campusGroundOverlay = null;
    private GroundOverlay facilityGroundOverlay = null;
    private LatLng currentloc = null;
    private float currentzoom = defaultZoom;
    private int navDistance = 1000;
    private int endGoogleNavDistance = 150;
    private SpreoMapViewListener mapviewlistener = null;
    private Map<IPoi, MarkerObject> markers = new HashMap<IPoi, MarkerObject>();
    private Map<Marker, IPoi> pois = new HashMap<Marker, IPoi>();
    private List<GroundOverlay> campusGroundOverlays = new ArrayList<GroundOverlay>();
    private int distanceFromKml = 30;
    private FrameLayout instructionLayout;
    private int reachDestinationRange = 5;
    private OnMarkerClickListener markerClickListener = new OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {

            if (parkingmarker != null && marker.equals(parkingmarker.getMarker())) {
                notifyOnMyParkingMarkerClick();
                return true;
            } else if (gmLoc != null && gmLoc.equals(marker)) {
                notifyUserClick();
                return true;
            } else {
                IPoi poi = getPoiFromMarker(marker);
                if (poi != null) {
                    onPoiClick(poi);
                    return true;
                }
            }
            return false;
        }

    };
    private OnInfoWindowClickListener infoListener = new OnInfoWindowClickListener() {

        @Override
        public void onInfoWindowClick(Marker marker) {

            IPoi poi = getPoiFromMarker(marker);
            if (poi != null) {
                onBubbleClick(poi);
            } else if (gmLoc != null && gmLoc.equals(marker)) {
                notifyUserInfoClick();
            } else {

                if (parkingmarker != null) {
                    if (marker != null) {
                        if (marker.equals(parkingmarker.getMarker())) {
                            notifyOnMyParkingBubbleClick();
                        }
                    }
                }

                final String fac = marker.getTitle().toLowerCase();
                final String id = facilityNameIdMap.get(fac);
                for (final SpreoMapViewListener o : listeners) {
                    try {
                        o.facilityClickListener(id);
                    } catch (Exception e) {
                        Log.getInstance().error(TAG, e.getMessage(), e);
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }


            }

        }


    };
    private OnClickListener targetlistener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (myLocation != null) {
                LatLng latlng = new LatLng(myLocation.getLat(), myLocation.getLon());
                googlemap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, defaultZoom));
                followMeMode = true;
            }
        }
    };
    private OnTouchListener dummyTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            followMeMode = false;
            return false;
        }
    };


    public OutdoorMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.outdoor_map_view, this, true);
    }

    public void init(Bundle savedInstanceState) {
        Log.getInstance().debug(TAG, "Enter, init()");
        try {
            MapsInitializer.initialize(ctx);
        } catch (Throwable e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mMapView = (MapView) findViewById(R.id.googleMap);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                OutdoorMapView.this.googlemap = googleMap;
                int maptype = PropertyHolder.getInstance().getMapType();
                googlemap.setMapType(maptype);
                googlemap.getUiSettings().setZoomControlsEnabled(false);
                googlemap.setOnInfoWindowClickListener(infoListener);
                googlemap.setOnMarkerClickListener(markerClickListener);
            }
        });
        ImageButton target = (ImageButton) findViewById(R.id.target);
        target.setOnClickListener(targetlistener);
        dummyLayoutFollowMeModeDisable = (FrameLayout) findViewById(R.id.dummyLayoutFollowMeModeDisable);
        dummyLayoutFollowMeModeDisable.setOnTouchListener(dummyTouchListener);
        instructionLayout = (FrameLayout) findViewById(R.id.instruction_layout);
        initParkingLocation();
        loadCampusOverlayDefinedByUser();
        loadFacilityOverlyDefinedByUser();
        loadCampusOverlays();
        Log.getInstance().debug(TAG, "Exit, init()");
    }

    public void applyOutdoorLocation(ILocation location) {
        Log.getInstance().debug(TAG, "Enter, applyOutdoorLocation()");
        myLocation = location;
        LatLng latlng = new LatLng(myLocation.getLat(), myLocation.getLon());
        if (gmLoc != null) {
            gmLoc.setPosition(latlng);
        } else {

            BitmapDescriptor locicon = BitmapDescriptorFactory.fromBitmap(locbitmap);
            gmLoc = googlemap.addMarker(new MarkerOptions().position(latlng).icon(locicon).anchor(0.5f, 0.5f));
        }

        if (followMeMode) {
            googlemap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, defaultZoom));
        }

        if (PropertyHolder.getInstance().getRotatingMapType() != MapRotationType.STATIC) {
            float bearing = OrientationMonitor.getInstance().getAzimuth();
            updateBearing(bearing);
        }


        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    setNavigation();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


        Log.getInstance().debug(TAG, "Exit, applyOutdoorLocation()");
    }

    public void setNavigation() {
        PoiData dest = aStarData.getInstance().getExternalPoi();
        if (myLocation != null && dest != null) {
            double mylocationlat = myLocation.getLat();
            double mylocationlon = myLocation.getLon();
            LatLng mylatlng = new LatLng(mylocationlat, mylocationlon);
            double destlat = dest.getPoiLatitude();
            double destlon = dest.getPoiLongitude();
            LatLng destLatLng = new LatLng(destlat, destlon);
            final double distancefromdest = MathUtils.distance(mylatlng, destLatLng);

            if (distancefromdest < navDistance) {
                if (PropertyHolder.getInstance().isNotifyGoogleDestination() && distancefromdest < endGoogleNavDistance) {
                    notifyGoogleNavigationEnded();
                    PropertyHolder.getInstance().setNotifyGoogleDestination(false);
                }

                if (distancefromdest < reachDestinationRange) {
                    Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (aStarData.getInstance().getInternalDestination() == null) {
                                    if (!PropertyHolder.getInstance().isNavigationInstructionsSoundMute()) {
                                        List<String> sounds = new ArrayList<String>();
                                        sounds.add("destination");
                                        SoundPlayer.getInstance().play(sounds);
                                    }
                                    notifyOnNavigationStateChanged(NavigationState.DESTINATION_REACHED);
                                    stopNaviagtion();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    post(r);
                    return;
                }

                List<GisLine> l = CampusGisData.getInstance().getLines();
                if (l.isEmpty() || PropertyHolder.getInstance().isLocationPlayer()) {
                    updateArrow(mylatlng, destLatLng);
                } else {
                    aStarData.getInstance().cleanAStar();
                    GisPoint startpoint = new GisPoint(mylocationlon, mylocationlat, 0);
                    GisPoint endpoint = new GisPoint(destlon, destlat, 0);
                    aStarData.getInstance().loadData(startpoint, endpoint, l);
                    aStarAlgorithm a = new aStarAlgorithm(startpoint, endpoint);
                    List<GisSegment> path = null;
                    path = a.getPath();
                    CampusNavigationPath navpath = new CampusNavigationPath(path);
                    aStarData.getInstance().setCurrentCampusPath(navpath);
                    LatLng projectedloc = CampusGisData.getInstance().findClosestPointOnLine(mylatlng);
                    LatLng projecteddest = CampusGisData.getInstance().findClosestPointOnLine(destLatLng);
                    double dfromloc = MathUtils.distance(mylatlng, projectedloc);
                    double dfromdest = MathUtils.distance(destLatLng, projecteddest);
                    if (dfromloc < distanceFromKml && dfromdest < distanceFromKml) {
                        updatePath(navpath);
                    } else {
                        updateArrow(mylatlng, destLatLng);
                    }
                }

                if (!PropertyHolder.getInstance().isNavigationState()) {
                    PropertyHolder.getInstance().setNavigationState(true);
                }

                Runnable r1 = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "outdoorInstruction");
                            String text = getResources().getString(tmptxt);
                            Bitmap signBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.outdoor_instruction_sign);
                            NavInstruction navInst = new NavInstruction(INavInstruction.OUTDOOR_INSTRUCTION_TAG, text, signBitmap, distancefromdest);
                            notifyOnNavigationInstructionChanged(navInst);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                };
                post(r1);


                if (PropertyHolder.getInstance().isDisplayNavigationInstructionsOnMap() && instructionLayout.getVisibility() == View.INVISIBLE) {
                    Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            try {
                                instructionLayout.setVisibility(View.VISIBLE);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    post(r);
                }

            } else {
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            removeNavigationPath();
                            removeNavigationArrow();
                            if (instructionLayout.getVisibility() == View.VISIBLE) {
                                instructionLayout.setVisibility(View.INVISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                post(r);

                aStarData.getInstance().setCurrentCampusPath(null);
            }
        } else {
            if (instructionLayout.getVisibility() == View.VISIBLE) {
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            instructionLayout.setVisibility(View.INVISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                post(r);
            }
        }


    }

    private void updateArrow(final LatLng mylatlng, final LatLng destLatLng) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    removeNavigationPath();
                    List<LatLng> points = new ArrayList<LatLng>();
                    points.add(mylatlng);
                    points.add(destLatLng);
                    if (navarrow == null) {
                        navarrow = googlemap.addPolyline(new PolylineOptions().zIndex(2f).color(Color.parseColor(PropertyHolder.getInstance().getNavRouteColor())));
                    }
                    navarrow.setPoints(points);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        post(r);
    }

    private void updatePath(final CampusNavigationPath navpath) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    removeNavigationArrow();
                    removeNavigationPath();
                    List<GisSegment> segments = navpath.getPath();
                    if (segments.size() > 0) {
                        PolylineOptions polyoptions = new PolylineOptions().color(Color.parseColor(PropertyHolder.getInstance().getNavRouteColor()));
                        for (GisSegment o : segments) {
                            polyoptions.add(new LatLng(o.getLine().getPoint1().getY(), o.getLine().getPoint1().getX())).add(new LatLng(o.getLine().getPoint2().getY(), o.getLine().getPoint2().getX()));
                        }
                        if (polyoptions != null) {
                            campusPath = googlemap.addPolyline(polyoptions);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        post(r);

    }

    public void stopNaviagtion() {
        aStarData.getInstance().setExternalDestination(null);
        aStarData.getInstance().setExternalPoi(null);
        aStarData.getInstance().setCurrentCampusPath(null);
        removeNavigationPath();
        removeNavigationArrow();
        if (instructionLayout.getVisibility() == View.VISIBLE) {
            instructionLayout.setVisibility(View.INVISIBLE);
        }
        navigationState = false;
        if (PropertyHolder.getInstance().isNavigationState()) {
            notifyOnNavigationStateChanged(NavigationState.STOPED);
            PropertyHolder.getInstance().setNavigationState(false);
        }
    }

    public void removeNavigationPath() {
        if (campusPath != null) {
            campusPath.remove();
            campusPath = null;
        }
    }

    public void removeNavigationArrow() {
        if (navarrow != null) {
            navarrow.remove();
            navarrow = null;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        init(savedInstanceState);
    }

    public void onResume() {
        try {
            mMapView.onResume();
        } catch (Exception e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // googlemap.animateCamera(CameraUpdateFactory.zoomTo(defaultZoom));
    }

    public void onDestroy() {
        mMapView.onDestroy();
    }

    public void onPause() {
        mMapView.onPause();
    }

    public void onLowMemory() {
        mMapView.onLowMemory();
    }

    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    public void updateBearing(float bearing) {
        // if (gmLoc != null) {
        // gmLoc.setRotation(bearing);
        // }

        if (followMeMode && myLocation != null) {
            currentloc = new LatLng(myLocation.getLat(), myLocation.getLon());
            currentzoom = defaultZoom;
        } else {
            currentloc = googlemap.getCameraPosition().target;
            currentzoom = googlemap.getCameraPosition().zoom;
        }
        CameraPosition currentPlace = new CameraPosition.Builder().target(new LatLng(currentloc.latitude, currentloc.longitude)).bearing(bearing).zoom(currentzoom).build();
        googlemap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));

        // CameraPosition currentPlace = new CameraPosition.Builder()
        // .target(new LatLng(myLocation.getLat(), myLocation.getLon()))
        // .bearing(bearing).zoom(defaultZoom).build();
        // googlemap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
    }

    public void ShowUserBubble(View v) {
        if (gmLoc != null) {
            if (v != null) {
                googlemap.setInfoWindowAdapter(new CustomInfoWindowAdapter(v, gmLoc));
            }
            gmLoc.showInfoWindow();
        }
    }

    public void hideUserBubble() {
        if (gmLoc != null) {
            gmLoc.hideInfoWindow();
        }
    }

    public void showFacilityMarkers() {
        Log.getInstance().debug(TAG, "Enter, showFacilityMarkers()");

        Campus campus = ProjectConf.getInstance().getSelectedCampus();

        if (campus != null) {
            nameIdMap(campus);
            Map<String, FacilityConf> facilitiesConfMap = campus.getFacilitiesConfMap();
            if (facilitiesConfMap != null) {
                for (FacilityConf o : facilitiesConfMap.values()) {
                    if (o != null) {
                        LatLng facloc = new LatLng(o.getCenterLatitude(), o.getCenterLongtitude());
                        String facid = o.getId();
                        String facname = o.getName();
                        if (facloc != null && facid != null) {
                            Marker marker = googlemap.addMarker(new MarkerOptions().position(facloc).title(facname));

                            // facilityByMarker.put(marker, o);
                        }
                    }
                }
            }
        }
        Log.getInstance().debug(TAG, "Exit, showFacilityMarkers()");
    }

    public void nameIdMap(Campus campus) {
        Log.getInstance().debug(TAG, "Enter, nameIdMap()");
        String facid = null;

        if (campus != null) {
            Map<String, FacilityConf> facilitiesConfMap = campus.getFacilitiesConfMap();
            if (facilitiesConfMap != null) {
                for (FacilityConf o : facilitiesConfMap.values()) {
                    if (o != null) {
                        facid = o.getId();
                        if (facid != null) {
                            facilityNameIdMap.put(o.getName().toLowerCase(), o.getId());

                        }
                    }
                }
            }
        }
        Log.getInstance().debug(TAG, "Exit, nameIdMap()");
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
        }
    }

    public void unregisterListener(SpreoMapViewListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public void mapZoomIn() {
        float zoomin = googlemap.getCameraPosition().zoom + 1;
        googlemap.animateCamera(CameraUpdateFactory.zoomTo(zoomin));
    }

    public void mapZoomOut() {
        float zoomin = googlemap.getCameraPosition().zoom - 1;
        googlemap.animateCamera(CameraUpdateFactory.zoomTo(zoomin));
    }

    public void showMyLocation() {
        if (myLocation != null) {
            LatLng latlng = new LatLng(myLocation.getLat(), myLocation.getLon());
            googlemap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
        }

    }

    public Bitmap getUserIcon() {
        return locbitmap;
    }

    public void setUserIcon(Bitmap usericon) {
        this.locbitmap = usericon;
        if (gmLoc != null) {
            BitmapDescriptor locicon = BitmapDescriptorFactory.fromBitmap(locbitmap);
            gmLoc.setIcon(locicon);
        }
    }

    public boolean isNavigationState() {
        return navigationState;
    }

    private void initParkingLocation() {
        ILocation ploc = ParkingUtil.getInstance().getParkingLocation();
        if (ploc != null && ploc.getLocationType() == LocationMode.OUTDOOR_MODE) {
            LatLng parkingloc = new LatLng(ploc.getLat(), ploc.getLon());
            updateParkingMarker(parkingloc);
        }
    }

    public void setCurrentLocationAsParking() {
        LatLng loc = GoogleLocationHolder.getInstance().getGoogleLocation();
        if (loc != null) {
            // LatLng parkingloc = new LatLng(myLocation.getLat(),
            // myLocation.getLon());
            com.mlins.utils.gis.Location parking = new com.mlins.utils.gis.Location(loc);
            updateParkingMarker(loc);
            ParkingUtil.getInstance().save(parking);
        }
    }

    private void updateParkingMarker(LatLng parkingloc) {
        if (parkingmarker != null) {
            parkingmarker.setPosition(parkingloc);
        } else {

            parkingmarker = new ParkingMarker(parkingloc, null, ctx, googlemap, this);
//			Bitmap parkingbm = BitmapFactory.decodeResource(getResources(), R.drawable.parking);
//			BitmapDescriptor parkingicon = BitmapDescriptorFactory.fromBitmap(parkingbm);
//			MarkerOptions moptions = new MarkerOptions().icon(parkingicon).position(parkingloc);
//			parkingmarker = googlemap.addMarker(moptions);
        }
    }

    public void removeParkingLocation() {
        ParkingUtil.getInstance().delete();
        if (parkingmarker != null) {
            parkingmarker.remove();
            parkingmarker = null;
        }
    }

    public void registerNavigationListener(SpreoNavigationListener listener) {
        if (!navigationListeners.contains(listener)) {
            navigationListeners.add(listener);
        }
    }

    public void unregisterNavigationListener(SpreoNavigationListener listener) {
        if (navigationListeners.contains(listener)) {
            navigationListeners.remove(listener);
        }
    }

    public void notifyOnNavigationStateChanged(NavigationState navigationState) {
        // android.util.Log.i("notifyOnNavigationStateChanged",navigationState.toString());
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
        if (instruction == null) {
            return;
        }
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
                listener.OnNavigationInstructionChanged(instruction);
            }
        }
    }

    public void notifyGoogleNavigationEnded() {
        GoogleNavigationUtil.getInstance().notifyEnd();
    }

    public void setOverlayForCampus(String campusId, BitmapDescriptor planeImage, LatLng center, float height, float width, float bearingAngle, float transparency) {

        if (campusGroundOverlay != null) {
            campusGroundOverlay.remove();
            campusGroundOverlay = null;
        }

        campusGroundOverlay = googlemap.addGroundOverlay(new GroundOverlayOptions().image(planeImage).zIndex(2f).position(center, height, width).bearing(bearingAngle).transparency(transparency));

    }

    public void setOverlayForFacility(String campusId, String facilityId, BitmapDescriptor planeImage, LatLng center, float height, float width, float bearingAngle, float transparency) {

        if (facilityGroundOverlay != null) {
            facilityGroundOverlay.remove();
            facilityGroundOverlay = null;
        }

        facilityGroundOverlay = googlemap.addGroundOverlay(new GroundOverlayOptions().image(planeImage).position(center, height, width).bearing(bearingAngle).transparency(transparency));

    }

    private void loadFacilityOverlyDefinedByUser() {
        GMapOverlyData gmFacilityOverlyData = PropertyHolder.getInstance().getGmFacilityOverlyData();
        if (gmFacilityOverlyData != null) {
            setOverlayForFacility(gmFacilityOverlyData.campusId, gmFacilityOverlyData.facilityId, gmFacilityOverlyData.planeImage, gmFacilityOverlyData.center, gmFacilityOverlyData.height,
                    gmFacilityOverlyData.width, gmFacilityOverlyData.bearingAngle, gmFacilityOverlyData.transparency);
        }
    }

    private void loadCampusOverlayDefinedByUser() {
        GMapOverlyData gmCampusOverlyData = PropertyHolder.getInstance().getGmCampusOverlyData();
        if (gmCampusOverlyData != null) {
            setOverlayForCampus(gmCampusOverlyData.campusId, gmCampusOverlyData.planeImage, gmCampusOverlyData.center, gmCampusOverlyData.height, gmCampusOverlyData.width,
                    gmCampusOverlyData.bearingAngle, gmCampusOverlyData.transparency);
        }

    }

    public void presentLocation(LatLng latlng) {
        googlemap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, defaultZoom));
    }

    private void loadCampusOverlays() {
        try {
            Map<String, Campus> campuses = ProjectConf.getInstance().getCampusesMap();
            if (campuses == null || campuses.size() == 0) {
                return;
            }

            // clean
            if (campusGroundOverlays.size() > 0) {
                for (GroundOverlay go : campusGroundOverlays) {
                    go.remove();
                    go = null;
                }
            }

            for (Campus c : campuses.values()) {

                if (c != null) {

                    List<CampusOverlay> overlays = c.getOverlaysList();
                    if (overlays != null) {
                        for (CampusOverlay o : overlays) {

                            if (o != null) {
                                BitmapDescriptor bd = o.getBitmapDescriptor();
                                if (bd != null) {
                                    try {
                                        // displayOverlayOnMap(bd, o.getCenter(),o.getHeight(),o.getWidth(),0,0.6f,o.getBounds());
                                        displayOverlayOnMap(bd, 0, 0.3f, o.getBounds());
                                    } catch (Throwable t) {
                                        t.printStackTrace();
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

    }

    // public void displayOverlayOnMap(BitmapDescriptor planeImage, LatLng corner, float height, float width, float bearingAngle, float transparency, LatLngBounds bounds) {
    public void displayOverlayOnMap(BitmapDescriptor planeImage, float bearingAngle, float transparency, LatLngBounds bounds) {
        try {

            GroundOverlay gOverlay = googlemap.addGroundOverlay(new GroundOverlayOptions().image(planeImage).zIndex(2f)
                    // .position(corner, height,width)
                    .positionFromBounds(bounds).bearing(bearingAngle)

                    .transparency(transparency));

            campusGroundOverlays.add(gOverlay);

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public void showPoi(IPoi poi) {
        if (poi != null) {
            ILocation loc = poi.getLocation();
            if (loc != null) {
                try {
                    LatLng poiloc = new LatLng(loc.getLat(), loc.getLon());
                    presentLocation(poiloc);
                    showPoiBubble(poi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void drawPois() {
//		FacilityContainer.getInstance().getSelected().setSelected(-1);
//		PoiDataHelper.getInstance().loadPois();
        removeMarkers();
        List<IPoi> poilist = PoiDataHelper.getInstance().getExternalPois();
        for (IPoi o : poilist) {
            if (o != null && o.isShowPoiOnMap()) {
                MarkerObject marker = new MarkerObject(o, ctx, googlemap, this);
                markers.put(o, marker);
                pois.put(marker.getMarker(), o);
//				Marker_Objects marker = new Marker_Objects(o, ctx,googlemap,this);// googlemap.addMarker(new MarkerOptions().position(poiloc).title(o.getpoiDescription()).icon( icon ));
            }
        }
    }

    private void removeMarkers() {
        for (MarkerObject o : markers.values()) {
            o.removeMarkerFromMap();
        }
        pois.clear();
        markers.clear();
    }

    public void showPoiBubble(IPoi poi) {
        MarkerObject marker = markers.get(poi);
        if (marker != null) {
            marker.showBaubble();
//			notifyBubbleOpened(poi);
        }
    }


    public void setOnPoiClickListener(SpreoMapViewListener listener) {
        mapviewlistener = listener;

    }

    public void notifyUserClick() {
        for (SpreoMapViewListener o : listeners) {
            try {
                if (o != null) {
                    o.onUserlocationClick();
                }
            } catch (Exception e) {
                Log.getInstance().error(TAG, e.getMessage(), e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public void onPoiClick(IPoi poi) {
        for (SpreoMapViewListener o : listeners) {
            try {
                o.onPoiClick(poi);
            } catch (Exception e) {
                Log.getInstance().error(TAG, e.getMessage(), e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
//		if (mapviewlistener != null && poi != null)
//			mapviewlistener.onPoiClick(poi);
    }

    public void onBubbleClick(IPoi poi) {
        for (SpreoMapViewListener o : listeners) {
            try {
                o.onBubbleClick(poi);
            } catch (Exception e) {
                Log.getInstance().error(TAG, e.getMessage(), e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public IPoi getPoiFromMarker(Marker mark) {
        IPoi result = pois.get(mark);
        return result;
    }

    private void notifyBubbleOpened(IPoi poi) {
        if (poi == null) {
            return;
        }
        for (SpreoMapViewListener l : listeners) {
            if (l != null) {
                try {
                    l.onBubbleOpend(poi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void notifyBubbleClosed(IPoi poi) {
        if (poi == null) {
            return;
        }
        for (SpreoMapViewListener l : listeners) {
            if (l != null) {
                try {
                    l.onBubbleClosed(poi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void closeBubble(IPoi poi) {
        if (poi == null) {
            return;
        }
        MarkerObject mark = markers.get(poi);
        if (mark != null) {
            mark.closeBubble();
//			notifyBubbleClosed(poi);
        }
    }


    public void openMyParkingMarkerBubble() {
        if (parkingmarker != null) {
            parkingmarker.showBubble();
        }
    }

    public void closeMyParkingMarkerBubble() {
        if (parkingmarker != null) {
            parkingmarker.closeBubble();
        }
    }


    public void setMyParkingBubbleText(String txt) {
        if (parkingmarker != null) {
            parkingmarker.setBubbleText(txt);
        }
    }

    public void setMyParkingMarkerIcon(Bitmap icon) {
        if (parkingmarker != null) {
            parkingmarker.setIcon(icon);
        }
    }

    public void notifyOnMyParkingMarkerClick() {
        for (SpreoMapViewListener o : listeners) {
            try {
                o.onMyParkingMarkerClick();
            } catch (Exception e) {
                Log.getInstance().error(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }

    }

    public void notifyOnMyParkingBubbleClick() {
        for (SpreoMapViewListener o : listeners) {
            try {
                if (o != null) {
                    o.onMyParkingBubbleClick();
                }
            } catch (Exception e) {
                Log.getInstance().error(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }

    private void notifyUserInfoClick() {

        for (SpreoMapViewListener o : listeners) {
            try {
                if (o != null) {
                    o.onUserLocationBubbleClick();
                }
            } catch (Exception e) {
                Log.getInstance().error(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }


    }
}
