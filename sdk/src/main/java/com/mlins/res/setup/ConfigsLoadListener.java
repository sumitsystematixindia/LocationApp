package com.mlins.res.setup;

public interface ConfigsLoadListener {
    public void onPreConfigsLoad(LoadStatus loadStatus);

    public void onPostConfigsLoad(LoadStatus status);
}
