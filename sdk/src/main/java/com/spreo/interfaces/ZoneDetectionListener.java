package com.spreo.interfaces;

import com.spreo.enums.BleState;
import com.spreo.enums.DetectionType;
import com.spreo.enums.Zone;

public interface ZoneDetectionListener {
    public void onZoneDetection(Zone zone, DetectionType type);

    public void onBleStateChange(BleState state);

    public void onTheftProtectionStateChange(boolean isTheftProtectionVaild);
}
