package com.spreo.interfaces;

import android.graphics.Bitmap;

import com.spreo.nav.interfaces.ILocation;


public interface ILocationSharingUser {

    String getUserId();

    void setUserId(String userId);

    void setUserIcon(Bitmap userIcon);

    Bitmap getuserIcon();

    ILocation getLocation();

    void setLocation(ILocation location);

}
