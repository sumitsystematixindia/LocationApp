package com.location.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
    private static final NetworkUtils ourInstance = new NetworkUtils();

    public static NetworkUtils getInstance() {
        return ourInstance;
    }

    private NetworkUtils() {
    }


    static NetworkInfo WifiInformation, mobileInformation;
    static ConnectivityManager connection_manager;
    private static boolean isNetworkAvaliable = false;

    public static boolean isNetworkAvailable(Context cxt) {
        connection_manager = (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiInformation = connection_manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mobileInformation = connection_manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        try {
            if (WifiInformation.isConnected() || mobileInformation.isConnected()) {
                isNetworkAvaliable = true;
            } else {
                isNetworkAvaliable = false;
            }
        }catch (Exception e){
            isNetworkAvaliable = false;
            e.printStackTrace();
        }
        return isNetworkAvaliable;
    }

}



