package com.mlins.res.setup;

public interface ConfigsUpdaterListener {
    public void onPreConfigsDownload();

    public void onPostConfigsDownload(ResUpdateStatus status);
}
