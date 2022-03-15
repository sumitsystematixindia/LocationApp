package com.mlins.locator;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.widget.Toast;

import com.mlins.aStar.NavigationPath;
import com.mlins.ndk.wrappers.FLocation;
import com.mlins.ndk.wrappers.NdkLocationFinder;
import com.mlins.orientation.OrientationMonitor;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.FloorData;
import com.mlins.utils.Lookup;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.GisData;
import com.mlins.utils.gis.GisPoint;
import com.mlins.wireless.WlBlip;
import com.spreo.geofence.GeoFenceHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class LocationLocator {
    public static final String UNKNOWN_GEOFENCE_GROUP_TAG = "UNKNOWN";
    private static final LOCATORS LOCATOR = LOCATORS.MATRIX;

    private static final int QUEUE_LENGTH = 5;

    // private static final double FIX_THRESHOLD = 0.6;

    // private static final int HITS_REQUIRED = 3;

    // private static final double FIX_RATIO = 0.4;
    private static final int DETECT_THRESHOLD = 2;

    LoadMatrixTask loadMatrixTask = null;
    private PointF currentLock = null; //new PointF((float) 394.15384, (float) 74.84653);
    private Map<String, Queue<WlBlip>> mSpots = new HashMap<String, Queue<WlBlip>>();
    private PointF lastAverage = null;
    private String lastDetectedGeofenceGroupId = "aisle1";//UNKNOWN_GEOFENCE_GROUP_TAG;
    // private int hitCount;
    private int detectionCount = 0;
    private Comparator<WlBlip> filterComp = new Comparator<WlBlip>() {

        @Override
        public int compare(WlBlip blip1, WlBlip blip2) {
            Integer l1 = blip1.level;
            Integer l2 = blip2.level;
            return l2.compareTo(l1);
        }
    };

    //not sure if it depends on project data, it's better to perform cleanup
    public static LocationLocator getInstance() {
        return Lookup.getInstance().get(LocationLocator.class);
    }

    public String getDetectedGeofenceId() {
        return lastDetectedGeofenceGroupId;
    }

    /**
     * Finds location inside a given geofence using groups method
     *
     * @param spots
     * @param angle
     * @param usendkfilter
     * @return
     */
    public PointF findLocationInsideGeofence(List<WlBlip> spots, float angle, boolean usendkfilter) {
        addScan(spots);

        NdkLocationFinder aml = NdkLocationFinder.getInstance();
        PointF avePt = new PointF();
        if (aml != null) {

            FLocation loc = new FLocation();
            WlBlip[] res = spots.toArray(new WlBlip[spots.size()]);


            String geofenceGroupId = aml.getGroupIdByBlips(res);
            //lastDetectedGeofenceGroupId = "aisle1";
            if (!geofenceGroupId.equals(UNKNOWN_GEOFENCE_GROUP_TAG)) {


                if (!lastDetectedGeofenceGroupId.equals(geofenceGroupId)) {
                    //debug
                    detectionCount++;
                    if (detectionCount >= DETECT_THRESHOLD) {
                        lastDetectedGeofenceGroupId = geofenceGroupId;
                        Toast.makeText(PropertyHolder.getInstance().getMlinsContext(), geofenceGroupId, Toast.LENGTH_SHORT).show();
                        detectionCount = 0;
                    }
                } else {
                    detectionCount = 0;
                }


            }

            // use the regular method on first time
//				if(lastDetectedGeofenceGroupId.equals(UNKNOWN_GEOFENCE_GROUP_TAG)){
//					return findLocation(spots, angle, usendkfilter);
//				}


            String type = "location";
            RectF grect = GeoFenceHelper.getInstance().getGeofenceRectById(lastDetectedGeofenceGroupId, type);


            if (grect == null) {
                return null;
            }


            float topLeftX = grect.left;
            float topLeftY = grect.top;
            float bottomRightX = grect.right;
            float bottomRightY = grect.bottom;
            aml.findLocationInsideGeofence(res, loc, topLeftX, topLeftY, bottomRightX, bottomRightY);


            avePt = new PointF(loc.getX(), loc.getY());
            if (currentLock == null) {
                currentLock = avePt;
            }

            LocationCorrector lcr = LocationCorrector.getInstance();
            if (lcr != null) {
                PointF dr = lcr.getDeadReckoning();
                //FIX THE LOCATION
                if (dr != null && avePt != null) {

                    float dx = avePt.x - dr.x;
                    float dy = avePt.y - dr.y;

                    float dist = (float) Math.sqrt(dx * dx + dy * dy);

                    PropertyHolder ph = PropertyHolder.getInstance();

                    FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

                    if (facConf != null) {

                        float actionDist = dist / facConf.getPixelsToMeter();

                        float locatorradius = facConf.getLocatorRadius();


                        if (!isUserIndirection(dr, avePt)) {
                            locatorradius *= 2f;

                        }

                        if (actionDist > locatorradius) {
                            float w = PropertyHolder.getInstance().getLocatorDeadReckoningWeight();

                            float wAvgLocX = w * dr.x + (1.0f - w) * avePt.x;

                            float wAvgLocY = w * dr.y + (1.0f - w) * avePt.y;

                            PointF wAvgLocation = new PointF(wAvgLocX, wAvgLocY);
                            lcr.setLocationPositive(wAvgLocation);

                        }

                    }
                }


            }
        }


        if (!LocationCorrector.getInstance().isInitialized()) {
            LocationCorrector.getInstance().setPosition(getCurrentLock());
        }
        PointF p = LocationCorrector.getInstance().correctLocation();
        if (p != null) {
            //XXX debug without dead reckoning
            setCurrentLock(avePt);
            // setCurrentLock(p);

        }
        if (getCurrentLock() == null) {
            setCurrentLock(new PointF());
        }
        return getCurrentLock();


    }

    public PointF findLocation(List<WlBlip> spots, float angle, boolean usendkfilter) {
        addScan(spots);
        //XXX NDK
//		AsociativeMemoryLocator aml = AsociativeMemoryLocator.getInstance();
        NdkLocationFinder aml = NdkLocationFinder.getInstance();
        if (aml != null) {
            //			aml.findClosestPoint(spots, angle);
            //			PointF avePt = aml.getAvePoint();
            //			if (currentLock == null) {
            //				currentLock = avePt;
            //			}
            //aml.findClosestPoint(spots);
            FLocation loc = new FLocation();

            spots = filterByRssiLevelAndTopK(spots);

            WlBlip[] res = spots.toArray(new WlBlip[spots.size()]);
            aml.findLocation(res, loc, usendkfilter);

            PointF avePt = null;

            if (loc.getX() == -1 || loc.getX() == -999) {
                avePt = getCurrentLock();
            } else {
                avePt = new PointF(loc.getX(), loc.getY());
            }

            //for the red dot
            lastAverage = avePt;

            if (currentLock == null) {
                currentLock = avePt;
            }

            LocationCorrector lcr = LocationCorrector.getInstance();
            if (lcr != null) {
                PointF dr = lcr.getDeadReckoning();
                //FIX THE LOCATION
                if (dr != null && avePt != null) {

                    float dx = avePt.x - dr.x;
                    float dy = avePt.y - dr.y;

                    float dist = (float) Math.sqrt(dx * dx + dy * dy);

                    PropertyHolder ph = PropertyHolder.getInstance();

                    FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

                    if (facConf != null) {

                        float actionDist = dist / facConf.getPixelsToMeter();

                        float locatorradius = facConf.getLocatorRadius();


                        FloorData floordata = facConf.getSelectedFloorData();
                        if (floordata != null && floordata.stickyRadius != -1) {
                            locatorradius = floordata.stickyRadius;
                        }

                        //						 if (PropertyHolder.getInstance().isInsideElevatorZone() && !PropertyHolder.getInstance().isLocationPlayer()) {
                        //							locatorradius = 5000;
                        //						 }

                        if (!isUserIndirection(dr, avePt)) {
                            locatorradius *= 2f;

                        }

                        if (actionDist > locatorradius) {
                            float w = PropertyHolder.getInstance().getLocatorDeadReckoningWeight();

                            if (locatorradius < 0.1) {
                                w = 0;
                            }

                            float wAvgLocX = w * dr.x + (1.0f - w) * avePt.x;

                            float wAvgLocY = w * dr.y + (1.0f - w) * avePt.y;

                            PointF wAvgLocation = new PointF(wAvgLocX, wAvgLocY);
                            lcr.setLocationPositive(wAvgLocation);

                        }

                        //						float t = ph.getDistanceTresh() * ph.getPixelsToMeter();
                        //						float r = ph.getRatioTresh();
                        //						r = Math.max(-1, Math.min(r, 1)); // clamp 'r' to range {-1
                        //															// -> 1}.
                        //						if (dx * dx + dy * dy > t * t) {
                        //							LocationCorrector.getInstance().setLocationPositive(
                        //									new PointF(dr.x + dx * r, dr.y + dy * r));
                        //						}
                    }
                }

//					else {
//						lcr.setLocationPositive(avePt);
//						
//					}
            }
        }

        // AsociativeMemoryLocator aml = AsociativeMemoryLocator.getInstance();
        // AssociativeDataSorter lastBestMatch = aml.getBestMatch();
        // setCurrentLock(aml.findClosestPoint(spots));
        // AssociativeDataSorter bestMatch = aml.getBestMatch();
        // AssociativeDataSorter secondMatch = aml.getSecondMatch();
        // boolean ratioOK = false;
        // boolean thresholdOK = false;
        // if (bestMatch != null || secondMatch != null)
        // {
        // ratioOK = bestMatch != null && secondMatch != null
        // && bestMatch.getD() / secondMatch.getD() <
        // PropertyHolder.getInstance().getRatioTresh();
        // thresholdOK = bestMatch.getD() <
        // PropertyHolder.getInstance().getDistanceTresh()
        // && (lastBestMatch == null // normally on first invocation.
        // || (lastBestMatch.getD() <
        // PropertyHolder.getInstance().getDistanceTresh() &&
        // bestMatch.data.point
        // .equals(lastBestMatch.data.point)));
        // }
        //
        //
        // if (ratioOK || thresholdOK) {
        // hitCount++;
        // if (hitCount >= HITS_REQUIRED) {
        // LocationCorrector.getInstance().setLocationPositive(
        // new PointF(bestMatch.data.point.x,bestMatch.data.point.y));
        // }
        // } else {
        // hitCount = 0;
        // }

        if (!LocationCorrector.getInstance().isInitialized()) {
            LocationCorrector.getInstance().setPosition(getCurrentLock());
        }
        PointF p = LocationCorrector.getInstance().correctLocation();
        if (p != null) {
            setCurrentLock(p);
        }
        if (getCurrentLock() == null) {
            setCurrentLock(new PointF());
        }
        return getCurrentLock();


    }

    public PointF findUnprojectedLocation(List<WlBlip> spots, float angle, boolean usendkfilter) {
        addScan(spots);
        //XXX NDK
        NdkLocationFinder aml = NdkLocationFinder.getInstance();
        if (aml != null) {

            FLocation loc = new FLocation();
            spots = filterByRssiLevelAndTopK(spots);

            WlBlip[] res = spots.toArray(new WlBlip[spots.size()]);
            aml.findLocation(res, loc, usendkfilter);

            PointF avePt = null;

            if (loc.getX() == -1 || loc.getX() == -999) {
                avePt = getCurrentLock();
            } else {
                avePt = new PointF(loc.getX(), loc.getY());
            }

            //for the red dot
            lastAverage = avePt;

            if (currentLock == null) {
                currentLock = avePt;
            }

            LocationCorrector lcr = LocationCorrector.getInstance();
            if (lcr != null) {
                PointF dr = lcr.getDeadReckoning();
                //FIX THE LOCATION
                if (dr != null && avePt != null) {

                    float dx = avePt.x - dr.x;
                    float dy = avePt.y - dr.y;

                    float dist = (float) Math.sqrt(dx * dx + dy * dy);

                    PropertyHolder ph = PropertyHolder.getInstance();

                    FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

                    if (facConf != null) {

                        float actionDist = dist / facConf.getPixelsToMeter();

                        float locatorradius = facConf.getLocatorRadius();


                        FloorData floordata = facConf.getSelectedFloorData();
                        if (floordata != null && floordata.stickyRadius != -1) {
                            locatorradius = floordata.stickyRadius;
                        }


                        if (!isUserIndirection(dr, avePt)) {
                            locatorradius *= 2f;

                        }

                        if (actionDist > locatorradius) {
                            float w = PropertyHolder.getInstance().getLocatorDeadReckoningWeight();

                            if (locatorradius < 0.1) {
                                w = 0;
                            }

                            float wAvgLocX = w * dr.x + (1.0f - w) * avePt.x;

                            float wAvgLocY = w * dr.y + (1.0f - w) * avePt.y;

                            PointF wAvgLocation = new PointF(wAvgLocX, wAvgLocY);
                            lcr.setLocationPositive(wAvgLocation);

                        }

                    }
                }

            }
        }

        if (!LocationCorrector.getInstance().isInitialized()) {
            LocationCorrector.getInstance().setPosition(getCurrentLock());
        }

        PointF p = LocationCorrector.getInstance().getDeadReckoning();
        if (p != null) {
            setCurrentLock(p);
        }

        if (getCurrentLock() == null) {
            setCurrentLock(new PointF());
        }
        return getCurrentLock();
    }

    public PointF findRfLocation(List<WlBlip> spots, float angle, boolean usendkfilter) {
        addScan(spots);
        //XXX NDK

        NdkLocationFinder aml = NdkLocationFinder.getInstance();
        if (aml != null) {

            FLocation loc = new FLocation();
            spots = filterByRssiLevelAndTopK(spots);

            WlBlip[] res = spots.toArray(new WlBlip[spots.size()]);

            aml.findLocation(res, loc, usendkfilter);

            PointF avePt = null;

            if (loc.getX() == -1 || loc.getX() == -999) {
                avePt = getCurrentLock();
            } else {
                avePt = new PointF(loc.getX(), loc.getY());
            }

            //for the red dot
            lastAverage = avePt;

            currentLock = avePt;

        }

        if (currentLock == null) {
            setCurrentLock(new PointF());
        }
        return currentLock;
    }

    public PointF findProjectedRfLocation(List<WlBlip> spots, float angle, boolean usendkfilter) {
        addScan(spots);
        //XXX NDK

        NdkLocationFinder aml = NdkLocationFinder.getInstance();
        if (aml != null) {

            FLocation loc = new FLocation();
            spots = filterByRssiLevelAndTopK(spots);

            WlBlip[] res = spots.toArray(new WlBlip[spots.size()]);
            aml.findLocation(res, loc, usendkfilter);

            PointF avePt = null;

            if (loc.getX() == -1 || loc.getX() == -999) {
                avePt = getCurrentLock();
            } else {
                avePt = new PointF(loc.getX(), loc.getY());
            }

            //for the red dot
            lastAverage = avePt;

            currentLock = avePt;

        }

        PointF p = GisData.getInstance().findClosestPointOnLine(currentLock);
        if (p != null) {
            currentLock = p;
        }

        if (currentLock == null) {
            setCurrentLock(new PointF());
        }
        return currentLock;

    }

    protected boolean isUserIndirection(PointF dr, PointF avePt) {
        if (!PropertyHolder.getInstance().isAsymetricLocatorRadius()) {
            return true;
        }

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        float floorRotation = 0;
        if (facConf != null) {
            floorRotation = facConf.getFloorRotation();
        }

        float pointsangle = MathUtils.getLIneAngle(dr, avePt);
        pointsangle = (pointsangle + 360) % 360;
        float userangle = OrientationMonitor.getInstance().getAzimuth() - floorRotation;
        userangle = (userangle + 360) % 360;
        return Math.abs(userangle - pointsangle) < 60;

    }

    public void asyncInit() {
        if (LOCATOR == LOCATORS.MATRIX) {


            cancelLoadMatrixTask();

            loadMatrixTask = new LoadMatrixTask();

            loadMatrixTask.execute();

            // AsociativeMemoryLocator.getInstance().load();


            mSpots = new HashMap<String, Queue<WlBlip>>();
        }
    }

    public void init() {
        if (LOCATOR == LOCATORS.MATRIX) {

            //AsociativeMemoryLocator.getInstance().load();
            loadNdkLocationFinder();
            mSpots = new HashMap<String, Queue<WlBlip>>();
        }
    }


    //XXX NDK
    private void loadNdkLocationFinder() {

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

        float pixelsToMeter = 1;
        int kTopLevelThr = 0;

        if (facConf != null) {
            pixelsToMeter = facConf.getPixelsToMeter();
            kTopLevelThr = facConf.getTopKlevelsThr();
        }

        int floor = facConf.getSelectedFloor(); // FacilityConf.getInstance().getSelectedFloor();
        String facility = facConf.getId();
        String campus = PropertyHolder.getInstance().getCampusId();
        String project = PropertyHolder.getInstance().getProjectId();
        boolean isBin = PropertyHolder.getInstance().isTypeBin();
        String appDirPath = PropertyHolder.getInstance().getAppDir().getAbsolutePath();
        String scanType = PropertyHolder.getInstance().getMatrixFilePrefix();
        if (PropertyHolder.useZip) {
            appDirPath = PropertyHolder.getInstance().getZipAppdir().getAbsolutePath();
            scanType = "";
        }
        int locationCloseRange = facConf.getNdkCloseRange();
        int k = PropertyHolder.getInstance().getK();
        int floorcount = facConf.getFloorDataList().size(); //FacilityConf.getInstance().getFloorDataList().size();
        int averageRange = PropertyHolder.getInstance().getAverageRange();

        ArrayList<String> filter = PropertyHolder.getInstance().getSsidFilter();
        String[] ssidfilter = filter.toArray(new String[filter.size()]);

        float closeDevicesThreshold = PropertyHolder.getInstance().getCloseDeviceThreshold();
        float closeDeviceWeight = PropertyHolder.getInstance().getCloseDeviceWeight();


        NdkLocationFinder.getInstance().initParams(appDirPath, locationCloseRange, k, pixelsToMeter, averageRange, ssidfilter, floorcount, scanType,
                closeDevicesThreshold, closeDeviceWeight, kTopLevelThr);

        String path = project + "/" + campus + "/" + facility;
        if (PropertyHolder.useZip) {
            path = project + "/" + campus + "/facilities/" + facility + "/floors";
        }

        NdkLocationFinder.getInstance().load(path, floor, isBin);
    }

    public void cancelLoadMatrixTask() {
//		if(loadMatrixTask!=null){
//			if(loadMatrixTask.cancel(true)){
//				Log.d("MATRIX","MATRIX LOAD CANCELED");
//			}
//			loadMatrixTask=null;
//		}

        // Cancel potentially running tasks
        if (loadMatrixTask != null
                && loadMatrixTask.getStatus() != LoadMatrixTask.Status.FINISHED) {
            loadMatrixTask.cancel(true);
            loadMatrixTask = null;
            Log.d("MATRIX", "MATRIX LOAD CANCELED");
        }
    }


    public void syncInit() {
        if (LOCATOR == LOCATORS.MATRIX) {
            //XXX NDK
            //AsociativeMemoryLocator.getInstance().load();
            loadNdkLocationFinder();
            mSpots = new HashMap<String, Queue<WlBlip>>();
        }
    }


    public List<PointF> findPossibleLoc(boolean intersect) {
        if (LOCATOR == LOCATORS.MATRIX) {
            return WIFILevelMatrix.getInstance().findPossibleLoc(
                    getMeanBlips(), intersect);
        }
        return null;
    }

    public void addScan(List<WlBlip> scan) {
        Queue<WlBlip> queue;
        for (WlBlip blip : scan) {
            if (!mSpots.containsKey(blip.BSSID)) {
                mSpots.put(blip.BSSID, new LinkedList<WlBlip>());
            }
            queue = mSpots.get(blip.BSSID);
            if (queue.size() >= QUEUE_LENGTH) {
                queue.remove();
            }
            queue.add(blip);
        }
    }

    private List<WlBlip> getMeanBlips() {
        WlBlip mean;
        List<WlBlip> means = new ArrayList<WlBlip>();
        for (String bssid : mSpots.keySet()) {
            mean = getMeanBlip(bssid);
            if (mean != null) {
                means.add(mean);
            }
        }
        return means;
    }

    private WlBlip getMeanBlip(String bssid) {
        WlBlip source;
        if (mSpots.containsKey(bssid)) {
            source = mSpots.get(bssid).peek();
            if (source != null) {
                WlBlip mean = new WlBlip(mSpots.get(bssid).element());
                mean.level = getMeanLevel(bssid);
                return mean;
            }
        }
        return null;
    }

    public List<PointF> findPossibleLoc(String bssid) {
        if (LOCATOR == LOCATORS.MATRIX) {
            return WIFILevelMatrix.getInstance().findPossibleLoc(bssid,
                    getMeanLevel(bssid));
        }
        return null;
    }

    private int getMeanLevel(String bssid) {
        int acc = 0;
        int i = 0;
        for (WlBlip blip : mSpots.get(bssid)) {
            acc += blip.level;
            i++;
        }
        return (int) acc / i;
    }

    //XXX NDK
    public List<AssociativeDataSorter> getclosetspoints() {

        return new ArrayList<AssociativeDataSorter>(); //AsociativeMemoryLocator.getInstance().getClosePoints();

    }


    public void setNavigationState(boolean navState, NavigationPath path) {
        LocationCorrector.getInstance().setNavigationState(navState, path);
    }

    public void resetNavState() {
        LocationCorrector.getInstance().resetNavState();
    }

    public PointF getCurrentLock() {
        return currentLock;
    }

    public void setCurrentLock(PointF currentLock) {
        this.currentLock = currentLock;
    }

    public GisPoint getLocation() {

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

        if (facConf == null) {
            return new GisPoint();
        }

        double z = facConf.getSelectedFloor();
        GisPoint location = new GisPoint(currentLock.x, currentLock.y, z);
        return location;
    }

    public PointF getLastAverage() {
        return lastAverage;
    }

    public void setLastAverage(PointF lastAverage) {
        this.lastAverage = lastAverage;
    }

    private List<WlBlip> filterByRssiLevelAndTopK(List<WlBlip> blips) {

        FacilityConf cfacility = FacilityContainer.getInstance().getCurrent();
        List<WlBlip> filtered = new ArrayList<WlBlip>();

        if (cfacility == null || cfacility.getLocationLevelThreshold() <= -127 || blips.size() == 0) {
            return blips;
        }

        int topK = cfacility.getTopKlevelsThr();
        int levelThr = cfacility.getLocationLevelThreshold();

        Collections.sort(blips, filterComp);

        for (WlBlip b : blips) {

            if (b.level < levelThr || filtered.size() == topK) {
                break;
            }

            filtered.add(b);
        }


        return filtered;
    }

    public enum LOCATORS {
        MATRIX, YARON, MOCK
    }

}
