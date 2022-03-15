package com.spreo.nav.interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface IProject {
    public String getName();

    public void setName(String name);

    public String getId();

    public void setId(String id);

    public String getApikey();

    public void setApikey(String apikey);

    public LatLng getLocation();

    public void setLocation(LatLng location);

    public String getImageUrl();

    public void setImageUrl(String url);
}
