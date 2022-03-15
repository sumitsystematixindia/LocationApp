#include <jni.h>

#ifndef _Included_com_mlins_ndk_wrappers_NdkShortPathCalculator
#define _Included_com_mlins_ndk_wrappers_NdkShortPathCalculator
#ifdef __cplusplus
extern "C" {
#endif


JNIEXPORT void JNICALL Java_com_mlins_ndk_wrappers_NdkShortPathCalculator_findBestEnterAndExist
        (JNIEnv *, jobject, jobject, jobject, jobjectArray, jobjectArray, jobject, jobject, jdouble,
         jdouble);

#ifdef __cplusplus
}
#endif
#endif
