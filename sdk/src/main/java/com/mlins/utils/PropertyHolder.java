package com.mlins.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.mlins.aStar.NavigationPath;
import com.mlins.interfaces.DeliveredLocationType;
import com.mlins.locationutils.LocationFinder;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.project.bridges.BridgeSelectionType;
import com.mlins.utils.gis.Location;
import com.mlins.wireless.filePlayer.FilePlayer;
import com.spreo.nav.enums.ExitsSelectionType;
import com.spreo.nav.enums.MapRotationType;
import com.spreo.nav.enums.NavigationType;
import com.spreo.nav.enums.ProjectLocationType;
import com.spreo.nav.enums.SwitchFloorSelectionType;
import com.spreo.nav.interfaces.IPoi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.mlins.enums.MapRotationType;

public class PropertyHolder implements Cleanable {


    //private String ServerName = "http://52.4.20.227/middle/ios/";
    public static final int MODE_WIFI_SCAN = 0;
    public static final int MODE_BLE_SCAN = 1;
    public static final int MODE_RADIUS_SCAN = 2;
    public static final int MODE_APRIL_SCAN = 3;
    //if true, user is observer and can't navigate inside facility
    //public static final String SURVEY_LINK = "https://docs.google.com/forms/d/1lpDfUJnt6r_u-T5vjXa7p4yWaK7q3yVj8WjscnoU9rc/viewform";
    public static final String SURVEY_LINK = "/survey/survey.htm";
    public final static int KITKAT_VERSION = 19;
    public static final int MODE_PATH_NAVIGATION = 0;
    public static final int MODE_ARROW_NAVIGATION = 1;
    public static final String READ_HIDDENS_NA = "NA";
    public static final String READ_HIDDENS_YES = "YES";
    public static final String READ_HIDDENS_NO = "NO";
    public static final boolean useZip = true;
    static final String EN = "en";
    static final String AR = "ar";
    static final String RU = "ru";
    static final String HE = "he";

    public int scannerMode = MODE_BLE_SCAN;
    public Typeface ClalitFont;
    public Typeface ClalitFontBold = null;
    //    private int floorselectionLevelLowerBound= -87;
    public int logginglevel = 0;
    public ArrayList<String> filteredpackages = new ArrayList<String>();
    public boolean showMediaMenu = false;
    ArrayList<String> recordedFiles = new ArrayList<String>();
    ArrayList<String> ssidFilter = new ArrayList<String>();
    //XXX ADDED BAROMETER SETTING
    int barometerSwSize = 31;
    float barometerModelThreshold = 0.35f;
    int barometerHistoryThreshold = 10;
    private String appLanguage = "english";
    private String loadedFacilityByMajorityVoting = "unknown";
    private boolean isFirstRunForMajorityVoting = true;
    private boolean recordingState = false;
    private boolean playingState = false;
    private File recordingFile = null;
    private File playingFile = null;
    private int playingFileIndex = -1;
    private String recordingFileName = null;
    private int ssidindex = 0;
    private boolean saveRawResults = false;
    private File appdir;
    private File zipAppdir;
    //XXX APK
    private String externalStoragedir;
    //
    private File facilityDir = null;
    private File floorDir = null;
    private File recordingdir = null;
    private String facilityID = "";
    private boolean showGis = false;
    private float distanceTresh = 0.6f;
    private boolean autoSelectFacility = true;
    private boolean isOnExit = false;
    private Matrix mMatrix;
    private boolean averageResults = true;
    private boolean lowPassResults = false;
    private boolean stepResults = false;
    private Context applicationContext = null;
    private float mStepSize = 0.7f;
    private float mMapRotation = 0.0f;
    private float mPixelsToMeter = -1;
    private float mProxyDistance = 10.0f;
    private boolean mTurnLogicOn = false;
    private double mTurnLogicDistance = 2;
    private boolean mUseLegacySensors = true;
    /**
     * false;
     */
    private boolean projectOnGis = false;
    private boolean projectOnPath = false;
    private boolean autoSelectFloor = true;
    private double FloorSelectorDistance = 15;
    private boolean stopNavigatinState = true;
    private float mArOffset = 0.0f;
    private int k = 10;
    private boolean autocalibration = true;
    private float instructionsDistance = 5;
    private int floorSelectionK = 25;
    private int averageRange = 15;
    private boolean selectfloorbysum = true;
    private boolean selectfloorbybonus = false;
    private boolean typeBin = true;
    private int wifilistminimum = 4;
    private boolean onecandidatefloor = false;
    //	private int floorSelectionBlips = 5;
    private long bannerTimer = 6000;
    private long bannershideTimer = 6000;
    private boolean isLocal = false;
    //	private int locationCloseRange = 600;
    private boolean isRecWlBlips = false;
    private boolean RotatingMap = false;
    private boolean poiLocal = true;
    private boolean poiRemote = true;
    private int blipsForZRecalculate = 3;
    //	private int distanceFromNavPath= 15;
    private String ServerName = "https://developer.spreo.co/middle/ios/"; // "https://developer.spreo.co/middle/DebugProjSrv/";
    //	private float locatorRadius=6.5f;
    private float locatorDeadReckoningWieght = 0.5f;
    private boolean bannersShow = false;
    private boolean barometerOn = false;
    private boolean brometerCombinedMethod = false;
    private boolean geoAutoDetect = false;
    private double boundsOffset = 20;
    //XXX APK WRITE DATA TO STORAGE
    private boolean firstInstall = true;
    private int poiForInstructionRadius = 10;
    private int barometerPatternSize = 1;
    private String calibrationFilterPrefix = "";
    private boolean developmentMode = false;//true for developers, false for user
    private float levelInsideElevatorThr = 0.7f;
    private boolean insideMainLobby = false;
    private boolean insideElevatorZone = false;
    private boolean selectedPoiFromDialog = false;
    private boolean wazeRuning = false;
    private boolean observerUser = false;//if false, user is participant and can navigate inside facility
    private boolean locationPlayer = false;
    private String facilityByBlips = "unknown";
    private int chkDestReachTHR = 4;
    private float chkDestReachRectRangeWidthMeters = 5;
    private long startNavigationTime = 0;
    private float preInsructionDistance = 40;
    private boolean isNavigationState = false;
    private boolean isMovingAverageOverLocation = false;
    private int movingAverageOverLocationSW = 3;
    private boolean isTurnToClosestGisLineMethod = false;
    private float turnToClosestGisLineTHR = 2;
    private float turnToClosestGisLineAngle = 90;
    private boolean asymetricLocatorRadius = false;
    private String deviceLang = getLocalLanguageString();
    private String userLaguagePreference = deviceLang;//as start before loadGlobals
    //private String diviceLanguega =  java.util.Locale.getDefault().getDisplayName();
    private PoiData poiextradetails;
    private String wazeNavigationOption = "yesToWaze";//or "notToWaze"
    private boolean userPrefToShowWazeDialog = false;
    private boolean justExitNavigationActivity = false;
    private float closeLineOnPathThreshold = 7;
    private boolean darwpoiadmin = false;
    private String appVersion = "1.0";
    private boolean isPlayingMedia = false;
    private Location ExternalDestination = null;
    private PoiData internalDestination = null;

//    private int  exitNoDetectionCount=5;
//    private  int exitMinBleDetectionDevices=2;
//    private  int exitMinBleDetectionLevel = -100;

//    private int loggerMode = 0;
    private boolean isIgnoreStepsCounter = false;
    //    private int topKlevelsThr=4;
//    private int floorsTopKlevelsThr = 3;
    private boolean simulationMode = false;
    private int outdoorNavigationMode = MODE_ARROW_NAVIGATION;
    private String navRouteColor = "#218aca";  //oldvalue:#701E84
    private Bitmap poiBubbleIcon = null;
    private int poiBubblesCount = 3;
    private Map<String, Boolean> mapLayersState = new HashMap<String, Boolean>();
    private boolean navigationInstructionsSoundMute = false;
    private boolean displayNavigationInstructionsOnMap = true;
    private NavigationType navigationType = NavigationType.ESCALATORS;
    private long userAutoFollowTimeInterval = 15000;
    private MapRotationType rotatingMapType = MapRotationType.COMPASS;
    private ProjectLocationType projectLocationType = ProjectLocationType.FULL_LOCATION;
    private boolean downloadMatrixes = true; //XXX CHANGED
    private GMapOverlyData gmCampusOverlyData = null;
    private GMapOverlyData gmFacilityOverlyData = null;
    private boolean shareLocation = false;
    //  private boolean displayLoginDialog= false;
    private String signInUserName = null;
    private String uuidScanType = null;
    private List<String> uuidList = null;
    private int poiBubbleTransparentLevel = 255;
    // is to use the location finder inside geofence method
    private boolean useLocationInsideGeofenceMethod = false;
    // display app language as device language
    private boolean displayAsDeviceLang = false;
    private String streamServerName = "https://stream.spreo.co/middle/stream/";
    private int refreshUsersInterval = 5000;
    private boolean analytic = false;
    //XXX  === end onSameOrNeighborsSegment check params
    //XXX  === onSameOrNeighborsSegment check params
    private int sameSegmentThreshold = 3;
    private boolean neighborsSegmentMethod = false;
    private float smallSegmentLength = 0.5f;
    private float distanceFromIntersection = 3;
    private float defaultDualMapZoom = 19;
    private boolean mulitPoisDrawSpritesState = true;
    private Bitmap iconForMultiPointSwitchFloor = null;
    private Bitmap iconForMultiPointExit = null;
    private float hidingPoisZoomLevel = 15.5f;
    //XXX for wifi hiddens testing
    private String readHiddens = "NA";
    private boolean testWifiReadingHiddens = true;
    //XXX for using solution of 4.4 KitKat
    private boolean useKitKatVersionSolution = true;
    private boolean BleLevelfilter = false;  //XXX
    private String campusId = null;
    private File campusDir = null;
    private boolean showFarewellDialog = true;
    //    private int blipLevelForEntrance = -150;
//    private int minimumDevicesForEntrance = 1;
    private int numberOfFarewellTimer = 0;
    private int closeDeviceThreshold = -10;
    private float closeDeviceWeight = 1;
    private float bubbleRange = 15;
    private boolean downloadingData = false;
    private String projectId = null;
    private File projectDir = null;
    private boolean sdkObserverMode = false;
    private int proximityThreshold = -70;
    private int mapType = GoogleMap.MAP_TYPE_NORMAL;
    private float checkBubblesRange = 2;
    private List<NavigationPath> multiPoiNavRoute = null;
    private List<IPoi> multiPoiList = null;
    private List<IPoi> visitedPoiList = null;

    private boolean addParkingToMultiPois = true;
    private boolean addSwitchFloorsToMultiPois = true;
    private boolean addEntranceToMultiPois = true;

    private boolean IncludeEntrancesInPoisNumbering = false;
    private boolean includeSwitchFloorsInPoisNumbering = false;
    private String multiPoisRouteColor = "#2c90d4";
    private String multiPoisPointColor = "#ffa500";
    private String multiPoisVisitedPointColor = "#551a8b";
    private String multiPoisPointNumberColor = "#ffffff";
    private String multiPoisVisitedPointNumberColor = "#ffffff";
    private boolean uuidScan = false;
    private boolean notifyGoogleDestination = false;
    private float defaultMapZoom = 3;
    private boolean presentDestinationIcon = false;
    private float poiBubbleScaleFactor = 0.5f;
    private String lockedFacility = null;
    private boolean lockedOnFacility = false;
    private DeliveredLocationType deliveredLocationType = DeliveredLocationType.SENSOR_FUSION;
    private ExitsSelectionType exitsSelectionType = ExitsSelectionType.CLOSE_TO_DESTINATION;
    private boolean FloorPickerVisibile = true;
    private boolean isSimplifiedInstruction = false;
    private float navPathWidth = 20;
    private boolean drawArrowsOnPath = false;


    private double distanceToNavOrigin = 7; //meters
    private int onSameNavLineContThreshold = 2; // times
    private boolean forceFullZip = false;
    private float ratioTresh = 0.2f;

    private boolean trasnlateLabels = false;

    private boolean showNavigationMarkers = false;

    private boolean useProximityLocation = false;

    private boolean useFeetForDistance = true;

    private boolean displayTopFloorContent = false;
    private boolean displayLabelsForPOIs = false;

    private boolean displayDashedNavigationRoute = false;

    private BridgeSelectionType bridgeSelectionType = BridgeSelectionType.ALL;

    private boolean useRotatingUserIcon = true;

    private float mapMaxZoomLimit = -1;

    private boolean playStraightSound = false;

    private boolean StayIndoor = false;

    private boolean mapAnimation = true;

    private SwitchFloorSelectionType switchFloorSelectionType = SwitchFloorSelectionType.SHORTEST;

    private boolean useZipWithoutMaps = false;

    private boolean useExitPoiRangeForExit = true;

    private int exitPoiRange = 10;

    private int consecutiveCountForExit = 30;

    private int LowerBoundSupplement = 0;

    private int floorFilterSupplement = 0;

    private boolean displaySwitchFloorInstructionExtra = false;

    private String tilesServerName = "https://sandbox.spreo.co/middle/";

    private boolean chooseShortestRoute = false;

    private boolean useTurnBackInstruction = false;

    private boolean poisZoomFiltering = false;

    private boolean drawInvisibleFloorsRoute = false;

    private boolean clickableDynamicBubbles = false;

    private boolean useBridgeEntranceParameters = false;

    private boolean drawInvisibleNavMarkers = false;

    private boolean drawRouteTails = false;

    private int playSwitchFloorRadius = 8;

    private float virtualRouteAlpha = 0.5f;

    private boolean drawGoogleMapsBuildings = false;

    private boolean handicappedRouting = false;
    private boolean staffRouting = false;
    private String restServerName = "https://developer.spreo.co/rest/";

    public boolean isDisplayDashedNavigationRoute() {
        return displayDashedNavigationRoute;
    }

    public void setDisplayDashedNavigationRoute(boolean displayDashedNavigationRoute) {
        this.displayDashedNavigationRoute = displayDashedNavigationRoute;
    }

    private int screenDensity;

    private PropertyHolder(){
        if (useZip) {
            appdir = zipAppdir;
        }
    }

    private boolean staticBlueDotForNavigation = false;

    public void setStaticBlueDotForNavigation(boolean staticBlueDotForNavigation){
        this.staticBlueDotForNavigation = staticBlueDotForNavigation;
    }

    public boolean useStaticBlueDotInNavigation(){
        return staticBlueDotForNavigation;
    }

    public void setDisplayTopFloorContent(boolean display){
        displayTopFloorContent = display;
    }

    public boolean shouldDisplayTopFloorContent() {
        return displayTopFloorContent;
    }

    public boolean isUseFeetForDistance() {
        return useFeetForDistance;
    }

    public void setUseFeetForDistance(boolean useFootForDistance) {
        this.useFeetForDistance = useFootForDistance;
    }

    public static PropertyHolder getInstance() {
        return Lookup.getInstance().get(PropertyHolder.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(PropertyHolder.class);
    }

    public int getOnSameNavLineContThreshold() {
        return onSameNavLineContThreshold;
    }

    public void setOnSameNavLineContThreshold(int onSameNavLineContThreshold) {
        this.onSameNavLineContThreshold = onSameNavLineContThreshold;
    }

    public double getDistanceToNavOrigin() {
        return distanceToNavOrigin;
    }

    public void setDistanceToNavOrigin(double distanceToNavOrigin) {
        this.distanceToNavOrigin = distanceToNavOrigin;
    }

    public boolean isMulitPoisDrawSpritesState() {
        return mulitPoisDrawSpritesState;
    }

    public void setMulitPoisDrawSpritesState(boolean mulitPoisDrawSpritesState) {
        this.mulitPoisDrawSpritesState = mulitPoisDrawSpritesState;
    }

    public boolean isAppLangAsDeviceLang() {
        return this.displayAsDeviceLang;
    }

    public void setAppLangAsDeviceLang(boolean display) {
        this.displayAsDeviceLang = display;
    }

    public boolean isUseLocationInsideGeofenceMethod() {
        return useLocationInsideGeofenceMethod;
    }

    public void setUseLocationInsideGeofenceMethod(
            boolean useLocationInsideGeofenceMethod) {
        this.useLocationInsideGeofenceMethod = useLocationInsideGeofenceMethod;
    }

    public int getPoiBubbleTransparentLevel() {
        return poiBubbleTransparentLevel;
    }

    public void setPoiBubbleTransparentLevel(int poiBubbleTransparentLevel) {
        this.poiBubbleTransparentLevel = poiBubbleTransparentLevel;
    }

    public String getUuidScanType() {
        return uuidScanType;
    }

    public void setUuidScanType(String uuidScanType) {
        this.uuidScanType = uuidScanType;
    }

    public List<String> getUuidList() {
        return uuidList;
    }

//	public boolean isDisplayLoginDialog() {
//		return displayLoginDialog;
//	}
//
//	public void setDisplayLoginDialog(boolean displayLoginDialog) {
//		this.displayLoginDialog = displayLoginDialog;
//	}

    public void setUuidList(List<String> uuidList) {
        this.uuidList = uuidList;
    }

    public String getSignInUserName() {
        return signInUserName;
    }

    //TODO ask meir to remove such methods
    public void setSignInUserName(String signInUserName) {
        this.signInUserName = signInUserName;
    }

    public boolean isShareLocation() {
        return shareLocation;
    }

    public void setShareLocation(boolean shareLocation) {
        this.shareLocation = shareLocation;
    }

    public GMapOverlyData getGmCampusOverlyData() {
        return gmCampusOverlyData;
    }

    public void setGmCampusOverlyData(GMapOverlyData gmCampusOverlyData) {
        this.gmCampusOverlyData = gmCampusOverlyData;
    }

    public GMapOverlyData getGmFacilityOverlyData() {
        return gmFacilityOverlyData;
    }

    public void setGmFacilityOverlyData(GMapOverlyData gmFacilityOverlyData) {
        this.gmFacilityOverlyData = gmFacilityOverlyData;
    }

    public boolean isDownloadMatrixes() {
        return downloadMatrixes;
    }

    public void setDownloadMatrixes(boolean downloadMatrixes) {
        this.downloadMatrixes = downloadMatrixes;
    }

    public MapRotationType getRotatingMapType() {
        return rotatingMapType;
    }

    public void setRotatingMapType(MapRotationType rotatingMapType) {
        this.rotatingMapType = rotatingMapType;
    }

    public long getUserAutoFollowTimeInterval() {
        return userAutoFollowTimeInterval;
    }

    public void setUserAutoFollowTimeInterval(long userAutoFollowTimeInterval) {
        this.userAutoFollowTimeInterval = userAutoFollowTimeInterval;
    }

    public ProjectLocationType getProjectLocationType() {
        return projectLocationType;
    }

    /**
     * Attention! Setting project location type to NO_LOCATION will force
     * {@link LocationFinder#getCurrentLocation() LocationFinder.getCurrentLocation()}
     * to return default campus location (ProjectConf.getInstance().getSelectedCampus().getDefaultLatlng())
     * in the case when location wasn't set to LocationFinder before with
     * {@link LocationFinder#setSimulatedLocation(Location)} LocationFinder.setSimulatedLocation(Location)} method.
     *
     * @param projectLocationType
     */
    public void setProjectLocationType(ProjectLocationType projectLocationType) {
        this.projectLocationType = projectLocationType;
    }

    public boolean isDisplayNavigationInstructionsOnMap() {
        return displayNavigationInstructionsOnMap;
    }

    public void setDisplayNavigationInstructionsOnMap(boolean displayNavigationInstructionsOnMap) {
        this.displayNavigationInstructionsOnMap = displayNavigationInstructionsOnMap;
    }

    public boolean isNavigationInstructionsSoundMute() {
        boolean result = false;
        if (navigationInstructionsSoundMute || isSimplifiedInstruction()) {
            result = true;
        }
        return result;
    }

    public void setNavigationInstructionsSoundMute(boolean navigationInstructionsSoundMute) {
        this.navigationInstructionsSoundMute = navigationInstructionsSoundMute;
    }

    public void setLayerOnMap(String layerType, boolean display) {
        if (layerType != null) {
            mapLayersState.put(layerType, display);
        }
    }

    public boolean isShowLayerOnMap(String layerType) {

        if (mapLayersState.size() == 0) {
            return true;
        }

        if (layerType != null && mapLayersState.containsKey(layerType)) {
            return mapLayersState.get(layerType);
        } else {
            return true; // default is to show layer if not set by user.
        }
    }

    public int getPoiBubblesCount() {
        return poiBubblesCount;
    }

    public void setPoiBubblesCount(int poiBubblesCount) {
        this.poiBubblesCount = poiBubblesCount;
    }

    public Bitmap getPoiBubbleIcon() {
        return poiBubbleIcon;
    }

    public void setPoiBubbleIcon(Bitmap poiBubbleIcon) {
        this.poiBubbleIcon = poiBubbleIcon;
    }

    public String getNavRouteColor() {
        return navRouteColor;
    }

    public void setNavRouteColor(String navRouteColor) {
        this.navRouteColor = navRouteColor;
    }

    public boolean isIgnoreStepsCounter() {
        return isIgnoreStepsCounter;
    }

    public void setIgnoreStepsCounter(boolean isIgnoreStepsCounter) {
        this.isIgnoreStepsCounter = isIgnoreStepsCounter;
    }

//	public File getZipFloorDir() {
//		return zipFloorDir;
//	}
//	
//	public void setZipFloorDir(File zipFloorDir) {
//		this.zipFloorDir = zipFloorDir;
//	}
//	
//	public File getZipProjectDir() {
//		return zipProjectDir;
//	}
//
//
//
//	public void setZipProjectDir(File zipProjectDir) {
//		this.zipProjectDir = zipProjectDir;
//	}
//
//
//
//	public File getZipCampusDir() {
//		return zipCampusDir;
//	}
//
//
//
//	public void setZipCampusDir(File zipCampusDir) {
//		this.zipCampusDir = zipCampusDir;
//	}
//
//
//
//	public File getZipFacilityDir() {
//		return zipFacilityDir;
//	}
//
//
//
//	public void setZipFacilityDir(File zipFacilityDir) {
//		this.zipFacilityDir = zipFacilityDir;
//	}

    public void setExternalStorage(String altStorage) {
        externalStoragedir = altStorage;
    }

    public File getZipAppdir() {
        if(zipAppdir == null)
            throw new NullPointerException();
        return zipAppdir;
    }

    public Typeface getClalitFont() {
        ClalitFont = Typeface.createFromAsset(applicationContext.getAssets(),
                "fonts/oronreg.ttf");
        return ClalitFont;
    }

    public Typeface getClalitFontBold() {
        try {
            ClalitFontBold = Typeface.createFromAsset(applicationContext.getAssets(),
                    "fonts/oronbold.ttf");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ClalitFontBold;
    }

    private String getLocalLanguageString() {

        String devicelocalLang = java.util.Locale.getDefault().getDisplayName();

        String settingLocal = "";
        if (devicelocalLang.contains("English")) {
            settingLocal = EN;
        } else if (devicelocalLang.contains("עברית")) {
            settingLocal = HE;
        } else if (devicelocalLang.contains("العربية")) {
            settingLocal = AR;
        } else if (devicelocalLang.contains("русский")) {
            settingLocal = RU;
        } else {
            settingLocal = EN; //default = English
        }

        return settingLocal;

    }

    public boolean isPoiLocal() {
        return poiLocal;
    }

    public void setPoiLocal(boolean poiLocal) {
        this.poiLocal = poiLocal;
    }

    public boolean isPoiRemote() {
        return poiRemote;
    }

    public void setPoiRemote(boolean poiRemote) {
        this.poiRemote = poiRemote;
    }

    public boolean isOnExit() {
        return isOnExit;
    }

    public void setOnExit(boolean inExit) {
        isOnExit = inExit;
    }

    public float getDistanceTresh() {
        return distanceTresh;
    }

    public void setDistanceTresh(float distanceTresh) {
        this.distanceTresh = distanceTresh;
    }

    public float getRatioTresh() {
        return ratioTresh;
    }

    public void setRatioTresh(float ratioTresh) {
        this.ratioTresh = ratioTresh;
    }

    public void clean() {}

    public Context getMlinsContext() {
        return applicationContext;
    }

    public void setMlinsContext(Context ctxt) {
        applicationContext = ctxt;
        externalStoragedir = ctxt.getExternalFilesDir(null).getAbsolutePath() + '/';
        appdir = new File(externalStoragedir, "mlins");
        zipAppdir = new File(externalStoragedir, "spreo");
        if (useZip) {
            appdir = zipAppdir;
        }
        screenDensity = ctxt.getResources().getDisplayMetrics().densityDpi;
    }

    public Matrix getMatrix() {
        return mMatrix;
    }

    public void setMatrix(Matrix nMatrix) {
        mMatrix = nMatrix;
    }

    public File getAppDir() {
        if (!appdir.exists()) {
            appdir.mkdirs();
        }
        return appdir;
    }

    public void setAppDir(String altdir) {
        appdir = new File(altdir);
    }

    public void setToolbarvisibility(Boolean visible, View toolbar, View map) {
        if (visible == true) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.INVISIBLE);
            // XXX  DEBUG - commented
            //LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(-1,
            //		-1);
            //map.setLayoutParams(parms);
        }
    }

    public boolean isRecordingState() {
        return recordingState;
    }

    public void setRecordingState(boolean recordingState) {
        this.recordingState = recordingState;
    }

    public File getRecordingFile() {
        if (recordingFile == null) {
            setRecordingFile("default");
        }
        return recordingFile;
    }

    public void setRecordingFile(String filename) {
        recordingdir = new File(getFloorDir(), "records");
        File rfile = null;
        if (filename != null) {
            if (!recordingdir.exists()) {
                recordingdir.mkdirs();
            }
            String nfilename = filename + ".txt";
            rfile = new File(recordingdir, nfilename);
            if (!rfile.exists()) {
                try {
                    rfile.createNewFile();
                    setRecordingFileName(filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.recordingFile = rfile;
    }

    public String getRecordingFileName() {
        return recordingFileName;
    }

    public void setRecordingFileName(String recordingFileName) {
        this.recordingFileName = recordingFileName;
    }

    public Boolean isRecordingFileExists(String filename) {
        recordingdir = new File(getFloorDir(), "records");
        File rfile;
        String nfilename = filename + ".txt";
        rfile = new File(recordingdir, nfilename);
        if (rfile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getRecorededFiles() {
        recordingdir = new File(getFloorDir(), "records");
        recordedFiles.clear();
        File dir = recordingdir;
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File[] filelist = dir.listFiles();
        for (int i = 0; i < filelist.length; i++) {
            recordedFiles.add(filelist[i].getName());
        }
        return recordedFiles;
    }


    public boolean isPlayingState() {
        return playingState;
    }

    public void setPlayingState(boolean playingState) {
        this.playingState = playingState;
    }

    public File getPlayingFile() {
        recordingdir = new File(getFloorDir(), "records");
        if (playingFile == null) {
            playingFile = new File(recordingdir, "default.txt");
            if (!playingFile.exists()) {
                recordingdir.mkdirs();
                try {
                    playingFile.createNewFile();
                } catch (IOException traceMe) {
                    traceMe.printStackTrace();
                }
            }
        }
        return playingFile;
    }

    public void setPlayingFile(String filename, int position) {
        recordingdir = new File(getFloorDir(), "records");
        File pfile;
        if (filename != null) {
            pfile = new File(recordingdir, filename);
            if (pfile.exists()) {
                this.playingFile = pfile;
                FilePlayer.getInstance().load(pfile.getAbsolutePath());


                setPlayingFileIndex(position);
            } else {
                setPlayingFileIndex(-1);
            }

        }
    }

    public int getSsidindex() {
        return ssidindex;
    }

    public void setSsidindex(int ssidindex) {
        this.ssidindex = ssidindex;
    }

    public boolean isResultsAvarage() {
        return averageResults;
    }

    public void setAverageResults(boolean average) {
        averageResults = average;
    }

    public boolean isLowPassResults() {
        return lowPassResults;
    }

    public void setLowPassResults(boolean lowPass) {
        lowPassResults = lowPass;
    }

    public boolean isStepResults() {
        return stepResults;
    }

    public void setStepResults(boolean step) {
        stepResults = step;
    }

    public int getPlayingFileIndex() {
        return playingFileIndex;
    }

    public void setPlayingFileIndex(int playingFileIndex) {
        this.playingFileIndex = playingFileIndex;
    }

    public ArrayList<String> getSsidFilter() {
        return ssidFilter;
    }

    public void setSsidFilter(ArrayList<String> ssidFilter) {
        this.ssidFilter = ssidFilter;
    }

    public boolean isSaveRawResults() {
        return saveRawResults;
    }

    public void setSaveRawResults(boolean saveRawResults) {
        this.saveRawResults = saveRawResults;
    }

    public String getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
        setFacilityDir(this.facilityID);
    }

    public File getFacilityDir() {
        return facilityDir;
    }

    public void setFacilityDir(String facilityid) {
        File dir = new File(campusDir, facilityid);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.facilityDir = dir;
//		if(useZip){
//			File zipdir = new File(zipCampusDir, "facilities" +"/"+facilityid);
//			if (!zipdir.exists()) {
//				zipdir.mkdirs();
//			}
//			this.zipFacilityDir = zipdir;
//		}
    }

//	public File getZipCampusFacilitiesDir(){
//		return new File(zipCampusDir, "facilities");
//	}

    public File getFloorDir() {
        return floorDir;
    }

    public void setFloorDir(String floor) {
        File dir = new File(facilityDir, floor);
        this.floorDir = dir;
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.floorDir = dir;

//		if(useZip){
//			File zipdir = new File(zipFacilityDir, "floors" + "/"+ floor);
//			if (!zipdir.exists()) {
//				zipdir.mkdirs();
//			}
//			this.zipFloorDir = zipdir;
//		}
    }

    public CharSequence[] getPathFiles() {
        File pathdir = new File(getFacilityDir(), "pathplaying");
        // recordedFiles.clear();
        // File dir = recordingdir;
        if (!pathdir.exists()) {
            pathdir.mkdirs();
        }
        File[] filelist = pathdir.listFiles();
        CharSequence[] files = new String[filelist.length + 1];
        files[0] = "none";
        for (int i = 1; i < filelist.length + 1; i++) {
            files[i] = filelist[i - 1].getName();
        }
        return files;
    }

    public boolean isShowGis() {
        return showGis;
    }

    public void setShowGis(boolean showGis) {
        this.showGis = showGis;
    }

    public float getStepSize() {
        return mStepSize;
    }

    public void setStepSize(float inMeters) {
        mStepSize = inMeters;
    }

//	public float getPixelsToMeter() {
//		FloorData data = FacilityContainer.getInstance().getSelected().getSelectedFloorData();
//		if (data != null) {
//			return data.pixelsToMeter;
//		}
//		return mPixelsToMeter;
//	}
//
//	public void setPixelsToMeter(float p2m) {
//		if (p2m == -1)
//			return;
//		mPixelsToMeter = p2m;
//		FloorData data = FacilityContainer.getInstance().getSelected().getSelectedFloorData();
//		if (data != null) {
//			data.setPixelToMeters(p2m);
//		}
//	}
//
//	public float getMapRotation() {
//		mMapRotation = FacilityContainer.getInstance().getSelected().getFloorRotation();
//		return mMapRotation;
//	}
//
//	public void setMapRotation(float inDegrees) {
//		mMapRotation = inDegrees;
//		FloorData data = FacilityContainer.getInstance().getSelected().getSelectedFloorData();
//		if (data != null) {
//			data.rotation = inDegrees;
//		}
//	}

    public float getProxyDistance() {
        return mProxyDistance;
    }

    public void setProxyDistance(float mProxyDistance) {
        this.mProxyDistance = mProxyDistance;
    }

    public void saveGlobals() {
        StringBuffer sb = new StringBuffer();
        sb.append(getProxyDistance() + "\t");
        sb.append(getAutoSelectFacility() + "\t");
        sb.append(ismTurnLogicOn() + "\t");
        sb.append(getmTurnLogicDistance() + "\t");
        sb.append(useLegacySensors() + "\t");
        sb.append(isProjectOnGis() + "\t");
        sb.append(isProjectOnPath() + "\t");
        //XXX mb call setting method AutoSelectFloor
        sb.append(isSettingAutoSelectFloor() + "\t");
        sb.append(getFloorSelectorDistance() + "\t");
        sb.append(isStopNavigatinState() + "\t");
        sb.append(getK() + "\t");
        sb.append(isAutocalibration() + "\t");
        sb.append(getInstructionsDistance() + "\t");
        sb.append(getFloorSelectionK() + "\t");
        sb.append(getAverageRange() + "\t");
        sb.append(isSelectfloorbysum() + "\t");
        sb.append(isSelectfloorbybonus() + "\t");
        sb.append(isTypeBin() + "\t");
        sb.append(getWifilistminimum() + "\t");
        sb.append(isOnecandidatefloor() + "\t");
//		sb.append(getFloorSelectionBlips() + "\t");
        sb.append(getBannerTimer() + "\t");
//		sb.append(getLocationCloseRange() + "\t");
        sb.append(isRecWlBlips() + "\t");
        sb.append(isRotatingMap() + "\t");
        sb.append(getBlipsForZRecalculate() + "\t");
//		sb.append(getDistanceFromNavPath() + "\t");
        sb.append(getServerName() + "\t");
        sb.append(getScannerMode() + "\t");
//		sb.append(getLocatorRadius() + "\t");
        sb.append(getLocatorDeadReckoningWeight() + "\t");
        sb.append(isBannersShow() + "\t");
        sb.append(getBarometerSwSize() + "\t");
        sb.append(getBarometerModelThreshold() + "\t");
        sb.append(getBarometerHistoryThreshold() + "\t");
        sb.append(isBarometerOn() + "\t");
        sb.append(isBarometerCombinedMethod() + "\t");
        sb.append(getBoundsOffset() + "\t");
        //XXX APK
        sb.append(isFirstInstall() + "\t");
        sb.append(getPoiForInstructionRadius() + "\t");
        sb.append(isGeoAutoDetect() + "\t");
        sb.append(getBarometerPatternSize() + "\t");
        sb.append(getCalibrationFilterPrefix() + "\t");
        sb.append(getLevelInsideElevatorThr() + "\t");

        sb.append(getChkDestReachTHR() + "\t");
        sb.append(getChkDestReachRectRangeWidthMeters() + "\t");
        sb.append(getPreInsructionDistance() + "\t");
        sb.append(isMovingAverageOverLocation() + "\t");
        sb.append(getMovingAverageOverLocationSW() + "\t");
        sb.append(isTurnToClosestGisLineMethod() + "\t");
        sb.append(getTurnToClosestGisLineTHR() + "\t");
        sb.append(getTurnToClosestGisLineAngle() + "\t");
        sb.append(isAsymetricLocatorRadius() + "\t");

        sb.append(getUserLaguagePreference() + "\t");
        sb.append(getWazeNavigationOption() + "\t");
        sb.append(isUserPrefToShowWazeDialog() + "\t");
        sb.append(getCloseLineOnPathThreshold() + "\t");
        sb.append(getStepSize() + "\t");

        //XXX FOR WIFI READS HIDDENS CHECKS
        if (!getReadHiddensStatus().equals(READ_HIDDENS_YES)) {
            sb.append(READ_HIDDENS_NA + "\t");
        } else {
            sb.append(getReadHiddensStatus() + "\t");
        }

        sb.append(isTestWifiReadingHiddens() + "\t");

        //XXX for using solution of 4.4 KitKat
        sb.append(isUseKitKatVersionSolution() + "\t");

        //XXX app version
        sb.append(getAppVersion() + "\t");
        // show media popup
        sb.append(isshowMediaMenu() + "\t");
//		sb.append(blipLevelForEntrance + "\t");
//		sb.append(minimumDevicesForEntrance + "\t");
        sb.append(getCloseDeviceThreshold() + "\t");
        sb.append(getCloseDeviceWeight() + "\t");
//		sb.append(getTopKlevelsThr() + "\t");
        sb.append(getBubbleRange() + "\t");
//		sb.append(getFloorselectionLevelLowerBound() + "\t");

        File dir = getAppDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File gfile = new File(dir, "globalProperties.txt");
        if (gfile.exists()) {
            gfile.delete();
        }
        try {
            gfile.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(gfile, true));
            out.write(sb.toString());
            out.flush();
        } catch (IOException e) {
            e.toString();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (Exception e2) {
                    Log.e("", e2.getMessage());
                    e2.printStackTrace();
                }
        }
    }

    public void loadGlobals() {
        File dir = getAppDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File pfile = new File(dir, "globalProperties.txt");
        if (!pfile.exists()) {
            return;
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(pfile));
            String line = null;
            while ((line = in.readLine()) != null) {
                String[] vals = line.split("\t");
                if (vals[0] != null) {
                    setProxyDistance(Float.parseFloat(vals[0]));
                }

                if (vals.length >= 2 && vals[1] != null) {
                    setAutoSelectFacility(Boolean.parseBoolean(vals[1]));
                }
                if (vals.length >= 3 && vals[2] != null) {
                    setmTurnLogicOn(Boolean.parseBoolean(vals[2]));
                }
                if (vals.length >= 4 && vals[3] != null) {
                    setmTurnLogicDistance(Double.parseDouble(vals[3]));
                }
                if (vals.length >= 5 && vals[4] != null) {
                    setUseLegacySensors(Boolean.parseBoolean(vals[4]));
                }
                if (vals.length >= 6 && vals[5] != null) {
                    setProjectOnGis(Boolean.parseBoolean(vals[5]));
                }
                if (vals.length >= 7 && vals[6] != null) {
                    setProjectOnPath(Boolean.parseBoolean(vals[6]));
                }
                if (vals.length >= 8 && vals[7] != null) {
                    setAutoSelectFloor(Boolean.parseBoolean(vals[7]));
                }
                if (vals.length >= 9 && vals[8] != null) {
                    setFloorSelectorDistance(Double.parseDouble(vals[8]));
                }
                if (vals.length >= 10 && vals[9] != null) {
                    setStopNavigatinState(Boolean.parseBoolean(vals[9]));
                }
                if (vals.length >= 11 && vals[10] != null) {
                    setK(Integer.parseInt(vals[10]));
                }
                if (vals.length >= 12 && vals[11] != null) {
                    setAutocalibration(Boolean.parseBoolean(vals[11]));
                }
                if (vals.length >= 13 && vals[12] != null) {
                    setInstructionsDistance(Float.parseFloat(vals[12]));
                }
                if (vals.length >= 14 && vals[13] != null) {
                    setFloorSelectionK(Integer.parseInt(vals[13]));
                }
                if (vals.length >= 15 && vals[14] != null) {
                    setAverageRange(Integer.parseInt(vals[14]));
                }
                if (vals.length >= 16 && vals[15] != null) {
                    setSelectfloorbysum(Boolean.parseBoolean(vals[15]));
                }
                if (vals.length >= 17 && vals[16] != null) {
                    setSelectfloorbybonus(Boolean.parseBoolean(vals[16]));
                }
                if (vals.length >= 18 && vals[17] != null) {
                    setTypeBin(Boolean.parseBoolean(vals[17]));
                }
                if (vals.length >= 19 && vals[18] != null) {
                    setWifilistminimum(Integer.parseInt(vals[18]));
                }
                if (vals.length >= 20 && vals[19] != null) {
                    setOnecandidatefloor(Boolean.parseBoolean(vals[19]));
                }
                if (vals.length >= 21 && vals[20] != null) {
//					setFloorSelectionBlips(Integer.parseInt(vals[20]));
                }
//				if (vals.length >= 22 && vals[21] != null) {
//					setBannerTimer(12000/*Long.parseLong(vals[21])*/);
//				}
                if (vals.length >= 23 && vals[22] != null) {
//					setLocationCloseRange(Integer.parseInt(vals[22]));
                }
                if (vals.length >= 24 && vals[23] != null) {
                    setRecWlBlips(Boolean.parseBoolean(vals[23]));
                }
                if (vals.length >= 25 && vals[24] != null) {
                    setRotatingMap(Boolean.parseBoolean(vals[24]));
                }
                if (vals.length >= 26 && vals[25] != null) {
                    setBlipsForZRecalculate(Integer.parseInt(vals[25]));
                }
                if (vals.length >= 27 && vals[26] != null) {
//					setDistanceFromNavPath(Integer.parseInt(vals[26]));
                }
                if (vals.length >= 28 && vals[27] != null) {
                    setServerName(vals[27]);
                }
                if (vals.length >= 29 && vals[28] != null) {
                    setScannerMode(Integer.parseInt(vals[28]));
                }
                // XXX  ADED
                if (vals.length >= 30 && vals[29] != null) {
//					setLocatorRadius(Float.parseFloat(vals[29]));
                }
                if (vals.length >= 31 && vals[30] != null) {
                    setLocatorDeadReckoningWieght(Float.parseFloat(vals[30]));
                }

                if (vals.length >= 32 && vals[31] != null) {
                    setBannersShow(Boolean.parseBoolean(vals[31]));
                }

                //XXX BAROMETER ADDED
                if (vals.length >= 33 && vals[32] != null) {
                    setBarometerSwSize(Integer.parseInt(vals[32]));
                }
                if (vals.length >= 34 && vals[33] != null) {
                    setBarometerModelThreshold(Float.parseFloat(vals[33]));
                }
                if (vals.length >= 35 && vals[34] != null) {
                    setBarometerHistoryThreshold(Integer.parseInt(vals[34]));
                }
                if (vals.length >= 36 && vals[35] != null) {
                    setBarometerOn(Boolean.parseBoolean(vals[35]));
                }
                if (vals.length >= 37 && vals[36] != null) {
                    setBarometerCombinedMethod(Boolean.parseBoolean(vals[36]));
                }
                if (vals.length >= 38 && vals[37] != null) {
                    setBoundsOffset(Double.parseDouble(vals[37]));
                }
                //XXX APK FIRST LOAD INSTALL
                if (vals.length >= 39 && vals[38] != null) {
                    setFirstInstall(Boolean.parseBoolean(vals[38]));
                }
                if (vals.length >= 40 && vals[39] != null) {
                    setPoiForInstructionRadius(Integer.parseInt(vals[39]));
                }
                if (vals.length >= 41 && vals[40] != null) {
                    setGeoAutoDetect(Boolean.parseBoolean(vals[40]));
                }
                if (vals.length >= 42 && vals[41] != null) {
                    setBarometerPatternSize(Integer.parseInt(vals[41]));
                }
                if (vals.length >= 43 && vals[42] != null) {
                    setCalibrationFilterPrefix(vals[42]);
                }
                if (vals.length >= 44 && vals[43] != null) {
                    setLevelInsideElevatorThr(Float.parseFloat(vals[43]));
                }
                if (vals.length >= 45 && vals[44] != null) {
                    setChkDestReachTHR(Integer.parseInt(vals[44]));
                }
                if (vals.length >= 46 && vals[45] != null) {
                    setChkDestReachRectRangeWidthMeters(Float.parseFloat(vals[45]));
                }
                if (vals.length >= 47 && vals[46] != null) {
                    setPreInsructionDistance(Float.parseFloat(vals[46]));
                }
                if (vals.length >= 48 && vals[47] != null) {
                    setMovingAverageOverLocation(Boolean.parseBoolean(vals[47]));
                }
                if (vals.length >= 49 && vals[48] != null) {
                    setMovingAverageOverLocationSW(Integer.parseInt(vals[48]));
                }
                if (vals.length >= 50 && vals[49] != null) {
                    setTurnToClosestGisLineMethod(Boolean.parseBoolean(vals[49]));
                }
                if (vals.length >= 51 && vals[50] != null) {
                    setTurnToClosestGisLineTHR(Float.parseFloat(vals[50]));
                }
                if (vals.length >= 52 && vals[51] != null) {
                    setTurnToClosestGisLineAngle(Float.parseFloat(vals[51]));
                }
                if (vals.length >= 53 && vals[52] != null) {
                    setAsymetricLocatorRadius(Boolean.parseBoolean(vals[52]));
                }
                if (vals.length >= 54 && vals[53] != null) {
                    if (!vals[53].equals("def"))
                        setUserLaguagePreference(vals[53]);
                }
                if (vals.length >= 55 && vals[54] != null) {
                    setWazeNavigationOption(vals[54]);
                }
                if (vals.length >= 56 && vals[55] != null) {
                    setUserPrefToShowWazeDialog(Boolean.parseBoolean(vals[55]));
                }
                if (vals.length >= 57 && vals[56] != null) {
                    setCloseLineOnPathThreshold(Float.parseFloat(vals[56]));
                }
                if (vals.length >= 58 && vals[57] != null) {
                    setStepSize(Float.parseFloat(vals[57]));
                }
                //XXX FOR TEST READING HIDDENS
                if (vals.length >= 59 && vals[58] != null) {
                    setReadHiddensStatus(vals[58]);
                }
                if (vals.length >= 60 && vals[59] != null) {
                    setTestWifiReadingHiddens(Boolean.parseBoolean(vals[59]));
                }
                //XXX for using solution of 4.4 KitKat
                if (vals.length >= 61 && vals[60] != null) {
                    setUseKitKatVersionSolution(Boolean.parseBoolean(vals[60]));
                }

                //XXX app version
                if (vals.length >= 62 && vals[61] != null) {
                    setAppVersion(vals[61]);
                }
                //show play list again
//				if (vals.length >= 63 && vals[62] != null) {
//					setshowMediaMenu(Boolean.parseBoolean(vals[62]));
//				}

                if (vals.length >= 64 && vals[63] != null) {
//					setBlipLevelForEntrance(Integer.parseInt(vals[63]));
                }
                if (vals.length >= 65 && vals[64] != null) {
//					setMinimumDevicesForEntrance(Integer.parseInt(vals[64]));
                }

                if (vals.length >= 66 && vals[65] != null) {
                    setCloseDeviceThreshold(Integer.parseInt(vals[65]));
                }

                if (vals.length >= 67 && vals[66] != null) {
                    setCloseDeviceWeight(Float.parseFloat(vals[66]));
                }

                if (vals.length >= 68 && vals[67] != null) {
//					setTopKlevelsThr(Integer.parseInt(vals[67]));
                }

                if (vals.length >= 69 && vals[68] != null) {
                    setBubbleRange(Float.parseFloat(vals[68]));
                }

                if (vals.length >= 70 && vals[69] != null) {
//					setFloorselectionLevelLowerBound(Integer.parseInt(vals[69]));
                }

            }
        } catch (Throwable e) {
            e.toString();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Throwable e2) {
                    Log.e("", e2.getMessage());
                    e2.printStackTrace();
                }
        }
    }

    public void saveProperties() {
        File dir = getFloorDir();
        StringBuffer sb = new StringBuffer();
        sb.append(distanceTresh + "\t");
        sb.append(ratioTresh + "\t");
        sb.append(mMapRotation + "\t");
        sb.append(mPixelsToMeter + "\t");
        sb.append(mStepSize + "\t");
        sb.append(averageResults + "\t");
        sb.append(lowPassResults + "\t");
        sb.append(stepResults + "\t");
        sb.append(playingState + "\t");
        sb.append(mArOffset + "\t");
        sb.append(isLocal() + "\t");
        sb.append(poiRemote + "\t");
        sb.append(poiLocal + "\t");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = "properties.txt";
        File pfile = new File(dir, fileName);
        if (pfile.exists()) {
            pfile.delete();
        }
        try {
            pfile.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(pfile, true));
            out.write(sb.toString());
            out.flush();
        } catch (IOException e) {
            e.toString();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (Exception e2) {
                    Log.e("", e2.getMessage());
                    e2.printStackTrace();
                }
        }
    }

    public void loadProperties() {

        File dir = getFloorDir();

        if (dir == null)
            return;

        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = "properties.txt";
        File pfile = new File(dir, fileName);
        if (!pfile.exists()) {
            return;
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(pfile));
            String line = null;
            while ((line = in.readLine()) != null) {
                String[] vals = line.split("\t");
                for (int i = 0; i < vals.length; i++) {
                    switch (i) {
                        case 0:
                            setDistanceTresh(Float.parseFloat(vals[0]));
                            break;
                        case 1:
                            setRatioTresh(Float.parseFloat(vals[1]));
                            break;
                        case 2:
//						setMapRotation(Float.parseFloat(vals[2]));
                            break;
                        case 3:
//						setPixelsToMeter(Float.parseFloat(vals[3]));
                            break;
                        case 4:
//						setStepSize(Float.parseFloat(vals[4]));
                            break;
                        case 5:
                            setAverageResults(Boolean.parseBoolean(vals[5]));
                            break;
                        case 6:
                            setLowPassResults(Boolean.parseBoolean(vals[6]));
                            break;
                        case 7:
                            setStepResults(Boolean.parseBoolean(vals[7]));
                            break;

                        case 8:
                            setPlayingState(Boolean.parseBoolean(vals[8]));
                            break;
                        case 9:
                            setArOffset(Float.parseFloat(vals[9]));
                            break;
                        case 10:
                            setLocal(Boolean.parseBoolean(vals[10]));
                            break;
                        case 11:
                            setPoiRemote(Boolean.parseBoolean(vals[11]));
                            break;
                        case 12:
                            setPoiLocal(Boolean.parseBoolean(vals[12]));
                            break;

                    }

                }
            }
        } catch (IOException e) {
            e.toString();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception e2) {
                    Log.e("", e2.getMessage());
                    e2.printStackTrace();
                }
        }
    }

    public boolean getAutoSelectFacility() {
        return autoSelectFacility;
    }

    public boolean isAutoSelectFacility() {
        return autoSelectFacility;
    }

    public void setAutoSelectFacility(boolean autoSelectFacility) {
        this.autoSelectFacility = autoSelectFacility;
    }

    public boolean ismTurnLogicOn() {
        return mTurnLogicOn;
    }

    public void setmTurnLogicOn(boolean mTurnLogicOn) {
        this.mTurnLogicOn = mTurnLogicOn;
    }

    public double getmTurnLogicDistance() {
        return mTurnLogicDistance;
    }

    public void setmTurnLogicDistance(double mTurnLogicDistance) {
        this.mTurnLogicDistance = mTurnLogicDistance;
    }

    public boolean isProjectOnGis() {
        return projectOnGis;
    }

    public void setProjectOnGis(boolean projectOnGis) {
        this.projectOnGis = projectOnGis;
    }

    public boolean isProjectOnPath() {
        return projectOnPath;
    }

    public void setProjectOnPath(boolean projectOnPath) {
        this.projectOnPath = projectOnPath;
    }

    public boolean useLegacySensors() {
        return mUseLegacySensors;
    }

    public void setUseLegacySensors(boolean useLegacySensors) {
        mUseLegacySensors = useLegacySensors;
    }

    public boolean isAutoSelectFloor() {
        if (isObserverUser()) {
            return false;
        }

        return autoSelectFloor;
    }

    public void setAutoSelectFloor(boolean autoSelectFloor) {
        this.autoSelectFloor = autoSelectFloor;
    }

    public boolean isSettingAutoSelectFloor() {
        return autoSelectFloor;
    }

    public double getFloorSelectorDistance() {
        return FloorSelectorDistance;
    }

    public void setFloorSelectorDistance(double floorSelectorDistance) {
        FloorSelectorDistance = floorSelectorDistance;
    }

    public boolean isStopNavigatinState() {
        return stopNavigatinState;
    }

    public void setStopNavigatinState(boolean stopNavigatinState) {
        this.stopNavigatinState = stopNavigatinState;
    }

    public float getArOffset() {
        return mArOffset;
    }

    public void setArOffset(float offset) {
        mArOffset = offset;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public boolean isAutocalibration() {
        return autocalibration;
    }

    public void setAutocalibration(boolean autocalibration) {
        this.autocalibration = autocalibration;
    }

    public float getInstructionsDistance() {
        return instructionsDistance;
    }

    public void setInstructionsDistance(float instructionsDistance) {
        this.instructionsDistance = instructionsDistance;
    }

    public int getFloorSelectionK() {
        return floorSelectionK;
    }

    public void setFloorSelectionK(int floorSelectionK) {
        this.floorSelectionK = floorSelectionK;
    }

    public int getAverageRange() {
        return averageRange;
    }

    public void setAverageRange(int averageRange) {
        this.averageRange = averageRange;
    }

    public boolean isSelectfloorbysum() {
        return selectfloorbysum;
    }

    public void setSelectfloorbysum(boolean selectfloorbysum) {
        this.selectfloorbysum = selectfloorbysum;
    }

    public boolean isSelectfloorbybonus() {
        return selectfloorbybonus;
    }

    public void setSelectfloorbybonus(boolean selectfloorbybouns) {
        this.selectfloorbybonus = selectfloorbybouns;
    }

    public boolean isTypeBin() {
        return typeBin;
    }

    public void setTypeBin(boolean typeBin) {
        this.typeBin = typeBin;
    }

    public int getWifilistminimum() {
        return wifilistminimum;
    }

    public void setWifilistminimum(int wifilistminimum) {
        this.wifilistminimum = wifilistminimum;
    }

    public boolean isOnecandidatefloor() {
        return onecandidatefloor;
    }

    public void setOnecandidatefloor(boolean onecandidatefloor) {
        this.onecandidatefloor = onecandidatefloor;
    }


    public long getBannerTimer() {
        return bannerTimer;
    }

    public void setBannerTimer(long bannerTimer) {
        this.bannerTimer = bannerTimer;
    }

    public long getBannerHideTimer() {
        return bannershideTimer;
    }

    public void setBannerHideTimer(long bannersHideTimer) {
        this.bannershideTimer = bannersHideTimer;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }

//	public int getLocationCloseRange() {
//		return locationCloseRange;
//	}
//
//	public void setLocationCloseRange(int locationCloseRange) {
//		this.locationCloseRange = locationCloseRange;
//	}

    public boolean isRecWlBlips() {
        return isRecWlBlips;
    }

    public void setRecWlBlips(boolean isRecWlBlips) {
        this.isRecWlBlips = isRecWlBlips;
    }

    public boolean isRotatingMap() {
        return RotatingMap;
    }

    public void setRotatingMap(boolean rotatingMap) {
        RotatingMap = rotatingMap;
    }

    public int getBlipsForZRecalculate() {
        return blipsForZRecalculate;
    }

    public void setBlipsForZRecalculate(int blipsForZRecalculate) {
        this.blipsForZRecalculate = blipsForZRecalculate;
    }


    public String getServerName() {
        return ServerName;
    }

    public void setServerName(String serverName) {
        ServerName = serverName;
    }

    public int getScannerMode() {
        return scannerMode;
    }

    public void setScannerMode(int scannerMode) {
        this.scannerMode = scannerMode;
    }

    public String getMatrixFilePrefix() {
        String result = null;
        if (scannerMode == MODE_BLE_SCAN) {
            result = "ble";
        } else if (scannerMode == MODE_RADIUS_SCAN) {
            result = "rds";
        } else {
            result = "";
        }
        return result;
    }

    //XXX  ADDED
    public float getLocatorDeadReckoningWeight() {
        return locatorDeadReckoningWieght;
    }

    //XXX  ADDED
    public void setLocatorDeadReckoningWieght(float locatorDeadReckoningWieght) {
        this.locatorDeadReckoningWieght = locatorDeadReckoningWieght;
    }

    public boolean isBannersShow() {
        return bannersShow;
    }

    public void setBannersShow(boolean bannersShow) {
        this.bannersShow = bannersShow;
    }


    //XXX  ADDED
    public int getBarometerSwSize() {
        return barometerSwSize;
    }

    //XXX  ADDED
    public void setBarometerSwSize(int barometerSwSize) {
        this.barometerSwSize = barometerSwSize;
    }

    //XXX  ADDED
    public float getBarometerModelThreshold() {
        return barometerModelThreshold;
    }

    //XXX  ADDED
    public void setBarometerModelThreshold(float barometerModelThreshold) {
        this.barometerModelThreshold = barometerModelThreshold;
    }

    //XXX  ADDED
    public int getBarometerHistoryThreshold() {
        return barometerHistoryThreshold;
    }

    //XXX  ADDED
    public void setBarometerHistoryThreshold(int barometerHistoryThreshold) {
        this.barometerHistoryThreshold = barometerHistoryThreshold;
    }

    //XXX  ADDED
    public boolean isBarometerOn() {
        return barometerOn;
    }

    //XXX  ADDED
    public void setBarometerOn(boolean barometerOn) {
        this.barometerOn = barometerOn;
    }

    //XXX  ADDED
    public boolean isBarometerCombinedMethod() {
        return brometerCombinedMethod;
    }

    //XXX  ADDED
    public void setBarometerCombinedMethod(boolean brometerCombinedMethod) {
        this.brometerCombinedMethod = brometerCombinedMethod;
    }

    public double getBoundsOffset() {
        return boundsOffset;
    }

    public void setBoundsOffset(double boundsOffset) {
        this.boundsOffset = boundsOffset;
    }

    //XXX APK
    public boolean isFirstInstall() {
        return firstInstall;
    }

    //XXX APK
    public void setFirstInstall(boolean firstInstall) {
        this.firstInstall = firstInstall;
    }

    //XXX APK
    public String getExternalStoragedir() {
        return externalStoragedir;
    }


    public int getPoiForInstructionRadius() {
        return poiForInstructionRadius;
    }

    public void setPoiForInstructionRadius(int poiForInstructionRadius) {
        this.poiForInstructionRadius = poiForInstructionRadius;
    }

    public boolean isGeoAutoDetect() {
        return geoAutoDetect;
    }

    public void setGeoAutoDetect(boolean geoAutoDetect) {
        this.geoAutoDetect = geoAutoDetect;
    }

    public int getBarometerPatternSize() {
        return barometerPatternSize;
    }

    public void setBarometerPatternSize(int barometerPatternSize) {
        this.barometerPatternSize = barometerPatternSize;
    }

    public String getCalibrationFilterPrefix() {
        return calibrationFilterPrefix;
    }

    public void setCalibrationFilterPrefix(String calibrationFilterPrefix) {
        this.calibrationFilterPrefix = calibrationFilterPrefix;
    }

    public boolean isDevelopmentMode() {
        return developmentMode;
    }

    public void setDevelopmentMode(boolean developmentMode) {
        this.developmentMode = developmentMode;
    }

    public float getLevelInsideElevatorThr() {
        return levelInsideElevatorThr;
    }

    public void setLevelInsideElevatorThr(float levelInsideElevatorThr) {
        this.levelInsideElevatorThr = levelInsideElevatorThr;
    }

    public boolean isInsideMainLobby() {
        return insideMainLobby;
    }

    public void setInsideMainLobby(boolean insideMainLobby) {
        this.insideMainLobby = insideMainLobby;
    }

    public boolean isInsideElevatorZone() {
        return insideElevatorZone;
    }

    public void setInsideElevatorZone(boolean insideElevatorZone) {
        this.insideElevatorZone = insideElevatorZone;
    }

    public int getChkDestReachTHR() {
        return chkDestReachTHR;
    }

    public void setChkDestReachTHR(int chkDestReachTHR) {
        this.chkDestReachTHR = chkDestReachTHR;
    }

    public float getChkDestReachRectRangeWidthMeters() {
        return chkDestReachRectRangeWidthMeters;
    }

    public void setChkDestReachRectRangeWidthMeters(
            float chkDestReachRectRangeWidthMeters) {
        this.chkDestReachRectRangeWidthMeters = chkDestReachRectRangeWidthMeters;
    }


    public String getLoadedFacilityByMajorityVoting() {
        return loadedFacilityByMajorityVoting;
    }

    public void setLoadedFacilityByMajorityVoting(
            String loadedFacilityByMajorityVoting) {
        this.loadedFacilityByMajorityVoting = loadedFacilityByMajorityVoting;
    }


    public boolean isFirstRunForMajorityVoting() {
        return isFirstRunForMajorityVoting;
    }

    public void setFirstRunForMajorityVoting(boolean isFirstRunForMajorityVoting) {
        this.isFirstRunForMajorityVoting = isFirstRunForMajorityVoting;
    }

    public boolean isSelectedPoiFromDialog() {
        return selectedPoiFromDialog;
    }

    public void setSelectedPoiFromDialog(boolean selectedPoiFromDialog) {
        this.selectedPoiFromDialog = selectedPoiFromDialog;
    }

    public boolean isWazeRuning() {
        return wazeRuning;
    }

    public void setWazeRuning(boolean wazeRuning) {
        this.wazeRuning = wazeRuning;
    }

    public boolean isObserverUser() {
        boolean result = (facilityID != null && !facilityID.equals(facilityByBlips)) && !locationPlayer;
        return result;
    }

    public boolean isLocationPlayer() {
        return locationPlayer;
    }

    public void setLocationPlayer(boolean locationPlayer) {
        this.locationPlayer = locationPlayer;
    }

    public String getFacilityByBlips() {
        return facilityByBlips;
    }

    public void setFacilityByBlips(String facilityByBlips) {
        this.facilityByBlips = facilityByBlips;
    }

    public long getStartNavigationTime() {
        return startNavigationTime;
    }

    public void setStartNavigationTime(long startNavigationTime) {
        this.startNavigationTime = startNavigationTime;
    }

    public float getPreInsructionDistance() {
        return preInsructionDistance;
    }

    public void setPreInsructionDistance(float preInsructionDistance) {
        this.preInsructionDistance = preInsructionDistance;
    }

    public boolean isNavigationState() {
        return isNavigationState;
    }

    public void setNavigationState(boolean isNavigationState) {
        this.isNavigationState = isNavigationState;
    }

    public boolean isMovingAverageOverLocation() {
        return isMovingAverageOverLocation;
    }

    public void setMovingAverageOverLocation(boolean isMovingAverageOverLocation) {
        this.isMovingAverageOverLocation = isMovingAverageOverLocation;
    }

    public int getMovingAverageOverLocationSW() {
        return movingAverageOverLocationSW;
    }

    public void setMovingAverageOverLocationSW(int movingAverageOverLocationSW) {
        this.movingAverageOverLocationSW = movingAverageOverLocationSW;
    }

    public boolean isTurnToClosestGisLineMethod() {
        return isTurnToClosestGisLineMethod;
    }

    public void setTurnToClosestGisLineMethod(boolean isTurnToClosestGisLineMethod) {
        this.isTurnToClosestGisLineMethod = isTurnToClosestGisLineMethod;
    }

    public float getTurnToClosestGisLineTHR() {
        return turnToClosestGisLineTHR;
    }

    public void setTurnToClosestGisLineTHR(float turnToClosestGisLineTHR) {
        this.turnToClosestGisLineTHR = turnToClosestGisLineTHR;
    }

    public float getTurnToClosestGisLineAngle() {
        return turnToClosestGisLineAngle;
    }

    public void setTurnToClosestGisLineAngle(float turnToClosestGisLineAngle) {
        this.turnToClosestGisLineAngle = turnToClosestGisLineAngle;
    }

    public boolean isAsymetricLocatorRadius() {
        return asymetricLocatorRadius;
    }

    public void setAsymetricLocatorRadius(boolean asymetricLocatorRadius) {
        this.asymetricLocatorRadius = asymetricLocatorRadius;
    }

    public String getUserLaguagePreference() {
        return userLaguagePreference;
    }

    public void setUserLaguagePreference(String userLaguagePreference) {
        this.userLaguagePreference = userLaguagePreference;
    }

    public String getDiviceLanguega() {
        return deviceLang;
    }

    public void setDiviceLanguega(String diviceLanguega) {
        this.deviceLang = diviceLanguega;
    }

    public PoiData getPoiextradetails() {
        return poiextradetails;
    }

    public void setPoiextradetails(PoiData poiextradetails) {
        this.poiextradetails = poiextradetails;
    }

    public String getWazeNavigationOption() {
        return wazeNavigationOption;
    }

    public void setWazeNavigationOption(String wazeNavigationOption) {
        this.wazeNavigationOption = wazeNavigationOption;
    }

    public boolean isUserPrefToShowWazeDialog() {
        return userPrefToShowWazeDialog;
    }

    public void setUserPrefToShowWazeDialog(boolean userPrefToShowWazeDialog) {
        this.userPrefToShowWazeDialog = userPrefToShowWazeDialog;
    }

    public boolean isJustExitNavigationActivity() {
        return justExitNavigationActivity;
    }

    public void setJustExitNavigationActivity(boolean justExitNavigationActivity) {
        this.justExitNavigationActivity = justExitNavigationActivity;
    }

    public float getCloseLineOnPathThreshold() {
        return closeLineOnPathThreshold;
    }

    public void setCloseLineOnPathThreshold(float closeLineOnPathThreshold) {
        this.closeLineOnPathThreshold = closeLineOnPathThreshold;
    }

    public String getReadHiddensStatus() {
        return readHiddens;
    }

    public void setReadHiddensStatus(String readHiddens) {
        this.readHiddens = readHiddens;
    }

    public boolean isTestWifiReadingHiddens() {
        return testWifiReadingHiddens;
    }

    public void setTestWifiReadingHiddens(boolean testWifiReadingHiddens) {
        this.testWifiReadingHiddens = testWifiReadingHiddens;
    }

    public boolean isUseKitKatVersionSolution() {
        return useKitKatVersionSolution;
    }

    public void setUseKitKatVersionSolution(boolean useKitKatVersionSolution) {
        this.useKitKatVersionSolution = useKitKatVersionSolution;
    }

    public boolean isDarwpoiadmin() {
        return darwpoiadmin;
    }

    public void setDarwpoiadmin(boolean darwpoiadmin) {
        this.darwpoiadmin = darwpoiadmin;
    }


    public String getCurrentAppVersion() {
        String app_ver = "";
        Context context = getMlinsContext();
        PackageManager packageManage = context.getPackageManager();
        if (packageManage != null) {
            PackageInfo packageInfo = null;
            try {
                packageInfo = packageManage.getPackageInfo(context.getPackageName(), 0);
                if (packageInfo != null) {
                    app_ver = packageInfo.versionName;
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (app_ver.equals("")) {
            return null;
        }

        return app_ver;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public boolean isBleLevelfilter() {
        return BleLevelfilter;
    }

    public void setBleLevelfilter(boolean bleLevelfilter) {
        BleLevelfilter = bleLevelfilter;
    }

    public boolean isPlayingMedia() {
        return isPlayingMedia;
    }

    public void setPlayingMedia(boolean isPlayingMedia) {
        this.isPlayingMedia = isPlayingMedia;
    }

    public String getCampusId() {
        return campusId;
    }

    public void setCampusId(String campusid) {
        this.campusId = campusid;
        setCampusDir(this.campusId);
    }

    public File getCampusDir() {
        return campusDir;
    }

    private void setCampusDir(String campusid) {
        File dir = new File(projectDir, campusid);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.campusDir = dir;
//		if(useZip){
//			File zipdir = new File(zipProjectDir, campusid);
//			if (!zipdir.exists()) {
//				zipdir.mkdirs();
//			}
//			this.zipCampusDir = zipdir;
//		}
    }

//	public Location getExternalDestination() {
//		return ExternalDestination;
//	}
//
//	public void setExternalDestination(Location externalDestination) {
//		ExternalDestination = externalDestination;
//	}

    public PoiData getInternalDestination() {
        return internalDestination;
    }

    public void setInternalDestination(PoiData internalDestination) {
        this.internalDestination = internalDestination;
    }


    public boolean isshowMediaMenu() {
//		return showMediaMenu;
        return false;
    }

    public void setshowMediaMenu(boolean showMediaMenu) {
        this.showMediaMenu = showMediaMenu;
    }

    public boolean isShowFarewellDialog() {
        return showFarewellDialog;
    }

    public void setShowFarewellDialog(boolean showFarewellDialog) {
        this.showFarewellDialog = showFarewellDialog;
        if (showFarewellDialog == false) {
            updateFarewellTimer();
        }
    }

    private void updateFarewellTimer() {
        numberOfFarewellTimer++;
        new Thread(new Runnable() {
            @Override
            public void run() {
                long delay = 30000;
                try {
                    Thread.sleep(delay);
                    if (numberOfFarewellTimer < 2) {
                        setShowFarewellDialog(true);
                    }
                    numberOfFarewellTimer--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public int getCloseDeviceThreshold() {
        return closeDeviceThreshold;
    }

    public void setCloseDeviceThreshold(int closeDeviceThreshold) {
        this.closeDeviceThreshold = closeDeviceThreshold;
    }

    public float getCloseDeviceWeight() {
        return closeDeviceWeight;
    }

    public void setCloseDeviceWeight(float closeDeviceWeight) {
        this.closeDeviceWeight = closeDeviceWeight;
    }

    public float getBubbleRange() {
        return bubbleRange;
    }

    public void setBubbleRange(float bubbleRange) {
        this.bubbleRange = bubbleRange;
    }
    //XXX MM
//	public boolean isDownloadingData() {
//		return downloadingData;
//	}
//
//	public void setDownloadingData(boolean downloadingData) {
//		this.downloadingData = downloadingData;
//	}

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projrctId) {
        this.projectId = projrctId;
    }

    public File getProjectDir() {
        return projectDir;
    }

    public void setProjectDir(String projectid) {
        File dir = new File(appdir, projectid);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.projectDir = dir;
//		if(useZip){
//			File zipDir = new File(zipAppdir,projectid);
//			if (!zipDir.exists()) {
//				zipDir.mkdirs();
//			}
//			this.zipProjectDir = zipDir;
//		}
    }


    public boolean isSdkObserverMode() {
        return sdkObserverMode;
    }

    public void setSdkObserverMode(boolean sdkObserverMode) {
        this.sdkObserverMode = sdkObserverMode;
    }


    public boolean isSimulationMode() {
        return simulationMode;
    }

    public void setSimulationMode(boolean simulationMode) {
        this.simulationMode = simulationMode;
    }

    public int getProximityThreshold() {
        return proximityThreshold;
    }

    public void setProximityThreshold(int proximityThreshold) {
        this.proximityThreshold = proximityThreshold;
    }

    public int getOutdoorNavigationMode() {
        return outdoorNavigationMode;
    }

    public void setOutdoorNavigationMode(int outdoorNavigationMode) {
        this.outdoorNavigationMode = outdoorNavigationMode;
    }


    public int getLogginglevel() {
        return logginglevel;
    }

    public void setLogginglevel(int logginglevel) {
        this.logginglevel = logginglevel;
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }

    public ArrayList<String> getFilteredpackages() {
        return filteredpackages;
    }

    public void setFilteredpackages(ArrayList<String> filteredpackages) {
        this.filteredpackages = filteredpackages;
    }

    public float getCheckBubblesRange() {
        return checkBubblesRange;
    }

    public void setCheckBubblesRange(float checkBubblesRange) {
        this.checkBubblesRange = checkBubblesRange;
    }

    public NavigationType getNavigationType() {
        return navigationType;
    }

    public void setNavigationType(NavigationType navigationType) {
        this.navigationType = navigationType;
    }

    public List<NavigationPath> getMultiPoiNavRoute() {
        return multiPoiNavRoute;
    }

    public void setMultiPoiNavRoute(List<NavigationPath> multiPoiNavRoute) {
        this.multiPoiNavRoute = multiPoiNavRoute;
    }

    public List<IPoi> getMultiPoiList() {
        return multiPoiList;
    }

    public void setMultiPoiList(List<IPoi> multiPoiList) {
        this.multiPoiList = multiPoiList;
    }

    public List<IPoi> getVisitedPoiList() {
        return visitedPoiList;
    }

    public void setVisitedPoiList(List<IPoi> visitedPoiList) {
        this.visitedPoiList = visitedPoiList;
    }

    public boolean isAddParkingToMultiPois() {
        return addParkingToMultiPois;
    }

    public void setAddParkingToMultiPois(boolean addParkingToMultiPois) {
        this.addParkingToMultiPois = addParkingToMultiPois;
    }

    public boolean isAddSwitchFloorsToMultiPois() {
        return addSwitchFloorsToMultiPois;
    }

    public void setAddSwitchFloorsToMultiPois(boolean addSwitchFloorsToMultiPois) {
        this.addSwitchFloorsToMultiPois = addSwitchFloorsToMultiPois;
    }

    public boolean isIncludeEntrancesInPoisNumbering() {
        return IncludeEntrancesInPoisNumbering;
    }

    public void setIncludeEntrancesInPoisNumbering(
            boolean includeEntrancesInPoisNumbering) {
        IncludeEntrancesInPoisNumbering = includeEntrancesInPoisNumbering;
    }

    public boolean isIncludeSwitchFloorsInPoisNumbering() {
        return includeSwitchFloorsInPoisNumbering;
    }

    public void setIncludeSwitchFloorsInPoisNumbering(
            boolean includeSwitchFloorsInPoisNumbering) {
        this.includeSwitchFloorsInPoisNumbering = includeSwitchFloorsInPoisNumbering;
    }

    public String getMultiPoisRouteColor() {
        return multiPoisRouteColor;
    }

    public void setMultiPoisRouteColor(String multiPoisRouteColor) {
        this.multiPoisRouteColor = multiPoisRouteColor;
    }

    public String getMultiPoisPointColor() {
        return multiPoisPointColor;
    }

    public void setMultiPoisPointColor(String multiPoisPointColor) {
        this.multiPoisPointColor = multiPoisPointColor;
    }

    public String getMultiPoisPointNumberColor() {
        return multiPoisPointNumberColor;
    }

    public void setMultiPoisPointNumberColor(String multiPoisPointNumberColor) {
        this.multiPoisPointNumberColor = multiPoisPointNumberColor;
    }

    public boolean isUuidScan() {
        return uuidScan;
    }

    public void setUuidScan(boolean uuidScan) {
        this.uuidScan = uuidScan;
    }

    public boolean isNotifyGoogleDestination() {
        return notifyGoogleDestination;
    }

    public void setNotifyGoogleDestination(boolean notifyGoogleDestination) {
        this.notifyGoogleDestination = notifyGoogleDestination;
    }

    public String getMultiPoisVisitedPointColor() {
        return multiPoisVisitedPointColor;
    }

    public void setMultiPoisVisitedPointColor(String multiPoisVesetedPointColor) {
        this.multiPoisVisitedPointColor = multiPoisVesetedPointColor;
    }

    public float getDefaultMapZoom() {
        return defaultMapZoom;
    }

    public void setDefaultMapZoom(float defaultMapZoom) {
        this.defaultMapZoom = defaultMapZoom;
    }

    public boolean isPresentDestinationIcon() {
        return presentDestinationIcon;
    }

    public void setPresentDestinationIcon(boolean presentDestinationIcon) {
        this.presentDestinationIcon = presentDestinationIcon;
    }

    public String getAppLanguage() {

        if (isAppLangAsDeviceLang()) {
            String localLang = getUserLaguagePreference();
            if (localLang.contains("en")) {
                return "english";
            } else if (localLang.contains("he")) {
                return "hebrew";
            } else if (localLang.contains("ar")) {
                return "arabic";
            } else if (localLang.contains("ru")) {
                return "russian";
            } else if (localLang.contains("es")) {
                return "spanish";
            } else if (localLang.contains("ja")) {
                return "japanies";
            } else {
                return "english";
            }
        }
        return appLanguage;
    }

    public void setAppLanguage(String appLanguage) {
        this.appLanguage = appLanguage;
    }


    public float getPoiBubbleScaleFactor() {
        return poiBubbleScaleFactor;
    }

    public void setPoiBubbleScaleFactor(float poiBubbleScaleFactor) {
        this.poiBubbleScaleFactor = poiBubbleScaleFactor;
    }

    public String getStreamServerName() {
        return streamServerName;
    }

    public void setStreamServerName(String streamServerName) {
        this.streamServerName = streamServerName;
    }

    public int getRefreshUsersInterval() {
        return refreshUsersInterval;
    }

    public void setRefreshUsersInterval(int refreshUsersInterval) {
        this.refreshUsersInterval = refreshUsersInterval;
    }


    public boolean isAnalytic() {
        return analytic;
    }

    public void setAnalytic(boolean analytic) {
        this.analytic = analytic;
    }

    public String getLockedFacility() {
        return lockedFacility;
    }

    public void setLockedFacility(String lockedFacility) {
        this.lockedFacility = lockedFacility;
    }

    public boolean isLockedOnFacility() {
        return lockedOnFacility;
    }

    public void setLockedOnFacility(boolean lockedOnFacility) {
        this.lockedOnFacility = lockedOnFacility;
    }

    public DeliveredLocationType getDeliveredLocationType() {
        return deliveredLocationType;
    }

    public void setDeliveredLocationType(DeliveredLocationType deliveredLocationType) {
        this.deliveredLocationType = deliveredLocationType;
    }


    public int getSameSegmentThreshold() {
        return sameSegmentThreshold;
    }

    public void setSameSegmentThreshold(int thr) {
        sameSegmentThreshold = thr;
    }

    public boolean isNeighborsSegmentMethod() {
        return neighborsSegmentMethod;
    }

    public void setNeighborsSegmentMethod(boolean neighborsSegmentMethod) {
        this.neighborsSegmentMethod = neighborsSegmentMethod;
    }


    public float getSmallSegmentLength() {
        return smallSegmentLength;
    }

    public void setSmallSegmentLength(float smallSegmentLength) {
        this.smallSegmentLength = smallSegmentLength;
    }

    public float getDistanceFromIntersection() {
        return distanceFromIntersection;
    }

    public void setDistanceFromIntersection(float distanceFromIntersection) {
        this.distanceFromIntersection = distanceFromIntersection;
    }

    public float getDefaultDualMapZoom() {
        return defaultDualMapZoom;
    }


    public void setDefaultDualMapZoom(float defaultDualMapZoom) {
        this.defaultDualMapZoom = defaultDualMapZoom;
    }


    public ExitsSelectionType getExitsSelectionType() {
        return exitsSelectionType;
    }


    public void setExitsSelectionType(ExitsSelectionType exitsSelectionType) {
        this.exitsSelectionType = exitsSelectionType;
    }


    public boolean isFloorPickerVisibile() {
        return FloorPickerVisibile;
    }


    public void setFloorPickerVisibile(boolean floorPickerVisibile) {
        FloorPickerVisibile = floorPickerVisibile;
    }


    public boolean isSimplifiedInstruction() {
        return isSimplifiedInstruction;
    }


    public void setSimplifiedInstruction(boolean isSimplifiedInstruction) {
        this.isSimplifiedInstruction = isSimplifiedInstruction;
    }


    public String getMultiPoisVisitedPointNumberColor() {
        return multiPoisVisitedPointNumberColor;
    }


    public void setMultiPoisVisitedPointNumberColor(
            String multiPoisVisitedPointNumberColor) {
        this.multiPoisVisitedPointNumberColor = multiPoisVisitedPointNumberColor;
    }


    public Bitmap getIconForMultiPointSwitchFloor() {
        return iconForMultiPointSwitchFloor;
    }


    public void setIconForMultiPointSwitchFloor(
            Bitmap iconForMultiPointSwitchFloor) {
        this.iconForMultiPointSwitchFloor = iconForMultiPointSwitchFloor;
    }


    public Bitmap getIconForMultiPointExit() {
        return iconForMultiPointExit;
    }


    public void setIconForMultiPointExit(Bitmap iconForMultiPointExit) {
        this.iconForMultiPointExit = iconForMultiPointExit;
    }


    public boolean isAddEntranceToMultiPois() {
        return addEntranceToMultiPois;
    }


    public void setAddEntranceToMultiPois(boolean addEntranceToMultiPois) {
        this.addEntranceToMultiPois = addEntranceToMultiPois;
    }


    public float getHidingPoisZoomLevel() {
        return hidingPoisZoomLevel;
    }


    public void setHidingPoisZoomLevel(float hidingPoisZoomLevel) {
        this.hidingPoisZoomLevel = hidingPoisZoomLevel;
    }


    public float getNavPathWidth() {
        return navPathWidth;
    }


    public void setNavPathWidth(float navPathWidth) {
        this.navPathWidth = navPathWidth;
    }


    public boolean isDrawArrowsOnPath() {
        return drawArrowsOnPath;
    }


    public void setDrawArrowsOnPath(boolean drawArrowsOnPath) {
        this.drawArrowsOnPath = drawArrowsOnPath;
    }

    public boolean isForceFullZip() {
        return forceFullZip;
    }

    public void setForceFullZip(boolean forceFullZip) {
        this.forceFullZip = forceFullZip;
    }

    public boolean isTrasnlateLabels() {
        return trasnlateLabels;
    }

    public void setTrasnlateLabels(boolean trasnlateLabels) {
        this.trasnlateLabels = trasnlateLabels;
    }

    public boolean isShowNavigationMarkers() {
        return showNavigationMarkers;
    }

    /**
     * Method is deprecated, use
     *
     * {@link com.mlins.dualmap.DualMapView#showOriginMarker(boolean) DualMapView.showOriginMarker()}
     * {@link com.mlins.dualmap.DualMapView#showSwitchFloorMarkers(boolean)} (boolean) DualMapView.showSwitchFloorMarkers()}
     * {@link com.mlins.dualmap.DualMapView#showNavigatedDestinationMarker(boolean)} (boolean) DualMapView.showNavigatedDestinationMarker()}
     *
     * methods instead.
     */
    @Deprecated
    public void setShowNavigationMarkers(boolean showNavigationMarkers) {
        this.showNavigationMarkers = showNavigationMarkers;
    }

    public boolean isUseProximityLocation() {
        return useProximityLocation;
    }

    public void setUseProximityLocation(boolean useProximityLocation) {
        this.useProximityLocation = useProximityLocation;
    }

    public void setDisplayLabelsForPOIs(boolean display) {
        this.displayLabelsForPOIs = display;
    }

    public boolean displayLabelsForPOIs() {
        return displayLabelsForPOIs;
    }

    public int getScreenDensity(){
        return screenDensity;
    }


    public BridgeSelectionType getBridgeSelectionType() {
        return bridgeSelectionType;
    }

    public void setBridgeSelectionType(BridgeSelectionType bridgeSelectionType) {
        this.bridgeSelectionType = bridgeSelectionType;
    }

    public boolean isUseRotatingUserIcon() {
        return useRotatingUserIcon;
    }

    public void setUseRotatingUserIcon(boolean useRotatingUserIcon) {
        this.useRotatingUserIcon = useRotatingUserIcon;
    }

    public float getMapMaxZoomLimit() {
        return mapMaxZoomLimit;
    }

    public void setMapMaxZoomLimit(float mapMaxZoomLimit) {
        this.mapMaxZoomLimit = mapMaxZoomLimit;
    }

    public boolean isPlayStraightSound() {
        return playStraightSound;
    }

    public void setPlayStraightSound(boolean playStraightSound) {
        this.playStraightSound = playStraightSound;
    }

    public boolean isStayIndoor() {
        return StayIndoor;
    }

    public void setStayIndoor(boolean stayIndoor) {
        StayIndoor = stayIndoor;
    }

    public boolean isMapAnimation() {
        return mapAnimation;
    }

    public void setMapAnimation(boolean mapAnimation) {
        this.mapAnimation = mapAnimation;
    }

    public SwitchFloorSelectionType getSwitchFloorSelectionType() {
        return switchFloorSelectionType;
    }

    public void setSwitchFloorSelectionType(SwitchFloorSelectionType switchFloorSelectionType) {
        this.switchFloorSelectionType = switchFloorSelectionType;
    }

    public boolean isUseZipWithoutMaps() {
        return useZipWithoutMaps;
    }

    public void setUseZipWithoutMaps(boolean useZipWithoutMaps) {
        this.useZipWithoutMaps = useZipWithoutMaps;
    }

    public boolean isUseExitPoiRangeForExit() {
        return useExitPoiRangeForExit;
    }

    public void setUseExitPoiRangeForExit(boolean useExitPoiRangeForExit) {
        this.useExitPoiRangeForExit = useExitPoiRangeForExit;
    }

    public int getConsecutiveCountForExit() {
        return consecutiveCountForExit;
    }

    public void setConsecutiveCountForExit(int consecutiveCountForExit) {
        this.consecutiveCountForExit = consecutiveCountForExit;
    }

    public int getExitPoiRange() {
        return exitPoiRange;
    }

    public void setExitPoiRange(int exitPoiRange) {
        this.exitPoiRange = exitPoiRange;
    }

    public int getLowerBoundSupplement() {
        return LowerBoundSupplement;
    }

    public void setLowerBoundSupplement(int lowerBoundSupplement) {
        LowerBoundSupplement = lowerBoundSupplement;
    }

    public int getFloorFilterSupplement() {
        return floorFilterSupplement;
    }

    public void setFloorFilterSupplement(int floorFilterSupplement) {
        this.floorFilterSupplement = floorFilterSupplement;
    }

    public boolean isDisplaySwitchFloorInstructionExtra() {
        return displaySwitchFloorInstructionExtra;
    }

    public void setDisplaySwitchFloorInstructionExtra(boolean displaySwitchFloorInstructionExtra) {
        this.displaySwitchFloorInstructionExtra = displaySwitchFloorInstructionExtra;
    }

    public String getTilesServerName() {
        return tilesServerName;
    }

    public void setTilesServerName(String tilesServerName) {
        this.tilesServerName = tilesServerName;
    }

    public boolean isChooseShortestRoute() {
        return chooseShortestRoute;
    }

    public void setChooseShortestRoute(boolean chooseShortestRoute) {
        this.chooseShortestRoute = chooseShortestRoute;
    }

    public boolean isUseTurnBackInstruction() {
        return useTurnBackInstruction;
    }

    public void setUseTurnBackInstruction(boolean useTurnBackInstruction) {
        this.useTurnBackInstruction = useTurnBackInstruction;
    }

    public boolean isPoisZoomFiltering() {
        return poisZoomFiltering;
    }

    public void setPoisZoomFiltering(boolean poisZoomFiltering) {
        this.poisZoomFiltering = poisZoomFiltering;
    }

    public boolean isDrawInvisibleFloorsRoute() {
        return drawInvisibleFloorsRoute;
    }

    public void setDrawInvisibleFloorsRoute(boolean drawInvisibleFloorsRoute) {
        this.drawInvisibleFloorsRoute = drawInvisibleFloorsRoute;
    }

    public boolean isClickableDynamicBubbles() {
        return clickableDynamicBubbles;
    }

    public void setClickableDynamicBubbles(boolean clickableDynamicBubbles) {
        this.clickableDynamicBubbles = clickableDynamicBubbles;
    }

    public boolean isUseBridgeEntranceParameters() {
        return useBridgeEntranceParameters;
    }

    public void setUseBridgeEntranceParameters(boolean useBridgeEntranceParameters) {
        this.useBridgeEntranceParameters = useBridgeEntranceParameters;
    }

    public boolean isDrawInvisibleNavMarkers() {
        return drawInvisibleNavMarkers;
    }

    public void setDrawInvisibleNavMarkers(boolean drawInvisibleNavMarkers) {
        this.drawInvisibleNavMarkers = drawInvisibleNavMarkers;
    }

    public boolean isDrawRouteTails() {
        return drawRouteTails;
    }

    public void setDrawRouteTails(boolean drawRouteTails) {
        this.drawRouteTails = drawRouteTails;
    }

    public int getPlaySwitchFloorRadius() {
        return playSwitchFloorRadius;
    }

    public void setPlaySwitchFloorRadius(int playSwitchFloorRadius) {
        this.playSwitchFloorRadius = playSwitchFloorRadius;
    }

    public float getVirtualRouteAlpha() {
        return virtualRouteAlpha;
    }

    public void setVirtualRouteAlpha(float virtualRouteAlpha) {
        this.virtualRouteAlpha = virtualRouteAlpha;
    }

    public boolean isDrawGoogleMapsBuildings() {
        return drawGoogleMapsBuildings;
    }

    public void setDrawGoogleMapsBuildings(boolean drawGoogleMapsBuildings) {
        this.drawGoogleMapsBuildings = drawGoogleMapsBuildings;
    }

    public boolean isHandicappedRouting() {
        return handicappedRouting;
    }

    public void setHandicappedRouting(boolean handicappedRouting) {
        this.handicappedRouting = handicappedRouting;
    }

    public boolean isStaffRouting() {
        return staffRouting;
    }

    public void setStaffRouting(boolean staffRouting) {
        this.staffRouting = staffRouting;
    }

    public String getRestServerName() {
        return restServerName;
    }

    public void setRestServerName(String restServerName) {
        this.restServerName = restServerName;
    }

    public String getWebInterfaceUrl() {
        String result = null;
        Campus campus = ProjectConf.getInstance().getSelectedCampus();
        if (campus != null) {
           result = campus.getWebInterfaceUrl();
        }
        return result;
    }
}
