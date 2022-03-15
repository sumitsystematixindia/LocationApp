package com.mlins.interfaces;

import java.util.List;

import proximity.ProximityObj;

public interface ProximityDetection {
    void onProximityDetected(List<ProximityObj> zones);
}
