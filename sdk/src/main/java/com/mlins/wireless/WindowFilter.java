package com.mlins.wireless;

import com.mlins.wireless.WlScannerImpl.ResultsFilter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WindowFilter implements ResultsFilter {
    private static final int WINDOW = 20;
    Map<String, WlBlip> lastVals = new HashMap<String, WlBlip>();
    Set<String> keys = new HashSet<String>();

    @Override
    public void filter(List<WlBlip> detects) {
        keys.clear();
        for (WlBlip b : detects) {
            keys.add(b.BSSID);
            if (lastVals.containsKey(b.BSSID)) {
                WlBlip prev = lastVals.get(b.BSSID);
                if (Math.abs(prev.level - b.level) > WINDOW) {
                    b.level = prev.level;
                } else {
                    prev.level = b.level;
                }
            } else {
                lastVals.put(b.BSSID, b);
            }
        }
        lastVals.keySet().retainAll(keys);
    }
}