#include <string.h>
#include <jni.h>
#include "ShortPathCalculator.h"
#include "JNIShortPathCalculatorUtils.h"

using namespace std;

JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkShortPathCalculator_findBestEnterAndExist(
        JNIEnv *env, jobject thisObj,
        jobject origin,
        jobject dest,
        jobjectArray entrances,
        jobjectArray exists,
        jobject selectedEntrance,
        jobject selectedExit,
        jdouble p2mOrigin,
        jdouble p2mDest) {

    int enteranceslength = (env)->GetArrayLength(entrances);
    int existslength = (env)->GetArrayLength(exists);

    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "enteranceslength :%d",enteranceslength);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "existslength :%d",existslength);

    vector<Location> entrancesVector;
    vector<Location> existsVector;

    // Get a class reference

    jclass classFLocation = env->FindClass("com/mlins/ndk/wrappers/FLocation");
    jfieldID midX = (env)->GetFieldID(classFLocation, "x", "F");
    jfieldID midY = (env)->GetFieldID(classFLocation, "y", "F");
    jfieldID midZ = (env)->GetFieldID(classFLocation, "z", "F");
    jfieldID midLat = (env)->GetFieldID(classFLocation, "lat", "D");
    jfieldID midLon = (env)->GetFieldID(classFLocation, "lon", "D");
    jfieldID midFacilityId = (env)->GetFieldID(classFLocation, "facilityId", "Ljava/lang/String;");
    jfieldID midPoiId = (env)->GetFieldID(classFLocation, "poiId", "Ljava/lang/String;");

    if (NULL == midX) return;
    if (NULL == midY) return;
    if (NULL == midZ) return;
    if (NULL == midLat) return;
    if (NULL == midLon) return;
    if (NULL == midFacilityId) return;
    if (NULL == midPoiId) return;

    double convP2mOrigin = (double) p2mOrigin;
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "convP2mOrigin :%f",convP2mOrigin);
    double convP2mDest = (double) p2mDest;
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "convP2mDest :%f",convP2mDest);

    // convert origin

    Location originLoc;
    originLoc.x = (float) env->GetFloatField(origin, midX);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "originLoc.x :%f",originLoc.x);

    originLoc.y = (float) env->GetFloatField(origin, midY);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "originLoc.y :%f",originLoc.y);

    originLoc.z = (float) env->GetFloatField(origin, midZ);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "originLoc.z :%f", originLoc.z);

    originLoc.lat = (double) env->GetDoubleField(origin, midLat);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "originLoc.lat :%f", originLoc.lat);

    originLoc.lon = (double) env->GetDoubleField(origin, midLon);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "originLoc.lon :%f",originLoc.lon);

    jstring facilityId2 = (jstring) env->GetObjectField(origin, midFacilityId);
    const char *facilityIdSrc2 = env->GetStringUTFChars(facilityId2, 0);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "originLoc.facilityIdSrc2 :%s",facilityIdSrc2);

    jstring poiId2 = (jstring) env->GetObjectField(origin, midPoiId);
    const char *poiIdSrc2 = env->GetStringUTFChars(poiId2, 0);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "originLoc.poiIdSrc2 :%s",poiIdSrc2);

    originLoc.facilityId = facilityIdSrc2;
    originLoc.poiId = poiIdSrc2;
    env->ReleaseStringUTFChars(facilityId2, facilityIdSrc2);
    env->ReleaseStringUTFChars(poiId2, poiIdSrc2);

    // convert dest

    Location destLoc;
    destLoc.x = (float) env->GetFloatField(dest, midX);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "destLoc.x :%f",destLoc.x);

    destLoc.y = (float) env->GetFloatField(dest, midY);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "destLoc.y :%f",destLoc.y);

    destLoc.z = (float) env->GetFloatField(dest, midZ);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "destLoc.z :%f", destLoc.z);

    destLoc.lat = (double) env->GetDoubleField(dest, midLat);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "destLoc.lat :%f", destLoc.lat);

    destLoc.lon = (double) env->GetDoubleField(dest, midLon);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "destLoc.lon :%f",destLoc.lon);

    jstring facilityId1 = (jstring) env->GetObjectField(dest, midFacilityId);
    const char *facilityIdSrc1 = env->GetStringUTFChars(facilityId1, 0);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "destLoc.facilityIdSrc1 :%s",facilityIdSrc1);

    jstring poiId1 = (jstring) env->GetObjectField(dest, midPoiId);
    const char *poiIdSrc1 = env->GetStringUTFChars(poiId1, 0);
    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "destLoc.poiIdSrc1 :%s",poiIdSrc1);

    destLoc.facilityId = facilityIdSrc1;
    destLoc.poiId = poiIdSrc1;

    env->ReleaseStringUTFChars(facilityId1, facilityIdSrc1);
    env->ReleaseStringUTFChars(poiId1, poiIdSrc1);

    // Get the value of each Integer object in the array - entrances

    for (int i = 0; i < enteranceslength; i++) {

        jobject enterObj = (env)->GetObjectArrayElement(entrances, i);

        if (NULL != enterObj) {

            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "i :%d",i);

            float x = (float) env->GetFloatField(enterObj, midX);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "x :%f",x);

            float y = (float) env->GetFloatField(enterObj, midY);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "y :%f",y);

            float z = (float) env->GetFloatField(enterObj, midZ);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "z :%f",z);

            double lat = (double) env->GetDoubleField(enterObj, midLat);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "lat :%f",lat);

            double lon = (double) env->GetDoubleField(enterObj, midLon);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "lon :%f",lon);
            jstring facilityId = (jstring) env->GetObjectField(enterObj, midFacilityId);

            const char *facilityIdSrc = env->GetStringUTFChars(facilityId, 0);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "facilityIdSrc :%s",facilityIdSrc);

            jstring poiId = (jstring) env->GetObjectField(enterObj, midPoiId);
            const char *poiIdSrc = env->GetStringUTFChars(poiId, 0);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "poiIdSrc :%s",poiIdSrc);

            // build ndk object
            Location loc;
            loc.x = x;
            loc.y = y;
            loc.z = z;
            loc.lat = lat;
            loc.lon = lon;
            loc.facilityId = facilityIdSrc;
            loc.poiId = poiIdSrc;
            entrancesVector.push_back(loc);

            env->ReleaseStringUTFChars(facilityId, facilityIdSrc);
            env->ReleaseStringUTFChars(poiId, poiIdSrc);

        }
    }

    // Get the value of each Integer object in the array - exists

    for (int i = 0; i < existslength; i++) {

        jobject exitObj = (env)->GetObjectArrayElement(exists, i);

        if (NULL != exitObj) {

            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "i :%d",i);

            float x = (float) env->GetFloatField(exitObj, midX);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "x :%f",x);

            float y = (float) env->GetFloatField(exitObj, midY);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "y :%f",y);

            float z = (float) env->GetFloatField(exitObj, midZ);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "z :%f",z);

            double lat = (double) env->GetDoubleField(exitObj, midLat);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "lat :%f",lat);

            double lon = (double) env->GetDoubleField(exitObj, midLon);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "lon :%f",lon);
            jstring facilityId = (jstring) env->GetObjectField(exitObj, midFacilityId);

            const char *facilityIdSrc = env->GetStringUTFChars(facilityId, 0);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "facilityIdSrc :%s",facilityIdSrc);

            jstring poiId = (jstring) env->GetObjectField(exitObj, midPoiId);
            const char *poiIdSrc = env->GetStringUTFChars(poiId, 0);
            //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist", "poiIdSrc :%s",poiIdSrc);

            // build ndk object
            Location loc;
            loc.x = x;
            loc.y = y;
            loc.z = z;
            loc.lat = lat;
            loc.lon = lon;
            loc.facilityId = facilityIdSrc;
            loc.poiId = poiIdSrc;
            existsVector.push_back(loc);

            env->ReleaseStringUTFChars(facilityId, facilityIdSrc);
            env->ReleaseStringUTFChars(poiId, poiIdSrc);

        }
    }

    Location resEnter;
    Location resExit;

    ShortPathCalculator calculator;
    calculator.findBestEnterAndExist(originLoc, destLoc, entrancesVector, existsVector, resEnter,
                                     resExit, convP2mOrigin, convP2mDest);

    //__android_log_print(ANDROID_LOG_INFO, "NdkShortPathCalculator_findBestEnterAndExist"," (enter , exit) ==> (%s , %s)", resEnter.poiId.c_str(), resExit.poiId.c_str());

    //fill objects and return it back to java env.

    jclass FLocationClass = (env)->GetObjectClass(selectedEntrance);
    jmethodID midCallSetX = (env)->GetMethodID(FLocationClass, "setX", "(F)V");
    jmethodID midCallSetY = (env)->GetMethodID(FLocationClass, "setY", "(F)V");
    jmethodID midCallSetZ = (env)->GetMethodID(FLocationClass, "setZ", "(F)V");
    jmethodID midCallSetLat = (env)->GetMethodID(FLocationClass, "setLat", "(D)V");
    jmethodID midCallSetLon = (env)->GetMethodID(FLocationClass, "setLon", "(D)V");
    jmethodID midCallSetFacilityId = (env)->GetMethodID(FLocationClass, "setFacilityId",
                                                        "(Ljava/lang/String;)V");
    jmethodID midCallSetPoiId = (env)->GetMethodID(FLocationClass, "setPoiId",
                                                   "(Ljava/lang/String;)V");

    if (NULL == midCallSetX) return;
    if (NULL == midCallSetY) return;
    if (NULL == midCallSetZ) return;
    if (NULL == midCallSetLat) return;
    if (NULL == midCallSetLon) return;
    if (NULL == midCallSetFacilityId) return;
    if (NULL == midCallSetPoiId) return;

    // fill selected entrance
    (env)->CallVoidMethod(selectedEntrance, midCallSetX, resEnter.x);

    (env)->CallVoidMethod(selectedEntrance, midCallSetY, resEnter.y);
    (env)->CallVoidMethod(selectedEntrance, midCallSetZ, resEnter.z);
    (env)->CallVoidMethod(selectedEntrance, midCallSetLat, resEnter.lat);
    (env)->CallVoidMethod(selectedEntrance, midCallSetLon, resEnter.lon);

    const char *resEnterPoiId = resEnter.poiId.c_str();
    const char *resEnterFacilityId = resEnter.facilityId.c_str();

    (env)->CallVoidMethod(selectedEntrance, midCallSetFacilityId,
                          (env)->NewStringUTF(resEnterFacilityId));
    (env)->CallVoidMethod(selectedEntrance, midCallSetPoiId, (env)->NewStringUTF(resEnterPoiId));



    // fill selected exit
    (env)->CallVoidMethod(selectedExit, midCallSetX, resExit.x);
    (env)->CallVoidMethod(selectedExit, midCallSetY, resExit.y);
    (env)->CallVoidMethod(selectedExit, midCallSetZ, resExit.z);
    (env)->CallVoidMethod(selectedExit, midCallSetLat, resExit.lat);
    (env)->CallVoidMethod(selectedExit, midCallSetLon, resExit.lon);

    const char *resExitPoiId = resExit.poiId.c_str();
    const char *resExitFacilityId = resExit.facilityId.c_str();

    (env)->CallVoidMethod(selectedExit, midCallSetFacilityId,
                          (env)->NewStringUTF(resExitFacilityId));
    (env)->CallVoidMethod(selectedExit, midCallSetPoiId, (env)->NewStringUTF(resExitPoiId));

}
