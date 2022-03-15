package com.mlins.ndk.wrappers;

public class NdkShortPathCalculator {


    public native void findBestEnterAndExist(
            FLocation origin,
            FLocation dest,
            FLocation[] originExits,
            FLocation[] destExits,
            FLocation selectedOriginExit,
            FLocation selectedDestExit,
            double p2mOrigin,
            double p2mDest);

}
