#include <jni.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_location_app_jni_1models_BeaconCallback_jni_1resolve(JNIEnv *env, jclass clazz,
                                                              jstring uuid, jint major,
                                                              jint minor, jint rssi) {
    // TODO: implement jni_resolve()
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_location_app_jni_1models_ConfiguredBeacon_addToConfiguredBeacons( [[maybe_unused]] JNIEnv *env, [[maybe_unused]] jclass clazz, jlong beacon_ptr) {
    // TODO: implement addToConfiguredBeacons()
    auto* configured_beacon_ptr = (ConfiguredBeacon*)beacon_ptr;
    return addToConfiguredBeacons(*configured_beacon_ptr);
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_location_app_jni_1models_ConfiguredBeacon_00024Builder_jni_1allocate( [[maybe_unused]] JNIEnv *env, [[maybe_unused]] jclass clazz) {
    // TODO: implement jni_allocate()
    auto* builder_ptr = new ConfiguredBeacon::Builder();
    return (long)builder_ptr;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_location_app_jni_1models_ConfiguredBeacon_00024Builder_jni_1free([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jclass clazz, jlong pointer) {
    // TODO: implement jni_free()
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    delete builder_ptr;
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_location_app_jni_1models_ConfiguredBeacon_00024Builder_jni_1create([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jlong pointer) {
    // TODO: implement jni_create()
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    return (long)(builder_ptr->create());
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_location_app_jni_1models_ConfiguredBeacon_00024Builder_jni_1setId([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jlong pointer,jint id) {
    // TODO: implement jni_setId()
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    return (long)(builder_ptr->setId(id));
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_location_app_jni_1models_ConfiguredBeacon_00024Builder_jni_1setPos([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jlong pointer,
                                                                            jint x, jint y, jint z) {
    // TODO: implement jni_setPos()
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    return (long)(builder_ptr->setPos(x,y,z));
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_location_app_jni_1models_ConfiguredBeacon_00024Builder_jni_1setRssiD0([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jlong pointer,
                                                                               jint rssi) {
    // TODO: implement jni_setRssiD0()
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    return (long)(builder_ptr->setRssiD0(rssi));
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_location_app_jni_1models_ConfiguredBeacon_00024Builder_jni_1setD0([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jlong pointer,
                                                                           jint d0) {
    // TODO: implement jni_setD0()
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    return (long)(builder_ptr->setD0(d0));
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_location_app_jni_1models_ConfiguredBeacon_00024Builder_jni_1setBeaconCoeff( [[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jlong pointer, jfloat coeff) {
    // TODO: implement jni_setBeaconCoeff()
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    return (long)(builder_ptr->setBeaconCoeff(coeff));
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_location_app_jni_1models_ConfiguredBeacon_00024Builder_jni_1setXSigma([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jlong pointer,
                                                                               jfloat x_sig) {
    // TODO: implement jni_setXSigma()
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    return (long)(builder_ptr->setXSigma(x_sig));
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_location_app_jni_1models_ConfiguredBeacon_00024Builder_jni_1registerFilter( [[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jlong pointer, jlong filter_ptr) {
    // TODO: implement jni_registerFilter()
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    auto* filter_pointer = (Filter*)filter_ptr;
    return (long)(builder_ptr->registerFilter(filter_pointer));
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_location_app_jni_1models_ConfiguredBeacon_00024Builder_jni_1removeFilter(  [[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jlong pointer, jlong filter_ptr) {
    // TODO: implement jni_removeFilter()
    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    auto* filter_pointer = (Filter*)filter_ptr;
    return (long)(builder_ptr->removeFilter(filter_pointer));
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_location_app_jni_1models_ConfiguredBeacon_00024Builder_jni_1build([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jlong pointer) {
    // TODO: implement jni_build()

    auto* builder_ptr = (ConfiguredBeacon::Builder*)pointer;
    //build() would return a reference to the dynamically allocated ConfigureBeacon object
    //and this function would return the memory address of that object.
    return (long)(&builder_ptr->build());
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_location_app_jni_1models_MeanFilter_jni_1allocate([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jclass clazz,
                                                           jint window_size) {
    // TODO: implement jni_allocate()
    auto* mean_filter_ptr = new MeanFilter(window_size);
    return (long)mean_filter_ptr;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_location_app_jni_1models_MeanFilter_jni_1free([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jclass clazz,
                                                       jlong pointer) {
    // TODO: implement jni_free()
    auto* mean_filter_ptr = (MeanFilter*)pointer;
    delete mean_filter_ptr;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_location_app_jni_1models_Trilateration_jni_1startTrilateration(JNIEnv *env, jclass clazz) {
    // TODO: implement jni_startTrilateration()
    startTrilateration();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_location_app_jni_1models_Trilateration_jni_1stopTrilateration(JNIEnv *env, jclass clazz) {
    // TODO: implement jni_stopTrilateration()
    stopTrilateration();
}
