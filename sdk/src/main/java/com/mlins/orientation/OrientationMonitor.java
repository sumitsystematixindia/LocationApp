package com.mlins.orientation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

public final class OrientationMonitor extends Observable implements Cleanable {

    private static final int MOVING_AVERAGE_FILTER = 0;
    private static final int LOW_PASS_FILTER = 1;
    private static final String TAG = "OrientationMonitor";
    private static final boolean LEGACY_SENSORS_ONLY = false;
    private static final float GRADS = 1.0f;
    private static final float ACC_LOW_PASS_FILTER_ALPHA = 0.70f;
    private static final float MAG_LOW_PASS_FILTER_ALPHA = 0.90f;
    private static final int SW_SIZE = 7;
    private static final float ROTATION_RECALC = (float) Math.toRadians(3);

    final int sdkVersion = Build.VERSION.SDK_INT;
    private final int mSensorDelay = SensorManager.SENSOR_DELAY_GAME;
    private final int mFilterSize = 10;
    private Filter mAccelFilter = new LowPassFilter(ACC_LOW_PASS_FILTER_ALPHA); //new MovingAverageFilter(mFilterSize);
    private Filter mMagFilter = null; //new LowPassFilter(MAG_LOW_PASS_FILTER_ALPHA); //new MovingAverageFilter(mFilterSize);
    private SensorManager mSensMan;
    // filtering on azimuths
    private float azimuth = 0f;
    private ArrayList<Float> azimuthsForFiltering = new ArrayList<Float>();
    private float filteredAzimuth = 0f;
    // MovingAverage mAvgFilter = new MovingAverage(SW_SIZE);
    private float[] mOrientation = new float[3];
    private float[] mRotationM = new float[16];
    private float[] mRemapedRotationM = new float[16];

    private boolean mRegistered = false;
    private boolean mOrintationUpdate = false;
    private SensorEventListener mSensorEventListener;

    private float filteredRadians = 0f;

	/*
     * TODO: Pitch and Roll getters and setters. SENSOR_DELAY settings.
	 * Sensitivity settings. Data formats settings (degrees, mills, gradians,
	 * radians, ranges, etc.) Coordinate systems (Auto, camera, natural, etc.)
	 * Clean up: unregister from sensors if no observers are set. Done?
	 * Synchronize methods? Use local variables?
	 */

    private OrientationMonitor() {
        super();
        Context cntxt = PropertyHolder.getInstance().getMlinsContext();
        mSensMan = (SensorManager) cntxt
                .getSystemService(Context.SENSOR_SERVICE);
        registerToSensors();

    }

    public static OrientationMonitor getInstance() {
        return Lookup.getInstance().get(OrientationMonitor.class);
    }

    static public void releaseInstance() {
        Lookup.getInstance().remove(OrientationMonitor.class);
    }

    public void clean(){
        unregisterFromSensors();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observable#addObserver(java.util.Observer)
     */
    @Override
    public void addObserver(Observer observer) {
        if (!mRegistered)
            registerToSensors();
        super.addObserver(observer);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observable#deleteObserver(java.util.Observer)
     */
    @Override
    public synchronized void deleteObserver(Observer observer) {

        super.deleteObserver(observer);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observable#deleteObservers()
     */
    @Override
    public synchronized void deleteObservers() {

        super.deleteObservers();
    }

    public void setInputFilter(int sensorType, Filter inputFilter) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelFilter = inputFilter;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagFilter = inputFilter;
            default:
                return;
        }
    }

    public void setFilter(int sensorType, int filterType, float filterParam) {
        Filter inputFilter;

        switch (filterType) {
            case MOVING_AVERAGE_FILTER:
                inputFilter = new MovingAverageFilter((int) filterParam);
                break;
            case LOW_PASS_FILTER:
                inputFilter = new LowPassFilter(filterParam);
            default:
                inputFilter = null;
                break;
        }
        setInputFilter(sensorType, inputFilter);
    }

    public void calculateAzimuth() {
        // if device is flat (up to 26 degrees) use the device's natural
        // orientation.
        if (Math.abs(mRotationM[10]) >= 0.9f)
            getOrientation();
            // else use the camera axis for azimuth.
        else
            getOrientation(SensorManager.AXIS_X, SensorManager.AXIS_Z);
        // Express azimuth in the range 0-360 degrees with 5.0 graduation.
        //azimuth = graduate(mOrientation[0], GRADS);
        azimuth = mOrientation[0];
        addAzimuthToSlidingWindow(azimuth);

    }

    private void addAzimuthToSlidingWindow(float az) {

        if (azimuthsForFiltering != null) {
            if (Math.abs(filteredRadians - az) >= ROTATION_RECALC || azimuthsForFiltering.size() < SW_SIZE) {


                if (azimuthsForFiltering.size() < SW_SIZE) {
                    azimuthsForFiltering.add(az);
                } else {
                    if (azimuthsForFiltering.size() > 0) {

                        azimuthsForFiltering.add(azimuthsForFiltering.size() - 1, az);
                        azimuthsForFiltering.remove(0);
                    }
                }
                // filteredAzimuth = getMA();
                filteredRadians = getMovingAverage();
                filteredAzimuth = graduate(filteredRadians, GRADS);
                //System.out.println("angles rad : " + String.valueOf(filteredRadians) + " deg :" + String.valueOf(filteredAzimuth));

            }

        }
    }

    private float getMovingAverage() {

        //	if (azimuthsForFiltering.size() < SW_SIZE) {
        //		return azimuth;
        //	}

        float sumsin = 0;
        float sumcos = 0;
        for (int i = 0; i < azimuthsForFiltering.size(); i++) {

            sumsin += Math.sin(azimuthsForFiltering.get(i));
            sumcos += Math.cos(azimuthsForFiltering.get(i));

        }
        sumsin = sumsin / ((float) azimuthsForFiltering.size());
        sumcos = sumcos / ((float) azimuthsForFiltering.size());
        float result = (float) Math.atan2(sumsin, sumcos);
        return result;

    }

    private float getMA() {

        if (azimuthsForFiltering.size() < SW_SIZE) {
            return azimuth;
        }

        float max = azimuthsForFiltering.get(0);
        float min = azimuthsForFiltering.get(0);

        float avg = 0;
        float total = 0;

        for (int i = 0; i < azimuthsForFiltering.size(); i++) {

            if (azimuthsForFiltering.get(i) > max) {
                max = azimuthsForFiltering.get(i);
            }
            if (azimuthsForFiltering.get(i) < min) {
                min = azimuthsForFiltering.get(i);
            }
        }

        float delta = Math.abs(max - min);
        float moveTo = delta / 2f;
        if (moveTo > 180) {
            moveTo = (delta / 2f) + 180;
        }

        for (int i = 0; i < azimuthsForFiltering.size(); i++) {
            total = total + normelize(azimuthsForFiltering.get(i) + moveTo);
        }

        avg = total / ((float) azimuthsForFiltering.size());

        return normelize(avg - moveTo);

    }

    private float normelize(float angle) {
        float nangle = angle % 360;
        if (nangle < 0)
            nangle = 360 + nangle;
        return nangle;
    }

    /**
     * @return the Azimuth in the range 0-360 degrees with 0.5 graduation,
     * relative to the device's natural orientation.
     */
    public float getAzimuth() {
        return filteredAzimuth; // azimuth;

    }

    private float graduate(float radians, float grads) {
        return (float) Math.round(((Math.toDegrees(radians) + 360.0) % 360.0)
                / grads)
                * grads;

		/*
		 * return (float) Math.round((Math.toDegrees(mOrientation[0]) + 360) %
		 * 360 / grads) grads;
		 */
    }

    /**
     * @param screenRotation as obtained through {@link android.view.Display#getRotation()}
     * @return the Azimuth in the range 0-360 degrees with 0.5 graduation,
     * adapted to screen orientation.
     */
    public float getAzimuth(int screenRotation) {
        return filteredAzimuth + screenRotation;
//		// if device is flat (up to 26 degrees) use screen rotation.
//		if (Math.abs(mRotationM[10]) >= 0.9f) {
//			screenRotation *= 90;
//			getOrientation();
//		}
//		// else use the camera axis for azimuth.
//		else {
//			screenRotation = 0;
//			getOrientation(SensorManager.AXIS_X, SensorManager.AXIS_Z);
//		}
//		// Express azimuth in the range 0-360 degrees with 0.5 graduation.
//		return graduate(mOrientation[0], GRADS);
    }

    public float getAzimuthReturn() {
        return (getAzimuth() + 180.0f) % 360.0f;
    }

    /**
     * @return the Orientation as defined by
     * {@link SensorManager#getOrientation
     * SensorManager.getOrientation()}
     */
    public float[] getOrientation() {
        if (mOrintationUpdate) {
            SensorManager.getOrientation(mRotationM, mOrientation);
            mOrintationUpdate = false;
        }
        return mOrientation;
    }

    /**
     * Gets orientation by a specified coordinate system, combines the
     * functionality of {@link SensorManager#getOrientation
     * SensorManager.getOrientation()} with
     * {@link SensorManager#remapCoordinateSystem
     * SensorManager.remapCoordinateSystem()}
     *
     * @param X specify on which axis, as constants defined in
     *          {@link SensorManager SensorManager} to map
     *          the X axis of the device.
     * @param y defines on which axis, as constants defined in SensorManager,
     *          to map the Y axis of the device.
     * @return the orientation data by the specified coordinates system.
     * @see #getOrientation()
     * @see SensorManager
     * @see SensorManager#remapCoordinateSystem(float[], int,
     * int, float[])
     * @see SensorManager#getOrientation(float[], float[])
     */

    public float[] getOrientation(int X, int Y) {
        if (mOrintationUpdate) {
            if (X != SensorManager.AXIS_X || Y != SensorManager.AXIS_Y) {
                SensorManager.remapCoordinateSystem(mRotationM, X, Y,
                        mRemapedRotationM);
                SensorManager.getOrientation(mRemapedRotationM, mOrientation);
                mOrintationUpdate = false;
            } else
                getOrientation();
        }
        return mOrientation;
    }

    public Boolean registerToSensors() {
        Sensor sensor;
        boolean result = false;
        // if (sdkVersion >= Build.VERSION_CODES.GINGERBREAD){ // API Level 9,
        // Android 2.3[.X]
        // }
        if (!PropertyHolder.getInstance().useLegacySensors()
                && (sensor = mSensMan.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)) != null) {
            mSensorEventListener = new RotationVectorSensorEventListener();
            result = mSensMan.registerListener(mSensorEventListener, sensor, mSensorDelay);

        } else if ((sensor = mSensMan.getDefaultSensor(Sensor.TYPE_GRAVITY)) != null &&
                (sensor = mSensMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) != null) {
            mSensorEventListener = new LegacySensorEventListener();
            result = (
                    mSensMan.registerListener(mSensorEventListener, sensor, mSensorDelay) &&
                            mSensMan.registerListener(mSensorEventListener, mSensMan.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), mSensorDelay) &&
                            mSensMan.registerListener(mSensorEventListener, mSensMan.getDefaultSensor(Sensor.TYPE_GRAVITY), mSensorDelay));
        }
        // XXX FOR SM-TAB
        else if ((sensor = mSensMan.getDefaultSensor(Sensor.TYPE_ORIENTATION)) != null) {
            mSensorEventListener = new LegacySensorEventListener();
            result = (mSensMan.registerListener(mSensorEventListener, sensor,
                    mSensorDelay) && mSensMan.registerListener(
                    mSensorEventListener,
                    mSensMan.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    mSensorDelay));
        }

        if (result)
            Log.d(TAG, "Registered "
                    + mSensorEventListener.getClass().getSimpleName()
                    + " for events from " + sensor.getName() + ".");
        else
            Log.e(TAG, "No orientation sensors could be registered.");
        return mRegistered = result;
    }

    public void unregisterFromSensors() {
        mSensMan.unregisterListener(mSensorEventListener);
        mRegistered = false;
    }

    private interface Filter {
        void filter(float[] data);

        void filter(Float[] array);

        void reset();
    }

    private class LegacySensorEventListener implements SensorEventListener {

        private static final String TAG = "LegacySensorEventListener";
        private float[] mGravs = new float[3];
        private float[] mGeoMags = new float[3];

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {


            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER /*|| event.sensor.getType()==Sensor.TYPE_GRAVITY*/) {
                // Should only register for one of:

                // TODO: Detect if both are delivering events or use separate
                // listeners.
//				System.arraycopy(event.values, 0, mGravs, 0, 3);
//				if (mAccelFilter != null)
//					mAccelFilter.filter(mGravs);
                if (SensorManager.getRotationMatrix(mRotationM, null, mGravs,
                        mGeoMags)) {
                    mOrintationUpdate = true;
                    setChanged();
                    calculateAzimuth();
                    notifyObservers(mRotationM);
                }
                // TODO: else log/except orientation fault.
                // else mAzimuth = 365f;
            } else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                //XXX SOLUTION FOR GALAXY TAB 2
                float degree = Math.round(event.values[0]);
                filteredAzimuth = degree;
                //System.out.println(degree);
            }


            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

                System.arraycopy(event.values, 0, mGeoMags, 0, 3);
                if (mMagFilter != null)
                    mMagFilter.filter(mGeoMags);
                // Only process on acceleration changes, just keep the magnetic
                // data.
            }

            if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                System.arraycopy(event.values, 0, mGravs, 0, 3);
                if (mAccelFilter != null)
                    mAccelFilter.filter(mGravs);

            }
        }


//			switch (event.sensor.getType()) {
//			// Should only register for one of:
//			case Sensor.TYPE_ACCELEROMETER:
//			case Sensor.TYPE_GRAVITY:
//				// TODO: Detect if both are delivering events or use separate
//				// listeners.
//				System.arraycopy(event.values, 0, mGravs, 0, 3);
//				if (mAccelFilter != null)
//					mAccelFilter.filter(mGravs);
//				if (SensorManager.getRotationMatrix(mRotationM, null, mGravs,
//						mGeoMags)) {
//					mOrintationUpdate = true;
//					setChanged();
//					calculateAzimuth();
//					notifyObservers(mRotationM);
//				}
//				// TODO: else log/except orientation fault.
//				// else mAzimuth = 365f;
//				break;
//			case Sensor.TYPE_MAGNETIC_FIELD:
//				System.arraycopy(event.values, 0, mGeoMags, 0, 3);
//				if (mMagFilter != null)
//					mMagFilter.filter(mGeoMags);
//				// Only process on acceleration changes, just keep the magnetic
//				// data.
//				break;
//			default:
//				Log.d(TAG, "Recieved incompatible sensor event from: "
//						+ event.sensor.getName() + ".");
//			}
//		}

    }

    @SuppressLint("NewApi")
    private class RotationVectorSensorEventListener implements
            SensorEventListener {

        private static final String TAG = "RotationVectorSensorEventListener";
        private float[] mRotationVector = new float[3];

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                System.arraycopy(event.values, 0, mRotationVector, 0, 3);
                SensorManager.getRotationMatrixFromVector(mRotationM,
                        mRotationVector);
                mOrintationUpdate = true;
                setChanged();
                notifyObservers(mRotationM);

            }

//			switch (event.sensor.getType()) {
//			case Sensor.TYPE_ROTATION_VECTOR:
//				System.arraycopy(event.values, 0, mRotationVector, 0, 3);
//				SensorManager.getRotationMatrixFromVector(mRotationM,
//						mRotationVector);
//				mOrintationUpdate = true;
//				setChanged();
//				notifyObservers(mRotationM);
//				break;
//
//			default:
//				Log.d(TAG, "Recieved incompatible sensor event from: "
//						+ event.sensor.getName() + ".");
//				return;
//			}
        }

    }

    private class MovingAverageFilter implements Filter {
        /**
         * A moving average filter
         **/

        private final int mSize;
        private boolean mFilterFull = false;
        private int mFilterIndex;
        private float[][] mInValues;
        private double[] mAccumulator = new double[3];

        public MovingAverageFilter(int size) {
            mSize = size;
            mInValues = new float[3][size];
        }

        @Override
        public void filter(float[] data) {
            // Assert data.length == 3 ??
            if (!mFilterFull && mFilterIndex == mSize - 1)
                mFilterFull = true;
            int denominator = mFilterFull ? mSize : mFilterIndex + 1;
            int j = mFilterIndex;
            int k = (j + 1) % mSize;
            for (int i = 0; i < 3; i++) {
                mInValues[i][j] = data[i];
                mAccumulator[i] += mInValues[i][j] - mInValues[i][k]; // use
                // accumulator
                // (double)
                // to
                // avoid
                // round-off
                // drift.
                data[i] = (float) (mAccumulator[i] / denominator);
            }
            mFilterIndex = k;
        }

        public void filter(Float[] data) {
            // Assert data.length == 3 ??
            if (!mFilterFull && mFilterIndex == mSize - 1)
                mFilterFull = true;
            int denominator = mFilterFull ? mSize : mFilterIndex + 1;
            int j = mFilterIndex;
            int k = (j + 1) % mSize;
            for (int i = 0; i < 3; i++) {
                mInValues[i][j] = data[i];
                mAccumulator[i] += mInValues[i][j] - mInValues[i][k]; // use
                // accumulator
                // (double)
                // to
                // avoid
                // round-off
                // drift.
                data[i] = (float) (mAccumulator[i] / denominator);
            }
            mFilterIndex = k;
        }

        @Override
        public void reset() {
            for (int i = 0; i < 3; i++) {
                mAccumulator[i] = 0;
                for (int j = 0; j < mSize; j++)
                    mInValues[i][j] = 0;
            }
            mFilterIndex = 0;
            mFilterFull = false;
        }
    }

    private class LowPassFilter implements Filter {
        /**
         * A single pole low-pass filter
         **/

        float mAlfa, mBeta;
        float[] mlastData = new float[3];

        /**
         * @param alfa specifies the coefficient for the filter, must be 0 <=
         *             alfa <= 1, typical values about 0.85, the higher the value
         *             the slower the response.
         */
        public LowPassFilter(float alfa) {
            // Assert 0 <= alfa <= 1 ??
            mAlfa = alfa;
            mBeta = 1 - alfa;
        }

        @Override
        public void filter(float[] data) {
            // Assert data.length == 3 ??
            for (int i = 0; i < 3; i++) {
                mlastData[i] = mAlfa * mlastData[i] + mBeta * data[i];
                data[i] = mlastData[i];
            }
        }

        @Override
        public void reset() {
        }

        @Override
        public void filter(Float[] data) {
            // Assert data.length == 3 ??
            for (int i = 0; i < 3; i++) {
                mlastData[i] = mAlfa * mlastData[i] + mBeta * data[i];
                data[i] = mlastData[i];
            }

        }

    }

    private class MovingAverage {
        private float circularBuffer[];
        private float mean;
        private int circularIndex;
        private int count;

        public MovingAverage(int size) {
            circularBuffer = new float[size];
            reset();
        }

        /**
         * Get the current moving average.
         */
        public float getValue() {

			/*
			 *
			 * for (int i = 0; i < circularBuffer.length; i++) {
			 * Log.i("circularBuffer"+i,""+circularBuffer[i]); }
			 *
			 * float max=circularBuffer[0]; float min=circularBuffer[0];
			 *
			 * for (int i = 1; i < circularBuffer.length; i++) {
			 * if(circularBuffer[i]>max){ max=circularBuffer[i]; }
			 * if(circularBuffer[i]<min){ min=circularBuffer[i]; }
			 *
			 * }
			 */

			/*
			 * float sumSin = 0f; float sumCos = 0f; for (int i = 0; i <
			 * circularBuffer.length; i++) { sumSin +=
			 * Math.sin(circularBuffer[i]); sumCos +=
			 * Math.cos(circularBuffer[i]); } return (float) Math.atan2(sumSin /
			 * ((float)circularBuffer.length), sumCos /
			 * ((float)circularBuffer.length));
			 */
            int isOneRotatedRight = 0;
            int isOneRotatedLeft = 0;

            float copy[] = Arrays.copyOf(circularBuffer, circularBuffer.length);

            for (int i = 0; i < copy.length; i++) {
                if (copy[i] < 90.0f)
                    isOneRotatedRight++;
                if (copy[i] > 270.0f)
                    isOneRotatedLeft++;
            }

            if ((isOneRotatedRight > 0 && isOneRotatedRight != copy.length)
                    || (isOneRotatedLeft > 0 && isOneRotatedLeft != copy.length)) {
                for (int i = 0; i < copy.length; i++) {
                    if (copy[i] < 90.0)
                        copy[i] = 360.0f + copy[i];
                }

                float total = 0f;
                for (float x : copy) {
                    total += x;
                }
                float avg = total / ((float) copy.length);

                if (avg > 360f)
                    return avg;
                else
                    return avg - 360f;

            } else {
                return mean;
            }

            /**
             * float max = Math.max(circularBuffer[0],
             * Math.max(circularBuffer[1], circularBuffer[2])); float min =
             * Math.min(circularBuffer[0], Math.min(circularBuffer[1],
             * circularBuffer[2]));
             *
             * if ((max - min) > 180.0f) return (circularBuffer[1] +
             * circularBuffer[2]) / 2.0f;
             *
             * return mean;
             */

            /**
             * float total = 0f; for (float x : circularBuffer) { total += x; }
             * float avg = total / ((float) circularBuffer.length);
             *
             * return avg;
             */
            /**
             * float medoids[] = new float[circularBuffer.length]; for (int i =
             * 0; i < copy.length; i++) { medoids[i] = 0; for (int j = 0; j <
             * copy.length; j++) { if (i != j) { medoids[i] += Math.abs(copy[i]-
             * copy[j]); } } } float minMedoid = medoids[0]; int index = 0; for
             * (int i = 0; i < medoids.length; i++) { if (medoids[i] <
             * minMedoid) { minMedoid = medoids[i]; index = i; } }
             */
            //
            // Log.i("me", copy[index] + "");
            // Log.i("tekon", "" + copy[0] + "," + copy[1] + ","
            // + copy[2] + "," + copy[3] + ","
            // + copy[4]);
            //
            // Log.i("raw", "" + circularBuffer[0] + "," + circularBuffer[1] +
            // ","
            // + circularBuffer[2] + "," + circularBuffer[3] + ","
            // + circularBuffer[4]);
            //
            // Log.i("medoids", "" + medoids[0] + "," + medoids[1] + ","
            // + medoids[2] + "," + medoids[3] + "," + medoids[4]);
            // return copy[index];
            // Median med = new Median();
            // float copy[]=Arrays.copyOf(circularBuffer,
            // circularBuffer.length);
            // Arrays.sort(copy);

            // float total = 0f;
            // for (float x : circularBuffer) {
            // total += 1.0f / x;
            // }
            // return 1.0f / ((1.0f / ((float) circularBuffer.length)) * total);
            // return mean;

            // }
            //
            // return (float) med.evaluate(copy);
            // return transformForierReal();

            /**
             * int isOneRotatedRight=0; int isOneRotatedLeft=0;
             *
             * float copy[]=Arrays.copyOf(circularBuffer,circularBuffer.length);
             * for (int i = 0; i < circularBuffer.length; i++){ copy[i] =
             * circularBuffer[i];
             *
             * }
             *
             * for (int i = 0; i < copy.length; i++){ if(copy[i]<90.0)
             * isOneRotatedRight++; if(copy[i]>270.0) isOneRotatedLeft++; }
             *
             * if(isOneRotatedRight>0 || isOneRotatedLeft>0){ for (int i = 0; i
             * < copy.length; i++){ if(copy[i]<90.0) copy[i]=360.0f+copy[i]; } }
             *
             *
             * // float total = 0f; // for (float x : copy) { // total += 1.0f /
             * x; // } // return 1.0f / ((1.0f / ((float) copy.length)) *
             * total);
             *
             *
             * float total = 0f; for (float x : copy) { total += x; } float
             * avg=total/ ((float) copy.length);
             *
             * return avg;
             */

        }

        /**
         * // do public float transformForierReal() {
         * <p>
         * double copy[] = new double[circularBuffer.length]; for (int i = 0; i
         * < circularBuffer.length; i++) copy[i] = circularBuffer[i];
         * <p>
         * final double[] x = copy; // createRealData(n);
         * <p>
         * FastFourierTransformer fft = new FastFourierTransformer(
         * DftNormalization.STANDARD); try { Complex[] actual = fft.transform(x,
         * TransformType.FORWARD); double f[] = new double[actual.length]; int i
         * = 0; for (Complex c : actual) { // c.getImaginary(); f[i++] =
         * c.getReal(); }
         * <p>
         * float total = 0f; for (double y : f) { total += 1.0 / y; }
         * <p>
         * return ((float) (1.0 / ((1.0 / ((float) f.length)) * total)));
         * <p>
         * } catch (MathIllegalArgumentException e) { // Expected behaviour
         * return 0; }
         * <p>
         * }
         */

        public void pushValue(float x) {
            if (count++ == 0) {
                primeBuffer(x);
            }
            float lastValue = circularBuffer[circularIndex];
            mean = mean + (x - lastValue) / ((float) circularBuffer.length);
            circularBuffer[circularIndex] = x;
            circularIndex = nextIndex(circularIndex);
        }

        public void reset() {
            count = 0;
            circularIndex = 0;
            mean = 0;
        }

        public long getCount() {
            return count;
        }

        private void primeBuffer(float val) {
            for (int i = 0; i < circularBuffer.length; ++i) {
                circularBuffer[i] = val;
            }
            mean = val;
        }

        private int nextIndex(int curIndex) {
            if (curIndex + 1 >= circularBuffer.length) {
                return 0;
            }
            return curIndex + 1;
        }
    }
}
