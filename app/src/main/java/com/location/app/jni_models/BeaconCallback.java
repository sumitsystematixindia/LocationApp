package com.location.app.jni_models;

public class BeaconCallback {

    private static native void jni_resolve(String uuid, int major, int minor, int rssi);

    public static void resolve(String uuid, int major, int minor, int rssi){
        jni_resolve(uuid, major, minor, rssi);
    }
}
