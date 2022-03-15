package com.spreo.interfaces;

import com.spreo.enums.ResUpdateStatus;

public interface ConfigsUpdaterListener {
    public void onPreConfigsDownload();

    public void onPostConfigsDownload(ResUpdateStatus status);


    public void onPreConfigsInit();

    public void onPostConfigsInit(ResUpdateStatus status);


}
