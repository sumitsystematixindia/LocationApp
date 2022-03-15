package com.mlins.screens;

import com.mlins.ble.BleScanner;
import com.mlins.ibeacon.IBeaconScanner;
import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.WlBlip;
import com.mlins.wireless.WlScanner;

import java.util.List;

public class BlipsHolder {

    public static List<WlBlip> getLastResults() {
        List<WlBlip> results = null;
        if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_BLE_SCAN) {
            results = BleScanner.getInstance().getBleBlips();

        } else if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_RADIUS_SCAN) {

            results = IBeaconScanner.getInstance().getIBeaconBlips();
        } else if (PropertyHolder.getInstance().getScannerMode() == PropertyHolder.MODE_APRIL_SCAN) {
//			results = AprilBeaconScanner.getInstance().getBleBlips();

        } else {
            results = WlScanner.getInstance().getLastResults();
        }
        return results;
    }
}
