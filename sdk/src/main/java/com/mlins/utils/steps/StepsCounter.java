package com.mlins.utils.steps;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.mlins.utils.Cleanable;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;

import java.util.ArrayList;
import java.util.List;

public class StepsCounter implements Cleanable, StepListener {

    private static final long PERIOD = 60000; // Milliseconds

    private StepDetector mDetector;
    private boolean mInitialized = false;
    private List<Long> mSteps = new ArrayList<Long>();
    private SensorManager mSensorManager;
    private float mStepSize = 0;
    //	private Object lock = new Object();
    private int mCount;
    public StepsCounter() {
        super();
        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        float p2m = 1;
        if (facConf != null) {
            p2m = facConf.getPixelsToMeter();
        }

        mStepSize = 0.83f * p2m; // step in meter * pixels/meter

    }

    public static StepsCounter getInstance() {
        return Lookup.getInstance().get(StepsCounter.class);
    }

    public void clean(){
        stop();
    }

    public void init() {
        if (mInitialized)
            return;

        PropertyHolder ph = PropertyHolder.getInstance();

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        float p2m = 1;
        if (facConf != null) {
            p2m = facConf.getPixelsToMeter();
        }

        mStepSize = ph.getStepSize() * p2m;

        if (mDetector == null) {
            mDetector = new StepDetector();
            mDetector.setSensitivity(1.3f);
        }
        mSensorManager = (SensorManager) PropertyHolder.getInstance()
                .getMlinsContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor accel = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (PropertyHolder.getInstance().isUseKitKatVersionSolution()
                /*android.os.Build.VERSION.SDK_INT >= PropertyHolder.KITKAT_VERSION*/) {
            mSensorManager.registerListener(mDetector, accel, SensorManager.SENSOR_DELAY_FASTEST);
            // require api 19
            //mSensorManager.registerListener(mDetector, accel,SensorManager.SENSOR_DELAY_FASTEST,0);
        } else {
            mSensorManager.registerListener(mDetector, accel, SensorManager.SENSOR_DELAY_NORMAL);
        }

        mDetector.addStepListener(this);
        mInitialized = true;
    }

    public void addListener(StepListener listener) {
        if (mDetector != null) {
            mDetector.addStepListener(listener);
        }
    }

    public void reset() {
        mSteps.clear();
    }

    public void stop() {
        mSensorManager.unregisterListener(mDetector);
        mInitialized = false;
    }

    public int getCount(int seconds) {
//		int count = 0;
//		synchronized (lock) {
//			if (mSteps.isEmpty())
//				return 0;
//			if (seconds <= 0) {
//				return mSteps.size();
//			}
//
//			long milis = 1000 * seconds;
//
//			long now = SystemClock.elapsedRealtime();
//			long first = now - milis;
//			// reverse iteration:
//			for (ListIterator<Long> iterator = mSteps.listIterator(mSteps
//					.size()); iterator.hasPrevious();) {
//				Long time = (Long) iterator.previous();
//				if (time > first) {
//					count++;
//				} else
//					break;
//			}
//		}
//
//		return count;
        return mCount;
    }

    @Override
    public void onStep() {

        if (PropertyHolder.getInstance().isIgnoreStepsCounter()) {
            mCount = 0;
            mSteps.clear();
            return;
        }

        mCount++;
//		long now = SystemClock.elapsedRealtime();
//		synchronized (lock) {
//			mSteps.add(Long.valueOf(now));
//			List<Long> tmplist = new ArrayList<Long>();
//			long first = now - PERIOD;
//			for (Long time : mSteps) {
//				if (time < first) {
//					tmplist.add(time);
////				} else {
////					break;
//				}
//			}
//			mSteps.removeAll(tmplist);
//		}
    }

    @Override
    public void passValue() {
        // XXX: what is it for?
    }

    public float getSpeed(int seconds) {
        float s = getCount(seconds);
        if (s != 0)
            s /= seconds;
//		else {
//			long dt = -1;
//			synchronized (lock) {
//				if (!mSteps.isEmpty()) {
//					dt = SystemClock.elapsedRealtime() - lastStep();
//					s = 1000f / dt;
//				}
//
//			}
//
//		}
        return s * mStepSize;
    }

//	private long lastStep() {
//		long result = -1;
////		synchronized (lock) {
//			result = mSteps.get(mSteps.size() - 1);
////		}
//
//		return result;
//	}

    public float getStepSize() {

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        float p2m = 1;

        if (facConf != null) {
            p2m = facConf.getPixelsToMeter();
        }

        return PropertyHolder.getInstance().getStepSize() * p2m;
    }

    public float countReset() {
        int count = mCount;
        mCount = 0;
        return count;
    }

}
