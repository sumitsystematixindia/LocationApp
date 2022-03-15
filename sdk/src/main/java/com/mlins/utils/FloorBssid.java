package com.mlins.utils;

import com.mlins.ndk.wrappers.NdkLocationFinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FloorBssid {
    private List<String> wifiList = new ArrayList<String>();
    private int floornumber = -10;
    private int Counter = -1;

    public FloorBssid() {

    }

    public FloorBssid(int floor) {
        floornumber = floor;
    }

    public FloorBssid(int floor, List<String> list) {
        floornumber = floor;
        wifiList = list;
    }

    public void loadwifilist() {

        if (floornumber == -10) {
            return;
        }

        //XXX NDK
        //AsociativeMemoryLocator.getInstance();
        //String filename = AsociativeMemoryLocator.getFileName();
        String filename = NdkLocationFinder.getInstance().getFileName();

        String floor = String.valueOf(floornumber);
        File floordir = new File(PropertyHolder.getInstance().getFacilityDir(), floor);
        File file = new File(floordir, filename);

        if (!file.exists()) {
            return;
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = in.readLine()) != null) {
                String[] vals = line.split("\t");
                if (vals.length >= 3 && vals[2] != null) {
                    String bssid = vals[2];
                    if (!wifiList.contains(bssid)) {
                        wifiList.add(bssid);
                    }
                }

            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                in = null;
            }
        }

    }

    public List<String> getWifiList() {
        return wifiList;
    }

    public void setWifiList(List<String> wifiList) {
        this.wifiList = wifiList;
    }

    public int getFloornumber() {
        return floornumber;
    }

    public void setFloornumber(int floornumber) {
        this.floornumber = floornumber;
    }

    public int getCounter() {
        return Counter;
    }

    public void setCounter(int counter) {
        Counter = counter;
    }
}
