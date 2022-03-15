package com.mlins.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mlins.aStar.GisSegment;
import com.mlins.aStar.NavigationPath;
import com.mlins.aStar.aStarData;
import com.mlins.aStar.aStarMath;
import com.mlins.instructions.Instruction;
import com.mlins.instructions.InstructionBuilder;
import com.mlins.locationutils.LocationFinder;
import com.mlins.locator.LocationCorrector;
import com.mlins.locator.LocationLocator;
import com.mlins.maping.IconSprite;
import com.mlins.maping.InstructionSprite;
import com.mlins.maping.LayerObject;
import com.mlins.maping.MultiPoiListSprite;
import com.mlins.maping.RectSprite;
import com.mlins.nav.utils.OrederPoisUtil;
import com.mlins.nav.utils.ParkingUtil;
import com.mlins.nav.utils.SoundPlayer;
import com.mlins.navigation.NavigationUtil;
import com.mlins.navigation.PathCalculator;
import com.mlins.orientation.OrientationMonitor;
import com.mlins.res.setup.ConfigsLoader;
import com.mlins.utils.AnimationsHolder;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.FloorData;
import com.mlins.utils.LablesDataHolder;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PathConvertor;
import com.mlins.utils.PoiData;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceDownloader;
import com.mlins.utils.gis.GisData;
import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.GisPoint;
import com.mlins.utils.gis.Location;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class IndoorMapView extends FrameLayout implements IFloorSetter {
    private static final String TAG = IndoorMapView.class.getName();
    private Context ctx = null;
    private d2dTouchImageView facilityMap;
    private ILocation myLocation = null;
    private int floor = -1;
    // private long followMeModeResetInterval = 6000;
    private int bubbleCounter = 0;
    private boolean navigationState = false;
    private NavigationInstructionsViewer instructionsViewer;
    private boolean postDestination = false;
    private int destinationCounter = 0;
    private List<SpreoMapViewListener> listeners = new ArrayList<SpreoMapViewListener>();
    private List<SpreoNavigationListener> navigationListeners = new ArrayList<SpreoNavigationListener>();
    private Location lastPlayerPoint = null;
    private ILocation lastLocatonForBubbles = null;
//	private Location parking = null;

    // // ======Camera Nav
    // private CameraPreview mCameraNavPreview = null;
    // private GLSurfaceView mCameraNavGLView = null;
    // private direction mCameraNavDirection = null;
    // private FrameLayout cameraNavLayout = null;
    // private float mCameraNavAngle = 0;
    // private Handler mCameraNavHandler = null;
    // private boolean isCameraNavState = false;
    // private GeoFenceRect currentElevatorZone = null;
    // private int lastSelectedFloor = -100;
    // // ======Camera Nav

    public IndoorMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.indoor_map_view, this, true);
    }

    public void init() {
        facilityMap = (d2dTouchImageView) findViewById(R.id.facilityMap);
        instructionsViewer = (NavigationInstructionsViewer) findViewById(R.id.na_nav_ins_row);
        facilityMap.setActivity(this);
        facilityMap.setZoom(PropertyHolder.getInstance().getDefaultMapZoom());
        facilityMap.setFollowMeMode(true);

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        facilityMap.setPreferedSize(width, height);
        facilityMap.createCtrls();
        // initCameraNavigation();
    }

    public void refreshPois() {
        PoiDataHelper.getInstance().drawPois(facilityMap);
    }

    private void setFacility(ILocation cloc) {
        if (cloc instanceof Location) {
            Location loc = (Location) cloc;
            FacilityConf facConf = FacilityContainer.getInstance().getSelected();
            if (PropertyHolder.getInstance().isLockedOnFacility()) {
                String lockedfacid = PropertyHolder.getInstance().getLockedFacility();
                if (lockedfacid != null && loc.getFacilityId() != null && !lockedfacid.equals(loc.getFacilityId())) {
                    return;
                }
            }
            if (!facConf.getId().equals(loc.getFacilityId()) && !loc.getFacilityId().equals("unknown")) {

                for (SpreoMapViewListener o : listeners) {
                    try {
                        o.mapWillSwapTo(LocationFinder.INDOOR_MODE);
                    } catch (Exception e) {
                        Log.getInstance().error(TAG, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }

                // do load
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

                setFloor((int) loc.getZ());
                setFloorMap();
                reDrawPois();
                resetMapRotation();
                resetAnimations();


                PoiData destination = aStarData.getInstance().getInternalDestination();
                if (destination != null && myLocation != null && myLocation.getFacilityId().equals(destination.getFacilityID())) {
                    aStarData.getInstance().setExternalDestination(null);
                    aStarData.getInstance().setExternalPoi(null);
                    Location origin = (Location) myLocation;
                    NavigationUtil.indoorToIndoorNavigation(origin, destination);
                }
                setNavigationPath();
            }
        }

    }

    public void applyIndoorLocation(ILocation location) {
//		if (PropertyHolder.getInstance().isSdkObserverMode()) {
//			return;
//		}

        myLocation = location;
        float x = (float) myLocation.getX();
        float y = (float) myLocation.getY();
        int z = (int) myLocation.getZ();
        setFacility(location);
        PointF p = new PointF(x, y);
        if (!PropertyHolder.getInstance().isSdkObserverMode()) {
            checkFloorChange(z);
        }
        if (facilityMap.getFollowMeMode() && !PropertyHolder.getInstance().isSdkObserverMode())
            facilityMap.moveMyLocationAndMapTo(p);
        else {
            // facilityMap.moveMyLocationTo(p);
            facilityMap.setMyLocation(p);
        }

        if (PropertyHolder.getInstance().isDevelopmentMode()) {
            facilityMap.drawClosestPoints(LocationLocator.getInstance()
                    .getclosetspoints());
        }

        if (!PropertyHolder.getInstance().isSdkObserverMode()) {
            rotateMap(p);
            checkFollowMeMode(p);
            checkPoiBubbles(p, z);
        }

        if (navigationState) {
            navigationRoutine(p, z);
        }

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    facilityMap.invalidate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        post(r);

    }

    private void checkFloorChange(int z) {
        if (z != floor) {

            facilityMap.setFloorNumber(z);
            if (navigationState && floor != -1) {
                notifyOnNavigationStateChanged(NavigationState.FLOOR_CHANGED);
            }
            floor = z;
            checkRecalculateByZ(floor);
            facilityMap.setNavigationPath();

            drawMultiPoiRoute();
        }
    }

    private void checkRecalculateByZ(int z) {
        if (navigationState) {
            boolean iszpathexists = isZPathExists(z);
            if (!iszpathexists) {
                // PathCalculator.calculatePath();
                // NavigationPath nav = aStarData.getInstance().getCurrentPath();
                //
                // if (nav != null) {
                // InstructionBuilder.getInstance().getInstractions(nav);
                // }
                PoiData destination = aStarData.getInstance().getDestination().getPoi();
                NavigationUtil.indoorToIndoorNavigation((Location) myLocation, destination);
            }
        }

    }

    private void navigationRoutine(PointF p, int z) {

        recalculatepath(p);
        updateNavigationPathWithoutGis();
        Instruction instruction = null;
        Instruction closeswitchinstruction = InstructionBuilder.getInstance().findCloseSwitchFloorInstruction(z, p);
        if (closeswitchinstruction != null) {
            PointF pointforplaying = new PointF((float) closeswitchinstruction.getLocation().getX(), (float) closeswitchinstruction.getLocation().getY());
            playSwitchInstruction(closeswitchinstruction, pointforplaying);
        } else {
            instruction = InstructionBuilder.getInstance().findCloseInstruction(p);
            if (instruction != null) {
                updateInstruction(instruction, p);
            }
        }

        Location destLoc = aStarData.getInstance().getDestination();
        if (destLoc != null && aStarData.getInstance().getExternalDestination() == null) {
            checkNavDestinationReached(destLoc, p);
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

    private void playSwitchInstruction(final Instruction instruction, PointF pointforplaying) {
        InstructionBuilder.getInstance().setNextInstruction(instruction);
        instructionsViewer.setVisibility((PropertyHolder.getInstance().isDisplayNavigationInstructionsOnMap() ? View.VISIBLE : View.INVISIBLE));
        instructionsViewer.updateBubble(0);

        if (!instruction.hasPlayed()) {
            instruction.setPlayed(true);
            if (!PropertyHolder.getInstance().isNavigationInstructionsSoundMute()) {
                SoundPlayer.getInstance().play(instruction.getSound());
            }
            INavInstruction inavInst = InstructionBuilder.getInstance().getNextNavInstruction(instruction);
            notifyOnNavigationInstructionRangeEntered(inavInst);
        }

    }

    private void checkPoiBubbles(PointF p, int z) {

        facilityMap.openClosePoiBubbles(p);

    }

    private void checkFollowMeMode(PointF p) {
        Long t = System.currentTimeMillis();
        if (facilityMap.followMeMode == false && t > facilityMap.getTimeOfTouchEvent() + PropertyHolder.getInstance().getUserAutoFollowTimeInterval()/* followMeModeResetInterval */) {
            facilityMap.SetCenter(p);
            AnimationsHolder.getInstance().reset();
            AnimationsHolder.releaseInstance();
            facilityMap.setFollowMeMode(true);
            facilityMap.hideTarget();
            if (PropertyHolder.getInstance().getRotatingMapType() == MapRotationType.COMPASS) {
                facilityMap.setCompassMode(true, true);
            }
        }

    }

    public void setOnPoiClickListener(SpreoMapViewListener listener) {
        facilityMap.setListener(listener);
    }

    public void setFloorMap() {
        Log.getInstance().debug(TAG, "Enter SetFloorMap ()");
        try {
            String currentMapUri;
            int currentFloor;
            List<FloorData> currentMapData;
            // FacilityConf.getInstance();
            FacilityConf FacilityConf = FacilityContainer.getInstance().getSelected();
            currentFloor = FacilityConf.getSelectedFloor();
            if (currentFloor == -1) {
                // error should never happen

            } else {

                for (SpreoMapViewListener o : listeners) {
                    o.mapWillChangeFloor(currentFloor);
                }

                currentMapData = FacilityConf.getFloorDataList();
                // XXX: 07-22 08:43:10.898: E/AndroidRuntime(24875): Caused by:
                // java.lang.IndexOutOfBoundsException: Invalid index 0, size is
                // 0
                currentMapUri = currentMapData.get(currentFloor).mapuri;

                facilityMap.setImageBitmap(ResourceDownloader.getInstance().getLocalBitmap(currentMapUri, true));

                facilityMap.setMaxZoom(5f);

                showLablesSprite();
                if (PropertyHolder.getInstance().isDevelopmentMode()) {
                    facilityMap.drawGeofence();
                    facilityMap.drawSwitchFloors();
                }

                ILocation parking = ParkingUtil.getInstance().getParkingLocation();
                if (parking != null) {
                    if (parking.getFacilityId() != null && parking.getFacilityId().equals(FacilityConf.getId())
                            && parking.getZ() == currentFloor) {
                        updateParkingMarker(parking);
                    }
                }

                // if (PropertyHolder.getInstance().isSdkObserverMode()) {
                // facilityMap.SetCenter(facilityMap.getCenter());
                // }
                facilityMap.invalidate();

                for (SpreoMapViewListener o : listeners) {
                    o.mapDidChangeFloor(currentFloor);
                }

            }
        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);

        }
        Log.getInstance().debug(TAG, "Exit SetFloorMap()");
    }

    private void rotateMap(PointF p) {
        FacilityConf FacilityConf = FacilityContainer.getInstance().getCurrent();


        if (PropertyHolder.getInstance().isLocationPlayer() && navigationState) {
            Location playerLocation = new Location();
            playerLocation.setX(p.x);
            playerLocation.setY(p.y);
            playerLocation.setZ(FacilityConf.getSelectedFloor());
            if (GisData.getInstance().isLocationsOnSameLine(lastPlayerPoint, playerLocation)) {
                float myangle = MathUtils.getLIneAngle(lastPlayerPoint, playerLocation);
                float todegrees = (-(myangle + FacilityConf.getFloorRotation()) + 360) % 360;
                facilityMap.setImageRotationAnimation(todegrees);
            }
        } else if (!PropertyHolder.getInstance().isSdkObserverMode() && facilityMap.getFollowMeMode()) {
            if (PropertyHolder.getInstance().getRotatingMapType() == MapRotationType.COMPASS) {
                facilityMap.updateCompassRotation();
            } else if (PropertyHolder.getInstance().getRotatingMapType() == MapRotationType.ORIENTATION) {
                int range = 45;
                GisLine cline = GisData.getInstance().findGisLine(p);
                if (cline != null) {
                    float myangle = OrientationMonitor.getInstance().getAzimuth() - FacilityContainer.getInstance().getSelected().getFloorRotation();
                    myangle = (myangle + 360) % 360;
                    float degrees = getLineAngle(cline);
                    float tempdegrees = getLineAngle(cline);

                    tempdegrees = (tempdegrees + 360) % 360;

                    if (Math.abs(myangle - tempdegrees) < range) {
                        float todegrees = (-(degrees + FacilityContainer.getInstance().getSelected().getFloorRotation()) + 360) % 360;
                        float currentangle = ((facilityMap.getImageRotation() - FacilityContainer.getInstance().getSelected().getFloorRotation()) + 360) % 360;
                        if (Math.abs(currentangle - todegrees) > 1.0) {
                            facilityMap.setImageRotationAnimation(todegrees);
                        }
                    } else {
                        if (Math.abs(myangle - (tempdegrees + 180)) % 360 < range || Math.abs(myangle - (tempdegrees - 180)) % 360 < range) {
                            float todegrees = (-((degrees + 180) % 360 + FacilityContainer.getInstance().getSelected().getFloorRotation()) + 360) % 360;
                            float currentangle = ((facilityMap.getImageRotation() - FacilityContainer.getInstance().getSelected().getFloorRotation()) + 360) % 360;
                            if (Math.abs(currentangle - todegrees) > 1.0) {
                                facilityMap.setImageRotationAnimation(todegrees);
                            }
                        }
                    }
                }
            }

            facilityMap.setFollowMeMode(true);
        }


        if (PropertyHolder.getInstance().isLocationPlayer()) {
            Location lastplayerpoint = new Location();
            lastplayerpoint.setX(p.x);
            lastplayerpoint.setY(p.y);
            lastplayerpoint.setZ(FacilityConf.getSelectedFloor());
            lastPlayerPoint = lastplayerpoint;
        }

    }

    private float getLineAngle(GisLine line) {
        float p1x = (float) line.getPoint1().getX();
        float p1y = (float) line.getPoint1().getY();
        float p2x = (float) line.getPoint2().getX();
        float p2y = (float) line.getPoint2().getY();
        double dx = p1x - p2x;
        // Minus to correct for coord re-mapping
        double dy = -(p1y - p2y);

        double inRads = Math.atan2(dy, dx);

        // We need to map to coord system when 0 degree is at 3 O'clock, 270 at
        // 12 O'clock
        if (inRads < 0)
            inRads = Math.abs(inRads);
        else
            inRads = 2 * Math.PI - inRads;

        return (float) (Math.toDegrees(inRads) - 90);
    }

    public double getAngle(GisSegment line) {
        float p1x = (float) line.getLine().getPoint1().getX();
        float p1y = (float) line.getLine().getPoint1().getY();
        float p2x = (float) line.getLine().getPoint2().getX();
        float p2y = (float) line.getLine().getPoint2().getY();
        double dx = p1x - p2x;
        // Minus to correct for coord re-mapping
        double dy = -(p1y - p2y);

        double inRads = Math.atan2(dy, dx);

        // We need to map to coord system when 0 degree is at 3 O'clock, 270 at
        // 12 O'clock
        if (inRads < 0)
            inRads = Math.abs(inRads);
        else
            inRads = 2 * Math.PI - inRads;

        return Math.toDegrees(inRads) - 90;
    }

    public void showLablesSprite() {
        FacilityConf FacilityConf = FacilityContainer.getInstance().getSelected();

        int cfloor = FacilityConf.getSelectedFloor();

        LablesDataHolder.getInstance().addLablesSprites(facilityMap, cfloor);

    }

    public void removeLablesSprites() {
        LablesDataHolder.getInstance().clean();
    }

    @Override
    public void setFloorNumber(int floorNumber) {

    }

    @Override
    public void setNavigationPath() {
        Log.getInstance().debug(TAG, "Enter setNavigationPath()");
        try {
//            PointF myloc = aStarData.getInstance().getMyLocation();
//            if (myloc == null && PropertyHolder.getInstance().isSdkObserverMode()) {
//                // set my location to entrance
//                PoiData epoi = PoiDataHelper.getInstance().getExitPoi();
//                if (epoi != null) {
//                    myloc = epoi.getPoint();
//                } else {
//                    myloc = new PointF(0, 0);
//                }
//
//            }
//            Location poiloc = aStarData.getInstance().getPoilocation();
//            if (poiloc != null && myloc != null) {
//
//                NavigationPath navigation = aStarData.getInstance().getCurrentPath(); // new NavigationPath(p);
//                FacilityConf FacilityConf = FacilityContainer.getInstance().getSelected();
//                int currentz = FacilityConf.getSelectedFloor();
//
//                List<Drawable> sprites = new ArrayList<Drawable>();
//                sprites = PathConvertor.convertPath(navigation, this.getContext(), currentz);
//
//                // LayerObject path = new LayerObject();
//                LayerObject path = facilityMap.getLayers().get("path");
//                path.clearSprites();
//                path.addAll(sprites);
//
//                if (PropertyHolder.getInstance().isPresentDestinationIcon()) {
//                    LayerObject instructionslayer = facilityMap.getLayers()
//                            .get("instructions");
//                    instructionslayer.clearSprites();
//                    Location dest = aStarData.getInstance().getDestination();
//                    if (dest.getZ() == currentz) {
//                        List<GisSegment> data = navigation.getPathByZ(currentz);
//                        InstructionSprite destsprite = new InstructionSprite(
//                                data.get(data.size() - 1), null);
//                        // path.addSprite(destsprite);
//
//                        // XXX PLASTER
//                        GisSegment destSeg = data.get(data.size() - 1);
//
//                        destsprite.scaleBitmap(0.8);
//                        IconSprite ics = destsprite.convertToIconSprite();
//                        float destX = (float) destSeg.getLine().point2.getX();
//                        float destY = (float) destSeg.getLine().point2.getY();
//                        // destY=destY-53;
//                        // destX=destX-25;
//                        ics.setLoc(new PointF(destX, destY));
//                        instructionslayer.addSprite(ics);
//                        // XXXX======
//
//                    }
//                }
//                // sprites = PathConvertor.convertInstructions(navigation, this,
//                // currentz);
//                // path.addAll(sprites);
//                path.show();
//                // mPlanView.getLayers().put("path", path);
//
//                navigationState = true;
//                PropertyHolder.getInstance().setNavigationState(navigationState);
//
//                LocationLocator.getInstance().setNavigationState(navigationState, navigation);
//
//                //XXX display and notify about first instruction
//                List<Instruction> instructionsList = InstructionBuilder.getInstance().getCurrentInstructions();
//                if (instructionsList != null && !instructionsList.isEmpty()) {
//                    Instruction instruction = instructionsList.get(0);
//                    InstructionBuilder.getInstance().setNextInstruction(instruction);
//                    if (instructionsViewer != null) {
//                        instructionsViewer.setVisibility((PropertyHolder.getInstance().isDisplayNavigationInstructionsOnMap() ? View.VISIBLE : View.INVISIBLE));
//                        instructionsViewer.updateBubble(0);
//                        INavInstruction inavInst = InstructionBuilder.getInstance().getNextNavInstruction(instruction, true);
//                        notifyOnNavigationInstructionChanged(inavInst);
//                    }
//                }
//
//                facilityMap.invalidate();
//                postDestination = false;
//
//
//            } else {
//                navigationState = false;
//                PropertyHolder.getInstance().setNavigationState(navigationState);
//                LocationCorrector.getInstance().setNavigationState(false, null);
//            }
        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);

        }

        Log.getInstance().debug(TAG, "Exit, setNavigationPath()");

    }


    private void updateNavigationPathWithoutGis() {

        Log.getInstance().debug(TAG, "Enter updateNavigationPathWithoutGis()");

        try {

//            if (!PropertyHolder.getInstance().isNavigationState()) {
//                return;
//            }
//
//            PointF myLoc = aStarData.getInstance().getMyLocation();
//
//            Location destPoi = aStarData.getInstance().getPoilocation();
//            PointF destLoc = new PointF((float) destPoi.getX(), (float) destPoi.getY());
//
//            if (myLoc == null && PropertyHolder.getInstance().isSdkObserverMode()) {
//                // set my location to entrance
//                PoiData epoi = PoiDataHelper.getInstance().getExitPoi();
//                if (epoi != null) {
//                    myLoc = epoi.getPoint();
//                }
//            }
//
//
//            if (destLoc != null && myLoc != null) {
//
//                // no Gis found
//                if (!GisData.getInstance().hasGis()) {
//
//                    FacilityConf FacilityConf = FacilityContainer.getInstance().getSelected();
//                    int currentz = FacilityConf.getSelectedFloor();
//                    NavigationPath navigation = aStarData.getInstance().getCurrentPath();
//                    if (navigation != null) {
//                        List<GisSegment> lineslist = navigation
//                                .getPathByZ(currentz);
//                        if (lineslist != null && !lineslist.isEmpty()) {
//                            GisSegment line = lineslist.get(0);
//                            if (line != null) {
//                                line.getLine().point1.setX(myLoc.x);
//                                line.getLine().point1.setY(myLoc.y);
//                                double weight = line.calcweight();
//                                line.setWeight(weight);
//                            }
//
//                        }
//                        List<Drawable> sprites = PathConvertor.convertPath(
//                                navigation, this.getContext(), currentz);
//                        LayerObject path = facilityMap.getLayers().get(
//                                "path");
//                        path.clearSprites();
//                        path.addAll(sprites);
//                        path.show();
//                    }
//
//                    navigationState = true;
//                    PropertyHolder.getInstance().setNavigationState(navigationState);
//
//                    LocationLocator.getInstance().setNavigationState(navigationState, null);
//                    facilityMap.invalidate();
//                }
//            } else {
//                navigationState = false;
//                PropertyHolder.getInstance().setNavigationState(navigationState);
//                LocationCorrector.getInstance().setNavigationState(false, null);
//            }


        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);

        }

        Log.getInstance().debug(TAG, "Exit, setNavigationPathWithoutGis()");

    }

    private void updateInstruction(Instruction instruction, PointF p) {

        FacilityConf FacilityConf = FacilityContainer.getInstance().getSelected();

        InstructionBuilder.getInstance().setNextInstruction(instruction);
        int z = FacilityConf.getSelectedFloor();
        GisPoint currentpoint = new GisPoint(p.x, p.y, z);
        GisPoint turnpoint = new GisPoint(instruction.getLocation().getX(), instruction.getLocation().getY(), instruction.getLocation().getZ());
        double dfromturn = aStarMath.findDistance(currentpoint, turnpoint) / FacilityConf.getPixelsToMeter();

        // instructionsViewer.init();
        instructionsViewer.setVisibility((PropertyHolder.getInstance().isDisplayNavigationInstructionsOnMap() ? View.VISIBLE : View.INVISIBLE));
        instructionsViewer.updateBubble(dfromturn);
        // mPlanView.updateBubble(dfromturn);
        INavInstruction inavInst = InstructionBuilder.getInstance().getNextNavInstruction(instruction, true);
        notifyOnNavigationInstructionChanged(inavInst);


        float itreshold = PropertyHolder.getInstance().getInstructionsDistance();
        float pretreshold = PropertyHolder.getInstance().getPreInsructionDistance();
        float pretresholdminimum = pretreshold / 2;
        Instruction inst = InstructionBuilder.getInstance().findCloseMergedInstruction(p);

        if (inst != null) {
            if ((dfromturn < itreshold) && !inst.hasPlayed()) {
                inst.setPlayed(true);
                if (!PropertyHolder.getInstance().isNavigationInstructionsSoundMute()) {
                    SoundPlayer.getInstance().play(inst.getSound());
                }
                inavInst = InstructionBuilder.getInstance().getNextNavInstruction(instruction);
                notifyOnNavigationInstructionRangeEntered(inavInst);

            } else if (dfromturn < pretreshold && dfromturn > pretresholdminimum && !inst.hasPreInstructionPlayed() && !inst.hasPlayed()) {
                Instruction firstinstruction = inst.getInstructions().get(0);
                if (firstinstruction != null && !firstinstruction.getSound().isEmpty()) {
                    if (firstinstruction.getType() == Instruction.TYPE_SWITCH_FLOOR) {
                        // List<String> playlist = new ArrayList<String>();
                        // playlist.add("soon");
                        // playlist.addAll(firstinstruction.getSound());
                        // SoundPlayer.getInstance().play(playlist);
                        // inst.setPreInstructionPlayed(true);
                    } else if (firstinstruction.getType() == Instruction.TYPE_DESTINATION) {
                        // SoundPlayer.getInstance().play(firstinstruction.getSound());
                        // inst.setPreInstructionPlayed(true);
                    } else if (firstinstruction.getType() != Instruction.TYPE_DESTINATION && firstinstruction.getType() != Instruction.TYPE_STRAIGHT) {
                        List<String> playlist = new ArrayList<String>();
                        playlist.add("next_turn");
                        playlist.add(firstinstruction.getSound().get(0));

                        if (!PropertyHolder.getInstance().isNavigationInstructionsSoundMute()) {
                            SoundPlayer.getInstance().play(playlist);
                        }
                        inavInst = InstructionBuilder.getInstance().getNextNavInstruction(instruction);
                        notifyOnNavigationInstructionRangeEntered(inavInst);

                        inst.setPreInstructionPlayed(true);
                    }
                }

            }
        }

    }

    private void checkStopNavigation(PointF p) {
        if (PropertyHolder.getInstance().isStopNavigatinState()) {
            List<Instruction> instructionlist = InstructionBuilder.getInstance().getCurrentInstructions();
            Instruction lastinstruction = instructionlist.get(instructionlist.size() - 1);
            float destinationx = (float) lastinstruction.getLocation().getX();
            float destinationy = (float) lastinstruction.getLocation().getY();
            PointF destinationp = new PointF(destinationx, destinationy);
            FacilityConf FacilityConf = FacilityContainer.getInstance().getSelected();
            double dfromdestination = MathUtils.distance(p, destinationp) / FacilityConf.getPixelsToMeter();
            if (dfromdestination < 0.5) {
                LocationLocator.getInstance().resetNavState();
                postDestination = true;
                // XXX clear navins
                // deletedestination();
            }
            if (postDestination && dfromdestination > 10) {
                navigationState = false;
                PropertyHolder.getInstance().setNavigationState(navigationState);
                // XXX
                // deletedestination();
            }

        }

    }

    public void pauseNavigation() {
        LocationLocator.getInstance().resetNavState();

        // PropertyHolder.getInstance().setNavigationState(navigationState);

        LayerObject path = facilityMap.getLayers().get("path");
        path.clearSprites();
        LayerObject instructionslayer = facilityMap.getLayers().get("instructions");
        instructionslayer.clearSprites();
        // SoundPlayer.getInstance().reset();
        instructionsViewer.clearInbstructionsBubbleText();

        aStarData.getInstance().setCurrentPath(null);
        aStarData.getInstance().setPoilocation(null);
        aStarData.getInstance().setDestination(null);
        aStarData.getInstance().cleanAStar();
        InstructionBuilder.getInstance().clear();
        // stopCameraNavigation();
        aStarData.getInstance().setInternalDestination(null);
        facilityMap.invalidate();
        destinationCounter = 0;
        List<IPoi> nextpois = aStarData.getInstance().getMultiNavigationPois();
        if (nextpois != null) {
            IPoi arrivedToPoi = nextpois.get(0);
            if (!nextpois.isEmpty()) {
                nextpois.remove(0);
            }
            notifyOnNavigationArriveToPoi(arrivedToPoi, nextpois);
        }
        notifyOnNavigationStateChanged(NavigationState.PENDING);
    }

    public void stopNavigation() {
        LocationLocator.getInstance().resetNavState();

        navigationState = false;
        PropertyHolder.getInstance().setNavigationState(navigationState);

        LayerObject path = facilityMap.getLayers().get("path");
        path.clearSprites();
        LayerObject instructionslayer = facilityMap.getLayers().get("instructions");
        instructionslayer.clearSprites();
        // SoundPlayer.getInstance().reset();
        instructionsViewer.clearInbstructionsBubbleText();

        aStarData.getInstance().setCurrentPath(null);
        aStarData.getInstance().setPoilocation(null);
        aStarData.getInstance().setDestination(null);
        aStarData.getInstance().cleanAStar();
        InstructionBuilder.getInstance().clear();
        // stopCameraNavigation();
        aStarData.getInstance().setInternalDestination(null);
        aStarData.getInstance().setMultiNavigationPois(null);
        facilityMap.invalidate();
        destinationCounter = 0;
        notifyOnNavigationStateChanged(NavigationState.STOPED);
    }

    private boolean checkNavDestinationReached(Location destLoc, PointF currentLocation) {

        if (destLoc == null || currentLocation == null) {
            return false;
        }

        FacilityConf facilityConf = FacilityContainer.getInstance().getSelected();

        if (destLoc.getZ() != facilityConf.getSelectedFloor())
            return false;

        PointF destPoint = new PointF();
        destPoint.x = (float) destLoc.getX();
        destPoint.y = (float) destLoc.getY();

        PointF ProjectedDestLocation = GisData.getInstance().findClosestPointOnLine(destPoint);
        PointF tempCurrLoc = new PointF(currentLocation.x, currentLocation.y);

        float rangeInPixels = PropertyHolder.getInstance().getChkDestReachRectRangeWidthMeters() * facilityConf.getPixelsToMeter();
        float destLeft = ProjectedDestLocation.x - rangeInPixels;
        float destRight = ProjectedDestLocation.x + rangeInPixels;
        float destTop = ProjectedDestLocation.y - rangeInPixels;
        float destBottom = ProjectedDestLocation.y + rangeInPixels;

        RectF destinationArea = new RectF();
        destinationArea.set(destLeft, destTop, destRight, destBottom);

        if (PropertyHolder.getInstance().isDevelopmentMode()) {
            RectSprite rect = new RectSprite(destinationArea);
            facilityMap.addPOI(rect);
            facilityMap.invalidate();
        }

        // GisLine cline = GisData.getInstance().findGisLine(currentLocation);
        // float degrees = getLIneAngle(cline);

        if (destinationArea.contains(tempCurrLoc.x, tempCurrLoc.y)) {
            destinationCounter++;
            if (PropertyHolder.getInstance().isDevelopmentMode()) {
                Toast.makeText(ctx, "INSIDE DEST", Toast.LENGTH_SHORT).show();
            }
        } else {
            destinationCounter = 0;
        }

        if (destinationCounter >= 1) {
            Instruction instruction = NavigationUtil.playDestinationSound();
            INavInstruction inavInst = InstructionBuilder.getInstance().getNextNavInstruction(instruction);
            notifyOnNavigationInstructionRangeEntered(inavInst);
            notifyOnNavigationStateChanged(NavigationState.DESTINATION_REACHED);
            List<IPoi> nextpois = aStarData.getInstance().getMultiNavigationPois();
            if (nextpois != null && nextpois.size() > 1) {
                pauseNavigation();
            } else {
                stopNavigation();
            }

        }

        return false;
    }

    private void recalculatepath(PointF p) {
        int state = -1;
        // if (pathPlayingfile.equals("none")) {
        PointF pointonpath = aStarData.getInstance().getCurrentPath().getClosestPointOnPath(p);

        FacilityConf facilityConf = FacilityContainer.getInstance().getSelected();

        if (facilityConf == null) {
            return;
        }

        double rdistance = MathUtils.distance(p, pointonpath) / facilityConf.getPixelsToMeter();
        // Turn back instruction
        // Should be programmed for settings:
        if (rdistance > facilityConf.getDistanceFromNavPath()) {
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
            PathCalculator.calculatePath();
            NavigationPath nav = aStarData.getInstance().getCurrentPath();

            if (nav != null) {
                InstructionBuilder.getInstance().getInstractions(nav);

            }
            facilityMap.setNavigationPath();
//			float myangle = OrientationMonitor.getInstance().getAzimuth();
//			NavigationPath navpath = aStarData.getInstance().getCurrentPath();
//			int z = facilityConf.getSelectedFloor();
//			if (navpath != null && navpath.getPathByZ(z) != null && navpath.getPathByZ(z).size() > 0) {
//				GisSegment s = navpath.getPathByZ(z).get(0);
//				double lineangle = getAngle(s);
//				lineangle += FacilityContainer.getInstance().getSelected().getFloorRotation();
//				double realangle = Math.abs(lineangle - myangle);
//				if (realangle > 150 && realangle < 210) {
//					ArrayList<String> sounds = new ArrayList<String>();
//					sounds.add("turnback");
//					SoundPlayer.getInstance().play(sounds);
//					GisPoint pt2 = new GisPoint(s.getLine().getPoint2().getX(), s.getLine().getPoint2().getY(), s.getLine().getPoint2().getZ());
//					GisPoint pt1 = new GisPoint(s.getLine().getPoint1().getX(), s.getLine().getPoint1().getY(), s.getLine().getPoint1().getZ());
//
//					GisLine l = new GisLine(pt2, pt1, s.getLine().getZ());
//					GisSegment stemp = new GisSegment();
//					stemp.setLine(l);
//					double imageangle = getAngle(stemp);
//					facilityMap.drawTurnBack(p, imageangle);
//					state = 2;
//				}
//			}

            facilityMap.invalidate();
        }

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

        // }

    }

    public int getPresentedFloorId() {

        FacilityConf facilityConf = FacilityContainer.getInstance().getSelected();
        return facilityConf.getSelectedFloor();
    }

    public void resetAnimations() {
        AnimationsHolder.getInstance().reset();
        AnimationsHolder.releaseInstance();
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
            if (floor != -1) {
                try {
                    listener.mapDidChangeFloor(floor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (facilityMap != null) {
            facilityMap.setListener(listener);
        }
    }

    public void unregisterListener(SpreoMapViewListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public void showEntranceFloor() {
        try {

            FacilityConf facilityConf = FacilityContainer.getInstance().getSelected();
            int EntranceFloor = 0;
            PoiData exitpoi = PoiDataHelper.getInstance().getExitPoi();
            if (exitpoi != null) {
                EntranceFloor = (int) exitpoi.getZ();
            }
            facilityMap.hideTarget();
            facilityMap.hideMyLocation();
            floor = EntranceFloor;
            facilityMap.setFloorNumber(EntranceFloor);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            PointF center = new PointF(facilityMap.getDrawable().getBounds().exactCenterX(), facilityMap.getDrawable().getBounds().exactCenterY());
                            ;
                            facilityMap.SetCenter(center);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                };

                @Override
                public void run() {
                    post(r);
                }
            }, 500);

        } catch (Throwable e) {
            e.printStackTrace();
        }

        drawMultiPoiRoute();

    }


    public void showFloorWithId(int floorId) {
        showFloorWithId(floorId, true);
    }

    public void showFloorWithId(int floorId, boolean showcenter) {

        FacilityConf facilityConf = FacilityContainer.getInstance().getSelected();
        List<FloorData> floors = facilityConf.getFloorDataList();
        String selectedid = facilityConf.getId();
        if (floorId >= 0 && floorId <= floors.size() - 1) {
            ILocation currentLocation = LocationFinder.getInstance().getCurrentLocation();
            if (currentLocation != null && currentLocation.getFacilityId() != null && selectedid.equals(currentLocation.getFacilityId()) && currentLocation.getZ() == floorId) {
                PropertyHolder.getInstance().setSdkObserverMode(false);
                facilityMap.showMyLocation();
            } else {
                PropertyHolder.getInstance().setSdkObserverMode(true);
                facilityMap.hideTarget();
                facilityMap.hideMyLocation();
                floor = floorId;

            }
            facilityMap.setFloorNumber(floorId);
            resetMapRotation();
            resetAnimations();

            if (showcenter) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            PointF center = new PointF(facilityMap.getDrawable().getBounds().exactCenterX(), facilityMap.getDrawable().getBounds().exactCenterY());
                            ;
                            facilityMap.SetCenter(center);
                        }

                    };

                    @Override
                    public void run() {
                        post(r);
                    }
                }, 500);
            }
            facilityMap.setNavigationPath();
            drawMultiPoiRoute();
        }
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void returnToMyLocation() {
        PropertyHolder.getInstance().setSdkObserverMode(false);
        facilityMap.showMyLocation();
    }

    public void showPoi(IPoi poi) {
        if (poi != null) {
            ILocation loc = poi.getLocation();
            if (loc != null) {
                presentLocation(loc);
                openPoiBubble(poi);
            }
        }
    }

    public void showPoi(IPoi poi, String bubbletext) {
        if (poi != null) {
            ILocation loc = poi.getLocation();
            if (loc != null) {
                presentLocation(loc);
                openPoiBubble(poi, bubbletext);
            }
        }
    }

    public void presentLocation(ILocation location) {
        facilityMap.hideMyLocation();
        int locz = (int) location.getZ();
        float locx = (float) location.getX();
        float locy = (float) location.getY();
        final PointF locpoint = new PointF(locx, locy);
        if (locz != floor) {
            facilityMap.setFloorNumber(locz);
            floor = locz;
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    facilityMap.SetCenter(locpoint);
                }

            };

            @Override
            public void run() {
                post(r);
            }
        }, 500);
    }

    public void showMyLocation() {
        facilityMap.showMyLocation();
        // if (PropertyHolder.getInstance().isRotatingMap()) {
        if (PropertyHolder.getInstance().getRotatingMapType() == MapRotationType.COMPASS) {
            facilityMap.setCompassMode(true, true);
        }

        AnimationsHolder.getInstance().reset();
        AnimationsHolder.releaseInstance();

        facilityMap.setFollowMeMode(true);
        if (myLocation != null) {
            PointF location = new PointF((float) myLocation.getX(), (float) myLocation.getY());
            facilityMap.SetCenter(location);
        }

    }

    public void mapZoomIn() {
        facilityMap.setZoom(1.2f);
        facilityMap.invalidate();
    }

    public void mapZoomOut() {
        facilityMap.setZoom(0.8f);
        facilityMap.invalidate();
    }

    // public PointF getMapCenter() {
    // PointF result = null;
    // if (facilityMap != null) {
    // // result = facilityMap.getCenter();
    // result = new PointF(facilityMap.getDrawable().getBounds().exactCenterX(), facilityMap.getDrawable().getBounds().exactCenterY());
    // }
    // return result;
    // }

    public void recoverMapStae(MapState mstate) {
        final Matrix mmatrix = mstate.getMapMatrix();
        final float zoom = mstate.getMapZoom();
        int floorid = mstate.getMapFloor();
        showFloorWithId(floorid, false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    facilityMap.recoverState(mmatrix, zoom);
                }

            };

            @Override
            public void run() {
                post(r);
            }
        }, 200);
    }

    public Matrix getMapMatrix() {
        Matrix result = null;
        if (facilityMap != null) {
            result = facilityMap.getImageMatrix();
        }
        return result;
    }

    public float getMapZoom() {
        float result = -100;
        if (facilityMap != null) {
            result = facilityMap.getSaveScale();
        }
        return result;
    }

    public void setUserIcon(Bitmap userBitmap) {
        if (facilityMap != null && userBitmap != null) {
            facilityMap.setMyLocationBmp(userBitmap);
        }

    }

    public boolean isNavigationState() {
        return navigationState;
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
        // if(navigationState!=NavigationState.NAVIGATE && navigationState!=NavigationState.IDLE && navigationState!=NavigationState.SILENT_REROUTE){
        // Toast.makeText(this.getContext(), navigationState.toString(), Toast.LENGTH_LONG).show();
        // }
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
        // Toast.makeText(this.getContext(), instruction.getText(), Toast.LENGTH_LONG).show();
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
        if (facilityMap != null) {
            facilityMap.reDrawPois();
        }
    }

    public void openPoiBubble(IPoi poi) {
        if (facilityMap != null) {

            facilityMap.createUserPoiBubble(poi, null);
            facilityMap.invalidate();
        }
    }

    public void openPoiBubble(IPoi poi, String customtext) {
        if (facilityMap != null) {

            facilityMap.createUserPoiBubble(poi, customtext);
            facilityMap.invalidate();
        }
    }

    public void resetMapRotation() {
        if (facilityMap != null) {
            facilityMap.setImageRotation(-FacilityContainer.getInstance().getSelected().getFloorRotation());
            facilityMap.setImageOffsetRotation(FacilityContainer.getInstance().getSelected().getFloorRotation());
        }
    }

    public void drawMultiPoiRoute() {
        List<IPoi> poilist = PropertyHolder.getInstance().getMultiPoiList();
        List<NavigationPath> paths = PropertyHolder.getInstance().getMultiPoiNavRoute();
        List<Drawable> sprites = new ArrayList<Drawable>();
        int z = FacilityContainer.getInstance().getSelected().getSelectedFloor();
        boolean includeEntrances = PropertyHolder.getInstance().isIncludeEntrancesInPoisNumbering();
        boolean includeSwitchFloors = PropertyHolder.getInstance().isIncludeSwitchFloorsInPoisNumbering();
        List<GisLine> gislines = GisData.getInstance().getLines(FacilityContainer.getInstance().getSelected().getId(), z);

        if (paths != null && !paths.isEmpty()) {
            for (NavigationPath o : paths) {
                List<Drawable> tmpsprites = new ArrayList<Drawable>();
                tmpsprites = PathConvertor.convertMultiPoiPath(o, this.getContext(), z);
                sprites.addAll(tmpsprites);
            }
        }

        if (PropertyHolder.getInstance().isMulitPoisDrawSpritesState()) {
            if (poilist != null && !poilist.isEmpty()) {
                List<IPoi> filterdpoilist = new ArrayList<IPoi>();
                for (IPoi o : poilist) {
                    if (o.getPoiNavigationType().equals("internal") && (includeEntrances || !o.getPoiID().contains("idr"))
                            && (includeSwitchFloors || !o.getPoiID().startsWith(OrederPoisUtil.switchIdPrefix))) {
                        filterdpoilist.add(o);
                    }
                }

                for (IPoi p : filterdpoilist) {
                    Boolean isVeseted = false;
                    if (p == null)
                        continue;
                    if (p.getZ() == z) {
                        int number = filterdpoilist.indexOf(p) + 1;
                        PointF point = GisData.getInstance().findClosestPointOnLine(gislines, p.getPoint());
                        if (PropertyHolder.getInstance().getVisitedPoiList() != null) {
                            for (IPoi iPoi : PropertyHolder.getInstance().getVisitedPoiList()) {
                                if (p.getPoiID().equals(iPoi.getPoiID())) {
                                    isVeseted = true;
                                    break;
                                }
                            }
                        }

                        Drawable poisprite = new MultiPoiListSprite(point.x, point.y, number, isVeseted);
                        sprites.add(poisprite);
                    }
                }
            }
        }

        LayerObject path = facilityMap.getLayers().get("multipath");
        path.clearSprites();
        facilityMap.invalidate();
        path.addAll(sprites);
        path.show();
    }

    public void removeMultiPoiRoute() {
        LayerObject path = facilityMap.getLayers().get("multipath");
        path.clearSprites();
        facilityMap.invalidate();
    }

    public void closeBubble(IPoi poi) {
        if (facilityMap != null) {
            facilityMap.removeBubble(poi);
            facilityMap.invalidate();
        }
    }

    public void closeAllPoiBubbules() {
        if (facilityMap != null) {
            facilityMap.removeAllBubbles();
        }
    }


    public void setCurrentLocationAsParking() {
        ILocation loc = LocationFinder.getInstance().getCurrentLocation();
        if (loc != null) {
            // LatLng parkingloc = new LatLng(myLocation.getLat(),
            // myLocation.getLon());
            Location parking = new Location(loc);
            updateParkingMarker(loc);
            ParkingUtil.getInstance().save(parking);
        }

    }

    private void updateParkingMarker(final ILocation parkingloc) {
        if (parkingloc != null && parkingloc.getLocationType() == LocationMode.INDOOR_MODE) {

            Runnable r = new Runnable() {

                @Override
                public void run() {
                    try {
                        Bitmap parkingbm = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.parking);
                        IconSprite ics = new IconSprite(parkingbm);
                        LayerObject layer = facilityMap.getLayers().get("parking");
                        layer.clearSprites();
                        facilityMap.invalidate();
                        float destX = (float) parkingloc.getX();
                        float destY = (float) parkingloc.getY();
                        ics.setLoc(new PointF(destX, destY));
                        layer.addSprite(ics);
                        layer.show();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }

                }
            };

            post(r);

        }

    }

    public void removeParkingLocation() {
        LayerObject layer = facilityMap.getLayers().get("parking");
        layer.clearSprites();
        invalidate();
    }


    /**
     * display or hide pois on map
     *
     * @param display true for display pois, false for hiding pois
     */
    public void displayPois(boolean display) {
        if (facilityMap != null) {
            facilityMap.displayPois(display);
        }
    }


    public void hideLocationMark() {
        facilityMap.hideMyLocation();
        facilityMap.setFollowMeMode(false);
        facilityMap.invalidate();
    }

    public void showLocationMark(ILocation location) {
        double locz = location.getZ();
        if (locz == floor) {
//			PropertyHolder.getInstance().setSdkObserverMode(false);
            facilityMap.showMyLocation();
            facilityMap.invalidate();
        }
    }

    // // //=== camera nav ====
    // private void initCameraNavigation(){
    // try{
    // mCameraNavAngle=0;
    // isCameraNavState=true;
    //
    // // if(cameraNavStateButton!=null){
    // // cameraNavStateButton.setImageDrawable(getResources().getDrawable(R.drawable.camera_stop_nav_state));
    // // }
    //
    //
    //
    // mCameraNavHandler=new Handler();
    // mCameraNavHandler.removeCallbacks(mUpdateCameraNavClockTask);
    // mCameraNavHandler.postDelayed(mUpdateCameraNavClockTask, 100);
    //
    // // hide zoom +/- buttons of the image view when camera nav is on
    // //zoomMinusImageButton.setVisibility(View.INVISIBLE);
    // //zoomPlusImageButton.setVisibility(View.INVISIBLE);
    //
    // mCameraNavGLView = new GLSurfaceView(ctx);
    // mCameraNavGLView.setZOrderMediaOverlay(true);
    // mCameraNavGLView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
    // mCameraNavGLView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    // mCameraNavDirection=new direction();
    // mCameraNavGLView.setRenderer(mCameraNavDirection);
    // mCameraNavGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    // mCameraNavPreview = new CameraPreview(ctx);
    // //mCameraNavPreview.setAlpha(0.2f);
    // cameraNavLayout=(FrameLayout) findViewById(R.id.cameraView);
    //
    // mCameraNavDirection.setTranslation(0f, 0f, -1.7f);
    //
    // cameraNavLayout.addView(mCameraNavPreview);
    // cameraNavLayout.addView(mCameraNavGLView);
    //
    // cameraNavLayout.setVisibility(View.VISIBLE);
    //
    // cameraNavLayout.setOnClickListener(new OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // //do nothing
    // System.out.println("no");
    // }
    // });
    //
    // }
    // catch(Throwable e){
    // e.printStackTrace();
    // }
    // }
    //
    // private void stopCameraNavigation(){
    // try{
    //
    // isCameraNavState=false;
    // mCameraNavAngle=0;
    //
    // // if(cameraNavStateButton!=null){
    // // cameraNavStateButton.setImageDrawable(getResources().getDrawable(R.drawable.camera_nav_state));
    // // }
    //
    // if(mCameraNavHandler!=null){
    // mCameraNavHandler.removeCallbacks(mUpdateCameraNavClockTask);
    // mCameraNavHandler=null;
    // }
    // // show zoom +/- buttons of the image view when camera nav is off
    // //zoomMinusImageButton.setVisibility(View.VISIBLE);
    // //zoomPlusImageButton.setVisibility(View.VISIBLE);
    //
    // if(cameraNavLayout!=null){
    // cameraNavLayout.removeAllViews();
    // cameraNavLayout.setVisibility(View.INVISIBLE);
    //
    // }
    //
    // if(mCameraNavGLView!=null){
    // mCameraNavGLView.destroyDrawingCache();
    // mCameraNavGLView.clearAnimation();
    // }
    //
    // if(mCameraNavPreview!=null){
    // mCameraNavPreview.destroyDrawingCache();
    // mCameraNavPreview.clearAnimation();
    //
    // }
    //
    // mCameraNavPreview=null;
    // cameraNavLayout=null;
    // mCameraNavDirection=null;
    //
    //
    //
    // }
    // catch(Throwable t)
    // {
    // t.printStackTrace();
    // }
    // }
    //
    //
    // private float getInstructionAngleForCameraNav(PointF p){
    //
    // Instruction instruction=InstructionBuilder.getInstance().getNextInstruction();
    //
    // if(instruction==null)
    // return 0;
    //
    // float angle=aStarMath.getSegmentAngle(instruction.getSegment())
    // + FacilityContainer.getInstance().getCurrent().getFloorRotation();
    //
    // if (instruction.getType()== Instruction.TYPE_SWITCH_FLOOR ||
    // instruction.getType() == Instruction.TYPE_DESTINATION) {
    // return angle;
    // }
    //
    // //double turnIsNear = PropertyHolder.getInstance().getmTurnLogicDistance();
    // double turnIsNear = PropertyHolder.getInstance().getInstructionsDistance();
    // float p2m = FacilityContainer.getInstance().getCurrent().getPixelsToMeter();
    // int fl = FacilityContainer.getInstance().getCurrent().getSelectedFloor();
    // GisPoint currentpoint = new GisPoint(p.x, p.y, fl);
    // GisPoint turnpoint = new GisPoint(instruction.getLocation().getX(),
    // instruction.getLocation().getY(), instruction.getLocation().getZ());
    //
    // double distfromturn = aStarMath.findDistance(currentpoint, turnpoint)/ p2m;
    //
    // if (distfromturn < turnIsNear) {
    // NavigationPath path = aStarData.getInstance().getCurrentPath();
    // GisSegment segment = path.getClosestSegment(p);
    // GisSegment nextSegment = path.getNext(segment);
    // if (nextSegment != null) {
    // angle = aStarMath.getSegmentAngle(nextSegment)+ FacilityContainer.getInstance().getCurrent().getFloorRotation();;
    // }
    // }
    //
    // return angle;
    // }
    //
    //
    //
    // private void updateCameraNavigation(float angle, int instructionType) {
    // try{
    // if(isCameraNavState){
    // if(cameraNavLayout!=null){
    // cameraNavLayout.setVisibility(View.VISIBLE);
    // }
    //
    // // float angle=aStarMath.getSegmentAngle(instruction.getSegment())
    // // + PropertyHolder.getInstance().getMapRotation();
    //
    // if (instructionType== Instruction.TYPE_SWITCH_FLOOR) {
    // mCameraNavDirection.setTexture(direction.TEX_INDEX_ELEVATOR);
    // } else if (instructionType == Instruction.TYPE_DESTINATION) {
    // mCameraNavDirection.setTexture(direction.TEX_INDEX_DESTINATION);
    // }
    // else{
    // mCameraNavDirection.setTexture(direction.TEX_INDEX_ARROW);
    // }
    //
    // mCameraNavDirection.rotate(angle - mCameraNavAngle, 0, 0, 1);
    //
    //
    // mCameraNavAngle = angle;
    // }
    // }
    // catch(Throwable t){
    // t.printStackTrace();
    // }
    //
    // }
    //
    // private Runnable mUpdateCameraNavClockTask = new Runnable() {
    // public void run() {
    //
    // if(isCameraNavState && navigationState){
    // // set angle on ui
    // float ang = mCameraNavAngle
    // + PropertyHolder.getInstance().getArOffset()
    // - OrientationMonitor.getInstance().getAzimuth();
    // if(mCameraNavDirection!=null && mCameraNavGLView!=null){
    // mCameraNavDirection.setRotation(ang, 0, 0, 1);
    // mCameraNavGLView.requestRender();
    // }
    // if(mCameraNavHandler!=null){
    // mCameraNavHandler.postDelayed(mUpdateCameraNavClockTask, 300);
    // }
    // }
    //
    //
    // }
    // };
    //
    // private OnClickListener cameraNavStateListener= new OnClickListener() {
    // @Override
    // public void onClick(View v) {
    //
    // if(navigationState){
    //
    // if(isCameraNavState){
    // stopCameraNavigation();
    // }
    // else
    // {
    // stopCameraNavigation();
    // initCameraNavigation();
    // }
    // }
    //
    //
    // }
    // };
    // //=== camera nav ====
}
