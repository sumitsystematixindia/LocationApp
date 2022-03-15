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
#include "FloorSelectorUtils.h"
#include "OptFloorSelectorFinder.h"
//#ifndef NDEBUG
//usleep(5000 * 1000);
//#endif
using namespace std;


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkFloorSelector_load(JNIEnv *env,
                                                                         jobject thisObj,
                                                                         jstring facility,
                                                                         jint floor,
                                                                         jboolean isBin) {

    const char *facilityName = (env)->GetStringUTFChars(facility, 0);

    OptFloorSelectorFinder *optLocf;
    optLocf = OptFloorSelectorFinder::getInstance();
    optLocf->load(facilityName, floor, true, isBin);

    env->ReleaseStringUTFChars(facility, facilityName);
    return;
}


JNIEXPORT jint JNICALL Java_com_mlins_ndk_wrappers_NdkFloorSelector_getFloor(JNIEnv *env,
                                                                             jobject thisObj,
                                                                             jobjectArray blips,
                                                                             jboolean isFirstTime) {


    int length = (env)->GetArrayLength(blips);

    //  __android_log_print(ANDROID_LOG_DEBUG, "getFloor", "length :%d",length);

    WlBlip blip;
    list<WlBlip> blibsList;


    // Get a class reference
    jclass classWB = env->FindClass("com/mlins/wireless/WlBlip");
    //jclass classWB = reinterpret_cast<jclass>(env->NewGlobalRef(localClass));



    jfieldID midBSSID = (env)->GetFieldID(classWB, "BSSID", "Ljava/lang/String;");
    jfieldID midlevel = (env)->GetFieldID(classWB, "level", "I");


    if (NULL == midBSSID)
        return -100;
    if (NULL == midlevel)
        return -100;

    // Get the value of each

    for (int i = 0; i < length; i++) {
        jobject blipObj = (env)->GetObjectArrayElement(blips, i);

        if (NULL != blipObj) {

            //    	  __android_log_print(ANDROID_LOG_DEBUG, "findLocation", "i :%d",i);

            WlBlip blip;
            jstring BSSID = (jstring) env->GetObjectField(blipObj, midBSSID);
            const char *BSSIDSrc = env->GetStringUTFChars(BSSID, 0);
            blip.BSSID = BSSIDSrc;
            env->ReleaseStringUTFChars(BSSID, BSSIDSrc);

            // 	__android_log_print(ANDROID_LOG_DEBUG, "findLocation", "BSSIDSrc :%s",BSSIDSrc);

            int level = (int) env->GetIntField(blipObj, midlevel);

            //	__android_log_print(ANDROID_LOG_DEBUG, "findLocation", "level :%d",level);

            blip.level = level;


            blibsList.push_back(blip);
        }
    }


    OptFloorSelectorFinder *optLocf;
    optLocf = OptFloorSelectorFinder::getInstance();
    //Location resLoc;  // kan false

    jint x = optLocf->getFloorByBlips(blibsList, isFirstTime);

    return x;
}

//JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkFloorSelector_initParams(
//		JNIEnv * env, jobject thisObj, jstring appDirPath,
//		jint locationCloseRange, jint k, jfloat pixelsToMeter, jint averageRange,
//		jobjectArray ssidfilter, jint floorcount,jstring scantype) {
//
//	const char *strRootPath = (env)->GetStringUTFChars(appDirPath, 0);
//	//__android_log_print(ANDROID_LOG_DEBUG, "initParams", "appDirPath :%s",strRootPath);  //printf("%s", str); //need to release this string when done with it in
//	const char *strScantype = (env)->GetStringUTFChars(scantype, 0);
//
//	int locCloseRng = (int) locationCloseRange;
//	//__android_log_print(ANDROID_LOG_DEBUG, "initParams", "locCloseRng :%d",locCloseRng);
//	int K = k;
//	//bool isfloorSel = (bool) isfloorSelection;
//
//	float pixToMtr = (float) pixelsToMeter;
//	int avgRange = (int) averageRange;
//
//	int stringCount = env->GetArrayLength(ssidfilter);
//
//	// __android_log_print(ANDROID_LOG_DEBUG, "initParams", "length :%d",stringCount);
//
//	jstring ssid;
//	vector<string> ssidsVec;
//
//	const char *rawSSID;
//	for (int i = 0; i < stringCount; i++) {
//		ssid = (jstring) env->GetObjectArrayElement(ssidfilter, i);
//		if (NULL != ssid) {
//
//			rawSSID = env->GetStringUTFChars(ssid, 0);
//
//			ssidsVec.push_back(rawSSID);
//			env->ReleaseStringUTFChars(ssid, rawSSID);
//			//	__android_log_print(ANDROID_LOG_DEBUG, "initParams", "ssidfilter :%s",rawSSID);
//		}
//
//	}
//
//
//	OptFloorSelectorFinder* optLocf;
//		optLocf = OptFloorSelectorFinder::getInstance();
//		optLocf->initParams(strRootPath, locCloseRng, K, pixToMtr, avgRange,ssidsVec,floorcount,strScantype);
//
//	//avoid memory leak
//
//	env->ReleaseStringUTFChars(appDirPath, strRootPath);
//	env->ReleaseStringUTFChars(scantype, strScantype);
//
//	return;
//}



//JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkFloorSelector_initParams(
//		JNIEnv * env, jobject thisObj, jstring appDirPath,
//		jint locationCloseRange, jint k, jfloat pixelsToMeter, jint averageRange,
//		jobjectArray ssidfilter, jint floorcount,jstring scantype,
//		jfloat closeDevicesThreshold, jfloat closeDeviceWeight, jint kTopLevelThr) {
//
//	const char *strRootPath = (env)->GetStringUTFChars(appDirPath, 0);
//	//__android_log_print(ANDROID_LOG_DEBUG, "initParams", "appDirPath :%s",strRootPath);  //printf("%s", str); //need to release this string when done with it in
//	const char *strScantype = (env)->GetStringUTFChars(scantype, 0);
//
//	int locCloseRng = (int) locationCloseRange;
//	//__android_log_print(ANDROID_LOG_DEBUG, "initParams", "locCloseRng :%d",locCloseRng);
//	int K = k;
//	//bool isfloorSel = (bool) isfloorSelection;
//
//	float pixToMtr = (float) pixelsToMeter;
//	int avgRange = (int) averageRange;
//
//	int stringCount = env->GetArrayLength(ssidfilter);
//
//	// __android_log_print(ANDROID_LOG_DEBUG, "initParams", "length :%d",stringCount);
//
//	jstring ssid;
//	vector<string> ssidsVec;
//
//	const char *rawSSID;
//	for (int i = 0; i < stringCount; i++) {
//		ssid = (jstring) env->GetObjectArrayElement(ssidfilter, i);
//		if (NULL != ssid) {
//
//			rawSSID = env->GetStringUTFChars(ssid, 0);
//
//			ssidsVec.push_back(rawSSID);
//			env->ReleaseStringUTFChars(ssid, rawSSID);
//			//	__android_log_print(ANDROID_LOG_DEBUG, "initParams", "ssidfilter :%s",rawSSID);
//		}
//
//	}
//
//	float conv_closeDevicesThreshold=(float) closeDevicesThreshold;
//	float conv_closeDeviceWeight=(float) closeDeviceWeight;
//	int conv_kTopLevelThr=(int) kTopLevelThr;
//
//	OptFloorSelectorFinder::releaseInstance();
//
//	OptFloorSelectorFinder* optLocf;
//		optLocf = OptFloorSelectorFinder::getInstance();
//		optLocf->initParams(strRootPath, locCloseRng, K, pixToMtr, avgRange,ssidsVec,floorcount,strScantype,conv_closeDevicesThreshold,conv_closeDeviceWeight,conv_kTopLevelThr);
//
//	//avoid memory leak
//
//	env->ReleaseStringUTFChars(appDirPath, strRootPath);
//	env->ReleaseStringUTFChars(scantype, strScantype);
//
//	return;
//}


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkFloorSelector_initParams(
        JNIEnv *env, jobject thisObj, jstring appDirPath,
        jint locationCloseRange, jint k, jfloat pixelsToMeter, jint averageRange,
        jobjectArray ssidfilter, jint floorcount, jstring scantype,
        jfloat closeDevicesThreshold, jfloat closeDeviceWeight, jint kTopLevelThr,
        jint levelLowerBound) {

    const char *strRootPath = (env)->GetStringUTFChars(appDirPath, 0);
    //__android_log_print(ANDROID_LOG_DEBUG, "initParams", "appDirPath :%s",strRootPath);  //printf("%s", str); //need to release this string when done with it in
    const char *strScantype = (env)->GetStringUTFChars(scantype, 0);

    int locCloseRng = (int) locationCloseRange;
    //__android_log_print(ANDROID_LOG_DEBUG, "initParams", "locCloseRng :%d",locCloseRng);
    int K = k;
    //bool isfloorSel = (bool) isfloorSelection;

    float pixToMtr = (float) pixelsToMeter;
    int avgRange = (int) averageRange;

    int stringCount = env->GetArrayLength(ssidfilter);

    // __android_log_print(ANDROID_LOG_DEBUG, "initParams", "length :%d",stringCount);

    jstring ssid;
    vector<string> ssidsVec;

    const char *rawSSID;
    for (int i = 0; i < stringCount; i++) {
        ssid = (jstring) env->GetObjectArrayElement(ssidfilter, i);
        if (NULL != ssid) {

            rawSSID = env->GetStringUTFChars(ssid, 0);

            ssidsVec.push_back(rawSSID);
            env->ReleaseStringUTFChars(ssid, rawSSID);
            //	__android_log_print(ANDROID_LOG_DEBUG, "initParams", "ssidfilter :%s",rawSSID);
        }

    }

    float conv_closeDevicesThreshold = (float) closeDevicesThreshold;
    float conv_closeDeviceWeight = (float) closeDeviceWeight;
    int conv_kTopLevelThr = (int) kTopLevelThr;

    int conv_levelLowerBound = (int) levelLowerBound;

    OptFloorSelectorFinder::releaseInstance();

    OptFloorSelectorFinder *optLocf;
    optLocf = OptFloorSelectorFinder::getInstance();
    optLocf->initParams(strRootPath, locCloseRng, K, pixToMtr, avgRange, ssidsVec, floorcount,
                        strScantype, conv_closeDevicesThreshold, conv_closeDeviceWeight,
                        conv_kTopLevelThr, conv_levelLowerBound);

    // __android_log_print(ANDROID_LOG_DEBUG, "initParams", "fs initParams :%d",2);
    //avoid memory leak

    env->ReleaseStringUTFChars(appDirPath, strRootPath);
    env->ReleaseStringUTFChars(scantype, strScantype);

    return;
}


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkFloorSelector_saveBin(
        JNIEnv *env, jobject thisObj) {
    OptFloorSelectorFinder *optLocf;
    optLocf = OptFloorSelectorFinder::getInstance();
    optLocf->saveBin();

    //optLocf->loadBin("office",2);

}

