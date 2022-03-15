package com.mlins.ble;

import static android.content.ContentValues.TAG;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.LongDef;

import com.mlins.screens.ScanningActivity;
import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.IResultReceiver;
import com.mlins.wireless.Scannable;
import com.mlins.wireless.WlBlip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("MissingPermission")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleScanner implements Cleanable, Scannable {
    private static final String ENCRYPTED_UUID = "b2dd3555-ea39-4f08-862a-00fb026a800b";
    private static final int ENCRYPTED_INTEVAL = 19;
    private static final String HEXES = "0123456789ABCDEF";
    private static final int REQUEST_ENABLE_BT = 1200;
    private static final String spreoPrefix = "spreo";
    private static final String centrakPrefix = "centrak";

    HashMap<String, WlBlip> bleBlips = new HashMap<>();
    List<IResultReceiver> listeneres = new ArrayList<>();
    private BleThread bleThread = null;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mBleScanning = false;
    private boolean initialized = false;
    private ScanningActivity activity = null;
    private List<WlBlip> BleBlips = new ArrayList<>();
    private HashMap<String, BleFilterData> bleFilterMap = new HashMap<>();
    private List<String> uuidFilter = new ArrayList<>();
    // Device scan callback.
    private ScanCallback scanCallback;
    private int counter = 0;

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            onScan(device, rssi, scanRecord);

        }
    };

    private void onScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
        boolean uuidscan = PropertyHolder.getInstance().isUuidScan();

        if (uuidscan) {

            String uuid = getUuid(scanRecord);
            String major = getMajor(scanRecord);
            String minor = getMinor(scanRecord);

            if (uuid != null && major != null && minor != null) {
                boolean isspreo = isSpreoUuid(uuid);
                if (isspreo) {
                    String BSSID = major + "__" + minor;
                    int frequency = 2;
                    long time = System.currentTimeMillis();
                    WlBlip blip = new WlBlip(BSSID, BSSID, rssi, frequency, time);
                    bleBlips.put(BSSID, blip);
                } else if (isEncryptedUuid(uuid)) {
                    int minorint = Integer.parseInt(minor, 16);
                    String realminor = Integer.toHexString(minorint % ENCRYPTED_INTEVAL);
                    int majorint = Integer.parseInt(minor, 16);
                    String realmajorint = Integer.toHexString(majorint % ENCRYPTED_INTEVAL);
                    String BSSID = realmajorint + "__" + realminor;
                    int frequency = 2;
                    long time = System.currentTimeMillis() / 1000L;
                    WlBlip blip = new WlBlip(BSSID, BSSID, rssi, frequency, time);
                    bleBlips.put(BSSID, blip);
                }
            }
        } else {
            String BSSID = device.getName();

            if (BSSID != null) {
                String SSID = device.getAddress();
                boolean isspreo = isSpreo(BSSID, SSID);
                if (isspreo) {
                    int frequency = 2;
                    long time = System.currentTimeMillis();
                    WlBlip blip = new WlBlip(SSID, BSSID, rssi, frequency, time);
                    bleBlips.put(BSSID, blip);
                }
            }
        }
    }

    public BleScanner() {
        if (PropertyHolder.getInstance().isUuidScan()) {
            //uuidFilter.add("B2DD3555EA394F08862A00FB026A800B");
            List<String> uuids = PropertyHolder.getInstance().getUuidList();
            if (uuids != null && uuids.size() > 0) {
                uuidFilter = uuids;
                Log.d(TAG, "BleScanner: "+uuids);
            }
        }
    }

    public static BleScanner getInstance() {
        return Lookup.getInstance().get(BleScanner.class);
    }

    public void clean(){
        stopScanning();
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(BleScanner.class);
    }

    public static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public void bleInit() {
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Log.d(TAG, "onScanResult: bleInit");
                        onScan(result.getDevice(), result.getRssi(), result.getScanRecord() == null ? null : result.getScanRecord().getBytes());
                    }
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        for (ScanResult result : results) {
                            Log.d(TAG, "onBatchScanResults: bleInit");
                            onScan(result.getDevice(), result.getRssi(), result.getScanRecord() == null ? null : result.getScanRecord().getBytes());
                        }
                    }
                }
            };
        }
        // Ensures Bluetooth is available on the device and it is enabled. If
        // not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void startSingleScan() {
        if (!mBleScanning) {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                Log.d(TAG, "run:startSingleScan ");
            }
            mBleScanning = true;
        }
    }

    public void stopSingleScan() {
        if (mBleScanning) {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                Log.d(TAG, "stopSingleScan: ");
            }
            mBleScanning = false;
        }
    }

    public void startScanning(ScanningActivity act) {
        setActivity(act);
        subscribeForResults(act);
      //  BleScanner();
        Log.d(TAG, "startScanning: ");
        if (!initialized) {
            bleInit();
            bleThread = new BleThread(this);
            bleThread.start();
            initialized = true;
        }
    }

    public void startScanning() {
        if (!initialized) {
            //bleInit();
            bleThread = new BleThread(this);
            bleThread.start();
            initialized = true;
        }
    }

    public void setBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public void stopScanning() {
        bleThread.stopThread();
        stopSingleScan();
        bleBlips.clear();
        listeneres.clear();
        setInitialized(false);
    }

    public void deliverResults() {
        boolean filterEnabled = PropertyHolder.getInstance().isBleLevelfilter();
        List<WlBlip> result = new ArrayList<>();
        List<WlBlip> temp = new ArrayList<>();
        temp.addAll(bleBlips.values());
        long time = System.currentTimeMillis();
        for (WlBlip o : temp) {
            if (time - o.timestamp < 3000) {
                if (filterEnabled) {
                    BleFilterData filterdata;
                    if (bleFilterMap.containsKey(o.BSSID)) {
                        filterdata = bleFilterMap.get(o.BSSID);
                    } else {
                        filterdata = new BleFilterData(o.BSSID);
                        bleFilterMap.put(o.BSSID, filterdata);
                    }
                    if (filterdata != null) {
                        o.level = filterdata.getFilteredLevel(o.level);
                        result.add(o);
                    }
                } else {
                    result.add(o);
                }
            } else {
                if (filterEnabled) {
                    if (bleFilterMap.containsKey(o.BSSID)) {
                        bleFilterMap.remove(o.BSSID);
                    }
                }
            }
        }


        counter++;
      // result = getBeaconList();

        setBleBlips(result);

        for (IResultReceiver o : listeneres) {
            o.onRecieve(result);
        }
    }

    private List<WlBlip> getBeaconList() {
        List<WlBlip> result = new ArrayList<>();
        List<String> tmp = new ArrayList<>();
        tmp.add("838B__8AE0");
        tmp.add("C65E__37B2");
        tmp.add("D42A__957A");
        tmp.add("9606__50E1");
        tmp.add("E015__2764");
        tmp.add("9C48__56F5");
        tmp.add("57F4__CEE5");
        tmp.add("3630__E651");
        tmp.add("8C3B__7C8D");
        tmp.add("DF18__ED1B");
        tmp.add("FA13__4330");
        tmp.add("E60D__79FF");
        tmp.add("57E6__C8E4");


        int start = 10;

        if (counter > start) {
            int inc = (int)((counter - start) / 2);
               if (!(inc > tmp.size() - 2)) {
                   int startlevel = -70;
                   for(int i = 0; i < 3; ++i) {
                       result.add(new WlBlip(tmp.get(inc - 1 + i) , tmp.get(inc - 1 + i) , startlevel - (i * 10), 0, 0));
                   }
               } else {
                   counter = 0;
               }
        }

        return result;
    }

    private boolean isEncryptedUuid(String uuid) {
        boolean result = false;
        if (uuid != null) {
            if (uuid.equalsIgnoreCase(ENCRYPTED_UUID)) {
                result = true;
            }
        }
        return result;
    }

    public boolean isSpreo(String name, String address) {
        if (name.toLowerCase().startsWith(centrakPrefix)) {
            return true;
        }
        if (name.toLowerCase().startsWith(spreoPrefix)) {
            return true;
        }
        String tmpname = name;
        String covertedaddress = convertAddressToName(address);
        if (!tmpname.isEmpty() && tmpname.length() < covertedaddress.length()) {
            tmpname = new StringBuilder(tmpname).insert(tmpname.length() - 1, "0").toString();
        }
        return !TextUtils.isEmpty(covertedaddress) && covertedaddress.startsWith(tmpname);
    }

    private String convertAddressToName(String address) {
        String[] items = address.split(":");
        List<String> list = new ArrayList<>();
        Collections.addAll(list, items);
        Collections.reverse(list);
        return TextUtils.join("", list);
    }

    public String getUuid(byte[] raw) {
        if (raw == null || raw.length < 24) {
            return null;
        }
        byte[] uuid = new byte[16];
        System.arraycopy(raw, 9, uuid, 0, 16);
        return getHex(uuid);
    }

    public String getMajor(byte[] raw) {
        if (raw == null || raw.length < 27) {
            return null;
        }
        byte[] majorbytes = new byte[2];
        majorbytes[0] = raw[25];
        majorbytes[1] = raw[26];
        return getHex(majorbytes);
    }

    public String getMinor(byte[] raw) {
        if (raw == null || raw.length < 29) {
            return null;
        }
        byte[] minorbytes = new byte[2];
        minorbytes[0] = raw[27];
        minorbytes[1] = raw[28];
        return getHex(minorbytes);
    }

    public boolean isSpreoUuid(String uuid) {
        boolean result = false;
        for (String o : uuidFilter) {
            if (o.equalsIgnoreCase(uuid)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void subscribeForResults(IResultReceiver receiver) {
        if (!listeneres.contains(receiver)) {
            listeneres.add(receiver);
        }
    }

    public boolean unsubscibeForResults(IResultReceiver receiver) {
        return listeneres.contains(receiver) && listeneres.remove(receiver);
    }

    public ScanningActivity getActivity() {
        return activity;
    }

    public void setActivity(ScanningActivity activity) {
        this.activity = activity;
    }

    public List<WlBlip> getBleBlips() {
        return BleBlips;
    }

    public void setBleBlips(List<WlBlip> bleBlips) {
        BleBlips = bleBlips;
    }

    @Override
    public List<WlBlip> getBlipsList() {
        return BleBlips;
    }

    private void setInitialized(boolean ninit) {
        if (ninit != initialized) {
            initialized = ninit;
        }
    }
}
