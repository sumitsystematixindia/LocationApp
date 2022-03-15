package com.mlins.dualmap;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
//import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.android.gms.maps.model.VisibleRegion;
import com.mlins.aStar.CampusNavigationPath;
import com.mlins.aStar.FloorNavigationPath;
import com.mlins.aStar.GisSegment;
import com.mlins.aStar.NavigationPath;
import com.mlins.aStar.aStarData;
import com.mlins.aStar.aStarMath;
import com.mlins.custom.markers.CustomMarkerObject;
import com.mlins.custom.markers.CustomMarkersManager;
import com.mlins.enums.ScanMode;
import com.mlins.instructions.Instruction;
import com.mlins.instructions.InstructionBuilder;
import com.mlins.instructions.Instructionobject;
import com.mlins.instructions.NavInstruction;
import com.mlins.interfaces.ILocationMapView;
import com.mlins.labels.LabelOverlay;
import com.mlins.locationutils.LocationFinder;
import com.mlins.locator.LocationLocator;
import com.mlins.nav.utils.CustomInfoWindowAdapter;
import com.mlins.nav.utils.OrederPoisUtil;
import com.mlins.nav.utils.ParkingUtil;
import com.mlins.nav.utils.SoundPlayer;
import com.mlins.navigation.GoogleNavigationUtil;
import com.mlins.ndk.wrappers.NdkConversionUtils;
import com.mlins.ndk.wrappers.NdkLocation;
import com.mlins.objects.MultiPoiMarkerObject;
import com.mlins.objects.ParkingMarker;
import com.mlins.orientation.OrientationMonitor;
import com.mlins.overlay.CampusOverlay;
import com.mlins.overlay.FacilityOverlay;
import com.mlins.overlay.OverlayChunk;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.ui.NumberPicker;
import com.mlins.ui.NumberPicker.OnValueChangeListener;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.FloorData;
import com.mlins.utils.MathUtils;
import com.mlins.utils.Objects;
import com.mlins.utils.PoiData;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceTranslator;
import com.mlins.utils.gis.GisData;
import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.GisPoint;
import com.mlins.utils.gis.Location;
import com.spreo.enums.NavigationResultStatus;
import com.spreo.geofence.GeoFenceHelper;
import com.spreo.geofence.GeoFenceObject;
import com.spreo.interfaces.CameraChangeListener;
import com.spreo.interfaces.ICustomMarker;
import com.spreo.interfaces.MyLocationListener;
import com.spreo.interfaces.SpreoCustomMarkersListener;
import com.spreo.interfaces.SpreoDualMapViewListener;
import com.spreo.interfaces.SpreoNavigationListener;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.enums.MapRotationType;
import com.spreo.nav.enums.NavigationState;
import com.spreo.nav.enums.ProjectLocationType;
import com.spreo.nav.interfaces.ILabel;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.INavInstruction;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.sdk.data.SpreoDataProvider;
import com.spreo.spreosdk.R;
import com.spreo.ui.utils.FloorPickerUIOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import gps.CampusGisData;
import simulation.SimulationPlayer;

public class DualMapView extends FrameLayout implements MyLocationListener, ILocationMapView, GoogleMap.OnGroundOverlayClickListener, GoogleMap.OnCameraIdleListener, PoisClusterListener, NavCalculationListener {

    private static final String TAG = DualMapView.class.getName();

    private static int segmentId = 0;
    // XXX NDK LOAD LIB
    static {
        System.loadLibrary("MlinsLocationFinderUtils");
    }

    private final CameraChangeNotifier cameraChangeNotifier = new CameraChangeNotifier();

    private FloorDisplayPolicy floorDisplayPolicy;

    private Context ctx = null;
    private MapView mMapView;
    private GoogleMap googlemap;
    private Marker gmLoc = null;
    private Bitmap locbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mylocaion);
    private Bitmap parkingIcon = BitmapFactory.decodeResource(getResources(), R.drawable.parking);
    private float defaultZoom = PropertyHolder.getInstance().getDefaultDualMapZoom();
    private boolean followMeMode = true;
    private FrameLayout dummyLayoutFollowMeModeDisable;
    private NumberPicker numberPicker;
    private FrameLayout floorsControlbuttons;
    private ILocation myLocation = null;
    private RelativeLayout instructionLayout;
    private LatLng currentloc = null;
    private float currentzoom = defaultZoom;
    private ParkingMarker parkingmarker = null;
    private List<SpreoDualMapViewListener> listeners = new ArrayList<SpreoDualMapViewListener>();
    private HashMap<String, FacilityOverlay> facilityOverlays = new HashMap<String, FacilityOverlay>();
    //	private FacilityConf presentedFacility = null;
    private String locationFacility = null;
    private String[] floorstxtArr = null;
    private List<SpreoNavigationListener> navigationListeners = new ArrayList<SpreoNavigationListener>();
    private boolean isAnimatingLocation = false;
    private Map<String, List<Polyline>> facilitiesNavPaths = new HashMap<String, List<Polyline>>();
    private boolean firstLocation = true;
    private int navDistance = 1000;
    private int endGoogleNavDistance = 150;
    //	private int reachDestinationRange = 3;
//    private List<Polyline> campusPath = new ArrayList<>();
    private Polyline navarrow = null;
    private ILocation lastRawLocation = null;
    //private boolean displayPois=true;
    private boolean showAutoBubble = true;
    private int showAutoBubblesTimerInterval = 15000;
    private TextView instructionTextView;
    private ImageView instructionImageView;
    private ImageView stopNavBtn;
    private ILocation lastSimultedLcation = null;
    private int enterInstructionRange = 20;
    private IPoi autoBubblePoi = null;
    private List<CampusOverlay> campusOverlays = new ArrayList<CampusOverlay>();
    private double kmlRerouteDistance = 20;
    private double distanceFromKml = 10;
    private double endOfRouteRadius = 7;
    private Circle blePoint = null;
    private Circle rubberPoint = null;
    private int kmlRerouteCounter = 0;
    private int kmlRerouteTresh = 5;
    private IPoi poiToPresent = null;
    private boolean zoomToDefault = true;
    private ILocation locationToPresent = null;
    private long lastFollowMeTime = 0;
    private Map<ICustomMarker, CustomMarkerObject> customObjectsToMarkers = new HashMap<ICustomMarker, CustomMarkerObject>();
    private Map<Marker, ICustomMarker> customMarkersToObjects = new HashMap<Marker, ICustomMarker>();
    private List<SpreoCustomMarkersListener> customMarkerListeners = new ArrayList<SpreoCustomMarkersListener>();
    private FacilityConf floorsPickerFacility = null;
    private boolean outdoorInstructionNotified = false;
    private List<ILabel> labelOverlayList = new ArrayList<ILabel>();
    private Map<GroundOverlay, ILabel> overlayToLabelMap = new HashMap<GroundOverlay, ILabel>();
    private HashMap<IPoi, MultiPoiMarkerObject> poiToMultiPointmarker = new HashMap<IPoi, MultiPoiMarkerObject>();
    private HashMap<Marker, IPoi> multiPointmarkerToPoi = new HashMap<Marker, IPoi>();
    private float hidingPoiZoom = PropertyHolder.getInstance().getHidingPoisZoomLevel();
    private float lastZoom = 0;
    private boolean enableAutoPoiBubble = true;
    private Marker finalDestinationMarker = null;
    private Bitmap finalDestinationIcon = null;

    private boolean showDestinationMarker = false;
    private boolean showOriginMarker = false;
    private boolean showSwitchFloorMarkers = false;

    private int counterForFloorReroute = 0;
    private int counterForFacilityReroute = 0;
    private int counterForIndoorReroute = 0;
    private int counterForOutdoorReroute = 0;
    private List<Marker> campusArrows = new ArrayList<Marker>();
    private HashMap<String, List<Marker>> facilitiesArrows = new HashMap<String, List<Marker>>();
    private Polyline selectedInstructionSegment = null;
    private Polyline selectedSegmentPath = null;
    private IPoi poiToDisplay = null;
    private List<Marker> exitsMarkers = new ArrayList<Marker>();
    private HashMap<Marker, Integer> switchfloorMarkers = new HashMap<>();
    private Marker originMarker = null;
    private boolean isLocationHidden = false;

    private TileOverlay floorTileOverlay;

    private NavigationModeReRouteLogic navigationModeReRouteLogic = new NavigationModeReRouteLogic();

    private InstructionPlayerHelper soundPlayerHelper = new InstructionPlayerHelper();

    public static final int PATTERN_GAP_LENGTH_PX = 20;
    public static final int PATTERN_DASH_LENGTH_PX = 60;
    public static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem DOT = new Dot();
    public static final List<PatternItem> PATTERN_POLYLINE = Arrays.asList(GAP, DASH);
    private int animationDuration = 200;

    private List<Polygon> externalPolygons = new ArrayList<>();
    private HashMap<String, List<Polygon>> facilityPolygonsMap = new HashMap<>();
    private float polygonsMinZoomLevel = defaultZoom;

    private List<ILocation> turnBackLocationHistory = new ArrayList<>();
    private int turnBackLocationHistorySize = 4;
    private int itemsForTurnBackLocationAverage = 3;
    private Instruction instructionForTurnBack = null;

    private LatLng lastCameraLatLng = null;
    PoisClusterHelper poisClusterHelper = null;
    HashMap<Marker, IPoi> markersPoisMap = new HashMap<>();
    HashMap<IPoi, Marker> poisMarkerMap = new HashMap<>();
    private HashMap<Marker, Integer> bridgeMarkers = new HashMap<>();

    private ServerNavigationTask navigationTask = null;

    HashMap<String, Map<Integer, List<Polyline>>> invisibleFloorsRoutes = new HashMap<>();

    public static final int STATUS_SUCCEED = 200;

    private long lastReroute = 0;
    private long timeForReroute = 5000;

    private Marker facilityMarker = null;
    private Handler hideFacilityMarkerHandler;
    private HashMap<Marker, String> facilityMarkerMap = new HashMap<>();
    private float hideFacilityMarkersZoom = 16;
    private HashMap<Polyline, CampusNavigationPath> campusPathsMap = new HashMap<>();

    public void setMapType(int type) {
        try {
            if (googlemap != null) {
                googlemap.setMapType(type);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }


    private OnClickListener stopNavListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (PropertyHolder.getInstance().isLocationPlayer()) {
                stopSimulation();
            } else {
                stopNavigation();
            }
        }
    };
    private OnMapLongClickListener mapLongClicklistener = new OnMapLongClickListener() {

        @Override
        public void onMapLongClick(LatLng latlng) {
            int floor = 0;
            String facilityid = null;
            try {
                for (FacilityOverlay o : facilityOverlays.values()) {
                    if (o != null) {
                        LatLngBounds facilitybounds = o.getBounds();
                        if (facilitybounds.contains(latlng)) {
                            facilityid = o.getFacilityId();
                            floor = o.getFloor();
                            break;
                        }
                    }
                }
            } catch (Throwable t) {
                MapDebugHelper.onException(ctx,  t);
            }

            for (SpreoDualMapViewListener o : listeners) {
                try {
                    if (o != null) {
                        o.onMapLongClick(latlng, facilityid, floor);
                    }
                } catch (Throwable t) {
                    MapDebugHelper.onException(ctx,  t);
                }
            }

            ILocation location = getRealLocation(latlng);
            if (location == null) {
                location = new Location(latlng);
            }


            for (SpreoDualMapViewListener o : listeners) {
                try {
                    if (o != null) {
                        o.onMapLongClick(location);
                    }
                } catch (Throwable t) {
                    MapDebugHelper.onException(ctx,  t);
                }
            }


        }
    };
    private OnMapClickListener mapClicklistener = new OnMapClickListener() {

        @Override
        public void onMapClick(LatLng latlng) {
            int floor = 0;
            String facilityid = null;
            try {
                for (FacilityOverlay o : facilityOverlays.values()) {
                    if (o != null) {
                        LatLngBounds facilitybounds = o.getBounds();
                        if (facilitybounds.contains(latlng)) {
                            facilityid = o.getFacilityId();
                            floor = o.getFloor();
                            break;
                        }
                    }
                }
            } catch (Throwable t) {
                MapDebugHelper.onException(ctx,  t);
            }

            for (SpreoDualMapViewListener o : listeners) {
                try {
                    if (o != null) {
                        o.onMapClick(latlng, facilityid, floor);
                    }
                } catch (Throwable t) {
                    MapDebugHelper.onException(ctx,  t);
                }
            }
        }
    };
    private OnValueChangeListener numberPickerListener = new OnValueChangeListener() {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            executePicker();
        }
    };


    private boolean setPolygonVisibility(Polygon polygon) {
        boolean result = false;
        try {
            LatLngBounds latLngBounds = googlemap.getProjection().getVisibleRegion().latLngBounds;
            boolean inbounds = false;
            for (LatLng latLng : polygon.getPoints()) {
                if (latLngBounds.contains(latLng)) {
                    inbounds = true;
                    break;
                }
            }
            if (inbounds && googlemap.getCameraPosition().zoom >= polygonsMinZoomLevel) {
                polygon.setVisible(true);
            } else {
                polygon.setVisible(false);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    private void checkFacilitesPolygons() {
        try {
            for (String o : facilityPolygonsMap.keySet()) {
                if (o != null) {
                    List<Polygon> lp = facilityPolygonsMap.get(o);
                    if (lp != null) {
                        for (Polygon polygon : lp) {
                            setPolygonVisibility(polygon);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void checkExternalPolygons() {
        try {
            for (Polygon polygon : externalPolygons) {
                setPolygonVisibility(polygon);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }


    private CameraChangeListener newCameraChangeListener = new CameraChangeListener() {

        @Override
        public void onCameraChange(CameraPosition position) {
            updateLocationMarkerBearing();
        }
    };


    private OnMarkerClickListener markerClickListener = new OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {
            try {
                if (parkingmarker != null
                        && marker.equals(parkingmarker.getMarker())) {
                    notifyOnMyParkingMarkerClick();
                    return true;
                } else if (gmLoc != null && gmLoc.equals(marker)) {
                    notifyUserClick();
                    return true;
                } else {
                    IPoi poi = markersPoisMap.get(marker);
                    if (poi != null) {
                        onPoiClick(poi);
                        return true;
                    }

                    ICustomMarker cmarker = customMarkersToObjects.get(marker);
                    if (cmarker != null) {
                        notifyCustomMarkerClick(cmarker);
                        return true;
                    }


                    IPoi multiPointPoi = multiPointmarkerToPoi.get(marker);
                    if (multiPointPoi != null) {
                        onMultiPointMarkerClick(multiPointPoi);
                        return true;
                    }

                    if (PropertyHolder.getInstance().isClickableDynamicBubbles()) {
                        Integer bridgemarkerfloor = bridgeMarkers.get(marker);
                        if (bridgemarkerfloor != null) {
                            showFloorWithId(bridgemarkerfloor);
                            return true;
                        }

                        Integer switchmarkerfloor = switchfloorMarkers.get(marker);
                        if (switchmarkerfloor != null) {
                            showFloorWithId(switchmarkerfloor);
                            return true;
                        }
                    }
                }

            } catch (Throwable t) {
                MapDebugHelper.onException(ctx,  t);
            }

            return false;

        }

    };
    private OnInfoWindowClickListener infoListener = new OnInfoWindowClickListener() {

        @Override
        public void onInfoWindowClick(Marker marker) {

            try {

                IPoi poi = markersPoisMap.get(marker);
                ICustomMarker cmarker = customMarkersToObjects.get(marker);
                if (poi != null) {
                    onBubbleClick(poi);
                } else if (cmarker != null) {
                    notifyCustomMarkerBubbleClick(cmarker);
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

                    // final String fac = marker.getTitle().toLowerCase();
                    // final String id = facilityNameIdMap.get(fac);
                    // for (final SpreoMapViewListener o : listeners) {
                    // try {
                    // o.facilityClickListener(id);
                    // } catch (Exception e) {
                    // MapDebugHelper.onException(ctx,  e);
                    // }
                    // }

                }

            } catch (Throwable t) {
                MapDebugHelper.onException(ctx,  t);
            }
        }

    };
    private OnTouchListener dummyTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!PropertyHolder.getInstance().isLocationPlayer()) {
                setFollowMeFalse();
            }
            return false;
        }
    };
    private OnMapReadyCallback mapCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap gm) {
            googlemap = gm;

            try {
                // Register a listener to respond to clicks on GroundOverlays.
                googlemap.setOnGroundOverlayClickListener(DualMapView.this);
                googlemap.setMapStyle(MapStyleOptions.loadRawResourceStyle(ctx, R.raw.style_json));
                Campus campus = ProjectConf.getInstance().getSelectedCampus();
                if (campus != null) {
                    LatLng defaultloc = campus.getDefaultLatlng();
                    if (defaultloc != null) {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(defaultloc, defaultZoom);
                        googlemap.moveCamera(cameraUpdate);
                    }

                    if(!campus.isUsingFloorTiles())
                        addTileOverlay(new SpreoTileProvider(), 1);
                }
            } catch (Throwable t) {
                MapDebugHelper.onException(ctx,  t);
            }
            continueInit();
        }
    };

    public DualMapView(Context context) {
        this(context, null);
    }


    public DualMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public DualMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // can't call this(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) beacause we don't have it on API < 21
        // so creating layouts from both constructors
        createLayout(context);
        int blueDotResID = R.drawable.mylocaion;
        if (PropertyHolder.getInstance().isUseRotatingUserIcon() && PropertyHolder.getInstance().getRotatingMapType() != MapRotationType.COMPASS) {
            blueDotResID = R.drawable.mylocation_bearing;
        }
        locbitmap = BitmapFactory.decodeResource(getResources(), blueDotResID);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DualMapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        createLayout(context);
    }

    private void createLayout(Context context) {
        ctx = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.dual_map_view, this, true);
    }

    public void init(Bundle savedInstanceState) {

        floorsControlbuttons = (FrameLayout) findViewById(R.id.floorsControlbuttons);
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setOnValueChangedListener(numberPickerListener);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        instructionLayout = (RelativeLayout) findViewById(R.id.instruction_layout);
        instructionTextView = (TextView) findViewById(R.id.instructiontText);
        instructionImageView = (ImageView) findViewById(R.id.instructioniSign);
        stopNavBtn = (ImageView) findViewById(R.id.stopNavBtn);
        stopNavBtn.setOnClickListener(stopNavListener);

        SoundPlayer.getInstance().setContext(ctx);

        try {
            MapsInitializer.initialize(ctx);
        } catch (Throwable e) {
            MapDebugHelper.onException(ctx,  e);
        }

        mMapView = (MapView) findViewById(R.id.googleMap);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(mapCallback);


    }

    protected void continueInit() {
        try {

            poisClusterHelper = new PoisClusterHelper(ctx, this, googlemap.getMaxZoomLevel());

            int maptype = PropertyHolder.getInstance().getMapType();
            googlemap.setMapType(maptype);
            googlemap.getUiSettings().setZoomControlsEnabled(false);
            googlemap.setIndoorEnabled(false);
            googlemap.getUiSettings().setCompassEnabled(false);
            dummyLayoutFollowMeModeDisable = (FrameLayout) findViewById(R.id.dummyLayoutFollowMeModeDisable);
            dummyLayoutFollowMeModeDisable.setOnTouchListener(dummyTouchListener);
            googlemap.setOnInfoWindowClickListener(infoListener);
            googlemap.setOnMarkerClickListener(markerClickListener);
            googlemap.setOnMapClickListener(mapClicklistener);
            googlemap.setOnMapLongClickListener(mapLongClicklistener);
            googlemap.setBuildingsEnabled(PropertyHolder.getInstance().isDrawGoogleMapsBuildings());

            float maxzoom = PropertyHolder.getInstance().getMapMaxZoomLimit();
            if (maxzoom != -1) {
                googlemap.setMaxZoomPreference(maxzoom);
            }

            cameraChangeNotifier.setup(googlemap);
            cameraChangeNotifier.addCameraChangeListener(newCameraChangeListener);

            floorDisplayPolicy = new FloorDisplayPolicy(
                    ProjectConf.getInstance().getSelectedCampus()
            );

            loadCampusOverlay();
            drawExternalPolygons();
            loadoverlays();

            if (!LocationFinder.getInstance().isStarted()) {
                LocationFinder.getInstance()
                        .startLocationService(ctx, ScanMode.BLE);
            }
            LocationFinder.getInstance().cleanMapViewListeners();
            LocationFinder.getInstance().subscribeForLocation(this);

            loadParkingLocation();
            initParkingLocation();

            if (PropertyHolder.getInstance().isDevelopmentMode()) {
                drawKml();
            }

            DrawCustomMarkers();

            Campus campus = ProjectConf.getInstance().getSelectedCampus();
            if (campus != null) {
                navDistance = campus.getRadius();
                kmlRerouteDistance = campus.getReroute_min_distance();
                distanceFromKml = campus.getDistance_from_kml();
                endOfRouteRadius = campus.getEnd_of_route_radius();
            }

            if (poiToPresent != null) {
                showPoi(poiToPresent, zoomToDefault);
                poiToPresent = null;
                zoomToDefault = true;
            } else if (locationToPresent != null) {
                presentLocation(locationToPresent);
                locationToPresent = null;
            }

            googlemap.setOnCameraIdleListener(this);

            notifyMapDidLoad();

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private void executePicker() {
        try {
            int newval = numberPicker.getValue();
            Campus campus = ProjectConf.getInstance().getSelectedCampus();
            if (campus != null && floorstxtArr != null) {
                int floor = floorstxtArr.length - 1 - newval;
                loadOverlayesFloor(floor);
                if (lastRawLocation != null) {
                    if (lastRawLocation.getZ() != floor) {
                        setFollowMeFalse();
                    } else {
//						followMeMode = true;
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void notifyZoomChange(float zoom) {
        for (SpreoDualMapViewListener o : listeners) {
            try {
                if (o != null) {
                    o.onZoomChange(zoom);
                }
            } catch (Throwable e) {
                MapDebugHelper.onException(ctx,  e);
            }
        }
    }

    /**
     * Gets the current position of the camera.
     * @return The current position of the Camera (snapshot).
     */
    public CameraPosition getCameraPosition(){
        checkMapReady();
        return googlemap.getCameraPosition();
    }

    private void checkMapReady(){
        if(googlemap == null)
            throw new IllegalStateException("Map is not ready yet");
    }

    /**
     * Deprecated. Use getCameraPosition().zoom instead.
     * @return map zoom level
     */
    public float getZoom() {
        float result = 0;
        try {
            result = googlemap.getCameraPosition().zoom;
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    /**
     * Deprecated. Use getCameraPosition().target instead.
     * @return The location that the camera is pointing at.
     */
    public LatLng getCenterPoint() {
        LatLng result = null;
        try {
            result = googlemap.getCameraPosition().target;
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    private void loadoverlays() {
        try {

            FacilityConf facility = getFloorsPickerFacility();
            if (facility != null) {
                floorsPickerFacility = facility;
                int z = facility.getEntranceFloor();
                floorDisplayPolicy.setCurrentFloor(z);

                drawProjectPOis();

                Campus c = ProjectConf.getInstance().getSelectedCampus();
                drawGoogleMapsTiles(c, z);
                if (c != null) {
                    Map<String, FacilityConf> facilitiesmap = c.getFacilitiesConfMap();
                    for (FacilityConf o : facilitiesmap.values()) {
                        if (o != null) {
                            loadOverlay(o, c, z);
                        }
                    }
                    createFloorsPicker(facility);
                }
            }

            if (PropertyHolder.getInstance().isDrawInvisibleFloorsRoute()) {
                List<CampusNavigationPath> navpath = RouteCalculationHelper.getInstance().getOutdoorNavPath();
                if (navpath != null && !navpath.isEmpty()) {
                    updatePath(navpath);
                }
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private void loadOverlay(FacilityConf fac, Campus campus, int floor) {
        try {
            int numberoffloors = fac.getFloorDataList().size();
            int maxFloor = numberoffloors - 1;

            boolean removeOrDisplayLastFloor = !floorDisplayPolicy.shouldDisplayTopFloorContent() && floor >= maxFloor ;

            FacilityOverlay overlay = facilityOverlays.get(fac.getId());

            if(floor > maxFloor)
                floor = maxFloor;

            boolean loadingNewFloor = (overlay == null) || overlay.getFloor() != floor;

            boolean needRedraw = loadingNewFloor || removeOrDisplayLastFloor;

            if (overlay == null) {

				overlay = new FacilityOverlay(fac.getId(),
                        fac.getConvRectTLlat(), fac.getConvRectTLlon(),
                        fac.getConvRectTRlat(), fac.getConvRectTRlon(),
                        fac.getConvRectBRlat(), fac.getConvRectBRlon(),
                        fac.getConvRectBLlat(), fac.getConvRectBLlon());

                facilityOverlays.put(fac.getId(), overlay);
            }

            if (needRedraw) {
                float bearing = -fac.getRot_angle();
                overlay.setFloor(floor);

                drawTiles(campus, overlay, floor, bearing);
                drawFloorPolygons(fac.getId(), floor);
                drawLabelOverlays(campus, fac, floor);
                drawMultiPoiPoints(fac.getId(), floor);

                if (PropertyHolder.getInstance().isNavigationState()) {
                    setFloorNavPath(fac.getId());
                }

                DrawCustomMarkers();
            }

            if(loadingNewFloor) { //this check prevents infinite loop when someone calls presentLocation/setFloor inside mapDidLoad
                notifyFloorLoad(campus.getId(), fac.getId(), floor);
            }

            if (parkingmarker != null) {
                LocationMode locmode = parkingmarker.getLocationMode();
                if (locmode != null) {
                    if (locmode == LocationMode.INDOOR_MODE) {
                        setIndoorParkingVisibility();
                    }
                }
            }

            updateNavigationMarkerState();

            System.gc();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    public GroundOverlay drawOverlayOnMap(BitmapDescriptor planeImage, float bearingAngle, float transparency, LatLngBounds bounds) {

        GroundOverlay gOverlay = null;
        try {
            gOverlay = googlemap
                    .addGroundOverlay(new GroundOverlayOptions()
                            .image(planeImage).zIndex(2f)
                            .positionFromBounds(bounds).bearing(bearingAngle)
                            .transparency(transparency));

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        return gOverlay;
    }

    private void drawGoogleMapsTiles(Campus campus, int floor){
        if(floorTileOverlay != null)
            floorTileOverlay.remove();


        if (campus.isUsingFloorTiles()) {
            floorTileOverlay = addTileOverlay(new SpreoTileProvider(floor), 2);
        }

    }

    private void drawTiles(Campus campus, FacilityOverlay fo, int floor, float bearing) {
        fo.removeTiles();

        if (!campus.isUsingFloorTiles()) {
        	if(floorDisplayPolicy.displayFloorContent(fo.getFacilityId(), floor)){
				try {
					List<OverlayChunk> bmList = fo.getChunkedImages();

					for (OverlayChunk oc : bmList) {
						GroundOverlay gOverlay = drawOverlayOnMap(oc.getBd(), bearing, 0, oc.getBound());
						fo.addGroundOverlay(gOverlay);
					}
				} catch (Throwable t) {
					MapDebugHelper.onException(ctx,  t);
				}
			}
        }
    }

    private TileOverlay addTileOverlay(TileProvider tileProvider, int zIndex) {
        return googlemap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(1));
    }

    public void displayOverlayOnMap(BitmapDescriptor planeImage, float bearingAngle, float transparency, LatLngBounds bounds, FacilityOverlay fo) {
        try {

            GroundOverlay gOverlay = googlemap
                    .addGroundOverlay(new GroundOverlayOptions()
                            .image(planeImage).zIndex(2f)
                            .positionFromBounds(bounds).bearing(bearingAngle)
                            .transparency(transparency));

            fo.setOverlay(gOverlay);

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private void onMultiPointMarkerClick(IPoi poi) {
        for (SpreoDualMapViewListener o : listeners) {
            try {
                if (o != null) {
                    o.onMultipointClick(poi);
                }
            } catch (Throwable e) {
                MapDebugHelper.onException(ctx,  e);
            }
        }
    }

    private void loadKdTreePois(String facilityId, int floor) {
        try {
            if (facilityId != null) {
                Campus campus = ProjectConf.getInstance().getSelectedCampus();
                if (campus != null) {
                    String campusId = campus.getId();
                    if (campusId != null) {
                        List<IPoi> poilist = ProjectConf.getInstance().getAllFloorPoisList(campusId, facilityId, floor);
                        if (poilist != null) {
                            DualMapPoisUtil.getInstance().loadPoisKDimensionalTree(
                                    poilist);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void onBubbleClick(IPoi poi) {
        for (SpreoDualMapViewListener o : listeners) {
            try {
                if (o != null) {
                    o.onBubbleClick(poi);
                }
            } catch (Throwable e) {
                MapDebugHelper.onException(ctx,  e);
            }
        }
    }

    private void notifyUserInfoClick() {

        for (SpreoDualMapViewListener o : listeners) {
            try {
                if (o != null) {
                    o.onUserLocationBubbleClick();
                }
            } catch (Throwable e) {
                MapDebugHelper.onException(ctx,  e);
            }
        }

    }

    public void notifyOnMyParkingBubbleClick() {
        for (SpreoDualMapViewListener o : listeners) {
            try {
                if (o != null) {
                    o.onMyParkingBubbleClick();
                }
            } catch (Throwable e) {
                MapDebugHelper.onException(ctx,  e);
            }
        }
    }

    public void updateBearing(float bearing) {

        try {
            if (followMeMode && myLocation != null) {
                currentloc = new LatLng(myLocation.getLat(), myLocation.getLon());
                currentzoom = googlemap.getCameraPosition().zoom; // defaultZoom;
            } else {
                currentloc = googlemap.getCameraPosition().target;
                currentzoom = googlemap.getCameraPosition().zoom;
            }
            CameraPosition currentPlace = new CameraPosition.Builder()
                    .target(new LatLng(currentloc.latitude, currentloc.longitude))
                    .bearing(bearing).zoom(currentzoom).build();
            Log.d("Bearing", "Map bearing: " + bearing);
            if (PropertyHolder.getInstance().isMapAnimation()) {
                googlemap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(currentPlace), animationDuration,
                        new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            } else {
                googlemap.moveCamera(CameraUpdateFactory
                        .newCameraPosition(currentPlace));
            }

            updateLabelOverlaysBearing(bearing);
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    public void notifyOnMyParkingMarkerClick() {
        for (SpreoDualMapViewListener o : listeners) {
            try {
                o.onMyParkingMarkerClick();
            } catch (Throwable e) {
                MapDebugHelper.onException(ctx,  e);
            }
        }

    }

    public void notifyUserClick() {
        for (SpreoDualMapViewListener o : listeners) {
            try {
                if (o != null) {
                    o.onUserlocationClick();
                }
            } catch (Throwable e) {
                MapDebugHelper.onException(ctx,  e);
            }
        }
    }

    public void onPoiClick(IPoi poi) {
        for (SpreoDualMapViewListener o : listeners) {
            try {
                o.onPoiClick(poi);
            } catch (Throwable e) {
                MapDebugHelper.onException(ctx,  e);
            }
        }
    }

    public void openPoiBubble(IPoi poi) {
        openPoiBubble(poi, false);
    }

    private void openPoiBubble(IPoi poi, boolean isAutoBubble) {
        try {

            if (poi != null) {
                Marker marker = poisMarkerMap.get(poi);
                if (marker != null) {
                    if (!isAutoBubble) {
                        setShowAutoBubblesTimer();
                    }
                    View customview = null;
                    for (SpreoDualMapViewListener o : listeners) {
                        if (o != null) {
                            try {
                                View v = o.aboutToOpenBubble(poi);
                                if (v != null) {
                                    customview = v;
                                }
                            } catch (Exception e) {
                                MapDebugHelper.onException(ctx,  e);
                            }
                        }
                    }

                    if (customview != null) {
                        googlemap.setInfoWindowAdapter(new CustomInfoWindowAdapter(customview, marker));
                    }
                    marker.showInfoWindow();

                    //			notifyBubbleOpened(poi);
                }
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private void setShowAutoBubblesTimer() {
        showAutoBubble = false;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    try {
                        showAutoBubble = true;
                    } catch (Exception e) {
                        MapDebugHelper.onException(ctx,  e);
                    }
                }

            };

            @Override
            public void run() {
                post(r);
            }
        }, showAutoBubblesTimerInterval);
    }

    public void mapZoomIn() {
        try {
            float zoomin = googlemap.getCameraPosition().zoom + 1;
            if (PropertyHolder.getInstance().isMapAnimation()) {
                googlemap.animateCamera(CameraUpdateFactory.zoomTo(zoomin), animationDuration,
                        new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            } else {
                googlemap.moveCamera(CameraUpdateFactory.zoomTo(zoomin));
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void mapZoomOut() {
        try {
            float zoomin = googlemap.getCameraPosition().zoom - 1;
            if (PropertyHolder.getInstance().isMapAnimation()) {
                googlemap.animateCamera(CameraUpdateFactory.zoomTo(zoomin), animationDuration,
                        new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            } else {
                googlemap.moveCamera(CameraUpdateFactory.zoomTo(zoomin));
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void showMyLocation() {
        try {
            if (myLocation != null) {
                LatLng latlng = new LatLng(myLocation.getLat(), myLocation.getLon());
                if (PropertyHolder.getInstance().isMapAnimation()) {
                    googlemap.animateCamera(CameraUpdateFactory.newLatLng(latlng), animationDuration,
                            new GoogleMap.CancelableCallback() {
                                @Override
                                public void onFinish() {

                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                } else {
                    googlemap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                }
            }
            followMeMode = true;
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    public Bitmap getUserIcon() {
        return locbitmap;
    }

    public void setUserIcon(Bitmap usericon) {
        try {
            this.locbitmap = usericon;
            if (gmLoc != null) {
                BitmapDescriptor locicon = BitmapDescriptorFactory
                        .fromBitmap(locbitmap);
                gmLoc.setIcon(locicon);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void initParkingLocation() {
        try {
            ILocation ploc = ParkingUtil.getInstance().getParkingLocation();
            if (ploc != null) {
                ILocation convertedploc = null;
                if (ploc.getLocationType() == LocationMode.INDOOR_MODE) {
                    convertedploc = getIndoorConvertedLocation(ploc);
                } else {
                    convertedploc = ploc;
                }
                if (convertedploc != null) {
//					LatLng parkingloc = new LatLng(convertedploc.getLat(),
//							convertedploc.getLon());
                    updateParkingMarker(convertedploc);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void setCurrentLocationAsParking() {
        try {
            ILocation loc = LocationFinder.getInstance().getCurrentLocation();
            if (loc != null) {
                setLocationAsParking(loc);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void setLocationAsParking(ILocation location) {
        try {
            if (location != null) {
                Location loc = new Location(location);
                ParkingUtil.getInstance().save(loc);
                ILocation convertedloc = null;
                if (loc.getLocationType() == LocationMode.INDOOR_MODE) {
                    convertedloc = getIndoorConvertedLocation(loc);
                } else {
                    convertedloc = loc;
                }
                if (convertedloc != null) {
//					LatLng parklatlng = new LatLng(convertedloc.getLat(),
//							convertedloc.getLon());
                    updateParkingMarker(convertedloc);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void updateParkingMarker(ILocation parkingloc) {
        try {
            if (parkingloc != null) {
                if (parkingmarker != null) {
                    parkingmarker.setPosition(parkingloc);
                } else {
                    parkingmarker = new ParkingMarker(parkingloc, null, ctx, googlemap, parkingIcon);
                }

                LocationMode locmode = parkingmarker.getLocationMode();
                if (locmode != null) {
                    if (locmode == LocationMode.OUTDOOR_MODE) {
                        parkingmarker.setVisibility(true);
                    } else if (locmode == LocationMode.INDOOR_MODE) {
                        setIndoorParkingVisibility();
                    }
                }

            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void setIndoorParkingVisibility() {
        if (parkingmarker != null) {
            parkingmarker.setVisibility(
            		isContentVisible(parkingmarker.getFacilityID(), parkingmarker.getFloor())
			);
        }
    }

    public void removeParkingLocation() {
        try {

            ParkingUtil.getInstance().delete();
            if (parkingmarker != null) {
                parkingmarker.remove();
                parkingmarker = null;
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private FacilityConf getPresentedFacility() {

        FacilityConf result = null;
        try {

            Projection p = googlemap.getProjection();
            VisibleRegion vr = p.getVisibleRegion();
            LatLngBounds visiblebounds = vr.latLngBounds;
            LatLng visiblecenter = visiblebounds.getCenter();
            List<FacilityOverlay> visiblefacilities = new ArrayList<FacilityOverlay>();
            for (FacilityOverlay o : facilityOverlays.values()) {
                LatLngBounds facilitybounds = o.getBounds();
                if (intersects(visiblebounds, facilitybounds)) {
                    visiblefacilities.add(o);
                }

            }

            String presentedfacilityid = null;

            if (visiblefacilities.size() > 1) {
                presentedfacilityid = getFacilityClosestToCenter(visiblefacilities,
                        visiblecenter);
            } else if (visiblefacilities.size() > 0) {
                presentedfacilityid = visiblefacilities.get(0).getFacilityId();
            }

            if (presentedfacilityid != null) {
                Campus campus = ProjectConf.getInstance().getSelectedCampus();
                if (campus != null) {
                    result = campus.getFacilityConf(presentedfacilityid);
                }
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    private String getFacilityClosestToCenter(
            List<FacilityOverlay> visiblefacilities, LatLng visiblecenter) {

        String result = null;

        try {

            double min = Double.MAX_VALUE;
            for (FacilityOverlay o : visiblefacilities) {
                LatLng facilitycenter = o.getCenter();
                double d = MathUtils.distance(visiblecenter, facilitycenter);
                if (d < min) {
                    result = o.getFacilityId();
                    min = d;
                }
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        return result;
    }

    private boolean intersects(LatLngBounds lb1, LatLngBounds lb2) {
        boolean latIntersects = (lb2.northeast.latitude >= lb1.southwest.latitude)
                && (lb2.southwest.latitude <= lb1.northeast.latitude);
        boolean lngIntersects = (lb2.northeast.longitude >= lb1.southwest.longitude)
                && (lb2.southwest.longitude <= lb1.northeast.longitude);

        return latIntersects && lngIntersects;
    }

    public void onCreate() {
        mMapView = (MapView) findViewById(R.id.googleMap);
        mMapView.onCreate(null);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googlemap = googleMap;
                Campus campus = ProjectConf.getInstance().getSelectedCampus();
                if (campus != null) {
                    LatLng defaultloc = campus.getDefaultLatlng();
                    if (defaultloc != null) {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(defaultloc, defaultZoom);
                        googleMap.moveCamera(cameraUpdate);
                    }
                }
            }
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        init(savedInstanceState);
    }

    public void onResume() {
        mMapView.onResume();
    }

    public void onDestroy() {
        LocationFinder.getInstance().unsubscibeForLocation(this);

        try {
            if (navigationTask != null) {
                navigationTask.cancel(true);
                navigationTask = null;
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        try {
            for (CampusOverlay co : campusOverlays) {
                if (co != null) {
                    co.removeTiles();
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        try {
            for (FacilityOverlay o1 : facilityOverlays.values()) {
                if (o1 != null) {
                    o1.removeTiles();
                }
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        try {
            cleanLabelsOvelay();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        try {
            removeCustomMarkers();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        try {
            removeMultiPoiPoints();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        try {
            removeNavigationPath();
            removeAllFacilityPath();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        removeAllPolygons();

        try {
            removePoiMarkers();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        try {
           removeFacilityMarkers();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        try {
            for (Marker o : bridgeMarkers.keySet()) {
                o.remove();
            }
            bridgeMarkers.clear();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        try {
            for (Marker o : exitsMarkers) {
                o.remove();
            }
            exitsMarkers.clear();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        try {
            for (Marker o : switchfloorMarkers.keySet()) {
                o.remove();
            }
            switchfloorMarkers.clear();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        try {
            if (hideFacilityMarkerHandler != null) {
                hideFacilityMarkerHandler.removeCallbacks(mHideFacilityMarker);
            }
            if (facilityMarker != null) {
                facilityMarker.remove();
                facilityMarker = null;
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        poisClusterHelper.unregisterListener();

        try {


            for (FacilityOverlay o : facilityOverlays.values()) {
                if (o.getOverlay() != null) {
                    GroundOverlay go = o.getOverlay();
                    go.remove();

                    go = null;
                }
            }
            System.gc();
            facilityOverlays.clear();
            googlemap.clear();

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        mMapView.onDestroy();
    }

    public void onPause() {
        mMapView.onPause();
    }

    public void onLowMemory() {
        mMapView.onLowMemory();
        System.gc();
    }

    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationDelivered(ILocation location) {

        try {

            if (PropertyHolder.getInstance().isUseTurnBackInstruction()) {
                addToTurnBackLocationHistory(location);
            }

            checkFloorPickerVisibility();

            if (location == null) {
                return;
            }

            checkFollowMeMode();

            long interval = PropertyHolder.getInstance().getUserAutoFollowTimeInterval();
            if (interval != -1 && lastRawLocation != null &&  lastRawLocation.getZ() != location.getZ()) {
                followMeMode = true;
            }

            if (location.getLocationType() == LocationMode.INDOOR_MODE) {
                String facid = location.getFacilityId();
                int z = (int) location.getZ();
                if (facid != null) {
                    if (followMeMode) {
                        setFloor(z);
                    }

                    if (locationFacility == null) {
                        locationFacility = facid;
                        enterFacility(location, facid);
                    } else if (!facid.equals(locationFacility)) {
//						setEntranceFloor(locationFacility);
                        locationFacility = facid;
                        enterFacility(location, facid);
                    }

//					if (PropertyHolder.getInstance().isDevelopmentMode()) {
//						drawDebugPoints(facid);
//					}
                }
                chekLocationFloorChange(location);
                openClosePoiBubble(location);
                myLocation = getConvertedLocation(location);
                checkObserverMode(location);
            } else {
                if (locationFacility != null) {
                    exitFacility(location, locationFacility);
                    locationFacility = null;
                }
                if (followMeMode) {
                    setEntranceFloor();
                }
                myLocation = location;
                openClosePoiBubble(location);
                startOutDoorNavigationThread(myLocation);
            }

            lastRawLocation = location;

            if (PropertyHolder.getInstance().isNavigationState()) {
                navigationRoutine(location);
            }

            if (myLocation == null) {
                return;
            }

            updateLocationMarker(myLocation);
            navigationModeReRouteLogic.checkReRoute();

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private void updateLocationMarker(ILocation loc) {
        try {
            LatLng latlng = new LatLng(loc.getLat(), loc.getLon());
            if (gmLoc != null) {
//				gmLoc.setPosition(latlng);
                animateMarkerTo(gmLoc, latlng);
            } else {

                BitmapDescriptor locicon = BitmapDescriptorFactory
                        .fromBitmap(locbitmap);
                gmLoc = googlemap.addMarker(new MarkerOptions().position(latlng)
                        .icon(locicon).anchor(0.5f, 0.5f).zIndex(10));
                if (PropertyHolder.getInstance().getProjectLocationType() == ProjectLocationType.NO_LOCATION || isLocationHidden == true ) {
                    gmLoc.setVisible(false);
                }
            }

            updateLocationMarkerBearing();

            MapRotationType mapRotationType = PropertyHolder.getInstance().getRotatingMapType();

            //PropertyHolder.getInstance().setRotatingMapType( MapRotationType.NAVIGATION);
            if (followMeMode) {
                if (!isAnimatingLocation) {
                    if (firstLocation) {
                        try {
                            if (PropertyHolder.getInstance().isMapAnimation()) {
                                isAnimatingLocation = true;
                                googlemap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        latlng, defaultZoom), animationDuration,
                                        new GoogleMap.CancelableCallback() {
                                            @Override
                                            public void onFinish() {
                                                isAnimatingLocation = false;
                                                if (googlemap.getCameraPosition().zoom == defaultZoom) {
                                                    firstLocation = false;
                                                }
                                            }

                                            @Override
                                            public void onCancel() {
                                                isAnimatingLocation = false;
                                                if (googlemap.getCameraPosition().zoom == defaultZoom) {
                                                    firstLocation = false;
                                                }
                                            }
                                        });
                            } else {
                                googlemap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        latlng, defaultZoom));
                                if (googlemap.getCameraPosition().zoom == defaultZoom) {
                                    firstLocation = false;
                                }
                            }
                        } catch (Throwable t) {
                            MapDebugHelper.onException(ctx,  t);
                        }

                    } else if (mapRotationType != MapRotationType.STATIC) {

                        if (PropertyHolder.getInstance().isLocationPlayer()) {
                            if (lastSimultedLcation != null && !isTheSameLatLng(lastSimultedLcation, loc)) {
                                float bearing = (float) getBearing(lastSimultedLcation, loc);
//									if(PropertyHolder.getInstance().getRotatingMapType() == MapRotationType.NAVIGATION && isInIndoorNavigationState(lastRawLocation)){
//										bearing =  (float) getInNavigationStateBearing(lastRawLocation);
//									}
                                updateBearing(bearing);
                            }
                            lastSimultedLcation = loc;
                        } else {
                            float bearing = 0; //oriented to NORTH when MapRotationType == NAVIGATION but navigation is not started
                            if(mapRotationType == MapRotationType.COMPASS)
                                bearing = OrientationMonitor.getInstance().getAzimuth();
                            else if (mapRotationType == MapRotationType.NAVIGATION && PropertyHolder.getInstance().isNavigationState()) {
                                bearing = Location.isInDoor(lastRawLocation) ? (float) getInNavigationStateBearing(lastRawLocation) : OrientationMonitor.getInstance().getAzimuth();
                                if(PropertyHolder.getInstance().useStaticBlueDotInNavigation()) {
                                    if(gmLoc != null) {
                                        Log.d("Bearing", "Bearing from updateLocationMarker (new version, flat marker): " + bearing);
                                        gmLoc.setRotation(bearing);
                                        gmLoc.setFlat(true);
                                    }
                                }
                            }
                            updateBearing(bearing);
                        }

                    } else {
                        if (PropertyHolder.getInstance().isMapAnimation()) {
                            isAnimatingLocation = true;
                            googlemap.animateCamera(
                                    CameraUpdateFactory.newLatLng(latlng), animationDuration,
                                    new GoogleMap.CancelableCallback() {
                                        @Override
                                        public void onFinish() {
                                            isAnimatingLocation = false;
                                        }

                                        @Override
                                        public void onCancel() {
                                            isAnimatingLocation = false;
                                        }
                                    });
                        } else {
                            googlemap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                        }
                    }

                }

            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private boolean isInIndoorNavigationState(ILocation myloc) {
        boolean result = false;
        if (myloc != null) {
            String facility = myloc.getFacilityId();
            if (facility != null) {
                List<Polyline> polylines = facilitiesNavPaths.get(facility);
                if (polylines != null && !polylines.isEmpty()) {
                    ILocation navOrigin = RouteCalculationHelper.getInstance().getOrigin();
                    ILocation convNavOrigin = getConvertedLocation(navOrigin);
                    LatLng originLatLng = new LatLng(convNavOrigin.getLat(), convNavOrigin.getLon());

                    ILocation convMyloc = getConvertedLocation(myloc);
                    LatLng myLocLatLng = new LatLng(convMyloc.getLat(), convMyloc.getLon());

                    double dist = MathUtils.distance(myLocLatLng, originLatLng);
                    if (dist > PropertyHolder.getInstance().getDistanceToNavOrigin()) {
                        result = true;
                    }

                }
            }
        }
        return result;
    }

    private double getInNavigationStateBearing(ILocation myloc) {
        double result = 0;
        boolean isComputeBearing = false;

        if (myloc != null) {
            String facility = myloc.getFacilityId();
            if (facility != null) {

                NavigationPath path = RouteCalculationHelper.getInstance().getFacilityPath(facility);
                if (path != null) {
                    String pathfacilityid = path.getFacilityId();
                    if (facility.equals(pathfacilityid)) {

                        List<List<GisSegment>> paths = getPresentedFloorPath(path, facility);
                        List<GisSegment> fpath = new ArrayList<>();
                        for (List<GisSegment> l : paths) {
                            fpath.addAll(l);
                        }

                        GisPoint pLoc = new GisPoint((Location) myloc);
                        pLoc.setZ(myloc.getZ());


                        GisSegment cl = aStarMath.findCloseSegment(pLoc, fpath);
                        GisLine closestLine = cl.getLine();

                        // initial state
                        if (RouteCalculationHelper.getInstance().getCurrentClosestNavLine() == null) {
                            isComputeBearing = true;
                            RouteCalculationHelper.getInstance().setOnSameNavLineCounter(0);
                        } else {
                            // update counter
                            GisLine lastLine = RouteCalculationHelper.getInstance().getCurrentClosestNavLine();
                            if (lastLine.point2.getX() == closestLine.point2.getX()
                                    && lastLine.point2.getY() == closestLine.point2.getY()) {
                                int tmp = RouteCalculationHelper.getInstance().getOnSameNavLineCounter();
                                RouteCalculationHelper.getInstance().setOnSameNavLineCounter(tmp + 1);
                            }
                        }

                        if (RouteCalculationHelper.getInstance().getOnSameNavLineCounter() >= PropertyHolder.getInstance().getOnSameNavLineContThreshold()) {
                            //Toast.makeText(ctx, "2", Toast.LENGTH_SHORT).show();
                            RouteCalculationHelper.getInstance().setOnSameNavLineCounter(0);
                            isComputeBearing = true;
                        }

                        // update state
                        RouteCalculationHelper.getInstance().setCurrentClosestNavLine(closestLine);
                        result = RouteCalculationHelper.getInstance().getLastBearing();

                        if (isComputeBearing) {
                            ILocation startL = new Location();
                            startL.setX(closestLine.point1.getX());
                            startL.setY(closestLine.point1.getY());
                            startL.setZ((int) myloc.getZ());

                            startL.setCampusId(myloc.getCampusId());
                            startL.setFacilityId(facility);
                            ILocation convStartL = getConvertedLocation(startL);
                            ILocation endL = new Location();
                            endL.setX(closestLine.point2.getX());
                            endL.setY(closestLine.point2.getY());
                            endL.setZ((int) myloc.getZ());
                            endL.setCampusId(myloc.getCampusId());
                            endL.setFacilityId(facility);
                            ILocation convEndL = getConvertedLocation(endL);

                            result = getBearing(convStartL, convEndL);
                            RouteCalculationHelper.getInstance().setLastBearing(result);
                        }
                    }
                }



            }
        }
        return result;
    }


    private boolean isTheSameLatLng(ILocation loc1, ILocation loc2) {
        boolean result = false;
        try {
            if (loc1.getLat() == loc2.getLat() && loc1.getLon() == loc2.getLon()) {
                result = true;
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    private double getBearing(ILocation loc1, ILocation loc2) {
        LatLng from = new LatLng(loc1.getLat(), loc1.getLon());
        LatLng to = new LatLng(loc2.getLat(), loc2.getLon());
        return MathUtils.computeHeading(from, to);
    }

    private void checkObserverMode(ILocation location) {
        try {
            boolean visible = isLocationVisible(location);
            if (PropertyHolder.getInstance().getProjectLocationType() == ProjectLocationType.NO_LOCATION || isLocationHidden == true) {
                visible = false;
            }
            if (gmLoc != null) {
                gmLoc.setVisible(visible);
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void navigationRoutine(ILocation location) {

        try {

            if (location != null) {
                if (location.getLocationType() == LocationMode.INDOOR_MODE) {
                    PointF p = new PointF((float) location.getX(), (float) location.getY());
                    int z = (int) location.getZ();
                    indoorNavigationRutin(p, z, location);
                } else if (location.getLocationType() == LocationMode.OUTDOOR_MODE) {

                }
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private void indoorNavigationRutin(PointF p, int z, ILocation location) {


        try {
            recalculatepath(p, z);
            Instruction instruction = null;
            Instruction closeswitchinstruction = InstructionBuilder.getInstance().findCloseSwitchFloorInstruction(z, p);
            if (closeswitchinstruction != null) {
                if (!PropertyHolder.getInstance().isSimplifiedInstruction()) {
                    PointF pointforplaying = new PointF((float) closeswitchinstruction.getLocation().getX(), (float) closeswitchinstruction.getLocation().getY());
                    playSwitchInstruction(closeswitchinstruction, pointforplaying);
                }
            } else {
                instruction = InstructionBuilder.getInstance().findCloseInstruction(p);
                if (instruction != null) {
                    updateInstruction(instruction, p);
                }
            }

            checkIndoorDestination(location);
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private void recalculatepath(PointF p, int z) {
        try {
            if (!shouldDisableReroute()) {
                int state = -1;
                PointF pointonpath = aStarData.getInstance().getCurrentPath().getClosestPointOnPath(p, z);

                FacilityConf facilityConf = FacilityContainer.getInstance().getCurrent();

                if (facilityConf == null) {
                    return;
                }

                double rdistance = MathUtils.distance(p, pointonpath) / facilityConf.getPixelsToMeter();
                if (rdistance > facilityConf.getDistanceFromNavPath()) {
                    counterForIndoorReroute++;
                    if (counterForIndoorReroute >= getCountForReroute()) {
                        state = 0;
                        long currenttime = System.currentTimeMillis();
                        if (currenttime > PropertyHolder.getInstance().getStartNavigationTime() + 10000) {
                            if (!PropertyHolder.getInstance().isNavigationInstructionsSoundMute()) {
                                List<String> recalcsound = new ArrayList<String>();
                                recalcsound.add("recalculate");
                                SoundPlayer.getInstance().play(recalcsound);
                            }
                            state = 1;
                        }

                        reRoute();

                        switch (state) {
                            case 0:
                                notifyOnNavigationStateChanged(NavigationState.SILENT_REROUTE);
                                break;
                            case 1:
                                notifyOnNavigationStateChanged(NavigationState.REROUTE);
                                break;
                            case 2:
                                notifyOnNavigationStateChanged(NavigationState.TURNED_BACK);
                                break;
                        }

                        counterForIndoorReroute = 0;
                    }
                } else {
                    counterForIndoorReroute = 0;
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void checkRecalculateByZ(int z) {
        try {
            if (PropertyHolder.getInstance().isNavigationState()) {
                boolean iszpathexists = isZPathExists(z);
                if (!iszpathexists) {
                    counterForFloorReroute++;
                    if (counterForFloorReroute >= getCountForReroute()) {
                        reRoute();
                        notifyOnNavigationStateChanged(NavigationState.REROUTE);
                        counterForFloorReroute = 0;
                    }
                } else {
                    counterForFloorReroute = 0;
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private boolean isZPathExists(int currentz) {
        boolean result = false;
        NavigationPath path = aStarData.getInstance().getCurrentPath();
        if (path != null && path.getPathByZ(currentz) != null) {
            result = true;
        }
        return result;
    }

    private void checkIndoorDestination(ILocation location) {
        try {
            if (location != null) {
                PoiData finaldest = RouteCalculationHelper.getInstance().getFinalDestination();
                if (finaldest != null && finaldest.getPoiNavigationType().equals("internal")) {
                    String locfac = location.getFacilityId();
                    String finalfac = finaldest.getFacilityID();
                    if (locfac != null && finalfac != null && locfac.equals(finalfac)) {
                        double locz = location.getZ();
                        double finalz = finaldest.getZ();
                        if (locz == finalz) {
                            PointF locpoint = new PointF((float) location.getX(), (float) location.getY());
                            PointF finalpoint = finaldest.getPoint();

                            FacilityConf facility = FacilityContainer.getInstance().getCurrent();
                            if (facility != null) {
                                PointF projectedpoint = GisData.getInstance().findClosestPointOnLine(finalpoint);
                                if (projectedpoint != null) {
                                    finalpoint = projectedpoint;
                                }
                                double distance = MathUtils.distance(locpoint, finalpoint);
                                float pixeltometer = facility.getPixelsToMeter();
                                double distanceinmeters = distance / pixeltometer;
                                double reachDestinationRange = facility.getEndOfRouteRadius();
                                if (distanceinmeters < reachDestinationRange) {
                                    if (!PropertyHolder.getInstance().isNavigationInstructionsSoundMute()) {
                                        List<String> sounds = new ArrayList<String>();
                                        sounds.add("destination");
                                        SoundPlayer.getInstance().play(sounds);
                                    }
                                    notifyOnNavigationStateChanged(NavigationState.DESTINATION_REACHED);
                                    stopNavigation();
                                    notifyArriveToPoi(finaldest);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void updateInstruction(Instruction ins, PointF p) {

        try {
            FacilityConf FacilityConf = FacilityContainer.getInstance().getCurrent();

            if (FacilityConf == null || ins == null) {
                return;
            }

            Instruction instruction = null;

            if (ins.getType() == Instruction.TYPE_DESTINATION) {
                instruction = getExitInstruction(ins, FacilityConf.getId());
            } else {
                instruction = ins;
            }

            if (instruction == null) {
                instruction = ins;
            }

            InstructionBuilder.getInstance().setNextInstruction(instruction);
            int z = FacilityConf.getSelectedFloor();
            GisPoint currentpoint = new GisPoint(p.x, p.y, z);
            GisPoint turnpoint = new GisPoint(instruction.getLocation().getX(), instruction.getLocation().getY(), instruction.getLocation().getZ());
            double dfromturn = aStarMath.findDistance(currentpoint, turnpoint) / FacilityConf.getPixelsToMeter();

            instructionLayout.setVisibility((PropertyHolder.getInstance().isDisplayNavigationInstructionsOnMap() ? View.VISIBLE : View.INVISIBLE));

            NavInstruction inavInst = null;

            if (PropertyHolder.getInstance().isSimplifiedInstruction()) {
                inavInst = instruction.getSimplifiedInstruction();
                updateNavBubble(inavInst);
            } else {
                updateNavBubble(instruction);
            }

            if (inavInst == null) {
                inavInst = InstructionBuilder.getInstance().getNextNavInstruction(instruction, true);
            }

            if (inavInst != null) {
                notifyOnNavigationInstructionChanged(inavInst);
            }

            Instruction inst = null;
            if (instruction.getType() == Instruction.TYPE_EXIT) {
                inst = instruction;
            } else {
                inst = InstructionBuilder.getInstance().findCloseMergedInstruction(p);
            }

            if (inst != null) {
                float itreshold = FacilityConf.getPlayInstructionDistance();
                if ((dfromturn < itreshold) && !inst.hasPlayed()) {
                    if (inst.getType() != Instruction.TYPE_EXIT && inst.getType() != Instruction.TYPE_DESTINATION) {
                        inst.setPlayed(true);
                        soundPlayerHelper.playInstructionSound(inst);
                    }

                    inavInst = InstructionBuilder.getInstance().getNextNavInstruction(instruction);
                    notifyOnNavigationInstructionRangeEntered(inavInst);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private double getOutDoorPathDistance() {
        double result = 0;
        try {
//            List<CampusNavigationPath> navpath = RouteCalculationHelper.getInstance().getOutdoorNavPath();
//            if (navpath != null && !navpath.isEmpty()) {
//                List<GisSegment> segments = new ArrayList<>(navpath.getPath());
//                for (GisSegment segment : segments) {
//                    result += segment.getLine().getDistanceBetweenPoints();
//                }
//            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    private NavInstruction getOutdoorInstruction(ILocation myloc) {
        NavInstruction result = null;
        try {
            if (campusPathsMap != null && !campusPathsMap.isEmpty()) {
                double mylocationlat = myloc.getLat();
                double mylocationlon = myloc.getLon();
                LatLng mylatlng = new LatLng(mylocationlat, mylocationlon);
                double mind = Double.MAX_VALUE;
                CampusNavigationPath closepath = null;
                for (CampusNavigationPath o : campusPathsMap.values()){
                    LatLng projectedloc = CampusGisData.getInstance().findClosestPointOnLine(mylatlng, o.getPath());
                    double d = MathUtils.distance(mylatlng, projectedloc);
                    if (d < mind) {
                        mind = d;
                        closepath = o;
                    }
                }
                if (closepath != null) {
                    NavInstruction simplifiedins = closepath.getSimplifiedInstruction();
                    if (simplifiedins != null) {
                        result = simplifiedins;
                    }
                }
            }
            if (result == null) {
                int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "outdoorInstruction");
                String text = getResources().getString(tmptxt);
                Bitmap signBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.outdoor_instruction_sign);
                result = new NavInstruction(INavInstruction.OUTDOOR_INSTRUCTION_TAG, text, signBitmap, getOutDoorPathDistance());
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }


    private NavInstruction getOutdoorInstruction(PoiData dest, double distancefromdest) {
        NavInstruction result = null;
        try {
            if (dest != null) {
                PoiData finaldest = RouteCalculationHelper.getInstance().getFinalDestination();
                if (finaldest != null && finaldest.equals(dest)) {
                    int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "simplified");
                    String instext = getResources().getString(tmptxt);
                    String destname = null;
                    if (finaldest.getpoiDescription() != null && !finaldest.getpoiDescription().isEmpty()) {
                        destname = finaldest.getpoiDescription();
                    } else {
                        destname = "the Destination";
                    }
                    String text = instext + " " + destname;
                    Bitmap signBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.destination);
                    result = new NavInstruction(INavInstruction.DESTINATION_INSTRUCTION_TAG, text, signBitmap, getOutDoorPathDistance());
                } else {
                    int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "simplified");
                    String text = getResources().getString(tmptxt);
                    String destname = null;
                    if (dest.getpoiDescription() != null && !dest.getpoiDescription().isEmpty()) {
                        destname = dest.getpoiDescription();
                    } else {
                        destname = "the Entrannce";
                    }
                    text += " " + destname;
                    Bitmap signBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.mp_exit);
                    result = new NavInstruction(INavInstruction.OUTDOOR_INSTRUCTION_TAG, text, signBitmap, getOutDoorPathDistance());
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    private Instruction getExitInstruction(Instruction ins, String facid) {
        Instruction result = ins;
        try {
            if (ins != null && facid != null) {

                String bridgeto = null;
                List<List<Location>> bridgepoints = RouteCalculationHelper.getInstance().getBridgesPoints();
                for (List<Location> o : bridgepoints) {
                    if (o != null && o.size() > 1) {
                        Location point1 = o.get(0);
                        Location point2 = o.get(1);
                        if (point1 != null && point2 != null) {
                            if (point1.getFacilityId().equals(facid)) {
                                FacilityConf fac = ProjectConf.getInstance().getFacilityConf(point2);
                                if (fac != null) {
                                    bridgeto =  fac.getName();
                                    break;
                                }
                            }
                        }
                    }
                }
                if (bridgeto != null) {
                    result = new Instructionobject(ins.getDistance());
                    result.setLocation(ins.getLocation());
                    result.setSegment(ins.getSegment());
                    int image = R.drawable.outdoor_instruction_sign;
                    int text = ResourceTranslator.getInstance().getTranslatedResourceId("string", "simplified");
                    result.addImage(image);
                    result.addText(text);
                    result.setType(Instruction.TYPE_EXIT);
                    result.setToFacilty(bridgeto);
                    NavInstruction simplifiedins = ins.getSimplifiedInstruction();
                    if (PropertyHolder.getInstance().isSimplifiedInstruction() && simplifiedins != null) {
                        result.setSimplifiedInstruction(simplifiedins);
                    }
                } else {
                    PoiData poi = RouteCalculationHelper.getInstance().getFinalDestination();
                    if (poi != null) {
                        if (poi.getpoiDescription() != null && !poi.getpoiDescription().isEmpty()) {
                            ins.setDestinationName(poi.getpoiDescription());
                        }
                        if (poi.getPoiNavigationType().equals("external") || poi.getFacilityID() == null || poi.getFacilityID().equals("unknown") || !poi.getFacilityID().equals(facid)) {
                            result = new Instructionobject(ins.getDistance());
                            result.setLocation(ins.getLocation());
                            result.setSegment(ins.getSegment());
                            int image = R.drawable.mp_exit;
                            int text = ResourceTranslator.getInstance().getTranslatedResourceId("string", "exit_building");
                            result.addImage(image);
                            result.addText(text);
                            result.setType(Instruction.TYPE_EXIT);
                            NavInstruction simplifiedins = ins.getSimplifiedInstruction();
                            if (PropertyHolder.getInstance().isSimplifiedInstruction() && simplifiedins != null) {
                                result.setSimplifiedInstruction(simplifiedins);
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
            result = ins;
        }
        return result;
    }

    public List<INavInstruction> getInstructionsList(){
//        List<Instruction> source = RouteCalculationHelper.getInstance().getCombinedInstructions();
//        List<INavInstruction> result = new ArrayList<>(source.size());
//        if(source != null) {
//            for (Instruction instruction : source) {
//                result.add(new NavInstruction(instruction));
//            }
//        }
        return InstructionBuilder.getInstance().getNavInstructions();
    }

    protected void updateNavBubble(NavInstruction instruction) {
        try {
            if (instruction != null) {
                instructionLayout.setVisibility((PropertyHolder.getInstance().isDisplayNavigationInstructionsOnMap() ? View.VISIBLE : View.INVISIBLE));
                Bitmap bmp = instruction.getSignBitmap();
                String text = instruction.getText();
                instructionTextView.setText(text);
                instructionImageView.setImageBitmap(bmp);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void updateNavBubble(Instruction instruction) {
        try {
            if (instruction != null) {
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), instruction.getImage().get(0));
                String text = "";
                if (PropertyHolder.getInstance().isSimplifiedInstruction()) {
                    text = getSimplifiedText(instruction);
                } else {
                    text = instruction.toString();
                }
                instructionTextView.setText(text);
                instructionImageView.setImageBitmap(bmp);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void playSwitchInstruction(Instruction instruction, PointF pointforplaying) {
        try {
            InstructionBuilder.getInstance().setNextInstruction(instruction);
            instructionLayout.setVisibility((PropertyHolder.getInstance().isDisplayNavigationInstructionsOnMap() ? View.VISIBLE : View.INVISIBLE));
            updateNavBubble(instruction);

            if (!instruction.hasPlayed()) {
                instruction.setPlayed(true);
                soundPlayerHelper.playInstructionSound(instruction);
                INavInstruction inavInst = InstructionBuilder.getInstance().getNextNavInstruction(instruction);
                notifyOnNavigationInstructionRangeEntered(inavInst);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void enableAutoOpenClosePoiBubble(boolean enable) {
        enableAutoPoiBubble = enable;
    }

    private void openClosePoiBubble(ILocation location) {
        try {
            if (showAutoBubble && enableAutoPoiBubble && !PropertyHolder.getInstance().isNavigationState()) {
                if (location != null) {
                    int maxReturnedCount = 1;
                    float rangeInMeters = PropertyHolder.getInstance().getBubbleRange();
                    LocationMode locType = location.getLocationType();
                    // Outdoor location
                    if (locType == LocationMode.OUTDOOR_MODE) {
                        Location loc = new Location(location);
                        List<IPoi> extpois = ProjectConf.getInstance().getExternalInRangePois(loc, rangeInMeters, maxReturnedCount);
                        boolean exthasclosebubble = false;
                        if (extpois != null && !extpois.isEmpty()) {
                            IPoi extpoi = extpois.get(0);
                            if (extpoi != null) {
                                LatLng poiLatLonpoint = new LatLng(extpoi.getPoiLatitude(), extpoi.getPoiLongitude());
                                LatLng myExtLoc = new LatLng(loc.getLat(), loc.getLon());
                                double dist = MathUtils.distance(poiLatLonpoint, myExtLoc);

                                if (dist < rangeInMeters) {
                                    openPoiBubble(extpoi, true);
                                    autoBubblePoi = extpoi;
                                    exthasclosebubble = true;
                                }
                            }
                        }

                        if (!exthasclosebubble && autoBubblePoi != null) {
                            closeBubble(autoBubblePoi);
                        }
                    } else {    // indoor location
                        String facilityid = location.getFacilityId();
                        if (facilityid != null) {
                            Campus campus = ProjectConf.getInstance().getSelectedCampus();
                            if (campus != null) {
                                FacilityConf fac = campus.getFacilityConf(facilityid);
                                if (fac != null) {
                                    PointF myLoc = new PointF((float) location.getX(), (float) location.getY());
                                    float pixelToMeter = fac.getPixelsToMeter();


                                    List<IPoi> bpois = DualMapPoisUtil.getInstance().getInRangePois(myLoc, pixelToMeter, rangeInMeters, maxReturnedCount);
                                    boolean hasclosebubble = false;
                                    if (bpois != null && !bpois.isEmpty()) {
                                        IPoi poi = bpois.get(0);
                                        if (poi != null) {
                                            PointF poipoint = poi.getPoint();
                                            double distance = MathUtils.distance(poipoint, myLoc);
                                            double distanceinmeters = distance / pixelToMeter;
                                            if (distanceinmeters < rangeInMeters) {
                                                openPoiBubble(poi, true);
                                                autoBubblePoi = poi;
                                                hasclosebubble = true;
                                            }
                                        }
                                    }
                                    if (!hasclosebubble && autoBubblePoi != null) {
                                        closeBubble(autoBubblePoi);
                                    }
                                }
                            }
                        }
                    }// else indoor loc
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void chekLocationFloorChange(ILocation location) {

        try {

            if (lastRawLocation != null && location != null) {
                if (lastRawLocation.getLocationType() == LocationMode.INDOOR_MODE && location.getLocationType() == LocationMode.INDOOR_MODE) {
                    if (lastRawLocation.getFacilityId().equals(location.getFacilityId())) {
                        if (lastRawLocation.getZ() != location.getZ()) {
                            lastSimultedLcation = null;
                            checkRecalculateByZ((int) location.getZ());
                            loadKdTreePois(location.getFacilityId(), (int) location.getZ());
                            if (PropertyHolder.getInstance().isNavigationState()) {
                                notifyOnNavigationStateChanged(NavigationState.FLOOR_CHANGED);
                            }
                        }
                    }
                }
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void exitFacility(ILocation location, String facid) {
        try {
            lastSimultedLcation = null;
//			removeFacilityPath(facid);
            calculateCampusPath(location);
            startOutDoorNavigationThread(location);
//			outdoorNavigationRutin(location);
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void enterFacility(ILocation location, String facid) {
        try {
            lastSimultedLcation = null;
            checkFacilityReoute(location, facid);
            setIndoorNavigation(location);
//			removeNavigationPath();
            loadKdTreePois(facid, (int) location.getZ());
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void checkFacilityReoute(ILocation location, String facid) {
        try {
            if (location != null && facid != null) {
                HashMap<String, NavigationPath> pathmap = RouteCalculationHelper.getInstance().getIndoorNavPaths();
                if (!pathmap.containsKey(facid)) {
                    counterForFacilityReroute++;
                    if (counterForFacilityReroute >= getCountForReroute()) {
                        reRoute();
                        counterForFacilityReroute = 0;
                    }
                } else {
                    counterForFacilityReroute = 0;
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void setEntranceFloor() {
        try {
            if (floorsPickerFacility != null) {
                int floor = floorsPickerFacility.getEntranceFloor();
                String id = floorsPickerFacility.getId();
                if (id != null && !id.isEmpty()) {
                    FacilityOverlay fo = facilityOverlays.get(id);
                    if (floor != fo.getFloor()) {
                        setFloor(floor);
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }


    private void drawProjectPOis() {
        float zoom = googlemap.getCameraPosition().zoom;
        poisClusterHelper.setPOIs(zoom,  floorDisplayPolicy);
    }


    @Override
    public void poiListDelivered(List<IPoi> withLabels, List<IPoi> withoutLabels) {

        removePoiMarkers(withLabels, withoutLabels);

        for (IPoi poi : withLabels) {
            if (isPoiInRange(poi)) {
                drawPOi(poi, true);
            }
        }

        for (IPoi poi : withoutLabels) {
            if (isPoiInRange(poi)) {
                drawPOi(poi, false);
            }
        }
    }

    private void drawPOi(IPoi poi, boolean withlabel) {
        try {
            if (!poisMarkerMap.keySet().contains(poi)) {
                MarkerOptions options = poisClusterHelper.getPoiMarkerOptions(poi, withlabel);
                if (options != null) {
                    Marker marker = googlemap.addMarker(options);
                    markersPoisMap.put(marker, poi);
                    poisMarkerMap.put(poi, marker);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void removePoiMarkers(List<IPoi> withLabels, List<IPoi> withoutLabels) {
        try {
            Iterator<Marker> it = markersPoisMap.keySet().iterator();
            while (it.hasNext()) {
                Marker o = it.next();
                IPoi poi = markersPoisMap.get(o);
                if (poi == null || (!withLabels.contains(poi) && !withoutLabels.contains(poi))) {
                    o.remove();
                    it.remove();
                    poisMarkerMap.remove(poi);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private void removePoiMarkers() {
        try {
            for (Marker o : markersPoisMap.keySet()) {
                o.remove();
            }
            markersPoisMap.clear();
            poisMarkerMap.clear();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void removeNotInRangePoiMarkers() {
        try {
            Iterator<Marker> it = markersPoisMap.keySet().iterator();
            while (it.hasNext()) {
                Marker o = it.next();
                IPoi poi = markersPoisMap.get(o);
                if (poi == null || !isPoiInRange(poi)) {
                    o.remove();
                    it.remove();
                    poisMarkerMap.remove(poi);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }


    private boolean isPoiInRange(IPoi poi) {
        boolean result = false;
        try {
            LatLngBounds latLngBounds = googlemap.getProjection().getVisibleRegion().latLngBounds;
            LatLng latLng = new LatLng(poi.getPoiLatitude(), poi.getPoiLongitude());
            if(latLngBounds.contains(latLng)) {
                result = true;
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    private ILocation getConvertedLocation(ILocation location) {

        ILocation result = null;
        try {
            if (location != null) {
//				String campusid = location.getCampusId();
                String facid = location.getFacilityId();
                if (facid != null) {
                    double x = location.getX();
                    double y = location.getY();
                    int z = (int) location.getZ();
                    LatLng latlng = convertToLatlng(x, y, z, facid);
                    result = new Location(latlng);
//					result.setCampusId(campusid);
//					result.setFacilityId(facid);
//					result.setX(x);
//					result.setY(y);
//					result.setZ(z);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    private ILocation getIndoorConvertedLocation(ILocation location) {

        try {
            if (location != null && location.getLocationType() == LocationMode.INDOOR_MODE) {
                String facid = location.getFacilityId();
                if (facid != null) {
                    double x = location.getX();
                    double y = location.getY();
                    int z = (int) location.getZ();
                    LatLng latlng = convertToLatlng(x, y, z, facid);
                    location.setLat(latlng.latitude);
                    location.setLon(latlng.longitude);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return location;
    }

    public LatLng convertToLatlng(double x, double y, int z, String facid) {

        LatLng result = null;

        try {
            Campus c = ProjectConf.getInstance().getSelectedCampus();
            if (c != null) {
                Map<String, FacilityConf> facilitiesmap = c.getFacilitiesConfMap();
                FacilityConf fac = facilitiesmap.get(facid);
                if (fac != null) {
                    NdkLocation point = new NdkLocation(x, y);
                    point.setZ(z);

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
                        result = new LatLng(covertedPoint.getLat(),
                                covertedPoint.getLon());
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        return result;
    }

    public void showFloorWithId(String FacilityId, int floorId) {
        try {
            setFloor(floorId);
            if (lastRawLocation != null) {
                if (lastRawLocation.getZ() != floorId) {
                    setFollowMeFalse();
                } else {
//						followMeMode = true;
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void showFloorWithId(int floorId) {
        try {
            setFloor(floorId);
            if (lastRawLocation != null) {
                if (lastRawLocation.getZ() != floorId) {
                    setFollowMeFalse();
                } else {
//						followMeMode = true;
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void showPoi(IPoi poi) {
        showPoi(poi, true);
    }

    public void showPoi(IPoi poi, boolean returnToDefaultZoom) {

        try {
            if (poi != null) {
                if (googlemap == null) {
                    poiToPresent = poi;
                    zoomToDefault = returnToDefaultZoom;
                } else {
                    poi.setVisible(true);
                    ILocation loc = new Location(poi);
                    if (loc != null) {
                        presentLocation(loc, poi, returnToDefaultZoom);
//						openPoiBubble(poi);
                    }
                    displayPoi(poi);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void showPoi(IPoi poi, String bubbletext) {
        if (poi != null) {
            displayPoi(poi);
        }
    }

    private void displayPoi(final IPoi poi) {
        try {
            if (poi != null) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                                poi.setVisible(true);
                                drawPOi(poi, false);
                                openPoiBubble(poi);
                        }

                    };
                    @Override
                    public void run() {
                        post(r);
                    }
                }, 1000);

            }
        } catch (Throwable t){
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void presentLocation(ILocation location) {
        presentLocation(location, null, true);
    }

    public void presentLocation(ILocation location, boolean returnToDefaultZoom) {
        if (location != null) {
            if (googlemap == null) {
                locationToPresent = location;
            } else {
                presentLocation(location, null, returnToDefaultZoom);
            }
        }
    }

    private void presentLocation(ILocation location, final IPoi poi, boolean returnToDefaultZoom) {
        try {
            LatLng latlng = null;
            if (location.getLocationType() == LocationMode.INDOOR_MODE) {
                String facilityid = location.getFacilityId();
                if (facilityid != null) {
                    int z = (int) location.getZ();
                    setFloor(z);
                }
                latlng = convertToLatlng(location.getX(), location.getY(),
                        (int) location.getZ(), location.getFacilityId());
            } else if (location.getLocationType() == LocationMode.OUTDOOR_MODE) {
                int z = getEntranceFloor();
                setFloor(z);
                latlng = new LatLng(location.getLat(), location.getLon());
            }

            if (latlng != null) {
                setFollowMeFalse();

                if (returnToDefaultZoom) {
                    if (PropertyHolder.getInstance().isMapAnimation()) {
                        isAnimatingLocation = true;
                        googlemap.stopAnimation();
                        googlemap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(latlng, defaultZoom), animationDuration,
                                new GoogleMap.CancelableCallback() {
                                    @Override
                                    public void onFinish() {
                                        isAnimatingLocation = false;
                                        if (poi != null) {
                                            openPoiBubble(poi);
                                        }
                                    }

                                    @Override
                                    public void onCancel() {
                                        isAnimatingLocation = false;
                                        if (poi != null) {
                                            openPoiBubble(poi);
                                        }
                                    }
                                });
                    } else {
                        googlemap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, defaultZoom));
                        if (poi != null) {
                            openPoiBubble(poi);
                        }
                    }
                } else {
                    if (PropertyHolder.getInstance().isMapAnimation()) {
                        isAnimatingLocation = true;
                        googlemap.stopAnimation();
                        googlemap.animateCamera(
                                CameraUpdateFactory.newLatLng(latlng), animationDuration,
                                new GoogleMap.CancelableCallback() {
                                    @Override
                                    public void onFinish() {
                                        isAnimatingLocation = false;
                                        if (poi != null) {
                                            openPoiBubble(poi);
                                        }
                                    }

                                    @Override
                                    public void onCancel() {
                                        isAnimatingLocation = false;
                                        if (poi != null) {
                                            openPoiBubble(poi);
                                        }
                                    }
                                });
                    } else {
                        googlemap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                        if (poi != null) {
                            openPoiBubble(poi);
                        }
                    }
                }

            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public int getEntranceFloor() {
        int result = 0;
        try {
            FacilityConf facility = getFloorsPickerFacility();
            if (facility != null) {
                floorsPickerFacility = facility;
                result = facility.getEntranceFloor();
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx, t);
        }

        return result;
    }

    public List<SpreoDualMapViewListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<SpreoDualMapViewListener> listeners) {
        this.listeners = listeners;
    }

    public void registerListener(SpreoDualMapViewListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            for (FacilityOverlay o : facilityOverlays.values()) {
                try {
                    if (o != null) {
                        int z = o.getFloor();
                        if (z >= 0) {
                            Campus campus = ProjectConf.getInstance().getSelectedCampus();
                            if (campus != null) {
                                String campusid = campus.getId();
                                String facilityid = o.getFacilityId();
                                notifyFloorLoad(campusid, facilityid, z);
                            }
                        }
                    }
                } catch (Throwable t) {
                    MapDebugHelper.onException(ctx,  t);
                }
            }
        }
    }

    public void unregisterListener(SpreoDualMapViewListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    @Override
    public void onCampusRegionEntrance(String campusId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFacilityRegionEntrance(String campusId, String facilityId) {
        // TODO Auto-generated method stub

    }

    private void loadParkingLocation() {
        try {
            ParkingUtil.getInstance().load();
        } catch (Throwable e) {

        }
    }

    @Override
    public void navigateTo(IPoi poi) {
        navigateTo(poi, false);
    }

    private void navigateTo(IPoi poi, boolean forReroute){
        try {
//            removeAllFacilityPath();
//            removeNavigationPath();

            if(!forReroute) soundPlayerHelper.reset();
            long startnavigationtime = System.currentTimeMillis();
            PropertyHolder.getInstance().setStartNavigationTime(startnavigationtime);
            outdoorInstructionNotified = false;
            ILocation loc = LocationFinder.getInstance().getCurrentLocation();
            Location origin = null;
            PoiData destination = null;

            if (loc != null) {
                if (loc instanceof Location) {
                    origin = (Location) loc;
                } else {
                    origin = new Location(loc);
                }
            }

            if (poi != null && poi instanceof PoiData) {
                destination = (PoiData) poi;
            }

            if (origin != null && destination != null) {
                if (navigationTask != null) {
                    navigationTask.cancel(true);
                }
                navigationTask = new ServerNavigationTask(ctx, this, origin, destination);
                navigationTask.setReroute(forReroute);
                navigationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                PropertyHolder.getInstance().setNavigationState(true);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }


    private void startOutDoorNavigationThread(final ILocation myloc) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    outdoorNavigationRutin(myloc);
                } catch (Throwable e) {
                    MapDebugHelper.onException(ctx,  e);
                }

            }
        }).start();
    }

    private void outdoorNavigationRutin(final ILocation myloc) {
        try {
            final PoiData dest = RouteCalculationHelper.getInstance().getFinalDestination();
            if (myloc != null && dest != null && dest.getPoiNavigationType().equals("external")) {
                double mylocationlat = myloc.getLat();
                double mylocationlon = myloc.getLon();
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

                    if (distancefromdest < endOfRouteRadius) {
                        Runnable r = new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    if (!PropertyHolder.getInstance().isNavigationInstructionsSoundMute()) {
                                        List<String> sounds = new ArrayList<String>();
                                        sounds.add("destination");
                                        SoundPlayer.getInstance().play(sounds);
                                    }
                                    notifyOnNavigationStateChanged(NavigationState.DESTINATION_REACHED);
                                    stopNavigation();
                                    notifyArriveToPoi(dest);
                                } catch (Throwable e) {
                                    MapDebugHelper.onException(ctx,  e);
                                }
                            }
                        };

                        post(r);
                        return;
                    } else {

                        CheckKmlReroute(myloc);

                        Runnable r1 = new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    if (!outdoorInstructionNotified) {
//                                        NavInstruction navInst = getOutdoorInstruction(dest, distancefromdest);
                                        NavInstruction navInst = getOutdoorInstruction(myloc);
                                        updateNavBubble(navInst);
                                        notifyOnNavigationInstructionChanged(navInst);
                                        outdoorInstructionNotified = navInst.getDistance() != 0;
                                    }
                                } catch (Throwable e) {
                                    MapDebugHelper.onException(ctx,  e);
                                }
                            }
                        };
                        post(r1);
                    }

                }
            } else {
                if (dest != null) {
                    CheckKmlReroute(myloc);

                    Runnable r1 = new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (!outdoorInstructionNotified) {
//                                        NavInstruction navInst = getOutdoorInstruction(dest, distancefromdest);
                                    NavInstruction navInst = getOutdoorInstruction(myloc);
                                    updateNavBubble(navInst);
                                    notifyOnNavigationInstructionChanged(navInst);
                                    outdoorInstructionNotified = navInst.getDistance() != 0;
                                }
                            } catch (Throwable e) {
                                MapDebugHelper.onException(ctx, e);
                            }
                        }
                    };
                    post(r1);
                }
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void showNavigatedDestinationMarker(boolean show) {
        if(checkShowNavigationMarkersSettingIsOk(show, "destination marker")){
            showDestinationMarker = show;
            updateDestinationMarkerState();
        }
    }

    public void updateNavigationMarkerState() {
        updateDestinationMarkerState();
        updateOriginMarkerState();
        updateExitsnMarkersState();
        updateSwitchfloorsMarkersState();
        updateBridgesState();
    }

    private void updateSwitchfloorsMarkersState() {
        try {

            if (switchfloorMarkers != null) {
                for (Marker m : switchfloorMarkers.keySet()) {
                    m.remove();
                }
                switchfloorMarkers.clear();
            }

            if (showSwitchFloorMarkers || PropertyHolder.getInstance().isShowNavigationMarkers()) {

                List<List<Location>> bridgepoints = RouteCalculationHelper.getInstance().getSwitchFloorPoints();
                for (List<Location> o : bridgepoints) {
                    if (o != null && o.size() > 1) {
                        Location point1 = o.get(0);
                        Location point2 = o.get(1);

                        if(isContentVisible(point1.getFacilityId(), point1.getZ())){
                            Campus campus = ProjectConf.getInstance().getSelectedCampus();
                            if (campus != null) {
                                LayoutInflater inflater = LayoutInflater.from(ctx);
                                View view = inflater.inflate(R.layout.custom_floor_bubble, null);
                                String lng = PropertyHolder.getInstance().getAppLanguage();
                                if (lng != null && (lng.equals("hebrew") || lng.equals("arabic"))) {
                                    view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                                }
                                TextView t1 = (TextView) view.findViewById(R.id.t1);
                                String floortitle = SpreoDataProvider.getFloorTitle(campus.getId(), point2.getFacilityId(), (int) point2.getZ());
                                String directiontext = "";
                                if(point1.getZ() > point2.getZ()) {
                                    int resid = ResourceTranslator.getInstance().getTranslatedResourceId("string", "elvator_down");
                                    directiontext = getResources().getString(resid);
                                } else if(point1.getZ() < point2.getZ()) {
                                    int resid = ResourceTranslator.getInstance().getTranslatedResourceId("string", "elvator_up");
                                    directiontext = getResources().getString(resid);
                                }
                                String fulltext = directiontext + " " + floortitle;
                                t1.setText(fulltext);
                                t1.setTypeface(t1.getTypeface(), Typeface.BOLD);
                                Bitmap sIcon = getBitmapFromView(view);
                                if (sIcon != null) {
                                    BitmapDescriptor locicon = BitmapDescriptorFactory.fromBitmap(sIcon);
                                    sIcon.recycle();
                                    sIcon = null;

                                    LatLng latlng = Location.getLatLng(point1.getX(), point1.getY(), point1.getFacilityId());
                                    switchfloorMarkers.put(googlemap.addMarker(new MarkerOptions().position(latlng).icon(locicon).anchor(0.5f, 0.9f).zIndex(6)), (int)point2.getZ());
                                }
                            }
                        }
                    }
                }



//                HashMap<String, NavigationPath> pathmap = RouteCalculationHelper.getInstance().getIndoorNavPaths();
//                for (NavigationPath p : pathmap.values()) {
//                    List<FloorNavigationPath> fullpath = p.getFullPath();
//                    if (fullpath.size() > 1) {
//                        FloorNavigationPath firstfloor = fullpath.get(0);
//                        List<GisSegment> lines = firstfloor.getPath();
//                        GisSegment lastline = lines.get(lines.size() - 1);
//                        GisPoint lastpoint = lastline.getLine().getPoint2();
//                        String facid = p.getFacilityId();
//                        if(isContentVisible(facid, lastpoint.getZ())){
//
//                            Campus campus = ProjectConf.getInstance().getSelectedCampus();
//                            if (campus != null) {
//                                FloorNavigationPath secondfloor = fullpath.get(1);
//                                if (secondfloor != null) {
//                                    int z = (int) secondfloor.getZ();
//                                    LayoutInflater inflater = LayoutInflater.from(ctx);
//                                    View view = inflater.inflate(R.layout.custom_floor_bubble, null);
//                                    String lng = PropertyHolder.getInstance().getAppLanguage();
//                                    if (lng != null && (lng.equals("hebrew") || lng.equals("arabic"))) {
//                                        view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//                                    }
//                                    TextView t1 = (TextView) view.findViewById(R.id.t1);
//                                    String floortitle = SpreoDataProvider.getFloorTitle(campus.getId(), p.getFacilityId(), (int) z);
//                                    String directiontext = "";
//                                    if(lastpoint.getZ() > z) {
//                                        int resid = ResourceTranslator.getInstance().getTranslatedResourceId("string", "elvator_down");
//                                        directiontext = getResources().getString(resid);
//                                    } else if(lastpoint.getZ() < z) {
//                                        int resid = ResourceTranslator.getInstance().getTranslatedResourceId("string", "elvator_up");
//                                        directiontext = getResources().getString(resid);
//                                    }
//                                    String fulltext = directiontext + " " + floortitle;
//                                    t1.setText(fulltext);
//                                    t1.setTypeface(t1.getTypeface(), Typeface.BOLD);
//                                    Bitmap sIcon = getBitmapFromView(view);
//                                    if (sIcon != null) {
//                                        BitmapDescriptor locicon = BitmapDescriptorFactory.fromBitmap(sIcon);
//                                        sIcon.recycle();
//                                        sIcon = null;
//
//                                        LatLng latlng = Location.getLatLng(lastpoint.getX(), lastpoint.getY(), facid);
//                                        switchfloorMarkers.put(googlemap.addMarker(new MarkerOptions().position(latlng).icon(locicon).anchor(0.5f, 0.9f).zIndex(6)), z);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }

            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private void updateExitsnMarkersState() {
        try {
            if (exitsMarkers != null) {
                for (Marker m : exitsMarkers) {
                    m.remove();
                }
            }

            if (showSwitchFloorMarkers || PropertyHolder.getInstance().isShowNavigationMarkers()) {

//                int resid = ResourceTranslator.getInstance().getTranslatedResourceId("drawable", "entance_exit_bubble");
//                Bitmap exitIcon = BitmapFactory.decodeResource(getResources(), resid);
//                BitmapDescriptor locicon = BitmapDescriptorFactory.fromBitmap(exitIcon);
//                exitIcon.recycle();
//                exitIcon = null;

                HashMap<Location, String> exits = RouteCalculationHelper.getInstance().getExitPoints();

                for (Location o : exits.keySet()) {


                    if (o != null) {



                        if (isLocationVisible(o)) {
                            String text = "";
                            String type = exits.get(o);
                            if (o != null) {
                                if (type.equals(RouteCalculationHelper.TYPE_ENTRANCE)) {
                                    int txtid = ResourceTranslator.getInstance().getTranslatedResourceId("string", "entrance_building_bubble");
                                    text = getResources().getString(txtid);
                                } else if (type.equals(RouteCalculationHelper.TYPE_EXIT)) {
                                    int txtid = ResourceTranslator.getInstance().getTranslatedResourceId("string", "exit_building_bubble");
                                    text = getResources().getString(txtid);
                                }

                                LayoutInflater inflater = LayoutInflater.from(ctx);
                                View view = inflater.inflate(R.layout.custom_exit_bubble, null);
                                String lng = PropertyHolder.getInstance().getAppLanguage();
                                if (lng != null && (lng.equals("hebrew") || lng.equals("arabic"))) {
                                    view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                                }
                                TextView t1 = (TextView) view.findViewById(R.id.t1);

                                t1.setText(text);
                                t1.setTypeface(t1.getTypeface(), Typeface.BOLD);
                                Bitmap sIcon = getBitmapFromView(view);
                                if (sIcon != null) {
                                    BitmapDescriptor locicon = BitmapDescriptorFactory.fromBitmap(sIcon);
                                    sIcon.recycle();
                                    sIcon = null;
                                    LatLng latlon = Location.getLatLng(o);
                                    exitsMarkers.add(googlemap.addMarker(new MarkerOptions().position(latlon).icon(locicon).anchor(0.5f, 0.9f).zIndex(6)));
                                }

                            }
                        }
                    }
                }

            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void updateBridgesState() {
        try {
            if (bridgeMarkers != null) {
                for (Marker m : bridgeMarkers.keySet()) {
                    m.remove();
                }
                bridgeMarkers.clear();
            }

            Campus campus = ProjectConf.getInstance().getSelectedCampus();

            if (showSwitchFloorMarkers || PropertyHolder.getInstance().isShowNavigationMarkers() && campus != null) {

                List<List<Location>> bridgepoints = RouteCalculationHelper.getInstance().getBridgesPoints();
                for (List<Location> o : bridgepoints) {
                    if (o != null && o.size() > 1) {

                        Location point1 = o.get(0);
                        Location point2 = o.get(1);

                        if (point1 != null && point2 != null && point1.getZ() != point2.getZ()) {

                            String facilityid = point2.getFacilityId();

                            if (facilityid != null && isLocationVisible(point1)) {
                                LayoutInflater inflater = LayoutInflater.from(ctx);
                                View view = inflater.inflate(R.layout.custom_bubble, null);

                                String lng = PropertyHolder.getInstance().getAppLanguage();
                                if (lng != null && (lng.equals("hebrew") || lng.equals("arabic"))) {
                                    view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                                }

                                TextView t1 = (TextView) view.findViewById(R.id.t1);
                                TextView t2 = (TextView) view.findViewById(R.id.t2);
                                TextView t3 = (TextView) view.findViewById(R.id.t3);

                                int titletxtid = ResourceTranslator.getInstance().getTranslatedResourceId("string", "bridge_bubble_title");
                                String title = getResources().getString(titletxtid);

                                if (title != null) {
                                    t1.setText(title);
                                }

                                FacilityConf fac = campus.getFacilityConf(facilityid);
                                if (fac != null) {
                                    String facname = fac.getName();
                                    if (facname != null) {
                                        t2.setText(facname);
                                    }
                                }

                                int tofloor = (int)point2.getZ();

                                String floortitle = SpreoDataProvider.getFloorTitle(campus.getId(), facilityid, tofloor);

                                int floortxtid = ResourceTranslator.getInstance().getTranslatedResourceId("string", "floor");
                                String floor = getResources().getString(floortxtid);
                                if (floor != null && floortitle != null) {
                                    t3.setText(floor + " " + floortitle);
                                }

                                Bitmap bIcon = getBitmapFromView(view);
                                if (bIcon != null) {
                                    BitmapDescriptor locicon = BitmapDescriptorFactory.fromBitmap(bIcon);
                                    bIcon.recycle();
                                    bIcon = null;

                                    LatLng latlon = Location.getLatLng(point1);
                                    bridgeMarkers.put(googlemap.addMarker(new MarkerOptions().position(latlon).icon(locicon).anchor(0.5f, 0.9f).zIndex(6)), tofloor);
                                }

                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void updateOriginMarkerState() {
        try {
            if (originMarker != null) {
                originMarker.remove();
            }

            if (showOriginMarker || PropertyHolder.getInstance().isShowNavigationMarkers()) {

                Bitmap originIcon = BitmapFactory.decodeResource(getResources(), R.drawable.map_origin_center_anchor);

                Location location = RouteCalculationHelper.getInstance().getOrigin();

                if (location != null && (isLocationVisible(location) || PropertyHolder.getInstance().isDrawInvisibleNavMarkers())) {
                    LatLng latlon = Location.getLatLng(location);
                    BitmapDescriptor locicon = BitmapDescriptorFactory.fromBitmap(originIcon);
                    originMarker = googlemap.addMarker(new MarkerOptions().position(latlon).icon(locicon).anchor(0.5f, 0.9f).zIndex(6));
                    if (!isLocationVisible(location)) {
                        originMarker.setAlpha(PropertyHolder.getInstance().getVirtualRouteAlpha());
                    }
                    originIcon.recycle();
                    originIcon = null;
                }

            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void updateDestinationMarkerState() {
        try {
            if (finalDestinationMarker != null) {
                finalDestinationMarker.remove();
            }

            if (showDestinationMarker || PropertyHolder.getInstance().isShowNavigationMarkers()) {

                    Location destLocation = RouteCalculationHelper.getInstance().getDestination();  //new Location(finalDestination);

                    if (destLocation != null && (isLocationVisible(destLocation) || PropertyHolder.getInstance().isDrawInvisibleNavMarkers())) {

                        if (finalDestinationIcon == null) {
                            finalDestinationIcon = BitmapFactory.decodeResource(getResources(), R.drawable.map_destination_center_anchor);
                        }

                        LatLng destLatLon = Location.getLatLng(destLocation);
                        BitmapDescriptor locicon = BitmapDescriptorFactory.fromBitmap(finalDestinationIcon);
                        finalDestinationMarker = googlemap.addMarker(new MarkerOptions().position(destLatLon).icon(locicon).anchor(0.5f, 0.9f).zIndex(6));
                        if (!isLocationVisible(destLocation)) {
                            finalDestinationMarker.setAlpha(PropertyHolder.getInstance().getVirtualRouteAlpha());
                        }
                        finalDestinationIcon.recycle();
                        finalDestinationIcon = null;
                    }
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = null;
        try{
            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//            int mheight = (int) (view.getMeasuredHeight() * 1.9);
            bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.draw(canvas);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return bitmap;
    }

    private void showFacilityMarker(String facilityID, LatLng latlon) {
        try {
            if (facilityID != null) {
                Campus campus = ProjectConf.getInstance().getSelectedCampus();
                if (campus != null) {
                    LayoutInflater inflater = LayoutInflater.from(ctx);
                    View view = inflater.inflate(R.layout.custom_facility_bubble, null);
                    String lng = PropertyHolder.getInstance().getAppLanguage();
                    if (lng != null && (lng.equals("hebrew") || lng.equals("arabic"))) {
                        view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                    TextView t1 = (TextView) view.findViewById(R.id.t1);
                    FacilityConf fac = campus.getFacilityConf(facilityID);
                    if (fac != null) {
                        String facname = fac.getName();
                        if (facname != null) {
                            t1.setText(facname);
                        }
                        Bitmap bIcon = getBitmapFromView(view);
                        if (bIcon != null) {
                            BitmapDescriptor locicon = BitmapDescriptorFactory.fromBitmap(bIcon);
                            bIcon.recycle();
                            bIcon = null;
//                            LatLng latlon = new LatLng(fac.getCenterLatitude(), fac.getCenterLongtitude());
                            if (hideFacilityMarkerHandler != null) {
                                hideFacilityMarkerHandler.removeCallbacks(mHideFacilityMarker);
                            }
                            if (facilityMarker != null) {
                                facilityMarker.remove();
                            }
                            facilityMarker = googlemap.addMarker(new MarkerOptions().position(latlon).icon(locicon).anchor(0.5f, 0.9f).zIndex(6));
                            hideFacilityMarkerHandler = new Handler();
                            hideFacilityMarkerHandler.postDelayed(mHideFacilityMarker, 10000);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    Runnable mHideFacilityMarker = new Runnable() {
        @Override
        public void run() {
            try {
                if (facilityMarker != null) {
                    facilityMarker.remove();
                    facilityMarker = null;
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }

        }
    };


    private boolean isLocationVisible(ILocation location) {
        return Location.isOutDoor(location) || isContentVisible(location.getFacilityId(), location.getZ());
    }

    public int getPresentedFloor() {
        return getPresentedFloorId(getFloorsPickerFacility().getId());
    }

    private Runnable сampusPathDrawingLogic;

    private void updatePath(final List<CampusNavigationPath> outpath) {
        removeCallbacks(сampusPathDrawingLogic);
        сampusPathDrawingLogic = new Runnable() {
            @Override
            public void run() {
                try {
                    removeNavigationPath();
                    for (CampusNavigationPath navpath : outpath) {
                        List<GisSegment> segments = navpath.getPath();
                        if (segments.size() > 0) {
                            int color = Color.parseColor(PropertyHolder.getInstance().getNavRouteColor());
                            float zindex = 5;
                            float width = PropertyHolder.getInstance().getNavPathWidth();
                            List<LatLng> points = new ArrayList<LatLng>();
                            for (GisSegment o : segments) {
                                LatLng l1 = new LatLng(o.getLine().getPoint1().getY(), o.getLine().getPoint1().getX());
                                LatLng l2 = new LatLng(o.getLine().getPoint2().getY(), o.getLine().getPoint2().getX());
                                if (segments.indexOf(o) == 0) {
                                    points.add(l1);
                                }

                                points.add(l2);

                            }
                            PolylineOptions polyoptions = new PolylineOptions().zIndex(zindex).color(color).width(width);
                            polyoptions.addAll(points);
                            if (PropertyHolder.getInstance().isDisplayDashedNavigationRoute()) {
                                polyoptions.pattern(PATTERN_POLYLINE);
                            }
                            if (PropertyHolder.getInstance().isDrawInvisibleFloorsRoute() && getPresentedFloor() != getEntranceFloor()) {
                                polyoptions.pattern(PATTERN_POLYLINE);
                                int dashedcolor = getColorWithAlpha(color, PropertyHolder.getInstance().getVirtualRouteAlpha());
                                polyoptions.color(dashedcolor);
                            }
                            campusPathsMap.put(googlemap.addPolyline(polyoptions), navpath);
                            if (PropertyHolder.getInstance().isDrawArrowsOnPath()) {
                                drawCampusPathArrows(points);
                            }
                            updateNavigationMarkerState();

                        }
                    }
                } catch (Throwable e) {
                    MapDebugHelper.onException(ctx,  e);
                }

            }
        };

        post(сampusPathDrawingLogic);

    }

    protected CircleOptions getCircle(LatLng latlng, int color, float zindex, double radius) {
        CircleOptions result = new CircleOptions()
                .center(latlng)
                .fillColor(color)
                .strokeColor(color)
                .zIndex(zindex)
                .radius(radius);
        return result;
    }

//	public void removeNavigationArrow() {
//		try {
//			if (navarrow != null) {
//				navarrow.remove();
//				navarrow = null;
//			}
//		} catch (Throwable t) {
//			MapDebugHelper.onException(ctx,  t);
//		}
//	}

    public void removeNavigationPath() {
        try {
            if (campusPathsMap != null) {
                for (Polyline o : campusPathsMap.keySet()) {
                    o.remove();
                    o = null;
                }
                campusPathsMap.clear();
            }
            removeCallbacks(сampusPathDrawingLogic);
            if (campusArrows != null && !campusArrows.isEmpty()) {
                for (Marker o : campusArrows) {
                    o.remove();
                }
                campusArrows.clear();
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void notifyGoogleNavigationEnded() {
        try {
            GoogleNavigationUtil.getInstance().notifyEnd();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void setIndoorNavigation(ILocation loc) {

        try {

            if (loc != null) {
                HashMap<String, NavigationPath> pathmap = RouteCalculationHelper.getInstance().getIndoorNavPaths();
                String facid = loc.getFacilityId();

                if (pathmap != null && !pathmap.isEmpty()) {
                    for (String o : pathmap.keySet()) {
                        if (facid != null && facid.equals(o)) {
                            NavigationPath shortpath = pathmap.get(o);
                            if (shortpath != null) {
                                aStarData.getInstance().setCurrentPath(shortpath);
                                notifyOnNavigationStateChanged(NavigationState.STARTED);
                                RouteCalculationHelper.getInstance().setInstructions(shortpath, facid);
                                setFirstInstruction();
                            }
                        }
                        setFloorNavPath(o);
                    }

                    removeNavigationPath();
                    List<CampusNavigationPath> navpath = RouteCalculationHelper.getInstance().getOutdoorNavPath();
                    if (navpath != null && !navpath.isEmpty()) {
                        updatePath(navpath);
                    }
                } else {
                    reRoute();
                    notifyOnNavigationStateChanged(NavigationState.REROUTE);
                }

            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void setFloorNavPath(String facilityId) {
        try {
            NavigationPath path = RouteCalculationHelper.getInstance().getFacilityPath(facilityId);
            if (path != null && facilityId != null) {
                String pathfacilityid = path.getFacilityId();
                if (facilityId.equals(pathfacilityid)) {
                    List<List<GisSegment>> paths = getPresentedFloorPath(path, facilityId);
                    int presentedfloor = getPresentedFloorId(facilityId);
                    boolean hasrealroutefloor = false;
                    if (paths != null
                            && floorDisplayPolicy.displayFloorContent(facilityId,  presentedfloor)) {
                        hasrealroutefloor = true;
                        List<List<GisSegment>> pathtodraw = new ArrayList<>();
                        for (List<GisSegment> l : paths) {
                            List<GisSegment> p = convertPath(l, facilityId);
                            pathtodraw.add(p);
                        }

                        if (pathtodraw != null) {
                            DrawPath(pathtodraw, facilityId);
                        }
                    } else {
                        removeFacilityPath(facilityId);
                    }
                    if (PropertyHolder.getInstance().isDrawInvisibleFloorsRoute()) {
                        drawInvisibleFloorsRoutes(path, facilityId, presentedfloor, hasrealroutefloor);
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private List<List<GisSegment>> getPresentedFloorPath(NavigationPath shortPath,
                                                   String facid) {
        List<List<GisSegment>> result = new ArrayList<>();
        try {
            FacilityOverlay fo = facilityOverlays.get(facid);
            if (fo != null) {
                int z = fo.getFloor();
                List<FloorNavigationPath> floorspaths = shortPath.getFullPath();
                if (floorspaths != null && !floorspaths.isEmpty()) {
                    for (FloorNavigationPath o : floorspaths) {
                        if (o.getZ() == z) {
                            result.add(o.getPath());
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    private List<GisSegment> convertPath(List<GisSegment> path, String facid) {

        List<GisSegment> result = new ArrayList<GisSegment>();
        try {
            for (GisSegment o : path) {
                GisSegment segment = convertSegment(facid, o);
                if (segment != null) {
                    result.add(segment);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    private GisSegment convertSegment(String facid, GisSegment o) {
        GisSegment result = null;
        GisLine oline = o.getLine();
        if (oline != null) {
            GisPoint op1 = oline.getPoint1();
            GisPoint op2 = oline.getPoint2();
            int z = (int) oline.getZ();
            if (op1 != null && op2 != null) {
                LatLng converted1 = convertToLatlng(op1.getX(), op1.getY(),
                        z, facid);
                LatLng converted2 = convertToLatlng(op2.getX(), op2.getY(),
                        z, facid);
                if (converted1 != null && converted2 != null) {
                    GisPoint p1 = new GisPoint(converted1.longitude,
                            converted1.latitude, z);
                    GisPoint p2 = new GisPoint(converted2.longitude,
                            converted2.latitude, z);
                    GisLine line = new GisLine(p1, p2, z);
                    result = new GisSegment(line, segmentId++);
                }
            }
        }

        return result;
    }

    private void DrawPath(List<List<GisSegment>> segmentslist, String facilityId) {
        try {
            if (facilityId != null) {
                List<Polyline> facilitypath = facilitiesNavPaths.get(facilityId);
                if (facilitypath != null) {
                    for (Polyline p : facilitypath) {
                        clearSelectedInstruction(p);
                        p.remove();
                        p = null;
                    }
                    facilitiesNavPaths.remove(facilityId);
                }

                if (!segmentslist.isEmpty()) {
                    List<Polyline> pl = new ArrayList<>();
                    for (List<GisSegment> segments : segmentslist) {
                        if (segments != null && !segments.isEmpty()) {
                            int color = Color.parseColor(PropertyHolder.getInstance().getNavRouteColor());
                            float zindex = 5;
                            float width = PropertyHolder.getInstance().getNavPathWidth();
                            List<LatLng> points = new ArrayList<LatLng>();
                            for (GisSegment o : segments) {
                                LatLng l1 = new LatLng(o.getLine().getPoint1().getY(), o.getLine().getPoint1().getX());
                                LatLng l2 = new LatLng(o.getLine().getPoint2().getY(), o.getLine().getPoint2().getX());
                                if (segments.indexOf(o) == 0) {
                                    points.add(l1);
                                }
                                points.add(l2);

                            }


                            PolylineOptions polyoptions = new PolylineOptions().zIndex(zindex).color(color).width(width);
                            polyoptions.addAll(points);
                            if (PropertyHolder.getInstance().isDisplayDashedNavigationRoute()) {
                                polyoptions.pattern(PATTERN_POLYLINE);
                            }
                            Polyline path = googlemap.addPolyline(polyoptions);
                            pl.add(path);
                            if (PropertyHolder.getInstance().isDrawArrowsOnPath()) {
                                drawFacilityPathArrows(points, facilityId);
                            }
                        }
                    }
                    facilitiesNavPaths.put(facilityId, pl);
                }
                updateNavigationMarkerState();
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private void drawInvisibleFloorsRoutes(NavigationPath path, String facilityId, int presentedfloor, boolean hasrealroutefloor) {
        if (path != null && facilityId != null) {
            List<FloorNavigationPath> floors = path.getFullPath();
            if (floors != null) {
                for (FloorNavigationPath o : floors) {
                    if (!hasrealroutefloor || o.getZ() != presentedfloor) {
                        List<List<GisSegment>> plist = path.getPathByZ(o.getZ());
                        List<List<GisSegment>> pathtodraw = new ArrayList<>();
                        for (List<GisSegment> l : plist) {
                            List<GisSegment> converted = convertPath(l, facilityId);
                            pathtodraw.add(converted);
                        }
                        drawInvisibleFloorRoute(pathtodraw, facilityId, (int)o.getZ());
                    }
                }
            }
        }
    }

    private void drawInvisibleFloorRoute(List<List<GisSegment>> segmentslist, String facilityId, int floor) {
        try {
            if (facilityId != null) {
                Map<Integer, List<Polyline>> facpath = invisibleFloorsRoutes.get(facilityId);
                if (facpath != null) {
                    List<Polyline> fpaths = facpath.get(floor);
                    if (fpaths != null) {
                        for (Polyline floorpath : fpaths) {
                            if (floorpath != null) {
                                floorpath.remove();
                                floorpath = null;
                            }
                        }
                        fpaths.clear();
                        facpath.remove(floor);
                    }
                }

                if (!segmentslist.isEmpty()) {
                    List<Polyline> pl = new ArrayList<>();
                    for (List<GisSegment> segments : segmentslist) {
                        if (segments != null && !segments.isEmpty()) {
                            int color = Color.parseColor(PropertyHolder.getInstance().getNavRouteColor());
                            int dashedcolor = getColorWithAlpha(color, PropertyHolder.getInstance().getVirtualRouteAlpha());
                            float zindex = 5;
                            float width = PropertyHolder.getInstance().getNavPathWidth();
                            List<LatLng> points = new ArrayList<LatLng>();
                            for (GisSegment o : segments) {
                                LatLng l1 = new LatLng(o.getLine().getPoint1().getY(), o.getLine().getPoint1().getX());
                                LatLng l2 = new LatLng(o.getLine().getPoint2().getY(), o.getLine().getPoint2().getX());
                                if (segments.indexOf(o) == 0) {
                                    points.add(l1);
                                }
                                points.add(l2);

                            }


                            PolylineOptions polyoptions = new PolylineOptions().zIndex(zindex).color(dashedcolor).width(width).pattern(PATTERN_POLYLINE);
                            polyoptions.addAll(points);
                            Polyline path = googlemap.addPolyline(polyoptions);
                            pl.add(path);
                            if (facpath == null) {
                                facpath = new HashMap<>();
                            }
                            facpath.put(floor, pl);

                            if (PropertyHolder.getInstance().isDrawArrowsOnPath()) {
                                drawFacilityPathArrows(points, facilityId);
                            }

                        }
                    }
                    invisibleFloorsRoutes.put(facilityId, facpath);
                }

            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private int getColorWithAlpha(int color, float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }

    private void removeFacilityPath(String facilityId) {
        try {
            if (facilityId != null) {
                List<Polyline> facilitypath = facilitiesNavPaths.get(facilityId);
                for (Polyline path : facilitypath) {
                    if (path != null) {
                        clearSelectedInstruction(path);
                        path.remove();
                        path = null;
                    }
                }
                facilitypath.clear();
                facilitiesNavPaths.remove(facilityId);
                removeFaciliyArrows(facilityId);

                Map<Integer, List<Polyline>> dashedpath = invisibleFloorsRoutes.get(facilityId);
                for (List<Polyline> lp : dashedpath.values()) {
                    for (Polyline o : lp) {
                        if (o != null) {
                            o.remove();
                            o = null;
                        }
                    }
                    lp.clear();
                }
                dashedpath.clear();
                invisibleFloorsRoutes.remove(facilityId);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void removeFacilityDashedPath(String facilityId) {
        try {
            if (facilityId != null) {
                Map<Integer, List<Polyline>> dashedpath = invisibleFloorsRoutes.get(facilityId);
                for (List<Polyline> lp : dashedpath.values()) {
                    for (Polyline o : lp) {
                        if (o != null) {
                            o.remove();
                            o = null;
                        }
                    }
                    lp.clear();
                }
                dashedpath.clear();
                invisibleFloorsRoutes.remove(facilityId);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    @Override
    public void navigateTo(ILocation destination) {
        try {
            soundPlayerHelper.reset();
            if (destination != null && destination.getLocationType() != null) {
                if (destination.getLocationType() == LocationMode.INDOOR_MODE) {
                    PointF point = new PointF((float) destination.getX(), (float) destination.getY());
                    PoiData indoorpoi = new PoiData(point);
                    indoorpoi.setZ(destination.getZ());
                    indoorpoi.setCampusID(destination.getCampusId());
                    if (destination.getFacilityId() != null) {
                        indoorpoi.setFacilityID(destination.getFacilityId());
                        if (indoorpoi != null) {
                            navigateTo(indoorpoi);
                        }
                    }
                } else if (destination.getLocationType() == LocationMode.OUTDOOR_MODE) {
                    double lat = destination.getLat();
                    double lon = destination.getLon();
                    PoiData outdoorpoi = new PoiData();
                    outdoorpoi.setPoiLatitude(lat);
                    outdoorpoi.setPoiLongitude(lon);
                    outdoorpoi.setPoiNavigationType("external");
                    if (outdoorpoi != null) {
                        navigateTo(outdoorpoi);
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    @Override
    public void simulateNavigationTo(IPoi poi) {
        try {
            if (poi != null) {
                ILocation loc = LocationFinder.getInstance().getCurrentLocation();
                if (loc == null || loc.getCampusId() == null || loc.getCampusId().equals("unknown")) {
                    Campus campus = ProjectConf.getInstance().getSelectedCampus();
                    if (campus != null) {
                        LatLng l = campus.getDefaultLatlng();
                        if (l != null) {
                            loc = new Location(l);
                            simulateNavigationTo(loc, poi);
                        }
                    }
                } else {
                    simulateNavigationTo(loc, poi);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    @Override
    public void simulateNavigationTo(ILocation origin, IPoi poi) {
        try {
            if (numberPicker != null && PropertyHolder.getInstance().isFloorPickerVisibile()) {
                numberPicker.setEnabled(false);
            }
            SimulationPlayer.getInstance().stopPlaying();
            stopNavigation();
            Location start = null;
            PoiData destination = null;

            if (origin != null && origin instanceof Location) {
                start = (Location) origin;
            } else {
                start = new Location(origin);
            }

            if (poi != null && poi instanceof PoiData) {
                destination = (PoiData) poi;
            }

            final Location fstart = start;
            final PoiData fdestination = destination;

            navigateTo(fstart, fdestination, true);

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void navigateTo(Location origin, PoiData destination) {
        navigateTo(origin, destination, false);
    }

    private void navigateTo(Location origin, PoiData destination, boolean isSimulation) {
        try {
            soundPlayerHelper.reset();
            long startnavigationtime = System.currentTimeMillis();
            PropertyHolder.getInstance().setStartNavigationTime(startnavigationtime);
            outdoorInstructionNotified = false;
            if (origin != null && destination != null) {
                if (navigationTask != null) {
                    navigationTask.cancel(true);
                }
                navigationTask = new ServerNavigationTask(ctx, this, origin, destination);
                navigationTask.setSimulation(isSimulation);
                navigationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                PropertyHolder.getInstance().setNavigationState(true);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    @Override
    public void simulateNavigationToParking(ILocation origin) {
        try {
            ILocation parkingloc = ParkingUtil.getInstance().getParkingLocation();
            if (parkingloc != null) {
                IPoi parkingpoi = new PoiData();
                parkingpoi.setLocation(parkingloc);
                simulateNavigationTo(origin, parkingpoi);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    @Override
    public void stopNavigation() {
        try {
            if (navigationTask != null) {
                navigationTask.cancel(true);
                navigationTask = null;
            }
            removeNavigationPath();
            removeAllFacilityPath();
            RouteCalculationHelper.getInstance().clean();
            if (instructionLayout.getVisibility() == View.VISIBLE) {
                instructionLayout.setVisibility(View.INVISIBLE);
            }
            if (PropertyHolder.getInstance().isNavigationState()) {
                notifyOnNavigationStateChanged(NavigationState.STOPED);
                PropertyHolder.getInstance().setNavigationState(false);
            }

            updateNavigationMarkerState();

            counterForFacilityReroute = 0;
            counterForFloorReroute = 0;
            counterForIndoorReroute = 0;
            counterForOutdoorReroute = 0;

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void removeAllFacilityPath() {
        try {
            for (String o : facilitiesNavPaths.keySet()) {
                if (o != null) {
                    List<Polyline> facilitypath = facilitiesNavPaths.get(o);
                    for (Polyline path : facilitypath) {
                        if (path != null) {
                            clearSelectedInstruction(path);
                            path.remove();
                            path = null;
                        }
                    }
                    facilitypath.clear();
                }
            }
            facilitiesNavPaths.clear();

            for (Map<Integer, List<Polyline>> p : invisibleFloorsRoutes.values()) {
                for (List<Polyline> lp : p.values()) {
                    for (Polyline o : lp) {
                        if (o != null) {
                            o.remove();
                            o = null;
                        }
                    }
                    lp.clear();
                }
                p.clear();
            }
            invisibleFloorsRoutes.clear();

            for (String o : facilitiesArrows.keySet()) {
                if (o != null) {
                    List<Marker> arrows = facilitiesArrows.get(o);
                    if (arrows != null) {
                        for (Marker a : arrows) {
                            a.remove();
                        }
                    }
                }
            }
            facilitiesArrows.clear();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    //    @Override
//    public void presentFacility(String campusId, String facilityId) {
//        try {
//            if (campusId != null && facilityId != null) {
//                Campus campus = ProjectConf.getInstance().getCampus(campusId);
//                FacilityConf fac = campus.getFacilityConf(facilityId);
//                LatLng latlng = new LatLng(fac.getCenterLatitude(), fac.getCenterLongtitude());
//                Location facLocation = new Location(latlng);
//                presentLocation(facLocation);
//            }
//        } catch (Throwable t) {
//            MapDebugHelper.onException(ctx,  t);
//        }
//    }

    @Override
    public void presentFacility(String campusId, String facilityId) {
        try {
            if (campusId != null && facilityId != null) {
                Campus campus = ProjectConf.getInstance().getCampus(campusId);
                FacilityConf fac = campus.getFacilityConf(facilityId);
                List<LatLng> points = new ArrayList<>();
                LatLng tl = new LatLng(fac.getConvRectTLlat(), fac.getConvRectTLlon());
                points.add(tl);
                LatLng tr = new LatLng(fac.getConvRectTRlat(), fac.getConvRectTRlon());
                points.add(tr);
                LatLng br = new LatLng(fac.getConvRectBRlat(), fac.getConvRectBRlon());
                points.add(br);
                LatLng bl = new LatLng(fac.getConvRectBLlat(), fac.getConvRectBLlon());
                points.add(bl);
                ShowPoints(points);
                LatLng centerpoint = computeCenter(points);
                showFacilityMarker(facilityId, centerpoint);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }


    private LatLng computeCenter(List<LatLng> polygonPointsList){
        LatLng centerLatLng = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0 ; i < polygonPointsList.size() ; i++)
        {
            builder.include(polygonPointsList.get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng =  bounds.getCenter();

        return centerLatLng;
    }


    @Override
    public void presentCampus(String CampusId) {
        try {
            if (CampusId != null) {
                Campus campus = ProjectConf.getInstance().getCampus(CampusId);
                LatLng latlng = new LatLng(campus.getCenterLatitude(), campus.getCenterLongtitude());
                Location facLocation = new Location(latlng);
                presentLocation(facLocation);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    @Override
    public int getPresentedFloorId(String facilityId) {

        int result = 0;
        try {
            if (facilityId != null) {
                FacilityOverlay fo = facilityOverlays.get(facilityId);
                if (fo != null) {
                    result = fo.getFloor();
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    public String getFloorTitleForFloorId(String campusId, String faciltyId, int floorId) {

        String result = null;
        try {
            Campus campus = ProjectConf.getInstance().getCampus(campusId);
            if (campus != null) {
                Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();
                FacilityConf facility = facilitiesmap.get(faciltyId);
                if (facility != null) {
                    FloorData floor = facility.getFloor(floorId);
                    if (floor != null) {
                        result = floor.getTitle();
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    @Override
    public boolean isNavigationState() {
        return PropertyHolder.getInstance().isNavigationState();
    }

    @Override
    public void navigateToParking() {
        try {
            soundPlayerHelper.reset();
            ILocation parkingloc = ParkingUtil.getInstance().getParkingLocation();
            if (parkingloc != null) {
                IPoi parkingpoi = new PoiData();
                parkingpoi.setLocation(parkingloc);
                navigateTo(parkingpoi);
            }
        } catch (Throwable e) {

            MapDebugHelper.onException(ctx,  e);
        }
    }

    @Override
    public void registerNavigationListener(SpreoNavigationListener listener) {
        try {
            if (!navigationListeners.contains(listener)) {
                navigationListeners.add(listener);
            }
        } catch (Throwable e) {
            MapDebugHelper.onException(ctx,  e);
        }
    }

    @Override
    public void unregisterNavigationListener(SpreoNavigationListener listener) {
        try {
            if (navigationListeners.contains(listener)) {
                navigationListeners.remove(listener);
            }
        } catch (Throwable e) {
            MapDebugHelper.onException(ctx,  e);
        }

    }

    private void notifyOnNavigationFailed(NavigationResultStatus status) {
        for (SpreoNavigationListener listener : navigationListeners) {
            if (listener != null) {
                try {
                    listener.OnNavigationFailed(status);
                } catch (Throwable t) {
                    MapDebugHelper.onException(ctx,  t);
                }
            }
        }
    }

    private void notifyOnNavigationStateChanged(NavigationState navigationState) {
        for (SpreoNavigationListener listener : navigationListeners) {
            if (listener != null) {
                try {
                    listener.onNavigationStateChanged(navigationState);
                } catch (Throwable t) {
                    MapDebugHelper.onException(ctx,  t);
                }
            }
        }
    }

    private void notifyArriveToPoi(IPoi poi) {
        for (SpreoNavigationListener listener : navigationListeners) {
            if (listener != null) {
                try {
                    List<IPoi> nextPois = new ArrayList<IPoi>();
                    listener.onNavigationArriveToPoi(poi, nextPois);
                } catch (Throwable t) {
                    MapDebugHelper.onException(ctx,  t);
                }
            }
        }
    }

    private void notifyOnNavigationInstructionChanged(INavInstruction instruction) {
        for (SpreoNavigationListener o : navigationListeners) {
            if (o != null) {
                try {
                    o.OnNavigationInstructionChanged(instruction);
                } catch (Throwable t) {
                    MapDebugHelper.onException(ctx,  t);
                }
            }
        }

    }

    private void notifyOnNavigationInstructionRangeEntered(
            INavInstruction instruction) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reDrawPois() {
        reDrawPois(true);
    }


    //TODO Does this method support hideTopFloor feature?
    public void reDrawPois(boolean considerZoomLevel) {
        try {

            removePoiMarkers();

            drawProjectPOis();

//            if(!considerZoomLevel) { //supporting old behaviour, if !considerZoomLevel will draw all pois (as it happens on zoomLevels > hidingPoisZoomLevel)
//                float hidingPoisZoomLevel = PropertyHolder.getInstance().getHidingPoisZoomLevel();
//                poiPresenter.onZoomChange(
//                        hidingPoisZoomLevel + 0.1f, // any value which is greater than hidingPoiZoomLel
//                        Float.MAX_VALUE //this just should be greater than previous argument
//                );
//            }
        } catch (Throwable e) {
            MapDebugHelper.onException(ctx,  e);
        }
    }

    @Override
    public void closeBubble(IPoi poi) {
        try {
            Marker marker = poisMarkerMap.get(poi);
            if (marker != null) {
                marker.hideInfoWindow();
            }
        } catch (Throwable e) {
            MapDebugHelper.onException(ctx,  e);
        }
    }

    @Override
    public void openMyParkingMarkerBubble() {
        try {
            if (parkingmarker != null) {
                setShowAutoBubblesTimer();
                View customview = null;
                for (SpreoDualMapViewListener o : listeners) {
                    if (o != null) {
                        try {
                            View v = o.aboutToOpenParkingBubble();
                            if (v != null) {
                                customview = v;
                            }
                        } catch (Exception e) {
                            MapDebugHelper.onException(ctx,  e);
                        }
                    }
                }

                if (customview != null) {
                    parkingmarker.setBubbleView(customview);
                }
                parkingmarker.showBubble();
            }
        } catch (Throwable e) {
            MapDebugHelper.onException(ctx,  e);
        }
    }

    @Override
    public void closeMyParkingMarkerBubble() {
        try {
            if (parkingmarker != null) {
                parkingmarker.closeBubble();
            }
        } catch (Throwable e) {
            MapDebugHelper.onException(ctx,  e);
        }
    }

    @Override
    public void setMyParkingMarkerIcon(Bitmap icon) {
        try {
            parkingIcon = icon;
            if (parkingmarker != null) {
                parkingmarker.setIcon(parkingIcon);
            }
        } catch (Throwable e) {
            MapDebugHelper.onException(ctx,  e);
        }
    }

    @Override
    public void stopSimulation() {
        try {
            SimulationPlayer.getInstance().stopPlaying();
            PropertyHolder.getInstance().setLocationPlayer(false);
            stopNavigation();
            if (numberPicker != null && PropertyHolder.getInstance().isFloorPickerVisibile()) {
                numberPicker.setEnabled(true);
            }
        } catch (Throwable e) {
            MapDebugHelper.onException(ctx,  e);
        }
    }

    @Override
    public void displayPois(boolean display) {
        try {
            //		this.displayPois=display;
            if (display) {
                showAllPois();
            } else {
                hideAllPois();
            }
//			reDrawPois();
//			for (MarkerObject o : markers.values()) {
//				if (o != null) {
//					o.setVisible(display);
//				}
//			}
//
//			for(IPoi poi:pois.values()){
//				if(poi!=null){
//					poi.setShowPoiOnMap(display);
//				}
//			}

        } catch (Throwable e) {
            MapDebugHelper.onException(ctx,  e);
        }
    }

    @Override
    public boolean hasParkingLocation() {
        boolean result = false;
        try {
            ILocation ploc = ParkingUtil.getInstance().getParkingLocation();
            if (ploc != null) {
                result = true;
            }
        } catch (Throwable e) {
            MapDebugHelper.onException(ctx,  e);
        }
        return result;
    }

    @Override
    public ILocation getParkingLocation() {
        return ParkingUtil.getInstance().getParkingLocation();
    }

    private void loadCampusOverlay() {
        try {

            Campus c = ProjectConf.getInstance().getSelectedCampus();
            if (c != null) {
                campusOverlays.clear();
                List<CampusOverlay> campusOverlayList = c.getOverlaysList();
                for (CampusOverlay co : campusOverlayList) {
                    if (co == null) {
                        continue;
                    }
                    campusOverlays.add(co);
                    drawCampusOverlayOnMap(co, 0, 0);
                }

            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    public void drawCampusOverlayOnMap(CampusOverlay co, float bearingAngle, float transparency) {

        try {
            List<OverlayChunk> chunks = co.getChunkedImages();
            for (OverlayChunk oc : chunks) {
                if (oc != null) {
                    LatLngBounds bounds = oc.getBound();
                    GroundOverlay gOverlay = googlemap
                            .addGroundOverlay(new GroundOverlayOptions()
                                    .image(oc.getBd()).zIndex(1f)
                                    .positionFromBounds(bounds).bearing(bearingAngle)
                                    .transparency(transparency));

                    co.addGroundOverlay(gOverlay);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    public void setIconForPoi(IPoi poi, Bitmap icon) {
        try {
            if (poi != null) {
                poi.setIcon(icon);
                Marker marker = poisMarkerMap.get(poi);
                if (marker != null) {
                    marker.setIcon((BitmapDescriptorFactory.fromBitmap(icon)));
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void setIconForPoiList(List<IPoi> poiList, Bitmap icon) {
        try {
            if (poiList != null) {
                for (IPoi poi : poiList) {
                    setIconForPoi(poi, icon);

                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
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

    public void setFloorPickerUIOptions(FloorPickerUIOptions uioptions) {
        try {
            if (numberPicker != null && PropertyHolder.getInstance().isFloorPickerVisibile()) {
                numberPicker.setUIOptions(uioptions);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    public void returnToDefaultZoom() {
        try {
            if (PropertyHolder.getInstance().isMapAnimation()) {
                googlemap.animateCamera(CameraUpdateFactory.zoomTo(PropertyHolder.getInstance().getDefaultDualMapZoom()), animationDuration,
                        new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            } else {
                googlemap.moveCamera(CameraUpdateFactory.zoomTo(PropertyHolder.getInstance().getDefaultDualMapZoom()));
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void notifyFloorLoad(String campusid, String facilityid, int floor) {
        for (SpreoDualMapViewListener o : listeners) {
            try {
                if (o != null) {
                    o.mapDidLoadFloor(campusid, facilityid, floor);
                    if (floorsPickerFacility != null && facilityid != null) {
                        String id = floorsPickerFacility.getId();
                        if (id != null && id.equals(facilityid)) {
                            o.OnFloorChange(floor);
                        }
                    }
                }
            } catch (Throwable t) {
                MapDebugHelper.onException(ctx,  t);
            }
        }
    }


    public void redrawPolygons() {

        removeAllPolygons();

        drawExternalPolygons();

        for (FacilityOverlay o : facilityOverlays.values()) {
            if (o != null) {
                String id = o.getFacilityId();
                if (id != null) {
                    int floor = o.getFloor();
                    drawFloorPolygons(id, floor);
                }
            }
        }

    }

    private void drawFloorPolygons(String facid, int floor) {
        try {
            List<Polygon> lp = facilityPolygonsMap.get(facid);
            if (lp == null) {
                lp = new ArrayList<>();
                facilityPolygonsMap.put(facid, lp);
            } else {
                removePolygons(lp);
            }

            List<PolygonObject> polygons = ProjectConf.getInstance().getFloorPolygons(facid, floor);
            if (polygons != null) {
                for (PolygonObject o : polygons) {
                    if (o.isVisible() && o.getFillColor() != null) {
                        List<LatLng> l = o.getPolygon();
                        LatLng[] points = l.toArray(new LatLng[l.size()]);
                        Polygon polygon = googlemap.addPolygon(new PolygonOptions()
                                .add(points)
                                .zIndex(2)
                                .strokeWidth(o.getStrokeWidth())
                                .strokeColor(Color.parseColor(o.getBorderColor()))
                                .fillColor(Color.parseColor(o.getFillColor())));
                        setPolygonVisibility(polygon);
                        lp.add(polygon);
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }


    }

    private void drawExternalPolygons() {
        try {
            removePolygons(externalPolygons);
            List<PolygonObject> polygons = ProjectConf.getInstance().getExternalPolygons();
            if (polygons != null) {
                for (PolygonObject o : polygons) {
                    if (o.isVisible() && o.getFillColor() != null) {
                        List<LatLng> l = o.getPolygon();
                        LatLng[] points = l.toArray(new LatLng[l.size()]);
                        Polygon polygon = googlemap.addPolygon(new PolygonOptions()
                                .add(points)
                                .zIndex(2)
                                .strokeWidth(o.getStrokeWidth())
                                .strokeColor(Color.parseColor(o.getBorderColor()))
                                .fillColor(Color.parseColor(o.getFillColor())));
                        setPolygonVisibility(polygon);
                        externalPolygons.add(polygon);
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private void removePolygons(List<Polygon> polygons) {
        try {
            for (Polygon o : polygons) {
                o.remove();
            }
            polygons.clear();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void removeAllPolygons() {
        try {
            removePolygons(externalPolygons);
            for (String o : facilityPolygonsMap.keySet()) {
                if (o != null) {
                    List<Polygon> lp = facilityPolygonsMap.get(o);
                    if (lp != null) {
                        removePolygons(lp);
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }


    public void setVisiblePoisWithIds(List<String> poiIdsList) {
        try {
            if (poiIdsList != null) {
                List<IPoi> pois = ProjectConf.getInstance().getAllPoisList();

                for (IPoi poi : pois) {
                    if (poi == null) {
                        continue;
                    }
                    String id = poi.getPoiID();
                    if (id != null) {
                        if (poiIdsList.contains(id)) {
                            poi.setVisible(true);
                        } else {
                            poi.setVisible(false);
                        }
                    }
                }

                reDrawPois();
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void showAllPois() {
        try {
            List<IPoi> pois = ProjectConf.getInstance().getAllPoisList();

            for (IPoi poi : pois) {
                if (poi == null) {
                    continue;
                }
                poi.setVisible(true);
            }

            reDrawPois();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void hideAllPois() {
        try {
            List<IPoi> pois = ProjectConf.getInstance().getAllPoisList();

            for (IPoi poi : pois) {
                if (poi == null) {
                    continue;
                }
                poi.setVisible(false);
            }

            reDrawPois();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void calculateCampusPath(ILocation myloc) {
        try {
            if (myloc != null) {
                List<CampusNavigationPath> navpath = RouteCalculationHelper.getInstance().getOutdoorNavPath();
                if (navpath != null && !navpath.isEmpty()) {
//                    aStarData.getInstance().setCurrentCampusPath(navpath);
                    updatePath(navpath);

                    removeAllFacilityPath();
                    HashMap<String, NavigationPath> pathmap = RouteCalculationHelper.getInstance().getIndoorNavPaths();
                    if (pathmap != null && !pathmap.isEmpty()) {
                        for (String o : pathmap.keySet()) {
                            setFloorNavPath(o);
                        }
                    }
                } else {
                    reRoute();
                    notifyOnNavigationStateChanged(NavigationState.REROUTE);
                }
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void CheckKmlReroute(ILocation myloc) {
        try {
            if (myloc != null) {
                List<GisSegment> pathlist = new ArrayList<>();
                List<CampusNavigationPath> navpath = RouteCalculationHelper.getInstance().getOutdoorNavPath();
                if (campusPathsMap != null) {
                    for (CampusNavigationPath o : navpath) {
                        pathlist.addAll(o.getPath());
                    }
                }
                if (pathlist != null && !pathlist.isEmpty()) {
                    boolean lineReroute = false;
                    if (pathlist.size() == 1) {
                        lineReroute = true;
                        if (kmlRerouteCounter >= kmlRerouteTresh) {
//								calculateCampusPath(myloc);
                            Runnable r = new Runnable() {

                                @Override
                                public void run() {
                                    reRoute();
                                    notifyOnNavigationStateChanged(NavigationState.REROUTE);
                                }
                            };
                            post(r);

                            kmlRerouteCounter = 0;
                        } else {
                            kmlRerouteCounter++;
                        }
                    }

                    if (!lineReroute) {
                        double mylocationlat = myloc.getLat();
                        double mylocationlon = myloc.getLon();
                        LatLng mylatlng = new LatLng(mylocationlat, mylocationlon);
                        LatLng projectedloc = CampusGisData.getInstance().findClosestPointOnLine(mylatlng, pathlist);
                        double d = MathUtils.distance(mylatlng, projectedloc);
                        if (d > kmlRerouteDistance) {
                            counterForOutdoorReroute++;
                            if (counterForOutdoorReroute >= getCountForReroute()) {
                                Runnable r = new Runnable() {

                                    @Override
                                    public void run() {
                                        reRoute();
                                        notifyOnNavigationStateChanged(NavigationState.REROUTE);
                                    }
                                };
                                post(r);
                                counterForOutdoorReroute = 0;
                            }
                        } else {
                            counterForOutdoorReroute = 0;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void drawKml() {
        try {
            List<GisLine> l = CampusGisData.getInstance().getLines();
            if (l != null && !l.isEmpty()) {
                List<PolylineOptions> plines = new ArrayList<PolylineOptions>();
                for (GisLine o : l) {
                    PolylineOptions polyoptions = new PolylineOptions().zIndex(
                            4).color(Color.parseColor("#ffff00"));
                    polyoptions.add(new LatLng(o.getPoint1().getY(), o.getPoint1().getX())).add(new LatLng(o.getPoint2().getY(), o.getPoint2().getX()));
                    plines.add(polyoptions);
                }
                for (PolylineOptions o : plines) {
                    if (o != null) {
                        googlemap.addPolyline(o);
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void drawDebugPoints(String facid) {
        PointF blepoint = LocationLocator.getInstance().getLastAverage();
        if (blepoint != null) {
            LatLng blatlng = convertToLatlng(blepoint.x, blepoint.y, 0, facid);
            if (blatlng != null) {
                int color = Color.parseColor("#ff0000");
                drawBlePoint(blatlng, color);
            }
        }

        PointF rubberpoint = LocationFinder.getInstance().getRubberFilterPoint();
        if (rubberpoint != null) {
            LatLng blatlng = convertToLatlng(rubberpoint.x, rubberpoint.y, 0, facid);
            if (blatlng != null) {
                int color = Color.parseColor("#0000ff");
                drawRubberPoint(blatlng, color);
            }
        }
    }

    private void drawRubberPoint(LatLng latlng, int color) {
        if (rubberPoint == null) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(latlng)
                    .fillColor(color)
                    .strokeColor(color)
                    .zIndex(10f)
                    .radius(0.5);
            rubberPoint = googlemap.addCircle(circleOptions);
        } else {
            rubberPoint.setCenter(latlng);
        }

    }

    private void drawBlePoint(LatLng latlng, int color) {
        if (blePoint == null) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(latlng)
                    .fillColor(color)
                    .strokeColor(color)
                    .zIndex(10f)
                    .radius(0.5);
            blePoint = googlemap.addCircle(circleOptions);
        } else {
            blePoint.setCenter(latlng);
        }

    }

    private void reRoute() {
        try {
            if (!shouldDisableReroute() && shouldReroute()) {
                PoiData poi = RouteCalculationHelper.getInstance().getFinalDestination();
                navigateTo(poi, true);
                if (PropertyHolder.getInstance().isUseTurnBackInstruction() && shouldTurnBack()) {
                    int txtid = ResourceTranslator.getInstance().getTranslatedResourceId("string", "turn_back");
                    String turnback = getResources().getString(txtid);
                    Toast.makeText(ctx, turnback, Toast.LENGTH_SHORT).show();
                    if (!PropertyHolder.getInstance().isNavigationInstructionsSoundMute()) {
                        List<String> sounds = new ArrayList<>();
                        sounds.add("turnback");
                        SoundPlayer.getInstance().play(sounds);
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private boolean shouldReroute() {
        boolean result = false;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastReroute > (timeForReroute-100)) {
            result = true;
            lastReroute = System.currentTimeMillis();
        }
        return result;
    }

    private boolean shouldDisableReroute() {
        boolean result = false;
        try {
            ILocation loc = LocationFinder.getInstance().getCurrentLocation();
            if (loc != null && loc.getLocationType() == LocationMode.INDOOR_MODE && loc.getFacilityId() != null) {
                List<GeoFenceObject> glist = GeoFenceHelper.getInstance().getNoRerouteGeofences(loc.getFacilityId(), (int) loc.getZ());
                for (GeoFenceObject o : glist) {
                    if (o.isContains((float) loc.getX(), (float) loc.getY())) {
                        result = true;
                        break;
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    private void setFirstInstruction() {
        try {
            List<Instruction> instructionsList = InstructionBuilder.getInstance().getCurrentInstructions();
            if (instructionsList != null && !instructionsList.isEmpty()) {
                Instruction ins = instructionsList.get(0);
                if (ins != null) {
                    FacilityConf FacilityConf = FacilityContainer.getInstance().getCurrent();
                    Instruction instruction = null;
                    if (ins.getType() == Instruction.TYPE_DESTINATION) {
                        instruction = getExitInstruction(ins, FacilityConf.getId());
                    } else {
                        instruction = ins;
                    }

                    if (instruction == null) {
                        instruction = ins;
                    }


                    InstructionBuilder.getInstance().setNextInstruction(instruction);
                    instructionLayout.setVisibility((PropertyHolder.getInstance().isDisplayNavigationInstructionsOnMap() ? View.VISIBLE : View.INVISIBLE));


                    NavInstruction inavInst = null;

                    if (PropertyHolder.getInstance().isSimplifiedInstruction()) {
                        inavInst = instruction.getSimplifiedInstruction();
                        updateNavBubble(inavInst);
                    } else {
                        updateNavBubble(instruction);
                    }

                    if (inavInst == null) {
                        inavInst = InstructionBuilder.getInstance().getNextNavInstruction(instruction, true);
                    }

                    if (inavInst != null) {
                        notifyOnNavigationInstructionChanged(inavInst);
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void notifyMapDidLoad() {
        for (SpreoDualMapViewListener o : listeners) {
            try {
                if (o != null) {
                    o.mapDidLoad();
                }
            } catch (Throwable t) {
                MapDebugHelper.onException(ctx,  t);
            }
        }
    }

    private void setFollowMeFalse() {
        followMeMode = false;
        lastFollowMeTime = System.currentTimeMillis();
    }

    private void checkFollowMeMode() {
        if (!followMeMode) {
            long interval = PropertyHolder.getInstance().getUserAutoFollowTimeInterval();
            long currenttime = System.currentTimeMillis();
            if (interval != -1 && currenttime - lastFollowMeTime > interval) {
                followMeMode = true;
            }
        }
    }

    private void animateMarkerTo(Marker marker, LatLng finalPosition) {
        try {
            TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
                @Override
                public LatLng evaluate(float fraction, LatLng a, LatLng b) {
                    double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                    double lng = (b.longitude - a.longitude) * fraction + a.longitude;
                    return new LatLng(lat, lng);
                }
            };
            Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
            ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);
            animator.setDuration(300);
            animator.start();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    // ==== start labels
    private void drawLabelOverlays(Campus campus, FacilityConf facility, int floor) {
        try {

            removeLabelOverlays(facility.getId());

            if(floorDisplayPolicy.displayFloorContent(facility, floor)) {
				String campusId = campus.getId();
				String facilityId = facility.getId();
				List<ILabel> labelDataList = ProjectConf.getInstance().getAllFloorLabelsList(campusId, facilityId, floor);

				for (ILabel l : labelDataList) {

					if (l == null) {
						continue;
					}

					if (l instanceof LabelOverlay) {
						LabelOverlay label = new LabelOverlay((LabelOverlay) l);
						label.prepareOverlay(googlemap, facility);
						labelOverlayList.add(label);
						GroundOverlay go = label.getGOverlay();
						if (go != null) {
							overlayToLabelMap.put(go, label);
						}
					}


				}
			}
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }


    }

    private void cleanLabelsOvelay() {
        try {
            overlayToLabelMap.clear();

            for (ILabel labelOvelay : labelOverlayList) {

                if (labelOvelay == null) {
                    continue;
                }

                labelOvelay.removeFromMap();
            }

            labelOverlayList.clear();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private void removeLabelOverlays(String facid) {
        try {


            List<ILabel> ovelaystoremove = new ArrayList<ILabel>();
            for (ILabel labelOvelay : labelOverlayList) {
                if (labelOvelay == null) {
                    continue;
                }
                String ofId = labelOvelay.getFacilityId();
                if (ofId != null && ofId.equals(facid)) {
                    labelOvelay.removeFromMap();
                    ovelaystoremove.add(labelOvelay);
                }
            }


            for (ILabel o : ovelaystoremove) {
                if (o != null) {
                    overlayToLabelMap.remove(o.getGOverlay());
                    labelOverlayList.remove(o);
                }
            }

            ovelaystoremove.clear();
            ovelaystoremove = null;

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    private void updateLabelOverlaysBearing(float bearing) {
        try {
            for (ILabel labelOvelay : labelOverlayList) {
                if (labelOvelay == null) {
                    continue;
                }
                labelOvelay.setRotation(bearing);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    // ==== end labels

    @Override
    public void onGroundOverlayClick(GroundOverlay goverlay) {

        ILabel lblOverlay = overlayToLabelMap.get(goverlay);

        if (lblOverlay != null) {
            for (SpreoDualMapViewListener o : listeners) {
                try {
                    if (o != null) {
                        o.onLabelClick(lblOverlay);
                    }
                } catch (Throwable t) {
                    MapDebugHelper.onException(ctx,  t);
                }
            }
        }

    }

    public void setCustomMarkers(List<ICustomMarker> markerslist) {
        CustomMarkersManager.getInstance().setCustomMarkers(markerslist);
        DrawCustomMarkers();
    }

    private void DrawCustomMarkers() {
        try {
            removeCustomMarkers();
            List<ICustomMarker> tmp = CustomMarkersManager.getInstance().getCustomMarkers();
            if (tmp != null && !tmp.isEmpty()) {
                for (ICustomMarker customMarker : tmp) {
                    LocationMode mode = customMarker.getLocationMode();
                    if (mode != null) {
                        if (mode == LocationMode.OUTDOOR_MODE) {
                            CustomMarkerObject marker = new CustomMarkerObject(customMarker, ctx, googlemap);
                            customObjectsToMarkers.put(customMarker, marker);
                            customMarkersToObjects.put(marker.getMarker(), customMarker);
                        } else if (mode == LocationMode.INDOOR_MODE) {
                            if (isContentVisible(customMarker.getFacilityId(), customMarker.getFloor())) {
                                LatLng latlng = convertToLatlng(customMarker.getX(), customMarker.getY(), customMarker.getFloor(), customMarker.getFacilityId());
                                CustomMarkerObject marker = new CustomMarkerObject(customMarker, ctx, googlemap, latlng);
                                customObjectsToMarkers.put(customMarker, marker);
                                customMarkersToObjects.put(marker.getMarker(), customMarker);
                            } else if((PropertyHolder.getInstance().isDrawInvisibleNavMarkers() && customMarker.getId().startsWith(ICustomMarker.navPrefix))) {
                                LatLng latlng = convertToLatlng(customMarker.getX(), customMarker.getY(), customMarker.getFloor(), customMarker.getFacilityId());
                                CustomMarkerObject marker = new CustomMarkerObject(customMarker, ctx, googlemap, latlng, PropertyHolder.getInstance().getVirtualRouteAlpha());
                                customObjectsToMarkers.put(customMarker, marker);
                                customMarkersToObjects.put(marker.getMarker(), customMarker);
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private boolean isContentVisible(String contentFacilityID, double contentFloor){
        return isContentVisible(contentFacilityID, (int) contentFloor);
    }

    private boolean isContentVisible(String contentFacilityID, int contentFloor){
        if (contentFacilityID != null) { // we had this check before, looks like some "content" might not have facility id for some reason
            return facilityOverlays.get(contentFacilityID) != null // we have loaded overlay for facility
                    && floorDisplayPolicy.displayFloorContent(contentFacilityID, contentFloor); // and we don't want to hide content of the top facility floor when currently selected floor is greater then the top facility floor
        }
        return false;
    }

    private void removeCustomMarkers() {
        try {
            List<ICustomMarker> Objectstoremove = new ArrayList<ICustomMarker>();
            List<CustomMarkerObject> markerstoremove = new ArrayList<CustomMarkerObject>();
            for (ICustomMarker o : customObjectsToMarkers.keySet()) {
                CustomMarkerObject m = customObjectsToMarkers.get(o);
                m.removeMarkerFromMap();
                Objectstoremove.add(o);
                markerstoremove.add(m);
            }

            for (ICustomMarker o : Objectstoremove) {
                customObjectsToMarkers.remove(o);
                o = null;
            }

            for (CustomMarkerObject o : markerstoremove) {
                customMarkersToObjects.remove(o);
                o = null;
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    public void registerCustomMarkerListener(SpreoCustomMarkersListener listener) {
        if (listener != null && !customMarkerListeners.contains(listener)) {
            customMarkerListeners.add(listener);
        }
    }

    public void unregisterCustomMarkerListener(SpreoCustomMarkersListener listener) {
        if (listener != null && customMarkerListeners.contains(listener)) {
            customMarkerListeners.remove(listener);
        }
    }

    private void notifyCustomMarkerClick(ICustomMarker marker) {
        if (marker != null) {
            for (SpreoCustomMarkersListener o : customMarkerListeners) {
                if (o != null) {
                    try {
                        o.onCustomMarkerClick(marker);
                    } catch (Throwable t) {
                        MapDebugHelper.onException(ctx,  t);
                    }
                }
            }
        }
    }

    private void notifyCustomMarkerBubbleClick(ICustomMarker marker) {
        if (marker != null) {
            for (SpreoCustomMarkersListener o : customMarkerListeners) {
                if (o != null) {
                    try {
                        o.onCustomMarkerBubbleClick(marker);
                    } catch (Throwable t) {
                        MapDebugHelper.onException(ctx,  t);
                    }
                }
            }
        }
    }

    public void openCustomMarkerBubble(ICustomMarker marker) {
        try {
            if (marker != null) {
                CustomMarkerObject cmo = customObjectsToMarkers.get(marker);
                if (cmo != null) {
                    View customview = null;
                    for (SpreoCustomMarkersListener o : customMarkerListeners) {
                        try {
                            View v = o.aboutToOpenCustomMarkerBubble(marker);
                            if (v != null) {
                                customview = v;
                            }
                        } catch (Throwable t) {
                            MapDebugHelper.onException(ctx,  t);
                        }
                    }

                    if (customview != null) {
                        cmo.setBubbleView(customview);
                    }

                    cmo.showBaubble();
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void closeCustomMarkerBubble(ICustomMarker marker) {
        try {
            if (marker != null) {
                CustomMarkerObject cmo = customObjectsToMarkers.get(marker);
                if (cmo != null) {
                    cmo.closeBubble();
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void createFloorsPicker(FacilityConf facility) {
        try {

            if (facility != null) {
                List<Integer> floors = new ArrayList<Integer>();
                for (FloorData o : facility.getFloorDataList()) {
                    int index = facility.getFloorDataList().indexOf(o);
                    floors.add(index);
                }

                if (floors != null && !floors.isEmpty()) {
                    ArrayList<String> floorsListnumbers = new ArrayList<String>();
                    for (int i = 0; i < floors.size(); i++) {
                        floorsListnumbers.add(facility.getFloorTitle(i));
                    }
                    floorstxtArr = new String[floorsListnumbers.size()];
                    for (int i = floorsListnumbers.size() - 1; i >= 0; i--) {
                        floorstxtArr[floorsListnumbers.size() - 1 - i] = floorsListnumbers
                                .get(i);
                    }
                    int maxfloor = floors.size() - 1;
                    int minFloor = 0;
                    numberPicker.setVisibility(View.GONE);
                    numberPicker.setDisplayedValues(null);

                    numberPicker.setMaxValue(maxfloor);
                    numberPicker.setMinValue(minFloor);
                    numberPicker.setDisplayedValues(floorstxtArr);
                    if (PropertyHolder.getInstance().isFloorPickerVisibile()) {
                        numberPicker.setVisibility(View.VISIBLE);
                    }
                    floorsControlbuttons.setVisibility(View.VISIBLE);

                    FacilityOverlay foverlay = facilityOverlays.get(facility
                            .getId());
                    if (foverlay != null) {
                        int f = foverlay.getFloor();
                        numberPicker.setValue(floorstxtArr.length - f - 1);
                        // numberPicker.invalidate();
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void checkFloorPickerVisibility() {
        try {
            if (PropertyHolder.getInstance().isFloorPickerVisibile()) {
                boolean visible = false;
                Projection p = googlemap.getProjection();
                VisibleRegion vr = p.getVisibleRegion();
                LatLngBounds visiblebounds = vr.latLngBounds;
                for (FacilityOverlay o : facilityOverlays.values()) {
                    LatLngBounds facilitybounds = o.getBounds();
                    if (intersects(visiblebounds, facilitybounds)) {
                        visible = true;
                        break;
                    }

                }

                if (visible) {
//					if (floorsPickerFacility == null) {
//						FacilityConf fac = getFloorsPickerFacility();
//						if (fac != null) {
//							floorsPickerFacility = fac;
//							createFloorsPicker(fac);
//						}
//					}
                    floorsControlbuttons.setVisibility(View.VISIBLE);
                } else {
                    floorsControlbuttons.setVisibility(View.INVISIBLE);
                }

            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private FacilityConf getFloorsPickerFacility() {
        FacilityConf result = null;
        try {
            Campus campus = ProjectConf.getInstance().getSelectedCampus();
            if (campus != null) {
                Map<String, FacilityConf> tmpmap = campus.getFacilitiesConfMap();
                int max = 0;
                for (FacilityConf o : tmpmap.values()) {
                    int numberoffloors = o.getFloorDataList().size();
                    if (numberoffloors > max) {
                        max = numberoffloors;
                        result = o;
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    private void loadOverlayesFloor(int floor) {
        try {

            if(floor != floorDisplayPolicy.getCurrentFloor()) {
                floorDisplayPolicy.setCurrentFloor(floor);

                poisClusterHelper.clearPois();
                removePoiMarkers();

                drawProjectPOis();

                Campus campus = ProjectConf.getInstance().getSelectedCampus();
                drawGoogleMapsTiles(campus, floor);
                if (campus != null && floor >= 0) {
                    for (FacilityOverlay o : facilityOverlays.values()) {
                        String id = o.getFacilityId();
                        if (id != null) {
                            FacilityConf fac = campus.getFacilityConf(o.getFacilityId());
                            loadOverlay(fac, campus, floor);
                        }
                    }
                }

                if (PropertyHolder.getInstance().isDrawInvisibleFloorsRoute()) {
                    List<CampusNavigationPath> navpath = RouteCalculationHelper.getInstance().getOutdoorNavPath();
                    if (navpath != null && !navpath.isEmpty()) {
                        updatePath(navpath);
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void setFloor(int floor) {
        try {
            if (floor >= 0) {
                loadOverlayesFloor(floor);
                if (PropertyHolder.getInstance().isFloorPickerVisibile()) {
                    int newvalue = floorstxtArr.length - floor - 1;
                    if (numberPicker != null && newvalue != numberPicker.getValue()) {
                        numberPicker.setValue(floorstxtArr.length - floor - 1);
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private String getSimplifiedText(Instruction instruction) {
        String result = "";
        try {
            int type = instruction.getType();
            if (type == Instruction.TYPE_SWITCH_FLOOR) {
                int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "simplified_switch");
                result = getResources().getString(tmptxt) + " " + instruction.getTofloor();
            } else if (type == Instruction.TYPE_EXIT || type == Instruction.TYPE_DESTINATION) {
                int tmptxt = ResourceTranslator.getInstance().getTranslatedResourceId("string", "outdoorInstruction");
                result = getResources().getString(tmptxt);
//				String exit = "the Exit";
//                FacilityConf fac = FacilityContainer.getInstance().getCurrent();
//                if (fac != null) {
//                    String facid = fac.getId();
//                    if (facid != null) {
//                        PoiData poi = DualMapNavUtil.getFacilityDestination(facid);
//                        if (poi != null) {
//                            String tmp = poi.getpoiDescription();
//                            if (tmp != null && !tmp.isEmpty()) {
//                                String exit = tmp;
//                                result += " " + exit;
//                            }
//                        }
//                    }
//                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    public void presentMultiPoiRoute(List<IPoi> poilist, List<IPoi> visitedPoiList) {
        try {
            if (poilist != null && !poilist.isEmpty()) {
                PropertyHolder.getInstance().setMultiPoiList(poilist);
                if (visitedPoiList != null && !visitedPoiList.isEmpty()) {
                    PropertyHolder.getInstance().setVisitedPoiList(visitedPoiList);
                }

                for (String o : facilityOverlays.keySet()) {
                    FacilityOverlay fo = facilityOverlays.get(o);
                    if (fo != null) {
                        drawMultiPoiPoints(o, fo.getFloor());
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void drawMultiPoiPoints(String facilityid, int floor) {
        try {
            if (facilityid != null) {

                removeMultiPoiPoints(facilityid);

                if(floorDisplayPolicy.displayFloorContent(facilityid, floor)) {
					List<IPoi> poilist = PropertyHolder.getInstance().getMultiPoiList();
					List<IPoi> visitedlist = PropertyHolder.getInstance().getVisitedPoiList();
					boolean includeEntrances = PropertyHolder.getInstance().isIncludeEntrancesInPoisNumbering();
					boolean includeSwitchFloors = PropertyHolder.getInstance().isIncludeSwitchFloorsInPoisNumbering();
					if (poilist != null && !poilist.isEmpty()) {
						List<IPoi> filterdpoilist = new ArrayList<IPoi>();
						HashMap<String, IPoi> exitandswitch = new HashMap<String, IPoi>();
						for (IPoi o : poilist) {
							String type = o.getPoiNavigationType();
							if ((type.equals("internal") && o.getPoiID().contains("idr"))) {
								if (includeEntrances) {
									exitandswitch.put(MultiPoiMarkerObject.TYPE_EXIT, o);
								}
							} else if ((o.getPoiID().startsWith(OrederPoisUtil.switchIdPrefix))) {
								if (includeSwitchFloors) {
									exitandswitch.put(MultiPoiMarkerObject.TYPE_SWITCH, o);
								}
							} else {
								filterdpoilist.add(o);
							}
						}

						for (IPoi o : filterdpoilist) {
							String poifacility = o.getFacilityID();
							if (poifacility != null && !poifacility.isEmpty() && poifacility.equals(facilityid)) {
								String type = o.getPoiNavigationType();
								int poifloor = (int) o.getZ();
								if (type != null && (type.equals("external") || poifloor == floor)) {
									boolean visited = false;
									if (visitedlist != null && !visitedlist.isEmpty() && visitedlist.contains(o)) {
										visited = true;
									}
									if (type.equals("internal")) {
										LatLng latlng = convertToLatlng(o.getX(), o.getY(), poifloor, poifacility);
										if (latlng != null) {
											o.setPoiLatitude(latlng.latitude);
											o.setPoiLongitude(latlng.longitude);
										}
									}
									int number = filterdpoilist.indexOf(o) + 1;
									MultiPoiMarkerObject m = new MultiPoiMarkerObject(o, number, MultiPoiMarkerObject.TYPE_NUMBER, visited, ctx, googlemap);
									poiToMultiPointmarker.put(o, m);
									multiPointmarkerToPoi.put(m.getMarker(), o);
								}
							}
						}

						for (String o : exitandswitch.keySet()) {
							IPoi tmp = exitandswitch.get(o);
							if (tmp != null) {
								String poifacility = tmp.getFacilityID();
								if (poifacility != null && !poifacility.isEmpty() && poifacility.equals(facilityid)) {
									String type = tmp.getPoiNavigationType();
									int poifloor = (int) tmp.getZ();
									if (type != null && (type.equals("external") || poifloor == floor)) {
										boolean visited = false;
										if (visitedlist != null && !visitedlist.isEmpty() && visitedlist.contains(o)) {
											visited = true;
										}
										if (type.equals("internal")) {
											LatLng latlng = convertToLatlng(tmp.getX(), tmp.getY(), poifloor, poifacility);
											if (latlng != null) {
												tmp.setPoiLatitude(latlng.latitude);
												tmp.setPoiLongitude(latlng.longitude);
											}
										}
										int number = -1;
										MultiPoiMarkerObject m = new MultiPoiMarkerObject(tmp, number, o, visited, ctx, googlemap);
										poiToMultiPointmarker.put(tmp, m);
										multiPointmarkerToPoi.put(m.getMarker(), tmp);
									}
								}
							}
						}

					}
				}
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void removeMultiPoiRoute() {
        PropertyHolder.getInstance().setMultiPoiList(null);
        PropertyHolder.getInstance().setVisitedPoiList(null);

        removeMultiPoiPoints();
    }

    private void removeMultiPoiPoints() {
        for (String o : facilityOverlays.keySet()) {
            removeMultiPoiPoints(o);
        }
    }

    private void removeMultiPoiPoints(String facilityid) {
        try {
            List<IPoi> poitoremove = new ArrayList<IPoi>();
            List<MultiPoiMarkerObject> markerstoremove = new ArrayList<MultiPoiMarkerObject>();
            for (IPoi o : poiToMultiPointmarker.keySet()) {
                if (o.getFacilityID().equals(facilityid)) {
                    MultiPoiMarkerObject m = poiToMultiPointmarker.get(o);
                    m.removeMarkerFromMap();
                    poitoremove.add(o);
                    markerstoremove.add(m);
                }
            }

            for (IPoi o : poitoremove) {
                multiPointmarkerToPoi.remove(o);
                o = null;
            }

            for (MultiPoiMarkerObject o : markerstoremove) {
                poiToMultiPointmarker.remove(o);
                o = null;
            }

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    //=======================  custom labels apis ==========================
    public void setVisibleLabelsWithIds(List<String> labelIdsList) {
        try {
            if (labelIdsList != null) {
                List<ILabel> labels = ProjectConf.getInstance().getAllLabelsList();

                for (ILabel lbl : labels) {
                    if (lbl == null) {
                        continue;
                    }
                    String id = lbl.getPlaceId();
                    if (id != null) {
                        if (labelIdsList.contains(id)) {
                            lbl.setVisible(true);
                        } else {
                            lbl.setVisible(false);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void showAllLabels() {
        try {
            List<ILabel> labels = ProjectConf.getInstance().getAllLabelsList();

            for (ILabel lbl : labels) {
                if (lbl == null) {
                    continue;
                }
                lbl.setVisible(true);
            }


        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void hideAllLabels() {
        try {
            List<ILabel> labels = ProjectConf.getInstance().getAllLabelsList();

            for (ILabel lbl : labels) {
                if (lbl == null) {
                    continue;
                }
                lbl.setVisible(false);
            }


        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    //======================== end custom labels apis ===============

    protected void setCustomStyleForLabelsList(List<String> labelIdsList, String font, int foregroundColor,
                                               int backgroundColor, int borderWidth, int borderColor,
                                               int borderRoundCournerPx, boolean fontBold, boolean fontItalic, boolean fontUnderline) {
        try {
            if (labelIdsList != null) {
                List<ILabel> labels = ProjectConf.getInstance().getAllLabelsList();

                for (ILabel lbl : labels) {
                    if (lbl == null) {
                        continue;
                    }
                    String id = lbl.getPlaceId();
                    if (id != null) {
                        if (labelIdsList.contains(id)) {
                            lbl.setStyle(font, foregroundColor, backgroundColor, borderWidth,
                                    borderColor, borderRoundCournerPx,
                                    fontBold, fontItalic, fontUnderline);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public ILocation getCenterLocation() {
        ILocation result = null;
        try {
            LatLng center = getCenterPoint();
            if (center != null) {
                ILocation tmp = getRealLocation(center);
                if (tmp != null) {
                    result = tmp;
                } else {
                    result = new Location(center);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    private ILocation getRealLocation(LatLng latlng) {
        ILocation result = null;
        boolean isindoor = false;
        try {
            if (latlng != null) {
                for (FacilityOverlay o : facilityOverlays.values()) {
                    if (o != null) {
                        List<LatLng> poly = o.getPolygone();
                        if (poly != null && !poly.isEmpty()) {
                            if (ConvertingUtils.isPointInPolygon(poly, latlng)) {
                                ILocation tmp = ConvertingUtils.convertToXYZ(latlng, o);
                                if (tmp != null) {
                                    result = tmp;
                                    isindoor = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!isindoor) {
                    result = new Location(latlng);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

        return result;
    }

    public void setMapBounds(LatLng southwest, LatLng northeast) {
        try {
            setFollowMeFalse();
            LatLngBounds bounds = new LatLngBounds(southwest, northeast);
            if (PropertyHolder.getInstance().isMapAnimation()) {
                googlemap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0), animationDuration,
                        new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            } else {
                googlemap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }

    public void openUserBubble(String text) {
        try {
            if (gmLoc != null) {
                View customview = null;
                for (SpreoDualMapViewListener o : listeners) {
                    if (o != null) {
                        try {
                            View v = o.aboutToOpenUserBubble();
                            if (v != null) {
                                customview = v;
                            }
                        } catch (Exception e) {
                            MapDebugHelper.onException(ctx,  e);
                        }
                    }
                }

                if (customview != null) {
                    googlemap.setInfoWindowAdapter(new CustomInfoWindowAdapter(customview, gmLoc));
                    gmLoc.showInfoWindow();
                } else {
                    if (text != null) {
                        gmLoc.setTitle(text);
                        gmLoc.showInfoWindow();
                    }
                }

            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }

    }


    public LatLngBounds getMapBounds() {
        LatLngBounds bounds = null;
        try {
            VisibleRegion vr = googlemap.getProjection().getVisibleRegion();
            CameraPosition cpos = googlemap.getCameraPosition();

            LatLng sw = vr.latLngBounds.southwest;
            LatLng ne = vr.latLngBounds.northeast;
            bounds = new LatLngBounds(sw, ne);

        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return bounds;
    }

    public void setMapBounds(String facilityId) {
        try {
            if (facilityId != null) {
                Campus campus = ProjectConf.getInstance().getSelectedCampus();
                if (campus != null) {
                    String campusId = campus.getId();
                    if (campusId != null) {
                        FacilityConf fac = ProjectConf.getInstance().getFacilityConfById(campusId, facilityId);
                        if (fac != null) {
                            LatLng southwest = new LatLng(fac.getConvRectBLlat(), fac.getConvRectBLlon());
                            LatLng northeast = new LatLng(fac.getConvRectTRlat(), fac.getConvRectTRlon());
                            setMapBounds(southwest, northeast);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void setMapBounds(String facilityId, float bearing) {
        try {
            if (facilityId != null) {
                Campus campus = ProjectConf.getInstance().getSelectedCampus();
                if (campus != null) {
                    String campusId = campus.getId();
                    if (campusId != null) {
                        FacilityConf fac = ProjectConf.getInstance().getFacilityConfById(campusId, facilityId);
                        if (fac != null) {
                            LatLng southwest = new LatLng(fac.getConvRectBLlat(), fac.getConvRectBLlon());
                            LatLng northeast = new LatLng(fac.getConvRectTRlat(), fac.getConvRectTRlon());


                            setFollowMeFalse();
                            LatLngBounds bounds = new LatLngBounds(southwest, northeast);
                            googlemap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));

                            LatLngBounds latLonBounds = getMapBounds();
                            LatLng center = latLonBounds.getCenter();
                            CameraPosition currentPlace = new CameraPosition.Builder()
                                    .target(new LatLng(center.latitude, center.longitude))
                                    .bearing(bearing).zoom(getZoom()).build();
                            googlemap.moveCamera(CameraUpdateFactory
                                    .newCameraPosition(currentPlace));

                        }
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void setMapBounds(LatLng southwest, LatLng northeast, float bearing, float mapZoom) {

        try {

            setMapBounds(southwest, northeast);
            LatLngBounds latLonBounds = new LatLngBounds(southwest, northeast);
            LatLng center = latLonBounds.getCenter();
            CameraPosition currentPlace = new CameraPosition.Builder()
                    .target(new LatLng(center.latitude, center.longitude))
                    .bearing(bearing).zoom(mapZoom).build();
            if (PropertyHolder.getInstance().isMapAnimation()) {
                googlemap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(currentPlace), animationDuration,
                        new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            } else {
                googlemap.moveCamera(CameraUpdateFactory
                        .newCameraPosition(currentPlace));
            }
        } catch (Throwable e) {
            MapDebugHelper.onException(ctx,  e);
        }
    }

    /**
     * Animates the movement of the camera from the current position to the position defined in the update argument.
     * During the animation, a call to getCameraPosition() returns an intermediate location of the camera.
     * @param update The change that should be applied to the camera.
     */
    public void animateCamera(CameraUpdate update){
        checkMapReady();
        setFollowMeFalse();
        if (PropertyHolder.getInstance().isMapAnimation()) {
            googlemap.animateCamera(update, animationDuration,
                    new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onCancel() {

                        }
                    });
        } else {
            googlemap.moveCamera(update);
            }
    }

    /**
     * Deprecated. Use getCameraPosition().bearing instead.
     * @return Direction that the camera is pointing in, in degrees clockwise from north.
     */
    public float getMapBearing() {
        float mapBearing = 0;
        try {
            CameraPosition cp = googlemap.getCameraPosition();
            mapBearing = cp.bearing;
        } catch (Throwable e) {
            MapDebugHelper.onException(ctx,  e);
        }
        return mapBearing;
    }

    private int getCountForReroute() {
        int result = 1;
        Campus campus = ProjectConf.getInstance().getSelectedCampus();
        if (campus != null) {
            int count = campus.getCountForReroute();
            if (count > 1) {
                result = count;
            }
        }
        return result;
    }

    private void drawCampusPathArrows(List<LatLng> points) {
        try {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.patharrow);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bm);
            for (LatLng o : points) {
                int index = points.indexOf(o);
                if (index > 0) {
                    LatLng l1 = points.get(index - 1);
                    LatLng l2 = o;
                    double d = MathUtils.distance(l1, l2);
                    if (d > 5) {
                        double angle = MathUtils.computeHeading(l1, l2);
                        LatLng position = MathUtils.GetMidPoint(l1, l2);
                        Marker marker = googlemap.addMarker(new MarkerOptions().position(position)
                                .icon(icon).anchor(0.5f, 0.5f).rotation((float) angle).flat(true));
                        campusArrows.add(marker);
                    }
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void drawFacilityPathArrows(List<LatLng> points, String facilityid) {
        try {
            removeFaciliyArrows(facilityid);
            if (facilityid != null && points != null && !points.isEmpty()) {
                List<Marker> arrows = new ArrayList<Marker>();
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.patharrow);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bm);
                for (LatLng o : points) {
                    int index = points.indexOf(o);
                    if (index > 0) {
                        LatLng l1 = points.get(index - 1);
                        LatLng l2 = o;
                        double d = MathUtils.distance(l1, l2);
                        if (d > 5) {
                            double angle = MathUtils.computeHeading(l1, l2);
                            LatLng position = MathUtils.GetMidPoint(l1, l2);
                            Marker marker = googlemap.addMarker(new MarkerOptions().position(position)
                                    .icon(icon).anchor(0.5f, 0.5f).rotation((float) angle).flat(true));
                            arrows.add(marker);
                        }
                    }
                }
                facilitiesArrows.put(facilityid, arrows);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void removeFaciliyArrows(String facilityid) {
        try {
            if (facilityid != null) {
                List<Marker> arrows = facilitiesArrows.get(facilityid);
                if (arrows != null) {
                    for (Marker o : arrows) {
                        o.remove();
                    }
                }
                facilitiesArrows.remove(facilityid);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void setSelectedInstruction(Instruction instruction, String facilityid, String color) {
        try {
//            clearSelectedInstruction();
//            if (facilityid != null) {
//                if (instruction != null) {
//
//                    Location loc = instruction.getLocation();
//                    if (loc != null) {
//                        loc.setFacilityId(facilityid);
//                        loc.setType(LocationMode.INDOOR_MODE);
//                        presentLocation(loc);
//                    }
//
//                    GisSegment segment = instruction.getSegment();
//                    Polyline facilitypath = facilitiesNavPaths.get(facilityid);
//                    if (segment != null && facilitypath != null) {
//                        GisSegment convertedsegment = convertSegment(facilityid, segment);
//                        if (convertedsegment != null) {
//                            int scolor = Color.parseColor(color);
//                            float zindex = 6;
//                            float width = PropertyHolder.getInstance().getNavPathWidth();
//                            List<LatLng> points = new ArrayList<LatLng>();
//                            LatLng l1 = new LatLng(convertedsegment.getLine().getPoint1().getY(), convertedsegment.getLine().getPoint1().getX());
//                            points.add(l1);
//                            LatLng l2 = new LatLng(convertedsegment.getLine().getPoint2().getY(), convertedsegment.getLine().getPoint2().getX());
//                            points.add(l2);
//                            PolylineOptions polyoptions = new PolylineOptions().zIndex(zindex).color(scolor).width(width);
//                            polyoptions.addAll(points);
//                            selectedInstructionSegment = googlemap.addPolyline(polyoptions);
//                            selectedSegmentPath = facilitypath;
//                        }
//                    }
//                }
//            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void clearSelectedInstruction(Polyline facilitypath) {
        try {
            if (facilitypath != null && selectedSegmentPath != null && facilitypath.equals(facilitypath)) {
                clearSelectedInstruction();
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void clearSelectedInstruction() {
        try {
            if (selectedInstructionSegment != null) {
                selectedInstructionSegment.remove();
                selectedInstructionSegment = null;
                selectedSegmentPath = null;
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void setUserLocationVisibilty(boolean visible) {
        try {
            isLocationHidden = !visible;
            if (gmLoc != null) {
                gmLoc.setVisible(visible);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    public void presentRoute() {
        try {
//            ILocation loc = LocationFinder.getInstance().getCurrentLocation();
//            if (loc != null) {
//                LocationMode mode = loc.getLocationType();
//                if (mode != null) {
//                    if (mode == LocationMode.INDOOR_MODE) {
//                        String fac = loc.getFacilityId();
//                        if (fac != null) {
//                            Polyline navpath = facilitiesNavPaths.get(fac);
//                            if (navpath != null) {
//                                List<LatLng> points = navpath.getPoints();
//                                if (points != null && points.size() > 1) {
//                                    ShowPoints(points);
//                                }
//                            }
//                        }
//                    } else if (mode == LocationMode.OUTDOOR_MODE) {
//                        if (campusPath != null) {
//                            for (Polyline o : campusPath) {
//                                List<LatLng> points = o.getPoints();
//                                if (points != null && points.size() > 1) {
//                                    ShowPoints(points);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private void ShowPoints(List<LatLng> points) {
        try {
            setFollowMeFalse();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng o : points) {
                builder.include(o);
            }
            LatLngBounds bounds = builder.build();
            int padding = 5;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            if (PropertyHolder.getInstance().isMapAnimation()) {
                googlemap.animateCamera(cu, animationDuration,
                        new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            } else {
                googlemap.moveCamera(cu);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }
    // ================= end ndk link ==============


    public void showOriginMarker(boolean show) {
        if(checkShowNavigationMarkersSettingIsOk(show, "origin marker")) {
            updateOriginMarkerState();
            this.showOriginMarker = show;
        }
    }

    public void showSwitchFloorMarkers(boolean show) {
        if(checkShowNavigationMarkersSettingIsOk(show, "switch floor markers")) {
            updateSwitchfloorsMarkersState();
            this.showSwitchFloorMarkers = show;
        }
    }

    private boolean checkShowNavigationMarkersSettingIsOk(boolean show, String markers){
        if(!show && PropertyHolder.getInstance().isShowNavigationMarkers()) {
            Log.e(TAG, "Can't hide " + markers + ". Navigation markers are enabled via deprecated PropertyHolder.setNavigationMarkers method.");
            return false;
        }
        return true;
    }

    /**
     *
     * @return total route distance in meters (by default) or -1d if there are no active route.
     * Use {@link com.spreo.sdk.setting.SettingsProvider#setUseFeetForDistance(boolean) SettingsProvider.setUseFeetForDistance(true)} to retrieve distance in feet units
     *
     * */
    public double getRouteDistance(){
        double result = 0;
        try {
            result = RouteCalculationHelper.getInstance().getRouteDistance();
            if (PropertyHolder.getInstance().isUseFeetForDistance() && result != 0) {
                result = MathUtils.metersToFeet(result);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;

//        double totalDistance = 0d;
//        List<Instruction> combinedInstructions = RouteCalculationHelper.getInstance().getCombinedInstructions();
//        if(combinedInstructions != null){
//            combinedInstructions = new ArrayList<>(combinedInstructions);
//            for (Instruction instruction : combinedInstructions) {
//                totalDistance += instruction.getDistance();
//            }
//        }
//        if(PropertyHolder.getInstance().isUseFeetForDistance() && totalDistance != 0d)
//            totalDistance = MathUtils.metersToFeet(totalDistance);
//
//        return totalDistance != 0d ? totalDistance : -1d;
    }

    public double getRouteLength(){

        double result = 0;
        try {
            result = RouteCalculationHelper.getInstance().getRouteDistance();
            if (PropertyHolder.getInstance().isUseFeetForDistance() && result != 0) {
                result = MathUtils.metersToFeet(result);
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;

//        double result = 0;
//        try {
//            if (facilitiesNavPaths != null && !facilitiesNavPaths.isEmpty()) {
//                for (List<Polyline> pl : facilitiesNavPaths.values()) {
//                    for (Polyline o : pl) {
//                        List<LatLng> list = o.getPoints();
//                        if (list != null && !list.isEmpty()) {
//                            double l = MathUtils.computeLength(list);
//                            result += l;
//                        }
//                    }
//                }
//            }
//            if (campusPath != null) {
//                for (Polyline o : campusPath) {
//                    List<LatLng> list = o.getPoints();
//                    if (list != null && !list.isEmpty()) {
//                        double l = MathUtils.computeLength(list);
//                        result += l;
//                    }
//                }
//            }
//        } catch (Throwable t) {
//            MapDebugHelper.onException(ctx,  t);
//        }
//        return result;
    }

    public double getTimeEstimation() {
        double result = 0;
        try {
            result = RouteCalculationHelper.getInstance().getTimeEstimation();
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
        return result;
    }

    public void addCameraChangeListener(CameraChangeListener listener){
        cameraChangeNotifier.addCameraChangeListener(listener);
    }

    public void removeCameraChangeListener(CameraChangeListener listener){
        cameraChangeNotifier.removeCameraChangeListener(listener);
    }

    private void updateLocationMarkerBearing(){
        if(gmLoc != null && PropertyHolder.getInstance().getRotatingMapType() != MapRotationType.COMPASS) {
            if(!(PropertyHolder.getInstance().useStaticBlueDotInNavigation() && PropertyHolder.getInstance().isNavigationState())) {
                Log.d("Bearing", "Bearing from updateLocationMarkerBearing: " + (OrientationMonitor.getInstance().getAzimuth() - googlemap.getCameraPosition().bearing));
                gmLoc.setFlat(false);
                gmLoc.setRotation(OrientationMonitor.getInstance().getAzimuth() - googlemap.getCameraPosition().bearing);
            }
        }
    }

    @Override
    public void onCameraIdle() {

        LatLng newlatlng = googlemap.getCameraPosition().target;


        float zoom = googlemap.getCameraPosition().zoom;
        if (poisClusterHelper.havePois() && Math.abs(zoom - lastZoom) > 0.1) {
            poisClusterHelper.zoomChange(zoom);
        } else if (lastCameraLatLng == null || MathUtils.distance(lastCameraLatLng, newlatlng) > 1) {

            if (poisClusterHelper.havePois()) {
                removeNotInRangePoiMarkers();
                poisClusterHelper.cameraMove(zoom);
            }

            checkExternalPolygons();
            checkFacilitesPolygons();

            lastCameraLatLng = newlatlng;
        }

        if (lastZoom != 0) {
            if ((lastZoom > poisClusterHelper.getHidingAllZoomLevel() && zoom < poisClusterHelper.getHidingAllZoomLevel()) || (lastZoom < hideFacilityMarkersZoom && zoom > hideFacilityMarkersZoom)) {
                drawFacilityMarkers();
            } else if (zoom > poisClusterHelper.getHidingAllZoomLevel() || zoom < hideFacilityMarkersZoom) {
                removeFacilityMarkers();
            }
        }

        if (zoom != lastZoom) {
            lastZoom = zoom;
            notifyZoomChange(zoom);
        }




    }

    private void removeFacilityMarkers() {
        try {
            for (Marker o : facilityMarkerMap.keySet()){
                o.remove();
            }
            facilityMarkerMap.clear();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void drawFacilityMarkers() {

        removeFacilityMarkers();

        try {
            Campus campus = ProjectConf.getInstance().getSelectedCampus();
            if (campus != null) {
                Map<String, FacilityConf> facilities = campus.getFacilitiesConfMap();
                for (String facid : facilities.keySet()) {
                    if (!facid.toLowerCase().contains("outdoor") && !facid.toLowerCase().contains("campus")) {
                        FacilityConf fac = facilities.get(facid);
//                        List<LatLng> points = new ArrayList<>();
//                        LatLng tl = new LatLng(fac.getConvRectTLlat(), fac.getConvRectTLlon());
//                        points.add(tl);
//                        LatLng tr = new LatLng(fac.getConvRectTRlat(), fac.getConvRectTRlon());
//                        points.add(tr);
//                        LatLng br = new LatLng(fac.getConvRectBRlat(), fac.getConvRectBRlon());
//                        points.add(br);
//                        LatLng bl = new LatLng(fac.getConvRectBLlat(), fac.getConvRectBLlon());
//                        points.add(bl);
//                        LatLng centerpoint = computeCenter(points);

                        LatLng centerpoint = new LatLng(fac.getCenterLatitude(), fac.getCenterLongtitude());

                        LayoutInflater inflater = LayoutInflater.from(ctx);
                        View view = inflater.inflate(R.layout.custom_facility_bubble, null);
                        String lng = PropertyHolder.getInstance().getAppLanguage();
                        if (lng != null && (lng.equals("hebrew") || lng.equals("arabic"))) {
                            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        }
                        TextView t1 = (TextView) view.findViewById(R.id.t1);
                        if (fac != null) {
                            String facname = fac.getName();
                            if (facname != null) {
                                t1.setText(facname);
                            }
                            Bitmap bIcon = getBitmapFromView(view);
                            if (bIcon != null) {
                                BitmapDescriptor locicon = BitmapDescriptorFactory.fromBitmap(bIcon);
                                bIcon.recycle();
                                bIcon = null;
                                Marker fmarker = googlemap.addMarker(new MarkerOptions().position(centerpoint).icon(locicon).anchor(0.5f, 0.9f).zIndex(6));
                                facilityMarkerMap.put(fmarker, facid);
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    @Override
    public void OnNavigationCalculationFinished(boolean isSimulation, NavigationResultStatus status, boolean isReroute) {
        try {
            if (status == NavigationResultStatus.SUCCEED) {
                removeAllFacilityPath();
                ILocation myloc = LocationFinder.getInstance().getCurrentLocation();
                if (myloc != null) {
                    if (myloc.getLocationType() == LocationMode.INDOOR_MODE) {
                        setIndoorNavigation(myloc);
                    } else if (myloc.getLocationType() == LocationMode.OUTDOOR_MODE) {
                        calculateCampusPath(myloc);
                        notifyOnNavigationStateChanged(NavigationState.STARTED);
                        startOutDoorNavigationThread(myloc);
                    }
                }

                if (isSimulation) {
                    SimulationPlayer.getInstance().loadFullRoute();
                    SimulationPlayer.getInstance().play();
                }
            } else {
                if (!isReroute) {
                    notifyOnNavigationFailed(status);
                }
            }
        } catch (Throwable t) {
            MapDebugHelper.onException(ctx,  t);
        }
    }

    private static class InstructionPlayerHelper {

        private Location lastLocation;
        private String lastSound;

        private void playInstructionSound(Instruction inst) {
            if (!PropertyHolder.getInstance().isNavigationInstructionsSoundMute()) {
                Location instructionLocation = inst.getLocation();
                List<String> sounds = inst.getSound();
                if(sounds != null && sounds.size() > 0) {
                    String sound = sounds.get(sounds.size()-1);
                    if(Objects.equals(sound, lastSound) && // the same sound
                            (lastLocation == instructionLocation ||
                                    lastLocation != null
                                            && instructionLocation != null
                                            && Location.areEqual(lastLocation, instructionLocation)
                            ) //same location
                    ) {
                        //don't need to play
                    } else {
                        lastLocation = instructionLocation;
                        lastSound = sound;
                        SoundPlayer.getInstance().play(sounds);
                    }
                }
            }
        }

        private void reset() {
            lastSound = null;
            lastLocation = null;
        }
    }

    private class NavigationModeReRouteLogic {

        private long lastPeriodicReroute;
        private boolean navigating;
        private int distanceFromIntersection = 5;

        private NavigationModeReRouteLogic(){
            registerNavigationListener(new SpreoNavigationListener() {
                @Override
                public void onNavigationStateChanged(NavigationState navigationState) {
                    if(navigationState == NavigationState.STARTED) {
                        lastPeriodicReroute = System.currentTimeMillis();
                        navigating = true;
                    } else if(navigationState == NavigationState.STOPED) {
                        navigating = false;
                    }
                }

                @Override
                public void onNavigationArriveToPoi(IPoi arrivedToPoi, List<IPoi> nextPois) {}

                @Override
                public void OnNavigationInstructionChanged(INavInstruction instruction) {}

                @Override
                public void onInstructionRangeEntered(INavInstruction instruction) {}

                @Override
                public void OnNavigationFailed(NavigationResultStatus status) {}
            });
        }

        private void checkReRoute(){
            long currentTime = System.currentTimeMillis();
            if(navigating
                    && PropertyHolder.getInstance().getRotatingMapType() == MapRotationType.NAVIGATION
                    && lastRawLocation != null && Location.isInDoor(lastRawLocation) // don't need to re-route when outdoor because currently we don't rotate map according to navigation path when outdoor
                    && currentTime - lastPeriodicReroute > timeForReroute) {//timeout expired
                if (shouldReroute()) {
                    lastPeriodicReroute = currentTime;
                    reRoute();
                }
            }
        }

        private boolean shouldReroute() {
            boolean result = true;
            try {
                GisLine line = RouteCalculationHelper.getInstance().getCurrentClosestNavLine();
                if (line != null && lastRawLocation != null && Location.isInDoor(lastRawLocation)) {
                    PointF mypoint = new PointF((float)lastRawLocation.getX(), (float)lastRawLocation.getY());
                    PointF point1 = line.point1.asPointF();
                    PointF point2 = line.point2.asPointF();
                    double d1 = MathUtils.distance(mypoint, point1);
                    double d2 = MathUtils.distance(mypoint, point2);
                    FacilityConf facility = FacilityContainer.getInstance().getCurrent();
                    if (facility != null) {
                        float pixeltometer = facility.getPixelsToMeter();
                        double d1inmeters = d1 / pixeltometer;
                        double d2inmeters = d2 / pixeltometer;
                        if (d1inmeters < distanceFromIntersection || d2inmeters < distanceFromIntersection) {
                            result = false;
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return result;
        }

    }

    private boolean shouldTurnBack() {
        boolean result = false;
        try {
            List<ILocation> list = new ArrayList<>();
            list.addAll(turnBackLocationHistory);
            Instruction tmpins = InstructionBuilder.getInstance().getNextInstruction();
            if (isTurnBackHistoryValid(list) && tmpins != null && Location.areEqual(instructionForTurnBack.getLocation(), tmpins.getLocation())) {
//                List<PointF> oldlist = new ArrayList<>();
//                List<PointF> newlist = new ArrayList<>();
//                for (ILocation o : list) {
//                    PointF p = new PointF((float)o.getX(), (float)o.getY());
//                    if (list.indexOf(o) < itemsForTurnBackLocationAverage) {
//                        oldlist.add(p);
//                    } else if (list.indexOf(o) == itemsForTurnBackLocationAverage) {
//                        oldlist.add(p);
//                        newlist.add(p);
//                    } else if (list.indexOf(o) > itemsForTurnBackLocationAverage) {
//                        newlist.add(p);
//                    }
//                }
//
//                PointF oldaverage = getAverage(oldlist);
//                PointF newaverage = getAverage(newlist);

                PointF oldaverage = new PointF((float)list.get(0).getX(), (float)list.get(0).getY());
                PointF newaverage = new PointF((float)list.get(list.size() - 1).getX(), (float)list.get(list.size() - 1).getY());

//                if (oldlist != null && newlist != null && instructionForTurnBack != null) {
                if (instructionForTurnBack != null) {
                    ILocation insloc = instructionForTurnBack.getLocation();
                    if (insloc != null) {
                        PointF inspoint = new PointF((float)insloc.getX(), (float)insloc.getY());
                        double oldd = MathUtils.distance(oldaverage, inspoint);
                        double newd = MathUtils.distance(newaverage, inspoint);
                        FacilityConf facility = FacilityContainer.getInstance().getCurrent();
                        if (facility != null) {
                            float pixeltometer = facility.getPixelsToMeter();
                            int distanceforturnback = facility.getDistanceForTurnBack();
                            double olddinmeters = oldd / pixeltometer;
                            double newdinmeters = newd / pixeltometer;
                            if (newdinmeters > olddinmeters + distanceforturnback) {
                                result = true;
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

    private PointF getAverage(List<PointF> list) {
        PointF avgLoc = null;
        try {
            if (!list.isEmpty()) {
                float sumX = 0;
                float sumY = 0;
                float cnt = list.size();
                for (PointF pt : list) {
                    sumX = sumX + pt.x;
                    sumY = sumY + pt.y;
                }
                avgLoc = new PointF(sumX / cnt, sumY / cnt);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return avgLoc;
    }

    private void addToTurnBackLocationHistory(ILocation loc) {
        try {
            if (PropertyHolder.getInstance().isNavigationState()) {
                Instruction tmpins = InstructionBuilder.getInstance().getNextInstruction();
                if (tmpins != null) {
                    if (instructionForTurnBack != null && !Location.areEqual(instructionForTurnBack.getLocation(), tmpins.getLocation()) ) {
                        turnBackLocationHistory.clear();
                    }
                    instructionForTurnBack = tmpins;
                } else {
                    turnBackLocationHistory.clear();
                }

                if (loc != null) {
                    turnBackLocationHistory.add(loc);
                    if (turnBackLocationHistory.size() > turnBackLocationHistorySize) {
                        turnBackLocationHistory.remove(0);
                    }
                }

            } else {
                instructionForTurnBack = null;
                turnBackLocationHistory.clear();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private boolean isTurnBackHistoryValid(List<ILocation> list) {
        boolean result = true;

        try {
            if (list.size() < turnBackLocationHistorySize) {
                result = false;
            } else {
                double z = -1;
                for (ILocation o : list) {
                    if (o.getLocationType() != LocationMode.INDOOR_MODE) {
                        result = false;
                        break;
                    }

                    if (z != -1 && o.getZ() != z) {
                        result = false;
                        break;
                    }

                    z = o.getZ();
                }
            }

        } catch (Throwable t) {
            result = false;
            t.printStackTrace();
        }
        return result;
    }
}



class SpreoTileProvider extends UrlTileProvider{

    private final int floor;

    /**
     * Constructor for outdoor tiles
     */
    SpreoTileProvider() {
        this(Integer.MAX_VALUE);
    }

    /**
     * Constructor for indoor tiles
     * @param floor
     */
    SpreoTileProvider(int floor) {
        super(256, 256);
        this.floor = floor;
    }

    private String getUrlString(){
        return PropertyHolder.getInstance().getTilesServerName() +
            "tiles/%s%s/zoom%d/%d-%d-%d.png";
    }

    public URL getTileUrl(int x, int y, int zoom) {
        String floorString = floor != Integer.MAX_VALUE ? '/' + Integer.toString(floor) : "";
        String s = String.format(Locale.US, getUrlString(), PropertyHolder.getInstance().getProjectId(), floorString, zoom, x, y, zoom);
        URL url;

        //Log.d("TilesProvider", s);

        try {
            url = new URL(s);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
        return url;
    }


}