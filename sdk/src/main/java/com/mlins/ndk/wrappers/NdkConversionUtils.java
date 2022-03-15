package com.mlins.ndk.wrappers;

public class NdkConversionUtils {

    public NdkConversionUtils() {
        super();
    }

    public native void convertPoint(NdkLocation point, double tlLon, double tlLat,
                                    double trLon, double trLat, double blLon, double blLat,
                                    double brLon, double brLat, double widthPixels, double heightPixels,
                                    double rotationAngle, NdkLocation covertedPoint);

    public native void convertLatLonPoint(NdkLocation point, double tlLon, double tlLat,
                                          double trLon, double trLat, double blLon, double blLat,
                                          double brLon, double brLat, double widthPixels, double heightPixels,
                                          double rotationAngle, NdkLocation covertedPoint);

}
