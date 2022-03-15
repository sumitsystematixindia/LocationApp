package gps;

import android.app.Activity;
import android.graphics.PointF;

import com.mlins.orientation.OrientationMonitor;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.steps.StepsCounter;


public class GpsStateCollectorThread extends Thread {
    private static final String TAG = "GpsStateCollectorThread";
    private static final long INTERVAL = 1000;
    Object lock = new Object();
    //	private long mLastCollect;
//	private Handler mMainHandler = new Handler(Looper.getMainLooper());
    Activity activity;
    private boolean mRunning = false;
    private boolean mFinishing = false;
    private PointF deadRacoon = null;
    private boolean mProjectonGis = false;

    public GpsStateCollectorThread() {
        super(TAG);
    }

    public void setActivity(Activity act) {
        activity = act;
    }

    @Override
    public void run() {
        while (mRunning) {
            //			collect state :)
            if (activity != null)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateReckoning();
                    }
                });

            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        mFinishing = false;
    }

    @Override
    public synchronized void start() {
        mRunning = true;
        mProjectonGis = PropertyHolder.getInstance().isProjectOnGis();
        super.start();
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void finish() {
        mRunning = false;
        mFinishing = true;
    }

    public boolean isFinishing() {
        return mFinishing;
    }

    public void setFix(PointF fix) {
        synchronized (lock) {
            setDeadRacoon(fix);
            GpsLocationCorrector.getInstance().setDeadReckon(getDeadRacoon());
        }
    }

//	public PointF getReckoning() {
//		synchronized (lock) {
//			return deadRacoon;
//		}
//	}

    private void updateReckoning() {

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

        if (facConf == null) {
            return;
        }

        if (facConf.getSelectedFloor() == -1)
            return;
//		long now = SystemClock.elapsedRealtime();
//		long dt = (now - mLastCollect) / 1000; // seconds
//		mLastCollect = now;
//		float speed = StepsCounter.getInstance().getSpeed(5);
        double heading =/*HeadingObserver.getInstance().getHeading()*/OrientationMonitor.getInstance().getAzimuth() - facConf.getFloorRotation();
//		updateReckoning(speed * dt, heading);
        float distance = StepsCounter.getInstance().countReset();
        distance *= StepsCounter.getInstance().getStepSize();
        updateReckoning(distance, heading);
    }

    private void updateReckoning(float distance, double heading) {
        if (getDeadRacoon() == null)
            return;
        synchronized (lock) {
            double radangle = Math.toRadians(heading);
            getDeadRacoon().x += distance * Math.sin(radangle);
            getDeadRacoon().y -= distance * Math.cos(radangle);  // Y increase towards the bottom.
            if (mProjectonGis) {
                PointF p = null;
                if (PropertyHolder.getInstance().isTurnToClosestGisLineMethod()) {

                    FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
                    float rot = 0;
                    if (facConf != null) {
                        rot = facConf.getFloorRotation();
                    }

                    double angle = OrientationMonitor.getInstance().getAzimuth() - rot;
                    p = CampusGisData.getInstance().findClosestPointOnSegment(getDeadRacoon(), angle);
                    if (p == null) {
                        p = CampusGisData.getInstance().findClosestPointOnLine(
                                getDeadRacoon(), angle);
                    }
                } else {
                    p = CampusGisData.getInstance().findClosestPointOnLine(getDeadRacoon());
                }
                if (p != null) {
                    getDeadRacoon().set(p);
                }
            }
            GpsLocationCorrector.getInstance().setDeadReckon(getDeadRacoon());
        }
    }


    public PointF getDeadRacoon() {
        return deadRacoon;
    }


    public void setDeadRacoon(PointF deadRacoon) {
        this.deadRacoon = deadRacoon;
    }
}
