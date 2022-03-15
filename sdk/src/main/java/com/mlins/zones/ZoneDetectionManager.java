package com.mlins.zones;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.mlins.enums.ScanMode;
import com.mlins.ndk.wrappers.NdkFloorSelector;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.scanners.BlipsScanner;
import com.mlins.utils.Cleanable;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.FacilitySelector;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.IResultReceiver;
import com.mlins.wireless.WlBlip;
import com.spreo.enums.BleState;
import com.spreo.enums.DetectionType;
import com.spreo.enums.Zone;
import com.spreo.interfaces.ZoneDetectionListener;

import java.util.ArrayList;
import java.util.List;

public class ZoneDetectionManager implements IResultReceiver, Cleanable {
    private static final int OUT_TAG = 1;
    private static final int IN_TAG = 0;

    // XXX NDK LOAD LIB
    static {
        System.loadLibrary("MlinsLocationFinderUtils");
    }

    private List<ZoneDetectionListener> listeners = new ArrayList<ZoneDetectionListener>();
    private BlipsScanner blipsScanner = null;
    private Context ctx = null;
    private boolean theftProtectionVaild = false;
    private int initialThresh = 5;
    private int ignoreThresh = 3;
    private int initialCounter = 0;
    private int ignoreCounter = 0;
    private boolean initialCheck = false;
    private boolean ignore = true;
    private int currentZone = OUT_TAG;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_OFF) {
                    currentZone = OUT_TAG;
                    Zone translatedZone = translateZone(currentZone);
                    if (translatedZone != null) {
                        notifyZoneDetection(translatedZone, DetectionType.BLE_OFF);
                    }
                    notifyBleState(BleState.OFF);
                } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_ON) {
                    resetIgnore();
                    notifyBleState(BleState.ON);
                }
            }

        }

    };
    private int detectedFacilityCounter = 0;
    private int detectedFacilityThresh = 3;

    public ZoneDetectionManager() {
        reloadNdkFloorSelector();
        TpManager.getInstance();
    }

    public static ZoneDetectionManager getInstance() {
        return Lookup.getInstance().get(ZoneDetectionManager.class);
    }

    public void clean(){
        stopDetection();
    }

    public void startDetection(Context context) {
        if (context != null) {
            ctx = context;
            ScanMode scanmode = ScanMode.BLE;
            BlipsScanner.getInstance().initBluetoothAdapter(ctx);
            blipsScanner = BlipsScanner.getInstance();
            blipsScanner.startScanning(scanmode);
            blipsScanner.subscribeForResults(this);

            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            ctx.registerReceiver(mReceiver, filter);
        }
    }

    public void stopDetection() {
        if (blipsScanner != null) {
            blipsScanner.stopScanning();
        }
    }

    public void startInitialDetection() {
        initialCheck = true;
        initialCounter = 0;
        detectedFacilityCounter = 0;
    }

    public boolean isTheftProtectionVaild() {
        return theftProtectionVaild;
    }

    public Zone getZone() {
        return translateZone(currentZone);
    }

    public void setZone(Zone zone) {
        if (zone != null) {
            if (zone == Zone.IN) {
                currentZone = IN_TAG;
            } else if (zone == Zone.OUT) {
                currentZone = OUT_TAG;
            }

            notifyZoneDetection(zone, DetectionType.USER_DEFINED);
        }
    }

    public void registerListener(ZoneDetectionListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unregisterListener(ZoneDetectionListener listener) {
        if (listener != null && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    @Override
    public void onRecieve(List<WlBlip> results) {
        if (ctx != null) {
            Handler mainHandler = new Handler(ctx.getMainLooper());

            Runnable myRunnable = new Runnable() {

                @Override
                public void run() {
                    ZoneDetectionRutin();
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    private void ZoneDetectionRutin() {

        List<WlBlip> results = blipsScanner.getBlips();
        boolean tmptp = TpManager.getInstance().isTp(results);
        if (tmptp != theftProtectionVaild) {
            theftProtectionVaild = tmptp;
            notifyTp();
        }

        if (ignore) {
            if (ignoreCounter < ignoreThresh) {
                ignoreCounter++;
                return;
            } else {
                ignore = false;
            }
        }

        if (initialCheck) {
            if (initialCounter < initialThresh) {
                initialCounter++;
                int zone = getInitialZone(results);
                if (zone == IN_TAG) {
                    detectedFacilityCounter++;
                }
                return;
            } else {
                int zone = OUT_TAG;
                if (detectedFacilityCounter >= detectedFacilityThresh) {
                    zone = IN_TAG;
                }

                currentZone = zone;
                Zone translatedZone = translateZone(currentZone);
                if (translatedZone != null) {
                    notifyZoneDetection(translatedZone, DetectionType.INITIAL);
                }
                initialCheck = false;
                return;
            }
        }


        WlBlip[] res = results.toArray(new WlBlip[results.size()]);
        int zone = NdkFloorSelector.getInstance().getFloor(res, false);
        if (zone >= 0 && zone != currentZone) {
            currentZone = zone;
            Zone translatedZone = translateZone(currentZone);
            if (translatedZone != null) {
                notifyZoneDetection(translatedZone, DetectionType.GATE);
            }
        }
    }

    private void notifyTp() {
        for (ZoneDetectionListener o : listeners) {
            if (o != null) {
                try {
                    o.onTheftProtectionStateChange(theftProtectionVaild);
                } catch (Throwable t) {

                }
            }
        }
    }

    private void notifyZoneDetection(Zone translatedZone, DetectionType type) {
        for (ZoneDetectionListener o : listeners) {
            if (o != null) {
                try {
                    o.onZoneDetection(translatedZone, type);
                } catch (Throwable t) {

                }
            }
        }
    }

    private void notifyBleState(BleState state) {
        for (ZoneDetectionListener o : listeners) {
            if (o != null) {
                try {
                    o.onBleStateChange(state);
                } catch (Throwable t) {

                }
            }
        }
    }

    private void resetIgnore() {
        ignoreCounter = 0;
        ignore = true;
    }

    private Zone translateZone(int zone) {
        Zone result = null;
        if (zone == IN_TAG) {
            result = Zone.IN;
        } else if (zone == OUT_TAG) {
            result = Zone.OUT;
        }
        return result;
    }

    private int getInitialZone(List<WlBlip> results) {
        int result = OUT_TAG;
        String resultid = FacilitySelector.getInstance().getFacilityByBlips(results);
        String facilityid = FacilityContainer.getInstance().getSelected().getId();
        if (resultid != null && resultid.equals(facilityid)) {
            result = IN_TAG;
        }
        return result;
    }

    private void reloadNdkFloorSelector() {

        Campus ccampus = ProjectConf.getInstance().getSelectedCampus();

        FacilityConf cfacility = FacilityContainer.getInstance().getSelected();

        if (cfacility != null) {
            int floor = cfacility.getSelectedFloor();
            String facility = cfacility.getId(); // currentDetectedFacilityId; //PropertyHolder.getInstance().getFacilityID();
            String campus = ccampus.getId(); // PropertyHolder.getInstance().getCampusId();
            String project = PropertyHolder.getInstance().getProjectId();
            String appDirPath = PropertyHolder.getInstance().getAppDir().getAbsolutePath();
            String scanType = PropertyHolder.getInstance().getMatrixFilePrefix();
            if (PropertyHolder.useZip) {
                appDirPath = PropertyHolder.getInstance().getZipAppdir().getAbsolutePath();
                scanType = "";
            }
            int locationCloseRange = cfacility.getNdkCloseRange();
            int k = PropertyHolder.getInstance().getK();
            float pixelsToMeter = cfacility.getPixelsToMeter();
            int floorcount = cfacility.getFloorDataList().size(); // FacilityConf.getInstance().getFloorDataList().size();
            int averageRange = PropertyHolder.getInstance().getAverageRange();
            ArrayList<String> filter = PropertyHolder.getInstance().getSsidFilter();
            String[] ssidfilter = filter.toArray(new String[filter.size()]);
            float closeDevicesThreshold = PropertyHolder.getInstance().getCloseDeviceThreshold();
            float closeDeviceWeight = PropertyHolder.getInstance().getCloseDeviceWeight();
            int kTopLevelThr = cfacility.getFloorsTopKlevelsThr();
            int levelLowerBound = cfacility.getFloorselectionLevelLowerBound();
            NdkFloorSelector.getInstance().initParams(appDirPath, locationCloseRange, k, pixelsToMeter, averageRange, ssidfilter, floorcount, scanType, closeDevicesThreshold, closeDeviceWeight,
                    kTopLevelThr, levelLowerBound);

            String path = project + "/" + campus + "/" + facility;
            if (PropertyHolder.useZip) {
                path = project + "/" + campus + "/facilities/" + facility + "/" + "floors";
            }

            NdkFloorSelector.getInstance().load(path, floor, true);
        }
    }
    // ================= end ndk link ==============


}
