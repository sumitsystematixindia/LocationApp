package com.mlins.locator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WIFIData {

    // window based on std dev
    static int window = 3;
    String bssid = "";
    List<WIFILevelData> data = new ArrayList<WIFILevelData>();

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public List<WIFILevelData> find(int level) {

        List<WIFILevelData> result = new ArrayList<WIFILevelData>();

        for (Iterator<WIFILevelData> iterator = data.iterator(); iterator
                .hasNext(); ) {
            WIFILevelData wifiLevelData = (WIFILevelData) iterator.next();

            int diff = Math.abs(level - wifiLevelData.level);
            if (diff < window) {
                result.add(wifiLevelData);
            }

        }

        return result;

    }

}
