package com.spreo.interfaces;

import com.spreo.nav.interfaces.ILocation;

public interface MyConvertedLocationListener {

    /**
     * converted location to lat/lon :
     *
     * @param location
     */
    public void onConvertedLocationDelivered(ILocation location);

}
