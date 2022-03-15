package com.location.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.location.app.model.Floor;
import com.location.app.model.IndoorPathway;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class Preferences {

    public static final String APP_PREF = "LocationAppPrefrences";
    public static final String KEY_FLOOR_LIST = "Floor list";
    public static final String KEY_SELECTED_ITEM_FACILITY_ID="selecteditemfacility" ;
    public static final String KEY_SELECTED_ITEM_CAMPUS_ID ="CampusId" ;
    public static final String KEY_SELECTED_ITEM_BUILDING = "Buliding";
    public static final String KEY_SELECTED_ITEM_FLOOR = "floors";
    public static final String KEY_FLOOR_URL = "floorUrl";
    public static final String KEY_FLOOR_URL_POI = "floorMap";
    public static final String KEY_LAT_LONG = "oldLatLong";
    public static final String KEY_INDOOR_PATHWAY = "pathways";
    public static final String SAVE_POINTS = "lat_long";


    public static SharedPreferences sp;


    public static void saveFloorDataArrayList(Context context, String key, List<Floor> value) {
        try {
            sp = context.getSharedPreferences(APP_PREF, 0);
            SharedPreferences.Editor edit = sp.edit();

            Gson gson = new Gson();
            Type type = new TypeToken<List<Floor>>() {
            }.getType();

            String jsonData = gson.toJson(value, type);
            edit.putString(key, jsonData);

            edit.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<Floor> getFloorDataArrayList(Context context, String key) {
        Gson gson = new Gson();
        sp = context.getSharedPreferences(APP_PREF, 0);
        String json = sp.getString(key, null);
        Log.d("FloorData", "getFloorData: " + json);
        Type type = new TypeToken<List<Floor>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    public static void saveStringPointsList(Context context, String key, List<String> value) {
        try {
            sp = context.getSharedPreferences(APP_PREF, 0);
            SharedPreferences.Editor edit = sp.edit();

            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {
            }.getType();

            String jsonData = gson.toJson(value, type);
            edit.putString(key, jsonData);

            edit.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<String> getStringPointList(Context context, String key) {
        Gson gson = new Gson();
        sp = context.getSharedPreferences(APP_PREF, 0);
        String json = sp.getString(key, null);
        Log.d("FloorData", "getFloorData: " + json);
        Type type = new TypeToken<List<String>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    public static void saveIndoorPathWayDataArrayList(Context context, String key, ArrayList<IndoorPathway> value) {
        try {
            sp = context.getSharedPreferences(APP_PREF, 0);
            SharedPreferences.Editor edit = sp.edit();

            Gson gson = new Gson();
            Type type = new TypeToken<List<IndoorPathway>>() {
            }.getType();

            String jsonData = gson.toJson(value, type);
            edit.putString(key, jsonData);

            edit.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<IndoorPathway> getIndoorPathWayDataArrayList(Context context, String key) {
        Gson gson = new Gson();
        sp = context.getSharedPreferences(APP_PREF, 0);
        String json = sp.getString(key, null);
        Log.d("FloorData", "getFloorData: " + json);
        Type type = new TypeToken<List<IndoorPathway>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    public static void saveString1(Context context, String key, String value) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }
    public static String getString1(Context context, String key) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        String userId = sp.getString(key, "");
        return userId;
    }
    public static void saveString2(Context context, String key, String value) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }
    public static String getString2(Context context, String key) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        String userId = sp.getString(key, "");
        return userId;
    }
    public static void saveString3(Context context, String key, String value) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }
    public static String getString3(Context context, String key) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        String userId = sp.getString(key, "");
        return userId;
    }
    public static void saveString4(Context context, String key, String value) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }
    public static String getString4(Context context, String key) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        String userId = sp.getString(key, "");
        return userId;
    }

    public static void saveString5(Context context, String key, String value) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }
    public static String getString5(Context context, String key) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        String userId = sp.getString(key, "");
        return userId;
    }
    public static void saveString6(Context context, String key, String value) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }
    public static String getString6(Context context, String key) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        String userId = sp.getString(key, "");
        return userId;
    }

    public static void saveString7(Context context, String key, String value) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }
    public static String getString7(Context context, String key) {
        sp = context.getSharedPreferences(APP_PREF, 0);
        String userId = sp.getString(key, "");
        return userId;
    }
}
