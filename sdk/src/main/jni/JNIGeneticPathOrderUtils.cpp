/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>
#include "JNIGeneticPathOrderUtils.h"
#include "GeneticPathFinder.h"
#include "GeneticOrderBuilder.h"
//#ifndef NDEBUG
//usleep(5000 * 1000);
//#endif
using namespace std;

JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkGeneticPathOrderFinder_getPoisOrder(
        JNIEnv *env, jobject thisObj, jstring switchFloorFilePath, jint generationsCount,
        jobject origin, jobjectArray poisDestList, jobjectArray fullSolutionPath) {

    const char *strSwitchFloorFilePath = (env)->GetStringUTFChars(switchFloorFilePath, 0);

    int GenerationsCount = generationsCount;
    __android_log_print(ANDROID_LOG_DEBUG, "NdkGeneticPathOrderFinder", "GenerationsCount :%d",
                        GenerationsCount);
    __android_log_print(ANDROID_LOG_DEBUG, "NdkGeneticPathOrderFinder",
                        "strSwitchFloorFilePath :%s", strSwitchFloorFilePath);

    // Get a class reference
    jclass classFLocationRef = env->FindClass("com/mlins/ndk/wrappers/FLocation");

    jfieldID midX = (env)->GetFieldID(classFLocationRef, "x", "F");
    jfieldID midY = (env)->GetFieldID(classFLocationRef, "y", "F");
    jfieldID midZ = (env)->GetFieldID(classFLocationRef, "z", "F");

    if (NULL == midX)
        return;
    if (NULL == midY)
        return;
    if (NULL == midZ)
        return;

    jfloat jX = (env)->GetFloatField(origin, midX);
    jfloat jY = (env)->GetFloatField(origin, midY);
    jfloat jZ = (env)->GetFloatField(origin, midZ);


    Location originLoc;
    originLoc.x = (float) jX;
    originLoc.y = (float) jY;
    originLoc.z = (float) jZ;

    __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder",
                        "in originLoc (x :%f, y :%f, z :%f)", originLoc.x, originLoc.y,
                        originLoc.z);


    int poiDestLength = (env)->GetArrayLength(poisDestList);
    list<Location> jPoisDestList;

    // Get the value of each Integer object in the array

    for (int i = 0; i < poiDestLength; i++) {
        jobject poiObj = (env)->GetObjectArrayElement(poisDestList, i);

        if (NULL != poiObj) {

            __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder", "in i :%d", i);

            jfloat jX = (env)->GetFloatField(poiObj, midX);
            jfloat jY = (env)->GetFloatField(poiObj, midY);
            jfloat jZ = (env)->GetFloatField(poiObj, midZ);

            Location poiDestLoc;
            poiDestLoc.x = (float) jX;
            poiDestLoc.y = (float) jY;
            poiDestLoc.z = (float) jZ;
            jPoisDestList.push_back(poiDestLoc);

            __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder",
                                "in poiDestLoc (x :%f, y :%f, z :%f)", poiDestLoc.x, poiDestLoc.y,
                                poiDestLoc.z);

        }
    }

    GeneticPathFinder geneticAlg;
    list<Location> midFullSolutionPath;
    geneticAlg.getPoisOrder(strSwitchFloorFilePath, GenerationsCount, originLoc, jPoisDestList,
                            midFullSolutionPath);

    //fill the solution array and return it back to java env.
    int fullSolLength = (env)->GetArrayLength(fullSolutionPath);

    int i = 0;

    for (list<Location>::iterator it = midFullSolutionPath.begin();
         it != midFullSolutionPath.end(); it++) {

        Location &poi = *it;

        if (i < fullSolLength) {

            jobject solLocObj = (env)->GetObjectArrayElement(fullSolutionPath, i);

            __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder", "sol i :%d", i);

            if (NULL != solLocObj) {
                jclass FLocationClass = (env)->GetObjectClass(solLocObj);
                jmethodID midCallSetX = (env)->GetMethodID(FLocationClass, "setX", "(F)V");
                jmethodID midCallSetY = (env)->GetMethodID(FLocationClass, "setY", "(F)V");
                jmethodID midCallSetZ = (env)->GetMethodID(FLocationClass, "setZ", "(F)V");

                if (NULL == midCallSetX) return;
                if (NULL == midCallSetY) return;
                if (NULL == midCallSetZ) return;

                (env)->CallVoidMethod(solLocObj, midCallSetX, poi.x);
                (env)->CallVoidMethod(solLocObj, midCallSetY, poi.y);
                (env)->CallVoidMethod(solLocObj, midCallSetZ, poi.z);

                __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder",
                                    "sol returned poi (x :%f, y :%f, z :%f)", poi.x, poi.y, poi.z);
            }
        }

        i++;
    }
    // release memory
    env->ReleaseStringUTFChars(switchFloorFilePath, strSwitchFloorFilePath);

    return;

}


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkGeneticPathOrderFinder_getMixedPoisOrder(
        JNIEnv *env, jobject thisObj, jstring switchFloorFilePath, jint generationsCount,
        jobject origin, jobjectArray poisDestList, jobjectArray poisExitsList,
        jobjectArray fullSolutionPath) {

    const char *strSwitchFloorFilePath = (env)->GetStringUTFChars(switchFloorFilePath, 0);

    int GenerationsCount = generationsCount;
    __android_log_print(ANDROID_LOG_DEBUG, "NdkGeneticPathOrderFinder", "GenerationsCount :%d",
                        GenerationsCount);
    __android_log_print(ANDROID_LOG_DEBUG, "NdkGeneticPathOrderFinder",
                        "strSwitchFloorFilePath :%s", strSwitchFloorFilePath);

    // Get a class reference
    jclass classFLocationRef = env->FindClass("com/mlins/ndk/wrappers/FLocation");

    jfieldID midX = (env)->GetFieldID(classFLocationRef, "x", "F");
    jfieldID midY = (env)->GetFieldID(classFLocationRef, "y", "F");
    jfieldID midZ = (env)->GetFieldID(classFLocationRef, "z", "F");
    jfieldID midType = (env)->GetFieldID(classFLocationRef, "type", "I");
    jfieldID midLat = (env)->GetFieldID(classFLocationRef, "lat", "D");
    jfieldID midLon = (env)->GetFieldID(classFLocationRef, "lon", "D");


    if (NULL == midX)
        return;
    if (NULL == midY)
        return;
    if (NULL == midZ)
        return;

    if (NULL == midType)
        return;

    if (NULL == midLat)
        return;

    if (NULL == midLon)
        return;

    jfloat jX = (env)->GetFloatField(origin, midX);
    jfloat jY = (env)->GetFloatField(origin, midY);
    jfloat jZ = (env)->GetFloatField(origin, midZ);
    jint jtype = (env)->GetIntField(origin, midType);
    jdouble jLat = (env)->GetDoubleField(origin, midLat);
    jdouble jLon = (env)->GetDoubleField(origin, midLon);

    Location originLoc;
    originLoc.x = (float) jX;
    originLoc.y = (float) jY;
    originLoc.z = (float) jZ;
    originLoc.type = (int) jtype;
    originLoc.lat = (double) jLat;
    originLoc.lon = (double) jLon;

    __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder",
                        "in originLoc (x :%f, y :%f, z :%f ,lat:%f , lon:%f, type: %d)",
                        originLoc.x, originLoc.y, originLoc.z, originLoc.lat, originLoc.lon,
                        originLoc.type);


    // get poiDestLoc array
    int poiDestLength = (env)->GetArrayLength(poisDestList);
    list<Location> jPoisDestList;

    for (int i = 0; i < poiDestLength; i++) {
        jobject poiObj = (env)->GetObjectArrayElement(poisDestList, i);

        if (NULL != poiObj) {

            __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder", "in i :%d", i);

            jfloat jX = (env)->GetFloatField(poiObj, midX);
            jfloat jY = (env)->GetFloatField(poiObj, midY);
            jfloat jZ = (env)->GetFloatField(poiObj, midZ);
            jint jtype = (env)->GetIntField(poiObj, midType);
            jdouble jLat = (env)->GetDoubleField(poiObj, midLat);
            jdouble jLon = (env)->GetDoubleField(poiObj, midLon);

            Location poiDestLoc;
            poiDestLoc.x = (float) jX;
            poiDestLoc.y = (float) jY;
            poiDestLoc.z = (float) jZ;
            poiDestLoc.type = (int) jtype;
            poiDestLoc.lat = (double) jLat;
            poiDestLoc.lon = (double) jLon;

            jPoisDestList.push_back(poiDestLoc);

            __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder",
                                "in poiDestLoc (x :%f, y :%f, z :%f,lat:%f , lon:%f, type: %d)",
                                poiDestLoc.x, poiDestLoc.y, poiDestLoc.z, poiDestLoc.lat,
                                poiDestLoc.lon, poiDestLoc.type);

        }
    }
    // end get poiDestLoc array


    // get poisExitsList array
    int poiExitsLength = (env)->GetArrayLength(poisExitsList);
    list<Location> jPoisExitsList;

    for (int i = 0; i < poiExitsLength; i++) {
        jobject poiExitObj = (env)->GetObjectArrayElement(poisExitsList, i);

        if (NULL != poiExitObj) {

            __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder", "in i :%d", i);

            jfloat jX = (env)->GetFloatField(poiExitObj, midX);
            jfloat jY = (env)->GetFloatField(poiExitObj, midY);
            jfloat jZ = (env)->GetFloatField(poiExitObj, midZ);
            jint jtype = (env)->GetIntField(poiExitObj, midType);
            jdouble jLat = (env)->GetDoubleField(poiExitObj, midLat);
            jdouble jLon = (env)->GetDoubleField(poiExitObj, midLon);

            Location poiExitLoc;
            poiExitLoc.x = (float) jX;
            poiExitLoc.y = (float) jY;
            poiExitLoc.z = (float) jZ;
            poiExitLoc.lat = (double) jLat;
            poiExitLoc.lon = (double) jLon;
            poiExitLoc.type = (int) jtype;

            jPoisExitsList.push_back(poiExitLoc);

            __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder",
                                "in poisExitsList (x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d)",
                                poiExitLoc.x, poiExitLoc.y, poiExitLoc.z, poiExitLoc.lat,
                                poiExitLoc.lon, poiExitLoc.type);

        }
    }
    // end get poisExitsList array



    GeneticPathFinder geneticAlg;
    list<Location> midFullSolutionPath;
    geneticAlg.getMixedPoisOrder(strSwitchFloorFilePath, GenerationsCount, originLoc, jPoisDestList,
                                 jPoisExitsList, midFullSolutionPath);

    //fill the solution array and return it back to java env.
    int fullSolLength = (env)->GetArrayLength(fullSolutionPath);

    int i = 0;

    for (list<Location>::iterator it = midFullSolutionPath.begin();
         it != midFullSolutionPath.end(); it++) {

        Location &poi = *it;

        if (i < fullSolLength) {

            jobject solLocObj = (env)->GetObjectArrayElement(fullSolutionPath, i);

            __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder", "sol i :%d", i);

            if (NULL != solLocObj) {
                jclass FLocationClass = (env)->GetObjectClass(solLocObj);
                jmethodID midCallSetX = (env)->GetMethodID(FLocationClass, "setX", "(F)V");
                jmethodID midCallSetY = (env)->GetMethodID(FLocationClass, "setY", "(F)V");
                jmethodID midCallSetZ = (env)->GetMethodID(FLocationClass, "setZ", "(F)V");

                jmethodID midCallSetType = (env)->GetMethodID(FLocationClass, "setType", "(I)V");
                jmethodID midCallSetLat = (env)->GetMethodID(FLocationClass, "setLat", "(D)V");
                jmethodID midCallSetLon = (env)->GetMethodID(FLocationClass, "setLon", "(D)V");

                if (NULL == midCallSetX) return;
                if (NULL == midCallSetY) return;
                if (NULL == midCallSetZ) return;
                if (NULL == midCallSetType) return;
                if (NULL == midCallSetLat) return;
                if (NULL == midCallSetLon) return;

                (env)->CallVoidMethod(solLocObj, midCallSetX, poi.x);
                (env)->CallVoidMethod(solLocObj, midCallSetY, poi.y);
                (env)->CallVoidMethod(solLocObj, midCallSetZ, poi.z);

                (env)->CallVoidMethod(solLocObj, midCallSetType, poi.type);
                (env)->CallVoidMethod(solLocObj, midCallSetLat, poi.lat);
                (env)->CallVoidMethod(solLocObj, midCallSetLon, poi.lon);

                __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder",
                                    "sol returned poi (x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d)",
                                    poi.x, poi.y, poi.z, poi.lat, poi.lon, poi.type);
            }
        }

        i++;
    }
    // release memory
    env->ReleaseStringUTFChars(switchFloorFilePath, strSwitchFloorFilePath);

    return;

}


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkGeneticPathOrderFinder_getMultiFacilitiesPoisOrder(
        JNIEnv *env, jobject thisObj, jstring appDir, jstring projectid, jstring campusid,
        jint generationsCount,
        jobject origin, jobjectArray poisDestList, jobjectArray facilitiesCentersList,
        jobjectArray fullSolutionPath) {


    const char *strAppDir = (env)->GetStringUTFChars(appDir, 0);
    const char *strProjectId = (env)->GetStringUTFChars(projectid, 0);
    const char *strCampusId = (env)->GetStringUTFChars(campusid, 0);

    int GenerationsCount = generationsCount;

    //XXX print debug
//	__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder_getMultiFacilitiesPoisOrder", "appDir :%s",strAppDir);
//    __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder_getMultiFacilitiesPoisOrder", "projectid :%s",strProjectId);
//	__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder_getMultiFacilitiesPoisOrder", "campusid :%s",strCampusId);
//	__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder_getMultiFacilitiesPoisOrder", "GenerationsCount :%d",GenerationsCount);


    // Get a class reference
    jclass classFLocationRef = env->FindClass("com/mlins/ndk/wrappers/FLocation");

    jfieldID midX = (env)->GetFieldID(classFLocationRef, "x", "F");
    jfieldID midY = (env)->GetFieldID(classFLocationRef, "y", "F");
    jfieldID midZ = (env)->GetFieldID(classFLocationRef, "z", "F");
    jfieldID midType = (env)->GetFieldID(classFLocationRef, "type", "I");
    jfieldID midLat = (env)->GetFieldID(classFLocationRef, "lat", "D");
    jfieldID midLon = (env)->GetFieldID(classFLocationRef, "lon", "D");
    jfieldID midFacilityId = (env)->GetFieldID(classFLocationRef, "facilityId",
                                               "Ljava/lang/String;");

    if (NULL == midX)
        return;
    if (NULL == midY)
        return;
    if (NULL == midZ)
        return;

    if (NULL == midType)
        return;

    if (NULL == midLat)
        return;

    if (NULL == midLon)
        return;

    if (NULL == midFacilityId)
        return;

    jfloat jX = (env)->GetFloatField(origin, midX);
    jfloat jY = (env)->GetFloatField(origin, midY);
    jfloat jZ = (env)->GetFloatField(origin, midZ);
    jint jtype = (env)->GetIntField(origin, midType);
    jdouble jLat = (env)->GetDoubleField(origin, midLat);
    jdouble jLon = (env)->GetDoubleField(origin, midLon);
    jstring jfacilityId = (jstring) env->GetObjectField(origin, midFacilityId);
    const char *facilityIdSrc = env->GetStringUTFChars(jfacilityId, 0);

    Location originLoc;
    originLoc.x = (float) jX;
    originLoc.y = (float) jY;
    originLoc.z = (float) jZ;
    originLoc.type = (int) jtype;
    originLoc.lat = (double) jLat;
    originLoc.lon = (double) jLon;
    originLoc.facilityId = facilityIdSrc;

//	__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder_getMultiFacilitiesPoisOrder", "in originLoc (x :%f, y :%f, z :%f ,lat:%f , lon:%f, type: %d, facilityId: %s)",
//							originLoc.x, originLoc.y, originLoc.z, originLoc.lat, originLoc.lon, originLoc.type, facilityIdSrc);

    env->ReleaseStringUTFChars(jfacilityId, facilityIdSrc);

    // get poiDestLoc array
    int poiDestLength = (env)->GetArrayLength(poisDestList);
    list<Location> jPoisDestList;

    for (int i = 0; i < poiDestLength; i++) {
        jobject poiObj = (env)->GetObjectArrayElement(poisDestList, i);

        if (NULL != poiObj) {

//	  	  __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder_getMultiFacilitiesPoisOrder", "in i :%d",i);

            jfloat jX = (env)->GetFloatField(poiObj, midX);
            jfloat jY = (env)->GetFloatField(poiObj, midY);
            jfloat jZ = (env)->GetFloatField(poiObj, midZ);
            jint jtype = (env)->GetIntField(poiObj, midType);
            jdouble jLat = (env)->GetDoubleField(poiObj, midLat);
            jdouble jLon = (env)->GetDoubleField(poiObj, midLon);
            jstring jfacilityId = (jstring) env->GetObjectField(poiObj, midFacilityId);
            const char *facilityIdSrc = env->GetStringUTFChars(jfacilityId, 0);

            Location poiDestLoc;
            poiDestLoc.x = (float) jX;
            poiDestLoc.y = (float) jY;
            poiDestLoc.z = (float) jZ;
            poiDestLoc.type = (int) jtype;
            poiDestLoc.lat = (double) jLat;
            poiDestLoc.lon = (double) jLon;
            poiDestLoc.facilityId = facilityIdSrc;

            jPoisDestList.push_back(poiDestLoc);

//			__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder_getMultiFacilitiesPoisOrder",
//											"in poiDestLoc (x :%f, y :%f, z :%f,lat:%f , lon:%f, type: %d, facId: %s)",
//											poiDestLoc.x, poiDestLoc.y, poiDestLoc.z,poiDestLoc.lat, poiDestLoc.lon, poiDestLoc.type, facilityIdSrc);

            env->ReleaseStringUTFChars(jfacilityId, facilityIdSrc);
        }
    }
    // end get poiDestLoc array


    // get poisExitsList array
    int facCentersLength = (env)->GetArrayLength(facilitiesCentersList);
    list<Location> jFacCentersList;

    for (int i = 0; i < facCentersLength; i++) {
        jobject facCenterObj = (env)->GetObjectArrayElement(facilitiesCentersList, i);

        if (NULL != facCenterObj) {

//	  	  __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder_getMultiFacilitiesPoisOrder", "in i :%d",i);

            jfloat jX = (env)->GetFloatField(facCenterObj, midX);
            jfloat jY = (env)->GetFloatField(facCenterObj, midY);
            jfloat jZ = (env)->GetFloatField(facCenterObj, midZ);
            jint jtype = (env)->GetIntField(facCenterObj, midType);
            jdouble jLat = (env)->GetDoubleField(facCenterObj, midLat);
            jdouble jLon = (env)->GetDoubleField(facCenterObj, midLon);
            jstring jfacilityId = (jstring) env->GetObjectField(facCenterObj, midFacilityId);
            const char *facilityIdSrc = env->GetStringUTFChars(jfacilityId, 0);

            Location facCenterLoc;
            facCenterLoc.x = (float) jX;
            facCenterLoc.y = (float) jY;
            facCenterLoc.z = (float) jZ;
            facCenterLoc.lat = (double) jLat;
            facCenterLoc.lon = (double) jLon;
            facCenterLoc.type = (int) jtype;
            facCenterLoc.facilityId = facilityIdSrc;

            jFacCentersList.push_back(facCenterLoc);

//			__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder_getMultiFacilitiesPoisOrder",
//									"in facilitiesCentersList (x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d, facId: %s)",
//									facCenterLoc.x, facCenterLoc.y, facCenterLoc.z, facCenterLoc.lat, facCenterLoc.lon, facCenterLoc.type, facilityIdSrc);

            env->ReleaseStringUTFChars(jfacilityId, facilityIdSrc);
        }
    }
    // end get poisExitsList array



    list<Location> midFullSolutionPath;
    GeneticOrderBuilder orderBuilder;
    orderBuilder.buildOrder(strAppDir, strProjectId, strCampusId, GenerationsCount, originLoc,
                            jPoisDestList, jFacCentersList, midFullSolutionPath);

    //fill the solution array and return it back to java env.
    int fullSolLength = (env)->GetArrayLength(fullSolutionPath);

    int i = 0;

    for (list<Location>::iterator it = midFullSolutionPath.begin();
         it != midFullSolutionPath.end(); it++) {

        Location &poi = *it;

        if (i < fullSolLength) {

            jobject solLocObj = (env)->GetObjectArrayElement(fullSolutionPath, i);

//			 __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder_getMultiFacilitiesPoisOrder", "sol i :%d",i);

            if (NULL != solLocObj) {
                jclass FLocationClass = (env)->GetObjectClass(solLocObj);
                jmethodID midCallSetX = (env)->GetMethodID(FLocationClass, "setX", "(F)V");
                jmethodID midCallSetY = (env)->GetMethodID(FLocationClass, "setY", "(F)V");
                jmethodID midCallSetZ = (env)->GetMethodID(FLocationClass, "setZ", "(F)V");

                jmethodID midCallSetType = (env)->GetMethodID(FLocationClass, "setType", "(I)V");
                jmethodID midCallSetLat = (env)->GetMethodID(FLocationClass, "setLat", "(D)V");
                jmethodID midCallSetLon = (env)->GetMethodID(FLocationClass, "setLon", "(D)V");
                jmethodID midCallSetFacilityId = (env)->GetMethodID(FLocationClass, "setFacilityId",
                                                                    "(Ljava/lang/String;)V");

                if (NULL == midCallSetX) return;
                if (NULL == midCallSetY) return;
                if (NULL == midCallSetZ) return;
                if (NULL == midCallSetType) return;
                if (NULL == midCallSetLat) return;
                if (NULL == midCallSetLon) return;
                if (NULL == midCallSetFacilityId) return;

                (env)->CallVoidMethod(solLocObj, midCallSetX, poi.x);
                (env)->CallVoidMethod(solLocObj, midCallSetY, poi.y);
                (env)->CallVoidMethod(solLocObj, midCallSetZ, poi.z);

                (env)->CallVoidMethod(solLocObj, midCallSetType, poi.type);
                (env)->CallVoidMethod(solLocObj, midCallSetLat, poi.lat);
                (env)->CallVoidMethod(solLocObj, midCallSetLon, poi.lon);

                const char *poiFacilityId = poi.facilityId.c_str();
                (env)->CallVoidMethod(solLocObj, midCallSetFacilityId,
                                      (env)->NewStringUTF(poiFacilityId));

//				__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder_getMultiFacilitiesPoisOrder",
//										"sol returned poi (x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d, facid: %s)",
//										poi.x, poi.y, poi.z, poi.lat, poi.lon, poi.type, poiFacilityId);
            }
        }

        i++;
    }

    // release memory
    env->ReleaseStringUTFChars(appDir, strAppDir);
    env->ReleaseStringUTFChars(projectid, strProjectId);
    env->ReleaseStringUTFChars(campusid, strCampusId);

    return;

}



