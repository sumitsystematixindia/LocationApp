package com.mlins.nav.location.sharing;

import android.graphics.Bitmap;

import com.spreo.interfaces.ILocationSharingUser;
import com.spreo.nav.interfaces.ILocation;

public class LocationSharingUser implements ILocationSharingUser {
    private String userId = null;
    private Bitmap userIcon = null;
    private ILocation location = null;

    public LocationSharingUser() {

    }

    public LocationSharingUser(String userId) {
        this.userId = userId;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void setUserIcon(Bitmap userIcon) {
        this.userIcon = userIcon;
    }

    @Override
    public Bitmap getuserIcon() {
        return userIcon;
    }

    @Override
    public ILocation getLocation() {
        return location;
    }

    @Override
    public void setLocation(ILocation location) {
        this.location = location;
    }

}
