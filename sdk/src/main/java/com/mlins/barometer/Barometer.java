package com.mlins.barometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.mlins.utils.PropertyHolder;

import java.util.ArrayList;

public class Barometer implements SensorEventListener {
    // private static final long CLEAR_THRESHOLD = 15 * 1000;
    static private BarometerStatus EMPTY_STATUS = new BarometerStatus(0);
    private static Barometer instance = null;
    private static Object lock = new Object();
    private SensorManager mSensorManager = null;
    private Sensor mPressure = null;
    private int count = 0;
    // private float millibars_of_pressure = 0;
    private ArrayList<Float> sw = new ArrayList<Float>();
    private int SW_SIZE = 31;
    private float MODEL_TRHESHOLD = 0.35f;
    private int HISTORY_THRESHOLD_IN_SECONDS = 10 * 1000;
    private int PATTERN_SIZE = 1;
    private ArrayList<Integer> upWardPattern = new ArrayList<Integer>();
    private ArrayList<Integer> downWardPattern = new ArrayList<Integer>();

    // private int HISTORY_SIZE = 10;
    // private ArrayList<BarometerStatus> trendsStatusHistory = new
    // ArrayList<BarometerStatus>();
    private BarometerStatus lastTrendsStatus = EMPTY_STATUS;
    private ExponentialMovingAverage expMAfilter = new ExponentialMovingAverage(0.7);

    private Barometer() {
        initSensor();
    }

    //no usages for now
    public static Barometer getInstance() {
        if (instance == null) {
            instance = new Barometer();

        }
        return instance;
    }

    public static void releaseInstance() {
        if (instance != null) {
            instance.clean();
            instance.unRegisterSensorOnPause();
            instance = null;
        }
    }

    public void initSensor(SensorManager mSensorManager, Sensor mPressure) {
        this.mSensorManager = mSensorManager;
        this.mPressure = mPressure;
        registerSensorOnResume();
    }

    private void initSensor() {

        Context appCtx = PropertyHolder.getInstance().getMlinsContext();
        if (appCtx != null) {
            mSensorManager = (SensorManager) appCtx
                    .getSystemService(appCtx.SENSOR_SERVICE);
            mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            if (mPressure == null) {
                PropertyHolder.getInstance().setBarometerOn(false);
            } else {
                registerSensorOnResume();
            }
        }
    }

    public void initParameters(int swSize, float model_threshold,
                               int history_threshold_threshold, int pattern_size) {

        this.SW_SIZE = swSize;
        this.MODEL_TRHESHOLD = model_threshold;
        this.HISTORY_THRESHOLD_IN_SECONDS = history_threshold_threshold * 1000;
        this.PATTERN_SIZE = pattern_size;

        sw.clear();

        upWardPattern.clear();
        downWardPattern.clear();
        // trendsStatusHistory.clear();
        lastTrendsStatus = EMPTY_STATUS;
        count = 0;

    }

    private void clean() {
        sw.clear();
        upWardPattern.clear();
        downWardPattern.clear();
        // trendsStatusHistory.clear();
        lastTrendsStatus = EMPTY_STATUS;

        count = 0;
    }

    public void registerSensorOnResume() {
        registerSensorOnResume(this);

    }

    public void registerSensorOnResume(SensorEventListener sel) {

        if (/*android.os.Build.VERSION.SDK_INT >= PropertyHolder.KITKAT_VERSION*/
                PropertyHolder.getInstance().isUseKitKatVersionSolution()) {

            //KITKAT require api 19
            //mSensorManager.registerListener(sel, mPressure, SensorManager.SENSOR_DELAY_FASTEST, 0);
            mSensorManager.registerListener(sel, mPressure, SensorManager.SENSOR_DELAY_FASTEST);

        } else {
            mSensorManager.registerListener(sel, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
        }


    }


    public void unRegisterSensorOnPause() {
        unRegisterSensorOnPause(this);
    }

    public void unRegisterSensorOnPause(SensorEventListener sel) {
        mSensorManager.unregisterListener(sel);
    }

    /**
     * @param millibars_of_pressure
     */
    private void addSample(float millibars_of_pressure) {
        // Do something with this sensor data.
        addToSlidingWindow(millibars_of_pressure);
        if (sw.size() == SW_SIZE) {
            checkTrend();
        }
    }

    // public BarometerStatus getTrendStaus() {
    //
    // synchronized (this) {
    //
    // if (trendsStatusHistory.size() > 0) {
    // BarometerStatus bs = trendsStatusHistory.get(0);
    // // REMOVE
    // trendsStatusHistory.clear();
    // return bs;
    //
    // }
    //
    // }
    // return EMPTY_STATUS;
    // }

    public BarometerStatus getTrendStausWithinPeriod() {

        synchronized (lock) {

            long currTime = System.currentTimeMillis();
            // BarometerStatus last=null;

            if (lastTrendsStatus == null)
                return EMPTY_STATUS;

            if ((currTime - lastTrendsStatus.getTimeStamp()) <= HISTORY_THRESHOLD_IN_SECONDS) {
                return lastTrendsStatus;
            }

            // if(trendsStatusHistory.size()==0)
            // return EMPTY_STATUS;
            //
            // int hisSize=trendsStatusHistory.size()-1;
            // for(int i=0; i<=hisSize; i++)
            // {
            // last=trendsStatusHistory.get(hisSize-i);
            // if ((currTime-last.getTimeStamp()) <= convertToMilliSecond){
            // return last;
            // }
            // }
        }
        return EMPTY_STATUS;

    }

    private void checkTrend() {

        // int status = 0;
        // status: 1= up trend , 0 = no trend, -1 =down trend
        float yi = 0;
        float yj = 0;
        float diff = 0;
        int S = 0;
        // Nonparametric test: Mann-Kendall
        for (int i = 0; i < sw.size() - 1; i++) {
            yi = sw.get(i);
            for (int j = i + 1; j < sw.size(); j++) {
                yj = sw.get(j);
                diff = (yj - yi);
                if (diff > 0)
                    S++;
                else if (diff < 0)
                    S--;
            }
        }

        float tau = 0;
        tau = S / ((sw.size() * (sw.size() - 1)) / 2.0f);
        if (tau >= MODEL_TRHESHOLD) {
            if (PATTERN_SIZE == 1) {
                addTrendStatus(-1);
                sw.clear();
            } else {
                checkDownTrendPattern();
            }

        } else if (tau <= -MODEL_TRHESHOLD) {

            if (PATTERN_SIZE == 1) {
                addTrendStatus(1);
                sw.clear();
            } else {

                checkUpTrendPattern();
            }

        }
        // else { // check if stopped any where in the floor
        // // Murad Method
        // if (downWardPattern.size() >= STEPS_THRESHOLD) {
        // boolean alertDown = true;
        // for (int i = 0; i < downWardPattern.size() - 1; i++) {
        // if (downWardPattern.get(i + 1) - downWardPattern.get(i) != 1) {
        // alertDown = false;
        // break;
        // }
        // }
        // if (alertDown) {
        // // showResults.append("DOWN-WARD END" + "\n");
        // // status = -1;
        // addTrendStatus(-1);
        // downWardPattern.clear();
        // // upWardPattern.clear();
        // sw.clear();
        // count = 0;
        //
        // }
        // }
        //
        // if (upWardPattern.size() >= STEPS_THRESHOLD) {
        //
        // boolean alertUp = true;
        // for (int i = 0; i < upWardPattern.size() - 1; i++) {
        // if (upWardPattern.get(i + 1) - upWardPattern.get(i) != 1) {
        // alertUp = false;
        // break;
        // }
        // }
        // if (alertUp) {
        // // showResults.append("UP-WARD END" + "\n");
        // // status = 1;
        // addTrendStatus(1);
        // upWardPattern.clear();
        // // downWardPattern.clear();
        // sw.clear();
        // count = 0;
        // }
        // }
        //
        // }

        // return status;

    }

    private void checkDownTrendPattern() {

        downWardPattern.add(++count);

        if (downWardPattern.size() >= PATTERN_SIZE) {
            boolean alertDown = true;
            for (int i = 0; i < PATTERN_SIZE - 1; i++) {
                if (downWardPattern.get(i + 1) - downWardPattern.get(i) != 1) {
                    alertDown = false;
                    break;
                }
            }
            if (alertDown) {

                addTrendStatus(-1);
                downWardPattern.clear();
                upWardPattern.clear();
                sw.clear();
                count = 0;

            }
        }
    }

    private void checkUpTrendPattern() {

        upWardPattern.add(++count);

        if (upWardPattern.size() >= PATTERN_SIZE) {

            boolean alertUp = true;
            for (int i = 0; i < PATTERN_SIZE - 1; i++) {
                if (upWardPattern.get(i + 1) - upWardPattern.get(i) != 1) {
                    alertUp = false;
                    break;
                }
            }
            if (alertUp) {

                addTrendStatus(1);
                upWardPattern.clear();
                downWardPattern.clear();
                sw.clear();
                count = 0;
            }
        }

    }

    private void addToSlidingWindow(float airpress) {

        if (sw != null) {

            if (sw.size() < SW_SIZE) {
                sw.add(airpress);
            } else {
                if (sw.size() > 0) {

                    sw.add(sw.size() - 1, airpress);
                    sw.remove(0);
                }
            }
        }

    }

    private void addTrendStatus(int status) {

        lastTrendsStatus = new BarometerStatus(status);

        // if (trendsStatusHistory != null) {
        //
        // if (trendsStatusHistory.size() < HISTORY_SIZE) {
        // trendsStatusHistory.add(new BarometerStatus(status));
        // } else {
        // if (trendsStatusHistory.size() > 0) {
        //
        // trendsStatusHistory.add(trendsStatusHistory.size() - 1,
        // new BarometerStatus(status));
        // trendsStatusHistory.remove(0);
        // }
        // }
        // }

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // XXX Do something here if barometer sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        // if(s!=null && s.hasNext()){
        // Barometer.getInstance().addSample(Float.valueOf(s.nextLine()));
        // }

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            //addSample(event.values[0]);

            float sample = (float) expMAfilter.average(event.values[0]);

            //System.out.println(sample);

            addSample(sample);

            // XXX BAROMETER
            // BarometerStatus bs=
            // Barometer.getInstance().getTrendStausWithinPeriod(5);
            // if(bs!=null){
            // //((bs.getStatus()==1)? "UP-WARD END" : "DOWN-WARD END")
            // Toast.makeText(this, ((bs.getStatus()==1)? "UP-WARD END" :
            // "DOWN-WARD END"), Toast.LENGTH_SHORT).show();
            // }

            // BarometerStatus bs = getTrendStausWithinPeriod(3600);
            // if (bs.getStatus() != 0) {
            // Toast.makeText(
            // PropertyHolder.getInstance().getMlinsContext(),
            // ((bs.getStatus() == 1) ? "UP-WARD END"
            // : "DOWN-WARD END"), Toast.LENGTH_SHORT).show();
            // }
        }
    }


    private class ExponentialMovingAverage {
        private double alpha;
        private Double oldValue;

        public ExponentialMovingAverage(double alpha) {
            this.alpha = alpha;
        }

        public double average(double value) {
            if (oldValue == null) {
                oldValue = value;
                return value;
            }
            double newValue = oldValue + alpha * (value - oldValue);
            oldValue = newValue;
            return newValue;
        }
    }

}
