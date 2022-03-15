package com.location.app.jni_models;

public class Trilateration {

    private static native void jni_startTrilateration();

    private static native void jni_stopTrilateration();

    public static void startTrilateration(){
        jni_startTrilateration();
    }

    public static void stopTrilateration(){
        jni_stopTrilateration();
    }
}
