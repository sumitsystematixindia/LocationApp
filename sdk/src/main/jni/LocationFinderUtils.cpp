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
#include "LocationFinderUtils.h"
#include "OptLocFinder.h"
//#ifndef NDEBUG
//usleep(5000 * 1000);
//#endif
using namespace std;

JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_getLastpt(
        JNIEnv *env, jobject thisObj, jobject loc) {


    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();
    Location resLoc;
    optLocf->getLastLocation(resLoc);

    __android_log_print(ANDROID_LOG_DEBUG, "getLastpt", "getLastpt:location :(x=%f y=%f)", resLoc.x,
                        resLoc.y);

    //fill the loc and return it back to java env.

    jclass FLocationClass = (env)->GetObjectClass(loc);
//	jclass FLocationClass = reinterpret_cast<jclass>(env->NewGlobalRef(FLocationClass2));



    jmethodID midCallSetX = (env)->GetMethodID(FLocationClass, "setX", "(F)V");
    jmethodID midCallSetY = (env)->GetMethodID(FLocationClass, "setY", "(F)V");
    if (NULL == midCallSetX) return;
    if (NULL == midCallSetY) return;

    (env)->CallVoidMethod(loc, midCallSetX, resLoc.x);
    (env)->CallVoidMethod(loc, midCallSetY, resLoc.y);

    return;

}

JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_load(JNIEnv *env,
                                                                          jobject thisObj,
                                                                          jstring facility,
                                                                          jint floor,
                                                                          jboolean isBin) {

    const char *facilityName = (env)->GetStringUTFChars(facility, 0);


    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();
    optLocf->load(facilityName, floor, false, isBin);

    env->ReleaseStringUTFChars(facility, facilityName);
    return;
}


//JNIEXPORT jint JNICALL Java_com_mlins_locator_LocationFinder_getFloor(JNIEnv * env, jobject thisObj, jobjectArray blips){
//
//
//	int length = (env)->GetArrayLength(blips);
//
//		//  __android_log_print(ANDROID_LOG_DEBUG, "getFloor", "length :%d",length);
//
//		WlBlip blip;
//		list<WlBlip> blibsList;
//
//
//		// Get a class reference
//		jclass classWB = env->FindClass("com/mlins/wireless/WlBlip");
//		//jclass classWB = reinterpret_cast<jclass>(env->NewGlobalRef(localClass));
//
//
//
//		jfieldID midBSSID = (env)->GetFieldID(classWB, "BSSID","Ljava/lang/String;");
//		jfieldID midlevel = (env)->GetFieldID(classWB, "level", "I");
//
//
//
//		if (NULL == midBSSID)
//			return -100;
//		if (NULL == midlevel)
//			return -100;
//
//		// Get the value of each
//
//		for (int i = 0; i < length; i++) {
//			jobject blipObj = (env)->GetObjectArrayElement(blips, i);
//
//			if (NULL != blipObj) {
//
//				//    	  __android_log_print(ANDROID_LOG_DEBUG, "findLocation", "i :%d",i);
//
//				WlBlip blip;
//				jstring BSSID = (jstring) env->GetObjectField(blipObj, midBSSID);
//				const char* BSSIDSrc = env->GetStringUTFChars(BSSID, 0);
//				blip.BSSID = BSSIDSrc;
//				env->ReleaseStringUTFChars(BSSID, BSSIDSrc);
//
//				// 	__android_log_print(ANDROID_LOG_DEBUG, "findLocation", "BSSIDSrc :%s",BSSIDSrc);
//
//				int level = (int) env->GetIntField(blipObj, midlevel);
//
//				//	__android_log_print(ANDROID_LOG_DEBUG, "findLocation", "level :%d",level);
//
//				blip.level = level;
//
//
//				blibsList.push_back(blip);
//			}
//		}
//
////		OptLocFinder* optLocf;
////		optLocf = OptLocFinder::getInstance();
////		//Location resLoc;
////		jint x = optLocf->getFloorByBlips(blibsList);
//
//		OptFloorSelectorFinder* optLocf;
//		optLocf = OptFloorSelectorFinder::getInstance();
//			//Location resLoc;
//		jint x = optLocf->getFloorByBlips(blibsList);
//
//		return x;
//}

//JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_initParams(
//		JNIEnv * env, jobject thisObj, jstring appDirPath,
//		jint locationCloseRange, jint k, jfloat pixelsToMeter, jint averageRange,
//		jobjectArray ssidfilter, jint floorcount,jstring scantype) {
//
//	const char *strRootPath = (env)->GetStringUTFChars(appDirPath, 0);
//	const char *strScantype = (env)->GetStringUTFChars(scantype, 0);
//	//__android_log_print(ANDROID_LOG_DEBUG, "initParams", "appDirPath :%s",strRootPath);  //printf("%s", str); //need to release this string when done with it in
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
//	OptLocFinder* optLocf;
//	optLocf = OptLocFinder::getInstance();
//
//	optLocf->initParams(strRootPath, locCloseRng, K, pixToMtr, avgRange,ssidsVec,floorcount,strScantype);
//
//
//
//	//avoid memory leak
//
//	env->ReleaseStringUTFChars(appDirPath, strRootPath);
//	env->ReleaseStringUTFChars(scantype, strScantype);
//
//	return;
//}


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_initParams(
        JNIEnv *env, jobject thisObj, jstring appDirPath,
        jint locationCloseRange, jint k, jfloat pixelsToMeter, jint averageRange,
        jobjectArray ssidfilter, jint floorcount, jstring scantype,
        jfloat closeDevicesThreshold, jfloat closeDeviceWeight, jint kTopLevelThr) {

    const char *strRootPath = (env)->GetStringUTFChars(appDirPath, 0);
    const char *strScantype = (env)->GetStringUTFChars(scantype, 0);
    //__android_log_print(ANDROID_LOG_DEBUG, "initParams", "appDirPath :%s",strRootPath);  //printf("%s", str); //need to release this string when done with it in

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

    OptLocFinder::releaseInstance();

    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();


    optLocf->initParams(strRootPath, locCloseRng, K, pixToMtr, avgRange, ssidsVec, floorcount,
                        strScantype, conv_closeDevicesThreshold, conv_closeDeviceWeight,
                        conv_kTopLevelThr);

    //__android_log_print(ANDROID_LOG_DEBUG, "initParams", "initparams relased %d",1);

    //avoid memory leak

    env->ReleaseStringUTFChars(appDirPath, strRootPath);
    env->ReleaseStringUTFChars(scantype, strScantype);

    return;
}


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_findLocation(
        JNIEnv *env, jobject thisObj, jobjectArray blips, jobject loc, jboolean isFirstTime) {


    int length = (env)->GetArrayLength(blips);

    //  __android_log_print(ANDROID_LOG_DEBUG, "findLocation", "length :%d",length);

    WlBlip blip;
    list<WlBlip> blibsList;

    // Get a class reference
    //jclass classWBtmp = (env)->FindClass("com/mlins/wireless/WlBlip");

    jclass classWB = env->FindClass("com/mlins/wireless/WlBlip");
//	jclass classWB = reinterpret_cast<jclass>(env->NewGlobalRef(localClass));

    jfieldID midBSSID = (env)->GetFieldID(classWB, "BSSID", "Ljava/lang/String;");
    jfieldID midlevel = (env)->GetFieldID(classWB, "level", "I");

    if (NULL == midBSSID)
        return;
    if (NULL == midlevel)
        return;

    // Get the value of each Integer object in the array

    for (int i = 0; i < length; i++) {
        jobject blipObj = (env)->GetObjectArrayElement(blips, i);

        if (NULL != blipObj) {

            //    	  __android_log_print(ANDROID_LOG_DEBUG, "findLocation", "i :%d",i);

            jstring BSSID = (jstring) env->GetObjectField(blipObj, midBSSID);
            const char *BSSIDSrc = env->GetStringUTFChars(BSSID, 0);


            // 	__android_log_print(ANDROID_LOG_DEBUG, "findLocation", "BSSIDSrc :%s",BSSIDSrc);

            int level = (int) env->GetIntField(blipObj, midlevel);
            WlBlip blip;
            blip.BSSID = BSSIDSrc;
            blip.level = level;

            blibsList.push_back(blip);
            env->ReleaseStringUTFChars(BSSID, BSSIDSrc);

            //	__android_log_print(ANDROID_LOG_DEBUG, "findLocation", "level :%d",level);

        }
    }

    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();
    Location resLoc;
    optLocf->findLocation(blibsList, resLoc, isFirstTime);

    //__android_log_print(ANDROID_LOG_DEBUG, "findLocation","findLocation:location :(x=%f y=%f)", resLoc.x, resLoc.y);

    //fill the loc and return it back to java env.

    jclass FLocationClass = (env)->GetObjectClass(loc);
//	jclass FLocationClass = reinterpret_cast<jclass>(env->NewGlobalRef(FLocationClass2));
    jmethodID midCallSetX = (env)->GetMethodID(FLocationClass, "setX", "(F)V");
    jmethodID midCallSetY = (env)->GetMethodID(FLocationClass, "setY", "(F)V");

    if (NULL == midCallSetX) return;
    if (NULL == midCallSetY) return;

    (env)->CallVoidMethod(loc, midCallSetX, resLoc.x);
    (env)->CallVoidMethod(loc, midCallSetY, resLoc.y);

    return;

}


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_findLocationInsideGeofence(
        JNIEnv *env, jobject thisObj, jobjectArray blips, jobject loc, jfloat topLeftX,
        jfloat topLeftY, jfloat bottomRightX, jfloat bottomRightY) {


    int length = (env)->GetArrayLength(blips);

    //  __android_log_print(ANDROID_LOG_DEBUG, "findLocationInsideGeofence", "length :%d",length);

    WlBlip blip;
    list<WlBlip> blibsList;

    // Get a class reference
    jclass classWB = env->FindClass("com/mlins/wireless/WlBlip");

    jfieldID midBSSID = (env)->GetFieldID(classWB, "BSSID", "Ljava/lang/String;");
    jfieldID midlevel = (env)->GetFieldID(classWB, "level", "I");

    if (NULL == midBSSID)
        return;
    if (NULL == midlevel)
        return;

    // Get the value of each Integer object in the array
    for (int i = 0; i < length; i++) {
        jobject blipObj = (env)->GetObjectArrayElement(blips, i);

        if (NULL != blipObj) {

            // __android_log_print(ANDROID_LOG_DEBUG, "findLocationInsideGeofence", "i :%d",i);

            jstring BSSID = (jstring) env->GetObjectField(blipObj, midBSSID);
            const char *BSSIDSrc = env->GetStringUTFChars(BSSID, 0);


            // 	__android_log_print(ANDROID_LOG_DEBUG, "findLocationInsideGeofence", "BSSIDSrc :%s",BSSIDSrc);

            int level = (int) env->GetIntField(blipObj, midlevel);
            WlBlip blip;
            blip.BSSID = BSSIDSrc;
            blip.level = level;

            blibsList.push_back(blip);
            env->ReleaseStringUTFChars(BSSID, BSSIDSrc);

            //	__android_log_print(ANDROID_LOG_DEBUG, "findLocationInsideGeofence", "level :%d",level);

        }
    }

    float conv_topLeftX = (float) topLeftX;
    float conv_topLeftY = (float) topLeftY;
    float conv_bottomRightX = (float) bottomRightX;
    float conv_bottomRightY = (float) bottomRightY;

    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();
    Location resLoc;
    optLocf->findLocationInsideGeofence(blibsList, resLoc, conv_topLeftX, conv_topLeftY,
                                        conv_bottomRightX, conv_bottomRightY);

    //__android_log_print(ANDROID_LOG_DEBUG, "findLocationInsideGeofence","findLocationInsideGeofence:location :(x=%f y=%f)", resLoc.x, resLoc.y);

    //fill the loc and return it back to java env.

    jclass FLocationClass = (env)->GetObjectClass(loc);
    jmethodID midCallSetX = (env)->GetMethodID(FLocationClass, "setX", "(F)V");
    jmethodID midCallSetY = (env)->GetMethodID(FLocationClass, "setY", "(F)V");

    if (NULL == midCallSetX) return;
    if (NULL == midCallSetY) return;

    (env)->CallVoidMethod(loc, midCallSetX, resLoc.x);
    (env)->CallVoidMethod(loc, midCallSetY, resLoc.y);

    return;

}


JNIEXPORT jstring JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_getGroupIdByBlips(
        JNIEnv *env, jobject thisObj, jobjectArray blips) {

    int length = (env)->GetArrayLength(blips);

    //  __android_log_print(ANDROID_LOG_DEBUG, "getGroupIdByBlips", "length :%d",length);

    WlBlip blip;
    list<WlBlip> blibsList;

    // Get a class reference
    jclass classWB = env->FindClass("com/mlins/wireless/WlBlip");
    jfieldID midBSSID = (env)->GetFieldID(classWB, "BSSID", "Ljava/lang/String;");
    jfieldID midlevel = (env)->GetFieldID(classWB, "level", "I");

    const char *nullTag = "NULL";
    if (NULL == midBSSID) {
        return (env)->NewStringUTF(nullTag);
    }
    if (NULL == midlevel) {
        return (env)->NewStringUTF(nullTag);
    }

    // Get the value of each Integer object in the array
    for (int i = 0; i < length; i++) {
        jobject blipObj = (env)->GetObjectArrayElement(blips, i);

        if (NULL != blipObj) {

            //  __android_log_print(ANDROID_LOG_DEBUG, "getGroupIdByBlips", "i :%d",i);

            jstring BSSID = (jstring) env->GetObjectField(blipObj, midBSSID);
            const char *BSSIDSrc = env->GetStringUTFChars(BSSID, 0);


            // 	__android_log_print(ANDROID_LOG_DEBUG, "getGroupIdByBlips", "BSSIDSrc :%s",BSSIDSrc);

            int level = (int) env->GetIntField(blipObj, midlevel);
            WlBlip blip;
            blip.BSSID = BSSIDSrc;
            blip.level = level;

            blibsList.push_back(blip);
            env->ReleaseStringUTFChars(BSSID, BSSIDSrc);

            //	__android_log_print(ANDROID_LOG_DEBUG, "getGroupIdByBlips", "level :%d",level);

        }
    }

    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();

    const char *fname = optLocf->getGroupIdByBlips(blibsList).c_str();
    return (env)->NewStringUTF(fname);

}


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_saveBin(
        JNIEnv *env, jobject thisObj) {
    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();
    optLocf->saveBin();

    //optLocf->loadBin("office",2);

}


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_resetLastpt(JNIEnv *env,
                                                                                 jobject thisObj) {
    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();
    optLocf->restLastAverage();
}


JNIEXPORT jstring JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_getFileName(JNIEnv *env,
                                                                                    jobject thisObj) {
    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();
    const char *fname = optLocf->getFileName().c_str();

    return (env)->NewStringUTF(fname);
}


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_getPointsList(JNIEnv *env,
                                                                                   jobject thisObj,
                                                                                   jobjectArray pointsList) {


    Location loc;

    // Get a class reference

    jclass classFL = env->FindClass("com/mlins/ndk/wrappers/FLocation");

    jmethodID midCallSetX = (env)->GetMethodID(classFL, "setX", "(F)V");
    jmethodID midCallSetY = (env)->GetMethodID(classFL, "setY", "(F)V");

    if (NULL == midCallSetX) return;
    if (NULL == midCallSetY) return;

    // Get the value of each  object in the array

    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();

    list<AssociativeData> theList = optLocf->getTheList();
    int i = 0;
    for (list<AssociativeData>::iterator it = theList.begin(); it != theList.end(); it++) {
        AssociativeData &p = *it;
        loc.x = p.point.x;
        loc.y = p.point.y;

        jobject LocObj = (env)->GetObjectArrayElement(pointsList, i);

        if (NULL != LocObj) {

            (env)->CallVoidMethod(LocObj, midCallSetX, loc.x);
            (env)->CallVoidMethod(LocObj, midCallSetY, loc.y);

            (env)->SetObjectArrayElement(pointsList, i, LocObj);
        }

        i = i + 1;
    }
}

JNIEXPORT jint JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_getPointsCount(JNIEnv *env,
                                                                                    jobject thisObj) {
    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();
    jint size = optLocf->getTheListSize();
    return size;
}


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_getSsidnames(JNIEnv *env,
                                                                                  jobject thisObj,
                                                                                  jobjectArray ssidsNamesList) {

    jstring ssidname;

    // Get a class reference

    jclass classFL = env->FindClass("Ljava/lang/String;");

    jmethodID midCallToString = (env)->GetMethodID(classFL, "toString", "()Ljava/lang/String;");

    if (NULL == midCallToString) return;


    // Get the value of each  object in the array

    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();

    list<string> ssidnames = optLocf->getSsidnames();
    int i = 0;
    for (list<string>::iterator it = ssidnames.begin(); it != ssidnames.end(); it++) {

        string ssidn = *it;

        const char *ssidName = ssidn.c_str();

        (env)->SetObjectArrayElement(ssidsNamesList, i, (env)->NewStringUTF(ssidName));
        i = i + 1;
    }
}

JNIEXPORT jint JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_getSssidnamesCount(JNIEnv *env,
                                                                                        jobject thisObj) {
    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();
    jint size = optLocf->getSsidnamesSize();
    return size;
}

/** start MB Zone Group finder */
JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_initZoneGroupFinder(
        JNIEnv *env, jobject thisObj, jstring appDirPath) {

    const char *strRootPath = (env)->GetStringUTFChars(appDirPath, 0);

    __android_log_print(ANDROID_LOG_DEBUG, "initZoneGroupFinder", "appDirPath :%s", strRootPath);

    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();
    optLocf->loadZoneGroupsFile(strRootPath);
    //avoid memory leak
    env->ReleaseStringUTFChars(appDirPath, strRootPath);
}

JNIEXPORT jint JNICALL Java_com_mlins_ndk_wrappers_NdkLocationFinder_findZoneGroup(JNIEnv *env,
                                                                                   jobject thisObj,
                                                                                   jobjectArray blips) {

    int length = (env)->GetArrayLength(blips);

    __android_log_print(ANDROID_LOG_DEBUG, "findZoneGroup", "length :%d", length);

    WlBlip blip;
    list<WlBlip> blibsList;


    // Get a class reference
    jclass classWB = env->FindClass("com/mlins/wireless/WlBlip");
    //jclass classWB = reinterpret_cast<jclass>(env->NewGlobalRef(localClass));



    jfieldID midBSSID = (env)->GetFieldID(classWB, "BSSID", "Ljava/lang/String;");
    jfieldID midlevel = (env)->GetFieldID(classWB, "level", "I");

    //const char* zoneId =  "UNKNOWN_ZONE_GROUP";

    if (NULL == midBSSID)
        return -100;//(env)->NewStringUTF(zoneId);
    if (NULL == midlevel)
        return -100; //(env)->NewStringUTF(zoneId);

    // Get the value of each

    for (int i = 0; i < length; i++) {
        jobject blipObj = (env)->GetObjectArrayElement(blips, i);

        if (NULL != blipObj) {

            __android_log_print(ANDROID_LOG_DEBUG, "findZoneGroup", "i :%d", i);

            WlBlip blip;
            jstring BSSID = (jstring) env->GetObjectField(blipObj, midBSSID);
            const char *BSSIDSrc = env->GetStringUTFChars(BSSID, 0);
            blip.BSSID = BSSIDSrc;
            env->ReleaseStringUTFChars(BSSID, BSSIDSrc);

            __android_log_print(ANDROID_LOG_DEBUG, "findZoneGroup", "BSSIDSrc :%s", BSSIDSrc);

            int level = (int) env->GetIntField(blipObj, midlevel);

            __android_log_print(ANDROID_LOG_DEBUG, "findZoneGroup", "level :%d", level);

            blip.level = level;


            blibsList.push_back(blip);
        }
    }


    OptLocFinder *optLocf;
    optLocf = OptLocFinder::getInstance();
    jint zoneId = optLocf->getZoneGroupByBlips(blibsList);
    return zoneId;
//			zoneId = optLocf->getZoneGroupByBlips(blibsList).c_str();
//
//			return (env)->NewStringUTF(zoneId);


}
/** end MB Zone Group finder */
