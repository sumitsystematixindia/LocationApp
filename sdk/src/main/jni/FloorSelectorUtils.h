/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_mlins_locator_FloorSelector */

#ifndef _Included_com_mlins_ndk_wrappers_NdkFloorSelector
#define _Included_com_mlins_ndk_wrappers_NdkFloorSelector
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_mlins_locator_FloorSelector
 * Method:    load
 * Signature: (Ljava/lang/String;Ljava/lang/String;IZZ)V
 */
JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkFloorSelector_load
        (JNIEnv *env, jobject thisObj, jstring facility, jint floor, jboolean isBin);

/*
 * Class:     com_mlins_locator_FloorSelector
 * Method:    getFloor
 * Signature: ([Lcom/mlins/wireless/WlBlip;)I
 */
JNIEXPORT jint JNICALL Java_com_mlins_ndk_wrappers_NdkFloorSelector_getFloor(JNIEnv *, jobject,
                                                                             jobjectArray,
                                                                             jboolean isFirstTime);

//JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkFloorSelector_initParams
//  (JNIEnv *, jobject, jstring appDirPath, jint locationCloseRange,jint k, jfloat pixelsToMeter, jint averageRange, jobjectArray ssidfilter, int floorcount,jstring scantype);


//JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkFloorSelector_initParams
//  (JNIEnv *, jobject, jstring appDirPath, jint locationCloseRange,jint k, jfloat pixelsToMeter, jint averageRange, jobjectArray ssidfilter, int floorcount,jstring scantype,jfloat closeDevicesThreshold, jfloat closeDeviceWeight, jint kTopLevelThr);


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkFloorSelector_initParams
        (JNIEnv *, jobject, jstring appDirPath, jint locationCloseRange, jint k,
         jfloat pixelsToMeter, jint averageRange, jobjectArray ssidfilter, int floorcount,
         jstring scantype, jfloat closeDevicesThreshold, jfloat closeDeviceWeight,
         jint kTopLevelThr, jint levelLowerBound);


/*
 * Class:     com_mlins_locator_FloorSelector
 * Method:    saveBin
 * Signature: ()Z
 */
JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkFloorSelector_saveBin(JNIEnv *env, jobject);


#ifdef __cplusplus
}
#endif
#endif