package com.location.app.jni_models;

public class ConfiguredBeacon {

    private final long cnfg_beacon_ptr;

    private ConfiguredBeacon(long cnfg_beacon_ptr){
        this.cnfg_beacon_ptr = cnfg_beacon_ptr;
    }

    /**
     * This function accepts a ConfiguredBeacon* and tries to add it to the configured_beacons vector
     * in  C++. If the addition was successful, true is returned. Otherwise false.
     * @param beacon_ptr 64-bit memory address the ConfiguredBeacon object
     * @return true if  successful
     */
    private static native boolean addToConfiguredBeacons(long beacon_ptr);

//    /**
//     * This function would remove all the ConfiguredBeacons
//     */
//    private static native void removeAllConfiguredBeacons();

    public static boolean addToConfiguredBeacons(ConfiguredBeacon beacon){
        return addToConfiguredBeacons(beacon.cnfg_beacon_ptr);
    }

    public static class Builder{


        private final long builder_ptr;

        /**
         * This method must allocate a ConfiguredBeacon::Builder object dynamically and return a pointer to it.
         * @return pointer to the underlying ConfiguredBeacon::Builder object.
         */
        private static native long jni_allocate();

        /**
         * This method must free the space allocated to ConfiguredBeacon::Builder object.
         */
        private static native void jni_free(long pointer);

        /**
         * This method must call the create() method of the underlying ConfiguredBeacon::Builder object
         * and return a pointer to it.
         * @return a pointer to the updated builder object(same as 'this').
         */
        private native long jni_create(long pointer);

        /**
         * This method must call the setId() method of the underlying ConfiguredBeacon::Builder object
         * and return a pointer to it.
         * @return a pointer to the updated builder object(same as 'this').
         */
        private native long jni_setId(long pointer, int id);

        /**
         * This method must call the setPos() method of the underlying ConfiguredBeacon::Builder object
         * and return a pointer to it.
         * @return a pointer to the updated builder object(same as 'this').
         */
        private native long jni_setPos(long pointer, int x, int y, int z);

        /**
         * This method must call the setRssiD0() method of the underlying ConfiguredBeacon::Builder object
         * and return a pointer to it.
         * @return a pointer to the updated builder object(same as 'this').
         */
        private native long jni_setRssiD0(long pointer, int rssi);

        /**
         * This method must call the setD0() method of the underlying ConfiguredBeacon::Builder object
         * and return a pointer to it.
         * @return a pointer to the updated builder object(same as 'this').
         */
        private native long jni_setD0(long pointer, int d0);

        /**
         * This method must call the setBeaconCoeff() method of the underlying ConfiguredBeacon::Builder object
         * and return a pointer to it.
         * @return a pointer to the updated builder object(same as 'this').
         */
        private native long jni_setBeaconCoeff(long pointer, float coeff);

        /**
         * This method must call the setXSigma() method of the underlying ConfiguredBeacon::Builder object
         * and return a pointer to it.
         * @return a pointer to the updated builder object(same as 'this').
         */
        private native long jni_setXSigma(long pointer, float x_sig);

        /**
         * This method must call the registerFilter() method of the underlying ConfiguredBeacon::Builder object
         * and return a pointer to it.
         * @return a pointer to the updated builder object(same as 'this').
         */
        private native long jni_registerFilter(long pointer, long filter_ptr);

        /**
         * This method must call the removeFilter() method of the underlying ConfiguredBeacon::Builder object
         * and return a pointer to it.
         * @return a pointer to the updated builder object(same as 'this').
         */
        private native long jni_removeFilter(long pointer, long filter_ptr);

        /**
         * This method must call the build() method of the underlying ConfiguredBeacon::Builder object
         * and return a pointer to the ConfiguredBeacon object. This can then be passed to addToConfiguredBeacons(...).
         * @return a pointer to the updated builder object(same as 'this').
         */
        private native long jni_build(long pointer);

        private Builder(){
            this.builder_ptr = jni_allocate();
            jni_create(this.builder_ptr);
        }

        public static Builder allocate(){
            return new Builder();
        }

        public static void free(Builder builder){
            jni_free(builder.builder_ptr);
        }

        public Builder setId(int id){
            jni_setId(builder_ptr, id);
            return this;
        }

        public Builder setPos(int x, int y, int z){
            jni_setPos(builder_ptr, x, y, z);
            return this;
        }

        public Builder setRssiD0(int rssi){
            jni_setRssiD0(builder_ptr, rssi);
            return this;
        }

        public Builder setD0(int d0){
            jni_setD0(builder_ptr, d0);
            return this;
        }

        public Builder setBeaconCoeff(float coeff){
            jni_setBeaconCoeff(builder_ptr, coeff);
            return this;
        }

        public Builder setXSigma(float X_sig){
            jni_setXSigma(builder_ptr, X_sig);
            return this;
        }

        public Builder registerFilter(Filter f){
            jni_registerFilter(builder_ptr, f.getPointer());
            return this;
        }

        public Builder removeFilter(Filter f){
            jni_removeFilter(builder_ptr, f.getPointer());
            return this;
        }

        public ConfiguredBeacon build(){
            long cnfg_ptr = jni_build(builder_ptr);
            return new ConfiguredBeacon(cnfg_ptr);
        }
    }
}
