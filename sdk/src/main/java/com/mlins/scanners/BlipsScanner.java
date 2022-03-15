package com.mlins.scanners;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mlins.ble.BleScanner;
import com.mlins.enums.ScanMode;
import com.mlins.ibeacon.IBeaconScanner;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.logging.Log;
import com.mlins.wireless.IResultReceiver;
import com.mlins.wireless.Scannable;
import com.mlins.wireless.WlBlip;
import com.mlins.wireless.WlScanner;

import java.util.List;

public class BlipsScanner {
    private final static String TAG = "com.mlins.scanners.BlipsScanner";

    private BluetoothAdapter bluetoothAdapter = null;
    private Scannable scanner = null;

    public static BlipsScanner getInstance() {
        return Lookup.getInstance().get(BlipsScanner.class);
    }

    // public void init(ScanMode scanMode) {
    // //if (PropertyHolder.getInstance().getScannerMode() ==
    // PropertyHolder.MODE_BLE_SCAN) {
    // if(scanMode==ScanMode.BLE){
    // scanner = BleScanner.getInstance();
    // //} else if (PropertyHolder.getInstance().getScannerMode() ==
    // PropertyHolder.MODE_RADIUS_SCAN) {
    // }else if(scanMode==ScanMode.RADIOUS_IBEACON){
    // scanner = IBeaconScanner.getInstance();
    // }else if(scanMode==ScanMode.WIFI){
    // //} else if (PropertyHolder.getInstance().getScannerMode() ==
    // PropertyHolder.MODE_WIFI_SCAN) {
    // scanner = WlScanner.getInstance();
    // }
    // }

    @SuppressLint("NewApi")
    public void initBluetoothAdapter(Context ctx) {
        Log.getInstance().debug(TAG, "Enter, initBluetoothAdapter()");

        if (android.os.Build.VERSION.SDK_INT < 18) {
            Toast.makeText(ctx, "Sorry: Bluetooth/IBeacon is not supported by your device", Toast.LENGTH_LONG).show();
            return;
        }
        bluetoothAdapter = null;
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) ctx.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(enableBtIntent);
        }
        Log.getInstance().debug(TAG, "Exit, initBluetoothAdapter()");
    }

    public void startScanning(ScanMode scanMode) {
        Log.getInstance().debug(TAG, "Enter, startScanning()");

        if (scanMode == ScanMode.BLE) {
            PropertyHolder.getInstance().setBleLevelfilter(true);
            scanner = BleScanner.getInstance();
            BleScanner.getInstance().setBluetoothAdapter(bluetoothAdapter);
            scanner.startScanning();

        } else if (scanMode == ScanMode.RADIOUS_IBEACON) {

            scanner = IBeaconScanner.getInstance();
            scanner.startScanning();
        } else if (scanMode == ScanMode.APRILBEACON) {
//			PropertyHolder.getInstance().setBleLevelfilter(true);
//			scanner = AprilBeaconScanner.getInstance();
//			AprilBeaconScanner.getInstance().setBluetoothAdapter(bluetoothAdapter);
//			scanner.startScanning();
        } else {

            scanner = WlScanner.getInstance();
            WlScanner.getInstance().scan(-1, 0, 0);

        }
        Log.getInstance().debug(TAG, "Exit, startScanning()");
    }

    public void stopScanning() {
        if (scanner != null) {
            scanner.stopScanning();
        }
    }

    public List<WlBlip> getBlips() {
        return scanner.getBlipsList();
    }

    public void subscribeForResults(IResultReceiver receiver) {
        scanner.subscribeForResults(receiver);
    }

    public boolean unsubscibeForResults(IResultReceiver receiver) {
        return scanner.unsubscibeForResults(receiver);
    }

}
