package com.mlins.screens;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.mlins.ble.BleScanner;
import com.mlins.ibeacon.IBeaconScanner;
import com.mlins.orientation.OrientationMonitor;
import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.IResultReceiver;
import com.mlins.wireless.WlBlip;
import com.mlins.wireless.WlScanner;

import java.util.ArrayList;
import java.util.List;

public abstract class ScanningActivity extends BaseActivity implements
        IResultReceiver {

    public static final int BACKGROUND_BEHAVIOR_STOP = 0;
    public static final int BACKGROUND_BEHAVIOR_PAUSE = 1;
    public static final int BACKGROUND_BEHAVIOR_SCAN = 2;
    protected boolean isProssesing = false;
    private int mBackgroundBehavior = BACKGROUND_BEHAVIOR_STOP;
    private int mCount = 0;
    private int mInterval;
    private boolean eventOnUi = true;
    private Object lock = new Object();

    private Handler mHandler = new Handler();
    private List<WlBlip> results = new ArrayList<WlBlip>();
    private Runnable mUpdateClockTask = new Runnable() {
        public void run() {

            if (!isProssesing) {
                updateClock();
                isProssesing = false;
            }
            mHandler.postDelayed(mUpdateClockTask, 700);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_BLE_SCAN) {
            requestBTScan();
            Log.d(TAG, "onCreate: MODE_BLE_SCAN");
        }

        if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_RADIUS_SCAN) {
            IBeaconScanner.initBluetoothAdapter(getApplicationContext());
            IBeaconScanner.getInstance().startScanning();
            Log.d(TAG, "onCreate: MODE_RADIUS_SCAN");
        }

        if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_APRIL_SCAN) {
            Log.d(TAG, "onCreate: MODE_RADIUS_SCAN");
//			AprilBeaconScanner.getInstance().startScanning(this);
        }
    }

    @Override
    protected void onPause() {
        if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_BLE_SCAN) {
            BleScanner.getInstance().unsubscibeForResults(this);
           // Log.d(TAG, "onCreate: MODE_BLE_SCAN");
        }

        if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_RADIUS_SCAN) {
            IBeaconScanner.getInstance().unsubscibeForResults(this);
           // Log.d(TAG, "onCreate: MODE_RADIUS_SCAN");
        }

        if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_APRIL_SCAN) {
//			AprilBeaconScanner.getInstance().unsubscibeForResults(this);
        }

        switch (mBackgroundBehavior) {
            case BACKGROUND_BEHAVIOR_SCAN: // do nothing
                break;
            case BACKGROUND_BEHAVIOR_PAUSE: // just pause scanner.
                WlScanner.getInstance().pause();
                break;

            default: // Stop.
                WlScanner scanner = WlScanner.getInstance();
                scanner.stop();
                scanner.unsubscibeForResults(this);
                break;
        }
        super.onPause();

        if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_WIFI_SCAN /*&&
                android.os.Build.VERSION.SDK_INT >= PropertyHolder.KITKAT_VERSION*/
                && PropertyHolder.getInstance().isUseKitKatVersionSolution()) {

            mHandler.removeCallbacks(mUpdateClockTask);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_BLE_SCAN) {
            BleScanner.getInstance().subscribeForResults(this);
        }

        if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_RADIUS_SCAN) {
            IBeaconScanner.getInstance().subscribeForResults(this);
        }

        if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_APRIL_SCAN) {
//			AprilBeaconScanner.getInstance().subscribeForResults(this);
        }

        switch (mBackgroundBehavior) {
            case BACKGROUND_BEHAVIOR_STOP: // subscribe
                WlScanner scanner = WlScanner.getInstance();
                scanner.subscribeForResults(this);
                if (mCount < 0) {
                    scanner.scan(mCount, mInterval);
                }
                break;
            case BACKGROUND_BEHAVIOR_PAUSE: // resume
                WlScanner.getInstance().resume();
                break;
            // default: do nothing, invalid behaviors will not receive results after
            // resume.
        }

        if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_WIFI_SCAN /*&&
				android.os.Build.VERSION.SDK_INT >= PropertyHolder.KITKAT_VERSION*/
                && PropertyHolder.getInstance().isUseKitKatVersionSolution()) {
            mHandler.removeCallbacks(mUpdateClockTask);
            mHandler.postDelayed(mUpdateClockTask, 100);
        }
    }

    @Override
    protected void onDestroy() {

        // if (PropertyHolder.getInstance().getScannerMode() ==
        // PropertyHolder.MODE_BLE_SCAN) {
        // BleScanner.releaseInstance();
        // }


        WlScanner scanner = WlScanner.getInstance();
        // scanner.stop();
        scanner.unsubscibeForResults(this);
        WlScanner.releaseInstance();

        OrientationMonitor.releaseInstance();

        super.onDestroy();
    }

    @Override
    public void onRecieve(final List<WlBlip> results) {
        if (eventOnUi) {
            // on ui thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onResultsDelivered(results);
                }
            });
        } else {
            // on background thread
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    onResultsDelivered(results);

                }
            });
            t.start();

        }

    }

    private void updateClock() {
        try {

            if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_WIFI_SCAN) {
                WlScanner.getInstance().stop();
                requestScan(-1, 0, 0);
                //results = WlScanner.getInstance().getCurrentResult();
                results = null;
                //System.out.println(results);
                onResultsDelivered(results);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean isEventOnUi() {
        return eventOnUi;
    }

    public void setEventOnUi(boolean eventOnUi) {
        this.eventOnUi = eventOnUi;
    }

    public void requestScan(int count, int interval, int delay) {
        WlScanner.getInstance().scan(count, interval, delay);
        mCount = count;
        mInterval = interval;
    }

    public void requestBTScan() {
        BleScanner.getInstance().startScanning(this);
    }

    public List<WlBlip> getLastResults() {
        return WlScanner.getInstance().getLastResults();
    }

    public int setBackgroundBehavior(int behavior) {
        mBackgroundBehavior = behavior;
        return mBackgroundBehavior;
    }

    abstract protected void onResultsDelivered(List<WlBlip> results);

}