package com.mlins.utils;

import com.mlins.wireless.WlBlip;

import java.util.HashSet;
import java.util.List;

public class FacilityBSSIDS implements Comparable<FacilityBSSIDS> {
    private String name;
    private HashSet<String> wifiSet;
    private Integer existsWifiCount;


    public FacilityBSSIDS(String name) {
        super();
        this.name = name;
        wifiSet = new HashSet<String>();
        existsWifiCount = new Integer(0);
    }

    public Integer getExistsWifiCount() {
        return existsWifiCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashSet<String> getWifiSet() {
        return wifiSet;
    }

    public boolean addBSSID(String ssid) {
        return wifiSet.add(ssid);
    }

    @Override
    public String toString() {
        return "FacilitySSIDS [name=" + name + ", wifiSet=" + wifiSet + "]";
    }

    public void setExistsWifiCount(List<WlBlip> blips, int enterlevel) {
        existsWifiCount = 0;
        for (WlBlip o : blips) {
            if (o.level > enterlevel && wifiSet.contains(o.BSSID)) {
                existsWifiCount++;
            }
        }
    }

    public int compareTo(FacilityBSSIDS o) {
        return o.existsWifiCount.compareTo(existsWifiCount);
    }
}
