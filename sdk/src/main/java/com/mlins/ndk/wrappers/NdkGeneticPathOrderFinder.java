package com.mlins.ndk.wrappers;

public class NdkGeneticPathOrderFinder {

    public native void getPoisOrder(String switchFloorFilePath, int generationsCount, FLocation origin, FLocation[] poisDestList, FLocation[] fullSolutionPath);

    public native void getMixedPoisOrder(String switchFloorFilePath, int generationsCount, FLocation origin, FLocation[] poisDestList, FLocation[] poisExitsList, FLocation[] fullSolutionPath);

    public native void getMultiFacilitiesPoisOrder(String appDir, String projectId, String campusId, int generationsCount, FLocation origin, FLocation[] poisDestList, FLocation[] facilitiesCenters, FLocation[] fullSolutionPath);


}
