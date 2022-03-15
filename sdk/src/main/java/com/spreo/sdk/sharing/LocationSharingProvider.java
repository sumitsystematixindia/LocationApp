package com.spreo.sdk.sharing;

import com.mlins.nav.location.sharing.LocationSharingUsersManger;
import com.spreo.interfaces.LocationSharingListener;

import java.util.List;

public class LocationSharingProvider {

    public static void registerListener(LocationSharingListener listener) {
        LocationSharingUsersManger.getInstance().registerListener(listener);
    }

    public static void unregisterListener(LocationSharingListener listener) {
        LocationSharingUsersManger.getInstance().unregisterListener(listener);
    }

    public static void setUserIdList(List<String> idList) {
        LocationSharingUsersManger.getInstance().setIdList(idList);
    }

}
