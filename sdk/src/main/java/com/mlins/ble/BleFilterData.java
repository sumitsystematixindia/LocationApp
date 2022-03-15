package com.mlins.ble;

import java.util.ArrayList;
import java.util.List;

public class BleFilterData {
    private static int filterSize = 5;
    private String id = "";
    private List<Integer> lastLevels = new ArrayList<Integer>();

    public BleFilterData(String bssid) {
        setId(bssid);
    }

    public int getFilteredLevel(int level) {
        int result = level;
        lastLevels.add(level);
        int filtersum = 0;
        if (lastLevels.size() > filterSize) {
            lastLevels.remove(0);
        }
        if (lastLevels.size() > 0) {
            for (Integer o : lastLevels) {
                filtersum += o;
            }

            result = filtersum / lastLevels.size();
        }
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
