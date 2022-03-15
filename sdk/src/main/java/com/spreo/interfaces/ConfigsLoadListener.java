package com.spreo.interfaces;

import com.spreo.enums.LoadStatus;

public interface ConfigsLoadListener {
    public void onPreConfigsLoad(LoadStatus loadStatus);

    public void onPostConfigsLoad(LoadStatus status);
}
