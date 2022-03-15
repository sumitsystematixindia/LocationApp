#include <jni.h>

#ifndef _Included_com_mlins_ndk_wrappers_NdkConversionUtils
#define _Included_com_mlins_ndk_wrappers_NdkConversionUtils
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT  void JNICALL Java_com_mlins_ndk_wrappers_NdkConversionUtils_convertPoint(JNIEnv *,
                                                                                    jobject,
                                                                                    jobject,
                                                                                    jdouble,
                                                                                    jdouble,
                                                                                    jdouble,
                                                                                    jdouble,
                                                                                    jdouble,
                                                                                    jdouble,
                                                                                    jdouble,
                                                                                    jdouble,
                                                                                    jdouble,
                                                                                    jdouble,
                                                                                    jdouble,
                                                                                    jobject covertdPoint);

JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkConversionUtils_convertLatLonPoint(JNIEnv *,
                                                                                         jobject,
                                                                                         jobject,
                                                                                         jdouble,
                                                                                         jdouble,
                                                                                         jdouble,
                                                                                         jdouble,
                                                                                         jdouble,
                                                                                         jdouble,
                                                                                         jdouble,
                                                                                         jdouble,
                                                                                         jdouble,
                                                                                         jdouble,
                                                                                         jdouble,
                                                                                         jobject covertedPoint);

#ifdef __cplusplus
}
#endif
#endif
