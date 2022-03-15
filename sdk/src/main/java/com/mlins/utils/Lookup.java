package com.mlins.utils;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A holder for other singleton instances.
 *
 */
public class Lookup {

    private static final Lookup INSTANCE = new Lookup();

    private static final String TAG = "Lookup";

    private final Map<Class, Object> instances = new HashMap<>();

    private Lookup() {}
    
    public static Lookup getInstance() {
        return INSTANCE;
    }
    
    public synchronized <T> T lookup(Class<T> clazz){
        return (T) instances.get(clazz);
    }

    public synchronized <T> T get(Class<T> clazz){
        T instance = lookup(clazz);
        if(instance == null) {
            try {
                Log.d("TAG", "Instance of " + clazz + " not found. Going to create new one.");
                Constructor<T> cons = clazz.getDeclaredConstructor();
                cons.setAccessible(true);
                instance = cons.newInstance();
                put(instance);
                Log.d("TAG", "Instance of " + clazz + " had been successfully created.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    };

    public synchronized void put(Object obj) {
        instances.put(obj.getClass(), obj);
    }

    public synchronized void remove(Class clazz){
        Object instance = instances.get(clazz);
        Log.d(TAG, "Removing instance of " + clazz + ": " + instance);
        if(instance instanceof Cleanable) {
            Log.d(TAG, instance.toString()  + ", is Cleanable, performing cleanup");
            try {
                ((Cleanable) instance).clean();
                Log.d(TAG, instance.toString()  + ", had been successfully cleaned up");
            } catch (Throwable t) {
                Log.e(TAG, "Exception while instance cleanup: " + instance);
            }
        }
        instances.remove(clazz);
    }



    public synchronized void clean(){
        Log.d(TAG, "Cleaning up all instances");
        List<Class> instanceClasses = new ArrayList<>(instances.keySet());
        for (Class instanceClass : instanceClasses) {
            remove(instanceClass);
        }
    }

    public void put(Object instance, Class clazz) {
        Log.d(TAG, "Adding instance of " + clazz + " to LookUp (" + instance + ")");
        instances.put(clazz, instance);
    }
}
