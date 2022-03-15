package com.mlins.interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface GoogleNavigationListener {
    void onGoogleNavigationOption(LatLng latlng);

    void onGoogleNavigationEnd();
}
