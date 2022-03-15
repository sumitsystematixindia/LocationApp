package com.mlins.ndk.wrappers;


import com.mlins.wireless.WlBlip;

public class NdkFloorSelector {


    private static NdkFloorSelector instance = null;

    public static NdkFloorSelector getInstance() {
        if (instance == null) {
            instance = new NdkFloorSelector();

        }
        return instance;

    }

    public native void initParams(String appDirPath, int locationCloseRange,
                                  int k, float pixelsToMeter, int averageRange, String ssidfilter[],
                                  int floorcount, String scanType,
                                  float closeDevicesThreshold, float closeDeviceWeight, int kTopLevelThr, int levelLowerBound);


    public native void load(String facility, int floor, boolean isBin);


    public native int getFloor(WlBlip blips[], boolean isFirstTime);

    public native void saveBin();


}
