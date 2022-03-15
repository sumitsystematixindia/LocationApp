package com.mlins.locator;

import android.app.Activity;
import android.graphics.PointF;

import com.mlins.aStar.NavigationPath;
import com.mlins.orientation.OrientationMonitor;
import com.mlins.utils.Cleanable;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.GisData;
import com.mlins.utils.steps.StepsCounter;

import java.util.LinkedHashMap;
import java.util.Map;

public class LocationCorrector implements Cleanable {

    protected PointF mReckon = null;
    protected NavigationPath mPath = null;
    protected boolean mNavState = false;
    private Map<Long, PointF> mLocationHistory;
    private PointF mLocation;
    private float mQuality;
    private StateCollectorThread mCollector;
    private Activity activity;

    public LocationCorrector(){
        init();
    }

    public static LocationCorrector getInstance() {
        return Lookup.getInstance().get(LocationCorrector.class);
    }

    protected void init() {
        mQuality = 0;
        if (mLocation == null)
            mLocation = new PointF();
        if (mLocationHistory == null) {
            mLocationHistory = new LinkedHashMap<Long, PointF>();
        } else {
            // saveHistory();
            mLocationHistory.clear();
        }
        StepsCounter.getInstance().init();
        HeadingObserver.getInstance().init();
        mCollector = new StateCollectorThread();
        mCollector.start();
    }

    protected PointF correctLocation() {
        PointF p = null;
        // remember(location);
        if (mReckon != null) {
            if (mNavState && PropertyHolder.getInstance().isProjectOnPath()) {
                p = mPath.getClosestPointOnPath(mReckon);
            } else {
                if (PropertyHolder.getInstance().isTurnToClosestGisLineMethod()) {
                    FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
                    if (facConf != null) {
                        double angle = OrientationMonitor.getInstance().getAzimuth() - facConf.getFloorRotation();//FacilityConf.getInstance().getFloorRotation();
                        p = GisData.getInstance().findClosestPointOnSegment(mReckon, angle);
                        if (p == null) {
                            p = GisData.getInstance().findClosestPointOnLine(
                                    mReckon, angle);
                        }
                        if (p == null) {
                            p = GisData.getInstance().findClosestPointOnLine(mReckon);
                        }
                    }
                } else {
                    p = GisData.getInstance().findClosestPointOnLine(mReckon);
                }
            }
            // if (p != null)
            // location.set(p);
        }
        return p;
    }

    private void remember(PointF location) {
        mLocationHistory.put(now(), location);
    }

    private Long now() {
        // XXX: don't bloat history until ready.
        return 0L;
        // return SystemClock.elapsedRealtime();
    }

    public void setLocationPositive(PointF location) {
        mLocation = location;
        mQuality = 1;
        remember(location);
        mCollector.setFix(location);
    }

    public void setLocation(PointF location, int quality) {
        // XXX: method stub simple average.
        float q = Math.max(Math.min(quality / 255, 1), 0); // clamp to 0-1.
        mLocation.x = (mLocation.x * mQuality + location.x * quality)
                / (mQuality + q);
        mLocation.y = (mLocation.y * mQuality + location.y * quality)
                / (mQuality + q);
        mQuality = (mQuality + q) / 2;
        remember(location);
    }

    public PointF getDeadReckoning() {
        return mReckon;
    }

    public void setDeadReckon(PointF deadRacoon) {

        mReckon = deadRacoon;

    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        mCollector.setActivity(activity);

    }

    public void setPosition(PointF currentLock) {
        mReckon = currentLock;

    }

    public boolean isInitialized() {
        // TODO Auto-generated method stub
        return mReckon != null;
    }

    public void setNavigationState(boolean navState, NavigationPath path) {
        mNavState = navState;
        mPath = path;

    }

    public void resetNavState() {
        mNavState = false;
        mPath = null;
    }

    public boolean isCollectorInitialized() {
        boolean result = false;
        if (mCollector.getDeadRacoon() != null) {
            result = true;
        }
        return result;
    }

    @Override
    public void clean() {
        mCollector.finish();
    }
}
