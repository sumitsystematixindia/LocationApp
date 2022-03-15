package com.mlins.ndk.wrappers;

import com.mlins.wireless.WlBlip;

public class NdkLocationFinder {

    private static NdkLocationFinder instance;

    //doesn't hold any state, no need to rework
    public static NdkLocationFinder getInstance() {
        if (instance == null) {
            instance = new NdkLocationFinder();
        }

        return instance;
    }

    public native void initParams(String appDirPath, int locationCloseRange,
                                  int k, float pixelsToMeter, int averageRange, String ssidfilter[],
                                  int floorcount, String scanType,
                                  float closeDevicesThreshold, float closeDeviceWeight, int kTopLevelThr);


    public native void load(String facility, int floor, boolean isBin);

    public native void findLocation(WlBlip blips[], FLocation loc, boolean isFirstTime);

    public native void getLastpt(FLocation loc);

    public native void saveBin();

    public native void resetLastpt();

    public native String getFileName();

    public native void getPointsList(FLocation pointsList[]);

    public native int getPointsCount();

    public native void getSsidnames(String list[]);

    public native int getSssidnamesCount();

    //XXX NDK
//	// load native lib
//	static {
//		System.loadLibrary("MlinsLocationFinderUtils");
//	}

    // === mb zone group finder
    public native void initZoneGroupFinder(String zoneGroupsFilePath);

    public native int findZoneGroup(WlBlip blips[]);
    // === end zone group finder


    public native String getGroupIdByBlips(WlBlip blips[]);

    public native void findLocationInsideGeofence(WlBlip blips[], FLocation loc, float topLeftX, float topLeftY, float bottomRightX, float bottomRightY);

}
