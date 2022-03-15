#include <jni.h>
#include "JNIConversionUtils.h"
#include "ConversionUtils.h"

using namespace std;


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkConversionUtils_convertPoint(
        JNIEnv *env, jobject thisObj,
        jobject point, jdouble tlLon, jdouble tlLat,
        jdouble trLon, jdouble trLat, jdouble blLon, jdouble blLat,
        jdouble brLon, jdouble brLat, jdouble widthPixels, jdouble heightPixels,
        jdouble rotationAngle, jobject covertedPoint) {

    // Get a class reference

    jclass classFLocation = env->FindClass("com/mlins/ndk/wrappers/NdkLocation");
    jfieldID midX = (env)->GetFieldID(classFLocation, "x", "D");
    jfieldID midY = (env)->GetFieldID(classFLocation, "y", "D");
    jfieldID midZ = (env)->GetFieldID(classFLocation, "z", "D");
    jfieldID midLat = (env)->GetFieldID(classFLocation, "lat", "D");
    jfieldID midLon = (env)->GetFieldID(classFLocation, "lon", "D");

    if (NULL == midX) return;
    if (NULL == midY) return;
    if (NULL == midZ) return;
    if (NULL == midLat) return;
    if (NULL == midLon) return;

    double jtlLon = (double) tlLon;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jtlLon:%f",jtlLon);

    double jtlLat = (double) tlLat;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jtlLat:%f",jtlLat);

    double jtrLon = (double) trLon;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jtrLon:%f",jtrLon);

    double jtrLat = (double) trLat;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jtrLat:%f",jtrLat);

    double jblLon = (double) blLon;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jblLon:%f",jblLon);

    double jblLat = (double) blLat;
    ////__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jblLat:%f",jblLat);

    double jbrLon = (double) brLon;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jbrLon:%f",jbrLon);

    double jbrLat = (double) brLat;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jbrLat:%f",jbrLat);

    double jwidthPixels = (double) widthPixels;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jwidthPixels:%f",jwidthPixels);

    double jheightPixels = (double) heightPixels;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jheightPixels:%f",jheightPixels);

    double jrotationAngle = (double) rotationAngle;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jrotationAngle:%f",jrotationAngle);
    // convert locPoint

    Location pointLoc;
    pointLoc.x = (double) env->GetDoubleField(point, midX);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "pointLoc.x :%f",pointLoc.x);

    pointLoc.y = (double) env->GetDoubleField(point, midY);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "pointLoc.y :%f",pointLoc.y);

    pointLoc.z = (double) env->GetDoubleField(point, midZ);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "pointLoc.z :%f", pointLoc.z);

    pointLoc.lat = (double) env->GetDoubleField(point, midLat);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "pointLoc.lat :%f", pointLoc.lat);

    pointLoc.lon = (double) env->GetDoubleField(point, midLon);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "pointLoc.lon :%f",pointLoc.lon);


    Location resPoint;


    ConversionUtils::convertPoint(pointLoc, jtlLon, jtlLat,
                                  jtrLon, jtrLat, jblLon, jblLat,
                                  jbrLon, jbrLat, jwidthPixels, jheightPixels,
                                  jrotationAngle, resPoint);


    //fill objects and return it back to java env.

    jclass FLocationClass = (env)->GetObjectClass(covertedPoint);
    jmethodID midCallSetX = (env)->GetMethodID(FLocationClass, "setX", "(D)V");
    jmethodID midCallSetY = (env)->GetMethodID(FLocationClass, "setY", "(D)V");
    jmethodID midCallSetZ = (env)->GetMethodID(FLocationClass, "setZ", "(D)V");
    jmethodID midCallSetLat = (env)->GetMethodID(FLocationClass, "setLat", "(D)V");
    jmethodID midCallSetLon = (env)->GetMethodID(FLocationClass, "setLon", "(D)V");

    if (NULL == midCallSetX) return;
    if (NULL == midCallSetY) return;
    if (NULL == midCallSetZ) return;
    if (NULL == midCallSetLat) return;
    if (NULL == midCallSetLon) return;


    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "resPoint.x :%f",resPoint.x);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "resPoint.y :%f",resPoint.y);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "resPoint.z :%f",resPoint.z);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "resPoint.lat :%f",resPoint.lat);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "resPoint.lon :%f",resPoint.lon);

    // fill
    (env)->CallVoidMethod(covertedPoint, midCallSetX, resPoint.x);
    (env)->CallVoidMethod(covertedPoint, midCallSetY, resPoint.y);
    (env)->CallVoidMethod(covertedPoint, midCallSetZ, resPoint.z);
    (env)->CallVoidMethod(covertedPoint, midCallSetLat, resPoint.lat);
    (env)->CallVoidMethod(covertedPoint, midCallSetLon, resPoint.lon);

}


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkConversionUtils_convertLatLonPoint(
        JNIEnv *env, jobject thisObj,
        jobject point, jdouble tlLon, jdouble tlLat,
        jdouble trLon, jdouble trLat, jdouble blLon, jdouble blLat,
        jdouble brLon, jdouble brLat, jdouble widthPixels, jdouble heightPixels,
        jdouble rotationAngle, jobject covertedPoint) {

    // Get a class reference

    jclass classFLocation = env->FindClass("com/mlins/ndk/wrappers/NdkLocation");
    jfieldID midX = (env)->GetFieldID(classFLocation, "x", "D");
    jfieldID midY = (env)->GetFieldID(classFLocation, "y", "D");
    jfieldID midZ = (env)->GetFieldID(classFLocation, "z", "D");
    jfieldID midLat = (env)->GetFieldID(classFLocation, "lat", "D");
    jfieldID midLon = (env)->GetFieldID(classFLocation, "lon", "D");

    if (NULL == midX) return;
    if (NULL == midY) return;
    if (NULL == midZ) return;
    if (NULL == midLat) return;
    if (NULL == midLon) return;

    double jtlLon = (double) tlLon;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jtlLon:%f",jtlLon);

    double jtlLat = (double) tlLat;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jtlLat:%f",jtlLat);

    double jtrLon = (double) trLon;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jtrLon:%f",jtrLon);

    double jtrLat = (double) trLat;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jtrLat:%f",jtrLat);

    double jblLon = (double) blLon;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jblLon:%f",jblLon);

    double jblLat = (double) blLat;
    ////__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jblLat:%f",jblLat);

    double jbrLon = (double) brLon;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jbrLon:%f",jbrLon);

    double jbrLat = (double) brLat;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jbrLat:%f",jbrLat);

    double jwidthPixels = (double) widthPixels;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jwidthPixels:%f",jwidthPixels);

    double jheightPixels = (double) heightPixels;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jheightPixels:%f",jheightPixels);

    double jrotationAngle = (double) rotationAngle;
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "jrotationAngle:%f",jrotationAngle);
    // convert locPoint

    Location pointLoc;
    pointLoc.x = (double) env->GetDoubleField(point, midX);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "pointLoc.x :%f",pointLoc.x);

    pointLoc.y = (double) env->GetDoubleField(point, midY);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "pointLoc.y :%f",pointLoc.y);

    pointLoc.z = (double) env->GetDoubleField(point, midZ);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "pointLoc.z :%f", pointLoc.z);

    pointLoc.lat = (double) env->GetDoubleField(point, midLat);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "pointLoc.lat :%f", pointLoc.lat);

    pointLoc.lon = (double) env->GetDoubleField(point, midLon);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "pointLoc.lon :%f",pointLoc.lon);


    Location resPoint;


    ConversionUtils::convertLatLonPoint(pointLoc, jtlLon, jtlLat,
                                        jtrLon, jtrLat, jblLon, jblLat,
                                        jbrLon, jbrLat, jwidthPixels, jheightPixels,
                                        jrotationAngle, resPoint);


    //fill objects and return it back to java env.

    jclass FLocationClass = (env)->GetObjectClass(covertedPoint);
    jmethodID midCallSetX = (env)->GetMethodID(FLocationClass, "setX", "(D)V");
    jmethodID midCallSetY = (env)->GetMethodID(FLocationClass, "setY", "(D)V");
    jmethodID midCallSetZ = (env)->GetMethodID(FLocationClass, "setZ", "(D)V");
    jmethodID midCallSetLat = (env)->GetMethodID(FLocationClass, "setLat", "(D)V");
    jmethodID midCallSetLon = (env)->GetMethodID(FLocationClass, "setLon", "(D)V");

    if (NULL == midCallSetX) return;
    if (NULL == midCallSetY) return;
    if (NULL == midCallSetZ) return;
    if (NULL == midCallSetLat) return;
    if (NULL == midCallSetLon) return;


    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "resPoint.x :%f",resPoint.x);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "resPoint.y :%f",resPoint.y);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "resPoint.z :%f",resPoint.z);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "resPoint.lat :%f",resPoint.lat);
    //__android_log_print(ANDROID_LOG_INFO, "NdkConversionUtils_convertPoint", "resPoint.lon :%f",resPoint.lon);

    // fill
    (env)->CallVoidMethod(covertedPoint, midCallSetX, resPoint.x);
    (env)->CallVoidMethod(covertedPoint, midCallSetY, resPoint.y);
    (env)->CallVoidMethod(covertedPoint, midCallSetZ, resPoint.z);
    (env)->CallVoidMethod(covertedPoint, midCallSetLat, resPoint.lat);
    (env)->CallVoidMethod(covertedPoint, midCallSetLon, resPoint.lon);

}
