package com.mlins.wireless;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the WiFi scanning facilities, necessary for the application,
 * through a single point of contact to the system.
 * Use {@link #getInstance(Context)} to obtain an handle to the WlScanner.
 *
 * @author OddN
 */

/* package */ final class WlScannerImpl implements IScanner, Cleanable {

    private static final boolean LOG = true; //BuildConfig.DEBUG; // BEWARE: may not work see https://code.google.com/p/android/issues/detail?id=27940
    private static final String TAG = "WlScanner";
    private static final int AVERAGE_FILTER_SIZE = 5;
    private static final float LOW_PASS_FILTER_ALFA = 0.85f;

    private final Runnable RUN_SCAN = new Runnable() {
        @Override
        public void run() {
            requestScan();
        }
    };

    private Context mContext;
    private WifiManager mWifiMan;
    private Handler mScheduler = new Handler();
    private int mCount;
    private int mInterval;
    private int mWifiSavedState = -1;
    private long mNextScan;
    private boolean mExpectResults = false;
    private WifiLock mLock;
    private List<IResultReceiver> mClients = new ArrayList<IResultReceiver>();
    private List<String> mAddedSsids = new ArrayList<String>();
    private List<WlBlip> currentResult = new ArrayList<WlBlip>();
    private ResultsFilter mWindowFilter = null; //new WindowFilter();
    private List<ResultsFilter> mFilters;
    private final BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //if (isInitialStickyBroadcast()) return; // WiFi state is sticky. yuck!
            String action = intent.getAction();
            if (mExpectResults && action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION || action.equalsIgnoreCase("android.net.wifi.SCAN_RESULTS")) {
                handleScanResults();
            } else if (mCount != 0 && action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
                handleWifiStateChange(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1));
            }
        }
    };


    //ON:	Using the "Singleton" is bad enough, doing it this way is even worse. We'll suffer.
    private WlScannerImpl(Context ctxt) {
        mContext = ctxt;
        mWifiMan = (WifiManager) ctxt.getSystemService(Context.WIFI_SERVICE);
        mLock = mWifiMan.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, TAG);
        if (mWindowFilter != null) {
            setFilter(0, mWindowFilter);
        }
    }

    public void clean(){
        stop();

        if(mLock != null)
            mLock.release();

        try {
            mContext.unregisterReceiver(mReciever);
        } catch(RuntimeException e) {
        }


    }

    /**
     * Retrieve an handle to the single WlScaner object.
     *
     * @param ctxt Context of the caller.
     * @return WlScaner instance.
     */

    //need to clean up because instance might hold references to listeners
    public static WlScannerImpl getInstance(Context ctxt) {
        WlScannerImpl sInstance = Lookup.getInstance().lookup(WlScannerImpl.class);
        if (sInstance == null && ctxt != null) {
            sInstance = new WlScannerImpl(ctxt.getApplicationContext());
            Lookup.getInstance().put(sInstance);
        }
        return sInstance;
    }

    public List<WlBlip> getCurrentResult() {
        return currentResult;
    }

    public void setFilter(int location, ResultsFilter filter) {
        if (mFilters == null) {
            mFilters = new ArrayList<ResultsFilter>();
        }
        if (location < 0 || location >= mFilters.size()) {
            mFilters.add(filter);
        } else {
            mFilters.set(location, filter);
        }
    }

    public void removeFilter(int location) {
        if (mFilters != null && location < mFilters.size()) {
            mFilters.remove(location);
        }
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#addHidden(java.lang.String, java.lang.String)
     */
    @Override
    public int addHidden(String ssid, String bssid) {
        if (TextUtils.isEmpty(ssid)) return -1; //!\ required
        WifiConfiguration beacon = new WifiConfiguration();
        beacon.SSID = "\"" + ssid + "\"";
        beacon.BSSID = bssid;
        beacon.hiddenSSID = true;
        beacon.allowedKeyManagement.set(KeyMgmt.NONE); //!\ required
        int id = mWifiMan.addNetwork(beacon);
        if (id >= 0) {
            mAddedSsids.add(beacon.SSID);
            mWifiMan.enableNetwork(id, false);
            if (LOG) {
                //XXX Log.d(TAG, "addHidden: beacon id = " + id);
            }
        } else if (LOG) {
            //XXX Log.w(TAG, "addHidden: Failed" + beacon.toString());
        }
        return id;
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#getConfigueredBeacons()
	 */
    @Override
    public List<WifiConfiguration> getConfiguredBeacons() {
        return mWifiMan.getConfiguredNetworks();
    }

    /* (non-Javadoc)
	 * @see com.mlins.wireless.IScanner#getLastResults()
	 */
    @Override
    public List<WlBlip> getLastResults() {
        List<WlBlip> detects = new ArrayList<WlBlip>();
        for (ScanResult result : mWifiMan.getScanResults()) {
            detects.add(new WlBlip(result));
        }
        if (mFilters != null) {
            for (ResultsFilter rf : mFilters) {
                rf.filter(detects);
            }
        }
        return detects;
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#scan(int, int, int)
     */
    @Override
    public void scan(int count, int interval, int delay) {
        mCount = count;
        if (mCount != 0) {
            if (PropertyHolder.getInstance().isResultsAvarage()) {
                setFilter(1, new MovingAverageFilter(AVERAGE_FILTER_SIZE));
            } else if (PropertyHolder.getInstance().isLowPassResults()) {
                setFilter(1, new LowPassFilter(LOW_PASS_FILTER_ALFA));
            } else if (PropertyHolder.getInstance().isStepResults()) {
                setFilter(1, new StepFilter());
            } else {
                removeFilter(1);
            }

            mInterval = interval * 1000;
            mNextScan = SystemClock.uptimeMillis() + delay;
            IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            mContext.registerReceiver(mReciever, filter);
            if (!mLock.isHeld()) {
                mLock.acquire();
            }
            if (LOG) {
                //XXX Log.d(TAG, "doScan: Booking " + (count >= 0 ? count : "indefinite") + " scans at " + interval + "\" intervals.");
            }
            if (mWifiMan.isWifiEnabled()) {
                requestScan();
            } else { // XXX Should respect Airplane mode? Alternative: alert user and open WiFi settings (ACTION_WIFI_SETTINGS)
                pause();
                mWifiSavedState = mWifiMan.getWifiState();
                if (mWifiSavedState != WifiManager.WIFI_STATE_ENABLING) {
                    mWifiMan.setWifiEnabled(true);
                }
            }
        }
    }

    private void requestScan() {
        if (mNextScan > SystemClock.uptimeMillis()) {
            scanLater(mNextScan);
            return;
        }
        mExpectResults = mWifiMan.startScan();
        if (LOG) {
            //XXX Log.d(TAG, "requestScan: Initiated scan... " + (mExpectResults ? "succesful." : "fail."));
        }
    }

    private boolean scanLater(long atTime) {
        return mScheduler.postAtTime(RUN_SCAN, atTime);
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#stop()
     */
    @Override
    public void stop() {
        scan(0, 0, 0);
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#pause()
     */
    @Override
    public void pause() {
        mScheduler.removeCallbacks(RUN_SCAN);
        mExpectResults = false;
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#resume()
     */
    @Override
    public boolean resume() {
        if (mCount == 0) return false;
        mNextScan = Math.max(mNextScan, SystemClock.uptimeMillis());
        requestScan();
        return true;
    }

    private void handleWifiStateChange(int state) {
        String msg = "WiFi state %s during scan sequence, new state is: %d, scans to go: %d.";

        switch (state) {
            case WifiManager.WIFI_STATE_ENABLING:
//			do nothing expect WiFi enabled soon.
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                // WiFi needs time to recover or WifiManager.startScan() returns true but never delivers.
                // May be device specific.
                mNextScan = SystemClock.uptimeMillis() + 1200;
                resume();
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
//			There's problem with WiFi if it recurs we should bail out
                if (mWifiSavedState == state) {
                    stop();
                    //XXX  ADDED
                    scan(mCount, mInterval, 4);
                    break;
                } // else fall through...
            case WifiManager.WIFI_STATE_DISABLED:
            case WifiManager.WIFI_STATE_DISABLING:
                mWifiSavedState = state;
                mWifiMan.setWifiEnabled(true);
                break;

            default: // This should never happen!
                //XXX Log.e(TAG, String.format(msg, "UNDEFINED", state, mCount));
                stop();
                //XXX  ADDED
                scan(mCount, mInterval, 4);
                return;
        }
        //XXX Log.i(TAG, String.format(msg, "changed", state, mCount));
    }

    private void handleScanResults() {
        List<WlBlip> detects = getLastResults();

        mExpectResults = false;
        if (mCount > 0) {
            mCount--;
        }
        if (LOG) {
            //XXX Log.d(TAG, "Recieved " + detects.size() + " scan results.");
            //XXX  for (WlBlip res : detects) {
            //XXX	Log.v(TAG, res.toString());
            //XXX}
        }
        if (detects != null)
            deliverResults(detects);

        if (mCount == 0) { // We're done, cleanup.
            mLock.release();
            mContext.unregisterReceiver(mReciever);
            restoreWifiState();
        } else {
            if (mInterval > 0) {
                mNextScan += mInterval;
            }
            requestScan();
        }
    }

    private void restoreWifiState() {
        if (mWifiSavedState == WifiManager.WIFI_STATE_DISABLED
                || mWifiSavedState == WifiManager.WIFI_STATE_DISABLING) {
            mWifiMan.setWifiEnabled(false);
            mWifiSavedState = -1;
        }
    }

    private void deliverResults(List<WlBlip> detects) {
        if (PropertyHolder.getInstance().isUseKitKatVersionSolution()/*android.os.Build.VERSION.SDK_INT >= PropertyHolder.KITKAT_VERSION*/) {
            currentResult = detects;
        } else {
            for (IResultReceiver client : mClients) {
                client.onRecieve(detects);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#subscribeForResults(com.mlins.wireless.WlScanner.ResultsReceiver)
     */
    @Override
    public void subscribeForResults(IResultReceiver receiver) {
        if (!mClients.contains(receiver)) {
            mClients.add(receiver);
        }
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#unsubscibeForResults(com.mlins.wireless.WlScanner.ResultsReceiver)
     */
    @Override
    public boolean unsubscibeForResults(IResultReceiver receiver) {
        return mClients.remove(receiver);
    }

    public void RestoreConfiguration() {
        List<WifiConfiguration> cb = getConfiguredBeacons();
        if (cb != null) {
            for (WifiConfiguration b : cb) {
                if (mAddedSsids.contains(b.SSID)) {
                    mWifiMan.removeNetwork(b.networkId);
                }
            }
        }
        mWifiMan.saveConfiguration();
        mAddedSsids.clear();
    }

    public interface ResultsFilter {
        public void filter(List<WlBlip> detects);
    }

}
