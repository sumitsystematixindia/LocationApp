package com.location.app.jni_models;

public class MeanFilter implements Filter{

    private final long mean_filter_ptr;

    private static native long jni_allocate(int windowSize);

    private static native void jni_free(long pointer);

    private MeanFilter(int windowSize){
        mean_filter_ptr = jni_allocate(windowSize);
    }

    public static MeanFilter allocate(int windowSize){
        return new MeanFilter(windowSize);
    }

    public static void free(MeanFilter filter){
        jni_free(filter.mean_filter_ptr);
    }

    @Override
    public long getPointer() {
        return mean_filter_ptr;
    }
}
